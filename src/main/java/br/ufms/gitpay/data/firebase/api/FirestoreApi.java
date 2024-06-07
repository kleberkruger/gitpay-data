package br.ufms.gitpay.data.firebase.api;

import br.ufms.gitpay.data.firebase.config.FirebaseConfig;
import br.ufms.gitpay.data.repository.Repository;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public abstract class FirestoreApi<E> implements Repository<E, String> {

    private static final Firestore db = FirebaseConfig.getFirestore();

    private final String collectionName;

    public FirestoreApi(String collectionName) {
        this.collectionName = collectionName;
    }

    private CompletableFuture<E> add(E entity) {
        return toCompletableFuture(db.collection(collectionName).add(entityToMap(entity)))
                .thenCompose(documentReference -> toCompletableFuture(documentReference.get())
                        .thenApply(this::documentToEntity));
    }

    private CompletableFuture<E> update(String id, E entity) {
        return toCompletableFuture(db.collection(collectionName).document(id).set(entityToMap(entity)))
                .thenApply(writeResult -> entity);
    }

    @Override
    public CompletableFuture<E> save(E entity) {
        var id = getId(entity);
        return id.isEmpty() ? add(entity) : update(id.get(), entity);
    }

    @Override
    public CompletableFuture<Void> delete(String id) {
        return toCompletableFuture(db.collection(collectionName).document(idToStr(id)).delete())
                .thenApply(writeResult -> null);
    }

    @Override
    public CompletableFuture<Optional<E>> get(String id) {
        return toCompletableFuture(db.collection(collectionName).document(idToStr(id)).get())
                .thenApply(document -> Optional.ofNullable(documentToEntity(document)));
    }

    @Override
    public CompletableFuture<Collection<E>> getAll() {
        return toCompletableFuture(db.collection(collectionName).get()).thenApply(queryDocumentSnapshots ->
                queryDocumentSnapshots.getDocuments().stream()
                        .map(this::documentToEntity)
                        .collect(Collectors.toList()));
    }

    private <T> CompletableFuture<T> toCompletableFuture(ApiFuture<T> apiFuture) {
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

    protected abstract Optional<String> getId(E entity);

    protected String idToStr(String id) {
        return id;
    }

    protected abstract Map<String, Object> entityToMap(E entity);

    protected abstract E documentToEntity(DocumentSnapshot document);
}
