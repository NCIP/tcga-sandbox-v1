package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.remote;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.soundcheck.RemoteValidationHelper;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Remote validator for center (domain) names;
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class RemoteDomainNameValidator extends AbstractRemoteValidator<Archive> {

    /**
     * Calls setRemoteValidationHelper on parameter passed in.
     *
     * @param helper the remote validation helper to use in this validator
     */
    public RemoteDomainNameValidator(RemoteValidationHelper helper) {
        super(helper);
    }

    /**
     * Calls the remote validation helper to see if the archive's center (domain name) is valid.
     *  
     * @param archive the archive to validate
     * @param context the context for this QC call
     * @return true if the archive's domain name is valid, false if not
     * @throws ProcessorException if the validation cannot complete for some reason
     */
    protected Boolean doWork(Archive archive, QcContext context) throws ProcessorException {
        try {
            final Boolean ret = remoteValidationHelper.centerExists(archive.getDomainName());
            if(!ret){
                context.addError("Invalid domain name "+archive.getDomainName());
            }
            return ret;
        } catch (ApplicationException e) {
            throw new ProcessorException(e.getMessage(), e);
        }
    }

    /**
     * @return the descriptive name of this validator
     */
    public String getName() {
        return "remote domain name validation";
    }
}
