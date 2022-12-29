package com.fraktalio.fmodel.domain.example.api;

public sealed interface Event permits OddEvent, EvenEvent {
}
