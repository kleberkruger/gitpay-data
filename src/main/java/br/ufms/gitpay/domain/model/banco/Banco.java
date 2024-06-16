package br.ufms.gitpay.domain.model.banco;

import br.ufms.gitpay.domain.util.Validador;

import java.util.Objects;

public class Banco {

    public static final Banco GitPay = new Banco(666, "GitPay", "GitPay Pagamentos S.I.");

    private final String codigo;
    private final String nome;
    private final String razaoSocial;

    public Banco(int codigo, String nome, String razaoSocial) {
        this(String.valueOf(codigo), nome, razaoSocial);
    }

    public Banco(String codigo, String nome, String razaoSocial) {
        Validador.validarCodigoBanco(codigo);
        Validador.validarNomeEmpresa(nome);
        Validador.validarRazaoSocial(razaoSocial);

        this.codigo = String.format("%03d", Integer.parseInt(codigo));
        this.nome = nome;
        this.razaoSocial = razaoSocial;
    }

    public String getCodigo() {
        return codigo;
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
        return Objects.equals(codigo, banco.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codigo);
    }
}
