package com.fraktalio.fmodel.application.aggregate.eventsourced;

import com.fraktalio.fmodel.domain.decider.IDecider;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class EventSourcedAggregate<C, S, E> implements IDecider<C, S, E>, IEventRepository<C, E> {
    public EventSourcedAggregate(IDecider<C, S, E> decider, IEventRepository<C, E> repository) {
        this.decider = decider;
        this.repository = repository;
    }

    private final IDecider<C, S, E> decider;
    private final IEventRepository<C, E> repository;


    @Override
    public BiFunction<C, S, Stream<E>> decide() {
        return decider.decide();
    }

    @Override
    public BiFunction<S, E, S> evolve() {
        return decider.evolve();
    }

    @Override
    public Supplier<S> initialState() {
        return decider.initialState();
    }


    @Override
    public Stream<E> fetchEvents(C command) {
        return repository.fetchEvents(command);
    }

    @Override
    public Stream<E> save(Stream<E> events) {
        return repository.save(events);
    }

    public Stream<E> handle(C command) {
        return save(computeNewEvents(fetchEvents(command), command));
    }

    private Stream<E> computeNewEvents(Stream<E> oldEvents, C command) {
        var currentState = oldEvents.sequential().reduce(initialState().get(), (s, e) -> evolve().apply(s, e), (s, s2) -> s);
        return decide().apply(command, currentState);
    }

}
