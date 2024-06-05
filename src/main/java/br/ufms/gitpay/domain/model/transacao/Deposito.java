package br.ufms.gitpay.domain.model.transacao;

import br.ufms.gitpay.domain.model.conta.ContaBancaria;

import java.time.LocalDateTime;

public class Deposito extends Transacao {

    private final ContaBancaria destino;

    public Deposito(ContaBancaria destino, double valor) {
        this(null, destino, valor, null);
    }

    public Deposito(String id, ContaBancaria destino, double valor, LocalDateTime dataHora) {
        super(id, valor, dataHora);

        this.destino = destino;
    }

    public ContaBancaria getDestino() {
        return destino;
    }

    @Override
    public TipoTransacao getTipo() {
        return TipoTransacao.DEPOSITO;
    }
}
