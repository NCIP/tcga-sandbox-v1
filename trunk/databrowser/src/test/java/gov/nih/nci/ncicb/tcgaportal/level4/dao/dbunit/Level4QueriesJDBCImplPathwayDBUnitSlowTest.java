package gov.nih.nci.ncicb.tcgaportal.level4.dao.dbunit;

import gov.nih.nci.ncicb.tcgaportal.level4.dao.QueriesException;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.jdbc.Level4QueriesJDBCImpl;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.AnomalySearchConstants;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterChromRegion;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.pathway.SinglePathwayResults;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.pathway.SinglePathwaySpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.util.FishersExactImpl;
import gov.nih.nci.ncicb.tcgaportal.level4.util.PearsonCorrelationCalculator;

import java.util.List;

/**
 * Description : Class used to test pathway related DBUnit tests
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class Level4QueriesJDBCImplPathwayDBUnitSlowTest extends Level4QueriesJDBCImplDBUnitConfig {

    private static String testDbFileName_Pathway = "TestDB_Pathway.xml";

    public Level4QueriesJDBCImplPathwayDBUnitSlowTest() {
        super(testDbFileName_Pathway);
        queries = new Level4QueriesJDBCImpl(1000, new PearsonCorrelationCalculator(),
                new FishersExactImpl(), dataSource);
    }

    public void testPathwaySearchCopyNumber() throws Throwable {

        queries = new Level4QueriesJDBCImpl(1000, new PearsonCorrelationCalculator(),
                new FishersExactImpl(), dataSource);
        
        String inputFile = "input_testPathwaySearchCopyNumber.xml";
        String expectedResultFileName = "expected_testPathwaySearchCopyNumber";

        filter = new FilterSpecifier();
        createFilterFromInputFile(inputFile);

        Results results = runPathwayFilter();

        //write the results to a text file
        String actualResultFileName = "testPathwaySearchCopyNumber";
        writeDatasetOutput(results, PORTAL_FOLDER + actualResultFileName);

        assertNotNull(results);
        assertEquals(1, results.getActualRowCount()); // 5 pathways match the gene CDK4

        assertEquals("Filter was not set back to Pathway mode", FilterSpecifier.ListBy.Pathways,
                filter.getListBy());

        // each row should have an annotation with the pathway id and one with the fisher's exact value
        for (int i = 0; i < results.getActualRowCount(); i++) {
            assertNotNull("Row " + i + " does not have a pathway ID annotation",
                    results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_PATHWAYID));
            assertNotNull("Row " + i + " does not have a Fisher's Exact annotation",
                    results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_PATHWAY_FISHER));
        }

    }

    public void testGetSinglePathwway() throws Throwable{

        queries = new Level4QueriesJDBCImpl(1000, new PearsonCorrelationCalculator(),
                new FishersExactImpl(), dataSource);
        String inputFile = "input_testGetSinglePathwway.xml";
        String expectedResultFileName = "expected_testGetSinglePathwway";

        filter = new FilterSpecifier();
        createFilterFromInputFile(inputFile);

        SinglePathwaySpecifier sps = new SinglePathwaySpecifier();
        sps.setId("89");
        //  Copy number column 
        sps.setFilterSpecifier(filter);
        SinglePathwayResults results = queries.getSinglePathway(sps);

        //write the results to a text file
        String actualResultFileName = "testGetSinglePathwway";
        writeDatasetOutput(results, PORTAL_FOLDER + actualResultFileName);

        assertNotNull(results);
        assertTrue(results.getTotalRowCount() > 0);
        assertTrue(results.getTotalRowCount() < 100); // sanity check on size of pathway...
        for (int i = 0; i < results.getTotalRowCount(); i++) {
            assertNotNull("Chromosome not in row annotation",
                    results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_CHROM));
            assertNotNull("Start not in row annotation",
                    results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_START));
            assertNotNull("Stop not in row annotation",
                    results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_STOP));
            assertNotNull("Biocarta Symbol not in row annotation for " + results.getRow(i).getName(),
                    results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_BCGENE));
            assertNotNull("Matched Search annotation not present",
                    results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_MATCHED_SEARCH));
            assertNotNull("CNV annotation not present",
                    results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_CNV));
            assertEquals("Result for row not present", 1,
                    results.getRow(i).getColumnResults().length); // should be 1 result b/c only 1 column
        }
    }


    public void testGetSinglePathwayByChrom() throws QueriesException {
        queries = new Level4QueriesJDBCImpl(1000, new PearsonCorrelationCalculator(),
                new FishersExactImpl(), dataSource);
        
        SinglePathwaySpecifier sps = new SinglePathwaySpecifier();
        sps.setId("89");

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
            String chrom = results.getRow(i).getRowAnnotation(
                    AnomalySearchConstants.ROWANNOTATIONKEY_REGION_CHROM).toString();
            if ("2".equals(chrom)) {
                assertTrue("Result " + i + " is on chrom 2 but is not flagged as matching",
                        (Boolean) results.getRow(i).getRowAnnotation(
                        AnomalySearchConstants.ROWANNOTATIONKEY_MATCHED_SEARCH));
            } else {
                assertFalse("Result " + i + " is not on chrom 2 but is flagged as matching (" + chrom + ")",
                        (Boolean) results.getRow(i).getRowAnnotation(
                        AnomalySearchConstants.ROWANNOTATIONKEY_MATCHED_SEARCH));
            }
        }
    }

}
