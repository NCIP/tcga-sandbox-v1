/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.web;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMFacadeI;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DataAccessDownloadModel;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.ErrorInfo;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.DADRequest;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.SelectionRequest;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author: David Nassau
 * <p/>
 * Constructs DAD model for DataAccessDownload page.  In particular, supports presentation
 * of file list as a tree.
 */
public class DataAccessDownloadController extends WebController {

    //DAO instance
    private DataAccessMatrixQueries dataAccessMatrixQueries;
    private ArchiveQueries archiveQueries;
    private int sizeLimitGigs = -1;

    /**
     * Injects the DAO instance
     *
     * @param value
     */
    public void setDataAccessMatrixQueries( final DataAccessMatrixQueries value ) {
        dataAccessMatrixQueries = value;
    }

    public void setSizeLimitGigs( final int sizeLimitGigs ) {
        this.sizeLimitGigs = sizeLimitGigs;
    }

    protected ModelAndView handle(
            final HttpServletRequest request, final HttpServletResponse response, final Object command,
            final BindException errors ) {
        ModelAndView ret = null;
        try {
            final DADRequest dadRequest = (DADRequest) command;
            dadRequest.validate();
            DAMFacadeI facadeI = (DAMFacadeI) request.getSession().getAttribute( "damFacade" );
            if(facadeI == null) {
                //redirect to new DAM page
                ret = new ModelAndView( "redirect:/dataAccessMatrix.htm" );
            } else {
                setDiseaseForDataSource(facadeI.getDiseaseType());
                ret = makeModelAndViewForDAD( request.getSession(), facadeI, dadRequest, false );
            }
        }
        catch(IllegalArgumentException e) {
            ret = new ModelAndView( errorView, "ErrorInfo", new ErrorInfo( e ) );
        }
        catch(IllegalStateException e) {
            ret = new ModelAndView( errorView, "ErrorInfo", new ErrorInfo( e ) );
        }
        catch(DataAccessMatrixQueries.DAMQueriesException e) {
            ret = new ModelAndView( errorView, "ErrorInfo", new ErrorInfo( e ) );
        }
        return ret;
    }

    //called internally, also externally from DataAccessExternalFilterController
    ModelAndView makeModelAndViewForDAD( final HttpSession session, final DAMFacadeI facadeI, final DADRequest dadRequest,
                                         final boolean isExternalFilter ) throws DataAccessMatrixQueries.DAMQueriesException {
        ModelAndView ret;
        updateSelections( facadeI, dadRequest.getSelectedCells() );
        List<DataSet> selectedDataSets = facadeI.getSelectedDataSets();
        List<DataFile> fileInfos = dataAccessMatrixQueries.getFileInfoForSelectedDataSets( selectedDataSets, dadRequest.isConsolidateFiles() );
        DataAccessDownloadModel dadModel = new DataAccessDownloadModel( facadeI.getDiseaseType(), sizeLimitGigs );
        dadModel.addFileInfo( fileInfos );
        dadModel.setFromExternalFilter( isExternalFilter );
        dadModel.setConsolidateFiles(dadRequest.isConsolidateFiles());
        dadModel.setEmail(dadRequest.getEmail());
        dadModel.setEmail2(dadRequest.getEmail2());
        dadModel.setFlatten(dadRequest.isFlatten());

        final Set<Archive> archives = new HashSet<Archive>();
        for (final DataSet dataSet : selectedDataSets) {
            final int archiveId = dataSet.getArchiveId();
            final Archive archive = archiveQueries.getArchive((long)archiveId);
            archives.add(archive);
        }
        dadModel.setOriginalArchives(archives);
                
        //for now, we'll keep both damModel and dadModel in memory, giving the ability to go back to
        //the DAM page in its current state, as well as to move forward to DAFP
        session.setAttribute( "dadModel", dadModel );
        session.setAttribute("damSelectedCells", dadRequest.getSelectedCells());
        ret = new ModelAndView( successView, "dadModel", dadModel );
        return ret;
    }

    private void updateSelections( DAMFacadeI facadeI, String selectedCells ) {
        SelectionRequest selreq = new SelectionRequest();
        selreq.setMode( SelectionRequest.MODE_CELLS );
        selreq.setSelectedCells( selectedCells );
        facadeI.setSelection( selreq );
    }

    public void setArchiveQueries(final ArchiveQueries archiveQueries) {
        this.archiveQueries = archiveQueries;
    }
}
