/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.Disease;

import java.util.List;

/**
 * Interface for DAM disease queries interface
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public interface DAMDiseaseQueries {
    /**
     * Gets the disease object for this abbreviation.
     * @param diseaseAbbreviation the disease abbreviation (such as 'GBM')
     * @return the disease object or null if disease not found
     */
    public Disease getDisease(final String diseaseAbbreviation);

    /**
     * Gets the list of active diseases. (Diseases that are set to active in their DBs.)
     *
     * @return active diseases
     */
    public List<Disease> getActiveDiseases();

    /**
     * Gets the list of all diseases.
     *
     * @return all diseases
     */
    public List<Disease> getDiseases();
}
