/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.CenterQueriesJDBCImpl;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test class for CenterQueriesJDBCImplTest
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */
public class CenterQueriesJDBCImplSlowTest extends DBUnitTestCase {

    private final static String tcgaTestPropertiesFile = "unittest.properties";
    private static final String SAMPLE_FOLDER = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String SAMPLE_FILE = "dao/Center_TestDB.xml";
    private CenterQueriesJDBCImpl centerQueries;

    public CenterQueriesJDBCImplSlowTest() {
        super(SAMPLE_FOLDER, SAMPLE_FILE, tcgaTestPropertiesFile);
    }

    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.CLEAN_INSERT;
    }

    @Override    
    protected DatabaseOperation getTearDownOperation() {
        return DatabaseOperation.DELETE_ALL;
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        centerQueries = new CenterQueriesJDBCImpl();
        centerQueries.setDataSource(getDataSource());
    }

    @Override
    protected void setUpDatabaseConfig(final DatabaseConfig config) {
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new OracleDataTypeFactory());
    }

    @Test
    public void testFindCenterId() {
        Integer centerId = centerQueries.findCenterId("broad.mit.edu", "CGCC");
        assertNotNull(centerId);
        assertEquals(1, centerId.intValue());
    }

    @Test
    public void testFindCenterForBadCenterName() {
        Integer centerId = centerQueries.findCenterId("badcenter", "badcentertypecode");
        assertNull(centerId);
    }

    @Test
    public void testFindCenterByIdForNullCenterType() {
        Integer centerId = centerQueries.findCenterId("broad.mit.edu", null);
        assertNull(centerId);
    }
    
    @Test
    public void testGetCenterById() {
        Center center = centerQueries.getCenterById(1);
        assertNotNull(center);
        assertSame(1, center.getCenterId());
        assertEquals("broad.mit.edu", center.getCenterName());
        assertNotNull(center.getEmailList());
        assertEquals("BI", center.getShortName());
    }

    @Test
    public void testGetCenterForBadCenterID() {
        Center center = centerQueries.getCenterById(314567857);
        assertNull(center);
    }

    @Test
    public void testGetCenterEMail() {
        Center center = centerQueries.getCenterById(1);
        assertNotNull(center);
        assertSame(1, center.getCenterId());
        assertNotNull(center.getEmailList());
        List<String> emailList = center.getEmailList();
        assertTrue(emailList.contains("test1@test.com"));
        assertTrue(emailList.contains("test2@test.com"));
        String emailStr = center.getCommaSeparatedEmailList();
        assertNotNull(emailStr);
        assertEquals("test1@test.com,test2@test.com", emailStr);
    }

    @Test    
    public void testNoEMailForCenter() {
        Center center = centerQueries.getCenterById(2);
        assertNotNull(center);
        assertSame(2, center.getCenterId());
        assertNull(center.getEmailList());
        assertNull(center.getCommaSeparatedEmailList());
    }

    @Test    
    public void testGetCenterByName() {
        String centerName = "broad.mit.edu";
        String centerTypeCode = "CGCC";
        Center center = centerQueries.getCenterByName(centerName, centerTypeCode);
        assertNotNull(center);
        assertSame(1, center.getCenterId());
        assertEquals(centerName, center.getCenterName());
        assertEquals("BI", center.getShortName());
    }

    @Test    
    public void testGetCenterForIncorrectName() {
        String centerName = "wrongcentername";
        String centerTypeCode = "CGCC";
        Center center = centerQueries.getCenterByName(centerName, centerTypeCode);
        assertNull(center);
    }    

    @Test    
    public void testGetCenterList() {
        List<Center> centers = centerQueries.getCenterList();
        assertNotNull(centers);
        assertSame(4, centers.size());
        List<String> centerNames = new ArrayList<String>();
        List<String> shortNames = new ArrayList<String>();
        for (final Center center : centers) {
            centerNames.add(center.getCenterName());
            shortNames.add(center.getShortName());
        }
        String[] testCenters = {"broad.mit.edu", "jhu-usc.edu", "hms.harvard.edu", "lbl.gov"};
        assertTrue(centerNames.containsAll(Arrays.asList(testCenters)));

        final String[] expectedShortNames = { "BI", "JHU_USC", "HMS", "LBL"};
        assertTrue(shortNames.containsAll(Arrays.asList(expectedShortNames)));
    }

    @Test
    public void testGetRealCenterList() {
        List<Center> centers = centerQueries.getRealCenterList();
        assertNotNull(centers);
        assertSame(1, centers.size());
        List<String> centerNames = new ArrayList<String>();
        List<String> shortNames = new ArrayList<String>();
        List<String> bcrCenterIds = new ArrayList<String>();
        for (final Center center : centers) {
            centerNames.add(center.getCenterName());
            shortNames.add(center.getShortName());
            bcrCenterIds.add(center.getBcrCenterId());
        }
        final String[] testCenters = {"broad.mit.edu"};
        assertTrue(centerNames.containsAll(Arrays.asList(testCenters)));

        final String[] expectedShortNames = {"BI"};
        assertTrue(shortNames.containsAll(Arrays.asList(expectedShortNames)));

        final String []expectedBcrCenterIds = {"01"};
        assertTrue(bcrCenterIds.containsAll(Arrays.asList(expectedBcrCenterIds)));
    }
       
    public void testGetCenterIdForBCRCenter() {
         Integer centerId = centerQueries.getCenterIdForBCRCenter("01");
         assertNotNull(centerId);
         assertEquals(1, centerId.intValue());
     }


    public void testGetCenterIdForInvalidBCRCenter() {
         Integer centerId = null;
         centerId = centerQueries.getCenterIdForBCRCenter("Invalid");
         assertNull(centerId);
    }
    
    public void testGetConvertedCenters(){    	
    	List <Center> convertedCenterList = centerQueries.getConvertedToUUIDCenters();    	
    	assertTrue( convertedCenterList.size() == 2);
    	assertEquals("BI",convertedCenterList.get(0).getShortName());
    	assertEquals("JHU_USC",convertedCenterList.get(1).getShortName());    	    	
    }
    
    public void testIsCenterConverted(){
    	Center testCenter = new Center();    
    	testCenter.setCenterDisplayName("Broad Institute of MIT and Harvard");
    	testCenter.setCenterId(1);
    	testCenter.setCenterName("broad.mit.edu");
    	testCenter.setCenterType("CGCC");    	
    	testCenter.setShortName("BI");
    	
    	assertTrue(centerQueries.isCenterCenvertedToUUID(testCenter));
    }
    
    public void testIsNonConvertedCenterConverted(){
    	Center testCenter = new Center();    
    	testCenter.setCenterDisplayName("Lawrence Berkeley National Laboratory");
    	testCenter.setCenterId(4);
    	testCenter.setCenterName("lbl.gov");
    	testCenter.setCenterType("CGCC");    	
    	testCenter.setShortName("LBL");
    	
    	assertFalse(centerQueries.isCenterCenvertedToUUID(testCenter));
    }

    public void testCenterConvertedToUUID(){
        assertTrue(centerQueries.isCenterConvertedToUUID("broad.mit.edu","CGCC"));
    }

    public void testCenterNotConvertedToUUID(){
        assertFalse(centerQueries.isCenterConvertedToUUID("lbl.gov","CGCC"));
    }

    public void testCenterRequiresMageTab() {
        assertTrue(centerQueries.doesCenterRequireMageTab("broad.mit.edu", "CGCC"));
        assertFalse(centerQueries.doesCenterRequireMageTab("lbl.gov", "CGCC"));
    }

    public void testRequiresMageTabUnknownCenter() {
        assertFalse(centerQueries.doesCenterRequireMageTab("hello", "world"));
    }

}
