/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import javax.sql.DataSource;

/**
 * Created by IntelliJ IDEA.
 * User: nanans
 * Date: Oct 15, 2008
 * Time: 3:25:22 PM
 * To change this template use File | Settings | File Templates.
 */
public interface DAMQueriesStatsI {

    void setDataSource( DataSource dataSource );

    String[][] getStats();
}
