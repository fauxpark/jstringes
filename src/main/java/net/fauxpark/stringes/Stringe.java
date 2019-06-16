package net.fauxpark.stringes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Represents a string or a substring in relation to its parent.
 * Provides line number, column, offset and other useful data.
 */
public class Stringe implements CharSequence, Iterable<Chare> {
	/**
	 * Cached Chare data for the Stringe.
	 */
	private Stref stref;

	/**
	 * The offset of the stringe, relative to its parent string.
	 * If the Stringe has no parent, the offset is 0.
	 */
	private int offset;

	/**
	 * The length of the Stringe.
	 */
	private int length;

	/**
	 * The 1-based line number at which the Stringe begins.
	 */
	private int line;

	/**
	 * The 1-based column number at which the Stringe begins.
	 */
	private int column;

	/**
	 * The substring represented by the Stringe.
	 */
	private String substring;

	/**
	 * Used to cache requested metadata so that we don't have a bunch of unused fields.
	 */
	private Map<String, Object> meta;

	/**
	 * Constructs a new Stringe from the specified string.
	 *
	 * @param str The string to turn into a Stringe.
	 * @throws IllegalArgumentException If the input string is null.
	 */
	public Stringe(String str) throws IllegalArgumentException {
		if(str == null) {
			throw new IllegalArgumentException("Input string cannot be null");
		}

		stref = new Stref(str);
		offset = 0;
		length = str.length();
		line = 1;
		column = 1;
		substring = null;
	}

	/**
	 * Constructs a new Stringe from another Stringe.
	 *
	 * @param stre The Stringe to clone.
	 */
	Stringe(Stringe stre) {
		stref = stre.stref;
		offset = stre.offset;
		length = stre.length;
		line = stre.line;
		column = stre.column;
		substring = stre.substring;
	}

	/**
	 * Mutates the Stringe into another Stringe.
	 *
	 * @param stre The Stringe to mutate into.
	 */
	void mutate(Stringe stre) {
		stref = stre.stref;
		offset = stre.offset;
		length = stre.length;
		line = stre.line;
		column = stre.column;
		substring = stre.substring;
	}

	/**
	 * Constructs a new Stringe from a parent Stringe with a relative offset and a length.
	 *
	 * @param parent The parent Stringe to create the Stringe from.
	 * @param offset The relative offset of the Stringe.
	 * @param length The length of the Stringe.
	 */
	private Stringe(Stringe parent, int offset, int length) {
		stref = parent.stref;
		this.offset = parent.offset + offset;
		this.length = length;
		substring = null;

		// Calculate line/col
		line = parent.line;
		column = parent.column;

		// If the offset is to the left, the line/col is already calculated. Fetch it from the Chare cache.
		if(offset < 0) {
			line = stref.chares[this.offset].getLine();
			column = stref.chares[this.offset].getColumn();

			return;
		}

		// Do nothing if the offset is the same
		if(offset == 0) {
			return;
		}

		int aOffset;

		for(int i = 0; i < offset; i++) {
			aOffset = parent.offset + i;

			if(stref.string.charAt(aOffset) == '\n') {
				line++;
				column = 1;
			} else if(stref.bases[i]) { // Advance column only for non-combining characters
				column++;
			}

			if(stref.chares[aOffset] == null) {
				stref.chares[aOffset] = new Chare(parent, stref.string.charAt(aOffset), aOffset, line, column);
			}
		}
	}

	/**
	 * Converts the specified object into a Stringe.
	 *
	 * @param obj The object to convert.
	 */
	public static Stringe toStringe(Object obj) {
		return new Stringe(obj.toString());
	}

	/**
	 * Returns an empty Stringe based on the position of another Stringe.
	 *
	 * @param basis The basis Stringe to get position info from.
	 */
	public static Stringe empty(Stringe basis) {
		return new Stringe(basis, 0, 0);
	}

	/**
	 * Indicates whether the specified Stringe is null or empty.
	 *
	 * @param stre The Stringe to test.
	 */
	public static boolean isNullOrEmpty(Stringe stre) {
		return stre == null || stre.length() == 0;
	}

	/**
	 * Returns a Stringe comprised of all text between and including the two specified Stringes.
	 * The Stringes must both belong to the same parent string.
	 *
	 * @param a The first Stringe.
	 * @param b The second Stringe.
	 *
	 * @throws IllegalArgumentException If either of the arguments are null, or if the Stringes do not belong to the same parent.
	 */
	public static Stringe range(Stringe a, Stringe b) throws IllegalArgumentException {
		if(a == null || b == null) {
			throw new IllegalArgumentException("Input Stringes cannot be null");
		}

		if(a.stref != b.stref) {
			throw new IllegalArgumentException("The Stringes must both belong to the same parent");
		}

		if(a == b) {
			return a;
		}

		if(a.isSubstringeOf(b)) {
			return b;
		}

		if(b.isSubstringeOf(a)) {
			return a;
		}

		// Right side of A intersects left side of B.
		if(a.offset > b.offset && a.offset + a.length < b.offset + b.length) {
			return a.substringe(0, b.offset + b.length - a.offset);
		}

		// Left side of A intersects right side of B.
		if(a.offset < b.offset + b.length && a.offset > b.offset) {
			return b.substringe(0, a.offset + a.length - b.offset);
		}

		// A is to the left of B.
		if(a.offset + a.length <= b.offset) {
			return a.substringe(0, b.offset + b.length - a.offset);
		}

		// B is to the left of A.
		if(b.offset + b.length <= a.offset) {
			return b.substringe(0, a.offset + a.length - b.offset);
		}

		return null;
	}

	/**
	 * Returns a Stringe comprised of all text between the two specified Stringes.
	 * Returns null if the Stringes are adjacent or intersected.
	 *
	 * @param a The first Stringe.
	 * @param b The second Stringe.
	 * @throws IllegalArgumentException If either of the arguments are null, or the Stringes do not belong to the same parent.
	 */
	public static Stringe between(Stringe a, Stringe b) throws IllegalArgumentException {
		if(a == null || b == null) {
			throw new IllegalArgumentException("Input Stringes cannot be null");
		}

		if(a.stref != b.stref) {
			throw new IllegalArgumentException("The Stringes must both belong to the same parent");
		}

		if(a == b) {
			return a;
		}

		if(a.isSubstringeOf(b)) {
			return b;
		}

		if(b.isSubstringeOf(a)) {
			return a;
		}

		// Right side of A intersects left side of B.
		if(a.offset > b.offset && a.offset + a.length < b.offset + b.length) {
			return null;
		}

		// Left side of A intersects right side of B.
		if(a.offset < b.offset + b.length && a.offset > b.offset) {
			return null;
		}

		// A is to the left of B.
		if(a.offset + a.length <= b.offset) {
			return a.substringe(a.length, b.offset - a.offset - a.length);
		}

		// B is to the left of A.
		if(b.offset + b.length <= a.offset) {
			return b.substringe(b.length, a.offset - b.offset - b.length);
		}

		return null;
	}

	/**
	 * Returns the offset of the Stringe in the string.
	 */
	public int offset() {
		return offset;
	}

	/**
	 * Returns the length of the string represented by the Stringe.
	 */
	@Override
	public int length() {
		return length;
	}

	/**
	 * Returns the 1-based line number at which the Stringe begins.
	 */
	public int getLine() {
		return line;
	}

	/**
	 * Returns the 1-based column number at which the Stringe begins.
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * Returns the index at which the Stringe ends in the string.
	 */
	public int getEnd() {
		return offset + length;
	}

	/**
	 * Returns the substring value represented by the Stringe. If the Stringe is the parent, this will provide the original string.
	 */
	public String getValue() {
		if(substring == null) {
			// Offset is added to length here because the second argument of substring() in Java is an index, not the length
			substring = stref.string.substring(offset, length + offset);
		}

		return substring;
	}

	/**
	 * Returns the original string from which the Stringe was originally derived.
	 */
	public String getParent() {
		return stref.string;
	}

	/**
	 * Returns the number of times the current string occurs in the parent string.
	 */
	public int getOccurrenceCount() {
		final String name = "occurrences";
		Map<String, Object> meta = getMeta();

		if(meta.containsKey(name)) {
			return (int) meta.get(name);
		}

		int count = Util.getMatchCount(stref.string, getValue());
		meta.put(name, count);

		return count;
	}

	/**
	 * Returns the next index in the parent string at which the current Stringe value occurs.
	 */
	public int getNextIndex() {
		final String name = "nextIndex";
		Map<String, Object> meta = getMeta();

		if(meta.containsKey(name)) {
			return (int) meta.get(name);
		}

		int nextIndex = stref.string.indexOf(getValue(), offset + 1);
		meta.put(name, nextIndex);

		return nextIndex;
	}

	/**
	 * Returns the meta information holder, creating it if necessary.
	 */
	private Map<String, Object> getMeta() {
		if(meta == null) {
			meta = new HashMap<>();
		}

		return meta;
	}

	/**
	 * Indicates if the Stringe is a substring.
	 */
	public boolean isSubstring() {
		return offset > 0 || length < stref.string.length();
	}

	/**
	 * Returns true if, and only if, length() is 0.
	 */
	public boolean isEmpty() {
		return length == 0;
	}

	/**
	 * Determines whether the current Stringe is a substringe of the specified parent Stringe.
	 *
	 * @param parent the parent Stringe to compare to.
	 */
	public boolean isSubstringeOf(Stringe parent) {
		if(stref != parent.stref) {
			return false;
		}

		return offset >= parent.offset && offset + length <= parent.offset + parent.length;
	}

	/**
	 * Returns the zero-based index at which the specified string first occurs, relative to the substringe.
	 *
	 * @param str The string to search for.
	 */
	public int indexOf(String str) {
		return indexOf(str, 0);
	}

	/**
	 * Returns the zero-based index at which the specified string first occurs, relative to the substringe.
	 * The search starts at the specified index.
	 *
	 * @param str The string to search for.
	 * @param startIndex The index at which to begin the search.
	 */
	public int indexOf(String str, int startIndex) {
		return getValue().indexOf(str, startIndex);
	}

	/**
	 * Returns the zero-based index at which the specified string first occurs, relative to the substringe.
	 *
	 * @param str The string to search for.
	 * @param startIndex The index at which to begin the search.
	 */
	public int indexOfIgnoreCase(String str, int startIndex) {
		return getValue().toLowerCase().indexOf(str.toLowerCase(), startIndex);
	}

	/**
	 * Returns the zero-based index at which the specified string first occurs, relative to the parent.
	 *
	 * @param str The string to search for.
	 */
	public int indexOfTotal(String str) {
		return indexOfTotal(str, 0);
	}

	/**
	 * Returns the zero-based index at which the specified string first occurs, relative to the parent.
	 * The search starts at the specified index.
	 *
	 * @param str The string to search for.
	 * @param startIndex The index at which to begin the search.
	 */
	public int indexOfTotal(String str, int startIndex) {
		int index = getValue().indexOf(str, startIndex);

		return index == -1 ? index : index + offset;
	}

	/**
	 * Returns the zero-based index at which the specified string first occurs, relative to the parent.
	 *
	 * @param str The string to search for.
	 * @param startIndex The index at which to begin the search.
	 */
	public int indexOfTotalIgnoreCase(String str, int startIndex) {
		int index = getValue().toLowerCase().indexOf(str.toLowerCase(), startIndex);

		return index == -1 ? index : index + offset;
	}

	/**
	 * Returns the zero-based index at which the specified character first occurs, relative to the substringe.
	 *
	 * @param c The character to search for.
	 */
	public int indexOf(char c) {
		return indexOf(c, 0);
	}

	/**
	 * Returns the zero-based index at which the specified character first occurs, relative to the substringe.
	 * The search starts at the specified index.
	 *
	 * @param c The character to search for.
	 * @param startIndex The index at which to begin the search.
	 */
	public int indexOf(char c, int startIndex) {
		return getValue().indexOf(c, startIndex);
	}

	/**
	 * Returns the zero-based index at which the specified character first occurs, relative to the substringe.
	 *
	 * @param c The character to search for.
	 * @param startIndex The index at which to begin the search.
	 */
	public int indexOfIgnoreCase(char c, int startIndex) {
		return getValue().toLowerCase().indexOf(Character.toLowerCase(c), startIndex);
	}

	/**
	 * Returns the zero-based index at which the specified character first occurs, relative to the parent.
	 *
	 * @param c The character to search for.
	 */
	public int indexOfTotal(char c) {
		return indexOfTotal(c, 0);
	}

	/**
	 * Returns the zero-based index at which the specified character first occurs, relative to the parent.
	 * The search starts at the specified index.
	 *
	 * @param c The character to search for.
	 * @param startIndex The index at which to begin the search.
	 */
	public int indexOfTotal(char c, int startIndex) {
		int index = getValue().indexOf(c, startIndex);

		return index == - 1 ? index : index + offset;
	}

	/**
	 * Returns the zero-based index at which the specified character first occurs, relative to the parent.
	 *
	 * @param c The character to search for.
	 * @param startIndex The index at which to begin the search.
	 */
	public int indexOfTotalIgnoreCase(char c, int startIndex) {
		int index = getValue().toLowerCase().indexOf(Character.toLowerCase(c), startIndex);

		return index == -1 ? index : index + offset;
	}

	/**
	 * Returns the zero-based index at which the specified string last occurs, relative to the substringe.
	 * The search starts at the specified index, and moves backwards.
	 *
	 * @param str The string to search for.
	 */
	public int lastIndexOf(String str) {
		return lastIndexOf(str, str.length());
	}

	/**
	 * Returns the zero-based index at which the specified string last occurs, relative to the substringe.
	 * The search starts at the specified index, and moves backwards.
	 *
	 * @param str The string to search for.
	 * @param endIndex The index at which to begin the search.
	 */
	public int lastIndexOf(String str, int endIndex) {
		return getValue().lastIndexOf(str, endIndex);
	}

	/**
	 * Returns the zero-based index at which the specified string last occurs, relative to the substringe.
	 * The search starts at the specified index, and moves backwards.
	 *
	 * @param str The string to search for.
	 * @param endIndex The index at which to begin the search.
	 */
	public int lastIndexOfIgnoreCase(String str, int endIndex) {
		return getValue().toLowerCase().lastIndexOf(str.toLowerCase(), endIndex);
	}

	/**
	 * Returns the zero-based index at which the specified string last occurs, relative to the parent.
	 * The search starts at the specified index, and moves backwards.
	 *
	 * @param str The string to search for.
	 */
	public int lastIndexOfTotal(String str) {
		return lastIndexOfTotal(str, getValue().length());
	}

	/**
	 * Returns the zero-based index at which the specified string last occurs, relative to the parent.
	 * The search starts at the specified index, and moves backwards.
	 *
	 * @param str The string to search for.
	 * @param endIndex The index at which to begin the search.
	 */
	public int lastIndexOfTotal(String str, int endIndex) {
		int index = getValue().lastIndexOf(str, endIndex);

		return index == -1 ? index : index + offset;
	}

	/**
	 * Returns the zero-based index at which the specified string last occurs, relative to the parent.
	 * The search starts at the specified index, and moves backwards.
	 *
	 * @param str The string to search for.
	 * @param endIndex The index at which to begin the search.
	 */
	public int lastIndexOfTotalIgnoreCase(String str, int endIndex) {
		int index = getValue().toLowerCase().lastIndexOf(str.toLowerCase(), endIndex);

		return index == -1 ? index : index + offset;
	}

	/**
	 * Returns the zero-based index at which the specified character last occurs, relative to the substringe.
	 *
	 * @param c The character to search for.
	 */
	public int lastIndexOf(char c) {
		return lastIndexOf(c, getValue().length());
	}

	/**
	 * Returns the zero-based index at which the specified character last occurs, relative to the substringe.
	 * The search starts at the specified index, and moves backwards.
	 *
	 * @param c The character to search for.
	 * @param endIndex The index at which to begin the search.
	 */
	public int lastIndexOf(char c, int endIndex) {
		return getValue().lastIndexOf(c, endIndex);
	}

	/**
	 * Returns the zero-based index at which the specified character last occurs, relative to the substringe.
	 * The search starts at the specified index, and moves backwards.
	 *
	 * @param c The character to search for.
	 * @param endIndex The index at which to begin the search.
	 */
	public int lastIndexOfIgnoreCase(char c, int endIndex) {
		return getValue().toLowerCase().lastIndexOf(Character.toLowerCase(c), endIndex);
	}

	/**
	 * Returns the zero-based index at which the specified character last occurs, relative to the parent.
	 *
	 * @param c The character to search for.
	 */
	public int lastIndexOfTotal(char c) {
		return lastIndexOfTotal(c, getValue().length());
	}

	/**
	 * Returns the zero-based index at which the specified character last occurs, relative to the parent.
	 * The search starts at the specified index, and moves backwards.
	 *
	 * @param c The character to search for.
	 * @param endIndex The index at which to begin the search.
	 */
	public int lastIndexOfTotal(char c, int endIndex) {
		int index = getValue().lastIndexOf(c, endIndex);

		return index == -1 ? index : index + offset;
	}

	/**
	 * Returns the zero-based index at which the specified character last occurs, relative to the parent.
	 * The search starts at the specified index, and moves backwards.
	 *
	 * @param c The character to search for.
	 * @param endIndex The index at which to begin the search.
	 */
	public int lastIndexOfTotalIgnoreCase(char c, int endIndex) {
		int index = getValue().toLowerCase().lastIndexOf(Character.toLowerCase(c), endIndex);

		return index == -1 ? index : index + offset;
	}

	/**
	 * Creates a substringe from the Stringe, starting at the specified index and extending for the specified length.
	 *
	 * @param offset The offset at which to begin the substringe.
	 * @param length The length of the substringe.
	 */
	public Stringe substringe(int offset, int length) {
		return new Stringe(this, offset, length);
	}

	/**
	 * Creates a substringe from the Stringe, starting at the specified index and extending to the end.
	 *
	 * @param offset The offset at which to begin the substringe.
	 */
	public Stringe substringe(int offset) {
		return new Stringe(this, offset, length - offset);
	}

	/**
	 * Returns a Stringe that is a subsequence of this sequence.
	 *
	 * @param startIndex The index to start the subsequence from.
	 * @param endIndex The index to end the subsequence at.
	 * @throws StringIndexOutOfBoundsException If start or end are negative, if end is greater than length(), or if start is greater than end.
	 */
	@Override
	public Stringe subSequence(int startIndex, int endIndex) throws StringIndexOutOfBoundsException {
		if(startIndex < 0 || endIndex < 0) {
			throw new StringIndexOutOfBoundsException("Indices cannot be negative");
		}

		if(startIndex >= length || endIndex >= length) {
			throw new StringIndexOutOfBoundsException("Indices must be within Stringe boundaries");
		}

		if(startIndex > endIndex) {
			throw new StringIndexOutOfBoundsException("The begin index cannot be greater than the end index");
		}

		return substringe(startIndex, startIndex + endIndex);
	}

	/**
	 * Returns a substringe that contains all characters between the two specified positions in the Stringe.
	 *
	 * @param left The left side of the slice.
	 * @param right The right side of the slice.
	 *
	 * @throws IllegalArgumentException If the right side of the slice is less than the left side.
	 * @throws StringIndexOutOfBoundsException If either of the arguments are negative, or greater than the length of the Stringe.
	 */
	public Stringe slice(int left, int right) throws IllegalArgumentException, StringIndexOutOfBoundsException {
		if(right < left) {
			throw new IllegalArgumentException("Right side of the slice cannot be less than the left side");
		}

		if(left < 0 || right < 0) {
			throw new StringIndexOutOfBoundsException("Indices cannot be negative");
		}

		if(left > length || right > length) {
			throw new StringIndexOutOfBoundsException("Indices must be within Stringe boundaries");
		}

		return new Stringe(this, left, right - left);
	}

	/**
	 * Returns a new substringe whose left and right boundaries are offset by the specified values.
	 *
	 * @param left The amount, in characters, to offset the left boundary to the left.
	 * @param right The amount, in characters, to offset the right boundary to the right.
	 *
	 * @throws StringIndexOutOfBoundsException If the new length is negative or the Stringe is expanded beyond the ends of the string.
	 */
	public Stringe dilate(int left, int right) throws StringIndexOutOfBoundsException {
		int exOffset = offset - left;

		if(exOffset < 0) {
			throw new StringIndexOutOfBoundsException("Expanded offset cannot be negative");
		}

		int exLength = length + right + left;

		if(exLength < 0) {
			throw new StringIndexOutOfBoundsException("Expanded length cannot be negative");
		}

		if(exOffset + exLength > stref.string.length()) {
			throw new StringIndexOutOfBoundsException("Tried to extend beyond the end of the string");
		}

		return new Stringe(this, -left, exLength);
	}

	/**
	 * Returns the Stringe with any leading and trailing white space removed.
	 */
	public Stringe trim() {
		if(length == 0) {
			return this;
		}

		int a = 0;
		int b = length;
		String value = getValue();

		do {
			if(Character.isWhitespace(value.charAt(a))) {
				a++;
			} else if(Character.isWhitespace(value.charAt(b - 1))) {
				b--;
			} else {
				break;
			}
		} while(a < b && b > 0 && a < length);

		return substringe(a, b - a);
	}

	/**
	 * Returns the Stringe with any leading and trailing occurrences of the specified characters removed.
	 *
	 * @param trimChars The characters to remove.
	 */
	public Stringe trim(char... trimChars) {
		if(length == 0) {
			return this;
		}

		boolean useDefault = trimChars.length == 0;
		int a = 0;
		int b = length;
		String value = getValue();

		do {
			if(useDefault ? Character.isWhitespace(value.charAt(a)) : Util.contains(trimChars, value.charAt(a))) {
				a++;
			} else if(useDefault ? Character.isWhitespace(value.charAt(b - 1)) : Util.contains(trimChars, value.charAt(b - 1))) {
				b--;
			} else {
				break;
			}
		} while(a < b && b > 0 && a < length);

		return substringe(a, b - a);
	}

	/**
	 * Returns the Stringe with any leading occurrences of the specified characters removed.
	 *
	 * @param trimChars The characters to remove.
	 */
	public Stringe trimStart(char... trimChars) {
		if(length == 0) {
			return this;
		}

		boolean useDefault = trimChars.length == 0;
		int a = 0;
		String value = getValue();

		while(a < length) {
			if(useDefault ? Character.isWhitespace(value.charAt(a)) : Util.contains(trimChars, value.charAt(a))) {
				a++;
			} else {
				break;
			}
		}

		return substringe(a);
	}

	/**
	 * Returns the Stringe with any trailing occurrences of the specified characters removed.
	 *
	 * @param trimChars The characters to remove.
	 */
	public Stringe trimEnd(char... trimChars) {
		if(length == 0) {
			return this;
		}

		boolean useDefault = trimChars.length == 0;
		int b = length;
		String value = getValue();

		do {
			if(useDefault ? Character.isWhitespace(value.charAt(b - 1)) : Util.contains(trimChars, value.charAt(b - 1))) {
				b--;
			} else {
				break;
			}
		} while(b > 0);

		return substringe(0, b);
	}

	/**
	 * Determines whether the left side of the line on which the Stringe exists is composed entirely of white space.
	 */
	public boolean isLeftPadded() {
		if(offset == 0) {
			for(int i = 0; i < length; i++) {
				if(Character.isWhitespace(stref.string.charAt(i))) {
					return true;
				}
			}

			return false;
		}

		for(int i = offset - 1; i >= 0; i--) {
			if(!Character.isWhitespace(stref.string.charAt(i))) {
				return false;
			}

			if(stref.string.charAt(i) == '\n') {
				return true;
			}
		}

		return true;
	}

	/**
	 * Determines whether the right side of the line on which the Stringe exists is composed entirely of white space.
	 */
	public boolean isRightPadded() {
		boolean found = false;

		if(offset + length == stref.string.length()) {
			for(int i = stref.string.length() - 1; i >= offset; i--) {
				if(!Character.isWhitespace(stref.string.charAt(i))) {
					return found;
				}

				found = true;
			}

			return false;
		}

		for(int i = offset + length; i < stref.string.length(); i++) {
			if(!Character.isWhitespace(stref.string.charAt(i))) {
				return false;
			}

			if(stref.string.charAt(i) == '\n') {
				return true;
			}
		}

		return true;
	}

	/**
	 * Splits the Stringe into multiple parts by the specified delimiters.
	 *
	 * @param separators The delimiters by which to split the Stringe.
	 */
	public List<Stringe> split(char... separators) {
		return split(separators, true);
	}

	/**
	 * Splits the Stringe into multiple parts by the specified delimiters.
	 *
	 * @param separators The delimiters by which to split the Stringe.
	 */
	public List<Stringe> split(String... separators) {
		return split(separators, true);
	}

	/**
	 * Splits the Stringe into multiple parts by the specified delimiters.
	 *
	 * @param separators The delimiters by which to split the Stringe.
	 * @param keepEmpty Specifies whether empty substringes should be included in the return value.
	 */
	public List<Stringe> split(char[] separators, boolean keepEmpty) {
		int start = 0;
		String value = getValue();
		List<Stringe> streList = new ArrayList<>();

		for(int i = 0; i < length; i++) {
			if(!Util.contains(separators, value.charAt(i))) {
				continue;
			}

			if(keepEmpty || i - start > 0) {
				streList.add(substringe(start, i - start));
			}

			start = i + 1;
		}

		if(start > length) {
			return streList;
		}

		if(keepEmpty || length - start > 0) {
			streList.add(substringe(start, length - start));
		}

		if(streList.size() > 0) {
			return streList;
		} else {
			return null;
		}
	}

	/**
	 * Splits the Stringe into multiple parts by the specified delimiters.
	 *
	 * @param separators The delimiters by which to split the Stringe.
	 * @param keepEmpty Specifies whether empty substringes should be included in the return value.
	 */
	public List<Stringe> split(String[] separators, boolean keepEmpty) {
		int start = 0;
		List<Stringe> streList = new ArrayList<>();

		for(int i = 0; i < length; i++) {
			String hit = null;

			for(String sep : separators) {
				if(indexOf(sep) == i) {
					hit = sep;

					break;
				}
			}

			if(hit == null) {
				continue;
			}

			if(keepEmpty || i - start > 0) {
				streList.add(substringe(start, i - start));
			}

			start = i + hit.length();
		}

		if(start > length) {
			return streList;
		}

		if(keepEmpty || length - start > 0) {
			streList.add(substringe(start, length - start));
		}

		return streList;
	}

	/**
	 * Splits the Stringe into multiple parts by the specified delimiters.
	 *
	 * @param separators The delimiters by which to split the Stringe.
	 * @param count The maximum number of substringes to return. If the count exceeds this number, the last item will be the remainder of the Stringe.
	 * @param keepEmpty Specifies whether empty substringes should be included in the return value.
	 */
	public List<Stringe> split(char[] separators, int count, boolean keepEmpty) {
		if(count == 0) {
			return null;
		}

		List<Stringe> streList = new ArrayList<>();

		if(count == 1) {
			streList.add(this);

			return streList;
		}

		int matches = 0;
		int start = 0;
		String value = getValue();

		for(int i = 0; i < length; i++) {
			if(Util.contains(separators, value.charAt(i))) {
				if(keepEmpty || i - start > 0) {
					streList.add(substringe(start, i - start));
				}

				start = i + 1;
				matches++;
			}

			if(matches < count - 1) {
				continue;
			}

			if(start > length) {
				return streList;
			}

			streList.add(substringe(start, length - start));

			return streList;
		}

		if(start > length || matches >= count) {
			return streList;
		}

		if(keepEmpty || length - start > 0) {
			streList.add(substringe(start, length - start));
		}

		return streList;
	}

	/**
	 * Splits the Stringe into multiple parts by the specified delimiters.
	 *
	 * @param separators The delimiters by which to split the Stringe.
	 * @param count The maximum number of substringes to return. If the count exceeds this number, the last item will be the remainder of the Stringe.
	 * @param keepEmpty Specifies whether empty substringes should be included in the return value.
	 */
	public List<Stringe> split(String[] separators, int count, boolean keepEmpty) {
		if(count == 0) {
			return null;
		}

		List<Stringe> streList = new ArrayList<>();

		if(count == 1) {
			streList.add(this);

			return streList;
		}

		int matches = 0;
		int start = 0;

		for(int i = 0; i < length; i++) {
			String hit = null;

			for(String sep : separators) {
				if(indexOf(sep) == i) {
					hit = sep;

					break;
				}
			}

			if(hit != null) {
				if(keepEmpty || i - start > 0) {
					streList.add(substringe(start, i - start));
				}

				start = i + hit.length();
				matches++;
			}

			if(matches < count - 1) {
				continue;
			}

			if(start > length) {
				return streList;
			}

			streList.add(substringe(start, length - start));

			return streList;
		}

		if(start > length || matches >= count) {
			return streList;
		}

		if(keepEmpty || length - start > 0) {
			streList.add(substringe(start, length - start));
		}

		return streList;
	}

	/**
	 * Tests if this Stringe's value starts with the specified prefix.
	 *
	 * @param prefix The prefix.
	 */
	public boolean startsWith(String prefix) {
		return getValue().startsWith(prefix);
	}

	/**
	 * Tests if the substring of this Stringe's value beginning at the specified index starts with the specified prefix.
	 *
	 * @param prefix The prefix.
	 * @param toffset Where to begin looking in the string.
	 */
	public boolean startsWith(String prefix, int toffset) {
		return getValue().startsWith(prefix, toffset);
	}

	/**
	 * Tests if the string value of this Stringe ends with the specified suffix.
	 *
	 * @param suffix The suffix.
	 */
	public boolean endsWith(String suffix) {
		return getValue().endsWith(suffix);
	}

	/**
	 * Returns a copy of this Stringe with all of its characters converted to lower case.
	 */
	public Stringe toLowerCase() {
		Stringe stre = new Stringe(this);
		stre.substring = getValue().toLowerCase();

		return stre;
	}

	/**
	 * Returns a copy of this Stringe with all of its characters converted to upper case.
	 */
	public Stringe toUpperCase() {
		Stringe stre = new Stringe(this);
		stre.substring = getValue().toUpperCase();

		return stre;
	}

	/**
	 * Returns the char value at the specified index.
	 *
	 * @param index The index of the char value.
	 * @throws StringIndexOutOfBoundsException If the index is negative or not less than the length of this Stringe.
	 */
	@Override
	public char charAt(int index) throws StringIndexOutOfBoundsException {
		return chareAt(index).getCharacter();
	}

	/**
	 * Returns the Chare at the specified index.
	 *
	 * @param index The index of the Chare.
	 * @throws StringIndexOutOfBoundsException If the index is negative or not less than the length of this Stringe.
	 */
	public Chare chareAt(int index) throws StringIndexOutOfBoundsException {
		if(index < 0 || index >= length) {
			throw new StringIndexOutOfBoundsException(index);
		}

		if(stref.chares[index] == null) {
			stref.chares[index] = new Chare(this, stref.string.charAt(index), index + offset);
		}

		return stref.chares[index];
	}

	/**
	 * Determines whether the current Stringe is equal to the specified object.
	 */
	@Override
	public boolean equals(Object obj) {
		Stringe stre = (Stringe) obj;

		if(stre == null) {
			return false;
		}

		return stre == this;
	}

	/**
	 * Compares the string value of this Stringe to another string, ignoring case considerations.
	 *
	 * @param str The string to compare this Stringe against.
	 */
	public boolean equalsIgnoreCase(String str) {
		return getValue().equalsIgnoreCase(str);
	}

	/**
	 * Compares the string value of this Stringe to the string value of another Stringe, ignoring case considerations.
	 *
	 * @param stre The Stringe to compare this Stringe against.
	 */
	public boolean equalsIgnoreCase(Stringe stre) {
		return getValue().equalsIgnoreCase(stre.getValue());
	}

	/**
	 * Returns the hash code of the current Stringe.
	 */
	@Override
	public int hashCode() {
		return Util.hashOf(stref.string, offset, length);
	}

	/**
	 * Returns the string value of the Stringe.
	 */
	@Override
	public String toString() {
		return getValue();
	}

	/**
	 * Returns an iterator that iterates through the Chares in the Stringe.
	 */
	@Override
	public Iterator<Chare> iterator() {
		List<Chare> chares = new ArrayList<>();

		for(int i = 0; i < length; i++) {
			chares.add(chareAt(i));
		}

		return chares.iterator();
	}

	/**
	 * Stores cached character data for a Stringe.
	 */
	private class Stref {
		/**
		 * The underlying String value.
		 */
		public final String string;

		/**
		 * An array of Chares representing each character in the string.
		 */
		public final Chare[] chares;

		/**
		 * An array for keeping track of non-combining characters for column numbers.
		 */
		public final boolean[] bases;

		/**
		 * Constructs a new Stref and populates the Chare and base arrays.
		 *
		 * @param str The underlying String object to set.
		 */
		public Stref(String str) {
			string = str;
			chares = new Chare[str.length()];
			bases = new boolean[str.length()];

			// If the character is not combining, it takes up space, and thus a column.
			for(int i = 0; i < str.length(); i++) {
				if(Character.getType(str.charAt(i)) != Character.NON_SPACING_MARK) {
					bases[i] = true;
				}
			}
		}
	}
}
