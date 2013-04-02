package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.ArchiveInfo;
import org.junit.Test;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * LevelThreeQueriesImpl unit test for common db
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class LevelThreeQueriesImplCommonDBUnitSlowTest extends DBUnitTestCase {
    private LevelThreeQueriesImpl levelThreeQueries = null;
    private SimpleJdbcTemplate template = null;

    private static final String PROPERTIES_FILE = "common.unittest.properties";
    private static final String TEST_DATA_FOLDER = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;

    private static final String TEST_DATA_FILE = "qclive/dao/levelThreeQueriesImpl_CommonTestDb.xml";

    public LevelThreeQueriesImplCommonDBUnitSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    @Override
    public void setUp() throws Exception {

        super.setUp();
        levelThreeQueries = new LevelThreeQueriesImpl();
        levelThreeQueries.setDataSource(getDataSource());
        template = new SimpleJdbcTemplate(getDataSource());
    }

	@Test
	public void testGetTumorBarcodesForFile() {
		List<String> tumorBarcodes= levelThreeQueries.getTumorBarcodesForFile(100l);
		assertTrue(tumorBarcodes.size() == 1);
		assertEquals(tumorBarcodes.get(0), "TCGA-AG-A032-01A-01D-A077-02");
	}

}
