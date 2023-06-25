package com.fraktalio.fmodel.domain.decider;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * An Interface for the {@link Decider}
 * <br><br>
 * Represents the main decision-making algorithm of the system, responsible for translating the commands (intent) into new events/state (facts).
 *
 * @param <C> Command
 * @param <S> State
 * @param <E> Event
 */
public interface IDecider<C, S, E> {
    /**
     * A function/lambda that takes command of type C and input state of type S as parameters, and returns/emits the flow of output events {@code Stream<E>}
     */
    BiFunction<C, S, Stream<E>> decide();

    /**
     * A function/lambda that takes input state of type S and input event of type E as parameters, and returns the output/new state S
     */
    BiFunction<S, E, S> evolve();

    /**
     * A starting point / An initial state of type S
     */
    Supplier<S> initialState();
}
