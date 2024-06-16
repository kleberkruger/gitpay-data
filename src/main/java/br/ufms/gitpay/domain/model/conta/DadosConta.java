package br.ufms.gitpay.domain.model.conta;

public record DadosConta(TipoConta tipo, String banco, int agencia, int numero, int digito) {

    public String agenciaString() {
        return String.format("%05d", agencia);
    }

    public String numeroDigito() {
        return String.format("%5d-%1d", numero, digito);
    }
}
