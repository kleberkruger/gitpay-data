package br.ufms.gitpay.domain.model.conta;

public interface ContaBancaria {

    TipoConta getTipo();

    int getBanco();

    default String getBancoFormatado() {
        return String.format("%03d", getBanco());
    }

    int getAgencia();

    int getNumero();

    int getDigito();

    default String getNumeroDigito() {
        return getNumero() + "-" + getDigito();
    }

    default DadosConta getDadosConta() {
        return new DadosConta(getTipo(), getBanco(), getAgencia(), getNumero(), getDigito());
    }

    String getNomeTitular();

    String getDocTitular();
}
