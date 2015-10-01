package net.fauxpark.tuples;

import net.fauxpark.stringes.Util;

/**
 * 2-tuple.
 *
 * @param <A> The first type.
 * @param <B> The second type.
 */
public final class TwoTuple<A, B> {
	public final A a;

	public final B b;

	/**
	 * Constructs a Tuple with two items.
	 *
	 * @param a The first item.
	 * @param b The second item.
	 */
	TwoTuple(A a, B b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public String toString() {
		return "Item 1: " + a + "\nItem 2: " + b;
	}

	@Override
	public int hashCode() {
		return Util.hashOf(a, b);
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

		final TwoTuple<?, ?> t = (TwoTuple<?, ?>) obj;

		if(a.equals(t.a) && b.equals(t.b)) {
			return true;
		}

		return false;
	}
}