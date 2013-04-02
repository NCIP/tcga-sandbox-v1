/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.bean;

import java.sql.Timestamp;

/**
 * This class is a row of infomation about a single sample
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class SampleSummary {

    private String disease;
    private String center;
    private String centerName;
    private String centerType;
    private String portionAnalyte;
    private String platform;
    private Long totalBCRSent;
    private Long totalCenterSent;
    private Long totalBCRUnaccountedFor;
    private Long totalCenterUnaccountedFor;
    private Long totalLevelOne;
    private Long totalLevelTwo;
    private Long totalLevelThree;
    private String levelFourSubmitted;
    private Timestamp lastRefresh;

    //This constructor has only a use for creating mock objets
    public SampleSummary(String disease,String centerName,String centerType,
                         String portionAnalyte,String platform){
        this.disease = disease;
        this.centerName = centerName;
        this.centerType = centerType;
        this.portionAnalyte = portionAnalyte;
        this.platform = platform;
    }

    public SampleSummary(){}
    
    public String getCenter() {
        return getCenterName() + " (" + getCenterType() + ")";
    }

    public void setCenter(final String center) {
        this.center = center;
    }

    public String getCenterName() {
        return (centerName==null)?"":centerName;
    }

    public void setCenterName(final String centerName) {
        this.centerName = centerName;
    }

    public void setCenterType(final String centerType) {
        this.centerType = centerType;
    }

    public String getCenterType() {
        return (centerType==null)?"":centerType;
    }

    public String getPortionAnalyte() {
        return (portionAnalyte ==null)?"": portionAnalyte;
    }

    public void setPortionAnalyte(final String portionAnalyte) {
        this.portionAnalyte = portionAnalyte;
    }

    public String getPlatform() {
        return (platform==null)?"Undetermined":platform;
    }

    public void setPlatform(final String platform) {
        this.platform = platform;
    }

    public String getLevelFourSubmitted() {
        return (levelFourSubmitted == null) ? "N" : levelFourSubmitted;
    }

    public void setLevelFourSubmitted(final String levelFourSubmitted) {
        this.levelFourSubmitted = levelFourSubmitted;
    }
    
    public Timestamp getLastRefresh() {
        return lastRefresh;
    }

    public void setLastRefresh(final Timestamp lastRefresh) {
        this.lastRefresh = lastRefresh;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(final String disease) {
        this.disease = disease;
    }

    public Long getTotalBCRSent() {
        return totalBCRSent;
    }

    public void setTotalBCRSent(final Long totalBCRSent) {
        this.totalBCRSent = totalBCRSent;
    }

    public Long getTotalCenterSent() {
        return totalCenterSent;
    }

    public void setTotalCenterSent(final Long totalCenterSent) {
        this.totalCenterSent = totalCenterSent;
    }

    public Long getTotalBCRUnaccountedFor() {
        return totalBCRUnaccountedFor;
    }

    public void setTotalBCRUnaccountedFor(final Long totalBCRUnaccountedFor) {
        this.totalBCRUnaccountedFor = totalBCRUnaccountedFor;
    }

    public Long getTotalCenterUnaccountedFor() {
        return totalCenterUnaccountedFor;
    }

    public void setTotalCenterUnaccountedFor(final Long totalCenterUnaccountedFor) {
        this.totalCenterUnaccountedFor = totalCenterUnaccountedFor;
    }

    public Long getTotalLevelOne() {
        return totalLevelOne;
    }

    public void setTotalLevelOne(final Long totalLevelOne) {
        this.totalLevelOne = totalLevelOne;
    }

    public Long getTotalLevelTwo() {
        return totalLevelTwo;
    }

    public void setTotalLevelTwo(final Long totalLevelTwo) {
        this.totalLevelTwo = totalLevelTwo;
    }

    public Long getTotalLevelThree() {
        return totalLevelThree;
    }

    public void setTotalLevelThree(final Long totalLevelThree) {
        this.totalLevelThree = totalLevelThree;
    }

}//End of Class
