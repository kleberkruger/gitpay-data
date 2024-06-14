package br.ufms.gitpay.data.firebase.repository;

import br.ufms.gitpay.data.repository.TransacaoRepository;
import br.ufms.gitpay.domain.model.banco.Banco;
import br.ufms.gitpay.domain.model.conta.ContaBancaria;
import br.ufms.gitpay.domain.model.conta.ContaGitPay;
import br.ufms.gitpay.domain.model.conta.TipoConta;
import br.ufms.gitpay.domain.model.transacao.*;
import com.google.cloud.firestore.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TransacaoFirestoreRepository extends FirestoreRepository<Transacao, String> implements TransacaoRepository {

    public static final String COLLECTION_NAME = "transacoes";

    public TransacaoFirestoreRepository() {
        super(db.collection(BancoFirestoreRepository.COLLECTION_NAME).document(Banco.GitPay.getCodigoFormatado())
                .collection(COLLECTION_NAME));
    }

    @Override
    protected Optional<String> getId(Transacao transacao) {
        return transacao.getId();
    }

    @Override
    protected CollectionReference getCollection(Transacao transacao) {
        return db.collection(BancoFirestoreRepository.COLLECTION_NAME).document(Banco.GitPay.getCodigoFormatado())
                .collection(COLLECTION_NAME);
    }

    private void atualizarSaldo(Transaction transaction, ContaBancaria conta) {
        if (conta instanceof ContaGitPay c) {
            transaction.update(getContaRef(conta), "saldo", c.getSaldo());
        }
    }

    @Override
    public CompletableFuture<Transacao> save(Transacao transacao) {
        return toCompletableFuture(db.runTransaction(transaction -> {
            switch (transacao) {
                case Deposito d -> atualizarSaldo(transaction, d.getDestino());
                case Saque s -> atualizarSaldo(transaction, s.getOrigem());
                case Transferencia t -> {
                    atualizarSaldo(transaction, t.getOrigem());
                    atualizarSaldo(transaction, t.getDestino());
                }
                default -> throw new UnsupportedOperationException("Tipo de transação não suportado");
            }
            var transacaoRef = collection.document();
            transaction.create(transacaoRef, entityToMap(transacao));
            return transacaoRef;

        })).thenCompose(transRef -> toCompletableFuture(transRef.get()).thenCompose(this::documentToEntity));
    }

    @Override
    public CompletableFuture<Collection<Transacao>> getAll(int numeroConta) {
        var bancoRef = db.collection(BancoFirestoreRepository.COLLECTION_NAME).document(Banco.GitPay.getCodigoFormatado());
        var contaRef = getContaRef(Banco.GitPay.getCodigoFormatado(), numeroConta, TipoConta.CONTA_PAGAMENTO);

        Query query = bancoRef.collection(COLLECTION_NAME).where(Filter.or(
                Filter.equalTo("origem", contaRef),
                Filter.equalTo("destino", contaRef)
        ));

        return toCompletableFuture(query.get()).thenCompose(queryDocumentSnapshots -> {
            List<CompletableFuture<Transacao>> futures = queryDocumentSnapshots.getDocuments().stream()
                    .map(this::documentToEntity)
                    .toList();
            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> futures.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList()));
        });
    }

    private DocumentReference getContaRef(ContaBancaria conta) {
        return getContaRef(conta.getBancoFormatado(), conta.getNumero(), conta.getTipo());
    }

    private DocumentReference getContaRef(String codigoBanco, int numeroConta, TipoConta tipoConta) {
        String contaId = numeroConta + "-" + tipoConta.getAbreviacao();
        return db.collection(BancoFirestoreRepository.COLLECTION_NAME).document(codigoBanco)
                .collection(ContaFirestoreRepository.COLLECTION_NAME).document(contaId);
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
        String id = doc.getId();
        LocalDateTime dataHora = toLocalDateTime(doc.getCreateTime());
        double valor = Objects.requireNonNull(doc.getDouble("valor"));

        switch (tipo) {
            case DEPOSITO, SAQUE -> {
                DocumentReference contaRef = Objects.requireNonNull(doc.get(tipo == TipoTransacao.SAQUE ?
                        "origem" : "destino", DocumentReference.class), "Conta não encontrada");

                return new ContaGitPayFirestoreRepository().get(contaRef).thenApply(contaOpt ->
                        criarTransacao(tipo, id, dataHora, valor, contaOpt.orElse(null))
                );
            }
            case TRANSFERENCIA, PIX -> {
                var origemRef = Objects.requireNonNull(doc.get("origem", DocumentReference.class));
                var destinoRef = Objects.requireNonNull(doc.get("destino", DocumentReference.class));
                var origemRepository = getContaRepository(origemRef);
                var destinoRepository = getContaRepository(destinoRef);

                return origemRepository.get(origemRef).thenCombineAsync(destinoRepository.get(destinoRef), (origem, destino) ->
                        criarTransacao(tipo, id, dataHora, valor, origem.orElse(null), destino.orElse(null))
                );
            }
            default -> throw new UnsupportedOperationException("Transação ainda não implementada");
        }
    }
}
