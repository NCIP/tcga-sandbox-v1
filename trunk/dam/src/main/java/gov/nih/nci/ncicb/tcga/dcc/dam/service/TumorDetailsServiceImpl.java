/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.stats.DataTypeCount;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataTypeCountQueries;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service implementation class to deal with TumorSampleTypeCount objects
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class TumorDetailsServiceImpl implements TumorDetailsService {

    @Autowired
    private DataTypeCountQueries dataTypeCountQueries;

    /**
     * Return a DataTypeCount for each of the sample types, as an array, in that order:
     * <p/>
     * - cases
     * - cancer-negative controls
     *
     * @param diseaseAbbreviation the disease abbreviation
     * @return a DataTypeCount for cases and for cancer-negative controls
     */
    @Override
    public DataTypeCount[] getTumorDataTypeCountArray(final String diseaseAbbreviation) {
        return dataTypeCountQueries.getDataTypeCountArray(diseaseAbbreviation);
    }

    /**
     *
     *
     */
    @Override
    public void calculateAndSaveTumorDataTypeCounts() {
        dataTypeCountQueries.calculateAndSaveCounts();
    }

    public void setDataTypeCountQueries(final DataTypeCountQueries dataTypeCountQueries) {
        this.dataTypeCountQueries = dataTypeCountQueries;
    }
}
