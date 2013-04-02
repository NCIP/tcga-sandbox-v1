package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.RedactionQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDHierarchyQueries;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Test class for RedactionQueriesJDBCImpl for disease schema
 *
 * @author Shelley Alonso
 *         Last updated by: $Shelley Alonso$
 * @version $Rev$
 */

public class RedactionQueriesJDBCImplDiseaseSlowTest extends DBUnitTestCase {
    // disease schema
    private static final String PROPERTIES_FILE = "oracle.unittest.properties";
    private static final String TEST_DATA_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "dao/RedactionQueriesTestDiseaseData.xml";

    private static final String appContextFile = "samples/applicationContext-dbunit.xml";
    private final ApplicationContext appContext;
    private final RedactionQueries queries;
    private final UUIDHierarchyQueries uuidHierarchyQueries;
    private SimpleJdbcTemplate sjdbc;

    public RedactionQueriesJDBCImplDiseaseSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
        appContext = new ClassPathXmlApplicationContext(appContextFile);
        queries = (RedactionQueries) appContext.getBean("redactionDiseaseQueries");
        ((RedactionQueriesJDBCImpl)queries).setCommonSchema(false);
        uuidHierarchyQueries = (UUIDHierarchyQueries) appContext.getBean("uuidHierarchyQueries") ;

        sjdbc = new SimpleJdbcTemplate(getDataSource());
    }

    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.CLEAN_INSERT;
    }

    @Override
    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.DELETE_ALL;
    }

    private List<UUIDDetail> makeUuidList() {
        List<UUIDDetail> uuidList = new LinkedList<UUIDDetail>();
        uuidList.add(new UUIDDetail(){{
            setUuid("810adcea-d548-42c8-ba9c-f5619597d931");
        }});
        uuidList.add(new UUIDDetail(){{
            setUuid("03c14838-bbab-4487-80c3-c5fdf6ec83f9");
        }});
        uuidList.add(new UUIDDetail(){{
            setUuid("24a37373-26d4-45fb-8ad8-b4c90100cafd");
        }});
        return uuidList;
    }

    @Test
    public void testRedact() {
        queries.redact(SqlParameterSourceUtils.createBatch(makeUuidList().toArray()));
        assertEquals(3, getBBChildren("REDACTED"));
        assertEquals(3, getShippedChildren("REDACTED"));
    }

    @Test
    public void testRescind() {
        queries.rescind(SqlParameterSourceUtils.createBatch(makeUuidList().toArray()));
        assertEquals(5, getShippedChildren("RESCINDED"));
        assertEquals(5, getBBChildren("RESCINDED"));
    }

    private int getBBChildren(String Action) {
        String selectString;
        if (Action.equals("REDACTED")) {
            selectString = "Select count (*) from biospecimen_barcode where is_viewable=0 ";
        } else {
            selectString = "Select count (*) from biospecimen_barcode where is_viewable=1";
        }
        int childCount = sjdbc.queryForInt(selectString);
        return childCount;
    }

    private int getShippedChildren(String action) {
         String selectString;
         if (action.equals("REDACTED")){
            selectString = "Select count (*) from shipped_biospecimen where is_redacted=1";
        } else {
            selectString = "Select count (*) from shipped_biospecimen where is_redacted=0";
        }
         int childCount = sjdbc.queryForInt(selectString);
         return childCount;
     }

}
