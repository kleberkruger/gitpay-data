package br.ufms.gitpay.data.firebase.repository;

import br.ufms.gitpay.domain.model.banco.Banco;
import br.ufms.gitpay.domain.model.conta.*;
import br.ufms.gitpay.domain.model.usuario.Pessoa;
import br.ufms.gitpay.domain.model.usuario.Usuario;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

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
        return Optional.of(conta.getNumero() + "-" + conta.getTipo().getAbreviacao());
    }

    private DocumentReference getUsuarioRef(Usuario<? extends Pessoa> usuario) {
        return FirestoreReferences.getUsuarioDocument(usuario.getDocumento());
    }

    @Override
    protected Map<String, Object> entityToMap(ContaBancaria conta) {
        Map<String, Object> data = new HashMap<>();

        data.put("tipo", conta.getTipo());
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

        ContaExterna c1 = new ContaExterna(TipoConta.CONTA_CORRENTE, "001", 552, 16942, 0,
                "Kleber Kruger", "02135730165");
        ContaGitPay c2 = new ContaGitPay(1, Usuario.criarPessoaFisica(
                "Kleber Kruger", "02135730165", "67996122809", "kleberkruger@gmail.com",
                LocalDate.of(1988, 12, 8), "123", LocalDateTime.now()));

        CompletableFuture<ContaBancaria> f1 = CompletableFuture.completedFuture(c1);
        CompletableFuture<ContaBancaria> f2 = CompletableFuture.completedFuture(c2);

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

    public CompletableFuture<Collection<ContaBancaria>> getAll(int codigoBanco) {
        return super.getAll();
    }
}
