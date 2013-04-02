/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Visibility;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;

/**
 * @version $Id: VisibilityQueries.java 1253 2008-06-11 17:19:52Z sfeirr $
 * @author: Robert S. Sfeir
 */
public interface VisibilityQueries {

    public Visibility getVisibilityForArchive( Archive theArchive );

    /**
     * Gets the most restrictive visibility for the given data type.  If any data for this data type is protected, then
     * will return protected visibility.  If all data is public for a data type, will return public visibility.
     * @param dataTypeName the data type to get the visibility for
     * @return most restrictive visibility possible for the data type or null if data type not valid
     */
    public Visibility getLeastVisibilityForDataType(String dataTypeName);
    
    /**
     * Gets the visibility for a given platform name, data type name, center type code and level number
     * @TODO : Add more comments as functionality becomes clearer
     * @param platformName
     * @return
     */
    public Visibility getVisibilityForPlatform(String platformName, 
			String dataTypeName,
			String centerTypeCode,
			Integer levelNumber);

}
