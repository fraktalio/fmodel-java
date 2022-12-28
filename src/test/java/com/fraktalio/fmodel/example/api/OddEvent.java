package com.fraktalio.fmodel.example.api;

public sealed interface OddEvent extends Event permits OddNumberAddedEvent, OddNumberMultipliedEvent {
}
