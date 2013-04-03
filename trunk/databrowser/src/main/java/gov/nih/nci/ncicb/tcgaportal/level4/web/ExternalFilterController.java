package gov.nih.nci.ncicb.tcgaportal.level4.web;

import gov.nih.nci.ncicb.tcgaportal.level4.dao.Level4Queries;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.Level4QueriesGetter;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.QueriesException;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Disease;
import gov.nih.nci.ncicb.tcgaportal.level4.web.request.ExternalRequest;
import gov.nih.nci.ncicb.tcgaportal.util.ProcessLogger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description :  Controller used to redirect to the appropriate Anomaly Search pages 
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */
//todo  need a new name for this, it's too similar to the DataAccessExternalFilterController  -dn
public class ExternalFilterController extends AbstractCommandController {

    private ProcessLogger logger;
    private String successView;
    private String failView;

    private Level4QueriesGetter level4QueriesGetter;
    
    public ExternalFilterController() {

    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public String getFailView() {
        return failView;
    }

    public void setFailView(String failView) {
        this.failView = failView;
    }

    public void setLogger(ProcessLogger logger) {
        this.logger = logger;
    }

    public void setLevel4QueriesGetter(Level4QueriesGetter level4QueriesGetter) {
        this.level4QueriesGetter = level4QueriesGetter;
    }

    protected ModelAndView handle(HttpServletRequest request, HttpServletResponse response,
                                  Object command, BindException errors) throws QueriesException {
        ModelAndView ret;
        ExternalRequest externalReq = (ExternalRequest) command;
        logger.logDebug("Mode="+externalReq.getMode());
        logger.logDebug("Disease="+externalReq.getDisease());        
        externalReq.validate();
        checkDisease(externalReq.getDisease());
        ret = new ModelAndView(successView, "externalRequest", externalReq);
        return ret;
    }                                               

    // check if the disease value is correct
    private boolean checkDisease(final String disease) throws QueriesException{

        boolean diseaseFound = false;
        for (final String diseaseFromList : level4QueriesGetter.getDiseaseNames()){
            if(diseaseFromList.equalsIgnoreCase(disease)){
                diseaseFound = true;
            }
        }

        if(!diseaseFound) {
            throw new IllegalArgumentException("Disease Name is not correct !");
        }
        
        return diseaseFound;
    }
    

}
