package com.fraktalio.fmodel.dsl;


import com.fraktalio.fmodel.domain.view.IView;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ViewDSL {

    public static <S, E> GivenEventsBuilder<S, E> givenEvents(IView<S, E> view, List<E> events) {
        return new GivenEventsBuilder<>(view, events);
    }

    public static class GivenEventsBuilder<S, E> {
        private final IView<S, E> view;
        private final List<E> events;

        GivenEventsBuilder(IView<S, E> view, List<E> events) {
            this.view = view;
            this.events = events;
        }

        public void thenState(S expected) {
            S result = events.stream()
                    .reduce(view.initialViewState().get(),
                            view.evolveView(),
                            (s1, s2) -> s2);
            assertEquals(expected, result);
        }
    }
}
