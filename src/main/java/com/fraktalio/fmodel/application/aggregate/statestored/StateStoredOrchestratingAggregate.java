package com.fraktalio.fmodel.application.aggregate.statestored;

import com.fraktalio.fmodel.domain.decider.IDecider;
import com.fraktalio.fmodel.domain.saga.ISaga;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * {@code StateStoredOrchestratingAggregate} implements {@link IDecider}, {@link  ISaga} and {@link IStateRepository} interfaces,
 * clearly communicating that it is composed out of these three behaviours.
 * <br /><br />
 * State stored aggregate is using/delegating a `decider` to handle commands and store/produce new state.
 * In order to handle the command, aggregate needs to fetch the current state via `IStateRepository.fetchState` function first, and then delegate the command to the `decider` which can compute new state as a result.
 * If the `decider` is combined out of many deciders via `combine` function, an optional `saga` of type {@code ISaga<E, C>} could be used to react on new events and send new commands to the 'decider` recursively, in single transaction.
 * New state is then stored via `IStateRepository.save` method.
 *
 * @param <C> command type(s) that this aggregate can handle
 * @param <S> aggregate state type
 * @param <E> event type(s) that this aggregate can publish/store
 * @author Иван Дугалић / Ivan Dugalic / @idugalic
 */
public final class StateStoredOrchestratingAggregate<C, S, E> implements IDecider<C, S, E>, ISaga<E, C>, IStateRepository<C, S> {
    public StateStoredOrchestratingAggregate(final IDecider<C, S, E> decider, final ISaga<E, C> saga, final IStateRepository<C, S> repository) {
        this.decider = decider;
        this.saga = saga;
        this.repository = repository;
    }

    private final IDecider<C, S, E> decider;
    private final ISaga<E, C> saga;
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

    @Override
    public Function<E, List<C>> react() {
        return saga.react();
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
     * Handle the command and store/produce new state  - async variant
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

        return events.stream()
                .reduce(currentState, (s, e) -> {
                    var evolved = evolve().apply(s, e);
                    return react().apply(e).stream()
                            .reduce(evolved, this::computeNewState, (s1, s2) -> s1);
                }, (s1, s2) -> s1);
    }

}
