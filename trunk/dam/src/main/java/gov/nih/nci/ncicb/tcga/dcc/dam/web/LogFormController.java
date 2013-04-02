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
import gov.nih.nci.ncicb.tcga.dcc.common.web.LogQueryRequest;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * @author Robert S. Sfeir, David Kane
 */
public class LogFormController extends SimpleFormController {

    private String selectView = null;
    private LogQueries logQueries = null;
    private LogQueryRequest logQueryRequest = null;

    protected void initBinder( final HttpServletRequest req,
                               final ServletRequestDataBinder binder ) throws Exception {
        binder.registerCustomEditor( Date.class, new CustomDateEditor(
                new SimpleDateFormat( "dd-MM-yyyy" ), true ) );
    }

    public LogQueryRequest getLogQuery() {
        return logQueryRequest;
    }

    public void setLogQuery( final LogQueryRequest logQueryRequest ) {
        this.logQueryRequest = logQueryRequest;
    }

    protected Object formBackingObject( HttpServletRequest request )
            throws Exception {
        return new LogQueryRequest();
    }

    public LogQueries getLogQueries() {
        return logQueries;
    }

    public void setLogQueries( final LogQueries logQueries ) {
        this.logQueries = logQueries;
    }

    public LogFormController() {
        setCommandName( "log" );
        setCommandClass( LogQueryRequest.class );
    }

    public void setSelectView( final String selectView ) {
        this.selectView = selectView;
    }

    protected void initApplicationContext() {
        super.initApplicationContext();
        if(this.selectView == null) {
            throw new IllegalArgumentException( "selectView isn't set" );
        }
    }

    protected ModelAndView onSubmit(
            final HttpServletRequest request, final HttpServletResponse response, final Object command,
            final BindException errors )
            throws Exception {
        final LogQueryRequest queryParameter = (LogQueryRequest) command;
        final Collection results = logQueries.getLogInDateRange( queryParameter );
        return new ModelAndView( getSuccessView(), "logList", results );
    }
}