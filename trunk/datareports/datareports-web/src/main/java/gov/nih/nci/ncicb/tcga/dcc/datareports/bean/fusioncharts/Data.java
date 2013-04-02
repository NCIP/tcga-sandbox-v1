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
 * Data bean for fusion charts
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class Data {

    private List<ChartDataRow> labelsAndValues;

    public List<ChartDataRow> getLabelsAndValues() {
        return labelsAndValues;
    }

    public void setLabelsAndValues(List<ChartDataRow> labelsAndValues) {
        this.labelsAndValues = labelsAndValues;
    }
    
}//End of Class
