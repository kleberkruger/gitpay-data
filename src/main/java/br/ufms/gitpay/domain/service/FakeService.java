package br.ufms.gitpay.domain.service;

import br.ufms.gitpay.data.repository.BancoRepository;
import br.ufms.gitpay.data.repository.ChavePixRepository;
import br.ufms.gitpay.data.repository.ContaExternaRepository;
import br.ufms.gitpay.domain.model.banco.Banco;
import br.ufms.gitpay.domain.model.conta.*;

import java.util.concurrent.CompletableFuture;

public class FakeService {

    private final BancoRepository bancoRepository;
    private final ContaExternaRepository contaRepository;
    private final ChavePixRepository chavePixRepository;

    public FakeService(BancoRepository bancoRepository, ContaExternaRepository contaRepository,
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

    public CompletableFuture<ContaExterna> adicionarConta(ContaExterna conta) {
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
