package net.fauxpark.tuples;

import net.fauxpark.stringes.Util;

/**
 * 7-tuple.
 *
 * @param <A> The first type.
 * @param <B> The second type.
 * @param <C> The third type.
 * @param <D> The fourth type.
 * @param <E> The fifth type.
 * @param <F> The sixth type.
 * @param <G> The seventh type.
 */
public final class SevenTuple<A, B, C, D, E, F, G> {
	public final A a;

	public final B b;

	public final C c;

	public final D d;

	public final E e;

	public final F f;

	public final G g;

	/**
	 * Constructs a Tuple with seven items.
	 *
	 * @param a The first item.
	 * @param b The second item.
	 * @param c The third item.
	 * @param d The fourth item.
	 * @param e The fifth item.
	 * @param f The sixth item.
	 * @param g The seventh item.
	 */
	SevenTuple(A a, B b, C c, D d, E e, F f, G g) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.e = e;
		this.f = f;
		this.g = g;
	}

	@Override
	public String toString() {
		return "Item 1: " + a + "\nItem 2: " + b + "\nItem 3: " + c + "\nItem 4: " + d +
				"\nItem 5: " + e + "\nItem 6: " + f + "\nItem 7: " + g;
	}

	@Override
	public int hashCode() {
		return Util.hashOf(a, b, c, d, e, f, g);
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}

		if(obj == null) {
			return false;
		}

		if(getClass() != obj.getClass()) {
			return false;
		}

		final SevenTuple<?, ?, ?, ?, ?, ?, ?> t = (SevenTuple<?, ?, ?, ?, ?, ?, ?>) obj;

		if(a.equals(t.a) && b.equals(t.b) && c.equals(t.c) && d.equals(t.d)
			&& e.equals(t.e) && f.equals(t.f) && g.equals(t.g)) {
			return true;
		}

		return false;
	}
}
