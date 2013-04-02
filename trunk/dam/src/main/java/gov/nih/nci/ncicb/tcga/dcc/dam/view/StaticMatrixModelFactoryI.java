/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.view;

import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;

/**
 * Created by IntelliJ IDEA.
 * User: nanans
 * Date: Sep 10, 2008
 * Time: 5:10:53 PM
 * This interface was created for testing purposes.
 */
public interface StaticMatrixModelFactoryI {

    DAMModel getOrMakeModel( String diseaseType,
                                   boolean force ) throws DataAccessMatrixQueries.DAMQueriesException;

    void refreshAll() throws DataAccessMatrixQueries.DAMQueriesException;
}
