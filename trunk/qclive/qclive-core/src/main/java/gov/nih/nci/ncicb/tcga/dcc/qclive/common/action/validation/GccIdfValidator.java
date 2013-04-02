/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 *  Copyright Notice.  The software subject to this notice and license includes both human
 *  readable source code form and machine readable, binary, object code form (the "caBIG
 *  Software").
 *
 *  Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * IDF validator for GCC archives.
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class GccIdfValidator extends AbstractIdfValidator {

    private static final String PROCESSOR_NAME = "IDF validation (GCC)";

    /**
     * IDF allowed headers: key = header name, value = is it a required header
     */
    private static final Map<String, Boolean> ALLOWED_IDF_HEADERS = new HashMap<String, Boolean>();

    static {
        // Required
        ALLOWED_IDF_HEADERS.put("Protocol Name", true);
        ALLOWED_IDF_HEADERS.put("Protocol Description", true);

        // Optional
        ALLOWED_IDF_HEADERS.put("Investigation Title", false);
        ALLOWED_IDF_HEADERS.put("Experimental Design", false);
        ALLOWED_IDF_HEADERS.put("Experimental Design Term Source REF", false);
        ALLOWED_IDF_HEADERS.put("Experimental Factor Name", false);
        ALLOWED_IDF_HEADERS.put("Experimental Factor Type", false);
        ALLOWED_IDF_HEADERS.put("Experimental Factor Type Term Source REF", false);
        ALLOWED_IDF_HEADERS.put("Person Last Name", false);
        ALLOWED_IDF_HEADERS.put("Person First Name", false);
        ALLOWED_IDF_HEADERS.put("Person Mid Initials", false);
        ALLOWED_IDF_HEADERS.put("Person Email", false);
        ALLOWED_IDF_HEADERS.put("Person Phone", false);
        ALLOWED_IDF_HEADERS.put("Person Fax", false);
        ALLOWED_IDF_HEADERS.put("Person Address", false);
        ALLOWED_IDF_HEADERS.put("Person Affiliation", false);
        ALLOWED_IDF_HEADERS.put("Person Roles", false);
        ALLOWED_IDF_HEADERS.put("Person Roles Term Source REF", false);
        ALLOWED_IDF_HEADERS.put("Quality Control Types", false);
        ALLOWED_IDF_HEADERS.put("Quality Control Types Term Source REF", false);
        ALLOWED_IDF_HEADERS.put("Replicate Type", false);
        ALLOWED_IDF_HEADERS.put("Replicate Type Term Source REF", false);
        ALLOWED_IDF_HEADERS.put("Normalization Type", false);
        ALLOWED_IDF_HEADERS.put("Normalization Term Source REF", false);
        ALLOWED_IDF_HEADERS.put("Date of Experiment", false);
        ALLOWED_IDF_HEADERS.put("Public Release Date", false);
        ALLOWED_IDF_HEADERS.put("Comment[ArrayExpressSubmissionDate]", false);
        ALLOWED_IDF_HEADERS.put("PubMed ID", false);
        ALLOWED_IDF_HEADERS.put("Publication DOI", false);
        ALLOWED_IDF_HEADERS.put("Publication Author List", false);
        ALLOWED_IDF_HEADERS.put("Publication Title", false);
        ALLOWED_IDF_HEADERS.put("Publication Status", false);
        ALLOWED_IDF_HEADERS.put("Publication Status Term Source REF", false);
        ALLOWED_IDF_HEADERS.put("Experiment Description", false);
        ALLOWED_IDF_HEADERS.put("Protocol Type", false);
        ALLOWED_IDF_HEADERS.put("Protocol Parameters", false);
        ALLOWED_IDF_HEADERS.put("Protocol Hardware", false);
        ALLOWED_IDF_HEADERS.put("Protocol Software", false);
        ALLOWED_IDF_HEADERS.put("Protocol Contact", false);
        ALLOWED_IDF_HEADERS.put("Protocol Term Source REF", false);
        ALLOWED_IDF_HEADERS.put("SDRF Files", false);
        ALLOWED_IDF_HEADERS.put("Term Source Name", false);
        ALLOWED_IDF_HEADERS.put("Term Source File", false);
        ALLOWED_IDF_HEADERS.put("Term Source Version", false);
    }


    @Override
    public Collection<String> getAllowedIdfHeaders() {
        return ALLOWED_IDF_HEADERS.keySet();
    }

    @Override
    protected Collection<String> getRequiredIdfHeaders() {

        final Collection<String> requiredHeaders = new ArrayList<String>();

        for (final String allowedHeader : getAllowedIdfHeaders()) {

            if(ALLOWED_IDF_HEADERS.get(allowedHeader)) {
                requiredHeaders.add(allowedHeader);
            }
        }

        return requiredHeaders;
    }

    @Override
    public String getName() {
        return PROCESSOR_NAME;
    }
}
