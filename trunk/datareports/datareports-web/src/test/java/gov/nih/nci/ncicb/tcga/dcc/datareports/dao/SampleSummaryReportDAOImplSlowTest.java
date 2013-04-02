/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.util.GetterMethod;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Sample;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.SampleSummary;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.jdbc.SampleSummaryReportDAOImpl;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.CGCC;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.GBM;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.OV;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.BCR_SENT;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.BCR_UNKNOWN;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.CENTER_SENT;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.CENTER_UNKNOWN;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.LEVEL1_SS;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.LEVEL2_SS;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.LEVEL3_SS;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.QUERY_TOTAL_SAMPLES_BCR_SENT;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.QUERY_TOTAL_SAMPLES_CGCC_SENT;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.QUERY_TOTAL_SAMPLES_GSC_SENT;

/**
 * Test our DAO against known values in the DB
 *
 * @author Jon Whitmore Last updated by: $
 * @version $
 */
public class SampleSummaryReportDAOImplSlowTest extends DatareportDBUnitConfig {

    /** the smalled number of columns a center may have data data for * */
    private final static int MIN_COLUMNS = 9;

    /** The largest possible number of columns a center may have data for * */
    private final static int MAX_COLUMNS = 15;

    /** allGSC and CGCC names */
    private final List<String> allGSCAndCGCCNames = Arrays.asList(
            "broad.mit.edu",
            "genome.wustl.edu",
            "hgsc.bcm.edu",
            "hms.harvard.edu",
            "hudsonalpha.org",
            "jhu-usc.edu",
            "lbl.gov",
            "mskcc.org",
            "unc.edu");

    private List<Map<String,Object>> getTotalSamplesGSCSent(){
        SimpleJdbcDaoSupport dao = new SimpleJdbcDaoSupport();
        dao.setDataSource(getDataSource());
        return dao.getJdbcTemplate().queryForList(QUERY_TOTAL_SAMPLES_GSC_SENT);
    }

    private List<Map<String,Object>> getTotalSamplesCGCCSent(){
        SimpleJdbcDaoSupport dao = new SimpleJdbcDaoSupport();
        dao.setDataSource(getDataSource());
        return dao.getJdbcTemplate().queryForList(QUERY_TOTAL_SAMPLES_CGCC_SENT);
    }

    private List<Map<String,Object>> getTotalSamplesBCRSent(){
        SimpleJdbcDaoSupport dao = new SimpleJdbcDaoSupport();
        dao.setDataSource(getDataSource());
        return dao.getJdbcTemplate().queryForList(QUERY_TOTAL_SAMPLES_BCR_SENT);
    }

    /**
     * Thoroughly test the query behind the Sample Summary table on the DataSummary.htm page. Note: this table only
     * contains CGCCs and GSCs
     */
    public void testSampleSummaryQuery() throws Exception {
        SampleSummaryReportDAO impl = new SampleSummaryReportDAOImpl();
        impl.setDataSource(getDataSource());

        List<SampleSummary> ovRows = impl.getSampleSummaryRows(OV);
        List<SampleSummary> gbmRows = impl.getSampleSummaryRows(GBM);

        assertEquals(6, ovRows.size());
        assertEquals(11, gbmRows.size());
    
        //data integrity test: do we get the correct amount of samples for broad.mit.edu with from GSC
        assertEquals(BigDecimal.valueOf(1L), getTotalSamplesGSCSent().get(0).get("samples"));
        //data integrity test: do we get the correct amount of samples for broad.mit.edu with from CGCC
        assertEquals(BigDecimal.valueOf(1L), getTotalSamplesCGCCSent().get(0).get("samples"));
        //data integrity test: do we get the correct amount of samples for broad.mit.edu with from BCR
        assertEquals(BigDecimal.valueOf(2L), getTotalSamplesBCRSent().get(0).get("samples"));

        List<SampleSummary> allRows = impl.getSampleSummaryRows();

        // The following totals were calculated from the database
        assessTotalForColumn(allRows, "totalBCRSent", 14);
        assessTotalForColumn(allRows, "totalCenterSent", 16);
        assessTotalForColumn(allRows, "totalLevelOne", 7);
        assessTotalForColumn(allRows, "totalLevelTwo", 4);
        assessTotalForColumn(allRows, "totalLevelThree", 5);
        assessTotalForColumn(allRows, "totalBCRUnaccountedFor", 14);
        assessTotalForColumn(allRows, "totalCenterUnaccountedFor", 2);

        ensureAllCenterNamesOccurAtLeastOnce(allGSCAndCGCCNames, allRows);
        ensureTotalsAreReasonable(allRows);
    }

    /**
     * Verify that the total sent by the BCR is greater than or equal all other totals
     *
     * @param rows a number of rows from the sample summary query
     */
    private void ensureTotalsAreReasonable(List<SampleSummary> rows) throws Exception {
        for (SampleSummary ss : rows) {
            Long bcrSent = ss.getTotalBCRSent();
            // There are lots of OV rows in this table with nothing to report
            // because they haven't been sent
            if (bcrSent == null) {
                continue;
            }
            int bcr = bcrSent.intValue();
            if (bcr == 0) {
                continue;
            }
            List<String> lessThans = Arrays.asList("totalLevelOne", "totalLevelTwo", "totalLevelThree");
            for (String columnLessThan : lessThans) {
                ensureLessThanOrEqual(ss, columnLessThan, bcr, "totalCenterSent");
            }
        }

    }

    private void ensureLessThanOrEqual(SampleSummary row, String getter,
                                       int larger, String largerName) throws Exception {
        final Long lessOrEqual = (Long)GetterMethod.getGetter(row.getClass(), getter).invoke(row);
        if (lessOrEqual != null) {
            assertTrue(getter + " is higher than " + largerName, larger >= lessOrEqual.intValue());
        }
    }

    /**
     * Make sure that <code>allGSCAndCGCCNames</code> represents the set of center names in the DB
     *
     * @param allRows A List of Maps representing the rows from the DB
     */
    private void ensureAllCenterNamesOccurAtLeastOnce(List<String> expectedCenters,
                                                      List<SampleSummary> allRows) {
        Set<String> centers = new TreeSet<String>();
        for (SampleSummary ss : allRows) {
            assertTrue(expectedCenters.contains(ss.getCenterName().toString()));
            centers.add(ss.getCenterName().toString());
        }
        for (String name : expectedCenters) {
            assertTrue(centers.contains(name));
        }
    }

    /**
     * Assert the total of total_centers_sent is as expected
     *
     * @param getter the column to count
     * @param expectedTotal the sum of the column we think we'll find
     * @param allRows a List of Maps representing rows in the DB
     */
    private void assessTotalForColumn(List<SampleSummary> allRows, String getter, int expectedTotal)
            throws Exception {
        int total = 0;
        for (SampleSummary ss : allRows) {
            String tcs = GetterMethod.getGetter(ss.getClass(), getter).invoke(ss).toString();
            total += Integer.parseInt(tcs);
        }
        assertEquals("Total for column: " + getter + " didn't match expected.", expectedTotal, total);
    }

    /**
     * This method asserts that all values in the expectedValues map are also in the queryResultMap.
     *
     * @param expectedValues a predefined map of columns with known correct values
     * @param queryResultValues a map of columns pulled from the DB
     */
    private void assessPresenceOfExpectedColumns(Map expectedValues, Map queryResultValues) {
        for (Object expectedKey : expectedValues.keySet()) {
            assertEquals(expectedValues.get(expectedKey).toString(), queryResultValues.get(expectedKey).toString());
        }
    }

    private int countForColumn(SampleSummaryReportDAO impl, String getter, String diseaseAbbr,
        String centerName,String centerType, String portionAnalyte, String platform) throws Exception {
        List<SampleSummary> summaries = impl.getSampleSummaryRows(diseaseAbbr);
        for (SampleSummary ss : summaries) {
            if (platform != null) {
                if (ss.getDisease().equals(diseaseAbbr)
                        && ss.getCenterName().equals(centerName)
                        && ss.getCenterType().equals(centerType)
                        && ss.getPortionAnalyte().equals(portionAnalyte)
                        && ss.getPlatform().equals(platform)) {
                    return Integer.parseInt(GetterMethod.getGetter(ss.getClass(), getter).invoke(ss).toString());
                }
            } else {
                if (ss.getDisease().equals(diseaseAbbr)
                        && ss.getCenterName().equals(centerName)
                        && ss.getCenterType().equals(centerType)
                        && ss.getPortionAnalyte().equals(portionAnalyte)) {
                    return Integer.parseInt(GetterMethod.getGetter(ss.getClass(), getter).invoke(ss).toString());
                }
            }
        }
        fail(sampleParamLabel(diseaseAbbr,centerName,centerType,portionAnalyte,platform)+
                "could not find the correct column");
        return 0;
    }

    private String sampleParamLabel(String diseaseAbbr, String centerName, String centerType,
            String portionAnalyte, String platform){
        return  diseaseAbbr+" - "+
                centerName+" - "+
                centerType+" - "+
                portionAnalyte+" - "+
                platform+"\n";
    }

    private void sampleTestingForSamplesBCRSent(
            SampleSummaryReportDAO impl,
            String columnName, String tumorAbbr, String centerName,
            String centerType,
            String portionAnalyte) throws Exception {
        List<Sample> samples = impl.getSamplesForTotalSamplesBCRSent(tumorAbbr, centerName, centerType, portionAnalyte);
        assertNotNull(sampleParamLabel(tumorAbbr,centerName,centerType,portionAnalyte,"")+
                "return value not null", samples);
        int querySize = samples.size();
        int tableSize = countForColumn(impl, columnName, tumorAbbr, centerName, centerType, portionAnalyte, null);
        assertEquals(sampleParamLabel(tumorAbbr,centerName,centerType,portionAnalyte,"")+
                "sample summary detail table column should match", querySize, tableSize);
    }

    private void sampleTestingForSamplesDCCRecieved(
            SampleSummaryReportDAO impl,
            String columnName, String tumorAbbr, String centerName,
            String centerType,
            String portionAnalyte,
            String platform) throws Exception {
        List<Sample> samples = impl.getSamplesForTotalSamplesCenterSent(tumorAbbr, centerName, centerType, portionAnalyte, platform);
        assertNotNull(sampleParamLabel(tumorAbbr,centerName,centerType,portionAnalyte,platform)+
                "return value not null", samples);
        int querySize = samples.size();
        int tableSize = countForColumn(impl, columnName, tumorAbbr, centerName, centerType, portionAnalyte, platform);
        assertEquals(sampleParamLabel(tumorAbbr,centerName,centerType,portionAnalyte,platform)+
                "sample summary detail table column should match", querySize, tableSize);
    }

    private void sampleTestingForSamplesUnaccountedFor(
            SampleSummaryReportDAO impl,
            String columnName, String tumorAbbr, String centerName,
            String centerType,
            String portionAnalyte,
            String platform) throws Exception {
        List<Sample> samples = impl.getSamplesForTotalSamplesUnaccountedForBCR(tumorAbbr, centerName, centerType, portionAnalyte, platform);
        assertNotNull(sampleParamLabel(tumorAbbr,centerName,centerType,portionAnalyte,platform)+
                "return value not null", samples);
        int querySize = samples.size();
        int tableSize = countForColumn(impl, columnName, tumorAbbr, centerName, centerType, portionAnalyte, platform);
        assertEquals(sampleParamLabel(tumorAbbr,centerName,centerType,portionAnalyte,platform)+
                "sample summary detail table column should match", querySize, tableSize);
    }

    private void sampleTestingForSamplesNotSentFromBCR(
            SampleSummaryReportDAO impl,
            String columnName, String tumorAbbr, String centerName,
            String centerType,
            String portionAnalyte,
            String platform) throws Exception {
        List<Sample> samples = impl.getSamplesForTotalSamplesUnaccountedForCenter(tumorAbbr, centerName, centerType, portionAnalyte,platform);
        assertNotNull(sampleParamLabel(tumorAbbr,centerName,centerType,portionAnalyte,platform)+
                "return value not null", samples);
        int querySize = samples.size();
        int tableSize = countForColumn(impl, columnName, tumorAbbr, centerName, centerType, portionAnalyte, platform);
        assertEquals(sampleParamLabel(tumorAbbr,centerName,centerType,portionAnalyte,platform)+
                "sample summary detail table column should match", querySize, tableSize);
    }

    private void sampleTestingForLevel(
            SampleSummaryReportDAO impl,
            String columnName, String tumorAbbr, String centerName,
            String centerType,
            String portionAnalyte,
            String platform,
            int level) throws Exception {
        List<Sample> samples = impl.getSamplesForLevelTotal(tumorAbbr, centerName, centerType, portionAnalyte, platform, level);
        assertNotNull(sampleParamLabel(tumorAbbr,centerName,centerType,portionAnalyte,platform)+
                "return value not null", samples);
        int querySize = samples.size();
        int tableSize = countForColumn(impl, columnName, tumorAbbr, centerName, centerType, portionAnalyte, platform);
        assertEquals(sampleParamLabel(tumorAbbr,centerName,centerType,portionAnalyte,platform)+
                "sample summary detail table column should match", querySize, tableSize);
    }

    /** Test the first column of totals by this name.  AKA SamplesBCRSent */
    public void testSampleIDsBCRReportedSendingToCenterQuery() throws Exception {
        SampleSummaryReportDAO impl = new SampleSummaryReportDAOImpl();
        impl.setDataSource(getDataSource());
        sampleTestingForSamplesBCRSent(impl, BCR_SENT, GBM, "unc.edu", CGCC, "T");
    }

    /** Test the second column of totals by this name.  AKA SamplesDCCRecieved */
    public void testSampleIDsDCCReceivedFromCenterQuery() throws Exception {
        SampleSummaryReportDAO impl = new SampleSummaryReportDAOImpl();
        impl.setDataSource(getDataSource());
        sampleTestingForSamplesDCCRecieved(impl, CENTER_SENT, GBM, "broad.mit.edu", CGCC, "D", "HG-U133A_2");
        sampleTestingForSamplesDCCRecieved(impl, CENTER_SENT, GBM, "hms.harvard.edu", CGCC, "T", "HG-U133A_2");
    }

    /** Test the third column of totals by this name.  AKA SamplesUnaccountedFor */
    public void testUnaccountedForBCRSampleIDsThatCenterReportedQuery() throws Exception {
        SampleSummaryReportDAO impl = new SampleSummaryReportDAOImpl();
        impl.setDataSource(getDataSource());
        sampleTestingForSamplesUnaccountedFor(impl, BCR_UNKNOWN, GBM, "broad.mit.edu", CGCC, "D", "HG-U133A_2");
        sampleTestingForSamplesUnaccountedFor(impl, BCR_UNKNOWN, GBM, "hms.harvard.edu", CGCC, "T", "HG-U133A_2");
    }

    /** Tese the fourth column of totals by this name.  AKA SamplesNotSentFromBCR */
    public void testUnaccountedForCenterSampleIDsThatBCRReportedQuery() throws Exception {
        SampleSummaryReportDAO impl = new SampleSummaryReportDAOImpl();
        impl.setDataSource(getDataSource());
        sampleTestingForSamplesNotSentFromBCR(impl, CENTER_UNKNOWN, GBM, "broad.mit.edu", CGCC, "D","HG-U133A_2");
        sampleTestingForSamplesNotSentFromBCR(impl, CENTER_UNKNOWN, GBM, "hms.harvard.edu", CGCC, "T","HG-U133A_2");
    }

    /** Tese the fifth column of totals by this name.  AKA SamplesWithLevel1 */
    public void testSampleIDsWithLevel1DataQuery() throws Exception {
        SampleSummaryReportDAO impl = new SampleSummaryReportDAOImpl();
        impl.setDataSource(getDataSource());
        sampleTestingForLevel(impl, LEVEL1_SS, GBM, "broad.mit.edu", CGCC, "D", "HG-U133A_2", 1);
        sampleTestingForLevel(impl, LEVEL1_SS, GBM, "hms.harvard.edu", CGCC, "T", "HG-U133A_2", 1);
    }

    /** Tese the sixth column of totals by this name.  AKA SamplesWithLevel2 */
    public void testSampleIDsWithLevel2DataQuery() throws Exception {
        SampleSummaryReportDAO impl = new SampleSummaryReportDAOImpl();
        impl.setDataSource(getDataSource());
        sampleTestingForLevel(impl, LEVEL2_SS, GBM, "broad.mit.edu", CGCC, "D", "HG-U133A_2", 2);
        sampleTestingForLevel(impl, LEVEL2_SS, GBM, "hms.harvard.edu", CGCC, "T", "HG-U133A_2", 2);
    }

    /** Tese the seventh column of totals by this name.  AKA SamplesWithLevel3 */
    public void testSampleIDsWithLevel3DataQuery() throws Exception {
        SampleSummaryReportDAO impl = new SampleSummaryReportDAOImpl();
        impl.setDataSource(getDataSource());
        sampleTestingForLevel(impl, LEVEL3_SS, GBM, "broad.mit.edu", CGCC, "D", "HG-U133A_2", 3);
        sampleTestingForLevel(impl, LEVEL3_SS, GBM, "hms.harvard.edu", CGCC, "T", "HG-U133A_2", 3);
    }

   
}//End of Class
