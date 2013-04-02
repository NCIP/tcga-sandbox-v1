package gov.nih.nci.ncicb.tcga.dcc.qclive.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileToArchive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BiospecimenToFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.DataMatrixFileBean;

import java.util.List;
import java.util.Map;

/**
 * Interface for Level2Data DAO queries
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface Level2DataService {
    /**
     * Returns multiple aliquots data matrix files details.
     * @return datamatrixfilebeans indexed by archive name
     */
    public Map<String,List<DataMatrixFileBean>> getMultipleAliquotDataMatrixFiles();

    /**
     * Returns sdrf file path
     * @param centerName
     * @param platformName
     * @param diseaseAbbreviation
     * @return sdrf filepath
     */
    public String getSdrfFilePathForExperiment(final String centerName,
                                               final String platformName,
                                               final String diseaseAbbreviation);

    /**
     * Adds file info records
     * @param filesInfo
     */
    public void addFiles(final List<FileInfo> filesInfo);

    /**
     * Deletes file info records
     * @param fileIds
     */
    public void deleteFiles(final List<Long> fileIds);

    /**
     * Adds filetoarchive records
     * @param fileToArchives
     */
    public void addFileToArchiveAssociations(final List<FileToArchive> fileToArchives);

    /**
     * Deletes filetoarchive records
     * @param fileIds
     * @param archiveId
     */
    public void deleteFileToArchiveAssociations(final List<Long> fileIds, final Long archiveId);

    /**
     * Updates biospecimenttofile records fileids to new fileids
     * @param biospecimenToFiles
     */
    public void updateBiospecimenToFileAssociations(final List<BiospecimenToFile> biospecimenToFiles);

    /**
     * Returns biospecimenids for the given barcodes
     * @param barcodes
     * @return biospecimenids indexed by biospecimen barcode
     */
    public Map<String,Integer> getBiospecimenIdsForBarcodes(final List<String> barcodes);

    /**
     * Validates database connections.
     * If there is an error throws runtime exception
     */
    public void validateConnections();

    /**
     * Returns database environment (Dev/Stage/QA/Production)
     * @return database environmen
     */
    public String getDatabaseEnvironment();

    /**
     * Returns disease database disease abbreviation
     * @return disease abbreviation
     */
    public String getDisease();
}
