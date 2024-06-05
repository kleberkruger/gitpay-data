package br.ufms.gitpay.domain.model.conta;

public interface ContaBancaria {

    TipoConta getTipo();

    int getBanco();

    int getAgencia();

    int getNumero();

    int getDigito();

    default String getNumeroDigito() {
        return getNumero() + "-" + getDigito();
    }

    default String getNumeroTipo() {
        return getNumero() + "-" + getTipo().getAbreviacao();
    }

    String getNomeTitular();

    String getDocumentoTitular();
}
