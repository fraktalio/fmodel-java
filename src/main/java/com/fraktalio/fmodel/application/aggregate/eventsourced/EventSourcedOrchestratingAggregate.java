package com.fraktalio.fmodel.application.aggregate.eventsourced;

import com.fraktalio.fmodel.domain.decider.IDecider;
import com.fraktalio.fmodel.domain.saga.ISaga;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class EventSourcedOrchestratingAggregate<C, S, E> implements IDecider<C, S, E>, ISaga<E, C>, IEventRepository<C, E> {
    public EventSourcedOrchestratingAggregate(final IDecider<C, S, E> decider, final ISaga<E, C> saga, final IEventRepository<C, E> repository) {
        this.decider = decider;
        this.saga = saga;
        this.repository = repository;
    }

    private final IDecider<C, S, E> decider;
    private final ISaga<E, C> saga;
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
    public Function<E, Stream<C>> react() {
        return saga.react();
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
        AtomicReference<Stream<E>> resultingEvents = new AtomicReference<>(decide().apply(command, currentState));
        resultingEvents.get()
                .flatMap(it -> react().apply(it))
                .forEach(c -> {
                    var newEvents = computeNewEvents(Stream.concat(fetchEvents(c), resultingEvents.get()), c);
                    resultingEvents.set(Stream.concat(resultingEvents.get(), newEvents));
                });
        return resultingEvents.get();
    }

}
