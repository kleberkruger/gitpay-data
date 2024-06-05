package br.ufms.gitpay.domain.model.transacao;

public enum TipoTransacao {

    DEPOSITO("Depósito"),
    SAQUE("Saque"),
    TRANSFERENCIA("Transferência"),
    PIX("Pix"),
    INVESTIMENTO("Investimento");

    TipoTransacao(String descricao) {
        this.descricao = descricao;
    }

    private final String descricao;

    public String getDescricao() {
        return descricao;
    }
}
