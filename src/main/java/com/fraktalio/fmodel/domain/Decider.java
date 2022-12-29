package com.fraktalio.fmodel.domain;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public record Decider<C, S, E>(BiFunction<C, S, Stream<E>> decide, BiFunction<S, E, S> evolve,
                               Supplier<S> initialState

) implements IDecider<C, S, E> {


    public <Cn> Decider<Cn, S, E> mapOnCommand(Function<? super Cn, ? extends C> f) {
        return Decider.of(new _Decider<>(this.decide, this.evolve, this.initialState).contraMapOnCommand(f));
    }

    public <En> Decider<C, S, En> mapOnEvent(Function<? super En, ? extends E> fl, Function<? super E, ? extends En> fr) {
        return Decider.of(new _Decider<>(this.decide, this.evolve, this.initialState).dimapOnEvent(fl, fr));
    }

    public <Sn> Decider<C, Sn, E> mapOnState(Function<? super Sn, ? extends S> fl, Function<? super S, ? extends Sn> fr) {
        return Decider.of(new _Decider<>(this.decide, this.evolve, this.initialState).dimapOnState(fl, fr));
    }

    public static <C_SUPER, E_SUPER, S1, S2, C1 extends C_SUPER, C2 extends C_SUPER, E1 extends E_SUPER, E2 extends E_SUPER> Decider<C_SUPER, Pair<S1, S2>, E_SUPER> combine(
            Decider<? super C1, S1, E1> x,
            Class<C1> clazzCX, Class<E1> clazzEX,
            Decider<? super C2, S2, E2> y,
            Class<C2> clazzCY, Class<E2> clazzEY
    ) {
        return Decider.of(_Decider.combineIt(new _Decider<>(x.decide, x.evolve, x.initialState), clazzCX, clazzEX, new _Decider<>(y.decide, y.evolve, y.initialState), clazzCY, clazzEY));
    }

    static <C, S, E> Decider<C, S, E> of(_Decider<C, S, S, E, E> decider) {
        return new Decider<>(decider.decide(), decider.evolve(), decider.initialState());
    }
}

