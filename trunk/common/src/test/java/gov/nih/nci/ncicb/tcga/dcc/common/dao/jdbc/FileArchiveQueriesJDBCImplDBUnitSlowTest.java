/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileToArchive;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * DBUnit test for FileArchiveQueries
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class FileArchiveQueriesJDBCImplDBUnitSlowTest extends DBUnitTestCase {

    private static final String PROPERTIES_FILE = "unittest.properties";
    private static final String TEST_DATA_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "dao/FileArchiveQueries_TestData.xml";

    private FileArchiveQueriesJDBCImpl queries;
    private FileInfo fileInfo1, unsavedFile, fileInfo2;
    private Archive archive;
    private JdbcTemplate jdbcTemplate;
    private static final long FILE_1_ID = 1L;
    private static final long ARCHIVE_ID = 10L;
    private static final long FILE_2_ID = 2L;
    private static final String ARCHIVE_NAME = "something";
    private static final String FILE_1_NAME = "wicked_this_way_comes.txt";
    private static final String FILE_2_NAME = "completely_different";

    public FileArchiveQueriesJDBCImplDBUnitSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    @Override
    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.DELETE_ALL;
    }

    @Override
    protected void setUpDatabaseConfig(final DatabaseConfig config) {
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new OracleDataTypeFactory());
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        jdbcTemplate = new JdbcTemplate(getDataSource());
        fileInfo1 = new FileInfo();
        fileInfo1.setId(FILE_1_ID);
        fileInfo1.setFileName(FILE_1_NAME);
        archive = new Archive();
        archive.setId(ARCHIVE_ID);
        archive.setDeployLocation(ARCHIVE_NAME + ".tar.gz");
        queries = new FileArchiveQueriesJDBCImpl();
        queries.setDataSource(getDataSource());
        unsavedFile = new FileInfo();
        unsavedFile.setId(-1L);
        fileInfo2 = new FileInfo();
        fileInfo2.setId(FILE_2_ID);
        fileInfo2.setFileName(FILE_2_NAME);
    }

    public void testAddFileToArchiveAssociation() {
        testAddAssociation(fileInfo1);
    }

    public void testAddExistingAssociation() {
        // association exists, but should update location url
        testAddAssociation(fileInfo2);
    }

    public void testAddAssociationUnsavedFile() {
        try {
            queries.addFileToArchiveAssociation(unsavedFile, archive);
            fail("Should have thrown an exception");
        } catch (DataIntegrityViolationException e) {
            // good
        }
    }

    public void testAssociationExists() {
        assertTrue(queries.associationExists(fileInfo2, archive));
        assertFalse(queries.associationExists(fileInfo1, archive));
        assertFalse(queries.associationExists(unsavedFile, archive));
    }

    private void testAddAssociation(final FileInfo fileInfo) {
        try {
            queries.addFileToArchiveAssociation(fileInfo, archive);
            String location = (String) jdbcTemplate.queryForObject("select file_location_url from file_to_archive " +
                    "where archive_id=" + ARCHIVE_ID + " and file_id=" + fileInfo.getId(), String.class);
            assertEquals(ARCHIVE_NAME + "/" + fileInfo.getFileName(), location);

        } finally {
            jdbcTemplate.update("delete from file_to_archive");
        }
    }

    public void testAddFileToArchiveAssociations() {
        try {
            final List<FileToArchive> fileToArchives = new ArrayList<FileToArchive>();
            final FileToArchive fileToArchiveBean_1 = getFileToArchiveBean(3l, "testlocation");
            fileToArchives.add(fileToArchiveBean_1);
            final FileToArchive fileToArchiveBean_2 = getFileToArchiveBean(4l, "testlocation");
            fileToArchives.add(fileToArchiveBean_2);

            queries.addFileToArchiveAssociations(fileToArchives);
            assertEquals("Failed to add filetoarchive bean", fileToArchiveBean_1, getFileToArchiveBeanFromDB(3l, 10l));
            assertEquals("Failed to add filetoarchive bean", fileToArchiveBean_2, getFileToArchiveBeanFromDB(4l, 10l));

        } finally {
            jdbcTemplate.update("delete from file_to_archive");
        }
    }

    public void testDeleteFileToArchiveAssociations() {

        final List<Long> fileIds = new ArrayList<Long>();
        fileIds.add(1000l);
        fileIds.add(1001l);
        queries.deleteFileToArchiveAssociations(fileIds, 10l);
        assertNull("Failed to delete filetoarchive record", getFileToArchiveBeanFromDB(1000l, 10l));
        assertNull("Failed to delete filetoarchive record", getFileToArchiveBeanFromDB(1001l, 10l));

    }

    public void testGetClinicalXMLFileLocations() {
        final Map<String, List<String>> actualXMLFileLocationsByCenter = queries.getClinicalXMLFileLocations();
        assertEquals(2, actualXMLFileLocationsByCenter.size());

        final List<String> expectedCenters = Arrays.asList("intgen.org", "test.org");
        final List<String> actualCenters = new ArrayList<String>(actualXMLFileLocationsByCenter.keySet());
        assertTrue(expectedCenters.containsAll(actualCenters));

        String expectedXMLFileLocations = "[/tcgafiles/biospecimen.TCGA-AA-1100.xml, /tcgafiles/clinical.TCGA-AA-1100.xml]";
        assertEquals(expectedXMLFileLocations, actualXMLFileLocationsByCenter.get("test.org").toString());

        expectedXMLFileLocations = "[/tcgafiles/biospecimen.TCGA-AA-1000.xml, /tcgafiles/clinical.TCGA-AA-1000.xml]";
        assertEquals(expectedXMLFileLocations, actualXMLFileLocationsByCenter.get("intgen.org").toString());
    }

    private FileToArchive getFileToArchiveBean(final long fileId,
                                               final String fileLocation) {
        final FileToArchive fileToArchive = new FileToArchive();
        fileToArchive.setFileId(fileId);
        fileToArchive.setFileLocationURL(fileLocation);
        fileToArchive.setArchiveId(10l);
        return fileToArchive;

    }

    private FileToArchive getFileToArchiveBeanFromDB(final Long fileId,
                                                     final Long archiveId) {
        final String GET_FILE_TO_ARCHIVE = "select file_archive_id,file_id,archive_id,file_location_url from file_to_archive " +
                "where file_id = ? and archive_id = ?";
        final SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(jdbcTemplate.getDataSource());
        final List<FileToArchive> fileToArchives = sjdbc.getJdbcOperations().query(GET_FILE_TO_ARCHIVE,
                new ParameterizedRowMapper<FileToArchive>() {
                    @Override
                    public FileToArchive mapRow(ResultSet resultSet, int i) throws SQLException {
                        final FileToArchive fileToArchive = new FileToArchive();
                        fileToArchive.setFileArchiveId(resultSet.getLong("file_archive_id"));
                        fileToArchive.setArchiveId(resultSet.getLong("archive_id"));
                        fileToArchive.setFileId(resultSet.getLong("file_id"));
                        fileToArchive.setFileLocationURL(resultSet.getString("file_location_url"));
                        return fileToArchive;
                    }
                }, new Object[]{fileId, archiveId});
        if (fileToArchives != null && fileToArchives.size() > 0) {
            return fileToArchives.get(0);
        }
        return null;
    }
}
