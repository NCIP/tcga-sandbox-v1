/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.web;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileInfoQueries;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/**
 * @author Robert S. Sfeir, David Kane
 */
public class FileInfoController extends MultiActionController implements InitializingBean {

    private FileInfoQueries fileQueries = null;

    public FileInfoController() {
    }

    public void setFileQueries( final FileInfoQueries fileQueries ) {
        this.fileQueries = fileQueries;
    }

    public void afterPropertiesSet() {
        if(this.fileQueries == null) {
            throw new IllegalArgumentException( "Property 'fileQueries' is required" );
        }
    }

    public static ModelAndView fileResultHandler( final HttpServletRequest request,
                                                  final HttpServletResponse response ) {
        return new ModelAndView();
    }

    public static ModelAndView fileHandler( final HttpServletRequest request, final HttpServletResponse response ) {
        return new ModelAndView().addObject( "fileList", new ArrayList() );
    }
}