/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.NcbiTrace;

import java.sql.Date;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: fengla
 * Date: Apr 14, 2008
 * Time: 10:19:35 AM
 * To change this template use File | Settings | File Templates.
 */
public interface TraceInfoQueries {

    public int addTraceInfo( NcbiTrace trace );

    public int updateTraceinfo( long old_trace_id, long new_trace_id );

    public Collection getMatchingTraces( String trace_name, String center_name );

    public int exists( long new_trace_id );

    public Date getLastLoadDate();
}
