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

import java.util.HashMap;
import java.util.Map;

/**
 * Represents LDAP Schema Attribute.
 * 
 * @author Chunyun Zhao
 */
public class Attribute {
	private String javaFieldName;
	private String name;
	private String ldapNames;
	private String syntax;
	private boolean multiValues = true;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		this.javaFieldName = normalizeJavaFieldName(name);
	}
	
	private String normalizeJavaFieldName(String name) {
		if ( name == null || name.length() == 0) {
			throw new IllegalArgumentException("Ldap attribute name can't be empty.");
		}
		String result = name.replaceAll("-", "_");
		result = result.substring(0, 1).toLowerCase() + result.substring(1);
		return result;
	}
	
	public String getLdapAttributeName() {
		return name;
	}
	
	public String getJavaFieldName() {
		return javaFieldName;
	}
	
	public String getLdapNames() {
		return ldapNames;
	}
	public void setLdapNames(String ldapNames) {
		this.ldapNames = ldapNames;
	}
	public String getSyntax() {
		return syntax;
	}
	public void setSyntax(String syntax) {
		this.syntax = syntax;
	}
	public boolean isMultiValues() {
		return multiValues;
	}
	public void setMultiValues(boolean multiValues) {
		this.multiValues = multiValues;
	}
	public String toString() {
		return getName() + "|" + isMultiValues();
	}	
	
	public String getAttributeType() {
		String result = syntaxTypeMap.get(getSyntax()) + (isMultiValues() ? "[]" : "");
		if ( result == null ) {
			//default to Object.
			return "Object";
		}
		return result;
	}
	
	private static Map<String, String> syntaxTypeMap = new HashMap<String, String>();

	static {
		syntaxTypeMap.put("caseExactString", "String");	
		syntaxTypeMap.put("caseIgnoreString", "String");
		syntaxTypeMap.put("distinguishedName", "String");	
		
		syntaxTypeMap.put("octetStringMatch", "byte[]");	
		syntaxTypeMap.put("binary", "byte[]");	
		
		syntaxTypeMap.put("generalizedTime", "Object");	
		syntaxTypeMap.put("integer", "Object");	
		syntaxTypeMap.put("boolean", "Object");	
		syntaxTypeMap.put("jpeg", "Object");	
	}
}
