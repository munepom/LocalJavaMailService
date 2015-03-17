package com.munepom.appmanager.functions;

import java.util.Objects;
import java.util.function.Function;

/**
 *
 * 3 引数 Function
 *
 * @author nishimura
 *
 * @param <A>
 * @param <B>
 * @param <C>
 * @param <R>
 */
@FunctionalInterface
public interface TriFunction<A, B, C, R> {

	R apply(A a, B b, C c);

	default <V> TriFunction<A, B, C, V> andThen(Function<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (A a, B b, C c) -> after.apply(apply(a, b, c));

	}
}
