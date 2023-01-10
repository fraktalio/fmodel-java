package com.fraktalio.fmodel.application.materializedview;

import com.fraktalio.fmodel.domain.Pair;


public interface IViewStateLockingRepository<S, E, SV, EI> {
    Pair<S, SV> fetchState(E event);

    S save(S state, EI eventIdentifier, SV currentStateVersion);
}