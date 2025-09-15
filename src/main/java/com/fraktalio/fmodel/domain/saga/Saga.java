package com.fraktalio.fmodel.domain.saga;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Function.identity;

/**
 * {@link Saga} is a datatype that represents the central point of control deciding what to execute next Action/A.
 * It is responsible for mapping different events into Action-Results/AR that the Saga then can use to calculate the next Actions/A to be mapped to command(s).
 *
 * @param react A function/lambda that takes input state of type AR, and returns the {@link  Stream} of Actions/A>.
 * @param <AR>  Action Result / Event
 * @param <A>   Action / Command
 */
public record Saga<AR, A>(Function<AR, List<A>> react) implements ISaga<AR, A> {
    /**
     * Contra map on the Action-Result/AR parameter
     *
     * @param f     function that maps action result of type {@code ARn} to action of type {@code AR}
     * @param <ARn> Action Result new
     * @return new Saga of type {@code Saga<ARn, A>}
     */
    public <ARn> Saga<ARn, A> contraMapActionResult(Function<? super ARn, ? extends AR> f) {
        return new Saga<>((arn) -> react.apply(f.apply(arn)));
    }

    /**
     * Map on the Action/A parameter
     *
     * @param f    function that maps action of type {@code A} to action of type {@code An}
     * @param <An> Action new
     * @return new Saga of type {@code Saga<AR, An>}
     */
    public <An> Saga<AR, An> mapAction(Function<? super A, ? extends An> f) {
        return new Saga<>((ar) -> react.apply(ar).stream().map(f).collect(Collectors.toUnmodifiableList()));
    }

    /**
     * Combine Sagas into one Saga
     *
     * @param x          saga 1/X
     * @param clazzARX   the type of the Action-Result of the first saga
     * @param y          saga 2/Y
     * @param clazzARY   the type of the Action-Result of the second saga
     * @param <AR_SUPER> a common super class of the AR1 and AR2
     * @param <AR1>      Action-Result of the first saga
     * @param <AR2>      Action-Result of the second saga
     * @param <A_SUPER>  a common super class of the A1 and A2
     * @param <A1>       Action of the first saga
     * @param <A2>       Action of the second saga
     * @return new saga of type {@code Saga<AR_SUPER, A_SUPER>}
     */
    public static <AR_SUPER, AR1 extends AR_SUPER, AR2 extends AR_SUPER, A_SUPER, A1 extends A_SUPER, A2 extends A_SUPER> Saga<AR_SUPER, A_SUPER> combine(
            Saga<? super AR1, ? extends A1> x,
            Class<AR1> clazzARX,
            Saga<? super AR2, ? extends A2> y,
            Class<AR2> clazzARY
    ) {
        var sagaX = x
                .<AR_SUPER>contraMapActionResult((it) -> safeCast(it, clazzARX))
                .<A_SUPER>mapAction(identity());

        var sagaY = y
                .<AR_SUPER>contraMapActionResult((it) -> safeCast(it, clazzARY))
                .<A_SUPER>mapAction(identity());
        return new Saga<>(it -> Stream.concat(sagaX.react.apply(it).stream(), sagaY.react.apply(it).stream()).toList());
    }

    private static <T> T safeCast(Object o, Class<T> clazz) {
        return clazz != null && clazz.isInstance(o) ? clazz.cast(o) : null;
    }
}
