package com.fraktalio.fmodel.domain.decider;

import com.fraktalio.fmodel.domain.Pair;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * {@link Decider} is a datatype that represents the main decision-making algorithm.
 * It has three generic parameters {@code C}, {@code S}, {@code E} , representing the type of the values that Decider may contain or use.
 * Decider can be specialized for any type {@code C} or {@code S} or {@code E} because these types does not affect its behavior.
 * Decider behaves the same for {@code C}= {@code Int} or {@code C}={@code OddNumberCommand}.
 *
 * @param decide       A function/lambda that takes command of type C and input state of type S as parameters, and returns/emits the flow of output events Stream<[E]>
 * @param evolve       A function/lambda that takes input state of type S and input event of type E as parameters, and returns the output/new state S
 * @param initialState A starting point / An initial state of type S
 * @param <C>          Command
 * @param <S>          State
 * @param <E>          Event
 */
public record Decider<C, S, E>(BiFunction<C, S, Stream<E>> decide,
                               BiFunction<S, E, S> evolve,
                               Supplier<S> initialState

) implements IDecider<C, S, E> {

    /**
     * Contra map on Command parameter
     *
     * @param f    function that maps command of type {@code Cn} to command of type {@code C}
     * @param <Cn> New Command type
     * @return new Decider of type {@code Decider<Cn, S, E>}
     */
    public <Cn> Decider<Cn, S, E> contraMapCommand(Function<? super Cn, ? extends C> f) {
        return Decider.of(new _Decider<>(this.decide, this.evolve, this.initialState).contraMapCommand(f));
    }

    /**
     * Dimap on E/Event parameter
     *
     * @param fl   Contra Map over {@code E} type parameter in contravariant/input/left position
     * @param fr   Map over {@code E} type parameter in covariant/output/right position
     * @param <En> New Event type
     * @return new Decider of type {@code Decider<C, S, En>}
     */
    public <En> Decider<C, S, En> dimapEvent(Function<? super En, ? extends E> fl, Function<? super E, ? extends En> fr) {
        return Decider.of(new _Decider<>(this.decide, this.evolve, this.initialState).dimapEvent(fl, fr));
    }

    /**
     * Dimap on S/State parameter
     *
     * @param fl   Contra Map over {@code S} type parameter in contravariant/input/left position
     * @param fr   Map over {@code S} type parameter in covariant/output/right position
     * @param <Sn> New State type
     * @return new Decider of type {@code Decider<C, Sn, E>}
     */
    public <Sn> Decider<C, Sn, E> dimapState(Function<? super Sn, ? extends S> fl, Function<? super S, ? extends Sn> fr) {
        return Decider.of(new _Decider<>(this.decide, this.evolve, this.initialState).dimapState(fl, fr));
    }

    /**
     * Combine Deciders into one Decider
     *
     * @param x         decider 1/X
     * @param clazzCX   the type of the Command of the first decider
     * @param clazzEX   the type of the Event of the first decider
     * @param y         decider 2/Y
     * @param clazzCY   the type of the Command of the second decider
     * @param clazzEY   the type of the Event of the second decider
     * @param <C_SUPER> a common super class of the clazzCX and clazzCY
     * @param <E_SUPER> a common super class of the clazzEX and clazzEY
     * @param <S1>      state of the first decider
     * @param <S2>      state of the second decider
     * @param <C1>      command of the first decider
     * @param <C2>      command of the second decider
     * @param <E1>      event of the first decider
     * @param <E2>      event of the first decider
     * @return new Decider that is aggregating the behaviour of both
     */
    public static <C_SUPER, E_SUPER, S1, S2, C1 extends C_SUPER, C2 extends C_SUPER, E1 extends E_SUPER, E2 extends E_SUPER> Decider<C_SUPER, Pair<S1, S2>, E_SUPER> combine(
            Decider<? super C1, S1, E1> x,
            Class<C1> clazzCX, Class<E1> clazzEX,
            Decider<? super C2, S2, E2> y,
            Class<C2> clazzCY, Class<E2> clazzEY
    ) {
        return Decider.of(_Decider.combine(new _Decider<>(x.decide, x.evolve, x.initialState), clazzCX, clazzEX, new _Decider<>(y.decide, y.evolve, y.initialState), clazzCY, clazzEY));
    }

    static <C, S, E> Decider<C, S, E> of(_Decider<C, S, S, E, E> decider) {
        return new Decider<>(decider.decide(), decider.evolve(), decider.initialState());
    }
}

