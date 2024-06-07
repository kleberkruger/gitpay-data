package br.ufms.gitpay.data.firebase.repository;

import br.ufms.gitpay.domain.model.banco.Banco;
import br.ufms.gitpay.domain.model.conta.ContaGitPay;
import br.ufms.gitpay.domain.model.usuario.Pessoa;
import br.ufms.gitpay.domain.model.usuario.Usuario;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

public class ContaGitPayFirestoreRepository extends ContaFirestoreRepository<ContaGitPay> {

    public ContaGitPayFirestoreRepository() {
        super(Banco.GitPay.getCodigo());
    }

    private DocumentReference getUsuarioRef(Usuario<? extends Pessoa> usuario) {
        return db.collection("bancos/" + Banco.GitPay.getCodigoFormatado() + "/usuarios")
                .document(usuario.getDocumento());
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
