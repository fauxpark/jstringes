package net.fauxpark.stringes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MetadataTest {
	@Test
	public void occurrenceCount() {
		Stringe streParent = Stringe.toStringe("no no no no no");
		Stringe streSub = streParent.substringe(0, 2);

		assertEquals(5, streSub.getOccurrenceCount());

		streParent = Stringe.toStringe("a b c d e f g h");
		streSub = streParent.substringe(0, 1);

		assertEquals(1, streSub.getOccurrenceCount());
	}

	@Test
	public void nextIndex() {
		Stringe streParent = Stringe.toStringe("no no no no no");
		Stringe streSub = streParent.substringe(0, 2);

		assertEquals(3, streSub.getNextIndex());

		streParent = Stringe.toStringe("a b c d a f g h");
		streSub = streParent.substringe(0, 1);

		assertEquals(8, streSub.getNextIndex());
	}
}
