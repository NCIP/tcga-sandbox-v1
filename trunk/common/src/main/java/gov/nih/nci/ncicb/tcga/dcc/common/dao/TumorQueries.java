/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Robert S. Sfeir
 */
public interface TumorQueries {

    /**
     * Get the id for the given disease abbreviation.  Will return -1 if not found.
     * @param tumorName the disease abbreviation
     * @return the disease ID from the database or -1 if no such disease found
     */
    public Integer getTumorIdByName( String tumorName );

    public Tumor getTumorForName( String tumorName );

    public Tumor getTumorForId( Integer diseaseId );

    public Collection<Map<String, Object>> getAllTumors();

    public String getTumorNameById( Integer tumorId );

    List<Integer> getTissueIdsForTumor(String tumorAbbreviation);

    public List<Tumor> getDiseaseList();
}
