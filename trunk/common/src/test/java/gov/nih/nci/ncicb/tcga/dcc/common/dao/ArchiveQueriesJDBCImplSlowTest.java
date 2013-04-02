/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human 
 * readable source code form and machine readable, binary, object code form (the "caBIG 
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ArchiveType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.ArchiveQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.web.ArchiveQueryRequest;
import org.apache.commons.lang.StringUtils;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * DBUnit tests for ArchiveQueriesJDBCImpl.
 *
 * @author Jeyanthi Thangiah
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ArchiveQueriesJDBCImplSlowTest extends DBUnitTestCase {

    private static final String SAMPLES_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "archive_testData.xml";
    private static final String PROPERTIES_FILE = "unittest.properties";
    private static final String PROTECTED_AVAILABLE_BIO_ARCHIVES_COUNT = "select count (*) from archive_info a, disease d where a.deploy_location like '%tcga4yeo%.bio.%' and a.deploy_status='Available' and a.disease_id=d.disease_id";
    private static final String PUBLIC_AVAILABLE_BIO_ARCHIVES_COUNT = PROTECTED_AVAILABLE_BIO_ARCHIVES_COUNT.replace("tcga4yeo", "anonymous");

    private ArchiveQueriesJDBCImpl queries;
    private SimpleJdbcTemplate sjdbc;


    public ArchiveQueriesJDBCImplSlowTest() {
        super(SAMPLES_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    protected DatabaseOperation getTearDownOperation() {
        return DatabaseOperation.DELETE_ALL;
    }

    @Override
    protected void setUpDatabaseConfig(final DatabaseConfig config) {
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new OracleDataTypeFactory());
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        queries = new ArchiveQueriesJDBCImpl();
        queries.setDataSource(getDataSource());
        sjdbc = new SimpleJdbcTemplate(queries.getDataSource());
    }

    public void testGetMatchingArchives() {
        ArchiveQueryRequest queryParams = new ArchiveQueryRequest();
        queryParams.setCenter("4");
        queryParams.setPlatform("1");
        List<Archive> archiveEntries = queries.getMatchingArchives(queryParams);
        assertEquals(1, archiveEntries.size());
        checkArchive(archiveEntries.get(0), 1L, 1, "1", "0");
    }

    public void testGetMatchingArchivesForSubmissionDate() {
        ArchiveQueryRequest queryParams = new ArchiveQueryRequest();
        queryParams.setDateStart("12/13/08");
        queryParams.setDateEnd("12/13/09");
        List<Archive> archiveEntries = queries.getMatchingArchives(queryParams);
        assertEquals(1, archiveEntries.size());
        assertEquals("available_latest", archiveEntries.get(0).getRealName());
        assertEquals(new Integer(1), archiveEntries.get(0).getDataLevel());
        checkArchive(archiveEntries.get(0), 1L, 1, "1", "0");

        // non-matching dates
        queryParams.setDateStart("12/13/04");
        queryParams.setDateEnd("12/13/05");
        archiveEntries = queries.getMatchingArchives(queryParams);
        assertEquals(0, archiveEntries.size());
    }

    public void testGetMatchingArchivesForFileName() {
        ArchiveQueryRequest queryParams = new ArchiveQueryRequest();
        queryParams.setFileName("Test file for");
        List<Archive> archiveEntries = queries.getMatchingArchives(queryParams);
        assertEquals(1, archiveEntries.size());
        assertEquals((Long) 1L, archiveEntries.get(0).getId());
        assertEquals("available_latest", archiveEntries.get(0).getRealName());
        checkArchive(archiveEntries.get(0), 1L, 1, "1", "0");

        queryParams.setFileName("badfilename");
        archiveEntries = queries.getMatchingArchives(queryParams);
        assertEquals(0, archiveEntries.size());
    }


    public void testGetArchive() {
        Archive testArchive = queries.getArchive(1);
        assertNotNull(testArchive);
        assertEquals("available_latest", testArchive.getRealName());
        checkArchive(testArchive, 1L, 1, "1", "0");
        assertEquals("somewhere else!", testArchive.getSecondaryDeployLocation());
    }

    public void testGetArchiveForTime() {
        Archive testArchive = queries.getArchive(1);
        assertNotNull(testArchive);
        assertEquals("2009-01-28 12:36:12.87", testArchive.getDateAdded().toString());
        checkArchive(testArchive, 1L, 1, "1", "0");
    }


    public void testGetLatestVersionArchive() {
        Archive testArchive = queries.getLatestVersionArchive(createFakeArchive());
        assertNotNull(testArchive);
        assertEquals("C:/tcgafiles/", testArchive.getDeployLocation());
        checkArchive(testArchive, 1L, 1, "1", "0");
    }

    public void testGetLatestVersionArchiveNotAvailable() {
        Archive testArchive = queries.getArchive(9);
        assertNull(queries.getLatestVersionArchive(testArchive));
    }

    public Archive createFakeArchive() {
        Archive fakeArchive = new Archive();
        Center center = new Center();
        Platform platform = new Platform();
        Tumor tumor = new Tumor();
        fakeArchive.setArchiveFile(new File("center_tumor.platform.type.1.0.0.tar.gz"));
        fakeArchive.setTumorType("tumorType");
        fakeArchive.setDomainName("domainName");
        platform.setPlatformId(1);
        platform.setPlatformName("platformName");
        fakeArchive.setThePlatform(platform);
        center.setCenterId(4);
        center.setCenterType("centerType");
        center.setCenterName("centerName");
        fakeArchive.setTheCenter(center);
        tumor.setTumorId(3);
        tumor.setTumorName("tumorName");
        fakeArchive.setTheTumor(tumor);
        fakeArchive.setDeployLocation(SAMPLES_FOLDER + " qclive/archiveDeployer/center_tumor.platform.type.1.0.0" + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        fakeArchive.setSerialIndex("1");
        fakeArchive.setArchiveTypeId(2);
        fakeArchive.setDeployStatus(Archive.STATUS_UPLOADED);
        fakeArchive.setRevision("1");
        fakeArchive.setSeries("1");
        return fakeArchive;
    }

    public void testAddArchive() {
        final Archive newArchive = createFakeArchive();
        newArchive.setSecondaryDeployLocation("Gallifrey");

        final long newArchiveId = queries.addArchive(newArchive);
        final long currentSequenceVal = sjdbc.queryForLong("SELECT archive_seq.CURRVAL FROM DUAL");
        assertEquals(currentSequenceVal, newArchiveId);

        assertEquals("Gallifrey", sjdbc.queryForObject("select secondary_deploy_location from archive_info where archive_id=?",
                String.class, newArchiveId));
    }

    public void testAddLogToArchiveEntry() {
        int log_id = sjdbc.queryForInt("SELECT process_log_seq.nextVal FROM DUAL");
        queries.addLogToArchiveEntry(1L, log_id);
        int test_log_id = sjdbc.queryForInt("select max(log_id) from log_to_archives where archive_id=1");
        assertEquals(log_id, test_log_id);
    }

    public void testGetArchiveIdByName() {
        long test_archive_id = queries.getArchiveIdByName("invalid");
        assertEquals(5L, test_archive_id);
    }

    public void testUpdateDeployLocation() {
        Archive newArchive = new Archive();
        newArchive.setId(2L);
        newArchive.setDeployLocation("Fake deploy location");
        queries.updateDeployLocation(newArchive);
        String test_deploy_location = sjdbc.queryForObject("select deploy_location from archive_info where archive_id=2", String.class);
        assertEquals("Fake deploy location", test_deploy_location);
    }

    public void testUpdateArchiveStatus() {
        Archive fakeArchive = createFakeArchive();
        fakeArchive.setId(1L);
        queries.updateArchiveStatus(fakeArchive);
        String test_deploy_status = sjdbc.queryForObject("select deploy_status from archive_info where archive_id=1", String.class);
        assertEquals(Archive.STATUS_UPLOADED, test_deploy_status);
    }

    public void testSetToLatest() {
        // verify archive 1 is the latest
        int numRows = sjdbc.queryForInt("select count(*) from archive_info where archive_id=1 and is_latest=1");
        assertEquals(1, numRows);

        Archive fakeArchive = createFakeArchive();
        fakeArchive.setId(4L);
        queries.setToLatest(fakeArchive);
        numRows = sjdbc.queryForInt("select count(*) from archive_info where archive_id=1 and is_latest=1");
        assertEquals("Archive with id 1 should no longer be latest", 0, numRows);
        numRows = sjdbc.queryForInt("select count(*) from archive_info where archive_id=4 and is_latest=1");
        assertEquals("Archive with id 4 should be latest", 1, numRows);
    }

    public void testSetToLatestLoaded() {
        int latest_archive_id = sjdbc.queryForInt("select archive_id from archive_info where is_latest_loaded=1");
        assertEquals(4, latest_archive_id);
        Archive fakeArchive = createFakeArchive();
        fakeArchive.setId(3L);
        queries.setToLatestLoaded(fakeArchive);
        int test_archive_id = sjdbc.queryForInt("select archive_id from archive_info where is_latest_loaded=1");
        assertEquals(3, test_archive_id);
    }

    public void testUpdateAddedDate() throws ParseException {
        Date updateDate = new SimpleDateFormat("dd.MM.yyyy").parse("21.05.2010");

        queries.updateAddedDate(3L, updateDate);
        Date test_date_added = sjdbc.queryForObject("select date_added from archive_info where archive_id=3", Date.class);
        assertEquals(updateDate, test_date_added);
    }

    public void testSetArchiveInitialSize() {
        queries.setArchiveInitialSize(1L, 1024);
        int test_size = sjdbc.queryForInt("select initial_size_kb from archive_info where archive_id=1");
        assertEquals(1024, test_size);
    }


    public void testSetArchiveFinalSize() {
        queries.setArchiveFinalSize(4L, 16384);
        int test_size = sjdbc.queryForInt("select final_size_kb from archive_info where archive_id=4");
        assertEquals(16384, test_size);
    }

    public void testGetArchiveSize() {
        queries.setArchiveFinalSize(5L, 7168);
        assertEquals(7168l, queries.getArchiveSize(5L));
        queries.setArchiveFinalSize(5L, 0);
        queries.setArchiveInitialSize(5L, 9216);
        assertEquals(9216l, queries.getArchiveSize(5L));
    }

    public void testSearchDataType() {
        ArchiveQueryRequest queryRequest = new ArchiveQueryRequest();
        queryRequest.setDataType("10");
        List<Archive> matchingArchives = queries.getMatchingArchives(queryRequest);
        assertEquals(1, matchingArchives.size());
        assertEquals((Long) 1L, matchingArchives.get(0).getId());
        assertEquals("aDataType", matchingArchives.get(0).getDataType());
    }

    public void testSearchMultipleDataTypes() {
        ArchiveQueryRequest queryRequest = new ArchiveQueryRequest();
        queryRequest.setDataType("11,12");
        List<Archive> matchingArchives = queries.getMatchingArchives(queryRequest);
        assertEquals(2, matchingArchives.size());
        assertEquals((Long) 6L, matchingArchives.get(0).getId());
        assertEquals((Long) 7L, matchingArchives.get(1).getId());
        assertEquals("bDataType", matchingArchives.get(0).getDataType());
        assertEquals("cDataType", matchingArchives.get(1).getDataType());
    }

    public void testSearchDataTypeNoArchives() {
        ArchiveQueryRequest queryRequest = new ArchiveQueryRequest();
        queryRequest.setDataType("13");
        List<Archive> matchingArchives = queries.getMatchingArchives(queryRequest);
        assertEquals(0, matchingArchives.size());
    }

    public void testGetMaxRevisionNoOtherRevisions() {
        final Archive archive = new Archive();
        archive.setDomainName("test.org");
        archive.setTumorType("TUM");
        archive.setPlatform("bPlatform");
        archive.setArchiveType("type");
        archive.setSerialIndex("200"); // no archives in db with this serial index for this experiment and archive type

        // should return -1 whether we constrain to available archives or not
        assertEquals(new Long(-1), queries.getMaxRevisionForArchive(archive, true));
        assertEquals(new Long(-1), queries.getMaxRevisionForArchive(archive, false));
    }

    public void testGetMaxRevisionRevisionsNotAvailable() {
        final Archive archive = new Archive();
        archive.setDomainName("test.org");
        archive.setTumorType("TUM");
        archive.setPlatform("bPlatform");
        archive.setArchiveType("type");
        archive.setSerialIndex("101"); // the only archive in the db for this serial index and experiment/type is Invalid
        // if all archives considered, find revision 2
        assertEquals(new Long(2), queries.getMaxRevisionForArchive(archive, false));
        // if only available archives considered, find nothing so -1
        assertEquals(new Long(-1), queries.getMaxRevisionForArchive(archive, true));
    }

    public void testGetMaxRevision() {
        final Archive archive = new Archive();
        archive.setDomainName("test.org");
        archive.setTumorType("TUM");
        archive.setPlatform("bPlatform");
        archive.setArchiveType("type");
        archive.setSerialIndex("100");
        // highest revision of any status is 6, but highest Available revision is 5
        assertEquals(new Long(6), queries.getMaxRevisionForArchive(archive, false));
        assertEquals(new Long(5), queries.getMaxRevisionForArchive(archive, true));
    }

    public void testUpdateSecondaryDeployLocation() {
        final Archive archive = new Archive();
        archive.setId(2L);
        archive.setSecondaryDeployLocation("221B Baker Street");

        queries.updateSecondaryDeployLocation(archive);
        final String secondaryLocation = sjdbc.queryForObject("select secondary_deploy_location from archive_info where archive_id=2", String.class);
        assertEquals("221B Baker Street", secondaryLocation);
    }

    private void checkArchive(final Archive archive, final Long expectedId, final Integer expectedLevel,
                              final String expectedSerialIndex, final String expectedRevision) {
        // make sure things are set in the archive
        assertEquals(expectedId, archive.getId());
        assertEquals(expectedLevel, archive.getDataLevel());
        assertEquals(expectedSerialIndex, archive.getSerialIndex());
        assertEquals(expectedRevision, archive.getRevision());
        assertFalse(archive.getId() == 0);
        assertNotNull(archive.getArchiveType());
        assertNotNull(archive.getArchiveFile());
        assertNotNull(archive.getRealName());
        assertNotNull(archive.getDomainName());
        assertNotNull(archive.getTumorType());
        assertNotNull(archive.getPlatform());
        assertNotNull(archive.getSerialIndex());
        assertNotNull(archive.getRevision());
        assertNotNull(archive.getDataType());
        assertNotNull(archive.getDateAdded());
        assertNotNull(archive.getDeployStatus());
        assertNotNull(archive.getDeployLocation());
        assertNotNull(archive.getThePlatform());
        assertNotNull(archive.getTheTumor());
        assertNotNull(archive.getTheCenter());
        assertNotNull(archive.getArchiveTypeId());
        assertNotNull(archive.getDisplayVersion());
        assertNotNull(archive.getExperimentType());
        assertTrue(archive.isDataTypeCompressed());

    }

    public void testGetSDRFDeployLocation() {
        String sdrf = queries.getSdrfDeployLocation("test.org", "aPlatform", "TUM");
        assertTrue(StringUtils.isNotEmpty(sdrf));
        assertEquals("/tcgafiles/sdrf", sdrf);
    }

    public void testFailGetSDRFDeployLocation() {
        try {
            queries.getSdrfDeployLocation("test.org", "3", "TEST");
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof EmptyResultDataAccessException);
        }

    }

    public void testGetCenterByDomainNameAndPlatformName() {
        Center center = queries.getCenterByDomainNameAndPlatformName("test.org", "aPlatform");
        assertEquals((int) center.getCenterId(), 4);
        assertEquals(center.getCenterName(), "test.org");
    }

    public void testFailGetCenterByDomainNameAndPlatformName() {
        try {
            queries.getCenterByDomainNameAndPlatformName("badCenterName", "aPlatform");
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof EmptyResultDataAccessException);
        }
    }

    public void testGetFilesForArchive() {
        List<FileInfo> fileList = queries.getFilesForArchive(6L);
        FileInfo info = fileList.get(0);
        assertEquals("archive_bDataType_File", info.getFileName());
    }

    public void testGetLatestArchiveId() {
        Long archiveId = queries.getLatestArchiveId("test.org_TUM.bPlatform.type.100");
        assertEquals(archiveId, new Long(100));

    }

    public void testFailGetLatestArchiveId() {
        Long archiveId = queries.getLatestArchiveId("test.org_TUM.bPlatform.type.102");
        assertNull(archiveId);

    }

    @Test
    public void testGetAllArchiveTypes() {

        final List<ArchiveType> archiveTypes = queries.getAllArchiveTypes();
        assertNotNull(archiveTypes);
        assertEquals(5, archiveTypes.size());

        for(final ArchiveType archiveType : archiveTypes) {

            assertNotNull(archiveType);

            final int archiveTypeId = archiveType.getArchiveTypeId();
            switch(archiveTypeId) {
                case 1:
                    assertEquals("Level_1", archiveType.getArchiveType());
                    assertEquals(new Integer(1), archiveType.getDataLevel());
                    break;
                case 2:
                    assertEquals("type", archiveType.getArchiveType());
                    assertEquals(new Integer(1), archiveType.getDataLevel());
                    break;
                case 6:
                    assertEquals("mage-tab", archiveType.getArchiveType());
                    assertEquals(new Integer(6), archiveType.getDataLevel());
                    break;
                case 7:
                    assertEquals("level2", archiveType.getArchiveType());
                    assertEquals(new Integer(2), archiveType.getDataLevel());
                    break;

                case 10:
                    assertEquals("z", archiveType.getArchiveType());
                    assertNull(archiveType.getDataLevel());
                    break;
                default:
                    fail("Unexpected archive type Id: " + archiveTypeId);
            }
        }
    }

    public void testGetArchivesWithArchiveType() {
        ArchiveQueryRequest archiveQueryRequest = new ArchiveQueryRequest();
        archiveQueryRequest.setArchiveType("2");

        final List<Archive> latestMageTabArchives = queries.getMatchingArchives(archiveQueryRequest);
        // search limits to latest archive that have files...
        assertEquals(3, latestMageTabArchives.size());

    }

    public void testGetArchivesWithMultipleArchiveTypes() {
        ArchiveQueryRequest archiveQueryRequest = new ArchiveQueryRequest();
        archiveQueryRequest.setArchiveType("2,6");

        final List<Archive> archives = queries.getMatchingArchives(archiveQueryRequest);
        // search limits to latest archive that have files...
        assertEquals(3, archives.size());

    }

    public void testGetArchivesWithArchiveTypeNone() {
        ArchiveQueryRequest archiveQueryRequest = new ArchiveQueryRequest();
        archiveQueryRequest.setArchiveType("123"); // does not exist

        final List<Archive> archives = queries.getMatchingArchives(archiveQueryRequest);
        assertEquals(0, archives.size());
    }


    public void testGetMagetabArchives() {
        final List<Archive> archives = queries.getMagetabArchives();
        assertEquals(1, archives.size());
        assertEquals("cgcsdrfArchive", archives.get(0).getRealName());

    }

    public void testGetAllAvailableProtectedBioArchives() {

        final List<Archive> archives = queries.getAllAvailableProtectedBioArchives();
        assertNotNull(archives);
        assertEquals(2, archives.size());

        for (final Archive archive : archives) {

            final Long archiveId = archive.getId();

            if (archiveId == 201) {
                assertEquals("/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/tumor/ov/bcr/intgen.org/bio/clin/intgen.org_OV.bio.Level_1.24.20.0.tar.gz",
                        archive.getDeployLocation());
                assertEquals("OV", archive.getTumorType());

            } else if (archiveId == 202) {
                assertEquals("/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/tumor/ov/bcr/intgen.org/bio/clin/intgen.org_OV.bio.Level_1.24.21.0.tar.gz",
                        archive.getDeployLocation());
                assertEquals("OV", archive.getTumorType());

            } else {
                fail("Unexpected archive Id: " + archiveId);
            }
        }
    }

    public void testGetMafArchives(){
        final List<Archive> archives = queries.getProtectedMafArchives();
        assertEquals(2, archives.size());
        final List<String> expectedArchives = Arrays.asList("broad.mit.edu_BLCA.IlluminaGA_DNASeq.Level_2.0.0.0","broad.mit.edu_BLCA.IlluminaGA_DNASeq.Level_2.0.1.0");
        final List<String> actualArchives = new ArrayList<String>();
        for(final Archive archive: archives){
            actualArchives.add(archive.getArchiveName());
        }
        assertTrue(expectedArchives.containsAll(actualArchives));
    }

    public void testUpdateMafArchivesLocation(){
        List<Archive> archives = queries.getProtectedMafArchives();
        assertEquals(2, archives.size());
        Set<Long> archiveIds = new HashSet<Long>();
        for(final Archive archive: archives){
            archiveIds.add(archive.getId());
        }
        queries.updateArchivesLocationToPublic(archiveIds);
        archives = queries.getProtectedMafArchives();
        assertEquals(0, archives.size());
    }

    public void testUpdateArchivesLocationToPublic() {

        final int numberOfProtectedBioArchivesBefore = getSimpleJdbcTemplate().queryForInt(PROTECTED_AVAILABLE_BIO_ARCHIVES_COUNT);
        assertEquals(2, numberOfProtectedBioArchivesBefore);

        final int numberOfPublicBioArchivesBefore = getSimpleJdbcTemplate().queryForInt(PUBLIC_AVAILABLE_BIO_ARCHIVES_COUNT);
        assertEquals(1, numberOfPublicBioArchivesBefore);

        final Set<Long> successfullyCopiedArchiveIds = new HashSet<Long>();
        successfullyCopiedArchiveIds.add(201L);
        successfullyCopiedArchiveIds.add(202L);

        queries.updateArchivesLocationToPublic(successfullyCopiedArchiveIds);

        final int numberOfProtectedBioArchivesAfter = getSimpleJdbcTemplate().queryForInt(PROTECTED_AVAILABLE_BIO_ARCHIVES_COUNT);
        assertEquals(0, numberOfProtectedBioArchivesAfter);

        final int numberOfPublicBioArchivesAfter = getSimpleJdbcTemplate().queryForInt(PUBLIC_AVAILABLE_BIO_ARCHIVES_COUNT);
        assertEquals(3, numberOfPublicBioArchivesAfter);
    }

    public void testUpdateArchivesLocationToPublicOneArchiveNotSuccessfullyCopied() {

        final int numberOfProtectedBioArchivesBefore = getSimpleJdbcTemplate().queryForInt(PROTECTED_AVAILABLE_BIO_ARCHIVES_COUNT);
        assertEquals(2, numberOfProtectedBioArchivesBefore);

        final int numberOfPublicBioArchivesBefore = getSimpleJdbcTemplate().queryForInt(PUBLIC_AVAILABLE_BIO_ARCHIVES_COUNT);
        assertEquals(1, numberOfPublicBioArchivesBefore);

        final Set<Long> successfullyCopiedArchiveIds = new HashSet<Long>();
        successfullyCopiedArchiveIds.add(201L);

        queries.updateArchivesLocationToPublic(successfullyCopiedArchiveIds);

        final int numberOfProtectedBioArchivesAfter = getSimpleJdbcTemplate().queryForInt(PROTECTED_AVAILABLE_BIO_ARCHIVES_COUNT);
        assertEquals(1, numberOfProtectedBioArchivesAfter);

        final int numberOfPublicBioArchivesAfter = getSimpleJdbcTemplate().queryForInt(PUBLIC_AVAILABLE_BIO_ARCHIVES_COUNT);
        assertEquals(2, numberOfPublicBioArchivesAfter);
    }
}