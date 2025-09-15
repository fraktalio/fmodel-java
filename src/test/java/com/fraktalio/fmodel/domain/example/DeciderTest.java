package com.fraktalio.fmodel.domain.example;

import com.fraktalio.fmodel.domain.Pair;
import com.fraktalio.fmodel.domain.decider.Decider;
import com.fraktalio.fmodel.domain.example.api.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class DeciderTest {

    @Test
    void deciderTest() {
        var addOddNumberCommand = new AddOddNumberCommand(1);
        var oddNumberAddedEvent = new OddNumberAddedEvent(1);

        var addEvenNumberCommand = new AddEvenNumberCommand(2);
        var evenNumberAddedEvent = new EvenNumberAddedEvent(2);

        var oddState = new OddNumberState(0);
        var evenState = new EvenNumberState(0);

        var state = new NumberState(evenState, oddState);

        Decider<? super OddCommand, OddNumberState, OddEvent> oddDecider = new Decider<>(
                (c, s) -> switch (c) {
                    case AddOddNumberCommand cmd -> List.of(new OddNumberAddedEvent(s.value() + cmd.value()));
                    case MultiplyOddNumberCommand cmd -> List.of(new OddNumberMultipliedEvent(s.value() * cmd.value()));
                    case null -> List.of();
                },
                (s, e) -> switch (e) {
                    case OddNumberAddedEvent evt -> new OddNumberState(evt.value());
                    case OddNumberMultipliedEvent evt -> new OddNumberState(evt.value());
                    case null -> s;
                },
                () -> oddState
        );

        Decider<? super EvenCommand, EvenNumberState, EvenEvent> evenDecider = new Decider<>(
                (c, s) -> switch (c) {
                    case AddEvenNumberCommand cmd -> List.of(new EvenNumberAddedEvent(s.value() + cmd.value()));
                    case MultiplyEvenNumberCommand cmd ->
                            List.of(new EvenNumberMultipliedEvent(s.value() * cmd.value()));
                    case null -> List.of();
                },
                (s, e) -> switch (e) {
                    case EvenNumberAddedEvent evt -> new EvenNumberState(evt.value());
                    case EvenNumberMultipliedEvent evt -> new EvenNumberState(evt.value());
                    case null -> s;
                },
                () -> evenState
        );

        // Combining two deciders into one
        Decider<Command, Pair<EvenNumberState, OddNumberState>, Event> _decider = Decider.combine(
                evenDecider, EvenCommand.class, EvenEvent.class,
                oddDecider, OddCommand.class, OddEvent.class
        );
        // Combining two deciders into one, plus mapping inconvenient `Pair` into a domain specific `NumberState`
        Decider<Command, NumberState, Event> decider = Decider
                .combine(
                        evenDecider, EvenCommand.class, EvenEvent.class,
                        oddDecider, OddCommand.class, OddEvent.class)
                .dimapState(
                        (ns) -> new Pair<>(ns.evenNumber(), ns.oddNumber()),
                        (p) -> new NumberState(p.first(), p.second())
                );

        assertIterableEquals(List.of(oddNumberAddedEvent), oddDecider.decide().apply(addOddNumberCommand, oddState));
        assertIterableEquals(List.of(evenNumberAddedEvent), evenDecider.decide().apply(addEvenNumberCommand, evenState));
        assertIterableEquals(List.of(oddNumberAddedEvent), decider.decide().apply(addOddNumberCommand, state));

        assertEquals(new OddNumberState(1), oddDecider.evolve().apply(oddState, oddNumberAddedEvent));
        assertEquals(new EvenNumberState(2), evenDecider.evolve().apply(evenState, evenNumberAddedEvent));
        assertEquals(new NumberState(new EvenNumberState(0), new OddNumberState(1)), decider.evolve().apply(state, oddNumberAddedEvent));
        assertEquals(new NumberState(new EvenNumberState(2), new OddNumberState(0)), decider.evolve().apply(state, evenNumberAddedEvent));
    }
}
