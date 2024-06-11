package br.ufms.gitpay.domain.model.conta;

public interface ContaBancaria {

    TipoConta getTipo();

    int getBanco();

    int getAgencia();

    int getNumero();

    int getDigito();

    String getNomeTitular();

    String getDocTitular();

    default String getBancoFormatado() {
        return String.format("%03d", getBanco());
    }

    default String getAgenciaFormatado() {
        return String.format("%04d", getAgencia());
    }

    default String getNumeroDigito() {
        return String.format("%4d-%1d", getNumero(), getDigito());
    }

    default DadosConta getDadosConta() {
        return new DadosConta(getTipo(), getBanco(), getAgencia(), getNumero(), getDigito());
    }
}
