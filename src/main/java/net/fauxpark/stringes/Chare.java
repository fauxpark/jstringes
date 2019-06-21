package net.fauxpark.stringes;

/**
 * Represents a Chare, which provides location information on a character taken from a Stringe.
 */
public final class Chare {
	/**
	 * The Stringe from which the Charactere was taken.
	 */
	private final Stringe source;

	/**
	 * The underlying character.
	 */
	private final char character;

	/**
	 * The position of the Chare in the Stringe.
	 */
	private final int offset;

	/**
	 * The line on which the Chare appears.
	 */
	private int line;

	/**
	 * The column on which the Chare appears.
	 */
	private int column;

	/**
	 * Constructs a new Chare with offset information.
	 *
	 * @param source The source Stringe this Chare was taken from.
	 * @param c The char value of the Chare.
	 * @param offset The offset of the Chare relative to the Stringe.
	 */
	Chare(Stringe source, char c, int offset) {
		this.source = source;
		character = c;
		this.offset = offset;
		line = 0;
		column = 0;
	}

	/**
	 * Constructs a new Chare with offset, line and column information.
	 *
	 * @param source The source Stringe this Chare was taken from.
	 * @param c The char value of the Chare.
	 * @param offset The offset of the Chare relative to the Stringe.
	 * @param line The 1-based line number in the Stringe.
	 * @param column The 1-based column number in the Stringe.
	 */
	Chare(Stringe source, char c, int offset, int line, int column) {
		this.source = source;
		character = c;
		this.offset = offset;
		this.line = line;
		this.column = column;
	}

	/**
	 * Returns the Chare's source Stringe.
	 *
	 * @return The Stringe this Chare is part of.
	 */
	public Stringe getSource() {
		return source;
	}

	/**
	 * Returns the Chare's underlying character.
	 *
	 * @return The char value this Chare represents.
	 */
	public char getCharacter() {
		return character;
	}

	/**
	 * Returns the Chare's offset.
	 *
	 * @return The offset of the Chare relative to its source Stringe.
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Returns the 1-based line number of the Chare.
	 *
	 * @return The line number this Chare is on.
	 */
	public int getLine() {
		if(line == 0) {
			setLineCol();
		}

		return line;
	}

	/**
	 * Returns the 1-based column number of the Chare.
	 *
	 * @return The column number this Chare is on.
	 */
	public int getColumn() {
		if(column == 0) {
			setLineCol();
		}

		return column;
	}

	/**
	 * Sets line and column information for the Chare.
	 */
	private void setLineCol() {
		line = source.getLine();
		column = source.getColumn();

		if(offset <= 0) {
			return;
		}

		for(int i = 0; i < offset; i++) {
			if(source.getParent().charAt(offset) == '\n') {
				line++;
				column = 1;
			} else {
				column++;
			}
		}
	}

	/**
	 * Returns the string representation of the current Chare.
	 * 
	 * @return The Chare as a String.
	 */
	@Override
	public String toString() {
		return String.valueOf(character);
	}
}
