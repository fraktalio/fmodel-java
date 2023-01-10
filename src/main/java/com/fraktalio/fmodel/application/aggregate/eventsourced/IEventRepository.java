package com.fraktalio.fmodel.application.aggregate.eventsourced;

import java.util.stream.Stream;

public interface IEventRepository<C, E> {
    Stream<E> fetchEvents(C command);

    Stream<E> save(Stream<E> events);
}
