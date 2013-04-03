/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.dao.dbunit.util;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.ext.oracle.OracleDataTypeFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Simple data extractor for quick data extraction
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class SimpleExtractor {

    private static final String DB_FILE_NAME = "simpleExtract.xml";
    private static final String PORTAL_FOLDER = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;

    private static final String QUERY = "SELECT * FROM L4_TARGET s WHERE target_id in " +
            "('58191')";

    public static void main(final String[] args) throws Exception {
        // database connection
        Class.forName("oracle.jdbc.driver.OracleDriver");
        final Connection jdbcConnection = DriverManager.getConnection(
                "jdbc:oracle:thin:@cbiodb540.nci.nih.gov:1521:TCGADEV", "tcgagbm", "tcga2874gbm");
        //"jdbc:oracle:thin:@cbiodb530.nci.nih.gov:1521:TCGAQA", "tcgaunittest", "tcga123unit" );
        final IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
        connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
                new OracleDataTypeFactory());
        // partial database export
        QueryDataSet ds = new QueryDataSet(connection);
        ds.addTable("L4_TARGET", QUERY);
        FlatXmlDataSet.write(ds, new FileOutputStream(PORTAL_FOLDER + DB_FILE_NAME));
    }

}//End of Class
