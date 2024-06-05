package br.ufms.gitpay.domain.model.usuario;

import br.ufms.gitpay.domain.util.Validador;

import java.time.LocalDateTime;
import java.util.Objects;

public class Usuario<P extends Pessoa> {

    private final P dados;
    private String senha;
    private final LocalDateTime dataHoraCadastro;

    public Usuario(P dados, String senha, LocalDateTime dataCadastro) {
        this.dados = dados;
        this.senha = senha;
        this.dataHoraCadastro = dataCadastro;
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
}
