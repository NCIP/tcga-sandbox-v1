/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.webservice;

import com.sun.jersey.api.core.InjectParam;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.PendingUUID;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.webservice.PendingUUIDResponse;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.PendingUUIDService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the pending UUID web service
 *
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Path("/pendinguuid")
@Scope("request")
public class PendingUUIDWS {

    protected final Log logger = LogFactory.getLog(PendingUUIDWS.class);

    @InjectParam
    private PendingUUIDService service;

    @POST
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public PendingUUIDResponse processJobToJson(final String jsonMessage) {
        return processJsonMessage(jsonMessage);
    }

    @POST
    @Path("/xml")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_JSON)
    public PendingUUIDResponse processJobToXml(final String jsonMessage) {
        return processJsonMessage(jsonMessage);
    }

    protected PendingUUIDResponse processJsonMessage(final String jsonMessage) {
        final PendingUUIDResponse pendingUUIDResponse = new PendingUUIDResponse();
        if (StringUtils.isBlank(jsonMessage)) {
            pendingUUIDResponse.setResponseMessage("failure");
            pendingUUIDResponse.setErrorMessages(new ArrayList<String>() {{
                add("The input json message is empty");
            }});
        } else {
            final Boolean valid = service.parseAndValidatePendingUUIDJson(jsonMessage);
            if (valid) {
                final List<PendingUUID> results = service.getPendingUUIDsFromJson(jsonMessage);
                service.persistPendingUUIDs(results);
                pendingUUIDResponse.setResponseMessage("success");
            } else {
                pendingUUIDResponse.setResponseMessage("failure");
                pendingUUIDResponse.setErrorMessages(service.getErrors());
            }
        }
        return pendingUUIDResponse;
    }

    public void setService(PendingUUIDService service) {
        this.service = service;
    }
}
