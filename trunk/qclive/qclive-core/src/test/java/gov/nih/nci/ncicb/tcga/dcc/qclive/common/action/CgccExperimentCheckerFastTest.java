package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
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
import java.util.Arrays;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * Test class for CgccExperimentValidator
 *
 * @author ramanr Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class CgccExperimentCheckerFastTest {
    private static final String SAMPLES_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;

    private static final String EXPERIMENT_CHECKER_DIR = SAMPLES_DIR + "qclive"
            + File.separator + "experimentChecker" + File.separator;

    private static final String LEVEL_1_ARCHIVE = "domain_disease.platform.Level_1.1.0.0";
    private static final String LEVEL_2_ARCHIVE = "domain_disease.platform.Level_2.1.1.0";
    private static final String LEVEL_3_ARCHIVE = "domain_disease.platform.Level_3.1.0.0";

    private Experiment experiment;

    private File sdrfFile;
    private String experimentName = "center_disease.platform";
    private Archive mageTabArchive;
    private Archive invalidMageTabArchive; // Contains archive names with invalid format
    private MageTabExperimentChecker cgccExperimentChecker;
    private QcContext qcContext;
    private Mockery context;
    private ArchiveQueries mockArchiveQueries;

    @Before
    public void setup() {
        cgccExperimentChecker = new MageTabExperimentChecker();
        sdrfFile = new File(EXPERIMENT_CHECKER_DIR+ "domain_disease.platform_mage-tab.1.3.0/test.sdrf.txt");

        experiment = new Experiment(){
            public File getSdrfFile() {
                return sdrfFile;
            }            
        };
        
        qcContext = new QcContext();
        qcContext.setExperiment(experiment);
        experiment.setName(experimentName);
        experiment.setType("CGCC");
        
        mageTabArchive = new Archive(EXPERIMENT_CHECKER_DIR+"domain_disease.platform_mage-tab.1.3.0.tar.gz");
        mageTabArchive.setArchiveType(Archive.TYPE_MAGE_TAB);
        mageTabArchive.setDeployStatus(Archive.STATUS_AVAILABLE);
        mageTabArchive.setPlatform("platform");
        mageTabArchive.setDeployLocation(EXPERIMENT_CHECKER_DIR+"domain_disease.platform_mage-tab.1.3.0.tar.gz");

        final String invalidMageTabArchiveName = "domain_disease.platform_mage-tab.1.4.0.tar.gz";
        invalidMageTabArchive = new Archive(EXPERIMENT_CHECKER_DIR + invalidMageTabArchiveName);
        invalidMageTabArchive.setArchiveType(Archive.TYPE_MAGE_TAB);
        invalidMageTabArchive.setDeployStatus(Archive.STATUS_AVAILABLE);
        invalidMageTabArchive.setPlatform("platform");
        invalidMageTabArchive.setDeployLocation(EXPERIMENT_CHECKER_DIR + invalidMageTabArchiveName);


        context = new JUnit4Mockery();
        mockArchiveQueries = context.mock(ArchiveQueries.class);
        cgccExperimentChecker.setArchiveQueries(mockArchiveQueries);
    }

    @Test
    public void checkValidMageTabArchive() throws Processor.ProcessorException, IOException{
        final Archive level1Archive = new Archive();
        level1Archive.setRealName(LEVEL_1_ARCHIVE);

        final Archive level2Archive = new Archive();
        level2Archive.setRealName(LEVEL_2_ARCHIVE);

        final Archive level3Archive = new Archive();
        level3Archive.setRealName(LEVEL_3_ARCHIVE);

        experiment.setArchives(Arrays.asList(level1Archive, level2Archive, level3Archive));
        cgccExperimentChecker.checkMageTabArchive(experiment,mageTabArchive,qcContext);
        assertEquals(0, qcContext.getErrorCount());
        
    }

    @Test
    public void testInvalidArchiveNameInMageTabArchive()
            throws IOException{

        final Archive level1Archive = new Archive();
        level1Archive.setRealName(LEVEL_1_ARCHIVE);

        final Archive level2Archive = new Archive();
        level2Archive.setRealName(LEVEL_2_ARCHIVE);

        final Archive level3Archive = new Archive();
        level3Archive.setRealName(LEVEL_3_ARCHIVE);

        experiment.setArchives(Arrays.asList(level1Archive,level2Archive,level3Archive));
        try {
            cgccExperimentChecker.checkMageTabArchive(experiment,invalidMageTabArchive,qcContext);
            fail("ProcessorException was not thrown.");

        } catch (final Processor.ProcessorException e) {

            assertEquals(Experiment.STATUS_FAILED, experiment.getStatus());

            final String expectedErrorMessage = "An error occurred while processing experiment 'center_disease.platform': " +
                    "Archive 'Squirrel' is listed in the SDRF but is not a valid archive name format (expecting [center]_[disease].[platform].[archive_type].[batch].[revision].[series])";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
    }

    @Test
    public void checkMageTabArchiveWithMissingArchives() throws Processor.ProcessorException, IOException{
        final Archive level1Archive = new Archive();
        level1Archive.setRealName(LEVEL_1_ARCHIVE);

        final Archive level2Archive = new Archive();
        level2Archive.setRealName(LEVEL_2_ARCHIVE);
        experiment.setArchives(Arrays.asList(level1Archive,level2Archive));
        context.checking(new Expectations() {
            {
                one(mockArchiveQueries).getArchiveIdByName(LEVEL_3_ARCHIVE);
                will(returnValue(-1L));
            }
        });

        cgccExperimentChecker.checkMageTabArchive(experiment,mageTabArchive,qcContext);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("An error occurred while processing experiment 'center_disease.platform': Archive 'domain_disease.platform.Level_3.1.0.0' is listed in the SDRF but has not yet been uploaded",
                qcContext.getErrors().get(0));
    }


    @Test
    public void checkMageTabArchiveWithOldArchive() throws Processor.ProcessorException, IOException{
        final Archive level1Archive = new Archive();
        level1Archive.setRealName(LEVEL_1_ARCHIVE);

        final Archive level2Archive = new Archive();
        level2Archive.setRealName(LEVEL_2_ARCHIVE);
        experiment.setArchives(Arrays.asList(level1Archive,level2Archive));
        context.checking(new Expectations() {
            {
                one(mockArchiveQueries).getArchiveIdByName(LEVEL_3_ARCHIVE);
                will(returnValue(1L));
            }
        });

        cgccExperimentChecker.checkMageTabArchive(experiment,mageTabArchive,qcContext);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("An error occurred while processing experiment 'center_disease.platform': Archive 'domain_disease.platform.Level_3.1.0.0' is listed in the SDRF but is not the latest available archive for that type and serial index",
                qcContext.getErrors().get(0));
    }


}
