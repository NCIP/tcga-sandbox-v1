/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.email;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.SampleSummary;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.SampleSummaryReportService;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * test class for the generation of email to center by the smaple summary report
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@RunWith (JMock.class)
public class CenterSampleReportGeneratorImplFastTest {

    private final Mockery context = new JUnit4Mockery();

    private SampleSummaryReportService service;

    private CenterSampleReportGeneratorImpl genEmail;

    @Before
    public void before() throws Exception {

        service = context.mock(SampleSummaryReportService.class);
        genEmail = new CenterSampleReportGeneratorImpl();

        //We use reflection to access the private field
        Field serviceControllerField = genEmail.getClass().getDeclaredField("ssService");
        serviceControllerField.setAccessible(true);
        serviceControllerField.set(genEmail, service);
    }

    @Test
    public void testGenerateHTMLForcenter() throws Exception {
        final List<SampleSummary> mockSamples = makeMockSampleSummary();
        context.checking(new Expectations() {{
            allowing(service).getFilteredSampleSummaryReport("mockCenter");
            will(returnValue(mockSamples));
        }});
        String res = genEmail.generateHTMLFor("mockCenter");
        assertNotNull(res);
        assertTrue(res.contains("Y*"));
        assertTrue(res.contains("GBM"));
        assertTrue(res.contains("www.rpi.edu"));
        assertTrue(res.contains("Super Nintendo"));
        assertTrue(res.contains("www.rpi.edu"));
    }

    @Test
    public void testGetServerUrlFull() throws Exception {
        Method m = genEmail.getClass().getDeclaredMethod("getServerUrlFull");
        m.setAccessible(true);
        String res = m.invoke(genEmail).toString();
        assertNotNull(res);
        assertEquals("null/datareports/sampleSummaryReport.htm", res);
    }

    @Test
    public void testGenSampleUrl() throws Exception {
        final SampleSummary sample = makeMockSampleSummary().get(0);
        Method m = genEmail.getClass().getDeclaredMethod("genSampleUrl",
                SampleSummary.class,String.class,String.class,Boolean.TYPE);
        m.setAccessible(true);
        String res = m.invoke(genEmail, sample,"mockColId","mockCenter",true).toString();
        assertNotNull(res);
        assertEquals("null/datareports/sampleSummaryReport.htm?centerEmail=mockCenter&" +
                "disease=GBM&center=www.rpi.edu (DOPE)&portionAnalyte=D&" +
                "platform=Super Nintendo&bcr=true&cols=mockColId", res);
    }

    @Test
    public void testGenEmailHeader() throws Exception {
        final List<SampleSummary> mockSamples = makeMockSampleSummary();
        final StringBuilder sb = new StringBuilder();
        Method m = genEmail.getClass().getDeclaredMethod("genEmailHeader", StringBuilder.class,List.class);
        m.setAccessible(true);
        m.invoke(genEmail, sb,mockSamples);
        assertTrue(sb.toString().contains(
                "<table style='border:1px solid #66a3d3;width:950px;margin:1em auto;border-collapse:collapse;" +
                "font-size:0.9em;'><tr><th style='background:#B8DBFF;text-align:center;border:1px solid #66a3d3;" +
                "font-weight:bold;color:#000099;padding:5px;'>Disease</th><th style='background:#B8DBFF;" +
                "text-align:center;border:1px solid #66a3d3;font-weight:bold;color:#000099;padding:5px;'>" +
                "Center</th><th style='background:#B8DBFF;text-align:center;border:1px solid #66a3d3;" +
                "font-weight:bold;color:#000099;padding:5px;'>Portion Analyte</th><th style='background:#B8DBFF;" +
                "text-align:center;border:1px solid #66a3d3;font-weight:bold;color:#000099;padding:5px;'>" +
                "Platform</th><th style='background:#B8DBFF;text-align:center;border:1px solid #66a3d3;" +
                "font-weight:bold;color:#000099;padding:5px;'>Sample IDs BCR Reported Sending to Center</th>" +
                "<th style='background:#B8DBFF;text-align:center;border:1px solid #66a3d3;font-weight:bold;" +
                "color:#000099;padding:5px;'>Sample IDs DCC Received from Center</th><th style='background:#B8DBFF;" +
                "text-align:center;border:1px solid #66a3d3;font-weight:bold;color:#000099;padding:5px;'>" +
                "Unaccounted for BCR Sample IDs that Center Reported</th><th style='background:#B8DBFF;" +
                "text-align:center;border:1px solid #66a3d3;font-weight:bold;color:#000099;padding:5px;'>" +
                "Unaccounted for Center Sample IDs that BCR Reported</th><th style='background:#B8DBFF;" +
                "text-align:center;border:1px solid #66a3d3;font-weight:bold;color:#000099;padding:5px;'>" +
                "Sample IDs with Level 1 Data</th><th style='background:#B8DBFF;text-align:center;" +
                "border:1px solid #66a3d3;font-weight:bold;color:#000099;padding:5px;'>Sample IDs with Level 2 Data" +
                "</th><th style='background:#B8DBFF;text-align:center;border:1px solid #66a3d3;font-weight:bold;" +
                "color:#000099;padding:5px;'>Sample IDs with Level 3 Data</th><th style='background:#B8DBFF;" +
                "text-align:center;border:1px solid #66a3d3;font-weight:bold;color:#000099;padding:5px;'>" +
                "Level 4 Submitted (Y/N)</th>"));
    }

    @Test
    public void testGenEmailFooter() throws Exception {
        final StringBuilder sb = new StringBuilder();
        Class[] cParam = new Class[2];
        cParam[0] = String.class;
        cParam[1] = StringBuilder.class;
        Method m = genEmail.getClass().getDeclaredMethod("genEmailFooter", cParam);
        m.setAccessible(true);
        Object[] oParam = new Object[2];
        oParam[0] = "mockCenter";
        oParam[1] = sb;
        m.invoke(genEmail, oParam);
        assertEquals("</table></div><br /><div align='left'><span>A value of <quote>Undetermined</quote> " +
                "for Platform indicates that the DCC has not received data for the indicated sample-analytes" +
                "<br/><span style='color: red;'>* = Although not in the latest current archive, level 4 data has" +
                " been submitted</span><br/></span><br /></div><div align='left'><span>The most up-to-date " +
                "information is always available as a dynamic web report.&nbsp;&nbsp;" +
                "<a href='null/datareports/sampleSummaryReport.htm?centerEmail=mockCenter'>" +
                "Use this link to access live data.</a>&nbsp;&nbsp;If you have any questions or comments about " +
                "this email or the reports listed here, please contact the DCC via the contact listed below.</span>" +
                "</div><br /><div align='left'><span>null<br />null<br />null</span></div>" +
                "<br /><div align='left' style='font-size:0.8em'>Disclaimer: The above table(s) reflect as " +
                "accurately as possible the sample IDs and ID annotations submitted to the DCC to date. " +
                "At the present time, the intended platform for a given aliquot can only be inferred from " +
                "the disease and the identity of the GSC/CGCC that is encoded in the aliquot ID. If a given " +
                "GSC/CGCC is using only one platform for a given disease, then this inference will be accurate. " +
                "If the GSC/CGCC is using more than one platform for a disease, the DCC cannot accurately report " +
                "ID counts per platform per disease. Of course, once a GSC/CGCC submits molecular data for an " +
                "aliquot to the DCC, then the platform is known. In Phase 2 of the TCGA project there will be a " +
                "standard operating procedure for a GSC/CGCC to report the intended platform for a given aliquot " +
                "before submitting the molecular data.</div><br /></body></html>", sb.toString());
    }

    @Test
    public void testGenServerUrl() throws Exception {
        Method m = genEmail.getClass().getDeclaredMethod("genServerUrl", String.class);
        m.setAccessible(true);
        String res = m.invoke(genEmail, "mockCenter").toString();
        assertNotNull(res);
        assertEquals("null/datareports/sampleSummaryReport.htm?centerEmail=mockCenter", res);

    }

    @Test
    public void testGenSignature() throws Exception {
        Method m = genEmail.getClass().getDeclaredMethod("genSignature");
        m.setAccessible(true);
        String res = m.invoke(genEmail).toString();
        assertNotNull(res);
        assertEquals("null<br />null<br />null", res);
    }

    public List<SampleSummary> makeMockSampleSummary() {
        List<SampleSummary> list = new ArrayList<SampleSummary>();
        SampleSummary mockSummary = new SampleSummary();
        mockSummary.setDisease("GBM");
        mockSummary.setCenterType("DOPE");
        mockSummary.setCenterName("www.rpi.edu");
        mockSummary.setPortionAnalyte("D");
        mockSummary.setPlatform("Super Nintendo");
        mockSummary.setTotalBCRSent(101L);
        mockSummary.setTotalCenterSent(202L);
        mockSummary.setTotalBCRUnaccountedFor(101L);
        mockSummary.setTotalCenterUnaccountedFor(0L);
        mockSummary.setTotalLevelOne(42L);
        mockSummary.setTotalLevelTwo(42L);
        mockSummary.setTotalLevelThree(42L);
        mockSummary.setLevelFourSubmitted("Y*");
        mockSummary.setLastRefresh(new Timestamp(new Date().getTime()));
        list.add(mockSummary);

        return list;
    }

} //End of Class
