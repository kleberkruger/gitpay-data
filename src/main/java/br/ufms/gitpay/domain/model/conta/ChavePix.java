package br.ufms.gitpay.domain.model.conta;

import br.ufms.gitpay.domain.util.Validador;

import java.util.Objects;

public class ChavePix {

    private final TipoChavePix tipo;
    private final String chave;
    private final ContaBancaria conta;

    public ChavePix(TipoChavePix tipo, String chave, ContaBancaria dadosConta) {
        Objects.requireNonNull(tipo, "O tipo da chave pix não pode ser nulo");
        Objects.requireNonNull(tipo, "Os dados da conta não pode ser nulo");

        this.tipo = tipo;
        switch (tipo) {
            case TELEFONE -> Validador.validarTelefone(chave, true);
            case EMAIL -> Validador.validarEmail(chave, true);
            case CPF -> Validador.validarCPF(chave);
            case CNPJ -> Validador.validarCNPJ(chave);
        }
        this.chave = chave;
        this.conta = dadosConta;
    }

    public TipoChavePix getTipo() {
        return tipo;
    }

    public String getChave() {
        return chave;
    }

    public ContaBancaria getContaBancaria() {
        return conta;
    }
}
