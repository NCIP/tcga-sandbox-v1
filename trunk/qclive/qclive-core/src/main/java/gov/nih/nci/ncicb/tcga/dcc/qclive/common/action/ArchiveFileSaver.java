/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileInfoQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.MD5Validator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.DirectoryListerImpl;
import org.springframework.dao.DataAccessException;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Saves file information for an archive to the database.  Adds/updates rows to file_info. Note: inserts to
 * file_data_level are done by the SdrfProcessor.
 *
 * @author Jessica Chen Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class ArchiveFileSaver extends AbstractProcessor<Archive, Archive> {

    private FileInfoQueries commonFileInfoQueries;
    private FileInfoQueries diseaseFileInfoQueries;

    private FileArchiveQueries fileArchiveQueries;
    private FileArchiveQueries diseaseFileArchiveQueries;
    private String additionalFiles;
    private List<String> additionalFileList;



    protected Archive doWork(final Archive archive, final QcContext context) throws ProcessorException {
        /*
        1. figure out which files haven't changed and which are revised and which are new
        2. save file-to-archive for unchanged files, using same file as previous archive
        3. new file info row for changed and new files, then file-to-archive 
         */

        // save each file found in the deploy directory of the archive
        final File[] archiveFiles = DirectoryListerImpl.getFilesInDir(archive.getDeployDirectory());
        final Map<String, Long> fileNametoIdMap = new HashMap<String, Long>();
        for (final File file : archiveFiles) {
            fileNametoIdMap.put(file.getName(), saveFile(file, archive, context).getId());
        }

        archive.setFilenameToIdMap(fileNametoIdMap);
        return archive;
    }

    private FileInfo saveFile(final File file, final Archive archive, final QcContext context)
            throws ProcessorException {

        boolean fileAlreadyExists;
        final FileInfo fileInfo = new FileInfo();
        fileInfo.setFileSize(file.length());
        fileInfo.setFileName(file.getName());
        fileInfo.setFileSize(file.length());

        if(allowedAdditionalFile(file.getName())) {
            fileInfo.setDataLevel(0); // additional files like manifest.txt etc should not have data level set             
        }else {
            if (archive.getDataLevel() != null) {
                fileInfo.setDataLevel(archive.getDataLevel());
            }
        }

        if (archive.getDataTypeId() != null) {
            fileInfo.setDataTypeId(archive.getDataTypeId());
        }

        try {
            String md5 = MD5Validator.getFileMD5(file);
            fileInfo.setFileMD5(md5);
            // check if the file is already there for this archive; if so just update it
            Long fileId = commonFileInfoQueries.getFileId(fileInfo.getFileName(), archive.getId());
            fileAlreadyExists = (fileId != null);

            if (fileAlreadyExists) {
                fileInfo.setId(fileId);
                // if we are re-processing an archive, the file may be there already, but need to update all fields
                updateExistingFile(fileInfo);
            } else {
                Long previousFileId;
                Archive previousArchive = context.getExperiment().getPreviousArchiveFor(archive);
                if (previousArchive != null) {
                    boolean revision = true;
                    //if the filename and md5 is same, get file info id and update that record
                    // if not, create a new record in database
                    previousFileId = commonFileInfoQueries.getFileId(file.getName(), previousArchive.getId());
                    if (previousFileId != null) {
                        FileInfo previousFile = commonFileInfoQueries.getFileForFileId(previousFileId);
                        if (md5.equals(previousFile.getFileMD5())) {
                            revision = false;
                        }
                        if (revision) {
                            // this file is a revision of an existing file in the previous archive
                            // add a new entry in file_info table, set file_info.file_to_revision to the file id in the previous archive
                            fileInfo.setRevision(previousFileId);
                            fileId = addNewFile(fileInfo);
                        } else {
                            // this file is exactly the same as the file in previous archive
                            // do not add a new entry to file_info, do not set revision
                            fileId = previousFileId;
                            fileInfo.setId(previousFileId);
                            updateExistingFile(fileInfo);
                        }
                    } else {
                        // there was a previous archive, but this file was not a part of it, add a new file record
                        fileId = addNewFile(fileInfo);
                    }
                } else {
                    // no previous archives, add a new file record
                    fileId = addNewFile(fileInfo);
                }
            }

            if (fileId == null) {
                archive.setDeployStatus(Archive.STATUS_IN_REVIEW);
                throw new ProcessorException(new StringBuilder().append("Could not save '").append(file.getName()).append("' to database").toString());
            }

            // now add file-to-archive association with the file location
            Long fileArchiveId = fileArchiveQueries.addFileToArchiveAssociation(fileInfo, archive);
            diseaseFileArchiveQueries.addFileToArchiveAssociation(fileInfo, archive, true, fileArchiveId);
            //fileInfo.setFileLocation(archive.getDeployDirectory() + File.separator + file.getName());
        }
        catch (DataAccessException e) {
            // catch SQL exceptions so we know something went wrong
            archive.setDeployStatus(Archive.STATUS_IN_REVIEW);
            throw new ProcessorException(new StringBuilder().append("Error saving '").append(file.getName()).append("' to database: ").append(e.getMessage()).toString(), e);
        } catch (NoSuchAlgorithmException e) {
            archive.setDeployStatus(Archive.STATUS_IN_REVIEW);
            throw new ProcessorException(e.getMessage(), e);
        } catch (IOException e) {
            archive.setDeployStatus(Archive.STATUS_IN_REVIEW);
            throw new ProcessorException(e.getMessage(), e);
        }

        return fileInfo;
    }

    private boolean allowedAdditionalFile(final String fileName) {
        getAdditionalFileList();
        for(final String additionalFileName : additionalFileList) {
            if (additionalFileName.equalsIgnoreCase(fileName)){
                return true;                
            }
        }
        return false;
    }

    private void getAdditionalFileList() {
        if(additionalFileList == null) {
            additionalFileList = new ArrayList<String>();
            StringTokenizer tokenizer = new StringTokenizer(getAdditionalFiles(), ",");
            while (tokenizer.hasMoreTokens()) {
                additionalFileList.add(tokenizer.nextToken());
            }
        }
    }


    private void updateExistingFile(final FileInfo fileInfo) {
        commonFileInfoQueries.updateFile(fileInfo);
        diseaseFileInfoQueries.updateFile(fileInfo);
    }

    private Long addNewFile(final FileInfo fileInfo) {
        Long fileId;
        fileId = commonFileInfoQueries.addFile(fileInfo);
        fileInfo.setId(fileId);
        diseaseFileInfoQueries.addFile(fileInfo, true);
        return fileId;
    }

    public String getName() {
        return "archive file saver";
    }

    public void setCommonFileInfoQueries(final FileInfoQueries commonFileInfoQueries) {
        this.commonFileInfoQueries = commonFileInfoQueries;
    }

    public void setDiseaseFileInfoQueries(final FileInfoQueries diseaseFileInfoQueries) {
        this.diseaseFileInfoQueries = diseaseFileInfoQueries;
    }

    public void setFileArchiveQueries(final FileArchiveQueries fileArchiveQueries) {
        this.fileArchiveQueries = fileArchiveQueries;
    }

    public void setDiseaseFileArchiveQueries(final FileArchiveQueries diseaseFileArchiveQueries) {
        this.diseaseFileArchiveQueries = diseaseFileArchiveQueries;
    }

    public String getAdditionalFiles() {
        return additionalFiles;
    }

    public void setAdditionalFiles(final String additionalFiles) {
        this.additionalFiles = additionalFiles;
    }
    
}
