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
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.AliquotIdBreakdown;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.AliquotIdBreakdownReportDAO;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.Predicate;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for the aliquot Id breakdown report service
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
@RunWith (JMock.class)
public class AliquotIdBreakdownReportServiceFastTest {

    private Mockery context;

    private AliquotIdBreakdownReportDAO dao;

    private AliquotIdBreakdownReportServiceImpl service;

    private DatareportsServiceImpl commonService;

    @Before
    public void before() throws Exception {
        context = new JUnit4Mockery();
        dao = context.mock(AliquotIdBreakdownReportDAO.class);
        service = new AliquotIdBreakdownReportServiceImpl();
        commonService = new DatareportsServiceImpl();
        Field daoServiceField = service.getClass().getDeclaredField("daoImpl");
        Field commonServiceField = service.getClass().getDeclaredField("commonService");
        daoServiceField.setAccessible(true);
        commonServiceField.setAccessible(true);
        daoServiceField.set(service, dao);
        commonServiceField.set(service, commonService);
    }

   @Test
    public void testGetAliquotIdBreakdown() {
        context.checking(new Expectations() {{
            one(dao).getAliquotIdBreakdown();
            will(returnValue(makeMockAliquotIdBreakdown()));
        }});
        List<AliquotIdBreakdown> bioList = service.getAliquotIdBreakdown();
        assertNotNull(bioList);
        assertEquals(3, bioList.size());
        assertEquals("mockaliquot2", bioList.get(1).getAliquotId());
    }

    @Test
    public void testGetAliquotBreakdownComparator() throws Exception {
       Map<String, Comparator> map = service.getAliquotIdBreakdownComparator();
       assertNotNull(map);
    }

    @Test
    public void testGetFilteredAliquotIdBreakdownList(){
        List<AliquotIdBreakdown> bList = service.
                getFilteredAliquotIdBreakdownList(makeMockAliquotIdBreakdown(),
                null,null,null,null);
        assertNotNull(bList);
        assertEquals(3, bList.size());
        assertEquals("mockaliquot1", bList.get(0).getAliquotId());
    }

    @Test
    public void testGetFilteredAliquotIdBreakdownList4AliquotId(){
        List<AliquotIdBreakdown> bList = service.
                getFilteredAliquotIdBreakdownList(makeMockAliquotIdBreakdown(),
                "mockaliquot",null,null,null);
        assertNotNull(bList);
        assertEquals(3, bList.size());
        assertEquals("mockaliquot1", bList.get(0).getAliquotId());
    }

    @Test
    public void testGetFilteredAliquotIdBreakdownList4AnalyteId(){
        List<AliquotIdBreakdown> bList = service.
                getFilteredAliquotIdBreakdownList(makeMockAliquotIdBreakdown(),
                null,"mockanalyte",null,null);
        assertNotNull(bList);
        assertEquals(3, bList.size());
        assertEquals("mockaliquot1", bList.get(0).getAliquotId());
    }

    @Test
    public void testGetFilteredAliquotIdBreakdownList4SampleId(){
        List<AliquotIdBreakdown> bList = service.
                getFilteredAliquotIdBreakdownList(makeMockAliquotIdBreakdown(),
                null,null,"mock",null);
        assertNotNull(bList);
        assertEquals(3, bList.size());
        assertEquals("mockaliquot1", bList.get(0).getAliquotId());
    }

    @Test
    public void testGetFilteredAliquotIdBreakdownList4ParticipantId(){
        List<AliquotIdBreakdown> bList = service.
                getFilteredAliquotIdBreakdownList(makeMockAliquotIdBreakdown(),
                null,null,null,"mockparticipant3");
        assertNotNull(bList);
        assertEquals(1, bList.size());
        assertEquals("mockaliquot3", bList.get(0).getAliquotId());
    }

    @Test
    public void testProcessAliquotIdBreakdownPredicatesTrue() throws Exception {
        Method m = service.getClass().getDeclaredMethod("processAliquotIdBreakdownPredicates",
                String.class,String.class,String.class);
        m.setAccessible(true);
        Predicate p = (Predicate)m.invoke(service,"aliquotId","mock","blah");
        assertNotNull(p);
        assertTrue(p.evaluate(makeMockAliquotIdBreakdown().get(0)));
    }

    @Test
    public void testProcessAliquotIdBreakdownPredicatesFalse() throws Exception {
        Method m = service.getClass().getDeclaredMethod("processAliquotIdBreakdownPredicates",
                String.class,String.class,String.class);
        m.setAccessible(true);
        Predicate p = (Predicate)m.invoke(service,"aliquotId","hum","blah");
        assertNotNull(p);
        assertFalse(p.evaluate(makeMockAliquotIdBreakdown().get(0)));
    }

    public List<AliquotIdBreakdown> makeMockAliquotIdBreakdown() {
        List<AliquotIdBreakdown> list = new LinkedList<AliquotIdBreakdown>();
        list.add(new AliquotIdBreakdown() {{
            setAliquotId("mockaliquot1");
            setAnalyteId("mockanalyte1");
            setSampleId("mocksample1");
            setParticipantId("mockparticipant1");
        }});
        list.add(new AliquotIdBreakdown() {{
            setAliquotId("mockaliquot2");
            setAnalyteId("mockanalyte2");
            setSampleId("mocksample2");
            setParticipantId("mockparticipant2");
        }});
        list.add(new AliquotIdBreakdown() {{
            setAliquotId("mockaliquot3");
            setAnalyteId("mockanalyte3");
            setSampleId("mocksample3");
            setParticipantId("mockparticipant3");
        }});
        return list;
    }
}//En dof Class
