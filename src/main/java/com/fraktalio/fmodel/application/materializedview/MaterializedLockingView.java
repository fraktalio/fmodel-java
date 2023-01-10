package com.fraktalio.fmodel.application.materializedview;

import com.fraktalio.fmodel.domain.Pair;
import com.fraktalio.fmodel.domain.view.IView;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class MaterializedLockingView<S, E, SV, EI> implements IView<S, E>, IViewStateLockingRepository<S, E, SV, EI> {
    public MaterializedLockingView(IView<S, E> view, IViewStateLockingRepository<S, E, SV, EI> repository) {
        this.view = view;
        this.repository = repository;
    }

    private final IView<S, E> view;
    private final IViewStateLockingRepository<S, E, SV, EI> repository;

    private S computeNewState(S state, E event) {
        var currentState = state != null ? state : initialViewState().get();
        return evolveView().apply(currentState, event);
    }

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
