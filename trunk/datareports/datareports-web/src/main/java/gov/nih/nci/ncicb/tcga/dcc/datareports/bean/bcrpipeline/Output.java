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
 * Bean class representing outputs for the nodeData of the bcrpipeline report
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class Output {

    private Integer count;
    private String label;
    private String connectToNextNode;
    private String arrow;
    private String color;
    private String pathDir;

    public Output() {
    }

    public Output(Integer count, String label, String color, String pathDir) {
        this.count = count;
        this.label = label;
        this.color = color;
        this.pathDir = pathDir;
    }

    public Output(Integer count, String label, String connectToNextNode, String arrow, String color) {
        this.count = count;
        this.label = label;
        this.connectToNextNode = connectToNextNode;
        this.arrow = arrow;
        this.color = color;
    }

    public String getPathDir() {
        return pathDir;
    }

    public void setPathDir(String pathDir) {
        this.pathDir = pathDir;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getConnectToNextNode() {
        return connectToNextNode;
    }

    public void setConnectToNextNode(String connectToNextNode) {
        this.connectToNextNode = connectToNextNode;
    }

    public String getArrow() {
        return arrow;
    }

    public void setArrow(String arrow) {
        this.arrow = arrow;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}//End of Class
