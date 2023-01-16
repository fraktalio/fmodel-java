package com.fraktalio.fmodel.application.aggregate.eventsourced;

import java.util.stream.Stream;

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
     * @param command
     * @return stream of Events
     */
    Stream<E> fetchEvents(C command);

    /**
     * Save Events
     *
     * @param events
     * @return stream of already saved events
     */
    Stream<E> save(Stream<E> events);
}
