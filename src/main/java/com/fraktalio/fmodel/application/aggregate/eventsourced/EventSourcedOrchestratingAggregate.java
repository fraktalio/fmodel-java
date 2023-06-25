package com.fraktalio.fmodel.application.aggregate.eventsourced;

import com.fraktalio.fmodel.domain.decider.IDecider;
import com.fraktalio.fmodel.domain.saga.ISaga;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * {@code EventSourcedOrchestratingAggregate} implements {@link IDecider}, {@link ISaga} and {@link IEventRepository} interfaces,
 * clearly communicating that it is composed out of these three behaviours.
 * <br /><br />
 * Event sourcing orchestrating aggregate is using/delegating a `decider` to handle commands and produce events.
 * In order to handle the command, aggregate needs to fetch the current state (represented as a list of events) via `IEventRepository.fetchEvents` function, and then delegate the command to the `decider` which can produce new event(s) as a result.
 * If the `decider` is combined out of many deciders via `combine` function, an optional `saga` of type {@code ISaga} could be used to react on new events and send new commands to the 'decider` recursively, in single transaction.
 * Produced events are then stored via `IEventRepository.save` method.
 *
 * @param <C> command type(s) that this aggregate can handle
 * @param <S> aggregate state type
 * @param <E> event type(s) that this aggregate can publish/store
 * @author Иван Дугалић / Ivan Dugalic / @idugalic
 */
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

    /**
     * Handle the command and store/produce new events
     *
     * @param command command to be handled
     * @return new events being stored
     */
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
