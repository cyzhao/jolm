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

/**
 * @author Chunyun Zhao
 */
public class SchemaBinding {
	private String bindingName;
	private String ldapName;
	private String allowableParent;
	private String namedBy;
	
	public String getBindingName() {
		return bindingName;
	}
	public void setBindingName(String bindingName) {
		this.bindingName = bindingName;
	}
	public String getLdapName() {
		return ldapName;
	}
	public void setLdapName(String ldapName) {
		this.ldapName = ldapName;
	}
	public String getAllowableParent() {
		return allowableParent;
	}
	public void setAllowableParent(String allowableParent) {
		this.allowableParent = allowableParent;
	}
	public String getNamedBy() {
		return namedBy;
	}
	public void setNamedBy(String namedBy) {
		this.namedBy = namedBy;
	}
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("SchemaBinding@" + this.hashCode() + "[\n");
		result.append("   bindingName:" + getBindingName());
		result.append("\n");
		result.append("   ldapName:" + getLdapName());
		result.append("\n");
		result.append("   allowableParent:" + getAllowableParent());
		result.append("\n");
		result.append("   namedBy:" + getNamedBy());
		result.append("]");
		return result.toString();
	}
}
