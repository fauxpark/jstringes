package net.fauxpark.stringes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class SubstringesTest {
	@Test
	public void twoLineSplit() {
		List<Stringe> splitList = Stringe.toStringe("Hello\nWorld").split('\n');
		Stringe[] split = splitList.toArray(new Stringe[splitList.size()]);

		assertTrue(split.length == 2);
		assertEquals("Hello", split[0].getValue());
		assertEquals("World", split[1].getValue());

		splitList = Stringe.toStringe("A\n").split('\n');
		split = splitList.toArray(new Stringe[splitList.size()]);

		assertTrue(split.length == 2);
		assertEquals("A", split[0].getValue());
		assertEquals("", split[1].getValue());

		splitList = Stringe.toStringe("\nB").split('\n');
		split = splitList.toArray(new Stringe[splitList.size()]);

		assertTrue(split.length == 2);
		assertEquals("", split[0].getValue());
		assertEquals("B", split[1].getValue());
	}

	@Test
	public void rangeText() {
		Stringe streParent = Stringe.toStringe("The quick brown fox jumps over the lazy dog");
		Stringe streA = streParent.substringe(0, 3);
		Stringe streB = streParent.substringe(16, 3);
		Stringe streRange = Stringe.range(streA, streB);

		assertEquals("The", streA.getValue());
		assertEquals("fox", streB.getValue());
		assertEquals("The quick brown fox", streRange.getValue());
	}

	@Test
	public void betweenText() {
		Stringe streParent = Stringe.toStringe("Get (the words) between the parentheses");
		Stringe streA = streParent.substringe(4, 1);
		Stringe streB = streParent.substringe(14, 1);
		Stringe streBetween = Stringe.between(streA, streB);

		assertEquals("(", streA.getValue());
		assertEquals(")", streB.getValue());
		assertEquals("the words", streBetween.getValue());
	}

	@Test
	public void lineTest() {
		List<Stringe> lines = new Stringe("Hello\nWorld!").split('\n');

		assertEquals(1, lines.get(0).getLine());
		assertEquals(2, lines.get(1).getLine());
	}
}
