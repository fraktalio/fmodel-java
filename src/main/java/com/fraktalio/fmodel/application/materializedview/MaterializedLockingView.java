package com.fraktalio.fmodel.application.materializedview;

import com.fraktalio.fmodel.domain.Pair;
import com.fraktalio.fmodel.domain.view.IView;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * {@code MaterializedLockingView} implements {@link IView} and {@link IViewStateLockingRepository} interfaces,
 * clearly communicating that it is composed out of these two behaviours.
 * <br><br>
 * Materialized View is using/delegating a `view` to handle events and to maintain a state of projection(s) as a result.
 * In order to handle the event, materialized view needs to fetch the current state via `IViewStateLockingRepository.fetchState` function first, and then delegate the event to the `view` which can compute new state as a result.
 * New state is then stored via `IViewStateLockingRepository.save` method.
 * <br><br>
 * Locking materialized view enables `optimistic locking` mechanism more explicitly.
 * If you fetch state from a storage, the application records the `version` number of that state.
 * You can update the state, but only if the `version` number in the storage has not changed.
 * If there is a `version` mismatch, it means that someone else has updated the state before you did.
 *
 * @param <S>  materialized view state type
 * @param <E>  event type(s) that are handled by this materialized view
 * @param <SV> materialized view state version type
 * @param <EI> event identifier type
 * @author Иван Дугалић / Ivan Dugalic / @idugalic
 */
public final class MaterializedLockingView<S, E, SV, EI> implements IView<S, E>, IViewStateLockingRepository<S, E, SV, EI> {
    public MaterializedLockingView(final IView<S, E> view, final IViewStateLockingRepository<S, E, SV, EI> repository) {
        this.view = view;
        this.repository = repository;
    }

    private final IView<S, E> view;
    private final IViewStateLockingRepository<S, E, SV, EI> repository;

    private S computeNewState(S state, E event) {
        var currentState = state != null ? state : initialViewState().get();
        return evolveView().apply(currentState, event);
    }

    /**
     * Handle the event and store/produce new state
     *
     * @param eventAndIdentifier event to handle
     * @return newly stored state
     */
    public S handle(Pair<E, EI> eventAndIdentifier) {
        var event = eventAndIdentifier.first();
        var eventIdentifier = eventAndIdentifier.second();
        var stateAndVersion = fetchState(event);
        var currentState = stateAndVersion.first();
        var currentStateVersion = stateAndVersion.second();
        return save(computeNewState(currentState, event), eventIdentifier, currentStateVersion);
    }

    @Override
    public BiFunction<S, E, S> evolveView() {
        return view.evolveView();
    }

    @Override
    public Supplier<S> initialViewState() {
        return view.initialViewState();
    }

    @Override
    public Pair<S, SV> fetchState(E event) {
        return repository.fetchState(event);
    }

    @Override
    public S save(S state, EI eventIdentifier, SV currentStateVersion) {
        return repository.save(state, eventIdentifier, currentStateVersion);
    }
}
