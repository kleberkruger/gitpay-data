package br.ufms.gitpay.domain.model.usuario;

import br.ufms.gitpay.domain.util.Validador;

import java.util.Objects;

public class PessoaJuridica extends Pessoa {

    private final String cnpj;
    private String razaoSocial;

    public PessoaJuridica(String nome, String cnpj) {
        super(nome);

        Validador.validarCNPJ(cnpj);
        this.cnpj = cnpj;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        Validador.validarRazaoSocial(razaoSocial);
        this.razaoSocial = razaoSocial;
    }

    @Override
    public TipoPessoa getTipo() {
        return TipoPessoa.PESSOA_JURIDICA;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PessoaJuridica that)) return false;
        return Objects.equals(cnpj, that.cnpj);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cnpj);
    }
}
