package com.fraktalio.fmodel.domain.example;

import com.fraktalio.fmodel.domain.Pair;
import com.fraktalio.fmodel.domain.example.api.*;
import com.fraktalio.fmodel.domain.view.View;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ViewTest {

    @Test
    void viewTest() {
        var oddNumberAddedEvent = new OddNumberAddedEvent(1);
        var evenNumberAddedEvent = new EvenNumberAddedEvent(2);

        var oddState = new OddNumberState(0);
        var evenState = new EvenNumberState(0);

        var state = new NumberState(evenState, oddState);

        View<OddNumberState, ? super OddEvent> oddView = new View<>(
                (s, e) -> switch (e) {
                    case OddNumberAddedEvent evt -> new OddNumberState(evt.value());
                    case OddNumberMultipliedEvent evt -> new OddNumberState(evt.value());
                    case null -> s;
                },
                () -> oddState
        );

        View<EvenNumberState, ? super EvenEvent> evenView = new View<>(
                (s, e) -> switch (e) {
                    case EvenNumberAddedEvent evt -> new EvenNumberState(evt.value());
                    case EvenNumberMultipliedEvent evt -> new EvenNumberState(evt.value());
                    case null -> s;
                },
                () -> evenState
        );

        // Combining two views into one
        View<Pair<EvenNumberState, OddNumberState>, ? super Event> _decider = View.combine(
                evenView, EvenEvent.class,
                oddView, OddEvent.class
        );
        // Combining two views into one, plus mapping inconvenient `Pair` into more domain specific `NumberState`
        View<NumberState, ? super Event> decider = View
                .combine(evenView, EvenEvent.class, oddView, OddEvent.class)
                .dimapState(
                        (ns) -> new Pair<>(ns.evenNumber(), ns.oddNumber()),
                        (p) -> new NumberState(p.first(), p.second())
                );

        assertEquals(new OddNumberState(1), oddView.evolveView().apply(oddState, oddNumberAddedEvent));
        assertEquals(new EvenNumberState(2), evenView.evolveView().apply(evenState, evenNumberAddedEvent));
        assertEquals(new NumberState(new EvenNumberState(0), new OddNumberState(1)), decider.evolveView().apply(state, oddNumberAddedEvent));
        assertEquals(new NumberState(new EvenNumberState(2), new OddNumberState(0)), decider.evolveView().apply(state, evenNumberAddedEvent));
    }
}
