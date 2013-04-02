package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.remote;

import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.soundcheck.RemoteValidationHelper;

/**
 * Abstract parent class for remote validators.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public abstract class AbstractRemoteValidator<I> extends AbstractProcessor<I, Boolean> {
    protected RemoteValidationHelper remoteValidationHelper;

    /**
     * Calls setRemoteValidationHelper on parameter passed in.
     *
     * @param helper the remote validation helper to use in this validator
     */
    public AbstractRemoteValidator( RemoteValidationHelper helper ) {
        setRemoteValidationHelper( helper );
    }

    /**
     * Sets the remote validation helper to use.
     * @param helper object that implements the RemoteValidationHelper interface
     */
    public void setRemoteValidationHelper( RemoteValidationHelper helper ) {
        this.remoteValidationHelper = helper;
    }
}
