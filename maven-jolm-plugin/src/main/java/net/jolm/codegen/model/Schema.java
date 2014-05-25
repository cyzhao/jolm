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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Java model for parsed LDAP schemas.
 * 
 * @author Chunyun Zhao
 */
public class Schema {
	private Map<String, Attribute> attributes;
	private List<ObjectClass> objectClasses;
	private Map<String, SchemaBinding> schemaBindings;
	
	public Schema() {
		attributes = new HashMap<String, Attribute>();
		objectClasses = new ArrayList<ObjectClass>();		
		schemaBindings = new HashMap<String, SchemaBinding>();		
	}

	public Map<String, Attribute> getAttributes() {
		return attributes;
	}

	public List<ObjectClass> getObjectClasses() {
		return objectClasses;
	}

	public Map<String, SchemaBinding> getSchemaBindings() {
		return schemaBindings;
	}
}
