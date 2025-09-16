package com.fraktalio.fmodel.domain.example;

import com.fraktalio.fmodel.domain.Pair;
import com.fraktalio.fmodel.domain.example.api.*;
import com.fraktalio.fmodel.domain.view.View;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.fraktalio.fmodel.dsl.ViewDSL.givenEvents;

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
        View<Pair<EvenNumberState, OddNumberState>, ? super Event> _view = View.combine(
                evenView, EvenEvent.class,
                oddView, OddEvent.class
        );
        // Combining two views into one, plus mapping inconvenient `Pair` into more domain specific `NumberState`
        View<NumberState, ? super Event> view = View
                .combine(evenView, EvenEvent.class, oddView, OddEvent.class)
                .dimapState(
                        (ns) -> new Pair<>(ns.evenNumber(), ns.oddNumber()),
                        (p) -> new NumberState(p.first(), p.second())
                );

        // --- DSL usage ---
        givenEvents(oddView, List.of(oddNumberAddedEvent))
                .thenState(new OddNumberState(1));

        givenEvents(evenView, List.of(evenNumberAddedEvent))
                .thenState(new EvenNumberState(2));

        givenEvents(view, List.of(oddNumberAddedEvent))
                .thenState(new NumberState(new EvenNumberState(0), new OddNumberState(1)));

        givenEvents(view, List.of(evenNumberAddedEvent))
                .thenState(new NumberState(new EvenNumberState(2), new OddNumberState(0)));
    }
}

