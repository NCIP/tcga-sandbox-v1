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

import java.util.List;

/**
 * Service class to deal with TumorMainCount objects
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface TumorMainService {

    /**
     * Return a TumorMainCount for each available disease, as a list, sorted by disease name
     *
     * @return a TumorMainCount for each available disease, as a list, sorted by disease name
     * @throws TumorMainServiceException
     */
    public List<TumorMainCount> getTumorMainCountList() throws TumorMainServiceException;

    /**
     * Exception class to be used by TumorDetailsService
     */
    public class TumorMainServiceException extends Exception {

        /**
         * Return a TumorMainServiceException with the given message
         *
         * @param message the exception message
         */
        public TumorMainServiceException(final String message) {
            super(message);
        }
    }
}
