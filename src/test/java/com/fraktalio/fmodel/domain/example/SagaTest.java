package com.fraktalio.fmodel.domain.example;

import com.fraktalio.fmodel.domain.example.api.*;
import com.fraktalio.fmodel.domain.saga.Saga;
import org.junit.jupiter.api.Test;

import java.util.List;

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
                    case OddNumberAddedEvent evt -> List.of(new AddEvenNumberCommand(evt.value() + 1));
                    case OddNumberMultipliedEvent evt -> List.of(new MultiplyEvenNumberCommand(evt.value() + 1));
                    case null -> List.of();
                }
        );

        Saga<? super EvenEvent, ? extends OddCommand> evenSaga = new Saga<>(
                (ar) -> switch (ar) {
                    case EvenNumberAddedEvent evt -> List.of(new AddOddNumberCommand(evt.value() + 1));
                    case EvenNumberMultipliedEvent evt -> List.of(new MultiplyOddNumberCommand(evt.value() + 1));
                    case null -> List.of();
                }
        );

        // Combining two sagas into one saga
        Saga<? super Event, ? extends Command> saga = Saga.combine(
                oddSaga, OddEvent.class,
                evenSaga, EvenEvent.class
        );

        assertIterableEquals(List.of(addEvenNumberCommand), oddSaga.react().apply(oddNumberAddedEvent));
        assertIterableEquals(List.of(addOddNumberCommand), evenSaga.react().apply(evenNumberAddedEvent));

        assertIterableEquals(List.of(addEvenNumberCommand), saga.react().apply(oddNumberAddedEvent));
        assertIterableEquals(List.of(addOddNumberCommand), saga.react().apply(evenNumberAddedEvent));
    }
}
