package gov.nih.nci.ncicb.tcgaportal.level4.dao.dbunit;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.dbunit.util.FilterSpecifierXmlInputParser;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.jdbc.Level4QueriesJDBCImpl;
import gov.nih.nci.ncicb.tcgaportal.level4.export.ExportData;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Description : Class to test the Level4QueriesJDBCImpl using DBUnit Test Framework
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class Level4QueriesJDBCImplDBUnitConfig extends DBUnitTestCase {

    public static final String PORTAL_FOLDER = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    public static final String TCGA_TEST_PROPERTIES_FILE = "databrowserDbunitTest.properties";

    protected Level4QueriesJDBCImpl queries;
    protected FilterSpecifier filter;

    public Level4QueriesJDBCImplDBUnitConfig(final String dataSetFile) {
        super(PORTAL_FOLDER, dataSetFile, TCGA_TEST_PROPERTIES_FILE);
    }

    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.CLEAN_INSERT;
    }

    @Override
    protected DatabaseOperation getTearDownOperation() {
        return DatabaseOperation.DELETE_ALL;
    }

    @Override
    protected void setUpDatabaseConfig(DatabaseConfig config) {
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new OracleDataTypeFactory());
    }

    protected Results runAnomalyFilter() throws Throwable {
        MockLevel4QueriesCallback callback = new MockLevel4QueriesCallback();
        queries.getAnomalyResults(filter, callback);
        while (!callback.done) {
            Thread.sleep(1000);
        }
        if (callback.caughtException != null) {
            throw callback.caughtException;
        }
        return callback.results;
    }

    public Results runPathwayFilter() throws Throwable {
        MockLevel4QueriesCallback callback = new MockLevel4QueriesCallback();
        queries.getPathwayResults(filter, callback);
        while (!callback.done) {
            Thread.sleep(1000);
        }
        if (callback.caughtException != null) {
            throw callback.caughtException;
        }
        return callback.results;
    }

    // read from the input file

    protected void createFilterFromInputFile(String inputFile) throws Exception {
        FilterSpecifierXmlInputParser parser = new FilterSpecifierXmlInputParser();
        parser.parseInputFile(PORTAL_FOLDER + inputFile, filter);
    }

    //Method to write the results in a flat file using export functionality

    protected void writeDatasetOutput(Results results, String filename) throws IOException {

        ExportData exportData = ExportData.getInstance(filter.getListBy());
        StringWriter sw = new StringWriter();
        PrintWriter printWriter = new PrintWriter(sw);

        exportData.export(results, printWriter, false);

        Writer writer = new BufferedWriter(new FileWriter(filename));
        writer.write(sw.toString());

        writer.flush();
        writer.close();

    }
}
