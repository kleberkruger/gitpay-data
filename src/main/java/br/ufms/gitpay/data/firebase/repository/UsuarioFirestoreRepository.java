package br.ufms.gitpay.data.firebase.repository;

import br.ufms.gitpay.domain.model.banco.Banco;
import br.ufms.gitpay.domain.model.usuario.*;
import com.google.cloud.firestore.DocumentSnapshot;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class UsuarioFirestoreRepository extends FirestoreRepository<Usuario<? extends Pessoa>, String> {

    public UsuarioFirestoreRepository() {
        super("bancos/" + Banco.GitPay.getCodigo() + "/usuarios");
    }

    @Override
    protected Optional<String> getId(Usuario<? extends Pessoa> usuario) {
        return Optional.of(usuario.getDocumento());
    }

    @Override
    protected Map<String, Object> entityToMap(Usuario<? extends Pessoa> usuario) {
        Map<String, Object> data = new HashMap<>();
        data.put("nome", usuario.getDados().getNome());
        data.put("telefone", usuario.getDados().getTelefone());
        data.put("email", usuario.getDados().getEmail());
        data.put("tipo", usuario.getDados().getTipo());
        data.put("senha", usuario.getSenha());
        if (usuario.getDados() instanceof PessoaFisica pf) {
            data.put("cpf", pf.getCpf());
            data.put("dataNascimento", toDate(pf.getDataNascimento()));
        } else if (usuario.getDados() instanceof PessoaJuridica pj) {
            data.put("cnpj", pj.getCnpj());
            data.put("razaoSocial", pj.getRazaoSocial());
        }
        return data;
    }

    @Override
    protected CompletableFuture<Usuario<? extends Pessoa>> documentToEntity(DocumentSnapshot doc) {
        String tipo = Objects.requireNonNull(doc.getString("tipo"));
        String nome = doc.getString("nome");
        String telefone = doc.getString("telefone");
        String email = doc.getString("email");
        String senha = doc.getString("senha");
        LocalDateTime dataHoraCadastro = toLocalDateTime(doc.getCreateTime());

        Usuario<? extends Pessoa> usuario;

        if (tipo.equals(TipoPessoa.PESSOA_FISICA.name())) {
            String cpf = doc.getString("cpf");
            LocalDate dataNascimento = toLocalDate(doc.getTimestamp("dataNascimento"));
            usuario = Usuario.criarPessoaFisica(nome, cpf, telefone, email, dataNascimento, senha, dataHoraCadastro);

        } else {
            String cnpj = doc.getString("cnpj");
            String razaoSocial = doc.getString("razaoSocial");
            usuario = Usuario.criarPessoaJuridica(nome, razaoSocial, cnpj, telefone, email, senha, dataHoraCadastro);
        }

        return CompletableFuture.completedFuture(usuario);
    }
}
