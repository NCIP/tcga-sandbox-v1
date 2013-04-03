/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.BiospecimenMetaData;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.PortionAnalyte;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.SampleType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDType;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.uuid.dao.jdbc.UUIDBrowserDAOImpl;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Test Class for the UUIDBrowser DAO impl
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class UUIDBrowserDAOImplSlowTest extends DBUnitTestCase {

    private final static String tcgaTestPropertiesFile = "tcgadata.properties";
    public static final String UUID_DB_DUMP_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    public static final String UUID_DB_FILE = "UUIDBrowser_TestDB.xml";

    private UUIDBrowserDAOImpl uuidBrowserDAOImpl;

    public UUIDBrowserDAOImplSlowTest() {
        super(UUID_DB_DUMP_FOLDER, UUID_DB_FILE, tcgaTestPropertiesFile);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        final Center center = new Center();
        center.setCenterDisplayText("broad.mit.edu (CGCC)");
        final Center bcr = new Center();
        bcr.setCenterDisplayText("intgen.org (BCR)");
        final SampleType st = new SampleType();
        st.setDefinition("Tumor Normal");
        uuidBrowserDAOImpl = new UUIDBrowserDAOImpl() {
            public int getInClauseSize() {
                return 2;
            }
        };

        uuidBrowserDAOImpl.setDataSource(getDataSource());
        uuidBrowserDAOImpl.setUuidTypes(new LinkedList<UUIDType>() {{
            add(new UUIDType(1, "Participant", 1, "patient"));
            add(new UUIDType(2, "Sample", 2, "sample"));
            add(new UUIDType(3, "Aliquot", 3, "aliquot"));
        }});
        uuidBrowserDAOImpl.setPortionAnalytes(new LinkedList<PortionAnalyte>() {{
            add(new PortionAnalyte("D", "DNA"));
        }});
        uuidBrowserDAOImpl.setCenters(new LinkedList<Center>() {{
            add(center);
        }});
        uuidBrowserDAOImpl.setBcrs(new LinkedList<Center>() {{
            add(bcr);
        }});
        uuidBrowserDAOImpl.setSampleTypes(new LinkedList<SampleType>() {{
            add(st);
        }});
    }

    public void testGetUUIDRowsFromBarcode() throws Exception {
        final String barcode = "TCGA-01-007";
        final List<BiospecimenMetaData> res = uuidBrowserDAOImpl.getBiospecimenMetaDataRowsFromBarcode(barcode);
        assertNotNull(res);
        assertTrue(res.size() == 1);
    }

    public void testGetUUIDRowsFromUUID() throws Exception {
        final String uuid = "1";
        final List<BiospecimenMetaData> res = uuidBrowserDAOImpl.getBiospecimenMetaDataRowsFromUUID(uuid);
        assertNotNull(res);
        assertTrue(res.size() == 1);
        assertFalse(res.get(0).getRedacted());
    }

    public void testGetUUIDRowsRedactedItem() {
        final List<BiospecimenMetaData> metaDataResults = uuidBrowserDAOImpl.getBiospecimenMetaDataRowsFromUUID("6");
        assertTrue(metaDataResults.get(0).getRedacted());
    }

    public void testGetUUIDRowsNullShipped() {
        final List<BiospecimenMetaData> metaDataResults = uuidBrowserDAOImpl.getBiospecimenMetaDataRowsFromUUID("6");
        assertFalse(metaDataResults.get(0).getShipped());
    }

    public void testGetUUIDRowsNoShipped() {
        final List<BiospecimenMetaData> metaDataResults = uuidBrowserDAOImpl.getBiospecimenMetaDataRowsFromUUID("5");
        assertFalse(metaDataResults.get(0).getShipped());
    }

    public void testGetUUIDRowsYesShipped() {
        final List<BiospecimenMetaData> metaDataResults = uuidBrowserDAOImpl.getBiospecimenMetaDataRowsFromUUID("4");
        assertTrue(metaDataResults.get(0).getShipped());
    }

    public void testGetUUIDRowsFromMultipleUUID() throws Exception {
        final List<String> uuids = new LinkedList<String>() {{
            add("1");
            add("2");
        }};
        final List<BiospecimenMetaData> res = uuidBrowserDAOImpl.getBiospecimenMetaDataRowsFromMultipleUUID(uuids);
        assertNotNull(res);
        assertTrue(res.size() == 2);
        assertEquals("2", res.get(0).getUuid());
        assertEquals("1", res.get(1).getUuid());
    }

    public void testGetUUIDRowsFromMultipleBarcode() throws Exception {
        final List<String> barcodes = new LinkedList<String>() {{
            add("TCGA-01-007");
            add("TCGA-00-1234");
        }};
        final List<BiospecimenMetaData> res = uuidBrowserDAOImpl.getBiospecimenMetaDataRowsFromMultipleBarcode(barcodes);
        assertNotNull(res);
        assertTrue(res.size() == 2);
        assertEquals("TCGA-01-007", res.get(0).getBarcode());
        assertEquals("TCGA-00-1234", res.get(1).getBarcode());
    }

    public void testExistingBarcodes() {

        final List<String> barcodes = Arrays.asList("tcga-a3-3308-11a-01w-0898-10",
                "tcga-a3-3308-11a-01d-0859-05",
                "tcga-a3-3308-11a-01w-0898-11",
                "tcga-a3-3308-11a-01d-0859-06",
                "tcga-a3-3308-11a-01w-0898-12",
                "tcga-a3-3308-11a-01d-0859-07");
        final List<String> barcodesExistsInTheDB = uuidBrowserDAOImpl.getExistingBarcodes(barcodes);

        assertEquals(6, barcodesExistsInTheDB.size());
    }

    public void testNonExistingBarcodes() {
        final List<String> barcodes = Arrays.asList("tcga-a3-3308-11a-01w-0898-10", "tcga-a3-3308-11a-01d-0859-05", "tcga-a3-3308-11a-01w-0898-20");
        final List<String> barcodesExistsInTheDB = uuidBrowserDAOImpl.getExistingBarcodes(barcodes);

        assertEquals(2, barcodesExistsInTheDB.size());
    }
}
