package com.fraktalio.fmodel.domain.saga;

import java.util.List;
import java.util.function.Function;

/**
 * An Interface for the {@link Saga}
 *
 * @param <AR> Action Result / Event
 * @param <A>  Action / Command
 */
public interface ISaga<AR, A> {
    Function<AR, List<A>> react();
}
