/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ArchiveType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.web.ArchiveQueryRequest;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Robert S. Sfeir
 * @author David Kane
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface ArchiveQueries {

    public List<Archive> getMatchingArchives(ArchiveQueryRequest queryParams);

    public Archive getArchive(long archiveId);

    public Archive getArchive(final String archiveName);

    /**
     * Gets the current latest available archive matching the disease, center, platform, archive type, and serial index
     * of the given archive.
     *
     * @param archive the archive whose latest version you are seeking
     * @return latest available archive or null
     */
    public Archive getLatestVersionArchive(Archive archive);

    /**
     * Adds an archive to database
     *
     * @param archive archive object
     * @return archive Id
     */
    public Long addArchive(Archive archive);

    /**
     * Adds an archive to database
     *
     * @param archive         the archive object
     * @param useIdFromCommon Set to true if the archive Id  set in the archive should be used while adding
     *                        the archive to database, false otherwise
     * @return archive Id
     */
    public Long addArchive(Archive archive, boolean useIdFromCommon);

    public void addLogToArchiveEntry(Long archiveId, Integer logId);

    public Long getArchiveIdByName(String archiveName);

    public void updateDeployLocation(Archive archive);

    /**
     * Updates the archive's secondary deploy location to whatever value is set in the archive bean
     *
     * @param archive the archive to update
     */
    public void updateSecondaryDeployLocation(Archive archive);

    public Long exists(String archiveName);

    public void updateArchiveStatus(Archive archive);

    public void updateAddedDate(final Long archiveId, final Date date);

    public void setToLatest(Archive archive);

    public void setToLatestLoaded(Archive archive);

    public void setArchiveInitialSize(final Long archiveId, final long sizeInKB);

    public void setArchiveFinalSize(final Long archiveId, final long sizeInKB);

    public long getArchiveSize(final Long archiveId);

    /**
     * Gets the max revision for the given archive (meaning the max revision for any archive with the same center,
     * disease, platform, archive type, and serial index as the given archive). If availableOnly is true, will only
     * consider archives with status Available, otherwise will consider all archives.
     *
     * @param archive       the archive to use for looking for revisions
     * @param availableOnly whether to consider only Available archives or not
     * @return the max revision number for archives of that experiment, type, and serial index or -1 if none found
     */
    public Long getMaxRevisionForArchive(Archive archive, boolean availableOnly);

    /**
     * updates archive_info table with the latest timestamp to indicate the load has been finished.
     *
     * @param archiveInfoId id of the archive to update
     */
    public void updateArchiveInfo(final Long archiveInfoId);

    /**
     * Retrieves barcode History based on barcode
     *
     * @param barcode for which to find UUID
     */
    public String getUUIDforBarcode(final String barcode);

    /**
     * Retrieve a location of SDRF file. SDRF file is part of a MAGE-TAB archive and the query
     * retrieves the latest file associated with the center,platform,disease combination
     *
     * @param center   for which to search for SDRF
     * @param platform platform for which to search for SDRF
     * @param disease  disease for which to search for SDRF
     * @return An SDRF dedeployed locations
     */
    public String getSdrfDeployLocation(final String center, final String platform, final String disease);

    /**
     * Retrieves a center based on domainName and platformName
     *
     * @param domainName   domain name for the center
     * @param platformName platform name for the center
     * @return center if found
     */
    public Center getCenterByDomainNameAndPlatformName(final String domainName, final String platformName);


    public List<FileInfo> getFilesForArchive(Long archiveId);

    /**
     * Retrieves latest archive id if archive exists for the given archive name filter
     * otherwise returns null
     * Archive name filter should be in the following format
     * domain_name_tumor.platform.archive_type.serial_index
     *
     * @param archiveNameFilter
     * @return archive id or null
     */
    public Long getLatestArchiveId(String archiveNameFilter);

    /**
     * Get list of all archive types
     *
     * @return list of all archive types
     */
    public List<ArchiveType> getAllArchiveTypes();

    /**
     * returns list of available mage-tab archives
     * @return
     */
    public List<Archive> getMagetabArchives();

    /**
     * Return a list of all available protected bio archives
     *
     * Note that for each returned archives, only archive Id, deploy location and tumor type are populated.
     *
     * @return a list of all available protected bio archives
     */
    public List<Archive> getAllAvailableProtectedBioArchives();
    
    /**
     * returns list of avialable maf archives
     * @return
     */
    public List<Archive> getProtectedMafArchives();


    /**
     * Update the location of the provided archives to the public location
     *
     * @param archiveIds the Ids of the archives for which to update the deploy location to public
     */
    public void updateArchivesLocationToPublic(final Set<Long> archiveIds);
}
