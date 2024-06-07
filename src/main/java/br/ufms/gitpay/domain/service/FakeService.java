package br.ufms.gitpay.domain.service;

import br.ufms.gitpay.data.repository.BancoRepository;
import br.ufms.gitpay.data.repository.ChavePixRepository;
import br.ufms.gitpay.data.repository.ContaRepository;
import br.ufms.gitpay.domain.model.banco.Banco;
import br.ufms.gitpay.domain.model.conta.ChavePix;
import br.ufms.gitpay.domain.model.conta.ContaBancaria;
import br.ufms.gitpay.domain.model.conta.DadosConta;
import br.ufms.gitpay.domain.model.conta.TipoConta;

import java.util.concurrent.CompletableFuture;

public class FakeService {

    private final BancoRepository bancoRepository;
    private final ContaRepository contaRepository;
    private final ChavePixRepository chavePixRepository;

    public FakeService(BancoRepository bancoRepository, ContaRepository contaRepository,
                       ChavePixRepository chavePixRepository) {

        this.bancoRepository = bancoRepository;
        this.contaRepository = contaRepository;
        this.chavePixRepository = chavePixRepository;
    }

    public CompletableFuture<Banco> adicionarBanco(Banco banco) {
        return bancoRepository.save(banco);
    }

    public CompletableFuture<Void> excluirBanco(int codigo) {
        return bancoRepository.delete(codigo);
    }

    public CompletableFuture<ContaBancaria> adicionarConta(ContaBancaria conta) {
        return contaRepository.save(conta);
    }

    public CompletableFuture<Void> excluirConta(int codigoBanco, int numeroConta, TipoConta tipoConta) {
        return contaRepository.delete(new DadosConta(tipoConta, codigoBanco, 0, numeroConta, 0));
    }

    public CompletableFuture<ChavePix> adicionarChavePix(ChavePix chavePix) {
        return chavePixRepository.save(chavePix);
    }

    public CompletableFuture<Void> excluirChavePix(String chavePix) {
        return chavePixRepository.delete(chavePix);
    }
}
