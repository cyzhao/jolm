/*
 * Copyright 2008 (C) Chunyun Zhao(Chunyun.Zhao@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.jolm;

import java.util.Arrays;


/**
 * <code>Null</code> defines the null values, one for each data type we support in LDAP 
 * Entity, of fields that need to be set to null in LDAP server. <p/>
 * 
 * When <code>LdapContextMapper</code> maps a LDAP Entity to <code>DirContext</code>,
 * it only sets the value for a field in the context when the value of a field is not 
 * null. The reason behind this is that we don't want to modify all the attributes
 * of a LDAP entity even when we only need to modify a few attributes, also modifying 
 * all attributes requires that a get operation has to happen before the modify operation
 * in this case.
 * <p/>
 * If we need to set certain fields to null in LDAP, we will need to set these fields in
 * LDAP Entity to respective constants defined in <code>Null</code>.
 * 
 * @author Chunyun Zhao
 * @since 1.0
 */
public final class Null {
	/**
	 * The represents a null string. Please note that none of the field in 
	 * LDAP can be modified to this value.
	 */
	private static final String NULL_STRING = ""; 
		
	public static final String STRING = NULL_STRING;
	public static final String[] STRING_ARRAY = new String[0];
	public static final Object OBJECT = STRING;
	public static final Object[] OBJECT_ARRAY = new Object[0];
	public static final byte[] BYTEARRAY = NULL_STRING.getBytes();
	public static final Object[] BYTEARRAY_ARRAY = new byte[0][0];
	
	/**
	 * Returns true if an instance represents a null object.
	 */
	public static boolean isNullObject(Object instance) {
		if ( instance instanceof String ) {
			return STRING.equals(instance);
		} else if ( instance instanceof byte[] ) {
			return Arrays.equals(BYTEARRAY, (byte[])instance);
		} else if ( instance != null && instance.getClass().isArray() ) { 
			return ((Object[])instance).length == 0;
		} else {
			return OBJECT.equals(instance);
		}
		
	}
	
	private Null() {
	}
}
