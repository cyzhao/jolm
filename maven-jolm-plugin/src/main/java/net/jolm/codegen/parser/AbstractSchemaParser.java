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
package net.jolm.codegen.parser;

import java.io.BufferedReader;
import java.io.FileReader;

import net.jolm.codegen.model.Attribute;
import net.jolm.codegen.model.ObjectClass;
import net.jolm.codegen.model.Schema;
import net.jolm.maven.mojo.Logger;

/**
 * @author Chunyun Zhao
 */
public abstract class AbstractSchemaParser implements SchemaParser {
	protected Schema schema;
	
	public Schema parse(String[] schemaFilePaths) throws Exception {
		schema = new Schema();
		initState();
		
		for (String schemaFilePath : schemaFilePaths ) {
			Logger.getInstance().info("Parsing " + schemaFilePath);
			BufferedReader fileReader = new BufferedReader(new FileReader(schemaFilePath));
			try {
				String line = null;
				while ((line = fileReader.readLine()) != null) {
					parseLine(line.trim());
				}
			} finally {
				if ( fileReader != null ) {
					fileReader.close();
				}
			}
		}
		
		resolveAttributesInObjectClasses();
		
		return schema;
	}
	
	private void resolveAttributesInObjectClasses() {
		for ( ObjectClass objectClass : schema.getObjectClasses() ) {
			resolveAttributesInObjectClass(objectClass);
		}
	}

	private void resolveAttributesInObjectClass(ObjectClass objectClass) {
		for ( String attributeName : objectClass.getRequiredAttributeNames() ) {
			objectClass.addRequiredAttribute(findOrCreateAttribute(attributeName));
		}
		
		for ( String attributeName : objectClass.getOptionalAttributeNames() ) {
			objectClass.addOptionalAttribute(findOrCreateAttribute(attributeName));
		}
	}

	/**
	 * Finds the attribute from the parsed attribute list. Create the default attribute
	 * object if it is not defined in the schema.
	 */
	private Attribute findOrCreateAttribute(String attributeName) {
		Attribute result = schema.getAttributes().get(attributeName.toLowerCase());
		if ( result == null ) {
			result = new Attribute();
			result.setName(attributeName);
			result.setLdapNames(attributeName);
			result.setSyntax("caseIgnoreString");
			result.setMultiValues(false);
			Logger.getInstance().debug("Created a default attribute object for attribute '" + attributeName + "'" );
			schema.getAttributes().put(attributeName.toLowerCase(), result);
		}
		
		return result;
	}
		
	/**
	 * Implement this method to parse the trimmed line.
	 * 
	 * @param trimmedLine the trimmed line
	 */
	protected abstract void parseLine(String trimmedLine);
	/**
	 * Initializes the internal state of the {@code SchemaParser}. This method is invoked within {@code #parse(String[])}
	 * to reset the internal state.
	 */
	protected abstract void initState();
}
