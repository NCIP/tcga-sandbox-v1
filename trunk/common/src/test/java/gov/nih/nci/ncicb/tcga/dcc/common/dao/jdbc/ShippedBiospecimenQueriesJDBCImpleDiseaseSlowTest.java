package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.ShippedBiospecimen;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;

import java.io.File;
import java.util.Arrays;

/**
 * Slow test for shipped biospecimen queries against the *disease* schema
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ShippedBiospecimenQueriesJDBCImpleDiseaseSlowTest extends DBUnitTestCase {
    // disease schema!
    private static final String PROPERTIES_FILE = "oracle.unittest.properties";
    private static final String TEST_DATA_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "dao/ShippedBiospecimenDiseaseTestData.xml";

    private final ShippedBiospecimenQueriesJDBCImpl queries;

    public ShippedBiospecimenQueriesJDBCImpleDiseaseSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);

        queries = new ShippedBiospecimenQueriesJDBCImpl();
        queries.setDataSource(getDataSource());
    }

    public void testAddShippedBiospecimens() {
        final ShippedBiospecimen aliquot = new ShippedBiospecimen();
        aliquot.setUuid("this-is-a-uuid");
        aliquot.setBarcode("TCGA-A3-1234-01A-02D-6789-20");
        // type set, but not type id, so DAO has to look it up
        aliquot.setShippedBiospecimenType(ShippedBiospecimen.SHIPPED_ITEM_NAME_ALIQUOT);
        aliquot.setProjectCode("TCGA");
        aliquot.setTssCode("A3");
        aliquot.setParticipantCode("1234");
        aliquot.setSampleTypeCode("01");
        aliquot.setSampleSequence("A");
        aliquot.setPortionSequence("02");
        aliquot.setAnalyteTypeCode("D");
        aliquot.setPlateId("6789");
        aliquot.setBcrCenterId("20");

        aliquot.setShippedBiospecimenId(123L);
        aliquot.setShippedBiospecimenTypeId(1);


        queries.addShippedBiospecimens(Arrays.asList(aliquot));

        long biospecimenId = getSimpleJdbcTemplate().queryForLong("select shipped_biospecimen_id from shipped_biospecimen where uuid='this-is-a-uuid' " +
                "and project_code='TCGA' and tss_code='A3' and participant_code='1234' and bcr_center_id='20' and built_barcode='TCGA-A3-1234-01A-02D-6789-20'");

        assertEquals(123L, biospecimenId);
    }
}
