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

import java.io.Serializable;

/**
 * The interface to be implemented by all LDAP entities. LDAP entities can be 
 * generated from LDAP schema using net.jolm.maven:maven-jolm-plugin.
 * 
 * @author Chunyun Zhao
 * @since 1.0
 */
public interface LdapEntity extends Serializable {
	/**
	 * @return Array of object classes including object class of the entity and object classes 
	 * 		inherited from its ancestors.			
	 */
	public String[] getObjectClasses();
	
	/**
	 * @return Array of object classes of all possible children for this LDAP entity.
	 */
	public String[] getChildObjectClasses();
	
	/**
	 * @return The object class of the LDAP entity. It doesn't include object classes from its 
	 * 		ancestors.
	 */
	public String getObjectClass();
	
	/**
	 * @return The DN of its parent.
	 */
	public String getParentDn();
	
	/**
	 * @return Relative Distinguished Name of the LDAP entity.
	 */
	public String getRdn();
	
	/**
	 * @return The DistinguishedName of the LDAP Entity. The 'dn' field will
	 * 		only be populated when the LDAP Entity is retrieved from LDAP
	 * 		server.
	 */
	public String getDn();
	
	/**
	 * Sets the DN of the LDAP Entity.
	 * @param dn The DN of the LDAP Entity.
	 */
	public void setDn(String dn);
}
