package gov.nih.nci.ncicb.tcgaportal.level4.dao.jdbc;

import gov.nih.nci.ncicb.tcgaportal.level4.dao.QueriesException;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.dbunit.Level4QueriesJDBCImplDBUnitConfig;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.AnomalySearchConstants;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterChromRegion;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.ResultRow;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.AnomalyResultRatio;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultBlank;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultDouble;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultValue;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.AggregateMutationType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.AnomalyType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.CopyNumberType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.CorrelationType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ExpressionType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.MethylationType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.MutationType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.NonMutationAnomalyType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.UpperAndLowerLimits;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.pathway.SinglePathwayResults;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.pathway.SinglePathwaySpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.util.FishersExactImpl;
import gov.nih.nci.ncicb.tcgaportal.level4.util.PearsonCorrelationCalculator;
import gov.nih.nci.ncicb.tcgaportal.util.ProcessLogger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for Level4QueriesJDBCImpl
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class Level4QueriesJDBCImplSlowTest extends Level4QueriesJDBCImplDBUnitConfig {

    FilterSpecifier filter;
    MockLevel4QueriesJDBCImpl queries;

    static String testDbFileName_Gene = "Level4Queries.xml";

    public Level4QueriesJDBCImplSlowTest() {
        super(testDbFileName_Gene);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        queries = new MockLevel4QueriesJDBCImpl(dataSource);
        queries.setLogger(new ProcessLogger());
        queries.setFetchSize(1000);
        queries.setCorrelationCalculator(new PearsonCorrelationCalculator());
    }

    private void setupPathwaySearch(final String geneList) throws QueriesException {
        filter = new FilterSpecifier();
        filter.setDisease("GBM");
        List<ColumnType> columnTypes = queries.getColumnTypes("GBM");
        filter.setColumnTypes(columnTypes);

        if (geneList != null) {
            filter.setGeneList(geneList);
            filter.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
        } else {
            filter.setGeneListOptions(FilterSpecifier.GeneListOptions.All);
        }

        filter.setListBy(FilterSpecifier.ListBy.Pathways);

        queries.setFishersExact(new FishersExactImpl());
    }

    private void setUpAnomalySearch() throws QueriesException {

        filter = new FilterSpecifier();
        filter.setDisease("GBM");
        filter.setListBy(FilterSpecifier.ListBy.Genes);
        List<ColumnType> columnTypes = queries.getColumnTypes("GBM");
        filter.setColumnTypes(columnTypes);

        filter.setPatientList("TCGA-02-0011,TCGA-02-0281,TCGA-08-0531,TCGA-02-0332,TCGA-02-0038"); // 5 patients
        filter.setPatientListOptions(FilterSpecifier.PatientListOptions.List);

        filter.setGeneList("C9orf152,CREB3L1"); // 2 genes... note lowercase should be converted
        filter.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
    }

    private void selectNonMutationType(double lower, double upper, double percent) {
        selectNonMutationType(lower, upper, percent, 10);
    }

    private void selectNonMutationType(double lower, double upper, double percent, Integer dataSetId) {
        // find first non-mutation type, or specific id if given

        for (ColumnType column : filter.getColumnTypes()) {
            if (column instanceof NonMutationAnomalyType && !column.isPicked() &&
                    ((AnomalyType) column).getGeneticElementType() == AnomalyType.GeneticElementType.Gene) {
                if (dataSetId == null || ((NonMutationAnomalyType) column).getDataSetId() == dataSetId) {
                    column.setPicked(true);
                    ((NonMutationAnomalyType) column).setLowerLimit(lower);
                    ((NonMutationAnomalyType) column).setUpperLimit(upper);
                    ((NonMutationAnomalyType) column).setLowerOperator(UpperAndLowerLimits.Operator.LT);
                    ((NonMutationAnomalyType) column).setUpperOperator(UpperAndLowerLimits.Operator.GT);
                    ((AnomalyType) column).setFrequency((float) percent);
                    return;
                }
            }
        }
        throw new RuntimeException("Data set isn't loaded?");
    }

    @Test
    public void testGetColumnTypes() throws QueriesException {
        List<ColumnType> columnTypes = queries.getColumnTypes("GBM");
        for (final ColumnType columnType : columnTypes) {
            if (columnType instanceof AnomalyType && ((AnomalyType) columnType).getAnomalyTypeId() == 1) {
                assertEquals("HMS HG-CGH-244A", columnType.getDisplayName());
            } else if (columnType instanceof CopyNumberType && ((CopyNumberType) columnType).getAnomalyTypeId() == 13) {
                assertEquals("BI Genome_Wide_SNP_6 log2 ratio", columnType.getDisplayName());
            } else if (columnType instanceof AggregateMutationType && ((AggregateMutationType) columnType).getCategory() == MutationType.Category.AnyNonSilent) {
                assertEquals("Any Non-Silent", columnType.getDisplayName());
            }
        }
    }

    @Test
    public void testWithNoColumns() throws Throwable {
        setUpAnomalySearch();
        Results results = runAnomalyFilter();
        // should be one row for each gene, and no data columns
        assertEquals(2, results.getActualRowCount());
        assertEquals(0, results.getRow(0).getColumnResults().length);
        assertEquals(0, results.getRow(1).getColumnResults().length);
    }

    @Test
    public void testNoColumnsWithChromosome() throws Throwable {
        filter = new FilterSpecifier();
        List<ColumnType> columnTypes = queries.getColumnTypes("GBM");
        filter.setColumnTypes(columnTypes);
        filter.setDisease("GBM");
        filter.setListBy(FilterSpecifier.ListBy.Genes);
        FilterChromRegion region = new FilterChromRegion("1", -1, -1);
        filter.addChromRegion(region);
        filter.setGeneListOptions(FilterSpecifier.GeneListOptions.Region);
        filter.setPatientListOptions(FilterSpecifier.PatientListOptions.All);

        Results results = runAnomalyFilter(filter);

        for (int i = 0; i < results.getActualRowCount(); i++) {
            ResultRow row = results.getRow(i);
            String chrom = (String) row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_CHROM);
            assertEquals(row.getName() + " is not on chrom 1!", "1", chrom);
        }


    }

    @Test
    public void testSingleGeneMutations() throws Throwable {
        // note: this test might fail once a new data set is loaded.  sorry.  This was to address a specific bug.
        filter = new FilterSpecifier();
        filter.setDisease("GBM");
        filter.setListBy(FilterSpecifier.ListBy.Genes);
        List<ColumnType> columnTypes = queries.getColumnTypes("GBM");
        filter.setColumnTypes(columnTypes);

        filter.setPatientListOptions(FilterSpecifier.PatientListOptions.All);

        filter.setGeneList("ABCC4");
        filter.setGeneListOptions(FilterSpecifier.GeneListOptions.List);

        for (ColumnType column : filter.getColumnTypes()) {
            if (column instanceof AggregateMutationType &&
                    ((AggregateMutationType) column).getCategory() == MutationType.Category.Silent) {
                column.setPicked(true);
                ((AnomalyType) column).setFrequency((float) 0.001);
            }
        }
        Results results = runAnomalyFilter();
        assertEquals(1, results.getActualRowCount());
        assertEquals(1, ((AnomalyResultRatio) results.getRow(0).getColumnResults()[0]).getTotal());
    }

    @Test
    public void testGetAnomalyResults() throws Throwable {
        setUpAnomalySearch();
        selectNonMutationType(-0.5, 0.5, .05);
        Results results = runAnomalyFilter();

        // should be 2 rows (one per gene), each cell should have total count = 5 (number of patients in list)
        assertEquals("Results should only have 1 row", new Integer(1), new Integer(results.getActualRowCount()));
        AnomalyResultRatio rr = (AnomalyResultRatio) results.getRow(0).getColumnResults()[0];
        assertEquals("Only 5 patients were selected, so the total for the result should be 5", 5, rr.getTotal());

        assertEquals("CREB3L1", results.getRow(0).getName());

        // make sure the annotations are there
        for (int i = 0; i < results.getActualRowCount(); i++) {
            assertNotNull(results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_CNV));
            assertNotNull(results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_CHROM));
            assertNotNull(results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_START));
            assertNotNull(results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_STOP));
        }
    }


    @Test
    public void testGetAnomalyResultsByPatient() throws Throwable {
        setUpAnomalySearch();
        selectNonMutationType(-0.5, 0.5, .05);

        // now do the search by patient
        filter.setListBy(FilterSpecifier.ListBy.Patients);
        Results results = runAnomalyFilter();
        assertEquals("Results should only have 1 row", new Integer(1), new Integer(results.getActualRowCount()));
        AnomalyResultRatio rr = (AnomalyResultRatio) results.getRow(0).getColumnResults()[0];
        assertEquals(1, rr.getTotal());
        assertEquals("TCGA-02-0038", results.getRow(0).getName());
    }

    @Test
    public void testPatientSearchTwoColumns() throws Throwable {
        setUpAnomalySearch();
        selectNonMutationType(-0.5, 0.5, .2);
        selectNonMutationType(-0.5, 0.5, .2, null); // select 2 columns
        filter.setListBy(FilterSpecifier.ListBy.Patients);
        Results results = runAnomalyFilter();
        assertNotNull(results);
        assertTrue(results.getActualRowCount() > 0);
        assertEquals(2, results.getRow(0).getColumnResults().length);
    }

    @Test
    public void testGistic() throws Throwable {
        double lowerLimit = 0.1;
        setUpAnomalySearch();

        boolean foundOne = false;
        for (ColumnType column : filter.getColumnTypes()) {
            if (column instanceof CopyNumberType && !foundOne) {
                column.setPicked(true);
                ((CopyNumberType) column).setCalculationType(CopyNumberType.CalculationType.GISTIC);
                ((NonMutationAnomalyType) column).setLowerOperator(UpperAndLowerLimits.Operator.GT);
                ((NonMutationAnomalyType) column).setLowerLimit(lowerLimit);
                foundOne = true;
            }
        }
        if (!foundOne) {
            throw new IllegalStateException("no copy number columns?");
        }

        Results results = runAnomalyFilter();
        // check that all results are either blanks or gistics
        for (int i = 0; i < results.getActualRowCount(); i++) {
            for (ResultValue value : results.getRow(i).getColumnResults()) {
                // all values should be GISTIC type
                assertTrue(value instanceof ResultDouble || value instanceof ResultBlank);
            }
        }
    }

    @Test
    public void testGetResultsWithLimits() throws Throwable {
        setUpAnomalySearch();
        selectNonMutationType(-0.5, 0.5, .05);

        // now set a constraint
        NonMutationAnomalyType anomalyType;
        for (ColumnType col : filter.getColumnTypes()) {
            if (col instanceof NonMutationAnomalyType) {
                anomalyType = (NonMutationAnomalyType) col;
                anomalyType.setLowerOperator(UpperAndLowerLimits.Operator.GT);
                anomalyType.setLowerLimit(100); // should yield zero matches since value so high
            }
        }

        Results results = runAnomalyFilter();
        assertNotNull(results);
        for (int i = 0; i < results.getActualRowCount(); i++) {
            for (ResultValue value : results.getRow(i).getColumnResults()) {
                assertTrue("no values should match ludicrous lower limit", ((AnomalyResultRatio) value).getAffected() == 0);
            }
        }
    }

    private void selectCorrelationColumn() {
        for (ColumnType column : filter.getColumnTypes()) {
            if (column instanceof CorrelationType) {
                column.setPicked(true);
                ((CorrelationType) column).setPvalueLimit(-1);
                return;
            }
        }
        throw new RuntimeException("There aren't any correlation types?");
    }

    @Test
    public void testGetCorrelationResults() throws Throwable {
        setUpAnomalySearch();
        selectCorrelationColumn();
        filter.setGeneListOptions(FilterSpecifier.GeneListOptions.All);

        Results results = runAnomalyFilter();
        assertNotNull(results);
        assertTrue(results.getActualRowCount() > 0);
        for (int i = 0; i < results.getActualRowCount(); i++) {
            for (ResultValue value : results.getRow(i).getColumnResults()) {
                assertTrue(value instanceof ResultDouble || value instanceof ResultBlank);
            }
        }
    }

    @Test
    public void testWithGeneRegion() throws Throwable {
        setUpAnomalySearch();
        FilterChromRegion region = new FilterChromRegion();
        region.setChromosome("1");
        int regionStart = 190000000;
        int regionStop = 210000000;
        region.setStart(regionStart);
        region.setStop(regionStop);

        filter.addChromRegion(region);
        filter.setGeneListOptions(FilterSpecifier.GeneListOptions.Region);
        selectNonMutationType(-0.5, 0.5, .05);

        Results results = runAnomalyFilter();
        assertNotNull(results);

        for (int i = 0; i < results.getActualRowCount(); i++) {
            // check that the genes listed are in the given region
            ResultRow row = results.getRow(i);
            String geneName = row.getName();
            List<Map<String, Object>> geneInfo = getGeneInfo(geneName);
            boolean matched = false;
            for (Map<String, Object> gene : geneInfo) {
                int start = Integer.valueOf(gene.get("start_pos").toString());
                int stop = Integer.valueOf(gene.get("stop_pos").toString());
                if (gene.get("chromosome").equals("1") &&
                        (start >= regionStart && start <= regionStop) ||
                        (stop >= regionStart && stop <= regionStop) ||
                        (start < regionStart && stop > regionStop)) {
                    matched = true;
                }
            }
            assertTrue("Gene " + row.getName() + " is not in queried region", matched);
        }
    }

    @Test
    public void testWithChromosome() throws Throwable {
        setUpAnomalySearch();
        // just set a chromosome but not a gene region
        FilterChromRegion region = new FilterChromRegion();
        region.setChromosome("19");
        filter.addChromRegion(region);
        filter.setGeneListOptions(FilterSpecifier.GeneListOptions.Region);
        selectNonMutationType(-0.5, 0.5, .05);

        Results results = runAnomalyFilter();
        assertNotNull(results);
        assertTrue(results.getActualRowCount() > 0);
        for (int i = 0; i < results.getActualRowCount(); i++) {
            ResultRow row = results.getRow(i);
            boolean matched = false;
            List<Map<String, Object>> geneInfo = getGeneInfo(row.getName());
            for (Map<String, Object> gene : geneInfo) {
                if (gene.get("chromosome").equals("19"))
                    matched = true;
            }
            assertTrue("Gene " + row.getName() + " is not on 19?", matched);
        }
    }

    List<Map<String, Object>> getGeneInfo(String geneName) {
        String query = "select chromosome, start_pos, stop_pos from L4_genetic_element where genetic_element_name=?";
        return queries.getSimpleJdbcTemplate().queryForList(query, geneName);
    }


    @Test
    public void testParseList() {
        testList(queries.parseList("1 2 3"));
        testList(queries.parseList("1,2,\n3"));
        testList(queries.parseList("1,2,3"));
        testList(queries.parseList("\n1\n2\n3\n"));
        testList(queries.parseList("1;2,3\n,,,"));

    }

    public void testList(String[] list) {
        assertEquals(3, list.length);
        assertEquals("1", list[0]);
        assertEquals("2", list[1]);
        assertEquals("3", list[2]);

    }

    public Results runAnomalyFilter() throws Throwable {
        return runAnomalyFilter(this.filter);
    }

    public Results runAnomalyFilter(FilterSpecifier filter) throws Throwable {
        MockCallback callback = new MockCallback();
        queries.getAnomalyResults(filter, callback);
        while (!callback.done) {
            Thread.sleep(1000);
        }
        if (callback.caughtException != null) {
            throw callback.caughtException;
        }
        return callback.results;
    }

    public Results runPathwayFilter() throws Throwable {
        MockCallback callback = new MockCallback();
        queries.getPathwayResults(filter, callback);
        while (!callback.done) {
            Thread.sleep(1000);
        }
        if (callback.caughtException != null) {
            throw callback.caughtException;
        }
        return callback.results;
    }


    @Test
    public void testMultiLocGene() throws Throwable {
        setUpAnomalySearch();
        filter.setPatientListOptions(FilterSpecifier.PatientListOptions.All);
        selectNonMutationType(0, 0, 0); // no filter
        // this gene has 2 locations in the db
        filter.setGeneList("PRG2");
        Results results = runAnomalyFilter();

        assertEquals("Results should have 2 rows!", new Integer(2), new Integer(results.getActualRowCount()));
        assertEquals(results.getRow(0).getName(), "PRG2");
        assertEquals(results.getRow(1).getName(), "PRG2");

        String loc1 = results.getRow(0).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_CHROM) + " " +
                results.getRow(0).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_START) + "-" +
                results.getRow(0).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_STOP);

        String loc2 = results.getRow(1).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_CHROM) + " " +
                results.getRow(1).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_START) + "-" +
                results.getRow(1).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_STOP);
        assertFalse(loc1.equals(loc2));
        assertNotSame(loc1, loc2);

        // now, do search by patient
        filter.setPatientListOptions(FilterSpecifier.PatientListOptions.List);
        filter.setListBy(FilterSpecifier.ListBy.Patients);
        results = runAnomalyFilter();
        assertEquals(2, ((AnomalyResultRatio) (results.getRow(0).getColumnResults()[0])).getTotal());

    }


    @Test
    public void testPathwaySearch() throws Throwable {
        setupPathwaySearch("CDK4");
        selectNonMutationType(-0.5, 0.5, .05);

        Results results = runPathwayFilter();
        assertNotNull(results);
        assertEquals(5, results.getActualRowCount()); // 5 pathways match the gene CDK4

        assertEquals("Filter was not set back to Pathway mode", FilterSpecifier.ListBy.Pathways, filter.getListBy());

        // each row should have an annotation with the pathway id and one with the fisher's exact value
        for (int i = 0; i < results.getActualRowCount(); i++) {
            assertNotNull("Row " + i + " does not have a pathway ID annotation",
                    results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_PATHWAYID));
            assertNotNull("Row " + i + " does not have a Fisher's Exact annotation",
                    results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_PATHWAY_FISHER));
        }
    }

    @Test
    public void testGetSinglePathwway() throws QueriesException {
        SinglePathwaySpecifier sps = new SinglePathwaySpecifier();
        sps.setId("57");
        setUpAnomalySearch();
        selectNonMutationType(-0.5, 0.5, .05);
        sps.setFilterSpecifier(filter);
        SinglePathwayResults results = queries.getSinglePathway(sps);
        assertNotNull(results);
        assertTrue(results.getTotalRowCount() > 0);
        assertTrue(results.getTotalRowCount() < 100); // sanity check on size of pathway...
        for (int i = 0; i < results.getTotalRowCount(); i++) {
            assertNotNull("Chromosome not in row annotation", results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_CHROM));
            assertNotNull("Start not in row annotation", results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_START));
            assertNotNull("Stop not in row annotation", results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_STOP));
            assertNotNull("Biocarta Symbol not in row annotation for " + results.getRow(i).getName(), results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_BCGENE));
            assertNotNull("Matched Search annotation not present", results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_MATCHED_SEARCH));
            assertNotNull("CNV annotation not present", results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_CNV));
            assertEquals("Result for row not present", 1, results.getRow(i).getColumnResults().length); // should be 1 result b/c only 1 column
        }
    }

    @Test
    public void testGetSinglePathwayByChrom() throws QueriesException {
        SinglePathwaySpecifier sps = new SinglePathwaySpecifier();
        sps.setId("57");
        filter = new FilterSpecifier();
        filter.setDisease("GBM");
        filter.setListBy(FilterSpecifier.ListBy.Genes);
        List<ColumnType> columnTypes = queries.getColumnTypes("GBM");
        filter.setColumnTypes(columnTypes);
        filter.setGeneListOptions(FilterSpecifier.GeneListOptions.Region);
        FilterChromRegion chrom2 = new FilterChromRegion();
        chrom2.setChromosome("2");
        filter.addChromRegion(chrom2);

        sps.setFilterSpecifier(filter);
        SinglePathwayResults results = queries.getSinglePathway(sps);
        assertNotNull(results);

        for (int i = 0; i < results.getTotalRowCount(); i++) {
            String chrom = results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_CHROM).toString();
            if ("2".equals(chrom)) {
                assertTrue("Result " + i + " is on chrom 2 but is not flagged as matching", (Boolean) results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_MATCHED_SEARCH));
            } else {
                assertFalse("Result " + i + " is not on chrom 2 but is flagged as matching (" + chrom + ")", (Boolean) results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_MATCHED_SEARCH));
            }
        }
    }

    @Test
    public void testPathwaySearchGeneList() throws Throwable {
        setupPathwaySearch("CDK4");

        Results results = runPathwayFilter();
        assertNotNull(results);
        assertEquals(5, results.getActualRowCount()); // 5 pathways match the gene CDK4

        assertEquals("Filter was not set back to Pathway mode", FilterSpecifier.ListBy.Pathways, filter.getListBy());

        // each row should have an annotation with the pathway id and one with the fisher's exact value
        for (int i = 0; i < results.getActualRowCount(); i++) {
            assertNotNull("Row " + i + " does not have a pathway ID annotation",
                    results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_PATHWAYID));
            assertNotNull("Row " + i + " does not have a Fisher's Exact annotation",
                    results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_PATHWAY_FISHER));
        }
    }

    @Test
    public void testPathwaySearchNoColumns() throws Throwable {
        setupPathwaySearch(null);

        Results results = runPathwayFilter();
        assertNotNull(results);
        assertEquals(5, results.getActualRowCount()); // 5 pathways will match

        // since no p-values, expect sorted by name, ignoring case
        final String[] expectedPathways = new String[]{"Cell Cycle: G1/S Check Point",
                "Cyclins and Cell Cycle Regulation",
                "Influence of Ras and Rho Proteins on G1 to S Transition",
                "p53 Signaling Pathway",
                "RB Tumor Suppressor/Checkpoint Signaling in Response to DNA Damage"};
        // each row should have an annotation with the pathway id
        for (int i = 0; i < results.getActualRowCount(); i++) {
            assertNotNull("Row " + i + " does not have a pathway ID annotation",
                    results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_PATHWAYID));
            assertNull("Row " + i + " has a Fisher's Exact annotation",
                    results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_PATHWAY_FISHER));
            assertEquals(expectedPathways[i], results.getRow(i).getName()
            );
        }
    }

    @Test
    public void testNoGeneHits() throws Throwable {
        setUpAnomalySearch();
        selectNonMutationType(-0.5, 0.5, .05);
        filter.setGeneList("squirrel"); // there is no such gene.  too bad.
        boolean exceptionThrown = false;
        try {
            runAnomalyFilter();
        } catch (QueriesException qe) {
            exceptionThrown = true;
        }
        // now should just return empty results set!
        assertTrue(exceptionThrown);
    }

    public void runAllGenes() throws Throwable {
        setUpAnomalySearch();
        filter.setPatientListOptions(FilterSpecifier.PatientListOptions.All);
        filter.setPatientList("");
        filter.setGeneListOptions(FilterSpecifier.GeneListOptions.All);

        selectNonMutationType(-0.5, 0.5, .2);
        runAnomalyFilter();
    }

    public void runMultiColumn() throws Throwable {
        setUpAnomalySearch();
        selectCorrelationColumn();
        selectNonMutationType(-0.8, 0.8, .5);
        filter.setPatientListOptions(FilterSpecifier.PatientListOptions.All);
        filter.setPatientList("");
        filter.setGeneListOptions(FilterSpecifier.GeneListOptions.All);

        long start = System.currentTimeMillis();
        runAnomalyFilter();
        long stop = System.currentTimeMillis();
        System.out.println("TOTAL TIME = " + (stop - start));
    }

    //checks that an OR is applied between columns of the same platform type.

    @Test
    public void testOrLogic() throws Throwable {
        String patients = "TCGA-02-0011,TCGA-02-0281,TCGA-08-0531,TCGA-02-0332,TCGA-02-0038"; //make searches faster
        String genes = "15E1.2, 2'-PDE, 7A5, A1BG, A1CF, A26A1, A26B1, A26B3, A26C1A, A26C1B, A26C2, " +
                "A26C3, A2BP1, A2M, A2ML1, A3GALT2, A4GALT, A4GNT, AAAS, AACS, AADAC, " +
                "AADACL1, AADACL2, AADACL3, AADACL4, AADAT, AAK1, AAMP, AANAT, AARS, " +
                "AARS2, AARSD1, AASDH, AASDHPPT, AASS, AATF, AATK, ABAT, ABBA-1, ABCA1, " +
                "ABCA10, ABCA11, ABCA12, ABCA13, ABCA2, ABCA3, ABCA4, ABCA5, ABCA6, ABCA7, " +
                "ABCA8, ABCA9, ABCB1, ABCB10, ABCB11, ABCB4, ABCB5, ABCB6, ABCB7, ABCB8, ABCB9, " +
                "ABCC1, ABCC10, ABCC11, ABCC12, ABCC13, ABCC2, ABCC3, ABCC4, ABCC5, ABCC6, ABCC8, " +
                "ABCC9, ABCD1, ABCD2, ABCD3, ABCD4, ABCE1, ABCF1, ABCF2, ABCF3, ABCG1, ABCG2, ABCG4, " +
                "ABCG5, ABCG8, ABHD1, ABHD10, ABHD11, ABHD12, ABHD12B, ABHD13, ABHD14A, ABHD14B, ABHD2, " +
                "ABHD3, ABHD4, ABHD5, ABHD6, ABHD7,C9orf152,CREB3L1";

        //find two CN columns
        List<ColumnType> cnColumns = new ArrayList<ColumnType>();
        for (ColumnType ctype : queries.getColumnTypes("GBM")) {
            if (ctype instanceof CopyNumberType && ((AnomalyType) ctype).getGeneticElementType() == AnomalyType.GeneticElementType.Gene) {
                CopyNumberType cntype = (CopyNumberType) ctype;
                cntype.setPicked(true);
                cntype.setLowerOperator(UpperAndLowerLimits.Operator.LTE);
                cntype.setLowerLimit(-.5);
                cntype.setUpperOperator(UpperAndLowerLimits.Operator.GTE);
                cntype.setUpperLimit(.5);
                cntype.setCalculationType(CopyNumberType.CalculationType.Regular);
                cnColumns.add(ctype);
                if (cnColumns.size() == 2) break;
            }
        }
        assertEquals(2, cnColumns.size());

        //
        // Checking OR logic
        //

        FilterSpecifier filterCombined = new FilterSpecifier();
        filterCombined.setDisease("GBM");
        filterCombined.setListBy(FilterSpecifier.ListBy.Genes);

        //set frequency high one one of them so they get different number of results
        ((AnomalyType) cnColumns.get(1)).setFrequency(.6F);

        //do a search with both columns
        filterCombined.setColumnTypes(cnColumns);
        filterCombined.setPatientListOptions(FilterSpecifier.PatientListOptions.List);
        filterCombined.setPatientList(patients); // 5 patients
        filterCombined.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
        filterCombined.setGeneList(genes);
        Results resultsCombined = runAnomalyFilter(filterCombined);

        //now do searches with one column each
        FilterSpecifier filterOne = new FilterSpecifier();
        filterOne.setDisease("GBM");
        filterOne.setListBy(FilterSpecifier.ListBy.Genes);
        List<ColumnType> columnOneList = new ArrayList<ColumnType>();
        columnOneList.add(cnColumns.get(0));
        filterOne.setColumnTypes(columnOneList);
        filterOne.setPatientListOptions(FilterSpecifier.PatientListOptions.List);
        filterOne.setPatientList(patients);
        filterOne.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
        filterOne.setGeneList(genes);
        Results resultsOne = runAnomalyFilter(filterOne);

        FilterSpecifier filterTwo = new FilterSpecifier();
        filterTwo.setDisease("GBM");
        filterTwo.setListBy(FilterSpecifier.ListBy.Genes);
        List<ColumnType> columnTwoList = new ArrayList<ColumnType>();
        columnTwoList.add(cnColumns.get(1));
        filterTwo.setColumnTypes(columnTwoList);
        filterTwo.setPatientListOptions(FilterSpecifier.PatientListOptions.List);
        filterTwo.setPatientList(patients);
        filterTwo.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
        filterTwo.setGeneList(genes);
        Results resultsTwo = runAnomalyFilter(filterTwo);

        //first check: the number of rows in the combined results cannot be less
        //  than the rows in the bigger of the other sets.
        int combinedRows = resultsCombined.getActualRowCount();
        int oneRows = resultsOne.getActualRowCount();
        int twoRows = resultsTwo.getActualRowCount();
        System.out.println("combinedRows==" + combinedRows);
        System.out.println("oneRows==" + oneRows);
        System.out.println("twoRows==" + twoRows);
        int biggerRows = (oneRows > twoRows ? oneRows : twoRows);

        assertTrue("Combined rows are less than other sets: cannot be doing OR", combinedRows >= biggerRows);


        //second check: number of unique symbols in combined set should equals
        //  number of unique symbols in each of the other sets, combined
        Map<String, Object> uniqueSymbols = new HashMap<String, Object>();
        Object dummy = new Object();
        for (int i = 0; i < oneRows; i++) {
            String symbol = resultsOne.getRow(i).getName();
            if (uniqueSymbols.get(symbol) == null) {
                uniqueSymbols.put(symbol, dummy);
            }
        }
        for (int i = 0; i < twoRows; i++) {
            String symbol = resultsTwo.getRow(i).getName();
            if (uniqueSymbols.get(symbol) == null) {
                uniqueSymbols.put(symbol, dummy);
            }
        }
        assertEquals("Wrong number of unique symbols in combined set", combinedRows, uniqueSymbols.size());

        //but it's still possible for our test to be inconclusive. If both one-column
        //sets contain exactly the same symbols, then it could be either OR or AND. We must fail
        assertFalse("Test inconclusive: sets contain same symbols", combinedRows == oneRows && combinedRows == twoRows);

        //
        // Checking AND logic
        //

        //next, need to test two dissimilar data types (CN and expression), make sure it's
        //doing AND logic
        ExpressionType expColumn = null;
        for (ColumnType ctype : queries.getColumnTypes("GBM")) {
            if (ctype instanceof ExpressionType) {
                expColumn = (ExpressionType) ctype;
                break;
            }
        }
        assertNotNull(expColumn);
        assert expColumn != null; // just to keep idea from complaining about possible null pointers...
        expColumn.setLowerOperator(UpperAndLowerLimits.Operator.LTE);
        expColumn.setLowerLimit(-.5);
        expColumn.setUpperOperator(UpperAndLowerLimits.Operator.GTE);
        expColumn.setUpperLimit(.5);
        expColumn.setFrequency(.4F);
        expColumn.setPicked(true);

        filterCombined = new FilterSpecifier();
        filterCombined.setDisease("GBM");
        filterCombined.setListBy(FilterSpecifier.ListBy.Genes);
        List<ColumnType> columnCombinedList = new ArrayList<ColumnType>();
        columnCombinedList.add(cnColumns.get(0));
        columnCombinedList.add(expColumn);
        filterCombined.setColumnTypes(columnCombinedList);
        filterCombined.setPatientListOptions(FilterSpecifier.PatientListOptions.List);
        filterCombined.setPatientList(patients); // 5 patients
        filterCombined.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
        filterCombined.setGeneList(genes);
        resultsCombined = runAnomalyFilter(filterCombined);

        //now do searches with one column each:: first cn, then exp
        filterOne = new FilterSpecifier();
        filterOne.setDisease("GBM");
        filterOne.setListBy(FilterSpecifier.ListBy.Genes);
        columnOneList = new ArrayList<ColumnType>();
        columnOneList.add(cnColumns.get(0));
        filterOne.setColumnTypes(columnOneList);
        filterOne.setPatientListOptions(FilterSpecifier.PatientListOptions.List);
        filterOne.setPatientList(patients);
        filterOne.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
        filterOne.setGeneList(genes);
        resultsOne = runAnomalyFilter(filterOne);

        filterTwo = new FilterSpecifier();
        filterTwo.setDisease("GBM");
        filterTwo.setListBy(FilterSpecifier.ListBy.Genes);
        columnTwoList = new ArrayList<ColumnType>();
        columnTwoList.add(expColumn);
        filterTwo.setColumnTypes(columnTwoList);
        filterTwo.setPatientListOptions(FilterSpecifier.PatientListOptions.List);
        filterTwo.setPatientList(patients);
        filterTwo.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
        filterTwo.setGeneList(genes);
        resultsTwo = runAnomalyFilter(filterTwo);

        //combined rows should be less than or equal to the smaller of the two one-column sets
        combinedRows = resultsCombined.getActualRowCount();
        oneRows = resultsOne.getActualRowCount();
        twoRows = resultsTwo.getActualRowCount();
        System.out.println("combinedRows==" + combinedRows);
        System.out.println("oneRows==" + oneRows);
        System.out.println("twoRows==" + twoRows);

        int smallerRows = (oneRows < twoRows ? oneRows : twoRows);
        assertTrue("Combined rows are greater than other sets: cannot be doing AND", combinedRows <= smallerRows);

        //number of unique symbols in combined set should equals
        //  number of unique symbols common to both one-column sets
        uniqueSymbols = new HashMap<String, Object>();
        for (int i = 0; i < oneRows; i++) {
            String symbol = resultsOne.getRow(i).getName();
            if (uniqueSymbols.get(symbol) == null) {
                uniqueSymbols.put(symbol, 1);
            }
        }
        for (int i = 0; i < twoRows; i++) {
            String symbol = resultsTwo.getRow(i).getName();
            if (uniqueSymbols.get(symbol) != null) {
                uniqueSymbols.put(symbol, 2);
            }
        }
        int uniqueSymbolCount = 0;
        for (String symbol : uniqueSymbols.keySet()) {
            if (uniqueSymbols.get(symbol) != null && uniqueSymbols.get(symbol).equals(2)) {
                uniqueSymbolCount++;
            }
        }
        assertEquals("Wrong number of unique symbols in combined set", combinedRows, uniqueSymbolCount);

        //but it's still possible for our test to be inconclusive. If both one-column
        //sets contain exactly the same symbols, then it could be either OR or AND. We must fail
        assertFalse("Test inconclusive: sets contain same symbols", combinedRows == oneRows && combinedRows == twoRows);

    }

    private ExpressionType setupGeneExpressionColumn() {
        String query = "select anomaly_data_set_id, ads.anomaly_type_id from L4_anomaly_data_set ads, L4_anomaly_type at where ads.anomaly_type_id=at.anomaly_type_id and anomaly_name like '%HT_HG-U133A%' and rownum=1";
        Map row = queries.getJdbcTemplate().queryForMap(query);
        ExpressionType geneExpColumn = new ExpressionType(AnomalyType.GeneticElementType.Gene);
        geneExpColumn.setAnomalyTypeId(Integer.valueOf(row.get("anomaly_type_id").toString()));
        geneExpColumn.setDataSetId(Integer.valueOf(row.get("anomaly_data_set_id").toString()));
        geneExpColumn.setDisplayCenter("CENTER");
        geneExpColumn.setDisplayPlatform("PLATFORM");
        geneExpColumn.setDisplayPlatformType("Expression-Gene");

        geneExpColumn.setFrequency(0.5f);
        geneExpColumn.setLowerLimit(-0.5);
        geneExpColumn.setLowerOperator(UpperAndLowerLimits.Operator.LTE);
        geneExpColumn.setUpperLimit(0.5);
        geneExpColumn.setUpperOperator(UpperAndLowerLimits.Operator.GTE);
        geneExpColumn.setPicked(true);

        return geneExpColumn;
    }

    private ExpressionType setupMirnaExpressionColumn() {
        String query = "select anomaly_data_set_id, ads.anomaly_type_id from L4_anomaly_data_set ads, L4_anomaly_type at where ads.anomaly_type_id=at.anomaly_type_id and anomaly_name like '%H-miRNA_8x15K%' and rownum=1";
        Map row = queries.getJdbcTemplate().queryForMap(query);
        ExpressionType mirnaColumn = new ExpressionType(AnomalyType.GeneticElementType.miRNA);
        mirnaColumn.setAnomalyTypeId(Integer.valueOf(row.get("anomaly_type_id").toString()));
        mirnaColumn.setDataSetId(Integer.valueOf(row.get("anomaly_data_set_id").toString()));
        mirnaColumn.setDisplayCenter("CENTER");
        mirnaColumn.setDisplayPlatform("PLATFORM");
        mirnaColumn.setDisplayPlatformType("Expression-miRNA");

        mirnaColumn.setFrequency(0.5f);
        mirnaColumn.setLowerLimit(-0.5);
        mirnaColumn.setLowerOperator(UpperAndLowerLimits.Operator.LTE);
        mirnaColumn.setUpperLimit(0.5);
        mirnaColumn.setUpperOperator(UpperAndLowerLimits.Operator.GTE);
        mirnaColumn.setPicked(true);

        return mirnaColumn;
    }

    @Test
    public void testMirnaExpressionQuery() throws Throwable {
        // 1. find data set id for mirna expression
        ExpressionType mirnaColumn = setupMirnaExpressionColumn();

        FilterSpecifier mirnaFilter = new FilterSpecifier();
        List<ColumnType> columns = new ArrayList<ColumnType>();
        columns.add(mirnaColumn);
        mirnaFilter.setColumnTypes(columns);
        mirnaFilter.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
        mirnaFilter.setGeneList("AACS,AAMP,FCXW4");
        mirnaFilter.setPatientListOptions(FilterSpecifier.PatientListOptions.All);
        mirnaFilter.setListBy(FilterSpecifier.ListBy.Genes);
        mirnaFilter.setDisease("GBM");

        Results results = runAnomalyFilter(mirnaFilter);
        assertNotNull(results);
        assertTrue(results.getActualRowCount() > 0);

        // all rows should have an mirna annotation
        for (int i = 0; i < results.getActualRowCount(); i++) {
            ResultRow resultRow = results.getRow(i);
            assertNotNull(resultRow.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_MIRNA));
        }
    }

    @Test
    public void testMirnaExpressionByPatientQuery() throws Throwable {
        ExpressionType mirnaColumn = setupMirnaExpressionColumn();
        mirnaColumn.setFrequency(0.05f);
        FilterSpecifier mirnaFilter = new FilterSpecifier();
        List<ColumnType> columns = new ArrayList<ColumnType>();
        columns.add(mirnaColumn);
        mirnaFilter.setColumnTypes(columns);
        mirnaFilter.setGeneListOptions(FilterSpecifier.GeneListOptions.All);
        mirnaFilter.setPatientListOptions(FilterSpecifier.PatientListOptions.All);
        mirnaFilter.setListBy(FilterSpecifier.ListBy.Patients);
        mirnaFilter.setDisease("GBM");

        Results results = runAnomalyFilter(mirnaFilter);
        assertTrue(results.getActualRowCount() > 0);
    }


    private MethylationType setupMethylationColumn() {
        // 1. find a data_set_id for methylation
        String query = "select anomaly_data_set_id, ads.anomaly_type_id from L4_anomaly_data_set ads, " +
                "L4_anomaly_type at where ads.anomaly_type_id=at.anomaly_type_id and " +
                "anomaly_name like '%Methylation%' and rownum=1";
        Map row = queries.getJdbcTemplate().queryForMap(query);
        MethylationType methylationColumn = new MethylationType();
        methylationColumn.setAnomalyTypeId(Integer.valueOf(row.get("anomaly_type_id").toString()));
        methylationColumn.setDataSetId(Integer.valueOf(row.get("anomaly_data_set_id").toString()));
        methylationColumn.setDisplayCenter("CENTER");
        methylationColumn.setDisplayPlatform("PLATFORM");
        methylationColumn.setDisplayPlatformType("TYPE");
        methylationColumn.setPlatformType(0);

        // 2. Set up the column with search constraints
        methylationColumn.setFrequency(0.2f);
        methylationColumn.setUpperLimit(0.5);
        methylationColumn.setUpperOperator(UpperAndLowerLimits.Operator.GTE);
        methylationColumn.setPicked(true);
        return methylationColumn;
    }

    @Test
    public void testMethylationByPatientQuery() throws Throwable {
        MethylationType methylationColumn = setupMethylationColumn();
        methylationColumn.setFrequency(0.05f);

        // create a filter with just this column
        FilterSpecifier methylationFilter = new FilterSpecifier();
        List<ColumnType> columns = new ArrayList<ColumnType>();
        columns.add(methylationColumn);
        methylationFilter.setColumnTypes(columns);
        methylationFilter.setGeneListOptions(FilterSpecifier.GeneListOptions.All);
        methylationFilter.setPatientListOptions(FilterSpecifier.PatientListOptions.All);
        methylationFilter.setListBy(FilterSpecifier.ListBy.Patients);
        methylationFilter.setDisease("GBM");

        // 4. run the query
        Results result = runAnomalyFilter(methylationFilter);
        assertTrue(result.getActualRowCount() > 0);
    }

    @Test
    public void testCopyNumberByPatientQuery() throws Throwable {
        List<ColumnType> columns = queries.getColumnTypes("GBM");
        CopyNumberType cnType = null;
        for (ColumnType column : columns) {
            if (column instanceof CopyNumberType) {
                cnType = (CopyNumberType) column;
                break;
            }
        }
        assert cnType != null;
        cnType.setPicked(true);
        cnType.setLowerLimit(-0.5); //todo  set some defaults
        cnType.setLowerOperator(UpperAndLowerLimits.Operator.LTE);
        cnType.setUpperLimit(0.5);
        cnType.setUpperOperator(UpperAndLowerLimits.Operator.GTE);
        cnType.setCalculationType(CopyNumberType.CalculationType.Regular);

        FilterSpecifier filter = new FilterSpecifier();
        filter.setColumnTypes(columns);
        filter.setListBy(FilterSpecifier.ListBy.Patients);
        filter.setDisease("GBM");
        filter.setGeneListOptions(FilterSpecifier.GeneListOptions.All);
        filter.setPatientListOptions(FilterSpecifier.PatientListOptions.All);
        Results results = runAnomalyFilter(filter);
        assertNotNull(results);
        assertTrue(results.getActualRowCount() > 0);
    }

    @Test
    public void testMethylationQuery() throws Throwable {

        MethylationType methylationColumn = setupMethylationColumn();

        // create a filter with just this column
        FilterSpecifier methylationFilter = new FilterSpecifier();
        List<ColumnType> columns = new ArrayList<ColumnType>();
        columns.add(methylationColumn);
        methylationFilter.setColumnTypes(columns);
        methylationFilter.setGeneListOptions(FilterSpecifier.GeneListOptions.All);
        methylationFilter.setPatientListOptions(FilterSpecifier.PatientListOptions.All);
        methylationFilter.setListBy(FilterSpecifier.ListBy.Genes);
        methylationFilter.setDisease("GBM");

        // 4. run the query
        Results result = runAnomalyFilter(methylationFilter);
        assertNotNull(result);

        // all rows should have a "methylation_region" annotation
        for (int i = 0; i < result.getActualRowCount(); i++) {
            ResultRow resultRow = result.getRow(i);
            assertNotNull(resultRow.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_METHYLATION_PROBE));
        }
    }

    private CopyNumberType setupMiRNACopyNumber() {
        String query = "select anomaly_data_set_id, ads.anomaly_type_id from L4_anomaly_data_set ads, " +
                "L4_anomaly_type at where ads.anomaly_type_id=at.anomaly_type_id " +
                "and anomaly_name like '%miRNA%' and data_type_id=4 and rownum=1";
        Map row = queries.getJdbcTemplate().queryForMap(query);
        CopyNumberType mirnaCopyNumberColumn = new CopyNumberType(AnomalyType.GeneticElementType.miRNA);
        mirnaCopyNumberColumn.setAnomalyTypeId(Integer.valueOf(row.get("anomaly_type_id").toString()));
        mirnaCopyNumberColumn.setDataSetId(Integer.valueOf(row.get("anomaly_data_set_id").toString()));

        mirnaCopyNumberColumn.setFrequency(0.2f);
        mirnaCopyNumberColumn.setUpperLimit(0.5);
        mirnaCopyNumberColumn.setUpperOperator(UpperAndLowerLimits.Operator.GTE);
        mirnaCopyNumberColumn.setLowerLimit(0.5);
        mirnaCopyNumberColumn.setLowerOperator(UpperAndLowerLimits.Operator.LTE);
        mirnaCopyNumberColumn.setPicked(true);

        return mirnaCopyNumberColumn;
    }

    @Test
    public void testMiRNACopyNumberGistic() throws Throwable {
        CopyNumberType mirnaCopyNumberColumn = setupMiRNACopyNumber();
        mirnaCopyNumberColumn.setCalculationType(CopyNumberType.CalculationType.GISTIC);

        FilterSpecifier mirnaFilter = new FilterSpecifier();
        List<ColumnType> columns = new ArrayList<ColumnType>();
        columns.add(mirnaCopyNumberColumn);
        mirnaFilter.setColumnTypes(columns);
        mirnaFilter.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
        mirnaFilter.setGeneList("AACS,AAMP,ABCB7");
        mirnaFilter.setPatientListOptions(FilterSpecifier.PatientListOptions.All);
        mirnaFilter.setListBy(FilterSpecifier.ListBy.Genes);
        mirnaFilter.setDisease("GBM");

        Results results = runAnomalyFilter(mirnaFilter);
        assertTrue(results.getActualRowCount() > 0);

        for (int i = 0; i < results.getActualRowCount(); i++) {
            ResultRow resultRow = results.getRow(i);
            assertNotNull(resultRow.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_MIRNA));
        }
    }

    @Test
    public void testMiRNACopyNumberQuery() throws Throwable {
        CopyNumberType mirnaCopyNumberColumn = setupMiRNACopyNumber();

        FilterSpecifier mirnaFilter = new FilterSpecifier();
        List<ColumnType> columns = new ArrayList<ColumnType>();
        columns.add(mirnaCopyNumberColumn);
        mirnaFilter.setColumnTypes(columns);
        mirnaFilter.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
        mirnaFilter.setGeneList("AACS,AAMP,ABCB7");
        mirnaFilter.setPatientListOptions(FilterSpecifier.PatientListOptions.All);
        mirnaFilter.setListBy(FilterSpecifier.ListBy.Genes);
        mirnaFilter.setDisease("GBM");

        Results results = runAnomalyFilter(mirnaFilter);
        assertTrue(results.getActualRowCount() > 0);

        for (int i = 0; i < results.getActualRowCount(); i++) {
            ResultRow resultRow = results.getRow(i);
            assertNotNull(resultRow.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_MIRNA));
        }
    }

    @Test
    public void testTwoExpressionColumns() throws Throwable {
        // gene-exp and mirna-exp
        ExpressionType mirnaColumn = setupMirnaExpressionColumn();
        ExpressionType geneColumn = setupGeneExpressionColumn();

        FilterSpecifier twoColFilter = new FilterSpecifier();
        List<ColumnType> columns = new ArrayList<ColumnType>();
        columns.add(geneColumn);
        columns.add(mirnaColumn);
        twoColFilter.setColumnTypes(columns);
        twoColFilter.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
        twoColFilter.setGeneList("ADARB2, AKR1CL2, BAG3, BNIP3, BTBD16, C10ORF4, C10ORF84, " +
                "C1QL3, CCAR1, CDKN2A, COMMD3,CRABP2, CYP2E1, DCLRE1A, DYDC1,EGFR, EXOSC1, " +
                "FBXW4,FCXW4, GTPBP4, ITIH5, KIF11, KIN, LDB1, MKX, MRPL43, MTG1, MYPN");
        twoColFilter.setPatientListOptions(FilterSpecifier.PatientListOptions.All);
        twoColFilter.setListBy(FilterSpecifier.ListBy.Genes);
        twoColFilter.setDisease("GBM");

        Results results = runAnomalyFilter(twoColFilter);
        assertTrue(results.getActualRowCount() > 0);

        // should not be any blanks in the results
        for (int i = 0; i < results.getActualRowCount(); i++) {
            ResultRow row = results.getRow(i);
            for (ResultValue result : row.getColumnResults()) {
                assertFalse("Blank result in 'AND'ed column results", result instanceof ResultBlank);
            }
        }
    }

    @Test
    public void testCombineElements() throws QueriesException {
        queries.addGeneticElement(1, 0, 0);
        queries.addGeneticElement(2, 0, 0);
        queries.addGeneticElement(3, 0, 0);
        queries.addGeneticElement(4, 0, 0);

        List<Map<String, ResultValue>> resultValues = new ArrayList<Map<String, ResultValue>>();
        Map<String, ResultValue> copynumberResults = new HashMap<String, ResultValue>();
        copynumberResults.put("1:0:0", null);
        copynumberResults.put("2:0:0", null);
        copynumberResults.put("3:0:0", null);
        resultValues.add(copynumberResults);

        Map<String, ResultValue> methylationResults = new HashMap<String, ResultValue>();
        methylationResults.put("1:4:0", null); // gene 1, methyl 4
        methylationResults.put("2:5:0", null); // gene 2, methyl 5
        resultValues.add(methylationResults);

        // 3. now add in an miRNA result
        Map<String, ResultValue> mirnaResults = new HashMap<String, ResultValue>();
        mirnaResults.put("1:0:6", null); // gene 1, mirna 6
        mirnaResults.put("1:0:7", null); // gene 1, mirna 7
        mirnaResults.put("3:0:8", null); // gene 3, mirna 8
        resultValues.add(mirnaResults);

        List<Level4QueriesJDBCImpl.ResultElement> elements = queries.combineElements(true, resultValues);

        // elements in there should be: 1:4:6, 1:4:7, 2:5:0, 3:0:8
        assertEquals(4, elements.size());
        boolean found146 = false;
        boolean found147 = false;
        boolean found250 = false;
        boolean found308 = false;
        for (Level4QueriesJDBCImpl.ResultElement element : elements) {
            if (element.getIdString().equals("1:4:6")) {
                found146 = true;
            } else if (element.getIdString().equals("1:4:7")) {
                found147 = true;
            } else if (element.getIdString().equals("2:5:0")) {
                found250 = true;
            } else if (element.getIdString().equals("3:0:8")) {
                found308 = true;
            }
        }
        assertTrue(found146 && found147 && found250 && found308);

        Map<String, ResultValue> mirnaResults2 = new HashMap<String, ResultValue>();
        mirnaResults2.put("1:0:10", null);
        mirnaResults2.put("2:0:12", null);
        mirnaResults2.put("3:0:14", null);
        mirnaResults2.put("4:0:15", null);
        resultValues.add(mirnaResults2);
        elements = queries.combineElements(true, resultValues);
        // now elements should be 1:4:6, 1:4:7, 1:4:10, 2:5:12, 3:0:8, 3:0:14 and 4:0:15
        assertEquals(7, elements.size());

    }

    @Test
    public void testPatientListSearchNoColumns() throws Throwable {

        filter = new FilterSpecifier();
        filter.setDisease("GBM");
        List<ColumnType> columnTypes = queries.getColumnTypes("GBM");
        filter.setColumnTypes(columnTypes);
        filter.setPatientListOptions(FilterSpecifier.PatientListOptions.List);
        filter.setPatientList("TCGA-02-0001, TCGA-02-0001, TCGA-02-0002");
        filter.setListBy(FilterSpecifier.ListBy.Patients);

        Results results = runAnomalyFilter(filter);

        assertEquals(2, results.getActualRowCount());
        assertTrue(results.getRow(0).getName().equals("TCGA-02-0001") || results.getRow(1).getName().equals("TCGA-02-0001"));
        assertTrue(results.getRow(0).getName().equals("TCGA-02-0002") || results.getRow(1).getName().equals("TCGA-02-0002"));
    }

    @Test
    public void testGeneListSearchNoColumns() throws Throwable {
        filter = new FilterSpecifier();
        filter.setDisease("GBM");
        List<ColumnType> columnTypes = queries.getColumnTypes("GBM");
        filter.setColumnTypes(columnTypes);
        filter.setGeneList("TP53, TP53, PTEN");
        filter.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
        filter.setListBy(FilterSpecifier.ListBy.Genes);

        Results results = runAnomalyFilter(filter);
        assertEquals(2, results.getActualRowCount());
        assertTrue(results.getRow(0).getName().equals("TP53") || results.getRow(1).getName().equals("TP53"));
        assertTrue(results.getRow(0).getName().equals("PTEN") || results.getRow(1).getName().equals("PTEN"));
    }

    @Test
    public void testNullOperator() throws Throwable {
        filter = new FilterSpecifier();
        filter.setDisease("GBM");
        List<ColumnType> columnTypes = queries.getColumnTypes("GBM");
        filter.setColumnTypes(columnTypes);
        filter.setListBy(FilterSpecifier.ListBy.Genes); // need to specify this for all searches
        CopyNumberType cntype = null;
        for (ColumnType ctype : columnTypes) {
            if (ctype instanceof CopyNumberType) {
                cntype = (CopyNumberType) ctype;
                break;
            }
        }
        assert cntype != null;
        cntype.setPicked(true);
        cntype.setLowerLimit(-0.5);
        cntype.setLowerOperator(UpperAndLowerLimits.Operator.None);
        Results results = runAnomalyFilter(filter);
        assertNotNull(results);
    }

    //jira record DBRO-8

    @Test
    public void testMirnaNotEmptyWithGeneList() throws Throwable {
        //first mirna CN type
        List<ColumnType> ctypes = queries.getColumnTypes("GBM");
        CopyNumberType cntype1 = null;
        for (ColumnType ctype : ctypes) {
            if (ctype instanceof CopyNumberType
                    && ((AnomalyType) ctype).getGeneticElementType() == AnomalyType.GeneticElementType.miRNA) {
                cntype1 = (CopyNumberType) ctype;
                break;
            }
        }
        assert (cntype1 != null);
        cntype1.setPicked(true);
        cntype1.setLowerLimit(-0.5);
        cntype1.setLowerOperator(UpperAndLowerLimits.Operator.LTE);
        cntype1.setUpperLimit(0.5);
        cntype1.setUpperOperator(UpperAndLowerLimits.Operator.GTE);
        cntype1.setFrequency(0.2f);

        //first establish that you can get data in gene mode for a single gene
        FilterSpecifier filter = new FilterSpecifier();
        filter.setColumnTypes(ctypes);
        filter.setListBy(FilterSpecifier.ListBy.Genes);
        filter.setDisease("GBM");
        filter.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
        filter.setGeneList("AACS");
        filter.setPatientListOptions(FilterSpecifier.PatientListOptions.All);
        Results summaryResults = runAnomalyFilter(filter);
        assertNotNull(summaryResults);
        assertTrue(summaryResults.getActualRowCount() > 0);

        //so you should be able to get data in patient mode too
        filter.setListBy(FilterSpecifier.ListBy.Patients);
        summaryResults = runAnomalyFilter(filter);
        assertNotNull(summaryResults);
        assertTrue(summaryResults.getActualRowCount() > 0);
    }


}
