package com.fraktalio.fmodel.application.sagamanager;

import com.fraktalio.fmodel.domain.saga.ISaga;

import java.util.function.Function;
import java.util.stream.Stream;

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
    public Stream<A> handle(AR actionResult) {
        return publish(react().apply(actionResult));
    }

    @Override
    public Function<AR, Stream<A>> react() {
        return saga.react();
    }

    @Override
    public Stream<A> publish(Stream<A> actions) {
        return publisher.publish(actions);
    }
}
