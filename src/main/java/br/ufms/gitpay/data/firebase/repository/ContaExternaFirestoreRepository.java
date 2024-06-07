package br.ufms.gitpay.data.firebase.repository;

import br.ufms.gitpay.domain.model.banco.Banco;
import br.ufms.gitpay.domain.model.conta.*;
import com.google.cloud.firestore.DocumentSnapshot;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ContaExternaFirestoreRepository extends ContaFirestoreRepository<ContaExterna> {

    public ContaExternaFirestoreRepository(int banco) {
        super(banco);
        if (banco == Banco.GitPay.getCodigo()) {
            throw new IllegalArgumentException("Este repositório não acessa contas GitPay");
        }
    }

    @Override
    protected Map<String, Object> entityToMap(ContaExterna conta) {
        Map<String, Object> data = super.entityToMap(conta);
        data.put("nomeTitular", conta.getNomeTitular());
        data.put("docTitular", conta.getDocTitular());
        return data;
    }

    @Override
    protected CompletableFuture<ContaExterna> documentToEntity(DocumentSnapshot doc) {
        return CompletableFuture.completedFuture(new ContaExterna(
                TipoConta.valueOf(doc.getString("tipo")),
                toInt(doc.getLong("banco")),
                toInt(doc.getLong("agencia")),
                toInt(doc.getLong("numero")),
                toInt(doc.getLong("digito")),
                doc.getString("nomeTitular"),
                doc.getString("docTitular"))
        );
    }
}
