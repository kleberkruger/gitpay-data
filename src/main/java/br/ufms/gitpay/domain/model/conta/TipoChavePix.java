package br.ufms.gitpay.domain.model.conta;

public enum TipoChavePix {

    ALEATORIA("Aleatória"),
    TELEFONE("Telefone"),
    EMAIL("Email"),
    CPF("CPF"),
    CNPJ("CNPJ");

    TipoChavePix(String descricao) {
        this.descricao = descricao;
    }

    private final String descricao;

    public String getDescricao() {
        return descricao;
    }
}
