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
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Visibility;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.MD5Validator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ManifestValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.ArchiveCompressor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.FileCopier;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ExperimentDAO;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.VisibilityQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DataTypeQueries;
import org.apache.commons.io.IOUtils;
import org.springframework.dao.DataAccessException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Deploys an archive by moving all required files into distro area, then creating a new compressed archive. To setup:
 * <p/>
 * pre-processing steps: AddFileStep for any files that need to be added to archives before deployment. SdrfRewriter to
 * modify SDRF to refer to only current archives
 * <p/>
 * post-processing steps:
 * <p/>
 * ArchiveFileSaver (to save the file_info rows into the db)  must be first to get all files in (order of below does not
 * matter) MafFileProcessor (to insert maf data into the db) TraceFileProcessor (to insert trace relationships into the
 * db) SdrfProcessor (to insert barcodes and file-barcode associations) ClinicalXmlProcessor (to insert barcodes) --
 * this also does biospecimen_to_archive!
 * <p/>
 * Above should set archive status to "in review" if there are errors!
 *
 * @author Jessica Chen Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class ArchiveDeployer extends AbstractProcessor<Archive, Archive> {

    protected static final String LOCATION_PUBLIC = "Public";
    private ArchiveCompressor archiveCompressor;
    private ManifestParser manifestParser;
    private String publicDeployRoot;
    private String privateDeployRoot;
    private VisibilityQueries visibilityQueries;
    private DataTypeQueries dataTypeQueries;
    private ArchiveQueries archiveQueries;
    private ExperimentDAO experimentDAO;
    private static final String NEWLINE = "\n";

    protected Archive doWork(final Archive archive, final QcContext context) throws ProcessorException {
        context.setArchive(archive);
        try {
            final String originalDeployLocation = archive.getDeployLocation();
            final Visibility archiveVisibility = visibilityQueries.getVisibilityForArchive(archive);
            final File deployDirectory = getDeployDirectory(archive);
            deployArchiveToDirectory(archive, deployDirectory, false);

            if (Archive.TYPE_MAGE_TAB.equals(archive.getArchiveType()) && !archiveVisibility.isIdentifiable()) {
                archive.setSecondaryDeployLocation(originalDeployLocation);
                deployMageTabToSecondaryLocation(archive, context);
            }

            // Set archive deploy status to Deployed. Once all the archives for the given experiment is deployed then
            // the archive deploy status will be set to Available.
            archive.setDeployStatus(Archive.STATUS_DEPLOYED);
            return archive;

        } catch (DataAccessException e) {
            archive.setDeployStatus(Archive.STATUS_IN_REVIEW);
            throw new ProcessorException("Unexpected error while deploying archive: " + e.getMessage(), e);
        }
    }

    protected void deployMageTabToSecondaryLocation(final Archive archive, final QcContext context) throws ProcessorException {
        boolean hasProtectedArchives = false;                      
        for (final Archive experimentArchive : context.getExperiment().getArchives()) {
        	
        	final Visibility visibility = visibilityQueries.getVisibilityForArchive(experimentArchive);
        	
        	if (visibility != null){
        		  if (visibility.isIdentifiable()) {
                      hasProtectedArchives = true;
                  }
        	}else{
        		 throw new ProcessorException("Unexpected data type and level combination, please contact the DCC. " + archive.getRealName());
        	}        	          
        }
        if (hasProtectedArchives) {
            // need to deploy to protected location too
            final File secondaryDeployDirectory = experimentDAO.getProtectedDeployDirectoryPath(archive);
            deployArchiveToDirectory(archive, secondaryDeployDirectory, true);
        }
    }

    /**
     * Deploy the given archive under the given deploy directory,
     * and update the archive's deploy directory.
     *
     * @param archive the archive to deploy
     * @param deployDirectory the directory to deploy under which to deploy the archive
     * @param isSecondaryLocation whether this is a secondary deployment location
     * @return the deployed archive file
     * @throws ProcessorException
     */
    private File deployArchiveToDirectory(final Archive archive,
                                          final File deployDirectory,
                                          final boolean isSecondaryLocation)
            throws ProcessorException {

        if (deployDirectory == null) {
            archive.setDeployStatus(Archive.STATUS_IN_REVIEW);
            throw new ProcessorException("No deploy directory found for archive " + archive.getRealName());
        }

        //noinspection ResultOfMethodCallIgnored
        deployDirectory.mkdirs();
        if (!deployDirectory.exists()) {
            archive.setDeployStatus(Archive.STATUS_IN_REVIEW);
            throw new ProcessorException(new StringBuilder().append("Could not make deploy directory '").append(deployDirectory).append("'").toString());
        }

        final List<File> files = deployFiles(archive, deployDirectory, isSecondaryLocation);

        final File archiveFile = createArchive(archive, deployDirectory, files);

        // Update the deploy directory of the Archive object
        try {
            if(isSecondaryLocation) {
                archive.setSecondaryDeployLocation(archiveFile.getCanonicalPath());
                archiveQueries.updateSecondaryDeployLocation(archive);
            } else {
                archive.setDeployLocation(archiveFile.getCanonicalPath());
            }
        } catch (final IOException e) {
            archive.setDeployStatus(Archive.STATUS_IN_REVIEW);
            throw new ProcessorException(new StringBuilder("Could not set ").append(isSecondaryLocation ? "secondary " : "").append("deploy location in archive: ").append(e.getMessage()).toString(), e);
        }

        createMd5(archive, deployDirectory, archiveFile, isSecondaryLocation);
        return archiveFile;
    }

    /**
     * Create an MD5 file for the given archive file.
     *
     * @param archive the archive that the file belongs to
     * @param deployDirectory the path to the MD5 file (without its extension)\
     * @param archiveFile the file for which to get the MD5
     * @param isSecondaryLocation whether this is for a secondary deployment location or not
     * @return the MD5 file
     * @throws ProcessorException
     */
    private File createMd5(final Archive archive,
                           final File deployDirectory,
                           final File archiveFile,
                           boolean isSecondaryLocation) throws ProcessorException {

        final String fileExtension = isSecondaryLocation?archive.getSecondaryDeployedArchiveExtension():archive.getDeployedArchiveExtension();
        final File md5File = new File(deployDirectory + fileExtension + ".md5");
        FileWriter writer = null;
        try {
            final String archiveChecksum = MD5Validator.getFileMD5(archiveFile);
            //noinspection IOResourceOpenedButNotSafelyClosed
            writer = new FileWriter(md5File);
            writer.write(archiveChecksum);
            writer.write("  ");
            writer.write(archiveFile.getName());
        }
        catch (IOException e) {
            archive.setDeployStatus(Archive.STATUS_IN_REVIEW);
            throw new ProcessorException(new StringBuilder().append("Error generating MD5 checksum for archive: ").append(e.getMessage()).toString());
        }
        catch (NoSuchAlgorithmException e) {
            archive.setDeployStatus(Archive.STATUS_IN_REVIEW);
            throw new ProcessorException(new StringBuilder().append("Error generating MD5 checksum for archive: ").append(e.getMessage()).toString());
        } finally {
            IOUtils.closeQuietly(writer);
        }
        return md5File;
    }

    private File createArchive(final Archive archive, final File deployDirectory, final List<File> files)
            throws ProcessorException {
        File archiveFile = null;
        try {
            Boolean compress = archive.isDataTypeCompressed();
            // If data type doesn't require the archive to be compressed then check the
            // archive extension, if the archive extension is .tar.gz then compress the archive
            // otherwise create archive with just tar extension
            if(!compress){
                compress = archive.isDepositedArchiveCompressed();
            }
            archiveFile = archiveCompressor.createArchive(files, archive.getArchiveName(), deployDirectory.getParentFile(),compress);
        }
        catch (IOException e) {
            archive.setDeployStatus(Archive.STATUS_IN_REVIEW);
            throw new ProcessorException(new StringBuilder().append("Could not create compressed archive ").append(archiveFile).append(": ").append(e.getMessage()).toString(), e);
        }
        return archiveFile;
    }

    /**
     * Deploy files from the given archive to the given deploy directory.
     *
     * @param archive the archive to deploy
     * @param deployDirectory the directory to deploy the archive to
     * @param isSecondaryLocation whether this is a secondary location deployment or not
     * @return the list of deployed files
     * @throws ProcessorException
     */
    private List<File> deployFiles(final Archive archive,
                                   final File deployDirectory,
                                   final boolean isSecondaryLocation) throws ProcessorException {

        final List<File> deployedFiles = new ArrayList<File>();
        final Archive latestArchive = archiveQueries.getLatestVersionArchive(archive);

        final File archiveDirectory;
        if(isSecondaryLocation) {
            archiveDirectory = new File(archive.getSecondaryDeployDirectory());
        } else {
            archiveDirectory = new File(archive.getDeployDirectory());
        }

        final File manifest = new File(archiveDirectory, ManifestValidator.MANIFEST_FILE);
        if (!manifest.exists()) {
            archive.setDeployStatus(Archive.STATUS_IN_REVIEW);
            throw new ProcessorException(new StringBuilder().append("Archive is missing its ").
                    append(ManifestValidator.MANIFEST_FILE).append(" file").toString());
        }
        try {
            final Map<String, String> manifestEntries = manifestParser.parseManifest(manifest);
            for (final String file : manifestEntries.keySet()) {
                // need to find out where this file is kept.  either in archive or in previous archive.
                final File sourceFile = getLocation(file, archive, latestArchive);
                if (sourceFile == null) {
                    throw new ProcessorException(new StringBuilder().append("File '").
                            append(file).append("' was not found in the archive and is not in the previous version of the archive").toString());
                }
                // copy the file to the new location
                // get the final MD5 for the file
                String md5hash = MD5Validator.getFileMD5(sourceFile);
                manifestEntries.put(file, md5hash);

                // copy the file to the deploy location, and add it to the list of deployed files
                deployedFiles.add(FileCopier.copy(sourceFile, deployDirectory));
            }

            // now copy manifest
            File deployedManifest = FileCopier.copy(manifest, deployDirectory);
            // update manifest in deployed location in case of new MD5s
            updateManifest(deployedManifest, manifestEntries);
            deployedFiles.add(deployedManifest);
        }
        catch (IOException e) {
            archive.setDeployStatus(Archive.STATUS_IN_REVIEW);
            throw new ProcessorException(e.getMessage(), e);
        }
        catch (ParseException e) {
            archive.setDeployStatus(Archive.STATUS_IN_REVIEW);
            throw new ProcessorException(e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            archive.setDeployStatus(Archive.STATUS_IN_REVIEW);
            throw new ProcessorException(e.getMessage(), e);
        }
        return deployedFiles;
    }

    private void updateManifest(final File manifest, final Map<String, String> manifestEntries) throws IOException {

        FileWriter writer = null;
        try {
            //noinspection IOResourceOpenedButNotSafelyClosed
            writer = new FileWriter(manifest);
            for (final String filename : manifestEntries.keySet()) {
                String md5 = manifestEntries.get(filename);
                writer.write(md5 + "  " + filename + NEWLINE);
            }
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    private File getLocation(final String filename, final Archive archive, final Archive latestArchive) {
        File file = new File(archive.getDeployDirectory(), filename);
        if (!file.exists() && latestArchive != null) {
            file = new File(latestArchive.getDeployDirectory(), filename);
        }
        return file;
    }

    protected File getDeployDirectory(final Archive archive) {
        return experimentDAO.getDeployDirectoryPath(archive);
    }

    public String getName() {
        return "archive deployer";
    }

    public void setVisibilityQueries(final VisibilityQueries visibilityQueries) {
        this.visibilityQueries = visibilityQueries;
    }

    public void setPublicDeployRoot(final String publicDeployRoot) {
        this.publicDeployRoot = publicDeployRoot;
    }

    public void setPrivateDeployRoot(final String privateDeployRoot) {
        this.privateDeployRoot = privateDeployRoot;
    }

    public void setDataTypeQueries(final DataTypeQueries dataTypeQueries) {
        this.dataTypeQueries = dataTypeQueries;
    }

    public void setManifestParser(final ManifestParser manifestParser) {
        this.manifestParser = manifestParser;
    }

    public void setArchiveQueries(final ArchiveQueries archiveQueries) {
        this.archiveQueries = archiveQueries;
    }

    public void setFileCompressor(final ArchiveCompressor archiveCompressor) {
        this.archiveCompressor = archiveCompressor;
    }

    public ExperimentDAO getExperimentDAO() {
        return experimentDAO;
    }

    public void setExperimentDAO(ExperimentDAO experimentDAO) {
        this.experimentDAO = experimentDAO;
    }
}
