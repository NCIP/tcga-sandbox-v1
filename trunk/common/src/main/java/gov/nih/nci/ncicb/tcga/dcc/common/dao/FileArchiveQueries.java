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
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileToArchive;

import java.util.List;
import java.util.Map;

/**
 * Interface for FileArchiveQueries
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface FileArchiveQueries {
    /**
     * Add an association between this file and this archive.
     *
     * @param fileInfo the file info
     * @param archive the archive
     * @return return the file to archive association ID 
     */
    public Long addFileToArchiveAssociation(FileInfo fileInfo, Archive archive);

    /**
     * Add an association between this file and this archive.  
     * @param fileInfo file info
     * @param archive archive
     * @param useIdFromCommon Set to true if fileArchiveId should be used while adding the association to database, false otherwise
     * @param fileArchiveId file archive id
     * @return the file to archive association id in database
     */
    public Long addFileToArchiveAssociation(final FileInfo fileInfo, final Archive archive, Boolean useIdFromCommon, Long fileArchiveId);

    public void addFileToArchiveAssociations(final List<FileToArchive>fileToArchives);
    public void deleteFileToArchiveAssociations(final List<Long> fileIds, final Long archiveId);
    public boolean associationExists(FileInfo fileInfo, Archive archive);

    public Map<String,List<String>> getClinicalXMLFileLocations();
}
