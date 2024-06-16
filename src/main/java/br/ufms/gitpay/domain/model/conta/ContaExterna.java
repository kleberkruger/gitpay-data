package br.ufms.gitpay.domain.model.conta;

import br.ufms.gitpay.domain.util.Validador;

import java.util.Objects;

public class ContaExterna implements ContaBancaria {

    private final String banco;
    private final int agencia;
    private final int numero;
    private final int digito;
    private final TipoConta tipo;
    private final String nomeTitular;
    private final String documentoTitular;
    private final DadosConta dados;

    public ContaExterna(TipoConta tipo, int banco, int agencia, int numero, int digito,
                        String nomeTitular, String documentoTitular) {

        this(tipo, String.valueOf(banco), agencia, numero, digito, nomeTitular, documentoTitular);
    }

    public ContaExterna(TipoConta tipo, String banco, int agencia, int numero, int digito,
                        String nomeTitular, String documentoTitular) {

        Objects.requireNonNull(tipo, "O tipo da conta não pode ser nulo");
        switch (getTipoPessoa()) {
            case PESSOA_FISICA -> {
                Validador.validarNomePessoa(nomeTitular);
                Validador.validarCPF(documentoTitular);
            }
            case PESSOA_JURIDICA -> {
                Validador.validarNomeEmpresa(nomeTitular);
                Validador.validarCNPJ(documentoTitular);
            }
        }
        Validador.validarCodigoBanco(banco);
        Validador.validarIntervaloNumero("Agência", agencia, 1, 99999);
        Validador.validarIntervaloNumero("Número de conta", numero, 1, 99999999);
        Validador.validarIntervaloNumero("Dígito", numero, 0, 9);
        Validador.validarIntervaloNumero("Dígito", numero, 0, 9);

        this.banco = banco;
        this.agencia = agencia;
        this.numero = numero;
        this.digito = digito;
        this.tipo = tipo;
        this.nomeTitular = nomeTitular;
        this.documentoTitular = documentoTitular;
        this.dados = new DadosConta(tipo, banco, agencia, numero, digito);
    }

    @Override
    public TipoConta getTipoConta() {
        return tipo;
    }

    @Override
    public String getBanco() {
        return banco;
    }

    @Override
    public int getAgencia() {
        return agencia;
    }

    @Override
    public int getNumero() {
        return numero;
    }

    @Override
    public int getDigito() {
        return digito;
    }

    @Override
    public String getNomeTitular() {
        return nomeTitular;
    }

    @Override
    public String getDocTitular() {
        return documentoTitular;
    }

    @Override
    public DadosConta getDadosConta() {
        return dados;
    }
}
