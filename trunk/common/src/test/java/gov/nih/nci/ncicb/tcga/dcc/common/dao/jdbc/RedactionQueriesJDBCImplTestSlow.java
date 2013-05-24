package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.RedactionQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDHierarchyQueries;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.io.File;
import java.util.List;

/**
 * Test class for RedactionQueriesJDBCImpl
 *
 * @author Shelley Alonso
 *         Last updated by: $Shelley Alonso$
 * @version $Rev$
 */

public class RedactionQueriesJDBCImplTestSlow extends DBUnitTestCase {
    // common schema
    private static final String PROPERTIES_FILE = "unittest.properties";
    private static final String TEST_DATA_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "dao/RedactionQueriesTestData.xml";

    private static final String appContextFile = "samples/applicationContext-dbunit.xml";
    private final RedactionQueries queries;
    private final UUIDHierarchyQueries uuidHierarchyQueries;
    private SimpleJdbcTemplate sjdbc;

    public RedactionQueriesJDBCImplTestSlow() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
        final ApplicationContext appContext = new ClassPathXmlApplicationContext(appContextFile);
        queries = (RedactionQueries) appContext.getBean("redactionQueries");
        uuidHierarchyQueries = (UUIDHierarchyQueries) appContext.getBean("uuidHierarchyQueries");
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

    @Test
    public void testRedactParticipant() {
        final String participantBarcode = "TCGA-02-0001";
        final List<UUIDDetail> uuidList = uuidHierarchyQueries.getChildUUIDs(participantBarcode, ConstantValues.BARCODE);
        final SqlParameterSource[] childUuids = SqlParameterSourceUtils.createBatch(uuidList.toArray());
        queries.redact(childUuids, true);
        assertNotNull(uuidList);
        assertNotNull(childUuids);
        assertEquals(9, uuidList.size());
        assertEquals("30a1fe5e-5b12-472c-aa86-c2db8167ab23", uuidList.get(0).getUuid());
        assertEquals("810adcea-d548-42c8-ba9c-f5619597d931", uuidList.get(8).getUuid());
        assertEquals(9, childUuids.length);
        assertEquals("30a1fe5e-5b12-472c-aa86-c2db8167ab23", childUuids[0].getValue("uuid"));
        assertEquals("810adcea-d548-42c8-ba9c-f5619597d931", childUuids[8].getValue("uuid"));
        assertEquals(3, getBBChildren("TCGA-02-0001", "BARCODE", "REDACTED"));
        assertEquals(3, getShippedChildren("TCGA-02-0001", "BARCODE", "REDACTED"));
        for (final UUIDDetail childUuid : uuidList) {
            assertTrue(getUuidHierarchyRedactionStatus(childUuid.getUuid()));
        }
    }

    private boolean getUuidHierarchyRedactionStatus(final String uuid) {
        int redactedValue = sjdbc.queryForInt("select is_redacted from uuid_hierarchy where regexp_like(uuid,?,'i')", uuid);
        return redactedValue == 1;
    }

    @Test
    public void testRedactParticipantAsUUID() {
        final String participantUuid = "d88b35a3-a291-457a-b15b-a314859b25c5";
        final List<UUIDDetail> uuidList = uuidHierarchyQueries.getChildUUIDs(participantUuid, ConstantValues.UUID);
        final SqlParameterSource[] childUuids = SqlParameterSourceUtils.createBatch(uuidList.toArray());
        queries.redact(childUuids, true);
        assertEquals(2, getShippedChildren("d88b35a3-a291-457a-b15b-a314859b25c5", "UUID", "REDACTED"));
        assertEquals(2, getBBChildren("d88b35a3-a291-457a-b15b-a314859b25c5", "UUID", "REDACTED"));
        for (final UUIDDetail childUuid : uuidList) {
            assertTrue(getUuidHierarchyRedactionStatus(childUuid.getUuid()));
        }
    }

    public void testRedactParticipantWithdrawal() {
        final String participantUuid = "d88b35a3-a291-457a-b15b-a314859b25c5";
        final List<UUIDDetail> uuidList = uuidHierarchyQueries.getChildUUIDs(participantUuid, ConstantValues.UUID);
        final SqlParameterSource[] childUuids = SqlParameterSourceUtils.createBatch(uuidList.toArray());
        queries.redact(childUuids, false);
        assertEquals(2, getShippedChildren("d88b35a3-a291-457a-b15b-a314859b25c5", "UUID", "WITHDRAWAL_REDACTION"));
        assertEquals(0, getBBChildren("d88b35a3-a291-457a-b15b-a314859b25c5", "UUID", "REDACTED"));
        for (final UUIDDetail childUuid : uuidList) {
            assertTrue(getUuidHierarchyRedactionStatus(childUuid.getUuid()));
        }
    }

    @Test
    public void testRescindParticipant() {
        final String participantBarcode = "TCGA-02-0001";
        final SqlParameterSource[] childUuids = SqlParameterSourceUtils.createBatch(uuidHierarchyQueries.getChildUUIDs(participantBarcode, ConstantValues.BARCODE).toArray());
        queries.rescind(childUuids);
        assertEquals(3, getShippedChildren("TCGA-02-0001", "BARCODE", "RESCINDED"));
        assertEquals(3, getBBChildren("TCGA-02-0001", "BARCODE", "RESCINDED"));
    }

    @Test
    public void testRescindParticipantAsUUID() {
        final String participantUuid = "d88b35a3-a291-457a-b15b-a314859b25c5";
        final SqlParameterSource[] childUuids = SqlParameterSourceUtils.createBatch(uuidHierarchyQueries.getChildUUIDs(participantUuid, ConstantValues.UUID).toArray());
        queries.rescind(childUuids);
        assertEquals(2, getShippedChildren("d88b35a3-a291-457a-b15b-a314859b25c5", "UUID", "RESCINDED"));
        assertEquals(2, getBBChildren("d88b35a3-a291-457a-b15b-a314859b25c5", "UUID", "RESCINDED"));
    }

    @Test
    public void testRescindRedactedParticipantAsUUID() {
        final String participantUuid = "d88b35a3-a291-457a-b15b-a314859b25c5";
        final SqlParameterSource[] childUuids = SqlParameterSourceUtils.createBatch(uuidHierarchyQueries.getChildUUIDs(participantUuid, ConstantValues.UUID).toArray());
        queries.redact(childUuids, true);
        queries.rescind(childUuids);
        final List<UUIDDetail> uuidList = uuidHierarchyQueries.getChildUUIDs(participantUuid, ConstantValues.UUID);
        assertEquals(2, getShippedChildren("d88b35a3-a291-457a-b15b-a314859b25c5", "UUID", "RESCINDED"));
        assertEquals(2, getBBChildren("d88b35a3-a291-457a-b15b-a314859b25c5", "UUID", "RESCINDED"));
        for (final UUIDDetail childUuid : uuidList) {
            assertFalse(getUuidHierarchyRedactionStatus(childUuid.getUuid()));
        }
    }

    @Test
    public void testRedactSample() {
        final String participantBarcode = "TCGA-02-0001-01C";
        final List<UUIDDetail> uuidList = uuidHierarchyQueries.getChildUUIDs(participantBarcode, ConstantValues.BARCODE);
        final SqlParameterSource[] childUuids = SqlParameterSourceUtils.createBatch(uuidList.toArray());
        queries.redact(childUuids, true);
        assertEquals(3, getShippedChildren("TCGA-02-0001-01C", "BARCODE", "REDACTED"));
        assertEquals(3, getBBChildren("TCGA-02-0001-01C", "BARCODE", "REDACTED"));
        for (final UUIDDetail childUuid : uuidList) {
            assertTrue(getUuidHierarchyRedactionStatus(childUuid.getUuid()));
        }
    }

    @Test
    public void testRedactSampleAsUUID() {
        final String participantUuid = "3df90f1c-94da-4bd5-bd8e-a0bc92d715f9";
        final List<UUIDDetail> uuidList = uuidHierarchyQueries.getChildUUIDs(participantUuid, ConstantValues.UUID);
        final SqlParameterSource[] childUuids = SqlParameterSourceUtils.createBatch(uuidList.toArray());
        queries.redact(childUuids, true);
        assertEquals(3, getShippedChildren("3df90f1c-94da-4bd5-bd8e-a0bc92d715f9", "UUID", "REDACTED"));
        assertEquals(3, getBBChildren("3df90f1c-94da-4bd5-bd8e-a0bc92d715f9", "UUID", "REDACTED"));
        for (final UUIDDetail childUuid : uuidList) {
            assertTrue(getUuidHierarchyRedactionStatus(childUuid.getUuid()));
        }
    }

    @Test
    public void testRescindSample() {
        final String participantBarcode = "TCGA-02-0001-01C";
        final SqlParameterSource[] childUuids = SqlParameterSourceUtils.createBatch(uuidHierarchyQueries.getChildUUIDs(participantBarcode, ConstantValues.BARCODE).toArray());
        queries.rescind(childUuids);
        assertEquals(3, getShippedChildren("TCGA-02-0001-01C", "BARCODE", "RESCINDED"));
        assertEquals(3, getBBChildren("TCGA-02-0001-01C", "BARCODE", "RESCINDED"));
    }

    @Test
    public void testRescindSampleAsUUID() {
        final String participantUuid = "3df90f1c-94da-4bd5-bd8e-a0bc92d715f9";
        final SqlParameterSource[] childUuids = SqlParameterSourceUtils.createBatch(uuidHierarchyQueries.getChildUUIDs(participantUuid, ConstantValues.UUID).toArray());
        queries.rescind(childUuids);
        assertEquals(3, getShippedChildren("3df90f1c-94da-4bd5-bd8e-a0bc92d715f9", "UUID", "RESCINDED"));
        assertEquals(3, getBBChildren("3df90f1c-94da-4bd5-bd8e-a0bc92d715f9", "UUID", "RESCINDED"));
    }

    private int getBBChildren(String parent, String parentType, String Action) {
        String selectString;
        if (Action.equals("REDACTED")) {
            if (parentType.equals("BARCODE")) {
                selectString = "Select count (*) from biospecimen_barcode where is_viewable=0 and uuid in (select uuid from uuid_hierarchy start with barcode = ? connect by prior uuid=parent_uuid)";
            } else {
                selectString = "Select count (*) from biospecimen_barcode where is_viewable=0 and uuid in (select uuid from uuid_hierarchy start with uuid = ? connect by prior uuid=parent_uuid)";
            }
        } else {
            if (parentType.equals("BARCODE")) {
                selectString = "Select count (*) from biospecimen_barcode where is_viewable=1 and uuid in (select uuid from uuid_hierarchy start with barcode = ? connect by prior uuid=parent_uuid)";
            } else {
                selectString = "Select count (*) from biospecimen_barcode where is_viewable=1 and uuid in (select uuid from uuid_hierarchy start with uuid = ? connect by prior uuid=parent_uuid)";
            }
        }
        return sjdbc.queryForInt(selectString, parent);
    }

    private int getShippedChildren(String parent, String parentType, String action) {
        String selectString;
        if (action.equals("REDACTED")) {
            if (parentType.equals("BARCODE")) {
                selectString = "Select count (*) from shipped_biospecimen where is_redacted=1 and is_viewable=0 and uuid in (select uuid from uuid_hierarchy start with barcode = ? connect by prior uuid=parent_uuid)";
            } else {
                selectString = "Select count (*) from shipped_biospecimen where is_redacted=1 and is_viewable=0 and uuid in (select uuid from uuid_hierarchy start with uuid = ? connect by prior uuid=parent_uuid)";
            }
        } else if (action.equals("WITHDRAWAL_REDACTION")) {
              if (parentType.equals("BARCODE")) {
                selectString = "Select count (*) from shipped_biospecimen where is_redacted=1 and uuid in (select uuid from uuid_hierarchy start with barcode = ? connect by prior uuid=parent_uuid)";
            } else {
                selectString = "Select count (*) from shipped_biospecimen where is_redacted=1 and uuid in (select uuid from uuid_hierarchy start with uuid = ? connect by prior uuid=parent_uuid)";
            }
        } else {
            if (parentType.equals("BARCODE")) {
                selectString = "Select count (*) from shipped_biospecimen where is_redacted=0 and uuid in (select uuid from uuid_hierarchy start with barcode = ? connect by prior uuid=parent_uuid)";
            } else {
                selectString = "Select count (*) from shipped_biospecimen where is_redacted=0 and uuid in (select uuid from uuid_hierarchy start with uuid = ? connect by prior uuid=parent_uuid)";
            }
        }
        return sjdbc.queryForInt(selectString, parent);
    }
}
