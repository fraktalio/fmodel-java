package com.fraktalio.fmodel.example.api;

public sealed interface Event permits OddEvent, EvenEvent {
}
