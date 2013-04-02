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
import gov.nih.nci.ncicb.tcga.dcc.common.bean.MetaDataBean;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.SampleType;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.SampleTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ShippedBiospecimenQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BarcodeTumorValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.util.ChromInfoUtilsImpl;
import junit.framework.Assert;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test class for MafFileValidator
 * 
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class MafFileValidatorFastTest {

	private static final String SAMPLES_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;
	
	private final Mockery context = new JUnit4Mockery();
	private MafFileValidator mafFileValidator;
	private Archive archive;
	private QcContext qcContext;
	private QcLiveBarcodeAndUUIDValidator mockBarcodeValidator;
	private SampleTypeQueries mockSampleTypeQueries;
	private ShippedBiospecimenQueries mockShippedBiospecimenQueries;
	final List<SampleType> sampleList = new ArrayList<SampleType>();
	
	@Before
	public void setup() throws Exception {		
		archive = new Archive();
		archive.setArchiveType(Archive.TYPE_LEVEL_2);
		qcContext = new QcContext();
		qcContext.setArchive(archive);
		mafFileValidator = new MafFileValidator();
		mockSampleTypeQueries = context.mock(SampleTypeQueries.class);
		mockShippedBiospecimenQueries = context.mock(ShippedBiospecimenQueries.class);
	    mafFileValidator.setSampleTypeQueries(mockSampleTypeQueries);
	    mafFileValidator.setShippedBiospecimenQueries(mockShippedBiospecimenQueries);
		mafFileValidator.setBarcodeTumorValidator(new BarcodeTumorValidator() {
			// just returns true
			public boolean barcodeIsValidForTumor(final String barcode,
					final String tumorAbbreviation) {
				return true;
			}
		});

		mockBarcodeValidator = context
				.mock(QcLiveBarcodeAndUUIDValidator.class);		
		mafFileValidator.setBarcodeValidator(mockBarcodeValidator);
        mafFileValidator.setChromInfoUtils(new ChromInfoUtilsImpl());
        mafFileValidator.sampleCodes = new HashMap<String,Boolean>();        
		mafFileValidator.sampleCodes.put("00", true);
		mafFileValidator.sampleCodes.put("03", true);
		mafFileValidator.sampleCodes.put("01", false);
		mafFileValidator.sampleCodes.put("19", false);
		mafFileValidator.sampleCodes.put("99", false);
		
		SampleType sampleElement = new SampleType();
		sampleElement.setSampleTypeCode("00");
		sampleElement.setIsTumor(true);		
		sampleList.add(sampleElement);
		
		sampleElement = new SampleType();
		sampleElement.setSampleTypeCode("01");
		sampleElement.setIsTumor(false);		
		sampleList.add(sampleElement);
		
		context.checking(new Expectations() {
			{						
				allowing(mockSampleTypeQueries).getAllSampleTypes();					
				will (returnValue(sampleList));
			}
		});
    
	}
	
	private void setUpForValidBarcodes() {
		context.checking(new Expectations() {
			{	
				allowing(mockBarcodeValidator).validate(
					with(any(String.class)), with(qcContext),
					with(any(String.class)), with(true));
					will(returnValue(true));
			}
		});
	}

    private void setUpForValidUUIDs() {
        setUpForValidUUIDs(true, true);
    }

	private void setUpForValidUUIDs(final boolean isAliquotReturnValue, final boolean uuidMappingReturnValue) {
        final String[] uuids = new String[]{
                "2e276db0-a903-46c0-8892-620fc0e94de6",
                "2e276db0-a903-46c0-8892-620fc0e94de8"
        };
		context.checking(new Expectations() {{

            for (final String uuid : uuids) {
                atLeast(1).of(mockBarcodeValidator).validateUuid(with(uuid), with(qcContext), with(any(String.class)), with(true));
                will(returnValue(true));

                atLeast(1).of(mockBarcodeValidator).isMatchingDiseaseForUUID(with(uuid), with(any(String.class)));
                will(returnValue(true));

                atLeast(1).of(mockBarcodeValidator).isAliquotUUID(uuid);
                will(returnValue(isAliquotReturnValue));
            }

            atLeast(1).of(mockShippedBiospecimenQueries).retrieveUUIDMetadata(
                    with("2e276db0-a903-46c0-8892-620fc0e94de6"));
            will(returnValue(
                    new MetaDataBean() {{
                        setTssCode("02");
                        setParticipantCode("0001");
                        setSampleCode("00");
                    }}
            ));

            atLeast(1).of(mockShippedBiospecimenQueries).retrieveUUIDMetadata(
                    with("2e276db0-a903-46c0-8892-620fc0e94de8"));
            will(returnValue(
                    new MetaDataBean() {{
                        setTssCode("02");
                        setParticipantCode("0001");
                        setSampleCode("01");
                    }}
            ));

            atLeast(1).of(mockBarcodeValidator).validateUUIDBarcodeMapping("2e276db0-a903-46c0-8892-620fc0e94de6", "TCGA-00-0000-03A-00B-0000-00");
            will(returnValue(uuidMappingReturnValue));

            atLeast(1).of(mockBarcodeValidator).validateUUIDBarcodeMapping("2e276db0-a903-46c0-8892-620fc0e94de8", "TCGA-00-0000-19C-00D-0000-00");
            will(returnValue(uuidMappingReturnValue));

        }});
	}
	
	@Test
	public void testGood() throws Processor.ProcessorException {
		setUpForValidBarcodes();		
		File goodFile = new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/good/test.maf");
		archive.setArchiveFile(new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/good/test.tar.gz"));

		boolean valid = mafFileValidator.execute(goodFile, qcContext);
		assertTrue("Errors: " + qcContext.getErrors(), valid);
	}

    @Test
    public void testHeaderWithoutUUID() throws Processor.ProcessorException {
        setUpForValidBarcodes();
        File goodFile = new File(SAMPLES_DIR
                + "qclive/mafFileValidator/good/test.maf");
        archive.setArchiveFile(new File(SAMPLES_DIR
                + "qclive/mafFileValidator/good/test.tar.gz"));
        qcContext.setCenterConvertedToUUID(true);
        boolean valid = mafFileValidator.execute(goodFile, qcContext);
        assertFalse(valid);
        assertEquals(1,qcContext.getErrorCount());
        assertTrue(qcContext.getErrors().get(0).contains("Required column 'Tumor_Sample_UUID' is missing"));
    }

    @Test
    public void testHeaderWithUUID() throws Processor.ProcessorException {
        setUpForValidBarcodes();
        setUpForValidUUIDs();
        File goodFile = new File(SAMPLES_DIR
                + "qclive/mafFileValidator/good/mafWithUUIDHeader.maf");
        archive.setArchiveFile(new File(SAMPLES_DIR
                + "qclive/mafFileValidator/good/mafWithUUIDHeader.tar.gz"));
        qcContext.setCenterConvertedToUUID(true);
        boolean valid = mafFileValidator.execute(goodFile, qcContext);
        assertTrue(valid);
    }
    
	@Test
	public void testBad() throws Processor.ProcessorException, IOException,
			ParseException {
		setUpForValidBarcodes();
		File badHeaders = new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/bad/bad_headers.maf");
		archive.setArchiveFile(new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/bad/bad_headers.tar.gz"));
		assertFalse(mafFileValidator.validate(badHeaders, qcContext));
		File incompleteRow = new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/bad/incomplete_row.maf");
		archive.setArchiveFile(new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/bad/incomplete_row.tar.gz"));
		assertFalse(mafFileValidator.validate(incompleteRow, qcContext));
		File badValues = new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/bad/bad_values.maf");
		archive.setArchiveFile(new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/bad/bad_values.tar.gz"));
		assertFalse(mafFileValidator.validate(badValues, qcContext));
		File badChromCoord = new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/bad/bad_chrom_coord.maf");
		archive.setArchiveFile(new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/bad/bad_chrom_coord.tar.gz"));
		assertFalse(mafFileValidator.validate(badChromCoord, qcContext));
	}

	@Test
	public void testBadConditions() throws IOException,
			Processor.ProcessorException {
		setUpForValidBarcodes();
		File badCondition = new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/bad/bad_conditions.maf");
		archive.setArchiveFile(new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/bad/bad_conditions.tar.gz"));
		assertFalse(mafFileValidator.validate(badCondition, qcContext));
		assertEquals(2, qcContext.getErrorCount());
		// saraswatv :made the change in line no. to give actual line no. of
		// file
		assertEquals(
				"An error occurred while validating MAF file 'bad_conditions.maf', line 2: 'Hugo_Symbol' value '' is invalid - may not be blank",
				qcContext.getErrors().get(0));
		assertEquals(
				"An error occurred while validating MAF file 'bad_conditions.maf', line 3: 'Entrez_Gene_Id' value 'hi' is invalid - must be an integer number",
				qcContext.getErrors().get(1));
	}

	@Test
	public void testConditionalRequirements() {
		setUpForValidBarcodes();
		MafFileValidator.ConditionalRequirement req = new MafFileValidator.ConditionalRequirement(
				"Validation_Status", "Valid", "Tumor_Validation_Allele1",
				Pattern.compile("^[TCGA\\-]+$"),
				"made only of characters 'A', 'C', 'G', 'T', and '-' ", false);
		Map<String, Integer> fields = new HashMap<String, Integer>();
		fields.put("Validation_Status", 0);
		fields.put("Tumor_Validation_Allele1", 1);
		fields.put("Mutation_Status", 2);
		fields.put("Tumor_Seq_Allele1", 3);
		fields.put("Match_Norm_Seq_Allele1", 4);
		String[] row = new String[] { "Valid", "ACTG-TGA", "Germline", "hello",
				"hello" };
		assertTrue(req.isSatisfied(row, fields));
		MafFileValidator.ConditionalRequirement req2 = new MafFileValidator.ConditionalRequirement(
				"Mutation_Status", "Germline", "Tumor_Seq_Allele1",
				"Match_Norm_Seq_Allele1", false);
		assertTrue(req2.isSatisfied(row, fields));
		// make an "or" requirement, which just needs 1 of the two to be true.
		MafFileValidator.ConditionalRequirement compositeReq = new MafFileValidator.ConditionalRequirement(
				MafFileValidator.ConditionalOperator.OR, req, req2);
		assertTrue(compositeReq.isSatisfied(row, fields));
		// now make it so req2 will not be satisfied, but composite req still
		// will, because it's an "OR"
		row[3] = "goodbye";
		assertFalse(req2.isSatisfied(row, fields));
		assertTrue(compositeReq.isSatisfied(row, fields));
	}

	@Test
	public void testLevel3Maf() throws Processor.ProcessorException {
		setUpForValidBarcodes();
		File goodFile = new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/good/test.maf");
		archive.setArchiveFile(new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/good/test.tar.gz"));
		archive.setArchiveType(Archive.TYPE_LEVEL_3);
		boolean valid = mafFileValidator.execute(goodFile, qcContext);
		assertTrue(valid);
		assertEquals(0, qcContext.getErrorCount());
	}

	@Test
	public void testLOHAndUnknownMutationStatus() throws IOException,
			Processor.ProcessorException {
		setUpForValidBarcodes();
		File badCondition = new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/other/loh.maf");
		archive.setArchiveFile(new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/other/loh.tar.gz"));
		boolean isValid = mafFileValidator.validate(badCondition, qcContext);
		assertTrue(qcContext.getErrors().toString(), isValid);
	}

	@Test
	public void testCaseInsensitiveHeaders()
			throws Processor.ProcessorException, IOException {
		setUpForValidBarcodes();
		archive.setArchiveFile(new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/other/weirdCase.tar.gz"));
		boolean isValid = mafFileValidator.validate(new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/other/weirdCase.maf"), qcContext);
		assertTrue(qcContext.getErrors().toString(), isValid);
	}

	@Test
	public void testMultiValueDbSnpStatus() throws Processor.ProcessorException {
		setUpForValidBarcodes();
		archive.setArchiveFile(new File(
				SAMPLES_DIR
						+ "qclive/mafFileValidator/other/multiValueDbSnpValStatus.tar.gz"));
		boolean valid = mafFileValidator
				.execute(
						new File(
								SAMPLES_DIR
										+ "qclive/mafFileValidator/other/multiValueDbSnpValStatus.maf"),
						qcContext);
		// should fail for this maf validator (only allowed in maf2)
		assertFalse(valid);
	}

	@Test
	public void testRowNumWithError() throws Processor.ProcessorException {
		setUpForValidBarcodes();
		archive.setArchiveFile(new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/bad/rownumTestError.tar.gz"));
		boolean valid = mafFileValidator
				.execute(new File(SAMPLES_DIR
						+ "qclive/mafFileValidator/bad/rownumTestError.maf"),
						qcContext);
		assertFalse(valid);
		assertEquals(
				"An error occurred while validating MAF file 'rownumTestError.maf', line 3: 'Hugo_Symbol' value '' is invalid - may not be blank",
				qcContext.getErrors().get(0));
		assertEquals(
				"An error occurred while validating MAF file 'rownumTestError.maf', line 6: 'Entrez_Gene_Id' value 'hi' is invalid - must be an integer number",
				qcContext.getErrors().get(1));
		assertEquals(2, qcContext.getErrorCount());
	}

	@Test
	public void testRowNumWithNoError() throws Processor.ProcessorException {
		setUpForValidBarcodes();
		archive.setArchiveFile(new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/good/rownumTestNoError.tar.gz"));
		boolean valid = mafFileValidator.execute(new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/good/rownumTestNoError.maf"),
				qcContext);
		assertTrue(valid);
		assertEquals(0, qcContext.getErrorCount());
	}

	@Test
	public void testBadFileNameMatchWithArchiveFolder()
			throws Processor.ProcessorException {
		setUpForValidBarcodes();
		qcContext
				.getArchive()
				.setArchiveFile(
						new File(
								SAMPLES_DIR
										+ "qclive/mafFileValidator/archive_with_wrong_file_name.file.1.0.0.tar.gz"));

		boolean valid = mafFileValidator.execute(new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/bad/test_archive_name.1.1.0.maf"),
				qcContext);
		assertTrue(qcContext.getErrors().toString(), valid);
	}

	@Test
	public void testGoodFileNameMatchWithArchiveFolder()
			throws Processor.ProcessorException {
		setUpForValidBarcodes();
		qcContext
				.getArchive()
				.setArchiveFile(
						new File(
								SAMPLES_DIR
										+ "qclive/mafFileValidator/test_archive_name.1.1.0.tar.gz"));

		boolean valid = mafFileValidator.execute(new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/good/test_archive_name.1.1.0.maf"),
				qcContext);
		assertTrue(valid);
		assertEquals(0, qcContext.getWarningCount());
	}
	
	@Test
	public void testMismatchedBarcodesGood() throws IOException, ProcessorException{
		setUpForValidBarcodes();		    		                						
		String barcodeTumor = "TCGA-00-0000-00A-00B-0000-00";
		String barcodeNormal = "TCGA-00-0000-01A-00B-0000-00";				
		assertTrue(mafFileValidator.validateTumorAndNormalIdsForPatient(barcodeTumor,barcodeNormal,qcContext,"fileName",1));		
		barcodeTumor = "TCGA-00-0000-00A-00B-0000-01";
		barcodeNormal = "TCGA-00-0000-19A-00C-0200-00";				
		assertTrue(mafFileValidator.validateTumorAndNormalIdsForPatient(barcodeTumor,barcodeNormal,qcContext,"fileName",1));
		
	}
	@Test
	public void testInvalidBarcodeSampleCodes() throws IOException, ProcessorException{
		setUpForValidBarcodes();		    		                						
		String barcodeTumor = "TCGA-00-0000-99A-00B-0000-00";
		String barcodeNormal = "TCGA-00-0000-03A-00B-0000-00";			
		assertFalse(mafFileValidator.validateTumorAndNormalIdsForPatient(barcodeTumor,barcodeNormal,qcContext,"fileName",1));
		assertEquals(qcContext.getErrorCount(),2);
	}
	
	@Test
	public void testValidBarcodeSampleCodes() throws IOException, ProcessorException{
		setUpForValidBarcodes();		    		                					
		String barcodeTumor = "TCGA-00-0000-03A-00B-0000-00";
		String barcodeNormal = "TCGA-00-0000-99A-00B-0000-00";				
		assertTrue(mafFileValidator.validateTumorAndNormalIdsForPatient(barcodeTumor,barcodeNormal,qcContext,"fileName",1));		
	}
	
	@Test
	public void testUnknownBarcodeSampleCodes() throws IOException, ProcessorException{
		setUpForValidBarcodes();		    		                					
		String barcodeTumor = "TCGA-00-0000-15A-00B-0000-00";
		String barcodeNormal = "TCGA-00-0000-99A-00B-0000-00";				
		assertFalse(mafFileValidator.validateTumorAndNormalIdsForPatient(barcodeTumor,barcodeNormal,qcContext,"fileName",1));		
		assertEquals("An error occurred while validating MAF file 'fileName', line 1: Unknown sample type code encountered, check your Ids and try again.",
				qcContext.getErrors().get(0));
	}
	
	@Test
	public void testMismatchedBarcodesTssMismatch() throws IOException, ProcessorException{
		setUpForValidBarcodes();
		String barcodeTumor = "TCGA-01-0000-00A-00B-0000-00";
		String barcodeNormal = "TCGA-00-0000-01A-00B-0000-00";				
		assertFalse(mafFileValidator.validateTumorAndNormalIdsForPatient(barcodeTumor,barcodeNormal,qcContext,"fileName",1));
		assertEquals(1,qcContext.getErrorCount());
		barcodeTumor = "TCGA-00-0000-00A-00B-0000-00";
		barcodeNormal = "TCGA-01-0000-01A-00B-0000-00";				
		assertFalse(mafFileValidator.validateTumorAndNormalIdsForPatient(barcodeTumor,barcodeNormal,qcContext,"fileName",1));
		assertEquals(2,qcContext.getErrorCount());
	}
	
	@Test
	public void testInvalidUUIDFormats() throws IOException, ProcessorException{
		setUpForValidUUIDs();
		qcContext.setCenterConvertedToUUID(true);
		File mafWithInvalidUUIDFormats = new File(SAMPLES_DIR + "qclive/mafFileValidator/bad/mafWithInvalidUUIDFormats.maf");
		archive.setArchiveFile(new File(SAMPLES_DIR + "qclive/mafFileValidator/bad/mafWithInvalidUUIDFormats.tar.gz"));
		boolean isValid = mafFileValidator.execute(mafWithInvalidUUIDFormats, qcContext);
		assertFalse(isValid);
		assertEquals(2, qcContext.getErrorCount());
		assertEquals("An error occurred while validating MAF file 'mafWithInvalidUUIDFormats.maf', line 2: 'Tumor_Sample_UUID' " +
				"value 'this-is-an-invalid-tumor-sample-uuid' is invalid - must be a valid aliquot UUID", qcContext.getErrors().get(0));
		assertEquals("An error occurred while validating MAF file 'mafWithInvalidUUIDFormats.maf', line 4: 'Matched_Norm_Sample_UUID' " +
				"value 'this-is-an-invalid-normal-sample-uuid' is invalid - must be a valid aliquot UUID", qcContext.getErrors().get(1));
	}
	
	@Test
	public void testNoMatchingDiseaseForUUID() throws IOException, ProcessorException{
		setUpForValidUUIDs();
        qcContext.getArchive().setTumorType("TEST");
		context.checking(new Expectations() {
			{
                atLeast(1).of(mockBarcodeValidator).validateUuid("965e37f6-10fd-4f4d-82e7-1f3b3e11d4f1", qcContext, "mafWithNoMatchingDiseaseForUUID.maf", true);
                will(returnValue(true));
                atLeast(1).of(mockBarcodeValidator).validateUuid("b030e916-821a-41f9-9697-38e32f7fb487", qcContext, "mafWithNoMatchingDiseaseForUUID.maf", true);
                will(returnValue(true));

				atLeast(1).of(mockBarcodeValidator).isMatchingDiseaseForUUID(
                        "965e37f6-10fd-4f4d-82e7-1f3b3e11d4f1", "TEST");
						will(returnValue(false));
						
				atLeast(1).of(mockBarcodeValidator).isMatchingDiseaseForUUID(
                        "b030e916-821a-41f9-9697-38e32f7fb487", "TEST");
						will(returnValue(true));
			}
		});
		qcContext.setCenterConvertedToUUID(true);
		File mafWithNoMatchingDiseaseForUUID = new File(SAMPLES_DIR + "qclive/mafFileValidator/bad/mafWithNoMatchingDiseaseForUUID.maf");
		archive.setArchiveFile(new File(SAMPLES_DIR + "qclive/mafFileValidator/bad/mafWithNoMatchingDiseaseForUUID.tar.gz"));
		boolean isValid = mafFileValidator.execute(mafWithNoMatchingDiseaseForUUID, qcContext);
		assertFalse(isValid);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("An error occurred while validating MAF file 'mafWithNoMatchingDiseaseForUUID.maf', line 3: UUID 965e37f6-10fd-4f4d-82e7-1f3b3e11d4f1 is not part of disease set for TEST", qcContext.getErrors().get(0));
	}
	
	@Test
	public void testTumorAndNormalIdMistmatch() {
		qcContext = new QcContext();
		boolean isValid = mafFileValidator.validateTumorAndNormalIdsForPatient(
				"TCGA-01-0000-00A-00B-0000-00", "b030e916-821a-41f9-9697-38e32f7fb487", qcContext, "testMaf.maf", 1);
		assertFalse(isValid);
		assertEquals(1, qcContext.getErrorCount());
		assertEquals("An error occurred while validating MAF file 'testMaf.maf', line 1: Both tumor Id 'TCGA-01-0000-00A-00B-0000-00' " +
				"and normal Id 'b030e916-821a-41f9-9697-38e32f7fb487' must match the same Id type (barcode or UUID) pattern", 
				qcContext.getErrors().get(0));
	}
	
	@Test
	public void testTumorAndNormalUUIDParticipantCodeMismatch() {
		context.checking(new Expectations() {
			{	
				allowing(mockShippedBiospecimenQueries).retrieveUUIDMetadata(
						with("965e37f6-10fd-4f4d-82e7-1f3b3e11d4f1"));
						will(returnValue(
								new MetaDataBean() {{
									setTssCode("02");
									setParticipantCode("0001");
									setSampleCode("00");
								}}
								));
						
				allowing(mockShippedBiospecimenQueries).retrieveUUIDMetadata(
						with("b030e916-821a-41f9-9697-38e32f7fb487"));
						will(returnValue(
								new MetaDataBean() {{
									setTssCode("02");
									setParticipantCode("0002");
									setSampleCode("01");
								}}
								));
			}
		});
		qcContext = new QcContext();
		boolean isValid = mafFileValidator.validateTumorAndNormalIdsForPatient(
				"965e37f6-10fd-4f4d-82e7-1f3b3e11d4f1", "b030e916-821a-41f9-9697-38e32f7fb487", qcContext, "testMaf.maf", 1);
		assertFalse(isValid);
		assertEquals(1, qcContext.getErrorCount());
		assertEquals("An error occurred while validating MAF file 'testMaf.maf', line 1: The Participant Code for both the tumor Id " +
				"'965e37f6-10fd-4f4d-82e7-1f3b3e11d4f1' and the normal Id 'b030e916-821a-41f9-9697-38e32f7fb487' must match.", 
				qcContext.getErrors().get(0));
	}
	
	@Test
	public void testTumorAndNormalUUIDTSSMismatch() {
		context.checking(new Expectations() {
			{	
				allowing(mockShippedBiospecimenQueries).retrieveUUIDMetadata(
						with("965e37f6-10fd-4f4d-82e7-1f3b3e11d4f1"));
						will(returnValue(
								new MetaDataBean() {{
									setTssCode("02");
									setParticipantCode("0001");
									setSampleCode("00");
								}}
								));
						
				allowing(mockShippedBiospecimenQueries).retrieveUUIDMetadata(
						with("b030e916-821a-41f9-9697-38e32f7fb487"));
						will(returnValue(
								new MetaDataBean() {{
									setTssCode("03");
									setParticipantCode("0001");
									setSampleCode("01");
								}}
								));
			}
		});
		qcContext = new QcContext();
		boolean isValid = mafFileValidator.validateTumorAndNormalIdsForPatient(
				"965e37f6-10fd-4f4d-82e7-1f3b3e11d4f1", "b030e916-821a-41f9-9697-38e32f7fb487", qcContext, "testMaf.maf", 1);
		assertFalse(isValid);
		assertEquals(1, qcContext.getErrorCount());
		assertEquals("An error occurred while validating MAF file 'testMaf.maf', line 1: The TSS code for both the tumor Id " +
				"'965e37f6-10fd-4f4d-82e7-1f3b3e11d4f1' and the normal Id 'b030e916-821a-41f9-9697-38e32f7fb487' must match.", 
				qcContext.getErrors().get(0));
	}
	
	@Test
	public void testMismatchedBarcodesPatientIdMismatch() throws IOException, ProcessorException{
		setUpForValidBarcodes();
		String barcodeTumor = "TCGA-00-1000-00A-00B-0000-00";
		String barcodeNormal = "TCGA-00-0000-01A-00B-0000-00";				
		assertFalse(mafFileValidator.validateTumorAndNormalIdsForPatient(barcodeTumor,barcodeNormal,qcContext,"fileName",1));
		assertEquals(1,qcContext.getErrorCount());
		
		barcodeTumor = "TCGA-00-0000-00A-00B-0000-00";
		barcodeNormal = "TCGA-01-0001-01A-00B-0000-00";				
		assertFalse(mafFileValidator.validateTumorAndNormalIdsForPatient(barcodeTumor,barcodeNormal,qcContext,"fileName",1));
		assertEquals(3,qcContext.getErrorCount());
	}

	
	@Test
	public void testValidateMismatchedBarcodes() throws IOException, ProcessorException{
		setUpForValidBarcodes();
		File invalidTestCase = new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/bad/mismatchingBarcodes.maf");
		
		archive.setArchiveFile(new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/bad/bad_conditions.tar.gz"));
		assertFalse(mafFileValidator.validate(invalidTestCase, qcContext));
		assertTrue(qcContext.getErrors().size() == 2);
		assertEquals("An error occurred while validating MAF file 'mismatchingBarcodes.maf', line 2: " +
				"The TSS code for both the tumor Id 'TCGA-00-0000-00A-00B-0000-00' and the normal Id 'TCGA-01-0000-01C-00D-0000-00' must match.", 
				qcContext.getErrors().get(0));
		assertEquals("An error occurred while validating MAF file 'mismatchingBarcodes.maf', line 3: " +
				"The Participant Code for both the tumor Id 'TCGA-00-0001-00A-00B-0000-00' and the normal Id 'TCGA-00-0000-01C-00D-0000-00' must match.", 
				qcContext.getErrors().get(1));
	}

	@Test
	public void testTumorandRefAllele() throws IOException,
			Processor.ProcessorException {
		setUpForValidBarcodes();
		File badCondition = new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/bad/TumorandRefAlleleCheck.maf");
		archive.setArchiveFile(new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/bad/bad_conditions.tar.gz"));
		assertFalse(mafFileValidator.validate(badCondition, qcContext));
		assertEquals(2, qcContext.getErrorCount());

		assertTrue(qcContext
				.getErrors()
				.get(0)
				.contains(
						"Tumor_Seq_Allele1 (TCGA) must not be equal to Reference_Allele OR Tumor_Seq_Allele2 (TCGA) must not be equal to Reference_Allele"));

	}

	@Test
	public void testNonExistentBarcodes() throws Processor.ProcessorException {
		File goodFile = new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/good/test.maf");
		archive.setArchiveFile(new File(SAMPLES_DIR
				+ "qclive/mafFileValidator/good/test.tar.gz"));

				
		context.checking(new Expectations() {
			{								
				exactly(3).of(mockBarcodeValidator).validate(
						"TCGA-00-0000-03A-00B-0000-00", qcContext, "test.maf",
						true);
				will(addErrorAndReturnFalse());

				exactly(3).of(mockBarcodeValidator).validate(
						"TCGA-00-0000-19C-00D-0000-00", qcContext, "test.maf",
						true);
				will(returnValue(true));								
			}
		});

		
		mafFileValidator.execute(goodFile, qcContext);
		assertEquals(3, qcContext.getErrorCount());
	}

    @Test
    public void testUuidsAreNotAliquots() throws ProcessorException {
        setUpForValidBarcodes();
        setUpForValidUUIDs(false, true);
        File goodFile = new File(SAMPLES_DIR
                + "qclive/mafFileValidator/good/mafWithUUIDHeader.maf");
        archive.setArchiveFile(new File(SAMPLES_DIR
                + "qclive/mafFileValidator/good/mafWithUUIDHeader.tar.gz"));
        qcContext.setCenterConvertedToUUID(true);
        boolean valid = mafFileValidator.execute(goodFile, qcContext);
        assertFalse(valid);
        assertEquals("An error occurred while validating MAF file 'mafWithUUIDHeader.maf', line 2: tumor sample UUID does not represent an aliquot", qcContext.getErrors().get(0));
        assertEquals("An error occurred while validating MAF file 'mafWithUUIDHeader.maf', line 2: normal sample UUID does not represent an aliquot", qcContext.getErrors().get(1));
    }

    @Test
    public void testUuidBarcodeMismatch() throws ProcessorException {
        setUpForValidBarcodes();
        setUpForValidUUIDs(true, false);
        File goodFile = new File(SAMPLES_DIR
                + "qclive/mafFileValidator/good/mafWithUUIDHeader.maf");
        archive.setArchiveFile(new File(SAMPLES_DIR
                + "qclive/mafFileValidator/good/mafWithUUIDHeader.tar.gz"));
        qcContext.setCenterConvertedToUUID(true);
        boolean valid = mafFileValidator.execute(goodFile, qcContext);
        assertFalse(valid);
        assertEquals(2, qcContext.getErrorCount());
        assertEquals("An error occurred while validating MAF file 'mafWithUUIDHeader.maf', line 2: tumor barcode (TCGA-00-0000-03A-00B-0000-00) doesn't map to given tumor UUID (2e276db0-a903-46c0-8892-620fc0e94de6)", qcContext.getErrors().get(0));
        assertEquals("An error occurred while validating MAF file 'mafWithUUIDHeader.maf', line 2: normal barcode (TCGA-00-0000-19C-00D-0000-00) doesn't map to given normal UUID (2e276db0-a903-46c0-8892-620fc0e94de8)", qcContext.getErrors().get(1));
    }


	public static Action addErrorAndReturnFalse() {
		return new Action() {
			@Override
			public Object invoke(final Invocation invocation) throws Throwable {
				final QcContext qcContext = (QcContext) invocation
						.getParameter(1);
				qcContext.addError("Error number "
						+ (qcContext.getErrorCount() + 1));
				return false;
			}

			@Override
			public void describeTo(final Description description) {
				description
						.appendText("adds an error message to the qcContext and returns null");
			}
		};
	}
}
