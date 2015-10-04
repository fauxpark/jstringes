package net.fauxpark.stringes;

import static org.junit.Assert.*;

import org.junit.Test;

public class IndexOfTest {
	@Test
	public void indexOfString() {
		Stringe stre = new Stringe("Hello world");

		assertEquals(6, stre.indexOf("world"));
		assertEquals(6, stre.indexOf("WoRlD", 0, true));
	}

	@Test
	public void indexOfStringTotal() {
		Stringe parent = new Stringe("Hello world");
		Stringe sub = parent.substringe(1, 8);

		assertEquals(6, sub.indexOfTotal("wor"));
		assertEquals(6, sub.indexOfTotal("WoR", 0, true));
	}

	@Test
	public void indexOfChar() {
		Stringe stre = new Stringe("Hello world");

		assertEquals(6, stre.indexOf('w'));
		assertEquals(6, stre.indexOf('W', 0, true));
	}

	@Test
	public void indexOfCharTotal() {
		Stringe parent = new Stringe("Hello world");
		Stringe sub = parent.substringe(1, 8);

		assertEquals(6, sub.indexOfTotal('w'));
		assertEquals(6, sub.indexOfTotal('W', 0, true));
	}
}
