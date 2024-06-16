package br.ufms.gitpay.domain.model.conta;

public record DadosConta(TipoConta tipo, String banco, int agencia, int numero, int digito) {

    public String agenciaFormatado() {
        return String.format("%04d", agencia);
    }

    public String numeroDigito() {
        return String.format("%4d-%1d", numero, digito);
    }
}
