package net.jolm.util;

import static org.junit.Assert.*;
import static net.jolm.util.UserPasswordHelper.HashAlg.*;

import org.junit.Test;

public class UserPasswordHelperTest {
	private String password = "password";
	private byte[] salt = new byte[] {1, 2, 3, 4, 5};
	
	@Test
	public void check_md5() {
		byte[] userPassword = UserPasswordHelper.clearPassToMD5UserPassword(password);
		assertTrue(UserPasswordHelper.verifyPassword(password, userPassword));
	}
	
	@Test
	public void check_md5__with_salt() {
		byte[] userPassword = UserPasswordHelper.clearPassToUserPassword(password, SMD5, salt);
		assertTrue(UserPasswordHelper.verifyPassword(password, userPassword));
	}
	
	@Test
	public void check_sha() {
		byte[] userPassword = UserPasswordHelper.clearPassToSHAUserPassword(password);
		assertTrue(UserPasswordHelper.verifyPassword(password, userPassword));
	}
	
	@Test
	public void check_sha_with_salt() {
		byte[] userPassword = UserPasswordHelper.clearPassToUserPassword(password, SSHA, salt);
		assertTrue(UserPasswordHelper.verifyPassword(password, userPassword));
	}
	
	@Test
	public void check_clear_pass() {
		assertTrue(UserPasswordHelper.verifyPassword(password, password.getBytes()));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void check_wrong_hash() {
		UserPasswordHelper.clearPassToUserPassword(password, null, salt);
	}
}
