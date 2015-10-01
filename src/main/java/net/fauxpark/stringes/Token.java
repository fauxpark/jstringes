package net.fauxpark.stringes;

/**
 * Represents a Token with a string value and a custom identifier.
 *
 * @param <T> The identifier type.
 */
public final class Token<T> extends Stringe {
	/**
	 * The token identifier.
	 */
	private final T id;

	/**
	 * Returns the token identifier.
	 */
	public T getId() {
		return id;
	}

	/**
	 * Constructs a new Token from the specified string.
	 *
	 * @param id The identifier of the token.
	 * @param value The underlying string value.
	 */
	public Token(T id, String value) {
		super(value);
		this.id = id;
	}

	/**
	 * Constructs a new Token from the specified Stringe.
	 *
	 * @param id The identifier of the token.
	 * @param value The Stringe to use for the token.
	 */
	public Token(T id, Stringe value) {
		super(value);
		this.id = id;
	}

	/**
	 * Returns a string representation of the current token.
	 */
	@Override
	public String toString() {
		String value = getValue();

		return "<" + id + ", L" + getLine() + " C" + getColumn() + " " + (value == null || value.isEmpty() ? "" : "'" + value + "'") + ">";
	}
}
