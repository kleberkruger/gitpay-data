package br.ufms.gitpay.domain.model.usuario;

import br.ufms.gitpay.domain.util.Validador;

import java.time.LocalDate;
import java.util.Objects;

public class PessoaFisica extends Pessoa {

    private final String cpf;
    private LocalDate dataNascimento;

    public PessoaFisica(String nome, String cpf) {
        super(nome);

        Validador.validarCPF(cpf);
        this.cpf = cpf;
    }

    public String getCpf() {
        return cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        Validador.validarDataNascimento(dataNascimento);
        this.dataNascimento = dataNascimento;
    }

    @Override
    public TipoPessoa getTipo() {
        return TipoPessoa.PESSOA_FISICA;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PessoaFisica that)) return false;
        return Objects.equals(cpf, that.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cpf);
    }
}
