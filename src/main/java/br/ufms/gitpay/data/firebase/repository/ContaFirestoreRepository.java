package br.ufms.gitpay.data.firebase.repository;

import br.ufms.gitpay.data.repository.Repository;
import br.ufms.gitpay.domain.model.conta.*;
import com.google.cloud.firestore.CollectionReference;

import java.util.*;

public abstract class ContaFirestoreRepository<C extends ContaBancaria> extends FirestoreRepository<C, DadosConta> {

    public static final String COLLECTION_NAME = "contas";

    protected ContaFirestoreRepository(int banco) {
        super(db.collection(BancoFirestoreRepository.COLLECTION_NAME).document(String.format("%03d", banco))
                .collection(COLLECTION_NAME));
    }

    @Override
    protected Optional<String> getId(C conta) {
        return Optional.of(conta.getNumero() + "-" + conta.getTipo().getAbreviacao());
    }

    @Override
    protected CollectionReference getCollection(C c) {
        return db.collection(BancoFirestoreRepository.COLLECTION_NAME).document(c.getBancoFormatado())
                .collection(COLLECTION_NAME);
    }

    @Override
    protected String idToStr(DadosConta dadosConta) {
        return dadosConta.numero() + "-" + dadosConta.tipo().getAbreviacao();
    }

    @Override
    protected Map<String, Object> entityToMap(C conta) {
        Map<String, Object> data = new HashMap<>();
        data.put("tipo", conta.getTipo());
        data.put("banco", conta.getBanco());
        data.put("agencia", conta.getAgencia());
        data.put("numero", conta.getNumero());
        data.put("digito", conta.getDigito());
        return data;
    }
}
