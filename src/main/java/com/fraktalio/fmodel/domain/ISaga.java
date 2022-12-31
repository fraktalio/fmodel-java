package com.fraktalio.fmodel.domain;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * An Interface for the {@link Saga}
 *
 * @param <AR> Action Result / Event
 * @param <A>  Action / Command
 */
public interface ISaga<AR, A> {
    Function<AR, Stream<A>> react();
}
