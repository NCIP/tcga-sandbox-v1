package gov.nih.nci.ncicb.tcgaportal.level4.dao.dbunit;

import gov.nih.nci.ncicb.tcgaportal.level4.dao.jdbc.Level4QueriesJDBCImpl;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.AnomalySearchConstants;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.ResultRow;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultBlank;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultDouble;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultValue;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.CopyNumberType;
import gov.nih.nci.ncicb.tcgaportal.level4.util.FishersExactImpl;
import gov.nih.nci.ncicb.tcgaportal.level4.util.PearsonCorrelationCalculator;

/**
 * Description :
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class Level4QueriesJDBCImplGeneDBUnitSlowTest extends Level4QueriesJDBCImplDBUnitConfig {

    private static String testDbFileName_Gene = "TestDB_Gene.xml";

    public Level4QueriesJDBCImplGeneDBUnitSlowTest() {
        super(testDbFileName_Gene);
    }

    public void testGetAnomalyResultsCopyNumberColumn() throws Throwable {

        queries = new Level4QueriesJDBCImpl(1000, new PearsonCorrelationCalculator(),
                new FishersExactImpl(), dataSource);        
        String inputFile = "input_testGetAnomalyResultsCopyNumberColumn.xml";
        String expectedResultFileName = "expected_testGetAnomalyResultsCopyNumberColumn";

        filter = new FilterSpecifier();
        createFilterFromInputFile(inputFile);

        Results results = runAnomalyFilter();

        //write the results to a text file
        String actualResultFileName = "testGetAnomalyResultsCopyNumberColumn";
        writeDatasetOutput(results, PORTAL_FOLDER + actualResultFileName);

        assertEquals("Results should only have 1 row", new Integer(1), new Integer(results.getActualRowCount()));
        assertEquals("EGFR", results.getRow(0).getName());

        // make sure the annotations are there
        for (int i = 0; i < results.getActualRowCount(); i++) {
            assertNotNull(results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_CNV));
            assertNotNull(results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_CHROM));
            assertNotNull(results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_START));
            assertNotNull(results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_STOP));
        }
    }


    public void testWithNoColumns() throws Throwable {
        
        queries = new Level4QueriesJDBCImpl(1000, new PearsonCorrelationCalculator(),
                new FishersExactImpl(), dataSource);
        
        String inputFile = "input_testWithNoColumns.xml";
        String expectedResultFileName = "expected_testWithNoColumns";

        filter = new FilterSpecifier();
        createFilterFromInputFile(inputFile);

        Results results = runAnomalyFilter();

        //write the results to a text file
        String actualResultFileName = "testWithNoColumns";
        //writeDatasetOutput(results, portalFolder + actualResultFileName);

        // should be one row for each gene, and no data columns
        assertEquals(1, results.getActualRowCount());
        assertEquals(0, results.getRow(0).getColumnResults().length);
    }

    public void testNoColumnsWithChromosome() throws Throwable {

        queries = new Level4QueriesJDBCImpl(1000, new PearsonCorrelationCalculator(),
                new FishersExactImpl(), dataSource);
        
        String inputFile = "input_testNoColumnsWithChromosome.xml";
        String expectedResultFileName = "expected_testNoColumnsWithChromosome";

        filter = new FilterSpecifier();
        createFilterFromInputFile(inputFile);

        Results results = runAnomalyFilter();

        //write the results to a text file
        String actualResultFileName = "testNoColumnsWithChromosome";
        //writeDatasetOutput(results, portalFolder + actualResultFileName);

        for (int i = 0; i < results.getActualRowCount(); i++) {
            ResultRow row = results.getRow(i);
            String chrom = (String) row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_CHROM);
            assertEquals(row.getName() + " is not on chrom 7!", "7", chrom);
        }
    }

    public void testCopyNumberGistic() throws Throwable {

        queries = new Level4QueriesJDBCImpl(1000, new PearsonCorrelationCalculator(),
                new FishersExactImpl(), dataSource);
        
        String inputFile = "input_testCopyNumberGistic.xml";

        filter = new FilterSpecifier();
        createFilterFromInputFile(inputFile);

        boolean foundOne = false;
        for (ColumnType column : filter.getColumnTypes()) {
            if (column instanceof CopyNumberType) {
                CopyNumberType copyNumberType = (CopyNumberType) column;
                if (copyNumberType.getCalculationType() == CopyNumberType.CalculationType.GISTIC)
                    foundOne = true;
            }
        }
        if (!foundOne) {
            throw new IllegalStateException("no copy number columns found !");
        }

        Results results = runAnomalyFilter();

        //write the results to a text file
        String actualResultFileName = "testCopyNumberGistic";
        writeDatasetOutput(results, PORTAL_FOLDER + actualResultFileName);

        assertTrue("No results found", results.getActualRowCount()>0);
        // check that all results are either blanks or gistics
        for (int i = 0; i < results.getActualRowCount(); i++) {
            for (final ResultValue value : results.getRow(i).getColumnResults()) {
                // all values should be GISTIC type
                assertTrue(value instanceof ResultDouble || value instanceof ResultBlank);
            }
        }
    }

}
