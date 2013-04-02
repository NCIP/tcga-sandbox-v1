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
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContent;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContentImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedContentNavigator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedFileParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ArchiveNameValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.DirectoryListerImpl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Specialized checker for experiments that (may) require mage-tabs.  Checks that the experiment is complete and ready
 * for validation.
 * NOTE: this is used in ExperimentCheckerTest as well
 *
 * @author Jessica Walton
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class MageTabExperimentChecker extends AbstractProcessor<Experiment, Boolean> {

    private static final String MAF_EXTENSION = ".maf";

    private ArchiveQueries archiveQueries;
    private ManifestParser manifestParser;
    private CenterQueries centerQueries;

    /**
     * Return {@code true} if the given experiment requires a mage-tab archive by default, {@code false} otherwise.
     *
     * @param experiment the {@link Experiment}
     * @return  {@code true} if the given experiment requires a mage-tab archive by default, {@code false} otherwise
     * @throws IOException if an I/O exception occurs
     * @throws ParseException if a parsing exception occurs
     */
    protected boolean experimentRequiresMageTab(final Experiment experiment)
            throws IOException, ParseException {

        return centerQueries.doesCenterRequireMageTab(experiment.getCenterName(), experiment.getType());
    }

    /**
     * Return whether an experiment can but is not required to submit a mage-tab archive
     *
     * @param experiment the {@link Experiment}
     * @return whether an experiment can but is not required to submit a mage-tab archive
     * @throws IOException if an I/O exception occurs
     * @throws ParseException if a parsing exception occurs
     */
    protected boolean isMageTabOptional(final Experiment experiment) throws IOException, ParseException {

        boolean result = false;

        final List<Archive> uploadedArchives = experiment.getArchivesForStatus( Archive.STATUS_UPLOADED );

        boolean onlyAuxArchives = true;
        boolean hasMafFiles = false;

        for (final Archive archive : uploadedArchives) {
            if (!Archive.TYPE_AUX.equals(archive.getArchiveType())) {
                onlyAuxArchives = false;
            }

            if (Experiment.TYPE_GSC.equals(experiment.getType())) {
                hasMafFiles = hasMafFiles(archive);
            }
        }

        if (Experiment.TYPE_GSC.equals(experiment.getType()) && !hasMafFiles) {
            // No MAF files so no mage-tab needed
            result = true;
        } else if (Experiment.TYPE_CGCC.equals(experiment.getType()) && onlyAuxArchives) {
            // only aux archives so no mage tab needed
            result = true;
        }

        return result;
    }

    /**
     * Reads the archive's manifest and return {@code true} if it contains MAF (.maf) files, {@code false} otherwise.
     *
     * @param archive the {@link Archive}
     * @return {@code true} if the archive's manifest contains MAF (.maf) files, {@code false} otherwise
     * @throws IOException if there is an I/O error while reading the manifest
     * @throws ParseException if there is an error while parsing the manifest
     */
    private boolean hasMafFiles(final Archive archive) throws IOException, ParseException {

        boolean result = false;

        final File manifest = new File(archive.getExplodedArchiveDirectoryLocation() + File.separator + MANIFEST_NAME);

        if (manifest.exists() && manifest.canRead()) {

            final Map<String, String> manifestEntries = manifestParser.parseManifest(manifest);

            for (final String filename : manifestEntries.keySet()) {
                if (filename != null && filename.endsWith(MAF_EXTENSION)) {
                    result = true;
                }
            }
        }

        return result;
    }

    protected Boolean doWork( final Experiment experiment, final QcContext context ) throws ProcessorException {

        context.setExperiment(experiment);

        try {
            context.setExperimentRequiresMageTab(experimentRequiresMageTab(experiment));

            // Check for mage-tab archive in ALL archives
            final List<Archive> mageTabArchives = new ArrayList<Archive>();
            for (final Archive archive : experiment.getArchives()) {
                if (Archive.TYPE_MAGE_TAB.equals(archive.getArchiveType())) {
                    mageTabArchives.add(archive);
                }
            }

            if (context.experimentRequiresMageTab()) {
                if (mageTabArchives.size() > 1) {
                    experiment.setStatus(Experiment.STATUS_FAILED);
                    
                    // this is incorrect.  figure out the best error message to report...
                    Archive availableMageTab = null;
                    for (final Archive mageTab : mageTabArchives) {
                        if (mageTab.getDeployStatus().equals(Archive.STATUS_AVAILABLE)) {
                            availableMageTab = mageTab;
                        }
                    }

                    if (availableMageTab == null) {
                        // they uploaded multiple mage-tabs
                        throw new ProcessorException(new StringBuilder().
                                append("Only one mage-tab archive is allowed per experiment.  Please submit a ").
                                append("single mage-tab archive containing all experiment samples in a its SDRF file.").
                                toString());
                    } else {
                        // they submitted a new mage-tab that was not a revision of the current available one
                        throw new ProcessorException(new StringBuilder().
                                append("Only one mage-tab archive is allowed per experiment.  Please submit the ").
                                append("new mage-tab archive as a revision of the currently deployed mage-tab archive, ").
                                append(availableMageTab.getRealName()).append(".").toString());
                    }
                } else if (mageTabArchives.size() == 1) {
                    Archive availableMageTab = mageTabArchives.get(0);
                    if(!Archive.STATUS_UPLOADED.equals(availableMageTab.getDeployStatus())) {
                        experiment.setStatus( Experiment.STATUS_PENDING );
                        context.addError(MessageFormat.format(
                                MessagePropertyType.EXPERIMENT_PROCESSING_ERROR,
                                experiment,
                                "Updated mage-tab archive has not been uploaded yet."));
                        return true;
                    }
                } else if(mageTabArchives.size() == 0) {

                    if (isMageTabOptional(experiment)) {
                        context.setExperimentRequiresMageTab(false);
                        return true;
                    } else {
                        context.addError(MessageFormat.format(
                                MessagePropertyType.EXPERIMENT_PROCESSING_ERROR,
                                experiment,
                                "MAGE-TAB archive not found"));
                        experiment.setStatus( Experiment.STATUS_PENDING );
                        
                        // return true because pending status is not failure.  Sort of.
                        return true;
                    }
                }

                // if we got here we have one Uploaded mage-tab archive
                // need to check full mage-tab to look for expected archives
                return checkMageTabArchive( experiment, mageTabArchives.get(0), context );
                
            } if (!context.experimentRequiresMageTab() && mageTabArchives.size() > 0) {
                // A mage-tab was submitted even though we were not expecting it -> fail
                throw new ProcessorException(new StringBuilder("The experiment included 1 or more mage-tab archive(s) which is not expected for center ")
                        .append(context.getCenterName()).toString());

            } else {
                // nothing to check
                return true;
            }
        } catch (IOException e) {
            throw new ProcessorException(e.getMessage());
        } catch (ParseException e) {
            throw new ProcessorException(e.getMessage());
        }

    }

    protected boolean checkMageTabArchive( final Experiment experiment, final Archive mageTabArchive,
                                           final QcContext context ) throws ProcessorException, IOException {
        context.setArchive( mageTabArchive );
        // find the IDF
        final File[] idfFiles = DirectoryListerImpl.getFilesByExtension( mageTabArchive.getDeployDirectory(), ".idf.txt" );
        if(idfFiles == null || idfFiles.length < 1) {
            throw new ProcessorException( "The MAGE-TAB archive does not contain an IDF file" );
        }
        // find the SDRF
        final File[] sdrfFiles = DirectoryListerImpl.getFilesByExtension( mageTabArchive.getExplodedArchiveDirectoryLocation(), AbstractSdrfHandler.SDRF_EXTENSION );
        if(sdrfFiles == null || sdrfFiles.length < 1) {
            // whatever calls this checker knows that an exception means failure, while return with Pending means wait longer...
            throw new ProcessorException( "The MAGE-TAB archive does not contain an SDRF" );
        }
        final File sdrfFile = sdrfFiles[0];
        // parse the SDRF
        TabDelimitedContent sdrf = new TabDelimitedContentImpl();
        TabDelimitedFileParser sdrfParser = new TabDelimitedFileParser();
        sdrfParser.setTabDelimitedContent( sdrf );
        try {
            sdrfParser.loadTabDelimitedContent( sdrfFile,true );
        } catch (IOException e) {
            throw new ProcessorException(new StringBuilder().append("Failed to parse SDRF '")
                    .append(sdrfFile.getName()).append("' (").append(e.getMessage()).append(")").toString(), e);
        } catch (ParseException e) {
            throw new ProcessorException(new StringBuilder().append("Failed to parse SDRF ")
                    .append(sdrfFile.getName()).append("' (").append(e.getMessage()).append(")").toString(), e);
        }
        sdrfParser.loadTabDelimitedContentHeader();
        TabDelimitedContentNavigator sdrfNavigator = new TabDelimitedContentNavigator();
        sdrfNavigator.setTabDelimitedContent( sdrf );
        experiment.setSdrf( sdrf );
        experiment.setSdrfFile( sdrfFile );
        // get the list of all archives referred to in the SDRF
        final List<Integer> archiveColumns = sdrfNavigator.getHeaderIdsForName( AbstractSdrfHandler.COMMENT_ARCHIVE_NAME );
        if(archiveColumns == null || archiveColumns.size() == 0) {
            throw new ProcessorException( new StringBuilder().append( "SDRF doesn't have any '" ).append( AbstractSdrfHandler.COMMENT_ARCHIVE_NAME ).append( "' columns." ).toString() );
        }
        final Set<String> sdrfArchives = new HashSet<String>();
        for(final Integer col : archiveColumns) {
            final List<String> archives = sdrfNavigator.getColumnValues( col );
            sdrfArchives.addAll( archives );
        }
        // make list of archive names from experiment, and previous archive names
        final List<String> experimentArchives = new ArrayList<String>();
        for(final Archive a : experiment.getArchives()) {
            experimentArchives.add( a.getRealName() );
        }
        // okay if SDRF refers to replaced archives (?) for files that have not changed -- a later step
        // will modify the SDRF once the existing files have been copied into the new archive.
        // (see SdrfRewriter)
        for(final Archive a : experiment.getPreviousArchives()) {
            experimentArchives.add( a.getRealName() );
        }
        for(final String archiveName : sdrfArchives) {
            if(!archiveName.equals( "->" )) {

                if(isValidArchiveName(archiveName)) {
                    if(!experimentArchives.contains( archiveName )) {
                        // for stand-alone, it's not an error if an archive is missing
                        if(!context.isNoRemote()) {
                            // does this archive exist in the database at all?
                            if (archiveQueries != null) {
                                long archiveId = archiveQueries.getArchiveIdByName(archiveName);
                                if (archiveId == -1) {
                                    // no -- not yet uploaded
                                    context.addError(MessageFormat.format(
                                            MessagePropertyType.EXPERIMENT_PROCESSING_ERROR,
                                            experiment,
                                            new StringBuilder().append("Archive '").append(archiveName).
                                                    append("' is listed in the SDRF but has not yet been uploaded").toString()));
                                } else {
                                    // yes -- must not be the latest available
                                    context.addError(MessageFormat.format(
                                            MessagePropertyType.EXPERIMENT_PROCESSING_ERROR,
                                            experiment,
                                            new StringBuilder().append("Archive '").append(archiveName).
                                                    append("' is listed in the SDRF but is not the latest available archive for that type and serial index").toString()));
                                }
                            } else {
                                // no archive queries available, so we can't check why the archive is missing
                                context.addError(MessageFormat.format(
                                        MessagePropertyType.EXPERIMENT_PROCESSING_ERROR,
                                        experiment,
                                        new StringBuilder().append("Archive '").append(archiveName).
                                                append("' is listed in the SDRF but either has not yet been uploaded or is not the latest available archive for that type and serial index").toString()));
                            }
                            experiment.setStatus( Experiment.STATUS_PENDING );
                        }
                    }
                } else {
                    // Archive name format is not valid
                    experiment.setStatus(Experiment.STATUS_FAILED);

                    throw new ProcessorException(
                            MessageFormat.format(
                                    MessagePropertyType.EXPERIMENT_PROCESSING_ERROR,
                                    experiment,
                                    new StringBuilder().append("Archive '").append(archiveName)
                                            .append("' is listed in the SDRF but is not a valid archive name format (expecting ")
                                            .append(ArchiveNameValidator.getExpectedArchiveNameFormat())
                                            .append(")"))
                    );
                }
            }
        }
        // this is weird, but Pending is not failed, so this returns true always
        // basically this class returns true or else throws an exception.  hmm.
        return true;
    }

    /**
     * Return <code>true</code>if the archive name is valid, <code>false</code> otherwise.
     *
     * @param archiveName the archive name to validate
     * @return <code>true</code>if the archive name is valid, <code>false</code> otherwise
     */
    private boolean isValidArchiveName(final String archiveName) {
        return ArchiveNameValidator.ARCHIVE_NAME_PATTERN.matcher(archiveName).matches();
    }

    public String getName() {
        return "Mage-Tab experiment checker";
    }

    public ArchiveQueries getArchiveQueries() {
        return archiveQueries;
    }

    public void setArchiveQueries(final ArchiveQueries archiveQueries) {
        this.archiveQueries = archiveQueries;
    }

    public ManifestParser getManifestParser() {
        return manifestParser;
    }

    public void setManifestParser(final ManifestParser manifestParser) {
        this.manifestParser = manifestParser;
    }

    public CenterQueries getCenterQueries() {
        return centerQueries;
    }

    public void setCenterQueries(final CenterQueries centerQueries) {
        this.centerQueries = centerQueries;
    }
}
