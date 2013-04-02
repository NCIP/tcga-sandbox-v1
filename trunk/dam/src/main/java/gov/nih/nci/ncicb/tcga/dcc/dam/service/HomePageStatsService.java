/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.HomePageStats;

import java.util.List;

/**
 * Service class to populate HOME_PAGE_STATS table
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface HomePageStatsService {

    /**
     * Populate HOME_PAGE_STATS table
     */
    public void populateTable() throws HomePageStatsServiceImpl.HomePageStatsServiceException;
}
