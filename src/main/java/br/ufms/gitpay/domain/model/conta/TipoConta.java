package br.ufms.gitpay.domain.model.conta;

public enum TipoConta {

    CONTA_CORRENTE("Conta Corrente", "cc"),
    CONTA_POUPANCA("Conta Poupança", "cp"),
    CONTA_PAGAMENTO("Conta de Pagamento", "pg");

    TipoConta(String descricao, String abreviacao) {
        this.descricao = descricao;
        this.abreviacao = abreviacao;
    }

    private final String descricao;
    private final String abreviacao;

    public String getDescricao() {
        return descricao;
    }

    public String getAbreviacao() {
        return abreviacao;
    }
}
