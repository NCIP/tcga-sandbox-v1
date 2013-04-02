package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DataTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Visibility;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ExperimentDAO;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.VisibilityQueries;

import java.io.File;

/**
 * DAO for experiment
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ExperimentDAOImpl implements ExperimentDAO {
    private ArchiveQueries commonArchiveQueries;
    private ArchiveQueries diseaseArchiveQueries;
    private VisibilityQueries accessQueries;
    private DataTypeQueries dataTypeQueries;
    private String publicDeployRoot;
    private String privateDeployRoot;

    public ArchiveQueries getCommonArchiveQueries() {
        return commonArchiveQueries;
    }

    public void setCommonArchiveQueries(ArchiveQueries commonArchiveQueries) {
        this.commonArchiveQueries = commonArchiveQueries;
    }

    public ArchiveQueries getDiseaseArchiveQueries() {
        return diseaseArchiveQueries;
    }

    public void setDiseaseArchiveQueries(ArchiveQueries diseaseArchiveQueries) {
        this.diseaseArchiveQueries = diseaseArchiveQueries;
    }

    public VisibilityQueries getAccessQueries() {
        return accessQueries;
    }

    public void setAccessQueries(VisibilityQueries accessQueries) {
        this.accessQueries = accessQueries;
    }

    public DataTypeQueries getDataTypeQueries() {
        return dataTypeQueries;
    }

    public void setDataTypeQueries(DataTypeQueries dataTypeQueries) {
        this.dataTypeQueries = dataTypeQueries;
    }

    public String getPublicDeployRoot() {
        return publicDeployRoot;
    }

    public void setPublicDeployRoot(String publicDeployRoot) {
        this.publicDeployRoot = publicDeployRoot;
    }

    public String getPrivateDeployRoot() {
        return privateDeployRoot;
    }

    public void setPrivateDeployRoot(String privateDeployRoot) {
        this.privateDeployRoot = privateDeployRoot;
    }

    public Archive getArchiveByName(final String archiveName) {
        return commonArchiveQueries.getArchive(archiveName);
    }

    public File getProtectedDeployDirectoryPath(final Archive archive) {
        final Visibility protectedVisibility = new Visibility();
        protectedVisibility.setIdentifiable(true);
        return getDeployDirectoryPath(archive, protectedVisibility);
    }

    public File getDeployDirectoryPath(final Archive archive) {
        final Visibility visibility = accessQueries.getVisibilityForArchive(archive);
        return getDeployDirectoryPath(archive, visibility);
    }

    private File getDeployDirectoryPath(final Archive archive, final Visibility visibility) {
        final String deployRoot;
        if (visibility != null && !visibility.isIdentifiable()) {
            deployRoot = publicDeployRoot;
        } else { // if can't find visibility, use private
            deployRoot = privateDeployRoot;
        }
        final String dataType = dataTypeQueries.getDataTypeFTPDisplayForPlatform(archive.getThePlatform().getPlatformId().toString());
        if (dataType == null) {
            return null;
        }
        return new File(new StringBuilder().append(deployRoot)
                .append(File.separator)
                .append("tumor")
                .append(File.separator)
                .append(archive.getTheTumor().getTumorName().toLowerCase())
                .append(File.separator)
                .append(archive.getTheCenter().getCenterType().toLowerCase())
                .append(File.separator)
                .append(archive.getTheCenter().getCenterName().toLowerCase())
                .append(File.separator)
                .append(archive.getThePlatform().getPlatformName().toLowerCase())
                .append(File.separator)
                .append(dataType.toLowerCase())
                .append(File.separator)
                .append(archive.getArchiveName())
                .toString());


    }

    public void updateArchiveStatus(final Archive archive) {
        commonArchiveQueries.updateArchiveStatus(archive);
        diseaseArchiveQueries.updateArchiveStatus(archive);
    }
}
