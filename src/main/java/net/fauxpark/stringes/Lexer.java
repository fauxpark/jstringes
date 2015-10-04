package net.fauxpark.stringes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import net.fauxpark.tuples.ThreeTuple;
import net.fauxpark.tuples.Tuples;
import net.fauxpark.tuples.TwoTuple;

/**
 * Represents a set of rules for creating tokens from a Stringe.
 *
 * @param <T> The identifier type to use in tokens created from the context.
 */
public class Lexer<T> {
	/**
	 * The list of punctuation characters.
	 */
	private HashSet<Character> punctuation;

	/**
	 * The list of normal priority constant rules.
	 */
	private List<ThreeTuple<String, T, Boolean>> listNormal;

	/**
	 * The list of high priority constant rules.
	 */
	private List<ThreeTuple<String, T, Boolean>> listHigh;

	/**
	 * The list of regex rules.
	 */
	private List<ThreeTuple<Pattern, RuleMatchValueGenerator<T>, SymbolPriority>> regexes;

	/**
	 * The list of function rules.
	 */
	private List<ThreeTuple<Function<StringeReader, Boolean>, T, SymbolPriority>> functions;

	/**
	 * A rule for the token at the end of the lexer.
	 */
	private TwoTuple<String, T> endToken;

	/**
	 * A rule for any undefined tokens.
	 */
	private TwoTuple<Function<Stringe, Stringe>, T> undefToken;

	/**
	 * The list of token identifiers that should be ignored.
	 */
	private HashSet<T> ignore;

	/**
	 * Indicates whether the rulesets have been sorted.
	 */
	private boolean sorted;

	/**
	 * Constructs a new Lexer.
	 */
	public Lexer() {
		endToken = null;
		undefToken = null;
		punctuation = new HashSet<>();
		listNormal = new ArrayList<>(8);
		listHigh = new ArrayList<>(8);
		regexes = new ArrayList<>(8);
		functions = new ArrayList<>(8);
		ignore = new HashSet<>();
		sorted = false;
	}

	/**
	 * Returns the list of token identifiers that should be ignored.
	 */
	public HashSet<T> getIgnoreRules() {
		return ignore;
	}

	/**
	 * Adds the specified token identifiers to the ignore list.
	 *
	 * @param ids The token identifiers to ignore.
	 */
	@SuppressWarnings("unchecked")
	public void ignore(T... ids) {
		for(T id : ids) {
			ignore.add(id);
		}
	}

	/**
	 * Returns the symbol that represents the specified identifier.
	 * Returns an empty string if the identifier cannot be found.
	 *
	 * @param id The identifier to get the symbol for.
	 */
	public String getSymbolForId(T id) {
		List<ThreeTuple<String, T, Boolean>> listConcat = new ArrayList<>();
		listConcat.addAll(listNormal);
		listConcat.addAll(listHigh);

		for(ThreeTuple<String, T, Boolean> rule : listConcat) {
			if(id.equals(rule.b)) {
				return rule.a;
			}
		}

		return "";
	}

	/**
	 * Indicates whether the symbol is available in one of the constant rule lists.
	 *
	 * @param symbol The symbol to test for.
	 */
	private boolean available(String symbol) {
		List<ThreeTuple<String, T, Boolean>> listConcat = new ArrayList<>();
		listConcat.addAll(listNormal);
		listConcat.addAll(listHigh);

		for(ThreeTuple<String, T, Boolean> rule : listConcat) {
			if(symbol.equals(rule.a)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Defines a lexer rule that returns a token when the end of the input is reached.
	 *
	 * @param id The token identifier to associate with the rule.
	 * @throws UnsupportedOperationException If called after after the context is used to create tokens.
	 */
	public void addEndToken(T id) throws UnsupportedOperationException {
		if(sorted) {
			throw new UnsupportedOperationException("Cannot add more rules after they have been used");
		}

		endToken = Tuples.create("EOF", id);
	}

	/**
	 * Defines a lexer rule that returns a token when the end of the input is reached.
	 *
	 * @param id The token identifier to associate with the rule.
	 * @param symbol The symbol to assign to the end token.
	 * @throws IllegalArgumentException If the supplied identifer is null.
	 * @throws UnsupportedOperationException If called after the context is used to create tokens.
	 */
	public void addEndToken(T id, String symbol) throws IllegalArgumentException, UnsupportedOperationException {
		if(id == null) {
			throw new IllegalArgumentException("End token identifier cannot be null");
		}

		if(sorted) {
			throw new UnsupportedOperationException("Cannot add more rules after they have been used");
		}

		endToken = Tuples.create(symbol, id);
	}

	/**
	 * Defines a lexer rule that captures unrecognized characters as a token.
	 *
	 * @param id The token identifier to associate with the rule.
	 * @param func A function that processes the captured Stringe.
	 * @throws UnsupportedOperationException If called after the context is used to create tokens.
	 */
	public void addUndefinedCaptureRule(T id, Function<Stringe, Stringe> func) throws UnsupportedOperationException {
		if(sorted) {
			throw new UnsupportedOperationException("Cannot add more rules after they have been used");
		}

		undefToken = Tuples.create(func, id);
	}

	/**
	 * Defines a lexer rule that returns a token when the specified string is found.
	 *
	 * @param symbol The symbol to test for.
	 * @param id The token identifier to associate with the symbol.
	 * @throws UnsupportedOperationException If called after the context is used to create tokens, or a rule with the symbol already exists.
	 * @throws IllegalArgumentException If the symbol is null or empty.
	 */
	public void add(String symbol, T id) throws UnsupportedOperationException, IllegalArgumentException {
		add(symbol, id, SymbolPriority.LAST);
	}

	/**
	 * Defines a lexer rule that returns a token when the specified string is found.
	 *
	 * @param symbol The symbol to test for.
	 * @param id The token identifier to associate with the symbol.
	 * @param priority Whether the symbol should be tested before any regex rules.
	 * @throws UnsupportedOperationException If called after the context is used to create tokens, or a rule with the symbol already exists.
	 * @throws IllegalArgumentException If the symbol is null or empty.
	 */
	public void add(String symbol, T id, SymbolPriority priority) throws UnsupportedOperationException, IllegalArgumentException {
		if(sorted) {
			throw new UnsupportedOperationException("Cannot add more rules after they have been used");
		}

		if(symbol == null || symbol.isEmpty()) {
			throw new IllegalArgumentException("Symbol cannot be null or empty");
		}

		if(!available(symbol)) {
			throw new UnsupportedOperationException("A rule with the symbol '" + symbol + "' already exists");
		}

		(priority == SymbolPriority.FIRST ? listHigh : listNormal).add(Tuples.create(symbol, id, true));
		punctuation.add(symbol.charAt(0));
	}

	/**
	 * Defines a lexer rule that returns a token when any of the specified strings are found.
	 *
	 * @param symbols The symbols to test for.
	 * @param id The token identifier to associate with the symbols.
	 * @throws UnsupportedOperationException If called after the context is used to create tokens, or a rule with the symbol already exists.
	 * @throws IllegalArgumentException If the symbols array or any of its members are null or empty.
	 */
	public void add(String[] symbols, T id) throws UnsupportedOperationException, IllegalArgumentException {
		add(symbols, id, SymbolPriority.LAST);
	}

	/**
	 * Defines a lexer rule that returns a token when any of the specified strings are found.
	 *
	 * @param symbols The symbols to test for.
	 * @param id The token identifier to associate with the symbols.
	 * @param priority Whether the symbol should be tested before any regex rules.
	 * @throws UnsupportedOperationException If called after the context is used to create tokens, or a rule with the symbol already exists.
	 * @throws IllegalArgumentException If the symbols array or any of its members are null or empty.
	 */
	public void add(String[] symbols, T id, SymbolPriority priority) throws UnsupportedOperationException, IllegalArgumentException {
		if(sorted) {
			throw new UnsupportedOperationException("Cannot add more rules after they have been used");
		}

		if(symbols == null || symbols.length == 0) {
			throw new IllegalArgumentException("Symbols array cannot be null or empty");
		}

		for(String s : symbols) {
			if(s == null || s.isEmpty()) {
				throw new IllegalArgumentException("One or more symbols in the provided array are null or empty");
			}

			if(!available(s)) {
				throw new UnsupportedOperationException("A rule with the symbol '" + s + "' already exists");
			}

			(priority == SymbolPriority.FIRST ? listHigh : listNormal).add(Tuples.create(s, id, true));
			punctuation.add(s.charAt(0));
		}
	}

	/**
	 * Defines a lexer rule that returns a token when the specified string is found.
	 *
	 * @param symbol The symbol to test for.
	 * @param id The token identifier to associate with the symbol.
	 * @param ignoreCase Whether the rule should ignore capitalization.
	 * @throws UnsupportedOperationException If called after the context is used to create tokens, or a rule with the symbol already exists.
	 * @throws IllegalArgumentException If the symbol is null or empty.
	 */
	public void add(String symbol, T id, boolean ignoreCase) throws UnsupportedOperationException, IllegalArgumentException {
		add(symbol, id, ignoreCase, SymbolPriority.LAST);
	}

	/**
	 * Defines a lexer rule that returns a token when the specified string is found.
	 *
	 * @param symbol The symbol to test for.
	 * @param id The token identifier to associate with the symbol.
	 * @param ignoreCase Whether the rule should ignore capitalization.
	 * @param priority Whether the symbol should be tested before any regex rules.
	 * @throws UnsupportedOperationException If called after the context is used to create tokens, or a rule with the symbol already exists.
	 * @throws IllegalArgumentException If the symbol is null or empty.
	 */
	public void add(String symbol, T id, boolean ignoreCase, SymbolPriority priority) throws UnsupportedOperationException, IllegalArgumentException {
		if(sorted) {
			throw new UnsupportedOperationException("Cannot add more rules after they have been used");
		}

		if(symbol == null || symbol.isEmpty()) {
			throw new IllegalArgumentException("Symbol cannot be null or empty");
		}

		if(!available(symbol)) {
			throw new UnsupportedOperationException("A rule with the symbol '" + symbol + "' already exists");
		}

		(priority == SymbolPriority.FIRST ? listHigh : listNormal).add(Tuples.create(symbol, id, ignoreCase));
		punctuation.add(symbol.charAt(0));
	}

	/**
	 * Defines a lexer rule that returns a token when any of the specified strings are found.
	 *
	 * @param symbols The symbols to test for.
	 * @param id The token identifier to associate with the symbols.
	 * @param ignoreCase Whether the rule should ignore capitalization.
	 * @throws UnsupportedOperationException If called after the context is used to create tokens, or a rule with the symbol already exists.
	 * @throws IllegalArgumentException If the symbols array or any of its members are null or empty.
	 */
	public void add(String[] symbols, T id, boolean ignoreCase) throws UnsupportedOperationException, IllegalArgumentException {
		add(symbols, id, ignoreCase, SymbolPriority.LAST);
	}

	/**
	 * Defines a lexer rule that returns a token when any of the specified strings are found.
	 *
	 * @param symbols The symbols to test for.
	 * @param id The token identifier to associate with the symbols.
	 * @param ignoreCase Whether the rule should ignore capitalization.
	 * @param priority Whether the symbol should be tested before any regex rules.
	 * @throws UnsupportedOperationException If called after the context is used to create tokens, or a rule with the symbol already exists.
	 * @throws IllegalArgumentException If the symbols array or any of its members are null or empty.
	 */
	public void add(String[] symbols, T id, boolean ignoreCase, SymbolPriority priority) throws UnsupportedOperationException, IllegalArgumentException {
		if(sorted) {
			throw new UnsupportedOperationException("Cannot add more rules after they have been used");
		}

		if(symbols == null || symbols.length == 0) {
			throw new IllegalArgumentException("Symbols array cannot be null or empty");
		}

		for(String s : symbols) {
			if(s == null || s.isEmpty()) {
				throw new IllegalArgumentException("One or more symbols in the provided array are null or empty");
			}

			if(!available(s)) {
				throw new UnsupportedOperationException("A rule with the symbol '" + s + "' already exists");
			}

			(priority == SymbolPriority.FIRST ? listHigh : listNormal).add(Tuples.create(s, id, ignoreCase));
			punctuation.add(s.charAt(0));
		}
	}

	/**
	 * Defines a lexer rule that returns a token when the specified regular expression finds a match.
	 *
	 * @param regex The regex to test for.
	 * @param id The token identifier to associate with the regex.
	 * @throws UnsupportedOperationException If called after the context is used to create tokens, or a rule with the same pattern already exists.
	 * @throws IllegalArgumentException If the regex is null.
	 */
	public void add(Pattern regex, T id) throws UnsupportedOperationException, IllegalArgumentException {
		add(regex, id, SymbolPriority.FIRST);
	}

	/**
	 * Defines a lexer rule that returns a token when the specified regular expression finds a match.
	 *
	 * @param regex The regex to test for.
	 * @param id The token identifier to associate with the regex.
	 * @param priority The priority of the token. Higher values are checked first.
	 * @throws UnsupportedOperationException If called after the context is used to create tokens, or a rule with the same pattern already exists.
	 * @throws IllegalArgumentException If the regex is null.
	 */
	public void add(Pattern regex, T id, SymbolPriority priority) throws UnsupportedOperationException, IllegalArgumentException {
		if(sorted) {
			throw new UnsupportedOperationException("Cannot add more rules after they have been used");
		}

		if(regex == null) {
			throw new IllegalArgumentException("Regex cannot be null");
		}

		for(ThreeTuple<Pattern, RuleMatchValueGenerator<T>, SymbolPriority> r : regexes) {
			if(regex.equals(r.a)) {
				throw new UnsupportedOperationException("A rule with this pattern already exists");
			}
		}

		regexes.add(Tuples.create(regex, new RuleMatchValueGenerator<T>(id), priority));
	}

	/**
	 * Defines a lexer rule that returns a token when the specified regular expression finds a match.
	 *
	 * @param regex The regex to test for.
	 * @param generator A function that generates a token identifier from the match.
	 * @throws UnsupportedOperationException If called after the context is used to create tokens, or a rule with the same pattern already exists.
	 * @throws IllegalArgumentException If either the regex or generator are null.
	 */
	public void add(Pattern regex, Function<MatchResult, T> generator) throws UnsupportedOperationException, IllegalArgumentException {
		add(regex, generator, SymbolPriority.FIRST);
	}

	/**
	 * Defines a lexer rule that returns a token when the specified regular expression finds a match.
	 *
	 * @param regex The regex to test for.
	 * @param generator A function that generates a token identifier from the match.
	 * @param priority The priority of the rule. Higher values are checked first.
	 * @throws UnsupportedOperationException If called after the context is used to create tokens, or a rule with the same pattern already exists.
	 * @throws IllegalArgumentException If either the regex or generator are null.
	 */
	public void add(Pattern regex, Function<MatchResult, T> generator, SymbolPriority priority) throws UnsupportedOperationException, IllegalArgumentException {
		if(sorted) {
			throw new UnsupportedOperationException("Cannot add more rules after they have been used");
		}

		if(regex == null) {
			throw new IllegalArgumentException("Regex cannot be null");
		}

		if(generator == null) {
			throw new IllegalArgumentException("Generator cannot be null");
		}

		for(ThreeTuple<Pattern, RuleMatchValueGenerator<T>, SymbolPriority> r : regexes) {
			if(regex.equals(r.a)) {
				throw new UnsupportedOperationException("A rule with this pattern already exists");
			}
		}

		regexes.add(Tuples.create(regex, new RuleMatchValueGenerator<T>(generator), priority));
	}

	/**
	 * Defines a lexer rule that returns a token when the specified function returns true.
	 *
	 * @param func The function to read the token with.
	 * @param id The token identifier to associate with the function.
	 * @throws UnsupportedOperationException If called after the context is used to create tokens.
	 * @throws IllegalArgumentException If the function is null.
	 */
	public void add(Function<StringeReader, Boolean> func, T id) throws UnsupportedOperationException, IllegalArgumentException {
		add(func, id, SymbolPriority.FIRST);
	}

	/**
	 * Defines a lexer rule that returns a token when the specified function returns true.
	 *
	 * @param func The function to read the token with.
	 * @param id The token identifier to associate with the function.
	 * @param priority The priority of the rule. Higher values are checked first.
	 * @throws UnsupportedOperationException If called after the context is used to create tokens.
	 * @throws IllegalArgumentException If the function is null.
	 */
	public void add(Function<StringeReader, Boolean> func, T id, SymbolPriority priority) throws UnsupportedOperationException, IllegalArgumentException {
		if(sorted) {
			throw new UnsupportedOperationException("Cannot add more rules after they have been used");
		}

		if(func == null) {
			throw new IllegalArgumentException("Function cannot be null");
		}

		functions.add(Tuples.create(func, id, priority));
	}

	/**
	 * Tokenizes the input string and enumerates the resulting tokens.
	 *
	 * @param str The string to tokenize.
	 */
	public Iterable<Token<T>> tokenize(String str) {
		return tokenize(new Stringe(str));
	}

	/**
	 * Tokenizes the input Stringe and enumerates the resulting tokens.
	 *
	 * @param stre The Stringe to tokenize.
	 */
	public Iterable<Token<T>> tokenize(Stringe stre) {
		StringeReader reader = new StringeReader(stre);
		List<Token<T>> iterable = new ArrayList<>();

		while(!reader.atEndOfStringe()) {
			iterable.add(reader.readToken(this, (stringe, t) -> new Token<T>(t, stringe)));
		}

		return iterable;
	}

	/**
	 * Tokenizes the input Stringe and enumerates the resulting tokens using the specified token emitter.
	 *
	 * @param <U> The type of token to be created.
	 * @param stre The Stringe to tokenize.
	 * @param tokenEmitter The function that will create the tokens.
	 * @throws IllegalArgumentException If the token emitter is null.
	 */
	public <U> Iterable<U> tokenize(Stringe stre, BiFunction<Stringe, T, U> tokenEmitter) throws IllegalArgumentException {
		if(tokenEmitter == null) {
			throw new IllegalArgumentException("Token emitter cannot be null");
		}

		StringeReader reader = new StringeReader(stre);
		List<U> iterable = new ArrayList<>();

		while(!reader.atEndOfStringe()) {
			iterable.add(reader.readToken(this, tokenEmitter));
		}

		return iterable;
	}

	/**
	 * Determines if the lexer contains the specified punctuation mark.
	 *
	 * @param c The character to look for.
	 */
	boolean hasPunctuation(int c) {
		return c != -1 && punctuation.contains((char) c);
	}

	/**
	 * Determines if the lexer contains the specified punctuation mark.
	 *
	 * @param c The character to look for.
	 */
	boolean hasPunctuation(char c) {
		return punctuation.contains(c);
	}

	/**
	 * Sorts the rule lists in descending order.
	 * The normal and high priority constant rule lists are sorted by the length of their string values.
	 * The regular expression and function lists are sorted by priority.
	 */
	private void sort() {
		if(sorted) {
			return;
		}

		Comparator<ThreeTuple<String, T, Boolean>> cmp = (t1, t2) -> Integer.compare(t1.a.length(), t2.a.length());
		Collections.sort(listNormal, cmp);
		Collections.sort(listHigh, cmp);
		Collections.sort(regexes, (t1, t2) -> Integer.compare(t1.c.ordinal(), t2.c.ordinal()));
		Collections.sort(functions, (t1, t2) -> Integer.compare(t1.c.ordinal(), t2.c.ordinal()));

		sorted = true;
	}

	/**
	 * Returns the rule for undefined symbols.
	 */
	TwoTuple<Function<Stringe, Stringe>, T> getUndefinedCaptureRule() {
		return undefToken;
	}

	/**
	 * Returns the rule for the end token symbol.
	 */
	TwoTuple<String, T> getEndToken() {
		return endToken;
	}

	/**
	 * Returns the list of normal priority symbol rules.
	 */
	List<ThreeTuple<String, T, Boolean>> getNormalSymbols() {
		sort();

		return listNormal;
	}

	/**
	 * Returns the list of high priority symbol rules.
	 */
	List<ThreeTuple<String, T, Boolean>> getHighSymbols() {
		sort();

		return listHigh;
	}

	/**
	 * Returns the list of regex rules.
	 */
	List<ThreeTuple<Pattern, RuleMatchValueGenerator<T>, SymbolPriority>> getRegexes() {
		sort();

		return regexes;
	}

	/**
	 * Returns the list of function rules.
	 */
	List<ThreeTuple<Function<StringeReader, Boolean>, T, SymbolPriority>> getFunctions() {
		return functions;
	}

	/**
	 * Used to manipulate the order in which symbol (non-regex) rules are tested.
	 */
	public static enum SymbolPriority {
		/**
		 * Test symbol after testing regex rules. This is the default value for all symbol rules.
		 */
		LAST,
		/**
		 * Test symbol before testing any regex rules.
		 */
		FIRST;
	}

	/**
	 * Generates token identifiers for a rule from either a constant value, or a generator function processing a MatchResult.
	 *
	 * @param <U> The type of token identifier to use.
	 */
	class RuleMatchValueGenerator<U> {
		/**
		 * A token identifier.
		 */
		private U id;

		/**
		 * A function to generate token identifiers.
		 */
		private Function<MatchResult, U> func;

		/**
		 * Constructs a new RuleMatchValueGenerator with a token identifier.
		 *
		 * @param id The token identifier to use.
		 */
		public RuleMatchValueGenerator(U id) {
			this.id = id;
			this.func = null;
		}

		/**
		 * Constructs a new RuleMatchValueGenerator with a function that generates token identifiers.
		 *
		 * @param generator The generator function to use.
		 */
		public RuleMatchValueGenerator(Function<MatchResult, U> generator) {
			func = generator;
		}

		/**
		 * Returns the token identifier.
		 * If there is no constant value, returns the result of the function against the supplied MatchResult.
		 *
		 * @param m The regex match result to apply the generator to.
		 */
		public U getId(MatchResult m) {
			return func == null ? id : func.apply(m);
		}
	}
}
