/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.standalone;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileInfoQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDDAO;
import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.web.FileInfoQueryRequest;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.Log4JLoggerDestination;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.Logger;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.LoggerImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRIDProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.FileCopier;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static gov.nih.nci.ncicb.tcga.dcc.ConstantValues.FILE_EXTENSION_XML;

/**
 * Utility to copy protected archives to the public location.
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ArchiveCopyStandalone {

    private static final String BCR_ARG = "-bcr";
    private static final String MAF_ARG = "-maf";
    private static final String DRY_RUN_ARG = "-dryRun";
    private static final String USAGE = "Usage: ArchiveCopyStandalone [" + BCR_ARG + "|" + MAF_ARG + "] [" + DRY_RUN_ARG + "]";

    private Logger logger = new LoggerImpl();
    private ArchiveQueries dccCommonArchiveQueries;
    private ArchiveQueries diseaseArchiveQueries;
    private FileInfoQueries dccCommonFileInfoQueries;
    private FileInfoQueries diseaseFileInfoQueries;
    private UUIDDAO diseaseUUIDQueries;
    private UUIDDAO dccCommonUUIDQueries;
    private BCRIDProcessor bcrIdProcessor;
    private String actualDeployRootLocation;
    private String mountDeployRootLocation;
    private boolean dryRun;
    private static final String APP_CONTEXT_FILE_NAME = "applicationContext.xml";

    /**
     * Copy archives to the public location, making the necessary database and filesystem changes.
     * <p/>
     * Right now the only archive type that is supported is "bcr", meaning here all available protected 'bio' archives.
     *
     * @param args the archive type to copy to the public location
     */
    public static void main(final String[] args) {


        if (args.length == 0 || !hasValidArgs(args)) {

            System.out.println(USAGE);
            System.exit(-1);

        } else {
            final long startTime = System.currentTimeMillis();
            final ApplicationContext appContext = new ClassPathXmlApplicationContext(APP_CONTEXT_FILE_NAME);
            final ArchiveCopyStandalone archiveCopyStandalone = (ArchiveCopyStandalone)appContext.getBean("archiveCopyStandalone");
            archiveCopyStandalone.getLogger().addDestination(new Log4JLoggerDestination(archiveCopyStandalone.getClass().toString()));

            try{
                final List<String> argList = Arrays.asList(args);
                long noOfFilesCopied = 0;
                if(argList.contains(DRY_RUN_ARG)) {

                    archiveCopyStandalone.getLogger().log(Level.INFO, "This is a DRY RUN, no actual file system and Database change will be made.");
                    archiveCopyStandalone.setDryRun(true);
                }

                if(argList.contains(BCR_ARG)) {
                    noOfFilesCopied = archiveCopyStandalone.copyBCRArchivesFromProtectedToPublic();
                }

                if(argList.contains(MAF_ARG)){
                    noOfFilesCopied = archiveCopyStandalone.copyMafArchivesFromProtectedToPublic();
                }
                archiveCopyStandalone.getLogger().log(Level.INFO, "Total archives copied : "+noOfFilesCopied+ ". Total execution time: "+ ((System.currentTimeMillis() - startTime)/1000)+" secs.");
            }catch(Exception e){
                archiveCopyStandalone.getLogger().log(Level.ERROR,e.getMessage());
                e.printStackTrace();
            }
        }
    }


    /**
     * Return <code>true</code> if the arguments are valid, <code>false</code> otherwise.
     *
     * @param args the arguments to validate
     * @return <code>true</code> if the arguments are valid, <code>false</code> otherwise
     */
    private static boolean hasValidArgs(final String[] args) {

        boolean result = true;

        for(final String arg : args) {

            if(!BCR_ARG.equals(arg) && !MAF_ARG.equals(arg) && !DRY_RUN_ARG.equals(arg)) {
                result = false;
            }
        }

        return result;
    }

    protected long copyBCRArchivesFromProtectedToPublic(){
        final List<Archive> dccCommonBioArchives = getAllDccCommonAvailableProtectedBioArchives();

        // Update the FS
        final Set<Long> successfullyCopiedArchiveIds = copyArchivesToPublicLocation(dccCommonBioArchives);

        // Update all disease schemas that need to be updated
        final Map<String, Set<Long>> diseaseAbbreviations = getDiseaseAbbreviationsToArchiveIds(dccCommonBioArchives, successfullyCopiedArchiveIds);
        getLogger().log(Level.INFO, "Found disease(s) in dccCommon, for which archives have successfully been copied to the public location: " + diseaseAbbreviations.keySet());

        // Update the DB
        updateProtectedAvailableBioArchivesLocationToPublic(successfullyCopiedArchiveIds, diseaseAbbreviations);
        createPatientUUIDFileAssociations(diseaseAbbreviations);

        return successfullyCopiedArchiveIds.size();

    }


    /**
     * Return all available, protected 'bio' archives from DccCommon schema.
     *
     * @return all available, protected 'bio' archives from DccCommon schema
     */
    private List<Archive> getAllDccCommonAvailableProtectedBioArchives() {

        final List<Archive> result = getDccCommonArchiveQueries().getAllAvailableProtectedBioArchives();
        getLogger().log(Level.INFO, "Found " + result.size() + " available protected bio archive(s) in dccCommon.");

        return result;
    }

    /**
     * Copies maf archives from restricted access to open access dir and updates the references in common and disease schema.
     * @return   number of files copied
     */
    protected long copyMafArchivesFromProtectedToPublic(){
        // get the protected maf archives
        logger.log(Level.INFO," Reading protected maf archives location from db...");
        final List<Archive> protectedMafArchives = dccCommonArchiveQueries.getProtectedMafArchives();
        logger.log(Level.INFO," Found "+ protectedMafArchives.size()+ " maf archives.");

        // copy the archives to public location
        logger.log(Level.INFO,"Copying maf archives to public location");
        final Set<Long> copiedArchiveIds = copyArchivesToPublicLocation(protectedMafArchives);

        if(!isDryRun()){

            final Map<String, Set<Long>> diseaseAbbreviations = getDiseaseAbbreviationsToArchiveIds(protectedMafArchives,copiedArchiveIds);

            for(final String disease: diseaseAbbreviations.keySet()){
                DiseaseContextHolder.setDisease(disease);
                logger.log(Level.INFO,"Updating disease schema["+disease+"] maf archives "+diseaseAbbreviations.get(disease)+"locations to public...");
                diseaseArchiveQueries.updateArchivesLocationToPublic(diseaseAbbreviations.get(disease));
                logger.log(Level.INFO,"Updating disease schema["+disease+"] maf file locations to public...");
                diseaseFileInfoQueries.updateArchiveFilesLocationToPublic(diseaseAbbreviations.get(disease));
            }
            logger.log(Level.INFO,"Updating common schema maf archives "+copiedArchiveIds+" locations to public...");
            dccCommonArchiveQueries.updateArchivesLocationToPublic(copiedArchiveIds);
            logger.log(Level.INFO, "Updating common schema maf file locations  to public...");
            dccCommonFileInfoQueries.updateArchiveFilesLocationToPublic(copiedArchiveIds);

        }

        return copiedArchiveIds.size();
    }

    /**
     * Update the location of all protected, available 'bio' archives to the public location
     *
     * @param successfullyCopiedArchiveIds archive Ids of all archives that were successfully copied to the public location
     * @param diseaseAbbreviations disease abbreviations for all archives that have successfully been copied to the public location
     */
    protected void updateProtectedAvailableBioArchivesLocationToPublic(final Set<Long> successfullyCopiedArchiveIds,
                                                                       final Map<String, Set<Long>> diseaseAbbreviations) {

        getLogger().log(Level.INFO, "Updating available protected bio archive(s) deploy location to public ...");

        for(final String diseaseAbbreviation : diseaseAbbreviations.keySet()) {

            getLogger().log(Level.INFO, "Updating Disease schema: " + diseaseAbbreviation + " ...");

            if(!isDryRun()) {
                DiseaseContextHolder.setDisease(diseaseAbbreviation);
                final Set<Long> successfullyCopiedDiseaseArchiveIds = diseaseAbbreviations.get(diseaseAbbreviation);
                getDiseaseArchiveQueries().updateArchivesLocationToPublic(successfullyCopiedDiseaseArchiveIds);
                getDiseaseFileInfoQueries().updateArchiveFilesLocationToPublic(successfullyCopiedDiseaseArchiveIds);
            }
        }
        // Update DccCommon schema
        getLogger().log(Level.INFO, "Updating DccCommon schema ...");

        if(!isDryRun()) {
            getDccCommonArchiveQueries().updateArchivesLocationToPublic(successfullyCopiedArchiveIds);
            getDccCommonFileInfoQueries().updateArchiveFilesLocationToPublic(successfullyCopiedArchiveIds);
        }
    }

    /**
     * Return a {@link Map} of all disease abbreviations to archive Ids from the given list of archives,
     * ignoring archives that were not successfully copied to the public location.
     *
     * @param dccCommonArchives the list of archives to extract disease abbreviations to archive Ids from
     * @param successfullyCopiedArchiveIds archive Ids of all archives that were successfully copied to the public location
     * @return a {@link Map} of all disease abbreviations to archive Ids from the given list of archives
     */
    private Map<String, Set<Long>> getDiseaseAbbreviationsToArchiveIds(final List<Archive> dccCommonArchives,
                                                                        final Set<Long> successfullyCopiedArchiveIds) {

        final Map<String, Set<Long>> result = new HashMap<String, Set<Long>>();
        final String errorMessagePrefix = "Error while getting the disease abbreviations from DccCommon schema: ";

        if (dccCommonArchives != null) {

            for (final Archive archive : dccCommonArchives) {

                if (archive != null) {

                    final String disease = archive.getTumorType();

                    if(disease != null) {

                        if(result.get(disease) == null) {
                            result.put(disease, new HashSet<Long>());
                        }

                        final Long archiveId = archive.getId();
                        
                        if(archiveId > 0) {

                            if(successfullyCopiedArchiveIds.contains(archiveId)) { // Ignore archives that were not successfully copied to the public location
                                result.get(disease).add(archiveId);
                            }

                        } else {
                            getLogger().log(Level.ERROR, errorMessagePrefix + "the provided list of archives contains an archive for which the archive Id is not set.");
                        }

                    } else {
                        getLogger().log(Level.ERROR, errorMessagePrefix + "the provided list of archives contains an archive for which the tumor type is not set.");
                    }

                } else {
                    getLogger().log(Level.ERROR, errorMessagePrefix + "the provided list of archives contains an archive that is null.");
                }
            }

        } else {
            getLogger().log(Level.ERROR, errorMessagePrefix + "the provided list of archives is null.");
        }

        return result;
    }

    /**
     * Copy the given archives to their respective public location
     * and return the Ids of the archives that have successfully been copied.
     * <p/>
     * Note 1: no database changes will be made.
     * Note 2: if either the tar/tar.gz, md5 or exploded directory was not successfully copied, the associated archive Id will not be returned
     *
     * @param archives the archives to copy to their respective public location
     * @return the Ids of the archives that have successfully been copied to the public location
     */
    protected Set<Long> copyArchivesToPublicLocation(final List<Archive> archives) {

        final Set<Long> result = new HashSet<Long>();

        getLogger().log(Level.INFO, "Starting file system copy of all archives to the public location ...");

        // For each archive, copy the tar/tar.gz, md5 and exploded archive to the public location, only if all 3 exist in the protected location
        // and none of the 3 exist in the public location
        for (int i = 0; i < archives.size(); i++) {

            final Archive archive = archives.get(i);

            getLogger().log(Level.INFO, "Starting copy of archive #" + (i + 1) + " ...");

            if (archive != null) {

                final Long archiveId = archive.getId();
                String tarOrTarGzProtectedDeployLocation = getDeployLocation(archive.getDeployLocation());
                if (StringUtil.isValidTarOrTarGzProtectedDeployLocation(tarOrTarGzProtectedDeployLocation)) {

                    final String md5ProtectedDeployLocation = StringUtil.getMd5ProtectedDeployLocation(tarOrTarGzProtectedDeployLocation);
                    final String explodedProtectedDeployLocation = StringUtil.getExplodedProtectedDeployLocation(tarOrTarGzProtectedDeployLocation);

                    // Verify all 3 protected files exist
                    final File tarOrTarGzProtectedDeployLocationFile = new File(tarOrTarGzProtectedDeployLocation);
                    final File md5ProtectedDeployLocationFile = new File(md5ProtectedDeployLocation);
                    final File explodedProtectedDeployLocationFile = new File(explodedProtectedDeployLocation);

                    if (!tarOrTarGzProtectedDeployLocationFile.exists()) {
                        getLogger().log(Level.ERROR, "Protected (Source) File '" + tarOrTarGzProtectedDeployLocation + "' [Id:" + archiveId + "] does not exist - tar/tar.gz, md5 and exploded archive will not be copied to the public location");
                    }

                    if (!md5ProtectedDeployLocationFile.exists()) {
                        getLogger().log(Level.ERROR, "Protected (Source) File '" + md5ProtectedDeployLocation + "' [Id:" + archiveId + "] does not exist - tar/tar.gz, md5 and exploded archive will not be copied to the public location");
                    }

                    if (!explodedProtectedDeployLocationFile.exists()) {
                        getLogger().log(Level.ERROR, "Protected (Source) File '" + explodedProtectedDeployLocationFile + "' [Id:" + archiveId + "] does not exist - tar/tar.gz, md5 and exploded archive will not be copied to the public location");
                    }

                    final boolean allThreeProtectedFilesExist = tarOrTarGzProtectedDeployLocationFile.exists()
                            && md5ProtectedDeployLocationFile.exists()
                            && explodedProtectedDeployLocationFile.exists();

                    // Verifying all 3 public files do not already exist
                    final String tarOrTarGzPublicDeployLocation = StringUtil.getPublicDeployLocation(tarOrTarGzProtectedDeployLocation);
                    final String md5PublicDeployLocation = StringUtil.getPublicDeployLocation(md5ProtectedDeployLocation);
                    final String explodedPublicDeployLocation = StringUtil.getPublicDeployLocation(explodedProtectedDeployLocation);

                    final File tarOrTarGzPublicDeployLocationFile = new File(tarOrTarGzPublicDeployLocation);
                    final File md5PublicDeployLocationFile = new File(md5PublicDeployLocation);
                    final File explodedPublicDeployLocationFile = new File(explodedPublicDeployLocation);

                    if (allThreeProtectedFilesExist) {
                        // Copy all three protected files to their respective public location
                        final boolean tarOrTarGzCopiedSuccessfully = copyFileOrDirectory(tarOrTarGzProtectedDeployLocationFile, tarOrTarGzPublicDeployLocationFile, archiveId);
                        final boolean md5CopiedSuccessfully = copyFileOrDirectory(md5ProtectedDeployLocationFile, md5PublicDeployLocationFile, archiveId);
                        final boolean explodedDirectoryCopiedSuccessfully = copyFileOrDirectory(explodedProtectedDeployLocationFile, explodedPublicDeployLocationFile, archiveId);

                        // Add archive Id to the result only if all 3 files/directory were successfully copied to the public location
                        if(tarOrTarGzCopiedSuccessfully
                                && md5CopiedSuccessfully
                                && explodedDirectoryCopiedSuccessfully) {
                            result.add(archiveId);
                        }
                    }

                } else {
                    getLogger().log(Level.ERROR, "Archive file '" + tarOrTarGzProtectedDeployLocation + "' [Id:" + archiveId
                            + "] is not a valid tar or tar.gz protected deploy location - tar/tar.gz, md5 and exploded archive will not be copied to the public location");
                }

            } else {
                getLogger().log(Level.ERROR, "Archive is null - tar/tar.gz, md5 and exploded archive will not be copied to the public location");
            }
        }

        return result;
    }

    /**
     * For each given archive, create the patient uuid to file associations in both common and disease schemas.
     *
     * @param diseaseAbbreviations disease abbreviations for all archives that have successfully been copied to the public location
     */
    protected void createPatientUUIDFileAssociations(final Map<String, Set<Long>> diseaseAbbreviations) {

        for (final String diseaseAbbreviation : diseaseAbbreviations.keySet()) {

            getLogger().log(Level.INFO, "Adding patient UUID and file id relationships for disease " + diseaseAbbreviation + " ...");
            DiseaseContextHolder.setDisease(diseaseAbbreviation);

            // Get the files for each archive
            for (final Long archiveId : diseaseAbbreviations.get(diseaseAbbreviation)) {

                getLogger().log(Level.INFO, "Adding patient UUID and file id relationships for archive " + archiveId + " ...");

                final FileInfoQueryRequest fileInfoQueryRequest = new FileInfoQueryRequest();
                fileInfoQueryRequest.setArchiveId(archiveId.intValue());

                final Collection<FileInfo> allFiles = getDccCommonFileInfoQueries().getFilesForArchive(fileInfoQueryRequest);
                final Collection<FileInfo> bcrXMLFiles = filterXMLFiles(allFiles);
                final List<Object[]> patientUUIDData = new ArrayList<Object[]>();

                for (final FileInfo fileInfo : bcrXMLFiles) {
                    final String fileLocation = getDeployLocation(fileInfo.getFileLocation());
                    getLogger().log(Level.INFO, "Adding patient UUID and file id relationships for file " + new File(fileLocation).getPath() + " ...");

                    try {

                        final File file = new File(fileLocation);

                        final String patientUUID = getBcrIdProcessor().getPatientUUIDfromFile(file);

                        if (StringUtils.isBlank(patientUUID)) {
                            getLogger().log(Level.ERROR, "Error reading " + fileLocation + ". Patient uuid is null/empty. Archive id [" + archiveId + "," + fileInfo.getId() + "] file id relationship will not be added into the db");
                        } else {
                            getLogger().log(Level.INFO, "Adding patient UUID [ " + patientUUID + "] file id [" + fileInfo.getId() + "] ...");

                            final Object[] data = new Object[4];
                            data[0] = patientUUID;
                            data[1] = fileInfo.getId();
                            data[2] = patientUUID;  // doing it twice because the query needs it
                            data[3] = fileInfo.getId();
                            patientUUIDData.add(data);
                        }

                    } catch (final Exception e) {
                        getLogger().log(Level.ERROR, "Error reading " + fileLocation + ". Archive id [" + archiveId + "," + fileInfo.getId() + "] file id relationship will not be added into the db: " + e.toString());
                    }
                }

                if (!isDryRun()) {
                    getDiseaseUUIDQueries().addParticipantFileUUIDAssociation(patientUUIDData);
                    getDccCommonUUIDQueries().addParticipantFileUUIDAssociation(patientUUIDData);
                }
            }
        }
    }

    /**
     * Return a new {@link Collection} without non-XML files
     *
     * @param fileInfos the {@link Collection} of files to filter
     * @return the given {@link Collection} without non-XML files
     */
    private Collection<FileInfo> filterXMLFiles(final Collection<FileInfo> fileInfos) {

        final Collection<FileInfo> result = new ArrayList<FileInfo>();

        for(final FileInfo fileInfo : fileInfos) {

            if(fileInfo != null) {

                final String filename = fileInfo.getFileName();

                if(filename != null && filename.toLowerCase().endsWith(FILE_EXTENSION_XML)) {
                    result.add(fileInfo);
                }
            }
        }

        return result;
    }

    /**
     * Copy the given source file or directory to a given destination.
     *
     * Note 1: The copy will actually happen only if it is not a dry run.
     * Note 2: if it is a dry run, it will return <code>true</code>
     *
     * @param sourceFileOrDirectory      the source file or directory
     * @param destinationFileOrDirectory the destination file or directory
     * @param archiveId                  the Id of the archive the file is associated to
     * @return <code>true</code> if the copy was successful, <code>false</code> otherwise
     */
    private boolean copyFileOrDirectory(final File sourceFileOrDirectory,
                                     final File destinationFileOrDirectory,
                                     final Long archiveId) {
        boolean result = true;

        try {
            getLogger().log(Level.INFO, new StringBuilder("Copying '")
                    .append(sourceFileOrDirectory)
                    .append("' [archive Id:")
                    .append(archiveId)
                    .append("] to '")
                    .append(destinationFileOrDirectory)
                    .append("'")
                    .toString());

            deleteFileOrDirectoryIfExists(destinationFileOrDirectory);

            if(!isDryRun()) {
                FileCopier.copyFileOrDirectory(sourceFileOrDirectory, destinationFileOrDirectory);
            }

        } catch (final IOException e) {

            result = false;
            getLogger().log(Level.ERROR, new StringBuilder("Error while copying '")
                    .append(sourceFileOrDirectory)
                    .append("' to '")
                    .append(destinationFileOrDirectory)
                    .append("': ")
                    .append(e.getMessage())
                    .toString());
        }

        return result;
    }

    protected String getDeployLocation(String deployLocation){

        // replace the actual deploy root location with mounted root location
        if(!StringUtils.isBlank(actualDeployRootLocation) && !StringUtils.isBlank(mountDeployRootLocation) ){
            deployLocation = deployLocation.replace(actualDeployRootLocation,mountDeployRootLocation);
        }
        return deployLocation;
    }


    /**
     * Delete the given file or directory if it exists.
     *
     * Note: The deletion will actually happen only if it is not a dry run.
     *
     * @param fileOrDirectory the file or directory to delete
     * @return <code>true</code> if the file or directory does not exist or was successfully deleted, <code>false</code> otherwise
     */
    private boolean deleteFileOrDirectoryIfExists(final File fileOrDirectory) {

        boolean result = true;

        if (fileOrDirectory.exists()) {

            getLogger().log(Level.INFO, "File '" + fileOrDirectory + "' already exists - deleting now ...");

            if(!isDryRun()) {

                if(fileOrDirectory.isDirectory()) {

                    try {
                        FileUtils.deleteDirectory(fileOrDirectory);
                    } catch (final IOException e) {
                        result = false;
                        getLogger().log(Level.ERROR, "Directory '" + fileOrDirectory + "' could not be deleted");
                    }

                } else {

                    result = fileOrDirectory.delete();

                    if(!result) {
                        getLogger().log(Level.ERROR, "File '" + fileOrDirectory + "' could not be deleted");
                    }
                }
            }
        }

        return result;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(final Logger logger) {
        this.logger = logger;
    }

    public ArchiveQueries getDccCommonArchiveQueries() {
        return dccCommonArchiveQueries;
    }

    public void setDccCommonArchiveQueries(final ArchiveQueries dccCommonArchiveQueries) {
        this.dccCommonArchiveQueries = dccCommonArchiveQueries;
    }

    public ArchiveQueries getDiseaseArchiveQueries() {
        return diseaseArchiveQueries;
    }

    public void setDiseaseArchiveQueries(final ArchiveQueries diseaseArchiveQueries) {
        this.diseaseArchiveQueries = diseaseArchiveQueries;
    }

    public FileInfoQueries getDccCommonFileInfoQueries() {
        return dccCommonFileInfoQueries;
    }

    public void setDccCommonFileInfoQueries(FileInfoQueries dccCommonFileInfoQueries) {
        this.dccCommonFileInfoQueries = dccCommonFileInfoQueries;
    }

    public FileInfoQueries getDiseaseFileInfoQueries() {
        return diseaseFileInfoQueries;
    }

    public void setDiseaseFileInfoQueries(FileInfoQueries diseaseFileInfoQueries) {
        this.diseaseFileInfoQueries = diseaseFileInfoQueries;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(final boolean dryRun) {
        this.dryRun = dryRun;
    }

    public UUIDDAO getDiseaseUUIDQueries() {
        return diseaseUUIDQueries;
    }

    public void setDiseaseUUIDQueries(final UUIDDAO diseaseUUIDQueries) {
        this.diseaseUUIDQueries = diseaseUUIDQueries;
    }

    public UUIDDAO getDccCommonUUIDQueries() {
        return dccCommonUUIDQueries;
    }

    public void setDccCommonUUIDQueries(final UUIDDAO dccCommonUUIDQueries) {
        this.dccCommonUUIDQueries = dccCommonUUIDQueries;
    }

    public BCRIDProcessor getBcrIdProcessor() {
        return bcrIdProcessor;
    }

    public void setBcrIdProcessor(final BCRIDProcessor bcrIdProcessor) {
        this.bcrIdProcessor = bcrIdProcessor;
    }

    public String getActualDeployRootLocation() {
        return actualDeployRootLocation;
    }

    public void setActualDeployRootLocation(String actualDeployRootLocation) {
        this.actualDeployRootLocation = actualDeployRootLocation;
    }

    public String getMountDeployRootLocation() {
        return mountDeployRootLocation;
    }

    public void setMountDeployRootLocation(String mountDeployRootLocation) {
        this.mountDeployRootLocation = mountDeployRootLocation;
    }
}
