package com.fraktalio.fmodel.application.sagamanager;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Action Publisher Interface
 *
 * @param <A> Action
 */
public interface IActionPublisher<A> {
    /**
     * @param actions actions
     * @return list of Actions being published
     */
    List<A> publish(List<A> actions);

    // --------------------------------------------------------------------
    // Default async variants
    // --------------------------------------------------------------------

    default CompletableFuture<List<A>> publishAsync(List<A> actions) {
        return CompletableFuture.supplyAsync(() -> publish(actions));
    }
}
