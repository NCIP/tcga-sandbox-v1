/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.dao.util;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.commons.io.IOUtils;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

/**
 * Utility class for extracting data for dbunit testcases 
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */
public class ExtractUUIDData {
    
    private static final String dbFileName = "TestDB_UUID.xml";
    private static final String sampleFolder = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    
    private static final String SQL_DISEASE = "Select disease_id, disease_name, disease_abbreviation, disease_schema, active " +
            "from disease where disease_id in (1, 2) ";

    private static final String SQL_CENTER = "Select center_Id, domain_name, center_type_code, display_name, short_name, sort_order " +
            "from center where center_id in (1, 2, 3, 4)";
    
    private static final String SQL_CENTER_EMAIL = "Select center_email_id, center_id, email_address " +
            "from center_email where center_id in (1,2, 3, 4)";
    
    private static final String SQL_GENERATION_METHOD = "Select generation_method_id, generation_method from generation_method";

    private static final String SQL_UUID = "Select uuid, create_date, center_Id, generation_method_id, created_by, latest_barcode_id from uuid";
    
    private static final String SQL_BARCODE = "Select barcode_id, barcode, uuid, disease_id, effective_date from barcode";

    public static void main(final String[] args) throws Exception {
        // database connection
        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection jdbcConnection = null;
        FileOutputStream fileOutputStream = null;
        try {
            jdbcConnection =  DriverManager.getConnection("jdbc:oracle:thin:@ncidb-tcga-d.nci.nih.gov:1562:TCGADEV", "dcccommondev", "dcc58920dev");
            final IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
            // partial database export
            QueryDataSet ds = new QueryDataSet(connection);

            ds.addTable("Center", SQL_CENTER);
            ds.addTable("Center_Email", SQL_CENTER_EMAIL);
            ds.addTable("Disease", SQL_DISEASE);
            ds.addTable("Generation_Method", SQL_GENERATION_METHOD);

            ds.addTable("UUID", SQL_UUID);
            ds.addTable("BARCODE", SQL_BARCODE);
            //noinspection IOResourceOpenedButNotSafelyClosed
            fileOutputStream = new FileOutputStream(sampleFolder + dbFileName);
            FlatXmlDataSet.write(ds, fileOutputStream);
        }finally {
            if(jdbcConnection != null) {
                jdbcConnection.close();
            }
            IOUtils.closeQuietly(fileOutputStream);
        }
    }

}
