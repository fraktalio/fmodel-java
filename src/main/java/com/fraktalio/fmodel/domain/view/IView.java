package com.fraktalio.fmodel.domain.view;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * An Interface for the {@link View}
 *
 * @param <S> State
 * @param <E> Event
 */
public interface IView<S, E> {
    /**
     * A function/lambda that takes input state of type S and input event of type E as parameters, and returns the output/new state S
     */
    BiFunction<S, E, S> evolve();

    /**
     * A starting point / An initial state of type S
     */
    Supplier<S> initialState();
}
