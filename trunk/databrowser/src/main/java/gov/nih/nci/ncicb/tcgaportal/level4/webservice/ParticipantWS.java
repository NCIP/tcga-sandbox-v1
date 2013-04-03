/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.webservice;

import com.sun.jersey.api.json.JSONWithPadding;
import com.sun.jersey.spi.inject.Inject;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.HttpStatusCode;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.WebServiceUtil;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.Level4Queries;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.Level4QueriesGetter;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.QueriesException;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.ResultRow;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.CopyNumberType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.UpperAndLowerLimits;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Web service class for the patients web service
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Path("/participant")
@Scope("request")
public class ParticipantWS {

    protected final Log logger = LogFactory.getLog(getClass());

    @Inject
    protected Level4QueriesGetter l4QueriesGetter;

    @QueryParam("disease")
    protected String disease;
    @QueryParam("genes")
    protected String genes;
    @QueryParam("frequency")
    protected Float frequency;
    @QueryParam("lowerLimit")
    protected Double lowerLimit;
    @QueryParam("upperLimit")
    protected Double upperLimit;

    /**
     * participant list query method
     *
     * @return participant list
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getParticipantList() {
        Results res = processRequest();
        String str = "Participant";
            if (res != null) {
                for (ResultRow rr : res.getRows()) {
                    str += "\n" + rr.getName();
                }
            }
        return str;
    }

    @GET
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    public String getParticipantListToJSON() {
        Results res = processRequest();
        JSONArray jsonArray = new JSONArray();
         if (res != null) {
                for (ResultRow rr : res.getRows()) {
                    jsonArray.add(new JSONObject().element("barcode",rr.getName()));
                }
            }
        return new JSONObject().element("participant",jsonArray).toString();
    }

    @GET
    @Path("/jsonp")
    @Produces("application/x-javascript")
    public JSONWithPadding getParticipantListToJSONP(@QueryParam("callback") @DefaultValue("fn") String callback) {
        Results res = processRequest();
        JSONArray jsonArray = new JSONArray();
         if (res != null) {
                for (ResultRow rr : res.getRows()) {
                    jsonArray.add(new JSONObject().element("barcode",rr.getName()));
                }
            }
        return new JSONWithPadding(new JSONObject().element("participant",jsonArray).toString(), callback);
    }



    private Results processRequest() {
        validateQueryParams();
        final Level4Queries l4Queries = l4QueriesGetter.getLevel4Queries(disease);
        final Results res;
        CopyNumberType cnt = new CopyNumberType();
        try {
            for (ColumnType col : l4Queries.getColumnTypes(disease)) {
                if (col instanceof CopyNumberType) {
                    if ("broad.mit.edu".equals(((CopyNumberType) col).getDisplayCenter()) &&
                            "Genome_Wide_SNP_6".equals(((CopyNumberType) col).getDisplayPlatform())) {
                        cnt = (CopyNumberType) col;
                        break;
                    }
                }
            }
            final List<ColumnType> cList = new ArrayList<ColumnType>();
            cnt.setPicked(true);
            cnt.setFrequency((frequency == 0f) ? 0f : frequency / 100f);
            cnt.setLowerLimit(lowerLimit);
            cnt.setUpperLimit(upperLimit);
            cnt.setLowerOperator(UpperAndLowerLimits.Operator.LTE);
            cnt.setUpperOperator(UpperAndLowerLimits.Operator.GTE);
            cList.add(cnt);
            final FilterSpecifier filter = new FilterSpecifier();
            filter.setDisease(disease);
            filter.setGeneList(genes);
            filter.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
            filter.setListBy(FilterSpecifier.ListBy.Patients);
            filter.setColumnTypes(cList);
            res = l4Queries.getAnomalyResults(filter);
        } catch (QueriesException e) {
            throw new WebApplicationException(WebServiceUtil.getStatusResponse(
                    HttpStatusCode.INTERNAL_SERVER_ERROR, e.getMessage()));
        } catch (IllegalArgumentException ill) {
            throw new WebApplicationException(WebServiceUtil.getStatusResponse(
                    HttpStatusCode.INTERNAL_SERVER_ERROR, ill.getMessage()));
        }
        return res;
    }

    private void validateQueryParams() {
        if (frequency == null) {
            throw new WebApplicationException(WebServiceUtil.getStatusResponse(
                    HttpStatusCode.PRECONDITION_FAILED, "Frequency cannot be null"));
        }
        if (lowerLimit == null) {
            throw new WebApplicationException(WebServiceUtil.getStatusResponse(
                    HttpStatusCode.PRECONDITION_FAILED, "Lower Limit cannot be null"));
        }
        if (upperLimit == null) {
            throw new WebApplicationException(WebServiceUtil.getStatusResponse(
                    HttpStatusCode.PRECONDITION_FAILED, "Upper Limit cannot be null"));
        }
    }

}//End of Class
