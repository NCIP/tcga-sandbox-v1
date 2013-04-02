package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.util.QCliveXMLSchemaValidator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class XSDSchemaValidatorFastTest {

    private XSDSchemaValidator xsdSchemaValidator = null;
    private QcContext qcContext = null;
    private static final String SAMPLES_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;

    private final String testLocation = SAMPLES_DIR
            + "qclive" + File.separator
            + "clinicalXmlValidator";

    private final String schemaValidationValidXml = testLocation + File.separator
            + "nationwidechildrens.org_COAD.bio.Level_1.30.2.0" + File.separator
            + "nationwidechildrens.org_biospecimen.TCGA-00-0000.xml";

    private final String schemaValidationValidXml2Dot5Version = testLocation + File.separator
            + "nationwidechildrens.org_BRCA.bio.Level_1.85.20.0" + File.separator
            + "nationwidechildrens.org_biospecimen.TCGA-E2-A14Z.xml";

    private final String schemaValidationInValidXml = testLocation + File.separator
            + "schemaValidation" + File.separator
            + "intgen.org_clinical.TCGA-02-0004.xml";

    @Before
	public void setUp() {					
		xsdSchemaValidator = new XSDSchemaValidator();
		QCliveXMLSchemaValidator validator = new QCliveXMLSchemaValidator();		
		validator.setValidXsdDomainPattern("ncisvn\\.nci\\.nih\\.gov");
		validator.setValidXsdPrefixPattern("bcr");
		validator.setValidXsdVersionPattern("2\\.6(\\.\\d*)?");
		xsdSchemaValidator.setqCliveXMLSchemaValidator(validator);
		qcContext = new QcContext();
	}

	@Test
	public void testValidXMLFile() throws ProcessorException {
		xsdSchemaValidator.getqCliveXMLSchemaValidator().setAllowLocalSchema(false);		
		xsdSchemaValidator.processFile(new File(schemaValidationValidXml), qcContext);
		assertTrue(qcContext.getErrors().toString(),qcContext.getErrorCount() == 0);
	}

    @Test
    public void testValidXMLFile2Dot5Version() throws ProcessorException {

        xsdSchemaValidator.getqCliveXMLSchemaValidator().setValidXsdDomainPattern("tcga-data\\.nci\\.nih\\.gov");
        xsdSchemaValidator.getqCliveXMLSchemaValidator().setAllowLocalSchema(false);
        xsdSchemaValidator.getqCliveXMLSchemaValidator().setValidXsdVersionPattern("2\\.5(\\.\\d*)?");
        final boolean isValid = xsdSchemaValidator.processFile(new File(schemaValidationValidXml2Dot5Version), qcContext);

        assertTrue(isValid);
        assertEquals(0, qcContext.getErrorCount());
        assertEquals(0, qcContext.getWarningCount());
    }

	@Test
	public void testInValidXMLFile() throws ProcessorException {
		xsdSchemaValidator.getqCliveXMLSchemaValidator().setAllowLocalSchema(false);		
		boolean isValid = xsdSchemaValidator.processFile(new File(schemaValidationInValidXml), qcContext);
		assertFalse(isValid);
		assertEquals(1,qcContext.getErrorCount());
	}
	@Test
	public void testBadXMLFile() throws ProcessorException {
		xsdSchemaValidator.getqCliveXMLSchemaValidator().setAllowLocalSchema(false);		
		boolean isValid = xsdSchemaValidator.processFile(new File(schemaValidationInValidXml+"badFileName"), qcContext);
		assertFalse(isValid);
		assertEquals(1,qcContext.getErrorCount());
	}	
	
	
	@Test
	public void testGetName(){
		assertEquals (xsdSchemaValidator.getName()," XML schema validation");				
	}

	
	@Test
	public void testDefaultMethods(){
		Archive archive = new Archive();
		archive.setExperimentType("BCR");
		assertTrue(xsdSchemaValidator.getDefaultReturnValue(archive));
		assertEquals(".xml",xsdSchemaValidator.getFileExtension());
		assertTrue(xsdSchemaValidator.isCorrectArchiveType(archive));	
		
		Map<File,Boolean> errorMap = new HashMap<File,Boolean>();
		errorMap.put(new File(""), true);
		assertTrue(xsdSchemaValidator.getReturnValue(errorMap, qcContext));
	}	
	
}
