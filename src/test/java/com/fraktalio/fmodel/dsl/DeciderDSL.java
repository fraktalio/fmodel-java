package com.fraktalio.fmodel.dsl;


import com.fraktalio.fmodel.domain.decider.IDecider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;


public final class DeciderDSL {

    private DeciderDSL() {
    }

    // --------------------------
    // givenEvents
    // --------------------------
    public static <C, S, E> GivenEventsBuilder<C, S, E> givenEvents(
            IDecider<C, S, E> decider,
            List<E> events
    ) {
        return new GivenEventsBuilder<>(decider, events);
    }

    // --------------------------
    // givenState
    // --------------------------
    public static <C, S, E> GivenStateBuilder<C, S, E> givenState(
            IDecider<C, S, E> decider,
            S state
    ) {
        return new GivenStateBuilder<>(decider, state);
    }

    // --------------------------
    // Builders
    // --------------------------

    public static class GivenEventsBuilder<C, S, E> {
        private final IDecider<C, S, E> decider;
        private final List<E> priorEvents;

        GivenEventsBuilder(IDecider<C, S, E> decider, List<E> priorEvents) {
            this.decider = Objects.requireNonNull(decider);
            this.priorEvents = priorEvents;
        }

        public WhenEventsBuilder<C, S, E> whenCommand(C command) {
            var currentState = priorEvents.stream().reduce(decider.initialState().get(), (s, e) -> decider.evolve().apply(s, e), (s, s2) -> s);

            List<E> decided = decider.decide().apply(command, currentState);
            return new WhenEventsBuilder<>(decided);
        }
    }

    public static class WhenEventsBuilder<C, S, E> {
        private final List<E> actualEvents;

        WhenEventsBuilder(List<E> actualEvents) {
            this.actualEvents = new ArrayList<>(actualEvents);
        }

        public void thenEvents(List<E> expected) {
            assertIterableEquals(expected, actualEvents);
        }
    }

    public static class GivenStateBuilder<C, S, E> {
        private final IDecider<C, S, E> decider;
        private final S priorState;

        GivenStateBuilder(IDecider<C, S, E> decider, S priorState) {
            this.decider = Objects.requireNonNull(decider);
            this.priorState = priorState;
        }

        public WhenStateBuilder<C, S, E> whenCommand(C command) {
            S currentState = (priorState != null) ? priorState : decider.initialState().get();
            List<E> decidedEvents = decider.decide().apply(command, currentState);

            S newState = decidedEvents.stream().reduce(currentState, (s, e) -> decider.evolve().apply(s, e), (s, s2) -> s);

            return new WhenStateBuilder<>(newState);
        }
    }

    public static class WhenStateBuilder<C, S, E> {
        private final S actualState;

        WhenStateBuilder(S actualState) {
            this.actualState = actualState;
        }

        public void thenState(S expected) {
            assertEquals(expected, actualState);
        }
    }
}
