package net.fauxpark.tuples;

import net.fauxpark.stringes.Util;

/**
 * 4-tuple.
 *
 * @param <A> The first type.
 * @param <B> The second type.
 * @param <C> The third type.
 * @param <D> The fourth type.
 */
public final class FourTuple<A, B, C, D> {
	public final A a;

	public final B b;

	public final C c;

	public final D d;

	/**
	 * Constructs a Tuple with four items.
	 *
	 * @param a The first item.
	 * @param b The second item.
	 * @param c The third item.
	 * @param d The fourth item.
	 */
	FourTuple(A a, B b, C c, D d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}

	@Override
	public String toString() {
		return "Item 1: " + a + "\nItem 2: " + b + "\nItem 3: " + c + "\nItem 4: " + d;
	}

	@Override
	public int hashCode() {
		return Util.hashOf(a, b, c, d);
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

		final FourTuple<?, ?, ?, ?> t = (FourTuple<?, ?, ?, ?>) obj;

		if(a.equals(t.a) && b.equals(t.b) && c.equals(t.c) && d.equals(t.d)) {
			return true;
		}

		return false;
	}
}