package com.fraktalio.fmodel.application.aggregate.statestored;

import com.fraktalio.fmodel.domain.decider.IDecider;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class StateStoredAggregate<C, S, E> implements IDecider<C, S, E>, IStateRepository<C, S> {
    public StateStoredAggregate(IDecider<C, S, E> decider, IStateRepository<C, S> repository) {
        this.decider = decider;
        this.repository = repository;
    }

    private final IDecider<C, S, E> decider;
    private final IStateRepository<C, S> repository;

    @Override
    public S fetchState(C command) {
        return repository.fetchState(command);
    }

    @Override
    public S save(S newState) {
        return repository.save(newState);
    }

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

    public S handle(C command) {
        return save(computeNewState(fetchState(command), command));
    }

    private S computeNewState(S state, C command) {
        var currentState = state != null ? state : initialState().get();
        var events = decide().apply(command, currentState);
        return events.sequential().reduce(currentState, (s, e) -> evolve().apply(s, e), (s, s2) -> s);
    }
}
