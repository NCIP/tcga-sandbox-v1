package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.BatchNumberAssignment;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.BatchNumberQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Test class for BCRBatchNumberValidator
 *
 * @author Your Name
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class BCRBatchNumberValidatorFastTest {
    private final Mockery context = new JUnit4Mockery();
    private final BatchNumberQueries mockBatchNumberQueries = context.mock(BatchNumberQueries.class);
    private final PlatformQueries mockPlatformQueries = context.mock(PlatformQueries.class);
    private BCRBatchNumberValidator val;
    private Archive archive;
    private QcContext qcContext;

    private final static String center = "intgen.org";
    private final static String disease = "GBM";
    private final static String platform = "bio";
    private final static String type = "Level_1";
    private final static String batch = "3";
    private final static String revision = "2";
    private final static String series = "0";
    private final static String badPlatform = "Genome_Wide_SNP_6";

    @Before
    public void setup() {
        qcContext = new QcContext();
        val = new BCRBatchNumberValidator();
        context.assertIsSatisfied();
        archive = new Archive();
        val.setBatchNumberQueries(mockBatchNumberQueries);
        val.setPlatformQueries(mockPlatformQueries);
        archive.setArchiveFile(new File(center + "_" + disease + "." + platform + "." + type + "." +
                batch + "." + revision + "." + series + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION));
        archive.setPlatform(platform);
        archive.setTumorType(disease);
        archive.setDomainName(center);
        archive.setSerialIndex(batch);
        archive.setArchiveType(type);
        archive.setRevision(revision);
        archive.setSeries(series);
        qcContext.setArchive(archive);
    }

    @Test
    public void testExecute() throws Processor.ProcessorException {
        context.checking(new Expectations() {{
            one(mockBatchNumberQueries).isValidBatchNumberAssignment(Integer.valueOf(archive.getSerialIndex()), archive.getTumorType(), archive.getDomainName());
            will(returnValue(true));
            one(mockPlatformQueries).getPlatformForName(archive.getPlatform());
            will(returnValue(getPlatformForBCRCenterType()));
        }});


        assertTrue("Errors: " + qcContext.getErrors(), val.execute(archive, qcContext));
        assertEquals(0, qcContext.getErrorCount());
        assertEquals(0, qcContext.getWarningCount());
        assertEquals(center, archive.getDomainName());
        assertEquals(disease, archive.getTumorType());
        assertEquals(platform, archive.getPlatform());
        assertEquals(type, archive.getArchiveType());
        assertEquals(batch, archive.getSerialIndex());
        assertEquals(revision, archive.getRevision());
        assertEquals(series, archive.getSeries());
    }

    @Test
    public void testInvalidSerialIndexForBCRArchive() throws Processor.ProcessorException {
        context.checking(new Expectations() {{
            one(mockBatchNumberQueries).isValidBatchNumberAssignment(Integer.valueOf(archive.getSerialIndex()), archive.getTumorType(), archive.getDomainName());
            will(returnValue(false));
            one(mockBatchNumberQueries).getBatchNumberAssignment(Integer.valueOf(archive.getSerialIndex()));
            will(returnValue(Arrays.asList(getInValidBatchNumberAssignment())));
            one(mockPlatformQueries).getPlatformForName(archive.getPlatform());
            will(returnValue(getPlatformForBCRCenterType()));

        }});

        assertFalse(val.execute(archive, qcContext));
        assertEquals(1, qcContext.getErrorCount());
        assertTrue(qcContext.getErrors().get(0).contains("Invalid serial index"));
    }

    @Test
    public void testUnknownBatch() throws Processor.ProcessorException {
        context.checking(new Expectations() {{
            one(mockBatchNumberQueries).isValidBatchNumberAssignment(Integer.valueOf(archive.getSerialIndex()), archive.getTumorType(), archive.getDomainName());
            will(returnValue(false));
            one(mockBatchNumberQueries).getBatchNumberAssignment(Integer.valueOf(archive.getSerialIndex()));
            will(returnValue(null));
            one(mockPlatformQueries).getPlatformForName(archive.getPlatform());
            will(returnValue(getPlatformForBCRCenterType()));
        }});

        assertFalse(val.execute(archive, qcContext));
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("Batch 3 has not yet been registered with the DCC", qcContext.getErrors().get(0));
    }


    @Test
    public void testSerialIndexForBCRArchive() throws Processor.ProcessorException {

        context.checking(new Expectations() {{
            one(mockBatchNumberQueries).isValidBatchNumberAssignment(Integer.valueOf(archive.getSerialIndex()), archive.getTumorType(), archive.getDomainName());
            will(returnValue(true));
            one(mockPlatformQueries).getPlatformForName(archive.getPlatform());
            will(returnValue(getPlatformForBCRCenterType()));
        }});

        assertTrue(val.execute(archive, qcContext));
        assertEquals(0, qcContext.getErrorCount());

    }

    private BatchNumberAssignment getInValidBatchNumberAssignment() {
        final BatchNumberAssignment batchNumberAssignment = new BatchNumberAssignment();
        batchNumberAssignment.setDisease("OV");
        batchNumberAssignment.setCenterDomainName(badPlatform);
        return batchNumberAssignment;
    }


    private Platform getPlatformForBCRCenterType() {
        final Platform platform = new Platform();
        platform.setCenterType(Experiment.TYPE_BCR);
        return platform;
    }
}
