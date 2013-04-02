/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDType;

import java.util.List;

/**
 * Interface for queries for UUID Types.
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface UUIDTypeQueries {
    /**
     * Gets a list of UUID Type beans.
     *
     * @return a list of all UUID Type found
     */
    public List<UUIDType> getAllUUIDTypes();


    /**
     * Gets a UUID Type Id from a UUID Type Name.
     *
     * @return a valid UUID Type ID or null if there is no match
     */
    public Long getUUIDTypeID(final String itemType);
}
