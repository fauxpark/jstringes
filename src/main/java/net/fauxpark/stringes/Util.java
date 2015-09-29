package net.fauxpark.stringes;

import java.util.Arrays;

public class Util {
	/**
	 * Calculates an aggregated hash code from the specified objects.
	 *
	 * @param objects The objects to hash.
	 */
	public static int hashOf(Object... objects) {
		return Arrays.stream(objects).map(o -> o.hashCode()).reduce((hash, next) -> hash * 31 + next).get();
	}

	/**
	 * Counts the number of times a substring occurs in a parent string.
	 *
	 * @param parent The parent string to search.
	 * @param sub The substring to look for.
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
	 * Determines whether the specified character is present in the given character array.
	 *
	 * @param array The array to search.
	 * @param key The character to look for.
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
	 * Determines whether the specified string is present in the given string array.
	 *
	 * @param array The array to search.
	 * @param key The string to look for.
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
