/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao;

/**
 * DAO interface for refreshing materialized view
 *
 *
 * @author fengla 
 */
public interface MVRefreshQueries {

    public void refreshMV( String mv_name );
    
}
