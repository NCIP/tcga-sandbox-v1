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
import gov.nih.nci.ncicb.tcga.dcc.common.web.ArchiveQueryRequest;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: fengla
 * Date: Jun 18, 2008
 * Time: 2:54:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class LatestArchivesController extends AbstractCommandController {

    private ArchiveQueries archiveQueries = null;

    public LatestArchivesController() {
        setCommandClass( ArchiveQueryRequest.class );
    }

    public ArchiveQueries getArchiveQueries() {
        return archiveQueries;
    }

    public void setArchiveQueries( final ArchiveQueries archiveQueries ) {
        this.archiveQueries = archiveQueries;
    }

    protected ModelAndView handle( final HttpServletRequest request,
                                   final HttpServletResponse response,
                                   final Object command,
                                   final BindException errors ) throws Exception {
        final ArchiveQueryRequest queryParameter = (ArchiveQueryRequest) command;
        final Collection results = archiveQueries.getMatchingArchives( queryParameter );
        return new ModelAndView( "latestArchives", "archiveList", results );
    }
}