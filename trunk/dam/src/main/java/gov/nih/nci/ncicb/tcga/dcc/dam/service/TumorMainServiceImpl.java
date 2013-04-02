/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.stats.TumorMainCount;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.TumorMainCountQueries;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Service implementation class to deal with TumorMainCount objects
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class TumorMainServiceImpl implements TumorMainService {

    @Autowired
    private TumorMainCountQueries tumorMainCountQueries;

    /**
     * Return a TumorMainCount for each available disease, as a list, sorted by disease name
     *
     * @return a TumorMainCount for each available disease, as a list, sorted by disease name
     * @throws gov.nih.nci.ncicb.tcga.dcc.dam.service.TumorMainService.TumorMainServiceException
     *
     */
    @Override
    public List<TumorMainCount> getTumorMainCountList() throws TumorMainServiceException {

        final List<TumorMainCount> result;

        try {
            result = tumorMainCountQueries.getAllTumorMainCount();

        } catch (final TumorMainCountQueries.TumorMainCountQueriesException e) {
            throw new TumorMainServiceException(e.getMessage());
        }

        return result;
    }

    /**
     * For unit tests
     *
     * @param tumorMainCountQueries the TumorMainCountQueries to set
     */
    public void setTumorMainCountQueries(final TumorMainCountQueries tumorMainCountQueries) {
        this.tumorMainCountQueries = tumorMainCountQueries;
    }
}
