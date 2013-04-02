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
import gov.nih.nci.ncicb.tcga.dcc.dam.view.ErrorInfo;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * Controller for portal news page.
 *
 * @author Jim Jordan
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class NewspageController extends WebController {

    private boolean enabled=true;

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }


    protected ModelAndView handle( final HttpServletRequest request, final HttpServletResponse response, final Object command,
                                   final BindException errors ) {
        ModelAndView modelAndView;
        try {
            if (!enabled) {
                throw new IllegalStateException("Data Matrix user interface is disabled");
            }
            modelAndView = new ModelAndView( successView );
        }
        catch(IllegalArgumentException e) {
            modelAndView = new ModelAndView( errorView, "ErrorInfo", new ErrorInfo( e ) );
        }
        catch(IllegalStateException e) {
            modelAndView = new ModelAndView( errorView, "ErrorInfo", new ErrorInfo( e ) );
        }
        return modelAndView;
    }
}