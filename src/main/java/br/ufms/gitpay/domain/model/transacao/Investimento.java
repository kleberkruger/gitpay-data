package br.ufms.gitpay.domain.model.transacao;

import java.time.LocalDateTime;

public class Investimento extends Transacao {

    private final TipoInvestimento tipo;
    private final float taxaJuros;
//    private LocalDate dataMinRetirada;

    public Investimento(TipoInvestimento tipo, float taxaJuros, double valor) {
        this(null, tipo, taxaJuros, valor, null);
    }

    public Investimento(String id, TipoInvestimento tipo, float taxaJuros, double valor, LocalDateTime dataHora) {
        super(id, valor, dataHora);
        this.tipo = tipo;
        this.taxaJuros = taxaJuros;
    }

    public TipoInvestimento getTipoInvestimento() {
        return tipo;
    }

    public float getTaxaJuros() {
        return taxaJuros;
    }

    @Override
    public TipoTransacao getTipo() {
        return TipoTransacao.INVESTIMENTO;
    }
}
