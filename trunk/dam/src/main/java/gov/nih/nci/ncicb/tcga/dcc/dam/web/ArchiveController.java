/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.web;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/**
 * @author Robert S. Sfeir, David Kane
 */
public class ArchiveController extends MultiActionController implements InitializingBean {

    private ArchiveQueries archiveQueries = null;

    public ArchiveController() {
    }

    public void setArchiveQueries( final ArchiveQueries archiveQueries ) {
        this.archiveQueries = archiveQueries;
    }

    public void afterPropertiesSet() {
        if(this.archiveQueries == null) {
            throw new IllegalArgumentException( "Property 'archiveQueries' is required" );
        }
    }

    public static ModelAndView archiveResultHandler( final HttpServletRequest request,
                                                     final HttpServletResponse response ) {
        return new ModelAndView();
    }

    public static ModelAndView archiveHandler( final HttpServletRequest request, final HttpServletResponse response ) {
        return new ModelAndView().addObject( "archiveList", new ArrayList() );
    }
}