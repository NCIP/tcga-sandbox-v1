/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import static junit.framework.Assert.assertEquals;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.HomePageStats;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.HomePageStatsQueries;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import gov.nih.nci.ncicb.tcga.dcc.dam.util.JsonFileUtils;
import net.sf.json.JSONObject;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;

/**
 * HomePageStatsServiceImpl unit tests
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class HomePageStatsServiceImplFastTest {

    private Mockery context = new JUnit4Mockery();
    private HomePageStatsQueries mockHomePageStatsQueries;
    private HomePageStatsServiceImpl homePageStatsService;
    private JsonFileUtils mockJsonFileUtils;

    @Before
    public void setUp() {
        mockJsonFileUtils = context.mock(JsonFileUtils.class);
        mockHomePageStatsQueries = context.mock(HomePageStatsQueries.class);
        homePageStatsService = new HomePageStatsServiceImpl();
        homePageStatsService.setHomePageStatsQueries(mockHomePageStatsQueries);
        homePageStatsService.setJsonFileUtils(mockJsonFileUtils);
    }

    @Test
    public void testPopulateTable() throws Exception {

        final File jsonDir = new File("dir");
        final File latestJsonFile = new File("fake");
        final File latestJsonFileWithPath = new File("dir/fake");

        homePageStatsService.setJsonDir("dir");

        final JSONObject jsonObject = JSONObject.fromObject("{" +
                "\"version\" : \"bcr-102412/DCC\",\n" +
                "   \"timestamp\" : \"10/26/12\",\n" +
                "   \"case_summary_by_disease\" : [\n" +
                "      {\n" +
                "         \"pending_shipment\" : 16,\n" +
                "         \"dq_init_screen\" : 30,\n" +
                "         \"qualified_hold\" : 83,\n" +
                "         \"dq_path\" : 174,\n" +
                "         \"dq_genotype\" : 27,\n" +
                "         \"pending_init_screen\" : 0,\n" +
                "         \"submitted_to_bcr\" : 1441,\n" +
                "         \"tumor_abbrev\" : \"BRCA\",\n" +
                "         \"pending_path_qc\" : 17,\n" +
                "         \"shipped\" : 919,\n" +
                "         \"pending_mol_qc\" : 3,\n" +
                "         \"qual_pass_rate\" : 0.72,\n" +
                "         \"annotated\" : 20,\n" +
                "         \"qual_mol\" : 1045,\n" +
                "         \"qual_path\" : 1250,\n" +
                "         \"dq_other\" : 0,\n" +
                "         \"dq_mol\" : 202,\n" +
                "         \"total_cases_rcvd\" : 1471\n" +
                "      },\n" +
                "      {\n" +
                "         \"dq_init_screen\" : 4,\n" +
                "         \"qualified_hold\" : 2,\n" +
                "         \"dq_path\" : 199,\n" +
                "         \"dq_genotype\" : 27,\n" +
                "         \"pending_init_screen\" : 0,\n" +
                "         \"submitted_to_bcr\" : 1017,\n" +
                "         \"tumor_abbrev\" : \"OV\",\n" +
                "         \"pending_path_qc\" : 0,\n" +
                "         \"shipped\" : 572,\n" +
                "         \"pending_mol_qc\" : 0," +
                "         \"annotated\" : 30,\n" +
                "         \"qual_mol\" : 602,\n" +
                "         \"qual_path\" : 818,\n" +
                "         \"dq_other\" : 3,\n" +
                "         \"dq_mol\" : 216,\n" +
                "         \"total_cases_rcvd\" : 1021\n" +
                "      }]}"
        );
        

        final Map<String, Integer> expectedValuesInStats = new HashMap<String, Integer>();
        expectedValuesInStats.put("BRCA", 919);
        expectedValuesInStats.put("OV", 572);

        context.checking(new Expectations() {{
            one(mockJsonFileUtils).getLatestJsonFile(with(jsonDir), with(any(Pattern.class)),
                    with(any(SimpleDateFormat.class)));
            will(returnValue(latestJsonFile));

            one(mockJsonFileUtils).getJsonObjectFromFile(latestJsonFileWithPath);
            will(returnValue(jsonObject));

            allowing(mockHomePageStatsQueries).populateTable(with(expectedMap(expectedValuesInStats)));
        }});

        homePageStatsService.populateTable();

    }

    private Matcher<Map<String, HomePageStats>> expectedMap(final Map<String, Integer> expectedValuesInStats) {
        return new TypeSafeMatcher<Map<String, HomePageStats>>() {
            @Override
            public boolean matchesSafely(final Map<String, HomePageStats> stringHomePageStatsMap) {
                for (final String disease : expectedValuesInStats.keySet()) {
                    HomePageStats diseaseStats = stringHomePageStatsMap.get(disease);
                    assertEquals(expectedValuesInStats.get(disease), diseaseStats.getCasesShipped());
                }

                return true;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("checks stats for expected values");
            }
        };
    }
}
