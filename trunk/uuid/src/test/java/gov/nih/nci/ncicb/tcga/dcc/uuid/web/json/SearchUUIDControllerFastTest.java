/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.web.json;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.SearchCriteria;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
import gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants;
import gov.nih.nci.ncicb.tcga.dcc.uuid.service.UUIDReportService;
import gov.nih.nci.ncicb.tcga.dcc.uuid.service.UUIDReportServiceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;

/**
 * Class to test Search controller
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */

@RunWith (JMock.class)
public class SearchUUIDControllerFastTest {

    private final JUnit4Mockery context = new JUnit4Mockery();
    private gov.nih.nci.ncicb.tcga.dcc.uuid.web.json.SearchUUIDController controller;
    private UUIDService uuidService;
    private HttpSession session;
    private SearchCriteria criteria;
    private List<UUIDDetail> list;    


    @Before
    public void setup() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        session = request.getSession();
        controller = new SearchUUIDController();
        uuidService = context.mock(UUIDService.class);
        controller.setUuidService(uuidService);

        final UUIDReportService uuidReportService = new UUIDReportServiceImpl();
        controller.setUuidReportService(uuidReportService);

        criteria = new SearchCriteria();
        list = new ArrayList<UUIDDetail>();
        Center center = new Center();
        center.setCenterId(1);
        center.setCenterName("");
        list.add(new UUIDDetail("mammoth", new Date(), UUIDConstants.GenerationMethod.Web, center, "master_user"));
        list.add(new UUIDDetail("tiger", new Date(), UUIDConstants.GenerationMethod.Web, center, "master_user"));
        list.add(new UUIDDetail("mammothKid", new Date(), UUIDConstants.GenerationMethod.Web, center, "master_user"));
        list.add(new UUIDDetail("tigeress", new Date(), UUIDConstants.GenerationMethod.Web, center, "master_user"));
        
    }

    @Test
    public void testSearchUUIDRequest() throws UUIDException {
        mockService();

        ModelMap modelMap = controller.performSearch(new ModelMap(), criteria, 0, 25, session);
        
        assertTrue(modelMap != null);
        int totalCount = (Integer)modelMap.get(UUIDConstants.TOTAL_COUNT);
        assertEquals(4,totalCount);
        List<UUIDDetail> uuidList = (List<UUIDDetail>)modelMap.get("searchResults");
        assertEquals(uuidList.get(0).getUuid(), "mammoth");
        assertEquals(uuidList.get(1).getUuid(), "tiger");
    }

    @Test
    public void testSearchUUIDRequestNoStartSpecified() throws UUIDException {
        mockService();

        ModelMap modelMap = controller.performSearch(new ModelMap(), criteria, null, null, session);

        assertTrue(modelMap != null);
        int totalCount = (Integer)modelMap.get(UUIDConstants.TOTAL_COUNT);
        assertEquals(4,totalCount);
        List<UUIDDetail> uuidList = (List<UUIDDetail>)modelMap.get("searchResults");
        assertEquals(uuidList.get(0).getUuid(), "mammoth");
        assertEquals(uuidList.get(1).getUuid(), "tiger");
    }    

    @Test
    public void testPaginateResults() throws UUIDException {
        mockService();

        ModelMap modelMap = controller.paginateAndSortResults(new ModelMap(), criteria, 0, 2, UUIDConstants.COLUMN_UUID, UUIDConstants.ASC, session);
        assertTrue(modelMap != null);
        int totalCount = (Integer)modelMap.get(UUIDConstants.TOTAL_COUNT);
        assertEquals(4,totalCount);
        List<UUIDDetail> uuidList = (List<UUIDDetail>)modelMap.get("searchResults");
        assertEquals(2,uuidList.size());
        assertEquals(uuidList.get(0).getUuid(), "mammoth");
    }

    @Test
    public void testPaginateResultsFromSession() throws UUIDException {
        //note that there is no need to mock the service here, since the data will be picked up from session 
        session.setAttribute(UUIDConstants.SEARCH_RESULTS, list);
        ModelMap modelMap = controller.paginateAndSortResults(new ModelMap(), criteria, 0, 2, UUIDConstants.COLUMN_UUID, UUIDConstants.ASC, session);
        assertTrue(modelMap != null);
        int totalCount = (Integer)modelMap.get(UUIDConstants.TOTAL_COUNT);
        assertEquals(4,totalCount);
        List<UUIDDetail> uuidList = (List<UUIDDetail>)modelMap.get("searchResults");
        assertEquals(2,uuidList.size());        
        assertEquals(uuidList.get(0).getUuid(), "mammoth");
    }

    private void mockService() {
        context.checking(new Expectations() {{
            one(uuidService).searchUUIDs(criteria);
            will(returnValue(list));
        }});
    }

}
