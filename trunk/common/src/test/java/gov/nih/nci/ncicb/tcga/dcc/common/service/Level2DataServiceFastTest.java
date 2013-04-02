package gov.nih.nci.ncicb.tcga.dcc.common.service;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.Level2DataQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.DataException;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test class for Level2 data service
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class Level2DataServiceFastTest {
    private static final String SAMPLE_DIR =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;

    private static final String cacheFileName = "GBM_level2_jhu-usc.edu_HumanMethylation27_detection-p-value.txt";
    private static final int platformId = 1;
    private static final int centerId = 1;
    private static final String sourceFileType = "detection-p-value";

    private Mockery context = new JUnit4Mockery();
    private Level2DataService level2DataService;
    private Level2DataQueries level2DataQueries;


    @Before
    public void setup() {
        level2DataService = new Level2DataService();
        level2DataQueries = context.mock(Level2DataQueries.class);
        level2DataService.setLevel2DataQueries(level2DataQueries);
    }

    @Test
    public void testGetFileName() {
        assertEquals(cacheFileName, level2DataService.getFileName("GBM", "HumanMethylation27", "jhu-usc.edu", "detection-p-value"));
    }

    @Test
    public void testGenerateDataFile() throws DataException, IOException {
        final List<Long> dataSetIds = Arrays.asList(new Long[]{(long) 1});
        final List<String> hybDataGroupNames = getHybDataGroupNames();
        final List<Long> hybRefIds = getHybRefIds();
        final Map<String, Long> barcodesHybRefIdMap = getBarcodeHybRefIds();
        level2DataService.setMinExpectedRowsToUseHintQuery(100);

        context.checking(new Expectations() {{
            one(level2DataQueries).getLevel2DataSetIds(platformId, centerId, sourceFileType);
            will(returnValue(dataSetIds));
            one(level2DataQueries).getHybridizationDataGroupNames(dataSetIds.get(0));
            will(returnValue(hybDataGroupNames));
            one(level2DataQueries).getHybridizationRefIds(dataSetIds);
            will(returnValue(hybRefIds));
            one(level2DataQueries).getBarcodesForHybrefIds(hybRefIds);
            will(returnValue(barcodesHybRefIdMap));
            one(level2DataQueries).getProbeCountForValidChromosome(platformId);
            will(returnValue(0));
            one(level2DataQueries).getProbeCount(platformId);
            will(returnValue(1));
            one(level2DataQueries).getDataGroupsCount(dataSetIds.get(0));
            will(returnValue(1));
            one(level2DataQueries).getHybridizationValue(platformId, false, hybRefIds, dataSetIds, hybDataGroupNames, barcodesHybRefIdMap, false, level2DataService);
            will(generateData(level2DataService, hybDataGroupNames, barcodesHybRefIdMap));

        }});
        final File generatedFile = level2DataService.generateDataFile(platformId, centerId, sourceFileType, SAMPLE_DIR + cacheFileName);

        assertTrue(generatedFile.getPath().equals(new File(SAMPLE_DIR + cacheFileName).toString()));
        assertTrue(generatedFile.exists());
        final String generatedData = FileUtil.readFile(generatedFile, true);
        final StringTokenizer stk = new StringTokenizer(generatedData, "\n");
        int rowCount = 0;
        while (stk.hasMoreTokens()) {
            String row = stk.nextToken();
            rowCount++;
            switch (rowCount) {
                // validate major header
                case 1:
                    assertTrue("Hybridization REF\tTCGA-02-0014-01A-01D-0186-05\tTCGA-02-0014-01A-01D-0186-05\tTCGA-02-0014-01A-01D-0186-05\tTCGA-02-0014-01A-01D-0186-05\tTCGA-02-0014-01A-01D-0186-05\tTCGA-02-0060-01A-01D-0186-05\tTCGA-02-0060-01A-01D-0186-05\tTCGA-02-0060-01A-01D-0186-05\tTCGA-02-0060-01A-01D-0186-05\tTCGA-02-0060-01A-01D-0186-05".equals(row));
                    break;
                // validate minor header
                case 2:
                    assertTrue("CompositeElement REF\tCall\tBeta_Value\tSignal\tNA\t0.2935\tCall\tBeta_Value\tSignal\tNA\t0.2935".equals(row));
                    break;
                // validate data
                case 3:
                    assertTrue("CN_052529\t0.678\t0.678\t0.678\t0.678\t0.678\t0.678\t0.678\t0.678\t0.678\t0.678".equals(row));
                    break;
                // validate data
                case 4:
                    assertTrue("CN_052529\t0.999\t0.999\t0.999\t0.999\t0.999\t0.999\t0.999\t0.999\t0.999\t0.999".equals(row));
                    break;
                default:
                    fail("Generated data in not in the correct format");
            }

        }
        FileUtil.deleteDir(generatedFile);

    }


    private List<String> getHybDataGroupNames() {
        final String[] testData = new String[]{"Call", "Beta_Value", "Signal", "NA", "0.2935"};
        return Arrays.asList(testData);
    }

    private List<Long> getHybRefIds() {
        return Arrays.asList(new Long[]{(long) 1, (long) 2});
    }

    private List<Long> getDataSetIds() {
        return Arrays.asList(new Long[]{(long) 1});
    }

    private Map<String, Long> getBarcodeHybRefIds() {
        final Map<String, Long> testData = new LinkedHashMap();
        testData.put("TCGA-02-0014-01A-01D-0186-05", (long) 1);
        testData.put("TCGA-02-0060-01A-01D-0186-05", (long) 2);
        return testData;
    }


    private Action generateData(final Level2DataService level2DataService,
                                final List<String> hybDataGroupNames,
                                final Map<String, Long> barcodesHybRefIdMap) {
        return new GenerateDataAction(level2DataService, hybDataGroupNames, barcodesHybRefIdMap);
    }


    public class GenerateDataAction implements Action {
        Level2DataService level2DataService;
        List<String> hybDataGroupNames;
        Map<String, Long> barcodesHybRefIdMap;

        public GenerateDataAction(final Level2DataService level2DataService,
                                  final List<String> hybDataGroupNames,
                                  final Map<String, Long> barcodesHybRefIdMap) {
            this.level2DataService = level2DataService;
            this.hybDataGroupNames = hybDataGroupNames;
            this.barcodesHybRefIdMap = barcodesHybRefIdMap;
        }

        public void describeTo(Description description) {
            description.appendText("Generate data ");
        }

        public Object invoke(Invocation invocation) throws Throwable {
            final String[] probe = new String[1];
            probe[0] = "CN_052529";
            level2DataService.processData(probe, getRowData("0.678"), hybDataGroupNames, barcodesHybRefIdMap, false);
            level2DataService.processData(probe, getRowData("0.999"), hybDataGroupNames, barcodesHybRefIdMap, false);
            return null;
        }

        private Map<String, String> getRowData(final String value) {
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
                    "2.0.2935"
            };

            final Map<String, String> rowValues = new HashMap();
            for (String columnName : testData) {
                rowValues.put(columnName, value);
            }
            return rowValues;
        }
    }

}
