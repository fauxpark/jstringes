package net.fauxpark.stringes;

public class Util {
	public static int hashOf(Object... objects) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Not yet implemented");
	}

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

	public static boolean contains(final char[] array, final char key) {
		for(final char c : array) {
			if(c == key) {
				return true;
			}
		}

		return false;
	}

	public static boolean contains(final String[] array, final String key) {
		for(final String str : array) {
			if(str == key) {
				return true;
			}
		}

		return false;
	}
}
