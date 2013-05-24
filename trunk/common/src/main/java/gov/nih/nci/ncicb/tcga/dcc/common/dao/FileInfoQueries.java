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
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.web.FileInfoQueryRequest;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Robert S. Sfeir
 * @author David Kane
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface FileInfoQueries {

    public Collection<FileInfo> getFilesForArchive(FileInfoQueryRequest queryParams);

    /**
     * Adds the file information to the database
     * @param theFile fileInfo object
     * @return fileId
     */
    public Long addFile(FileInfo theFile);

    /**
     * Adds the file information to the database
     * @param theFile fileInfo object
     * @param useIdFromCommon Set to true if the fileId set in the fileInfo should be used while adding
     * the fileInfo to the database, false otherwise   
     * @return fileId
     */
    public Long addFile(FileInfo theFile, Boolean useIdFromCommon);

    public void updateFile(FileInfo theFile);

    public Long getFileId(String fileName, Long archiveId);

    public String getFileNameById(Long integer);

    public void updateFileDataLevel(final Long fileId, final Integer dataLevel);

    public Integer getFileDataLevel(final Long fileId);

    /**
     * Returns FileInfo for a given file id
     * @param fileId file identifier
     * @return FileInfo object
     */
    public FileInfo getFileForFileId(final Long fileId);

    public Archive getLatestArchiveContainingFile(FileInfo fileInfo);

    public String getSdrfFilePathForExperiment(String domainName, String platformName, String diseaseAbbreviation);

    public void addFiles(final List<FileInfo> fileInfoList);
    public void deleteFiles(final List<Long> fileIds);

    /**
     * Updates the file info datatype in the database
     * @param fileId The id of the file that is to be updated
     * @param dataTypeId The data type id of the datatype to which the file should be associated
     */
    public void updateFileDataType(final Long fileId, final Integer dataTypeId);

    /**
     * Updates batch of file info data type ids
     * @param fileInfoList
     */
    public void updateFileDatTypes(final List<FileInfo> fileInfoList );

    /**
     * Returns BCRXMLFiles location for the given patient barcodes
     * @param patientBarcodes
     * @return list of bcrxmlfiles location
     */
    public List<String> getBCRXMLFileLocations(final List<String> patientBarcodes);


    /**
     * Update the location of all files contained in the given archives to the public location
     *
     * @param archiveIds the Ids of the archives for which to update the files deploy location to public
     */
    public void updateArchiveFilesLocationToPublic(final Set<Long> archiveIds);

    /**
     * Deletes all the file id references for the given archive.
     * @param archiveName archiveName.
     */
    void deleteFilesFromArchive(final String archiveName);
}
