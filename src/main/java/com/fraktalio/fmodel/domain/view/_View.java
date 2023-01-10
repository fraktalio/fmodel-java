package com.fraktalio.fmodel.domain.view;


import com.fraktalio.fmodel.domain.Pair;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.function.Function.identity;

record _View<Si, So, E>(
        BiFunction<Si, E, So> evolve,
        Supplier<So> initialState
) {

    <En> _View<Si, So, En> contraMapEvent(Function<? super En, ? extends E> f) {
        return new _View<>(
                (si, en) -> evolve.apply(si, f.apply(en)),
                initialState
        );
    }

    <Sin, Son> _View<Sin, Son, E> dimapState(Function<? super Sin, ? extends Si> fl, Function<? super So, ? extends Son> fr) {
        return new _View<>(
                (sin, ei) -> fr.apply(evolve.apply(fl.apply(sin), ei)),
                () -> fr.apply(initialState.get())
        );
    }

    <Son> _View<Si, Son, E> applyState(_View<? super Si, ? extends Function<? super So, ? extends Son>, ? super E> decider2) {
        return new _View<>(
                (si, ei) -> decider2.evolve.apply(si, ei).apply(evolve.apply(si, ei)),
                () -> decider2.initialState.get().apply(initialState.get())
        );
    }

    <Son> _View<Si, Pair<So, Son>, E> productState(_View<? super Si, ? extends Son, ? super E> decider2) {
        return this.applyState(decider2.dimapState(identity(), (b) -> ((a) -> new Pair<>(a, b))));
    }

    static <E_SUPER, Si1, So1, Si2, So2, E1 extends E_SUPER, E2 extends E_SUPER> _View<Pair<Si1, Si2>, Pair<So1, So2>, E_SUPER> combine(
            _View<? super Si1, ? extends So1, ? super E1> x,
            Class<E1> clazzE1,
            _View<? super Si2, ? extends So2, ? super E2> y,
            Class<E2> clazzE2
    ) {

        var deciderX = x
                .<E_SUPER>contraMapEvent((it) -> safeCast(it, clazzE1))
                .<Pair<Si1, Si2>, So1>dimapState(Pair::first, identity());

        var deciderY = y
                .<E_SUPER>contraMapEvent((it) -> safeCast(it, clazzE2))
                .<Pair<Si1, Si2>, So2>dimapState(Pair::second, identity());

        return deciderX.productState(deciderY);
    }

    private static <T> T safeCast(Object o, Class<T> clazz) {
        return clazz != null && clazz.isInstance(o) ? clazz.cast(o) : null;
    }

}

