/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CodeTableQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRUtils;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Test class for the biospecimenXmlValidator
 * 
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class BiospecimenXmlValidatorFastTest {

	private static final String SAMPLES_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;

	private final String testLocation = SAMPLES_DIR
			+ "qclive/biospecimenXmlValidator";

	private final String biospecimenXmlEtalon = testLocation + File.separator
			+ "nationwidechildrens.org_biospecimen.TCGA-C4-A0F6.xml";
	private final String clinicalXmlEtalon = testLocation + File.separator
			+ "nationwidechildrens.org_clinical.TCGA-C4-A0F6.xml";
	private final String biospecimenXmlBad = testLocation + File.separator
			+ "biospecimen_bad.xml";
    private final String missingBatchNumber = testLocation + File.separator + "missingBatchNumber.xml";
    private final String invalidBatchNumber = testLocation + File.separator + "invalidBatchNumber.xml";

    private final String controlXmlGood = testLocation + File.separator + "good_control.TCGA-ZZ-1234.xml";
    private final String controlXmlBad = testLocation + File.separator + "bad_control.TCGA-ZZ-1234.xml";
    private final String biospecimenXmlDuplicateCodes = testLocation + File.separator + "nationwidechildrens.org_biospecimen.TCGA-00-0000.xml";

	private final Mockery context = new JUnit4Mockery();
	private BiospecimenXmlValidator biospecimenXmlValidator;
	private Archive archive;
	private QcContext qcContext;
	private CodeTableQueries mockCodeTableQueries;
    private BCRUtils mockBcrUtils;

	@Before
	public void setup() {

		archive = new Archive();
		biospecimenXmlValidator = new BiospecimenXmlValidator();
		archive.setExperimentType(Experiment.TYPE_BCR);
		archive.setTumorType("fakeTumor");
		qcContext = new QcContext();
		qcContext.setArchive(archive);
		mockCodeTableQueries = context.mock(CodeTableQueries.class);

        mockBcrUtils = context.mock(BCRUtils.class);
        biospecimenXmlValidator.setBcrUtils(mockBcrUtils);
		biospecimenXmlValidator.setCodeTableQueries(mockCodeTableQueries);
	}

    @Test
    public void testWrongSerialIndex() throws Processor.ProcessorException {
        archive.setSerialIndex("999");

        context.checking(new Expectations() {{
            one(mockBcrUtils).isBiospecimenFile(with(any(File.class)));
            will(returnValue(true));

            one(mockBcrUtils).isControlFile(with(any(File.class)));
            will(returnValue(false));

            one(mockCodeTableQueries).tssCodeExists(with("C4"));
            will(returnValue(true));

            one(mockCodeTableQueries).sampleTypeExists(with("10"));
            will(returnValue(true));

            one(mockCodeTableQueries).sampleTypeExists(with("01"));
            will(returnValue(true));

            one(mockCodeTableQueries).portionAnalyteExists(with("D"));
            will(returnValue(true));

            one(mockCodeTableQueries).portionAnalyteExists(with("R"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("08"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("05"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("07"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("13"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("01"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("02"));
            will(returnValue(true));
        }});

        final boolean isValid = biospecimenXmlValidator.processFile(new File(
                biospecimenXmlEtalon), qcContext);

        assertFalse(isValid);
        assertEquals("Validation error in XML file 'nationwidechildrens.org_biospecimen.TCGA-C4-A0F6.xml': batch_number is 86 but archive has serial index 999",
                qcContext.getErrors().get(0));

    }

    @Test
    public void testMissingSerialIndex() throws Processor.ProcessorException {
        archive.setSerialIndex("86");

        context.checking(new Expectations() {{

            one(mockBcrUtils).isBiospecimenFile(with(any(File.class)));
            will(returnValue(true));

            one(mockBcrUtils).isControlFile(with(any(File.class)));
            will(returnValue(false));

            one(mockCodeTableQueries).tssCodeExists(with("C4"));
            will(returnValue(true));

            one(mockCodeTableQueries).sampleTypeExists(with("10"));
            will(returnValue(true));

            one(mockCodeTableQueries).sampleTypeExists(with("01"));
            will(returnValue(true));

            one(mockCodeTableQueries).portionAnalyteExists(with("D"));
            will(returnValue(true));

            one(mockCodeTableQueries).portionAnalyteExists(with("R"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("08"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("05"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("07"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("13"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("01"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("02"));
            will(returnValue(true));
        }});

        assertFalse(biospecimenXmlValidator.processFile(new File(missingBatchNumber), qcContext));
        assertEquals("Validation error in XML file 'missingBatchNumber.xml': batch_number is empty", qcContext.getErrors().get(0));
    }

    @Test
    public void testInvalidBatchNumber() throws Processor.ProcessorException {
        archive.setSerialIndex("123");

        context.checking(new Expectations() {{

            one(mockBcrUtils).isBiospecimenFile(with(any(File.class)));
            will(returnValue(true));

            one(mockBcrUtils).isControlFile(with(any(File.class)));
            will(returnValue(false));

            one(mockCodeTableQueries).tssCodeExists(with("C4"));
            will(returnValue(true));

            one(mockCodeTableQueries).sampleTypeExists(with("10"));
            will(returnValue(true));

            one(mockCodeTableQueries).sampleTypeExists(with("01"));
            will(returnValue(true));

            one(mockCodeTableQueries).portionAnalyteExists(with("D"));
            will(returnValue(true));

            one(mockCodeTableQueries).portionAnalyteExists(with("R"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("08"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("05"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("07"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("13"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("01"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("02"));
            will(returnValue(true));
        }});

        assertFalse(biospecimenXmlValidator.processFile(new File(invalidBatchNumber), qcContext));
        assertEquals("Validation error in XML file 'invalidBatchNumber.xml': batch_number does not contain a batch number", qcContext.getErrors().get(0));
    }

    @Test
    public void testControlXmlGoodFile() throws Processor.ProcessorException {
        archive.setSerialIndex("0");
        final File controlXmlFile = new File(controlXmlGood);

        context.checking(new Expectations() {{

            one(mockBcrUtils).isBiospecimenFile(controlXmlFile);
            will(returnValue(false));

            exactly(2).of(mockBcrUtils).isControlFile(controlXmlFile);
            will(returnValue(true));

            one(mockCodeTableQueries).tssCodeExists(with("ZZ"));
            will(returnValue(true));

            one(mockCodeTableQueries).sampleTypeExists(with("10"));
            will(returnValue(true));

            one(mockCodeTableQueries).sampleTypeExists(with("01"));
            will(returnValue(true));

            one(mockCodeTableQueries).portionAnalyteExists(with("D"));
            will(returnValue(true));

            one(mockCodeTableQueries).portionAnalyteExists(with("R"));
            will(returnValue(true));

            one(mockCodeTableQueries).portionAnalyteExists(with("W"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("08"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("05"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("07"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("13"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("01"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("20"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("09"));
            will(returnValue(true));
        }});

        boolean isValid = biospecimenXmlValidator.processFile(controlXmlFile, qcContext);
        assertTrue(qcContext.getErrors().toString(), isValid);
    }

    @Test
    public void testControlXmlBadFile() throws Processor.ProcessorException {
        archive.setSerialIndex("0");
        final File controlXmlFile = new File(controlXmlBad);

        context.checking(new Expectations() {{

            one(mockBcrUtils).isBiospecimenFile(controlXmlFile);
            will(returnValue(false));

            exactly(2).of(mockBcrUtils).isControlFile(controlXmlFile);
            will(returnValue(true));

            one(mockCodeTableQueries).tssCodeExists(with("ZZ"));
            will(returnValue(true));

            one(mockCodeTableQueries).sampleTypeExists(with("10"));
            will(returnValue(true));

            one(mockCodeTableQueries).sampleTypeExists(with("01"));
            will(returnValue(true));

            one(mockCodeTableQueries).portionAnalyteExists(with("D"));
            will(returnValue(true));

            one(mockCodeTableQueries).portionAnalyteExists(with("R"));
            will(returnValue(true));

            one(mockCodeTableQueries).portionAnalyteExists(with("W"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("08"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("05"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("07"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("13"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("01"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("20"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("09"));
            will(returnValue(true));
        }});

        assertFalse(biospecimenXmlValidator.processFile(controlXmlFile, qcContext));
        assertEquals(4, qcContext.getErrorCount());
        assertEquals("Validation error in XML file 'bad_control.TCGA-ZZ-1234.xml': control with UUID 3aad87f2-5a55-48d9-96ef-5e33870008ee does not have a corresponding 'aliquot' block", qcContext.getErrors().get(0));
        assertEquals("Validation error in XML file 'bad_control.TCGA-ZZ-1234.xml': aliquot TCGA-ZZ-1234-10A-01D-A075-09 (2aad87f2-5a55-48d9-96ef-5e33870008ee) does not have a corresponding 'control' block", qcContext.getErrors().get(1));
        assertEquals("Validation error in XML file 'bad_control.TCGA-ZZ-1234.xml': aliquot TCGA-ZZ-1234-01A-11D-A058-05 (71213ae5-0aa7-4caf-ad3c-3453ea6c9ebd) has a different UUID in its 'control' block: 61213ae5-0aa7-4caf-ad3c-3453ea6c9ebd", qcContext.getErrors().get(2));
        assertEquals("Validation error in XML file 'bad_control.TCGA-ZZ-1234.xml': control TCGA-ZZ-4321-01A-21-A13A-20 (188575e2-83fd-4463-8bd3-aed928b5acf0) does not have a corresponding 'aliquot' block", qcContext.getErrors().get(3));
    }

	@Test
	public void testBiospecimenXmlGoodFile() throws Exception {

        archive.setSerialIndex("86");

        context.checking(new Expectations() {{

            one(mockBcrUtils).isBiospecimenFile(with(any(File.class)));
            will(returnValue(true));

            one(mockBcrUtils).isControlFile(with(any(File.class)));
            will(returnValue(false));

            one(mockCodeTableQueries).tssCodeExists(with("C4"));
            will(returnValue(true));

            one(mockCodeTableQueries).sampleTypeExists(with("10"));
            will(returnValue(true));

            one(mockCodeTableQueries).sampleTypeExists(with("01"));
            will(returnValue(true));

            one(mockCodeTableQueries).portionAnalyteExists(with("D"));
            will(returnValue(true));

            one(mockCodeTableQueries).portionAnalyteExists(with("R"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("08"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("05"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("07"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("13"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("01"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("02"));
            will(returnValue(true));
        }});

		assertTrue(biospecimenXmlValidator.processFile(new File(
				biospecimenXmlEtalon), qcContext));     
		
		assertEquals (0,qcContext.getWarnings().size());
	}

	@Test
	public void testBiospecimenXmlClinicalFile() throws Exception {

        context.checking(new Expectations() {{
            one(mockBcrUtils).isBiospecimenFile(with(any(File.class)));
            will(returnValue(false));

            one(mockBcrUtils).isControlFile(with(any(File.class)));
            will(returnValue(false));
        }});

		assertTrue(biospecimenXmlValidator.processFile(new File(
				clinicalXmlEtalon), qcContext));
	}

    @Test
    public void testAuxiliaryFile() throws Exception {

        final File file = new File("auxiliary.test");

        context.checking(new Expectations() {{
            allowing(mockBcrUtils).isBiospecimenFile(file);
            will(returnValue(false));
            allowing(mockBcrUtils).isControlFile(with(any(File.class)));
            will(returnValue(false));
        }});

        assertTrue(biospecimenXmlValidator.processFile(file, qcContext));
    }

	@Test
	public void testBiospecimenXmlBadFile() throws Exception {

        archive.setSerialIndex("86");

        context.checking(new Expectations() {{

            one(mockBcrUtils).isBiospecimenFile(with(any(File.class)));
            will(returnValue(true));

            allowing(mockBcrUtils).isControlFile(with(any(File.class)));
            will(returnValue(false));

            one(mockCodeTableQueries).tssCodeExists(with("ZORG"));
            will(returnValue(false));

            one(mockCodeTableQueries).sampleTypeExists(with("999"));
            will(returnValue(false));

            one(mockCodeTableQueries).sampleTypeExists(with("01"));
            will(returnValue(true));

            one(mockCodeTableQueries).portionAnalyteExists(with("ZORG"));
            will(returnValue(false));

            one(mockCodeTableQueries).portionAnalyteExists(with("D"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("08"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("05"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("07"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("13"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("01"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("02"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("999"));
            will(returnValue(false));
        }});

		assertFalse(biospecimenXmlValidator.processFile(new File(biospecimenXmlBad), qcContext));
		assertEquals(qcContext.getErrors().toString(), 6, qcContext.getErrorCount());
		assertEquals(
						"Validation error in XML file 'biospecimen_bad.xml': "
						+ "TSS ZORG is not a valid tissue source site code",
				qcContext.getErrors().get(0));
		assertEquals(
                       "Validation error in XML file 'biospecimen_bad.xml': "
                        + "SampleTypeId 999 is not a valid sample type code",
				qcContext.getErrors().get(1));
		assertEquals(
						"Validation error in XML file 'biospecimen_bad.xml': "
						+ "portionNumber 11-7 is not alphanumeric", qcContext
						.getErrors().get(2));
		assertEquals(
						"Validation error in XML file 'biospecimen_bad.xml': "
						+ "AnalyteTypeId ZORG is not a valid analyte type code",
				qcContext.getErrors().get(3));
		assertEquals(
						"Validation error in XML file 'biospecimen_bad.xml': "
						+ "plateId A12W-l is not alphanumeric", qcContext
						.getErrors().get(4));
		assertEquals(
						"Validation error in XML file 'biospecimen_bad.xml': "
						+ "BCR centerId 999 is not a valid BCR center code",
				qcContext.getErrors().get(5));
	}

    @Test
    public void testCodesValidatedOnlyOnce() throws Processor.ProcessorException {

        archive.setSerialIndex("30");

        context.checking(new Expectations() {{

            one(mockBcrUtils).isBiospecimenFile(with(any(File.class)));
            will(returnValue(true));

            allowing(mockBcrUtils).isControlFile(with(any(File.class)));
            will(returnValue(false));

            one(mockCodeTableQueries).tssCodeExists(with("C4"));
            will(returnValue(true));

            one(mockCodeTableQueries).sampleTypeExists(with("01"));
            will(returnValue(true));

            one(mockCodeTableQueries).portionAnalyteExists(with("R"));
            will(returnValue(true));

            one(mockCodeTableQueries).portionAnalyteExists(with("D"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("08"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("01"));
            will(returnValue(true));

            one(mockCodeTableQueries).bcrCenterIdExists(with("02"));
            will(returnValue(true));
        }});

        assertTrue(biospecimenXmlValidator.processFile(new File(biospecimenXmlDuplicateCodes), qcContext));
        assertEquals(0, qcContext.getErrorCount());
    }
}
