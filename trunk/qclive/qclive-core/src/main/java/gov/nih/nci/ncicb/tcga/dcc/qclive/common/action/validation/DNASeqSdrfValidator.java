/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 *  Copyright Notice.  The software subject to this notice and license includes both human
 *  readable source code form and machine readable, binary, object code form (the "caBIG
 *  Software").
 *
 *  Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ProtocolNameValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedContentNavigator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Validator for SDRF files submitted by GSCs as per the following specs: https://wiki.nci.nih.gov/x/SS8lBQ.
 *
 * Only the general SDRF validation rules applies for now.
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DNASeqSdrfValidator extends AbstractSdrfValidator {

    private static final String PROTOCOL_REF_COLUMN_NAME = "Protocol REF";

    private static final Map<String, Boolean> CHECK_COLUMNS = new HashMap<String, Boolean>();

    static {
        CHECK_COLUMNS.put(EXTRACT_NAME_COLUMN_NAME, true);
        CHECK_COLUMNS.put("Comment [TCGA Barcode]", true);
        CHECK_COLUMNS.put("Comment [is tumor]", false);
        CHECK_COLUMNS.put("Material Type", true);
        CHECK_COLUMNS.put("Comment [TCGA Sequence Target File]", false);
        CHECK_COLUMNS.put("Annotation REF", true);
        CHECK_COLUMNS.put("Comment [TCGA Genome Reference]", true);
        CHECK_COLUMNS.put("Comment [Derived Data File REF]", true);
        CHECK_COLUMNS.put("Comment [TCGA CGHub ID]", true);
        CHECK_COLUMNS.put("Comment [TCGA CGHub metadata URL]", true);
        CHECK_COLUMNS.put("Parameter Value [variant caller]", true);
        CHECK_COLUMNS.put("Parameter Value [software parameters]", true);
        CHECK_COLUMNS.put("Parameter Value [conversion tool]", false);
        CHECK_COLUMNS.put("Comment [TCGA Spec Version]", true);
        CHECK_COLUMNS.put("Parameter Value [validation method]", false);

        // The columns below are listed multiples times in the specs but with different requirements.
        // Yunhu's recommends that we make them required for now.
        CHECK_COLUMNS.put(PROTOCOL_REF_COLUMN_NAME, true);
        CHECK_COLUMNS.put("Comment [TCGA Include for Analysis]", true);
        CHECK_COLUMNS.put("Derived Data File", true);
        CHECK_COLUMNS.put("Comment [TCGA Data Type]", true);
        CHECK_COLUMNS.put("Comment [TCGA Data Level]", true);
        CHECK_COLUMNS.put("Comment [TCGA Archive Name]", true);
    }

    @Override
    public String getName() {
        return "SDRF (DNASeq) validation";
    }

    @Override
    protected Collection<String> getAllowedSdrfHeaders() throws ProcessorException {
        return CHECK_COLUMNS.keySet();
    }

    @Override
    protected Map<String, Boolean> getColumnsToCheck() {
        return CHECK_COLUMNS;
    }

    @Override
    protected boolean getDataRequired() {
        return false;
    }

    @Override
    protected boolean validateFileHeaderAndLevel(final QcContext context,
                                                 final String header,
                                                 final int row,
                                                 final String level) {
        return true;
    }

    @Override
    protected boolean runSpecificValidations(final QcContext context,
                                             final TabDelimitedContentNavigator sdrfNavigator) {
        return true;
    }

    /**
     * This implementation only validates a Protocol REF column and makes sure it is a valid protocol name.
     *
     * @param columnName the column header
     * @param value      the value
     * @param lineNum    the line where the value was from
     * @param context    the qc context
     * @return {@code true} if Protocol REF value is a valid protocol name, {code false otherwise}
     */
    @Override
    protected boolean validateColumnValue(final String columnName,
                                          final String value,
                                          final int lineNum,
                                          final QcContext context) {
        boolean result = true;

        if (PROTOCOL_REF_COLUMN_NAME.equals(columnName)) {

            final boolean validProtocolName = ProtocolNameValidator.isValid(value);

            if(!validProtocolName) {

                result = false;
                context.addError(MessageFormat.format(
                        MessagePropertyType.LINE_VALUE_FORMAT_ERROR,
                        lineNum,
                        PROTOCOL_REF_COLUMN_NAME,
                        ProtocolNameValidator.getDescription(),
                        value));
            }
        }

        return result;
    }
}
