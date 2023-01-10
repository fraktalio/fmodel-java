package com.fraktalio.fmodel.domain.view;

import com.fraktalio.fmodel.domain.Pair;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * {@link View} is a datatype that represents the event handling algorithm,
 * responsible for translating the events into denormalized state,
 * which is more adequate for querying.
 * <p>
 * It has two generic parameters {@code S}, {@code E}, representing the type of the values that View may contain or use.
 * View can be specialized for any type {@code S} or {@code E} because these types does not affect its behavior.
 * View behaves the same for {@code S}= {@code Int} or {@code S}={@code OddNumber}.
 *
 * @param evolveView       A function/lambda that takes input state of type S and input event of type E as parameters, and returns the output/new state S
 * @param initialViewState A starting point / An initial state of type S
 * @param <S>              State
 * @param <E>              Event
 */
public record View<S, E>(BiFunction<S, E, S> evolveView,
                         Supplier<S> initialViewState

) implements IView<S, E> {

    /**
     * Contra map on Event parameter
     *
     * @param f    function that maps event of type {@code En} to event of type {@code E}
     * @param <En> New Event type
     * @return new View of type {@code View<S, En>}
     */
    public <En> View<S, En> contraMapEvent(Function<? super En, ? extends E> f) {
        return View.of(new _View<>(this.evolveView, this.initialViewState).contraMapEvent(f));
    }


    /**
     * Dimap on S/State parameter
     *
     * @param fl   Contra Map over {@code S} type parameter in contravariant/input/left position
     * @param fr   Map over {@code S} type parameter in covariant/output/right position
     * @param <Sn> New State type
     * @return new View of type {@code View<Sn, E>}
     */
    public <Sn> View<Sn, E> dimapState(Function<? super Sn, ? extends S> fl, Function<? super S, ? extends Sn> fr) {
        return View.of(new _View<>(this.evolveView, this.initialViewState).dimapState(fl, fr));
    }


    /**
     * Combine Views into one View
     *
     * @param x         view 1/X
     * @param clazzEX   the type of the Event of the first view
     * @param y         view 2/Y
     * @param clazzEY   the type of the Event of the second view
     * @param <E_SUPER> a common super class of the clazzEX and clazzEY
     * @param <S1>      state of the first view
     * @param <S2>      state of the second view
     * @param <E1>      event of the first view
     * @param <E2>      event of the first view
     * @return new View that is aggregating the behaviour of both
     */
    public static <E_SUPER, S1, S2, E1 extends E_SUPER, E2 extends E_SUPER> View<Pair<S1, S2>, E_SUPER> combine(
            View<S1, ? super E1> x,
            Class<E1> clazzEX,
            View<S2, ? super E2> y,
            Class<E2> clazzEY
    ) {
        return View.of(_View.combine(new _View<>(x.evolveView, x.initialViewState), clazzEX, new _View<>(y.evolveView, y.initialViewState), clazzEY));
    }

    static <S, E> View<S, E> of(_View<S, S, E> view) {
        return new View<>(view.evolve(), view.initialState());
    }
}

