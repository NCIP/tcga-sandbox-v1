package gov.nih.nci.ncicb.tcga.dcc.dam.web;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DAMJobStatus;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.FilePackagerBean;
import gov.nih.nci.ncicb.tcga.dcc.dam.processors.FilePackagerFactoryI;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.DAMJobStatusService;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAFPViewItems;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMFacadeI;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DataAccessDownloadModel;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.DAFPRequest;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.FilterRequest;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.FilterRequestI;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;

/**
 * Test for DataAccessFileProcessingController
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class DataAccessFileProcessingControllerFastTest {
    private final Mockery context = new JUnit4Mockery();
    private DataAccessFileProcessingController controller;
    private DAFPRequest dafpRequest;
    private DataAccessDownloadModel dadModel;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private HttpSession mockSession;
    private DAMFacadeI mockFacade;
    private FilterRequestI fakeFilterRequest;
    private FilePackagerFactoryI mockFilePackagerFactory;
    private List<DataFile> fakeDataFileList;
    private FilePackagerBean filePackagerBean;
    private DAMJobStatusService mockJobStatusService;

    @Before
    public void setup() {
        controller = new DataAccessFileProcessingController();
        controller.setSuccessView("yay");
        controller.setErrorView("boo");
        controller.setArchivePhysicalPathPrefix("/something/");
        controller.setLinkSite("http://awesome");
        controller.setStatusCheckUrl("http://whatisup");

        fakeFilterRequest = new FilterRequest();
        fakeDataFileList = new ArrayList<DataFile>();
        dafpRequest = new DAFPRequest();
        dadModel = new DataAccessDownloadModel("TEST") {
            // overriding this because we aren't trying to test the model functionality, and the model has no interface
            public List<DataFile> getFileInfoForSelectedTreeNodes(final String treeNodeIds) {
                return fakeDataFileList;
            }
        };
        mockRequest = context.mock(HttpServletRequest.class);
        mockResponse = context.mock(HttpServletResponse.class);
        mockSession = context.mock(HttpSession.class);
        mockFacade = context.mock(DAMFacadeI.class);
        mockFilePackagerFactory = context.mock(FilePackagerFactoryI.class);
        controller.setFilePackagerFactory(mockFilePackagerFactory);
        dafpRequest.setEmail("testEmail");
        dafpRequest.setTreeNodeIds("1,2,3");
        filePackagerBean = new FilePackagerBean();
        mockJobStatusService = context.mock(DAMJobStatusService.class);
        controller.setJobStatusService(mockJobStatusService);

        context.checking(new Expectations() {{
            allowing(mockRequest).getSession();
            will(returnValue(mockSession));
            allowing(mockSession).getAttribute("dadModel");
            will(returnValue(dadModel));
            allowing(mockSession).getAttribute("damFacade");
            will(returnValue(mockFacade));
            allowing(mockFacade).getPreviousFilterRequest();
            will(returnValue(fakeFilterRequest));

        }});
    }

    @Test
    public void testHandle() throws SchedulerException {

        final DAMJobStatus jobStatus = new DAMJobStatus();
        context.checking(new Expectations() {{
            one(mockFilePackagerFactory).createFilePackagerBean(with("TEST"), with(fakeDataFileList), with("testEmail"), with(false), with(false), with(any(UUID.class)), with(fakeFilterRequest));
            will(returnValue(filePackagerBean));

            one(mockFilePackagerFactory).enqueueFilePackagerBean(filePackagerBean);

            one(mockSession).removeAttribute("damFacade");
            one(mockSession).removeAttribute("dadModel");
            one(mockSession).removeAttribute("damSessionKey");

            one(mockJobStatusService).getJobStatusForJobKey(with(any(String.class)));
            will(returnValue(jobStatus));
        }});

        final ModelAndView modelAndView = controller.handle(mockRequest, mockResponse, dafpRequest, null);
        assertEquals("yay", modelAndView.getViewName());
        assertEquals("/something/", filePackagerBean.getArchivePhysicalPathPrefix());
        assertEquals("http://awesome", filePackagerBean.getArchiveLinkSite());
        assertEquals(jobStatus, ((DAFPViewItems) modelAndView.getModel().get("DAFPInfo")).getJobStatus());
        assertEquals("http://whatisup?job=" + filePackagerBean.getKey(), filePackagerBean.getStatusCheckUrl());
    }

    @Test
    public void testHandleWithSchedulerException() throws SchedulerException {
        context.checking(new Expectations() {{
            one(mockFilePackagerFactory).createFilePackagerBean(with("TEST"), with(fakeDataFileList), with("testEmail"), with(false), with(false), with(any(UUID.class)), with(fakeFilterRequest));
            will(returnValue(filePackagerBean));

            one(mockFilePackagerFactory).enqueueFilePackagerBean(filePackagerBean);
            will(throwException(new SchedulerException("this is a test, it's okay if you see this stack trace")));
        }});

        final ModelAndView modelAndView = controller.handle(mockRequest, mockResponse, dafpRequest, null);
        assertEquals("boo", modelAndView.getViewName());
    }
}
