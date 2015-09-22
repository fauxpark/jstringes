package net.fauxpark.stringes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TrimTest {
	@Test
	public void trimBoth() {
		Stringe stre = Stringe.toStringe("   Hello World!   ");
		Stringe streTrimmed = stre.trim();

		assertEquals("Hello World!", streTrimmed.getValue());
	}

	@Test
	public void trimBothWithChars() {
		Stringe stre = Stringe.toStringe("hw   Hello World!   hw");
		Stringe streTrimmed = stre.trim('h', 'w');

		assertEquals("   Hello World!   ", streTrimmed.getValue());
	}

	@Test
	public void trimStart() {
		Stringe stre = Stringe.toStringe("   Hello World!   ");
		Stringe streTrimmed = stre.trimStart();

		assertEquals("Hello World!   ", streTrimmed.getValue());
	}

	@Test
	public void trimStartWithChars() {
		Stringe stre = Stringe.toStringe("hw   Hello World!");
		Stringe streTrimmed = stre.trimStart('h', 'w');

		assertEquals("   Hello World!", streTrimmed.getValue());
	}

	@Test
	public void trimEnd() {
		Stringe stre = Stringe.toStringe("   Hello World!   ");
		Stringe streTrimmed = stre.trimEnd();

		assertEquals("   Hello World!", streTrimmed.getValue());
	}

	@Test
	public void trimEndWithChars() {
		Stringe stre = Stringe.toStringe("Hello World!   hw");
		Stringe streTrimmed = stre.trimEnd('h', 'w');

		assertEquals("Hello World!   ", streTrimmed.getValue());
	}

	@Test
	public void isLeftPadded() {
		Stringe stre = Stringe.toStringe("   Hello World!");

		assertTrue(stre.isLeftPadded());
	}

	@Test
	public void isRightPadded() {
		Stringe stre = Stringe.toStringe("Hello World!   ");

		assertTrue(stre.isRightPadded());
	}
}
