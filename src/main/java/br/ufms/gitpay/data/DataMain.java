package br.ufms.gitpay.data;

import br.ufms.gitpay.data.firebase.repository.*;
import br.ufms.gitpay.domain.model.banco.Banco;
import br.ufms.gitpay.domain.model.conta.*;
import br.ufms.gitpay.domain.model.transacao.Deposito;
import br.ufms.gitpay.domain.model.transacao.Saque;
import br.ufms.gitpay.domain.model.transacao.Transferencia;
import br.ufms.gitpay.domain.model.usuario.Usuario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Function;

public class DataMain {

    private static <E, I> void testarRepository(FirestoreRepository<E, I> repository, E entity,
                                                Function<E, I> getId, Function<E, String> printer) throws InterruptedException {
        System.out.println("----------------------------------------");

        repository.save(entity)
                .thenAccept(e -> System.out.println("save: " + printer.apply(e) + " inserido com sucesso."))
                .exceptionally(ex -> {
                    System.err.println("save: " + ex.getMessage());
                    return null;
                }).join();

        I id = getId.apply(entity);

        repository.get(id)
                .thenAccept(opt -> opt.ifPresentOrElse(e -> System.out.println("get: " + printer.apply(e) + " encontrado."),
                        () -> System.out.println("get: " + id + " objeto não encontrado.")))
                .exceptionally(ex -> {
                    System.err.println("get:\n" + ex.getMessage());
                    return null;
                }).join();

        repository.getAll()
                .thenAccept(list -> {
                    System.out.print("getAll: [");
                    var elements = list.stream().toList();
                    for (int i = 0; i < elements.size(); i++) {
                        System.out.printf("%s%s", i > 0 ? ", " : "", printer.apply(elements.get(i)));
                    }
                    System.out.println("]");
                })
                .exceptionally(ex -> {
                    System.err.println("getAll: " + ex.getMessage());
                    return null;
                }).join();

//        repository.delete(id)
//                .thenAccept(e -> System.out.println("delete: " + id + " deletado com sucesso."))
//                .exceptionally(ex -> {
//                    System.err.println("delete: " + ex.getMessage());
//                    return null;
//                }).join();

        System.out.println("----------------------------------------\n");
    }

    public static void main(String[] args) {
        System.out.println("Inicializando aplicação...");

        try {

            testarRepository(
                    new BancoFirestoreRepository(),
                    Banco.GitPay,
                    Banco::getCodigo,
                    Banco::getNome
            );

            testarRepository(
                    new ChavePixFirestoreRepository(),
                    new ChavePix(TipoChavePix.TELEFONE, "67996122809",
                            new ContaExterna(TipoConta.CONTA_CORRENTE, 1, 552, 16942, 0,
                                    "Kleber Kruger", "02135730165")),
                    ChavePix::getChave,
                    ch -> String.valueOf(ch.getChave())
            );

            testarRepository(
                    new UsuarioFirestoreRepository(),
                    Usuario.criarPessoaFisica(
                            "Kleber Kruger", "02135730165",
                            "67996122809", "kleberkruger@gmail.com",
                            LocalDate.of(1988, 12, 8), "123", null),
                    Usuario::getDocumento,
//                    u -> u.getDataHoraCadastro().format(DateTimeFormatter.ISO_DATE)
                    u -> String.valueOf(u.getDados().getNome())
            );

            ContaExterna contaBB = new ContaExterna(TipoConta.CONTA_CORRENTE, 1, 552, 16942, 0,
                    "Kleber Kruger", "02135730165");
            testarRepository(
                    new ContaExternaFirestoreRepository(1),
                    contaBB,
                    ContaBancaria::getDadosConta,
                    conta -> String.valueOf(conta.getNumero())
            );

            ContaGitPay contaGitPay = new ContaGitPay(1, Usuario.criarPessoaFisica(
                    "Kleber Kruger", "02135730165", "67996122809", "kleberkruger@gmail.com",
                    LocalDate.of(1988, 12, 8), "123", LocalDateTime.now()));
            testarRepository(
                    new ContaGitPayFirestoreRepository(),
                    contaGitPay,
                    ContaBancaria::getDadosConta,
                    conta -> String.valueOf(conta.getNumero())
            );

//            testarRepository(
//                    new Transacao2FirestoreRepository(1),
//                    new Deposito(contaGitPay, 10000),
//                    t -> t.getId().orElse(null),
//                    t -> t.getId().orElse(null)
//            );
//
//            testarRepository(
//                    new Transacao2FirestoreRepository(1),
//                    new Saque(contaGitPay, 2000),
//                    t -> t.getId().orElse(null),
//                    t -> t.getId().orElse(null)
//            );
//
//            testarRepository(
//                    new Transacao2FirestoreRepository(1),
//                    new Transferencia(3000, contaGitPay, contaBB),
//                    t -> t.getId().orElse(null),
//                    t -> t.getId().orElse(null)
//            );
//
//            testarRepository(
//                    new Transacao2FirestoreRepository(1),
//                    new Transferencia(3000, contaGitPay, contaBB),
//                    t -> t.getId().orElse(null),
//                    t -> {
//                        if (t instanceof Transferencia trans) {
//                            return trans.getOrigem().getNumeroDigito() + " -> " + trans.getDestino().getNumeroDigito();
//                        }
//                        return t.getId().orElse(null);
//                    }
//            );

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
