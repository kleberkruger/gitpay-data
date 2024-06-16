package br.ufms.gitpay.data.firebase.repository;

import br.ufms.gitpay.data.repository.ContaGitPayRepository;
import br.ufms.gitpay.domain.model.banco.Banco;
import br.ufms.gitpay.domain.model.conta.ContaGitPay;
import br.ufms.gitpay.domain.model.conta.DadosConta;
import br.ufms.gitpay.domain.model.usuario.Pessoa;
import br.ufms.gitpay.domain.model.usuario.Usuario;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Transaction;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ContaGitPayFirestoreRepository extends ContaFirestoreRepository<ContaGitPay> implements ContaGitPayRepository {

    public ContaGitPayFirestoreRepository() {
        super(Banco.GitPay.getCodigo());
    }

    private DocumentReference getUsuarioRef(Usuario<? extends Pessoa> usuario) {
        return db.collection("bancos/" + Banco.GitPay.getCodigo() + "/usuarios")
                .document(usuario.getDocumento());
    }

    private CompletableFuture<Integer> getProximoNumeroConta(Transaction transaction) {
        var bancoRef = FirestoreReferences.getBancoGitPayDocument();
        var indiceConta = "indiceConta";

        return toCompletableFuture(transaction.get(bancoRef))
                .thenApply(documentSnapshot -> documentSnapshot.contains(indiceConta) ?
                        Math.toIntExact(Objects.requireNonNull(documentSnapshot.getLong(indiceConta))) : 0)
                .thenApply(numero -> {
                    transaction.update(bancoRef, indiceConta, ++numero);
                    return numero;
                });
    }

    private int getNumeroConta(Transaction transaction) throws ExecutionException, InterruptedException {
        var campo = "indiceConta";
        var bancoRef = FirestoreReferences.getBancoGitPayDocument();
        var doc = transaction.get(bancoRef).get();
        var numero = doc.contains(campo) ? Math.toIntExact(Objects.requireNonNull(doc.getLong(campo))) : 0;
        transaction.update(bancoRef, campo, ++numero);
        return numero;
    }

    @Override
    public CompletableFuture<ContaGitPay> save(ContaGitPay contaGitPay) {
        return toCompletableFuture(db.runTransaction(transaction -> {
            var numeroConta = getNumeroConta(transaction);
            var contaRef = collection.document(numeroConta + "-pg");
            new UsuarioFirestoreRepository().save(transaction, contaGitPay.getUsuario());
            transaction.create(contaRef, entityToMap(contaGitPay));
            transaction.update(contaRef, "numero", numeroConta);
            return contaRef;
        })).thenCompose(contaRef -> toCompletableFuture(contaRef.get())
                .thenCompose(this::documentToEntity));
    }

    @Override
    public CompletableFuture<Void> delete(DadosConta dadosConta) {
//        return toCompletableFuture(db.recursiveDelete(db.collection(collectionName).document(idToStr(dadosConta))));

        return get(dadosConta).thenCompose(conta -> {
            if (conta.isPresent()) {
                return toCompletableFuture(db.runTransaction(transaction -> {
                    new UsuarioFirestoreRepository().delete(transaction, conta.get().getUsuario().getDocumento());
                    transaction.delete(collection.document(idToStr(dadosConta)));
                    return null;
                }));
            } else {
                return CompletableFuture.completedFuture(null);
            }
        });
    }

    @Override
    protected Map<String, Object> entityToMap(ContaGitPay conta) {
        Map<String, Object> data = super.entityToMap(conta);
        data.put("saldo", conta.getSaldo());
        data.put("limite", conta.getLimite());
        data.put("usuario", getUsuarioRef(conta.getUsuario()));
        return data;
    }

    @Override
    protected CompletableFuture<ContaGitPay> documentToEntity(DocumentSnapshot doc) {
        var usuarioRef = doc.get("usuario", DocumentReference.class);
        if (usuarioRef == null) {
            return CompletableFuture.failedFuture(new NoSuchElementException("Usuário não encontrado 1"));
        }
        return new UsuarioFirestoreRepository().get(usuarioRef).thenCompose(usuarioOpt ->
                usuarioOpt.map(usuario -> CompletableFuture.completedFuture(
                                new ContaGitPay(toInt(doc.getLong("numero")), usuario)))
                        .orElseGet(() -> CompletableFuture.failedFuture(
                                new NoSuchElementException("Usuário não encontrado 2"))));
    }
}
