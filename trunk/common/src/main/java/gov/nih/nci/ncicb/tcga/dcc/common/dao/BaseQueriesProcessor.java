/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.Collection;
import java.util.Map;

/**
 * @author Robert S. Sfeir
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BaseQueriesProcessor extends JdbcDaoSupport {

    public BaseQueriesProcessor() {
        // allow no-arg constructor for subclasses
    }

    /**
     * Return the value from a column (should be an Integer) in a table for the row that match the given criteria, or -1 if no match was found
     *
     * @param searchString  the string to match
     * @param tableName     the table in which to search
     * @param colToSearchBy the column in which to search
     * @param colToReturn   the column to return
     * @return the value from a column (should be an Integer) in a table for the row that match the given criteria, or -1 if no match was found
     */
    @Deprecated
    public Integer getObjectIdByNameAsInteger(final String searchString, final String tableName, final String colToSearchBy, final String colToReturn) {

        Integer result;

        final String select = "select " + colToReturn + " from " + tableName + " where " + colToSearchBy + " = ?";
        final SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());

        try {
            result = sjdbc.queryForInt(select, searchString);
        } catch(final DataAccessException e) {
            result = -1;
        }

        return result;
    }

    /**
     * Return the value from a column (should be an Integer) in a table for the row that match the given criteria, or -1 if no match was found
     *
     * @param searchString  the string to match
     * @param tableName     the table in which to search
     * @param colToSearchBy the column in which to search
     * @param colToReturn   the column to return
     * @return the value from a column (should be a Long) in a table for the row that match the given criteria, or -1 if no match was found
     */
    public Long getObjectIdByName(final String searchString, final String tableName, final String colToSearchBy, final String colToReturn) {

        Long result;

        final String select = "select " + colToReturn + " from " + tableName + " where " + colToSearchBy + " = ?";
        final SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());

        try {
            result = sjdbc.queryForLong(select, searchString);
        } catch(final DataAccessException e) {
            result = -1L;
        }

        return result;
    }

    /**
     * Return the next sequence number of the given sequence
     *
     * @param seqName the sequence name
     * @return the next sequence number of the given sequence
     */
    @Deprecated
    public Integer getNextSequenceNumberAsInteger(final String seqName) {

        assert seqName != null && seqName.trim().length() > 0;
        final String select = "select " + seqName + ".nextval from dual";
        final SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());
        return sjdbc.queryForInt(select);
    }

    /**
     * Return the next sequence number of the given sequence
     *
     * @param seqName the sequence name
     * @return the next sequence number of the given sequence
     */
    public Long getNextSequenceNumber(final String seqName) {

        assert seqName != null && seqName.trim().length() > 0;
        final String select = "select " + seqName + ".nextval from dual";
        final SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());
        return sjdbc.queryForLong(select);
    }

    /**
     * Return all rows in the given table as a Collection
     *
     * @param tableName the table name
     * @param orderBy   the column by which to order
     * @return all rows in the given table as a Collection
     */
    public Collection<Map<String, Object>> getAllObjectsAsList(final String tableName, final String orderBy) {

        final String select = new StringBuilder().append("select * from ").append(tableName).append(" order by ").append(orderBy).toString();
        final SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());

        return sjdbc.queryForList(select);
    }
}
