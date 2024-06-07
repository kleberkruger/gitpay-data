package br.ufms.gitpay.data.firebase.repository;

import br.ufms.gitpay.domain.model.conta.ContaBancaria;
import br.ufms.gitpay.domain.model.transacao.Saque;
import br.ufms.gitpay.domain.model.transacao.TipoTransacao;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class SaqueFirestoreRepository extends TransacaoFirestoreRepository<Saque> {

    public SaqueFirestoreRepository(int numeroConta) {
        super(numeroConta);
    }

    @Override
    protected Map<String, Object> entityToMap(Saque saque) {
        Map<String, Object> data = super.entityToMap(saque);
        data.put("origem", getContaRef(saque.getOrigem()));
        return data;
    }

    @Override
    protected CompletableFuture<Saque> documentToEntity(DocumentSnapshot doc) {
        // só tentar converter o doc para saque se o tipo no banco for 'SAQUE'
        if (!Objects.equals(doc.getString("tipo"), TipoTransacao.SAQUE.name())) {
            return CompletableFuture.completedFuture(null);
        }

        DocumentReference contaRef = doc.get("origem", DocumentReference.class);
        if (contaRef == null) {
            return CompletableFuture.failedFuture(new NoSuchElementException("Conta não encontrada 1"));
        }
        return new ContaGitPayFirestoreRepository().get(contaRef).thenCompose(conta -> conta.map(contaBancaria ->
                        CompletableFuture.completedFuture(new Saque(doc.getId(), contaBancaria,
                                Objects.requireNonNull(doc.getDouble("valor")),
                                toLocalDateTime(doc.getCreateTime()))))
                .orElseGet(() -> CompletableFuture.failedFuture(new NoSuchElementException("Conta não encontrada")))
        );
    }
}
