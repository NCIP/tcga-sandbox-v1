/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc;

import org.apache.commons.cli.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test class for the {@link QCLiveTestDataGenerator}.
 *
 * @author Matt Nicholls
 *         Last updated by: nichollsmc
 * @version 
 */
public class QCLiveTestDataGeneratorSlowTest {

	/** Spring application context file */
	private static final String APP_CONTEXT_FILE_NAME = "applicationContext-test.xml";
	
	/** Test Spring application context */
	private static final ApplicationContext testAppCtx = new ClassPathXmlApplicationContext(APP_CONTEXT_FILE_NAME);
	
	/** Byte streams for capturing output printed to System.err and System.out */
	private ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	
	/**
	 * Sets up System.out and System.err print streams before each test
	 */
	@Before
	public void setUpStreams() {
	    System.setOut(new PrintStream(outContent));
	    System.setErr(new PrintStream(errContent));
	}

	/**
	 * Cleans up System.out and System.err print streams after each test
	 */
	@After
	public void cleanUpStreams() {
	    System.setOut(null);
	    System.setErr(null);
	}

	/**
	 * Tests the {@link QCLiveTestDataGenerator#main(String[])} method by passing it an empty command line argument list.
	 */
	@Test
	public void testNoArgs() {
		
		// Call the QCLiveTestDataGenerator with no arguments and capture the output
		QCLiveTestDataGenerator.main(new String[]{});
		String actualOutput = outContent.toString().trim();
		
		// Print the expected output to system out and capture its output
		System.setOut(new PrintStream(outContent = new ByteArrayOutputStream()));
		QCLiveTestDataGenerator.displayHelp();
		String expectedOutput = outContent.toString().trim();
		
		// Compare the results
		assertEquals(expectedOutput, actualOutput);
	}
	
	/**
	 * Tests the {@link QCLiveTestDataGenerator#main(String[])} method by passing it an unsupported argument.
	 */
	@Test 
	public void testUnrecognizedArg() {
		
		// Call the QCLiveTestDataGenerator with an unsupported argument
		String unsupportedArg = "thisIsAnUnrecognizedArgument";
		QCLiveTestDataGenerator.main(new String[]{'-' + unsupportedArg});
		String actualOuput = errContent.toString().trim();
		
		// Compare the results
		assertEquals("Parsing failed. Reason: Unrecognized option: -" + unsupportedArg, actualOuput);
	}
	
	/**
	 * Tests the {@link QCLiveTestDataGenerator#main(String[])} with the {@link CommandLineOptionType#HELP} option type and asserts that the
	 * appropriate usage information is displayed via the command line. 
	 */
	@Test
	public void testHelpCommandLineArg() {
		
		// Call the QCLiveTestDataGenerator with the -? option to capture its output
		QCLiveTestDataGenerator.main(new String[]{'-' + CommandLineOptionType.HELP.getOptionValue().getOpt()});
		String actualOutput = outContent.toString();
		
		// Print the expected output to system out and capture its output
		System.setOut(new PrintStream(outContent = new ByteArrayOutputStream()));
		QCLiveTestDataGenerator.displayHelp();
		String expectedOutput = outContent.toString();
		
		// Compare the results
		assertEquals(expectedOutput, actualOutput);
	}
	
	/**
	 * Tests the {@link QCLiveTestDataGenerator#main(String[])} with the {@link CommandLineOptionType#ARCHIVE_NAME} option type 
	 * without a corresponding archive name value (e.g. intgen.org_READ.bio.Level_1.42.4.0.tar.gz) and asserts that the appropriate 
	 * error message is displayed via the command line indicating that the value for the argument is missing.
	 */
	@Test
	public void testMissingArchiveNameCmdLineArgValue() {
		
		// Call the QCLiveTestDataGenerator with the -a argument and no corresponding value
		String option = CommandLineOptionType.ARCHIVE_NAME.getOptionValue().getOpt();
		QCLiveTestDataGenerator.main(new String[]{'-' + option});
		String actualOuput = errContent.toString().trim();
		
		// Compare the results
		assertEquals("Parsing failed. Reason: Missing argument for option: " + option, actualOuput);
	}
	
	/**
	 * Tests the {@link QCLiveTestDataGenerator#main(String[])} with the {@link CommandLineOptionType#SQL_SCRIPT_FILE} option type 
	 * without a corresponding SQL script file value and asserts that the appropriate error message is displayed via the command line 
	 * indicating that the value for the argument is missing.
	 */
	@Test
	public void testMissingSQLScriptFileCmdLineArgValue() {
		
		// Call the QCLiveTestDataGenerator with the -f argument and no corresponding value
		String option = CommandLineOptionType.SQL_SCRIPT_FILE.getOptionValue().getOpt();
		QCLiveTestDataGenerator.main(new String[]{'-' + option});
		String actualOuput = errContent.toString().trim();
		
		// Compare the results
		assertEquals("Parsing failed. Reason: Missing argument for option: " + option, actualOuput);
	}
	
	/**
	 * Tests the {@link QCLiveTestDataGenerator#main(String[])} with the {@link CommandLineOptionType#SCHEMA} option type 
	 * without a corresponding schema value and asserts that the appropriate error message is displayed via the command line 
	 * indicating that the value for the argument is missing.
	 */
	@Test
	public void testMissingSchemaCmdLineArgValue() {
		
		// Call the QCLiveTestDataGenerator with the -s argument and no corresponding value
		String option = CommandLineOptionType.SCHEMA.getOptionValue().getOpt();
		QCLiveTestDataGenerator.main(new String[]{'-' + option});
		String actualOuput = errContent.toString().trim();
		
		// Compare the results
		assertEquals("Parsing failed. Reason: Missing argument for option: " + option, actualOuput);
	}
	
	/**
	 * Tests the {@link QCLiveTestDataGenerator#main(String[])} with the {@link CommandLineOptionType#SQL_SCRIPT_FILE} option type 
	 * without also setting the {@link CommandLineOptionType#SCHEMA} option, and asserts that the appropriate error message is displayed 
	 * via the command line indicating that setting the script file option also requires the schema option to be set.
	 */
	@Test
	public void testMissingSchemaForSQLScriptFileCmdLineOption() {
		
		// Call the QCLiveTestDataGenerator with the -f argument and value without specifying the required -s option
		String option = CommandLineOptionType.SQL_SCRIPT_FILE.getOptionValue().getOpt();
		String arg = '-' + option + " pathToSQLScriptFile";
		QCLiveTestDataGenerator.main(new String[]{arg});
		String actualOuput = errContent.toString().trim();
		
		// Compare the results
		assertEquals("Parsing failed. Reason: Setting the -f (or -sql_script_file) option also requires the -s (or -schema) to be set.", actualOuput);
	}
	
	/**
	 * Tests the {@link QCLiveTestDataGenerator#main(String[])} method with a schema name that is not supported.
	 */
	@Test
	public void testExecSQLScriptFileWithUnsupportedSchemaType() {
		
		// Set the -f and -s arguments with an unsupported schema type
		String scriptFileArg = '-' + CommandLineOptionType.SQL_SCRIPT_FILE.getOptionValue().getOpt() + " pathtoSQLScriptFile";
		String schemaArg = '-' + CommandLineOptionType.SCHEMA.getOptionValue().getOpt() + " unsupportedSchemaType";
		
		// Call the QCLiveTestDataGenerator with the -f an -s arguments with an unsupported schema type
		QCLiveTestDataGenerator.main(new String[]{scriptFileArg, schemaArg});
		String actualOuput = errContent.toString().trim();
		
		// Compare the results
		assertEquals("Parsing failed. Reason: Could not resolve schema name ' unsupportedSchemaType' to a supported schema type when attempting to execute SQL script file " +
				"' pathtoSQLScriptFile'. Supported types are '[ local_common, local_disease ]'", actualOuput);
	}
	
	/**
	 * Tests the {@link QCLiveTestDataGenerator#generateTestData(String)} method with an archive name that contains
	 * an invalid center domain name
	 */
	@Test
	public void testArchvNameWithInvalidCenterDomainName() throws IOException, SQLException {
		
		// Retrieve the QCLiveTestDataGenerator from the Spring context and invoke it using an archive name that contains
		// an invalid center domain name
		String actualMessage = null;
		try {
			((QCLiveTestDataGenerator)testAppCtx.getBean("qcLiveTestDataGenerator")).generateTestData("invalidDomainName.org_READ.bio.Level_1.42.4.0");
		} 
		catch (ParseException e) {
			actualMessage = e.getMessage();
		}
		
		// Assert that the correct exception message is thrown
		assertEquals("No information exists for archive name 'invalidDomainName.org_READ.bio.Level_1.42.4.0'.", actualMessage);
	}
	
	/**
	 * Tests the {@link QCLiveTestDataGenerator#generateTestData(String)} method with an archive name that contains
	 * an invalid disease abbreviation.
	 */
	@Test
	public void testArchvNameWithInvalidDuseaseAbbr() throws ParseException, IOException, SQLException {

		// Retrieve the QCLiveTestDataGenerator from the Spring context and invoke it using an archive name that contains
		// an invalid disease abbreviation
		String actualMessage = null;
		try {
			((QCLiveTestDataGenerator)testAppCtx.getBean("qcLiveTestDataGenerator")).generateTestData("intgen.org_invalidDiseaseAbbr.bio.Level_1.42.4.0");
		} 
		catch (ParseException e) {
			actualMessage = e.getMessage();
		}
		
		// Assert that the correct exception message is thrown
		assertEquals("No information exists for archive name 'intgen.org_invalidDiseaseAbbr.bio.Level_1.42.4.0'.", actualMessage);
	}
	
	/**
	 * Tests the {@link QCLiveTestDataGenerator#generateTestData(String)} method with an archive name that contains
	 * an invalid platform name.
	 */
	@Test
	public void testArchvNameWithInvalidPlatformName() throws ParseException, IOException, SQLException {

		// Retrieve the QCLiveTestDataGenerator from the Spring context and invoke it using an archive name that contains
		// an invalid platform name
		String actualMessage = null;
		try {
			((QCLiveTestDataGenerator)testAppCtx.getBean("qcLiveTestDataGenerator")).generateTestData("intgen.org_READ.invalidPlatformName.Level_1.42.4.0");
		} 
		catch (ParseException e) {
			actualMessage = e.getMessage();
		}
		
		// Assert that the correct exception message is thrown
		assertEquals("No information exists for archive name 'intgen.org_READ.invalidPlatformName.Level_1.42.4.0'.", actualMessage);
	}
	
	/**
	 * Tests the {@link QCLiveTestDataGenerator#executeSQLScriptFile(SchemaType, org.springframework.core.io.Resource)} method with
	 * a null SQL script file resource.
	 */
	@Test
	public void testExecuteNullSQLScriptFile() throws IOException, SQLException {
		
		String actualMessage = null;
		try {
			new QCLiveTestDataGenerator().executeSQLScriptFile(SchemaType.LOCAL_DISEASE, null);
		}
		catch(NullPointerException npe) {
			actualMessage = npe.getMessage();
		}
		
		// Assert that the correct exception message is thrown
		assertEquals("Cannot execute SQL script file for schema '" + SchemaType.LOCAL_DISEASE + "' and file 'null'.", actualMessage);
	}
	
	/**
	 * Tests the {@link QCLiveTestDataGenerator#executeSQLScriptFile(SchemaType, org.springframework.core.io.Resource)} method with
	 * a null schema name.
	 */
	@Test
	public void testExecuteSQLScriptFileWithNullSchema() throws IOException, SQLException {
		
		String actualMessage = null;
		try {
			new QCLiveTestDataGenerator().executeSQLScriptFile(null, new ClassPathResource("sql/TestSQLScriptFile.sql"));
		}
		catch(NullPointerException npe) {
			actualMessage = npe.getMessage();
		}
		
		// Assert that the correct exception message is thrown
		assertEquals("Cannot execute SQL script file for schema 'null' and file 'class path resource [sql/TestSQLScriptFile.sql]'.", actualMessage);
	}
	
	/**
	 * Tests the {@link QCLiveTestDataGenerator#executeSQLScriptFile(SchemaType, org.springframework.core.io.Resource)} method with
	 * a non-existent SQL script file resource.
	 */
	public void testExecSQLScriptFileWithInvalidFile() throws SQLException {
		
		String actualMessage = null;
		try {
			new QCLiveTestDataGenerator().executeSQLScriptFile(SchemaType.LOCAL_COMMON, new FileSystemResource("invalidSQLScriptFile"));
		}
		catch(IOException ioe) {
			actualMessage = ioe.getMessage();
		}
		
		// Assert that the correct exception message is thrown
		assertEquals("SQL script file resource 'class path resource [invalidSQLScriptFile]' is not readable.", actualMessage);
	}
	
	/**
	 * Tests the {@link QCLiveTestDataGenerator#executeSQLScriptFile(SchemaType, org.springframework.core.io.Resource)} method with
	 * all valid schema types.
	 * 
	 * <p>{@link NullPointerException} should be thrown since the data sources for the {@link QCLiveTestDataGenerator} have not been
	 * set.
	 */
	@Test(expected=NullPointerException.class)
	public void testExecSQLScriptFileWithValidParams() throws IOException, SQLException {
		
		QCLiveTestDataGenerator qcLiveTestDataGenerator = new QCLiveTestDataGenerator();
		
		// Execute test SQL script for each schema type
		for(SchemaType schemaType : SchemaType.values())
			qcLiveTestDataGenerator.executeSQLScriptFile(schemaType, new ClassPathResource("sql/TestSQLScriptFile.sql"));
	}
	
	/**
	 * Test the {@link QCLiveTestDataGenerator#setDccCommonDevJdbcTemplate(JdbcTemplate)} and 
	 * {@link QCLiveTestDataGenerator#setDiseaseDevJdbcTemplate(JdbcTemplate)} methods with a {@link JdbcTemplate} instance
	 * whose data source references a test account.
	 * 
	 * <p>An {@link IllegalArgumentException} should be thrown indicating that test accounts are not allowed.
	 */
	@Test
	public void testSetDevDataSourceWithTestAcct() {
		
		// Set up at data source and JDBC Connection template that uses a test account
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setUsername("test");
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		// Try setting the COMMON PRODUCTION JdbcTemplate for the QCLiveTestDataGenerator, should throw an IllegalArgumentException
		QCLiveTestDataGenerator qcLiveTestDataGenerator = new QCLiveTestDataGenerator();
		try {
			qcLiveTestDataGenerator.setDccCommonDevJdbcTemplate(jdbcTemplate);
		}
		catch(IllegalArgumentException iae) {
			assertEquals("Test account 'test' is not permitted for database connection property 'dccCommonDevJdbcTemplate'", iae.getMessage());
		}
		
		// Try setting the DISEASE PRODUCTION JdbcTemplate for the QCLiveTestDataGenerator, should throw an IllegalArgumentException
		try {
			qcLiveTestDataGenerator.setDiseaseDevJdbcTemplate(jdbcTemplate);
		}
		catch(IllegalArgumentException iae) {
			assertEquals("Test account 'test' is not permitted for database connection property 'diseaseDevJdbcTemplate'", iae.getMessage());
		}
	}
	
	/**
	 * Test the {@link QCLiveTestDataGenerator#setDccCommonLocalJdbcTemplate(JdbcTemplate)} and 
	 * {@link QCLiveTestDataGenerator#setDiseaseLocalJdbcTemplate(JdbcTemplate)} methods with a {@link JdbcTemplate} instance
	 * whose data source defines a URL that contains a hostname that points to a non-test database instance.
	 * 
	 * <p>An {@link IllegalArgumentException} should be thrown indicating that non-test hostnames in the database URL are
	 * not allowed.
	 */
	@Test
	public void testSetInvalidDevJDBCURLForLocalDB() {
		
		// Set up at data source and JDBC Connection template that uses a test account
		String url = "invalidTestDBHostName";
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setUrl(url);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		// Try setting the COMMON LOCAL JdbcTemplate for the QCLiveTestDataGenerator, should throw an IllegalArgumentException
		QCLiveTestDataGenerator qcLiveTestDataGenerator = new QCLiveTestDataGenerator();
		try {
			qcLiveTestDataGenerator.setDccCommonLocalJdbcTemplate(jdbcTemplate);
		}
		catch(IllegalArgumentException iae) {
			assertEquals("Connection URL '" + url + "' for datasource defined by 'dccCommonLocalJdbcTemplate' does not point to a " +
					"known test database host. Expected one of [" + qcLiveTestDataGenerator.getTestDBHostNames() + "]", iae.getMessage());
		}
		
		// Try setting the DISEASE LOCAL JdbcTemplate for the QCLiveTestDataGenerator, should throw an IllegalArgumentException
		try {
			qcLiveTestDataGenerator.setDiseaseLocalJdbcTemplate(jdbcTemplate);
		}
		catch(IllegalArgumentException iae) {
			assertEquals("Connection URL '" + url + "' for datasource defined by 'diseaseLocalJdbcTemplate' does not point to a " +
					"known test database host. Expected one of [" + qcLiveTestDataGenerator.getTestDBHostNames() + "]", iae.getMessage());
		}
	}

    /**
     * Tests the {@link QCLiveTestDataGenerator#generateTestData(String)} method with a valid archive name
     * and check assertions to verify that CNTL test data was loaded.
     */
    @Test
    public void testLoadCNTLTestDataForNonCNTLArchive() throws IOException, SQLException, ParseException {

        // Retrieve the QCLiveTestDataGenerator from the Spring context and invoke it using a valid archive name
        ((QCLiveTestDataGenerator)testAppCtx.getBean("qcLiveTestDataGenerator")).generateTestData("nationwidechildrens.org_BRCA.bio.Level_1.85.21.0");

        final JdbcTemplate diseaseLocalJdbcTemplate = (JdbcTemplate)testAppCtx.getBean("diseaseLocalJdbcTemplate");
        final SqlRowSet rowSet = diseaseLocalJdbcTemplate.queryForRowSet("select * from data_level");

        assertNotNull(rowSet);
        assertTrue(rowSet.first());
        assertEquals(1, rowSet.getInt("level_number"));
        assertEquals("cAsESeNsItIvE", rowSet.getString("level_definition"));
        assertTrue(rowSet.isLast());
    }

    /**
     * Tests the {@link QCLiveTestDataGenerator#generateTestData(String)} method with a valid archive name
     * and check assertions to verify that CNTL test data was NOT loaded.
     */
    @Test
    public void testLoadCNTLTestDataForCNTLArchive() throws IOException, SQLException, ParseException {

        // Retrieve the QCLiveTestDataGenerator from the Spring context and invoke it using a valid archive name
        ((QCLiveTestDataGenerator)testAppCtx.getBean("qcLiveTestDataGenerator")).generateTestData("intgen.org_CNTL.bio.Level_1.0.4.0");

        final JdbcTemplate diseaseLocalJdbcTemplate = (JdbcTemplate)testAppCtx.getBean("diseaseLocalJdbcTemplate");
        final int count = diseaseLocalJdbcTemplate.queryForInt("select count(*) from data_level");

        assertEquals(0, count);
    }
}
