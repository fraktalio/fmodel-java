package com.fraktalio.fmodel.application.sagamanager;

import java.util.stream.Stream;

public interface IActionPublisher<A> {
    Stream<A> publish(Stream<A> actions);
}
