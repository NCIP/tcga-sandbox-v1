/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContent;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContentImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.DataMatrix;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.DataMatrixParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.DataMatrixParser.DataMatrixParseError;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedFileParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for DataMatrixValidator class.
 * 
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class DataMatrixValidatorFastTest {

	private TabDelimitedContent sdrf = new TabDelimitedContentImpl();
	private DataMatrixValidator validator;
	private static final String SAMPLES_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;
	private String fileDirectory = SAMPLES_DIR + "qclive/dataMatrix/";
	private String sdrfFile = "Test.sdrf";
	private String goodMatrix = "Good-Matrix.data.txt";
	private String badMatrix1 = "Bad-Matrix-1.data.txt";
	private String badMatrix2 = "Bad-Matrix-2.data.txt";
	private String badMatrix4 = "Bad-Matrix-4.data.txt";

	/* protein array test items */
	private String goodProteinArraySample = "good_protein_expression_Level_3.txt";
	private String badProteinArraySample = "bad_protein_expression_Level_3.txt";
	private String proteinArraySdrfFile = "protein-array.sdrf";
	TabDelimitedContent proteinSdrf = new TabDelimitedContentImpl();

	@Before
	public void setUp() throws IOException,ParseException {
		// load test sdrf
		TabDelimitedFileParser tabDelimitedFileParser = new TabDelimitedFileParser();
		tabDelimitedFileParser.setTabDelimitedContent(sdrf);
		tabDelimitedFileParser.initialize(fileDirectory + sdrfFile);
		validator = new DataMatrixValidator();
	}

	private boolean runTest(String filename)
			throws Processor.ProcessorException,
			DataMatrixParser.DataMatrixParseError, IOException {
		DataMatrixParser parser = new DataMatrixParser();
		Archive archive = new Archive();
		archive.setSdrf(sdrf);
		QcContext qcContext = new QcContext();
		qcContext.setArchive(archive);
		qcContext.setSdrf(sdrf);
		return validator.execute(parser.parse(filename, fileDirectory),
				qcContext);
	}

	@Test
	public void testValidate() throws DataMatrixParser.DataMatrixParseError,
			IOException, Processor.ProcessorException {
		assertTrue(runTest(goodMatrix));
	}

	@Test
	public void testFailedValidate() throws Processor.ProcessorException,
			IOException, DataMatrixParser.DataMatrixParseError {
		boolean caught = false;
		try {
			runTest(badMatrix1);
		} catch (DataMatrixParser.DataMatrixParseError ex) {
			caught = true;
		}
		assertTrue(caught);
	}

	@Test
	public void testFailed() throws DataMatrixParser.DataMatrixParseError,
			Processor.ProcessorException, IOException {
		assertFalse(runTest(badMatrix2));
	}

	@Test
	public void testRowConcordance()
			throws DataMatrixParser.DataMatrixParseError,
			Processor.ProcessorException, IOException {
		// make sure validator catches problems with top and bottom headers not
		// matching:
		assertFalse(runTest(badMatrix4));
	}

	@Test
	public void testNonMatrix() throws Processor.ProcessorException,
			DataMatrixParser.DataMatrixParseError, IOException {
		String dir = SAMPLES_DIR + "qclive/dataMatrix/";
		String file = "notMatrix.seg.txt";
		DataMatrixParser parser = new DataMatrixParser();
		DataMatrix matrix = parser.parse(file, dir);
		Archive archive = new Archive();
		archive.setSdrf(sdrf);
		QcContext qcContext = new QcContext();
		qcContext.setArchive(archive);
		qcContext.setSdrf(sdrf);
		boolean valid = validator.execute(matrix, qcContext);
		assertFalse(valid);
	}

	@Test
	public void testMatrixForInvalidReporterType() {

		DataMatrix matrix = new DataMatrix();
		matrix.setNumReporters(1);
		matrix.setReporterType("InValid");
		QcContext qcContext = new QcContext();

		validator.validateReporterTypeForRFE(qcContext,
				matrix.getReporterType(), "Matrix.data.txt");
		assertTrue("Error count should be non-zero.",
				qcContext.getErrorCount() > 0);

	}

	@Test
	public void testValidReporterType() {

		DataMatrix matrix = new DataMatrix();
		matrix.setNumReporters(1);
		matrix.setReporterType(DataMatrixValidator.COMPOSITE_ELEMENT_REF);
		QcContext qcContext = new QcContext();

		validator.validateReporterTypeForRFE(qcContext,
				matrix.getReporterType(), "Matrix.data.txt");
		assertTrue("Error count should be non-zero.",
				qcContext.getErrorCount() == 0);

	}

	private QcContext setUpProteinArrayTest() throws IOException,ParseException {

		TabDelimitedFileParser tabDelimitedFileParser = new TabDelimitedFileParser();
		tabDelimitedFileParser.setTabDelimitedContent(proteinSdrf);
		tabDelimitedFileParser.initialize(fileDirectory + proteinArraySdrfFile);
		Archive archive = new Archive();
		archive.setSdrf(proteinSdrf);
		QcContext qcContext = new QcContext();
		qcContext.setArchive(archive);
		qcContext.setSdrf(proteinSdrf);
		return qcContext;
	}

	@Test
	public void testValidProteinArray() throws ProcessorException,
			DataMatrixParseError, IOException,ParseException {
		DataMatrixParser parser = new DataMatrixParser();
		assertTrue(validator.execute(parser.parse(
				"good_protein_expression_Level_3.txt", fileDirectory),
				setUpProteinArrayTest()));
	}

	@Test
	public void testInValidProteinArray() throws ProcessorException,
			DataMatrixParseError, IOException ,ParseException{
		DataMatrixParser parser = new DataMatrixParser();
		QcContext ctx = setUpProteinArrayTest();
		assertFalse(validator.execute(parser.parse(
				"bad_protein_expression_Level_3.txt", fileDirectory), ctx));
		assertTrue(ctx.getErrors() != null);
		Assert.assertEquals(
				"An error occurred while processing data matrix 'bad_protein_expression_Level_3.txt': Level_"
						+ "3 protein expression data files must contain Sample REF in major header",
				ctx.getErrors().get(0));

	}

}
