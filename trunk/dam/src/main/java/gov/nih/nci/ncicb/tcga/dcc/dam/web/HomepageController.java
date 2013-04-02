/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.web;

import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DAMQueriesStatsI;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.DataAccessMatrixJSPUtil;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMFacade;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMModel;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.ErrorInfo;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.StaticMatrixModelFactoryI;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.FilterRequestI;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * Controller for portal homepage.
 *
 * @author Silpa Nanan
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class HomepageController extends WebController {

    protected DAMQueriesStatsI statsQueries;
    private StaticMatrixModelFactoryI staticMatrixModelFactory;
    boolean isUsedInTest = false;   //todo kludge? But you have to avoid calling getWebApplicationContext()
    boolean enabled=true;

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public void setStaticMatrixModelFactory( StaticMatrixModelFactoryI staticMatrixModelFactory ) {
        this.staticMatrixModelFactory = staticMatrixModelFactory;
    }

    public void setStatsQueries( final DAMQueriesStatsI statsQueries ) {
        this.statsQueries = statsQueries;
    }

    protected ModelAndView handle( HttpServletRequest request, HttpServletResponse response, Object command,
                                   BindException errors ) {
        ModelAndView ret = null;
        try {
            if (!enabled) {
                throw new IllegalStateException("Data Matrix user interface is disabled");
            }
            HashMap mavReturn = new HashMap();
            final FilterRequestI filterRequest = (FilterRequestI) command;
            if(filterRequest.getDiseaseType() == null) {
                filterRequest.setDiseaseType( DataAccessMatrixQueries.DEFAULT_DISEASETYPE );
            }
            filterRequest.validate();
            setDiseaseForDataSource(filterRequest.getDiseaseType());

            if(!isUsedInTest) {
                DataAccessMatrixJSPUtil.storeLookups(getWebApplicationContext().getServletContext());
            }
            final DAMModel staticModel = staticMatrixModelFactory.getOrMakeModel( filterRequest.getDiseaseType(), false );
            final DAMFacade facade = new DAMFacade( staticModel );
            request.getSession().setAttribute( "damFacade", facade );  //needed for the mini-filter on the home page
            mavReturn.put( "damFacade", facade );
            ret = new ModelAndView( successView, mavReturn );
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
}
