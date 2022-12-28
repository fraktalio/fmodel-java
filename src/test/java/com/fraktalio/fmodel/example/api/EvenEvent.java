package com.fraktalio.fmodel.example.api;

public sealed interface EvenEvent extends Event permits EvenNumberAddedEvent, EvenNumberMultipliedEvent {
}

