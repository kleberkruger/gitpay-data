package br.ufms.gitpay.domain.model.conta;

public record DadosConta(TipoConta tipo, int banco, int agencia, int numero, int digito) {
}
