package br.ufms.gitpay.data.firebase.repository;

import br.ufms.gitpay.data.repository.BancoRepository;
import br.ufms.gitpay.domain.model.banco.Banco;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Transaction;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class BancoFirestoreRepository extends FirestoreRepository<Banco, Integer> implements BancoRepository {

//    public static final String COLLECTION_NAME = "bancos";

    public BancoFirestoreRepository() {
        super(FirestoreReferences.getBancoCollection());
    }

    @Override
    protected Optional<String> getId(Banco banco) {
        return Optional.of(banco.getCodigo());
    }

    @Override
    protected String idToStr(Integer id) {
        return String.format("%03d", id);
    }

    @Override
    protected Map<String, Object> entityToMap(Banco banco) {
        Map<String, Object> data = new HashMap<>();
        data.put("codigo", banco.getCodigo());
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
