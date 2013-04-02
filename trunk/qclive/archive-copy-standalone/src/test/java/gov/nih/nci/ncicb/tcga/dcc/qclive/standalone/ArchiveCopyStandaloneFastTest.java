/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.standalone;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileInfoQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDDAO;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.web.FileInfoQueryRequest;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.Logger;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRIDProcessor;
import org.apache.log4j.Level;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static gov.nih.nci.ncicb.tcga.dcc.ConstantValues.PROTECTED_DIR;
import static gov.nih.nci.ncicb.tcga.dcc.ConstantValues.PUBLIC_DIR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
/**
 * ArchiveCopyStandalone unit test
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class ArchiveCopyStandaloneFastTest {

    private static final String SAMPLE_DIR = Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String SOURCE_FILE_MISSING_DIR = SAMPLE_DIR + "sourceFileMissing" + File.separator + "tcga4yeo" + File.separator;
    private static final String DESTINATION_FILE_EXISTS_DIR = SAMPLE_DIR + "destinationFileExists" + File.separator + "tcga4yeo" + File.separator;
    private static final String DESTINATION_GOOD_DIR = SAMPLE_DIR + "destinationGood" + File.separator + "tcga4yeo" + File.separator;
    private static final String appContextFile = "applicationContext-test.xml";
    private ArchiveCopyStandalone archiveCopyStandalone;
    private Mockery mockery;
    private Logger mockLogger;
    private ArchiveQueries mockDccCommonArchiveQueries;
    private ArchiveQueries mockDiseaseArchiveQueries;
    private FileInfoQueries mockDccCommonFileInfoQueries;
    private FileInfoQueries mockDiseaseFileInfoQueries;
    private BCRIDProcessor mockBCRIDProcessor;
    private UUIDDAO mockDccCommonUuiddao;
    private UUIDDAO mockDiseaseUuiddao;

    @Before
    public void setUp() {
        mockery =  new Mockery();
        mockLogger = mockery.mock(Logger.class);
        final ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(appContextFile);
        archiveCopyStandalone = (ArchiveCopyStandalone)applicationContext.getBean("archiveCopyStandalone");
        archiveCopyStandalone.setLogger(mockLogger);

        mockDccCommonArchiveQueries = mockery.mock(ArchiveQueries.class, "commonArchive");
        mockDiseaseArchiveQueries = mockery.mock(ArchiveQueries.class, "diseaseArchive");
        mockDccCommonFileInfoQueries = mockery.mock(FileInfoQueries.class, "commonFileInfo");
        mockDiseaseFileInfoQueries = mockery.mock(FileInfoQueries.class, "diseaseFileInfo");
        mockBCRIDProcessor = mockery.mock(BCRIDProcessor.class);
        mockDccCommonUuiddao = mockery.mock(UUIDDAO.class, "dccCommonUuidDao");
        mockDiseaseUuiddao = mockery.mock(UUIDDAO.class, "diseaseUuidDao");

        archiveCopyStandalone.setDccCommonFileInfoQueries(mockDccCommonFileInfoQueries);
        archiveCopyStandalone.setDiseaseFileInfoQueries(mockDiseaseFileInfoQueries);
        archiveCopyStandalone.setDiseaseArchiveQueries(mockDiseaseArchiveQueries);
        archiveCopyStandalone.setDccCommonArchiveQueries(mockDccCommonArchiveQueries);
        archiveCopyStandalone.setBcrIdProcessor(mockBCRIDProcessor);
        archiveCopyStandalone.setDccCommonUUIDQueries(mockDccCommonUuiddao);
        archiveCopyStandalone.setDiseaseUUIDQueries(mockDiseaseUuiddao);
        archiveCopyStandalone.setDryRun(false);
        assertNotNull(archiveCopyStandalone);
        assertNotNull(archiveCopyStandalone.getDccCommonArchiveQueries());
        assertNotNull(archiveCopyStandalone.getDiseaseArchiveQueries());
        assertNotNull(archiveCopyStandalone.getDccCommonFileInfoQueries());
        assertNotNull(archiveCopyStandalone.getDiseaseFileInfoQueries());
        assertNotNull(archiveCopyStandalone.getBcrIdProcessor());
        assertNotNull(mockDccCommonUuiddao);
        assertNotNull(mockDiseaseUuiddao);
        assertFalse(archiveCopyStandalone.isDryRun());
    }

    @After
    public void tearDown(){
        deleteFilesFromPublicDir();
    }
    
    @Test
    public void testCopyArchivesToPublicLocationWhenArchiveDoesNotExist() {

        final String explodedDir = File.separator+"tcga4yeo"+File.separator+"doesNotExist";
        final String deployLocation = explodedDir + ".tar.gz";
        final String md5File = deployLocation + ".md5";

        mockery.checking(new Expectations() {{

            one(mockLogger).log(Level.INFO, "Starting file system copy of all archives to the public location ...");
            one(mockLogger).log(Level.INFO, "Starting copy of archive #1 ...");
            one(mockLogger).log(Level.ERROR, "Protected (Source) File '" + deployLocation + "' [Id:1] does not exist - tar/tar.gz, md5 and exploded archive will not be copied to the public location");
            one(mockLogger).log(Level.ERROR, "Protected (Source) File '" + md5File + "' [Id:1] does not exist - tar/tar.gz, md5 and exploded archive will not be copied to the public location");
            one(mockLogger).log(Level.ERROR, "Protected (Source) File '" + explodedDir + "' [Id:1] does not exist - tar/tar.gz, md5 and exploded archive will not be copied to the public location");
        }});

        final Set<Long> archiveIds = archiveCopyStandalone.copyArchivesToPublicLocation(makeArchives(deployLocation));
        assertNotNull(archiveIds);
        assertEquals(0, archiveIds.size());
    }

    @Test
    public void testCopyArchivesToPublicLocationWhenArchiveNull() {

        final List<Archive> archives = new ArrayList<Archive>();
        archives.add(null);

        mockery.checking(new Expectations() {{
            
            one(mockLogger).log(Level.INFO, "Starting file system copy of all archives to the public location ...");
            one(mockLogger).log(Level.INFO, "Starting copy of archive #1 ...");
            one(mockLogger).log(Level.ERROR, "Archive is null - tar/tar.gz, md5 and exploded archive will not be copied to the public location");
        }});

        final Set<Long> archiveIds = archiveCopyStandalone.copyArchivesToPublicLocation(archives);
        assertNotNull(archiveIds);
        assertEquals(0, archiveIds.size());
    }

    @Test
    public void testCopyArchivesToPublicLocationWhenArchiveHasInvalidTarOrTarGzPrivateDeployLocation() {

        final String deployLocation = "/tcga4yeo/invalidExtension.zip";

        mockery.checking(new Expectations() {{
            one(mockLogger).log(Level.INFO, "Starting file system copy of all archives to the public location ...");
            one(mockLogger).log(Level.INFO, "Starting copy of archive #1 ...");
            one(mockLogger).log(Level.ERROR, "Archive file '" + deployLocation + "' [Id:1] is not a valid tar or tar.gz protected deploy location - tar/tar.gz, md5 and exploded archive will not be copied to the public location");
        }});

        final Set<Long> archiveIds = archiveCopyStandalone.copyArchivesToPublicLocation(makeArchives(deployLocation));
        assertNotNull(archiveIds);
        assertEquals(0, archiveIds.size());
    }

    @Test
    public void testCopyArchivesToPublicLocationWhenSourceFileMissing() {

        final String deployLocation = SOURCE_FILE_MISSING_DIR + "source.tar.gz";

        mockery.checking(new Expectations() {{
            one(mockLogger).log(Level.INFO, "Starting file system copy of all archives to the public location ...");
            one(mockLogger).log(Level.INFO, "Starting copy of archive #1 ...");
            one(mockLogger).log(with(Level.ERROR), with(expectedLogErrorForMissingSourceFile()));
        }});

        final Set<Long> archiveIds = archiveCopyStandalone.copyArchivesToPublicLocation(makeArchives(deployLocation));
        assertNotNull(archiveIds);
        assertEquals(0, archiveIds.size());
    }

    @Test
    public void testCopyArchivesToPublicLocationWhenDestinationAlreadyExists() {

        final String deployLocation = DESTINATION_FILE_EXISTS_DIR + "source.tar.gz";

        mockery.checking(new Expectations() {{
            one(mockLogger).log(Level.INFO, "Starting file system copy of all archives to the public location ...");
            one(mockLogger).log(Level.INFO, "Starting copy of archive #1 ...");
            one(mockLogger).log(with(Level.INFO), with(expectedLogInfoForFileCopy("destinationFileExists", ".tar.gz")));
            one(mockLogger).log(with(Level.INFO), with(expectedLogInfoForExistingDestinationFile(".tar.gz")));
            one(mockLogger).log(with(Level.INFO), with(expectedLogInfoForFileCopy("destinationFileExists", ".tar.gz.md5")));
            one(mockLogger).log(with(Level.INFO), with(expectedLogInfoForFileCopy("destinationFileExists", "")));
        }});

        final Set<Long> archiveIds = archiveCopyStandalone.copyArchivesToPublicLocation(makeArchives(deployLocation));
        assertNotNull(archiveIds);
        assertEquals(1, archiveIds.size());
        assertTrue(archiveIds.contains(new Long(1)));

        assertAndCleanup(deployLocation);
    }

    @Test
    public void testCopyArchivesToPublicLocationGood() {

        final String deployLocation = DESTINATION_GOOD_DIR + "source.tar.gz";

        mockery.checking(new Expectations() {{
            one(mockLogger).log(Level.INFO, "Starting file system copy of all archives to the public location ...");
            one(mockLogger).log(Level.INFO, "Starting copy of archive #1 ...");
            one(mockLogger).log(with(Level.INFO), with(expectedLogInfoForFileCopy("destinationGood", ".tar.gz")));
            one(mockLogger).log(with(Level.INFO), with(expectedLogInfoForFileCopy("destinationGood", ".tar.gz.md5")));
            one(mockLogger).log(with(Level.INFO), with(expectedLogInfoForFileCopy("destinationGood", "")));
        }});

        final Set<Long> archiveIds = archiveCopyStandalone.copyArchivesToPublicLocation(makeArchives(deployLocation));
        assertNotNull(archiveIds);
        assertEquals(1, archiveIds.size());
        assertTrue(archiveIds.contains(new Long(1)));

        assertAndCleanup(deployLocation);
    }

    /**
     * Asserts that the expected files have been copied to the public location and delete them.
     *
     * @param deployLocation the tar.gz file deploy location
     */
    private void assertAndCleanup(final String deployLocation) {

        final String expectedPublicDeployLocation = deployLocation.replace("tcga4yeo", "anonymous");
        final String expectedPublicMd5Filename = expectedPublicDeployLocation + ".md5";
        final String expectedPublicExplodedDirName = expectedPublicDeployLocation.substring(0, expectedPublicDeployLocation.indexOf(".tar.gz"));

        final File expectedPublicDeployLocationFile = new File(expectedPublicDeployLocation);
        final File expectedPublicMd5File = new File(expectedPublicMd5Filename);
        final File expectedPublicExplodedDir = new File(expectedPublicExplodedDirName);

        assertTrue(expectedPublicDeployLocationFile.exists());
        assertTrue(expectedPublicMd5File.exists());
        assertTrue(expectedPublicExplodedDir.exists());

        final File[] explodedFiles = expectedPublicExplodedDir.listFiles();
        assertNotNull(explodedFiles);
        assertEquals(1, explodedFiles.length);

        final File explodedFile = explodedFiles[0];
        assertNotNull(explodedFile);
        assertEquals("data.xml", explodedFile.getName());

        //Cleanup public files
        final boolean deletedChildWithSuccess = explodedFile.delete();
        assertTrue(deletedChildWithSuccess);

        final boolean deletedExplodedDirWithSuccess = expectedPublicExplodedDir.delete();
        assertTrue(deletedExplodedDirWithSuccess);

        final boolean deletedTarGzWithSuccess = expectedPublicDeployLocationFile.delete();
        assertTrue(deletedTarGzWithSuccess);

        final boolean deletedMd5WithSuccess = expectedPublicMd5File.delete();
        assertTrue(deletedMd5WithSuccess);
    }

    @Test
    public void testCopyArchivesToPublicLocationGoodDryRun() {

        final String deployLocation = DESTINATION_GOOD_DIR + "source.tar.gz";

        mockery.checking(new Expectations() {{
            one(mockLogger).log(Level.INFO, "Starting file system copy of all archives to the public location ...");
            one(mockLogger).log(Level.INFO, "Starting copy of archive #1 ...");
            one(mockLogger).log(with(Level.INFO), with(expectedLogInfoForFileCopy("destinationGood", ".tar.gz")));
            one(mockLogger).log(with(Level.INFO), with(expectedLogInfoForFileCopy("destinationGood", ".tar.gz.md5")));
            one(mockLogger).log(with(Level.INFO), with(expectedLogInfoForFileCopy("destinationGood", "")));
        }});

        archiveCopyStandalone.setDryRun(true);
        archiveCopyStandalone.copyArchivesToPublicLocation(makeArchives(deployLocation));

        final String expectedPublicDeployLocation = deployLocation.replace("tcga4yeo", "anonymous");
        final String expectedPublicMd5Filename = expectedPublicDeployLocation + ".md5";
        final String expectedPublicExplodedDirName = expectedPublicDeployLocation.substring(0, expectedPublicDeployLocation.indexOf(".tar.gz"));

        final File expectedPublicDeployLocationFile = new File(expectedPublicDeployLocation);
        final File expectedPublicMd5File = new File(expectedPublicMd5Filename);
        final File expectedPublicExplodedDir = new File(expectedPublicExplodedDirName);

        assertFalse(expectedPublicDeployLocationFile.exists());
        assertFalse(expectedPublicMd5File.exists());
        assertFalse(expectedPublicExplodedDir.exists());
    }

    @Test
    public void testUpdateProtectedAvailableBioArchivesLocationToPublicWhenArchivesNull() {

        final Set<Long> successfullyCopiedArchiveIds = new HashSet<Long>();
        successfullyCopiedArchiveIds.add(1L);

        mockery.checking(new Expectations() {{
            one(mockLogger).log(Level.INFO, "Updating available protected bio archive(s) deploy location to public ...");
            one(mockLogger).log(Level.INFO, "Updating DccCommon schema ...");
            one(mockDccCommonArchiveQueries).updateArchivesLocationToPublic(successfullyCopiedArchiveIds);
            one(mockDccCommonFileInfoQueries).updateArchiveFilesLocationToPublic(successfullyCopiedArchiveIds);
        }});

        final Map<String, Set<Long>> diseaseAbbreviations = new HashMap<String, Set<Long>>();

        archiveCopyStandalone.updateProtectedAvailableBioArchivesLocationToPublic(successfullyCopiedArchiveIds, diseaseAbbreviations);
    }

    @Test
    public void testUpdateProtectedAvailableBioArchivesLocationToPublicWhenUnsuccessfulCopyForOneArchive() {

        final Set<Long> successfullyCopiedArchiveIds = new HashSet<Long>();
        successfullyCopiedArchiveIds.add(1L);

        final Set<Long> successfullyCopiedDiseaseArchiveIds = new HashSet<Long>();

        final Map<String, Set<Long>> diseaseAbbreviations = new HashMap<String, Set<Long>>();
        diseaseAbbreviations.put("ABC", successfullyCopiedDiseaseArchiveIds);

        mockery.checking(new Expectations() {{
            one(mockLogger).log(Level.INFO, "Updating available protected bio archive(s) deploy location to public ...");
            one(mockLogger).log(Level.INFO, "Updating DccCommon schema ...");
            one(mockDccCommonArchiveQueries).updateArchivesLocationToPublic(successfullyCopiedArchiveIds);
            one(mockDccCommonFileInfoQueries).updateArchiveFilesLocationToPublic(successfullyCopiedArchiveIds);
            one(mockLogger).log(Level.INFO, "Updating Disease schema: ABC ...");
            one(mockDiseaseArchiveQueries).updateArchivesLocationToPublic(successfullyCopiedDiseaseArchiveIds);
            one(mockDiseaseFileInfoQueries).updateArchiveFilesLocationToPublic(successfullyCopiedDiseaseArchiveIds);
        }});

        archiveCopyStandalone.updateProtectedAvailableBioArchivesLocationToPublic(successfullyCopiedArchiveIds, diseaseAbbreviations);
    }

    @Test
    public void testUpdateProtectedAvailableBioArchivesLocationToPublic() {

        final Set<Long> successfullyCopiedArchiveIds = new HashSet<Long>();
        successfullyCopiedArchiveIds.add(1L);

        final Set<Long> successfullyCopiedDiseaseArchiveIds = new HashSet<Long>();
        successfullyCopiedDiseaseArchiveIds.add(1L);

        final Map<String, Set<Long>> diseaseAbbreviations = new HashMap<String, Set<Long>>();
        diseaseAbbreviations.put("ABC", successfullyCopiedDiseaseArchiveIds);

        mockery.checking(new Expectations() {{
            one(mockLogger).log(Level.INFO, "Updating available protected bio archive(s) deploy location to public ...");
            one(mockLogger).log(Level.INFO, "Updating DccCommon schema ...");
            one(mockDccCommonArchiveQueries).updateArchivesLocationToPublic(successfullyCopiedArchiveIds);
            one(mockDccCommonFileInfoQueries).updateArchiveFilesLocationToPublic(successfullyCopiedArchiveIds);
            one(mockLogger).log(Level.INFO, "Updating Disease schema: ABC ...");
            one(mockDiseaseArchiveQueries).updateArchivesLocationToPublic(successfullyCopiedDiseaseArchiveIds);
            one(mockDiseaseFileInfoQueries).updateArchiveFilesLocationToPublic(successfullyCopiedDiseaseArchiveIds);
        }});

        archiveCopyStandalone.updateProtectedAvailableBioArchivesLocationToPublic(successfullyCopiedArchiveIds, diseaseAbbreviations);
    }

    @Test
    public void testUpdateProtectedAvailableBioArchivesLocationToPublicDryRun() {

        final Set<Long> successfullyCopiedArchiveIds = new HashSet<Long>();
        successfullyCopiedArchiveIds.add(1L);

        final Set<Long> successfullyCopiedDiseaseArchiveIds = new HashSet<Long>();
        successfullyCopiedDiseaseArchiveIds.add(1L);

        final Map<String, Set<Long>> diseaseAbbreviations = new HashMap<String, Set<Long>>();
        diseaseAbbreviations.put("ABC", successfullyCopiedDiseaseArchiveIds);

        mockery.checking(new Expectations() {{
            one(mockLogger).log(Level.INFO, "Updating available protected bio archive(s) deploy location to public ...");
            one(mockLogger).log(Level.INFO, "Updating DccCommon schema ...");
            one(mockLogger).log(Level.INFO, "Updating Disease schema: ABC ...");
        }});

        archiveCopyStandalone.setDryRun(true);
        archiveCopyStandalone.updateProtectedAvailableBioArchivesLocationToPublic(successfullyCopiedArchiveIds, diseaseAbbreviations);
    }

    @Test
    public void copyMafArchivesFromProtectedToPublic(){

        final Set<Long> successfullyCopiedArchiveIds = new HashSet<Long>(Arrays.asList(1l,3l));

        final List<Archive> mafArchives = getMafArchivesList();
        mockery.checking(new Expectations() {{
            one(mockLogger).log(Level.INFO, " Reading protected maf archives location from db...");
            one(archiveCopyStandalone.getDccCommonArchiveQueries()).getProtectedMafArchives();
            will(returnValue(mafArchives));
            one(mockLogger).log(Level.INFO, " Found 3 maf archives.");
            one(mockLogger).log(Level.INFO, "Copying maf archives to public location");
            one(mockLogger).log(Level.INFO, "Starting file system copy of all archives to the public location ...");

            one(mockLogger).log(Level.INFO, "Starting copy of archive #1 ...");
            one(mockLogger).log(Level.INFO, "Copying '"+getFilePath(mafArchives.get(0).getDeployLocation())+"' [archive Id:"+ mafArchives.get(0).getId()+"] to '"+getFilePath(mafArchives.get(0).getDeployLocation().replace(PROTECTED_DIR,PUBLIC_DIR))+"'");
            one(mockLogger).log(Level.INFO, "Copying '"+getFilePath(mafArchives.get(0).getDeployLocation()+".md5")+"' [archive Id:"+ mafArchives.get(0).getId()+"] to '"+getFilePath(mafArchives.get(0).getDeployLocation().replace(PROTECTED_DIR,PUBLIC_DIR)+".md5")+"'");
            one(mockLogger).log(Level.INFO, "Copying '"+getFilePath(mafArchives.get(0).getDeployDirectory())+"' [archive Id:"+ mafArchives.get(0).getId()+"] to '"+getFilePath(mafArchives.get(0).getDeployDirectory().replace(PROTECTED_DIR,PUBLIC_DIR))+"'");

            one(mockLogger).log(Level.INFO, "Starting copy of archive #2 ...");
            one(mockLogger).log(Level.ERROR, "Protected (Source) File '"+mafArchives.get(1).getDeployLocation()+"' [Id:"+ mafArchives.get(1).getId()+"] does not exist - tar/tar.gz, md5 and exploded archive will not be copied to the public location");
            one(mockLogger).log(Level.ERROR, "Protected (Source) File '"+mafArchives.get(1).getDeployLocation()+".md5' [Id:"+ mafArchives.get(1).getId()+"] does not exist - tar/tar.gz, md5 and exploded archive will not be copied to the public location");
            one(mockLogger).log(Level.ERROR, "Protected (Source) File '"+getFilePath(mafArchives.get(1).getDeployDirectory())+"' [Id:"+ mafArchives.get(1).getId()+"] does not exist - tar/tar.gz, md5 and exploded archive will not be copied to the public location");

            one(mockLogger).log(Level.INFO, "Starting copy of archive #3 ...");
            one(mockLogger).log(Level.INFO, "Copying '"+getFilePath(mafArchives.get(2).getDeployLocation())+"' [archive Id:"+ mafArchives.get(2).getId()+"] to '"+getFilePath(mafArchives.get(2).getDeployLocation().replace(PROTECTED_DIR,PUBLIC_DIR))+"'");
            one(mockLogger).log(Level.INFO, "Copying '"+getFilePath(mafArchives.get(2).getDeployLocation()+".md5")+"' [archive Id:"+ mafArchives.get(2).getId()+"] to '"+getFilePath(mafArchives.get(2).getDeployLocation().replace(PROTECTED_DIR,PUBLIC_DIR)+".md5")+"'");
            one(mockLogger).log(Level.INFO, "Copying '"+getFilePath(mafArchives.get(2).getDeployDirectory())+"' [archive Id:"+ mafArchives.get(2).getId()+"] to '"+getFilePath(mafArchives.get(2).getDeployDirectory().replace(PROTECTED_DIR,PUBLIC_DIR))+"'");

            one(mockLogger).log(Level.INFO, "Updating common schema maf archives [1, 3] locations to public...");
            one(mockDccCommonArchiveQueries).updateArchivesLocationToPublic(successfullyCopiedArchiveIds);
            one(mockLogger).log(Level.INFO, "Updating common schema maf file locations  to public...");
            one(mockDccCommonFileInfoQueries).updateArchiveFilesLocationToPublic(successfullyCopiedArchiveIds);

            one(mockLogger).log(Level.INFO, "Updating disease schema[BLCA] maf archives [1]locations to public...");
            one(mockDiseaseArchiveQueries).updateArchivesLocationToPublic(new HashSet<Long>(Arrays.asList(1l)));
            one(mockLogger).log(Level.INFO, "Updating disease schema[BLCA] maf file locations to public...");
            one(mockDiseaseFileInfoQueries).updateArchiveFilesLocationToPublic(new HashSet<Long>(Arrays.asList(1l)));

            one(mockLogger).log(Level.INFO, "Updating disease schema[GBM] maf archives [3]locations to public...");
            one(mockDiseaseArchiveQueries).updateArchivesLocationToPublic(new HashSet<Long>(Arrays.asList(3l)));
            one(mockLogger).log(Level.INFO, "Updating disease schema[GBM] maf file locations to public...");
            one(mockDiseaseFileInfoQueries).updateArchiveFilesLocationToPublic(new HashSet<Long>(Arrays.asList(3l)));

        }});

        archiveCopyStandalone.copyMafArchivesFromProtectedToPublic();
        fileExists(mafArchives.get(0).getDeployLocation().replace(PROTECTED_DIR,PUBLIC_DIR),true);
        fileExists(mafArchives.get(0).getDeployLocation().replace(PROTECTED_DIR, PUBLIC_DIR) + ".md5", true);
        fileExists(mafArchives.get(0).getDeployDirectory().replace(PROTECTED_DIR, PUBLIC_DIR), true);
        fileExists(mafArchives.get(0).getDeployDirectory().replace(PROTECTED_DIR, PUBLIC_DIR) + File.separator + "file1.maf", true);
        fileExists(mafArchives.get(0).getDeployDirectory().replace(PROTECTED_DIR, PUBLIC_DIR) + File.separator + "file2.maf", true);

        fileExists(mafArchives.get(1).getDeployLocation().replace(PROTECTED_DIR, PUBLIC_DIR), false);
        fileExists(mafArchives.get(1).getDeployLocation().replace(PROTECTED_DIR,PUBLIC_DIR)+".md5",false);
        fileExists(mafArchives.get(1).getDeployDirectory().replace(PROTECTED_DIR,PUBLIC_DIR),false);

        fileExists(mafArchives.get(2).getDeployLocation().replace(PROTECTED_DIR,PUBLIC_DIR),true);
        fileExists(mafArchives.get(2).getDeployLocation().replace(PROTECTED_DIR,PUBLIC_DIR)+".md5",true);
        fileExists(mafArchives.get(2).getDeployDirectory().replace(PROTECTED_DIR,PUBLIC_DIR),true);
        fileExists(mafArchives.get(2).getDeployDirectory().replace(PROTECTED_DIR,PUBLIC_DIR)+File.separator+"file1.maf",true);
        fileExists(mafArchives.get(2).getDeployDirectory().replace(PROTECTED_DIR, PUBLIC_DIR) + File.separator + "file2.maf", true);

        deleteFilesFromPublicDir();
    }


    @Test
    public void copyBCRArchivesFromProtectedToPublic() throws Exception{

        final Set<Long> successfullyCopiedArchiveIds = new HashSet<Long>(Arrays.asList(1l));
        final List<Archive> bcrArchives = getBcrArchivesList(SAMPLE_DIR);
        final FileInfoQueryRequest fileInfoQueryRequest = new FileInfoQueryRequest();
        fileInfoQueryRequest.setArchiveId(1);

        final Collection<FileInfo> allFiles = new ArrayList<FileInfo>();
        final FileInfo fileInfo = new FileInfo();
        fileInfo.setFileLocation(bcrArchives.get(0).getDeployDirectory()+File.separator+"nationwidechildrens.org_clinical.TCGA-00-A000.xml");
        fileInfo.setFileName("nationwidechildrens.org_clinical.TCGA-00-A000.xml");
        fileInfo.setId(1l);
        allFiles.add(fileInfo);

        final File bcrXMLFile = new File(fileInfo.getFileLocation());
        final String uuid = "ebedab7c-dd7c-448e-8324-78c86f13be8b";
        mockery.checking(new Expectations() {{
            one(archiveCopyStandalone.getDccCommonArchiveQueries()).getAllAvailableProtectedBioArchives();
            will(returnValue(bcrArchives));
            one(mockLogger).log(Level.INFO, "Found 1 available protected bio archive(s) in dccCommon.");
            one(mockLogger).log(Level.INFO, "Starting file system copy of all archives to the public location ...");

            one(mockLogger).log(Level.INFO, "Starting copy of archive #1 ...");
            one(mockLogger).log(Level.INFO, "Copying '"+getFilePath(bcrArchives.get(0).getDeployLocation())+"' [archive Id:"+ bcrArchives.get(0).getId()+"] to '"+getFilePath(bcrArchives.get(0).getDeployLocation().replace(PROTECTED_DIR,PUBLIC_DIR))+"'");
            one(mockLogger).log(Level.INFO, "Copying '"+getFilePath(bcrArchives.get(0).getDeployLocation()+".md5")+"' [archive Id:"+ bcrArchives.get(0).getId()+"] to '"+getFilePath(bcrArchives.get(0).getDeployLocation().replace(PROTECTED_DIR,PUBLIC_DIR)+".md5")+"'");
            one(mockLogger).log(Level.INFO, "Copying '"+getFilePath(bcrArchives.get(0).getDeployDirectory())+"' [archive Id:"+ bcrArchives.get(0).getId()+"] to '"+getFilePath(bcrArchives.get(0).getDeployDirectory().replace(PROTECTED_DIR,PUBLIC_DIR))+"'");

            one(mockLogger).log(Level.INFO, "Found disease(s) in dccCommon, for which archives have successfully been copied to the public location: [BLCA]");
            one(mockLogger).log(Level.INFO, "Updating available protected bio archive(s) deploy location to public ...");
            one(mockLogger).log(Level.INFO, "Updating Disease schema: BLCA ...");
            one(mockDiseaseArchiveQueries).updateArchivesLocationToPublic(new HashSet<Long>(Arrays.asList(1l)));
            one(mockDiseaseFileInfoQueries).updateArchiveFilesLocationToPublic(new HashSet<Long>(Arrays.asList(1l)));

            one(mockLogger).log(Level.INFO, "Updating DccCommon schema ...");
            one(mockDccCommonArchiveQueries).updateArchivesLocationToPublic(successfullyCopiedArchiveIds);
            one(mockDccCommonFileInfoQueries).updateArchiveFilesLocationToPublic(successfullyCopiedArchiveIds);


            one(mockLogger).log(Level.INFO, "Adding patient UUID and file id relationships for disease BLCA ...");
            one(mockLogger).log(Level.INFO, "Adding patient UUID and file id relationships for archive 1 ...");
            one(mockDccCommonFileInfoQueries).getFilesForArchive(with (any(FileInfoQueryRequest.class)));
            will(returnValue(allFiles));
            one(mockLogger).log(Level.INFO, "Adding patient UUID and file id relationships for file "+bcrXMLFile.getPath()+" ...");
            one(mockBCRIDProcessor).getPatientUUIDfromFile(bcrXMLFile);
            will(returnValue(uuid));
            one(mockLogger).log(Level.INFO, "Adding patient UUID [ "+uuid+"] file id [1] ...");
            one(mockDiseaseUuiddao).addParticipantFileUUIDAssociation(with(any(List.class)));
            one(mockDccCommonUuiddao).addParticipantFileUUIDAssociation(with(any(List.class)));

        }});

        archiveCopyStandalone.copyBCRArchivesFromProtectedToPublic();
        fileExists(bcrArchives.get(0).getDeployLocation().replace(PROTECTED_DIR,PUBLIC_DIR),true);
        fileExists(bcrArchives.get(0).getDeployLocation().replace(PROTECTED_DIR, PUBLIC_DIR) + ".md5", true);
        fileExists(bcrArchives.get(0).getDeployDirectory().replace(PROTECTED_DIR, PUBLIC_DIR), true);
        fileExists(bcrArchives.get(0).getDeployDirectory().replace(PROTECTED_DIR, PUBLIC_DIR) + File.separator + fileInfo.getFileName(), true);

    }

    @Test
    public void copyBCRArchivesFromProtectedMountToPublicMount() throws Exception{
        final String actualRootDir  = "rootDir"+File.separator;
        final String mountRootDir = getFilePath(SAMPLE_DIR)+File.separator;

        archiveCopyStandalone.setActualDeployRootLocation(actualRootDir);
        archiveCopyStandalone.setMountDeployRootLocation(mountRootDir);

        final Set<Long> successfullyCopiedArchiveIds = new HashSet<Long>(Arrays.asList(1l));
        final List<Archive> bcrArchives = getBcrArchivesList(actualRootDir);
        final FileInfoQueryRequest fileInfoQueryRequest = new FileInfoQueryRequest();
        fileInfoQueryRequest.setArchiveId(1);

        final Collection<FileInfo> allFiles = new ArrayList<FileInfo>();
        final FileInfo fileInfo = new FileInfo();
        fileInfo.setFileLocation(bcrArchives.get(0).getDeployDirectory()+File.separator+"nationwidechildrens.org_clinical.TCGA-00-A000.xml");
        fileInfo.setFileName("nationwidechildrens.org_clinical.TCGA-00-A000.xml");
        fileInfo.setId(1l);
        allFiles.add(fileInfo);

        final File bcrXMLFile = new File(archiveCopyStandalone.getDeployLocation(fileInfo.getFileLocation()));
        final String uuid = "ebedab7c-dd7c-448e-8324-78c86f13be8b";


        mockery.checking(new Expectations() {{
            one(archiveCopyStandalone.getDccCommonArchiveQueries()).getAllAvailableProtectedBioArchives();
            will(returnValue(bcrArchives));
            one(mockLogger).log(Level.INFO, "Found 1 available protected bio archive(s) in dccCommon.");
            one(mockLogger).log(Level.INFO, "Starting file system copy of all archives to the public location ...");

            one(mockLogger).log(Level.INFO, "Starting copy of archive #1 ...");
            one(mockLogger).log(Level.INFO, "Copying '"+getFilePath(archiveCopyStandalone.getDeployLocation(bcrArchives.get(0).getDeployLocation()))+"' [archive Id:"+ bcrArchives.get(0).getId()+"] to '"+getFilePath(archiveCopyStandalone.getDeployLocation(bcrArchives.get(0).getDeployLocation()).replace(PROTECTED_DIR,PUBLIC_DIR))+"'");
            one(mockLogger).log(Level.INFO, "Copying '"+getFilePath(archiveCopyStandalone.getDeployLocation(bcrArchives.get(0).getDeployLocation())+".md5")+"' [archive Id:"+ bcrArchives.get(0).getId()+"] to '"+getFilePath(archiveCopyStandalone.getDeployLocation(bcrArchives.get(0).getDeployLocation()).replace(PROTECTED_DIR,PUBLIC_DIR)+".md5")+"'");
            one(mockLogger).log(Level.INFO, "Copying '"+getFilePath(archiveCopyStandalone.getDeployLocation(bcrArchives.get(0).getDeployDirectory()))+"' [archive Id:"+ bcrArchives.get(0).getId()+"] to '"+getFilePath(archiveCopyStandalone.getDeployLocation(bcrArchives.get(0).getDeployDirectory()).replace(PROTECTED_DIR,PUBLIC_DIR))+"'");

            one(mockLogger).log(Level.INFO, "Found disease(s) in dccCommon, for which archives have successfully been copied to the public location: [BLCA]");
            one(mockLogger).log(Level.INFO, "Updating available protected bio archive(s) deploy location to public ...");
            one(mockLogger).log(Level.INFO, "Updating Disease schema: BLCA ...");
            one(mockDiseaseArchiveQueries).updateArchivesLocationToPublic(new HashSet<Long>(Arrays.asList(1l)));
            one(mockDiseaseFileInfoQueries).updateArchiveFilesLocationToPublic(new HashSet<Long>(Arrays.asList(1l)));

            one(mockLogger).log(Level.INFO, "Updating DccCommon schema ...");
            one(mockDccCommonArchiveQueries).updateArchivesLocationToPublic(successfullyCopiedArchiveIds);
            one(mockDccCommonFileInfoQueries).updateArchiveFilesLocationToPublic(successfullyCopiedArchiveIds);


            one(mockLogger).log(Level.INFO, "Adding patient UUID and file id relationships for disease BLCA ...");
            one(mockLogger).log(Level.INFO, "Adding patient UUID and file id relationships for archive 1 ...");
            one(mockDccCommonFileInfoQueries).getFilesForArchive(with (any(FileInfoQueryRequest.class)));
            will(returnValue(allFiles));
            one(mockLogger).log(Level.INFO, "Adding patient UUID and file id relationships for file "+bcrXMLFile.getPath()+" ...");
            one(mockBCRIDProcessor).getPatientUUIDfromFile(bcrXMLFile);
            will(returnValue(uuid));
            one(mockLogger).log(Level.INFO, "Adding patient UUID [ "+uuid+"] file id [1] ...");
            one(mockDiseaseUuiddao).addParticipantFileUUIDAssociation(with(any(List.class)));
            one(mockDccCommonUuiddao).addParticipantFileUUIDAssociation(with(any(List.class)));

        }});

        System.out.println("Copying '"+getFilePath(archiveCopyStandalone.getDeployLocation(bcrArchives.get(0).getDeployLocation()))+"' [archive Id:"+ bcrArchives.get(0).getId()+"] to '"+getFilePath(archiveCopyStandalone.getDeployLocation(bcrArchives.get(0).getDeployLocation()).replace(PROTECTED_DIR,PUBLIC_DIR))+"'");
        archiveCopyStandalone.copyBCRArchivesFromProtectedToPublic();

        fileExists(archiveCopyStandalone.getDeployLocation(bcrArchives.get(0).getDeployLocation()).replace(PROTECTED_DIR,PUBLIC_DIR),true);
        fileExists(archiveCopyStandalone.getDeployLocation(bcrArchives.get(0).getDeployLocation()).replace(PROTECTED_DIR, PUBLIC_DIR) + ".md5", true);
        fileExists(archiveCopyStandalone.getDeployLocation(bcrArchives.get(0).getDeployDirectory()).replace(PROTECTED_DIR, PUBLIC_DIR), true);
        fileExists(archiveCopyStandalone.getDeployLocation(bcrArchives.get(0).getDeployDirectory()).replace(PROTECTED_DIR, PUBLIC_DIR) + File.separator + fileInfo.getFileName(), true);

    }

    @Test
    public void testCreatePatientUUIDFileAssociations() throws TransformerException, IOException, SAXException, ParserConfigurationException {
        checkCreatePatientUUIDFileAssociations(false);
    }

    @Test
    public void testCreatePatientUUIDFileAssociationsDryRun() throws TransformerException, IOException, SAXException, ParserConfigurationException {
        checkCreatePatientUUIDFileAssociations(true);
    }

    @Test
    public void testCreatePatientUUIDFileAssociationsUUIDBlank() throws TransformerException, IOException, SAXException, ParserConfigurationException {

        final Long archiveId1 = 1L;
        final Set<Long> successfullyCopiedGBMArchiveIds = new HashSet<Long>();
        successfullyCopiedGBMArchiveIds.add(archiveId1);

        final String uuid1 = "";
        final Long fileId1 = 11L;

        final Map<String, Set<Long>> diseaseAbbreviations = new HashMap<String, Set<Long>>();
        diseaseAbbreviations.put("GBM", successfullyCopiedGBMArchiveIds);

        mockery.checking(new Expectations() {{
            one(mockLogger).log(Level.INFO, "Adding patient UUID and file id relationships for disease GBM ...");
            one(mockLogger).log(Level.INFO, "Adding patient UUID and file id relationships for archive " + archiveId1 + " ...");

            one(mockDccCommonFileInfoQueries).getFilesForArchive(with(expectedFileInfoQueryRequest(archiveId1)));
            will(returnValue(makeFileInfoCollection(fileId1)));

            one(mockLogger).log(Level.INFO, "Adding patient UUID and file id relationships for file file.xml ...");

            one(mockBCRIDProcessor).getPatientUUIDfromFile(with(expectedFile("file.xml")));
            will(returnValue(uuid1));

            one(mockLogger).log(Level.ERROR, "Error reading file.xml. Patient uuid is null/empty. Archive id [" + archiveId1 + "," + fileId1 + "] file id relationship will not be added into the db");

            one(mockDiseaseUuiddao).addParticipantFileUUIDAssociation(with(expectedPatientUuidFileObject(null, null, true)));
            one(mockDccCommonUuiddao).addParticipantFileUUIDAssociation(with(expectedPatientUuidFileObject(null, null, true)));
        }});

        archiveCopyStandalone.createPatientUUIDFileAssociations(diseaseAbbreviations);
    }

    @Test
    public void testCreatePatientUUIDFileAssociationsIOException() throws TransformerException, IOException, SAXException, ParserConfigurationException {

        final Long archiveId1 = 1L;
        final Set<Long> successfullyCopiedGBMArchiveIds = new HashSet<Long>();
        successfullyCopiedGBMArchiveIds.add(archiveId1);

        final Long fileId1 = 11L;

        final Map<String, Set<Long>> diseaseAbbreviations = new HashMap<String, Set<Long>>();
        diseaseAbbreviations.put("GBM", successfullyCopiedGBMArchiveIds);

        final String fileLocation = "file.xml";
        final String errorMessage = "File could not be found.";

        mockery.checking(new Expectations() {{
            one(mockLogger).log(Level.INFO, "Adding patient UUID and file id relationships for disease GBM ...");
            one(mockLogger).log(Level.INFO, "Adding patient UUID and file id relationships for archive " + archiveId1 + " ...");

            one(mockDccCommonFileInfoQueries).getFilesForArchive(with(expectedFileInfoQueryRequest(archiveId1)));
            will(returnValue(makeFileInfoCollection(fileId1)));

            one(mockLogger).log(Level.INFO, "Adding patient UUID and file id relationships for file file.xml ...");

            one(mockBCRIDProcessor).getPatientUUIDfromFile(with(expectedFile(fileLocation)));
            will(throwException(new IOException(errorMessage)));

            one(mockLogger).log(Level.ERROR, "Error reading " + fileLocation + ". Archive id [" + archiveId1 + "," + fileId1 + "] file id relationship will not be added into the db: java.io.IOException: " + errorMessage);

            one(mockDiseaseUuiddao).addParticipantFileUUIDAssociation(with(expectedPatientUuidFileObject(null, null, true)));
            one(mockDccCommonUuiddao).addParticipantFileUUIDAssociation(with(expectedPatientUuidFileObject(null, null, true)));
        }});

        archiveCopyStandalone.createPatientUUIDFileAssociations(diseaseAbbreviations);
    }

    @Test
    public void getDeployLocation(){
        archiveCopyStandalone.setActualDeployRootLocation("/tcgafiles/ftp_auth/distro_ftpusers/");
        archiveCopyStandalone.setMountDeployRootLocation("/sterling_stage/distro_ftpusers/" +
                "");

        final String actualValue = archiveCopyStandalone.getDeployLocation("/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/tumor/blca/bcr/nationwidechildrens.org/bio/clin/nationwidechildrens.org_BLCA.bio.Level_1.113.5.0.tar.gz");
        final String expectedValue = "/sterling_stage/distro_ftpusers/tcga4yeo/tumor/blca/bcr/nationwidechildrens.org/bio/clin/nationwidechildrens.org_BLCA.bio.Level_1.113.5.0.tar.gz";

        assertEquals(expectedValue,actualValue);


    }
    /**
     * Check assertions for a call to createPatientUUIDFileAssociations()
     *
     * @param isDryRun wether it is a dry run or not
     *
     * @throws TransformerException
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    private void checkCreatePatientUUIDFileAssociations(final boolean isDryRun) throws TransformerException, IOException, SAXException, ParserConfigurationException {

        final Long archiveId1 = 1L;
        final Set<Long> successfullyCopiedGBMArchiveIds = new HashSet<Long>();
        successfullyCopiedGBMArchiveIds.add(archiveId1);

        final Long archiveId2 = 2L;
        final Set<Long> successfullyCopiedOVArchiveIds = new HashSet<Long>();
        successfullyCopiedOVArchiveIds.add(archiveId2);

        final String uuid1 = "uuid1";
        final String uuid2 = "uuid2";

        final Long fileId1 = 11L;
        final Long fileId2 = 22L;

        final Map<String, Set<Long>> diseaseAbbreviations = new HashMap<String, Set<Long>>();
        diseaseAbbreviations.put("GBM", successfullyCopiedGBMArchiveIds);
        diseaseAbbreviations.put("OV", successfullyCopiedOVArchiveIds);

        mockery.checking(new Expectations() {{
            one(mockLogger).log(Level.INFO, "Adding patient UUID and file id relationships for disease OV ...");
            one(mockLogger).log(Level.INFO, "Adding patient UUID and file id relationships for archive " + archiveId2 + " ...");

            one(mockDccCommonFileInfoQueries).getFilesForArchive(with(expectedFileInfoQueryRequest(archiveId2)));
            will(returnValue(makeFileInfoCollection(fileId2)));

            one(mockLogger).log(Level.INFO, "Adding patient UUID and file id relationships for file file.xml ...");

            one(mockBCRIDProcessor).getPatientUUIDfromFile(with(expectedFile("file.xml")));
            will(returnValue(uuid2));

            one(mockLogger).log(Level.INFO, "Adding patient UUID [ uuid2] file id [22] ...");

            if(!isDryRun) {
                one(mockDiseaseUuiddao).addParticipantFileUUIDAssociation(with(expectedPatientUuidFileObject("uuid2", fileId2, false)));
                one(mockDccCommonUuiddao).addParticipantFileUUIDAssociation(with(expectedPatientUuidFileObject("uuid2", fileId2, false)));
            }

            one(mockLogger).log(Level.INFO, "Adding patient UUID and file id relationships for disease GBM ...");
            one(mockLogger).log(Level.INFO, "Adding patient UUID and file id relationships for archive " + archiveId1 + " ...");

            one(mockDccCommonFileInfoQueries).getFilesForArchive(with(expectedFileInfoQueryRequest(archiveId1)));
            will(returnValue(makeFileInfoCollection(fileId1)));

            one(mockLogger).log(Level.INFO, "Adding patient UUID and file id relationships for file file.xml ...");

            one(mockBCRIDProcessor).getPatientUUIDfromFile(with(expectedFile("file.xml")));
            will(returnValue(uuid1));

            one(mockLogger).log(Level.INFO, "Adding patient UUID [ uuid1] file id [11] ...");

            if(!isDryRun) {
                one(mockDiseaseUuiddao).addParticipantFileUUIDAssociation(with(expectedPatientUuidFileObject(uuid1, fileId1, false)));
                one(mockDccCommonUuiddao).addParticipantFileUUIDAssociation(with(expectedPatientUuidFileObject(uuid1, fileId1, false)));
            }
        }});

        archiveCopyStandalone.setDryRun(isDryRun);
        archiveCopyStandalone.createPatientUUIDFileAssociations(diseaseAbbreviations);
    }

    /**
     * Return a {@link Collection} of 2 {@link FileInfo} element:
     *
     * - 1 xml file with the given file Id.
     * - 1 txt file with the given file Id + 1.
     *
     * @param fileId the {@link FileInfo} file Id
     * @return a {@link Collection} of 1 {@link FileInfo} element with the given file Id
     */
    private Collection<FileInfo> makeFileInfoCollection(final Long fileId) {

        final List<FileInfo> result = new ArrayList<FileInfo>();

        final String xmlFilename = "file.xml";
        final FileInfo xmlFileInfo = new FileInfo();
        xmlFileInfo.setId(fileId);
        xmlFileInfo.setFileName(xmlFilename);
        xmlFileInfo.setFileLocation(xmlFilename);

        final String txtFilename = "file.txt";
        final FileInfo txtFileInfo = new FileInfo();
        txtFileInfo.setId(fileId + 1);
        txtFileInfo.setFileName(txtFilename);
        txtFileInfo.setFileLocation(txtFilename);

        result.add(xmlFileInfo);
        result.add(txtFileInfo);

        return result;
    }

    private void fileExists(final String path, final Boolean exists){
        final File copiedFile = new File(path);
        assertEquals(exists,copiedFile.exists());
    }

    private void deleteFilesFromPublicDir(){
        final File publicDir = new File(SAMPLE_DIR+PUBLIC_DIR);
        final File[] files = publicDir.listFiles();
        assertNotNull(files);
        for (final File fileToBeDeleted : files) {
            FileUtil.deleteDir(fileToBeDeleted);
        }

    }

    private String getFilePath(final String path){
        return new File(path).getPath();
    }

    private List<Archive> getMafArchivesList(){
        final List<Archive> result = new ArrayList<Archive>();
        Archive archive = new Archive();
        archive.setId(1L);
        archive.setDeployLocation(SAMPLE_DIR +
                PROTECTED_DIR +
                File.separator +
                "blca" +
                File.separator +
                "broad.mit.edu_BLCA.IlluminaGA_DNASeq.Level_2.0.1.0.tar.gz");
        archive.setTumorType("BLCA");
        result.add(archive);
        archive = new Archive();
        archive.setId(2L);
        archive.setDeployLocation(SAMPLE_DIR +
                PROTECTED_DIR +
                File.separator +
                "blca" +
                File.separator +
                "broad.mit.edu_BLCA.IlluminaGA_DNASeq.Level_2.0.0.0.tar.gz");
        archive.setTumorType("BLCA");
        result.add(archive);
        archive = new Archive();
        archive.setId(3L);
        archive.setDeployLocation(SAMPLE_DIR+
                PROTECTED_DIR+
                File.separator+
                "gbm"+
                File.separator+
                "broad.mit.edu_GBM.IlluminaGA_DNASeq.Level_2.20.1.0.tar.gz");
        archive.setTumorType("GBM");
        result.add(archive);

        return result;
    }

    private List<Archive> getBcrArchivesList(final String rootDir){
        final List<Archive> result = new ArrayList<Archive>();
        Archive archive = new Archive();
        archive.setId(1L);
        archive.setDeployLocation(getFilePath(rootDir +
                PROTECTED_DIR +
                File.separator +
                "blca" +
                File.separator +
                "nationwidechildrens.org_BLCA.bio.Level_1.128.10.0.tar.gz"));
        archive.setTumorType("BLCA");
        result.add(archive);
        return result;
    }
    /**
     * Return a {@link Matcher} for the expected log error from a missing source file.
     *
     * @return a {@link Matcher} for the expected log error from a missing source file
     */
    private static Matcher<String> expectedLogErrorForMissingSourceFile() {

        final File missingFile = new File(SAMPLE_DIR+ "sourceFileMissing"+File.separator+"tcga4yeo"+File.separator+"source");

        final String expectedMsg = "Protected (Source) File '"+missingFile.getPath()+ "' [Id:1] does not exist - tar/tar.gz, md5 and exploded archive will not be copied to the public location";
        return new TypeSafeMatcher<String>() {

            @Override
            public boolean matchesSafely(final String s) {
                return expectedMsg.equals(s);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("error log for missing source file has expected value.");
            }
        };
    }

    /**
     * Return a {@link Matcher} for the expected log info from an existing destination file.
     *
     * @param fileExtension the expected file extension
     * @return a {@link Matcher} for the expected log info from an existing destination file
     */
    private static Matcher<String> expectedLogInfoForExistingDestinationFile(final String fileExtension) {
        final File file = new File(SAMPLE_DIR+"destinationFileExists"+File.separator +"anonymous"+File.separator+"source");
        final String expectedMsg = "File '"+file.getPath()+fileExtension + "' already exists - deleting now ...";
        return new TypeSafeMatcher<String>() {

            @Override
            public boolean matchesSafely(final String s) {
                return expectedMsg.equals(s);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("info log for existing destination file has expected value.");
            }
        };
    }

    /**
     * Return a {@link Matcher} for the expected log info for the copy of a file.
     *
     * @param directoryName the directory name (parent of tcga4yeo)
     * @param fileExtension the expected file extension
     * @return a {@link Matcher} for the expected log info for the copy of a file
     */
    private static Matcher<String> expectedLogInfoForFileCopy(final String directoryName, final String fileExtension) {
        final File sourceFile = new File(SAMPLE_DIR+directoryName+File.separator+"tcga4yeo"+File.separator+"source");
        final File destFile = new File(SAMPLE_DIR+directoryName+File.separator+"anonymous"+File.separator+"source");
        final String expectedMsg = "Copying '"+sourceFile.getPath()+fileExtension + "' [archive Id:1] to '"+  destFile.getPath()+fileExtension+"'";
        return new TypeSafeMatcher<String>() {

            @Override
            public boolean matchesSafely(final String s) {
                return expectedMsg.equals(s);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("error log has expected value.");
            }
        };
    }

    /**
     * Return a {@link Matcher} for the expected FileInfoQueryRequest.
     *
     * @param archiveId the expected archive Id
     * @return a {@link Matcher} for the expected FileInfoQueryRequest
     */
    private static Matcher<FileInfoQueryRequest> expectedFileInfoQueryRequest(final Long archiveId) {

        return new TypeSafeMatcher<FileInfoQueryRequest>() {

            @Override
            public boolean matchesSafely(final FileInfoQueryRequest fileInfoQueryRequest) {
                return fileInfoQueryRequest != null && fileInfoQueryRequest.getArchiveId() == archiveId;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("FileInfoQueryRequest has expected value.");
            }
        };
    }

    /**
     * Return a {@link Matcher} for the expected File.
     *
     * @param filename the expected filename
     * @return a {@link Matcher} for the expected File
     */
    private static Matcher<File> expectedFile(final String filename) {

        return new TypeSafeMatcher<File>() {

            @Override
            public boolean matchesSafely(final File file) {
                return filename != null && filename.equals(file.getName());
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("File has expected value.");
            }
        };
    }

    /**
     * Return a {@link Matcher} for the expected patient uuid file object.
     *
     * @param patientUuid the expected patient uuid
     * @param fileId the expected file Id
     * @param isEmpty whether the list is expected to be empty or not
     * @return a {@link Matcher} for the expected patient uuid file object
     */
    private Matcher<List<Object[]>> expectedPatientUuidFileObject(final String patientUuid,
                                                                  final Long fileId,
                                                                  final boolean isEmpty) {

        return new TypeSafeMatcher<List<Object[]>>() {

            @Override
            public boolean matchesSafely(final List<Object[]> list) {

                boolean result = false;

                if(isEmpty) {
                    result = list != null && list.size() == 0;

                } else if(list != null && list.size() == 1) {

                    final Object[] objects = list.get(0);

                    if(objects.length == 4) {

                        if(objects[0] == patientUuid
                                && objects[1] == fileId
                                && objects[2] == patientUuid
                                && objects[3] == fileId) {
                            result = true;
                        }
                    }
                }

                return result;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("patient uuid file object has expected value.");
            }
        };
    }

    /**
     * Return a list with 1 archive
     *
     * @param deployLocation the archive's deploy location
     * @return a list with 1 archive
     */
    private List<Archive> makeArchives(final String deployLocation) {

        final Archive archive = new Archive();
        archive.setId(1L);
        archive.setDeployLocation(deployLocation);
        archive.setTumorType("ABC");

        final List<Archive> result = new ArrayList<Archive>();
        result.add(archive);

        return result;
    }
}
