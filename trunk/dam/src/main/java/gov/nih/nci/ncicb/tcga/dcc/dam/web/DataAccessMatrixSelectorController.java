/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.web;

import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMFacadeI;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.ErrorInfo;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.SelectionRequest;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Author: David Nassau
 * Processes user selections (such as selecting a row or column header) into the
 * DAMSelectionModel, which is contained in the DAMFacade.
 */
public class DataAccessMatrixSelectorController extends WebController {

    protected ModelAndView handle(
            final HttpServletRequest request, final HttpServletResponse response, final Object command,
            final BindException errors ) {
        ModelAndView ret = null;
        try {
            SelectionRequest damselRequest = (SelectionRequest) command;
            damselRequest.validate();
            DAMFacadeI facadeI = (DAMFacadeI) request.getSession().getAttribute( "damFacade" );
            if(facadeI == null) {
                return new ModelAndView("sessionError");
            }
            setDiseaseForDataSource(facadeI.getDiseaseType()); //may not actually be needed here

            ensureProperBackButtonBehavior( damselRequest, request.getSession() );
            facadeI.setSelection( damselRequest );
            ret = new ModelAndView( successView, "damFacade", facadeI );
        }
        catch(IllegalArgumentException e) {
            ret = new ModelAndView( errorView, "ErrorInfo", new ErrorInfo( e ) );
        }
        catch(IllegalStateException e) {
            ret = new ModelAndView( errorView, "ErrorInfo", new ErrorInfo( e ) );
        }
        return ret;
    }

    //if user reached this page by clicking Back in browser, we want it to function
    //as a no-op regardless of what the argument says.
    private void ensureProperBackButtonBehavior( final SelectionRequest damselRequest, final HttpSession session ) {
        final long millis = damselRequest.getMillis();
        if(millis > 0) {
            final Long storedMillis = (Long) session.getAttribute( "damselMillis" );
            if(storedMillis != null && storedMillis == millis) {
                //got the same millisecond argument, user must have clicked Back.
                //Make sure it just displays the current state
                damselRequest.setMode( SelectionRequest.MODE_NOOP );
            } else {
                //normal request, store the millis for next time
                session.setAttribute( "damselMillis", millis );
            }
        }
    }
}
