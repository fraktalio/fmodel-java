package com.fraktalio.fmodel.domain.decider;


import com.fraktalio.fmodel.domain.Pair;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.function.Function.identity;

record _Decider<C, Si, So, Ei, Eo>(
        BiFunction<C, Si, Stream<Eo>> decide,
        BiFunction<Si, Ei, So> evolve,
        Supplier<So> initialState
) {

    <Cn> _Decider<Cn, Si, So, Ei, Eo> contraMapCommand(Function<? super Cn, ? extends C> f) {
        return new _Decider<>(
                (cn, si) -> decide.apply(f.apply(cn), si),
                evolve,
                initialState
        );
    }

    <Ein, Eon> _Decider<C, Si, So, Ein, Eon> dimapEvent(Function<? super Ein, ? extends Ei> fl, Function<? super Eo, ? extends Eon> fr) {
        return new _Decider<>(
                (c, si) -> decide.apply(c, si).map(fr),
                (si, ein) -> evolve.apply(si, fl.apply(ein)),
                initialState
        );
    }

    <Sin, Son> _Decider<C, Sin, Son, Ei, Eo> dimapState(Function<? super Sin, ? extends Si> fl, Function<? super So, ? extends Son> fr) {
        return new _Decider<>(
                (c, sin) -> decide.apply(c, fl.apply(sin)),
                (sin, ei) -> fr.apply(evolve.apply(fl.apply(sin), ei)),
                () -> fr.apply(initialState.get())
        );
    }

    <Son> _Decider<C, Si, Son, Ei, Eo> applyState(_Decider<? super C, ? super Si, ? extends Function<? super So, ? extends Son>, ? super Ei, ? extends Eo> decider2) {
        return new _Decider<>(
                (c, si) -> Stream.concat(decide.apply(c, si), decider2.decide.apply(c, si)),
                (si, ei) -> decider2.evolve.apply(si, ei).apply(evolve.apply(si, ei)),
                () -> decider2.initialState.get().apply(initialState.get())
        );
    }

    <Son> _Decider<C, Si, Pair<So, Son>, Ei, Eo> productState(_Decider<? super C, ? super Si, ? extends Son, ? super Ei, ? extends Eo> decider2) {
        return this.applyState(decider2.dimapState(identity(), (b) -> ((a) -> new Pair<>(a, b))));
    }


    static <C_SUPER, Ei_SUPER, Eo_SUPER, Si1, So1, Si2, So2, C1 extends C_SUPER, C2 extends C_SUPER, Ei1 extends Ei_SUPER, Eo1 extends Eo_SUPER, Ei2 extends Ei_SUPER, Eo2 extends Eo_SUPER> _Decider<C_SUPER, Pair<Si1, Si2>, Pair<So1, So2>, Ei_SUPER, Eo_SUPER> combine(
            _Decider<? super C1, ? super Si1, ? extends So1, ? super Ei1, ? extends Eo1> x,
            Class<C1> clazzC1, Class<Ei1> clazzE1,
            _Decider<? super C2, ? super Si2, ? extends So2, ? super Ei2, ? extends Eo2> y,
            Class<C2> clazzC2, Class<Ei2> clazzE2
    ) {

        var deciderX = x
                .<C_SUPER>contraMapCommand((it) -> safeCast(it, clazzC1))
                .<Pair<Si1, Si2>, So1>dimapState(Pair::first, identity()).<Ei_SUPER, Eo_SUPER>dimapEvent((it) -> safeCast(it, clazzE1), (it) -> it);

        var deciderY = y
                .<C_SUPER>contraMapCommand((it) -> safeCast(it, clazzC2))
                .<Pair<Si1, Si2>, So2>dimapState(Pair::second, identity()).<Ei_SUPER, Eo_SUPER>dimapEvent((it) -> safeCast(it, clazzE2), (it) -> it);

        return deciderX.productState(deciderY);
    }

    private static <T> T safeCast(Object o, Class<T> clazz) {
        return clazz != null && clazz.isInstance(o) ? clazz.cast(o) : null;
    }

}

