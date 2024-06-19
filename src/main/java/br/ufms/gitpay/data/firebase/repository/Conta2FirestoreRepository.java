package br.ufms.gitpay.data.firebase.repository;

import br.ufms.gitpay.domain.model.banco.Banco;
import br.ufms.gitpay.domain.model.conta.*;
import br.ufms.gitpay.domain.model.usuario.Pessoa;
import br.ufms.gitpay.domain.model.usuario.Usuario;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Conta2FirestoreRepository extends FirestoreRepository<ContaBancaria, DadosConta> {

    public static final String COLLECTION_NAME = "contas";

    public Conta2FirestoreRepository() {
        super(null);
    }

    protected CollectionReference getCollection(DadosConta dadosConta) {
        String codigoBanco = dadosConta != null ? dadosConta.banco() : Banco.GitPay.getCodigo();
        return FirestoreReferences.getContaCollection(codigoBanco);
    }

    @Override
    protected Optional<String> getId(ContaBancaria conta) {
        return Optional.of(conta.getNumero() + "-" + conta.getTipoConta().getAbreviacao());
    }

    private DocumentReference getUsuarioRef(Usuario<? extends Pessoa> usuario) {
        return FirestoreReferences.getUsuarioDocument(usuario.getDocumento());
    }

    @Override
    protected Map<String, Object> entityToMap(ContaBancaria conta) {
        Map<String, Object> data = new HashMap<>();

        data.put("tipo", conta.getTipoConta());
        data.put("banco", conta.getBanco());
        data.put("agencia", conta.getAgencia());
        data.put("numero", conta.getNumero());
        data.put("digito", conta.getDigito());

        if (conta instanceof ContaGitPay c) {
            data.put("saldo", c.getSaldo());
            data.put("limite", c.getLimite());
            data.put("usuario", getUsuarioRef(c.getUsuario()));
        } else {
            data.put("nomeTitular", conta.getNomeTitular());
            data.put("docTitular", conta.getDocTitular());
        }

        return data;
    }

    @Override
    protected CompletableFuture<ContaBancaria> documentToEntity(DocumentSnapshot doc) {
        String banco = doc.getString("banco");

        if (banco.equals(Banco.GitPay.getCodigo())) {
            var usuarioRef = doc.get("usuario", DocumentReference.class);
            if (usuarioRef == null) {
                return CompletableFuture.failedFuture(new NoSuchElementException("Usuário não encontrado 1"));
            }
            return new UsuarioFirestoreRepository().get(usuarioRef).thenCompose(usuarioOpt ->
                    CompletableFuture.completedFuture(new ContaGitPay(1, usuarioOpt.orElse(null))));
        }

        return CompletableFuture.completedFuture(
                new ContaExterna(TipoConta.valueOf(doc.getString("tipo")),
                        banco, toInt(doc.getLong("agencia")),
                        toInt(doc.getLong("numero")), toInt(doc.getLong("digito")),
                        doc.getString("nomeTitular"), doc.getString("docTitular")
                )
        );
    }

    public CompletableFuture<Collection<ContaBancaria>> getAll(String codigoBanco) {
        return toCompletableFuture(FirestoreReferences.getContaCollection(codigoBanco).get())
                .thenCompose(queryDocumentSnapshots -> {
                    List<CompletableFuture<ContaBancaria>> futures = queryDocumentSnapshots.getDocuments().stream()
                            .map(this::documentToEntity)
                            .toList();
                    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                            .thenApply(v -> futures.stream()
                                    .map(CompletableFuture::join)
                                    .collect(Collectors.toList()));
                });
    }
}
