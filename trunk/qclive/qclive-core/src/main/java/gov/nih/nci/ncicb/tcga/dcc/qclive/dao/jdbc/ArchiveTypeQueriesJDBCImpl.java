/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ArchiveTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.BaseQueriesProcessor;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of ArchiveTypeQueries
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class ArchiveTypeQueriesJDBCImpl extends BaseQueriesProcessor implements ArchiveTypeQueries {

    public List<String> getAllArchiveTypes() {
        String query = "select archive_type from archive_type";
        final List<String> types = new ArrayList<String>();
        getJdbcTemplate().query( query, new RowCallbackHandler() {
            public void processRow( final ResultSet resultSet ) throws SQLException {
                types.add( resultSet.getString( "archive_type" ) );
            }
        } );
        return types;
    }

    public Integer getArchiveTypeId( final String type ) {
        String query = "select archive_type_id from archive_type where archive_type=?";
        return getJdbcTemplate().queryForInt( query, new Object[]{type} );
    }

    public String getArchiveType( final Integer id ) {
        String query = "select archive_type from archive_type where archive_type_id=?";
        return (String) getJdbcTemplate().queryForObject( query, new Object[]{id}, String.class );
    }

    public boolean isValidArchiveType( final String type ) {
        try {
            Integer id = getArchiveTypeId( type );
            return id != null;
        }
        catch(IncorrectResultSizeDataAccessException e) {
            return false;
        }
    }

    /**
     * @param type the archive type
     * @return the data level or null if the type has no level
     */
    public Integer getArchiveTypeDataLevel( final String type ) {
        String query = "select data_level from archive_type where archive_type=?";
        try {
            return getJdbcTemplate().queryForInt( query, new Object[]{type} );
        }
        catch(IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }
}
