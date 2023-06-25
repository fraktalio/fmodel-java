package com.fraktalio.fmodel.application.materializedview;

/**
 * View State repository interface
 *
 * @param <S> state
 * @param <E> event
 * @author Иван Дугалић / Ivan Dugalic / @idugalic
 */
public interface IViewStateRepository<S, E> {
    /**
     * @param event event
     * @return the State
     */
    S fetchState(E event);

    /**
     * @param state state
     * @return newly stored State
     */
    S save(S state);
}