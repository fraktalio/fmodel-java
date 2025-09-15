package com.fraktalio.fmodel.application.sagamanager;

import com.fraktalio.fmodel.domain.saga.ISaga;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Saga manager - Stateless process orchestrator.
 * <br /><br />
 * {@code SagaManager} implements {@link ISaga} and {@link IActionPublisher} interfaces,
 * clearly communicating that it is composed out of these two behaviours.
 * <br /><br />
 * It is reacting on Action Results of type `AR` and produces new actions `A` based on them.
 *
 * @param <AR> action Result type
 * @param <A>  action type
 * @author Иван Дугалић / Ivan Dugalic / @idugalic
 */
public final class SagaManager<AR, A> implements ISaga<AR, A>, IActionPublisher<A> {
    private final ISaga<AR, A> saga;
    private final IActionPublisher<A> publisher;

    public SagaManager(final ISaga<AR, A> saga, final IActionPublisher<A> publisher) {
        this.saga = saga;
        this.publisher = publisher;
    }

    /**
     * Handle the action result and publish new actions
     *
     * @param actionResult the action result to handle
     * @return the newly published actions
     */
    public List<A> handle(AR actionResult) {
        return publish(react().apply(actionResult));
    }

    /**
     * Handle the action result and publish new actions - async variant
     *
     * @param actionResult the action result to handle
     * @return the newly published actions
     */
    public CompletableFuture<List<A>> handleAsync(AR actionResult) {
        return CompletableFuture
                .supplyAsync(() -> react().apply(actionResult))   // compute actions from actionResult
                .thenCompose(this::publishAsync);         // publish them asynchronously
    }

    @Override
    public Function<AR, List<A>> react() {
        return saga.react();
    }

    @Override
    public List<A> publish(List<A> actions) {
        return publisher.publish(actions);
    }

}
