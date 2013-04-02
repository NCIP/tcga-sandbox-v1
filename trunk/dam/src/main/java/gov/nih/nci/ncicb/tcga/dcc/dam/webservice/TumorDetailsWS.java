/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.webservice;

import com.sun.jersey.api.core.InjectParam;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.HttpStatusCode;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.WebServiceUtil;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.stats.DataTypeCount;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.TumorDetailsService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

/**
 * Web Service to get tumor sample type count details
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@Path("/tumordetails")
public class TumorDetailsWS {

    @InjectParam
    private TumorDetailsService tumorDetailsService;

    /**
     * Return a TumorSampleTypeCount for each of the sample types, as an array, in JSON format:
     * <p/>
     * - tumor
     * - matched normal
     * - unmatched normal
     *
     * @param diseaseAbbreviation the disease abbreviation
     * @return a TumorSampleTypeCount for each of the sample types, as an array, in JSON format
     */
    @GET
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    public DataTypeCount[] getTumorSampleTypeCountToJson(@QueryParam("diseaseType") final String diseaseAbbreviation) {


            return tumorDetailsService.getTumorDataTypeCountArray(diseaseAbbreviation);
    }

    /**
     * For unit tests
     *
     * @param tumorDetailsService the TumorDetailsService to set
     */
    public void setTumorDetailsService(final TumorDetailsService tumorDetailsService) {
        this.tumorDetailsService = tumorDetailsService;
    }
}
