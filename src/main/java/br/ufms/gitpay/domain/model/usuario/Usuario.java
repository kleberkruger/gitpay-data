package br.ufms.gitpay.domain.model.usuario;

import br.ufms.gitpay.domain.util.Validador;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Usuario<P extends Pessoa> {

    private final P dados;
    private String senha;
    private final LocalDateTime dataHoraCadastro;

    public Usuario(P dados, String senha, LocalDateTime dataHoraCadastro) {
        this.dados = dados;
        this.senha = senha;
        this.dataHoraCadastro = dataHoraCadastro;
    }

    public P getDados() {
        return dados;
    }

    public String getDocumento() {
        return dados instanceof PessoaFisica ? ((PessoaFisica) dados).getCpf() : ((PessoaJuridica) dados).getCnpj();
    }

    public String getLogin() {
        return getDocumento();
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        Validador.validarSenha(senha);
        this.senha = senha;
    }

    public LocalDateTime getDataHoraCadastro() {
        return dataHoraCadastro;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario<?> usuario)) return false;
        return Objects.equals(dados, usuario.dados) && Objects.equals(senha, usuario.senha) && Objects.equals(dataHoraCadastro, usuario.dataHoraCadastro);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dados, senha, dataHoraCadastro);
    }

    public static Usuario<PessoaFisica> criarPessoaFisica(
            String nome,
            String cpf,
            String telefone,
            String email,
            LocalDate dataNascimento,
            String senha,
            LocalDateTime dataCadastro
    ) {
        PessoaFisica pf = new PessoaFisica(nome, cpf);
        pf.setTelefone(telefone);
        pf.setEmail(email);
        pf.setDataNascimento(dataNascimento);

        return new Usuario<>(pf, senha, dataCadastro);
    }

    public static Usuario<PessoaJuridica> criarPessoaJuridica(
            String nome,
            String razaoSocial,
            String cnpj,
            String telefone,
            String email,
            String senha,
            LocalDateTime dataCadastro
    ) {
        PessoaJuridica pj = new PessoaJuridica(nome, cnpj);
        pj.setRazaoSocial(razaoSocial);
        pj.setTelefone(telefone);
        pj.setEmail(email);

        return new Usuario<>(pj, senha, dataCadastro);
    }
}
