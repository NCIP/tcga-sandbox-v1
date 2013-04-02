package gov.nih.nci.ncicb.tcga.dcc.qclive.live;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcLiveStateBean;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.clinical.ClinicalLoaderException;

import java.util.List;

/**
 * TODO: class documentation
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface LiveI {

    public void processUpload(final String filename, Integer md5ValidationAttempts,QcLiveStateBean stateContex);

    /**
     * Method to be called by the scheduler when it is time for an experiment to be checked
     *
     * @param experimentName the name of the experiment to check (in form center_disease.platform)
     * @param experimentType the type of the experiment: CGCC, BCR, or GSC
     */
    public void checkExperiment(final String experimentName, final String experimentType, final QcLiveStateBean stateContext);

    /**
     * Method to be called by the scheduler when it is time for an experiment to be checked
     *
     * @param experimentName  the name of the experiment to check (in form center_disease.platform)
     * @param experimentType  the type of the experiment: CGCC, BCR, or GSC
     * @param archiveFileName the filename of the archive that will be exclusively considered as part of this experiment
     */
    public void checkExperiment(final String experimentName, final String experimentType, final String archiveFileName,final QcLiveStateBean stateContext);

    public void loadClinicalData(List<Archive> deployedArchives, QcLiveStateBean stateContext) throws ClinicalLoaderException;

    public String getServerUrl();

    public String getEmailBcc();
}
