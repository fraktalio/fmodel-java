package com.fraktalio.fmodel.application.materializedview;

import com.fraktalio.fmodel.domain.view.IView;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * {@code MaterializedView} implements {@link IView} and {@link IViewStateRepository} interfaces,
 * clearly communicating that it is composed out of these two behaviours.
 * <br><br>
 * Materialized View is using/delegating a `view` to handle events and to maintain a state of projection(s) as a result.
 * In order to handle the event, materialized view needs to fetch the current state via `IViewStateRepository.fetchState` function first, and then delegate the event to the `view` which can compute new state as a result.
 * New state is then stored via `IViewStateRepository.save` method.
 *
 * @param <S> materialized view state type
 * @param <E> event type(s) that are handled by this materialized view
 * @author Иван Дугалић / Ivan Dugalic / @idugalic
 */
public final class MaterializedView<S, E> implements IView<S, E>, IViewStateRepository<S, E> {
    public MaterializedView(final IView<S, E> view, final IViewStateRepository<S, E> repository) {
        this.view = view;
        this.repository = repository;
    }

    private final IView<S, E> view;
    private final IViewStateRepository<S, E> repository;

    private S computeNewState(S state, E event) {
        var currentState = state != null ? state : initialViewState().get();
        return evolveView().apply(currentState, event);
    }

    /**
     * Handle the event and store/produce new state
     *
     * @param event event to handle
     * @return newly stored state
     */
    public S handle(E event) {
        return save(computeNewState(fetchState(event), event));
    }

    @Override
    public S fetchState(E event) {
        return repository.fetchState(event);
    }

    @Override
    public S save(S state) {
        return repository.save(state);
    }

    @Override
    public BiFunction<S, E, S> evolveView() {
        return view.evolveView();
    }

    @Override
    public Supplier<S> initialViewState() {
        return view.initialViewState();
    }
}
