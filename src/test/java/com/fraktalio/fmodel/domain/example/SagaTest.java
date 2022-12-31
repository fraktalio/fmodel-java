package com.fraktalio.fmodel.domain.example;

import com.fraktalio.fmodel.domain.Saga;
import com.fraktalio.fmodel.domain.example.api.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class SagaTest {

    @Test
    void sagaTest() {
        var oddNumberAddedEvent = new OddNumberAddedEvent(1);
        var evenNumberAddedEvent = new EvenNumberAddedEvent(2);

        var addOddNumberCommand = new AddOddNumberCommand(3);
        var addEvenNumberCommand = new AddEvenNumberCommand(2);

        Saga<? super OddEvent, ? extends EvenCommand> oddSaga = new Saga<>(
                (ar) -> switch (ar) {
                    case OddNumberAddedEvent evt -> Stream.of(new AddEvenNumberCommand(evt.value() + 1));
                    case OddNumberMultipliedEvent evt -> Stream.of(new MultiplyEvenNumberCommand(evt.value() + 1));
                    case null -> Stream.empty();
                }
        );

        Saga<? super EvenEvent, ? extends OddCommand> evenSaga = new Saga<>(
                (ar) -> switch (ar) {
                    case EvenNumberAddedEvent evt -> Stream.of(new AddOddNumberCommand(evt.value() + 1));
                    case EvenNumberMultipliedEvent evt -> Stream.of(new MultiplyOddNumberCommand(evt.value() + 1));
                    case null -> Stream.empty();
                }
        );

        // Combining two sagas into one saga
        Saga<? super Event, ? extends Command> saga = Saga.combine(
                oddSaga, OddEvent.class,
                evenSaga, EvenEvent.class
        );

        assertIterableEquals(List.of(addEvenNumberCommand), oddSaga.react().apply(oddNumberAddedEvent).toList());
        assertIterableEquals(List.of(addOddNumberCommand), evenSaga.react().apply(evenNumberAddedEvent).toList());

        assertIterableEquals(List.of(addEvenNumberCommand), saga.react().apply(oddNumberAddedEvent).toList());
        assertIterableEquals(List.of(addOddNumberCommand), saga.react().apply(evenNumberAddedEvent).toList());
    }
}
