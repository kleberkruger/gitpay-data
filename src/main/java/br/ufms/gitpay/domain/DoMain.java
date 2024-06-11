package br.ufms.gitpay.domain;

import br.ufms.gitpay.data.firebase.repository.*;
import br.ufms.gitpay.domain.model.conta.ContaGitPay;
import br.ufms.gitpay.domain.model.usuario.Usuario;
import br.ufms.gitpay.domain.service.BancoService;
import br.ufms.gitpay.domain.service.ContaService;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DoMain {

    public static void main(String[] args) {
        try {

            ContaGitPay conta = new ContaGitPay(1, Usuario.criarPessoaFisica(
                    "Kleber Kruger", "02135730165", "67996122809", "kleberkruger@gmail.com",
                    LocalDate.of(1988, 12, 8), "123", LocalDateTime.now()));

            ContaService contaService = new ContaService(FirestoreRepositories.INSTANCE, conta);

//            contaService.depositar(600).join();
            contaService.transferir(2, 0, 600).join();

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
