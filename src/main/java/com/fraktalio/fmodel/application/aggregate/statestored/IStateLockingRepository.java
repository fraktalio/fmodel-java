package com.fraktalio.fmodel.application.aggregate.statestored;

import com.fraktalio.fmodel.domain.Pair;

/**
 * State locking repository interface
 * <br /><br />
 * State locking repository enables `optimistic locking` mechanism more explicitly.
 * If you fetch state from a storage, the application records the `version` number of that state.
 * You can update the state, but only if the `version` number in the storage has not changed.
 * If there is a `version` mismatch, it means that someone else has updated the state before you did.
 *
 * @param <C> command
 * @param <S> state
 * @param <V> version
 * @author Иван Дугалић / Ivan Dugalic / @idugalic
 */
public interface IStateLockingRepository<C, S, V> {
    /**
     * @param command command
     * @return a pair of State and Version for that state
     */
    Pair<S, V> fetchState(C command);

    /**
     * @param currentStateVersion current state version
     * @param newState            new state
     * @return a pair of State and Version for that state that are being stored
     */
    Pair<S, V> save(V currentStateVersion, S newState);
}

