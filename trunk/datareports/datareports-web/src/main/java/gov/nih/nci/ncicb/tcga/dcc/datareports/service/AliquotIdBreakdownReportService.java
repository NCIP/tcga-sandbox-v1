/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.service;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.AliquotIdBreakdown;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * service layer for the aliquotId breakdown report
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public interface AliquotIdBreakdownReportService {

    /**
     * A query to get all the aliquotId breakdown report
     *
     * @return a list of BiospecimenBreakdown
     */
    public List<AliquotIdBreakdown> getAliquotIdBreakdown();

    /**
     * get the AliquotIdBreakdown comparator map
     *
     * @return a AliquotIdBreakdown comparator map
     */
    public Map<String, Comparator> getAliquotIdBreakdownComparator();

    /**
     * creates a AliquotIdBreakdown filtered list according to filters
     *
     * @param list the list to be filtered
     * @param aliquotId filter
     * @param analyteId filter
     * @param sampleId filter
     * @param participantId filter
     *
     * @return a AliquotIdBreakdown filtered list
     */
    public List<AliquotIdBreakdown> getFilteredAliquotIdBreakdownList(
            List<AliquotIdBreakdown> list,
            String aliquotId,
            String analyteId,
            String sampleId,
            String participantId);
}
