package br.ufms.gitpay.data.firebase.repository;

import br.ufms.gitpay.domain.model.banco.Banco;
import com.google.cloud.firestore.DocumentSnapshot;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class BancoFirestoreRepository extends FirestoreRepository<Banco, Integer> {

    public static final String COLLECTION_NAME = "bancos";

    public BancoFirestoreRepository() {
        super(COLLECTION_NAME);
    }

    @Override
    protected Optional<String> getId(Banco banco) {
        return Optional.of(banco.getCodigoFormatado());
    }

    @Override
    protected String idToStr(Integer id) {
        return String.format("%03d", id);
    }

    @Override
    protected Map<String, Object> entityToMap(Banco banco) {
        Map<String, Object> data = new HashMap<>();
        data.put("codigo", banco.getCodigoFormatado());
        data.put("nome", banco.getNome());
        data.put("razaoSocial", banco.getRazaoSocial());
        return data;
    }

    @Override
    protected CompletableFuture<Banco> documentToEntity(DocumentSnapshot doc) {
        int codigo = Integer.parseInt(Objects.requireNonNull(doc.getString("codigo")));
        String nome = doc.getString("nome");
        String razaoSocial = doc.getString("razaoSocial");
        return CompletableFuture.completedFuture(new Banco(codigo, nome, razaoSocial));
    }
}
