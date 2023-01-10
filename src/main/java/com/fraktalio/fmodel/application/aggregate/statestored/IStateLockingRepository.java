package com.fraktalio.fmodel.application.aggregate.statestored;

import com.fraktalio.fmodel.domain.Pair;

public interface IStateLockingRepository<C, S, V> {
    Pair<S, V> fetchState(C command);

    Pair<S, V> save(V currentStateVersion, S newState);
}

