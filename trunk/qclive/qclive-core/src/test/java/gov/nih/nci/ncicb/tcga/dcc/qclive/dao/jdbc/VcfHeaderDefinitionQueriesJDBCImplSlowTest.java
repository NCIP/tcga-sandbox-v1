package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFileHeader;

import java.io.File;

/**
 * Slow test for VcfHeaderDefinitionQueries JDBC implementation.
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class VcfHeaderDefinitionQueriesJDBCImplSlowTest extends DBUnitTestCase {

    private static final String PROPERTIES_FILE = "common.unittest.properties";
	private static final String TEST_DATA_FOLDER = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;
	private static final String TEST_DATA_FILE = "/qclive/dao/VcfHeaderDefinition_TestData.xml";

    private VcfHeaderDefinitionQueriesJDBCImpl vcfHeaderDefinitionQueriesJDBC;

    public VcfHeaderDefinitionQueriesJDBCImplSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    public void setUp() throws Exception {
        super.setUp();

        vcfHeaderDefinitionQueriesJDBC = new VcfHeaderDefinitionQueriesJDBCImpl();
        vcfHeaderDefinitionQueriesJDBC.setDataSource(getDataSource());
    }

    private void verifyHeader(final VcfFileHeader header,
                              final String expectedId, final String expectedNumber,
                              final String expectedType, final String expectedDescription) {

        assertNotNull(header);
        assertEquals(expectedId, header.getValueFor("ID"));
        assertEquals(expectedType, header.getValueFor("Type"));
        assertEquals(expectedNumber, header.getValueFor("Number"));
        assertEquals(expectedDescription, header.getValueFor("Description"));
    }

    public void testGetABCInfo() {
        final VcfFileHeader header = vcfHeaderDefinitionQueriesJDBC.getHeaderDefinition("INFO", "ABC");
        verifyHeader(header, "ABC", "1", "String", "hi");
    }

    public void testGetABCFormat() {
        final VcfFileHeader header = vcfHeaderDefinitionQueriesJDBC.getHeaderDefinition("FORMAT", "ABC");
        verifyHeader(header, "ABC", "2", "Integer", "bye");

    }

    public void testGetXZYInfo() {
        final VcfFileHeader header = vcfHeaderDefinitionQueriesJDBC.getHeaderDefinition("INFO", "XYZ");
        verifyHeader(header, "XYZ", ".", "String", "the end of the alphabet");
    }

    public void testGetWrongType() {
        assertNull(vcfHeaderDefinitionQueriesJDBC.getHeaderDefinition("something", "ABC"));
    }

    public void testGetNoSuchId() {
        // IDs are case-sensitive so this will not be found
        assertNull(vcfHeaderDefinitionQueriesJDBC.getHeaderDefinition("INFO", "abc"));
    }
}
