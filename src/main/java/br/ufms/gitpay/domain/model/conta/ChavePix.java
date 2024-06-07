package br.ufms.gitpay.domain.model.conta;

import br.ufms.gitpay.domain.util.Validador;

public class ChavePix {

    private final TipoChavePix tipo;
    private final String chave;
    private final ContaBancaria conta;

    public ChavePix(TipoChavePix tipo, String chave, ContaBancaria dadosConta) {
        this.tipo = tipo;
        switch (tipo) {
            case TELEFONE -> Validador.validarTelefone(chave);
            case EMAIL -> Validador.validarEmail(chave);
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
