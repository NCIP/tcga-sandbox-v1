/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;

/**
 * @author Robert S. Sfeir
 * @version: $Id: FileType.java 1253 2008-06-11 17:19:52Z sfeirr $
 */
class FileType {

    private Integer id = null;
    private String fileTypeName = null;
    private String description = null;
    private String suffix = null;

    FileType() {
    }

    public Integer getId() {
        return id;
    }

    public void setId( final Integer id ) {
        this.id = id;
    }

    public String getFileTypeName() {
        return fileTypeName;
    }

    public void setFileTypeName( final String fileTypeName ) {
        this.fileTypeName = fileTypeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( final String description ) {
        this.description = description;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix( final String suffix ) {
        this.suffix = suffix;
    }

    public boolean equals( final Object o ) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        final FileType fileType = (FileType) o;
        if(!fileTypeName.equals( fileType.fileTypeName )) {
            return false;
        }
        if(id != null ? !id.equals( fileType.id ) : fileType.id != null) {
            return false;
        }
        if(!suffix.equals( fileType.suffix )) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result;
        result = ( id != null ? id.hashCode() : 0 );
        result = 31 * result + fileTypeName.hashCode();
        result = 31 * result + suffix.hashCode();
        return result;
    }

    public String toString() {
        return fileTypeName;
    }
}
