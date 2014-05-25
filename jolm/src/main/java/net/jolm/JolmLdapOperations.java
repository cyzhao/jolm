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

import java.util.List;

/**
 * Defines list of convenient CRUD operations.
 * 
 * @author Chunyun Zhao
 */
public interface JolmLdapOperations {
	/**
	 * Creates an entity in LDAP server. The 'dn' of the entity field will also
	 * be updated with DN of the newly created LDAP Entity.
	 * 
	 * @param parentDn The DN of the parent of this entity. Base DN should not
	 * 		be included.
	 * @param entity The LDAP Entity.
	 * @return The LDAP entity with DN populated.
	 * @throws NameAlreadyBoundException if entity already exists in LDAP server.
	 */
	public LdapEntity create(String parentDn, LdapEntity entity);
	/**
	 * Modifies the entity. Only the fields that has value within LDAP Entity
	 * will be modified.
	 * 
	 * @param parentDn The DN of its parent.
	 * @param entity The LDAP Entity.
	 * @throws NameNotFoundException when the LDAP Entity doesn't exist.
	 */
	public void modify(String parentDn, LdapEntity entity);
	/**
	 * Modifies the LDAP Entity. Only the fields that has value within LDAP Entity
	 * will be modified.
	 * 
	 * @param entity The LDAP Entity.
	 * @throws NameNotFoundException when the LDAP Entity doesn't exist.
	 */
	public void modify(LdapEntity entity);
	/**
	 * Finds the LDAP Entity by DN. 
	 * 
	 * @param dn The DN of the LDAP Entity.
	 * @param entityClass The LDAP Entity Class.
	 * @return The LDAP Entity.
	 * @throws NameNotFoundException if the DN doesn't exist in LDAP server.
	 */
	public LdapEntity get(String dn, Class<? extends LdapEntity> entityClass);
	/**
	 * Finds the LDAP Entities by Example. Uses AndFilter and EqualsFilter by default.
	 * 
	 * @param base The base path. 
	 * @param example The LDAP Entity that has query fields set properly. 
	 * @return List of LDAP Entities.
	 * @throws
	 */
	public List<? extends LdapEntity> findByExample(String base, LdapEntity example);
	
	/**
	 * Finds the LDAP Entities matched by all examples. Uses OrFilter at example level 
	 * and uses AndFilter and EqualsFilter within the example. 
	 * 
	 * @param base The base path. 
	 * @param examples The LDAP Entities that has query fields set properly.
	 * @return List of LDAP Entities.
	 * @throws
	 */
	public List<? extends LdapEntity> findByExamples(String base, LdapEntity[] examples);
	
	/**
	 * Finds the LDAP entities by LDAP search filter.
	 * 
	 * @param base The base.
	 * @param filter LDAP filter.
	 * @return List of LDAP Entities.
	 */
	public List<? extends LdapEntity> find(String base, String filter, Class<? extends LdapEntity> entityClass);
	
	/**
	 * Finds the LDAP Entities by Example. Uses AndFilter and EqualsFilter by default.
	 * 
	 * @param base The base path. 
	 * @param example The LDAP Entity that has all fields set 
	 * @param attributes List of attributes to return.
	 * @return List of LDAP Entities.
	 * @throws
	 */
	public List<? extends LdapEntity> findByExample(String base, LdapEntity example, String[] attributes);
	
	/**
	 * Finds the LDAP Entities matched by all examples. Uses OrFilter at example level 
	 * and uses AndFilter and EqualsFilter within the example. 
	 * 
	 * @param base The base path. 
	 * @param examples The LDAP Entities that has query fields set properly.
	 * @param attributes List of attributes to return.	 * 
	 * @return List of LDAP Entities.
	 * @throws
	 */	
	public List<? extends LdapEntity> findByExamples(String base, LdapEntity[] examples, String[] attributes);
	
	/**
	 * Finds the LDAP entities by LDAP search filter.
	 * 
	 * @param base The base.
	 * @param filter LDAP filter. For example (cstCustGuid=12345667)
	 * @param attributes List of attributes to return.
	 * @return List of LDAP Entities.
	 */
	public List<? extends LdapEntity> find(String base, String filter, String[] attributes, Class<? extends LdapEntity> entityClass);
	
	/**
	 * List the children of a specific objectClass under an entity identified by baseDn.
	 * 
	 * @param baseDn The base DN.
	 * @param childEntityClass The child entity class.
	 * @return List of LDAP Entities.
	 * @throws NameNotFoundException when the base DN doesn't exist
	 */
	public List<? extends LdapEntity> listChildren(String baseDn, Class<? extends LdapEntity> childEntityClass);
			
	/**
	 * List the children of a specific objectClass under an entity identified by baseDn.
	 * 
	 * @param baseDn The base DN.
	 * @param childEntityClass The child entity class.
	 * @param attributes  List of attributes to return.
	 * @return List of LDAP Entities.
	 * @throws NameNotFoundException when the base DN doesn't exist
	 */
	public List<? extends LdapEntity> listChildren(String baseDn, Class<? extends LdapEntity> childEntityClass, String[] attributes);
	
	/**
	 * Deletes the LDAP Entity. The entity must not have any children. If the 
	 * entity could have children, use deleteRecursively method instead.
	 * 
	 * @param dn The DN of the LDAP Entity.
	 * @throws NameNotFoundException when the DN doesn't exist
	 */
	public void delete(String dn);
	
	/**
	 * Deletes the LDAP Entity. Removes all the descendants if any.
	 * 
	 * @param dn The DN of the LDAP Entity.
	 * @throws NameNotFoundException when the DN doesn't exist
	 */
	public void deleteRecursively(String dn);
}
