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
 * Bean class representing a fusionChart category object
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class Category {

    private List<Label> category;

    public List<Label> getCategory() {
        return category;
    }

    public void setCategory(List<Label> category) {
        this.category = category;
    }
    
}//End of Class
