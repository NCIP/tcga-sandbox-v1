/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.web;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.LogQueries;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/**
 * @author Robert S. Sfeir, David Kane
 */
public class LogController extends MultiActionController implements InitializingBean {

    private LogQueries logQueries = null;

    public LogController() {
    }

    public void setLogQueries( final LogQueries logQueries ) {
        this.logQueries = logQueries;
    }

    public void afterPropertiesSet() throws Exception {
        if(this.logQueries == null) {
            throw new IllegalArgumentException( "Property 'logQueries' is required" );
        }
    }

    public static ModelAndView logResultHandler( final HttpServletRequest request,
                                                 final HttpServletResponse response ) {
        return new ModelAndView();
    }

    public static ModelAndView logHandler( final HttpServletRequest request, final HttpServletResponse response ) {
        return new ModelAndView().addObject( "logList", new ArrayList() );
    }
}