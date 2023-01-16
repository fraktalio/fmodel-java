package com.fraktalio.fmodel.application.materializedview;

import com.fraktalio.fmodel.domain.Pair;

/**
 * Materialized View State Locking repository
 *
 * @param <S>  state
 * @param <E>  event
 * @param <SV> state version
 * @param <EI> event identifier/version
 */
public interface IViewStateLockingRepository<S, E, SV, EI> {
    /**
     * @param event
     * @return pair of State and State Version
     */
    Pair<S, SV> fetchState(E event);

    /**
     * @param state
     * @param eventIdentifier
     * @param currentStateVersion
     * @return newly stored state
     */
    S save(S state, EI eventIdentifier, SV currentStateVersion);
}