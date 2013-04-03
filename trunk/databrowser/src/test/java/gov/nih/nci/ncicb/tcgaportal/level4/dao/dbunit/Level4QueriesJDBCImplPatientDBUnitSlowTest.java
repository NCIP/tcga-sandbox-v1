package gov.nih.nci.ncicb.tcgaportal.level4.dao.dbunit;

import gov.nih.nci.ncicb.tcgaportal.level4.dao.jdbc.Level4QueriesJDBCImpl;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.AnomalyResultRatio;
import gov.nih.nci.ncicb.tcgaportal.level4.util.FishersExactImpl;
import gov.nih.nci.ncicb.tcgaportal.level4.util.PearsonCorrelationCalculator;

/**
 * Description :
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class Level4QueriesJDBCImplPatientDBUnitSlowTest extends Level4QueriesJDBCImplDBUnitConfig {

    static String testDbFileName_Gene = "TestDB_Patient.xml";

    public Level4QueriesJDBCImplPatientDBUnitSlowTest() {
        super(testDbFileName_Gene);
        queries = new Level4QueriesJDBCImpl(1000, new PearsonCorrelationCalculator(),
                new FishersExactImpl(), dataSource);
    }

    public void testGetAnomalyResultsByPatientCopyNumber() throws Throwable {

        queries = new Level4QueriesJDBCImpl(1000, new PearsonCorrelationCalculator(),
                new FishersExactImpl(), dataSource);
        
        String inputFile = "input_testGetAnomalyResultsByPatientCopyNumber.xml";

        filter = new FilterSpecifier();
        createFilterFromInputFile(inputFile);

        Results results = runAnomalyFilter();

        String actualResultFileName = "testGetAnomalyResultsByPatientCopyNumber";
        writeDatasetOutput(results, PORTAL_FOLDER + actualResultFileName);

        assertEquals("Results should only have 1 row", new Integer(1),
                new Integer(results.getActualRowCount()));
        AnomalyResultRatio rr = (AnomalyResultRatio) results.getRow(0).getColumnResults()[0];
        assertEquals("Only 3 genes were selected, so the total for the result should be 3", 3, rr.getTotal());
        assertEquals("TCGA-02-0015", results.getRow(0).getName());

    }

    public void testPatientSearchTwoColumns() throws Throwable {

        queries = new Level4QueriesJDBCImpl(1000, new PearsonCorrelationCalculator(),
                new FishersExactImpl(), dataSource);
        String inputFile = "input_testPatientSearchTwoColumns.xml";
        //String expectedResultFileName = "expected_testGetAnomalyResultsByPatient";

        filter = new FilterSpecifier();
        createFilterFromInputFile(inputFile);

        Results results = runAnomalyFilter();

        //write the results to a text file
        String actualResultFileName = "testGetAnomalyResultsByPatient";
        writeDatasetOutput(results, PORTAL_FOLDER + actualResultFileName);

        assertNotNull(results);
        assertTrue(results.getActualRowCount() > 0);
        assertEquals(2, results.getRow(0).getColumnResults().length);
    }

}
