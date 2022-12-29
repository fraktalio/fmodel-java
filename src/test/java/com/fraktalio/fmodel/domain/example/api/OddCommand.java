package com.fraktalio.fmodel.domain.example.api;

public sealed interface OddCommand extends Command permits AddOddNumberCommand, MultiplyOddNumberCommand {
}
