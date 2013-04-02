/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.bean;

/**
 * This class specifies an archive, file and url of file from an aliquotId in the biospecimen report
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class AliquotArchive {

    private int archiveId;
    private String archiveName;
    private int fileId;
    private String fileName;
    private String fileUrl;

    public int getArchiveId() {
        return archiveId;
    }

    public void setArchiveId(final int archiveId) {
        this.archiveId = archiveId;
    }

    public String getArchiveName() {
        return archiveName;
    }

    public void setArchiveName(final String archiveName) {
        this.archiveName = archiveName;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(final int fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(final String fileUrl) {
        this.fileUrl = fileUrl;
    }
}//End of class
