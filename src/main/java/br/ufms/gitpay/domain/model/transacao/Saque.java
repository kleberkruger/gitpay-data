package br.ufms.gitpay.domain.model.transacao;

import br.ufms.gitpay.domain.model.conta.ContaBancaria;

import java.time.LocalDateTime;

public class Saque extends Transacao {

    private final ContaBancaria origem;

    public Saque(ContaBancaria origem, double valor) {
        this(null, origem, valor, null);
    }

    public Saque(String id, ContaBancaria origem, double valor, LocalDateTime dataHora) {
        super(id, valor, dataHora);

        this.origem = origem;
    }

    public ContaBancaria getOrigem() {
        return origem;
    }

    @Override
    public TipoTransacao getTipo() {
        return TipoTransacao.SAQUE;
    }
}
