package br.ufms.gitpay.domain.service;

import br.ufms.gitpay.data.repository.BancoRepository;
import br.ufms.gitpay.data.repository.ChavePixRepository;
import br.ufms.gitpay.data.repository.ContaRepository;
import br.ufms.gitpay.domain.model.banco.Banco;
import br.ufms.gitpay.domain.model.conta.ChavePix;
import br.ufms.gitpay.domain.model.conta.ContaBancaria;
import br.ufms.gitpay.domain.model.conta.DadosConta;
import br.ufms.gitpay.domain.model.conta.TipoConta;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class BancoService {

    private final BancoRepository bancoRepository;
    private final ChavePixRepository chavePixRepository;
    private final ContaRepository contaRepository;

    public BancoService(BancoRepository bancoRepository, ContaRepository contaRepository,
                        ChavePixRepository chavePixRepository) {
        this.bancoRepository = bancoRepository;
        this.contaRepository = contaRepository;
        this.chavePixRepository = chavePixRepository;
    }

    public CompletableFuture<Optional<Banco>> getBanco(int codigo) {
        return bancoRepository.get(codigo);
    }

    public CompletableFuture<Collection<Banco>> getBancos() {
        return bancoRepository.getAll();
    }

    public CompletableFuture<Optional<ContaBancaria>> getConta(String chavePix) {
//        return chavePixRepository.get(chavePix).thenApply(chavePixOpt -> chavePixOpt.map(ChavePix::getDadosConta));
        return null;
    }

    public CompletableFuture<Optional<ContaBancaria>> getConta(int numero, int digito) {
        return getConta(new DadosConta(TipoConta.CONTA_PAGAMENTO, Banco.GitPay.getCodigo(), 1, numero, digito));
    }

    public CompletableFuture<Optional<ContaBancaria>> getConta(DadosConta dadosConta) {
        return null;
    }
}
