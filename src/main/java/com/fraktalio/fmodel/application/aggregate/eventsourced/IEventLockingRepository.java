package com.fraktalio.fmodel.application.aggregate.eventsourced;

import com.fraktalio.fmodel.domain.Pair;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Event locking repository interface
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
     * @param command
     * @return stream of Event and Sequence/Version
     */
    Stream<Pair<E, V>> fetchEvents(C command);

    /**
     * Save Events
     *
     * @param events
     * @param versionProvider
     * @return stream of already saved Events with its Sequence/Version
     */
    Stream<Pair<E, V>> save(Stream<E> events, Function<E, Pair<E, V>> versionProvider);

    /**
     * Save Events
     *
     * @param events
     * @param version
     * @return stream of already saved Events with its Sequence/Version
     */
    Stream<Pair<E, V>> save(Stream<E> events, Pair<E, V> version);

    /**
     * A function - version provider
     */
    Function<E, Pair<E, V>> versionProvider();

}
