package br.ufms.gitpay.data.firebase.repository;

import br.ufms.gitpay.domain.model.banco.Banco;
import br.ufms.gitpay.domain.model.conta.ContaBancaria;
import br.ufms.gitpay.domain.model.transacao.*;
import com.google.cloud.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class TransacaoFirestoreRepository<T extends Transacao> extends FirestoreRepository<T, String> {

    protected TransacaoFirestoreRepository(int numeroConta) {
        super("bancos/" + Banco.GitPay.getCodigoFormatado() + "/contas/" + numeroConta + "-pg/transacoes");
    }

    @Override
    protected Optional<String> getId(Transacao transacao) {
        return transacao.getId();
    }

    @Override
    protected Map<String, Object> entityToMap(T transacao) {
        Map<String, Object> data = new HashMap<>();
        data.put("valor", transacao.getValor());
        data.put("tipo", transacao.getTipo());
        return data;
    }

    protected DocumentReference getContaRef(ContaBancaria conta) {
        String contaId = conta.getNumero() + "-" + conta.getTipo().getAbreviacao();
        return db.collection("bancos").document(conta.getBancoFormatado())
                .collection("contas").document(contaId);
    }
}
