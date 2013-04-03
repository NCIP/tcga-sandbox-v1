/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.webservice;

import com.sun.jersey.api.core.InjectParam;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.SearchCriteria;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.JSON;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.XML;

/**
 * REST Web service for Searching UUIDs
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Path("/uuidlist")
public class SearchUUIDWebService {

    @QueryParam("disease")
    private String disease;
    @QueryParam("centerName")
    private String centerName;
    @QueryParam("centerType")
    private String centerType;
    @QueryParam("uuid")
    private String uuid;
    @QueryParam("barcode")
    private String barcode;

    @InjectParam
    private UUIDService service;

    @InjectParam
    private UUIDWebServiceUtil webServiceUtil;

    private SearchCriteria searchCriteria = new SearchCriteria();

    public void setUuidService(final UUIDService uuidService) {
        this.service = uuidService;
    }

    /**
     * REST web service for getting UUID list in JSON format
     *
     * @return List of UUID details
     */
    @GET
    @Path(JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<UUIDDetail> searchUUIDToJSON() {
        return searchUUID(MediaType.APPLICATION_JSON);
    }

    /**
     * REST web service for getting UUID list in XML format
     *
     * @return List of UUID Details
     */
    @GET
    @Path(XML)
    @Produces(MediaType.APPLICATION_XML)
    public List<UUIDDetail> searchUUIDToXML() {
        return searchUUID(MediaType.APPLICATION_XML);
    }

    private List<UUIDDetail> searchUUID(String mediaType) {
        buildSearchCriteria(mediaType);
        return service.searchUUIDs(searchCriteria);
    }

    protected SearchCriteria buildSearchCriteria(String mediaType) {
        searchCriteria.setCenterId(webServiceUtil.getCenterId(centerName, centerType, mediaType));
        searchCriteria.setBarcode(barcode);
        searchCriteria.setUuid(uuid);
        searchCriteria.setDisease(webServiceUtil.getDiseaseId(disease, mediaType));
        return searchCriteria;
    }

    protected void setSearchCriteria(final SearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    protected void setWebServiceUtil(final UUIDWebServiceUtil webServiceUtil) {
        this.webServiceUtil = webServiceUtil;
    }
}
