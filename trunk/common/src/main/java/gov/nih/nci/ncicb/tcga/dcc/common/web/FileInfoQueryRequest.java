/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.web;

/**
 * @author Robert S. Sfeir, David Kane
 */
public class FileInfoQueryRequest {

    private int archiveId = 0;

    public FileInfoQueryRequest() {
    }

    public int getArchiveId() {
        return archiveId;
    }

    public void setArchiveId( final int theId ) {
        this.archiveId = theId;
    }
}
