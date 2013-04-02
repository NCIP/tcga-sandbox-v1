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
import gov.nih.nci.ncicb.tcga.dcc.dam.view.ErrorInfo;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Author: David Nassau
 * <p/>
 * Returns a tab-delimited file of all the constants that can be used in external filters
 */
public class DataAccessExternalFilterConstantsController extends WebController {

    protected ModelAndView handle( HttpServletRequest request, HttpServletResponse response, Object command,
                                   BindException errors ) {
        ModelAndView ret = null;
        try {
            setDiseaseForDataSource(DataAccessMatrixQueries.DEFAULT_DISEASETYPE);
            //in case no user has hit the DAM to load the lookup maps
            DataAccessMatrixJSPUtil.storeLookups( request.getSession().getServletContext() );
            ret = new ModelAndView( "filterLookups", "lookups", DataAccessMatrixJSPUtil.getAllLookups() );
        }
        catch(IllegalStateException e) {
            ret = new ModelAndView( errorView, "ErrorInfo", new ErrorInfo( e ) );
        }
        return ret;
    }
}
