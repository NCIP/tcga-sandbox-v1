package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.remote;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.CodeTableQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.soundcheck.RemoteValidationHelper;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Remote implementation of CodeTableQueries.  Uses RemoteValidationHelper to query the DCC Web Service.
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class RemoteCodeTableQueries implements CodeTableQueries {
    private final RemoteValidationHelper remoteValidationHelper;

    public RemoteCodeTableQueries(final RemoteValidationHelper remoteValidationHelper) {
        this.remoteValidationHelper = remoteValidationHelper;
    }

    /**
     * Checks if the given project name exists in database
     *
     * @param projectName project name
     * @return true if exists otherwise false
     */
    @Override
    public boolean projectNameExists(final String projectName) {
        try {
            return remoteValidationHelper.projectExists(projectName);
        } catch (ApplicationException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if the given tissue source site code exists in database
     *
     * @param tssCode tissue source site code
     * @return true if exists otherwise false
     */
    @Override
    public boolean tssCodeExists(final String tssCode) {
        try {
            return remoteValidationHelper.tssCodeExists(tssCode);
        } catch (ApplicationException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if a given sample type exists in database
     *
     * @param sampleType sample type
     * @return true if exists otherwise false
     */
    @Override
    public boolean sampleTypeExists(final String sampleType) {
        try {
            return remoteValidationHelper.sampleTypeExists(sampleType);
        } catch (ApplicationException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if the given portion analyte exists in database
     *
     * @param portionAnalyte portion analyte
     * @return true if exists otherwise false
     */
    @Override
    public boolean portionAnalyteExists(final String portionAnalyte) {
        try {
            return remoteValidationHelper.portionAnalyteExists(portionAnalyte);
        } catch (ApplicationException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if the given bcr center id exists in database
     *
     * @param bcrCenterId bcr center id
     * @return true if exists otherwise false
     */
    @Override
    public boolean bcrCenterIdExists(final String bcrCenterId) {
        try {
            return remoteValidationHelper.bcrCenterIdExists(bcrCenterId);
        } catch (ApplicationException e) {
            e.printStackTrace();
            return false;
        }
    }
}
