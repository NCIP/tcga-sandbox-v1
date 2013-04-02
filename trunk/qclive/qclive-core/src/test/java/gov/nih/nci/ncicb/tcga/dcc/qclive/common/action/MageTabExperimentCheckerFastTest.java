package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParserImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Test for MageTabExperimentChecker
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class MageTabExperimentCheckerFastTest {

    private static final String TEST_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator + "qclive" + File.separator + "mageTabExperimentChecker";

    private Mockery context = new JUnit4Mockery();
    private MageTabExperimentChecker mageTabExperimentChecker;
    private Experiment experiment;
    private QcContext qcContext;
    private CenterQueries mockCenterQueries;

    @Before
    public void setup() {
        mageTabExperimentChecker = new MageTabExperimentChecker();
        qcContext = new QcContext();
        experiment = new Experiment();
        mockCenterQueries = context.mock(CenterQueries.class);

        mageTabExperimentChecker.setManifestParser(new ManifestParserImpl());
        mageTabExperimentChecker.setCenterQueries(mockCenterQueries);
    }

    @Test
    public void testRequiresMageTabAuxOnly() throws IOException, ParseException {
        context.checking(new Expectations() {{
            one(mockCenterQueries).doesCenterRequireMageTab("test", Experiment.TYPE_CGCC);
            will(returnValue(true));
        }});

        // does not require because only aux archive in upload
        experiment.setCenterName("test");
        experiment.setType(Experiment.TYPE_CGCC);
        experiment.addArchive(makeArchive(Archive.TYPE_AUX, Archive.STATUS_UPLOADED, "test"));
        assertTrue(mageTabExperimentChecker.experimentRequiresMageTab(experiment));
        assertTrue(mageTabExperimentChecker.isMageTabOptional(experiment));
    }

    @Test
    public void testRequiresMageTabCgccWithLevel() throws IOException, ParseException {
        context.checking(new Expectations() {{
                    one(mockCenterQueries).doesCenterRequireMageTab("test", Experiment.TYPE_CGCC);
                    will(returnValue(true));
                }});
        experiment.setCenterName("test");
        experiment.setType(Experiment.TYPE_CGCC);
        experiment.addArchive(makeArchive(Archive.TYPE_LEVEL_1, Archive.STATUS_UPLOADED, "test"));
        assertTrue(mageTabExperimentChecker.experimentRequiresMageTab(experiment));
        assertFalse(mageTabExperimentChecker.isMageTabOptional(experiment));
    }

    @Test
    public void testRequiresMageTabGscVcfOnly() throws IOException, ParseException {
        context.checking(new Expectations() {{
            one(mockCenterQueries).doesCenterRequireMageTab("gsc.org", Experiment.TYPE_GSC);
            will(returnValue(true));
        }});

        // does not require mage tab
        experiment.setCenterName("gsc.org");
        experiment.setType(Experiment.TYPE_GSC);
        experiment.addArchive(makeArchive(Archive.TYPE_LEVEL_2, Archive.STATUS_UPLOADED,
                TEST_DIR + File.separator + "gsc.org_TEST.DNASeq.Level_2.1.0.0"));
        assertTrue(mageTabExperimentChecker.experimentRequiresMageTab(experiment));
        assertTrue(mageTabExperimentChecker.isMageTabOptional(experiment));
    }

    @Test
    public void testRequiresMageTabGscVcfOnlyNotConverted() throws IOException, ParseException {
        context.checking(new Expectations() {{
            one(mockCenterQueries).doesCenterRequireMageTab("gsc.org", Experiment.TYPE_GSC);
            will(returnValue(true));
        }});

        experiment.setCenterName("gsc.org");
        experiment.setType(Experiment.TYPE_GSC);
        experiment.addArchive(makeArchive(Archive.TYPE_LEVEL_2, Archive.STATUS_UPLOADED,
                TEST_DIR + File.separator + "gsc.org_TEST.DNASeq.Level_2.1.0.0"));
        assertTrue(mageTabExperimentChecker.experimentRequiresMageTab(experiment));
        assertTrue(mageTabExperimentChecker.isMageTabOptional(experiment));
    }

    @Test
    public void testRequiresMageTabGscMafOnly() throws IOException, ParseException {
        context.checking(new Expectations() {{
            one(mockCenterQueries).doesCenterRequireMageTab("gsc.org", Experiment.TYPE_GSC);
            will(returnValue(true));
        }});

        experiment.setCenterName("gsc.org");
        experiment.setType(Experiment.TYPE_GSC);
        experiment.addArchive(makeArchive(Archive.TYPE_LEVEL_2, Archive.STATUS_UPLOADED,
                TEST_DIR + File.separator + "gsc.org_DIS.DNASeq.Level_2.3.6.0"));
        assertTrue(mageTabExperimentChecker.experimentRequiresMageTab(experiment));
        assertFalse(mageTabExperimentChecker.isMageTabOptional(experiment));
    }

     @Test
    public void testRequiresMageTabGscMafOnlyNotConverted() throws IOException, ParseException {
        context.checking(new Expectations() {{
            one(mockCenterQueries).doesCenterRequireMageTab("gsc.org", Experiment.TYPE_GSC);
            will(returnValue(false));
        }});

        experiment.setCenterName("gsc.org");
        experiment.setType(Experiment.TYPE_GSC);
        experiment.addArchive(makeArchive(Archive.TYPE_LEVEL_2, Archive.STATUS_UPLOADED,
                TEST_DIR + File.separator + "gsc.org_DIS.DNASeq.Level_2.3.6.0"));
        assertFalse(mageTabExperimentChecker.experimentRequiresMageTab(experiment));
        assertFalse(mageTabExperimentChecker.isMageTabOptional(experiment));
    }

    @Test
    public void testRequiresMageTabBcr() throws IOException, ParseException {
        context.checking(new Expectations() {{
            one(mockCenterQueries).doesCenterRequireMageTab("nwch.org", Experiment.TYPE_BCR);
            will(returnValue(false));
        }});
        experiment.setCenterName("nwch.org");
        experiment.setType(Experiment.TYPE_BCR);
        assertFalse(mageTabExperimentChecker.experimentRequiresMageTab(experiment));
        assertFalse(mageTabExperimentChecker.isMageTabOptional(experiment));
    }

    private Archive makeArchive(final String archiveType, final String archiveStatus, final String archiveDirectory) {
        final Archive archive = new Archive();
        archive.setArchiveType(archiveType);
        archive.setDeployStatus(archiveStatus);
        archive.setArchiveFile(new File(archiveDirectory + ".tar.gz"));
        archive.setDeployLocation(archiveDirectory + ".tar.gz");

        return archive;
    }

    @Test
    public void testDoWorkRequiresMageTabButMissingMageTab() throws Processor.ProcessorException {
        context.checking(new Expectations() {{
            one(mockCenterQueries).doesCenterRequireMageTab("test.org", Experiment.TYPE_CGCC);
            will(returnValue(true));
        }});
        experiment.setCenterName("test.org");
        experiment.setType(Experiment.TYPE_CGCC);
        experiment.addArchive(makeArchive(Archive.TYPE_LEVEL_1, Archive.STATUS_UPLOADED, "whatever"));
        assertTrue(mageTabExperimentChecker.doWork(experiment, qcContext));
        assertEquals(1, qcContext.getErrorCount());
        assertTrue(qcContext.getErrors().get(0).contains("MAGE-TAB archive not found"));
        assertEquals(Experiment.STATUS_PENDING, experiment.getStatus());
    }

    @Test
    public void testDoWorkRequiresMageTabNotUploaded() throws Processor.ProcessorException {
        context.checking(new Expectations() {{
            one(mockCenterQueries).doesCenterRequireMageTab("test.org", Experiment.TYPE_CGCC);
            will(returnValue(true));
        }});
        experiment.setCenterName("test.org");
        experiment.setType(Experiment.TYPE_CGCC);
        experiment.addArchive(makeArchive(Archive.TYPE_LEVEL_1, Archive.STATUS_UPLOADED, "whatever"));
        experiment.addArchive(makeArchive(Archive.TYPE_MAGE_TAB, Archive.STATUS_AVAILABLE, "somewhere"));

        assertTrue(mageTabExperimentChecker.doWork(experiment, qcContext));
        assertEquals(1, qcContext.getErrorCount());
        assertTrue(qcContext.getErrors().get(0).contains("Updated mage-tab archive has not been uploaded yet."));
        assertEquals(Experiment.STATUS_PENDING, experiment.getStatus());
    }

    @Test
    public void testDoWorkTwoMageTabs() throws Processor.ProcessorException {
        context.checking(new Expectations() {{
            one(mockCenterQueries).doesCenterRequireMageTab("test.org", Experiment.TYPE_CGCC);
            will(returnValue(true));
        }});
        experiment.setCenterName("test.org");
        experiment.setType(Experiment.TYPE_CGCC);
        experiment.addArchive(makeArchive(Archive.TYPE_MAGE_TAB, Archive.STATUS_UPLOADED, "whatever"));
        experiment.addArchive(makeArchive(Archive.TYPE_MAGE_TAB, Archive.STATUS_UPLOADED, "somewhere"));

        try {
            mageTabExperimentChecker.doWork(experiment, qcContext);
            fail("Exception should have been thrown");
        } catch (Processor.ProcessorException e) {
            assertEquals("Only one mage-tab archive is allowed per experiment.  Please submit a single mage-tab archive containing all experiment samples in a its SDRF file.",
                    e.getMessage());
        }
    }

    @Test
    public void testDoWorkOneMageTabAllArchivesUploaded() throws Processor.ProcessorException {
        context.checking(new Expectations() {{
            one(mockCenterQueries).doesCenterRequireMageTab("test.org", Experiment.TYPE_CGCC);
            will(returnValue(true));
        }});
        experiment.setCenterName("test.org");
        experiment.setType(Experiment.TYPE_CGCC);
        experiment.addArchive(makeArchive(Archive.TYPE_MAGE_TAB, Archive.STATUS_UPLOADED,
                TEST_DIR + File.separator + "test.org_TEST.DNASeq.mage-tab.1.0.0"));
        final Archive dataArchive = makeArchive(Archive.TYPE_LEVEL_2, Archive.STATUS_UPLOADED, "na");
        dataArchive.setRealName("test.org_TEST.DNASeq.Level_2.1.0.0");
        experiment.addArchive(dataArchive);
        final Archive dataArchive2 = makeArchive(Archive.TYPE_LEVEL_2, Archive.STATUS_UPLOADED, "ok");
        dataArchive2.setRealName("test.org_TEST.DNASeq.Level_2.2.1.0");
        experiment.addArchive(dataArchive2);
        assertTrue(mageTabExperimentChecker.doWork(experiment, qcContext));
        assertEquals(0, qcContext.getErrorCount());
    }

    @Test
    public void testDoWorkOneMageTabUnexpected() {

        context.checking(new Expectations() {{
            one(mockCenterQueries).doesCenterRequireMageTab("test.org", Experiment.TYPE_CGCC);
            will(returnValue(false));
        }});

        experiment.setCenterName("test.org");
        experiment.setType(Experiment.TYPE_CGCC);
        experiment.addArchive(makeArchive(Archive.TYPE_MAGE_TAB, Archive.STATUS_UPLOADED,
                TEST_DIR + File.separator + "test.org_TEST.DNASeq.mage-tab.1.0.0"));

        final Archive dataArchive = makeArchive(Archive.TYPE_LEVEL_2, Archive.STATUS_UPLOADED, "na");
        dataArchive.setRealName("test.org_TEST.DNASeq.Level_2.1.0.0");

        experiment.addArchive(dataArchive);

        final Archive dataArchive2 = makeArchive(Archive.TYPE_LEVEL_2, Archive.STATUS_UPLOADED, "ok");
        dataArchive2.setRealName("test.org_TEST.DNASeq.Level_2.2.1.0");

        experiment.addArchive(dataArchive2);

        qcContext.setCenterName("squirrel.gov");

        try {
            mageTabExperimentChecker.doWork(experiment, qcContext);
            fail("ProcessorException was not thrown.");
        } catch (final Processor.ProcessorException e) {
            assertEquals("The experiment included 1 or more mage-tab archive(s) which is not expected for center squirrel.gov", e.getMessage());
        }
    }

    @Test
    public void testDoWorkOneMageTabDataArchiveMissing() throws Processor.ProcessorException {
        context.checking(new Expectations() {{
            one(mockCenterQueries).doesCenterRequireMageTab("test.org", Experiment.TYPE_CGCC);
            will(returnValue(true));
        }});
        experiment.setCenterName("test.org");
        experiment.setType(Experiment.TYPE_CGCC);
        experiment.addArchive(makeArchive(Archive.TYPE_MAGE_TAB, Archive.STATUS_UPLOADED,
                TEST_DIR + File.separator + "test.org_TEST.DNASeq.mage-tab.1.0.0"));
        final Archive dataArchive = makeArchive(Archive.TYPE_LEVEL_2, Archive.STATUS_UPLOADED, "na");
        dataArchive.setRealName("test.org_TEST.DNASeq.Level_2.1.0.0");
        experiment.addArchive(dataArchive);
        assertTrue(mageTabExperimentChecker.doWork(experiment, qcContext));
        assertEquals(1, qcContext.getErrorCount());
        assertTrue(qcContext.getErrors().get(0).
                contains("Archive 'test.org_TEST.DNASeq.Level_2.2.1.0' is listed in the SDRF but either has not yet " +
                        "been uploaded or is not the latest available archive for that type and serial index"));
        assertEquals(Experiment.STATUS_PENDING, experiment.getStatus());
    }


    @Test
    public void testDoWorkNoMageTabNeeded() throws Processor.ProcessorException {
         context.checking(new Expectations() {{
            one(mockCenterQueries).doesCenterRequireMageTab("test.org", Experiment.TYPE_GSC);
            will(returnValue(false));
        }});

        experiment.setCenterName("test.org");
        experiment.setType(Experiment.TYPE_GSC);
        assertTrue(mageTabExperimentChecker.doWork(experiment, qcContext));
    }

    @Test
    public void testMissingSdrf() {
        context.checking(new Expectations() {{
            one(mockCenterQueries).doesCenterRequireMageTab("test.org", Experiment.TYPE_CGCC);
            will(returnValue(true));
        }});
        experiment.setCenterName("test.org");
        experiment.setType(Experiment.TYPE_CGCC);
        experiment.addArchive(makeArchive(Archive.TYPE_MAGE_TAB, Archive.STATUS_UPLOADED,
                TEST_DIR + File.separator + "test.org_TEST.DNASeq.mage-tab.5.0.0"));
        try {
            mageTabExperimentChecker.doWork(experiment, qcContext);
            fail("Exception should have been thrown");
        } catch (Processor.ProcessorException e) {
            assertEquals("The MAGE-TAB archive does not contain an SDRF", e.getMessage());
        }

    }

     @Test
    public void testMissingIdf() {
        context.checking(new Expectations() {{
            one(mockCenterQueries).doesCenterRequireMageTab("test.org", Experiment.TYPE_CGCC);
            will(returnValue(true));
        }});
        experiment.setCenterName("test.org");
        experiment.setType(Experiment.TYPE_CGCC);
        experiment.addArchive(makeArchive(Archive.TYPE_MAGE_TAB, Archive.STATUS_UPLOADED,
                TEST_DIR + File.separator + "test.org_TEST.DNASeq.mage-tab.6.1.0"));
        try {
            mageTabExperimentChecker.doWork(experiment, qcContext);
            fail("Exception should have been thrown");
        } catch (Processor.ProcessorException e) {
            assertEquals("The MAGE-TAB archive does not contain an IDF file", e.getMessage());
        }

    }
}
