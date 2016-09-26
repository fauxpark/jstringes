package net.fauxpark.tuples;

import net.fauxpark.stringes.Util;

/**
 * 6-tuple.
 *
 * @param <A> The first type.
 * @param <B> The second type.
 * @param <C> The third type.
 * @param <D> The fourth type.
 * @param <E> The fifth type.
 * @param <F> The sixth type.
 */
public final class SixTuple<A, B, C, D, E, F> {
	public final A a;

	public final B b;

	public final C c;

	public final D d;

	public final E e;

	public final F f;

	/**
	 * Constructs a Tuple with six items.
	 *
	 * @param a The first item.
	 * @param b The second item.
	 * @param c The third item.
	 * @param d The fourth item.
	 * @param e The fifth item.
	 * @param f The sixth item.
	 */
	SixTuple(A a, B b, C c, D d, E e, F f) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.e = e;
		this.f = f;
	}

	@Override
	public String toString() {
		return "Item 1: " + a + "\nItem 2: " + b + "\nItem 3: " + c + "\nItem 4: " + d +
				"\nItem 5: " + e + "\nItem 6: " + f;
	}

	@Override
	public int hashCode() {
		return Util.hashOf(a, b, c, d, e, f);
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

		final SixTuple<?, ?, ?, ?, ?, ?> t = (SixTuple<?, ?, ?, ?, ?, ?>) obj;

		if(a.equals(t.a) && b.equals(t.b) && c.equals(t.c) && d.equals(t.d)
			&& e.equals(t.e) && f.equals(t.f)) {
			return true;
		}

		return false;
	}
}
