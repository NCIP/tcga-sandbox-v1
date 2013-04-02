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
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Aliquot;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.AliquotArchive;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.AliquotReportDAO;

import java.lang.reflect.Field;
import java.util.Comparator;
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
 * This class test for the service methods layer of the biospecimen report
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */


@RunWith (JMock.class)
public class AliquotReportServiceFastTest {

    private Mockery context;

    private AliquotReportDAO dao;

    private AliquotReportServiceImpl service;

    private DatareportsServiceImpl commonService;

    @Before
    public void before() throws Exception {
        context = new JUnit4Mockery();
        dao = context.mock(AliquotReportDAO.class);
        service = new AliquotReportServiceImpl();
        commonService = new DatareportsServiceImpl();
        Field daoServiceField = service.getClass().getDeclaredField("daoImpl");
        Field commonServiceField = service.getClass().getDeclaredField("commonService");
        daoServiceField.setAccessible(true);
        commonServiceField.setAccessible(true);
        daoServiceField.set(service, dao);
        commonServiceField.set(service, commonService);
    }

    @Test
    public void testGetAllAliquot() {
        context.checking(new Expectations() {{
            one(dao).getAliquotRows();
            will(returnValue(makeMockAliquotRows()));
        }});
        List<Aliquot> bioList = service.getAllAliquot();
        assertNotNull(bioList);
        assertEquals(3, bioList.size());
        assertEquals("OV", bioList.get(1).getDisease());
    }

    @Test
    public void testGetAllAliquotArchive(){
        context.checking(new Expectations() {{
            one(dao).getAliquotArchive("mockaliquot1",2);
            will(returnValue(makeMockAliquotArchives()));
        }});
        List<AliquotArchive> archiveList = service.getAllAliquotArchive("mockaliquot1",2);
        assertNotNull(archiveList);
        assertEquals(2, archiveList.size());
        assertEquals("mockFileName2", archiveList.get(1).getFileName());
    }

    @Test
    public void testGetFilteredAliquotList(){
        List<String> gbmList = new LinkedList<String>(){{add("GBM");}};
        List<Aliquot> bList = service.getFilteredAliquotList(makeMockAliquotRows(),
                null,gbmList,null,null,null,null,null,null);
        assertNotNull(bList);
        assertEquals(2, bList.size());
        assertEquals("1", bList.get(0).getBcrBatch());
    }

    @Test
    public void testGetFilteredAliquotListForMultipleLevel(){
        List<String> list = new LinkedList<String>(){{add("mockSubmitted");add("mockMissing");}};
        List<Aliquot> bList = service.getFilteredAliquotList(makeMockAliquotRows(),
                null,null,null,null,null,null,null,list);
        assertNotNull(bList);
        assertEquals(3, bList.size());
        assertEquals("1", bList.get(0).getBcrBatch());
    }

    @Test
    public void testGetFilteredAliquotListForSingleLevel(){
        List<String> list = new LinkedList<String>(){{add("mockSubmitted");}};
        List<Aliquot> bList = service.getFilteredAliquotList(makeMockAliquotRows(),
                null,null,null,null,null,null,null,list);
        assertNotNull(bList);
        assertEquals(1, bList.size());
        assertEquals("1", bList.get(0).getBcrBatch());
    }

    @Test
    public void testGetFilteredAliquotListForNull(){
        List<Aliquot> bList = service.getFilteredAliquotList(makeMockAliquotRows(),
                null,null,null,null,null,null,null,null);
        assertNotNull(bList);
        assertEquals(makeMockAliquotRows().size(), bList.size());
    }

    @Test
    public void testGetFilteredAliquotListForBcrBatch(){
        List<Aliquot> bList = service.getFilteredAliquotList(makeMockAliquotRows(),
                null,null,null,null,"12",null,null,null);
        assertNotNull(bList);
        assertEquals(1, bList.size());
        assertEquals("12", bList.get(0).getBcrBatch());
    }

    @Test
    public void testGetFilteredAliquotListForAliquot(){
        List<Aliquot> bList = service.getFilteredAliquotList(makeMockAliquotRows(),
                "mockaliq",null,null,null,null,null,null,null);
        assertNotNull(bList);
        assertEquals(3, bList.size());
        assertEquals("mockaliquot1", bList.get(0).getAliquotId());
    }

    @Test
    public void testGetAliquotFilterDistinctValues(){
        context.checking(new Expectations() {{
            one(dao).getAliquotRows();
            will(returnValue(makeMockAliquotRows()));
        }});
        List<ExtJsFilter> bfList = service.getAliquotFilterDistinctValues("disease");
        assertNotNull(bfList);
        assertEquals(2, bfList.size());
        assertEquals("GBM", bfList.get(0).getText());
        assertEquals("OV", bfList.get(1).getText());
    }

    @Test
    public void testGetAliquotComparator() throws Exception {
       Map<String, Comparator> map = service.getAliquotComparator();
       assertNotNull(map);
    }

    public List<AliquotArchive> makeMockAliquotArchives() {
        List<AliquotArchive> list = new LinkedList<AliquotArchive>();
        list.add(new AliquotArchive() {{
            setArchiveId(1);
            setArchiveName("mockArchive1");
            setFileId(1);
            setFileName("mockFileName1");
            setFileUrl("mockFileUrl1");
        }});
        list.add(new AliquotArchive() {{
            setArchiveId(2);
            setArchiveName("mockArchive2");
            setFileId(2);
            setFileName("mockFileName2");
            setFileUrl("mockFileUrl2");
        }});
        return list;
    }

    public List<Aliquot> makeMockAliquotRows() {
        List<Aliquot> list = new LinkedList<Aliquot>();
        list.add(new Aliquot() {{
            setAliquotId("mockaliquot1");
            setBcrBatch("1");
            setDisease("GBM");
            setLevelOne("mockSubmitted");
            setLevelTwo("mockSubmitted");
            setLevelThree("mockSubmitted");
            setPlatform("mockplatform1");
            setCenter("mockcenter1");
        }});
        list.add(new Aliquot() {{
            setAliquotId("mockaliquot2");
            setBcrBatch("12");
            setDisease("OV");
            setLevelOne("mockMissing");
            setLevelTwo("mockMissing");
            setLevelThree("mockMissing");
            setPlatform("mockplatform2");
            setCenter("mockcenter2");
        }});
        list.add(new Aliquot() {{
            setAliquotId("mockaliquot3");
            setBcrBatch("23");
            setDisease("GBM");
            setLevelOne("mockSubmitted");
            setLevelTwo("mockSubmitted");
            setLevelThree("mockMissing");
            setPlatform("mockplatform3");
            setCenter("mockcenter3");
        }});
        return list;
    }

}//End of class
