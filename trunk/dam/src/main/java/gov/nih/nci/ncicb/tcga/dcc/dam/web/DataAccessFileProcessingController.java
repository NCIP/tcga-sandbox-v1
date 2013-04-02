/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.web;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.FilePackagerBean;
import gov.nih.nci.ncicb.tcga.dcc.dam.processors.CachedOutputManager;
import gov.nih.nci.ncicb.tcga.dcc.dam.processors.FilePackagerFactoryI;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.DAMJobStatusService;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAFPViewItems;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMFacade;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMFacadeI;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DataAccessDownloadModel;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.ErrorInfo;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.DAFPRequest;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.FilterRequestI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerException;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

/**
 * Author: David Nassau
 * <p/>
 * Gets going the actual process of tarring/zipping the archive.  It communicates
 * by http to a secondary server, where the archive will be created.
 */
public class DataAccessFileProcessingController extends WebController {
    protected final Log logger = LogFactory.getLog(getClass());

    private String archivePhysicalPathPrefix, linkSite;
    private FilePackagerFactoryI filePackagerFactory;
    private DAMJobStatusService jobStatusService;
    private String statusCheckUrl;

    public void setArchivePhysicalPathPrefix(final String archivePhysicalPathPrefix) {
        this.archivePhysicalPathPrefix = archivePhysicalPathPrefix;
    }

    public void setLinkSite(final String value) {
        this.linkSite = value;
    }

    protected ModelAndView handle(
            final HttpServletRequest request, final HttpServletResponse response, final Object command,
            final BindException errors ) {
        ModelAndView modelAndView;
        try {
            final DataAccessDownloadModel dadModel = (DataAccessDownloadModel) request.getSession().getAttribute( "dadModel" );
            if(dadModel == null) {
                return new ModelAndView("sessionError");
            }
            final DAFPRequest dafpRequest = (DAFPRequest) command;
            dafpRequest.validate();
            setDiseaseForDataSource(dadModel.getDiseaseType());

            final List<DataFile> selectedFiles = dadModel.getFileInfoForSelectedTreeNodes( dafpRequest.getTreeNodeIds() );
            final UUID filePackagerKey = UUID.randomUUID();

            //detect whether any of these files should get the "special" treatment of a pre-generated cached file
            //We need to do it here, before sending over the wire to FP, because we can't assume the FP
            //as a populated DAMStaticMatrix.
            CachedOutputManager.addCachedFileNames( dadModel.getDiseaseType(), selectedFiles );
            final DAMFacadeI facadeI = (DAMFacadeI) request.getSession().getAttribute(DAMFacade.FACADE_KEY_NAME);
            final FilterRequestI filterRequest = facadeI.getPreviousFilterRequest();
            final FilePackagerBean filePackagerBean = filePackagerFactory.createFilePackagerBean(dadModel.getDiseaseType(), selectedFiles, dafpRequest.getEmail(),
                    dafpRequest.isFlatten(), isDownloadingProtected(selectedFiles), filePackagerKey, filterRequest);

            // use the request uri to construct the url for checking the job status
            filePackagerBean.setStatusCheckUrl(statusCheckUrl + "?" + DataMatrixStatusRequestController.PARAM_NAME_JOB + "=" + filePackagerBean.getKey());

            filePackagerBean.setArchivePhysicalPathPrefix(archivePhysicalPathPrefix);
            filePackagerBean.setArchiveLinkSite(linkSite);
            filePackagerFactory.enqueueFilePackagerBean(filePackagerBean);

            //to display in the web page:
            final DAFPViewItems jspInfo = new DAFPViewItems();
            jspInfo.setFilePackagerKey( filePackagerKey );
            jspInfo.setEmail( dafpRequest.getEmail() );
            jspInfo.setDiseaseType( dadModel.getDiseaseType() );
            jspInfo.setJobStatus(jobStatusService.getJobStatusForJobKey(filePackagerKey.toString()));
            modelAndView = new ModelAndView( successView, "DAFPInfo", jspInfo );
            //delete the dadModel and damModel from session memory now - won't be used again
            request.getSession().removeAttribute( DAMFacade.FACADE_KEY_NAME );
            request.getSession().removeAttribute( "dadModel" );
            clearSessionKey( request ); // so if user starts a new session, will not be tied to this one
        }
        catch(IllegalArgumentException e) {
            modelAndView = new ModelAndView( errorView, "ErrorInfo", new ErrorInfo( e ) );
        }
        catch(IllegalStateException e) {
            modelAndView = new ModelAndView( errorView, "ErrorInfo", new ErrorInfo( e ) );
        }
        catch (SchedulerException e) {
            modelAndView = new ModelAndView( errorView, "ErrorInfo", new ErrorInfo( e ) );
        }
        return modelAndView;
    }

    private boolean isDownloadingProtected( final List<DataFile> selectedFileInfo ) {
        boolean ret = false;
        for(final DataFile fileInfo : selectedFileInfo) {
            if(fileInfo.isProtected()) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    public void setFilePackagerFactory(final FilePackagerFactoryI filePackagerFactory) {
        this.filePackagerFactory = filePackagerFactory;
    }

    public void setJobStatusService(final DAMJobStatusService jobStatusService) {
        this.jobStatusService = jobStatusService;
    }

    public void setStatusCheckUrl(final String statusCheckUrl) {
        this.statusCheckUrl = statusCheckUrl;
    }
}