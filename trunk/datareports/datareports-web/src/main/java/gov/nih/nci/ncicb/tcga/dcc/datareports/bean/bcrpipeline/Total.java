/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.bean.bcrpipeline;

import java.util.List;

/**
 * Bean class representing totals for the bcrpipeline report
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class Total {

    private Integer textSize;
    private Integer width;
    private Position pos;
    private String fill;
    private String label;
    private List<Count> counts;

    public Total() {
    }

    public Total(Integer textSize, Integer width, Position pos, String fill, String label) {
        this.textSize = textSize;
        this.width = width;
        this.pos = pos;
        this.fill = fill;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getTextSize() {
        return textSize;
    }

    public void setTextSize(Integer textSize) {
        this.textSize = textSize;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Position getPos() {
        return pos;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }

    public String getFill() {
        return fill;
    }

    public void setFill(String fill) {
        this.fill = fill;
    }

    public List<Count> getCounts() {
        return counts;
    }

    public void setCounts(List<Count> counts) {
        this.counts = counts;
    }
}//end of Class
