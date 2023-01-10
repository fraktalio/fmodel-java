package com.fraktalio.fmodel.application.aggregate.eventsourced;

import com.fraktalio.fmodel.domain.Pair;
import com.fraktalio.fmodel.domain.decider.IDecider;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class EventSourcedLockingAggregate<C, S, E, V> implements IDecider<C, S, E>, IEventLockingRepository<C, E, V> {
    public EventSourcedLockingAggregate(IDecider<C, S, E> decider, IEventLockingRepository<C, E, V> repository) {
        this.decider = decider;
        this.repository = repository;
    }

    private final IDecider<C, S, E> decider;
    private final IEventLockingRepository<C, E, V> repository;


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
    public Stream<Pair<E, V>> fetchEvents(C command) {
        return repository.fetchEvents(command);
    }

    @Override
    public Stream<Pair<E, V>> save(Stream<E> events, Function<E, Pair<E, V>> versionProvider) {
        return repository.save(events, versionProvider);
    }

    @Override
    public Stream<Pair<E, V>> save(Stream<E> events, Pair<E, V> version) {
        return repository.save(events, version);
    }

    @Override
    public Function<E, Pair<E, V>> versionProvider() {
        return repository.versionProvider();
    }

    public Stream<Pair<E, V>> handle(C command) {
        var events = fetchEvents(command).toList();
        return save(computeNewEvents(events.stream().map(Pair::first), command), events.get(events.size() - 1));
    }

    private Stream<E> computeNewEvents(Stream<E> oldEvents, C command) {
        var currentState = oldEvents.sequential().reduce(initialState().get(), (s, e) -> evolve().apply(s, e), (s, s2) -> s);
        return decide().apply(command, currentState);
    }

}
