/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao;

import java.util.List;

/**
 * Queries for collection sites.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public interface TissueSourceSiteQueries {

    /**
     * Returns the list of disease abbreviations for a given tissues source site code
     * @param tissueSourceSiteCode tss code
     * @return List of disease abbreviations
     */
    List<String> getDiseasesForTissueSourceSiteCode(String tissueSourceSiteCode);
}
