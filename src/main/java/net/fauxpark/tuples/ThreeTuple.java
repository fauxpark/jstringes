package net.fauxpark.tuples;

import net.fauxpark.stringes.Util;

/**
 * 3-tuple.
 *
 * @param <A> The first type.
 * @param <B> The second type.
 * @param <C> The third type.
 */
public final class ThreeTuple<A, B, C> {
	public final A a;

	public final B b;

	public final C c;

	/**
	 * Constructs a Tuple with three items.
	 *
	 * @param a The first item.
	 * @param b The second item.
	 * @param c The third item.
	 */
	ThreeTuple(A a, B b, C c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	@Override
	public String toString() {
		return "Item 1: " + a + "\nItem 2: " + b + "\nItem 3: " + c;
	}

	@Override
	public int hashCode() {
		return Util.hashOf(a, b, c);
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

		final ThreeTuple<?, ?, ?> t = (ThreeTuple<?, ?, ?>) obj;

		if(a.equals(t.a) && b.equals(t.b) && c.equals(t.c)) {
			return true;
		}

		return false;
	}
}