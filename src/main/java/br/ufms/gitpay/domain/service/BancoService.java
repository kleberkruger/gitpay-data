package br.ufms.gitpay.domain.service;

import br.ufms.gitpay.data.repository.*;
import br.ufms.gitpay.domain.model.banco.Banco;
import br.ufms.gitpay.domain.model.conta.ChavePix;
import br.ufms.gitpay.domain.model.conta.ContaBancaria;
import br.ufms.gitpay.domain.model.conta.DadosConta;
import br.ufms.gitpay.domain.model.conta.TipoConta;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class BancoService {

    private final Repositories repositories;
    private final BancoRepository bancos;
    private final ChavePixRepository chavesPix;

    public BancoService(Repositories repositories) {
        this.repositories = repositories;

        this.bancos = repositories.getBancoRepository();
        this.chavesPix = repositories.getChavePixRepository();
    }

    public CompletableFuture<Optional<Banco>> getBanco(int codigo) {
        return bancos.get(codigo);
    }

    public CompletableFuture<Collection<Banco>> getBancos() {
        return bancos.getAll();
    }

    public CompletableFuture<Optional<ContaBancaria>> getConta(String chavePix) {
        return chavesPix.get(chavePix).thenApply(chavePixOpt -> chavePixOpt.map(ChavePix::getContaBancaria));
    }

    public CompletableFuture<Optional<ContaBancaria>> getConta(int numero, int digito) {
        return getConta(new DadosConta(TipoConta.CONTA_PAGAMENTO, Banco.GitPay.getCodigo(), 1, numero, digito));
    }

    public CompletableFuture<Optional<ContaBancaria>> getConta(DadosConta dadosConta) {
//        var contaRepository = repositories.getContaRepository(dadosConta.banco());
//        if (dadosConta.banco() != Banco.GitPay.getCodigo()) {
//            contaRepository.get(dadosConta);
//        }
//
//        return contaRepository.get(dadosConta).thenApply(contaGitPay -> Optional.of(contaGitPay.get()));
        return null;
    }
}
