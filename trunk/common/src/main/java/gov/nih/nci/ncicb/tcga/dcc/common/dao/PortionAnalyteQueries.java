/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.PortionAnalyte;

import java.util.List;

/**
 * Interface for queries for portion analytes.
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface PortionAnalyteQueries {

    /**
     * Gets a list of Portion Analyte beans.
     *
     * @return a list of all portion Analyte found
     */
    public List<PortionAnalyte> getAllPortionAnalytes();
}
