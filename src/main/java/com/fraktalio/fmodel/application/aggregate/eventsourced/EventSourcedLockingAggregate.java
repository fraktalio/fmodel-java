package com.fraktalio.fmodel.application.aggregate.eventsourced;

import com.fraktalio.fmodel.domain.Pair;
import com.fraktalio.fmodel.domain.decider.IDecider;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * {@code EventSourcedLockingAggregate} implements {@link IDecider} and {@link IEventLockingRepository} interfaces,
 * clearly communicating that it is composed out of these two behaviours.
 * <br /><br />
 * Locking Event sourcing aggregate is using/delegating a `decider` to handle commands and store/produce events.
 * In order to handle the command, aggregate needs to fetch the current state (represented as a list of events) via `IEventLockingRepository.fetchEvents` function, and then delegate the command to the `decider` which can produce new event(s) as a result.
 * Produced events are then stored via `IEventLockingRepository.save` method.
 * <br /><br />
 * Locking Event sourcing aggregate enables `optimistic locking` mechanism more explicitly.
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
public final class EventSourcedLockingAggregate<C, S, E, V> implements IDecider<C, S, E>, IEventLockingRepository<C, E, V> {
    public EventSourcedLockingAggregate(final IDecider<C, S, E> decider, final IEventLockingRepository<C, E, V> repository) {
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
        var events = fetchEvents(command).toList();
        return save(computeNewEvents(events.stream().map(Pair::first), command), events.stream().map(Pair::second).toList().get(events.size() - 1));
    }

    private Stream<E> computeNewEvents(Stream<E> oldEvents, C command) {
        var currentState = oldEvents.sequential().reduce(initialState().get(), (s, e) -> evolve().apply(s, e), (s, s2) -> s);
        return decide().apply(command, currentState);
    }

}
