package com.fraktalio.fmodel.application.aggregate.statestored;

import com.fraktalio.fmodel.domain.Pair;
import com.fraktalio.fmodel.domain.decider.IDecider;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * {@code StateStoredLockingAggregate} implements {@link IDecider} and {@link IStateLockingRepository} interfaces,
 * clearly communicating that it is composed out of these two behaviours.
 * <br /><br />
 * State stored aggregate is using/delegating a `decider` to handle commands and store/produce new state.
 * In order to handle the command, aggregate needs to fetch the current state via `IStateLockingRepository.fetchState` function first, and then delegate the command to the `decider` which can compute new state as a result.
 * New state is then stored via `IStateLockingRepository.save` method.
 * <br /><br />
 * Locking state stored aggregate enables `optimistic locking` mechanism more explicitly.
 * If you fetch state from a storage, the application records the `version` number of that state.
 * You can update the state, but only if the `version` number in the storage has not changed.
 * If there is a `version` mismatch, it means that someone else has updated the state before you did.
 *
 * @param <C> command type(s) that this aggregate can handle
 * @param <S> aggregate state type
 * @param <E> event type(s) that this aggregate can publish/store
 * @param <V> version type
 * @author Иван Дугалић / Ivan Dugalic / @idugalic
 */
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

    /**
     * Handle the command and store/produce new state
     *
     * @param command the command to handle
     * @return the newly stored state (with version)
     */
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
