/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.service;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.PendingUUID;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Service interface for the pending UUID report
 *
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface PendingUUIDReportService {


    /**
     * get All Pending UUID
     *
     * @return list of PendingUUID
     */
    public List<PendingUUID> getAllPendingUUIDs();

    /**
     * get the filtered PendingUUID list
     *
     * @param list
     * @param bcr
     * @return list of PendingUUID
     */
    public List<PendingUUID> getFilteredPendingUUIDList(List<PendingUUID> list, List<String> bcr,
                                                        List<String> center, String batch, String plateId);

    /**
     * creates a list of distinct possible values of a given filter for the report
     *
     * @param getterString method to call on the Pending UUID object to get the filter
     * @return a list of ExtJsFilter
     */
    public List<ExtJsFilter> getPendingUUIDFilterDistinctValues(final String getterString);


    /**
     * get the PendingUUID comparator map
     *
     * @return a PendingUUID comparator map
     */
    public Map<String, Comparator> getPendingUUIDComparator();


}
