/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * <b>IMPORTANT: Running this application will delete all data in a user's local (unit test) database.</b>
 * 
 * <p>This is the main utility class that can be executed via command line or within an IDE to generate and load QCLive test data into a user's 
 * local (unit test) database for a specific disease.
 *  
 * <p>Usage information if running from the command line:
 * <blockquote>
 * <pre>
 * usage: java -jar qclive-test-data-generator.jar -a [archive-name]
 * -?,--help                    Displays usage information.
 * -a,--archive_name <arg>      The name of the archive (without file
 *                              extension) to generate test data for.
 * -f,--sql_script_file <arg>   The absolute path to a SQL script file to
 *                              run. If used in conjunction with the -a
 *                              option, the provided script will run after
 *                              test data for an archive has been loaded.
 *                              The -s option denoting the databse schema to
 *                              run script file against is also required
 *                              when using this option
 * -s,--schema <arg>            The database schema to execute the SQL
 *                              script file against. Supported values are
 *                              local-common and local-disease
 * 
 * 
 * </pre>
 * </blockquote>
 *
 *
 * @author Matt Nicholls
 *         Last updated by: nichollsmc
 * @version 
 */
public class QCLiveTestDataGenerator {

	/** Logger */
	private static final Logger logger = Logger.getLogger(QCLiveTestDataGenerator.class);
	
	/** Spring application context file */
    private static final String APP_CONTEXT_FILE_NAME = "applicationContext.xml";

    /** Control schema name */
    private static final String CONTROL_SCHEMA_NAME = "CNTL";

    /** Data sources that will be used to create the insert statements for the test data. */
	private JdbcTemplate dccCommonDevJdbcTemplate;
	private JdbcTemplate diseaseDevJdbcTemplate;
	
	/** Local data sources that will be used to load the test data */
	private JdbcTemplate dccCommonLocalJdbcTemplate;
	private JdbcTemplate diseaseLocalJdbcTemplate;
	
	/** List of SQL scripts on the classpath used to initialize the local test database */
	private Map<String, SchemaType> initSQLScriptClassPathLocations;
	
	/** Locations of the SQL scripts used for generating insert statements for test data */
	private String dccCommonDevSQLInsertScriptFileName;
	private String diseaseDevRefDataSQLInsertScriptFileName;
	private String diseaseDevSQLInsertScriptFileName;
	private String barcodeSQLInsertScriptFileName;
    private String cntlDevRefDataSQLInsertScriptFileName;
	
	/** Regex pattern for validating the archive names */
	private final Pattern ARCHIVE_NAME_PATTERN = Pattern.compile("([a-zA-Z0-9\\.\\-]+)_([a-zA-Z0-9]+)\\.([\\w\\-]+)\\.([\\w\\-]+)\\.(\\d+)\\.(\\d+)\\.(\\d+)");
	
	/** Regex pattern for matching SQL statements read from a file */
	private final String[] SQL_OPS = new String[]{"select", "insert", "update", "delete"};
	private final Pattern SQL_STMT_PATTERN = Pattern.compile(String.format("(%s).+;", StringUtils.join(SQL_OPS, "|")));
	private final String[] SQL_STMT_EXCLUSION_STRINGS = new String[]{"commit", "dual"};
	private final Pattern SQL_STMT_EXCLUSION_PATTERN = Pattern.compile(String.format(".*(%s).*;", StringUtils.join(SQL_STMT_EXCLUSION_STRINGS, "|")));
	
	/** Replacement strings for SQL statements */
	private final String CENTER_REPLACMENT_STRING = "replace_center";
	private final String DISEASE_ABBR_REPLACMENT_STRING = "replace_disease";
	private final String PLATFORM_REPLACMENT_STRING = "replace_platform";
	
	/** String array of known test database host names */
	private final String[] KNOWN_TEST_DB_HOSTNAMES = new String[]{"ncias-c406"};
	
	/** String array of known platforms associated with BCR archives **/
	private final String[] KNOWN_BCR_PLATFORMS = new String[]{"bio"};
	
	/**
	 * Orchestrates the generation and loading of test data using the archive name for a disease.
	 * 
	 * @param archiveName - a string representing the archive name of a disease (without the extension)
	 * @throws IOException if an error occurs when reading in the SQL scripts used to generate inserts for test data
	 * @throws ParseException if an error occurs while parsing the archive name
	 * @throws SQLException if an error occurs while executing arbitrary SQL scripts
	 */
	public void generateTestData(final String archiveName) throws ParseException, IOException, SQLException  {
		
		String centerDomainName = null;
		String diseaseAbbr = null;
		String platformName = null;
		
		logger.debug("Parsing archive name '" + archiveName + "' for center name, disease abbreviation, and platform name...");
		
		// Assert that the archive name follows the correct pattern
		if(!(ARCHIVE_NAME_PATTERN.matcher(archiveName)).matches())
			throw new ParseException("Archive name '" + archiveName + "' does not match the pattern of '(center name)_(disease).(platform).(archivetype).(batch).(revision).(series)'");
		
		// Parse the archive name for the center
		String[] parsedForCenter = archiveName.split("_", 2);
		centerDomainName = parsedForCenter[0];
		
		// Parse for the disease abbreviation and platform name
		String[] parsedForDiseaseAndPlatform = parsedForCenter[1].split("\\.");
		diseaseAbbr = parsedForDiseaseAndPlatform[0];
		platformName = parsedForDiseaseAndPlatform[1];
		
		// Check to ensure that the center domain name, disease abbreviation and platform name parsed from the provided archive name have 
		// have associated data in the dev common database schema. If they do, load the test data, otherwise log an error
		if(recordsExist("center", "domain_name", centerDomainName, dccCommonDevJdbcTemplate) 
				&& recordsExist("disease", "disease_abbreviation", diseaseAbbr.toUpperCase(), dccCommonDevJdbcTemplate) 
				&& recordsExist("platform", "platform_name", platformName, dccCommonDevJdbcTemplate)) {
			
			logger.info("Initializing local database schemas...");
			
			// Initialize local database
			initializeLocalDatabase();
			
			logger.info("Generating and loading test data...");
			
			// Generate the insert statements for all required test data and execute them against the users local test DB
			generateAndLoadCommonTestData();
			generateAndLoadDiseaseRefTestData(diseaseAbbr);

			// Only load barcode test data for non-BCR archives
			if(!isPlatformForBCR(platformName))
				generateAndLoadBarcodeTestData(centerDomainName, diseaseAbbr, platformName);
			else
				logger.warn("Archive was identified as a BCR archive, barcode test data will not be loaded.");
			
			generateAndLoadDiseaseTestData(diseaseAbbr);

            // Load control specific test data only when the archive is not a CNTL archive.
            // The reason is that when the disease archive is CNTL, then all the data needed will be pulled from the CNTL schema
            // but when a non-CNTL archive is loaded the control data has to be inserted manually as it does not come from other disease schemas
            // (CNTL data might be needed if a non-CNTL archive contains control data).
            if(!CONTROL_SCHEMA_NAME.equals(diseaseAbbr)) {
                loadCNTLTestData();
            }
		}
		else
			throw new ParseException("No information exists for archive name '" + archiveName + "'.");
		
		logger.info("Test data generation and load completed successfully.");
	}

    /**
	 * Utility method for checking whether or not records exist for specific column within a table of a given database.
	 * 
	 * @param tableName - the table name to query
	 * @param columnName - the column name to query
	 * @param columnValue - the column value to query
	 * @param jdbcTemplate - the data source that defines the database to run the query against
	 * @return true if records exist, false if not records are found
	 */
	private boolean recordsExist(final String tableName, final String columnName, final String columnValue, JdbcTemplate jdbcTemplate) {
		
		logger.debug("Checking if records exist in table '" + tableName + "' for column '" + columnName + "' with value '" + columnValue + "'");
		
		// Query for the number records that exist using the provided parameters
		final int results = 
			jdbcTemplate.queryForInt("select count(0) from " + tableName + " where " + columnName + " = '" + columnValue + "'");
		
		// If the result from the query is greater than 0, return true, otherwise return false
		if(results > 0) {
			logger.debug("Query returned '" + results + "' result(s), returning true...");
			return true;
		}
		else {
			logger.debug("Query did not return any results, returning false...");
			return false;
		}
	}
	
	/**
	 * Initializes a local database using the SQL scripts set within {@link QCLiveTestDataGenerator#initSQLScriptClassPathLocations}.
	 * 
	 * <p>Typically, SQL scripts will be set within the Spring configuration file (e.g. applicationContext.xml), however they can
	 * also be set programatically via the {@link QCLiveTestDataGenerator#setInitSQLScriptClassPathLocations(Map)} method.
	 * 
	 * @throws IOException if an error occurs while processing a SQL script file resource
	 * @throws SQLException if an error occurs while executing a SQL script
	 */
	private void initializeLocalDatabase() throws IOException, SQLException {

		Resource sqlScriptFileResource = null;
		SchemaType schemaType = null;
		
		// Iterate through each entry in the map and execute the initialization scripts
		for(String scriptLocation : initSQLScriptClassPathLocations.keySet()) {
			
			// Get classpath resource reference to the SQL script file
			sqlScriptFileResource = new ClassPathResource(scriptLocation);
			
			// Get the schema type value keyed by the SQL script file
			schemaType = initSQLScriptClassPathLocations.get(scriptLocation);
			
			// Execute the SQL script based on its schema type
			executeSQLScriptFile(schemaType, sqlScriptFileResource);
		}
	}
	
	/**
	 * Checks the provided platform name associated with an archive and determines whether or not it is a
	 * BCR archive.
	 * 
	 * @param platformName - archive platform name
	 * @return true if the platform name is associated with BCR archive, otherwise false
	 */
	private boolean isPlatformForBCR(String platformName) {
		for(String knownBCRPlatform : KNOWN_BCR_PLATFORMS)
			if(platformName.equalsIgnoreCase(knownBCRPlatform))
				return true;
		
		return false;
	}
	
	/**
	 * Executes an arbitrary SQL script file for a specific database schema.
	 * 
	 * <p>The first parameter for this method must be one of the supported {@link SchemaType}s.
	 * 
	 * <p>The second parameter is a {@link Resource} that points to a SQL script file resource (e.g. {@link FileSystemResource}). 
	 * 
	 * @param schemaType - a {@link SchemaType} representing the database schema to run a SQL script file against
	 * @param sqlScriptFileResource - the SQL script file resource to be executed
	 * @throws IOException if the SQL script file resource is not readable
	 * @throws SQLException if an error occurs while executing a SQL script
	 */
	public void executeSQLScriptFile(final SchemaType schemaType, final Resource sqlScriptFileResource) throws IOException, SQLException {
		
		logger.info("Executing SQL script file '" + sqlScriptFileResource + "' for schema '" + schemaType + "'");
		
		List<String> sqlStmts = null;
		
		// Check the provided parameters to ensure that they are not null, otherwise throw an exception
		if(schemaType != null && sqlScriptFileResource != null) {
			
			// If the SQL script file resource is not readable, throw an exception
			if(!sqlScriptFileResource.isReadable())
				throw new IOException("SQL script file resource '" + sqlScriptFileResource + "' is not readable.");
			
			// Get the SQL statements from the SQL file
			sqlStmts = getSQLStmtsToLowerCaseFromFile(sqlScriptFileResource.getURL());
            for(String sql: sqlStmts) {
                System.out.println(sql+";");
            }
			try{
			if(schemaType.equals(SchemaType.LOCAL_COMMON)) 
				dccCommonLocalJdbcTemplate.batchUpdate(sqlStmts.toArray(new String[]{}));
			else if(schemaType.equals(SchemaType.LOCAL_DISEASE))
				diseaseLocalJdbcTemplate.batchUpdate(sqlStmts.toArray(new String[]{}));
            }catch(Exception e){
               e.printStackTrace();
            }
		}
		else
			throw new NullPointerException("Cannot execute SQL script file for schema '" + schemaType + "' and file '" + sqlScriptFileResource + "'.");
		
		logger.info("Done executing SQL script file.");
	}
	
	/**
	 * Populates a local unit test database with common test data for testing QCLive.
	 * 
	 * @throws IOException if an error occurs while reading the SQL script file that contains the insert generation statements
	 */
	private void generateAndLoadCommonTestData() throws IOException {
		
		logger.info("Loading common test data...");
		
		// Get reference to SQL script file used to create the insert statements for common test data
		final URL sqlScriptFileNameURL = new ClassPathResource(dccCommonDevSQLInsertScriptFileName).getURL();
		
		// Generate the insert statements using the data source referenced by dccCommonDevJdbcTemplate and the SQL script above
		final List<String> insertsForCommonTestData = new ArrayList<String>();
		final List<String> sqlInsertGenStmts = getSQLStmtsToLowerCaseFromFile(sqlScriptFileNameURL);
		for(String insertGenStmt : sqlInsertGenStmts) {
			insertsForCommonTestData.addAll(dccCommonDevJdbcTemplate.queryForList(insertGenStmt, String.class));
		}
		
		// Log the generated common test data insert statements if debug is enabled
		if(logger.isDebugEnabled()) {
			logGenSQLStmtsToDebug("common", insertsForCommonTestData);
		}
		
		// If the list of generated SQL statements is not empty execute the insert statements against the users local common test 
		// schema referenced by dccCommonLocalJdbcTemplate, otherwise do nothing and log a warning statement
        for(String sql: insertsForCommonTestData) {
        System.out.println( sql);
        }
		if(insertsForCommonTestData.isEmpty())
			logger.warn("No insert statements were generated using SQL script file '" + sqlScriptFileNameURL + "'. " +
					"Common test data will not be loaded.");
		else {
			dccCommonLocalJdbcTemplate.batchUpdate(stripEndingSemicolonFromSQLStmts(insertsForCommonTestData).toArray(new String[]{}));
			logger.info("Common test data loaded successfully.");
		}
	}
	
	/**
	 * Populates a local unit test database with disease reference test data for testing QCLive.
	 * 
	 * @param diseaseAbbr - a string representing the abbreviated disease name
	 * @throws IOException if an error occurs while reading the SQL script file that contains the insert generation statements
	 */
	private void generateAndLoadDiseaseRefTestData(final String diseaseAbbr) throws IOException {
		
		logger.info("Loading disease reference test data...");
		
		// Get reference to SQL script file used to create the insert statements for disease reference test data
		final URL sqlScriptFileNameURL = new ClassPathResource(diseaseDevRefDataSQLInsertScriptFileName).getURL();
		
		// Create the map of replacement strings that will be replaced for each statement read from the script file
		final Map<String, String> replacementStrings = new HashMap<String, String>();
		replacementStrings.put(DISEASE_ABBR_REPLACMENT_STRING, diseaseAbbr);
		
		// Generate the insert statements using the data source referenced by dccCommonDevJdbcTemplate and the SQL script above
		final List<String> insertsForDiseaseRefTestData = new ArrayList<String>();
		final List<String> sqlInsertGenStmts = replaceStringsForStmts(replacementStrings, getSQLStmtsToLowerCaseFromFile(sqlScriptFileNameURL));
		for(String insertGenStmt : sqlInsertGenStmts) {
			insertsForDiseaseRefTestData.addAll(dccCommonDevJdbcTemplate.queryForList(insertGenStmt, String.class));
		}
		
		// Log the generated disease reference test data insert statements if debug is enabled
		if(logger.isDebugEnabled()) {
			logGenSQLStmtsToDebug("disease reference", insertsForDiseaseRefTestData);
		}

		// If the list of generated SQL statements is not empty execute the insert statements against the users local common test 
		// schema referenced by diseaseLocalJdbcTemplate, otherwise do nothing and log a warning statement
		if(insertsForDiseaseRefTestData.isEmpty())
			logger.warn("No insert statements were generated using SQL script file '" + sqlScriptFileNameURL + "'. " +
					"Disease reference test data will not be loaded.");
		else {
			diseaseLocalJdbcTemplate.batchUpdate(stripEndingSemicolonFromSQLStmts(insertsForDiseaseRefTestData).toArray(new String[]{}));
			logger.info("Disease reference test data loaded successfully.");
		}
	}

	/**
	 * Populates a local unit test database with barcode test data for testing QCLive.
	 * 
	 * @param centerDomainName - a string representing the center domain name
	 * @param diseaseAbbr - a string representing the abbreviated disease name
	 * @param platformName - a string representing the platform name
	 * @throws IOException if an error occurs while reading the SQL script file that contains the insert generation statements
	 */
	private void generateAndLoadBarcodeTestData(final String centerDomainName, final String diseaseAbbr, final String platformName) throws IOException {
		
		logger.info("Loading barcode test data for center '" + centerDomainName + "', disease abbreviation '" + diseaseAbbr + 
				"', and platform '" + platformName + "'...");
		
		// Get reference to SQL script file used to create the insert statements for barcode test data
		final URL sqlScriptFileNameURL = new ClassPathResource(barcodeSQLInsertScriptFileName).getURL();
		
		// Create the map of replacement strings that will be replaced for each statement read from the script file
		final Map<String, String> replacementStrings = new HashMap<String, String>();
		replacementStrings.put(CENTER_REPLACMENT_STRING, centerDomainName);
		replacementStrings.put(DISEASE_ABBR_REPLACMENT_STRING, diseaseAbbr);
		replacementStrings.put(PLATFORM_REPLACMENT_STRING, platformName);
		
		// Generate the insert statements using the data source referenced by dccCommonDevJdbcTemplate and the SQL script above
		final List<String> insertsForBarcodeTestData = new ArrayList<String>();
		final List<String> sqlInsertGenStmts = replaceStringsForStmts(replacementStrings, getSQLStmtsToLowerCaseFromFile(sqlScriptFileNameURL));
		for(String insertGenStmt : sqlInsertGenStmts) {
			insertsForBarcodeTestData.addAll(dccCommonDevJdbcTemplate.queryForList(insertGenStmt, String.class));
		}
		
		// Log the generated barcode test data insert statements if debug is enabled
		if(logger.isDebugEnabled()) {
			logGenSQLStmtsToDebug("barcode", insertsForBarcodeTestData);
		}

		// If the list of generated SQL statements is not empty execute the insert statements against the users local common test 
		// schema referenced by diseaseLocalJdbcTemplate, otherwise do nothing and log a warning statement
		if(insertsForBarcodeTestData.isEmpty())
			logger.warn("No insert statements were generated using SQL script file '" + sqlScriptFileNameURL + "'. " +
					"Barcode test data will not be loaded.");
		else {
			dccCommonLocalJdbcTemplate.batchUpdate(stripEndingSemicolonFromSQLStmts(insertsForBarcodeTestData).toArray(new String[]{}));
			logger.info("Barcode test data loaded successfully.");
		}
	}
	
	/**
	 * Populates a local unit test database with specific disease test data for testing QCLive.
	 * 
	 * @param diseaseAbbr - a string representing the abbreviated disease name
	 * @throws IOException if an error occurs while reading the SQL script file that contains the insert generation statements
	 */
	private void generateAndLoadDiseaseTestData(final String diseaseAbbr) throws IOException {
		
		logger.info("Loading disease specific test data for disease abbreviation '" + diseaseAbbr + "'...");
		
		// Get reference to SQL script file used to create the insert statements for disease test data
		final URL sqlScriptFileNameURL = new ClassPathResource(diseaseDevSQLInsertScriptFileName).getURL();
		
		// Generate the insert statements using the data source referenced by dieseaseDevJdbcTemplate and the SQL script above
		final List<String> insertsForDiseaseTestData = new ArrayList<String>();
		final List<String> sqlInsertGenStmts = getSQLStmtsToLowerCaseFromFile(sqlScriptFileNameURL);
		final String replaceFromString = "from ";
		final String fromStringReplacement = "from tcga" + diseaseAbbr + ".";
		for(String insertGenStmt : sqlInsertGenStmts) {
			// Modify the ending "from " string for each insert statement to specify the appropriate disease schema before executing a query
			StringBuilder diseaseInsertGenStmt = new StringBuilder(insertGenStmt);
			int fromStringIndex = insertGenStmt.lastIndexOf(replaceFromString);
			diseaseInsertGenStmt.replace(fromStringIndex, fromStringIndex + replaceFromString.length(), fromStringReplacement);
			insertsForDiseaseTestData.addAll(diseaseDevJdbcTemplate.queryForList(diseaseInsertGenStmt.toString(), String.class));
		}
		
		// Log the generated disease test data insert statements if debug is enabled
		if(logger.isDebugEnabled()) {
			logGenSQLStmtsToDebug("disease specific", insertsForDiseaseTestData);
		}
		
		// If the list of generated SQL statements is not empty execute the insert statements against the users local disease test
		// schema referenced by diseaseLocalJdbcTemplate, otherwise do nothing and log a warning statement
		if(insertsForDiseaseTestData.isEmpty())
			logger.warn("No insert statements were generated using SQL script file '" + sqlScriptFileNameURL + "'. " +
					"Disease specific test data will not be loaded.");
		else {
			diseaseLocalJdbcTemplate.batchUpdate(stripEndingSemicolonFromSQLStmts(insertsForDiseaseTestData).toArray(new String[]{}));
			logger.info("Disease specific test data loaded successfully.");
		}
	}

    /**
     * Load data specific to CNTL schema
     */
    private void loadCNTLTestData() throws IOException {

        logger.info("Loading CNTL specific test data...");

        // Get reference to SQL script file used to create the insert statements for CNTL reference test data
        final URL sqlScriptFileNameURL = new ClassPathResource(getCntlDevRefDataSQLInsertScriptFileName()).getURL();
        final List<String> sqlInsertGenStmts = getSQLStmtsFromFile(sqlScriptFileNameURL, false);

        if(!sqlInsertGenStmts.isEmpty()) {
            diseaseLocalJdbcTemplate.batchUpdate(sqlInsertGenStmts.toArray(new String[]{}));
        }

        logger.info("CNTL specific test data loaded successfully.");
    }

	/**
	 * Loads SQL statements from a SQL script file and returns a list containing strings for each statement. 
	 * 
	 * <p>This method also runs the {@link QCLiveTestDataGenerator#stripEndingSemicolonFromSQLStmts(List)} method 
	 * on the returned list.
	 * 
	 * <p>All statements included in the returned list are lower case.
	 * 
	 * @param sqlScriptFileURL - the SQL script file to retrieve SQL statements from
	 * @return a list containing strings that represent each SQL statement in the provided SQL script file
	 * @throws IOException if an error occurs while reading a SQL script file
	 */
	private List<String> getSQLStmtsToLowerCaseFromFile(final URL sqlScriptFileURL) throws IOException {
        return getSQLStmtsFromFile(sqlScriptFileURL, true);
	}

    /**
     * Loads SQL statements from a SQL script file and returns a list containing strings for each statement.
     *
     * <p>This method also runs the {@link QCLiveTestDataGenerator#stripEndingSemicolonFromSQLStmts(List)} method
     * on the returned list.
     *
     * @param sqlScriptFileURL - the SQL script file to retrieve SQL statements from
     * @param toLowerCase wether to convert the SQL statements to lower case
     * @return a list containing strings that represent each SQL statement in the provided SQL script file
     * @throws IOException
     */
    private List<String> getSQLStmtsFromFile(final URL sqlScriptFileURL,
                                             final boolean toLowerCase) throws IOException {

        logger.debug("Retrieving SQL statements from file '" + sqlScriptFileURL + "'");

        InputStream inputStream = sqlScriptFileURL.openStream();
        try {
            // Read in all the lines from the SQL script file and remove all lines that do begin with "select", "delete", or "truncate" and end with ";", and
            // do not include commit.

            List<String> sqlStatements = new ArrayList<String>();
            List<String> sqlStatementsFromFile = IOUtils.readLines(inputStream);
            String trimmedStatement, trimmedStatementToLowerCase;
            for(String statement : sqlStatementsFromFile) {
                trimmedStatement = statement.trim();
                trimmedStatementToLowerCase = trimmedStatement.toLowerCase();
                if(SQL_STMT_PATTERN.matcher(trimmedStatementToLowerCase).matches() && !SQL_STMT_EXCLUSION_PATTERN.matcher(trimmedStatementToLowerCase).matches()) {
                    sqlStatements.add(toLowerCase?trimmedStatementToLowerCase:trimmedStatement);
                }
            }

            return stripEndingSemicolonFromSQLStmts(sqlStatements);
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
	 * Utility method that iterates through <code>java.util.List</code> of <code>java.lang.String</code> SQL statements
	 * and replaces each string with the value from the provided <code>java.util.Map<String, String></code>. 
	 * 
	 * <p>The replacement strings within statement should match the keys within the provided map.
	 * 
	 * @param replaceStrings - a map that contains the replacement strings, where the keys are the replacement strings 
	 * and the values are the new strings for replacing
	 * @param statements - a list of SQL statements
	 * @return a new list of SQL statements with all the replacement strings replaced for each statement
	 */
	private List<String> replaceStringsForStmts(final Map<String, String> replaceStrings, final List<String> statements) {
		
		final List<String> stmtsWithReplacedStrings = new ArrayList<String>();
		
		// For each replacement entry in the map, replace the corresponding value(s) for each statement in the list
		for(String statement : statements) {
			String stmt = new String(statement);
			for(String replaceStringKey : replaceStrings.keySet()) {
				stmt = stmt.replaceAll(replaceStringKey.toLowerCase(), "'" + replaceStrings.get(replaceStringKey) + "'");
			}
			stmtsWithReplacedStrings.add(stmt);
		}
		
		return stmtsWithReplacedStrings;
	}
	
	/**
	 * Utility method that iterates through a <code>java.util.List</code> of <code>java.lang.String</code> SQL statements 
	 * (e.g. retrieved from a SQL script file) and strips off the ending semicolon. This is necessary when using JdbcTemplate 
	 * for queries since it adds them automatically.
	 * 
	 * @param statements - list of SQL statements
	 * @return a new list of SQL statements with the ending semicolon stripped off each statement
	 */
	private List<String> stripEndingSemicolonFromSQLStmts(final List<String> statements) {
	
		List<String> statementsWithoutEndingSemiColons = new ArrayList<String>();
		for(String statement : statements) {
			StringBuilder stmt = new StringBuilder(statement);
			stmt.setCharAt(statement.lastIndexOf(';'), ' ');
			statementsWithoutEndingSemiColons.add(stmt.toString());
		}
		
		return statementsWithoutEndingSemiColons;
	}
	
	/**
	 * Convenience method for logging generated SQL statements to the debug log.
	 * 
	 * @param qualifier - the qualifier that specifies what the statements being logged belong to 
	 * @param statements - a list of statements to write to the debug log
	 */
	private void logGenSQLStmtsToDebug(final String qualifier, final List<String> statements) {
			StringBuilder generatedInserts = new StringBuilder();
			generatedInserts.append("\n\nExecuting '" + statements.size() + "' " + qualifier + " test data insert statements against local database\n");
			generatedInserts.append("\n---- Generated " + qualifier + " test data insert statements ----\n");
			for(String insertStatement : statements)
				generatedInserts.append(insertStatement + "\n");
			logger.debug(generatedInserts.toString());
	}
	
	/**
	 * Static utility method to print the usage information to Standard.out for the {@link QCLiveTestDataGenerator}.
	 */
	public static void displayHelp() {
		new HelpFormatter().printHelp("java -jar qclive-test-data-generator.jar -a [archive-name]", CommandLineOptionType.getOptions());
	}
	
	/**
	 * Utility method for checking the user name provided as part of the database connection
	 * credentials to assert whether it is a test account or not.
	 * 
	 * @param username - the user name to check
	 * @return true if the user name is associated with a known test account, false otherwise
	 */
	private boolean isTestAccount(String username) {
		if(username.toLowerCase().contains("test"))
			return true;
		else
			return false;
	}
	
	/**
	 * Utility method for checking the database connection URL provided as part of the database connection
	 * credentials to assert whether it points to a test database host name or not.
	 * 
	 * @param url - the URL connection string to check
	 * @return true if the URL contains a host name that is associated with a known test database, false otherwise
	 */
	private boolean isTestDBHost(String url) {
		for(String hostName : KNOWN_TEST_DB_HOSTNAMES)
			if(url.toLowerCase().contains(hostName))
				return true;
		
		// Return false if no match is found
		return false;
	}
	
	/**
	 * Convenience method for retrieving the list of known test database host names as comma delimited string.
	 * 
	 * @return a comma delimited string of known test database host names
	 */
	public String getTestDBHostNames() {
		StringBuilder hostNamesBuilder = new StringBuilder();
		String hostNames = null;
		
		for(String hostName : KNOWN_TEST_DB_HOSTNAMES)
			hostNamesBuilder.append(hostName + ',');
		
		// Strip off the ending comma and return the result
		hostNames = hostNamesBuilder.toString();
		return hostNames.substring(0, hostNames.lastIndexOf(',') - 1);
	}
	
	@Autowired
    public void setDccCommonDevJdbcTemplate(final JdbcTemplate dccCommonDevJdbcTemplate) {
		
		final DriverManagerDataSource dataSource = (DriverManagerDataSource) dccCommonDevJdbcTemplate.getDataSource();
		final String username = dataSource.getUsername();
		if(isTestAccount(username))
			throw new IllegalArgumentException("Test account '" + username + "' is not permitted for database connection property 'dccCommonDevJdbcTemplate'");
			
        this.dccCommonDevJdbcTemplate = dccCommonDevJdbcTemplate;
	}
	
	@Autowired
    public void setDiseaseDevJdbcTemplate(final JdbcTemplate diseaseDevJdbcTemplate) {
		
		final DriverManagerDataSource dataSource = (DriverManagerDataSource) diseaseDevJdbcTemplate.getDataSource();
		final String username = dataSource.getUsername();
		if(isTestAccount(username))
			throw new IllegalArgumentException("Test account '" + username + "' is not permitted for database connection property 'diseaseDevJdbcTemplate'");
		
        this.diseaseDevJdbcTemplate = diseaseDevJdbcTemplate;
	}
	
	@Autowired
    public void setDccCommonLocalJdbcTemplate(final JdbcTemplate dccCommonLocalJdbcTemplate) {
		
		final DriverManagerDataSource dataSource = (DriverManagerDataSource) dccCommonLocalJdbcTemplate.getDataSource();
		final String url = dataSource.getUrl();
		if(!isTestDBHost(url))
			throw new IllegalArgumentException("Connection URL '" + url + "' for datasource defined by 'dccCommonLocalJdbcTemplate' does not point to a " +
					"known test database host. Expected one of [" + getTestDBHostNames() + "]");
		
        this.dccCommonLocalJdbcTemplate = dccCommonLocalJdbcTemplate;
    }
	
	@Autowired
    public void setDiseaseLocalJdbcTemplate(final JdbcTemplate diseaseLocalJdbcTemplate) {
		
		final DriverManagerDataSource dataSource = (DriverManagerDataSource) diseaseLocalJdbcTemplate.getDataSource();
		final String url = dataSource.getUrl();
		if(!isTestDBHost(url))
			throw new IllegalArgumentException("Connection URL '" + url + "' for datasource defined by 'diseaseLocalJdbcTemplate' does not point to a " +
					"known test database host. Expected one of [" + getTestDBHostNames() + "]");
		
        this.diseaseLocalJdbcTemplate = diseaseLocalJdbcTemplate;
	}
	
	@Autowired
	public void setDccCommonDevSQLInsertScriptFileName(@Qualifier(value="dccCommonDevSQLInsertScriptFileName") final String dccCommonDevSQLInsertScriptFileName) {
		this.dccCommonDevSQLInsertScriptFileName = dccCommonDevSQLInsertScriptFileName;
	}
	
	@Autowired
	public void setDiseaseDevRefDataSQLInsertScriptFileName(@Qualifier(value="diseaseDevRefDataSQLInsertScriptFileName") final String diseaseDevRefDataSQLInsertScriptFileName) {
		this.diseaseDevRefDataSQLInsertScriptFileName = diseaseDevRefDataSQLInsertScriptFileName;
	}
	
	@Autowired
	public void setDiseaseDevSQLInsertScriptFileName(@Qualifier(value="diseaseDevSQLInsertScriptFileName") final String diseaseDevSQLInsertScriptFileName) {
		this.diseaseDevSQLInsertScriptFileName = diseaseDevSQLInsertScriptFileName;
	}
	
	@Autowired
	public void setBarcodeSQLInsertScriptFileName(@Qualifier(value="barcodeSQLInsertScriptFileName") final String barcodeSQLInsertScriptFileName) {
		this.barcodeSQLInsertScriptFileName = barcodeSQLInsertScriptFileName;
	}
	
	@javax.annotation.Resource(name="initSQLScriptClassPathLocations")
	public void setInitSQLScriptClassPathLocations(final Map<String, String> initSQLScriptClassPathLocations) {
		this.initSQLScriptClassPathLocations = new HashMap<String, SchemaType>();
		for(String sqlScriptLocation : initSQLScriptClassPathLocations.keySet()) {
			this.initSQLScriptClassPathLocations.put(sqlScriptLocation, SchemaType.valueOf(initSQLScriptClassPathLocations.get(sqlScriptLocation).toUpperCase()));
		}
	}

    public String getCntlDevRefDataSQLInsertScriptFileName() {
        return cntlDevRefDataSQLInsertScriptFileName;
    }

    @Autowired
    public void setCntlDevRefDataSQLInsertScriptFileName(@Qualifier(value="cntlDevRefDataSQLInsertScriptFileName") final String cntlDevRefDataSQLInsertScriptFileName) {
        this.cntlDevRefDataSQLInsertScriptFileName = cntlDevRefDataSQLInsertScriptFileName;
    }

	/**
	 * Main entry point for the application. Configures the Spring context and calls the {@link QCLiveTestDataGenerator}
	 * bean to load and generate test data for a specific archive name.
	 * 
	 * @param args - list of arguments to be passed to the {@link QCLiveTestDataGenerator} bean
	 */
	public static void main(final String[] args) {
		
		// Display help if no arguments are provided, otherwise parse the arguments
		if(args.length == 0)
			displayHelp();
		else {
			try {
				// Parse the command line arguments 
				final CommandLine commandLine = new GnuParser().parse(CommandLineOptionType.getOptions(), args);
				
				// If the command line instance contains the -? (--help) option display help, otherwise call the QCLiveTestDataGenerator
				// to process the command line arguments
				if(commandLine.hasOption(CommandLineOptionType.HELP.name().toLowerCase())) {
					displayHelp();
				}
				else {
					final String archiveNameOption = CommandLineOptionType.ARCHIVE_NAME.getOptionValue().getOpt();
					final String sqlScriptFileOption = CommandLineOptionType.SQL_SCRIPT_FILE.getOptionValue().getOpt();
					final String schemaOption = CommandLineOptionType.SCHEMA.getOptionValue().getOpt();
					
					// Initialize the Spring context
					final ApplicationContext appCtx = new ClassPathXmlApplicationContext(APP_CONTEXT_FILE_NAME);
					
					// Retrieve the QCLiveTestDataGenerator from the Spring context
					final QCLiveTestDataGenerator qcLiveTestDataGenerator = (QCLiveTestDataGenerator) appCtx.getBean("qcLiveTestDataGenerator");
					
					// Get the archive name from the command line argument(s) (if provided) and generate the test data
					if(commandLine.hasOption(archiveNameOption)) {
						qcLiveTestDataGenerator.generateTestData(commandLine.getOptionValue(archiveNameOption));
					}
					
					// If the SQL script file and schema options are provided, execute the script
					if(commandLine.hasOption(sqlScriptFileOption)) {
						if(commandLine.hasOption(schemaOption)) {
							// Try to resolve the schema type from the provided schema name. If it cannot be resolved, throw an exception that
							// indicates the supported schema types
							final String schemaOptionValue = commandLine.getOptionValue(schemaOption);
							SchemaType schemaTpye = null;
							try {
								schemaTpye = SchemaType.valueOf(schemaOptionValue.toUpperCase());
							}
							catch(IllegalArgumentException iae) {
								throw new ParseException("Could not resolve schema name '" + schemaOptionValue + "' to a supported schema type " +
										"when attempting to execute SQL script file '" + commandLine.getOptionValue(sqlScriptFileOption) + "'. " +
												"Supported types are '" + SchemaType.getSupportedSchemaTypes() + "'");
							}
							
							qcLiveTestDataGenerator.executeSQLScriptFile(
									schemaTpye, new FileSystemResource(commandLine.getOptionValue(sqlScriptFileOption)));
						}
						else
							throw new ParseException("Setting the -f (or -sql_script_file) option also requires the -s (or -schema) to be set.");
					}	
				}
		    }
		    catch(ParseException pe) {
		        System.err.println("\nParsing failed. Reason: " + pe.getMessage());
		        displayHelp();
		    }
		    catch(IOException ioe) {
		    	logger.error(ioe.getMessage());
		    }
		    catch(SQLException sqle) {
		    	logger.error(sqle.getMessage());
		    }
		}
	}
}
