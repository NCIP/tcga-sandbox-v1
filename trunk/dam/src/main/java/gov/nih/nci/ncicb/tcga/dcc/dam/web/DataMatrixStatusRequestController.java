package gov.nih.nci.ncicb.tcga.dcc.dam.web;

import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DAMJobStatus;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.DAMJobStatusService;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAFPViewItems;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

/**
 * Controller that handles independent requests for DAM job status.  Uses the same view as the DataAccessFileProcessingController.
 *
 * @see DataAccessFileProcessingController
 *
 * @author Your Name
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DataMatrixStatusRequestController {

    /**
     * Parameter expected to contain job key
     */
    public static final String PARAM_NAME_JOB = "job";

    /**
     * The request this controller is mapped to
     */
    public static final String REQUEST_MAPPING = "/jobStatus.htm";

    private String successView;
    private DAMJobStatusService damJobStatusService;
    private CommonBarcodeAndUUIDValidator barcodeAndUUIDValidator;

    /**
     * Handles request for status of a job.  Expected parameter is "job" with UUID of job as value.
     *
     * @param jobKey the key for looking up the job (a UUID as a string)
     * @return model and view containing the success or error view, and a DAFPView object as model
     */
    @RequestMapping(value=REQUEST_MAPPING, method={RequestMethod.GET} )
    public ModelAndView handleRequest(@RequestParam (value = PARAM_NAME_JOB) final String jobKey) {

        final DAMJobStatus jobStatus;

        final DAFPViewItems jspInfo = new DAFPViewItems();
        if (barcodeAndUUIDValidator.validateUUIDFormat(jobKey)) {
            jobStatus = damJobStatusService.getJobStatusForJobKey(jobKey);
            jspInfo.setFilePackagerKey(UUID.fromString(jobKey));
        } else {
            jobStatus = new DAMJobStatus();
            jobStatus.setStatus("Unknown");
            jobStatus.setMessage("Job key '" + jobKey + "' is not valid.");
        }

        jspInfo.setJobStatus(jobStatus);

        return new ModelAndView( successView, "DAFPInfo", jspInfo );
    }

    public void setSuccessView(final String successView) {
        this.successView = successView;
    }

    public void setDamJobStatusService(final DAMJobStatusService damJobStatusService) {
        this.damJobStatusService = damJobStatusService;
    }

    public void setBarcodeAndUUIDValidator(final CommonBarcodeAndUUIDValidator barcodeAndUUIDValidator) {
        this.barcodeAndUUIDValidator = barcodeAndUUIDValidator;
    }
}
