package net.fauxpark.stringes;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.fauxpark.tuples.ThreeTuple;
import net.fauxpark.tuples.TwoTuple;

/**
 * Represents a reader that can read data from a Stringe.
 */
public class StringeReader {
	private final Stringe stringe;

	private int pos;

	/**
	 * Constructs a new StringeReader using the specified string as input.
	 *
	 * @param str The string to use as input. This will be converted to a root-level Stringe.
	 */
	public StringeReader(String str) {
		stringe = new Stringe(str);
		pos = 0;
	}

	/**
	 * Constructs a new StringeReader using the specified Stringe as input.
	 *
	 * @param stre The Stringe to use as input.
	 */
	public StringeReader(Stringe stre) {
		stringe = stre;
		pos = 0;
	}

	/**
	 * Indicates whether the reader position is at the end of the input string.
	 */
	public boolean atEndOfStringe() {
		return pos >= stringe.length();
	}

	/**
	 * Reads the next Chare from the input and consumes it.
	 */
	public Chare readChare() {
		return stringe.chareAt(pos++);
	}

	/**
	 * Reads the next Chare in the input, but does not consume it.
	 */
	public Chare peekChare() {
		return atEndOfStringe() ? null : stringe.chareAt(pos);
	}

	/**
	 * Reads the next character in the input, but does not consume it.
	 * Returns -1 if no more characters can be read.
	 */
	public int peekChar() {
		return atEndOfStringe() ? -1 : stringe.chareAt(pos).getCharacter();
	}

	/**
	 * Reads a Stringe from the input and advances the position by the number of characters read.
	 *
	 * @param length The number of characters to read.
	 */
	public Stringe readStringe(int length) {
		int p = pos;
		pos += length;

		return stringe.substringe(p, length);
	}

	/**
	 * Indicates whether the specified character occurs at the reader's current position.
	 * If a match is found, the reader consumes it.
	 *
	 * @param c The character to test for.
	 */
	public boolean eat(char c) {
		if(atEndOfStringe() || peekChar() != c) {
			return false;
		}

		pos++;

		return true;
	}

	/**
	 * Indicates whether the specified character occurs at the reader's current position.
	 * If a match is found, the reader consumes it and any following matches.
	 *
	 * @param c The character to test for.
	 */
	public boolean eatAll(char c) {
		if(peekChar() != c) {
			return false;
		}

		do {
			pos++;
		} while(peekChar() == c);

		return true;
	}

	/**
	 * Indicates whether the specified predicate matches the character at the reader's current position.
	 * While the function returns true, the reader consumes it and any following matching characters.
	 *
	 * @param predicate The function to read the character with.
	 */
	public boolean eatWhile(Function<Character, Boolean> predicate) {
		if(atEndOfStringe() || !predicate.apply((char) peekChar())) {
			return false;
		}

		do {
			pos++;
		} while(!atEndOfStringe() && predicate.apply((char) peekChar()));

		return true;
	}

	/**
	 * Indicates whether the specified predicate matches the Chare at the reader's current position.
	 * While the function returns true, the reader consumes it and any following matching Chares.
	 *
	 * @param predicate The function to read the Chare with.
	 */
	public boolean eatChareWhile(Function<Chare, Boolean> predicate) {
		if(atEndOfStringe() || !predicate.apply(peekChare())) {
			return false;
		}

		do {
			pos++;
		} while(!atEndOfStringe() && predicate.apply(peekChare()));

		return true;
	}

	/**
	 * Indicates whether the specified string occurs at the reader's current position.
	 * If a match is found, the reader consumes it.
	 *
	 * @param str The string to test for.
	 */
	public boolean eat(String str) {
		if(str == null || str.isEmpty()) {
			return false;
		}

		if(stringe.indexOf(str, pos) != pos) {
			return false;
		}

		pos += str.length();

		return true;
	}

	/**
	 * Indicates whether the specified string occurs at the reader's current position.
	 * If a match is found, the reader consumes it and any following matching strings.
	 *
	 * @param str The string to test for.
	 */
	public boolean eatAll(String str) {
		if(str == null || str.isEmpty()) {
			return false;
		}

		if(stringe.indexOf(str, pos) != pos) {
			return false;
		}

		do {
			pos += str.length();
		} while(stringe.indexOf(str, pos) == pos);

		return true;
	}

	/**
	 * Indicates whether the specified regular expression matches the input at the reader's current position.
	 * If a match is found, the reader consumes it.
	 *
	 * @param regex The regular expression to test for.
	 * @throws IllegalArgumentException If the regex is null.
	 */
	public boolean eat(Pattern regex) throws IllegalArgumentException {
		if(regex == null) {
			throw new IllegalArgumentException("Regex cannot be null");
		}

		Matcher matcher = regex.matcher(stringe.getValue());
		boolean success = matcher.find(pos);

		if(!success || matcher.start() != pos) {
			return false;
		}

		pos = matcher.end();

		return true;
	}

	/**
	 * Indicates whether the specified regular expression matches the input at the reader's current position.
	 * If a match is found, the reader consumes it and outputs the result.
	 *
	 * @param regex The regular expression to test for.
	 * @param result The Stringe to output the result to.
	 * @throws IllegalArgumentException If the regex is null.
	 */
	public boolean eat(Pattern regex, Stringe result) throws IllegalArgumentException {
		if(regex == null) {
			throw new IllegalArgumentException("Regex cannot be null");
		}

		result = null;
		Matcher matcher = regex.matcher(stringe.getValue());
		boolean success = matcher.find(pos);

		if(!success || matcher.start() != pos) {
			return false;
		}

		result = stringe.substringe(pos, matcher.end() - pos);
		pos = matcher.end();

		return true;
	}

	/**
	 * Indicates whether the specified character occurs at the reader's current position.
	 *
	 * @param c The character to test for.
	 */
	public boolean isNext(char c) {
		return peekChar() == c;
	}

	/**
	 * Indicates whether the specified string occurs at the reader's current position.
	 *
	 * @param str The string to test for.
	 */
	public boolean isNext(String str) {
		if(str == null || str.isEmpty()) {
			return false;
		}

		return stringe.indexOf(str, pos) == pos;
	}

	/**
	 * Indicates whether the specified regular expression matches the input at the reader's current position.
	 *
	 * @param regex The regular expression to test for.
	 * @throws IllegalArgumentException If the regex is null.
	 */
	public boolean isNext(Pattern regex) throws IllegalArgumentException {
		if(regex == null) {
			throw new IllegalArgumentException("Regex cannot be null");
		}

		Matcher matcher = regex.matcher(stringe.getValue());
		boolean success = matcher.find(pos);

		return success && matcher.start() == pos;
	}

	/**
	 * Indicates whether the specified regular expression matches the input at the reader's current position, and outputs the result.
	 *
	 * @param regex The regular expression to test for.
	 * @param result The Stringe to output the result to.
	 * @throws IllegalArgumentException If the regex is null.
	 */
	public boolean isNext(Pattern regex, Stringe result) throws IllegalArgumentException {
		if(regex == null) {
			throw new IllegalArgumentException("Regex cannot be null");
		}

		result = null;
		Matcher matcher = regex.matcher(stringe.getValue());
		boolean success = matcher.find(pos);

		if(!success || matcher.start() != pos) {
			return false;
		}

		result = stringe.substringe(pos, matcher.end() - pos);

		return true;
	}

	/**
	 * Advances the reader's position past any immediate white space.
	 */
	public boolean skipWhitespace() {
		int oldPos = pos;

		while(!atEndOfStringe() && Character.isWhitespace(stringe.charAt(pos))) {
			pos++;
		}

		return pos > oldPos;
	}

	/**
	 * Reads the next token from the current position, then advances the position past it.
	 *
	 * @param <T> The token identifier type to use.
	 * @param <U> The token class to use.
	 * @param rules The lexer to use.
	 * @param tokenEmitter The callback that creates the returned token.
	 * @throws IllegalArgumentException If the token emitter is null.
	 * @throws IllegalStateException If the end of the Stringe is reached earlier than expected.
	 * @throws UnsupportedOperationException If there is no undefined capture rule and an invalid token is found.
	 */
	public <T, U> U readToken(Lexer<T> rules, BiFunction<Stringe, T, U> tokenEmitter) throws IllegalArgumentException, IllegalStateException, UnsupportedOperationException {
		if(tokenEmitter == null) {
			throw new IllegalArgumentException("Token emitter cannot be null");
		}

		// Token read loop
		readStart:
		while(true) {
			if(atEndOfStringe()) {
				TwoTuple<String, T> endToken = rules.getEndToken();

				if(endToken != null && !rules.getIgnoreRules().contains(endToken.b)) {
					return tokenEmitter.apply(new Stringe(endToken.a), endToken.b);
				}

				throw new IllegalStateException("Unexpected end of input");
			}

			// Indicates if undefined tokens should be created
			boolean captureUndef = rules.getUndefinedCaptureRule() != null;
			// Tracks the beginning of the undefined token content
			int u = pos;

			do {
				// If we've reached the end, return undefined token, if present.
				if(atEndOfStringe() && captureUndef && u < pos) {
					if(rules.getIgnoreRules().contains(rules.getUndefinedCaptureRule().b)) {
						continue readStart;
					}

					return tokenEmitter.apply(stringe.slice(u, pos), rules.getUndefinedCaptureRule().b);
				}

				if(rules.hasPunctuation(peekChar())) {
					// Check high priority symbol rules
					for(TwoTuple<String, T> t : rules.getHighSymbols()) {
						if(isNext(t.a)) {
							// Return undefined token if present
							if(captureUndef && u < pos) {
								if(rules.getIgnoreRules().contains(rules.getUndefinedCaptureRule().b)) {
									continue readStart;
								}

								return tokenEmitter.apply(rules.getUndefinedCaptureRule().a.apply(stringe.slice(u, pos)), rules.getUndefinedCaptureRule().b);
							}

							// Return symbol token
							Stringe c = stringe.substringe(pos, t.a.length());
							pos += t.a.length();

							if(rules.getIgnoreRules().contains(t.b)) {
								continue readStart;
							}

							return tokenEmitter.apply(c, t.b);
						}
					}
				}

				final String tokenGroupName = "value";

				// Check regex rules
				if(!rules.getRegexes().isEmpty()) {
					Matcher longestMatch = null;
					T id = null;

					// Find the longest match, if any.
					for(ThreeTuple<Pattern, Lexer<T>.RuleMatchValueGenerator<T>, Lexer.SymbolPriority> re : rules.getRegexes()) {
						Matcher matcher = re.a.matcher(stringe.getValue());

						if(matcher.find(pos)) {
							if(matcher.start() == pos && (longestMatch == null || matcher.end() - matcher.start() > longestMatch.end() - longestMatch.start())) {
								longestMatch = matcher;
								id = re.b.getId(matcher.toMatchResult());
							}
						}
					}

					// If there was a match, generate a token.
					if(longestMatch != null) {
						// Return undefined token if present
						if(captureUndef && u < pos) {
							if(rules.getIgnoreRules().contains(rules.getUndefinedCaptureRule().b)) {
								continue readStart;
							}

							return tokenEmitter.apply(stringe.slice(u, pos), rules.getUndefinedCaptureRule().b);
						}

						pos += longestMatch.end() - longestMatch.start();
						String longestMatchGroup = null;

						// Return longest match, narrow down to <value> group if available.
						try {
							longestMatchGroup = longestMatch.group(tokenGroupName);
						} catch(IllegalArgumentException e) {}

						if(longestMatchGroup != null) {
							if(rules.getIgnoreRules().contains(id)) {
								continue readStart;
							}

							return tokenEmitter.apply(stringe.substringe(longestMatch.start(tokenGroupName), longestMatch.end(tokenGroupName) - longestMatch.start(tokenGroupName)), id);
						}

						if(rules.getIgnoreRules().contains(id)) {
							continue readStart;
						}

						return tokenEmitter.apply(stringe.substringe(longestMatch.start(), longestMatch.end() - longestMatch.start()), id);
					}
				}

				if(!rules.getFunctions().isEmpty()) {
					int origPos = pos;

					for(ThreeTuple<Function<StringeReader, Boolean>, T, Lexer.SymbolPriority> fn : rules.getFunctions()) {
						if(fn.a.apply(this)) {
							if(rules.getIgnoreRules().contains(fn.b)) {
								continue readStart;
							}

							return tokenEmitter.apply(stringe.slice(origPos, pos), fn.b);
						}

						// Reset for next function
						pos = origPos;
					}
				}

				if(rules.hasPunctuation(peekChar())) {
					// Check normal priority symbol rules
					for(TwoTuple<String, T> t : rules.getNormalSymbols()) {
						if(isNext(t.a)) {
							// Return undefined token if present
							if(captureUndef && u < pos) {
								if(rules.getIgnoreRules().contains(rules.getUndefinedCaptureRule().b)) {
									continue readStart;
								}

								return tokenEmitter.apply(rules.getUndefinedCaptureRule().a.apply(stringe.slice(u, pos)), rules.getUndefinedCaptureRule().b);
							}

							// Return symbol token
							Stringe c = stringe.substringe(pos, t.a.length());
							pos += t.a.length();

							if(rules.getIgnoreRules().contains(t.b)) {
								continue readStart;
							}

							return tokenEmitter.apply(c, t.b);
						}
					}
				}

				pos++;

				if(!captureUndef) {
					Stringe bad = stringe.slice(u, pos);

					throw new UnsupportedOperationException("(Ln " + bad.getLine() + ", Col " + bad.getColumn() + ") Invalid token '" + bad + "'");
				}
			} while(captureUndef);
		}
	}

	/**
	 * Returns the current zero-based position of the reader.
	 */
	public int getPosition() {
		return pos;
	}

	/**
	 * Sets the position of the reader.
	 *
	 * @param newPos The new position of the reader.
	 * @throws StringIndexOutOfBoundsException If the new position is negative or greater than the stringe's length.
	 */
	public void setPosition(int newPos) throws StringIndexOutOfBoundsException {
		if(newPos < 0 || newPos >= stringe.length()) {
			throw new StringIndexOutOfBoundsException(newPos);
		}

		pos = newPos;
	}

	/**
	 * Returns the total length, in characters, of the Stringe being read.
	 */
	public int getLength() {
		return stringe.length();
	}
}
