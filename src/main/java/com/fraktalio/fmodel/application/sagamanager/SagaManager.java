package com.fraktalio.fmodel.application.sagamanager;

import com.fraktalio.fmodel.domain.saga.ISaga;

import java.util.function.Function;
import java.util.stream.Stream;

public final class SagaManager<AR, A> implements ISaga<AR, A>, IActionPublisher<A> {
    private final ISaga<AR, A> saga;
    private final IActionPublisher<A> publisher;

    public SagaManager(ISaga<AR, A> saga, IActionPublisher<A> publisher) {
        this.saga = saga;
        this.publisher = publisher;
    }

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
