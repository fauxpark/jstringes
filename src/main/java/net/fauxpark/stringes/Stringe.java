package net.fauxpark.stringes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Represents a string or a substring in relation to its parent.
 * Provides line number, column, offset and other useful data.
 */
public class Stringe implements CharSequence, Iterable<Chare> {
	private Stref stref;

	private int offset;

	private int length;

	private int line;

	private int column;

	private String substring;

	// Used to cache requested metadata so that we don't have a bunch of unused fields
	private Map<String, Object> meta = null;

	/**
	 * Creates a new stringe from the specified string.
	 *
	 * @param value The string to turn into a stringe.
	 */
	public Stringe(String value) throws IllegalArgumentException {
		if(value == null) {
			throw new IllegalArgumentException("value is null");
		}

		stref = new Stref(value);
		offset = 0;
		length = value.length();
		line = 1;
		column = 1;
		substring = null;
	}

	Stringe(Stringe value) {
		stref = value.stref;
		offset = value.offset;
		length = value.length;
		line = value.line;
		column = value.column;
		substring = value.substring;
	}

	private Stringe(Stringe parent, int relativeOffset, int length) {
		stref = parent.stref;
		offset = parent.offset + relativeOffset;
		this.length = length;
		substring = null;

		// Calculate line/col
		line = parent.line;
		column = parent.column;

		// If the offset is to the left, the line/col is already calculated. Fetch it from the Chare cache.
		if(relativeOffset < 0) {
			line = stref.chares[offset].getLine();
			column = stref.chares[offset].getColumn();

			return;
		}

		// Do nothing if the offset is the same
		if(relativeOffset == 0) {
			return;
		}

		int aOffset;

		for(int i = 0; i < relativeOffset; i++) {
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
	 * Converts the specified value into a stringe.
	 *
	 * @param value The object to convert.
	 */
	public static Stringe toStringe(Object value) {
		return new Stringe(value.toString());
	}

	/**
	 * Returns an empty stringe based on the position of another stringe.
	 *
	 * @param basis The basis stringe to get position info from.
	 */
	public static Stringe empty(Stringe basis) {
		return new Stringe(basis, 0, 0);
	}

	/**
	 * Indicates whether the specified stringe is null or empty.
	 *
	 * @param stringe The stringe to test.
	 */
	public static boolean isNullOrEmpty(Stringe stringe) {
		return stringe == null || stringe.length() == 0;
	}

	/**
	 * Returns a stringe whose endpoints are the specified strings.
	 * The stringes must both belong to the same parent string.
	 *
	 * @param a The first stringe.
	 * @param b The second stringe.
	 *
	 * @throws IllegalArgumentException If either of the arguments are null, or if the stringes do not belong to the same parent.
	 */
	public static Stringe range(Stringe a, Stringe b) throws IllegalArgumentException {
		if(a == null) {
			throw new IllegalArgumentException("a is null");
		}

		if(b == null) {
			throw new IllegalArgumentException("b is null");
		}

		if(a.stref != b.stref) {
			throw new IllegalArgumentException("The stringes do not belong to the same parent.");
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
	 * Returns a stringe comprised of all text between the two specified stringes.
	 * Returns null if the stringes are adjacent or intersected.
	 *
	 * @param a The first stringe.
	 * @param b The second stringe.
	 */
	public static Stringe between(Stringe a, Stringe b) throws IllegalArgumentException {
		if(a == null) {
			throw new IllegalArgumentException("a is null");
		}

		if(b == null) {
			throw new IllegalArgumentException("b is null");
		}

		if(a.stref != b.stref) {
			throw new IllegalArgumentException("The stringes do not belong to the same parent.");
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

		if(a.offset > b.offset && a.offset + a.length < b.offset + b.length) {
			return null;
		}

		if(a.offset < b.offset + b.length && a.offset > b.offset) {
			return null;
		}

		if(a.offset + a.length <= b.offset) {
			return a.substringe(a.length, b.offset - a.offset - a.length);
		}

		if(b.offset + b.length <= a.offset) {
			return b.substringe(b.length, a.offset - b.offset - b.length);
		}

		return null;
	}

	/**
	 * The offset of the stringe in the string.
	 */
	public int offset() {
		return offset;
	}

	/**
	 * The length of the string represented by the stringe.
	 */
	@Override
	public int length() {
		return length;
	}

	/**
	 * The 1-based line number at which the stringe begins.
	 */
	public int getLine() {
		return line;
	}

	/**
	 * The 1-based column at which the stringe begins.
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * The index at which the stringe ends in the string.
	 */
	public int getEnd() {
		return offset + length;
	}

	/**
	 * The substring value represented by the stringe. If the stringe is the parent, this will provide the original string.
	 */
	public String getValue() {
		if(substring == null) {
			// Offset is added to length here because the second argument of substring() in Java is an index, not the length
			substring = stref.string.substring(offset, length + offset);
		}

		return substring;
	}

	/**
	 * Gets the original string from which the stringe was originally derived.
	 * @return
	 */
	public String getParent() {
		return stref.string;
	}

	/**
	 * The number of times the current string occurs in the parent string.
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
	 * The next index in the parent string at which the current stringe value occurs.
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

	private Map<String, Object> getMeta() {
		if(meta == null) {
			meta = new HashMap<String, Object>();
		}

		return meta;
	}

	/**
	 * Indicates if the stringe is a substring.
	 */
	public boolean isSubstring() {
		return offset > 0 || length < stref.string.length();
	}

	/**
	 * Indicates if the stringe is empty.
	 */
	public boolean isEmpty() {
		return length == 0;
	}

	/**
	 * Determines whether the current stringe is a substringe of the specified parent stringe.
	 *
	 * @param parent the parent stringe to compare to.
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
	 * @param input The string to search for.
	 */
	public int indexOf(String input) {
		return getValue().indexOf(input);
	}

	/**
	 * Returns the zero-based index at which the specified string first occurs, relative to the substringe.
	 * The search starts at the specified index.
	 *
	 * @param input The string to search for.
	 * @param start The index at which to begin the search.
	 */
	public int indexOf(String input, int start) {
		return getValue().indexOf(input, start);
	}

	/**
	 * Returns the zero-based index at which the specified string first occurs, relative to the parent.
	 *
	 * @param input The string to search for.
	 */
	public int indexOfTotal(String input) {
		int index = getValue().indexOf(input);

		return index == -1 ? index : index + offset;
	}

	/**
	 * Returns the zero-based index at which the specified string first occurs, relative to the parent.
	 * The search starts at the specified index.
	 *
	 * @param input The string to search for.
	 * @param start The index at which to begin the search.
	 */
	public int indexOfTotal(String input, int start) {
		int index = getValue().indexOf(input, start);

		return index == -1 ? index : index + offset;
	}

	/**
	 * Returns the zero-based index at which the specified character first occurs, relative to the substringe.
	 *
	 * @param input The character to search for.
	 */
	public int indexOf(char input) {
		return getValue().indexOf(input);
	}

	/**
	 * Returns the zero-based index at which the specified character first occurs, relative to the substringe.
	 * The search starts at the specified index.
	 *
	 * @param input The character to search for.
	 * @param start The index at which to begin the search.
	 */
	public int indexOf(char input, int start) {
		return getValue().indexOf(input, start);
	}

	/**
	 * Returns the zero-based index at which the specified character first occurs, relative to the parent.
	 *
	 * @param input The character to search for.
	 */
	public int indexOfTotal(char input) {
		int index = getValue().indexOf(input);

		return index == -1 ? index : index + offset;
	}

	/**
	 * Returns the zero-based index at which the specified character first occurs, relative to the parent.
	 * The search starts at the specified index.
	 *
	 * @param input The character to search for.
	 * @param start The index at which to begin the search.
	 */
	public int indexOfTotal(char input, int start) {
		int index = getValue().indexOf(input, start);

		return index == -1 ? index : index + offset;
	}

	/**
	 * Returns the zero-based index at which the specified string last occurs, relative to the substringe.
	 * The search starts at the specified index, and moves backwards.
	 *
	 * @param input The string to search for.
	 */
	public int lastIndexOf(String input) {
		return getValue().lastIndexOf(input);
	}

	/**
	 * Returns the zero-based index at which the specified string last occurs, relative to the substringe.
	 * The search starts at the specified index, and moves backwards.
	 *
	 * @param input The string to search for.
	 * @param start The index at which to begin the search.
	 */
	public int lastIndexOf(String input, int start) {
		return getValue().lastIndexOf(input, start);
	}

	/**
	 * Returns the zero-based index at which the specified string last occurs, relative to the parent.
	 * The search starts at the specified index, and moves backwards.
	 *
	 * @param input The string to search for.
	 */
	public int lastIndexOfTotal(String input) {
		int index = getValue().lastIndexOf(input);

		return index == -1 ? index : index + offset;
	}

	/**
	 * Returns the zero-based index at which the specified string last occurs, relative to the parent.
	 * The search starts at the specified index, and moves backwards.
	 *
	 * @param input The string to search for.
	 * @param start The index at which to begin the search.
	 */
	public int lastIndexOfTotal(String input, int start) {
		int index = getValue().lastIndexOf(input, start);

		return index == -1 ? index : index + offset;
	}

	/**
	 * Returns the zero-based index at which the specified character last occurs, relative to the substringe.
	 *
	 * @param input The character to search for.
	 */
	public int lastIndexOf(char input) {
		return getValue().lastIndexOf(input);
	}

	/**
	 * Returns the zero-based index at which the specified character last occurs, relative to the substringe.
	 * The search starts at the specified index, and moves backwards.
	 *
	 * @param input The character to search for.
	 * @param start The index at which to begin the search.
	 */
	public int lastIndexOf(char input, int start) {
		return getValue().lastIndexOf(input, start);
	}

	/**
	 * Returns the zero-based index at which the specified character last occurs, relative to the parent.
	 *
	 * @param input The character to search for.
	 */
	public int lastIndexOfTotal(char input) {
		int index = getValue().lastIndexOf(input);

		return index == -1 ? index : index + offset;
	}

	/**
	 * Returns the zero-based index at which the specified character last occurs, relative to the parent.
	 * The search starts at the specified index, and moves backwards.
	 *
	 * @param input The character to search for.
	 * @param start The index at which to begin the search.
	 */
	public int lastIndexOfTotal(char input, int start) {
		int index = getValue().lastIndexOf(input, start);

		return index == -1 ? index : index + offset;
	}

	/**
	 * Creates a substringe from the stringe, starting at the specified index and extending for the specified length.
	 *
	 * @param offset The offset at which to begin the substringe.
	 * @param length The length of the substringe.
	 */
	public Stringe substringe(int offset, int length) {
		return new Stringe(this, offset, length);
	}

	/**
	 * Creates a substringe from the stringe, starting at  the specified index and extending to the end.
	 *
	 * @param offset The offset at which to begin the substringe.
	 */
	public Stringe substringe(int offset) {
		return new Stringe(this, offset, length - offset);
	}

	/**
	 * Returns a character sequence (as a substringe) that is a subsequence of this sequence.
	 */
	@Override
	public CharSequence subSequence(int beginIndex, int endIndex) {
		if(beginIndex < 0 || endIndex < 0) {
			throw new IndexOutOfBoundsException("Indices cannot be negative.");
		}

		if(endIndex > length) {
			throw new IndexOutOfBoundsException("The end index cannot be greater than the length.");
		}

		if(beginIndex > endIndex) {
			throw new IndexOutOfBoundsException("The begin index cannot be greater than the end index.");
		}

		return substringe(beginIndex, beginIndex + endIndex);
	}

	/**
	 * Returns a substringe that contains all characters between the two specified positions in the stringe.
	 *
	 * @param a The left side of the slice.
	 * @param b The right side of the slice.
	 */
	public Stringe slice(int a, int b) throws IllegalArgumentException, IndexOutOfBoundsException {
		if(b < a) {
			throw new IllegalArgumentException("'b' cannot be less than 'a'.");
		}

		if(b < 0 || a < 0) {
			throw new IllegalArgumentException("Indices cannot be negative.");
		}

		if(a > length || b > length) {
			throw new IndexOutOfBoundsException("Indices must be within stringe boundaries.");
		}

		return new Stringe(this, a, b - a);
	}

	/**
	 * Returns a new substringe whose left and right boundaries are offset by the specified values.
	 *
	 * @param left The amount, in characters, to offset the left boundary to the left.
	 * @param right The amount, in characters, to offset the right boundary to the right.
	 */
	public Stringe dilate(int left, int right) throws IllegalArgumentException, IndexOutOfBoundsException {
		int exIndex = offset - left;

		if(exIndex < 0) {
			throw new IllegalArgumentException("Expanded offset was negative.");
		}

		int exLength = length + right + left;

		if(exLength < 0) {
			throw new IllegalArgumentException("Expanded length was negative.");
		}

		if(exIndex + exLength > stref.string.length()) {
			throw new IndexOutOfBoundsException("Expanded stringe tried to extend beyond the end of the string.");
		}

		return new Stringe(this, -left, exLength);
	}

	/**
	 * Returns the stringe with all leading and trailing white space characters removed.
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
	 * Returns the stringe with any occurrences of the specified characters stripped from the ends.
	 *
	 * @param trimChars The characters to strip off the ends of the stringe.
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
	 * Returns a copy of the stringe with the specified characters removed from the start.
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
	 * Returns a copy of the stringe with the specified characters removed from the end.
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
	 * Indicates whether the left side of the line on which the stringe exists is composed entirely of white space.
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
	 * Indicates whether the line context to the right side of the stringe is composed of uninterrupted white space.
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
	 * Splits the stringe into multiple parts by the specified delimiters.
	 *
	 * @param separators The delimiters by which to split the stringe.
	 */
	public List<Stringe> split(char... separators) {
		return split(separators, true);
	}

	/**
	 * Splits the stringe into multiple parts by the specified delimiters.
	 *
	 * @param separators The delimiters by which to split the stringe.
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
	 * @param toffset Where to begin looking in the String.
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
	 * @throws IndexOutOfBoundsException If the index is negative or not less than the length of this Stringe.
	 */
	@Override
	public char charAt(int index) throws IndexOutOfBoundsException {
		return chareAt(index).getCharacter();
	}

	/**
	 * Returns the Chare at the specified index.
	 *
	 * @param index The index of the Chare.
	 * @throws IndexOutOfBoundsException if the index is negative or not less than the length of this Stringe.
	 */
	public Chare chareAt(int index) throws IndexOutOfBoundsException {
		if(index < 0 || index > length - 1) {
			throw new IndexOutOfBoundsException();
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
	 * Returns the hash of the current stringe.
	 */
	@Override
	public int hashCode() {
		return Util.hashOf(stref.string, offset, length);
	}

	/**
	 * Returns the string value of the stringe.
	 */
	@Override
	public String toString() {
		return getValue();
	}

	/**
	 * Returns an iterator that iterates through the characteres in the stringe.
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
		public final String string;

		public final Chare[] chares;

		public final boolean[] bases;

		public Stref(String str) {
			string = str;
			chares = new Chare[str.length()];
			bases = new boolean[str.length()];

			for(int i = 0; i < str.length(); i++) {
				if(!Pattern.matches("\\p{M}", "" + str.charAt(i))) {
					bases[i] = true;
				}
			}
		}
	}
}
