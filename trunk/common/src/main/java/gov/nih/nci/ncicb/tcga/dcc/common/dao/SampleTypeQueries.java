/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.SampleType;

import java.util.List;

/**
 * Interface for queries for sample type.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface SampleTypeQueries {
    /**
     * Gets a list of SampleType beans.
     *  
     * @return a list of all sample types found
     */
    public List<SampleType> getAllSampleTypes();
}
