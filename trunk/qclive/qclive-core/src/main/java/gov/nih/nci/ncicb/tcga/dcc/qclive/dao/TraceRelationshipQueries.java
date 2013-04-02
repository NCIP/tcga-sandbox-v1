/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.TraceRelationship;

/**
 * @Author: fengla
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface TraceRelationshipQueries {

    /**
     *
     * @param aTraceRelationship information about the relationship
     * @return the ID of the newly added relationship
     */
    public int addTraceRelationship( TraceRelationship aTraceRelationship );

    public int updateDccDate( TraceRelationship aTraceRelationship );

    public int updateFileID( TraceRelationship aTraceRelationship );

    public java.sql.Date getDccDate( int biospecimenId, long traceId );

    public Long getFileId( int biospecimenId, long traceId );
}
