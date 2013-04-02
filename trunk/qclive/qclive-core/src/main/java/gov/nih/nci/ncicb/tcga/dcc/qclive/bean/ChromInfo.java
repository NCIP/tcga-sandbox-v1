/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;

/**
 * @author Tarek Hassan
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ChromInfo {

    private String chromName;
    private Integer chromSize;
    private String build;

    public String getChromName() {
        return chromName;
    }

    public void setChromName(String chromName) {
        this.chromName = chromName;
    }

    public Integer getChromSize() {
        return chromSize;
    }

    public void setChromSize(Integer chromSize) {
        this.chromSize = chromSize;
    }
    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }
}
