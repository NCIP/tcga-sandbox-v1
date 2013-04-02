/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.HomePageStats;

import java.util.List;
import java.util.Map;

/**
 * Queries to populate the HOME_PAGE_STATS table
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface HomePageStatsQueries {

    /**
     * Populate HOME_PAGE_STATS table
     * @param homePageStats a map of stats objects populated with cases shipped already, keyed to disease abbrev
     */
    public void populateTable(Map<String, HomePageStats> homePageStats);
}
