package gov.nih.nci.ncicb.tcga.dcc.qclive.soundcheck;

import gov.nih.nci.ncicb.tcga.dccws.Archive;
import gov.nih.nci.ncicb.tcga.dccws.FileInfo;
import gov.nih.nci.system.applicationservice.ApplicationException;

import java.util.List;

/**
 * Interface for RemoteValidationHelper class.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface RemoteValidationHelper {
    boolean centerExists(String centerName) throws ApplicationException;

    /**
     * Gets the center ID of the center with the given domain name and type.
     *
     * @param centerName the center domain name
     * @param centerType the center type
     * @return the center ID or null
     * @throws ApplicationException if there is an error connecting to the web service
     */
    Integer getCenterId(String centerName, String centerType) throws ApplicationException;

    boolean platformExists( String platformName ) throws ApplicationException;

    boolean archiveExists( String archiveName ) throws ApplicationException;

    String getCenterTypeForPlatform(String platformName) throws ApplicationException;

    boolean diseaseExists( String diseaseName ) throws ApplicationException;

    boolean isLatest( String archiveName ) throws ApplicationException;

    String getLatestArchive( String diseaseName, String centerName, String platformName, Integer serialIndex ) throws ApplicationException;

    List<Archive> getLatestArchives(String diseaseName, String centerName, String platformName) throws ApplicationException;

    boolean fileExists( String fileName, String archiveName ) throws ApplicationException;

    /**
     * Checks validity of project name.
     *
     * @param projectName the project name
     * @return true if the project name exists in the DCC db, false if not
     * @throws ApplicationException
     */
    boolean projectExists(String projectName) throws ApplicationException;

    /**
     * Checks validity of TSS code.
     *
     * @param tssCode the TSS code
     * @return true if the TSS code exists in the DCC db, false if not
     * @throws ApplicationException
     */
    boolean tssCodeExists(String tssCode) throws ApplicationException;

    /**
     * Checks validity of sample type.
     *
     * @param sampleType the sample type
     * @return true if the sample type exists in the DCC db, false if not
     * @throws ApplicationException
     */
    boolean sampleTypeExists(String sampleType) throws ApplicationException;

    /**
     * Checks validity of portion analyte code
     * @param portionAnalyte the portion analyte code
     * @return true if the portion analyte exists in the DCC db, false if not
     * @throws ApplicationException
     */
    boolean portionAnalyteExists(String portionAnalyte) throws ApplicationException;

    /**
     * Checks validity of BCR center id.
     *
     * @param bcrCenterId the BCR center id
     * @return true if the BCR center id exists in the DCC db, false if not
     * @throws ApplicationException
     */
    boolean bcrCenterIdExists(String bcrCenterId) throws ApplicationException;

    /**
     * Get all data files for the given archive (which are files with data level > 0)
     *
     * @param archive the archive
     * @return list of data files
     * @throws ApplicationException
     */
    List<FileInfo> getArchiveDataFiles(Archive archive) throws ApplicationException;

}
