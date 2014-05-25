package net.jolm;

import static org.junit.Assert.*;

import org.junit.Test;

public class NullTest {
	@Test public void check_null() {
		assertFalse(Null.isNullObject(null));
	}
	
	@Test public void check_bytearray() {
		assertTrue(Null.isNullObject(Null.BYTEARRAY));
		assertFalse(Null.isNullObject(new byte[] {1, 2, 3}));
	}
	
	@Test public void check_bytearray_array() {
		assertTrue(Null.isNullObject(Null.BYTEARRAY_ARRAY));
		assertFalse(Null.isNullObject(new byte[][] {{1, 2, 3}}));
	}
	
	@Test public void check_string() {
		assertTrue(Null.isNullObject(Null.STRING));
		assertFalse(Null.isNullObject("test"));
	}
	
	@Test public void check_string_array() {
		assertTrue(Null.isNullObject(Null.STRING_ARRAY));
		assertFalse(Null.isNullObject(new String[] {"test"}));
	}
	
	@Test public void check_object() {
		assertTrue(Null.isNullObject(Null.OBJECT));
		assertFalse(Null.isNullObject(new Object()));
	}
	@Test public void check_object_array() {
		assertTrue(Null.isNullObject(Null.OBJECT_ARRAY));
		assertFalse(Null.isNullObject(new Object[] {new Object()}));
	}
}
