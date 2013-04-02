package gov.nih.nci.ncicb.tcga.dcc.common.dao;


import org.dbunit.DBTestCase;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

/**
 * Description :  this will be used as the base class for all the dbunit test cases for DCC
 *
 * @author Namrata Rane Last updated by: $Author$
 * @version $Rev$
 */
public class DBUnitTestCase extends DBTestCase {

    /**
     * Tell the XML parser there is no DTD.
     */
    protected boolean dtdMetadata = false;

    /**
     * Tell the DataSet creator to parse defensively.
     */
    protected boolean enableColumnSensing = true;

    /**
     * Enforce a case sensitive DataSet
     */
    protected boolean caseSensitiveDataSet = true;

    protected String moduleFolder;
    protected String driver;
    protected String connectionURL;
    protected String userName;
    protected String password;
    protected String schema;

    protected String datasetTestData;
    protected DataSource dataSource;
    protected SimpleJdbcTemplate simpleJdbcTemplate;
    protected TCGAProperties props;

    protected final ResourceBundle dbUnitProperties = ResourceBundle.getBundle(DBUnitTestCase.class.getName());

    public DBUnitTestCase() {
    }

    public DBUnitTestCase(String folder, String testDBFileName, String propertiesFile) {

        moduleFolder = folder;
        try {
            props = TCGAProperties.getInstance(folder, propertiesFile);
            driver = props.getProperty("dbUnitJDBCDriver");
            connectionURL = props.getProperty("dbUnitConnectionURL");
            userName = props.getProperty("dbUnitUserName");
            password = props.getProperty("dbUnitPassword");
            schema = props.getProperty("dbUnitSchema");

            System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, driver);
            System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, connectionURL);
            System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, userName);
            System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, password);
            if (!driver.contains("postgresql")) {
                System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_SCHEMA, schema);
            }
            this.datasetTestData = testDBFileName;
            initDataSource();
            this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
            resetSequenceNumber();
        } catch (Exception e) {
            fail(e.toString());
            e.printStackTrace();

        }
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

    }

    protected void initDataSource() throws SQLException {
        dataSource = new SingleConnectionDataSource(connectionURL, userName, password, true);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    protected SimpleJdbcTemplate getSimpleJdbcTemplate() {
        return simpleJdbcTemplate;
    }

    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.CLEAN_INSERT;
    }

    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.DELETE_ALL;
    }

    protected IDataSet getDataSet() throws Exception {
        FlatXmlDataSetBuilder fdsb = new FlatXmlDataSetBuilder();
        fdsb.setCaseSensitiveTableNames(caseSensitiveDataSet);
        fdsb.setColumnSensing(enableColumnSensing);
        fdsb.setDtdMetadata(dtdMetadata);
        return fdsb.build(new File(moduleFolder + datasetTestData));
    }

    protected void resetSequenceNumber() throws Exception {
        final String[] tableNames = getDataSet().getTableNames();
        String metaData;
        StringTokenizer stk;
        String columnName;
        String sequenceObjectName;
        int maxId;
        int theNextSequenceNumber;

        for (String tableName : tableNames) {
            try {
                metaData = dbUnitProperties.getString(tableName);
                stk = new StringTokenizer(metaData, ",");
                //read the sequence object name and column name from dbunit properties file
                if (stk.countTokens() == 2) {
                    columnName = stk.nextToken();
                    sequenceObjectName = stk.nextToken();
                    //get the max id from database
                    maxId = simpleJdbcTemplate.queryForInt("Select max(" + columnName + ") FROM " + tableName);
                    // get the  max id from sequence object
                    final String GET_MAX_SEQ_SQL = "select " + sequenceObjectName + ".nextval from dual";
                    theNextSequenceNumber = simpleJdbcTemplate.queryForInt(GET_MAX_SEQ_SQL);

                    // reset the sequence number only if it is less than maxId
                    if (theNextSequenceNumber < maxId) {
                        try {
                            simpleJdbcTemplate.update("alter sequence " + sequenceObjectName + " increment by " + (maxId - theNextSequenceNumber));
                        } catch (Exception e) {
                        }
                        simpleJdbcTemplate.queryForInt(GET_MAX_SEQ_SQL);
                        simpleJdbcTemplate.update("alter sequence " + sequenceObjectName + " increment by 1");
                    }
                }

            } catch (Exception e) {
            }
        }
    }


    @Override
    public void tearDown() throws Exception {
        Thread.sleep(1000); // added this because I got the same listener error otherwise when trying to do tear down
        super.tearDown();
        Thread.sleep(1000);

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } finally {
            connection.close();
        }
    }

    /*
     * Put this here because all of our test dbs are Oracle now...     
     */

    @Override
    protected void setUpDatabaseConfig(final DatabaseConfig config) {
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new OracleDataTypeFactory());
        config.setProperty(DatabaseConfig.FEATURE_SKIP_ORACLE_RECYCLEBIN_TABLES, true);
    }
}

