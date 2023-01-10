package com.fraktalio.fmodel.application.aggregate.statestored;

public interface IStateRepository<C, S> {
    S fetchState(C command);

    S save(S newState);
}
