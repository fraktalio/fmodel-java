package com.fraktalio.fmodel.domain;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

interface I_Decider<C, Si, So, Ei, Eo> {
    BiFunction<? super C, ? super Si, ? extends Stream<Eo>> decide();

    BiFunction<? super Si, ? super Ei, ? extends So> evolve();

    Supplier<? extends So> initialState();
}
