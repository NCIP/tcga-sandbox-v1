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
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.stats.TumorMainCount;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.TumorMainService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Web Service to get tumor main count
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@Path("/tumormain")
public class TumorMainWS {

    @InjectParam
    private TumorMainService tumorMainService;

    /**
     * Return a TumorMainCount for each of the available diseases, as a list ordered by disease name, in JSON format
     *
     * @return a TumorMainCount for each of the available diseases, as a list ordered by disease name, in JSON format
     */
    @GET
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TumorMainCount> getTumorMainCountList() {

        try {
            return tumorMainService.getTumorMainCountList();

        } catch (TumorMainService.TumorMainServiceException e) {
            throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.INTERNAL_SERVER_ERROR,
                    "Could not retrieve Tumor Main Count data: " + e.getMessage()));
        }
    }

    /**
     * For unit tests
     *
     * @param tumorMainService the TumorMainService to set
     */
    public void setTumorMainService(final TumorMainService tumorMainService) {
        this.tumorMainService = tumorMainService;
    }
}
