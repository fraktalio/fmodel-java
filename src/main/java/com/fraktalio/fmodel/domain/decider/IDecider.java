package com.fraktalio.fmodel.domain.decider;

import com.fraktalio.fmodel.domain.view.IView;

import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 * An Interface for the {@link Decider}
 *
 * @param <C> Command
 * @param <S> State
 * @param <E> Event
 */
public interface IDecider<C, S, E> extends IView<S, E> {
    /**
     * A function/lambda that takes command of type C and input state of type S as parameters, and returns/emits the flow of output events Stream<[E]>
     */
    BiFunction<C, S, Stream<E>> decide();
}
