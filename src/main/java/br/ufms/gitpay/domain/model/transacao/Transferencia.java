package br.ufms.gitpay.domain.model.transacao;

import br.ufms.gitpay.domain.model.conta.ContaBancaria;

import java.time.LocalDateTime;

public class Transferencia extends Transacao {

    private final ContaBancaria origem;
    private final ContaBancaria destino;

    public Transferencia(double valor, ContaBancaria origem, ContaBancaria destino) {
        this(null, origem, destino, valor, null);
    }

    public Transferencia(String id, ContaBancaria origem, ContaBancaria destino, double valor, LocalDateTime dataHora) {
        super(id, valor, dataHora);

        this.origem = origem;
        this.destino = destino;
    }

    public ContaBancaria getOrigem() {
        return origem;
    }

    public ContaBancaria getDestino() {
        return destino;
    }

    @Override
    public TipoTransacao getTipo() {
        return TipoTransacao.TRANSFERENCIA;
    }
}
