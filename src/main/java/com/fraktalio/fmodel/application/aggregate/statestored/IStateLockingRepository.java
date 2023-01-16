package com.fraktalio.fmodel.application.aggregate.statestored;

import com.fraktalio.fmodel.domain.Pair;

/**
 * State locking repository interface
 *
 * @param <C> command
 * @param <S> state
 * @param <V> version
 * @author Иван Дугалић / Ivan Dugalic / @idugalic
 */
public interface IStateLockingRepository<C, S, V> {
    /**
     * @param command
     * @return a pair of State and Version for that state
     */
    Pair<S, V> fetchState(C command);

    /**
     * @param currentStateVersion
     * @param newState
     * @return a pair of State and Version for that state that are being stored
     */
    Pair<S, V> save(V currentStateVersion, S newState);
}

