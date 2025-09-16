package com.fraktalio.fmodel.domain.example;

import com.fraktalio.fmodel.domain.Pair;
import com.fraktalio.fmodel.domain.decider.Decider;
import com.fraktalio.fmodel.domain.example.api.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.fraktalio.fmodel.dsl.DeciderDSL.givenEvents;
import static com.fraktalio.fmodel.dsl.DeciderDSL.givenState;

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


        givenState(oddDecider, oddState)
                .whenCommand(addOddNumberCommand)
                .thenState(new OddNumberState(1));

        givenEvents(oddDecider, List.of())
                .whenCommand(addOddNumberCommand)
                .thenEvents(List.of(oddNumberAddedEvent));

        // Even decider: given evenState + addEvenNumberCommand -> then evenNumberAddedEvent
        givenState(evenDecider, evenState)
                .whenCommand(addEvenNumberCommand)
                .thenState(new EvenNumberState(2));

        givenEvents(evenDecider, List.of())
                .whenCommand(addEvenNumberCommand)
                .thenEvents(List.of(evenNumberAddedEvent));

        // Combined decider: given state + odd command -> events
        givenEvents(decider, List.of())
                .whenCommand(addOddNumberCommand)
                .thenEvents(List.of(oddNumberAddedEvent));

        // Combined decider: given state + odd command -> new state
        givenState(decider, state)
                .whenCommand(addOddNumberCommand)
                .thenState(new NumberState(new EvenNumberState(0), new OddNumberState(1)));

        // Combined decider: given state + even command -> new state
        givenState(decider, state)
                .whenCommand(addEvenNumberCommand)
                .thenState(new NumberState(new EvenNumberState(2), new OddNumberState(0)));
    }
}
