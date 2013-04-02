/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.DataMatrix;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Validates a data matrix.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class DataMatrixValidator extends AbstractProcessor<DataMatrix, Boolean> {
    static final String TERM_SOURCE = "Term Source";
    static final String COORDINATES_REF = "Coordinates REF";
    static final String COMPOSITE_ELEMENT_REF = "Composite Element REF";
    static final String REPORTER_REF = "Reporter REF";
    static final String SAMPLE_REF = "Sample REF";
    static final String PROTEIN_EXPRESSION = "protein_expression";

    /**
     * Validates a DataMatrix object.
     *
     * @param matrix the matrix to validate
     * @return true if validation passed, false if it failed
     */
    protected Boolean doWork( final DataMatrix matrix, final QcContext context ) {
        boolean passed = true;
        // make sure Name type isn't blank
        if(matrix.getNameType() == null || matrix.getNameType().trim().length() == 0) {
            passed = false;
            context.addError(MessageFormat.format(
            		MessagePropertyType.DATA_MATRIX_PROCESSING_ERROR, 
            		matrix, 
            		"Name type not specified"));
        }                
        
        // make sure Reporter type isn't blank and it is valid
        passed = validateReporterTypeForRFE(context, matrix.getReporterType(), matrix.getFilename());
        
     // for Level_3 protein array data make sure the nameType is Sample REF
        if (matrix.getFilename().contains(Archive.TYPE_LEVEL_3) && matrix.getFilename().contains(PROTEIN_EXPRESSION)){
        	 if(!matrix.getNameType().equals(SAMPLE_REF)){
        		 passed = false;
                 context.addError(MessageFormat.format(
                 		MessagePropertyType.DATA_MATRIX_PROCESSING_ERROR, 
                 		matrix, 
                 		"Level_3 protein expression data files must contain Sample REF in major header"));
             }
        }
        
        // number of names = number of quantitation types
        if(matrix.getNames().length != matrix.getQuantitationTypes().length) {
            passed = false;
            context.addError(MessageFormat.format(
            		MessagePropertyType.DATA_MATRIX_PROCESSING_ERROR, 
            		matrix, 
            		"Major and minor headers don't have the same number of elements"));
        }
        // check that no names or quantitation types are blank
        for(int i = 0; i < matrix.getNames().length; i++) {
            if(matrix.getNames()[i].trim().equals( "" )) {
                passed = false;
                context.addError(MessageFormat.format(
                		MessagePropertyType.DATA_MATRIX_PROCESSING_ERROR, 
                		matrix, 
                		new StringBuilder().append("Major header has a blank element at column '").append(i).append("'").toString()));
            }
        }
        for(int i = 0; i < matrix.getQuantitationTypes().length; i++) {
            if(matrix.getQuantitationTypes()[i].trim().equals( "" )) {
                passed = false;
                context.addError(MessageFormat.format(
                		MessagePropertyType.DATA_MATRIX_PROCESSING_ERROR, 
                		matrix, 
                		new StringBuilder().append("Minor header has a blank element at column '").append(i).append("'").toString()));
            }
        }
        // check that each name occurs once exactly for each distinct quantitation type
        // first, figure out what is there
        Map<String, List<String>> found = new HashMap<String, List<String>>();
        for(int i = 0; i < matrix.getQuantitationTypes().length; i++) {
            String quantType = matrix.getQuantitationTypes()[i];
            String name = matrix.getName( i );
            if(found.get( name ) == null) {
                List<String> list = new ArrayList<String>();
                found.put( name, list );
            }
            found.get( name ).add( quantType );
        }
        // now check each name for each distinct quantitation type
        Set<String> quantTypes = matrix.getDistinctQuantitationTypes();
        for(String name : found.keySet()) {
            List<String> types = found.get( name );
            if(types.size() != quantTypes.size()) {
                passed = false;
                // if all quant types are unique, suspect that this is not a data matrix file 
                if(quantTypes.size() == matrix.getQuantitationTypes().length) {
                	context.addError(MessageFormat.format(
                    		MessagePropertyType.DATA_MATRIX_PROCESSING_ERROR, 
                    		matrix, 
                    		new StringBuilder().append("File ").append(matrix.getFilename()).append(" is in a 'Data Matrix' type column in the SDRF but does not appear to be a Data Matrix File.  Files that aren't MAGE-TAB Data Matrices should be in 'Data File' type columns.").toString()));
                } else {
                	context.addError(MessageFormat.format(
                    		MessagePropertyType.DATA_MATRIX_PROCESSING_ERROR, 
                    		matrix,
                    		new StringBuilder().append("Expected '").append(quantTypes.size()).append("' columns for '").append(name).append("' but found '").append(types.size()).append("'").toString()));
                }
            } else {
                for(String quantType : quantTypes) {
                    if(!types.contains( quantType )) {
                        passed = false;
                        context.addError(MessageFormat.format(
                        		MessagePropertyType.DATA_MATRIX_PROCESSING_ERROR, 
                        		matrix, 
                        		new StringBuilder().append("Missing '").append(quantType).append("' column for '").append(name).append("'").toString()));
                    }
                }
            }
        }
        // then get Name type and make sure it is a valid column in the SDRF
        // substitute Name for REF when if needed
        final String columnName = matrix.getNameType().replace( "REF", "Name" );
        // find the name column
        int nameColumn = -1;
        for(int i = 0; i < context.getSdrf().getTabDelimitedHeaderValues().length; i++) {
            if(context.getSdrf().getTabDelimitedHeaderValues()[i].equals( columnName )) {
                nameColumn = i;
                break;
            }
        }
        if(nameColumn == -1) {
            passed = false;
            context.addError(MessageFormat.format(
            		MessagePropertyType.DATA_MATRIX_PROCESSING_ERROR, 
            		matrix, 
            		"Column '" + columnName + "' does not appear in the SDRF, even though '" + matrix.getNameType() + "' is given as the type in the Data Matrix file"));
        } else {
            // found the column, so can proceed with validation
            final List<String> sdrfNames = new ArrayList<String>();
            // get this column from the SDRF (no method for this?)
            final Map<Integer, String[]> sdrfMap = context.getSdrf().getTabDelimitedContents();
            for(final String[] row : sdrfMap.values()) {
                sdrfNames.add( row[nameColumn] );
            }
            // make sure all Names from matrix are in this column in the SDRF
            for(final String name : matrix.getNames()) {

                if(!StringUtil.containsIgnoreCase(sdrfNames, name)) {
                    passed = false;
                    context.addError(MessageFormat.format(
                    		MessagePropertyType.DATA_MATRIX_PROCESSING_ERROR, 
                    		matrix, 
                    		new StringBuilder().append(matrix.getNameType()).append(" '").append(name).append("' does not appear in the '").append(columnName).append("' column in the SDRF file").toString()));
                }
            }
        }
        return passed;
    }

    /**
     * Gets the type of validator this represents.
     *
     * @return the type of validator this is  -- human readable, concise
     */
    public String getName() {
        return "data Matrix file validation";
    }

    boolean validateReporterTypeForRFE(final QcContext context, final String reporterType, final String matrixFileName) {

        boolean passed = true;
        
        if(reporterType == null || reporterType.trim().length() == 0) {
            passed = false;
            context.addError(MessageFormat.format(
            		MessagePropertyType.DATA_MATRIX_PROCESSING_ERROR, 
            		matrixFileName, 
            		"Reporter type not specified"));
        }else {
            if(!((reporterType.startsWith(TERM_SOURCE)) ||
                    (reporterType.startsWith(COORDINATES_REF)))) {

                if(!((reporterType.equals(COMPOSITE_ELEMENT_REF)) ||
                        (reporterType.equals(REPORTER_REF)))) {
                	context.addError(MessageFormat.format(
                    		MessagePropertyType.DATA_MATRIX_PROCESSING_ERROR, 
                    		matrixFileName, 
                    		new StringBuilder().append("Minor header value '").append(reporterType).append("' is not valid (should be one of: '").append( 
                    		COMPOSITE_ELEMENT_REF).append("', '").append(REPORTER_REF).append("', 'Term Source REF:<tag>', or 'Coordinates REF:<version>'").toString()));
                    passed = false;
                }
            }
        }
        return passed;
    }        

}
