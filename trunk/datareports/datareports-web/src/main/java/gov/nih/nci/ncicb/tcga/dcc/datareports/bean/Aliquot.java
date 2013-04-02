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
 * This class defined a row of infomation about a biospecimen
 * It is my biospecimen domain object
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class Aliquot {

    private String disease;
    private String aliquotId;
    private String bcrBatch;
    private String center;
    private String platform;
    private String levelOne;
    private String levelTwo;
    private String levelThree;

    public String getDisease() {
        return disease;
    }

    public void setDisease(final String disease) {
        this.disease = disease;
    }

    public String getAliquotId() {
        return aliquotId;
    }

    public void setAliquotId(final String aliquotId) {
        this.aliquotId = aliquotId;
    }

    public String getBcrBatch() {
        return bcrBatch;
    }

    public void setBcrBatch(final String bcrBatch) {
        this.bcrBatch = bcrBatch;
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(final String center) {
        this.center = center;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(final String platform) {
        this.platform = platform;
    }

    public String getLevelOne() {
        return levelOne;
    }

    public void setLevelOne(final String levelOne) {
        this.levelOne = levelOne;
    }

    public String getLevelTwo() {
        return levelTwo;
    }

    public void setLevelTwo(final String levelTwo) {
        this.levelTwo = levelTwo;
    }

    public String getLevelThree() {
        return levelThree;
    }

    public void setLevelThree(final String levelThree) {
        this.levelThree = levelThree;
    }

}//End of Class
