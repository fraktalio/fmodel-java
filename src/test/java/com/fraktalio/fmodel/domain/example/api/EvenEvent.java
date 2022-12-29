package com.fraktalio.fmodel.domain.example.api;

public sealed interface EvenEvent extends Event permits EvenNumberAddedEvent, EvenNumberMultipliedEvent {
}

