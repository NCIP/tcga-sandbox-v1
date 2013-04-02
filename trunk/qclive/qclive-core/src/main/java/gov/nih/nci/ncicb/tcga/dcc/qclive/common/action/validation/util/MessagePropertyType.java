/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util;

/**
 * The <code>MessagePropertyType</code> provides a 1-to-1 enumeration of the property values in the
 * <code>gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat.properties</code> file,
 * and defines the property types that are allowed for use with the 
 * {@link gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat} class.
 * 
 *
 * @author Matt Nicholls
 *         Last updated by: nichollsmc
 * @version $Rev$
 */
public enum MessagePropertyType {

	/** Archive name duplicate serial index error property **/
    ARCHIVE_NAME_DUPLICATE_SERIAL_INDEX_ERROR("archive.name.duplicate.serial.index.error"), 
    
    /** Archive name invalid serial index error property **/
	ARCHIVE_NAME_INVALID_SERIAL_INDEX_ERROR("archive.name.invalid.serial.index.error"), 

    /** Archive name invalid disease error property **/
    ARCHIVE_NAME_INVALID_DISEASE_ERROR("archive.name.invalid.disease.error"),
    /** Archive processing warning property **/
	ARCHIVE_PROCESSING_WARNING("archive.processing.warning"),

    /** Archive processing error property **/
	ARCHIVE_PROCESSING_ERROR("archive.processing.error"),
	
	/** Archive SDRF validation error property **/
	ARCHIVE_SDRF_VALIDATION_ERROR("archive.sdrf.validation.error"),
	
	/** Barcode validation error property **/
	BARCODE_VALIDATION_ERROR("barcode.validation.error"),

    /** Uuid validation error property **/
    UUID_VALIDATION_ERROR("uuid.validation.error"),

    /** Uuid validation warning property **/
    UUID_VALIDATION_WARNING("uuid.validation.warning"),

    /** Uuid / Barcode validation error property **/
    UUID_BARCODE_VALIDATION_ERROR("uuid.barcode.validation.error"),

    /** Uuid / Barcode validation error property **/
    UUID_BARCODE_VALIDATION_WARNING("uuid.barcode.validation.warning"),

	/** Column value format error property **/
	COLUMN_VALUE_FORMAT_ERROR("column.value.format.error"),
	
	/** Column precedence error property **/
	COLUMN_PRECEDENCE_ERROR("column.precedence.error"),
	
	/** Data matrix processing error property **/
	DATA_MATRIX_PROCESSING_ERROR("data.matrix.processing.error"),
	
	/** Empty column error proprty **/
	EMPTY_COLUMN_ERROR("empty.column.error"),
	
	/** Experiment processing error property **/
	EXPERIMENT_PROCESSING_ERROR("experiment.processing.error"),
	
	/** File compress error property type **/
    FILE_COMPRESS_ERROR("file.compress.error"),
	
	/** File extension error property **/
	FILE_EXTENSION_ERROR("file.extension.error"),
	
	/** General validation message property **/
	GENERAL_VALIDATION_MESSAGE("general.validation.message"),
	
	/** File processing error property **/
	FILE_PROCESSING_ERROR("file.processing.error"),
	
	/** Level 2 data generation error property **/
    LEVEL2_DATA_GENERATION_ERROR("level2.data.generation.error"),
	
    //LEVEL2_DATA_GENERATION_ERR = "Level2 Data Generation Error";
    
	/** Line error property **/
	LINE_ERROR("line.error"),
	
	/** Line value error property **/
	LINE_VALUE_ERROR("line.value.error"),
	
	/** Line value format error property **/
	LINE_VALUE_FORMAT_ERROR("line.value.format.error"),
	
	/** MAF file processing error property **/
	MAF_FILE_PROCESSING_ERROR("maf.file.processing.error"),
	
	/** MAF file validation error property **/
	MAF_FILE_VALIDATION_ERROR("maf.file.validation.error"),
	
	/** Missing required column error property **/
	MISSING_REQUIRED_COLUMN_ERROR("missing.required.column.error"),
	
	/** Missing required SDRF column error property **/
	MISSING_REQUIRED_SDRF_COLUMN_ERROR("missing.required.sdrf.column.error"),

    /** One of the value in the given column is blank **/
    SDRF_COLUMN_BLANK_VALUE_ERROR("sdrf.column.blank.value.error"),

	/** Missing required value error property **/
	MISSING_REQUIRED_VALUE_ERROR("missing.required.value.error"),
	
	/** No data transformation for data file property **/
	NO_TRANSFORMATION_NAME_FOR_FILE_WARNING("no.transformation.name.for.file.warning"),

    /** RNA seq data file validation error property **/
    RNA_SEQ_DATA_FILE_VALIDATION_ERROR("rna.seq.data.file.validation.error"),

    /** miRNA seq data file validation error property **/
    MIRNA_SEQ_DATA_FILE_VALIDATION_ERROR("mirna.seq.data.file.validation.error"),

	/** SDRF line value format error property **/
	SDRF_LINE_VALUE_FORMAT_ERROR("sdrf.line.value.format.error"),
	
	/** SDRF Deprecated Column Validation Error Property **/
	SDRF_DEPRECATED_COLUMN_VALIDATION_ERROR("sdrf.deprecated.column.validation.error"),
	
	/** Trace file processing error property **/
	TRACE_FILE_PROCESSING_ERROR("trace.file.processing.error"),
	
	/** Trace file validation error property **/
	TRACE_FILE_VALIDATION_ERROR("trace.file.validation.error"),
	
	/** Value not provided warning property **/
	VALUE_NOT_PROVIDED_WARNING("value.not.provided.warning"),
    
	/** WIG file processing error property **/
	WIG_FILE_PROCESSING_ERROR("wig.file.processing.error"),
	
    /** XML file processing error property **/
    XML_FILE_PROCESSING_ERROR("xml.file.processing.error"),

    XML_FILE_VALIDATION_ERROR("xml.file.validation.error"),
    
    /** XSD file processing error property **/
    XSD_FILE_PROCESSING_ERROR("xsd.file.processing.error");

	private String propertyValue;
	
	/**
	 * Private constructor used by the enum types defined in this class to instantiate a <code>MessagePropertyType</code>
	 * with a property value defined in the <code>gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat.properties</code> 
	 * file.
	 * 
	 * @param propertyValue - a string that representing the property value
	 */
	private MessagePropertyType(String propertyValue) {
		this.propertyValue = propertyValue;
	}
	
	/**
	 * Returns a string representing the property value that was assigned to an instance of the <code>MessagePropertyType</code> enum type.
	 * 
	 * @return a string representing the property value
	 */
	public String getPropertyValue() {
		return propertyValue;
	}
}
