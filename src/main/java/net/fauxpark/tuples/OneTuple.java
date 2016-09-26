package net.fauxpark.tuples;

/**
 * 1-tuple.
 *
 * @param <A> The first type.
 */
public final class OneTuple<A> {
	public final A a;

	/**
	 * Constructs a Tuple with one item.
	 *
	 * @param a The first item.
	 */
	OneTuple(A a) {
		this.a = a;
	}

	@Override
	public String toString() {
		return "Item 1: " + a;
	}

	@Override
	public int hashCode() {
		return a.hashCode();
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

		final OneTuple<?> t = (OneTuple<?>) obj;

		if(a.equals(t.a)) {
			return true;
		}

		return false;
	}
}
