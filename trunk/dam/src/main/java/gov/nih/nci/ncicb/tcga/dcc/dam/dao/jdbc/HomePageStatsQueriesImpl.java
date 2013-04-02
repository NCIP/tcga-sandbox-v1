/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.HomePageStats;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.HomePageStatsQueries;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * JDBC implementation of HomePageStatsQueries
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class HomePageStatsQueriesImpl extends SimpleJdbcDaoSupport implements HomePageStatsQueries {

    private static final String DISEASE_ABBREVIATION = "disease_abbreviation";
    private static final String CASES_SHIPPED = "cases_shipped";
    private static final String CASES_WITH_DATA = "cases_with_data";
    private static final String DATE_LAST_UPDATED = "date_last_updated";
    
    private static final String DATE_FORMAT = "'YYYY-MM-DD HH24:MI:SS'";

    /**
     * Query to retrieve 'disease_abbreviation', 'case_count' and 'date_last_updated' for the HOME_PAGE_STATS table
     *
     * This query assumes that the HOME_PAGE_DRILLDOWN table is populated <b>before</b> the HOME_PAGE_STATS table
     */
    private static final String TOTAL_AND_DATE_LAST_UPDATED_QUERY = "select " +
            "-1 as cases_shipped, " +
            "d.disease_abbreviation, " +
            "to_char(max(date_added)," + DATE_FORMAT + ") as date_last_updated, " +
            "h.case_count as cases_with_data " +
            "from archive_info a , disease d, home_page_drilldown h " +
            "where a.is_latest = 1 " +
            "and a.deploy_status = 'Available' " +
            "and a.disease_id = d.disease_id " +
            "and h.header_name = 'Total' " +
            "and h.disease_abbreviation = d.disease_abbreviation " +
            "group by d.disease_abbreviation, h.case_count";

    /**
     * Query to insert a row in the HOME_PAGE_STATS table
     */
    private static final String INSERT_HOME_PAGE_STATS_QUERY = "insert into home_page_stats " +
            "(disease_abbreviation, cases_shipped, cases_with_data, date_last_updated) " +
            "values(?, ?, ?, to_date(?, " + DATE_FORMAT + "))";

    /**
     * Query to clear the HOME_PAGE_STATS table
     */
    private static final String CLEAR_HOME_PAGE_STATS_QUERY = "delete from home_page_stats";

    private ParameterizedRowMapper<HomePageStats> homePageStatsDownRowMapper;

    public HomePageStatsQueriesImpl() {

        homePageStatsDownRowMapper = new ParameterizedRowMapper<HomePageStats>() {

            public HomePageStats mapRow(final ResultSet resultSet, final int i) throws SQLException {

                return new HomePageStats(
                        resultSet.getString(DISEASE_ABBREVIATION),
                        resultSet.getInt(CASES_SHIPPED),
                        resultSet.getInt(CASES_WITH_DATA),
                        resultSet.getString(DATE_LAST_UPDATED)
                );
            }
        };
    }

    /**
     * Populate HOME_PAGE_STATS table
     *
     * @param stats a map of stats containing shipped case counts already populated
     */
    @Override
    public void populateTable(final Map<String, HomePageStats> stats) {

        //Clear all data
        clearAllData();

        //Populate
        getHomePageStatsWithTotalAndDateLastUpdated(stats);

        final List<Object[]> batchParametersList = new LinkedList<Object[]>();
        for(final HomePageStats homePageStats : stats.values()) {

            //Add parameters to the batchParametersList
            final Object[] batchParameters = new Object[4];
            batchParameters[0] = homePageStats.getDiseaseAbbreviation();
            batchParameters[1] = homePageStats.getCasesShipped();
            batchParameters[2] = homePageStats.getCasesWithData();
            batchParameters[3] = homePageStats.getDateLastUpdated();

            batchParametersList.add(batchParameters);
        }

        getSimpleJdbcTemplate().batchUpdate(INSERT_HOME_PAGE_STATS_QUERY, batchParametersList);

    }

    /**
     * Clear the HOME_PAGE_STATS table
     */
    private void clearAllData() {
        getSimpleJdbcTemplate().update(CLEAR_HOME_PAGE_STATS_QUERY);
    }


    /**
     * Adds casesWithData and dateUpdated to stats objects.
     *
     * @param stats a map of existing stats
     */
    private void getHomePageStatsWithTotalAndDateLastUpdated(final Map<String, HomePageStats> stats) {
        final List<HomePageStats> totalAndDateStats = getSimpleJdbcTemplate().query(TOTAL_AND_DATE_LAST_UPDATED_QUERY, getHomePageStatsDownRowMapper());
        for (final HomePageStats tempStat : totalAndDateStats) {
            HomePageStats statForDisease = stats.get(tempStat.getDiseaseAbbreviation());
            if (statForDisease == null) {
                statForDisease = tempStat;
                statForDisease.setCasesShipped(0);
                stats.put(statForDisease.getDiseaseAbbreviation(), statForDisease);
            } else {
                statForDisease.setDateLastUpdated(tempStat.getDateLastUpdated());
                statForDisease.setCasesWithData(tempStat.getCasesWithData());
            }
        }
    }

    public ParameterizedRowMapper<HomePageStats> getHomePageStatsDownRowMapper() {
        return homePageStatsDownRowMapper;
    }
}
