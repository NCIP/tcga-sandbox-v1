/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.dao;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.dao.DatareportDBUnitConfig.DATA_SUMMARY_DB_DUMP_FOLDER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.SAMPLE_SUMMARY_DB_FILE;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;

/**
 * Pull data from the DB to be used to be loaded again later
 *
 * @author Jon Whitmore Last updated by: $
 * @version $
 */
public class ExtractDataSetForDBUnit {

    private static final String DBURL = "jdbc:postgresql://cbiodb590.nci.nih.gov:5456/atlas";
    private static final String DBUser = "atlasdev";
    private static final String DBPass = "tcgadev893";

    public static void main(final String[] args) throws Exception {
        // database connection
        Class.forName("org.postgresql.Driver");
        final Connection jdbcConnection = DriverManager.getConnection(
                DBURL, DBUser, DBPass);

        final IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

        ITableFilter filter = new DatabaseSequenceFilter(connection);
        IDataSet dataset = new FilteredDataSet(filter, connection.createDataSet());
        DatabaseConfig config = connection.getConfig();
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new PostgresqlDataTypeFactory());
        // partial database export
        QueryDataSet ds = new QueryDataSet(connection, true);

        // order is very important here!  Foriegn key constraints abound.
        ds.addTable("center_info");
        ds.addTable("platform_info");
        ds.addTable("tumor_info");
        ds.addTable("data_level");
        ds.addTable("datatype");
        ds.addTable("archive_type");
        ds.addTable("archive_info");
        ds.addTable("sample_type");
        ds.addTable("project");
        ds.addTable("portion_analyte");
        ds.addTable("tissue_info");
        ds.addTable("collection_center");
        ds.addTable("tissue_to_tumor");
        ds.addTable("file_type");
        ds.addTable("file_info");
        ds.addTable("file_info_url");
        ds.addTable("file_data_level");
        ds.addTable("shipped_biospecimen");
        ds.addTable("shipped_biospecimen_file");
        ds.addTable("biospecimen_gsc_file_mv");
        ds.addTable("shipped_biospec_bcr_archive");
        ds.addTable("center_bcr_center_map");
        ds.addTable("samples_sent_by_bcr");
        ds.addTable("latest_samples_received_by_dcc");
        ds.addTable("sample_level_count");
        ds.addTable("batch_number_assignment");
        ds.addTable("orphaned_biospecimen_barcode");

        String outputPath = DATA_SUMMARY_DB_DUMP_FOLDER + SAMPLE_SUMMARY_DB_FILE;
        FlatXmlDataSet.write(ds, new FileOutputStream(outputPath));
    }

}
