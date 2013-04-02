package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.TumorQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test the DiseaseTypeValidator class
 *
 * @author Your Name
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class DiseaseTypeValidatorFastTest {
    private final Mockery context = new JUnit4Mockery();
    private final TumorQueries mockDiseaseQueries = context.mock(TumorQueries.class);
    private DiseaseTypeValidator val;
    private Archive archive;
    private Tumor disease;
    private QcContext qcContext;
    private final static String diseaseName = "GBM";
    private final static String platform = "Genome_Wide_SNP_6";
    private final static String type = "Level_2";
    private final static String badDiseaseName = "BADDISEASE";
    private final static Integer diseaseId = 1;
     @Before
    public void setup() {
       qcContext = new QcContext();
       val = new DiseaseTypeValidator();
       context.assertIsSatisfied();
       archive = new Archive();
       disease = new Tumor();
       val.setDiseaseQueries(mockDiseaseQueries);
       archive.setPlatform(platform);
       archive.setTumorType(diseaseName);
       qcContext.setArchive(archive);
       disease.setTumorName(archive.getTumorType());
       disease.setTumorId(1);
    }
    @Test
    public void testExecute() throws Processor.ProcessorException {
        context.checking(new Expectations() {{
            one(mockDiseaseQueries).getTumorForName(archive.getTumorType());
            will(returnValue(disease));
        }});
        assertTrue("Errors: " + qcContext.getErrors(), val.execute(archive, qcContext));
        assertEquals(0, qcContext.getErrorCount());
        assertEquals(0, qcContext.getWarningCount());
        assertEquals(diseaseId, disease.getTumorId());
    }

    @Test
    public void testInvalidDiseaseForArchive() throws Processor.ProcessorException {
        archive.setTumorType(badDiseaseName);
        context.checking(new Expectations() {{
            one(mockDiseaseQueries).getTumorForName(archive.getTumorType());
            will(returnValue(null));
        }});
        assertFalse(val.execute(archive, qcContext));
        assertEquals(1, qcContext.getErrorCount());
        assertTrue(qcContext.getErrors().get(0).contains("Disease type '" + badDiseaseName + "' is not in the database"));

    }

    @Test
    public void testDiseaseForArchive() throws Processor.ProcessorException {

        context.checking(new Expectations() {{
            one(mockDiseaseQueries).getTumorForName(archive.getTumorType());
            will(returnValue(disease));
        }});

        assertTrue(val.execute(archive, qcContext));
        assertEquals(0, qcContext.getErrorCount());

    }


}
