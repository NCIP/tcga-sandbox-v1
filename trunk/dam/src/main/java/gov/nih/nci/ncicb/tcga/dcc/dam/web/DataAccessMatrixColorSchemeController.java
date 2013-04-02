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
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.ColorSchemeRequest;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.SelectionRequest;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Author: David Nassau
 * Processes a user's color-scheme selection to the DAMFacade.  The DAMFacade will then
 * choose from among several existing color scheme classes for rendering colors in the matrix.
 */
public class DataAccessMatrixColorSchemeController extends WebController {

    protected ModelAndView handle(
            HttpServletRequest request, HttpServletResponse response, Object command, BindException errors ) {
        ModelAndView ret = null;
        try {
            ColorSchemeRequest colorRequest = (ColorSchemeRequest) command;
            DAMFacadeI facadeI = (DAMFacadeI) request.getSession().getAttribute( "damFacade" );
            if(facadeI == null) {
                return new ModelAndView("sessionError");
            }
            setDiseaseForDataSource(facadeI.getDiseaseType()); //may not actually be needed here

            ensureProperBackButtonBehavior( colorRequest, request.getSession() );
            //update the set of selected cells so they will still be selected in new color scheme
            SelectionRequest selreq = new SelectionRequest();
            selreq.setMode( SelectionRequest.MODE_CELLS );
            selreq.setSelectedCells( colorRequest.getSelectedCells() );
            facadeI.setSelection( selreq );
            if(colorRequest.getMode() != ColorSchemeRequest.Mode.NoOp) {
                facadeI.setColorSchemeName( colorRequest.getColorSchemeName() );
            }
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
    private void ensureProperBackButtonBehavior( ColorSchemeRequest colorRequest, HttpSession session ) {
        long millis = colorRequest.getMillis();
        if(millis > 0) {
            Long storedMillis = (Long) session.getAttribute( "filterMillis" );
            if(storedMillis != null && storedMillis == millis) {
                //got the same millisecond argument, user must have clicked Back.
                //Make sure it just displays the current state
                colorRequest.setMode( ColorSchemeRequest.Mode.NoOp );
            } else {
                //normal request, store the millis for next time
                session.setAttribute( "filterMillis", millis );
            }
        }
    }
}