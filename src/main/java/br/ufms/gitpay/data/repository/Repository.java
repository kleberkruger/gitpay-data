package br.ufms.gitpay.data.repository;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Repository<Entity, Id> {

    CompletableFuture<Entity> save(Entity entity);

    CompletableFuture<Void> delete(Id id);

    CompletableFuture<Optional<Entity>> get(Id id);

    CompletableFuture<Collection<Entity>> getAll();
}
