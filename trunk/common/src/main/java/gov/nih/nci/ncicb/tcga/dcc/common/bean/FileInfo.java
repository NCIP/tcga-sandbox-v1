/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.bean;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;

/**
 * @author Robert S. Sfeir
 * @author David Kane
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class FileInfo {
    private Long fileId = (long) ConstantValues.NOT_ASSIGNED;
    private Long revision;
    private Long fileSize;
    private Integer dataLevel;
    private Integer dataTypeId;
    private String fileMD5;
    private String fileName = null;
    private String archiveName;
    private String fileLocation;

    public FileInfo() {}


    public Long getId() {
        return fileId;
    }

    public void setId( final Long fileId ) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName( final String fileName ) {
        this.fileName = fileName;
    }

    public boolean equals( final Object o ) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        final FileInfo fileInfo = (FileInfo) o;
        if(!fileId.equals(fileInfo.getId())) {
            return false;
        }
        if(!fileName.equals( fileInfo.fileName )) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result;
        result = getId().hashCode();
        result = 31 * result + fileName.hashCode();
        return result;
    }

    public String toString() {
        return fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(final Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileMD5() {
        return fileMD5;
    }

    public void setFileMD5(final String fileMD5) {
        this.fileMD5 = fileMD5;
    }

    public Integer getDataLevel() {
        return dataLevel;
    }

    public void setDataLevel(final Integer dataLevel) {
        this.dataLevel = dataLevel;
    }

    public Integer getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(final Integer dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    public Long getRevision() {
        return revision;
    }

    public void setRevision(final Long revision) {
        this.revision = revision;
    }

    public String getArchiveName() {
        return archiveName;
    }

    public void setArchiveName(String archiveName) {
        this.archiveName = archiveName;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }
}
