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
 * @author Robert S. Sfeir, David Kane Last updated by: $Author$ Jeyanthi Thangiah
 */
public class ArchiveQueryRequest {

    private String project = null;
    private String samples = null;
    private String platform = null;
    private String dataType = null;
    private String center = null;
    private String rowSort = null;
    private String colSort = null;
    private String dateStart = null;
    private String dateEnd = null;
    private int id = 0;
    private String fileName = null;
    private String deployStatus = null;
    private String isLatest = null;
    private String archiveType = null;

    public ArchiveQueryRequest() {
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart( final String dateStart ) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd( final String dateEnd ) {
        this.dateEnd = dateEnd;
    }

    public String getProject() {
        return project;
    }

    public void setProject( final String project ) {
        this.project = project;
    }

    public String getSamples() {
        return samples;
    }

    public void setSamples( final String samples ) {
        this.samples = samples;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform( final String platform ) {
        this.platform = platform;
    }

    public String getCenter() {
        return center;
    }

    public void setCenter( final String center ) {
        this.center = center;
    }

    public String getRowSort() {
        return rowSort;
    }

    public void setRowSort( final String rowSort ) {
        this.rowSort = rowSort;
    }

    public String getColSort() {
        return colSort;
    }

    public void setColSort( final String colSort ) {
        this.colSort = colSort;
    }

    public int getId() {
        return id;
    }

    public void setId( final int id ) {
        this.id = id;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType( final String dataType ) {
        this.dataType = dataType;
    }

    public String getArchiveType() {
        return archiveType;
    }

    public void setArchiveType(final String archiveType) {
        this.archiveType = archiveType;
    }

    public boolean hasAtLeastOneParameter() {
        return ( deployStatus != null && deployStatus.length() != 0 ) ||
                isLatest != null  ||
                ( dateStart!=null && dateStart.length() != 0 ) ||
                ( dataType!=null && !dataType.equals( "-1" ) ) ||
                ( project!=null && !project.equals( "-1" ) ) ||
                ( platform!=null && !platform.equals( "-1" ) ) ||
                ( fileName!=null && fileName.trim().length() != 0 ) ||
                ( center!=null && !center.equals( "-1" )) ||
                ( archiveType != null && !archiveType.equals("-1")
                );
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName( String fileName ) {
        this.fileName = fileName;
    }

    public String getDeployStatus() {
        return deployStatus;
    }

    public void setDeployStatus( final String deployStatus ) {
        this.deployStatus = deployStatus;
    }

    public String getLatest() {
        return isLatest;
    }

    public void setLatest( final String latest ) {
        isLatest = latest;
    }
}
