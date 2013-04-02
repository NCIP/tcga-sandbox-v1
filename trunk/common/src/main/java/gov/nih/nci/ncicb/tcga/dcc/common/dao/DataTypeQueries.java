/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import java.util.Collection;
import java.util.Map;

/**
 * @author Robert S. Sfeir
 */
public interface DataTypeQueries {


    public Collection<Map<String, Object>> getAllDataTypes();

    public String getDataTypeFTPDisplayForPlatform(String platformId);

    public String getBaseDataTypeDisplayNameForPlatform(final Integer platformId);

    public String getCenterTypeIdForPlatformId(final Integer platformId);

    /**
     * Get all data types id indexed by name.
     * @return list of data type ids
     */
    public Map<String,Long> getAllDataTypesId();
}
