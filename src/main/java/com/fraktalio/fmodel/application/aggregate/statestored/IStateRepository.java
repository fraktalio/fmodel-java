package com.fraktalio.fmodel.application.aggregate.statestored;

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
}
