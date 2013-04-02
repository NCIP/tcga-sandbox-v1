package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.AliquotsToDiseases;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.BcrUuid;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.Control;

import java.io.File;

/**
 * test class for the control queries in the disease database
 *
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ControlQueriesJDBCImplDiseaseSlowTest extends DBUnitTestCase {

    private static final String PROPERTIES_FILE = "oracle.unittest.properties";
    private static final String TEST_DATA_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "dao/ControlDiseaseTestData.xml";

    private final ControlQueriesJDBCImpl queries;

    public ControlQueriesJDBCImplDiseaseSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);

        queries = new ControlQueriesJDBCImpl();
        queries.setDataSource(getDataSource());
    }

    public void testUpdateControlForShippedBiospecimen() throws Exception {
        final Control control = new Control();
        final AliquotsToDiseases aliquotsToDiseases = new AliquotsToDiseases();
        final BcrUuid bcrUuid = new BcrUuid();
        bcrUuid.setValue("uuid3");
        aliquotsToDiseases.setBcrAliquotUuid(bcrUuid);
        control.setAliquotsToDiseases(aliquotsToDiseases);
        final String query = "select is_control from shipped_biospecimen where uuid='uuid3'";
        assertEquals(0, getSimpleJdbcTemplate().queryForInt(query));
        queries.updateControlForShippedBiospecimen(control);
        assertEquals(1, getSimpleJdbcTemplate().queryForInt(query));
    }
}
