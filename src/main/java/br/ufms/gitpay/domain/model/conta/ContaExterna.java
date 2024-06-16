package br.ufms.gitpay.domain.model.conta;

public class ContaExterna implements ContaBancaria {

    private final String banco;
    private final int agencia;
    private final int numero;
    private final int digito;
    private final TipoConta tipo;
    private final String nomeTitular;
    private final String documentoTitular;

    public ContaExterna(TipoConta tipo, String banco, int agencia, int numero, int digito,
                        String nomeTitular, String documentoTitular) {
        this.banco = banco;
        this.agencia = agencia;
        this.numero = numero;
        this.digito = digito;
        this.tipo = tipo;
        this.nomeTitular = nomeTitular;
        this.documentoTitular = documentoTitular;
    }

    @Override
    public TipoConta getTipo() {
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
}
