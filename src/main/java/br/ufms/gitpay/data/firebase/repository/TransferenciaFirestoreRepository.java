package br.ufms.gitpay.data.firebase.repository;

import br.ufms.gitpay.domain.model.banco.Banco;
import br.ufms.gitpay.domain.model.conta.ContaBancaria;
import br.ufms.gitpay.domain.model.transacao.TipoTransacao;
import br.ufms.gitpay.domain.model.transacao.Transferencia;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;

import java.sql.SQLOutput;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class TransferenciaFirestoreRepository extends TransacaoFirestoreRepository<Transferencia> {

    public TransferenciaFirestoreRepository(int numeroConta) {
        super(numeroConta);
    }

    @Override
    protected Map<String, Object> entityToMap(Transferencia transferencia) {
        Map<String, Object> data = super.entityToMap(transferencia);
        data.put("origem", getContaRef(transferencia.getOrigem()));
        data.put("destino", getContaRef(transferencia.getDestino()));
        return data;
    }

    private ContaFirestoreRepository<? extends ContaBancaria> getContaRepository(DocumentReference contaRef) {
        DocumentReference bancoRef;
        if (contaRef == null || (bancoRef = contaRef.getParent().getParent()) == null) {
            return null;
        }
        var bancoId = Integer.parseInt(bancoRef.getId());
        return bancoId == Banco.GitPay.getCodigo() ? new ContaGitPayFirestoreRepository() :
                new ContaExternaFirestoreRepository(bancoId);
    }

    @Override
    protected CompletableFuture<Transferencia> documentToEntity(DocumentSnapshot doc) {
        // só tentar converter o doc para transferência se o tipo no banco for 'TRANSFERENCIA'
        if (!Objects.equals(doc.getString("tipo"), TipoTransacao.TRANSFERENCIA.name())) {
            return CompletableFuture.completedFuture(null);
        }

        var origemRef = doc.get("origem", DocumentReference.class);
        var destinoRef = doc.get("destino", DocumentReference.class);
        ContaFirestoreRepository<? extends ContaBancaria> contaOrigemRepository;
        ContaFirestoreRepository<? extends ContaBancaria> contaDestinoRepository;

        if (origemRef == null || destinoRef == null ||
                (contaOrigemRepository = getContaRepository(origemRef)) == null ||
                (contaDestinoRepository = getContaRepository(destinoRef)) == null
        ) {
            return CompletableFuture.failedFuture(new NoSuchElementException("Conta não encontrada"));
        }

        var contaOrigemFuture = contaOrigemRepository.get(origemRef);
        var contaDestinoFuture = contaDestinoRepository.get(destinoRef);
        return contaOrigemFuture.thenCombineAsync(contaDestinoFuture, (contaOrigem, contaDestino) -> {
            if (contaOrigem.isEmpty() || contaDestino.isEmpty()) {
                throw new NoSuchElementException("Conta não encontrada");
            }
            return new Transferencia(doc.getId(), contaOrigem.get(), contaDestino.get(),
                    Objects.requireNonNull(doc.getDouble("valor")),
                    toLocalDateTime(doc.getCreateTime()));
        });
    }
}
