package br.ufms.gitpay.data.firebase.repository;

import br.ufms.gitpay.domain.model.transacao.Deposito;
import br.ufms.gitpay.domain.model.transacao.TipoTransacao;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class DepositoFirestoreRepository extends TransacaoFirestoreRepository<Deposito> {

    public DepositoFirestoreRepository(int numeroConta) {
        super(numeroConta);
    }

    @Override
    protected Map<String, Object> entityToMap(Deposito deposito) {
        Map<String, Object> data = super.entityToMap(deposito);
        data.put("destino", getContaRef(deposito.getDestino()));
        return data;
    }

    @Override
    protected CompletableFuture<Deposito> documentToEntity(DocumentSnapshot doc) {
        // só tentar converter o doc para depósito se o tipo no banco for 'DEPOSITO'
        if (!Objects.equals(doc.getString("tipo"), TipoTransacao.DEPOSITO.name())) {
            return CompletableFuture.completedFuture(null);
        }

        DocumentReference contaRef = doc.get("destino", DocumentReference.class);
        if (contaRef == null) {
            return CompletableFuture.failedFuture(new NoSuchElementException("Conta não encontrada 1"));
        }
        return new ContaGitPayFirestoreRepository().get(contaRef).thenCompose(conta -> conta.map(contaBancaria ->
                        CompletableFuture.completedFuture(new Deposito(doc.getId(), contaBancaria,
                                Objects.requireNonNull(doc.getDouble("valor")),
                                toLocalDateTime(doc.getCreateTime()))))
                .orElseGet(() -> CompletableFuture.failedFuture(new NoSuchElementException("Conta não encontrada 2")))
        );
    }
}
