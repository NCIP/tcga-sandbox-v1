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
 * Archive Info bean used to hold ArchiveInfo from database
 *
 * @author Stanley Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ArchiveInfo {

    private String centerName = null;
    private String centerType = null;
    private String totalCount = null;
    private String available = null;
    private String revised = null;
    private String processing = null;
    private String platform = null;
    private String dataType = null;
    private String latestDate = null;
    private String diseaseAbbreviation = null;
    private String deployLocation = null;
    private Integer isLatest = null;

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(final String centerName) {
        this.centerName = centerName;
    }

    public String getCenterType() {
        return centerType;
    }

    public void setCenterType(final String centerType) {
        this.centerType = centerType;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(final String totalCount) {
        this.totalCount = totalCount;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(final String platform) {
        this.platform = platform;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(final String dataType) {
        this.dataType = dataType;
    }

    public String getLatestDate() {
        return latestDate;
    }

    public void setLatestDate(final String latestDate) {
        this.latestDate = latestDate;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(final String available) {
        this.available = available;
    }

    public String getRevised() {
        return revised;
    }

    public void setRevised(final String revised) {
        this.revised = revised;
    }

    public String getProcessing() {
        return processing;
    }

    public void setProcessing(final String processing) {
        this.processing = processing;
    }

    public String getDeployLocation() {
        return deployLocation;
    }

    public void setDeployLocation(final String deployLocation) {
        this.deployLocation = deployLocation;
    }

    public Integer getLatest() {
        return isLatest;
    }

    public void setLatest(final Integer latest) {
        isLatest = latest;
    }

    public String getDiseaseAbbreviation() {
        return diseaseAbbreviation;
    }

    public void setDiseaseAbbreviation(String diseaseAbbreviation) {
        this.diseaseAbbreviation = diseaseAbbreviation;
    }
}
