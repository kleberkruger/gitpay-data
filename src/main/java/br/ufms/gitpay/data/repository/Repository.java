package br.ufms.gitpay.data.repository;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface Repository<Entity, Id> {

    CompletableFuture<Entity> save(Entity entity);

    CompletableFuture<Void> delete(Id id);

    CompletableFuture<Optional<Entity>> get(Id id);

    CompletableFuture<Collection<Entity>> getAll();

    //    <T> CompletableFuture<T> runTransaction(Function<Void, T> action);
    <T extends DocumentReference> CompletableFuture<DocumentSnapshot> runTransaction(Function<Void, T> action);
}
