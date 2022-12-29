package com.fraktalio.fmodel.domain.example.api;

public sealed interface EvenCommand extends Command permits AddEvenNumberCommand, MultiplyEvenNumberCommand {
}
