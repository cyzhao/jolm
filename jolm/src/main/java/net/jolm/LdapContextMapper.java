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

import org.springframework.ldap.core.DirContextOperations;

/**
 * The interface provides operations to map LDAP entities to and from
 * DirContextOperations.  
 * 
 * @author Chunyun Zhao
 * @since 1.0
 */
public interface LdapContextMapper extends org.springframework.ldap.core.ContextMapper {
	/**
	 * Maps the object attributes to LDAP DirContext.
	 * 
	 * @param object The LDAP Entity.
	 */
	public DirContextOperations mapToContext(Object object);	
	/**
	 * Maps the object attributes to LDAP DirContext. The DirContext is passed in and 
	 * its attributes will be populated with values from LDAP Entity. 
	 * 
	 * @param object The LDAP Entity.
	 * @param context LDAP DirContext
	 */
	public void mapToContext(Object object, DirContextOperations context);
	/**
	 * Maps the DirContext to LDAP Entity. The LDAP Entity will be created within
	 * implementation and returned back.
	 * 
	 * @param context LDAP DirContext
	 */
	public Object mapFromContext(Object ctx);
	
	/**
	 * Maps the DirContext to LDAP Entity. The LDAP Entity is passed in and its fields
	 * will be populated with values from LDAP DirContext. This method is to support 
	 * LdapContextMapper inheritence.
	 * 
	 * @param ctx The LDAP DirContext.
	 * @param resultObject The LDAP Entity
	 */
	public void mapFromContext(DirContextOperations ctx, Object resultObject);
}
