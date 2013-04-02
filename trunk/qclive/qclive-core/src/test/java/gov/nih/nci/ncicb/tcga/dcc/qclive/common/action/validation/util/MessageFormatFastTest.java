/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util;

import org.junit.Test;

import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;

/**
 * <code>MessageFormatTest</code> tests the {@link MessageFormatFastTest}
 *
 * @author Matt Nicholls
 *         Last updated by: nichollsmc
 * @version 
 */
public class MessageFormatFastTest {
	
	// Get the values from the MessageFormat properties file using a resource bundle
	private final ResourceBundle messageProperties = ResourceBundle.getBundle(MessageFormat.DEFAULT_BASE_NAME);
	
	// Get the test values from the MessageFormatTest properties file using a resource bundle
	private final ResourceBundle testMessageProperties = ResourceBundle.getBundle(MessageFormatFastTest.class.getName());
	
	/**
	 * Assert that the number of property types defined in the {@link MessagePropertyType} enum match the number of properties
	 * defined in the <code>gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat.properties</code> file.
	 */
	@Test
	public void testMessagePropertyTypeOrdinals() {
		
		// Assert that the number of values in the properties file and the MessagePropertyType enum are equal
		assertEquals(messageProperties.keySet().size(), MessagePropertyType.values().length);
	}
	
	/**
	 * Each test below asserts that the {@link MessageFormat} class correctly formats the strings defined in the 
	 * <code>gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat.properties</code> file. 
	 * They also test that the format strings in the properties file define the correct patterns.
	 */
	
	@Test
	public void testArchiveFormatStrings() {
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.ARCHIVE_NAME_DUPLICATE_SERIAL_INDEX_ERROR.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.ARCHIVE_NAME_DUPLICATE_SERIAL_INDEX_ERROR, 1, "testCenter", "testDisease"));
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.ARCHIVE_NAME_INVALID_SERIAL_INDEX_ERROR.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.ARCHIVE_NAME_INVALID_SERIAL_INDEX_ERROR, 2, "testCenter", "testDisease"));
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.ARCHIVE_PROCESSING_ERROR.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.ARCHIVE_PROCESSING_ERROR, "testArchive", "Test archive error message"));
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR, "testArchive", "test validation error"));
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.LEVEL2_DATA_GENERATION_ERROR.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.LEVEL2_DATA_GENERATION_ERROR, "item1", "item2", "item3", "item4", "error detail"));
		
	}
	
	@Test
	public void testBarcodeFormatStrings() {
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.BARCODE_VALIDATION_ERROR.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.BARCODE_VALIDATION_ERROR, "test-barcode", "barcode validation error message"));
		
	}

    @Test
    public void testUuidFormatStrings() {

        assertEquals(
                testMessageProperties.getString(MessagePropertyType.UUID_VALIDATION_ERROR.getPropertyValue()),
                MessageFormat.format(MessagePropertyType.UUID_VALIDATION_ERROR, "test-uuid", "uuid validation error message"));

    }
	
	@Test
	public void testDataMatrixFormatStrings() {
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.DATA_MATRIX_PROCESSING_ERROR.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.DATA_MATRIX_PROCESSING_ERROR, "dataMatrixFile", "data matrix file error message"));
		
	}
	
	@Test
	public void testExperimentFormatStrings() {
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.EXPERIMENT_PROCESSING_ERROR.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.EXPERIMENT_PROCESSING_ERROR, "test_experiment", "experiment error message"));
		
	}
	
	@Test
	public void testFileProcessingFormatStrings() {
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.FILE_COMPRESS_ERROR.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.FILE_COMPRESS_ERROR, "testFile", "file compression error message"));
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.FILE_PROCESSING_ERROR.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.FILE_PROCESSING_ERROR, "testFile", "file error message"));
		
	}
	
	@Test
	public void testGeneralFormatStrings() {
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.GENERAL_VALIDATION_MESSAGE.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.GENERAL_VALIDATION_MESSAGE, "This is a test message"));
		
	}
	
	@Test
	public void testMAFFormatStrings() {
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.MAF_FILE_PROCESSING_ERROR.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.MAF_FILE_PROCESSING_ERROR, "mafFile", "MAF file error message"));
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.MAF_FILE_VALIDATION_ERROR.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.MAF_FILE_VALIDATION_ERROR, "mafFile", 1, "MAF file validation error message"));
	}
	
	@Test
	public void testRNAFormatStrings() {
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.RNA_SEQ_DATA_FILE_VALIDATION_ERROR.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.RNA_SEQ_DATA_FILE_VALIDATION_ERROR, "rnaSeqFile", "RNA sequence file error message"));
		
	}
	
	@Test
	public void testSDRFValidationFormatStrings() {
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.MISSING_REQUIRED_SDRF_COLUMN_ERROR.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.MISSING_REQUIRED_SDRF_COLUMN_ERROR, "sdrf column"));
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.NO_TRANSFORMATION_NAME_FOR_FILE_WARNING.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.NO_TRANSFORMATION_NAME_FOR_FILE_WARNING, "3", "12", "derivedDataFile.sdrf.txt"));
		
	}
	
	@Test
	public void testTraceFileFormatStrings() {
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.TRACE_FILE_PROCESSING_ERROR.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.TRACE_FILE_PROCESSING_ERROR, "traceFile", "Trace file error message"));
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.TRACE_FILE_VALIDATION_ERROR.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.TRACE_FILE_VALIDATION_ERROR, "traceFile", "Trace file validation error message"));
		
	}
	
	@Test
	public void testValidationFormatStrings() {
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.COLUMN_PRECEDENCE_ERROR.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.COLUMN_PRECEDENCE_ERROR, "10", "1", "TestColumn1", "TestColmun2"));
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.COLUMN_VALUE_FORMAT_ERROR.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.COLUMN_VALUE_FORMAT_ERROR, "2", "test column", "testFormat", "wrongFormat"));
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.EMPTY_COLUMN_ERROR.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.EMPTY_COLUMN_ERROR, "test column"));
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.FILE_EXTENSION_ERROR.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.FILE_EXTENSION_ERROR, "2", "3", "testFile.ttx", ".txt"));
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.LINE_ERROR.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.LINE_ERROR, "5", "0", "testField1", "testValue1", "testValue2"));
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.LINE_VALUE_ERROR.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.LINE_VALUE_ERROR, "6", "testField2", "testValue3", "testValue4"));
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.LINE_VALUE_FORMAT_ERROR.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.LINE_VALUE_FORMAT_ERROR, "7", "testField3", "testFormat1", "testFormat2"));
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.MISSING_REQUIRED_COLUMN_ERROR.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.MISSING_REQUIRED_COLUMN_ERROR, "required column"));
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.MISSING_REQUIRED_VALUE_ERROR.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.MISSING_REQUIRED_VALUE_ERROR, "required value", "testField"));
		
	}
	
	@Test
	public void testValidationWarningFormatStrings() {
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.VALUE_NOT_PROVIDED_WARNING.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.VALUE_NOT_PROVIDED_WARNING, "testValue1", "testField5"));
		
	}
	
	@Test
	public void testWIGFormatStrings() {
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.WIG_FILE_PROCESSING_ERROR.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.WIG_FILE_PROCESSING_ERROR, "wigFile", "WIG file validation error message"));
		
	}
	
	@Test
	public void testXMLXSDFormatStrings() {
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.XML_FILE_PROCESSING_ERROR.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.XML_FILE_PROCESSING_ERROR, "xmlFile", "XML file error message"));
		
		assertEquals(
				testMessageProperties.getString(MessagePropertyType.XSD_FILE_PROCESSING_ERROR.getPropertyValue()), 
				MessageFormat.format(MessagePropertyType.XSD_FILE_PROCESSING_ERROR, "xsdFile", "XSD file error message"));
		
	}

    @Test
    public void testSdrfColumnBlankValueError() {

        assertEquals(
                testMessageProperties.getString(MessagePropertyType.SDRF_COLUMN_BLANK_VALUE_ERROR.getPropertyValue()),
                MessageFormat.format(MessagePropertyType.SDRF_COLUMN_BLANK_VALUE_ERROR, "column name", "1"));
    }
}

