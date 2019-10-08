package net.petafuel.styx.core.persistence;

/**
 * Interface for Styx persistance interaction
 *
 * @param <T>
 */
public interface PersistentDatabaseInterface<T> {
    T create(T model);

    T get(T model);

    T update(T model);

    T delete(T model);
}
