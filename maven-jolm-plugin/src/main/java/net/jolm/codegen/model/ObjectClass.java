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
package net.jolm.codegen.model;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents LDAP Schema ObjectClass.
 * 
 * @author Chunyun Zhao
 */
public class ObjectClass {
	private String name;
	private String ldapNames;
	private String subclassOf;
	private String kind;
	private Set<Attribute> requiredAttributes = new LinkedHashSet<Attribute>();
	private Set<Attribute> optionalAttributes = new LinkedHashSet<Attribute>();
	private Set<String> requiredAttributeNames = new LinkedHashSet<String>();
	private Set<String> optionalAttributeNames = new LinkedHashSet<String>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLdapNames() {
		return ldapNames;
	}
	public void setLdapNames(String ldapNames) {
		this.ldapNames = ldapNames;
	}
	public String getSubclassOf() {
		return subclassOf;
	}
	public void setSubclassOf(String subclassOf) {
		this.subclassOf = subclassOf;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public Set<Attribute> getRequiredAttributes() {
		return requiredAttributes;
	}

	public Set<Attribute> getOptionalAttributes() {
		return optionalAttributes;
	}
	
	public void addRequiredAttribute(Attribute attribute) {
		this.requiredAttributes.add(attribute);
	}
	
	public void addOptionalAttribute(Attribute attribute) {
		this.optionalAttributes.add(attribute);
	}
	
	public Set<String> getRequiredAttributeNames() {
		return requiredAttributeNames;
	}
	public Set<String> getOptionalAttributeNames() {
		return optionalAttributeNames;
	}
	
	public void addRequiredAttributeName(String attributeName) {
		this.requiredAttributeNames.add(attributeName);
	}
	
	public void addOptionalAttributeName(String attributeName) {
		this.optionalAttributeNames.add(attributeName);
	}	
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		
		result.append("ObjectClass:" + getName());
		result.append("[\n");
		result.append("\tname:" + getName());
		result.append("\n");
		result.append("\tldapNames:" + getLdapNames());
		result.append("\n");
		result.append("\tsubclassOf:" + getSubclassOf());
		result.append("\n");
		result.append("\tKind:" + getKind());
		result.append("\n");
		result.append("\tRequiredAttributes:" + getRequiredAttributes());
		result.append("\n");
		result.append("\tOptionalAttributes" + getOptionalAttributes());
		result.append("\n");
		result.append("]\n");
		
		return result.toString();
	}	
}
