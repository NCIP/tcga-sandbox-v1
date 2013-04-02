package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.util.SourceFileTypeFinder;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelTwo;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelTwoConsolidated;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelTwoThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetClinical;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetLevelTwoThree;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Fast tests for DAMQueriesLevel2
 *
 * @author Rohini Raman Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class DAMQueriesLevel2FastTest {

    private final Mockery context = new JUnit4Mockery();

    private final static String SAMPLE_DIR =
            Thread.currentThread().getContextClassLoader().getResource("samples/portal/dao/level2").getPath() + File.separator;
    private static final String GENERATED_CONSOLIDATED_FILE_WITH_CONSTANTS_NAME = "generatedConsolidatedFileWithConstants.txt";
    private final static File GENERATED_FILE_WITH_CONSTANTS = new File(SAMPLE_DIR + File.separator + GENERATED_CONSOLIDATED_FILE_WITH_CONSTANTS_NAME);
    private static final String GENERATED_CONSOLIDATED_FILE_NAME = "generatedConsolidatedFile.txt";
    private final static File GENERATED_FILE = new File(SAMPLE_DIR + File.separator + GENERATED_CONSOLIDATED_FILE_NAME);

    private DAMUtilsI mockDamUtilsI;

    @Before
    public void setUp() {
        mockDamUtilsI = context.mock(DAMUtilsI.class);
    }

    @After
    public void deleteGeneratedFiles() {
        if (GENERATED_FILE_WITH_CONSTANTS.exists()) {
            GENERATED_FILE_WITH_CONSTANTS.deleteOnExit();
        }
    }

    /**
     * Validates whether the method prepareQueryAndBindVariables returns hint query if the expected row count is greater
     * or equal to the limit set in the properties file
     */

    @Test
    public void validateHintQuery() {

        DAMQueriesLevel2 queries = new DAMQueriesLevel2() {
            protected long getExpectedRowCount(final DataFileLevelTwoThree dataFile) {
                return 15;
            }

            public int getMinExpectedRowsToUseHintQuery() {
                return 10;
            }
        };
        List<Object> queryBindValues = new ArrayList<Object>();
        DataFileLevelTwoThree dataFile = new DataFileLevelTwo();
        List<Integer> dataSet = new ArrayList<Integer>();
        dataSet.add(40);
        dataFile.setDataSetsDP(dataSet);
        assertTrue(queries.prepareQueryAndBindVariables(dataFile, queryBindValues, 5).contains(DAMQueriesLevel2.HYBRIDIZATION_VALUE_QUERY_HINT));
    }

    /**
     * Validates whether the method prepareQueryAndBindVariables returns regular query if the expected row count is less
     * than the limit set in the properties file
     */

    @Test
    public void validateRegularQuery() {

        DAMQueriesLevel2 queries = new DAMQueriesLevel2() {
            protected long getExpectedRowCount(final DataFileLevelTwoThree dataFile) {
                return 5;
            }

            public int getMinExpectedRowsToUseHintQuery() {
                return 10;
            }
        };
        List<Object> queryBindValues = new ArrayList<Object>();
        DataFileLevelTwoThree dataFile = new DataFileLevelTwo();
        List<Integer> dataSet = new ArrayList<Integer>();
        dataSet.add(40);
        dataFile.setDataSetsDP(dataSet);
        assertFalse(queries.prepareQueryAndBindVariables(dataFile, queryBindValues, 5).contains(DAMQueriesLevel2.HYBRIDIZATION_VALUE_QUERY_HINT));
    }

    @Test
    public void testBuildConsolidatedFiles() throws DataAccessMatrixQueries.DAMQueriesException, IOException {
        final DAMQueriesLevel2 queries = new DAMQueriesLevel2();
        final SourceFileTypeFinder sourceFileTypeFinder = context.mock(SourceFileTypeFinder.class);
        queries.setSourceFileTypeFinder(sourceFileTypeFinder);

        context.checking(new Expectations() {{
            one(sourceFileTypeFinder).findSourceFileType(1);
            will(returnValue("type1"));
            one(sourceFileTypeFinder).findSourceFileType(2);
            will(returnValue("type1"));
            one(sourceFileTypeFinder).findSourceFileType(3);
            will(returnValue("type2"));
            one(sourceFileTypeFinder).findSourceFileType(4);
            will(returnValue("type3"));
            one(sourceFileTypeFinder).findSourceFileType(5);
            will(returnValue(null));
        }});

        final DataFile file1 = makeDataFile("1", true);
        final DataFile file2 = makeDataFile("2", true);
        final DataFile file3 = makeDataFile("3", false);
        final DataFile file4 = makeDataFile("4", false);
        final DataFile file5 = makeDataFile("5", true);

        final DataSetLevelTwoThree representativeDataSet = new DataSetLevelTwoThree();
        representativeDataSet.setCenterName("test.org");
        representativeDataSet.setPlatformName("miRNA_Something");

        final List<DataFile> singleFiles = Arrays.asList(file1, file2, file3, file4, file5);
        final List<DataFile> consolidatedFiles = queries.buildConsolidatedFiles(singleFiles, representativeDataSet);
        assertEquals(4, consolidatedFiles.size());

        assertTrue(consolidatedFiles.get(0) instanceof DataFileLevelTwoConsolidated);
        final DataFileLevelTwoConsolidated consolidatedFile1 = (DataFileLevelTwoConsolidated) consolidatedFiles.get(0);
        assertEquals(2, consolidatedFile1.getConstituentDataFiles().size());
        assertEquals(file1, consolidatedFile1.getConstituentDataFiles().first());
        assertEquals(file2, consolidatedFile1.getConstituentDataFiles().last());
        assertEquals("test.org_miRNA_Something_type1.txt", consolidatedFile1.getFileName());
        assertTrue(consolidatedFile1.isProtected());
        assertEquals("selected_samples", consolidatedFile1.getDisplaySample());

        final DataFileLevelTwoConsolidated consolidatedFile2 = (DataFileLevelTwoConsolidated) consolidatedFiles.get(1);
        assertEquals(1, consolidatedFile2.getConstituentDataFiles().size());
        assertEquals(file3, consolidatedFile2.getConstituentDataFiles().first());
        assertEquals("test.org_miRNA_Something_type2.txt", consolidatedFile2.getFileName());
        assertFalse(consolidatedFile2.isProtected());

        final DataFileLevelTwoConsolidated consolidatedFile3 = (DataFileLevelTwoConsolidated) consolidatedFiles.get(2);
        assertEquals(1, consolidatedFile3.getConstituentDataFiles().size());
        assertEquals(file4, consolidatedFile3.getConstituentDataFiles().last());
        assertEquals("test.org_miRNA_Something_type3.txt", consolidatedFile3.getFileName());
        assertFalse(consolidatedFile3.isProtected());

        // this had no source file type so is in the final list
        assertEquals(file5, consolidatedFiles.get(3));
    }

    private DataFile makeDataFile(final String id, final boolean isProtected) {
        final DataFile dataFile = new DataFileLevelTwo();
        dataFile.setFileId(id);
        dataFile.setFileName(id);
        dataFile.setProtected(isProtected);
        return dataFile;
    }

    @Test
    public void testCreateConsolidatedFilesWithConstants() throws IOException {
        final DAMQueriesLevel2 queries = new DAMQueriesLevel2() {
            protected String getUniqueFilename(final DataFileLevelTwoThree dataFile) {
                return GENERATED_CONSOLIDATED_FILE_WITH_CONSTANTS_NAME;
            }
        };
        queries.setTempfileDirectory(SAMPLE_DIR);

        final DataFileLevelTwoConsolidated consolidatedFile = new DataFileLevelTwoConsolidated();
        final DataFileLevelTwo dataFileWithConstants1 = new DataFileLevelTwo();
        dataFileWithConstants1.setPath(SAMPLE_DIR + "a.dataMatrixWithConstants.txt");
        dataFileWithConstants1.setFileName("a.dataMatrixWithConstants.txt");
        final DataFileLevelTwo dataFileWithConstants2 = new DataFileLevelTwo();
        dataFileWithConstants2.setPath(SAMPLE_DIR + "b.dataMatrixWithConstants.txt");
        dataFileWithConstants2.setFileName("b.dataMatrixWithConstants.txt");
        final DataFileLevelTwo dataFileWithConstants3 = new DataFileLevelTwo();
        dataFileWithConstants3.setPath(SAMPLE_DIR + "c.dataMatrixWithConstants.txt");
        dataFileWithConstants3.setFileName("c.dataMatrixWithConstants.txt");
        consolidatedFile.addConstituentDataFile(dataFileWithConstants3);

        consolidatedFile.addConstituentDataFile(dataFileWithConstants1);
        consolidatedFile.addConstituentDataFile(dataFileWithConstants2);

        queries.createConsolidatedFiles(consolidatedFile);

        // now verify that the generated file is as expected
        assertTrue(GENERATED_FILE_WITH_CONSTANTS.exists());
        final String generatedContent = FileUtil.readFile(GENERATED_FILE_WITH_CONSTANTS, false);
        final String expectedContent = FileUtil.readFile(new File(SAMPLE_DIR + "/expectedConsolidatedFileWithConstants.txt"), false);
        assertEquals(expectedContent, generatedContent);
    }

    @Test
    public void testCreateConsolidatedFiles() throws IOException {
        final DAMQueriesLevel2 queries = new DAMQueriesLevel2() {
            protected String getUniqueFilename(final DataFileLevelTwoThree dataFile) {
                return GENERATED_CONSOLIDATED_FILE_NAME;
            }
        };
        queries.setTempfileDirectory(SAMPLE_DIR);

        final DataFileLevelTwoConsolidated consolidatedFile = new DataFileLevelTwoConsolidated();
        final DataFileLevelTwo dataFile1 = new DataFileLevelTwo();
        dataFile1.setPath(SAMPLE_DIR + "a.dataMatrix.txt");
        dataFile1.setFileName("a.dataMatrix.txt");
        final DataFileLevelTwo dataFile2 = new DataFileLevelTwo();
        dataFile2.setPath(SAMPLE_DIR + "b.dataMatrix.txt");
        dataFile2.setFileName("b.dataMatrix.txt");

        consolidatedFile.addConstituentDataFile(dataFile1);
        consolidatedFile.addConstituentDataFile(dataFile2);

        queries.createConsolidatedFiles(consolidatedFile);

        // now verify that the generated file is as expected
        assertTrue(GENERATED_FILE.exists());
        final String generatedContent = FileUtil.readFile(GENERATED_FILE, false);
        final String expectedContent = FileUtil.readFile(new File(SAMPLE_DIR + "/expectedConsolidatedFile.txt"), false);
        assertEquals(expectedContent, generatedContent);
    }

    @Test
    public void testGetFileInfoForClinicalDataSets() throws DataAccessMatrixQueries.DAMQueriesException {

        final DAMQueriesLevel2 damQueriesLevel2 = new DAMQueriesLevel2();

        damQueriesLevel2.setDamUtils(mockDamUtilsI);

        final List<DataSet> dataSets = new ArrayList<DataSet>();
        final DataSetClinical clinicalDataSet = new DataSetClinical();
        dataSets.add(clinicalDataSet);

        final Map<String, List<DataSet>> diseaseToDataSetsMap = new HashMap<String, List<DataSet>>();
        diseaseToDataSetsMap.put(dataSets.get(0).getDiseaseType(), dataSets);

        context.checking(new Expectations() {{
            one(mockDamUtilsI).groupDataSetsByDisease(dataSets);
            will(returnValue(diseaseToDataSetsMap));
        }});

        final List<DataFile> dataFiles = damQueriesLevel2.getFileInfoForSelectedDataSets(dataSets, false);
        assertEquals(0, dataFiles.size());
    }

    @Test
    public void testGetFileInfoForSelectedDataSetsMultipleDiseases() throws DataAccessMatrixQueries.DAMQueriesException {

        final List<String> diseaseInContextList = new ArrayList<String>();

        final DAMQueriesLevel2 damQueriesLevel2 = new DAMQueriesLevel2() {

            protected void setDiseaseInContext(final String diseaseType) {
                diseaseInContextList.add(diseaseType);
            }
        };

        damQueriesLevel2.setDamUtils(mockDamUtilsI);

        final String disease1 = "DIS1";
        final String disease2 = "DIS2";

        final List<DataSet> dataSets1 = makeDataSetForDiseaseType(disease1);
        final List<DataSet> dataSets2 = makeDataSetForDiseaseType(disease2);
        final List<DataSet> allDataSets = dataSets1;
        allDataSets.addAll(dataSets2);

        final Map<String, List<DataSet>> diseaseToDataSetsMap = new HashMap<String, List<DataSet>>();
        diseaseToDataSetsMap.put(disease1, dataSets1);
        diseaseToDataSetsMap.put(disease2, dataSets2);

        context.checking(new Expectations() {{
            one(mockDamUtilsI).groupDataSetsByDisease(allDataSets);
            will(returnValue(diseaseToDataSetsMap));
        }});

        final List<DataFile> dataFiles = damQueriesLevel2.getFileInfoForSelectedDataSets(allDataSets, false);
        assertNotNull(dataFiles);
        assertEquals(0, dataFiles.size());

        // Each disease in the dataSets has been set in the context, one after the other
        assertEquals(2, diseaseInContextList.size());
        assertTrue(diseaseInContextList.contains(disease1));
        assertTrue(diseaseInContextList.contains(disease2));
    }

    /**
     * Return a {@link List} of 1 {@link DataSet} for the given disease type.
     *
     * Note: the {@link DataSet} will not have barcodes, to make it a fast test (no DB queries).
     *
     * @param diseaseType the disease type
     * @return a {@link List} of 1 {@link DataSet} for the given disease type
     */
    private List<DataSet> makeDataSetForDiseaseType(final String diseaseType) {

        final List<DataSet> result = new ArrayList<DataSet>();
        final String level2 = "2";

        final DataSetLevelTwoThree dataSet = new DataSetLevelTwoThree();
        dataSet.setDiseaseType(diseaseType);
        dataSet.setLevel(level2);
        dataSet.setBarcodes(new ArrayList<String>());

        result.add(dataSet);

        return result;
    }
}
