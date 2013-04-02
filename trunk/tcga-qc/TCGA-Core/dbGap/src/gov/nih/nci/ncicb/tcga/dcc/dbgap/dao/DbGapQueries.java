/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dbgap.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.ClinicalMetaQueries;

import java.util.List;

/**
 * Interface for dbGap data queries.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public interface DbGapQueries {
    /**
     * Gets all clinical data for the given file.
     *
     * @param clinicalFile get the data for columns needed for this file
     * @return a list of lists of strings, where each list represents a unique record for the file, and the inner list
     * has values for the file columns in the order as given in the file 
     */
    public List<List<String>> getClinicalData(ClinicalMetaQueries.ClinicalFile clinicalFile);
}
