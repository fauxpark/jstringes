package net.fauxpark.stringes;

public class Util {
	/**
	 * Calculates a hash code from the specified objects.
	 *
	 * @param objects The objects to hash.
	 *
	 * @return The hash code of the given objects.
	 */
	public static int hashOf(Object... objects) {
		int hash = 0;

		for(Object o : objects) {
			hash = hash * 31 + (o == null ? 0 : o.hashCode());
		}

		return hash;
	}

	/**
	 * Counts the number of times a substring occurs in a parent string.
	 *
	 * @param parent The parent string to search.
	 * @param sub The substring to look for.
	 *
	 * @return The number of occurrences of the given substring in the given parent string.
	 */
	public static int getMatchCount(String parent, String sub) {
		if(parent == null || sub == null) {
			return 0;
		}

		if(parent.length() * sub.length() == 0) {
			return 0;
		}

		int next = 0;
		int start = 0;
		int count = 0;

		while(start + sub.length() < parent.length()) {
			next = parent.indexOf(sub, start);

			if(next == -1) {
				return count;
			}

			start = next + sub.length();
			count++;
		}

		return count;
	}

	/**
	 * Determines whether the specified array contains the specified character.
	 *
	 * @param array The array to search.
	 * @param key The character to look for.
	 *
	 * @return True if the given character is present in the given character array.
	 */
	public static boolean contains(final char[] array, final char key) {
		for(final char c : array) {
			if(c == key) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Determines whether the specified array contains the specified string.
	 *
	 * @param array The array to search.
	 * @param key The string to look for.
	 *
	 * @return True if the given string is present in the given string array.
	 */
	public static boolean contains(final String[] array, final String key) {
		for(final String str : array) {
			if(str == key) {
				return true;
			}
		}

		return false;
	}
}
