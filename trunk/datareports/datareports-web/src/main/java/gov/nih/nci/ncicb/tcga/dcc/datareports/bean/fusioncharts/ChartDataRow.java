/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.bean.fusioncharts;

/**
 * bean class representing a label and value association
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ChartDataRow {

    private String label;
    private String value;
    private String link;

    public ChartDataRow() {
    }

    public ChartDataRow(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public ChartDataRow(String label, String value, String link) {
        this.label = label;
        this.value = value;
        this.link = link;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
    
}//End of Class
