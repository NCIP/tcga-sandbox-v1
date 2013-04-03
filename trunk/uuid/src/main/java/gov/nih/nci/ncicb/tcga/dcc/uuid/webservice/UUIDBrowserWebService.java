/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.webservice;

import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.api.core.ResourceContext;
import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.BiospecimenMetaData;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.MetaDataBean;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.PortionAnalyte;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.SampleType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.TissueSourceSite;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ShippedBiospecimenQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.HttpStatusCode;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.WebServiceUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean.BarcodeListWS;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean.BarcodeWS;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws.AliquotUUIDWS;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws.AnalyteUUIDWS;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws.BCRUUIDWS;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws.BarcodeUUIDWS;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws.CenterUUIDWS;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws.DiseaseUUIDWS;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws.DrugUUIDWS;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws.ExaminationUUIDWS;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws.MetadataSearchWS;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws.MetadataViewWS;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws.ParticipantUUIDWS;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws.PortionUUIDWS;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws.RadiationUUIDWS;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws.SampleUUIDWS;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws.ShippedPortionUUIDWS;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws.SlideUUIDWS;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws.SurgeryUUIDWS;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws.TSSUUIDWS;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws.UUIDBrowserWS;
import gov.nih.nci.ncicb.tcga.dcc.uuid.service.UUIDBrowserService;
import gov.nih.nci.ncicb.tcga.dcc.uuid.service.UUIDCommonService;
import gov.nih.nci.ncicb.tcga.dcc.uuid.webservice.bean.UUIDBrowserWSQueryParamBean;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.ALIQUOT;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.ANALYTE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.BCR;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.BOTTOM;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.CELL_LINE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.CELL_LINE_CONTROL;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.DATE_FORMAT_US;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.DRUG;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.EXAMINATION;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.MIDDLE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.NORMAL;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.PARTICIPANT;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.PORTION;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.RADIATION;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.SAMPLE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.SEPARATOR;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.SHIPPED_PORTION;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.SLIDE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.SURGERY;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.TOP;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.TUMOR;

/**
 * UUID Browser Search WebService
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Path("/metadata")
@Scope("request")
public class UUIDBrowserWebService {

    // Logger
    protected final Log logger = LogFactory.getLog(getClass());

    @InjectParam
    private ShippedBiospecimenQueries shippedBiospecimenQueries;

    @InjectParam
    private UUIDBrowserService uuidBrowserService;

    @InjectParam
    private UUIDCommonService uuidCommonService;

    @InjectParam
    private UUIDService uuidService;

    @InjectParam
    private UUIDWebServiceUtil uuidWebServiceUtil;

    @Context
    protected UriInfo uriInfo;

    @Context
    protected ServletContext servletContext;

    @Context
    protected ResourceContext resourceContext;

    /**
     * get UUIDBrowser in JSON format
     *
     * @return MetadataSearchWS
     */
    @GET
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataSearchWS getUUIDBrowserJSON() {
        UUIDBrowserWSQueryParamBean queryParamBean =
                uuidWebServiceUtil.getQueryParams(resourceContext, uriInfo, MediaType.APPLICATION_JSON);
        return processSearchUUIDBrowserWS(queryParamBean);
    }

    /**
     * get UUIDBrowser in XML format
     *
     * @return MetadataSearchWS
     */
    @GET
    @Path("/xml")
    @Produces(MediaType.APPLICATION_XML)
    public MetadataSearchWS getUUIDBrowserXML() {
        final UUIDBrowserWSQueryParamBean queryParamBean =
                uuidWebServiceUtil.getQueryParams(resourceContext, uriInfo, MediaType.APPLICATION_XML);
        return processSearchUUIDBrowserWS(queryParamBean);
    }

    /**
     * get Metadata From Barcode in JSON format
     *
     * @param barcode
     * @return MetadataViewWS
     */
    @GET
    @Path("/json/barcode/{barcode:.+}")
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataViewWS getMetadataFromBarcodeJSON(@PathParam("barcode") final String barcode) {

        final String mediaType = MediaType.APPLICATION_JSON;
        final UUIDBrowserWSQueryParamBean queryParamBean = new UUIDBrowserWSQueryParamBean();
        queryParamBean.setBarcode(barcode);
        uuidWebServiceUtil.validate(queryParamBean, "barcode", mediaType);
        return processViewUUIDBrowserWS(mediaType, queryParamBean);
    }

    /**
     * get Metadata From Barcode in XML format
     *
     * @param barcode
     * @return MetadataViewWS
     */
    @GET
    @Path("/xml/barcode/{barcode:.+}")
    @Produces(MediaType.APPLICATION_XML)
    public MetadataViewWS getMetadataFromBarcodeXML(@PathParam("barcode") final String barcode) {

        final String mediaType = MediaType.APPLICATION_XML;
        final UUIDBrowserWSQueryParamBean queryParamBean = new UUIDBrowserWSQueryParamBean();
        queryParamBean.setBarcode(barcode);
        uuidWebServiceUtil.validate(queryParamBean, "barcode", mediaType);
        return processViewUUIDBrowserWS(mediaType, queryParamBean);
    }

    /**
     * get Metadata From Barcode in JSON format
     *
     * @param barcodes
     * @return MetadataViewWS
     */
    @GET
    @Path("/json/barcodeExists/{barcodes:.+}")
    @Produces(MediaType.APPLICATION_JSON)
    public BarcodeListWS getMetadataFromBarcodeExistsJSON(@PathParam("barcodes") final String barcodes) {
        return getBarcodesStatus(barcodes, MediaType.APPLICATION_JSON);
    }


    /**
     * get Metadata From Barcode in XML format
     *
     * @param barcodes
     * @return MetadataViewWS
     */
    @GET
    @Path("/xml/barcodeExists/{barcodes:.+}")
    @Produces(MediaType.APPLICATION_XML)
    public BarcodeListWS getMetadataFromBarcodeExistsXML(@PathParam("barcodes") final String barcodes) {
        return getBarcodesStatus(barcodes, MediaType.APPLICATION_XML);
    }

    private BarcodeListWS getBarcodesStatus(final String barcodes, final String mediaTypeString) {
        // get all barcodes
        final List<String> barcodesToValidate = Arrays.asList(barcodes.split(ConstantValues.WS_BARCODE_DELIMITER, -1));
        final List<String> barcodePassedFormatValidation = new ArrayList<String>();
        final List<BarcodeWS> barcodeList = new ArrayList<BarcodeWS>();
        final UUIDBrowserWSQueryParamBean queryParamBean = new UUIDBrowserWSQueryParamBean();
        // validate barcodes format
        for (final String barcode : barcodesToValidate) {
            queryParamBean.setBarcode(barcode);
            try {
                uuidWebServiceUtil.validate(queryParamBean, barcode, mediaTypeString);
                barcodePassedFormatValidation.add(barcode);
            } catch (WebApplicationException e) {
                BarcodeWS barcodeWS = new BarcodeWS();
                barcodeWS.setBarcode("Barcode: " + barcode + " has an invalid format");
                barcodeWS.setExists(false);
                barcodeList.add(barcodeWS);
            }
        }
        // get the existing barcodes from DB
        final List<String> existingBarcodes = uuidBrowserService.getExistingBarcodes(barcodePassedFormatValidation);

        // update the status of the barcodes
        for (final String barcode : barcodePassedFormatValidation) {
            BarcodeWS barcodeWS = new BarcodeWS();
            barcodeWS.setBarcode(barcode);
            barcodeWS.setExists((existingBarcodes.contains(barcode)) ? true : false);
            barcodeList.add(barcodeWS);
        }

        return buildBarcodeExistsViewWS(barcodeList);
    }

    /**
     * get Metadata From Uuid in JSON format
     *
     * @param uuid
     * @return MetadataViewWS
     */
    @GET
    @Path("/json/uuid/{uuid:.+}")
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataViewWS getMetadataFromUuidJSON(@PathParam("uuid") final String uuid) {

        final String mediaType = MediaType.APPLICATION_JSON;
        final UUIDBrowserWSQueryParamBean queryParamBean = new UUIDBrowserWSQueryParamBean();
        queryParamBean.setUuid(uuid);
        uuidWebServiceUtil.validate(queryParamBean, "uuid", mediaType);
        return processViewUUIDBrowserWS(mediaType, queryParamBean);
    }

    /**
     * get Metadata From Uuid in XML format
     *
     * @param uuid
     * @return MetadataViewWS
     */
    @GET
    @Path("/xml/uuid/{uuid:.+}")
    @Produces(MediaType.APPLICATION_XML)
    public MetadataViewWS getMetadataFromUuidXML(@PathParam("uuid") final String uuid) {

        final String mediaType = MediaType.APPLICATION_XML;
        final UUIDBrowserWSQueryParamBean queryParamBean = new UUIDBrowserWSQueryParamBean();
        queryParamBean.setUuid(uuid);
        uuidWebServiceUtil.validate(queryParamBean, "uuid", mediaType);
        return processViewUUIDBrowserWS(mediaType, queryParamBean);
    }

    /**
     * Retrieves UUID meta-data using the {@link ShippedBiospecimenQueries} resource and
     * produces a XML formatted response derived from an instance of the {@link MetaDataBean}.
     *
     * @param uuid - the UUID to retrieve meta-data for
     * @return MetaDataBean bean representing UUID meta-data formatted in XML
     */
    @GET
    @Path("/xml/uuid/shippedbiospecimen/{uuid:.+}")
    @Produces(MediaType.APPLICATION_XML)
    public MetaDataBean retrieveUUIDMetaDataXML(@PathParam("uuid") final String uuid) {

        final String mediaType = MediaType.APPLICATION_XML;
        final UUIDBrowserWSQueryParamBean queryParamBean = new UUIDBrowserWSQueryParamBean();
        queryParamBean.setUuid(uuid);
        uuidWebServiceUtil.validate(queryParamBean, "uuid", mediaType);
        return shippedBiospecimenQueries.retrieveUUIDMetadata(uuid);
    }

    /**
     * Retrieves UUID meta-data using the {@link ShippedBiospecimenQueries} resource and
     * produces a JSON formatted response derived from an instance of the {@link MetaDataBean}.
     *
     * @param uuid - the UUID to retrieve meta-data for
     * @return MetaDataBean bean representing UUID meta-data formatted in JSON
     */
    @GET
    @Path("/json/uuid/shippedbiospecimen/{uuid:.+}")
    @Produces(MediaType.APPLICATION_JSON)
    public MetaDataBean retrieveUUIDMetaDataJSON(@PathParam("uuid") final String uuid) {

        final String mediaType = MediaType.APPLICATION_JSON;
        final UUIDBrowserWSQueryParamBean queryParamBean = new UUIDBrowserWSQueryParamBean();
        queryParamBean.setUuid(uuid);
        uuidWebServiceUtil.validate(queryParamBean, "uuid", mediaType);
        return shippedBiospecimenQueries.retrieveUUIDMetadata(uuid);
    }

    /**
     * Retrieves UUID level using the {@link ShippedBiospecimenQueries} resource.
     *
     * @param uuid - the UUID
     * @return String representing the UUID level
     */
    @GET
    @Path("/uuid/uuidlevel/{uuid:.+}")
    @Produces(MediaType.TEXT_HTML)
    public String getUUIDLevelWS(@PathParam("uuid") final String uuid) {
        final UUIDBrowserWSQueryParamBean queryParamBean = new UUIDBrowserWSQueryParamBean();
        queryParamBean.setUuid(uuid);
        uuidWebServiceUtil.validate(queryParamBean, "uuid", MediaType.APPLICATION_JSON);
        return shippedBiospecimenQueries.getUUIDLevel(uuid);
    }

    /**
     * Retrieves disease abbreviation for UUID using the {@link ShippedBiospecimenQueries} resource.
     *
     * @param uuid - the UUID
     * @return String representing the disease abbreviation
     */
    @GET
    @Path("/uuid/uuiddisease/{uuid:.+}")
    @Produces(MediaType.TEXT_HTML)
    public String getDiseaseForUUIDWS(@PathParam("uuid") final String uuid) {
        final UUIDBrowserWSQueryParamBean queryParamBean = new UUIDBrowserWSQueryParamBean();
        queryParamBean.setUuid(uuid);
        uuidWebServiceUtil.validate(queryParamBean, "uuid", MediaType.APPLICATION_JSON);
        return shippedBiospecimenQueries.getDiseaseForUUID(uuid);
    }


    /**
     * process View UUIDBrowserWS
     *
     * @param mediaType the media type of the response
     * @return MetadataViewWS
     */
    protected MetadataViewWS processViewUUIDBrowserWS(String mediaType, final UUIDBrowserWSQueryParamBean queryParamBean) {
        final String uuid = queryParamBean.getUuid();
        final String barcode = queryParamBean.getBarcode();
        final List<String> uuidList = (List<String>) getUUIDAndBarcodeInfo(queryParamBean).get("uuidList");
        final Boolean allBarcode = (Boolean) getUUIDAndBarcodeInfo(queryParamBean).get("allBarcode");
        final Boolean hasBarcode = (Boolean) getUUIDAndBarcodeInfo(queryParamBean).get("hasBarcode");
        final List<BiospecimenMetaData> resList =
                uuidBrowserService.processMultipleBiospecimenMetadata(uuidList, hasBarcode, allBarcode);
        if (resList.size() > 0) {
            return buildMetadataViewWS(resList.get(0));
        } else {
            final int statusCode = HttpStatusCode.OK;
            Response response = null;

            if (uuid != null) {
                final String errorMessage = new StringBuilder("UUID '").append(uuid).append("' not found.").toString();
                response = WebServiceUtil.makeResponse(mediaType, statusCode, uuid, errorMessage);

            } else {
                final String errorMessage = new StringBuilder("Barcode '").append(barcode).append("' not found.").toString();
                response = WebServiceUtil.makeResponse(mediaType, statusCode, barcode, errorMessage);
            }

            throw new WebApplicationException(response);
        }
    }

    /**
     * build MetadataViewWS
     *
     * @param uuidBrowser
     * @return MetadataViewWS
     */
    protected MetadataViewWS buildMetadataViewWS(final BiospecimenMetaData uuidBrowser) {
        if (uuidBrowser == null) {
            return new MetadataViewWS();
        } else {
            final UUIDBrowserWS tcgaElement = new UUIDBrowserWS();
            processMetadataViewCommonPart(uuidBrowser, tcgaElement);
            processMetadataViewSpecialPart(uuidBrowser, tcgaElement);
            return new MetadataViewWS(tcgaElement);
        }
    }

    protected BarcodeListWS buildBarcodeExistsViewWS(final List<BarcodeWS> barcodeWSList) {
        if (barcodeWSList == null) {
            return new BarcodeListWS();
        } else {
            final BarcodeListWS tcgaElement = new BarcodeListWS();
            tcgaElement.setBarcodes(barcodeWSList);
            return tcgaElement;

        }
    }


    /**
     * process Search UUIDBrowserWS
     *
     * @return MetadataSearchWS
     */
    protected MetadataSearchWS processSearchUUIDBrowserWS(final UUIDBrowserWSQueryParamBean queryParamBean) {
        final UriBuilder ub = uriInfo.getAbsolutePathBuilder();
        final String path = ub.path("uuid/").build().toString();
        final List<BiospecimenMetaData> resList = new LinkedList<BiospecimenMetaData>();
        final List<String> uuidList = (List<String>) getUUIDAndBarcodeInfo(queryParamBean).get("uuidList");
        final Boolean allBarcode = (Boolean) getUUIDAndBarcodeInfo(queryParamBean).get("allBarcode");
        final Boolean hasBarcode = (Boolean) getUUIDAndBarcodeInfo(queryParamBean).get("hasBarcode");
        final List<BiospecimenMetaData> firstList =
                uuidBrowserService.processMultipleBiospecimenMetadata(uuidList, hasBarcode, allBarcode);
        final List<BiospecimenMetaData> secondList = uuidBrowserService.getBiospecimenMetadataList(firstList,
                uuidCommonService.makeListFromString(queryParamBean.getElementType()),
                uuidCommonService.makeListFromString(queryParamBean.getPlatform()),
                uuidCommonService.makeListFromString(queryParamBean.getParticipant()),
                uuidCommonService.makeListFromString(queryParamBean.getBatch()),
                uuidCommonService.makeListFromString(queryParamBean.getDisease()),
                resolveSampleType(uuidCommonService.makeListFromString(queryParamBean.getSampleType())),
                uuidCommonService.makeListFromString(queryParamBean.getVial()),
                uuidCommonService.makeListFromString(queryParamBean.getPortion()),
                resolveAnalyteType(uuidCommonService.makeListFromString(queryParamBean.getAnalyteType())),
                uuidCommonService.makeListFromString(queryParamBean.getPlate()),
                resolveBCR(uuidCommonService.makeListFromString(queryParamBean.getBcr())),
                uuidCommonService.makeListFromString(queryParamBean.getTss()),
                resolveCenter(uuidCommonService.makeListFromString(queryParamBean.getCenter())),
                uuidCommonService.makeListFromString(queryParamBean.getCenterType()),
                queryParamBean.getUpdatedAfter(),
                queryParamBean.getUpdatedBefore(),
                null,
                null,
                resolveSlideLayer(uuidCommonService.makeListFromString(queryParamBean.getSlideLayer())));
        for (final BiospecimenMetaData uuidBrowse : secondList) {
            resList.add(new BiospecimenMetaData((path + uuidBrowse.getUuid())));
        }
        return new MetadataSearchWS(resList);
    }

    /**
     * get UUID And Barcode Info
     *
     * @return map of string , object
     */
    protected Map<String, Object> getUUIDAndBarcodeInfo(final UUIDBrowserWSQueryParamBean queryParamBean) {
        final Map<String, Object> resMap = new LinkedHashMap<String, Object>();
        final String uuid = queryParamBean.getUuid();
        final String barcode = queryParamBean.getBarcode();
        final List<String> uuidList = new LinkedList<String>();
        if (StringUtils.isNotBlank(uuid)) {
            final String[] uuidTab = uuid.split(SEPARATOR);
            for (int i = 0; i < uuidTab.length; i++) {
                uuidList.add(uuidTab[i]);
            }
        }
        if (StringUtils.isNotBlank(barcode)) {
            final String[] barcodeTab = barcode.split(SEPARATOR);
            for (int i = 0; i < barcodeTab.length; i++) {
                uuidList.add(barcodeTab[i]);
            }
        }
        resMap.put("uuidList", uuidList);
        resMap.put("hasBarcode", StringUtils.isNotBlank(barcode));
        resMap.put("allBarcode", StringUtils.isNotBlank(barcode) & StringUtils.isBlank(uuid));
        return resMap;
    }

    /**
     * resolve AnalyteType
     *
     * @param analyteTypes
     * @return lisf of string
     */
    protected List<String> resolveAnalyteType(final List<String> analyteTypes) {
        final List<String> resList = new LinkedList<String>();
        if (analyteTypes != null && !analyteTypes.isEmpty()) {
            for (String analyteType : analyteTypes) {
                for (final PortionAnalyte analyte : uuidBrowserService.getPortionAnalytes()) {
                    if (analyteType.equalsIgnoreCase(analyte.getPortionAnalyteCode())) {
                        resList.add(analyte.getDefinition());
                    }
                }
            }

            return resList;
        } else {
            return null;
        }
    }

    /**
     * resolve SampleType
     *
     * @param sampleTypes
     * @return list of string
     */
    protected List<String> resolveSampleType(final List<String> sampleTypes) {
        final List<String> resList = new LinkedList<String>();
        if (sampleTypes != null && !sampleTypes.isEmpty()) {
            for (String sampleType : sampleTypes) {
                if (TUMOR.equalsIgnoreCase(sampleType)) {
                    processSampleTypes(resList, true, false, false);
                } else if (NORMAL.equalsIgnoreCase(sampleType)) {
                    processSampleTypes(resList, false, true, false);
                } else if (CELL_LINE.equalsIgnoreCase(sampleType)) {
                    processSampleTypes(resList, false, false, true);
                } else {
                    for (SampleType sample : uuidBrowserService.getSampleTypes()) {
                        if (sampleType.equalsIgnoreCase(sample.getSampleTypeCode()) ||
                                sampleType.equalsIgnoreCase(sample.getShortLetterCode())) {
                            resList.add(sample.getDefinition());
                        }
                    }
                }
            }

            return resList;
        } else {
            return null;
        }
    }

    /**
     * process SampleTypes
     *
     * @param resList
     * @param tumor
     * @param normal
     * @param cellLine
     */
    private void processSampleTypes(final List<String> resList, final boolean tumor, final boolean normal, final boolean cellLine) {

        if (tumor) {
            for (final SampleType sample : uuidBrowserService.getSampleTypes()) {
                if (sample.getIsTumor().booleanValue()) {
                    resList.add(sample.getDefinition());
                }
            }
        } else if (normal) {
            for (final SampleType sample : uuidBrowserService.getSampleTypes()) {
                if (!sample.getIsTumor().booleanValue() && !CELL_LINE_CONTROL.equals(sample.getDefinition())) {
                    resList.add(sample.getDefinition());
                }
            }
        } else if (cellLine) {
            for (final SampleType sample : uuidBrowserService.getSampleTypes()) {
                if (CELL_LINE_CONTROL.equals(sample.getDefinition())) {
                    resList.add(sample.getDefinition());
                }
            }
        }
    }

    /**
     * resolve SlideLayer
     *
     * @param slideLayers
     * @return list of string
     */
    protected List<String> resolveSlideLayer(final List<String> slideLayers) {
        final List<String> resList = new LinkedList<String>();
        if (slideLayers != null && !slideLayers.isEmpty()) {
            for (String slideLayer : slideLayers) {
                if ("T".equalsIgnoreCase(slideLayer) || TOP.equalsIgnoreCase(slideLayer)) {
                    resList.add(TOP);
                } else if ("M".equalsIgnoreCase(slideLayer) || MIDDLE.equalsIgnoreCase(slideLayer)) {
                    resList.add(MIDDLE);
                } else if ("B".equalsIgnoreCase(slideLayer) || BOTTOM.equalsIgnoreCase(slideLayer)) {
                    resList.add(BOTTOM);
                }
            }

            return resList;
        } else {
            return null;
        }
    }

    /**
     * resolve BCR
     *
     * @param bcrs
     * @return list of string
     */
    protected List<String> resolveBCR(final List<String> bcrs) {
        final List<String> resList = new LinkedList<String>();
        if (bcrs != null && !bcrs.isEmpty()) {
            for (final String bcr : bcrs) {
                for (final Center center : uuidBrowserService.getCenters()) {
                    if (BCR.equalsIgnoreCase(center.getCenterType()) &&
                            (bcr.equalsIgnoreCase(center.getShortName()) ||
                                    bcr.equalsIgnoreCase(center.getCenterName()))) {
                        resList.add(center.getCenterDisplayText());
                    }
                }
            }

            return resList;
        } else {
            return null;
        }
    }

    /**
     * resolve Center
     *
     * @param centers
     * @return lisf of string
     */
    protected List<String> resolveCenter(final List<String> centers) {
        final List<String> resList = new LinkedList<String>();
        if (centers != null && !centers.isEmpty()) {
            for (final String center : centers) {
                for (final Center c : uuidBrowserService.getCentersWithBCRCode()) {
                    if (center.equalsIgnoreCase(c.getShortName()) ||
                            center.equalsIgnoreCase(c.getCenterName())) {
                        resList.add(c.getCenterName());
                    } else if (center.equalsIgnoreCase(c.getBcrCenterId())) {
                        //Special handling for bcr center code
                        resList.add(c.getCenterName() + "~" + center);
                    }
                }
            }

            return resList;
        } else {
            return null;
        }
    }

    /**
     * get Slide From Layer
     *
     * @param layer
     * @return string
     */
    private String getSlideFromLayer(String layer) {
        if (layer == null) {
            return null;
        } else {
            return layer.substring(0, 1).toUpperCase();
        }
    }

    /**
     * process MetadataView Common Part
     *
     * @param uuidBrowser
     * @param tcgaElement
     */
    private void processMetadataViewCommonPart(final BiospecimenMetaData uuidBrowser, UUIDBrowserWS tcgaElement) {
        final Date updateDate = uuidBrowser.getUpdateDate();
        final Date createDate = uuidBrowser.getCreateDate();
        tcgaElement.setUuid(uuidBrowser.getUuid());
        tcgaElement.setRedacted(uuidBrowser.getRedacted());
        tcgaElement.setBatch(uuidBrowser.getBatch());
        tcgaElement.setElementType(uuidBrowser.getUuidType());
        tcgaElement.setLastUpdate(updateDate == null ? null : DATE_FORMAT_US.format(updateDate));
        tcgaElement.setBarcodes(new LinkedList<BarcodeUUIDWS>() {{
            add(new BarcodeUUIDWS(
                    createDate == null ? null : DATE_FORMAT_US.format(createDate), uuidBrowser.getBarcode()
            ));
        }});
        for (final Center b : uuidBrowserService.getCenters()) {
            if (b.getCenterDisplayText().equalsIgnoreCase(uuidBrowser.getBcr())) {
                tcgaElement.setBcr(new BCRUUIDWS(b.getShortName(), b.getCenterName(),
                        b.getCenterDisplayName()));
                break;
            }
        }
        for (final Tumor t : uuidBrowserService.getDiseases()) {
            if (t.getTumorName().equalsIgnoreCase(uuidBrowser.getDisease())) {
                tcgaElement.setDisease(new DiseaseUUIDWS(t.getTumorName(), t.getTumorDescription()));
                break;
            }
        }
        for (final TissueSourceSite tss : uuidBrowserService.getTissueSourceSites()) {
            if (tss.getTissueSourceSiteId().contains(uuidBrowser.getTissueSourceSite())) {
                tcgaElement.setTss(new TSSUUIDWS(uuidBrowser.getTissueSourceSite(), tss.getName()));
                break;
            }
        }
    }

    /**
     * manage Path
     *
     * @param uri
     * @return string
     */
    private String managePath(String uri) {
        if (uri != null) {
            if (uri.contains("barcode/")) {
                return uri.substring(0, uri.lastIndexOf("barcode/")) + "uuid/";
            } else {
                return uri.substring(0, uri.lastIndexOf("uuid/") + 5);
            }
        }
        return "";
    }

    /**
     * process Metadata View Special Part
     *
     * @param uuidBrowser
     * @param tcgaElement
     */
    private void processMetadataViewSpecialPart(final BiospecimenMetaData uuidBrowser, UUIDBrowserWS tcgaElement) {
        if (tcgaElement != null && tcgaElement.getElementType() != null) {
            final String uri = uriInfo.getAbsolutePathBuilder().build().toString();
            final String path = managePath(uri);
            final String elType = tcgaElement.getElementType();
            final List<BiospecimenMetaData> resList = uuidBrowserService.getAllBiospecimenMetadataParents(uuidBrowser.getUuid());
            if (PARTICIPANT.equalsIgnoreCase(elType)) {
                tcgaElement.setParticipant(new ParticipantUUIDWS(null, uuidBrowser.getParticipantId()));
                setParticipantChildren(tcgaElement, path, resList);
            } else if (DRUG.equalsIgnoreCase(elType)) {
                tcgaElement.setParticipant(new ParticipantUUIDWS(path + uuidBrowser.getParentUUID()));
                tcgaElement.setDrug(new DrugUUIDWS());
            } else if (EXAMINATION.equalsIgnoreCase(elType)) {
                tcgaElement.setParticipant(new ParticipantUUIDWS(path + uuidBrowser.getParentUUID()));
                tcgaElement.setExamination(new ExaminationUUIDWS());
            } else if (SURGERY.equalsIgnoreCase(elType)) {
                tcgaElement.setParticipant(new ParticipantUUIDWS(path + uuidBrowser.getParentUUID()));
                tcgaElement.setSurgery(new SurgeryUUIDWS());
            } else if (RADIATION.equalsIgnoreCase(elType)) {
                tcgaElement.setParticipant(new ParticipantUUIDWS(path + uuidBrowser.getParentUUID()));
                tcgaElement.setRadiation(new RadiationUUIDWS());
            } else if (SAMPLE.equalsIgnoreCase(elType)) {
                tcgaElement.setParticipant(new ParticipantUUIDWS(path + uuidBrowser.getParentUUID()));
                for (final SampleType sample : uuidBrowserService.getSampleTypes()) {
                    if (sample.getDefinition().equalsIgnoreCase(uuidBrowser.getSampleType())) {
                        tcgaElement.setSample(new LinkedList<SampleUUIDWS>() {{
                            add(new SampleUUIDWS(sample.getSampleTypeCode(),
                                    sample.getDefinition(), sample.getShortLetterCode(),
                                    uuidBrowser.getVialId()));
                        }});
                        break;
                    }
                }
                setSampleChildren(tcgaElement, path, uuidBrowserService.getSampleChildrenList(uuidBrowser.getUuid(), resList));
            } else if (PORTION.equalsIgnoreCase(elType)) {
                setPortionParents(uuidBrowser, tcgaElement, path, resList);
                tcgaElement.setPortion(new LinkedList<PortionUUIDWS>() {{
                    add(new PortionUUIDWS(null, uuidBrowser.getPortionId()));
                }});
                setPortionChildren(tcgaElement, path, uuidBrowserService.getPortionChildrenList(uuidBrowser.getUuid(), resList));
            } else if (SHIPPED_PORTION.equalsIgnoreCase(elType)) {
                final Date shippedDate = uuidBrowser.getShippedDate();
                setPortionParents(uuidBrowser, tcgaElement, path, resList);
                LinkedList<ShippedPortionUUIDWS> shippedPortions = new LinkedList<ShippedPortionUUIDWS>();
                for (final Center center : uuidBrowserService.getCentersWithBCRCode()) {
                    if (center.getCenterDisplayText().equalsIgnoreCase(uuidBrowser.getReceivingCenter()) &&
                            center.getBcrCenterId().equalsIgnoreCase(uuidBrowser.getCenterCode())) {
                        shippedPortions.add(new ShippedPortionUUIDWS(new CenterUUIDWS(center.getShortName(),
                                center.getCenterName(), center.getCenterDisplayName(), center.getBcrCenterId(),
                                center.getCenterType()), uuidBrowser.getPlatform(), uuidBrowser.getPlateId(),
                                null, uuidBrowser.getShipped(),
                                shippedDate == null ? null : DATE_FORMAT_US.format(shippedDate)));
                        break;
                    }
                }
                tcgaElement.setShippedPortion(shippedPortions);
            } else if (ANALYTE.equalsIgnoreCase(elType)) {
                setAnalyteParents(uuidBrowser, tcgaElement, path, resList);
                for (final PortionAnalyte portionAnalyte : uuidBrowserService.getPortionAnalytes()) {
                    if (portionAnalyte.getDefinition().equalsIgnoreCase(uuidBrowser.getAnalyteType())) {
                        tcgaElement.setAnalyte(new LinkedList<AnalyteUUIDWS>() {{
                            add(new AnalyteUUIDWS(portionAnalyte.getPortionAnalyteCode(),
                                    portionAnalyte.getDefinition()));
                        }});
                        break;
                    }
                }
                setAnalyteChildren(tcgaElement, path, uuidBrowserService.getAnalyteSlideChildrenList(uuidBrowser.getUuid(), resList));
            } else if (SLIDE.equalsIgnoreCase(elType)) {
                setAnalyteParents(uuidBrowser, tcgaElement, path, resList);
                tcgaElement.setSlide(new LinkedList<SlideUUIDWS>() {{
                    add(new SlideUUIDWS(getSlideFromLayer(uuidBrowser.getSlideLayer()),
                            uuidBrowser.getSlideLayer()));
                }});
            } else if (ALIQUOT.equalsIgnoreCase(elType)) {
                final Date shippedDate = uuidBrowser.getShippedDate();
                setAliquotParents(uuidBrowser, tcgaElement, path, resList);
                final List<AliquotUUIDWS> aliquots = new LinkedList<AliquotUUIDWS>();
                for (final Center center : uuidBrowserService.getCentersWithBCRCode()) {
                    if (center.getCenterDisplayText().equalsIgnoreCase(uuidBrowser.getReceivingCenter()) &&
                            center.getBcrCenterId().equalsIgnoreCase(uuidBrowser.getCenterCode())) {
                        aliquots.add(new AliquotUUIDWS(new CenterUUIDWS(center.getShortName(),
                                center.getCenterName(), center.getCenterDisplayName(), center.getBcrCenterId(),
                                center.getCenterType()), uuidBrowser.getPlatform(), uuidBrowser.getPlateId(),
                                null, uuidBrowser.getShipped(),
                                shippedDate == null ? null : DATE_FORMAT_US.format(shippedDate)));
                        break;
                    }
                }
                tcgaElement.setAliquot(aliquots);
            }
        }
    }

    /**
     * set Portion Parents
     *
     * @param uuidBrowser
     * @param tcgaElement
     * @param path
     * @param resList
     */
    private void setPortionParents(BiospecimenMetaData uuidBrowser, UUIDBrowserWS tcgaElement, final String path, List<BiospecimenMetaData> resList) {
        final String sampleUuid = uuidBrowser.getParentUUID();
        final String participantUuid = findUUIDBrowser(resList, sampleUuid).getParentUUID();
        tcgaElement.setParticipant(new ParticipantUUIDWS(path + participantUuid));
        tcgaElement.setSample(new LinkedList<SampleUUIDWS>() {{
            add(new SampleUUIDWS(path + sampleUuid));
        }});
    }

    /**
     * set Analyte Parents
     *
     * @param uuidBrowser
     * @param tcgaElement
     * @param path
     * @param resList
     */
    private void setAnalyteParents(BiospecimenMetaData uuidBrowser, UUIDBrowserWS tcgaElement, final String path, List<BiospecimenMetaData> resList) {
        final String portionUuid = uuidBrowser.getParentUUID();
        setPortionParents(findUUIDBrowser(resList, portionUuid), tcgaElement, path, resList);
        tcgaElement.setPortion(new LinkedList<PortionUUIDWS>() {{
            add(new PortionUUIDWS(path + portionUuid));
        }});
    }

    /**
     * set Aliquot Parents
     *
     * @param uuidBrowser
     * @param tcgaElement
     * @param path
     * @param resList
     */
    private void setAliquotParents(BiospecimenMetaData uuidBrowser, UUIDBrowserWS tcgaElement, final String path, List<BiospecimenMetaData> resList) {
        final String analyteUuid = uuidBrowser.getParentUUID();
        setAnalyteParents(findUUIDBrowser(resList, analyteUuid), tcgaElement, path, resList);
        tcgaElement.setAnalyte(new LinkedList<AnalyteUUIDWS>() {{
            add(new AnalyteUUIDWS(path + analyteUuid));
        }});
    }

    /**
     * find UUIDBrowser
     *
     * @param resList
     * @param uuid
     * @return UUIDBrowser
     */
    private BiospecimenMetaData findUUIDBrowser(List<BiospecimenMetaData> resList, String uuid) {
        for (final BiospecimenMetaData uuidTmp : resList) {
            if (uuidTmp.getUuid().equalsIgnoreCase(uuid)) {
                return uuidTmp;
            }
        }
        return null;
    }

    /**
     * set Analyte Children
     *
     * @param tcgaElement
     * @param path
     * @param resList
     */
    private void setAnalyteChildren(UUIDBrowserWS tcgaElement, String path, List<BiospecimenMetaData> resList) {
        List<SlideUUIDWS> slideList = new LinkedList<SlideUUIDWS>();
        List<AliquotUUIDWS> aliquotList = new LinkedList<AliquotUUIDWS>();
        for (final BiospecimenMetaData uuid : resList) {
            final String type = uuid.getUuidType();
            final String uuidPath = path + uuid.getUuid();
            if (SLIDE.equalsIgnoreCase(type)) {
                slideList.add(new SlideUUIDWS(uuidPath));
            } else if (ALIQUOT.equalsIgnoreCase(type)) {
                aliquotList.add(new AliquotUUIDWS(uuidPath));
            }
        }
        tcgaElement.setSlide(slideList);
        tcgaElement.setAliquot(aliquotList);
    }

    /**
     * set Portion Children
     *
     * @param tcgaElement
     * @param path
     * @param resList
     */
    private void setPortionChildren(UUIDBrowserWS tcgaElement, String path, List<BiospecimenMetaData> resList) {
        List<AnalyteUUIDWS> analyteList = new LinkedList<AnalyteUUIDWS>();
        List<SlideUUIDWS> slideList = new LinkedList<SlideUUIDWS>();
        List<AliquotUUIDWS> aliquotList = new LinkedList<AliquotUUIDWS>();
        for (final BiospecimenMetaData uuid : resList) {
            final String type = uuid.getUuidType();
            final String uuidPath = path + uuid.getUuid();
            if (ANALYTE.equalsIgnoreCase(type)) {
                analyteList.add(new AnalyteUUIDWS(uuidPath));
            } else if (SLIDE.equalsIgnoreCase(type)) {
                slideList.add(new SlideUUIDWS(uuidPath));
            } else if (ALIQUOT.equalsIgnoreCase(type)) {
                aliquotList.add(new AliquotUUIDWS(uuidPath));
            }
        }
        tcgaElement.setAnalyte(analyteList);
        tcgaElement.setSlide(slideList);
        tcgaElement.setAliquot(aliquotList);
    }

    /**
     * set Sample Children
     *
     * @param tcgaElement
     * @param path
     * @param resList
     */
    private void setSampleChildren(UUIDBrowserWS tcgaElement, String path, List<BiospecimenMetaData> resList) {
        List<PortionUUIDWS> portionList = new LinkedList<PortionUUIDWS>();
        List<ShippedPortionUUIDWS> shippedPortionList = new LinkedList<ShippedPortionUUIDWS>();
        List<AnalyteUUIDWS> analyteList = new LinkedList<AnalyteUUIDWS>();
        List<SlideUUIDWS> slideList = new LinkedList<SlideUUIDWS>();
        List<AliquotUUIDWS> aliquotList = new LinkedList<AliquotUUIDWS>();
        for (final BiospecimenMetaData uuid : resList) {
            final String type = uuid.getUuidType();
            final String uuidPath = path + uuid.getUuid();
            if (PORTION.equalsIgnoreCase(type)) {
                portionList.add(new PortionUUIDWS(uuidPath, null));
            } else if (SHIPPED_PORTION.equalsIgnoreCase(type)) {
                shippedPortionList.add(new ShippedPortionUUIDWS(uuidPath));
            } else if (ANALYTE.equalsIgnoreCase(type)) {
                analyteList.add(new AnalyteUUIDWS(uuidPath));
            } else if (SLIDE.equalsIgnoreCase(type)) {
                slideList.add(new SlideUUIDWS(uuidPath));
            } else if (ALIQUOT.equalsIgnoreCase(type)) {
                aliquotList.add(new AliquotUUIDWS(uuidPath));
            }
        }
        tcgaElement.setPortion(portionList);
        tcgaElement.setShippedPortion(shippedPortionList);
        tcgaElement.setAnalyte(analyteList);
        tcgaElement.setSlide(slideList);
        tcgaElement.setAliquot(aliquotList);
    }

    /**
     * set Participant Children
     *
     * @param tcgaElement
     * @param path
     * @param resList
     */
    private void setParticipantChildren(UUIDBrowserWS tcgaElement, String path, List<BiospecimenMetaData> resList) {
        List<SampleUUIDWS> sampleList = new LinkedList<SampleUUIDWS>();
        List<PortionUUIDWS> portionList = new LinkedList<PortionUUIDWS>();
        List<ShippedPortionUUIDWS> shippedPortionList = new LinkedList<ShippedPortionUUIDWS>();
        List<AnalyteUUIDWS> analyteList = new LinkedList<AnalyteUUIDWS>();
        List<SlideUUIDWS> slideList = new LinkedList<SlideUUIDWS>();
        List<AliquotUUIDWS> aliquotList = new LinkedList<AliquotUUIDWS>();
        for (final BiospecimenMetaData uuid : resList) {
            final String type = uuid.getUuidType();
            final String uuidPath = path + uuid.getUuid();
            if (SAMPLE.equalsIgnoreCase(type)) {
                sampleList.add(new SampleUUIDWS(uuidPath));
            } else if (PORTION.equalsIgnoreCase(type)) {
                portionList.add(new PortionUUIDWS(uuidPath, null));
            } else if (SHIPPED_PORTION.equalsIgnoreCase(type)) {
                shippedPortionList.add(new ShippedPortionUUIDWS(uuidPath));
            } else if (ANALYTE.equalsIgnoreCase(type)) {
                analyteList.add(new AnalyteUUIDWS(uuidPath));
            } else if (SLIDE.equalsIgnoreCase(type)) {
                slideList.add(new SlideUUIDWS(uuidPath));
            } else if (ALIQUOT.equalsIgnoreCase(type)) {
                aliquotList.add(new AliquotUUIDWS(uuidPath));
            }
        }
        tcgaElement.setSample(sampleList);
        tcgaElement.setPortion(portionList);
        tcgaElement.setShippedPortion(shippedPortionList);
        tcgaElement.setAnalyte(analyteList);
        tcgaElement.setSlide(slideList);
        tcgaElement.setAliquot(aliquotList);
    }

}//End of Class
