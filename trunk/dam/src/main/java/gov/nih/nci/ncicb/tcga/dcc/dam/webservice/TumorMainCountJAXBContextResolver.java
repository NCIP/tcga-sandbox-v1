/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.webservice;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.stats.TumorMainCount;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;

/**
 * JAXBContextResolver for TumorMainCount objects: this configuration allows for integer fields to be left unquoted.
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@Provider
public class TumorMainCountJAXBContextResolver  implements ContextResolver<JAXBContext> {

    private JAXBContext context;

    private final static Class<?>[] TYPES = {TumorMainCount.class};

    /**
     * Attributes which fields should be left unquoted in the JSON output
     */
    private final static String[] UNQUOTED_ATTRIBUTES = {"casesShipped", "casesWithData"};

    public TumorMainCountJAXBContextResolver() throws Exception {
        this.context = new JSONJAXBContext(JSONConfiguration.mapped().nonStrings(UNQUOTED_ATTRIBUTES).build(), TYPES);
    }

    /**
     * Return the JAXBContext for the given object type
     *
     * @param objectType the object type
     * @return the JAXBContext for the given object type
     */
    public JAXBContext getContext(final Class<?> objectType) {

        for (final Class<?> c : TYPES) {

            if (c.equals(objectType)) {
                return context;
            }
        }

        return null;
    }
}
