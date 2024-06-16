package br.ufms.gitpay.domain.model.conta;

import br.ufms.gitpay.domain.model.banco.Banco;
import br.ufms.gitpay.domain.model.transacao.TipoInvestimento;
import br.ufms.gitpay.domain.model.usuario.Pessoa;
import br.ufms.gitpay.domain.model.usuario.Usuario;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ContaGitPay implements ContaBancaria {

    private final int numero;
    private final int digito;
    private final Usuario<? extends Pessoa> usuario;
    private double saldo;
    private double limite;
    private final Map<TipoInvestimento, Double> saldoInvestimentos;
    private final DadosConta dados;

    public ContaGitPay(Usuario<? extends Pessoa> usuario) {
        this(0, usuario);
    }

    public ContaGitPay(int numero, Usuario<? extends Pessoa> usuario) {
        this.usuario = Objects.requireNonNull(usuario, "Usuário nulo");
        this.numero = numero;
        this.digito = digitoVerificador(numero);
        this.saldo = 0.00;
        this.limite = 0.00;
        this.saldoInvestimentos = new HashMap<>();

        this.dados = new DadosConta(TipoConta.CONTA_PAGAMENTO, Banco.GitPay.getCodigo(), 1, numero, digito);
    }

    @Override
    public TipoConta getTipoConta() {
        return TipoConta.CONTA_PAGAMENTO;
    }

    @Override
    public String getBanco() {
        return Banco.GitPay.getCodigo();
    }

    @Override
    public int getAgencia() {
        return 1;
    }

    @Override
    public int getNumero() {
        return numero;
    }

    @Override
    public int getDigito() {
        return digito;
    }

    public Usuario<? extends Pessoa> getUsuario() {
        return usuario;
    }

    @Override
    public String getNomeTitular() {
        return usuario.getDados().getNome();
    }

    @Override
    public String getDocTitular() {
        return usuario.getDocumento();
    }

    @Override
    public DadosConta getDadosConta() {
        return dados;
    }

    public double getSaldo() {
        return saldo;
    }

    public double getSaldoInvestimento(TipoInvestimento tipoInvestimento) {
        return saldoInvestimentos.computeIfAbsent(tipoInvestimento, k -> 0.0);
    }

    public Map<TipoInvestimento, Double> getSaldoInvestimentos() {
        return Collections.unmodifiableMap(saldoInvestimentos);
    }

    public double getLimite() {
        return limite;
    }

    public void setLimite(double limite) {
        if (limite < 0) {
            throw new IllegalArgumentException("Valor inválido");
        }
        this.limite = limite;
    }

    public static int digitoVerificador(int numero) {
        int[] digitos = new StringBuilder(numero).reverse().chars().map(Character::getNumericValue).toArray();
        int soma = 0;
        int multiplicador = 2;

        for (int digito : digitos) {
            soma += digito * multiplicador;
            multiplicador = multiplicador == 9 ? 2 : multiplicador + 1;
        }

        int resto = soma % 11;
        return resto == 0 || resto == 1 ? 0 : 11 - resto;
    }
}
