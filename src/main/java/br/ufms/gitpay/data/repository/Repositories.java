package br.ufms.gitpay.data.repository;

public interface Repositories {

    BancoRepository getBancoRepository();

    UsuarioRepository getUsuarioRepository();

    ChavePixRepository getChavePixRepository();

    ContaRepository getContaRepository();

    TransacaoRepository getTransacaoRepository();
}
