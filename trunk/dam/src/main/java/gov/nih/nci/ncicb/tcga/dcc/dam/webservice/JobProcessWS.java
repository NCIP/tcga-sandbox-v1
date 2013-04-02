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
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.HttpStatusCode;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.WebServiceUtil;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DAMWebServiceJobStatus;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.FilePackagerBean;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.JobProcess;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobHistory;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.processors.FilePackagerFactoryI;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.DataAccessMatrixJSPUtil;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMFacade;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMFacadeI;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMModel;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.StaticMatrixModelFactoryI;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.FilterRequest;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.FilterRequestI;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.SelectionRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerException;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Job Process jersey Web Service returning a JobProcess object serialized in xml or json
 * according to the request. Default jersey Scope is request, meaning a new instance/destroy of this class
 * and all of its member per requests.
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Path("/jobprocess")
public class JobProcessWS {

    @Context
    protected UriInfo uriInfo;

    @Context
    protected ServletContext servletContext;

    @InjectParam("wsFpFactory")
    protected FilePackagerFactoryI fpFactory;

    @InjectParam
    protected DAMWSUtil damWSUtil;

    @InjectParam
    protected StaticMatrixModelFactoryI staticMatrixModelFactory;

    @InjectParam("dataAccessMatrixQueries")
    protected DataAccessMatrixQueries dataAccessMatrixQueries;

    @QueryParam("email")
    protected String email;

    @QueryParam("disease")
    protected String disease;

    @QueryParam("center")
    protected String center;

    @QueryParam("platform")
    protected String platform;

    @QueryParam("platformType")
    protected String platformType;  // Sanity check: this should really be named dataType
    // but has been named that way to be consistent with the web app ...

    @QueryParam("level")
    protected String level;

    @QueryParam("batch")
    protected String batch;

    @QueryParam("sampleList")
    protected String sampleList;

    @QueryParam("availability")
    protected String availability;

    @QueryParam("noMeta")
    protected boolean noMeta;

    @QueryParam("protectedStatus")
    protected String protectedStatus;

    @QueryParam("tumorNormal")
    protected String tumorNormal;

    @QueryParam("startDate")
    protected String startDate;

    @QueryParam("endDate")
    protected String endDate;

    @QueryParam("consolidateFiles")
    @DefaultValue("true")
    protected boolean consolidateFiles;

    @QueryParam("flattenDir")
    @DefaultValue("false")
    protected boolean flattenDir;

    protected final Log logger = LogFactory.getLog(getClass());
    protected JobProcess jp = new JobProcess();
    protected DAMWebServiceJobStatus js;

    @GET
    @Path("/json{ticket:(/ticket/[a-fA-F0-9-]+)?}")
    @Produces(MediaType.APPLICATION_JSON)
    public JobProcess processJobToJson(@PathParam("ticket") final String ticket) {
        return getJobProcessResource(ticket);
    }

    @GET
    @Path("/xml{ticket:(/ticket/[a-fA-F0-9-]+)?}")
    @Produces(MediaType.APPLICATION_XML)
    public JobProcess processJobToXml(@PathParam("ticket") final String ticket) {
        return getJobProcessResource(ticket);
    }

    protected JobProcess getJobProcessResource(final String ticket) {
        js = new DAMWebServiceJobStatus("201", HttpStatusCode.getMessageForHttpStatusCode(HttpStatusCode.CREATED));
        if (ticket != null && ticket.length() > 0) {
            return checkStatusOfJobProcess(ticket);
        } else {
            return executeJobProcess();
        }
    }

    /**
     * Return the <code>JobProcess</code> with the given ticket.
     *
     * @param ticket the <code>JobProcess</code> ticket (must match UUID format)
     * @return the <code>JobProcess</code> with the given ticket
     */
    protected JobProcess checkStatusOfJobProcess(final String ticket) {

        logger.debug(ticket);

        final UUID key = getTicketUUID(ticket);
        final QuartzJobHistory quartzJobHistory = fpFactory.getQuartzJobHistory(key);

        if (quartzJobHistory != null) {

            if (quartzJobHistory.isAccepted()) {

                js.setStatusCode("202");
                js.setStatusMessage(HttpStatusCode.getMessageForHttpStatusCode(HttpStatusCode.ACCEPTED));

            } else if (quartzJobHistory.isSucceeded()) {

                logger.debug(quartzJobHistory.getLinkText());
                js.setStatusCode("200");
                js.setStatusMessage(HttpStatusCode.getMessageForHttpStatusCode(HttpStatusCode.OK));
                js.setArchiveUrl(quartzJobHistory.getLinkText());

            } else if (quartzJobHistory.isFailed()) {

                logger.debug(quartzJobHistory.getLinkText());
                throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.INTERNAL_SERVER_ERROR, "File Packager Failed"));
            }

        } else {

            throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.INTERNAL_SERVER_ERROR,
                    new StringBuilder("The file packager for the ticket '")
                            .append(key)
                            .append("' is unknown. ")
                            .append("This ticket has either never existed or the archive has expired and can not be downloaded anymore.").toString())
            );
        }

        return processJob(jp, js, quartzJobHistory, false);
    }

    /**
     * Parse the given ticket for its <code>UUID</code> and return it.
     *
     * @param ticket the ticket to parse
     * @return the <code>UUID</code>
     */
    private UUID getTicketUUID(String ticket) {

        final String key = ticket.substring(ticket.lastIndexOf("t/") + 2);
        try {
            return UUID.fromString(key);

        } catch (final IllegalArgumentException e) {

            throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.INTERNAL_SERVER_ERROR,
                    new StringBuilder("The file packager for the ticket '")
                            .append(key)
                            .append("' is invalid. It is not a properly formed ticket.").toString())
            );
        }
    }

    protected FilterRequest bindQueryParam() throws ParseException {

        final WSFilterAdapter filter = new WSFilterAdapter();
        filter.setMode(FilterRequestI.Mode.ApplyFilter);

        //this change is required to process case for platformType=c (for clinical)
        //user would enter url?platformType=c and it will be translated to -999 (pseudo platform type for nonplatformType=clinical)
        //Note: that platformType should be set first as it is used in various preconditions for mandatory fields
        if (StringUtils.equalsIgnoreCase(platformType, NonplatformType.NONPLATFORMTYPE_CLINICAL.getNonplatformTypeIdentifier())) {
            filter.setPlatformType(NonplatformType.NONPLATFORMTYPE_CLINICAL.getAssociatedPseudoPlatformType());//Note: this is non mandatory field
        } else {
            filter.setPlatformType(platformType);//Note: this is non mandatory field
        }

        //Mandatory fields
        filter.setDiseaseType(disease);
        filter.setPlatform(platform);
        filter.setCenter(center); //Warning: center must be set *AFTER* platform so that we can determine the correct center based on the platform's center type
        filter.setLevel(level);

        //Optional fields
        filter.setAvailability(availability);
        filter.setBatch(batch);
        filter.setEndDate(endDate);

        filter.setProtectedStatus(protectedStatus);
        filter.setSampleList(sampleList);
        filter.setStartDate(startDate);
        filter.setTumorNormal(tumorNormal);

        DiseaseContextHolder.setDisease(disease);
        return filter;
    }

    /**
     * Execute a <code>JobProcess</code> with the current filter
     *
     * @return a <code>JobProcess</code> with the current filter
     */
    protected JobProcess executeJobProcess() {
        try {
            //in case no user has hit the DAM to load the lookup maps
            if (servletContext != null) {
                DataAccessMatrixJSPUtil.storeLookups(servletContext);
            }
            List<DataFile> fileInfos;
            FilterRequest filterRequest = bindQueryParam();
            fileInfos = getFileInfoForRequest(filterRequest, noMeta);

            damWSUtil.checkTotalSize(fileInfos, damWSUtil.getSizeLimitGigs());

            boolean isProtected = damWSUtil.isDownloadingProtected(fileInfos);
            final UUID ticket = UUID.randomUUID();

            final FilePackagerBean filePackagerBean = fpFactory.createFilePackagerBean(disease,
                    fileInfos, email, flattenDir, isProtected, ticket, filterRequest);
            filePackagerBean.setArchivePhysicalPathPrefix(damWSUtil.getArchivePhysicalPathPrefix());
            filePackagerBean.setArchiveLinkSite(damWSUtil.getDownloadLinkSite());
            filePackagerBean.setJobWSSubmissionDate(new Date());
            if (filePackagerBean.getEstimatedUncompressedSize() == 0L) {
                throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.NO_CONTENT, "Archive has no content."));
            }
            fpFactory.enqueueFilePackagerBean(filePackagerBean);
            jp = processJob(jp, js, filePackagerBean.getUpdatedQuartzJobHistory(), true);
        } catch (DataAccessMatrixQueries.DAMQueriesException e) {
            logger.debug(e);
            throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.INTERNAL_SERVER_ERROR, "Error with DAM Queries"));
        } catch (SchedulerException e) {
            logger.debug(e);
            throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.INTERNAL_SERVER_ERROR, "Error with Scheduler"));
        } catch (ParseException e) {
            logger.debug(e);
            throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.INTERNAL_SERVER_ERROR, "Date Invalid"));
        }
        return jp;
    }

    /**
     * Return the given <code>JobProcess</code> after updating it.
     *
     * @param jobProcess       the <code>JobProcess</code> to update
     * @param jobStatus        the <code>JobProcess</code> new Status to be set
     * @param quartzJobHistory the <code>QuartzJobHistory</code> (a lightweight <code>FilePackagerBean</code>) with which to update the <code>JobProcess</code>
     * @param isNew            <code>true<code> if this is a new <code>JobProcess</code>, <code>false</code> otherwise
     * @return the given <code>JobProcess</code> after updating it
     */
    protected JobProcess processJob(final JobProcess jobProcess, final DAMWebServiceJobStatus jobStatus, final QuartzJobHistory quartzJobHistory, final boolean isNew) {

        jobProcess.setTicket(quartzJobHistory.getKey());
        jobProcess.setEstimatedSize(quartzJobHistory.getEstimatedUncompressedSize());
        jobProcess.setSubmissionTime(quartzJobHistory.getJobWSSubmissionDate());

        final UriBuilder ub = uriInfo.getAbsolutePathBuilder();
        final URI statusUri;

        if (isNew) {
            statusUri = ub.path("ticket/" + jobProcess.getTicket()).build();
        } else {
            statusUri = ub.build();
        }

        jobProcess.setStatusCheckUrl(statusUri.toString());
        jobProcess.setJobStatus(jobStatus);

        return jobProcess;
    }

    protected List<DataFile> getFileInfoForRequest(final FilterRequest filterRequest, final boolean noMeta)
            throws DataAccessMatrixQueries.DAMQueriesException {

        filterRequest.setMode(FilterRequest.Mode.ApplyFilter);
        DAMModel staticModel = staticMatrixModelFactory.getOrMakeModel(filterRequest.getDiseaseType(), false);
        DAMFacadeI facadeI = new DAMFacade(staticModel);
        facadeI.setFilter(filterRequest);

        SelectionRequest selreq = new SelectionRequest();
        selreq.setMode(SelectionRequest.MODE_SELECTALL);
        facadeI.setSelection(selreq);

        List<DataSet> selectedDataSets = facadeI.getSelectedDataSets();
        List<DataFile> fileInfos = dataAccessMatrixQueries.getFileInfoForSelectedDataSets(selectedDataSets, consolidateFiles);
        if (noMeta) {
            fileInfos = damWSUtil.removeMetadata(fileInfos);
        }
        return fileInfos;
    }

}//End of Class
