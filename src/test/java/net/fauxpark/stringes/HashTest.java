package net.fauxpark.stringes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.fauxpark.tuples.FourTuple;
import net.fauxpark.tuples.ThreeTuple;
import net.fauxpark.tuples.Tuples;

public class HashTest {
	@Test
	public void tupleHashCode() {
		ThreeTuple<Float, String, Boolean> threeTuple = Tuples.create(9.0f, "", true);
		FourTuple<String, Integer, Boolean, Character> fourTuple = Tuples.create("Hello", 1345634, false, '7');

		assertEquals(1024459983, threeTuple.hashCode());
		assertEquals(565071858, fourTuple.hashCode());
	}

	@Test
	public void regularHashOf() {
		String str = "Hello world!";

		assertEquals(str.hashCode(), Util.hashOf(str));
	}
}
