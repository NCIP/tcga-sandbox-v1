/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.ClinicalMetaQueriesJDBCImpl;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * DBUnit tests for ClinicalMetaQueries.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class ClinicalMetaQueriesDBUnitSlowTest extends DBUnitTestCase {
    private static final String SAMPLES_FOLDER = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "dao/ClinicalMetaQueries_TestDB.xml";
    private static final String PROPERTIES_FILE = "diseaseSpecific.unittest.properties";

    private ClinicalMetaQueries clinicalMetaQueries;

    public ClinicalMetaQueriesDBUnitSlowTest() {
        super(SAMPLES_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        clinicalMetaQueries = new ClinicalMetaQueriesJDBCImpl();
        ((ClinicalMetaQueriesJDBCImpl) clinicalMetaQueries).setDataSource(getDataSource());
    }

    public void testGetPublicClinicalFileNames() {
        Map<String, Integer> publicFiles = clinicalMetaQueries.getPublicClinicalFileNames("test");
        assertEquals(5, publicFiles.size());
        assertTrue(publicFiles.containsKey("patients"));
        assertTrue(publicFiles.containsKey("analytes"));
        assertTrue(publicFiles.containsKey("protocols"));
        assertTrue(publicFiles.containsKey("follow_up"));
        assertTrue(publicFiles.containsKey("biospecimen_cqcf"));
        assertEquals(new Integer(1), publicFiles.get("patients"));
        assertEquals(new Integer(4), publicFiles.get("analytes"));
        assertEquals(new Integer(5), publicFiles.get("protocols"));
        assertEquals(new Integer(6), publicFiles.get("follow_up"));
        assertEquals(new Integer(7), publicFiles.get("biospecimen_cqcf"));

    }

    public void testGetAllClinicalFileNames() {
        Map<String, Integer> allFiles = clinicalMetaQueries.getAllClinicalFileNames("test");
        assertEquals(7, allFiles.size());
        assertTrue(allFiles.containsKey("patients"));
        assertTrue(allFiles.containsKey("samples"));
        assertTrue(allFiles.containsKey("radiation"));
        assertTrue(allFiles.containsKey("analytes"));
        assertTrue(allFiles.containsKey("protocols"));
        assertTrue(allFiles.containsKey("follow_up"));
        assertTrue(allFiles.containsKey("biospecimen_cqcf"));
        assertEquals(new Integer(1), allFiles.get("patients"));
        assertEquals(new Integer(2), allFiles.get("samples"));
        assertEquals(new Integer(3), allFiles.get("radiation"));
        assertEquals(new Integer(4), allFiles.get("analytes"));
        assertEquals(new Integer(5), allFiles.get("protocols"));
        assertEquals(new Integer(6), allFiles.get("follow_up"));
        assertEquals(new Integer(7), allFiles.get("biospecimen_cqcf"));
    }

    public void testGetClinicalFilePublic() {
        ClinicalMetaQueries.ClinicalFile publicPatientsFile = clinicalMetaQueries.getClinicalFile(1, true, null);
        assertTrue(publicPatientsFile.byPatient);
        assertTrue(publicPatientsFile.publicOnly);
        assertEquals(2, publicPatientsFile.columns.size());
        final ClinicalMetaQueries.ClinicalFileColumn barcodeColumn = publicPatientsFile.columns.get(0);
        final ClinicalMetaQueries.ClinicalFileColumn genderColumn = publicPatientsFile.columns.get(1);
        assertEquals("BCRPATIENTBARCODE", barcodeColumn.columnName);
        assertEquals("GENDER", genderColumn.columnName);

        assertEquals("PATIENT_BARCODE", barcodeColumn.tableColumnName);
        assertEquals("GENDER", genderColumn.tableColumnName);

        assertNull(barcodeColumn.elementTableName);
        assertEquals("PATIENT_ELEMENT", genderColumn.elementTableName);

        assertEquals(1, barcodeColumn.xsdElementId);
        assertEquals(3, genderColumn.xsdElementId);

        assertEquals("patient barcode", barcodeColumn.description);
        assertEquals("gender", genderColumn.description);

        for (final ClinicalMetaQueries.ClinicalFileColumn column : publicPatientsFile.columns) {
            assertEquals("PATIENT", column.tableName);
            assertEquals("PATIENT_ID", column.tableIdColumn);
            assertTrue(column.hasNonNullData);
            assertFalse(column.isProtected);
        }
    }

    public void testGetClinicalFileProtected() {
        final ClinicalMetaQueries.ClinicalFile protectedPatientsFile = clinicalMetaQueries.getClinicalFile(1, false, null);
        assertTrue(protectedPatientsFile.byPatient);
        assertFalse(protectedPatientsFile.publicOnly);
        assertEquals(3, protectedPatientsFile.columns.size());
        final ClinicalMetaQueries.ClinicalFileColumn barcodeColumn = protectedPatientsFile.columns.get(0);
        final ClinicalMetaQueries.ClinicalFileColumn genderColumn = protectedPatientsFile.columns.get(1);
        final ClinicalMetaQueries.ClinicalFileColumn yearOfBirthColumn = protectedPatientsFile.columns.get(2);
        assertEquals("BCRPATIENTBARCODE", barcodeColumn.columnName);
        assertEquals("GENDER", genderColumn.columnName);
        assertEquals("YEAROFBIRTH", yearOfBirthColumn.columnName);
        assertFalse(barcodeColumn.isProtected);
        assertFalse(genderColumn.isProtected);
        assertTrue(yearOfBirthColumn.isProtected);
        assertEquals(1, barcodeColumn.xsdElementId);
        assertEquals(3, genderColumn.xsdElementId);
        assertEquals(4, yearOfBirthColumn.xsdElementId);

        for (final ClinicalMetaQueries.ClinicalFileColumn column : protectedPatientsFile.columns) {
            assertEquals("PATIENT", column.tableName);
            assertTrue(column.hasNonNullData);
            assertEquals("PATIENT_ID", column.tableIdColumn);
        }
    }

    public void testGetClinicalFileNullColumn() {
        ClinicalMetaQueries.ClinicalFile sampleFile = clinicalMetaQueries.getClinicalFile(2, false, null);
        assertEquals(2, sampleFile.columns.size());
        assertEquals("BCRSAMPLEBARCODE", sampleFile.columns.get(0).columnName);
        assertEquals("OCTEMBEDDED", sampleFile.columns.get(1).columnName);
        assertFalse(sampleFile.columns.get(1).hasNonNullData);
    }

    public void testGetClinicalFileMultipleIncludeTables() {
        // the analytes file includes values from analyte and dna tables
        ClinicalMetaQueries.ClinicalFile analyteFile = clinicalMetaQueries.getClinicalFile(4, false, null);
        // should have these columns: analyte barcode, strand, walrus
        assertEquals(3, analyteFile.columns.size());
        assertEquals("BCRANALYTEBARCODE", analyteFile.columns.get(0).columnName);
        assertEquals("WALRUS", analyteFile.columns.get(1).columnName);
        assertEquals("STRAND", analyteFile.columns.get(2).columnName);
    }

    public void testGetFileColumns() {
        Map<String, Integer> protectedFiles = clinicalMetaQueries.getAllClinicalFileNames("test");
        for (final Integer fileId : protectedFiles.values()) {
            ClinicalMetaQueries.ClinicalFile clinicalFile = clinicalMetaQueries.getClinicalFile(fileId, false, "disease");
            // file should have columns, and each should have all fields set
            assertNotNull(clinicalFile.columns);
            assertFalse(clinicalFile.publicOnly);
            assertEquals(fileId, clinicalFile.id);
            assertTrue(clinicalFile.columns.size() > 0);
            for (final ClinicalMetaQueries.ClinicalFileColumn column : clinicalFile.columns) {
                assertNotNull(column.columnName);
                assertNotNull(column.joinClause);
                assertNotNull(column.tableColumnName);
                assertNotNull(column.tableName);
                assertNotNull(column.tableIdColumn);
                assertNotNull(column.xsdElementId);
                assertNotNull("Column description is null", column.description);
                assertNotNull("Column values list is null", column.values);
            }
        }
    }

    public void testGetClinicalFileWithNullDisease() {
        Map<String, Integer> protectedFiles = clinicalMetaQueries.getAllClinicalFileNames();
        for (final Integer fileId : protectedFiles.values()) {
            ClinicalMetaQueries.ClinicalFile clinicalFile = clinicalMetaQueries.getClinicalFile(fileId, false, null);
            assertNotNull(clinicalFile);
            assertNotNull(clinicalFile.columns);
            assertTrue("clinical file columns were not fetched", clinicalFile.columns.size() > 0);
        }
    }

    public void testGetClinicalDataForBarcode() {
        final ClinicalMetaQueries.ClinicalFile patientFile = clinicalMetaQueries.getClinicalFile(1, false, null);
        final String patientBarcode = "barcode1";
        final Map<String, List<Map<ClinicalMetaQueries.ClinicalFileColumn, String>>> data =
                clinicalMetaQueries.getClinicalDataForBarcodes(patientFile, Arrays.asList(patientBarcode), true, true);
        assertEquals(1, data.size());
        assertTrue(data.containsKey("barcode1"));
        assertEquals(1, data.get("barcode1").size());
        final Map<ClinicalMetaQueries.ClinicalFileColumn, String> dataRow = data.get("barcode1").get(0);
        assertEquals(3, dataRow.size());
        assertEquals("barcode1", dataRow.get(patientFile.columns.get(0)));
        assertEquals("FEMALE", dataRow.get(patientFile.columns.get(1)));
        assertEquals("1930", dataRow.get(patientFile.columns.get(2)));
    }


    public void testGetClinicalDataForPatient() {

        final ClinicalMetaQueries.ClinicalFile patientFile = clinicalMetaQueries.getClinicalFile(1, false, null);
        final Map<String, List<Map<ClinicalMetaQueries.ClinicalFileColumn, String>>> data =
                clinicalMetaQueries.getClinicalDataForBarcodes(patientFile, null, true, false);
        assertEquals(3, data.size());
        assertTrue(data.containsKey("barcode1"));
        assertTrue(data.containsKey("barcode2"));
        assertTrue(data.containsKey("barcode3"));
        assertEquals(1, data.get("barcode1").size());
        assertEquals(1, data.get("barcode2").size());

        assertEquals("barcode1", data.get("barcode1").get(0).get(patientFile.columns.get(0)));
        assertEquals("FEMALE", data.get("barcode1").get(0).get(patientFile.columns.get(1)));
        assertEquals("1930", data.get("barcode1").get(0).get(patientFile.columns.get(2)));

        assertEquals("barcode2", data.get("barcode2").get(0).get(patientFile.columns.get(0)));
        assertEquals("MALE", data.get("barcode2").get(0).get(patientFile.columns.get(1)));
        assertEquals("1950", data.get("barcode2").get(0).get(patientFile.columns.get(2)));

        assertEquals("barcode3", data.get("barcode3").get(0).get(patientFile.columns.get(0)));
        assertEquals("FEMALE", data.get("barcode3").get(0).get(patientFile.columns.get(1)));
        assertEquals("1940", data.get("barcode3").get(0).get(patientFile.columns.get(2)));
    }

    public void testGetAnalyteDataForSample() {
        final ClinicalMetaQueries.ClinicalFile analyteFile = clinicalMetaQueries.getClinicalFile(4, false, null);
        final String sampleBarcode = "sample1";
        final Map<String, List<Map<ClinicalMetaQueries.ClinicalFileColumn, String>>> analyteData =
                clinicalMetaQueries.getClinicalDataForBarcodes(analyteFile, Arrays.asList(sampleBarcode), false, false);
        assertEquals(1, analyteData.size());
        assertTrue(analyteData.containsKey("sample1"));
        assertEquals(2, analyteData.get("sample1").size());
        final Map<ClinicalMetaQueries.ClinicalFileColumn, String> row1 = analyteData.get("sample1").get(0);
        assertEquals(3, row1.size());
        assertEquals("analyte1", row1.get(analyteFile.columns.get(0)));
        assertEquals("I am the", row1.get(analyteFile.columns.get(1))); // WALRUS value.  I know, I am hilarious.
        assertEquals("+", row1.get(analyteFile.columns.get(2)));

        final Map<ClinicalMetaQueries.ClinicalFileColumn, String> row2 = analyteData.get("sample1").get(1);
        assertEquals(2, row2.size());
        assertEquals("analyte2", row2.get(analyteFile.columns.get(0)));
        assertEquals("whiskers", row2.get(analyteFile.columns.get(1)));
    }

    public void testGetProtocolData() {
        final ClinicalMetaQueries.ClinicalFile protocolFile = clinicalMetaQueries.getClinicalFile(5, false, null);
        final String sampleBarcode = "sample1";
        final Map<String, List<Map<ClinicalMetaQueries.ClinicalFileColumn, String>>> analyteData =
                clinicalMetaQueries.getClinicalDataForBarcodes(protocolFile, Arrays.asList(sampleBarcode), false, false);
        assertEquals("analyte1", analyteData.get("sample1").get(0).get(protocolFile.columns.get(0)));
        assertEquals("awesome", analyteData.get("sample1").get(0).get(protocolFile.columns.get(1)));
    }

    public void testGetClinicalDataForAllBarcodes() {
        final ClinicalMetaQueries.ClinicalFile patientFile = clinicalMetaQueries.getClinicalFile(1, false, null);
        final Map<String, List<Map<ClinicalMetaQueries.ClinicalFileColumn, String>>> data =
                clinicalMetaQueries.getClinicalDataForBarcodes(patientFile, null, true, true);
        assertEquals(3, data.size());
        assertTrue(data.containsKey("barcode1"));
        assertTrue(data.containsKey("barcode2"));
        assertTrue(data.containsKey("barcode3"));
        assertEquals(1, data.get("barcode1").size());
        final Map<ClinicalMetaQueries.ClinicalFileColumn, String> dataRow = data.get("barcode1").get(0);
        assertEquals(3, dataRow.size());
        assertEquals("barcode1", dataRow.get(patientFile.columns.get(0)));
        assertEquals("FEMALE", dataRow.get(patientFile.columns.get(1)));
        assertEquals("1930", dataRow.get(patientFile.columns.get(2)));
    }

    public void testGetDynamicIdentifierValues() {
        final List<String> dynamicFollowUpValues = clinicalMetaQueries.getDynamicIdentifierValues("follow_up");
        assertNotNull(dynamicFollowUpValues);
        assertEquals(2, dynamicFollowUpValues.size());
        assertTrue(dynamicFollowUpValues.contains("follow_up_v2.0"));
        assertTrue(dynamicFollowUpValues.contains("follow_up_v3.1"));
    }

    public void testGetFollowUpClinicalFiles() {
        ClinicalMetaQueries.ClinicalFile followUp2_0File = clinicalMetaQueries.getClinicalFile(6, "follow_up_v2.0", false, null);
        assertEquals(3, followUp2_0File.columns.size());
        assertEquals("GENDER", followUp2_0File.columns.get(1).columnName);
        assertEquals("follow_up_date", followUp2_0File.columns.get(2).columnName);

        ClinicalMetaQueries.ClinicalFile followUp31File = clinicalMetaQueries.getClinicalFile(6, "follow_up_v3.1", false, null);
        assertEquals(3, followUp31File.columns.size());
        assertEquals("GENDER", followUp31File.columns.get(1).columnName);
        assertEquals("WALRUS", followUp31File.columns.get(2).columnName);
    }

    public void testGetDynamicIdentifierValuesNotDynamic() {
        assertNull(clinicalMetaQueries.getDynamicIdentifierValues("patient"));
    }

    public void testGetDataBiospecimenCqcf() {
        final ClinicalMetaQueries.ClinicalFile biospecimenCqcfFile = clinicalMetaQueries.getClinicalFile(7, false, null);
        final Map<String, List<Map<ClinicalMetaQueries.ClinicalFileColumn, String>>> data =
                clinicalMetaQueries.getClinicalDataForBarcodes(biospecimenCqcfFile, Arrays.asList("barcode1"), true, false);
        assertEquals(1, data.size());
        List<Map<ClinicalMetaQueries.ClinicalFileColumn, String>> dataRows = data.get("barcode1");
        assertEquals(2, dataRows.size());
        assertEquals("grapefruit", dataRows.get(0).get(biospecimenCqcfFile.columns.get(0)));
        assertEquals("orange", dataRows.get(1).get(biospecimenCqcfFile.columns.get(0)));
    }

    public void testGetDataFollowUpMultiple() {
        // there are 2 distinct entries for follow_up_v2.0 and patient 1, make sure they show up as separate rows in the data
        // one follow-up has date 1/1/12 and the other has 2/1/12
        ClinicalMetaQueries.ClinicalFile followUp2_0File = clinicalMetaQueries.getClinicalFile(6, "follow_up_v2.0", false, null);
        Map<String, List<Map<ClinicalMetaQueries.ClinicalFileColumn, String>>> data =
                clinicalMetaQueries.getClinicalDataForBarcodes(followUp2_0File, Arrays.asList("barcode1"), true, false);
        assertEquals(1, data.size());
        assertEquals(2, data.get("barcode1").size());

        Map<ClinicalMetaQueries.ClinicalFileColumn, String> dataForJanFollowup = data.get("barcode1").get(0);
        assertEquals(3, dataForJanFollowup.size());
        assertEquals("barcode1", dataForJanFollowup.get(followUp2_0File.columns.get(0)));
        assertEquals("male", dataForJanFollowup.get(followUp2_0File.columns.get(1)));
        assertEquals("01/01/2012", dataForJanFollowup.get(followUp2_0File.columns.get(2)));

        Map<ClinicalMetaQueries.ClinicalFileColumn, String> dataForFebFollowup = data.get("barcode1").get(1);
        assertEquals(3, dataForFebFollowup.size());
        assertEquals("barcode1", dataForFebFollowup.get(followUp2_0File.columns.get(0)));
        assertEquals("Male", dataForFebFollowup.get(followUp2_0File.columns.get(1)));
        assertEquals("02/01/2012", dataForFebFollowup.get(followUp2_0File.columns.get(2)));

    }

    public void testGetDataFollowUp() {
        // data for the follow up version 3.1 for same patient as prev test has different data than the 2.0 version
        ClinicalMetaQueries.ClinicalFile followUp31File = clinicalMetaQueries.getClinicalFile(6, "follow_up_v3.1", false, null);
        Map<String, List<Map<ClinicalMetaQueries.ClinicalFileColumn, String>>> data =
                clinicalMetaQueries.getClinicalDataForBarcodes(followUp31File, Arrays.asList("barcode1"), true, false);
        assertEquals(1, data.size());
        assertEquals(3, data.get("barcode1").get(0).size());
        assertEquals("barcode1", data.get("barcode1").get(0).get(followUp31File.columns.get(0)));
        assertEquals("female", data.get("barcode1").get(0).get(followUp31File.columns.get(1)));
        assertEquals("bucket", data.get("barcode1").get(0).get(followUp31File.columns.get(2)));
    }
}
