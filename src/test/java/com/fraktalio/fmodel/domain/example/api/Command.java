package com.fraktalio.fmodel.domain.example.api;

public sealed interface Command permits OddCommand, EvenCommand {
}
