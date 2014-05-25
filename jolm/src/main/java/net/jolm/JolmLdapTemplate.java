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

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.directory.SearchControls;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.core.LdapRdn;
import org.springframework.ldap.core.simple.SimpleLdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.OrFilter;
import org.springframework.ldap.filter.WhitespaceWildcardsFilter;
import org.springframework.util.Assert;

/**
 * Provides convenient CRUD operations that encapsulates LDAP complexity and specifics.
 * 
 * @author Chunyun Zhao
 */
public class JolmLdapTemplate extends SimpleLdapTemplate implements JolmLdapOperations, InitializingBean {
	private final static Log log = LogFactory.getLog(JolmLdapTemplate.class);
	private static final boolean RETURN_OBJ_FLAG = true;
	private String contextMappersPackage;
	private int searchTimeoutInMs;
	
	public int getSearchTimeoutInMs() {
		return searchTimeoutInMs;
	}

	public void setSearchTimeoutInMs(int searchTimeoutInMs) {
		this.searchTimeoutInMs = searchTimeoutInMs;
	}

	public JolmLdapTemplate(ContextSource contextSource) {
		super(contextSource);
	}

	public JolmLdapTemplate(LdapOperations ldapOperations) {
		super(ldapOperations);
	}
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
	public LdapEntity create(String parentDn, LdapEntity entity) {
		DistinguishedName dn = new DistinguishedName(parentDn);
		dn.add(new LdapRdn(entity.getRdn()));
		LdapContextMapper contextMapper = getLdapContextMapper(entity.getClass());
		getLdapOperations().bind(dn, contextMapper.mapToContext(entity), null);
		entity.setDn(dn.toString());
		return entity;
	}
	/**
	 * Modifies the entity in LDAP server. Only the fields that have values within 
	 * LDAP Entity will be modified. If you want to set a specific field in LDAP
	 * to null, use {@link net.jolm.Null} to set the fields.
	 * 
	 * @param parentDn The DN of its parent.
	 * @param entity The LDAP Entity.
	 * @throws NameNotFoundException when the LDAP Entity doesn't exist.
	 * 
	 * @see net.jolm.Null
	 */
	public void modify(String parentDn, LdapEntity entity) {
		DistinguishedName dn = new DistinguishedName(parentDn);
		dn.add(new LdapRdn(entity.getRdn()));
		
		modifyTemplate(dn, entity);
	}		
	/**
	 * Modifies the LDAP Entity. Only the fields that has value within LDAP Entity
	 * will be modified. The DN field of the entity has to be set.
	 * 
	 * @param entity The LDAP Entity.
	 * @throws NameNotFoundException when the LDAP Entity doesn't exist.
	 * @throws IllegalArgumentException if {@code dn} field is not set in the LDAP entity.
	 */
	public void modify(LdapEntity entity) {
		if ( StringUtils.isEmpty(entity.getDn()) ) {
			throw new IllegalArgumentException("The 'dn' field in the entity can not be empty with modify operation.");
		}
		modifyTemplate(new DistinguishedName(entity.getDn()), entity);
	}
	
	/**
	 * Finds the LDAP Entity by DN. 
	 * 
	 * @param dn The DN of the LDAP Entity.
	 * @param entityClass The LDAP Entity Class.
	 * @return The LDAP Entity.
	 * @throws NameNotFoundException if the DN doesn't exist in LDAP server.
	 */
	public LdapEntity get(String dn,
			Class<? extends LdapEntity> entityClass) {
		return (LdapEntity)getLdapOperations().lookup(new DistinguishedName(dn), getLdapContextMapper(entityClass));
	}
	
	/**
	 * Finds the LDAP Entities by Example. Uses AndFilter and EqualsFilter by default.
	 * 
	 * @param base The base path. 
	 * @param example The LDAP Entity that has query fields set properly. 
	 * @return List of LDAP Entities.
	 */
	public List<? extends LdapEntity> findByExample(String base, LdapEntity example) {
		return findByExample(base, example, false);
	}
	
	/**
	 * Finds the LDAP Entities by Example. Uses AndFilter and EqualsFilter by default.
	 * 
	 * @param base The base path. 
	 * @param example The LDAP Entity that has query fields set properly. 
	 * @param wildcardFilters Indicates whether to use {@code WhitespaceWildcardsFilter} or not. EqualsFilter will
	 * 		be used when it is false. 
	 * @return List of LDAP Entities.
	 * 
	 * @see org.springframework.ldap.filter.WhitespaceWildcardsFilter
	 */
	@SuppressWarnings("unchecked")
	public List<? extends LdapEntity> findByExample(String base, LdapEntity example, boolean wildcardFilters) {
		AndFilter filter = getAndFilterFromExample(example, wildcardFilters);
		return searchTemplate(base, filter.encode(), example.getClass());
	}
	

	/**
	 * Finds the LDAP Entities by Example. Uses AndFilter and EqualsFilter by default.
	 * 
	 * @param base The base path. 
	 * @param example The LDAP Entity that has all fields set 
	 * @param attributes Specifies list of attributes to return back. All the attributes 
	 * 		will be returned if it is null.
	 * @return List of LDAP Entities.
	 * @throws
	 */
	public List<? extends LdapEntity> findByExample(String base,
			LdapEntity example, String[] attributes) {
		return findByExample(base, example, attributes, false);
	}	
	
	/**
	 * Finds the LDAP Entities by Example. Uses AndFilter and EqualsFilter by default.
	 * 
	 * @param base The base path. 
	 * @param example The LDAP Entity that has query fields set properly. 
	 * @param attributes Specifies list of attributes to return back. All the attributes 
	 * 		will be returned if it is null.
	 * @param wildcardFilters Indicates whether to use {@code WhitespaceWildcardsFilter} or 
	 * 		not. EqualsFilter will be used when it is false. 
	 * @return List of LDAP Entities.
	 * 
	 * @see org.springframework.ldap.filter.WhitespaceWildcardsFilter
	 */
	public List<? extends LdapEntity> findByExample(String base,
			LdapEntity example, String[] attributes, boolean wildcardFilters) {
		AndFilter filter = getAndFilterFromExample(example, wildcardFilters);
		return searchTemplate(base, filter.encode(), example.getClass(), attributes);

	}	
	
	/**
	 * Finds the LDAP Entities matched by all examples. {@code OrFilter} is used at example level 
	 * and {@code AndFilter} and {@code EqualsFilter} are used within the example. 
	 * 
	 * @param base The base path. 
	 * @param examples The LDAP Entities that has query fields set properly.
	 * @return List of LDAP Entities.
	 */
	public List<? extends LdapEntity> findByExamples(String base, LdapEntity[] examples) {
		return findByExamples(base, examples, false);
	}		
	
	/**
	 * Finds the LDAP Entities matched by all examples. {@code OrFilter} is used at example level 
	 * and {@code AndFilter} are used within the example. 
	 * 
	 * @param base The base path. 
	 * @param examples The LDAP Entities that has query fields set properly.
	 * @param wildcardFilters Indicates whether to use {@code WhitespaceWildcardsFilter} or 
	 * 		not. EqualsFilter will be used when it is false.
	 * @return List of LDAP Entities.
	 * 
	 * @see org.springframework.ldap.filter.WhitespaceWildcardsFilter
	 */	
	public List<? extends LdapEntity> findByExamples(String base, LdapEntity[] examples, boolean wildcardFilters) {
		return findByExamples(base, examples, null, wildcardFilters);
	}	

	/**
	 * Finds the LDAP Entities matched by all examples. {@code OrFilter} is used at example level 
	 * and {@code AndFilter} are used within the example. 
	 * 
	 * @param base The base path. 
	 * @param examples The LDAP Entities that has query fields set properly.
	 * @param attributes Specifies list of attributes to return back. All the attributes 
	 * 		will be returned if it is null. 
	 * @param wildcardFilters Indicates whether to use {@code WhitespaceWildcardsFilter} or 
	 * 		not. EqualsFilter will be used when it is false.
	 * @return List of LDAP Entities.
	 */		
	public List<? extends LdapEntity> findByExamples(String base,
			LdapEntity[] examples, String[] attributes, boolean wildcardFilters) {
		if ( examples == null || examples.length == 0) {
			return new ArrayList<LdapEntity>();
		}
		OrFilter filter = getOrFilterFromExamples(examples, wildcardFilters);
		return searchTemplate(base, filter.encode(), examples[0].getClass(), attributes);
	}		
	
	/**
	 * Finds the LDAP Entities matched by all examples. Uses OrFilter at example level 
	 * and uses AndFilter and EqualsFilter within the example. 
	 * 
	 * @param base The base path. 
	 * @param examples The LDAP Entities that has query fields set properly.
	 * @param attributes Specifies list of attributes to return back. All the attributes 
	 * 		will be returned if it is null. 
	 * @return List of LDAP Entities.
	 * @throws
	 */	
	public List<? extends LdapEntity> findByExamples(String base, LdapEntity[] examples, String[] attributes) {
		return findByExamples(base, examples, attributes, false);
	}	
	
	/**
	 * Finds the LDAP entities by LDAP search filter. Use this method if you know LDAP query pretty well and want to
	 * construct the filter manually.
	 * 
	 * @param base The base.
	 * @param filter LDAP filter.
	 * @return List of LDAP Entities.
	 */
	public List<? extends LdapEntity> find(String base, String filter, Class<? extends LdapEntity> entityClass) {
		return searchTemplate(base, filter, entityClass);
	}	
		
	/**
	 * Finds the LDAP entities by LDAP search filter.
	 * 
	 * @param base The base.
	 * @param filter LDAP filter. For example (cstCustGuid=12345667)
	 * @param attributes Specifies list of attributes to return back. All the attributes 
	 * 		will be returned if it is null. 
	 * @return List of LDAP Entities.
	 */
	public List<? extends LdapEntity> find(String base, String filter, String[] attributes, Class<? extends LdapEntity> entityClass) {
		return searchTemplate(base, filter, entityClass, attributes);
	}
	
	/**
	 * List the children of a specific objectClass under an entity in LDAP identified by {@code baseDn}.
	 * 
	 * @param baseDn The base DN.
	 * @param childEntityClass The child entity class.
	 * @return List of LDAP Entities.
	 * @throws NameNotFoundException when the base DN doesn't exist
	 */
	public List<? extends LdapEntity> listChildren(String baseDn, Class<? extends LdapEntity> childEntityClass) {
		@SuppressWarnings({ "unchecked" })
		List<? extends LdapEntity> result = getLdapOperations().listBindings(baseDn, getLdapContextMapper(childEntityClass));
		result = filterNullEntities(result);
		return completeDnInEntities(baseDn, result);
	}		
			
	/**
	 * List the children of a specific objectClass under an entity identified by baseDn.
	 * 
	 * @param baseDn The base DN.
	 * @param childEntityClass The child entity class.
	 * @param attributes Specifies list of attributes to return back. All the attributes 
	 * 		will be returned if it is null. 
	 * @return List of LDAP Entities.
	 * @throws NameNotFoundException when the base DN doesn't exist
	 */
	@SuppressWarnings("unchecked")
	public List<? extends LdapEntity> listChildren(String baseDn, Class<? extends LdapEntity> childEntityClass, String[] attributes) {
		List<LdapEntity> result = (List<LdapEntity>) listChildren(baseDn, childEntityClass);
		return filterAttributes(result, attributes);
	}	
	
	/**
	 * Deletes the LDAP Entity. The entity must not have any children. If the 
	 * entity could have children, use {@code deleteRecursively} method instead.
	 * 
	 * @param dn The DN of the LDAP Entity.
	 * @throws NameNotFoundException when the DN doesn't exist
	 * 
	 * @see #deleteRecursively(String)
	 */
	public void delete(String dn) {
		deleteTemplate(dn, false);
	}		
	/**
	 * Deletes the LDAP Entity. Removes all the children if there are any.
	 * 
	 * @param dn The DN of the LDAP Entity.
	 * @throws NameNotFoundException when the DN doesn't exist
	 */
	public void deleteRecursively(String dn) {
		deleteTemplate(dn, true);
	}
	
	
	/**
	 * Returns a {@code LdapContextMapper} instance for a {@code ldapEntityClass}.
	 * 
	 * @param ldapEntityClass
	 * @return {@code LdapContextMapper} instance.
	 * 
	 * @see net.jolm.LdapContextMapper
	 */
	protected LdapContextMapper getLdapContextMapper(Class<? extends LdapEntity> ldapEntityClass) {
		String mapperClassName = contextMappersPackage + "." + ldapEntityClass.getSimpleName() + "Mapper";
		try {
			Class<?> mapperClass = Thread.currentThread().getContextClassLoader().loadClass(mapperClassName);
			return (LdapContextMapper)mapperClass.newInstance();
		} catch (ClassNotFoundException e) {
			//Should not happen.
			throw new RuntimeException("Unable to find context mapper class: " + mapperClassName);
		} catch (Exception e) {
			//Should not happen.
			throw new RuntimeException("Unable to instantiate context mapper class: " + mapperClassName);			
		}
	}
	
	private List<? extends LdapEntity> searchTemplate(String base, String filter, Class<? extends LdapEntity> entityClass) {
		return searchTemplate(base, filter, entityClass, null);
	}	
	
	@SuppressWarnings("unchecked")
	private List<? extends LdapEntity> searchTemplate(String base, String filter, Class<? extends LdapEntity> entityClass, String[] attributes) {
		List<? extends LdapEntity> result = null; 
		if (attributes != null ) {
			attributes = addObjectClassIfMissed(attributes);
		}
		result = getLdapOperations().search(base, filter, getDefaultSearchControls(SearchControls.SUBTREE_SCOPE, RETURN_OBJ_FLAG, attributes), getLdapContextMapper(entityClass));
		
		return filterNullEntities(result);
	}
	
	private SearchControls getDefaultSearchControls(int searchScope, boolean returnObjFlag, String[] attributes) {
        SearchControls controls = new SearchControls();
        
        controls.setSearchScope(searchScope);
        controls.setReturningObjFlag(returnObjFlag);
        controls.setReturningAttributes(attributes);
        controls.setTimeLimit(this.searchTimeoutInMs );
        
        return controls;
	}
	
	@SuppressWarnings("unchecked")
	private List<? extends LdapEntity> completeDnInEntities(String baseDn, List<? extends LdapEntity> entities) {
		for ( LdapEntity entity : entities ) {
			try {
				entity.setDn(new DistinguishedName(baseDn).add(entity.getRdn()).toString());
			} catch (InvalidNameException ignoreIt) {
				log.warn("Exception occurred while constructing DN.", ignoreIt);
			}
		}
		return entities;
	}

	private void modifyTemplate(DistinguishedName dn, LdapEntity entity) {
		DirContextOperations dirContext = getLdapOperations().lookupContext(dn);
		getLdapContextMapper(entity.getClass()).mapToContext(entity, dirContext);
		getLdapOperations().modifyAttributes(dirContext);				
	}
	
	private void deleteTemplate(String dn, boolean recursive) {
		DistinguishedName distinguishedName = new DistinguishedName(dn);
		
		//This is to make sure that the name actually exists. Throws
		//NameNotFoundException if it doesn't exist. 
		getLdapOperations().lookup(distinguishedName);
		
		getLdapOperations().unbind(distinguishedName, recursive);
	}
	

	/**
	 * Adds objectClass attribute to attributes list that need to be returned, it has to be
	 * returned.
	 */
	private String[] addObjectClassIfMissed(String[] attributes) {
		if (!Arrays.asList(attributes).contains("objectClass") ) {
			return (String[])ArrayUtils.add(attributes, "objectClass");
		}
		return attributes;
	}

	private List<? extends LdapEntity> filterNullEntities(List<? extends LdapEntity> entities) {
		List<LdapEntity> result = new ArrayList<LdapEntity>();
		
		for ( LdapEntity entity : entities ) {
			if ( entity != null ) {
				result.add(entity);
			}
		}
		return result;
	}

	
	private OrFilter getOrFilterFromExamples(LdapEntity[] examples, boolean wildcardFilters) {
		OrFilter orFilter = new OrFilter();
		for ( LdapEntity example : examples) {
			orFilter.or(getAndFilterFromExample(example, wildcardFilters, false));
		}
		if ( log.isDebugEnabled() ) {
			log.debug("Finding " + examples[0].getClass().getSimpleName() + "(s) using filter: " + orFilter.encode());
		}		
		return orFilter;
	}
	
	private AndFilter getAndFilterFromExample(LdapEntity example, boolean wildcardFilters, boolean logFilter) { 		
		try {
			AndFilter filter = new AndFilter();
	        BeanInfo info = Introspector.getBeanInfo( example.getClass() );
	        for ( PropertyDescriptor pd : info.getPropertyDescriptors() ) {
	        	if ( isAttributeApplicableInFilter(pd) ) {
	        		Object value = pd.getReadMethod().invoke(example);
	        		if ( value != null ) {
		        		if ( value.getClass().isArray() ) {
		        			Object[] valueArray = (Object[])value;
		        			for ( Object o : valueArray ) {
			        			addAndFilter(filter, pd, o, wildcardFilters);		        				
		        			}
		        		} else {
			        		if ( StringUtils.isNotEmpty(value.toString())) {
			        			addAndFilter(filter, pd, value, wildcardFilters);
			        		}
		        		}
	        		}
	        	}
	        }
			
			if ( logFilter && log.isDebugEnabled() ) {
				log.debug("Finding " + example.getClass().getSimpleName() + "(s) using filter: " + filter.encode());
			}		
			return filter;
		} catch (Exception e) {
			throw new RuntimeException("Unable to create the filter from ldap entity:" + example, e);
		}  
	}
	
	private AndFilter getAndFilterFromExample(LdapEntity example, boolean wildcardFilters) { 		
		return getAndFilterFromExample(example, wildcardFilters, true);
	}


	private void addAndFilter(AndFilter filter, PropertyDescriptor pd,
			Object value, boolean wildcardFilters) {
		if ( wildcardFilters ) {
			filter.and(new WhitespaceWildcardsFilter(pd.getName(), value.toString()));
		} else {
			filter.and(new EqualsFilter(pd.getName(), value.toString()));
		}
	}

	private List<? extends LdapEntity> filterAttributes(List<LdapEntity> entities, String[] attributes) {
		if ( entities == null || entities.size() == 0) {
			return entities;
		}
		List<String> attributesAsList = Arrays.asList(attributes);
		for ( LdapEntity entity : entities) {
			BeanInfo beanInfo;
			try {
				beanInfo = Introspector.getBeanInfo(entity.getClass());
				PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
				for ( PropertyDescriptor pd : propertyDescriptors ) {
					if ( isAttributeFiltered(pd.getName(), attributesAsList) ) {
						Method writeMethod = pd.getWriteMethod();
						writeMethod.invoke(entity, new Object[] {null});
					}
				}
			} catch (Exception e) {
				//Should never happen
				throw new RuntimeException(e);
			}
		}
		return entities;
	}

	private boolean isAttributeFiltered(String fieldName, List<String> attributes) {
		return !(attributes.contains(fieldName) || isReservedField(fieldName));
	}	

	@SuppressWarnings("unchecked")
	private static List applicableTypesInFilter = Arrays.asList(String.class, String[].class);
	
	private boolean isAttributeApplicableInFilter(PropertyDescriptor pd) {
		String propertyName = pd.getName();
		return applicableTypesInFilter.contains(pd.getPropertyType()) && !isReservedField(propertyName);
	}

	private static List<String> reservedAttributeNames = 
		Arrays.asList("class", "objectClass", "objectClasses", "childObjectClasses", "dn", "rdn", "parentDn");
	
	private boolean isReservedField(String attributeName) {
		return reservedAttributeNames.contains(attributeName);
	}

	public String getContextMappersPackage() {
		return contextMappersPackage;
	}

	public void setContextMappersPackage(String contextMappersPackage) {
		this.contextMappersPackage = contextMappersPackage;
	}
	
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(contextMappersPackage, "contextMappersPackage must be set");
	}
}
