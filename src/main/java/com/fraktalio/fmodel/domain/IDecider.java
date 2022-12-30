package com.fraktalio.fmodel.domain;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * An Interface for the {@link Decider}
 *
 * @param <C> Command
 * @param <S> State
 * @param <E> Event
 */
public interface IDecider<C, S, E> {
    /**
     * A function/lambda that takes command of type C and input state of type S as parameters, and returns/emits the flow of output events Stream<[E]>
     */
    BiFunction<? super C, ? super S, ? extends Stream<E>> decide();

    /**
     * A function/lambda that takes input state of type S and input event of type E as parameters, and returns the output/new state S
     */
    BiFunction<? super S, ? super E, ? extends S> evolve();

    /**
     * A starting point / An initial state of type S
     */
    Supplier<? extends S> initialState();
}
