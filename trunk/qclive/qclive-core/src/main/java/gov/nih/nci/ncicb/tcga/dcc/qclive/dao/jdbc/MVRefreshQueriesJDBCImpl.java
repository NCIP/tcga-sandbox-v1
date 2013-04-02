/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.BaseQueriesProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.MVRefreshQueries;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * @author Larry Feng
 * @version $Rev: 3628 $ 
 */
public class MVRefreshQueriesJDBCImpl extends BaseQueriesProcessor implements MVRefreshQueries {

    public void refreshMV( final String mv_name ) {
        String select = "select refresh_matview('" + mv_name + "');";
        SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate( getDataSource() );
        sjdbc.getJdbcOperations().execute( select );
    }    
}