package br.ufms.gitpay.domain.model.banco;

import br.ufms.gitpay.domain.util.Validador;

import java.util.Objects;

public class Banco {

    private final int codigo;
    private final String nome;
    private final String razaoSocial;

    public static final Banco GitPay = new Banco(666, "GitPay", "GitPay Pagamentos S.I.");

    public Banco(int codigo, String nome, String razaoSocial) {
        Validador.validarCodigoBanco(codigo);
        Validador.validarNomeEmpresa(nome);
        Validador.validarRazaoSocial(razaoSocial);

        this.codigo = codigo;
        this.nome = nome;
        this.razaoSocial = razaoSocial;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getCodigoFormatado() {
        return String.format("%03d", codigo);
    }

    public String getNome() {
        return nome;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Banco banco)) return false;
        return codigo == banco.codigo;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codigo);
    }
}
