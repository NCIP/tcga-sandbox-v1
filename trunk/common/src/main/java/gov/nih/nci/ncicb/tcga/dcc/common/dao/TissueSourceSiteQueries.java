/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.TissueSourceSite;

import java.util.List;

/**
 * interface defining dao method for retrieving tissue source sites from the database
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface TissueSourceSiteQueries {

    /**
     * Gets a list of TissueSourceSite beans.
     *
     * @return a list of all TissueSourceSite found
     */
    public List<TissueSourceSite> getAllTissueSourceSites();

    /**
     * Gets an aggregate list of TissueSourceSite beans.
     *
     * @return a list of all TissueSourceSite found
     */
    public List<TissueSourceSite> getAggregateTissueSourceSites();


    /**
     * Gets the disease abbreviations for the given tissue source site.
     *
     * @param tissueSourceSiteCode the collection site code
     * @return list of disease abbreviations
     */
    public List<String> getDiseasesForTissueSourceSiteCode(final String tissueSourceSiteCode);

    /**
     * Gets the list of TSS codes that correspond to cell line controls.
     * @return list of TSS codes for controls
     */
    public List<String> getControlTssCodes();
    
}
