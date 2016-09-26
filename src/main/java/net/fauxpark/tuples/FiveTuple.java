package net.fauxpark.tuples;

import net.fauxpark.stringes.Util;

/**
 * 5-tuple.
 *
 * @param <A> The first type.
 * @param <B> The second type.
 * @param <C> The third type.
 * @param <D> The fourth type.
 * @param <E> The fifth type.
 */
public final class FiveTuple<A, B, C, D, E> {
	public final A a;

	public final B b;

	public final C c;

	public final D d;

	public final E e;

	/**
	 * Constructs a Tuple with five items.
	 *
	 * @param a The first item.
	 * @param b The second item.
	 * @param c The third item.
	 * @param d The fourth item.
	 * @param e The fifth item.
	 */
	FiveTuple(A a, B b, C c, D d, E e) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.e = e;
	}

	@Override
	public String toString() {
		return "Item 1: " + a + "\nItem 2: " + b + "\nItem 3: " + c + "\nItem 4: " + d +
				"\nItem 5: " + e;
	}

	@Override
	public int hashCode() {
		return Util.hashOf(a, b, c, d, e);
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

		final FiveTuple<?, ?, ?, ?, ?> t = (FiveTuple<?, ?, ?, ?, ?>) obj;

		if(a.equals(t.a) && b.equals(t.b) && c.equals(t.c) && d.equals(t.d)
			&& e.equals(t.e)) {
			return true;
		}

		return false;
	}
}
