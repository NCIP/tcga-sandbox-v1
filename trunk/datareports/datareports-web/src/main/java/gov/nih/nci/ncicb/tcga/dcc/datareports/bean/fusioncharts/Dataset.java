/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.bean.fusioncharts;

import java.util.List;

/**
 * bean class representing a fusion chart dataset object
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class Dataset {

    private String seriesname;
    private String parentyaxis;
    private String color;
    private List<Value> data;

    public Dataset() {
    }

    public Dataset(String seriesname) {
        this.seriesname = seriesname;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSeriesname() {
        return seriesname;
    }

    public void setSeriesname(String seriesname) {
        this.seriesname = seriesname;
    }

    public List<Value> getData() {
        return data;
    }

    public void setData(List<Value> data) {
        this.data = data;
    }

    public String getParentyaxis() {
        return parentyaxis;
    }

    public void setParentyaxis(String parentyaxis) {
        this.parentyaxis = parentyaxis;
    }
    
}//End of Class
