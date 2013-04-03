/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.service;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;

import java.util.List;

/**
 * Interface for methods used by reports in UUID Manager  
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */

public interface UUIDReportService {

    /**
     * Get a page from the specified list for the given start and limit values
     * @param list list to be paginated
     * @param start start index of the page
     * @param limit number of entries from the list
     * @return Paginated list
     */
    public List getPaginatedList(final List list, final int start, final int limit);

    public int getTotalCount(final List list);

    /**
     * Sorts the data in the specified list on the column specified by sortColumn
     * and in the direction specified by direction
     * @param list list to be sorted
     * @param sortColumn column to be sorted
     * @param direction direction of sorting ASC/DESC
     */
    void sortList(List<UUIDDetail> list, String sortColumn, String direction);
}
