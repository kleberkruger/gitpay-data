package br.ufms.gitpay.domain.service;

import br.ufms.gitpay.domain.model.banco.Banco;
import br.ufms.gitpay.domain.model.conta.ContaGitPay;
import br.ufms.gitpay.domain.model.conta.TipoConta;
import br.ufms.gitpay.domain.model.transacao.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ContaService {

    private final ContaGitPay conta;

    public ContaService(ContaGitPay conta) {
        this.conta = conta;
    }

    CompletableFuture<Optional<ContaService>> login(String cpfOuCnpj, String senha) {
        return null;
    }

    CompletableFuture<Void> logout() {
        return null;
    }

    CompletableFuture<List<Transacao>> consultarExtrato(LocalDate dataInicial, LocalDate dataFinal, TipoTransacao tipoTransacao) {
        return null;
    }

    CompletableFuture<Deposito> depositar(double valor) {
        return null;
    }

    CompletableFuture<Saque> sacar(double valor) {
        return null;
    }

    CompletableFuture<Transferencia> transferir(String chavePix) {
        return null;
    }

    CompletableFuture<Transferencia> transferir(int numero, int digito, double valor) {
        return transferir(TipoConta.CONTA_PAGAMENTO, Banco.GitPay.getCodigo(), 1, numero, digito, null, valor);
    }

    CompletableFuture<Transferencia> transferir(TipoConta tipo, int banco, int agencia, int numero, int digito,
                                                String documento, double valor) {
        return null;
    }

    CompletableFuture<Transferencia> investir(TipoInvestimento tipo, double valor) {
        return null;
    }
}
