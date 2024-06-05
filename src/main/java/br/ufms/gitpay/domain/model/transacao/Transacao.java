package br.ufms.gitpay.domain.model.transacao;

import java.time.LocalDateTime;
import java.util.Optional;

public abstract class Transacao {

    private final String id;
    private final double valor;
    private final LocalDateTime dataHora;

    protected Transacao(double valor) {
        this(null, valor, null);
    }

    protected Transacao(String id, double valor, LocalDateTime dataHora) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor invalido");
        }
        this.id = id;
        this.valor = valor;
        this.dataHora = dataHora;
    }

    public Optional<String> getId() {
        return Optional.ofNullable(id);
    }

    public double getValor() {
        return valor;
    }

    public Optional<LocalDateTime> getDataHora() {
        return Optional.ofNullable(dataHora);
    }

    public boolean isEfetuada() {
        return id != null;
    }

    public abstract TipoTransacao getTipo();
}
