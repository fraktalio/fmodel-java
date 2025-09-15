package com.fraktalio.fmodel.application.materializedview;

import com.fraktalio.fmodel.domain.Pair;

import java.util.concurrent.CompletableFuture;

/**
 * Materialized View State Locking repository
 * <br /><br />
 * View state locking repository enables `optimistic locking` mechanism more explicitly.
 * If you fetch state from a storage, the application records the `version` number of that state.
 * You can update the state, but only if the `version` number in the storage has not changed.
 * If there is a `version` mismatch, it means that someone else has updated the state before you did.
 *
 * @param <S>  state
 * @param <E>  event
 * @param <SV> state version
 * @param <EI> event identifier/version
 */
public interface IViewStateLockingRepository<S, E, SV, EI> {
    /**
     * @param event event
     * @return pair of State and State Version
     */
    Pair<S, SV> fetchState(E event);

    /**
     * @param state               state
     * @param eventIdentifier     event identifier
     * @param currentStateVersion current state version
     * @return newly stored state
     */
    S save(S state, EI eventIdentifier, SV currentStateVersion);

    // --------------------------------------------------------------------
    // Default async variants
    // --------------------------------------------------------------------

    default CompletableFuture<Pair<S, SV>> fetchStateAsync(E event) {
        return CompletableFuture.supplyAsync(() -> fetchState(event));
    }

    default CompletableFuture<S> saveAsync(S state, EI eventIdentifier, SV currentStateVersion) {
        return CompletableFuture.supplyAsync(() -> save(state, eventIdentifier, currentStateVersion));
    }
}