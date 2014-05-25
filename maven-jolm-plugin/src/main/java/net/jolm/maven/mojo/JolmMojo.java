package net.jolm.maven.mojo;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import net.jolm.codegen.JolmGenerator;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * The Maven2 Mojo used to generate LDAP Java Beans and Mappers from LDAP Schema.
 * 
 * @author Chunyun Zhao
 * 
 * @requiresProject false
 * @goal generate
 * @phase generate-sources
 */
public class JolmMojo extends AbstractMojo {
	/**
	 * @parameter expression="${schemaDirectory}" default-value="${basedir}/src/main/schemas"
	 * @required
	 */
	protected File schemaDirectory;

	/**
	 * @parameter expression="${includeSchemas}"
	 */
	protected String[] includeSchemas;

	/**
	 * @parameter expression="${excludeSchemas}" 
	 */
	protected String[] excludeSchemas;

	/**
	 * @parameter expression="${generateTypes}" default-value="true"
	 * @required
	 */
	protected boolean generateTypes;
	
	/**
	 * @parameter expression="${generateMappers}" default-value="true"
	 * @required
	 */
	protected boolean generateMappers;
	
	/**
	 * @parameter expression="${generatePackage}"
	 * @required
	 */
	protected String generatePackage;	
	
	/**
	 * @parameter expression="${generateDirectory}" default-value="${basedir}/src/main/java"
	 * @required
	 */
	protected File generateDirectory;


	/**
	 * @parameter expression="${typeTemplateFile}"
	 * @optional
	 */
	protected String typeTemplateFile;
	
	/**
	 * @parameter expression="${mapperTemplateFile}"
	 * @optional
	 */
	protected String mapperTemplateFile;

	/**
	 * @parameter expression="${removeOldOutput}" default-value="false"
	 * @required
	 */
	protected boolean removeOldOutput;

	/**
	 * @parameter expression="${verbose}" default-value="false"
	 * @required
	 */
	protected boolean verbose;
	
	protected String[] schemaFiles;

	/**
	 * @parameter expression="${project}"
	 */
	protected MavenProject project;

	public JolmMojo() {
	}

	public void execute() throws MojoExecutionException {
		processSettings();
		
		if (verbose) {
			logSettings();
		}
		
		if (generateDirectory != null && !generateDirectory.exists()) {
			generateDirectory.mkdirs();
		}
		
		JolmGenerator jolmGenerator = new JolmGenerator();
		jolmGenerator.setGenerateDirectory(generateDirectory.getAbsolutePath());
		jolmGenerator.setGeneratePackage(generatePackage);
		jolmGenerator.setSchemaFilePaths(schemaFiles);
		jolmGenerator.setGenerateTypes(generateTypes);
		jolmGenerator.setGenerateMappers(generateMappers);
		jolmGenerator.setTypeTemplateFile(typeTemplateFile);
		jolmGenerator.setMapperTemplateFile(mapperTemplateFile);
		
		Logger.getInstance().setVerbose(verbose);
		Logger.getInstance().setLog(getLog());
		
		if ( removeOldOutput ) {
			jolmGenerator.removeOldOutput();
		}
		try {
			jolmGenerator.generate();
		} catch (Exception e) {
			throw new MojoExecutionException("Code generation failed.", e);
		}
	}

	private void processSettings() throws MojoExecutionException {
		failIfEmpty("schemaDirectory", schemaDirectory);
		failIfEmpty("generatePackage", generatePackage);
		failIfEmpty("generateDirectory", generateDirectory);
		
		Set<String> schemaFileSet = new LinkedHashSet<String>();
		
		if ( isEmpty(includeSchemas ) ) {
			getLog().info("includeSchemas is not defined. Will load *.* under " + schemaDirectory);
			for ( File file : schemaDirectory.listFiles() ) {
				if ( file.isFile() ) {
					schemaFileSet.add(file.getAbsolutePath());
				}
			}
		} else {
			for (String includeSchema : includeSchemas ) {
				schemaFileSet.add(getSchemaAbsolutePath(includeSchema));
			}
		}
		
		if ( !isEmpty(excludeSchemas) ) {
			for ( String excludeSchema : excludeSchemas ) {
				schemaFileSet.remove(getSchemaAbsolutePath(excludeSchema));
			}
		}
		
		schemaFiles = schemaFileSet.toArray(new String[schemaFileSet.size()]);
		
		getLog().info("Loading LDAP Schemas: " + Arrays.toString(schemaFiles));
	}
	
	private String getSchemaAbsolutePath(String schema) {
		return schemaDirectory.getAbsolutePath() + File.separator + schema;
	}

	private void failIfEmpty(String settingName, Object settingValue) throws MojoExecutionException {
		if ( isEmpty(settingValue) ) {
			logSettings();
			throw new MojoExecutionException(String.format("The <%s> setting must be defined.", settingName));
		}
	}
	
	private boolean isEmpty(Object o) {
		if ( o == null ) {
			return true;
		} else if (o instanceof String) {
			return ((String)o).length() == 0;
		} else if ( o.getClass().isArray() ) {
			return ((Object[])o).length == 0;
		} else if ( o instanceof Collection ) {
			return ((Collection<?>)o).size() == 0;
		} else {
			return false;
		}
	}
	
	private void logSettings() {
		getLog().info("schemaDirectory: " + schemaDirectory);
		getLog().info("includeSchemas: " + Arrays.toString(includeSchemas));
		getLog().info("excludeSchemas: " + Arrays.toString(excludeSchemas));
		getLog().info("generatePackage: " + generatePackage);
		getLog().info("generateTypes: " + generateTypes);
		getLog().info("generateMappers: " + generateMappers);
		getLog().info("generateDirectory: " + generateDirectory);
		getLog().info("removeOldOutput: " + removeOldOutput);
		getLog().info("verbose: " + verbose);
	}
}
