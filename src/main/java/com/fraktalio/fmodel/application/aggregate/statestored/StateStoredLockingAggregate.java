package com.fraktalio.fmodel.application.aggregate.statestored;

import com.fraktalio.fmodel.domain.Pair;
import com.fraktalio.fmodel.domain.decider.IDecider;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class StateStoredLockingAggregate<C, S, E, V> implements IDecider<C, S, E>, IStateLockingRepository<C, S, V> {

    public StateStoredLockingAggregate(final IDecider<C, S, E> decider, final IStateLockingRepository<C, S, V> repository) {
        this.decider = decider;
        this.repository = repository;
    }

    private final IDecider<C, S, E> decider;
    private final IStateLockingRepository<C, S, V> repository;

    @Override
    public Pair<S, V> fetchState(C command) {
        return repository.fetchState(command);
    }

    @Override
    public Pair<S, V> save(V currentStateVersion, S newState) {
        return repository.save(currentStateVersion, newState);
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

    public Pair<S, V> handle(C command) {
        var pairStateVersion = fetchState(command);
        return save(pairStateVersion.second(), computeNewState(pairStateVersion.first(), command));
    }

    private S computeNewState(S state, C command) {
        var currentState = state != null ? state : initialState().get();
        var events = decide().apply(command, currentState);
        return events.sequential().reduce(currentState, (s, e) -> evolve().apply(s, e), (s, s2) -> s);
    }
}
