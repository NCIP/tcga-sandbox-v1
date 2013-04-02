/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.web;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileInfoQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.web.FileInfoQueryRequest;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Robert S. Sfeir, David Kane
 */
public class FileInfoFormController extends SimpleFormController {

    private String selectView = null;
    private FileInfoQueries fileQueries = null;
    private FileInfoQueryRequest fileQueryRequest = null;
    private ArchiveQueries archiveQueries = null;

    public FileInfoQueryRequest getFileInfoQuery() {
        return fileQueryRequest;
    }

    public void setFileInfoQuery( final FileInfoQueryRequest fileInfoQueryRequest ) {
        this.fileQueryRequest = fileInfoQueryRequest;
    }

    protected Object formBackingObject( HttpServletRequest request ) {
        return new FileInfoQueryRequest();
    }

    public FileInfoQueries getFileQueries() {
        return fileQueries;
    }

    public void setFileQueries( final FileInfoQueries fileQueries ) {
        this.fileQueries = fileQueries;
    }

    public ArchiveQueries getArchiveQueries() {
        return archiveQueries;
    }

    public void setArchiveQueries( final ArchiveQueries archiveQueries ) {
        this.archiveQueries = archiveQueries;
    }

    public FileInfoFormController() {
        setCommandName( "file" );
        setCommandClass( FileInfoQueryRequest.class );
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
            final BindException errors ) {
        final FileInfoQueryRequest queryParameter = (FileInfoQueryRequest) command;
        final Collection fileResults = fileQueries.getFilesForArchive( queryParameter );
        final Map archiveAndFiles = new HashMap();
        archiveAndFiles.put( "fileList", fileResults );
        final Archive archive = archiveQueries.getArchive( queryParameter.getArchiveId() );
        archiveAndFiles.put( "archive", archive );
        return new ModelAndView( getSuccessView(), archiveAndFiles );
    }
}