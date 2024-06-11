package br.ufms.gitpay.domain.service;

import br.ufms.gitpay.data.repository.*;
import br.ufms.gitpay.domain.model.banco.Banco;
import br.ufms.gitpay.domain.model.conta.ContaGitPay;
import br.ufms.gitpay.domain.model.conta.TipoConta;
import br.ufms.gitpay.domain.model.transacao.*;
import br.ufms.gitpay.domain.model.usuario.Pessoa;
import br.ufms.gitpay.domain.model.usuario.Usuario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ContaService {

    private final ContaGitPay conta;
    private final Repositories repositories;

    public ContaService(Repositories repositories, ContaGitPay conta) {
        this.conta = conta;
        this.repositories = repositories;
    }

    public CompletableFuture<Optional<ContaService>> login(String cpfOuCnpj, String senha) {
        return repositories.getUsuarioRepository().get(cpfOuCnpj).thenCompose(usuario -> {
            if (usuario.isEmpty()) {
                throw new RuntimeException("Usuário inexistente");
            } else if (!usuario.get().getSenha().equals(senha)) {
                throw new RuntimeException("Senha incorreta");
            }
            return null;
        });
    }

    public CompletableFuture<Void> logout() {
        return null;
    }

    public CompletableFuture<List<Transacao>> consultarExtrato(LocalDate dataInicial, LocalDate dataFinal,
                                                               TipoTransacao tipoTransacao) {
        return null;
    }

    public CompletableFuture<Deposito> depositar(double valor) {
//        return contaRepository.runTransaction(t -> contaRepository.save(conta)
//                        .thenCompose(cb -> {
//                            Deposito deposito = new Deposito(cb, valor);
//                            return transacaoRepository.save(deposito);
//                        }).thenApply(dep -> (Deposito) dep))
//                .thenCompose(r -> r);

//        return transacaoRepository.runTransaction(t -> {
//            var contaRef = getContaRef(((Deposito) transacao).getDestino());
//            transaction.update(contaRef, "saldo", novoSaldo);
//            var transacaoRef = db.collection(collectionName).document();
//            transaction.create(transacaoRef, entityToMap(transacao));
//            return transacaoRef;
//        }).thenCompose(transRef -> toCompletableFuture(transRef.get())
//                .thenCompose(this::documentToEntity));

//        return transacaoRepository.saveTransaction(new Deposito(conta, 100000), 1000000.0)
//                .thenApply(d -> (Deposito) d);

        return null;
    }

    public CompletableFuture<Saque> sacar(double valor) {
        return null;
    }

    public CompletableFuture<Transferencia> transferir(String chavePix) {
        return null;
    }

    public CompletableFuture<Transferencia> transferir(int numero, int digito, double valor) {
        return transferir(TipoConta.CONTA_PAGAMENTO, Banco.GitPay.getCodigo(), 1, numero, digito, null, valor);
    }

    public CompletableFuture<Transferencia> transferir(TipoConta tipo, int banco, int agencia, int numero, int digito,
                                                       String documento, double valor) {

        var usuario = Usuario.criarPessoaFisica("Kleber Kruger", "02135730165", "67996122809",
                "kleberkruger@gmail.com", LocalDate.of(1988, 12, 8), "123", LocalDateTime.now());
        ContaGitPay conta = new ContaGitPay(1, usuario);
        ContaGitPay outraConta = new ContaGitPay(2, usuario);

//        return transacaoRepository.saveTransaction(new Transferencia(conta, outraConta, 100000), 1000000.0)
//                .thenApply(t -> (Transferencia) t);

        return null;
    }

    public CompletableFuture<Transferencia> investir(TipoInvestimento tipo, double valor) {
        return null;
    }

    private static class ContaCreditavel extends ContaGitPay {

        private double saldo2;

        public ContaCreditavel(int numero, Usuario<? extends Pessoa> usuario) {
            super(numero, usuario);
        }

        void creditar(double valor) {
            this.saldo2 += valor;
        }
    }

    private static class MinhaContaGitPay extends ContaCreditavel {

        private double saldo2;

        public MinhaContaGitPay(int numero, Usuario<? extends Pessoa> usuario) {
            super(numero, usuario);
        }

        void creditar(double valor) {
            this.saldo2 += valor;
        }

        void debitar(double valor) {
            this.saldo2 -= valor;
        }
    }
}
