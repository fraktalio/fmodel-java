package com.fraktalio.fmodel.example.api;

public sealed interface OddCommand extends Command permits AddOddNumberCommand, MultiplyOddNumberCommand {
}
