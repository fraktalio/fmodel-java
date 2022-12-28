package com.fraktalio.fmodel.example.api;

public sealed interface Command permits OddCommand, EvenCommand {
}
