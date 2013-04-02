/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedContentNavigator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Validator for array-based SDRFs.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: chenjw $
 * @version $Rev: 3441 $
 */
public class ArraySdrfValidator extends AbstractSdrfValidator {

    private static final Map<String, Boolean> CHECK_COLUMNS = new HashMap<String, Boolean>();
    private static final String SOURCE_NAME_COLUMN_NAME = "Source Name";
    private static final String SAMPLE_NAME_COLUMN_NAME = "Sample Name";
    private static final String MATRIX = "Matrix";
    private static final String ARRAY_DESIGN_REF_COLUMN_NAME = "Array Design REF";

    static {
        CHECK_COLUMNS.put(SOURCE_NAME, false);
        CHECK_COLUMNS.put(PROVIDER, false);
        CHECK_COLUMNS.put(MATERIAL_TYPE, false);
        CHECK_COLUMNS.put(CHARACTERISTICS_GENOTYPE, false);
        CHECK_COLUMNS.put(CHARACTERISTICS_ORGANISM, false);
        CHECK_COLUMNS.put(SAMPLE_NAME, false);
        CHECK_COLUMNS.put(EXTRACT_NAME_COLUMN_NAME, true);
    }

    /**
     * Checks if Source Name and Sample Name columns have values -- if so, this is a valid control row.
     * If not a valid control row and Extract Name doesn't start with TCGA or is not a valid uuid,
     * assume this is supposed to be a control row but data is missing, so add an error to context.
     * 
     * @param context the qc context
     * @param sdrfNavigator the sdrf
     * @param row the row number
     * @param extractName the extract name for this row
     * @return  if the row is a valid control row
     */
    protected boolean isValidControlRow(final QcContext context, final TabDelimitedContentNavigator sdrfNavigator,
                                      final int row, final String extractName) {

        boolean isValidControl = true;
        int sourceNameColumn = -1, sampleNameColumn = -1;

        List<String> headers = sdrfNavigator.getHeaders();
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).equals(SOURCE_NAME_COLUMN_NAME)) {
                sourceNameColumn = i;
            } else if (headers.get(i).equals(SAMPLE_NAME_COLUMN_NAME)) {
                sampleNameColumn = i;
            } else if (headers.get(i).equals(EXTRACT_NAME_COLUMN_NAME)) {
                break;
            }
        }
        if (sourceNameColumn == -1 || sampleNameColumn == -1) {
            isValidControl = false;
        } else {
            // make sure values are not blank
            String sourceName = sdrfNavigator.getValueByCoordinates(sourceNameColumn, row);
            String sampleName = sdrfNavigator.getValueByCoordinates(sampleNameColumn, row);
            if (sourceName.equals("->") || sampleName.equals("->")) {
                isValidControl = false;
            }
        }

        if (!isValidControl) {
            if (!extractName.trim().startsWith("TCGA")) {
                if (!getQcLiveBarcodeAndUUIDValidator().validateUUIDFormat(extractName)) {
                    context.addError(MessageFormat.format(
                            MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                            context.getArchive(),
                            new StringBuilder().append("line ").append((row + NUM_HEADERS)).append(": ").
                                    append(SOURCE_NAME_COLUMN_NAME).append(" and ").append(SAMPLE_NAME_COLUMN_NAME).
                                    append(" columns must be included for internal controls and non-BCR analytes").toString()));
                }
            }
        }
        return isValidControl;
    }

    /**
     * Does nothing.
     * @param context the qc context
     * @param header the name of the file header
     * @param row the row number being checked
     * @param level the value of the level column for this file column
     * @return if the level and file type are valid together
     */
    protected boolean validateFileHeaderAndLevel(final QcContext context, final String header, final int row, final String level) {
        return true;
    }

    @Override
    protected boolean runSpecificValidations(final QcContext context, final TabDelimitedContentNavigator sdrfNavigator) {
        return validateArrayDesignRefs(context, sdrfNavigator);
    }
    
    /**
     * Checks if the headers are valid...
     * Validity Test 1
     * 	 Using the deprecated headers list, i.e., getDeprecatedSdrfHeaders():
     * 		If the header is in the deprecated sdrf headers list, then isValid is set to false
     *
     * Validity Test 2
     *   If local validations are passed, result from call to super class' headersAreValid() method is 
     *   returned
     * 
     * @param sdrfNavigator the sdrf content
     * @param context the qc context
     * @return if the headers are valid
     * @throws ProcessorException
     */
    @Override
    protected boolean headersAreValid(final TabDelimitedContentNavigator sdrfNavigator,
            						final QcContext context) throws ProcessorException 
    {
    	boolean isValid = true;
    	
    	final Collection<String> deprecatedHeaders = getDeprecatedSdrfHeaders();
    	
    	//Validity Test 1 (LOCAL)
    	for (final String header : sdrfNavigator.getHeaders()) 
    	{
			if( deprecatedHeaders.contains(header) )
			{
				context.addError(MessageFormat.format(
						MessagePropertyType.SDRF_DEPRECATED_COLUMN_VALIDATION_ERROR, 
						context.getArchive(),
						context.getArchive().getSdrfFile(),
						header ));
			
				isValid = false;
			}
		}
    	
    	//Validity Test 2 (Super Class) IFF local validations pass...
    	return ( isValid ) ? super.headersAreValid(sdrfNavigator, context) : isValid;
	}


    @Override
    protected boolean getDataRequired() {
        return true;
    }

    @Override
    protected boolean validateColumnValue(final String columnName, final String value, final int lineNum, final QcContext context) {
        // for now don't do any validation of generic column values
        return true;
    }

    /*
     * Checks Array Design REF values to make sure they have the correct format.
     */
    private boolean validateArrayDesignRefs(final QcContext context, final TabDelimitedContentNavigator sdrfNavigator) {
        boolean isValid = true;
        // now check the Array Design REF values
        final String arrayDesignRefPatternStr = "([a-zA-Z0-9\\-_.]+)[:]+([a-zA-Z0-9\\-_]+)[:]+([a-zA-Z0-9\\-_]+)";
        final Pattern arrayDesignPattern = Pattern.compile(arrayDesignRefPatternStr);
        final List<Integer> colNums = sdrfNavigator.getHeaderIdsForName(ARRAY_DESIGN_REF_COLUMN_NAME);
        for (final Integer col : colNums) {
            final List<String> values = sdrfNavigator.getColumnValues(col);
            for (final String val : values) {
                if (!arrayDesignPattern.matcher(val).matches()) {
                	context.addError(MessageFormat.format(
                			MessagePropertyType.ARCHIVE_SDRF_VALIDATION_ERROR,
                			context.getArchive(),
                			new StringBuilder().append(ARRAY_DESIGN_REF_COLUMN_NAME).append(" value '").append(val).append("' is not valid").toString()));
                    isValid = false;
                }
            }
        }
        return isValid;
    }

    /*
     * Reads the allowed SDRF headers from a file
     */
    protected Collection<String> getAllowedSdrfHeaders() throws ProcessorException {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(getSdrfHeaderStream()));

            final List<String> headers = new ArrayList<String>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.trim().length() > 0) {
                    headers.add(line.trim());
                }
            }
            return headers;
        } catch (URISyntaxException e) {
            throw new ProcessorException("Could not get list of allowed SDRF headers: " + e.getMessage());
        } catch (IOException e) {
            throw new ProcessorException("Could not get list of allowed SDRF headers: " + e.getMessage());
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    // ignore
                }
            }

        }
    }
    
    /*
     * Reads the deprecated SDRF headers from a file
     */
    protected Collection<String> getDeprecatedSdrfHeaders() throws ProcessorException {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(getDeprecatedSdrfHeaderStream()));

            final List<String> headers = new ArrayList<String>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.trim().length() > 0) {
                    headers.add(line.trim());
                }
            }
            return headers;
        } catch (URISyntaxException e) {
            throw new ProcessorException("Could not get list of deprecated SDRF headers: " + e.getMessage());
        } catch (IOException e) {
            throw new ProcessorException("Could not get list of deprecated SDRF headers: " + e.getMessage());
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }


    @Override
    protected Map<String, Boolean> getColumnsToCheck() {
        return CHECK_COLUMNS;
    }

    protected InputStream getSdrfHeaderStream() throws URISyntaxException {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(SDRF_HEADER_FILE_PATH);
    }
    
    protected InputStream getDeprecatedSdrfHeaderStream() throws URISyntaxException {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(DEPRECATED_SDRF_HEADER_FILE_PATH);
    }
    
}
