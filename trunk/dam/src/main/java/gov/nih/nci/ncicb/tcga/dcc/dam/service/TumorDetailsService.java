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

import java.util.Collection;

/**
 * Service class to deal with TumorSampleTypeCount objects
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface TumorDetailsService {

    /**
     * Return a TumorSampleTypeCount for each of the sample types, as an array:
     * <p/>
     * - tumor
     * - matched normal
     * - unmatched normal
     *
     * @param diseaseAbbreviation the disease abbreviation
     * @return a TumorSampleTypeCount for each of the sample types, as an array
     */
    public DataTypeCount[] getTumorDataTypeCountArray(final String diseaseAbbreviation);

    /**
     * Insert the latest counts into the database.
     *
     */
    public void calculateAndSaveTumorDataTypeCounts();


}
