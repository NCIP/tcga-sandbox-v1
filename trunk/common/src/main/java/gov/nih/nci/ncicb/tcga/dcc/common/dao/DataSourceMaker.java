/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import javax.sql.DataSource;

/**
 * Interface for DataSourceMaker, which can make a data source object based on a property string, to be parsed
 * however the implementing class desires.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public interface DataSourceMaker {
    /**
     * Makes a DataSource object using the properties in the string given.
     *
     * @param propertyString string containing all the information needed to make a data source
     * @return a DataSource object
     */
    public DataSource makeDataSource(String propertyString);
}
