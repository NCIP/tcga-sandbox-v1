/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import org.apache.commons.io.IOUtils;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Pull data from the Postgres DB to be used to be loaded again later
 *
 * @author Jeyanthi Thangiah Last updated by: $
 * @version $
 */
public class ExtractDataSetForCommonDBUnit {

    private static final String DBURL = "jdbc:postgresql://cbiodb590.nci.nih.gov:5456/atlas";
    private static final String DBUser = "atlasdev";
    private static final String DBPass = "tcgadev893";
    private static final String DB_DUMP_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String DB_DUMP_FILE = "Sample_testDB.xml";


    public static void main(final String[] args) throws Exception {

        Connection jdbcConnection = null;
        FileOutputStream fileOutputStream = null;

        try {
            // database connection
            Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    DBURL, DBUser, DBPass);

            final IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

            ITableFilter filter = new DatabaseSequenceFilter(connection);
            DatabaseConfig config = connection.getConfig();
            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new PostgresqlDataTypeFactory());

            QueryDataSet ds = new QueryDataSet(connection, true);

            ds.addTable("log");

            String outputPath = DB_DUMP_FOLDER + DB_DUMP_FILE;
            //noinspection IOResourceOpenedButNotSafelyClosed
            fileOutputStream = new FileOutputStream(outputPath);
            FlatXmlDataSet.write(ds, fileOutputStream);
        } finally {
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }

            IOUtils.closeQuietly(fileOutputStream);
        }
    }
}