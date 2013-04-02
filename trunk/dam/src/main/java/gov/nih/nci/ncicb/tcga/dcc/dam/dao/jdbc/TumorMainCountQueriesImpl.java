/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.stats.TumorMainCount;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.TumorMainCountQueries;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * JDBC implementation of TumorMainCountQueries
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class TumorMainCountQueriesImpl extends SimpleJdbcDaoSupport implements TumorMainCountQueries {

    /**
     * Fields selected by the ALL_TUMOR_MAIN_COUNT_QUERY query
     */
    private final static String DISEASE_NAME = "disease_name";
    private final static String DISEASE_ABBREVIATION = "disease_abbreviation";
    private final static String CASES_SHIPPED = "cases_shipped";
    private final static String CASES_WITH_DATA = "cases_with_data";
    private final static String DATE_LAST_UPDATED_STRING = "date_last_updated_string";

    /**
     * Return all rows needed for the Data Portal home page main table
     */
    private static final String ALL_TUMOR_MAIN_COUNT_QUERY = "SELECT " +
            "d.disease_name, " +
            "h.disease_abbreviation, " +
            "h.cases_shipped, " +
            "h.cases_with_data, " +
            "to_char(h.date_last_updated,'MM/DD/YY') as date_last_updated_string " +
            "FROM home_page_stats h, disease d " +
            "WHERE h.disease_abbreviation = d.disease_abbreviation ";

    private ParameterizedRowMapper<TumorMainCount> tumorMainCountRowMapper;

    public TumorMainCountQueriesImpl() {

        tumorMainCountRowMapper = new ParameterizedRowMapper<TumorMainCount>() {

            public TumorMainCount mapRow(final ResultSet resultSet, final int i) throws SQLException {

                return new TumorMainCount(
                        resultSet.getString(DISEASE_NAME),
                        resultSet.getString(DISEASE_ABBREVIATION),
                        resultSet.getInt(CASES_SHIPPED),
                        resultSet.getInt(CASES_WITH_DATA),
                        resultSet.getString(DATE_LAST_UPDATED_STRING)
                );
            }
        };
    }

    /**
     * Return a list of TumorMainCount sorted by disease name
     *
     * @return a list of TumorMainCount sorted by disease name
     * @throws gov.nih.nci.ncicb.tcga.dcc.dam.dao.TumorMainCountQueries.TumorMainCountQueriesException
     *
     */
    @Override
    public List<TumorMainCount> getAllTumorMainCount() throws TumorMainCountQueriesException {

        final List<TumorMainCount> result = getSimpleJdbcTemplate().query(ALL_TUMOR_MAIN_COUNT_QUERY, tumorMainCountRowMapper);
        Collections.sort(result);

        return result;
    }
}
