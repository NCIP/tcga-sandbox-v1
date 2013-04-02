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
 * Bean class representing the GraphConfig of the bcrpipeline report
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class GraphConfig {

    private String renderTo;
    private Size paperSize;
    private String center;
    private Integer scale;
    private Position startPos;
    private Integer squareCorners;
    private Size squareSize;
    private String squareColor;
    private Integer pathHeight;
    private Integer pathLength;
    private Integer minPathwidth;
    private List<String> pathColors;

    public String getRenderTo() {
        return renderTo;
    }

    public void setRenderTo(String renderTo) {
        this.renderTo = renderTo;
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public Integer getMinPathwidth() {
        return minPathwidth;
    }

    public void setMinPathwidth(Integer minPathwidth) {
        this.minPathwidth = minPathwidth;
    }

    public Size getPaperSize() {
        return paperSize;
    }

    public void setPaperSize(Size paperSize) {
        this.paperSize = paperSize;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public Position getStartPos() {
        return startPos;
    }

    public void setStartPos(Position startPos) {
        this.startPos = startPos;
    }

    public Integer getSquareCorners() {
        return squareCorners;
    }

    public void setSquareCorners(Integer squareCorners) {
        this.squareCorners = squareCorners;
    }

    public Size getSquareSize() {
        return squareSize;
    }

    public void setSquareSize(Size squareSize) {
        this.squareSize = squareSize;
    }

    public String getSquareColor() {
        return squareColor;
    }

    public void setSquareColor(String squareColor) {
        this.squareColor = squareColor;
    }

    public Integer getPathHeight() {
        return pathHeight;
    }

    public void setPathHeight(Integer pathHeight) {
        this.pathHeight = pathHeight;
    }

    public Integer getPathLength() {
        return pathLength;
    }

    public void setPathLength(Integer pathLength) {
        this.pathLength = pathLength;
    }

    public List<String> getPathColors() {
        return pathColors;
    }

    public void setPathColors(List<String> pathColors) {
        this.pathColors = pathColors;
    }
}//End of Class
