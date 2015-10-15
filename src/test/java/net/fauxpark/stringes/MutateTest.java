package net.fauxpark.stringes;

import static org.junit.Assert.*;

import java.util.regex.Pattern;

import org.junit.Test;

public class MutateTest {
	@Test
	public void stringeMutation() {
		Stringe stre = new Stringe("Hello world! What's up?");
		Stringe hello = new Stringe("");
		Stringe world = new Stringe("");

		StringeReader reader = new StringeReader(stre);

		assertTrue(reader.eat(Pattern.compile("Hello"), hello));
		assertEquals("Hello", hello.getValue());
		reader.eat(' ');
		assertTrue(reader.isNext(Pattern.compile("[Ww]orld\\S"), world));
		assertEquals("world!", world.getValue());
	}
}
