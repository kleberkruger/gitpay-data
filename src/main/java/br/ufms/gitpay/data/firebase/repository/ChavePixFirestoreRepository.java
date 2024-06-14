package br.ufms.gitpay.data.firebase.repository;

import br.ufms.gitpay.data.repository.ChavePixRepository;
import br.ufms.gitpay.domain.model.banco.Banco;
import br.ufms.gitpay.domain.model.conta.*;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ChavePixFirestoreRepository extends FirestoreRepository<ChavePix, String> implements ChavePixRepository {

    public static final String COLLECTION_NAME = "chavesPix";

    public ChavePixFirestoreRepository() {
        super(db.collection(COLLECTION_NAME));
    }

    @Override
    protected Optional<String> getId(ChavePix chavePix) {
        return Optional.of(chavePix.getChave());
    }

    @Override
    protected CollectionReference getCollection(ChavePix chavePix) {
        return db.collection(COLLECTION_NAME);
    }

    private DocumentReference getContaRef(ContaBancaria conta) {
        return db.collection("bancos/" + conta.getBancoFormatado() + "/contas")
                .document(conta.getNumero() + "-" + conta.getTipo().getAbreviacao());
    }

    @Override
    protected Map<String, Object> entityToMap(ChavePix chavePix) {
        Map<String, Object> doc = new HashMap<>();
        doc.put("tipo", chavePix.getTipo());
        doc.put("chave", chavePix.getChave());
        doc.put("conta", getContaRef(chavePix.getContaBancaria()));
        return doc;
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
    protected CompletableFuture<ChavePix> documentToEntity(DocumentSnapshot document) {
        DocumentReference contaRef = document.get("conta", DocumentReference.class);
        ContaFirestoreRepository<? extends ContaBancaria> contaRepository;
        if (contaRef == null || (contaRepository = getContaRepository(contaRef)) == null) {
            return CompletableFuture.failedFuture(new NoSuchElementException("Conta não encontrada"));
        }
        return contaRepository.get(contaRef).thenCompose(conta -> conta.map(contaBancaria ->
                        CompletableFuture.completedFuture(new ChavePix(
                                TipoChavePix.valueOf(document.getString("tipo")),
                                document.getString("chave"), contaBancaria)))
                .orElseGet(() -> CompletableFuture.failedFuture(new NoSuchElementException("Conta não encontrada")))
        );
    }
}
