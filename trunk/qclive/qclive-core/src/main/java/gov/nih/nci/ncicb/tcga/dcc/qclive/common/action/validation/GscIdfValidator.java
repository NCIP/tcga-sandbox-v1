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
 * IDF validator for GSC archives.
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class GscIdfValidator extends AbstractIdfValidator{

    private static final String PROCESSOR_NAME = "IDF validation (GSC)";

    /**
     * IDF allowed headers: key = header name, value = is it a required header
     */
    private static final Map<String, Boolean> ALLOWED_IDF_HEADERS = new HashMap<String, Boolean>();

    static {
        // Required
        ALLOWED_IDF_HEADERS.put("Investigation Title", true);
        ALLOWED_IDF_HEADERS.put("Experimental Design", true);
        ALLOWED_IDF_HEADERS.put("Experimental Design Term Source REF", true);
        ALLOWED_IDF_HEADERS.put("Experimental Factor Name", true);
        ALLOWED_IDF_HEADERS.put("Experimental Factor Type", true);
        ALLOWED_IDF_HEADERS.put("Person Last Name", true);
        ALLOWED_IDF_HEADERS.put("Person First Name", true);
        ALLOWED_IDF_HEADERS.put("Person Email", true);
        ALLOWED_IDF_HEADERS.put("Person Affiliation", true);
        ALLOWED_IDF_HEADERS.put("Person Roles", true);
        ALLOWED_IDF_HEADERS.put("Experiment Description", true);
        ALLOWED_IDF_HEADERS.put("Protocol Name", true);
        ALLOWED_IDF_HEADERS.put("Protocol Type", true);
        ALLOWED_IDF_HEADERS.put("Protocol Description", true);
        ALLOWED_IDF_HEADERS.put("Protocol Parameters", true);
        ALLOWED_IDF_HEADERS.put("Protocol Term Source REF", true);
        ALLOWED_IDF_HEADERS.put("SDRF Files", true);
        ALLOWED_IDF_HEADERS.put("Term Source Name", true);
        ALLOWED_IDF_HEADERS.put("Term Source File", true);
        ALLOWED_IDF_HEADERS.put("Term Source Version", true);

        // Optional
        ALLOWED_IDF_HEADERS.put("Person Mid Initials", false);
        ALLOWED_IDF_HEADERS.put("Person Address", false);
        ALLOWED_IDF_HEADERS.put("PubMed ID", false);
        ALLOWED_IDF_HEADERS.put("Publication Author List", false);
        ALLOWED_IDF_HEADERS.put("Publication Title", false);
        ALLOWED_IDF_HEADERS.put("Publication Status", false);
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
