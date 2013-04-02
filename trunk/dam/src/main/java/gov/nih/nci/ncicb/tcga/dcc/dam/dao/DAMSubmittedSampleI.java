/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import java.util.List;
import java.util.Set;

/**
 * "internal" interface for getting list of submitted samples per center
 */
public interface DAMSubmittedSampleI {

    /**
     * Returns a set of all the submitted samples for each center, with values as "center:sample".
     * The DAM model then uses that to fill "gaps" in the matrix with either an "N" or "NA" cell.
     *
     * Should exclude control samples.
     *
     * @param diseaseType the disease type to get samples for
     * @return set of all submitted non-control samples
     * @throws gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries.DAMQueriesException
     *          when it wants to
     */
    Set<String> getSubmittedSampleIds( String diseaseType ) throws DataAccessMatrixQueries.DAMQueriesException;

    /**
     * Returns all submitted control samples.
     *
     * @param diseaseTypes
     * @return set of all submitted control samples
     */
    Set<String> getSubmittedControls(List<String> diseaseTypes) throws DataAccessMatrixQueries.DAMQueriesException;
}
