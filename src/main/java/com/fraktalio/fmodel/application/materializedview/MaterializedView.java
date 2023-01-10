package com.fraktalio.fmodel.application.materializedview;

import com.fraktalio.fmodel.domain.view.IView;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class MaterializedView<S, E> implements IView<S, E>, IViewStateRepository<S, E> {
    public MaterializedView(IView<S, E> view, IViewStateRepository<S, E> repository) {
        this.view = view;
        this.repository = repository;
    }

    private final IView<S, E> view;
    private final IViewStateRepository<S, E> repository;

    private S computeNewState(S state, E event) {
        var currentState = state != null ? state : initialState().get();
        return evolve().apply(currentState, event);
    }

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
    public BiFunction<S, E, S> evolve() {
        return view.evolve();
    }

    @Override
    public Supplier<S> initialState() {
        return view.initialState();
    }
}
