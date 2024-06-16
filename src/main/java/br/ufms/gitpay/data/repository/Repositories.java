package br.ufms.gitpay.data.repository;

import br.ufms.gitpay.domain.model.conta.ContaBancaria;

public interface Repositories {

    BancoRepository getBancoRepository();

    UsuarioRepository getUsuarioRepository();

    ChavePixRepository getChavePixRepository();

//    ContaRepository<? extends ContaBancaria> getContaRepository(int numeroBanco);

    TransacaoRepository getTransacaoRepository();
}
