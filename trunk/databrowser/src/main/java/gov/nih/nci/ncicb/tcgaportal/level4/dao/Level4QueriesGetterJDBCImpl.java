/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DataSourceMaker;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DataSourceMakerUtil;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.jdbc.Level4QueriesJDBCImpl;
import gov.nih.nci.ncicb.tcgaportal.level4.util.CorrelationCalculator;
import gov.nih.nci.ncicb.tcgaportal.level4.util.FishersExact;
import gov.nih.nci.ncicb.tcgaportal.util.ProcessLogger;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Gets the correct level 4 queries for a disease.  Initializes one per disease based on a string.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class Level4QueriesGetterJDBCImpl implements Level4QueriesGetter {
    private Map<String, Level4Queries> level4QueriesMap = new HashMap<String, Level4Queries>();

    public Level4QueriesGetterJDBCImpl(
            final int fetchSize,
            final CorrelationCalculator correlationCalculator,
            final FishersExact fishersExact,
            final ProcessLogger logger,
            final DataSourceMaker dataSourceMaker,
            final String dataSourcePropertyString) {

        Map<Object, Object> dataSources = DataSourceMakerUtil.makeDataSources(dataSourceMaker, dataSourcePropertyString);
        for (final Object diseaseName : dataSources.keySet()) {
            Level4QueriesJDBCImpl level4Queries = new Level4QueriesJDBCImpl(fetchSize, correlationCalculator,
                    fishersExact, (DataSource) dataSources.get(diseaseName));
            level4Queries.setLogger(logger);
            level4QueriesMap.put((String) diseaseName, level4Queries);
            logger.logDebug("Building queries object for " + diseaseName);
        }

    }

    public Level4Queries getLevel4Queries(final String disease) {
        return level4QueriesMap.get(disease);
    }

    public Collection<String> getDiseaseNames() {
        return new TreeSet<String>(level4QueriesMap.keySet());
    }
}
