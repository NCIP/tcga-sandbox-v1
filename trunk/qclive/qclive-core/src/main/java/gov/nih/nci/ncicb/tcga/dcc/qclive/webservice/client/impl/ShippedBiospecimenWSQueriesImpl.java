/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.client.impl;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.MetaDataBean;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ShippedBiospecimen;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ShippedBiospecimenElement;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ShippedBiospecimenQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidatorImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.Logger;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Level;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the {@link ShippedBiospecimenQueries} interface that provides remote access
 * to functionality provided by the {@link gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.ShippedBiospecimenQueriesJDBCImpl}
 * class via web services.
 *
 * @author nichollsmc
 */
public class ShippedBiospecimenWSQueriesImpl implements ShippedBiospecimenQueries {

    private static final Integer WEBSERVICE_CALL_SLEEP_DELAY = 200;

    private Logger logger;
    private String baseUUIDMetaDataURI;
    private QcContext qcContext;
    private boolean useRemoteService;

    @Override
    public void addShippedBiospecimens(final List<ShippedBiospecimen> shippedBiospecimens) {
        handleWarningMessage("No implementation of method addShippedBiospecimens provided, doing nothing.");
    }

    @Override
    public void addShippedBiospecimens(final List<ShippedBiospecimen> shippedBiospecimens, final Integer shippedItemId) {
        handleWarningMessage("No implementation of method addShippedBiospecimens provided, doing nothing.");
    }

    @Override
    public void addShippedBiospecimenElements(final List<ShippedBiospecimenElement> shippedBiospecimenElements) {
        handleWarningMessage("No implementation of method addShippedBiospecimenElements provided, doing nothing.");
    }

    @Override
    public Map<String, Integer> getShippedElementsType() {
        handleWarningMessage("No implementation of method getShippedElementsType provided, returning empty map as default.");
        return new HashMap<String, Integer>();
    }

    @Override
    public Integer getShippedItemId(final String shippedItemType) {
        handleWarningMessage("No implementation of method getShippedItemId provided, returning default value -1.");
        return -1;
    }

    @Override
    public Long getShippedBiospecimenId(final String UUID) {
        handleWarningMessage("No implementation of method getShippedBiospecimenId provided, returning default value -1.");
        return -1L;
    }

    @Override
    public List<Long> getShippedBiospecimenIds(final List<String> uuids) {
        handleWarningMessage("No implementation of method getShippedBiospecimenIds provided, returning empty list as default.");
        return new ArrayList<Long>();
    }

    @Override
    public Long getShippedBiospecimenElementId(final Long shippedBiospecimenId, final Integer elementTypeId) {
        handleWarningMessage("No implementation of method getShippedBiospecimenElementId provided, returning default value -1.");
        return -1L;
    }

    @Override
    public Long getShippedBiospecimenIdForUUID(final String uuid) {
        handleWarningMessage("No implementation of method getShippedBiospecimenIdForUUID provided, returning default value -1.");
        return -1L;
    }

    @Override
    public void addFileRelationship(final Long biospecimenId, final Long fileId) {
        handleWarningMessage("No implementation of method addFileRelationship provided, doing nothing.");
    }

    @Override
    public Boolean isShippedBiospecimenShippedPortionUUIDValid(final String uuid) {
        // Default is true so that calling this method from Soundcheck or any other remote
        // client will pass validation until a web service implementation is provided
        handleWarningMessage("No implementation of method isShippedBiospecimenShippedPortionUUIDValid provided, returning default value true.");
        return true;
    }

    @Override
    public void addFileRelationships(final List<Long> biospecimenIds, final Long fileId) {
        handleWarningMessage("No implementation of method addFileRelationships provided, doing nothing.");
    }

    @Override
    public List<String> getRedactedParticipants(final Collection<String> participantCodeList) {
        handleWarningMessage("No implementation of method getRedactedParticipants provided, returning empty list as default.");
        return new ArrayList<String>();
    }

    @Override
    public void addArchiveRelationship(final Long biospecimenId, final Long archiveId) {
        handleWarningMessage("No implementation of method addArchiveRelationship provided, doing nothing.");
    }

    @Override
    public void addArchiveRelationships(final List<Long> biospecimenIds, final Long archiveId) {
        handleWarningMessage("No implementation of method addArchiveRelationships provided, doing nothing.");
    }

    /**
     * Web service implementation of the
     * {@link gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.ShippedBiospecimenQueriesJDBCImpl#retrieveUUIDMetadata(String)} method.
     * <p/>
     * The specific web service URI end-point used by this method is <tt>http(s)://hostname/metadata/json/uuid/shippedbiospecimen</tt>,
     * where <tt>hostname</tt> is the name of the target server.
     */
    @Override
    public MetaDataBean retrieveUUIDMetadata(final String uuid) {
        MetaDataBean uuidMetaDataBean = null;

        // if the uuid isn't even valid format don't try the webservice call
        if (CommonBarcodeAndUUIDValidatorImpl.UUID_PATTERN.matcher(uuid).matches()) {
            try {
                Thread.sleep(WEBSERVICE_CALL_SLEEP_DELAY);
            } catch (InterruptedException e) {

            }

            if (useRemoteService) {
                WebResource uuidMetaDataJSONResource = null;
                ClientResponse clientResponse = null;
                try {
                    uuidMetaDataJSONResource = getJSONEnableResource(baseUUIDMetaDataURI);
                    clientResponse = uuidMetaDataJSONResource
                            .path("json")
                            .path("uuid")
                            .path("shippedbiospecimen")
                            .path(uuid)
                            .get(ClientResponse.class);

                    uuidMetaDataBean = handleClientResponse(clientResponse, MetaDataBean.class);
                } catch (UniformInterfaceException uie) {
                    handleErrorMessage(
                            "An error occured while attempting to retrieve meta-data for UUID '" + uuid +
                                    "': Error message was: " + uie.getMessage());
                }
            } else {
                uuidMetaDataBean = new MetaDataBean();
            }

        }
        return uuidMetaDataBean;
    }

    @Override
    public String getUUIDLevel(final String uuid) {
        // if the uuid isn't even valid format don't try the webservice call
        if (CommonBarcodeAndUUIDValidatorImpl.UUID_PATTERN.matcher(uuid).matches()) {
            try {
                try {
                    Thread.sleep(WEBSERVICE_CALL_SLEEP_DELAY);
                } catch (InterruptedException e) {

                }
                final WebResource webResource = Client.create().resource(baseUUIDMetaDataURI);
                final ClientResponse clientResponse = webResource.path("uuid").path("uuidlevel")
                        .path(uuid).get(ClientResponse.class);
                return handleClientResponse(clientResponse, String.class);
            } catch (UniformInterfaceException uie) {
                handleErrorMessage(
                        "An error occured while attempting to retrieve uuid level for UUID '" + uuid +
                                "': Error message was: " + uie.getMessage());
            }
        }
        return null;
    }

    @Override
    public String getDiseaseForUUID(final String uuid) {
        // if the uuid isn't even valid format don't try the webservice call
        if (CommonBarcodeAndUUIDValidatorImpl.UUID_PATTERN.matcher(uuid).matches()) {
            try {
                try {
                    Thread.sleep(WEBSERVICE_CALL_SLEEP_DELAY);
                } catch (InterruptedException e) {

                }
                final WebResource webResource = Client.create().resource(baseUUIDMetaDataURI);
                final ClientResponse clientResponse = webResource.path("uuid").path("uuiddisease")
                        .path(uuid.toLowerCase()).get(ClientResponse.class);
                return handleClientResponse(clientResponse, String.class);
            } catch (UniformInterfaceException uie) {
                handleErrorMessage(
                        "An error occured while attempting to retrieve uuid disease for UUID '" + uuid +
                                "': Error message was: " + uie.getMessage());
            }
        }
        return null;
    }

    private WebResource getJSONEnableResource(final String baseURI) {
        final ClientConfig clientConfig = new DefaultClientConfig();
        final WebResource jsonEnabledWebResource = Client.create(clientConfig).resource(baseURI);
        jsonEnabledWebResource.accept(MediaType.APPLICATION_JSON_TYPE);

        return jsonEnabledWebResource;
    }

    private <T> T handleClientResponse(final ClientResponse clientResponse, final Class<T> responseType) {
        T response = null;
        int responseStatus = clientResponse.getStatus();
        StringBuilder errorMessage = null;
        if (HttpStatus.SC_OK == responseStatus) {
            response = clientResponse.getEntity(responseType);
        } else if (HttpStatus.SC_UNPROCESSABLE_ENTITY == responseStatus) {
            // this actually means the entity was not found which might
            // be a valid response, so just return null, don't assume it's an error
            // HttpStatus 400
        } else if (HttpStatus.SC_BAD_REQUEST == responseStatus) {
            errorMessage = new StringBuilder()
                    .append("Request returned HTTP response status of '")
                    .append(responseStatus)
                    .append(" - ")
                    .append(HttpStatus.getStatusText(responseStatus))
                    .append("'. Check the request and try again.");
            // HttpStatus 413
        } else if (HttpStatus.SC_REQUEST_TOO_LONG == responseStatus) {
            errorMessage = new StringBuilder()
                    .append("Request returned HTTP response status of '")
                    .append(responseStatus)
                    .append(" - ")
                    .append(HttpStatus.getStatusText(responseStatus))
                    .append("'. The number of requests to this web service has exceeded the DCC limits.")
                    .append(" You should reduce the number of concurrent instances of the stand-alone validator running or use the -noremote flag.");
            if (qcContext != null) {
                qcContext.addError(errorMessage.toString());
            }
            throw new RuntimeException(errorMessage.toString());
        }

        if (errorMessage != null) {
            handleErrorMessage(errorMessage.toString());
        }

        return response;
    }

    private void handleErrorMessage(final String errorMessage) {
        if (qcContext != null) {
            qcContext.addError(errorMessage);
        } else {
            throw new RuntimeException(errorMessage);
        }
    }

    private void handleWarningMessage(final String warningMessage) {
        if (qcContext != null) {
            qcContext.addWarning(warningMessage);
        } else if (logger != null) {
            logger.log(Level.WARN, warningMessage);
        } else {
            final StringBuilder errorMessage = new StringBuilder()
                    .append("Could not handle warning message '")
                    .append(warningMessage)
                    .append("' because both the QcContext and Logger references are null.");

            throw new RuntimeException(errorMessage.toString());
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(final Logger logger) {
        this.logger = logger;
    }

    public String getBaseUUIDMetaDataURI() {
        return baseUUIDMetaDataURI;
    }

    public void setBaseUUIDMetaDataURI(final String baseUUIDMetaDataURI) {
        this.baseUUIDMetaDataURI = baseUUIDMetaDataURI;
    }

    public QcContext getQcContext() {
        return qcContext;
    }

    public void setQcContext(final QcContext qcContext) {
        this.qcContext = qcContext;
    }

    public boolean canUseRemoteService() {
        return useRemoteService;
    }

    public void setUseRemoteService(final boolean useRemoteService) {
        this.useRemoteService = useRemoteService;
    }
}
