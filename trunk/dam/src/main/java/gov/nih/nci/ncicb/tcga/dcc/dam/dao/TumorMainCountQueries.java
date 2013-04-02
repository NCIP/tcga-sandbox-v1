/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.stats.TumorMainCount;

import java.util.List;

/**
 * Class for TumorMainCountQueries queries
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface TumorMainCountQueries {

    /**
     * Return a list of TumorMainCount sorted by disease name
     *
     * @return a list of TumorMainCount sorted by disease name
     * @throws TumorMainCountQueriesException
     */
    public List<TumorMainCount> getAllTumorMainCount()
        throws TumorMainCountQueriesException;

    /**
     * Exception class to be used by TumorMainCountQueries
     */
    public class TumorMainCountQueriesException extends Exception {

        /**
         * Return a TumorMainCountQueriesException with the given message
         *
         * @param message the exception message
         */
        public TumorMainCountQueriesException(final String message) {
            super(message);
        }
    }
}
