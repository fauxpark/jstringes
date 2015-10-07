package net.fauxpark.stringes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BasesTest {
	@Test
	public void nonSpacingMarks() {
		Stringe stre = new Stringe("Tōkyō-to");
		Stringe sub = stre.substringe(8, 2);

		assertEquals(7, sub.getColumn());
	}
}
