package br.ufms.gitpay.domain.model.conta;

public abstract class ContaBancariaAbs implements ContaBancaria {

    private final int numero;
    private final int digito;
    private final DadosConta dados;

    public ContaBancariaAbs(int digito, int numero) {
        this.digito = digito;
        this.numero = numero;
        this.dados = new DadosConta(getTipoConta(), getBanco(), getAgencia(), getNumero(), getDigito());
    }

    @Override
    public abstract TipoConta getTipoConta();

    @Override
    public abstract String getBanco();

    @Override
    public abstract int getAgencia();

    @Override
    public int getNumero() {
        return numero;
    }

    @Override
    public int getDigito() {
        return digito;
    }

    @Override
    public abstract String getNomeTitular();

    @Override
    public abstract String getDocTitular();

    @Override
    public DadosConta getDadosConta() {
        return dados;
    }
}
