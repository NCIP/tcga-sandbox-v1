/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.HomePageStats;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.HomePageStatsQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.JsonFileUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Service implementation class to populate HOME_PAGE_STATS table
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class HomePageStatsServiceImpl implements HomePageStatsService {

    @Autowired
    private
    HomePageStatsQueries homePageStatsQueries;

    @Autowired
    private JsonFileUtils jsonFileUtils;

    private String jsonDir;

    /**
     * Populate HOME_PAGE_STATS table
     */
    @Override
    public void populateTable() throws HomePageStatsServiceException {
        final Map<String, HomePageStats> stats;
        try {
            stats = getShippedCountStats();
            homePageStatsQueries.populateTable(stats);
        } catch (ParseException e) {
            throw new HomePageStatsServiceException(e.getMessage(), e);
        }
    }

    private Map<String, HomePageStats> getShippedCountStats() throws ParseException, HomePageStatsServiceException {
        final File latestJsonFile = jsonFileUtils.getLatestJsonFile(new File(jsonDir),
                Pattern.compile("NWCH-BCR_(\\d\\d-\\d\\d\\d\\d)\\.json"), new SimpleDateFormat("MM-yyyy"));

        try {
            JSONObject jsonObject = jsonFileUtils.getJsonObjectFromFile(new File(jsonDir, latestJsonFile.getName()));

            final Map<String, HomePageStats> statsMap = new HashMap<String, HomePageStats>();
            JSONArray jsonDiseaseArray = jsonObject.getJSONArray("case_summary_by_disease");
            for (int i=0; i<jsonDiseaseArray.size(); i++) {
                JSONObject diseaseStats = jsonDiseaseArray.getJSONObject(i);
                String disease = diseaseStats.getString("tumor_abbrev");
                int shipped = 0;
                try {
                    shipped = diseaseStats.getInt("shipped");
                } catch (JSONException e) {
                    // means the number isn't there or is not integer, so just keep 0
                }

                statsMap.put(disease, new HomePageStats(disease, shipped, 0, null));
            }
            return  statsMap;
        } catch (IOException e) {
           throw new HomePageStatsServiceException(e.getMessage(), e);
        }
    }

    /**
     * For unit tests
     *
     * @param homePageStatsQueries the HomePageStatsQueries to set
     */
    public void setHomePageStatsQueries(final HomePageStatsQueries homePageStatsQueries) {
        this.homePageStatsQueries = homePageStatsQueries;
    }

    public void setJsonFileUtils(final JsonFileUtils jsonFileUtils) {
        this.jsonFileUtils = jsonFileUtils;
    }

    public void setJsonDir(final String jsonDir) {
        this.jsonDir = jsonDir;
    }

    public class HomePageStatsServiceException extends Exception {
        public HomePageStatsServiceException(final String message, final Exception cause) {
            super(message, cause);
        }

    }
}
