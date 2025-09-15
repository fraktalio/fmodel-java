package com.fraktalio.fmodel.application.aggregate.statestored;

import java.util.concurrent.CompletableFuture;

/**
 * State repository interface
 *
 * @param <C> command
 * @param <S> state
 * @author Иван Дугалић / Ivan Dugalic / @idugalic
 */
public interface IStateRepository<C, S> {
    /**
     * @param command command
     * @return the State
     */
    S fetchState(C command);

    /**
     * @param newState new state
     * @return newly stored State
     */
    S save(S newState);

    // --------------------------------------------------------------------
    // Default async variants
    // --------------------------------------------------------------------

    default CompletableFuture<S> fetchStateAsync(C command) {
        return CompletableFuture.supplyAsync(() -> fetchState(command));
    }

    default CompletableFuture<S> saveAsync(S newState) {
        return CompletableFuture.supplyAsync(() -> save(newState));
    }
}
