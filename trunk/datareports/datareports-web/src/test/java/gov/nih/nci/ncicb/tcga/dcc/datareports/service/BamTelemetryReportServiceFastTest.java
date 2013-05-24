/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.service;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamTelemetry;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.BamTelemetryReportDAO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Test class for the bam telemetry service
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class BamTelemetryReportServiceFastTest {

    private Mockery context;

    private BamTelemetryReportDAO dao;

    private BamTelemetryReportServiceImpl service;

    private DatareportsServiceImpl commonService;

    @Before
    public void before() throws Exception {
        context = new JUnit4Mockery();
        dao = context.mock(BamTelemetryReportDAO.class);
        service = new BamTelemetryReportServiceImpl();
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
            one(dao).getBamTelemetryRows();
            will(returnValue(makeMockBamTelemetryRows()));
        }});
        List<BamTelemetry> bioList = service.getAllBamTelemetry();
        assertNotNull(bioList);
        assertEquals(3, bioList.size());
        assertEquals("OV", bioList.get(1).getDisease());
    }

    @Test
    public void testGetFilteredAliquotList() {
        List<String> gbmList = new LinkedList<String>() {{
            add("GBM");
        }};
        List<BamTelemetry> bList = service.getFilteredBamTelemetryList(makeMockBamTelemetryRows(),
                null, null, null, null, gbmList, null, null, null, null);
        assertNotNull(bList);
        assertEquals(2, bList.size());
        assertEquals("mockcenter1", bList.get(0).getCenter());
    }

    @Test
    public void testGetFilteredAliquotListForNull() {
        List<BamTelemetry> bList = service.getFilteredBamTelemetryList(makeMockBamTelemetryRows(),
                null, null, null, null, null, null, null, null, null);
        assertNotNull(bList);
        assertEquals(makeMockBamTelemetryRows().size(), bList.size());
    }

    @Test
    public void testGetFilteredBamTelemetryListForAliquot() {
        List<BamTelemetry> bList = service.getFilteredBamTelemetryList(makeMockBamTelemetryRows(),
                null, "mockaliq", null, null, null, null, null, null, null);
        assertNotNull(bList);
        assertEquals(3, bList.size());
        assertEquals("mockaliquot1", bList.get(0).getAliquotId());
    }

    @Test
    public void testGetBamTelemetryFilterDistinctValues() {
        context.checking(new Expectations() {{
            one(dao).getBamTelemetryRows();
            will(returnValue(makeMockBamTelemetryRows()));
        }});
        List<ExtJsFilter> bfList = service.getBamTelemetryFilterDistinctValues("disease");
        assertNotNull(bfList);
        assertEquals(2, bfList.size());
        assertEquals("GBM", bfList.get(0).getText());
        assertEquals("OV", bfList.get(1).getText());
    }

    @Test
    public void testGetBamTelemetryComparator() throws Exception {
        Map<String, Comparator> map = service.getBamTelemetryComparator();
        assertNotNull(map);
    }

    public List<BamTelemetry> makeMockBamTelemetryRows() {
        List<BamTelemetry> list = new LinkedList<BamTelemetry>();
        list.add(new BamTelemetry() {{
            setAliquotId("mockaliquot1");
            setAliquotUUID("mockuuid1");
            setDisease("GBM");
            setCenter("mockcenter1");
        }});
        list.add(new BamTelemetry() {{
            setAliquotId("mockaliquot2");
            setAliquotUUID("mockuuid2");
            setDisease("OV");
            setCenter("mockcenter2");
        }});
        list.add(new BamTelemetry() {{
            setAliquotId("mockaliquot3");
            setAliquotUUID("mockuuid3");
            setDisease("GBM");
            setCenter("mockcenter3");
        }});
        return list;
    }

}//En dof class
