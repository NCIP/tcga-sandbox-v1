/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.webservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.SearchCriteria;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Class to test REST web service for searching UUIDs
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)

public class SearchUUIDWebServiceFastTest {

    private Mockery context = new JUnit4Mockery();
    private SearchUUIDWebService searchWS;
    private UUIDService uuidService;
    private SearchCriteria searchCriteria;

    @Before
    public void setUp() {
        uuidService = context.mock(UUIDService.class);
    }

   @Test
    public void testSearchUUIDToJSON() throws UUIDException {
       searchWS = new SearchUUIDWebService();
       mockSearchWebservice();
       final List<UUIDDetail> expectedList = mockUUIDService();
        List<UUIDDetail> actualList = searchWS.searchUUIDToJSON();
        assertNotNull(actualList);
        assertEquals(expectedList, actualList);
    }

    @Test
     public void testSearchUUIDToXML() throws UUIDException {
        searchWS = new SearchUUIDWebService();
        mockSearchWebservice();
        final List<UUIDDetail> expectedList = mockUUIDService();
         List<UUIDDetail> actualList = searchWS.searchUUIDToXML();
         assertNotNull(actualList);
         assertEquals(expectedList, actualList);
     }

    private List<UUIDDetail> mockUUIDService() {
        final List<UUIDDetail> expectedList = new ArrayList<UUIDDetail>();
        expectedList.add(new UUIDDetail());
        context.checking(new Expectations() {{
            one(uuidService).searchUUIDs(searchCriteria);
            will(returnValue(expectedList));
        }});
        return expectedList;
    }

    private void mockSearchWebservice() {
        searchWS.setUuidService(uuidService);
        searchCriteria = new SearchCriteria();
        searchWS.setSearchCriteria(searchCriteria);
        UUIDWebServiceUtilImpl wsUtil = new UUIDWebServiceUtilImpl();
        wsUtil.setService(uuidService);
        searchWS.setWebServiceUtil(wsUtil);
    }
    
}
