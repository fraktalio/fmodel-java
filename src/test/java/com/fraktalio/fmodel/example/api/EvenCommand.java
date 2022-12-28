package com.fraktalio.fmodel.example.api;

public sealed interface EvenCommand extends Command permits AddEvenNumberCommand, MultiplyEvenNumberCommand {
}
