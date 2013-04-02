/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.bean.bcrpipeline;

/**
 * Bean class representing Tumor list for the bcrpipeline report box graph
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class TumorTypes {
    private Integer textSize;
    private Integer width;
    private Position pos;
    private String fill;
    private String title;
    private String types;

    public TumorTypes() {
    }

    public TumorTypes(Integer textSize, Integer width, Position pos, String fill, String title, String types) {
        this.textSize = textSize;
        this.width = width;
        this.pos = pos;
        this.fill = fill;
        this.title = title;
        this.types = types;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }
}//End of Class
