package net.fauxpark.lexerdemo;

import java.util.regex.Pattern;

import net.fauxpark.stringes.Lexer;
import net.fauxpark.stringes.Token;

public class LexerDemo {
	private static Lexer<M> lexer = new Lexer<>();

	private static enum M {
		Plus,
		Minus,
		Asterisk,
		Slash,
		Caret,
		LeftParen,
		RightParen,
		Number,
		Whitespace;
	}

	public static void main(String[] args) {
		lexer.add("+", M.Plus);
		lexer.add("-", M.Minus);
		lexer.add("*", M.Asterisk);
		lexer.add("/", M.Slash);
		lexer.add("^", M.Caret);
		lexer.add("(", M.LeftParen);
		lexer.add(")", M.RightParen);
		lexer.add(reader -> {
			reader.eat('-');

			if(!reader.eatWhile(c -> Character.isDigit(c))) {
				return false;
			}

			return !reader.eat('.') || reader.eatWhile(c -> Character.isDigit(c));
		}, M.Number);
		lexer.add(Pattern.compile("\\s+"), M.Whitespace);
		lexer.ignore(M.Whitespace);

		System.out.println("Stringes Lexer Example");
		System.out.println("======================\n");

		String origText = "20 * 3.14 / (5 + 11) ^ 2";

		System.out.println("ORIGINAL:");
		System.out.println(origText + "\n");
		System.out.println("TOKENIZED:");

		for(Token<M> token : lexer.tokenize(origText)) {
			System.out.println(token);
		}
	}
}
