/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.HomePageStats;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

/**
 * HomePageStatsQueriesImpl unit tests
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class HomePageStatsQueriesImplSlowTest extends DBUnitTestCase {

    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    private static final String PATH_TO_DB_PROPERTIES = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;

    private static final String DB_PROPERTIES_FILE = "dccCommon.unittest.properties";

    private static final String DATA_FILE = "portal" + FILE_SEPARATOR + "dao" + FILE_SEPARATOR + "HomePageStatsData.xml";

    private HomePageStatsQueriesImpl homePageStatsQueries;

    public HomePageStatsQueriesImplSlowTest() {
        super(PATH_TO_DB_PROPERTIES, DATA_FILE, DB_PROPERTIES_FILE);
    }

    @Before
    public void setUp() throws Exception {

        super.setUp();

        homePageStatsQueries = new HomePageStatsQueriesImpl();
        homePageStatsQueries.setDataSource(getDataSource());
    }

    @Override
    public DatabaseOperation getTearDownOperation() {
        return DatabaseOperation.DELETE_ALL;
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testPopulateTable() {

        assertEquals("Unexpected row count: ", 0, getHomePageStatsRowCount());

        final Map<String, HomePageStats> statsMap = new HashMap<String, HomePageStats>();
        final HomePageStats gbmStats = new HomePageStats("GBM", 5, 0, null);
        statsMap.put("GBM", gbmStats);
        homePageStatsQueries.populateTable(statsMap);

        assertEquals("Unexpected row count: ", 2, getHomePageStatsRowCount());

        final List<HomePageStats> actualHomePageStatsList = getHomePageStatsList();
        checkHomePageStatsList(actualHomePageStatsList);
    }

    private int getHomePageStatsRowCount() {
        return getSimpleJdbcTemplate().queryForInt("select count(*) from HOME_PAGE_STATS");
    }

    private List<HomePageStats> getHomePageStatsList() {
        return getSimpleJdbcTemplate().query("select * from HOME_PAGE_STATS order by DISEASE_ABBREVIATION", homePageStatsQueries.getHomePageStatsDownRowMapper());
    }

    /**
     * Check the List of HomePageStats against expected values
     *
     * @param homePageStatsList the List of HomePageStats
     */
    private void checkHomePageStatsList(final List<HomePageStats> homePageStatsList) {

        assertNotNull(homePageStatsList);
        assertEquals("Unexpected row count: ", 2, homePageStatsList.size());

        final String[] homePageStatsExpectedValues1 = {"GBM", "5", "409", "2010-10-25 00:00:00"};
        chekHomePageStats(homePageStatsList.get(0), homePageStatsExpectedValues1);

        final String[] homePageStatsExpectedValues2 = {"LUSC", "0", "69", "2009-10-25 00:00:00"};
        chekHomePageStats(homePageStatsList.get(1), homePageStatsExpectedValues2);
    }

    /**
     * Check the given HomePageStats against expected values
     *
     * @param homePageStats the HomePageStats
     * @param expectedValues the HomePageStats expected values (length must be 4)
     */
    private void chekHomePageStats(final HomePageStats homePageStats, final String[] expectedValues) {

        assertEquals("Unexpected number of values: ", 4, expectedValues.length);

        assertEquals("Unexpected Disease Abbreviation: ", expectedValues[0], homePageStats.getDiseaseAbbreviation());
        assertEquals("Unexpected Cases Shipped: ", new Integer(expectedValues[1]), homePageStats.getCasesShipped());
        assertEquals("Unexpected Cases With Data: ", new Integer(expectedValues[2]), homePageStats.getCasesWithData());
        assertTrue("Unexpected DateLastUpdated: " + homePageStats.getDateLastUpdated(), homePageStats.getDateLastUpdated().contains(expectedValues[3]));
    }
}
