/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.web;

import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.DataAccessMatrixJSPUtil;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMFacade;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMFacadeI;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMModel;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.ErrorInfo;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.StaticMatrixModelFactory;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.DADRequest;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.FilterRequest;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.SelectionRequest;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;

/**
 * Author: David Nassau
 */
public class DataAccessExternalFilterController extends WebController {

    private StaticMatrixModelFactory staticMatrixModelFactory;
    private DataAccessDownloadController dataAccessDownloadController;
    private boolean enabled=true;

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public void setStaticMatrixModelFactory( StaticMatrixModelFactory staticMatrixModelFactory ) {
        this.staticMatrixModelFactory = staticMatrixModelFactory;
    }

    public void setDataAccessDownloadController( DataAccessDownloadController dataAccessDownloadController ) {
        this.dataAccessDownloadController = dataAccessDownloadController;
    }

    protected ModelAndView handle( HttpServletRequest request, HttpServletResponse response, Object command,
                                   BindException errors ) {
        ModelAndView ret = null;
        try {
            if (!enabled) {
                throw new IllegalStateException("Data Matrix user interface is disabled");
            }
            FilterRequest filterRequest = (FilterRequest) command;
            filterRequest.setMode( FilterRequest.Mode.ApplyFilter );
            if(filterRequest.getDiseaseType() == null) {
                filterRequest.setDiseaseType( DataAccessMatrixQueries.DEFAULT_DISEASETYPE );
            }
            filterRequest.validate();
            setDiseaseForDataSource(filterRequest.getDiseaseType());

            //in case no user has hit the DAM to load the lookup maps
            DataAccessMatrixJSPUtil.storeLookups( request.getSession().getServletContext() );
            //create a matrix facade, even though we aren't displaying a matrix
            DAMModel staticModel = staticMatrixModelFactory.getOrMakeModel( filterRequest.getDiseaseType(), false );
            DAMFacadeI facadeI = new DAMFacade( staticModel );
            facadeI.setFilter( filterRequest );
            //select all filtered cells
            SelectionRequest selreq = new SelectionRequest();
            selreq.setMode( SelectionRequest.MODE_SELECTALL );
            facadeI.setSelection( selreq );
            request.getSession().setAttribute( "damFacade", facadeI );
            //forward to DAD controller
            StringBuilder selectedCellList = new StringBuilder();
            Iterator selectedCellIter = facadeI.getSelectedCellIds().iterator();
            while(selectedCellIter.hasNext()) {
                selectedCellList.append( selectedCellIter.next() ).append( "," );
            }
            DADRequest dadRequest = new DADRequest();
            dadRequest.setSelectedCells(selectedCellList.toString());
            ret = dataAccessDownloadController.makeModelAndViewForDAD( request.getSession(), facadeI, dadRequest, true );
        }
        catch(DataAccessMatrixQueries.DAMQueriesException e) {
            ret = new ModelAndView( errorView, "ErrorInfo", new ErrorInfo( e ) );
        }        
        catch(IllegalArgumentException e) {
            ret = new ModelAndView( errorView, "ErrorInfo", new ErrorInfo( e ) );
        }
        catch (IllegalStateException e) {
            ret = new ModelAndView( errorView, "ErrorInfo", new ErrorInfo( e ) );
        }
        return ret;
    }
}
