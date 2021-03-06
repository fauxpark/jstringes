package net.fauxpark.tuples;

/**
 * Makes tuples for your pleasure.
 *
 * @author fauxpark
 */
public abstract class Tuples {
	/**
	 * Makes a 1-tuple.
	 *
	 * @param <A> The first type.
	 * @param a The first value.
	 *
	 * @return A 1-tuple of the given object.
	 */
	public static <A> OneTuple<A> create(A a) {
		return new OneTuple<>(a);
	}

	/**
	 * Makes a 2-tuple.
	 *
	 * @param <A> The first type.
	 * @param <B> The second type.
	 * @param a The first value.
	 * @param b The second value.
	 *
	 * @return A 2-tuple of the given two objects.
	 */
	public static <A, B> TwoTuple<A, B> create(A a, B b) {
		return new TwoTuple<>(a, b);
	}

	/**
	 * Makes a 3-tuple.
	 *
	 * @param <A> The first type.
	 * @param <B> The second type.
	 * @param <C> The third type.
	 * @param a The first value.
	 * @param b The second value.
	 * @param c The third value.
	 *
	 * @return A 3-tuple of the given three objects.
	 */
	public static <A, B, C> ThreeTuple<A, B, C> create(A a, B b, C c) {
		return new ThreeTuple<>(a, b, c);
	}

	/**
	 * Makes a 4-tuple.
	 *
	 * @param <A> The first type.
	 * @param <B> The second type.
	 * @param <C> The third type.
	 * @param <D> The fourth type.
	 * @param a The first value.
	 * @param b The second value.
	 * @param c The third value.
	 * @param d The fourth value.
	 *
	 * @return A 4-tuple of the given four objects.
	 */
	public static <A, B, C, D> FourTuple<A, B, C, D> create(A a, B b, C c, D d) {
		return new FourTuple<>(a, b, c, d);
	}

	/**
	 * Makes a 5-tuple.
	 *
	 * @param <A> The first type.
	 * @param <B> The second type.
	 * @param <C> The third type.
	 * @param <D> The fourth type.
	 * @param <E> The fifth type.
	 * @param a The first value.
	 * @param b The second value.
	 * @param c The third value.
	 * @param d The fourth value.
	 * @param e The fifth value.
	 *
	 * @return A 5-tuple of the given five objects.
	 */
	public static <A, B, C, D, E> FiveTuple<A, B, C, D, E> create(A a, B b, C c, D d, E e) {
		return new FiveTuple<>(a, b, c, d, e);
	}

	/**
	 * Makes a 6-tuple.
	 *
	 * @param <A> The first type.
	 * @param <B> The second type.
	 * @param <C> The third type.
	 * @param <D> The fourth type.
	 * @param <E> The fifth type.
	 * @param <F> The sixth type.
	 * @param a The first value.
	 * @param b The second value.
	 * @param c The third value.
	 * @param d The fourth value.
	 * @param e The fifth value.
	 * @param f The sixth value.
	 *
	 * @return A 6-tuple of the given six objects.
	 */
	public static <A, B, C, D, E, F> SixTuple<A, B, C, D, E, F> create(A a, B b, C c, D d, E e, F f) {
		return new SixTuple<>(a, b, c, d, e, f);
	}

	/**
	 * Makes a 7-tuple.
	 *
	 * @param <A> The first type.
	 * @param <B> The second type.
	 * @param <C> The third type.
	 * @param <D> The fourth type.
	 * @param <E> The fifth type.
	 * @param <F> The sixth type.
	 * @param <G> The seventh type.
	 * @param a The first value.
	 * @param b The second value.
	 * @param c The third value.
	 * @param d The fourth value.
	 * @param e The fifth value.
	 * @param f The sixth value.
	 * @param g The seventh value.
	 *
	 * @return A 7-tuple of the given seven objects.
	 */
	public static <A, B, C, D, E, F, G> SevenTuple<A, B, C, D, E, F, G> create(A a, B b, C c, D d, E e, F f, G g) {
		return new SevenTuple<>(a, b, c, d, e, f, g);
	}

	/**
	 * Makes an 8-tuple.
	 *
	 * @param <A> The first type.
	 * @param <B> The second type.
	 * @param <C> The third type.
	 * @param <D> The fourth type.
	 * @param <E> The fifth type.
	 * @param <F> The sixth type.
	 * @param <G> The seventh type.
	 * @param <H> The eighth type.
	 * @param a The first value.
	 * @param b The second value.
	 * @param c The third value.
	 * @param d The fourth value.
	 * @param e The fifth value.
	 * @param f The sixth value.
	 * @param g The seventh value.
	 * @param h The eighth value.
	 *
	 * @return An 8-tuple of the given eight objects.
	 */
	public static <A, B, C, D, E, F, G, H> EightTuple<A, B, C, D, E, F, G, H> create(A a, B b, C c, D d, E e, F f, G g, H h) {
		return new EightTuple<>(a, b, c, d, e, f, g, h);
	}
}
