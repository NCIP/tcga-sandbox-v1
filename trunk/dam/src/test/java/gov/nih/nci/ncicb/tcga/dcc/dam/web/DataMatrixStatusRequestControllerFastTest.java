package gov.nih.nci.ncicb.tcga.dcc.dam.web;

import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DAMJobStatus;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.DAMJobStatusService;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAFPViewItems;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.Assert.assertEquals;

/**
 * Fast test for DataMatrixStatusRequestController
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class DataMatrixStatusRequestControllerFastTest {
    private final Mockery context = new JUnit4Mockery();
    private DAMJobStatusService mockJobStatusService;
    private CommonBarcodeAndUUIDValidator mockBarcodeAndUUIDValidator;
    private DataMatrixStatusRequestController statusRequestController;

    @Before
    public void setup() {
        mockJobStatusService = context.mock(DAMJobStatusService.class);
        mockBarcodeAndUUIDValidator = context.mock(CommonBarcodeAndUUIDValidator.class);

        statusRequestController = new DataMatrixStatusRequestController();
        statusRequestController.setDamJobStatusService(mockJobStatusService);
        statusRequestController.setBarcodeAndUUIDValidator(mockBarcodeAndUUIDValidator);
    }

    @Test
    public void testHandleRequest() {
        final String jobKey = "12345678-1111-2222-3333-abcdefabcdef";
        final DAMJobStatus jobStatus = new DAMJobStatus();
        context.checking(new Expectations() {{
            one(mockJobStatusService).getJobStatusForJobKey(jobKey);
            will(returnValue(jobStatus));
            one(mockBarcodeAndUUIDValidator).validateUUIDFormat(jobKey);
            will(returnValue(true));
        }});
        final ModelAndView modelAndView = statusRequestController.handleRequest(jobKey);
        assertEquals(jobStatus, ((DAFPViewItems) modelAndView.getModel().get("DAFPInfo")).getJobStatus());
    }

    @Test
    public void testHandleRequestBadKey() {

        context.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidator).validateUUIDFormat("hello");
            will(returnValue(false));
        }});

        final ModelAndView modelAndView = statusRequestController.handleRequest("hello");
        final DAMJobStatus jobStatus = ((DAFPViewItems) modelAndView.getModel().get("DAFPInfo")).getJobStatus();

        assertEquals("Unknown", jobStatus.getStatus());
        assertEquals("Job key 'hello' is not valid.", jobStatus.getMessage());
    }
}
