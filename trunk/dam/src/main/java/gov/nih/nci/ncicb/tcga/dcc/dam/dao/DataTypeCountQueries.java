/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.stats.DataTypeCount;

/**
 * Interface for DataTypeCountQueries.
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface DataTypeCountQueries {

    /**
     * Calculates all the counts for cases and organ-specific controls for the Cancer Details page and saves them
     * to the home_page_drilldown table.
     */
    void calculateAndSaveCounts();

    /**
     * Gets an array of DataTypeCount objects representing Cancer Details counts for the given disease.
     * @param diseaseAbbreviation the disease to get the counts for
     * @return array of two DataTypeCount objects, for Cases and Organ-Specific Controls
     */
    DataTypeCount[] getDataTypeCountArray(String diseaseAbbreviation);
}
