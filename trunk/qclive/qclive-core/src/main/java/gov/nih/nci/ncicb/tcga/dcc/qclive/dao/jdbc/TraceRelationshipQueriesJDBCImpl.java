/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.BaseQueriesProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.TraceRelationship;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.TraceRelationshipQueries;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * @author fengla
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class TraceRelationshipQueriesJDBCImpl extends BaseQueriesProcessor implements TraceRelationshipQueries {

    ProcessLogger logger = new ProcessLogger();

    public TraceRelationshipQueriesJDBCImpl() {
    }

    public int addTraceRelationship( TraceRelationship theTraceRelationship ) {
        int nextId = getNextSequenceNumberAsInteger("BIOSPECIMEN_TRACE_SEQ");
        String insertSQL = "insert into biospecimen_ncbi_trace (biospecimen_trace_id,biospecimen_id, ncbi_trace_id, dcc_date_received, file_id ) values (?, ?, ?, ?, ?)";
        SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate( getDataSource() );
        int rowsInserted = sjdbc.update( insertSQL, nextId, theTraceRelationship.getBiospecimenID(), theTraceRelationship.getTraceID(), theTraceRelationship.getDccReceived(), theTraceRelationship.getFileID() );
        int shippedRowsInserted = addTraceRelationshipForShipped(theTraceRelationship );
        if (rowsInserted != 1 || shippedRowsInserted != 1) {
            return -1;
        }
        return nextId;
    }

    private int addTraceRelationshipForShipped( TraceRelationship theTraceRelationship ) {
         String insertSQL = "insert into shipped_biospec_ncbi_trace (biospecimen_trace_id,shipped_biospecimen_id, ncbi_trace_id, dcc_date_received, file_id ) values (BIOSPECIMEN_TRACE_SEQ.NEXTVAL, ?, ?, ?, ?)";
         SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate( getDataSource() );
         int rowsInserted = sjdbc.update( insertSQL, theTraceRelationship.getBiospecimenID(), theTraceRelationship.getTraceID(), theTraceRelationship.getDccReceived(), theTraceRelationship.getFileID() );
         if (rowsInserted != 1) {
             return -1;
         }
         return rowsInserted;
     }

    public int updateDccDate( final TraceRelationship theTraceRelationship ) {
        String updateSQL = "update biospecimen_ncbi_trace set dcc_date_received = ? where biospecimen_id = ? and ncbi_trace_id = ?";
        SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate( getDataSource() );
        int shippedUpdate = updateDccDateForShipped(theTraceRelationship);
        return sjdbc.update( updateSQL, theTraceRelationship.getDccReceived(), theTraceRelationship.getBiospecimenID(), theTraceRelationship.getTraceID() );
    }

    private int updateDccDateForShipped ( final TraceRelationship theTraceRelationship ) {
        String updateSQL = "update shipped_biospec_ncbi_trace set dcc_date_received = ? where shipped_biospecimen_id = ? and ncbi_trace_id = ?";
        SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate( getDataSource() );
        return sjdbc.update( updateSQL, theTraceRelationship.getDccReceived(), theTraceRelationship.getBiospecimenID(), theTraceRelationship.getTraceID() );
    }

    public int updateFileID( final TraceRelationship theTraceRelationship ) {
        String updateSQL = "update biospecimen_ncbi_trace set file_id = ? where biospecimen_id = ? and ncbi_trace_id = ?";
        SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate( getDataSource() );
        int shippedUpdate = updateFileIDForShipped(theTraceRelationship );
        return sjdbc.update( updateSQL, theTraceRelationship.getFileID(), theTraceRelationship.getBiospecimenID(), theTraceRelationship.getTraceID() );
    }

    private int updateFileIDForShipped( final TraceRelationship theTraceRelationship ) {
         String updateSQL = "update shipped_biospec_ncbi_trace set file_id = ? where shipped_biospecimen_id = ? and ncbi_trace_id = ?";
         SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate( getDataSource() );
         return sjdbc.update( updateSQL, theTraceRelationship.getFileID(), theTraceRelationship.getBiospecimenID(), theTraceRelationship.getTraceID() );
     }

    public java.sql.Date getDccDate( final int biospecimenId, final long traceId ) {
        String selectSQL = "select dcc_date_received from shipped_biospec_ncbi_trace where shipped_biospecimen_id = ? and ncbi_trace_id = ?";
        try {
            SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate( getDataSource() );
            return sjdbc.queryForObject( selectSQL, java.sql.Date.class, biospecimenId, traceId );
        }
        catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Long getFileId( int biospecimenId, long traceId ) {
         String selectSQL = "select file_id from shipped_biospec_ncbi_trace where shipped_biospecimen_id = ? and ncbi_trace_id = ?";
        SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate( getDataSource() );
        return sjdbc.queryForObject( selectSQL, Long.class, biospecimenId, traceId );
    }
}
