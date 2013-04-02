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
 * Bean class representing a nodeData for the bcrpipeline report
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class NodeData {

    private String name;
    private String label;
    private String image;
    private String numericLabel;
    private Listener listeners;
    private Integer pathLength;
    private Integer vertLoc;
    private List<Output> outputs;

    public NodeData() {
    }

    public NodeData(String name, String label, String image, String numericLabel, Integer pathLength, Integer vertLoc) {
        this.name = name;
        this.label = label;
        this.image = image;
        this.numericLabel = numericLabel;
        this.pathLength = pathLength;
        this.vertLoc = vertLoc;
    }

    public Integer getVertLoc() {
        return vertLoc;
    }

    public void setVertLoc(Integer vertLoc) {
        this.vertLoc = vertLoc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNumericLabel() {
        return numericLabel;
    }

    public void setNumericLabel(String numericLabel) {
        this.numericLabel = numericLabel;
    }

    public Listener getListeners() {
        return listeners;
    }

    public void setListeners(Listener listeners) {
        this.listeners = listeners;
    }

    public Integer getPathLength() {
        return pathLength;
    }

    public void setPathLength(Integer pathLength) {
        this.pathLength = pathLength;
    }

    public List<Output> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<Output> outputs) {
        this.outputs = outputs;
    }
}//End of Class
