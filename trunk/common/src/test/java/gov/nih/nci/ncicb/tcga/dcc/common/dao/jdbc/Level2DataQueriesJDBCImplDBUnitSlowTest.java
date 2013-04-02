package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.Level2DataQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.DataException;
import gov.nih.nci.ncicb.tcga.dcc.common.service.JDBCCallback;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class to test Level2DataQueries JDBC implementation
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class Level2DataQueriesJDBCImplDBUnitSlowTest extends DBUnitTestCase implements JDBCCallback {
    private static final String PROPERTIES_FILE = "diseaseSpecific.unittest.properties";
    private static final String TEST_DATA_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "dao/Level2_TestData.xml";

    private static final String appContextFile = "samples/applicationContext-dbunit.xml";
    private final ApplicationContext appContext;
    private final Level2DataQueries queries;
    private List<String> hybRefIdDataGroupNameList;

    public Level2DataQueriesJDBCImplDBUnitSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
        appContext = new ClassPathXmlApplicationContext(appContextFile);
        queries = (Level2DataQueries) appContext.getBean("level2DataQueries");

    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.CLEAN_INSERT;
    }

    @Override
    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.DELETE_ALL;
    }


    public void testGetHybridizationRefIds() throws Exception {
        final List<Long> dataSetIdList = new ArrayList<Long>();
        dataSetIdList.add((long) 1);
        final List<Long> hybridizationRefIdList = queries.getHybridizationRefIds(dataSetIdList);
        assertEquals(2, hybridizationRefIdList.size());
        assertTrue(getHybRefIdsExpectedData().containsAll(hybridizationRefIdList));

    }

    public void testGetBarcodesForHybrefIds() throws Exception {
        final Map<String, Long> barcodeHybrefMap = queries.getBarcodesForHybrefIds(getHybRefIdsExpectedData());
        assertEquals(2, barcodeHybrefMap.size());
        assertTrue(getBarcodeHybRefIdsTestData().keySet().containsAll(barcodeHybrefMap.keySet()));

    }

    public void testGetExperimentSourceFileTypes() throws Exception {
        final Set<Long> experimentIds = new HashSet<Long>();
        experimentIds.add((long) 1);
        final List<String> sourceFileTypeList = queries.getExperimentSourceFileTypes(experimentIds);
        final String[] testSourceFileTypeList = new String[]{"detection-p-value", "signal_intensity"};
        assertEquals(2, sourceFileTypeList.size());
        assertTrue(sourceFileTypeList.containsAll(Arrays.asList(testSourceFileTypeList)));
    }

    public void testGetLevel2DataSetIds() throws Exception {
        final List<Long> dataSetIds = queries.getLevel2DataSetIds(1, 1, "detection-p-value");
        assertEquals(1, dataSetIds.size());
        assertTrue(getDataSetIdsExpectedData().containsAll(dataSetIds));
    }

    public void processData(Object... data) throws DataException {

        final Map<String, String> rowData = (Map<String, String>) data[1];
        hybRefIdDataGroupNameList.addAll(rowData.keySet());
    }

    public void testUpdateDataSetUseInDAMStatus() throws Exception {
        queries.updateDataSetUseInDAMStatus(getExperimentIdsTestData());
        final List<Integer> testResult = getSimpleJdbcTemplate().getJdbcOperations().query(
                "select use_in_dam from data_set where experiment_id in(?)",
                new ParameterizedRowMapper<Integer>() {
                    public Integer mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
                        return resultSet.getInt("use_in_dam");
                    }
                },
                getExperimentIdsTestData().toArray());
        assertEquals(2, testResult.size());
        final List<Integer> expectedResult = Arrays.asList(new Integer[]{1, 1});
        assertTrue(expectedResult.containsAll(testResult));
    }

    private List<String> getHybDataGroupNamesTestData() {
        final String[] testData = new String[]{"Call", "Beta_Value", "Signal", "NA", "0.2935"};
        return Arrays.asList(testData);
    }

    private List<Long> getHybRefIdsExpectedData() {
        return Arrays.asList(new Long[]{(long) 1, (long) 2});
    }

    private List<Long> getDataSetIdsExpectedData() {
        return Arrays.asList(new Long[]{(long) 1});
    }

    private List<Long> getExperimentIdsTestData() {
        return Arrays.asList(new Long[]{(long) 1});
    }

    private Map<String, Long> getBarcodeHybRefIdsTestData() {
        final Map<String, Long> testData = new LinkedHashMap();
        testData.put("TCGA-02-0014-01A-01D-0186-05", (long) 1);
        testData.put("TCGA-02-0060-01A-01D-0186-05", (long) 2);
        return testData;
    }

    private List<String> getHybRefIdAndDataGroupNameExpectedData() {
        final String[] testData = new String[]{
                "1.Call",
                "1.Beta_Value",
                "1.Signal",
                "1.NA",
                "1.0.2935",
                "2.Call",
                "2.Beta_Value",
                "2.Signal",
                "2.NA",
                "2.0.2935",
                "1.Call",
                "1.Beta_Value",
                "1.Signal",
                "1.NA",
                "1.0.2935",
                "2.Call",
                "2.Beta_Value",
                "2.Signal",
                "2.NA",
                "2.0.2935"
        };
        return Arrays.asList(testData);
    }
}


