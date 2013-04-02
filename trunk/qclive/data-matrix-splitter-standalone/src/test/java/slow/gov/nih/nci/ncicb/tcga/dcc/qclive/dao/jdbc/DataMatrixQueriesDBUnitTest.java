package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.DataMatrixFileBean;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.DataMatrixQueries;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Test class for DataMatrixQueriesJDBCImpl
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DataMatrixQueriesDBUnitTest extends DBUnitTestCase {
    private static final String PROPERTIES_FILE = "common.unittest.properties";
        private static final String TEST_DATA_FOLDER = System.getProperty("user.dir") +
                File.separator+
                "qclive"+
                File.separator+
                "data-matrix-splitter-standalone"+
                File.separator+
                "src"+
                File.separator+
                "test"+
                File.separator+
                "resources"+
                File.separator;

        private static final String TEST_DATA_FILE = "samples"+
                File.separator+
                "DataMatrix_TestData.xml";

        private static final String appContextFile = "applicationContext-dbunit.xml";
        private final ApplicationContext appContext;
        private final DataMatrixQueries queries;


        public DataMatrixQueriesDBUnitTest() {
            super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
            appContext = new FileSystemXmlApplicationContext("file:" + TEST_DATA_FOLDER + appContextFile);
            queries = (DataMatrixQueries) appContext.getBean("dataMatrixQueries");

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

        public void testGetMultipleAliquotDataMatrixFiles(){
            final String archive_1 = "hms.12.0.0";
            final String archive_2 = "hms.13.0.1";

            final Map<String,List<DataMatrixFileBean>> dataMatrixFilesByArchiveName = queries.getMultipleAliquotDataMatrixFiles();
            assertEquals(2,dataMatrixFilesByArchiveName.size());
            final List<String> expectedArchiveNames = Arrays.asList(new String[]{archive_1, archive_2});
            assertTrue(expectedArchiveNames.containsAll(new ArrayList(dataMatrixFilesByArchiveName.keySet())));

            assertEquals(1,dataMatrixFilesByArchiveName.get(archive_1).size());
            assertEquals(2,dataMatrixFilesByArchiveName.get(archive_2).size());
        }


}
