/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

/**
 * Utility class for DataSourceMaker.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class DataSourceMakerUtil {
    /**
     * Make data sources as defined by a parameter string, with given data source maker.
     * 
     * @param dataSourceMaker the object used to make data sources
     * @param dataSourceNameMap string in the form name:properties,name:properties
     * @return a map of data source objects with name from string as key
     */
    public static Map<Object, Object> makeDataSources(final DataSourceMaker dataSourceMaker, final String dataSourceNameMap) {
        final Map<Object, Object> dataSourceMap = new HashMap<Object, Object>();
        final String[] dataSources = dataSourceNameMap.split(",");
        for (final String dataSourceString : dataSources) {
            // give a limit so if there is a colon in the properties part, it won't split along it...
            final String[] dataSourceNameAndProperties = dataSourceString.split(":", 2);
            final DataSource dataSource = dataSourceMaker.makeDataSource(dataSourceNameAndProperties[1]);
            dataSourceMap.put(dataSourceNameAndProperties[0], dataSource);
        }
        return dataSourceMap;
    }
}
