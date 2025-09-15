package com.fraktalio.fmodel.application.aggregate.eventsourced;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Event repository interface
 *
 * @param <C> command
 * @param <E> event
 * @author Иван Дугалић / Ivan Dugalic / @idugalic
 */
public interface IEventRepository<C, E> {
    /**
     * Fetch Events by command
     *
     * @param command command
     * @return list of Events
     */
    List<E> fetchEvents(C command);

    /**
     * Save Events
     *
     * @param events events
     * @return list of already saved events
     */
    List<E> save(List<E> events);

    // --------------------------------------------------------------------
    // Default async variants
    // --------------------------------------------------------------------
    default CompletableFuture<List<E>> fetchEventsAsync(C command) {
        return CompletableFuture.supplyAsync(() -> fetchEvents(command));
    }

    default CompletableFuture<List<E>> saveAsync(List<E> events) {
        return CompletableFuture.supplyAsync(() -> save(events));
    }
}
