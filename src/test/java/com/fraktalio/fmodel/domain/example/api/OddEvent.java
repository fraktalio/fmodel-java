package com.fraktalio.fmodel.domain.example.api;

public sealed interface OddEvent extends Event permits OddNumberAddedEvent, OddNumberMultipliedEvent {
}
