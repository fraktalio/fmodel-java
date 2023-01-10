package com.fraktalio.fmodel.application.aggregate.eventsourced;

import com.fraktalio.fmodel.domain.Pair;
import com.fraktalio.fmodel.domain.decider.IDecider;
import com.fraktalio.fmodel.domain.saga.ISaga;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class EventSourcedLockingOrchestratingAggregate<C, S, E, V> implements IDecider<C, S, E>, ISaga<E, C>, IEventLockingRepository<C, E, V> {
    public EventSourcedLockingOrchestratingAggregate(IDecider<C, S, E> decider, ISaga<E, C> saga, IEventLockingRepository<C, E, V> repository) {
        this.decider = decider;
        this.saga = saga;
        this.repository = repository;
    }

    private final IDecider<C, S, E> decider;
    private final ISaga<E, C> saga;
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
    public Function<E, Stream<C>> react() {
        return saga.react();
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
        return save(computeNewEvents(fetchEvents(command).map(Pair::first), command), versionProvider());
    }

    private Stream<E> computeNewEvents(Stream<E> oldEvents, C command) {
        var currentState = oldEvents.sequential().reduce(initialState().get(), (s, e) -> evolve().apply(s, e), (s, s2) -> s);
        AtomicReference<Stream<E>> resultingEvents = new AtomicReference<>(decide().apply(command, currentState));
        resultingEvents.get()
                .flatMap(it -> react().apply(it))
                .forEach(c -> {
                    var newEvents = computeNewEvents(Stream.concat(fetchEvents(c).map(Pair::first), resultingEvents.get()), c);
                    resultingEvents.set(Stream.concat(resultingEvents.get(), newEvents));
                });
        return resultingEvents.get();
    }


}
