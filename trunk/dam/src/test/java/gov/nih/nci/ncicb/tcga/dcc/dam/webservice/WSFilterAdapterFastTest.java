/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.webservice;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DataTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.HttpStatusCode;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.Disease;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DAMDiseaseQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DAMUtils;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.WebApplicationException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test class for the WSfilter Adapter
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class WSFilterAdapterFastTest {

    WSFilterAdapter wsFilterAdapter;
    private final Mockery context = new JUnit4Mockery();

    private DAMDiseaseQueries damDiseaseQueries;
    private PlatformQueries platformQueries;
    private CenterQueries centerQueries;
    private DataTypeQueries dataTypeQueries;
    private DAMUtils damUtils = DAMUtils.getInstance();

    private Disease mockDisease;
    private Disease mockDisease2;


    @Before
    public void setUp() throws Exception {
        damDiseaseQueries = context.mock(DAMDiseaseQueries.class);
        platformQueries = context.mock(PlatformQueries.class);
        centerQueries = context.mock(CenterQueries.class);
        dataTypeQueries = context.mock(DataTypeQueries.class);

        wsFilterAdapter = new WSFilterAdapter();


        mockDisease = new Disease("OV", "OV", true);
        mockDisease2 = new Disease("GBM2", "GBM2", false);

        Field damDiseaseQueriesField = DAMUtils.class.getDeclaredField("damDiseaseQueries");
        damDiseaseQueriesField.setAccessible(true);
        damDiseaseQueriesField.set(damUtils, damDiseaseQueries);
        Field dataTypeQueriesField = DAMUtils.class.getDeclaredField("dataTypeQueries");
        dataTypeQueriesField.setAccessible(true);
        dataTypeQueriesField.set(damUtils, dataTypeQueries);
        Field centerQueriesField = DAMUtils.class.getDeclaredField("centerQueries");
        centerQueriesField.setAccessible(true);
        centerQueriesField.set(damUtils, centerQueries);
        Field platformQueriesField = DAMUtils.class.getDeclaredField("platformQueries");
        platformQueriesField.setAccessible(true);
        platformQueriesField.set(damUtils, platformQueries);
    }


    @Test
    public void testValidateOK() throws Exception {
        context.checking(new Expectations() {{
            allowing(damDiseaseQueries).getDisease("OV");
            will(returnValue(mockDisease));
        }});
        wsFilterAdapter.setDiseaseType("OV");
    }

    @Test
    public void testSetDiseaseTypeBlank() throws Exception {

        testSetDiseaseTypeBlank(null);
        testSetDiseaseTypeBlank("");
    }

    private void testSetDiseaseTypeBlank(final String diseaseType) {

        final List<Disease> pretendDiseaseList = new LinkedList<Disease>();
        pretendDiseaseList.add(new Disease("Glioblastoma multiforme", "GBM", true));
        pretendDiseaseList.add(new Disease("Kidney renal clear cell carcinoma", "KIRC", true));

        context.checking(new Expectations() {{
            allowing(damDiseaseQueries).getActiveDiseases();
            will(returnValue(pretendDiseaseList));
        }});

        try {
            wsFilterAdapter.setDiseaseType(diseaseType);
            fail("Expected WebApplicationException to be raised");

        } catch (WebApplicationException expected) {

            assertEquals("Unexpected HTTP status code: ", HttpStatusCode.PRECONDITION_FAILED, expected.getResponse().getStatus());

            final String actualErrorMessage = expected.getResponse().getEntity().toString();
            assertTrue("Unexpected message: " + expected.getResponse().getEntity(),
                    actualErrorMessage.contains("Disease cannot be null."));
            assertTrue("Unexpected message: " + expected.getResponse().getEntity(),
                    actualErrorMessage.contains("Available diseases are:"));
            assertTrue("Unexpected message: " + expected.getResponse().getEntity(),
                    actualErrorMessage.contains("GBM - Glioblastoma multiforme"));
            assertTrue("Unexpected message: " + expected.getResponse().getEntity(),
                    actualErrorMessage.contains("KIRC - Kidney renal clear cell carcinoma"));
        }
    }

    @Test(expected = WebApplicationException.class)
    public void testValidateWrong() throws Exception {
        context.checking(new Expectations() {{
            allowing(damDiseaseQueries).getDisease("rhume");
            will(returnValue(null));
        }});
        wsFilterAdapter.setDiseaseType("rhume");
    }

    @Test(expected = WebApplicationException.class)
    public void testWrongCenter() throws Exception {
        context.checking(new Expectations() {{
            allowing(centerQueries).getAllCenters();
            will(returnValue(makeMockCenters()));
        }});
        wsFilterAdapter.setCenter("hum");
    }

    @Test(expected = WebApplicationException.class)
    public void testWrongPlatformType() throws Exception {
        context.checking(new Expectations() {{
            allowing(dataTypeQueries).getAllDataTypes();
            will(returnValue(makeMockDataTypes()));
        }});
        wsFilterAdapter.setPlatformType("hum");
    }

    @Test(expected = WebApplicationException.class)
    public void testWrongPlatform() throws Exception {
        context.checking(new Expectations() {{
            allowing(platformQueries).getAllPlatforms();
            will(returnValue(makeMockPlatforms()));
        }});
        wsFilterAdapter.setPlatform("hum");
    }

    @Test(expected = WebApplicationException.class)
    public void testValidateNotActive() throws Exception {
        context.checking(new Expectations() {{
            allowing(damDiseaseQueries).getDisease("GBM2");
            will(returnValue(mockDisease2));
        }});
        wsFilterAdapter.setDiseaseType("GBM2");
    }

    @Test
    public void testSetBatchNormal() throws Exception {
        wsFilterAdapter.setBatch("Batch 2");
        assertEquals(",Batch 2,", wsFilterAdapter.getBatch());
    }

    @Test
    public void testSetBatchWS() throws Exception {
        wsFilterAdapter.setBatch("3");
        assertEquals(",Batch 3,", wsFilterAdapter.getBatch());
    }

    @Test
    public void testSetBatchMultipleNormal() throws Exception {
        wsFilterAdapter.setBatch("Batch 2,Batch 5");
        assertEquals(",Batch 2,Batch 5,", wsFilterAdapter.getBatch());
    }

    @Test
    public void testSetBatchMultipleWS() throws Exception {
        wsFilterAdapter.setBatch("3,10");
        assertEquals(",Batch 3,Batch 10,", wsFilterAdapter.getBatch());
    }

    @Test
    public void testSetPlatformTypeNormal() throws Exception {
        wsFilterAdapter.setPlatformType("3");
        assertEquals(",3,", wsFilterAdapter.getPlatformType());
    }

    @Test
    public void testSetPlatformTypeWS1() throws Exception {
        context.checking(new Expectations() {{
            allowing(dataTypeQueries).getAllDataTypes();
            will(returnValue(makeMockDataTypes()));
        }});
        wsFilterAdapter.setPlatformType("Expression-Genes");
        assertEquals(",3,", wsFilterAdapter.getPlatformType());
    }

    @Test
    public void testSetPlatformTypeWS2() throws Exception {
        context.checking(new Expectations() {{
            allowing(dataTypeQueries).getAllDataTypes();
            will(returnValue(makeMockDataTypes()));
        }});
        wsFilterAdapter.setPlatformType("transcriptome");
        assertEquals(",3,", wsFilterAdapter.getPlatformType());
    }

    @Test
    public void testSetCenterNormal() throws Exception {

        wsFilterAdapter.setCenter("1");
        assertEquals(",1,", wsFilterAdapter.getCenter());
    }

    @Test
    public void testSetCenterWS1() throws Exception {

        final String platformAlias = "platformAlias";
        wsFilterAdapter.setPlatformAlias(platformAlias);

        final Platform platform = new Platform();
        platform.setPlatformAlias(platformAlias);
        platform.setCenterType("CGCC");

        context.checking(new Expectations() {{
            allowing(centerQueries).getAllCenters();
            will(returnValue(makeMockCenters()));
            allowing(platformQueries).getPlatformWithAlias(platformAlias);
            will(returnValue(platform));
        }});

        wsFilterAdapter.setCenter("LBL");
        assertEquals(",4,", wsFilterAdapter.getCenter());
    }

    @Test
    public void testSetCenterWSMultiple() throws Exception {

        final String platformAlias = "platformAlias";
        wsFilterAdapter.setPlatformAlias(platformAlias);

        final Platform platform = new Platform();
        platform.setPlatformAlias(platformAlias);
        platform.setCenterType("CGCC");

        context.checking(new Expectations() {{
            allowing(centerQueries).getAllCenters();
            will(returnValue(makeMockCenters()));
            allowing(platformQueries).getPlatformWithAlias(platformAlias);
            will(returnValue(platform));
        }});

        wsFilterAdapter.setCenter("LBL,broad.mit.edu");
        assertEquals(",4,1,", wsFilterAdapter.getCenter());
    }

    @Test
    public void testSetCenterPlatformSetCenterMatch() throws Exception {

        final String platformAlias = "platformAlias";
        wsFilterAdapter.setPlatformAlias(platformAlias);

        final Platform platform = new Platform();
        platform.setPlatformAlias(platformAlias);
        platform.setCenterType("CGCC");

        context.checking(new Expectations() {{
            allowing(centerQueries).getAllCenters();
            will(returnValue(makeMockCenters()));
            allowing(platformQueries).getPlatformWithAlias(platformAlias);
            will(returnValue(platform));
        }});

        wsFilterAdapter.setCenter("lbl.gov,broad.mit.edu");
        assertEquals(",4,1,", wsFilterAdapter.getCenter());
    }

    @Test
    public void testSetCenterPlatformSetNoCenterMatch() throws Exception {

        final String platformAlias = "platformAlias";
        wsFilterAdapter.setPlatformAlias(platformAlias);

        final Platform platform = new Platform();
        platform.setPlatformAlias(platformAlias);
        platform.setCenterType("CGCC");

        context.checking(new Expectations() {{
            allowing(centerQueries).getAllCenters();
            will(returnValue(makeMockCenters()));
            allowing(platformQueries).getPlatformWithAlias(platformAlias);
            will(returnValue(platform));
        }});

        try {
            wsFilterAdapter.setCenter("lbl.gov,No match expected");
            fail("Expected WebApplicationException to be raised");

        } catch (WebApplicationException expected) {

            assertEquals("Unexpected HTTP status code: ", HttpStatusCode.PRECONDITION_FAILED, expected.getResponse().getStatus());
            assertTrue("Unexpected message: " + expected.getResponse().getEntity(),
                    expected.getResponse().getEntity().toString().contains("Center 'No match expected' is unknown."));
        }
    }

    @Test
    public void testSetCenterPlatformSetNoPlatformMatch() throws Exception {

        final String platformAlias = "platformAlias";
        wsFilterAdapter.setPlatformAlias(platformAlias);

        final Platform platform = new Platform();
        platform.setPlatformAlias(platformAlias);
        platform.setCenterType("No match expected");

        context.checking(new Expectations() {{
            allowing(centerQueries).getAllCenters();
            will(returnValue(makeMockCenters()));
            allowing(platformQueries).getPlatformWithAlias(platformAlias);
            will(returnValue(platform));
        }});

        try {
            wsFilterAdapter.setCenter("lbl.gov,broad.mit.edu");
            fail("Expected WebApplicationException to be raised");

        } catch (WebApplicationException expected) {

            assertEquals("Unexpected HTTP status code: ", HttpStatusCode.PRECONDITION_FAILED, expected.getResponse().getStatus());
            assertTrue("Unexpected message: " + expected.getResponse().getEntity(),
                    expected.getResponse().getEntity().toString().contains("Center 'lbl.gov' does not support the platform 'platformAlias'."));
        }
    }

    @Test
    public void testSetCenterBlank() {

        testSetCenterBlank(null);
        testSetCenterBlank("");
    }

    private void testSetCenterBlank(final String center) {

        context.checking(new Expectations() {{
            allowing(centerQueries).getAllCenters();
            will(returnValue(makeMockCenters()));
        }});

        try {
            wsFilterAdapter.setCenter(center);
            fail("Expected WebApplicationException to be raised");

        } catch (WebApplicationException expected) {

            assertEquals("Unexpected HTTP status code: ", HttpStatusCode.PRECONDITION_FAILED, expected.getResponse().getStatus());

            final String actualErrorMessage = expected.getResponse().getEntity().toString();
            assertTrue("Unexpected message: " + expected.getResponse().getEntity(),
                    actualErrorMessage.contains("Center cannot be null."));
            assertTrue("Unexpected message: " + expected.getResponse().getEntity(),
                    actualErrorMessage.contains("Available centers are:"));
            assertTrue("Unexpected message: " + expected.getResponse().getEntity(),
                    actualErrorMessage.contains("LBL"));
            assertTrue("Unexpected message: " + expected.getResponse().getEntity(),
                    actualErrorMessage.contains("BI"));
        }
    }

    @Test
    public void testSetPlatformBlank() {

        testSetPlatformBlank(null);
        testSetPlatformBlank("");
    }

    private void testSetPlatformBlank(final String platform) {

        context.checking(new Expectations() {{
            allowing(platformQueries).getAllPlatforms();
            will(returnValue(makeMockPlatforms()));
        }});

        try {

            wsFilterAdapter.setPlatform(platform);
            fail("Expected WebApplicationException to be raised");

        } catch (final WebApplicationException expected) {

            assertEquals("Unexpected HTTP status code: ", HttpStatusCode.PRECONDITION_FAILED, expected.getResponse().getStatus());

            final String actualErrorMessage = expected.getResponse().getEntity().toString();
            assertTrue("Unexpected message: " + expected.getResponse().getEntity(),
                    actualErrorMessage.contains("Platform cannot be null."));
            assertTrue("Unexpected message: " + expected.getResponse().getEntity(),
                    actualErrorMessage.contains("Available platforms are (alias - name):"));
            assertTrue("Unexpected message: " + expected.getResponse().getEntity(),
                    actualErrorMessage.contains("Genome_Wide_SNP_6 - Genome_Wide_SNP_6"));
            assertTrue("Unexpected message: " + expected.getResponse().getEntity(),
                    actualErrorMessage.contains("ABI - ABI"));
        }
    }

    @Test
    public void testSetCenterPlatformNotSet() {

        try {
            wsFilterAdapter.setCenter("not blank and not an int");
            fail("Expected WebApplicationException to be raised");

        } catch (WebApplicationException expected) {

            assertEquals("Unexpected HTTP status code: ", HttpStatusCode.PRECONDITION_FAILED, expected.getResponse().getStatus());
            assertTrue("Unexpected message: " + expected.getResponse().getEntity(),
                    expected.getResponse().getEntity().toString().contains("The platform must be set."));
        }
    }

    @Test
    public void testSetPlatformNormal() throws Exception {

        final Integer platformId = 1;
        final String platformAlias = "Genome_Wide_SNP_6";

        final Platform pretendPlatform = new Platform();
        pretendPlatform.setPlatformId(platformId);
        pretendPlatform.setPlatformAlias(platformAlias);

        context.checking(new Expectations() {{
            one(platformQueries).getPlatformById(platformId);
            will(returnValue(pretendPlatform));
        }});
        wsFilterAdapter.setPlatform(platformId.toString());
        assertEquals("," + platformAlias + ",", wsFilterAdapter.getPlatform());
    }

    @Test
    public void testSetPlatformWS() throws Exception {
        context.checking(new Expectations() {{
            allowing(platformQueries).getAllPlatforms();
            will(returnValue(makeMockPlatforms()));
        }});
        wsFilterAdapter.setPlatform("ABI");
        assertEquals(",ABI,", wsFilterAdapter.getPlatform());
    }

    @Test
    public void testSetMultiplePlatformWSGood() throws Exception {
        context.checking(new Expectations() {{
            allowing(platformQueries).getAllPlatforms();
            will(returnValue(makeMockPlatforms()));
        }});
        wsFilterAdapter.setPlatform("ABI,Genome_Wide_SNP_6");
        assertEquals(",ABI,Genome_Wide_SNP_6,", wsFilterAdapter.getPlatform());
    }

    @Test
    public void testSetMultiplePlatformWSBad() throws Exception {
        context.checking(new Expectations() {{
            allowing(platformQueries).getAllPlatforms();
            will(returnValue(makeMockPlatforms()));
        }});
        try {
            wsFilterAdapter.setPlatform("ABI,Rubbish");
            fail("Expected WebApplicationException to be raised");
        } catch (WebApplicationException expected) {
            assertEquals("Unexpected HTTP status code: ", HttpStatusCode.PRECONDITION_FAILED,
                    expected.getResponse().getStatus());
            assertTrue("Unexpected message: " + expected.getResponse().getEntity(),
                    expected.getResponse().getEntity().toString().contains("Platform 'Rubbish' is unknown."));
        }
    }

    @Test
    public void testTrimFirstAndLastComma() throws Exception {
        assertEquals("ab,c", wsFilterAdapter.trimFirstAndLastComma("ab,c"));
        assertEquals("a,b,c", wsFilterAdapter.trimFirstAndLastComma(",a,b,c,"));
        assertEquals("", wsFilterAdapter.trimFirstAndLastComma(""));
        assertEquals(null, wsFilterAdapter.trimFirstAndLastComma(null));
    }

    private Collection<Map<String, Object>> makeMockDataTypes() {
        Collection<Map<String, Object>> rows = new LinkedList<Map<String, Object>>();
        rows.add(new HashMap() {{
            put("data_type_id", "3");
            put("name", "Expression-Genes");
            put("ftp_display", "transcriptome");
        }});
        rows.add(new HashMap() {{
            put("data_type_id", "1");
            put("name", "SNP");
            put("ftp_display", "snp");
        }});
        return rows;
    }

    private Collection<Map<String, Object>> makeMockCenters() {
        Collection<Map<String, Object>> rows = new LinkedList<Map<String, Object>>();
        rows.add(new HashMap() {{
            put("center_id", "4");
            put("short_name", "LBL");
            put("domain_name", "lbl.gov");
            put("center_type_code", "CGCC");
        }});
        rows.add(new HashMap() {{
            put("center_id", "1");
            put("short_name", "BI");
            put("domain_name", "broad.mit.edu");
            put("center_type_code", "CGCC");
        }});
        return rows;
    }

    private Collection<Map<String, Object>> makeMockPlatforms() {
        Collection<Map<String, Object>> rows = new LinkedList<Map<String, Object>>();
        rows.add(new HashMap() {{
            put("platform_id", "1");
            put("platform_name", "Genome_Wide_SNP_6");
            put("platform_alias", "Genome_Wide_SNP_6");
        }});
        rows.add(new HashMap() {{
            put("platform_id", "17");
            put("platform_name", "ABI");
            put("platform_alias", "ABI");
        }});
        return rows;
    }

}//End of Class
