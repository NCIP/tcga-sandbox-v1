package gov.nih.nci.ncicb.tcga.dcc.dam.web;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMFacadeI;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DataAccessDownloadModel;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.DADRequest;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.SelectionRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Test class for DataAccessDownloadController
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class DataAccessDownloadControllerFastTest {
    private Mockery context = new JUnit4Mockery();
    private ArchiveQueries mockArchiveQueries;
    private DataAccessMatrixQueries mockDAMQueries;
    private HttpServletRequest mockServletRequest;
    private HttpSession mockSession;
    private DAMFacadeI mockDAMFacade;

    private DataAccessDownloadController dataAccessDownloadController;

    @Before
    public void setup() {
        mockArchiveQueries = context.mock(ArchiveQueries.class);
        mockDAMQueries = context.mock(DataAccessMatrixQueries.class);
        mockServletRequest = context.mock(HttpServletRequest.class);
        mockSession = context.mock(HttpSession.class);
        mockDAMFacade = context.mock(DAMFacadeI.class);

        dataAccessDownloadController = new DataAccessDownloadController();
        dataAccessDownloadController.setArchiveQueries(mockArchiveQueries);
        dataAccessDownloadController.setDataAccessMatrixQueries(mockDAMQueries);

        context.checking(new Expectations() {{
            allowing(mockServletRequest).getSession();
            will(returnValue(mockSession));

            allowing(mockSession).getAttribute("damFacade");
            will(returnValue(mockDAMFacade));

            allowing(mockDAMFacade).setSelection(with(any(SelectionRequest.class)));

            allowing(mockDAMFacade).getDiseaseType();
            will(returnValue("TEST"));
        }});
    }

    @Test
    public void testHandler() throws DataAccessMatrixQueries.DAMQueriesException {
        final DADRequest dadRequest = new DADRequest();
        dadRequest.setSelectedCells("not blank");

        final Archive archive123 = new Archive();
        archive123.setId(123L);
        archive123.setRealName("archive123");
        archive123.setDeployLocation("/path/to/archive123");

        final List<DataSet> selectedDataSets = new ArrayList<DataSet>();
        final DataSet dataSet = new DataSet();
        dataSet.setArchiveId(123);
        selectedDataSets.add(dataSet);

        context.checking(new Expectations() {{
            one(mockDAMFacade).getSelectedDataSets();
            will(returnValue(selectedDataSets));

            one(mockDAMQueries).getFileInfoForSelectedDataSets(selectedDataSets, false);

            one(mockArchiveQueries).getArchive(123L);
            will(returnValue(archive123));

            one(mockSession).setAttribute(with("dadModel"), with(any(DataAccessDownloadModel.class)));

            one(mockSession).setAttribute("damSelectedCells", "not blank");
        }});

        final ModelAndView modelAndView = dataAccessDownloadController.handle(mockServletRequest, null, dadRequest, null);
        final DataAccessDownloadModel dadModel = (DataAccessDownloadModel) modelAndView.getModel().get("dadModel");
        assertEquals(archive123, dadModel.getOriginalArchives().iterator().next());
    }
}
