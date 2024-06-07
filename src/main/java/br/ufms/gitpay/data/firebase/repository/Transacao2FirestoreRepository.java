package br.ufms.gitpay.data.firebase.repository;

import br.ufms.gitpay.domain.model.banco.Banco;
import br.ufms.gitpay.domain.model.conta.ContaBancaria;
import br.ufms.gitpay.domain.model.transacao.*;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Transacao2FirestoreRepository extends FirestoreRepository<Transacao, String> {

    public Transacao2FirestoreRepository(int numeroConta) {
        super("bancos/" + Banco.GitPay.getCodigoFormatado() + "/contas/" + numeroConta + "-pg/transacoes");
    }

    @Override
    protected Optional<String> getId(Transacao transacao) {
        return transacao.getId();
    }

    @Override
    protected Map<String, Object> entityToMap(Transacao transacao) {
        Map<String, Object> data = new HashMap<>();
        data.put("valor", transacao.getValor());
        data.put("tipo", transacao.getTipo());
        switch (transacao) {
            case Deposito d -> data.put("destino", getContaRef(d.getDestino()));
            case Saque s -> data.put("origem", getContaRef(s.getOrigem()));
            case Transferencia t -> {
                data.put("origem", getContaRef(t.getOrigem()));
                data.put("destino", getContaRef(t.getDestino()));
            }
            default -> throw new UnsupportedOperationException("Transação ainda não implementada");
        }
        return data;
    }

    private DocumentReference getContaRef(ContaBancaria conta) {
        String contaId = conta.getNumero() + "-" + conta.getTipo().getAbreviacao();
        return db.collection("bancos").document(conta.getBancoFormatado())
                .collection("contas").document(contaId);
    }

    private ContaFirestoreRepository<? extends ContaBancaria> getContaRepository(DocumentReference contaRef) {
        DocumentReference bancoRef;
        if (contaRef == null || (bancoRef = contaRef.getParent().getParent()) == null) {
            return null;
        }
        var bancoId = Integer.parseInt(bancoRef.getId());
        return bancoId == Banco.GitPay.getCodigo() ? new ContaGitPayFirestoreRepository() :
                new ContaExternaFirestoreRepository(bancoId);
    }

    private Transacao criarTransacao(TipoTransacao tipo, String id, LocalDateTime dataHora, double valor,
                                     ContaBancaria conta) {
        return criarTransacao(tipo, id, dataHora, valor, conta, null);
    }

    private Transacao criarTransacao(TipoTransacao tipo, String id, LocalDateTime dataHora, double valor,
                                     ContaBancaria conta, ContaBancaria outraConta) {
        if (conta == null || (tipo == TipoTransacao.TRANSFERENCIA && outraConta == null)) {
            throw new NoSuchElementException("Conta não encontrada");
        }

        return switch (tipo) {
            case DEPOSITO -> new Deposito(id, conta, valor, dataHora);
            case SAQUE -> new Saque(id, conta, valor, dataHora);
            case TRANSFERENCIA -> new Transferencia(id, conta, outraConta, valor, dataHora);
            default -> throw new UnsupportedOperationException("Tipo de transação não suportado");
        };
    }

    @Override
    protected CompletableFuture<Transacao> documentToEntity(DocumentSnapshot doc) {
        TipoTransacao tipo = TipoTransacao.valueOf(doc.getString("tipo"));
        switch (tipo) {
            case DEPOSITO, SAQUE -> {
                DocumentReference contaRef = doc.get(tipo == TipoTransacao.SAQUE ? "origem" : "destino", DocumentReference.class);
                if (contaRef == null) {
                    return CompletableFuture.failedFuture(new NoSuchElementException("Conta não encontrada 1"));
                }
                return new ContaGitPayFirestoreRepository().get(contaRef).thenCompose(conta -> conta.map(contaBancaria ->
                                CompletableFuture.completedFuture(criarTransacao(tipo, doc.getId(),
                                        toLocalDateTime(doc.getCreateTime()),
                                        Objects.requireNonNull(doc.getDouble("valor")), contaBancaria)
                                ))
                        .orElseGet(() -> CompletableFuture.failedFuture(new NoSuchElementException("Conta não encontrada")))
                );
            }
            case TRANSFERENCIA, PIX -> {
                var origemRef = doc.get("origem", DocumentReference.class);
                var destinoRef = doc.get("destino", DocumentReference.class);

                ContaFirestoreRepository<? extends ContaBancaria> origemRepository;
                ContaFirestoreRepository<? extends ContaBancaria> destinoRepository;
                if (origemRef == null || destinoRef == null ||
                        (origemRepository = getContaRepository(origemRef)) == null ||
                        (destinoRepository = getContaRepository(destinoRef)) == null) {

                    return CompletableFuture.failedFuture(new NoSuchElementException("Conta não encontrada"));
                }

                return origemRepository.get(origemRef).thenCombineAsync(destinoRepository.get(destinoRef),
                        (contaOrigem, contaDestino) -> criarTransacao(
                                tipo, doc.getId(), toLocalDateTime(doc.getCreateTime()),
                                Objects.requireNonNull(doc.getDouble("valor")),
                                contaOrigem.orElse(null), contaDestino.orElse(null))
                );
            }
            default -> throw new UnsupportedOperationException("Transação ainda não implementada");
        }
    }
}
