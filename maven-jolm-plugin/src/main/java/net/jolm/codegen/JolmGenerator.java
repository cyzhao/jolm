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
package net.jolm.codegen;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.jolm.codegen.model.Attribute;
import net.jolm.codegen.model.ObjectClass;
import net.jolm.codegen.model.Schema;
import net.jolm.codegen.model.SchemaBinding;
import net.jolm.codegen.parser.DxcSchemaParser;
import net.jolm.codegen.parser.SchemaParser;
import net.jolm.maven.mojo.Logger;
import freemarker.template.Template;


/**
 * The main program that compiles the LDAP schema and generates Java
 * artifacts.
 * 
 * @author Chunyun Zhao
 */
public class JolmGenerator {
	private final static String OBJECT_CLASS_PARAM_NAME = "objectClass";
	private final static String CHILD_OBJECT_CLASS_PARAM_NAME = "childObjectClasses";
	private final static String RDN_ATTRIBUTE_PARAM_NAME = "rdnAttribute";
	private final static String JAVA_PACKAGE_PARAM_NAME = "javaPackage";
	private final static String CLASSNAME_PARAM_NAME = "className";
	private final static String PARENTCLASSNAME_PARAM_NAME = "parentClassName";
	
	private final static String TYPES_SUB_PACKAGE = "types"; 
	private final static String MAPPERS_SUB_PACKAGE = "mappers";
	private final static String MAPPERS_SUFFIX = "Mapper";
	
	private String generateDirectory;
	private String generatePackage;
	private String[] schemaFilePaths; 
	private boolean generateTypes;
	private boolean generateMappers;
	private String typeTemplateFile;
	private String mapperTemplateFile;

	public void generate() throws Exception {
		SchemaParser dxcSchemaParser = new DxcSchemaParser();
		Schema schema = dxcSchemaParser.parse(schemaFilePaths);
		
		List<ObjectClass> objectClasses = schema.getObjectClasses();
		Map<String, SchemaBinding> schemaBindings = schema.getSchemaBindings();
		Map<String, Attribute> attributes = schema.getAttributes();
		
		Template typeTemplate = getTypeTemplate();
		Template mapperTemplate = getMapperTemplate();
		
		for ( ObjectClass objectClass : objectClasses ) {
			SchemaBinding schemaBinding = schemaBindings.get(objectClass.getName());
			String[] childObjectClasses = getChildObjectClasses(schemaBindings, objectClass.getName(), objectClasses);
			Attribute rdnAttribute = null;
			if ( schemaBinding != null ) {
				rdnAttribute = attributes.get(schemaBinding.getNamedBy().toLowerCase());
			}
			
			Map<String, Object> model = new HashMap<String, Object>();
			model.put(OBJECT_CLASS_PARAM_NAME, objectClass);
			model.put(CHILD_OBJECT_CLASS_PARAM_NAME, childObjectClasses);
			model.put(RDN_ATTRIBUTE_PARAM_NAME, rdnAttribute);
			
			String className =capitalize(objectClass.getName());
			String parentClassName = null;
			if ( objectClass.getSubclassOf() != null ) {
				parentClassName = capitalize(findActualObjectClassName(objectClass.getSubclassOf(), objectClasses));
			}
			
			if ( generateTypes ) {
				generateByTemplate(typeTemplate, model, generatePackage + "." + TYPES_SUB_PACKAGE, className, parentClassName);
			}
			
			if ( generateMappers ) {
				className = className + MAPPERS_SUFFIX;
				if ( objectClass.getSubclassOf() != null ) {
					parentClassName = parentClassName + MAPPERS_SUFFIX;
				}
				generateByTemplate(mapperTemplate, model, generatePackage + "." + MAPPERS_SUB_PACKAGE, className, parentClassName);
			}
		}
	}

	private Template getTypeTemplate() throws IOException {
		if ( typeTemplateFile != null ) {
			return new Template(null, new FileReader(typeTemplateFile), null);
		} else {
			return new Template(null, new InputStreamReader(getClass().getClassLoader().getResourceAsStream("templates/type.fm")), null);
		}
	}
	
	private Template getMapperTemplate() throws IOException {
		if ( mapperTemplateFile != null ) {
			return new Template(null, new FileReader(mapperTemplateFile), null);
		} else {
			return new Template(null, new InputStreamReader(getClass().getClassLoader().getResourceAsStream("templates/mapper.fm")), null);
		}
	}
	
	public void removeOldOutput() {
		if ( generateTypes ) {
			String typesDir = generateDirectory + File.separator + (generatePackage + "." + TYPES_SUB_PACKAGE).replaceAll("\\.", "\\" + File.separator);
			Logger.getInstance().info("Deleting *.java under " + typesDir);
			deleteJavaFilesUnderDir(new File(typesDir));
		}
		
		if ( generateMappers ) {
			String mappersDir = generateDirectory + File.separator + (generatePackage + "." + MAPPERS_SUB_PACKAGE).replaceAll("\\.", "\\" + File.separator);
			Logger.getInstance().info("Deleting *.java under " + mappersDir);			
			deleteJavaFilesUnderDir(new File(mappersDir));
		}
	}
	
	private void deleteJavaFilesUnderDir(File dir) {
		if ( dir.exists() && dir.isDirectory() ) {
			File[] javaFiles = dir.listFiles(new FileFilter() {
				public boolean accept(File file) {
					if ( file.isFile() && file.getName().endsWith(".java")) { 
						return true;
					}
					return false;
				}
			});
			for (File javaFile : javaFiles) {
				javaFile.delete();
			}
		}
	}
	private String[] getChildObjectClasses(
			Map<String, SchemaBinding> schemaBindings, String name, List<ObjectClass> objectClasses) {
		List<String> result = new ArrayList<String>();
		for (String key: schemaBindings.keySet() ) {
			SchemaBinding schemaBinding = schemaBindings.get(key);
			if ( schemaBinding.getAllowableParent().equalsIgnoreCase(name)) {
				//The ldap name that is referred in schema-binding might not
				//match the actual object class name case sensitively.
				result.add(findActualObjectClassName(schemaBinding.getLdapName(), objectClasses));
			}
		}
		return result.toArray(new String[result.size()]);
	}

	private String findActualObjectClassName(String ldapName, List<ObjectClass> objectClasses) {
		for ( ObjectClass objectClass : objectClasses ) {
			if ( objectClass.getName().equalsIgnoreCase(ldapName) ) {
				return objectClass.getName();
			}
		}
		return ldapName;
	}
	
	private void generateByTemplate(Template template, Map<String, Object> model, String javaPackage, String className, String parentClassName) {
		String destDir = generateDirectory + File.separator + javaPackage.replaceAll("\\.", "\\" + File.separator);
		
		model.put(JAVA_PACKAGE_PARAM_NAME, javaPackage);
		model.put(CLASSNAME_PARAM_NAME, className);
		model.put(PARENTCLASSNAME_PARAM_NAME, parentClassName);
		
		String outputFilePath = destDir + File.separator + className + ".java";
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(outputFilePath);
			template.process(model, fileWriter);
			Logger.getInstance().info("Write output to file - " + outputFilePath);
		} catch ( Exception e ) {
			Logger.getInstance().error("Unable to write output to file - " + outputFilePath);
		} finally {
			if ( fileWriter != null ) {
				try {
					fileWriter.close();
				} catch (IOException e) {
					Logger.getInstance().warn("Unable to close the file writer.", e);
				}
			}
		}
	}
	
	private String capitalize(String name) {
		if ( name == null || name.length() == 0 ) {
			return name;
		}
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	
	public String getGenerateDirectory() {
		return generateDirectory;
	}

	public void setGenerateDirectory(String generateDirectory) {
		this.generateDirectory = generateDirectory;
	}

	public String getGeneratePackage() {
		return generatePackage;
	}

	public void setGeneratePackage(String generatePackage) {
		this.generatePackage = generatePackage;
	}

	public String[] getSchemaFilePaths() {
		return schemaFilePaths;
	}

	public void setSchemaFilePaths(String[] schemaFilePaths) {
		this.schemaFilePaths = schemaFilePaths;
	}

	public void setGenerateTypes(boolean generateTypes) {
		this.generateTypes = generateTypes;
	}

	public void setGenerateMappers(boolean generateMappers) {
		this.generateMappers = generateMappers;
	}

	public void setTypeTemplateFile(String typeTemplateFile) {
		this.typeTemplateFile = typeTemplateFile;
	}

	public void setMapperTemplateFile(String mapperTemplateFile) {
		this.mapperTemplateFile = mapperTemplateFile;
	}
}
