/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.service;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.PendingUUID;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.PendingUUIDDAO;
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
 * pending uuid report service test
 *
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class PendingUUIDReportServiceFastTest {

    private Mockery context;

    private PendingUUIDDAO dao;

    private PendingUUIDReportServiceImpl service;

    private DatareportsServiceImpl commonService;

    @Before
    public void before() throws Exception {
        context = new JUnit4Mockery();
        dao = context.mock(PendingUUIDDAO.class);
        service = new PendingUUIDReportServiceImpl();
        commonService = new DatareportsServiceImpl();
        Field daoServiceField = service.getClass().getDeclaredField("daoImpl");
        Field commonServiceField = service.getClass().getDeclaredField("commonService");
        daoServiceField.setAccessible(true);
        commonServiceField.setAccessible(true);
        daoServiceField.set(service, dao);
        commonServiceField.set(service, commonService);
    }

    @Test
    public void testGetAllPendingUUID() {
        context.checking(new Expectations() {{
            one(dao).getAllPendingUUIDs();
            will(returnValue(makeMockPendingUUID()));
        }});
        List<PendingUUID> pendingUUIDList = service.getAllPendingUUIDs();
        assertNotNull(pendingUUIDList);
        assertEquals(3, pendingUUIDList.size());
        assertEquals("IGC", pendingUUIDList.get(1).getBcr());
        assertEquals("broad.mit.edu (GSC)", pendingUUIDList.get(1).getCenter());
    }

    @Test
    public void testGetFilteredPendingUUIDList() {
        List<String> bcrList = new LinkedList<String>() {{
            add("IGC");
        }};
        List<PendingUUID> bList = service.getFilteredPendingUUIDList(makeMockPendingUUID(), bcrList,
                null, null, null);
        assertNotNull(bList);
        assertEquals(2, bList.size());
        assertEquals("uuid1", bList.get(0).getUuid());
    }

    @Test
    public void testGetFilteredPendingUUIDListForNull() {
        List<PendingUUID> bList = service.getFilteredPendingUUIDList(makeMockPendingUUID(),
                null, null, null, null);
        assertNotNull(bList);
        assertEquals(makeMockPendingUUID().size(), bList.size());
    }

    @Test
    public void testGetPendingUUIDFilterDistinctValues() {
        context.checking(new Expectations() {{
            one(dao).getAllPendingUUIDs();
            will(returnValue(makeMockPendingUUID()));
        }});
        List<ExtJsFilter> bfList = service.getPendingUUIDFilterDistinctValues("bcr");
        assertNotNull(bfList);
        assertEquals(2, bfList.size());
        assertEquals("IGC", bfList.get(0).getText());
        assertEquals("NCH", bfList.get(1).getText());
    }

    @Test
    public void testGetPendingUUIDComparator() throws Exception {
        Map<String, Comparator> map = service.getPendingUUIDComparator();
        assertNotNull(map);
    }

    public List<PendingUUID> makeMockPendingUUID() {
        List<PendingUUID> list = new LinkedList<PendingUUID>();
        list.add(new PendingUUID() {{
            setBcr("IGC");
            setBcrAliquotBarcode("barcode1");
            setUuid("uuid1");
            setCenter("broad.mit.edu (GSC)");
        }});
        list.add(new PendingUUID() {{
            setBcr("IGC");
            setBcrAliquotBarcode("barcode2");
            setUuid("uuid2");
            setCenter("broad.mit.edu (GSC)");
        }});
        list.add(new PendingUUID() {{
            setBcr("NCH");
            setBcrAliquotBarcode("barcode3");
            setUuid("uuid3");
            setCenter("broad.mit.edu (GDAC)");
        }});
        return list;
    }
}
