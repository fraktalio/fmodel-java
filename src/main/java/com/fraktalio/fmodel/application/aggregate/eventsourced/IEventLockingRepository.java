package com.fraktalio.fmodel.application.aggregate.eventsourced;

import com.fraktalio.fmodel.domain.Pair;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Event locking repository interface.
 * <br /><br />
 * Locking event repository enables `optimistic locking` mechanism more explicitly.
 * If you fetch events from a storage, the application records the `version` number of that event stream.
 * You can append/save new events, but only if the `version` number in the storage has not changed.
 * If there is a `version` mismatch, it means that someone else has added the event(s) before you did.
 *
 * @param <C> command
 * @param <E> event
 * @param <V> version / sequence number
 * @author Иван Дугалић / Ivan Dugalic / @idugalic
 */
public interface IEventLockingRepository<C, E, V> {
    /**
     * Fetch Events by Command
     *
     * @param command command
     * @return list of Event and Sequence/Version
     */
    List<Pair<E, V>> fetchEvents(C command);

    /**
     * Save Events
     *
     * @param events          events
     * @param versionProvider version provider function
     * @return list of already saved Events with its Sequence/Version
     */
    List<Pair<E, V>> save(List<E> events, Function<E, V> versionProvider);

    /**
     * Save Events
     *
     * @param events  events
     * @param version version
     * @return list of already saved Events with its Sequence/Version
     */
    List<Pair<E, V>> save(List<E> events, V version);

    /**
     * A function - version provider
     */
    Function<E, V> versionProvider();

    // --------------------------------------------------------------------
    // Default async variants
    // --------------------------------------------------------------------

    default CompletableFuture<List<Pair<E, V>>> fetchEventsAsync(C command) {
        return CompletableFuture.supplyAsync(() -> fetchEvents(command));
    }

    default CompletableFuture<List<Pair<E, V>>> saveAsync(List<E> events, Function<E, V> versionProvider) {
        return CompletableFuture.supplyAsync(() -> save(events, versionProvider));
    }

    default CompletableFuture<List<Pair<E, V>>> saveAsync(List<E> events, V version) {
        return CompletableFuture.supplyAsync(() -> save(events, version));
    }

    default CompletableFuture<Function<E, V>> versionProviderAsync() {
        return CompletableFuture.supplyAsync(this::versionProvider);
    }

}
