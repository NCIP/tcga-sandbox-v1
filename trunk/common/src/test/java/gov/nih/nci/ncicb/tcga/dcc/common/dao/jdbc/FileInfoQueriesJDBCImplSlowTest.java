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
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.common.web.FileInfoQueryRequest;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Slow test for FileInfoQueries JDBC implementation.
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class FileInfoQueriesJDBCImplSlowTest extends DBUnitTestCase {

    private static final String PROPERTIES_FILE = "unittest.properties";
    private static final String TEST_DATA_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "dao/FileInfo_TestData.xml";

    private FileInfoQueriesJDBCImpl fileInfoQueriesJDBCImpl;

    public FileInfoQueriesJDBCImplSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        fileInfoQueriesJDBCImpl = new FileInfoQueriesJDBCImpl();
        fileInfoQueriesJDBCImpl.setDataSource(getDataSource());
        ArchiveQueriesJDBCImpl archiveQueries = new ArchiveQueriesJDBCImpl();
        archiveQueries.setDataSource(getDataSource());
        fileInfoQueriesJDBCImpl.setArchiveQueries(archiveQueries);
    }

    @Override
    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.DELETE_ALL;
    }

    @Override
    protected void setUpDatabaseConfig(DatabaseConfig config) {
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new OracleDataTypeFactory());
    }

    public void testGetFilesForArchive() {

        final FileInfoQueryRequest request = new FileInfoQueryRequest();
        request.setArchiveId(1);

        final List<FileInfo> result = fileInfoQueriesJDBCImpl.getFilesForArchive(request);
        assertNotNull(result);

        final FileInfo fileInfo = result.get(0);
        assertNotNull(fileInfo);
        assertEquals("broad.mit.edu_GBM.HT_HG-U133A.1.sdrf.txt", fileInfo.getFileName());
        assertEquals("/broad.mit.edu_GBM.HT_HG-U133A.1.sdrf.txt", fileInfo.getFileLocation());
    }

    public void testGetFilesForArchiveNoResult() {

        final FileInfoQueryRequest request = new FileInfoQueryRequest();
        request.setArchiveId(-1);

        final List<FileInfo> result = fileInfoQueriesJDBCImpl.getFilesForArchive(request);
        assertNull(result);
    }

    public void testGetFileId() {
        final Long fileId = fileInfoQueriesJDBCImpl.getFileId("broad.mit.edu_GBM.HT_HG-U133A.1.sdrf.txt", 1L);
        assertNotNull(fileId);
        assertEquals(1, fileId.longValue());
        assertNull(fileInfoQueriesJDBCImpl.getFileId("bad", 1L));
    }

    public void testGetFileNameById() {
        final String fileName = fileInfoQueriesJDBCImpl.getFileNameById(1L);
        assertNotNull(fileName);
        assertEquals("broad.mit.edu_GBM.HT_HG-U133A.1.sdrf.txt", fileName);
    }

    public void testGetFileNameByIdForBadId() {
        assertNull(fileInfoQueriesJDBCImpl.getFileNameById(-1L));
    }

    public void testGetFileDataLevel() {
        final Integer level = fileInfoQueriesJDBCImpl.getFileDataLevel(1L);
        assertNotNull(level);
        assertEquals(1, level.longValue());
    }

    public void testAddFile() {
        final FileInfo fileInfo = mockFileInfo();
        final Long fileId = fileInfoQueriesJDBCImpl.addFile(fileInfo);
        assertNotNull(fileId);
        final String fileNameToVerify = fileInfoQueriesJDBCImpl.getFileNameById(fileId);
        assertNotNull(fileNameToVerify);
        assertEquals("newfile", fileNameToVerify);
    }

    public void testAddFileMinimalInfo() {
        // make sure only setting the values that are required works!
        final FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName("iAmAFile.txt");
        final Long fileId = fileInfoQueriesJDBCImpl.addFile(fileInfo);
        assertNotNull(fileId);
        assertEquals(fileId, fileInfo.getId());
        assertEquals(fileInfo.getFileName(),
                getSimpleJdbcTemplate().queryForObject("select file_name from file_info where file_id=?", String.class, fileId));
    }

    public void testUpdateFile() {
        final long fileId = 1;
        final FileInfo fileInfo = mockFileInfo();
        fileInfo.setId(fileId);
        fileInfoQueriesJDBCImpl.updateFile(fileInfo);
        final String fileNameToVerify = fileInfoQueriesJDBCImpl.getFileNameById(fileId);
        assertNotNull(fileNameToVerify);
        assertEquals("newfile", fileNameToVerify);
    }

    public void testUpdateDataLevel() {
        final long fileId = 1;
        final int dataLevel = 2;
        // in the test data the dataLevel is set to 2 for for the file with fileId = 1
        // now update the data level to 2 
        fileInfoQueriesJDBCImpl.updateFileDataLevel(fileId, dataLevel);
        final SimpleJdbcTemplate template = new SimpleJdbcTemplate(getDataSource());
        final Map<String, Object> values = template.queryForMap("select * from file_info where file_id=?", fileId);
        assertEquals("2", values.get("level_number").toString());
    }

    public void testUpdateFileDataType() {
        final long fileId = 1;
        final int dataTypeId = 199;
        fileInfoQueriesJDBCImpl.updateFileDataType(fileId, dataTypeId);
        final SimpleJdbcTemplate template = new SimpleJdbcTemplate(getDataSource());
        final Map<String, Object> values = template.queryForMap("select * from file_info where file_id=?", fileId);
        assertEquals("199", values.get("data_type_id").toString());
    }

    public void testUpdateFileDataTypes() {
        final List<FileInfo> fileInfoList = new ArrayList<FileInfo>();
        FileInfo fileInfo = new FileInfo();
        fileInfo.setDataTypeId(3);
        fileInfo.setFileName("level1.txt");
        fileInfo.setArchiveName("intgen.org_OV.level1.12.0.1");
        fileInfoList.add(fileInfo);

        fileInfo = new FileInfo();
        fileInfo.setDataTypeId(4);
        fileInfo.setFileName("level2.txt");
        fileInfo.setArchiveName("intgen.org_OV.level2.12.0.1");
        fileInfoList.add(fileInfo);

        fileInfo = new FileInfo();
        fileInfo.setDataTypeId(5);
        fileInfo.setFileName("level3.txt");
        fileInfo.setArchiveName("intgen.org_OV.level3.12.0.1");
        fileInfoList.add(fileInfo);
        fileInfoQueriesJDBCImpl.updateFileDatTypes(fileInfoList);


        final Map<String, Integer> expectedData = new HashMap<String, Integer>();
        expectedData.put("level1.txt", 3);
        expectedData.put("level2.txt", 4);
        expectedData.put("level3.txt", 5);

        final JdbcTemplate template = new JdbcTemplate(getDataSource());
        template.query("select file_name,data_type_id from file_info where file_name in ('level1.txt','level2.txt','level3.txt')", new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                assertEquals(expectedData.get(rs.getString("file_name")), new Integer(rs.getInt("data_type_id")));
            }
        });
    }

    public void testGetFileForFileId() {
        final FileInfo fileInfo = fileInfoQueriesJDBCImpl.getFileForFileId(1L);
        assertNotNull(fileInfo);
        assertEquals(1, fileInfo.getId().longValue());
        assertEquals("broad.mit.edu_GBM.HT_HG-U133A.1.sdrf.txt", fileInfo.getFileName());
    }

    public void testGetLatestArchiveWithFile() {
        final FileInfo fileInfo = new FileInfo();
        fileInfo.setId(1L);
        final Archive archive = fileInfoQueriesJDBCImpl.getLatestArchiveContainingFile(fileInfo);
        assertNotNull(archive);
        assertEquals("intgen.org_OV.bio.12.0.1", archive.getRealName());
        assertEquals(new Long(2), archive.getId());
    }

    public void testGetSdrfFilePathForExperiment() {
        final String path = fileInfoQueriesJDBCImpl.getSdrfFilePathForExperiment("hms.harvard.edu", "HG-U133A_2", "GBM");
        assertEquals("/broad.mit.edu_GBM.HT_HG-U133A.1.sdrf.txt", path);

        assertNull(fileInfoQueriesJDBCImpl.getSdrfFilePathForExperiment("hms.harvard.edu", "HG-U133A_2", "Hello"));
    }


    public void testAddFiles() {
        final FileInfo testFile1 = new FileInfo();

        testFile1.setFileName("intgen.org_OV.bio.12.0.0.txt");
        testFile1.setFileSize(13456L);
        testFile1.setDataLevel(1);
        testFile1.setDataTypeId(1);
        testFile1.setFileMD5("absdfgfjsj");

        final List<FileInfo> testFiles = new ArrayList<FileInfo>();

        testFiles.add(testFile1);

        final FileInfo testFile2 = new FileInfo();

        testFile2.setFileName("intgen.org_OV.bio.12.0.1.txt");
        testFile2.setFileSize(13473L);
        testFile2.setDataLevel(1);
        testFile2.setDataTypeId(1);
        testFile2.setFileMD5("acatfgfjsj");
        testFiles.add(testFile2);

        fileInfoQueriesJDBCImpl.addFiles(testFiles);

        assertEquals(testFile1, getFileInfoFromDB("intgen.org_OV.bio.12.0.0.txt"));
        assertEquals(testFile2, getFileInfoFromDB("intgen.org_OV.bio.12.0.1.txt"));
    }


    public void testDeleteFiles() {
        final List<Long> fileIds = new ArrayList<Long>();
        fileIds.add(3l);
        fileIds.add(4l);
        fileInfoQueriesJDBCImpl.deleteFiles(fileIds);
        assertNull("Failed to delete fileinfo record", getFileInfoFromDB("delete_1.txt"));
        assertNull("Failed to delete fileinfo record", getFileInfoFromDB("delete_2.txt"));

    }

    public void testGetBCRXMLFiles(){
        final List<String> barcodes = Arrays.asList("patient_1");
        final List<String> actualData = fileInfoQueriesJDBCImpl.getBCRXMLFileLocations(barcodes);
        assertEquals(4, actualData.size());
        final List<String> expectedData = Arrays.asList("/clinical.xml","/biospecimen.xml","/auxiliary.xml","/control.xml");
        assertTrue(actualData.containsAll(expectedData));
    }

    public void testUpdateMafFileLocationToPublic(){
        final String GET_PROTECTED_MAF_FILES_QUERY = "select file_name  " +
                " from archive_info a, file_to_archive fa, file_info f\n" +
                " where f.file_name like '%.maf'\n" +
                " and f.file_id=fa.file_id\n" +
                " and fa.archive_id = a.archive_id" +
                " and a.deploy_status = 'Available'  " +
                " and fa.file_location_url like '%tcga4yeo%'";

        final List<String> actualFilenames = getSimpleJdbcTemplate().getJdbcOperations().queryForList(GET_PROTECTED_MAF_FILES_QUERY, String.class);
        assertEquals(4,actualFilenames.size());
        final List<String> expectedFilenames = Arrays.asList("test_1.maf","test_2.maf","test_3.maf","test_4.maf");
        assertTrue(expectedFilenames.containsAll(actualFilenames));
        final Set<Long> archiveIds = new HashSet<Long>(Arrays.asList(106l,107l));
        fileInfoQueriesJDBCImpl.updateArchiveFilesLocationToPublic(archiveIds);
        final List<String> filesAfterUpdate = getSimpleJdbcTemplate().getJdbcOperations().queryForList(GET_PROTECTED_MAF_FILES_QUERY, String.class);
        assertEquals(0, filesAfterUpdate.size());
    }

    public void testUpdateArchiveFilesLocationToPublic() {

        final String protectedCountSql = "select count(*) from file_to_archive where archive_id in (106, 107) and file_location_url like '%tcga4yeo%'";
        final String publicCountSql = protectedCountSql.replace("tcga4yeo", "anonymous");

        final int protectedCountBefore = getSimpleJdbcTemplate().queryForInt(protectedCountSql);
        assertEquals(4, protectedCountBefore);

        final int publicCountBefore = getSimpleJdbcTemplate().queryForInt(publicCountSql);
        assertEquals(0, publicCountBefore);

        final Set<Long> archiveIds = new HashSet<Long>();
        archiveIds.add(106L);
        archiveIds.add(107L);

        fileInfoQueriesJDBCImpl.updateArchiveFilesLocationToPublic(archiveIds);

        final int protectedCountAfter = getSimpleJdbcTemplate().queryForInt(protectedCountSql);
        assertEquals(0, protectedCountAfter);

        final int publicCountAfter = getSimpleJdbcTemplate().queryForInt(publicCountSql);
        assertEquals(4, publicCountAfter);
    }

    private FileInfo getFileInfoFromDB(final String filename) {
        final String GET_FILE_INFO = "select file_id,file_name,file_size,level_number, data_type_id, md5 from file_info " +
                "where file_name= ?";
        final List<FileInfo> fileInfoList = getSimpleJdbcTemplate().getJdbcOperations().query(GET_FILE_INFO,
                new ParameterizedRowMapper<FileInfo>() {
                    @Override
                    public FileInfo mapRow(ResultSet resultSet, int i) throws SQLException {
                        final FileInfo fileInfo = new FileInfo();
                        fileInfo.setId(resultSet.getLong("file_id"));
                        fileInfo.setFileSize(resultSet.getLong("file_size"));
                        fileInfo.setDataLevel(resultSet.getInt("level_number"));
                        fileInfo.setDataTypeId(resultSet.getInt("data_type_id"));
                        fileInfo.setFileMD5(resultSet.getString("md5"));
                        fileInfo.setFileName(resultSet.getString("file_name"));
                        return fileInfo;
                    }
                }, new Object[]{filename});
        if (fileInfoList != null && fileInfoList.size() > 0) {
            return fileInfoList.get(0);
        }
        return null;
    }


    private FileInfo mockFileInfo() {
        final FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName("newfile");
        fileInfo.setDataLevel(1);
        fileInfo.setDataTypeId(1);
        fileInfo.setFileMD5("md5forNewFile");
        fileInfo.setFileSize(1024L);
        fileInfo.setRevision(1L);
        return fileInfo;
    }



}
