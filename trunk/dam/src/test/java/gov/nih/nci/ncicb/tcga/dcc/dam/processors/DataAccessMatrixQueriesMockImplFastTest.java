package gov.nih.nci.ncicb.tcga.dcc.dam.processors;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DAMUtilsI;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * DataAccessMatrixQueriesMockImpl unit tests
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class DataAccessMatrixQueriesMockImplFastTest {

    private static final String DATA_FILENAME = Thread.currentThread().getContextClassLoader()
            .getResource("samples" + File.separator + "filePackager").getPath() + File.separator + "testdownload.txt";
    private static final String LEVEL_1 = "1";
    private static final String LEVEL_2 = "2";
    private static final String LEVEL_3 = "3";
    public static final String DISEASE_1 = "DIS1";
    public static final String DISEASE_2 = "DIS2";

    private Mockery mockery = new JUnit4Mockery();
    private DAMUtilsI mockDamUtilsI = mockery.mock(DAMUtilsI.class);

    private DataAccessMatrixQueriesMockImpl dataAccessMatrixQueriesMock;
    private Set<String> diseasesSetInContext;

    @Before
    public void setUp() throws IOException {

        diseasesSetInContext = new HashSet<String>();
        dataAccessMatrixQueriesMock = new DataAccessMatrixQueriesMockImpl(DATA_FILENAME) {

            @Override
            protected void setDiseaseInContext(final String diseaseType) {
                diseasesSetInContext.add(diseaseType);
            }
        };

        dataAccessMatrixQueriesMock.setDamUtils(mockDamUtilsI);
    }

    @Test
    public void testGetFileInfoForSelectedDataSetsLevel1() throws DataAccessMatrixQueries.DAMQueriesException {
        testGetFileInfoForSelectedDataSets(LEVEL_1, true, true);
    }

    @Test
    public void testGetFileInfoForSelectedDataSetsLevel1MultipleDisease() throws DataAccessMatrixQueries.DAMQueriesException {
        testGetFileInfoForSelectedDataSets(LEVEL_1, true, false);
    }

    @Test
    public void testGetFileInfoForSelectedDataSetsLevel2() throws DataAccessMatrixQueries.DAMQueriesException {
        testGetFileInfoForSelectedDataSets(LEVEL_2, false, true);
    }

    @Test
    public void testGetFileInfoForSelectedDataSetsLevel3() throws DataAccessMatrixQueries.DAMQueriesException {
        testGetFileInfoForSelectedDataSets(LEVEL_3, true, true);
    }

    @Test
    public void testGetFileInfoForSelectedDataSetsLevelClinical() throws DataAccessMatrixQueries.DAMQueriesException {
        testGetFileInfoForSelectedDataSets(DataAccessMatrixQueries.LEVEL_CLINICAL, false, true);
    }

    @Test
    public void testGetFileInfoForSelectedDataSetsLevelMetadata() throws DataAccessMatrixQueries.DAMQueriesException {
        testGetFileInfoForSelectedDataSets(DataAccessMatrixQueries.LEVEL_METADATA, true, true);
    }

    /**
     * Check assertions after calling dataAccessMatrixQueriesMock.getFileInfoForSelectedDataSets()
     * with a List of {@link List} of {@link DataSet} from the given level
     *
     * @param singleDisease
     * @param level the level for each {@link DataSet} passed to the method call
     * @throws DataAccessMatrixQueries.DAMQueriesException
     *          if the method call throws such an exception
     */
    private void testGetFileInfoForSelectedDataSets(final String level,
                                                    final boolean isProtected,
                                                    final boolean singleDisease)
            throws DataAccessMatrixQueries.DAMQueriesException {

        final int dataSetsSize = 2;
        final List<DataSet> dataSets = makeDataSets(dataSetsSize, level, isProtected);

        mockery.checking(new Expectations() {{
            one(mockDamUtilsI).groupDataSetsByDisease(with(any(List.class)));
            will(returnValue(makeDiseaseToDataSetsMap(dataSets, singleDisease)));
        }});

        final List<DataFile> dataFiles = dataAccessMatrixQueriesMock.getFileInfoForSelectedDataSets(dataSets, false);
        final Set<String> fileIds = new HashSet<String>();

        assertNotNull(dataFiles);
        assertEquals(dataSetsSize, dataFiles.size());


        for (final DataFile dataFile : dataFiles) {

            assertNotNull(dataFile);

            final String fileName = dataFile.getFileName();
            final String fileId = dataFile.getFileId();
            final String platformTypeId = dataFile.getPlatformTypeId();
            final String centerId = dataFile.getCenterId();
            final boolean isDataFileProtected = dataFile.isProtected();
            final String displaySample = dataFile.getDisplaySample();
            final String dataFileLevel = dataFile.getLevel();

            assertEquals(level, dataFile.getLevel());

            assertNotNull(fileName);
            assertTrue(fileName.endsWith(".idat"));
            assertNotNull(dataFile.getSize());

            assertNotNull(fileId);
            fileIds.add(fileId);

            if (LEVEL_3.equals(level)) {
                assertFalse(isDataFileProtected);
            } else {
                assertEquals(isProtected, isDataFileProtected);
            }

            compareFieldsWithDataSets(dataSets, platformTypeId, centerId, isDataFileProtected, displaySample, dataFileLevel);
        }

        assertEquals(dataFiles.size(), fileIds.size());
        assertTrue(fileIds.contains("1"));
        assertTrue(fileIds.contains("2"));

        if(singleDisease) {
            assertEquals(1, diseasesSetInContext.size());
            assertTrue(diseasesSetInContext.contains(DISEASE_1));
        } else {
            assertEquals(2, diseasesSetInContext.size());
            assertTrue(diseasesSetInContext.contains(DISEASE_1));
            assertTrue(diseasesSetInContext.contains(DISEASE_2));
        }
    }

    /**
     * Return a {@link Map} of Disease to {@link DataSet}s.
     * The resulting {@link Map} will have as many keys as the requested number of disease.
     * If there is not enough {@link DataSet} to make the {@link Map}, then an empty {@link List} will be used
     *
     * @param dataSets the {@link List} of {@link DataSet} to create the {@link Map} from
     * @param numberOfDiseases the number of disease in the resulting {@link Map}
     * @return a {@link Map} of Disease to {@link DataSet}s
     */
    /**
     * Assuming the given dataSets {@link List} is size 2, put it in a {@link Map} with a single key or 2 keys.
     * depending on the value of <code>singleDisease</code>
     * @param dataSets the {@link DataSet} values to put in a {@link Map}
     * @param singleDisease whether to create a {@link Map} of with a single key or 2 keys
     * @returna a {@link Map} of with a single key or 2 keys
     */
    private Map<String, List<DataSet>> makeDiseaseToDataSetsMap(final List<DataSet> dataSets,
                                                                boolean singleDisease) {

        assertNotNull(dataSets);
        assertEquals(2, dataSets.size());

        final Map<String, List<DataSet>> result = new HashMap<String, List<DataSet>>();

        if(singleDisease) {
            result.put(DISEASE_1, dataSets);
        } else {
            result.put(DISEASE_1, dataSets.subList(0,1));
            result.put(DISEASE_2, dataSets.subList(1,2));
        }

        return result;
    }

    /**
     * Check that the given {@link DataFile} fields can all be found in 1 single {@link DataSet}
     *
     * @param dataSets            the {@link List} of {@link DataSet} to search into
     * @param platformTypeId      the {@link DataFile} platform type Id
     * @param centerId            the {@link DataFile} center Id
     * @param isDataFileProtected the {@link DataFile} isProtected field
     * @param sample              the {@link DataFile} sample
     * @param level               the {@link DataFile} level
     */
    private void compareFieldsWithDataSets(final List<DataSet> dataSets,
                                           final String platformTypeId,
                                           final String centerId,
                                           final boolean isDataFileProtected,
                                           final String sample,
                                           final String level) {
        assertNotNull(dataSets);
        assertNotNull(platformTypeId);
        assertNotNull(centerId);
        assertNotNull(sample);

        boolean foundDataSet = false;
        for (final DataSet dataSet : dataSets) {

            if (platformTypeId.equals(dataSet.getPlatformTypeId())) {

                foundDataSet = true;
                assertEquals(centerId, dataSet.getCenterId());
                assertEquals(sample, dataSet.getSample());

                if (LEVEL_3.equals(level)) {
                    assertFalse(isDataFileProtected);
                } else {
                    assertEquals(isDataFileProtected, dataSet.isProtected());
                }
            }
        }

        assertTrue(foundDataSet);
    }

    /**
     * Create a {@link List} of {@link DataSet} for testing
     *
     * @param size        the number of {@link DataSet} in the {@link List}
     * @param level       the level for each {@link DataSet}
     * @param isProtected whether each {@link DataSet} is protected or not
     * @return a {@link List} of {@link DataSet} for testing
     */
    private List<DataSet> makeDataSets(final int size,
                                       final String level,
                                       final boolean isProtected) {

        final List<DataSet> result = new ArrayList<DataSet>();

        for (int i = 0; i < size; i++) {

            final DataSet dataSet = new DataSet();
            final int id = i + 1;

            dataSet.setLevel(level);
            dataSet.setPlatformTypeId("platformTypeId" + id);
            dataSet.setCenterId("centerId" + id);
            dataSet.setSample("sample" + id);
            dataSet.setProtected(isProtected);

            result.add(dataSet);
        }

        return result;
    }
}
