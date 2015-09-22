package net.fauxpark.stringes;

/**
 * Represents a charactere, which provides location information on a character taken from a stringe.
 */
public final class Chare {
	/**
	 * The stringe from which the charactere was taken.
	 */
	private final Stringe src;

	/**
	 * The underlying character.
	 */
	private final char character;

	/**
	 * The position of the charactere in the stringe.
	 */
	private final int offset;

	/**
	 * The line on which the charactere appears.
	 */
	private int line;

	/**
	 * The column on which the charactere appears.
	 */
	private int column;

	public Stringe getSource() {
		return src;
	}

	public char getCharacter() {
		return character;
	}

	public int getOffset() {
		return offset;
	}

	public int getLine() {
		if(line == 0) {
			setLineCol();
		}

		return line;
	}

	public int getColumn() {
		if(column == 0) {
			setLineCol();
		}

		return column;
	}

	private void setLineCol() {
		line = src.getLine();
		column = src.getColumn();

		if(offset <= 0) {
			return;
		}

		for(int i = 0; i < offset; i++) {
			if(src.getParent().charAt(offset) == '\n') {
				line++;
				column = 1;
			} else {
				column ++;
			}
		}
	}

	Chare(Stringe source, char c, int offset) {
		src = source;
		character = c;
		this.offset = offset;
		line = 0;
		column = 0;
	}

	Chare(Stringe source, char c, int offset, int line, int column) {
		src = source;
		character = c;
		this.offset = offset;
		this.line = line;
		this.column = column;
	}

	/**
	 * Returns the string representation of the current charactere.
	 */
	@Override
	public String toString() {
		return String.valueOf(character);
	}
}
