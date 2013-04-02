/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.ShippedBiospecimenQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedContentNavigator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Validator for protein array-based SDRFs.
 *
 * @author Stanley Girshik
 *         Last updated by: $Author: 
 * @version $Rev: 3441 $
 */
public class ProteinArraySdrfValidator extends AbstractSdrfValidator {

    private static final String LEVEL_1 = "Level 1";
    private static final String LEVEL_2 = "Level 2";
    private static final String LEVEL_3 = "Level 3";
    private static final List<String> ALLOWED_LEVEL_1_FILE_COLUMN_HEADERS = Arrays.asList("Image File", "Array Data File", "Derived Array Data Matrix File");
    private static final List<String> ALLOWED_LEVEL_2_FILE_COLUMN_HEADERS = Arrays.asList("Derived Array Data File", "Derived Array Data Matrix File");
    private static final List<String> ALLOWED_LEVEL_3_FILE_COLUMN_HEADERS = Arrays.asList("Derived Array Data Matrix File");

    // platform used to validate file name
	public final static String PROTEIN_ARRAY_PLATFORM = "MDA_RPPA_Core";
    private static final String ARRAY_DESIGN_FILE = "Array Design File";
    private static final String SAMPLE_NAME = "Sample Name";
    private static final String ANNOTATIONS_FILE = "Annotations File";
    private static final String ARRAY_DATA_FILE = "Array Data File";
    private static final String DERIVED_ARRAY_DATA_FILE = "Derived Array Data File";
    private static final String DERIVED_ARRAY_DATA_MATRIX_FILE = "Derived Array Data Matrix File";
    private static final String COMMENT_COLUMN_PREFIX = "Comment";
    public static final String COMMENT_ANTIBODY_TYPE = "Comment [TCGA Antibody Name]";
    public static final String COMMENT_DATA_TYPE = "Comment [TCGA Data Type]";
    public static final String COMMENT_FILE_TYPE = "Comment [TCGA File Type]";
    public static final String COMMENT_BIOSPECIMEN_TYPE = "Comment [TCGA Biospecimen Type]";
    public static final String COMMENT_MD5 = "Comment [TCGA MD5]";
    public static final String HYBRIDIZATION_NAME = "Hybridization Name";
    public static final String ARRAY_NAME = "Array Name";
    public static final String SCAN_NAME = "Scan Name";
    public static final String DATA_TRANSFORMATION_NAME = "Data Transformation Name";
    public static final String NORMALIZATION_NAME = "Normalization Name";

    private static final String[] DATA_FILE_COLUMNS = {
        IMAGE_FILE,
        ARRAY_DATA_FILE,
        DERIVED_ARRAY_DATA_FILE,
        DERIVED_ARRAY_DATA_MATRIX_FILE
   };

    private static final List<String> VALID_VALUES_FOR_TCGA_INCLUDE_FOR_ANALYSIS = Arrays.asList("yes","no","->");
    private static final List<String> VALID_VALUES_FOR_TCGA_FILE_TYPE = Arrays.asList("Antibody Annotations (txt)",
            "Array Slide Image (TIFF)", "RPPA Slide Image Measurements (txt)","SuperCurve Results (txt)",
            "MDA_RPPA Slide Design (txt)", "Normalized Protein Expression (MAGE-TAB data matrix)");
    private static final List<String> VALID_VALUES_FOR_TCGA_COMMENT_TCGA_DATA_TYPE = Arrays.asList("Annotations-Platform Design","Expression-Protein","Annotations-Antibodies","->");

    // key = column name, value = is it a required column
    private static final Map<String, Boolean> CHECK_COLUMNS = new HashMap<String, Boolean>();
    private static final List<String> PROTEIN_FILE_COLUMNS = new ArrayList<String>();
    private static final List<String> PROTEIN_REQUIRED_COMMENTS_COLUMNS = new ArrayList<String>();

    static {
        CHECK_COLUMNS.put(SOURCE_NAME, false);
        CHECK_COLUMNS.put(MATERIAL_TYPE, false);
        CHECK_COLUMNS.put(TERM_SOURCE_REF, false);
        CHECK_COLUMNS.put(PROVIDER, false);
        CHECK_COLUMNS.put(PROTOCOL_REF, false);
        CHECK_COLUMNS.put(EXTRACT_NAME, false);
        CHECK_COLUMNS.put(ARRAY_NAME, false);
        CHECK_COLUMNS.put(COMMENT_DATA_TYPE, false);
        CHECK_COLUMNS.put(COMMENT_DATA_LEVEL, false);
        CHECK_COLUMNS.put(COMMENT_FILE_TYPE, false);
        CHECK_COLUMNS.put(COMMENT_ANTIBODY_TYPE, false);
        CHECK_COLUMNS.put(COMMENT_INCLUDE_FOR_ANALYSIS, false);
        CHECK_COLUMNS.put(COMMENT_ARCHIVE_NAME, false);
        CHECK_COLUMNS.put(COMMENT_MD5, false);
        CHECK_COLUMNS.put(HYBRIDIZATION_NAME, false);
        CHECK_COLUMNS.put(SCAN_NAME, false);
        CHECK_COLUMNS.put(ARRAY_DATA_FILE, false);
        CHECK_COLUMNS.put(DATA_TRANSFORMATION_NAME, false);
        CHECK_COLUMNS.put(DERIVED_ARRAY_DATA_FILE, false);
        CHECK_COLUMNS.put(DERIVED_ARRAY_DATA_MATRIX_FILE, false);
        CHECK_COLUMNS.put(NORMALIZATION_NAME, false);
        CHECK_COLUMNS.put(SAMPLE_NAME, true);
        CHECK_COLUMNS.put(COMMENT_BIOSPECIMEN_TYPE, true);
        CHECK_COLUMNS.put(ARRAY_DESIGN_FILE, true);
        CHECK_COLUMNS.put(IMAGE_FILE, true);
        CHECK_COLUMNS.put(ANNOTATIONS_FILE, true);

        PROTEIN_FILE_COLUMNS.add(ARRAY_DESIGN_FILE);
        PROTEIN_FILE_COLUMNS.add(ARRAY_DATA_FILE);
        PROTEIN_FILE_COLUMNS.add(DERIVED_ARRAY_DATA_FILE);
        PROTEIN_FILE_COLUMNS.add(DERIVED_ARRAY_DATA_MATRIX_FILE);
        PROTEIN_FILE_COLUMNS.add(IMAGE_FILE);
        PROTEIN_FILE_COLUMNS.add(ANNOTATIONS_FILE);

        PROTEIN_REQUIRED_COMMENTS_COLUMNS.add(COMMENT_DATA_LEVEL);
        PROTEIN_REQUIRED_COMMENTS_COLUMNS.add(COMMENT_DATA_TYPE);
        PROTEIN_REQUIRED_COMMENTS_COLUMNS.add(COMMENT_FILE_TYPE);
    }

	private ShippedBiospecimenQueries shippedBioQueries;

    @Override
    protected Collection<String> getAllowedSdrfHeaders() throws ProcessorException {
        return CHECK_COLUMNS.keySet();
    }

    @Override
    protected Map<String, Boolean> getColumnsToCheck() {
        return CHECK_COLUMNS;
    }

    @Override
    protected boolean validateFileHeaderAndLevel(final QcContext context,
                                                 final String header,
                                                 final int row,
                                                 final String level) {
        boolean result = true;

        if(LEVEL_1.equals(level) && !ALLOWED_LEVEL_1_FILE_COLUMN_HEADERS.contains(header)) {// Validating Level 1

            result = false;
            context.addError(MessageFormat.format(
                    MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                    context.getArchive(),
                    new StringBuilder("line ")
                            .append((row + NUM_HEADERS + ZERO_INDEX_OFFSET))
                            .append(": Level 1 files must one of ")
                            .append(ALLOWED_LEVEL_1_FILE_COLUMN_HEADERS)
                            .append(", but the type found was '").append(header).append("'"))
            );

        } else if (LEVEL_2.equals(level) && !ALLOWED_LEVEL_2_FILE_COLUMN_HEADERS.contains(header)) {// Validating Level 2

            result = false;
            context.addError(MessageFormat.format(
                    MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                    context.getArchive(),new StringBuilder("line ")
                            .append((row + NUM_HEADERS + ZERO_INDEX_OFFSET))
                            .append(": ").append(LEVEL_2).append(" files must one of ")
                            .append(ALLOWED_LEVEL_2_FILE_COLUMN_HEADERS)
                            .append(", but the type found was '").append(header).append("'"))
            );

        } else if (LEVEL_3.equals(level) && !ALLOWED_LEVEL_3_FILE_COLUMN_HEADERS.contains(header)) {// Validating Level 3

            result = false;
            context.addError(MessageFormat.format(
                    MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                    context.getArchive(),new StringBuilder("line ")
                            .append((row + NUM_HEADERS + ZERO_INDEX_OFFSET))
                            .append(": ").append(LEVEL_3).append(" files must one of ")
                            .append(ALLOWED_LEVEL_3_FILE_COLUMN_HEADERS)
                            .append(", but the type found was '").append(header).append("'"))
            );
        }

        return result;
    }

    @Override
    protected boolean runSpecificValidations(QcContext context, TabDelimitedContentNavigator sdrfNavigator) {
        boolean isValid =  validateSampleNameHeader(sdrfNavigator.getHeaders(), context);
        isValid &= validateDataFileColumnHeaders(sdrfNavigator.getHeaders(),context);
    	//run sample name column validation
    	isValid &= validateSampleNameColumn(sdrfNavigator, context);
        return isValid;
    }

    /**
     * Return <code>true</code> if the value in 'Sample Name' column ,
     * conforms to the rules below <code>false</code> otherwise
     * 
     * For each row, if the "Comment [TCGA Biospecimen Type]" is "->" then the "Sample Name"
     * should not be validated. (Because if the biospecimen type is null, that means the row represents
     * a cell line or control.) If the "Comment [TCGA Biospecimen Type]" is "Portion" (case-insensitive)
     * then the value of the "Sample Name column for that row must be a valid UUID that is in the 
     * shipped_biospecimen table with type indicating it is a shipped portion. Otherwise the Sample Name
     * value is invalid.
     * 
     * @param sdrfNavigator TabDelimitedContentNavigator sdrf navigator object
     * @param qcContext the qcLive context
     * @return <code>true</code> if the values in 'Sample Name' column follow the rules, <code>false</code> otherwise
     */
    protected boolean validateSampleNameColumn(TabDelimitedContentNavigator sdrfNavigator,QcContext qcContext ){    	    	
    	
    	boolean isValid = true;
    	
    	if (sdrfNavigator != null){
    		// the columns must be present since its presence should already been validated
    		int sampleNameIdx = sdrfNavigator.getHeaderIDByName(SAMPLE_NAME);
    		int commentBioTpeIdx = sdrfNavigator.getHeaderIDByName(COMMENT_BIOSPECIMEN_TYPE);
    		
    		List<String> sampleNameList = sdrfNavigator.getColumnValues(sampleNameIdx);
    		List<String> commentBioTypeList = sdrfNavigator.getColumnValues(commentBioTpeIdx);    		    		
    		
    		if (sampleNameList == null  || sampleNameList.size() < 1){
    			isValid = false;
    			 qcContext.addError(MessageFormat.format(
                         MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                         qcContext.getArchive(),
                         " Unable to validate 'Sample Name' column ")
                 );
    		}else if (commentBioTypeList == null  || commentBioTypeList.size() < 1){
    			isValid = false;
    			 qcContext.addError(MessageFormat.format(
                         MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                         qcContext.getArchive(),
                         " Unable to validate empty 'Sample Name' column . 'Comment [TCGA Biospecimen Type]' column can't be empty")
                 );    		
    		}else{
                // List of UUIDs for batch validation when this is run by the standalone validator
                final List<String> uuidBatchList = new ArrayList<String>();

                final String uuidNotInDBErrorMessagePrefix = " 'Sample Name' column has a UUID which has not been submitted by the BCR yet, "
                        + "so data for it cannot be accepted. The UUID value in the column is ";

                // loop through the Comment [TCGA Biospecimen Type] column
	    		for (int i = 0 ; i < commentBioTypeList.size() ; i ++){
	    			String comment = commentBioTypeList.get(i);
	    			if ("->".equalsIgnoreCase(comment)){
	    				// per spec do nothing since the row represents a cell line or control.
	    			}else if ("Shipped Portion".equalsIgnoreCase(comment)){
	    				// 'Sample name' must contain a valid UUID than
                        final String uuid = sampleNameList.get(i);
                        boolean isValidUUIDFormat = getQcLiveBarcodeAndUUIDValidator().validateUUIDFormat(uuid);
                        if(isValidUUIDFormat) {

                            boolean uuidExistsInDB = true;
                            if(qcContext.isStandaloneValidator()) {
                                // Add UUID to batch list to validate outside of this for loop
                                uuidBatchList.add(uuid);
                            } else {
                                uuidExistsInDB = validateUUIDagainstTCGADB(uuid);
                            }

                            if (!uuidExistsInDB){

                                isValid = false;
                                qcContext.addError(MessageFormat.format(
                                         MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                                         qcContext.getArchive(),
                                         uuidNotInDBErrorMessagePrefix + uuid)
                                );
                            }

                        } else {
                            isValid = false;
                            final String errorMessage = " 'Sample Name' column has a UUID with an invalid format. The UUID value in the column is " + uuid;
                            qcContext.addError(MessageFormat.format(
                                     MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                                     qcContext.getArchive(),
                                     errorMessage)
                            );
                        }

	    			}else{	
	    				// has to either be Shipped Portion or '->'
	    				isValid = false;
	    				qcContext.addError(MessageFormat.format(
	                             MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
	                             qcContext.getArchive(),
	                             "'Comment [TCGA Biospecimen Type]'" +
	                             "  must either be a blank line '->' or an UUID , but in this case it is :" + comment)
	                    );
	    			}    			    		
	    		}

                // Batch UUID validation when run by the standalone validator
                if(uuidBatchList.size() > 0) {
                    final Map<String, Boolean> uuidValidityMap = getUUIDValidityMap(uuidBatchList, qcContext);

                    for(final String uuid : uuidValidityMap.keySet()) {

                        final boolean uuidExistsInDB = uuidValidityMap.get(uuid);

                        if(!uuidExistsInDB) {

                            isValid = false;
                            qcContext.addError(MessageFormat.format(
                                     MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                                     qcContext.getArchive(),
                                     uuidNotInDBErrorMessagePrefix + uuid)
                            );
                        }
                    }
                }
    		}
    		
    	}else{
    		throw new IllegalArgumentException ( " Unable to navigate an empty SDRFNavigator object");
    	}    	    	    	
    	return isValid;
    }

    /**
     * Validate the given {@link List} of UUIDs and report validation errors in the {@link QcContext}
     *
     * Note: The UUIDs must exist in the DB to be valid.
     *
     * @param uuids the UUIDs to validate
     * @param qcContext the qclive context
     * @return a {@link Map} of UUID -> validity
     */
    private Map<String, Boolean> getUUIDValidityMap(final List<String> uuids, final QcContext qcContext) {

        final Map<String, Boolean> result = new HashMap<String, Boolean>();

        if(qcContext.isStandaloneValidator()) {
            final Map<String, Boolean> validityMap = getQcLiveBarcodeAndUUIDValidator().batchValidateUUIDsReportIndividualResults(uuids, qcContext, null, true);

            if(validityMap != null) {
                result.putAll(validityMap);
            }
        }

        return result;
    }

    @Override
    protected boolean getDataRequired() {
        return false;
    }

    @Override
    protected boolean validateColumnValue(String columnName, String value, int lineNum, QcContext context) {
    	Boolean isValid = true;
        if(columnName.equals(COMMENT_INCLUDE_FOR_ANALYSIS)){
            if(!VALID_VALUES_FOR_TCGA_INCLUDE_FOR_ANALYSIS.contains(value.toLowerCase())){
                context.addError(MessageFormat.format(
                        MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                        context.getArchive(),
                        new StringBuilder("Line ")
                                .append(lineNum)
                                .append(":")
                                .append(COMMENT_INCLUDE_FOR_ANALYSIS)
                                .append(" value is '")
                                .append(value)
                                .append("'. It should be one of [")
                                .append(VALID_VALUES_FOR_TCGA_INCLUDE_FOR_ANALYSIS)
                                .append("] ")));
                isValid = false;
            }            
        }else if (columnName.equals(COMMENT_DATA_TYPE)){
        	if (!VALID_VALUES_FOR_TCGA_COMMENT_TCGA_DATA_TYPE.contains(value)){
        		context.addError(MessageFormat.format(
                        MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                        context.getArchive(),
                        new StringBuilder("Line ")
                                .append(lineNum)
                                .append(":")
                                .append(COMMENT_DATA_TYPE)
                                .append(" value is '")
                                .append(value)
                                .append("'. It should be one of [")
                                .append(VALID_VALUES_FOR_TCGA_COMMENT_TCGA_DATA_TYPE)
                                .append("] ")));
                isValid = false;
        	}
        } else if (columnName.equals(COMMENT_FILE_TYPE)){
            if (!VALID_VALUES_FOR_TCGA_FILE_TYPE.contains(value)) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                        context.getArchive(),
                        new StringBuilder("Line ").append(lineNum).append(":")
                                .append(COMMENT_FILE_TYPE) .append(" value is '")
                                .append(value).append("'. It should be one of [")
                                .append(VALID_VALUES_FOR_TCGA_FILE_TYPE).append("] ")));
                isValid = false;
            }
        }
        return isValid;
    }

    @Override
    public List<String> getFileColumnNames() {
        return PROTEIN_FILE_COLUMNS;
    }

    @Override
    public List<String> getRequiredCommentColumns() {
        return PROTEIN_REQUIRED_COMMENTS_COLUMNS;
    }

    /**
     * Return <code>true</code> if the 'Sample Name' column header appear once and only once, <code>false</code> otherwise
     *
     * @param headers the SDRF headers
     * @param qcContext the qcLive context
     * @return <code>true</code> if the 'Sample Name' column header appear once and only once, <code>false</code> otherwise
     */
    protected Boolean validateSampleNameHeader(final List<String> headers, final QcContext qcContext) {

        boolean result = true;

        Integer sampleNameHeaderCount = 0;
        for(final String header : headers) {

            if(SAMPLE_NAME.equals(header)) {
                sampleNameHeaderCount++;
            }
        }

        if(sampleNameHeaderCount != 1) {

            result = false;

            if(sampleNameHeaderCount == 0) {
                qcContext.addError(MessageFormat.format(
                        MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                        qcContext.getArchive(),
                        new StringBuilder("header '").append(SAMPLE_NAME).append("' was not found")
                ));

            } else {
                qcContext.addError(MessageFormat.format(
                        MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                        qcContext.getArchive(),
                        new StringBuilder("header '").append(SAMPLE_NAME).append("' should only occur once")
                ));
            }
        }

        return result;
    }

    /**
     * Validate that data file columns are followed by 'include for analysis' comment column
     * This comment column can come after other comment columns but must come before the
     * next non-comment column
     * @param headers
     * @param qcContext
     * @return  true or false
     */

    public Boolean validateDataFileColumnHeaders(final List<String> headers, final QcContext qcContext){

        final List<String> dataFileColumnHeaders = Arrays.asList(DATA_FILE_COLUMNS);
        final List<String> requiredCommentColumns = Arrays.asList(COMMENT_INCLUDE_FOR_ANALYSIS, COMMENT_ARCHIVE_NAME);

        return validateRequiredCommentHeadersForDataFile(headers, dataFileColumnHeaders, requiredCommentColumns, qcContext);
    }

    /**
     * Validate that for the given headers, each header that matches the given data file column header
     * has the required associated comment columns. Return <code>true</code> if validation passed, <code>false</code> otherwise.
     *
     * @param headers the file headers to validate
     * @param dataFileColumnHeaders the data file column headers that should have the required comment column
     * @param requiredCommentColumns the names of the required comment columns
     * @param qcContext the qcLive context
     * @return <code>true</code> if validation passed, <code>false</code> otherwise
     */
    private Boolean validateRequiredCommentHeadersForDataFile(final List<String> headers,
                                                              final List<String> dataFileColumnHeaders,
                                                              final List<String> requiredCommentColumns,
                                                              final QcContext qcContext) {
        Boolean result = true;

        int headerIndex = 0;
        String commentHeader;
        final List<String> actualCommentColumns = new LinkedList<String>();

        while(headerIndex < headers.size()){

            int dataColumnHeaderIndex = headerIndex;

            if(dataFileColumnHeaders.contains(headers.get(headerIndex))){ // Found data file header

                commentHeader = null;
                actualCommentColumns.clear();

                // List all the actual comment columns that follow that header
                while(++headerIndex < headers.size()
                       && (commentHeader = headers.get(headerIndex)) != null
                       && commentHeader.startsWith(COMMENT_COLUMN_PREFIX)) {
                    actualCommentColumns.add(commentHeader);
                }

                // Validate that this header is followed by the required comment column headers
                for(final String requiredCommentColumn : requiredCommentColumns) {

                    if(!actualCommentColumns.contains(requiredCommentColumn)) {

                        qcContext.addError(MessageFormat.format(
                                MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                                qcContext.getArchive(),
                                new StringBuilder("header '")
                                        .append(headers.get(dataColumnHeaderIndex))
                                        .append("'[")
                                        .append(dataColumnHeaderIndex)
                                        .append("]")
                                        .append(" does not have '").append(requiredCommentColumn).append("' column.")
                        ));

                        result =  false;
                    }
                }

            } else {
                headerIndex++;
            }
        }

        return result;
    }

    /**
     * Validates that whether a UUID exists in the TCGA db and it's a shipped portion
     *
     * @param UUID to check
     * @return <code>true</code> if the UUID exists in the db and its type is 'shipped_portion', <code>false</code> otherwise
     */
    private Boolean validateUUIDagainstTCGADB(String UUID) {
        if(shippedBioQueries != null){
            Boolean isValid = shippedBioQueries.isShippedBiospecimenShippedPortionUUIDValid(UUID);
            return isValid;
        }
        return true;
    }
    
  
    public void setShippedBioQueries(ShippedBiospecimenQueries shippedBioQueries) {
		this.shippedBioQueries = shippedBioQueries;
	}
    
    /**
     *  In the protein array we should not be validating Extract Column at all
     *  the column is superseded by "Sample Name" column and should be left unchecked.
     *  from protein array spec:
     *  This is the first instance of a center submitting data where biospecimens IDs
     *  are tracked using UUID. The primary SDRF column containing TCGA UUIDs 
     *  for Protein Arrays is the Sample Name column, not the Extract Name column. 
     *  
     * (non-Javadoc)
     * @see gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.AbstractSdrfValidator#validateBarcodesAndUuids(gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext, gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedContentNavigator)
     */    
    @Override
    protected boolean validateBarcodesAndUuids(final QcContext context, final TabDelimitedContentNavigator sdrfNavigator)
    	throws ProcessorException {    	
    	return true;
    }
    
}