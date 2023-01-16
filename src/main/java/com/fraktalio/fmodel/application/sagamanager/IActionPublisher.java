package com.fraktalio.fmodel.application.sagamanager;

import java.util.stream.Stream;

/**
 * Action Publisher Interface
 *
 * @param <A> Action
 */
public interface IActionPublisher<A> {
    /**
     * @param actions
     * @return stream of Actions being published
     */
    Stream<A> publish(Stream<A> actions);
}
