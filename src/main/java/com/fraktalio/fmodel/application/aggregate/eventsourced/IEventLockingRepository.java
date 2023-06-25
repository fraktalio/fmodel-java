package com.fraktalio.fmodel.application.aggregate.eventsourced;

import com.fraktalio.fmodel.domain.Pair;

import java.util.function.Function;
import java.util.stream.Stream;

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
     * @return stream of Event and Sequence/Version
     */
    Stream<Pair<E, V>> fetchEvents(C command);

    /**
     * Save Events
     *
     * @param events          events
     * @param versionProvider version provider function
     * @return stream of already saved Events with its Sequence/Version
     */
    Stream<Pair<E, V>> save(Stream<E> events, Function<E, V> versionProvider);

    /**
     * Save Events
     *
     * @param events  events
     * @param version version
     * @return stream of already saved Events with its Sequence/Version
     */
    Stream<Pair<E, V>> save(Stream<E> events, V version);

    /**
     * A function - version provider
     */
    Function<E, V> versionProvider();

}
