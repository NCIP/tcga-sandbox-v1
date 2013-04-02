package gov.nih.nci.ncicb.tcga.dcc.qclive.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DataTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Visibility;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

/**
 * DAO interface for experiment
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface ExperimentDAO {
    public String getPrivateDeployRoot();

    public String getPublicDeployRoot();

    public Archive getArchiveByName(final String archiveName);

    public File getDeployDirectoryPath(final Archive archive);

    /**
     * Get the deploy directory to put this archive in the protected filesystem,
     * regardless of the public/protected status of the archive.
     * @param archive
     * @return
     */
    public File getProtectedDeployDirectoryPath(Archive archive);

    public void updateArchiveStatus(final Archive archive);

    public void setPublicDeployRoot(String publicDeployRoot);

    public void setPrivateDeployRoot(String privateDeployRoot);

    public void setDiseaseArchiveQueries(ArchiveQueries diseaseArchiveQueries);

    public void setCommonArchiveQueries(ArchiveQueries commonArchiveQueries);

    public void setAccessQueries(VisibilityQueries accessQueries);

    public void setDataTypeQueries(DataTypeQueries dataTypeQueries);
}
