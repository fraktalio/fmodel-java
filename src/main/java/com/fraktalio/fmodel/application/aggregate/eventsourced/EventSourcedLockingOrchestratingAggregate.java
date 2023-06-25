package com.fraktalio.fmodel.application.aggregate.eventsourced;

import com.fraktalio.fmodel.domain.Pair;
import com.fraktalio.fmodel.domain.decider.IDecider;
import com.fraktalio.fmodel.domain.saga.ISaga;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * {@code EventSourcedLockingOrchestratingAggregate} implements {@link IDecider}, {@link ISaga} and {@link IEventLockingRepository} interfaces,
 * clearly communicating that it is composed out of these three behaviours.
 * <br /><br />
 * Event sourcing locking orchestrating aggregate is using/delegating a `decider` to handle commands and produce events.
 * In order to handle the command, aggregate needs to fetch the current state (represented as a list of events) via `IEventLockingRepository.fetchEvents` function, and then delegate the command to the `decider` which can produce new event(s) as a result.
 * If the `decider` is combined out of many deciders via `combine` function, an optional `saga` of type {@code ISaga} could be used to react on new events and send new commands to the 'decider` recursively, in single transaction.
 * Produced events are then stored via `IEventLockingRepository.save` method.
 * <br /><br />
 * Locking and Orchestrating Event sourcing aggregate enables `optimistic locking` mechanism more explicitly.
 * If you fetch events from a storage, the application records the `version` number of that event stream.
 * You can append new events, but only if the `version` number in the storage has not changed.
 * If there is a `version` mismatch, it means that someone else has added the event(s) before you did.
 *
 * @param <C> command type(s) that this aggregate can handle
 * @param <S> aggregate state type
 * @param <E> event type(s) that this aggregate can publish/store
 * @param <V> version type
 * @author Иван Дугалић / Ivan Dugalic / @idugalic
 */
public final class EventSourcedLockingOrchestratingAggregate<C, S, E, V> implements IDecider<C, S, E>, ISaga<E, C>, IEventLockingRepository<C, E, V> {
    public EventSourcedLockingOrchestratingAggregate(final IDecider<C, S, E> decider, final ISaga<E, C> saga, final IEventLockingRepository<C, E, V> repository) {
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
    public Stream<Pair<E, V>> save(Stream<E> events, Function<E, V> versionProvider) {
        return repository.save(events, versionProvider);
    }

    @Override
    public Stream<Pair<E, V>> save(Stream<E> events, V version) {
        return repository.save(events, version);
    }

    @Override
    public Function<E, V> versionProvider() {
        return repository.versionProvider();
    }

    /**
     * Handle the command and store/produce new events
     *
     * @param command command to be handled
     * @return new events being stored
     */
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
