/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.service;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.LatestArchive;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Maf;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Sdrf;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.LatestGenericReportDAO;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test Class for the latest archive,sdrf,maf reports service.
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@RunWith (JMock.class)
public class LatestGenericReportServiceFastTest {

    private Mockery context;

    private LatestGenericReportDAO dao;

    private LatestGenericReportServiceImpl service;

    private DatareportsServiceImpl commonService;

    @Before
    public void before() throws Exception {
        context = new JUnit4Mockery();
        dao = context.mock(LatestGenericReportDAO.class);
        service = new LatestGenericReportServiceImpl();
        commonService = new DatareportsServiceImpl();
        Field daoServiceField = service.getClass().getDeclaredField("daoImpl");
        Field commonServiceField = service.getClass().getDeclaredField("commonService");
        daoServiceField.setAccessible(true);
        commonServiceField.setAccessible(true);
        daoServiceField.set(service, dao);
        commonServiceField.set(service, commonService);
    }

    @Test
    public void testGetLatestSdrfWS() {
        context.checking(new Expectations() {{
            one(dao).getLatestSdrfWS();
            will(returnValue(makeMockSdrfRows()));
        }});
        List<Sdrf> sdrfList = service.getLatestSdrfWS();
        assertNotNull(sdrfList);
        assertEquals(3, sdrfList.size());
        assertEquals("Sdrf2", sdrfList.get(1).getRealName());
        assertEquals("Sdrf3 url", sdrfList.get(2).getSdrfUrl());
    }

    @Test
    public void testGetLatestArchiveWS() throws Exception {
        context.checking(new Expectations() {{
            one(dao).getLatestArchiveWS();
            will(returnValue(makeMockArchiveRows()));
        }});
        List<Archive> archiveList = service.getLatestArchiveWS();
        assertNotNull(archiveList);
        assertEquals(3, archiveList.size());
        assertEquals("Archive2", archiveList.get(1).getRealName());
        assertEquals("Archive3 url", archiveList.get(2).getDeployLocation());
    }

    @Test
    public void testGetLatestArchiveWSByType() throws Exception {
        context.checking(new Expectations() {{
            one(dao).getLatestArchiveWSByType("lol");
            will(returnValue(makeMockArchiveRows()));
        }});
        List<Archive> archiveList = service.getLatestArchiveWSByType("lol");
        assertNotNull(archiveList);
        assertEquals(3, archiveList.size());
        assertEquals("Archive2", archiveList.get(1).getRealName());
        assertEquals("Archive3 url", archiveList.get(2).getDeployLocation());
    }

    @Test
    public void testGetLatestMafWS() throws Exception {
        context.checking(new Expectations() {{
            one(dao).getLatestMafWS();
            will(returnValue(makeMockMafRows()));
        }});
        List<Maf> mafList = service.getLatestMafWS();
        assertNotNull(mafList);
        assertEquals(3, mafList.size());
        assertEquals("Maf2", mafList.get(1).getRealName());
        assertEquals("Maf3 url", mafList.get(2).getMafUrl());
    }

    @Test
    public void testGetLatestArchive() throws Exception {
        context.checking(new Expectations() {{
            one(dao).getLatestArchive();
            will(returnValue(makeMockLatestArchiveRows()));
        }});
        List<LatestArchive> latestArchiveList = service.getLatestArchive();
        assertNotNull(latestArchiveList);
        assertEquals(3, latestArchiveList.size());
        assertEquals("archive 2", latestArchiveList.get(1).getArchiveName());
        assertEquals("maf 3 url", latestArchiveList.get(2).getMafUrl());
        assertEquals("sdrf 1 url", latestArchiveList.get(0).getSdrfUrl());
        assertEquals("maf 3", latestArchiveList.get(2).getMafName());
        assertEquals(new Date(987654321), latestArchiveList.get(1).getDateAdded());
    }

    @Test
    public void testGetFilteredLatestArchiveList(){
        List<String> strList = new LinkedList<String>(){{add("mock type 1");}};
        List<LatestArchive> bList = service.getFilteredLatestArchiveList(makeMockLatestArchiveRows(),
                strList,null,null);
        assertNotNull(bList);
        assertEquals(1, bList.size());
        assertEquals("archive 1", bList.get(0).getArchiveName());
    }

    @Test
    public void testGetFilteredLatestArchiveListForMultipleArchiveType(){
        List<String> list = new LinkedList<String>(){{add("mock type 1");add("mock type 2");}};
        List<LatestArchive> bList = service.getFilteredLatestArchiveList(makeMockLatestArchiveRows(),
                list,null,null);
        assertNotNull(bList);
        assertEquals(2, bList.size());
        assertEquals("archive 1", bList.get(0).getArchiveName());
    }

    @Test
    public void testGetFilteredLatestArchiveListForNull(){
        List<LatestArchive> bList = service.getFilteredLatestArchiveList(makeMockLatestArchiveRows(),
                null,null,null);
        assertNotNull(bList);
        assertEquals(makeMockLatestArchiveRows().size(), bList.size());
    }

    @Test
    public void testGetFilteredLatestArchiveListForWrongDate(){
        List<LatestArchive> bList = service.getFilteredLatestArchiveList(makeMockLatestArchiveRows(),
                null,"not a date obviously",null);
        assertNotNull(bList);
        assertEquals(3, bList.size());
        assertEquals("archive 1", bList.get(0).getArchiveName());
    }

    @Test
    public void testGetFilteredLatestArchiveListForDateFrom(){
        final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        List<LatestArchive> bList = service.getFilteredLatestArchiveList(makeMockLatestArchiveRows(),
                null,dateFormat.format(new Date(987654321)),null);
        assertNotNull(bList);
        assertEquals(1, bList.size());
        assertEquals("archive 2", bList.get(0).getArchiveName());
    }

    @Test
    public void testGetFilteredLatestArchiveListForDateTo(){
        final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        List<LatestArchive> bList = service.getFilteredLatestArchiveList(makeMockLatestArchiveRows(),
                null,null,dateFormat.format(new Date(987654321)));
        assertNotNull(bList);
        assertEquals(2, bList.size());
        assertEquals("archive 3", bList.get(1).getArchiveName());
    }

    @Test
    public void testGetLatestArchiveFilterDistinctValues(){
        context.checking(new Expectations() {{
            one(dao).getLatestArchive();
            will(returnValue(makeMockLatestArchiveRows()));
        }});
        List<ExtJsFilter> bfList = service.getLatestArchiveFilterDistinctValues("archiveType");
        assertNotNull(bfList);
        assertEquals(3, bfList.size());
        assertEquals("mock type 1", bfList.get(0).getText());
        assertEquals("mock type 2", bfList.get(1).getText());
    }

    @Test
    public void testGetLatestArchiveComparator() throws Exception {
       Map<String, Comparator> map = service.getLatestArchiveComparator();
       assertNotNull(map);
    }

    public List<Sdrf> makeMockSdrfRows() {
        List<Sdrf> list = new LinkedList<Sdrf>();
        list.add(new Sdrf() {{
            setRealName("Sdrf1");
            setDateAdded(new Date(123456789));
            setSdrfUrl("Sdrf1 url");
        }});
        list.add(new Sdrf() {{
            setRealName("Sdrf2");
            setDateAdded(new Date(987654321));
            setSdrfUrl("Sdrf2 url");
        }});
        list.add(new Sdrf() {{
            setRealName("Sdrf3");
            setDateAdded(new Date(123454321));
            setSdrfUrl("Sdrf3 url");
        }});
        return list;
    }

    public List<Maf> makeMockMafRows() {
        List<Maf> list = new LinkedList<Maf>();
        list.add(new Maf() {{
            setRealName("Maf1");
            setDateAdded(new Date(123456789));
            setMafUrl("Maf1 url");
        }});
        list.add(new Maf() {{
            setRealName("Maf2");
            setDateAdded(new Date(987654321));
            setMafUrl("Maf2 url");
        }});
        list.add(new Maf() {{
            setRealName("Maf3");
            setDateAdded(new Date(123454321));
            setMafUrl("Maf3 url");
        }});
        return list;
    }

    public List<Archive> makeMockArchiveRows() {
        List<Archive> list = new LinkedList<Archive>();
        list.add(new Archive() {{
            setRealName("Archive1");
            setDateAdded(new Date(123456789));
            setDeployLocation("Archive1 url");
        }});
        list.add(new Archive() {{
            setRealName("Archive2");
            setDateAdded(new Date(987654321));
            setDeployLocation("Archive2 url");
        }});
        list.add(new Archive() {{
            setRealName("Archive3");
            setDateAdded(new Date(123454321));
            setDeployLocation("Archive3 url");
        }});
        return list;
    }

    public List<LatestArchive> makeMockLatestArchiveRows() {
        List<LatestArchive> list = new LinkedList<LatestArchive>();
        list.add(new LatestArchive() {{
            setArchiveName("archive 1");
            setDateAdded(new Date(123456789));
            setArchiveUrl("archive 1 url");
            setArchiveType("mock type 1");
            setSdrfName("sdrf 1");
            setSdrfUrl("sdrf 1 url");
            setMafName("maf 1");
            setMafUrl("maf 1 url");
        }});
        list.add(new LatestArchive() {{
            setArchiveName("archive 2");
            setDateAdded(new Date(987654321));
            setArchiveUrl("archive 2 url");
            setArchiveType("mock type 2");
            setSdrfName("sdrf 2");
            setSdrfUrl("sdrf 2 url");
            setMafName("maf 2");
            setMafUrl("maf 2 url");
        }});
        list.add(new LatestArchive() {{
            setArchiveName("archive 3");
            setDateAdded(new Date(123654789));
            setArchiveUrl("archive 3 url");
            setArchiveType("mock type 3");
            setSdrfName("sdrf 3");
            setSdrfUrl("sdrf 3 url");
            setMafName("maf 3");
            setMafUrl("maf 3 url");
        }});
        return list;
    }

}//End of Class
