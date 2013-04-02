package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileCollection;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * DB unit test for FileCollectionQueries.
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class FileCollectionQueriesJDBCImplTest extends DBUnitTestCase {

    private static final String PROPERTIES_FILE = "unittest.properties";
    private static final String TEST_DATA_FOLDER =
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "dao/FileCollectionQueries_TestData.xml";

    private static final int NUM_COLLECTIONS_IN_TEST_FILE = 1;

    private FileCollectionQueriesJDBCImpl fileCollectionQueries;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private long fakeFileSize;
    private String fakeMd5;

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public FileCollectionQueriesJDBCImplTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    public void setUp() throws Exception {
        super.setUp();
        fileCollectionQueries = new FileCollectionQueriesJDBCImpl() {
            // override these two so we don't need to have a real file

            @Override
            protected long getFileSize(final File file) {
                return fakeFileSize;
            }

            @Override
            protected String getFileMd5(final File file) {
                return fakeMd5;
            }
        };

        fileCollectionQueries.setDataSource(getDataSource());

        TumorQueriesJDBCImpl tumorQueries = new TumorQueriesJDBCImpl();
        tumorQueries.setDataSource(getDataSource());
        fileCollectionQueries.setTumorQueries(tumorQueries);

        CenterQueriesJDBCImpl centerQueries = new CenterQueriesJDBCImpl();
        centerQueries.setDataSource(getDataSource());
        fileCollectionQueries.setCenterQueries(centerQueries);

        PlatformQueriesJDBCImpl platformQueries = new PlatformQueriesJDBCImpl();
        platformQueries.setDataSource(getDataSource());
        fileCollectionQueries.setPlatformQueries(platformQueries);

        FileInfoQueriesJDBCImpl fileInfoQueries = new FileInfoQueriesJDBCImpl();
        fileInfoQueries.setDataSource(getDataSource());
        fileCollectionQueries.setFileQueries(fileInfoQueries);

        simpleJdbcTemplate = new SimpleJdbcTemplate(getDataSource());
        // make sure sequence will not collide with IDs of rows in test data
        for (int i = 0; i<NUM_COLLECTIONS_IN_TEST_FILE; i++) {
            simpleJdbcTemplate.update("select file_collection_seq.nextval from dual");
        }
    }

    public void testSaveCollection() throws Exception {
        testSaveCollection("stuff1", false, null, null, null, null, 2L, null, null, null);
        testSaveCollection("stuff2", false, "TEST", null, null, null, 2L, 3L, null, null);
        testSaveCollection("stuff3", false, "TEST", "ABC", null, null, 2L, 3L, null, null);
        testSaveCollection("stuff4", false, "TEST", "ABC", "dcc.org", null, 2L, 3L, 4L, null);
        testSaveCollection("stuff5", false, "TEST", "ABC", "dcc.org", "ArrayPlatform12", 2L, 3L, 4L, 6L);
    }

    public void testSaveCollectionExisting() {
        // should find this in db already
        FileCollection fileCollection = fileCollectionQueries.saveCollection("something", true, null, null, null, null);
        assertEquals(new Long(1), fileCollection.getId());

    }

    public void testSaveCollectionBadDisease() {
        try {
            fileCollectionQueries.saveCollection("hi", false, "yo!", null, null, null);
            fail("exception was not thrown");
        } catch (IllegalArgumentException e) {
            // good
            assertEquals("Error saving collection 'hi': Disease 'yo!' not found", e.getMessage());
        }
    }

    public void testSaveCollectionBadPlatform() {
        try {
            fileCollectionQueries.saveCollection("nope", true, "TEST", "ABC", null, "heyyyy");
            fail("exception was not thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Error saving collection 'nope': Platform heyyyy not found", e.getMessage());
        }
    }

    private void testSaveCollection(String collectionName,
                                    boolean isIdentifiable, String disease, String centerType, String center, String platform,
                                    Long expectedVisibilityId, Long expectedDiseaseId, Long expectedCenterId, Long expectedPlatformId) {
        FileCollection collection = fileCollectionQueries.saveCollection(collectionName, isIdentifiable, disease, centerType, center, platform);
        assertNotNull(collection);

        Map<String, Object> values = simpleJdbcTemplate.queryForMap("select * from file_collection where collection_name=?", collectionName);
        assertEquals(collection.getId().toString(), values.get("file_collection_id").toString());
        assertEquals(expectedVisibilityId.toString(), values.get("visibility_id").toString());
        if (disease == null) {
            assertNull(values.get("disease_id"));
        } else {
            assertEquals(expectedDiseaseId.toString(), values.get("disease_id").toString());
        }

        if (centerType == null) {
            assertNull(values.get("center_type_code"));
        } else {
            assertEquals(centerType, values.get("center_type_code").toString());
        }

        if (center == null) {
            assertNull(values.get("center_id"));
        } else {
            assertEquals(expectedCenterId.toString(), values.get("center_id").toString());
        }

        if (platform == null) {
            assertNull(values.get("platform_id"));
        } else {
            assertEquals(expectedPlatformId.toString(), values.get("platform_id").toString());
        }

    }

    public void testSaveFileToCollectionExisting() throws Exception {
        final Date startOfTest = new Date();

        fakeFileSize = 1234;
        fakeMd5 = "d7de0d28728602e8359c9f79709592be";
        FileCollection existingCollection = new FileCollection();
        existingCollection.setId(1L);
        existingCollection.setName("something");
        // file is already in this collection
        fileCollectionQueries.saveFileToCollection(existingCollection, "/path/to/squirrel", startOfTest);

        // size and date should have been updated
        assertEquals(1, simpleJdbcTemplate.queryForInt("select count(*) from file_info where file_name=?", "squirrel"));
        Date newFileDate = simpleJdbcTemplate.queryForObject("select file_date from file_to_collection where file_collection_id=1 and file_id=100", Date.class);
        assertEquals(simpleDateFormat.format(startOfTest), simpleDateFormat.format(newFileDate));

        String newMd5 = simpleJdbcTemplate.queryForObject("select md5 from file_info where file_id=100", String.class);
        assertEquals(fakeMd5, newMd5);

        assertEquals(fakeFileSize, simpleJdbcTemplate.queryForLong("select file_size from file_info where file_id=100"));
    }

    public void testSaveFileToCollection() {
        // new file
        FileCollection collection = new FileCollection();
        collection.setId(1L);
        collection.setName("something");
        final Date now = new Date();
        fakeFileSize = 555;
        fakeMd5 = "9ddb2465c1a9887865cd1674d91a2af0";
        final String fileLocation = "/la/la/la/la/test.txt";
        fileCollectionQueries.saveFileToCollection(collection, fileLocation, now);

        // make sure new file was created correctly, and is linked to collection
        Long fileId = simpleJdbcTemplate.queryForLong("select file_id from file_to_collection where file_collection_id=1 and file_location_url=?", fileLocation);

        assertEquals("test.txt", simpleJdbcTemplate.queryForObject("select file_name from file_info where file_id=?", String.class, fileId));
        assertEquals(fakeFileSize, simpleJdbcTemplate.queryForLong("select file_size from file_info where file_id=?", fileId));
        assertEquals(fakeMd5, simpleJdbcTemplate.queryForObject("select md5 from file_info where file_id=?", String.class, fileId));

        assertEquals(simpleDateFormat.format(now), simpleDateFormat.format(simpleJdbcTemplate.queryForObject(
                "select file_date from file_to_collection where file_collection_id=1 and file_id=? and file_location_url=?",
                Date.class, fileId, fileLocation)));
    }


}
