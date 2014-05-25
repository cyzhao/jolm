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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.simple.SimpleLdapTemplate;
import org.springframework.ldap.core.support.BaseLdapPathAware;
import org.springframework.util.Assert;

/**
 * Convenient super class for LDAP-based data access objects providing 
 * convenient CRUD operations that shield LDAP complexity and specifics
 * from its derived classes via <code>jolmLdapTemplate</code>.
 *
 * <p>Requires <code>jolmLdapTemplate</code> to be set.
 *
 * @author Chunyun Zhao
 * @since 1.0
 * @see org.springframework.ldap.core.LdapTemplate
 */
public class LdapDaoSupport implements BaseLdapPathAware, InitializingBean {
	private JolmLdapTemplate jolmLdapTemplate;
	private DistinguishedName baseLdapPath;
	private boolean ignorePartialResultException = true;

	public void setJolmLdapTemplate(JolmLdapTemplate jolmLdapTemplate) {
		this.jolmLdapTemplate = jolmLdapTemplate;
	}
	
	public JolmLdapTemplate getJolmLdapTemplate() {
		return jolmLdapTemplate;
	}

	public SimpleLdapTemplate getSimpleLdapTemplate() {
		return (SimpleLdapTemplate)jolmLdapTemplate;
	}
	
	public LdapTemplate getLdapTemplate() {
		return (LdapTemplate)jolmLdapTemplate.getLdapOperations();
	}
	
	public LdapOperations getLdapOperations() {
		return (LdapTemplate)jolmLdapTemplate.getLdapOperations();
	}

	public void setBaseLdapPath(DistinguishedName baseLdapPath) {
		this.baseLdapPath = baseLdapPath;
	}
	
	public DistinguishedName getBaseLdapPath() {
		return this.baseLdapPath;
	}

	public boolean isIgnorePartialResultException() {
		return ignorePartialResultException;
	}

	public void setIgnorePartialResultException(boolean ignorePartialResultException) {
		this.ignorePartialResultException = ignorePartialResultException;
	}	

	public void afterPropertiesSet() throws Exception {
		getLdapTemplate().setIgnorePartialResultException(ignorePartialResultException);
		Assert.notNull(jolmLdapTemplate, "jolmLdapTemplate must be set");
	}
}
