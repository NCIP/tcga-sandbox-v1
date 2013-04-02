/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DataTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Visibility;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParserImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.ArchiveCompressorTarGzImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ExperimentDAO;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.VisibilityQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc.ExperimentDAOImpl;
import oracle.net.aso.a;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.DataIntegrityViolationException;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test class for ArchiveDeployer
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class ArchiveDeployerFastTest {

    private static final String SAMPLES_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;
    private Mockery context = new JUnit4Mockery();
    private final VisibilityQueries mockVisibilityQueries = context
            .mock(VisibilityQueries.class);
    private DataTypeQueries dataTypeQueries = context
            .mock(DataTypeQueries.class);
    private ManifestParser manifestParser = context.mock(ManifestParser.class);
    private ArchiveQueries archiveQueries = context.mock(ArchiveQueries.class);

    private ExperimentDAO experimentDAO;
    private final Archive archive = new Archive();
    private final Center center = new Center();
    private final Platform platform = new Platform();
    private final Tumor tumor = new Tumor();
    private ArchiveDeployer deployer;
    private Visibility visibility;
    private QcContext qcContext;

    private List<File> filesToDelete = new ArrayList<File>();

    @Before
    public void setup() {
        archive.setArchiveFile(new File("center_tumor.platform.type.1.0.0"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION));
        archive.setTumorType("tumorType");
        archive.setDomainName("domainName");
        archive.setDataType("dataType");
        platform.setPlatformId(1);
        platform.setPlatformName("platformName");
        archive.setThePlatform(platform);
        center.setCenterId(1);
        center.setCenterType("centerType");
        center.setCenterName("centerName");
        archive.setTheCenter(center);
        tumor.setTumorId(1);
        tumor.setTumorName("tumorName");
        archive.setTheTumor(tumor);
        archive.setDataTypeCompressed(true);
        archive.setDeployLocation(SAMPLES_DIR
                + "qclive/archiveDeployer/center_tumor.platform.type.1.0.0"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);

        deployer = new ArchiveDeployer();
        deployer.setPublicDeployRoot(SAMPLES_DIR + "qclive/archiveDeployer");
        deployer.setPrivateDeployRoot(SAMPLES_DIR
                + "qclive/archiveDeployer/private");
        deployer.setVisibilityQueries(mockVisibilityQueries);
        deployer.setDataTypeQueries(dataTypeQueries);
        deployer.setManifestParser(manifestParser);
        deployer.setArchiveQueries(archiveQueries);
        deployer.setFileCompressor(new ArchiveCompressorTarGzImpl());

        experimentDAO = new ExperimentDAOImpl();
        experimentDAO.setDataTypeQueries(dataTypeQueries);
        experimentDAO.setAccessQueries(mockVisibilityQueries);
        experimentDAO.setPublicDeployRoot(SAMPLES_DIR
                + "qclive/archiveDeployer");
        experimentDAO.setPrivateDeployRoot(SAMPLES_DIR
                + "qclive/archiveDeployer/private");
        deployer.setExperimentDAO(experimentDAO);
        visibility = new Visibility();
        visibility.setVisibilityName(ArchiveDeployer.LOCATION_PUBLIC);
        visibility.setIdentifiable(false);

        qcContext = new QcContext();
        qcContext.setArchive(archive);
    }

    @Test
    public void testNoDeployDirectories() {

        context.checking(new Expectations() {
            {
                exactly(2).of(mockVisibilityQueries).getVisibilityForArchive(
                        archive);
                will(returnValue(null));

                one(dataTypeQueries).getDataTypeFTPDisplayForPlatform(
                        archive.getThePlatform().getPlatformId().toString());

                will(returnValue(null));
            }
        });

        try {
            deployer.execute(archive, qcContext);
            fail("No exception thrown");
        } catch (Processor.ProcessorException e) {
            assertEquals(Archive.STATUS_IN_REVIEW, archive.getDeployStatus());
        }
    }

    @Test
    public void testGetDeployDirectoryNoVisibility()
            throws Processor.ProcessorException, IOException, ParseException {
        context.checking(new Expectations() {
            {
                one(mockVisibilityQueries).getVisibilityForArchive(archive);
                will(returnValue(null)); // no visibility
                one(dataTypeQueries).getDataTypeFTPDisplayForPlatform("1");
                will(returnValue("dataType"));
            }
        });

        File deployDir = deployer.getDeployDirectory(archive);
        // deploy dir should be in private root because we don't know if the
        // archive should be protected or not -- better safe than sorry
        assertEquals(
                deployDir,
                new File(
                        SAMPLES_DIR
                                + "qclive/archiveDeployer/private/tumor/tumorname/centertype/centername/platformname/datatype/center_tumor.platform.type.1.0.0"));
    }

    @Test
    public void testGood() throws Processor.ProcessorException, IOException,
            ParseException {

        final Map<String, String> manifestFiles = new HashMap<String, String>();
        manifestFiles.put("file1.txt", "");
        setCommonExpectations();
        context.checking(new Expectations() {
            {
                one(dataTypeQueries).getDataTypeFTPDisplayForPlatform("1");
                will(returnValue("dataType"));
                one(manifestParser)
                        .parseManifest(
                                new File(
                                        SAMPLES_DIR
                                                + "qclive/archiveDeployer/center_tumor.platform.type.1.0.0/MANIFEST.txt"));
                will(returnValue(manifestFiles));
                one(archiveQueries).getLatestVersionArchive(archive);
                will(returnValue(null));
            }
        });

        deployer.execute(archive, qcContext);
        final String expectedDir = SAMPLES_DIR
                + "qclive/archiveDeployer/tumor/tumorname/centertype/centername/platformname/datatype/center_tumor.platform.type.1.0.0";

        checkDeployedArchive(expectedDir);

    }


    @Test
    public void testDeployCompressedArchiveForUnCompressedDataType() throws Processor.ProcessorException, IOException,
            ParseException {

        final Map<String, String> manifestFiles = new HashMap<String, String>();
        manifestFiles.put("file1.txt", "");
        setCommonExpectations();
        context.checking(new Expectations() {
            {
                one(dataTypeQueries).getDataTypeFTPDisplayForPlatform("1");
                will(returnValue("dataType"));
                one(manifestParser)
                        .parseManifest(
                                new File(
                                        SAMPLES_DIR
                                                + "qclive/archiveDeployer/center_tumor.platform.type.1.0.0/MANIFEST.txt"));
                will(returnValue(manifestFiles));
                one(archiveQueries).getLatestVersionArchive(archive);
                will(returnValue(null));
            }
        });


        archive.setDataTypeCompressed(false);
        deployer.execute(archive, qcContext);
        final String expectedDir = SAMPLES_DIR
                + "qclive/archiveDeployer/tumor/tumorname/centertype/centername/platformname/datatype/center_tumor.platform.type.1.0.0";

        checkDeployedArchive(expectedDir);

    }

    @Test
    public void testDeployUnCompressedArchiveForUnCompressedDataType() throws Processor.ProcessorException, IOException,
            ParseException {

        final Map<String, String> manifestFiles = new HashMap<String, String>();
        manifestFiles.put("file1.txt", "");
        setCommonExpectations();
        context.checking(new Expectations() {
            {
                one(dataTypeQueries).getDataTypeFTPDisplayForPlatform("1");
                will(returnValue("dataType"));
                one(manifestParser)
                        .parseManifest(
                                new File(
                                        SAMPLES_DIR
                                                + "qclive/archiveDeployer/center_tumor.platform.type.1.0.0/MANIFEST.txt"));
                will(returnValue(manifestFiles));
                one(archiveQueries).getLatestVersionArchive(archive);
                will(returnValue(null));
            }
        });

        archive.setDeployLocation(SAMPLES_DIR
                + "qclive/archiveDeployer/center_tumor.platform.type.1.0.0"
                + ConstantValues.UNCOMPRESSED_ARCHIVE_EXTENSION);

        archive.setDataTypeCompressed(false);
        deployer.execute(archive, qcContext);
        final String expectedDir = SAMPLES_DIR
                + "qclive/archiveDeployer/tumor/tumorname/centertype/centername/platformname/datatype/center_tumor.platform.type.1.0.0";

        checkDeployedArchive(expectedDir);

    }

    private void checkDeployedArchive(final String expectedDirLocation)
            throws IOException, ParseException {
        final File expectedDir = new File(expectedDirLocation);
        // make sure the directory was created
        assertTrue("Deploy directory was not created", expectedDir.exists());

        // make sure the file was moved
        File expectedFile = new File(expectedDir, "file1.txt");
        assertTrue("Archive file was not moved to deploy directory",
                expectedFile.exists());

        // make sure the manifest was moved
        File manifest = new File(expectedDir, "MANIFEST.txt");
        assertTrue("Archive manifest was not moved to deploy directory",
                manifest.exists());

        // make sure manifest lists correct MD5 for file1.txt
        ManifestParser realManifestParser = new ManifestParserImpl();
        Map<String, String> manifestEntries = realManifestParser
                .parseManifest(manifest);
        assertEquals("f638bbc882c9d3b2b4312a40db2d80cf",
                manifestEntries.get("file1.txt"));

        // make sure the new archive was created
        File expectedArchive = new File(expectedDirLocation
                + archive.getDeployedArchiveExtension());
        assertTrue("Archive " + archive.getDeployedArchiveExtension() + " was not created in deploy location",
                expectedArchive.exists());

        // make sure there is an MD5 for the new zip archive
        File expectedMd5 = new File(expectedDirLocation
                + archive.getDeployedArchiveExtension() + ".md5");
        assertTrue("Archive MD5 was not created", expectedMd5.exists());
        assertEquals("Deployed", archive.getDeployStatus());
    }

    @Test(expected = Processor.ProcessorException.class)
    public void testDatabaseError() throws IOException, ParseException,
            Processor.ProcessorException {
        setCommonExpectations();
        context.checking(new Expectations() {
            {
                one(dataTypeQueries).getDataTypeFTPDisplayForPlatform("1");
                // noinspection ThrowableInstanceNeverThrown
                will(throwException(new DataIntegrityViolationException(
                        "oh no!")));
            }
        });

        deployer.execute(archive, qcContext);
    }

    private Experiment makeExperimentForMageTabTest(
            final Archive publicArchive, final Archive protectedArchive) {
        final Experiment experiment = new Experiment();
        if (publicArchive != null) {
            experiment.addArchive(publicArchive);
        }
        if (protectedArchive != null) {
            experiment.addArchive(protectedArchive);
        }
        return experiment;
    }

    @Test
    public void testDeployMageTabArchive() throws Processor.ProcessorException,
            IOException, ParseException {

        // experiment has public and protected data so mage tab should be copied
        // to protected
        final Archive publicDataArchive = new Archive();
        final Archive protectedDataArchive = new Archive();
        final Experiment experiment = makeExperimentForMageTabTest(
                publicDataArchive, protectedDataArchive);
        qcContext.setExperiment(experiment);

        final Map<String, String> manifestFiles = new HashMap<String, String>();
        manifestFiles.put("file1.txt", "");
        archive.setArchiveType(Archive.TYPE_MAGE_TAB);
        visibility.setIdentifiable(false);
        setCommonExpectations();
        final Visibility protectedVisibility = new Visibility();
        protectedVisibility.setIdentifiable(true);
        final Visibility publicVisibility = new Visibility();
        publicVisibility.setIdentifiable(false);

        context.checking(new Expectations() {
            {
                one(mockVisibilityQueries).getVisibilityForArchive(
                        publicDataArchive);
                will(returnValue(publicVisibility));
                one(mockVisibilityQueries).getVisibilityForArchive(
                        protectedDataArchive);
                will(returnValue(protectedVisibility));

                exactly(2).of(dataTypeQueries)
                        .getDataTypeFTPDisplayForPlatform("1");
                will(returnValue("dataType"));
                exactly(2).of(manifestParser).parseManifest(
                                new File(SAMPLES_DIR + "qclive/archiveDeployer/center_tumor.platform.type.1.0.0/MANIFEST.txt"));
                will(returnValue(manifestFiles));
                exactly(2).of(archiveQueries).getLatestVersionArchive(archive);
                will(returnValue(null));
                one(archiveQueries).updateSecondaryDeployLocation(archive);
            }
        });
        deployer.execute(archive, qcContext);

        final String expectedDir = SAMPLES_DIR
                + "qclive/archiveDeployer/tumor/tumorname/centertype/centername/platformname/datatype/center_tumor.platform.type.1.0.0";

        checkDeployedArchive(expectedDir);

        final String expectedSecondaryDir = SAMPLES_DIR
                + "qclive/archiveDeployer/private/tumor/tumorname/centertype/centername/platformname/datatype/center_tumor.platform.type.1.0.0";

        checkDeployedArchive(expectedSecondaryDir);
    }

    @Test
    public void testDeployMageTabArchiveNoProtectedData()
            throws Processor.ProcessorException, IOException, ParseException {

        // experiment has public data so mage tab should NOT be copied to
        // protected
        final Archive publicDataArchive = new Archive();
        final Experiment experiment = makeExperimentForMageTabTest(
                publicDataArchive, null);
        qcContext.setExperiment(experiment);

        final Map<String, String> manifestFiles = new HashMap<String, String>();
        manifestFiles.put("file1.txt", "");
        archive.setArchiveType(Archive.TYPE_MAGE_TAB);
        visibility.setIdentifiable(false);
        setCommonExpectations();
        final Visibility protectedVisibility = new Visibility();
        protectedVisibility.setIdentifiable(true);
        final Visibility publicVisibility = new Visibility();
        publicVisibility.setIdentifiable(false);

        context.checking(new Expectations() {
            {
                one(mockVisibilityQueries).getVisibilityForArchive(
                        publicDataArchive);
                will(returnValue(publicVisibility));

                one(dataTypeQueries).getDataTypeFTPDisplayForPlatform("1");
                will(returnValue("dataType"));
                one(manifestParser)
                        .parseManifest(
                                new File(
                                        SAMPLES_DIR
                                                + "qclive/archiveDeployer/center_tumor.platform.type.1.0.0/MANIFEST.txt"));
                will(returnValue(manifestFiles));
                one(archiveQueries).getLatestVersionArchive(archive);
                will(returnValue(null));
            }
        });
        deployer.execute(archive, qcContext);

        final String expectedDir = SAMPLES_DIR
                + "qclive/archiveDeployer/tumor/tumorname/centertype/centername/platformname/datatype/center_tumor.platform.type.1.0.0";

        checkDeployedArchive(expectedDir);
    }

    @Test
    public void testDeployProtectedMageTabArchive()
            throws Processor.ProcessorException, IOException, ParseException {
        final Map<String, String> manifestFiles = new HashMap<String, String>();
        manifestFiles.put("file1.txt", "");
        archive.setArchiveType(Archive.TYPE_MAGE_TAB);
        visibility.setVisibilityName("Protected");
        visibility.setIdentifiable(true);
        setCommonExpectations();
        final Visibility protectedVisibility = new Visibility();
        protectedVisibility.setIdentifiable(true);

        context.checking(new Expectations() {
            {
                one(dataTypeQueries).getDataTypeFTPDisplayForPlatform("1");
                will(returnValue("dataType"));
                one(manifestParser)
                        .parseManifest(
                                new File(
                                        SAMPLES_DIR
                                                + "qclive/archiveDeployer/center_tumor.platform.type.1.0.0/MANIFEST.txt"));
                will(returnValue(manifestFiles));
                one(archiveQueries).getLatestVersionArchive(archive);
                will(returnValue(null));
            }
        });
        deployer.execute(archive, qcContext);

        final String expectedDir = SAMPLES_DIR
                + "qclive/archiveDeployer/private/tumor/tumorname/centertype/centername/platformname/datatype/center_tumor.platform.type.1.0.0";

        checkDeployedArchive(expectedDir);
    }

    @Test (expected=ProcessorException.class)
    public void testNullMageTab() throws ProcessorException{
    	context.checking(new Expectations() {
            {
                allowing(mockVisibilityQueries).getVisibilityForArchive(
                        archive);
                will(returnValue(null));               
            }
        });
    	qcContext.setExperiment(makeExperimentForMageTabTest(archive,null));
    	deployer.deployMageTabToSecondaryLocation(archive, qcContext);
    }
    
    @After
    public void cleanup() {
        final File publicArchiveDir = new File(
                SAMPLES_DIR
                        + "qclive/archiveDeployer/tumor/tumorname/centertype/centername/platformname/datatype/center_tumor.platform.type.1.0.0");
        final File privateArchiveDir = new File(
                SAMPLES_DIR
                        + "qclive/archiveDeployer/private/tumor/tumorname/centertype/centername/platformname/datatype/center_tumor.platform.type.1.0.0");
        final File publicExpectedFile = new File(publicArchiveDir, "file1.txt");
        final File privateExpectedFile = new File(privateArchiveDir,
                "file1.txt");
        final File publicManifest = new File(publicArchiveDir, "MANIFEST.txt");
        final File privateManifest = new File(privateArchiveDir, "MANIFEST.txt");
        final File publicExpectedArchive = new File(
                SAMPLES_DIR
                        + "qclive/archiveDeployer/tumor/tumorname/centertype/centername/platformname/datatype/center_tumor.platform.type.1.0.0"
                        + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        final File publicExpectedMd5 = new File(
                SAMPLES_DIR
                        + "qclive/archiveDeployer/tumor/tumorname/centertype/centername/platformname/datatype/center_tumor.platform.type.1.0.0"
                        + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION + ".md5");
        final File privateExpectedArchive = new File(
                SAMPLES_DIR
                        + "qclive/archiveDeployer/tumor/tumorname/centertype/centername/platformname/datatype/center_tumor.platform.type.1.0.0"
                        + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        final File privateExpectedMd5 = new File(
                SAMPLES_DIR
                        + "qclive/archiveDeployer/tumor/tumorname/centertype/centername/platformname/datatype/center_tumor.platform.type.1.0.0"
                        + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION + ".md5");

        publicExpectedFile.deleteOnExit();
        privateExpectedFile.deleteOnExit();
        publicManifest.deleteOnExit();
        privateManifest.deleteOnExit();
        publicArchiveDir.deleteOnExit();
        privateArchiveDir.deleteOnExit();
        publicExpectedArchive.deleteOnExit();
        privateExpectedArchive.deleteOnExit();
        publicExpectedMd5.deleteOnExit();
        privateExpectedMd5.deleteOnExit();
    }

    private void setCommonExpectations() {
        context.checking(new Expectations() {
            {
                allowing(mockVisibilityQueries)
                        .getVisibilityForArchive(archive);
                will(returnValue(visibility));

            }
        });
    }
}
