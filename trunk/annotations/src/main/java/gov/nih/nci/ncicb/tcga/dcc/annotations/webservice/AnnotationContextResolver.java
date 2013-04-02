/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.annotations.webservice;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotation;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;

/**
 * Context Resolver for allowing natural json generation in json and jsonp calls
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Provider
public class AnnotationContextResolver implements ContextResolver<JAXBContext> {

    private JAXBContext context;
    private Class<?>[] types = {DccAnnotation.class};

    public AnnotationContextResolver() throws Exception {
        this.context = new JSONJAXBContext(JSONConfiguration.natural().build(),types);
    }

    public JAXBContext getContext(Class<?> objectType) {
        for (Class<?> c : types) {
            if (c.equals(objectType)) {
                return context;
            }
        }
        return null;
    }

}//End of Class
