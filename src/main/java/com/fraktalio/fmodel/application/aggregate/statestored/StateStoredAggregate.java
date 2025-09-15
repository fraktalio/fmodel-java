package com.fraktalio.fmodel.application.aggregate.statestored;

import com.fraktalio.fmodel.domain.decider.IDecider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * {@code StateStoredAggregate} implements {@link IDecider} and {@link IStateRepository} interfaces,
 * clearly communicating that it is composed out of these two behaviours.
 * <br /><br />
 * State stored aggregate is using/delegating a `decider` to handle commands and store/produce new state.
 * In order to handle the command, aggregate needs to fetch the current state via `IStateRepository.fetchState` function first, and then delegate the command to the `decider` which can compute new state as a result.
 * New state is then stored via `IStateRepository.save` method.
 *
 * @param <C> command type(s) that this aggregate can handle
 * @param <S> aggregate state type
 * @param <E> event type(s) that this aggregate can publish/store
 * @author Иван Дугалић / Ivan Dugalic / @idugalic
 */
public final class StateStoredAggregate<C, S, E> implements IDecider<C, S, E>, IStateRepository<C, S> {
    public StateStoredAggregate(final IDecider<C, S, E> decider, final IStateRepository<C, S> repository) {
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
    public BiFunction<C, S, List<E>> decide() {
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
     * @return the newly stored state
     */
    public S handle(C command) {
        return save(computeNewState(fetchState(command), command));
    }

    /**
     * Handle the command and store/produce new state - async variant
     *
     * @param command the command to handle
     * @return the newly stored state
     */
    public CompletableFuture<S> handleAsync(C command) {
        return fetchStateAsync(command)
                .thenApply(state -> computeNewState(state, command))
                .thenCompose(this::saveAsync);
    }

    private S computeNewState(S state, C command) {
        var currentState = state != null ? state : initialState().get();
        var events = decide().apply(command, currentState);
        return events.stream().reduce(currentState, (s, e) -> evolve().apply(s, e), (s, s2) -> s);
    }
}
