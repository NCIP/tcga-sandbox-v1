package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.AliquotsToDiseases;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.BcrUuid;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.Control;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.ControlElementType;
import org.dbunit.operation.DatabaseOperation;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * test class for the control queries in the common database
 *
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ControlQueriesJDBCImplSlowTest extends DBUnitTestCase {

    private static final String PROPERTIES_FILE = "unittest.properties";
    private static final String TEST_DATA_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() +
                    File.separator;
    private static final String TEST_DATA_FILE = "dao/ControlTestData.xml";
    private ControlQueriesJDBCImpl queries;
    private TumorQueriesJDBCImpl tumorQueries;

    public ControlQueriesJDBCImplSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        queries = new ControlQueriesJDBCImpl();
        tumorQueries = new TumorQueriesJDBCImpl();
        queries.setDataSource(getDataSource());
        tumorQueries.setDataSource(getDataSource());
        queries.setTumorQueries(tumorQueries);
    }

    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.CLEAN_INSERT;
    }

    @Override
    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.DELETE_ALL;
    }

    public void testPersistControl() throws Exception {
        final Control control = new Control();
        final AliquotsToDiseases aliquotsToDiseases = new AliquotsToDiseases();
        final BcrUuid bcrUuid = new BcrUuid();
        bcrUuid.setValue("uuid4");
        aliquotsToDiseases.setBcrAliquotUuid(bcrUuid);
        aliquotsToDiseases.getDiseaseCodeList().add("GBM");
        aliquotsToDiseases.getDiseaseCodeList().add("OV");
        aliquotsToDiseases.getDiseaseCodeList().add("COAD");
        control.setAliquotsToDiseases(aliquotsToDiseases);
        control.setControlElement(ControlElementType.NORMAL_NORMAL_CONTROL);
        final String controlQuery = "select count(*) from control where control_id='40'";
        final String controlTypeQuery = "select control_type_id from control where control_id='40'";
        final String controlDiseaseQuery = "select count(*) from control_to_disease where control_id='40'";
        queries.persistControl(control);
        assertEquals(1, getSimpleJdbcTemplate().queryForInt(controlQuery));
        assertEquals(2, getSimpleJdbcTemplate().queryForInt(controlTypeQuery));
        assertEquals(3, getSimpleJdbcTemplate().queryForInt(controlDiseaseQuery));
    }

    public void testUpdateControlForShippedBiospecimen() throws Exception {
        final Control control = new Control();
        final AliquotsToDiseases aliquotsToDiseases = new AliquotsToDiseases();
        final BcrUuid bcrUuid = new BcrUuid();
        bcrUuid.setValue("uuid5");
        aliquotsToDiseases.setBcrAliquotUuid(bcrUuid);
        control.setAliquotsToDiseases(aliquotsToDiseases);
        final String query = "select is_control from shipped_biospecimen where uuid='uuid5'";
        assertEquals(0, getSimpleJdbcTemplate().queryForInt(query));
        queries.updateControlForShippedBiospecimen(control);
        assertEquals(1, getSimpleJdbcTemplate().queryForInt(query));
    }


    public void testUpdateControlWithUpperCaseForShippedBiospecimen() throws Exception {
        final Control control = new Control();
        final AliquotsToDiseases aliquotsToDiseases = new AliquotsToDiseases();
        final BcrUuid bcrUuid = new BcrUuid();
        bcrUuid.setValue("D5571D40-189F-4BA4-ACAA-07AD37EB5EB8");
        aliquotsToDiseases.setBcrAliquotUuid(bcrUuid);
        control.setAliquotsToDiseases(aliquotsToDiseases);
        final String query = "select is_control from shipped_biospecimen where uuid='d5571d40-189f-4ba4-acaa-07ad37eb5eb8'";
        assertEquals(0, getSimpleJdbcTemplate().queryForInt(query));
        queries.updateControlForShippedBiospecimen(control);
        assertEquals(1, getSimpleJdbcTemplate().queryForInt(query));
    }

    public void testUpdateControlWithLowerCaseForShippedBiospecimen() throws Exception {
        final Control control = new Control();
        final AliquotsToDiseases aliquotsToDiseases = new AliquotsToDiseases();
        final BcrUuid bcrUuid = new BcrUuid();
        bcrUuid.setValue("d5571d40-189f-4ba4-acaa-07ad37eb5eb8");
        aliquotsToDiseases.setBcrAliquotUuid(bcrUuid);
        control.setAliquotsToDiseases(aliquotsToDiseases);
        final String query = "select is_control from shipped_biospecimen where uuid='d5571d40-189f-4ba4-acaa-07ad37eb5eb8'";
        assertEquals(0, getSimpleJdbcTemplate().queryForInt(query));
        queries.updateControlForShippedBiospecimen(control);
        assertEquals(1, getSimpleJdbcTemplate().queryForInt(query));
    }

    public void testAddControlToDiseaseNewInsert() throws Exception {
        final List<Integer> diseaseIdList = new LinkedList<Integer>();
        final String added = "select count(*) from control_to_disease where control_id=20";
        final String total = "select count(*) from control_to_disease";
        diseaseIdList.add(1);
        diseaseIdList.add(2);
        diseaseIdList.add(3);
        queries.addControlToDisease(20L, diseaseIdList);
        assertEquals(3, getSimpleJdbcTemplate().queryForInt(added));
        assertEquals(4, getSimpleJdbcTemplate().queryForInt(total));
    }

    public void testAddControlToDiseaseUpdate() throws Exception {
        final List<Integer> diseaseIdList = new LinkedList<Integer>();
        final String added = "select count(*) from control_to_disease where control_id=30";
        final String total = "select count(*) from control_to_disease";
        diseaseIdList.add(1);
        diseaseIdList.add(2);
        queries.addControlToDisease(30L, diseaseIdList);
        assertEquals(2, getSimpleJdbcTemplate().queryForInt(added));
        assertEquals(2, getSimpleJdbcTemplate().queryForInt(total));
    }

    public void testGetDiseaseIdList() throws Exception {
        final List<String> diseaseCodeTypeList = new LinkedList<String>();
        diseaseCodeTypeList.add("GBM");
        final List<Integer> resList = queries.getDiseaseIdList(diseaseCodeTypeList);
        assertNotNull(resList);
        assertEquals(1, resList.size());
        assertEquals(new Integer(1), resList.get(0));
    }

    public void testGetControlIdExist() throws Exception {
        Long res = queries.getControlId("uuid1");
        assertEquals(new Long(10), res);
    }

    public void testUppercaseUUID() throws Exception {
        assertEquals(new Long(61), queries.getControlId("D5571D40-189F-4BA4-ACAA-07AD37EB5EB8"));
    }

    public void testLowercaseUUID() throws Exception {
        assertEquals(new Long(61), queries.getControlId("d5571d40-189f-4ba4-acaa-07ad37eb5eb8"));
    }

    public void testGetControlIdNonExist() throws Exception {
        Long res = queries.getControlId("hector");
        assertNull(res);
    }

    public void testGetControlTypeIdExist() throws Exception {
        Long res = queries.getControlTypeId("normal_normal_control");
        assertEquals(new Long(2), res);
    }

    public void testGetControlTypeIdNonExist() throws Exception {
        Long res = queries.getControlId("oscar");
        assertNull(res);
    }
}
