/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.util.DiseaseNameLister;

import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * A virtual data source that dispatches to the appropriate underlying
 * data source based on a piece of thread-local data in DiseaseContextHolder.
 * Each of those underlying data sources maps to an Oracle schema for a disease.
 * Based on the example in http://blog.springsource.com/2007/01/23/dynamic-datasource-routing/
 *
 * @author David Nassau
 * @version $Rev$
 */
public class DiseaseRoutingDataSource extends AbstractRoutingDataSource implements DiseaseNameLister {

    private Set<Object> diseaseNames;

    /**
     * Constructor that takes a data source maker, and a string mapping diseases to data source properties.
     * The string must have the format "disease1:dataSourceProperty,disease2:dataSourceProperty" etc.
     *
     * @param dataSourceMaker   object that can make a data source
     * @param dataSourceNameMap string in form "disease1:properties,disease2:properties"
     * @see DataSourceMaker
     */
    public DiseaseRoutingDataSource(final DataSourceMaker dataSourceMaker, final String dataSourceNameMap) {
        Map<Object, Object> dataSourceMap = DataSourceMakerUtil.makeDataSources(dataSourceMaker, dataSourceNameMap);
        setTargetDataSources(dataSourceMap);
        diseaseNames = dataSourceMap.keySet();
    }


    @Override
    protected Object determineCurrentLookupKey() {
        //don't insert a default disease here - we'd rather have it fail than show the wrong data
        return DiseaseContextHolder.getDisease();
    }

    public Set<Object> getDiseaseNames() {
        return diseaseNames;
    }
}
