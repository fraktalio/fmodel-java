package com.fraktalio.fmodel.application.materializedview;

public interface IViewStateRepository<S, E> {
    S fetchState(E event);

    S save(S state);
}