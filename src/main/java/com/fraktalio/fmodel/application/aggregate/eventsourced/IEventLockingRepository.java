package com.fraktalio.fmodel.application.aggregate.eventsourced;

import com.fraktalio.fmodel.domain.Pair;

import java.util.function.Function;
import java.util.stream.Stream;

public interface IEventLockingRepository<C, E, V> {
    Stream<Pair<E, V>> fetchEvents(C command);

    Stream<Pair<E, V>> save(Stream<E> events, Function<E, Pair<E, V>> versionProvider);

    Stream<Pair<E, V>> save(Stream<E> events, Pair<E, V> version);

    Function<E, Pair<E, V>> versionProvider();

}
