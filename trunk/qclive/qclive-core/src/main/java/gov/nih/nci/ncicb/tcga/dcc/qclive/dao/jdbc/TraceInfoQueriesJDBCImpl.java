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
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.NcbiTrace;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.TraceInfoQueries;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.object.MappingSqlQuery;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Auher: fengla
 * Date: Apr 14, 2008
 */
public class TraceInfoQueriesJDBCImpl extends BaseQueriesProcessor implements TraceInfoQueries {

    private ProcessLogger logger = new ProcessLogger();
    private int rowupdated;
    private final String selectfields = "ncbi_trace_id, trace_name, center_name, submission_type, gene_name, " +
            "reference_accession, reference_acc_max, reference_acc_min, " +
            "replaced_by, basecall_length, load_date, state ";
    private final String insertfields = "ncbi_trace_id, trace_name, center_name, submission_type, gene_name, " +
            "reference_accession, reference_acc_max, reference_acc_min, " +
            "basecall_length, load_date, state ";

    public int addTraceInfo( NcbiTrace trace ) {
        String insertSQL = "insert into trace_info (" + insertfields + ") values (?,?,?,?,?,?,?,?,?,?,?)";
        SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate( getDataSource() );
        rowupdated = sjdbc.update( insertSQL, trace.getTi(), trace.getTrace_name(),trace.getCenter_name(), trace.getSubmission_type(), trace.getGene_name(),
                trace.getReference_accession(), trace.getReference_acc_max(), trace.getReference_acc_min(),
                trace.getBasecall_length(), trace.getLoad_date(), trace.getState() );
        return rowupdated;
    }

    public int updateTraceinfo( long old_trace_id, long new_trace_id ) {
        String updateSQL = "update trace_info set replaced_by = ?, state = ? where ncbi_trace_id = ? ";
        SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate( getDataSource() );
        rowupdated = sjdbc.update( updateSQL, new_trace_id, "retired", old_trace_id );
        return rowupdated;
    }

    public List<NcbiTrace> getMatchingTraces( String trace_name, String center_name ) {
        List<NcbiTrace> traceEntries = new ArrayList<NcbiTrace>();
        String selectSQL = "select " + selectfields + "from trace_info where trace_name = '" + trace_name + "' and center_name = '" + center_name+"'";
        TraceQueryByParameter traceByParameterQuery = new TraceQueryByParameter( getDataSource(), selectSQL );
        traceEntries.addAll( traceByParameterQuery.execute() );
        return traceEntries;
    }

    public int exists( long new_trace_id ) {
        String checkSQL = "select count(ncbi_trace_id) from trace_info where ncbi_trace_id = ?";
        SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate( getDataSource() );
        final int tiindb = sjdbc.queryForInt( checkSQL, new_trace_id );
        return tiindb;
    }

    public Date getLastLoadDate() {
        String dateSQL = "select max(load_date) from trace_info";
        SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate( getDataSource() );
        final Date lastdateindb = sjdbc.queryForObject( dateSQL, java.sql.Date.class );
        return lastdateindb;
    }

    static class TraceQueryByParameter extends MappingSqlQuery {

        TraceQueryByParameter( DataSource ds, final String selectSQL ) {
            super( ds, selectSQL );
        }

        protected NcbiTrace mapRow( ResultSet rs, int rownum ) throws SQLException {
            NcbiTrace trace = new NcbiTrace();
            trace.setTi( rs.getInt( "ncbi_trace_id" ) );
            trace.setCenter_name( rs.getString( "center_name" ) );
            trace.setTrace_name( rs.getString( "trace_name" ) );
            trace.setSubmission_type( rs.getString( "submission_type" ) );
            trace.setGene_name( rs.getString( "gene_name" ) );
            trace.setReference_accession( rs.getString( "reference_accession" ) );
            trace.setReference_acc_max( rs.getInt( "reference_acc_max" ) );
            trace.setReference_acc_min( rs.getInt( "reference_acc_min" ) );
            trace.setReplaced_by( rs.getInt( "replaced_by" ) );
            trace.setBasecall_length( rs.getInt( "basecall_length" ) );
            trace.setLoad_date( rs.getTimestamp( "load_date" ) );
            trace.setState( rs.getString( "state" ) );
            return trace;
        }
    }
}
