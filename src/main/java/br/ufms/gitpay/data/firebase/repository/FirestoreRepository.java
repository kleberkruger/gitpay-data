package br.ufms.gitpay.data.firebase.repository;

import br.ufms.gitpay.data.firebase.config.FirebaseConfig;
import br.ufms.gitpay.data.repository.Repository;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;

import java.time.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class FirestoreRepository<Entity, Id> implements Repository<Entity, Id> {

    protected static final Firestore db = FirebaseConfig.getFirestore();

    // Substituir collectionName pela CollectionReference??
    protected final String collectionName;

    public FirestoreRepository(String collectionName) {
        this.collectionName = collectionName;
    }

    private CompletableFuture<Entity> add(Entity entity) {
        return toCompletableFuture(db.collection(collectionName).add(entityToMap(entity)))
                .thenCompose(docRef -> toCompletableFuture(docRef.get())
                        .thenCompose(this::documentToEntity));
    }

    private CompletableFuture<Entity> update(String id, Entity entity) {
        return toCompletableFuture(db.collection(collectionName).document(id).set(entityToMap(entity)))
//                .thenApply(writeResult -> entity);
                .thenCompose(writeResult -> toCompletableFuture(db.collection(collectionName).document(id).get())
                        .thenCompose(this::documentToEntity));
    }

    @Override
    public CompletableFuture<Entity> save(Entity entity) {
        var id = getId(entity);
        return id.isEmpty() ? add(entity) : update(id.get(), entity);
    }

    protected DocumentReference save(Transaction transaction, Entity entity) {
        var id = getId(entity);
        var entityRef = id.map(s -> db.collection(collectionName).document(s))
                .orElseGet(() -> db.collection(collectionName).document());
        transaction.create(entityRef, entityToMap(entity));
        return entityRef;
    }

    @Override
    public CompletableFuture<Void> delete(Id id) {
        return toCompletableFuture(db.collection(collectionName).document(idToStr(id)).delete())
                .thenApply(writeResult -> null);
    }

    protected void delete(Transaction transaction, Id id) {
        transaction.delete(db.collection(collectionName).document(idToStr(id)));
    }

    protected CompletableFuture<Optional<Entity>> get(DocumentReference docRef) {
        return toCompletableFuture(docRef.get()).thenCompose(document -> document.exists() ?
                documentToEntity(document).thenApply(Optional::of) :
                CompletableFuture.completedFuture(Optional.empty()));
    }

    @Override
    public CompletableFuture<Optional<Entity>> get(Id id) {
        return get(db.collection(collectionName).document(idToStr(id)));
    }

    @Override
    public CompletableFuture<Collection<Entity>> getAll() {
        return toCompletableFuture(db.collection(collectionName).get())
                .thenCompose(queryDocumentSnapshots -> {
                    List<CompletableFuture<Entity>> futures = queryDocumentSnapshots.getDocuments().stream()
                            .map(this::documentToEntity)
                            .toList();
                    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                            .thenApply(v -> futures.stream()
                                    .map(CompletableFuture::join)
                                    .collect(Collectors.toList()));
                });
    }

    protected <T> CompletableFuture<T> toCompletableFuture(ApiFuture<T> apiFuture) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        ApiFutures.addCallback(apiFuture, new ApiFutureCallback<>() {
            @Override
            public void onSuccess(T result) {
                completableFuture.complete(result);
            }

            @Override
            public void onFailure(Throwable t) {
                completableFuture.completeExceptionally(t);
            }
        }, ForkJoinPool.commonPool());
        return completableFuture;
    }

    public <T extends DocumentReference> CompletableFuture<DocumentSnapshot> runTransaction(Function<Void, T> action) {
//        System.out.println("executando a transação");
//        return toCompletableFuture(db.runTransaction(t -> action.apply(null)));

        return toCompletableFuture(db.runTransaction(t -> action.apply(null)))
                .thenCompose(resultRef -> toCompletableFuture(resultRef.get()));
    }

    protected int toInt(Long value) {
        return Math.toIntExact(Objects.requireNonNull(value));
    }

    private ZonedDateTime toZonedDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toDate().toInstant().atZone(ZoneId.systemDefault());
    }

    protected LocalDate toLocalDate(Timestamp timestamp) {
        return toZonedDateTime(timestamp).toLocalDate();
    }

    protected LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return toZonedDateTime(timestamp).toLocalDateTime();
    }

//    protected LocalDateTime toLocalDateTime(Timestamp timestamp) {
//        return LocalDateTime.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos(), ZoneOffset.UTC);
//    }

    public Date toDate(LocalDate dateToConvert) {
        return Date.from(dateToConvert.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public Date toDate(LocalDateTime dateToConvert) {
        return Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant());
    }

    protected abstract Optional<String> getId(Entity entity);

    protected abstract CollectionReference getCollection(Entity entity);

    protected String idToStr(Id id) {
        return String.valueOf(id);
    }

    protected abstract Map<String, Object> entityToMap(Entity entity);

    protected abstract CompletableFuture<Entity> documentToEntity(DocumentSnapshot document);
}
