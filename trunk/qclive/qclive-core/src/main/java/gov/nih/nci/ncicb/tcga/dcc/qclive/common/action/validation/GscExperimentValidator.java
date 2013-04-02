/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractMafFileHandler;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.DirectoryLister;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.DirectoryListerImpl;

import java.io.File;

/**
 * Validates a GSC experiment. Right now, that just means checking level 2 and 3 archives to make sure that if there is
 * a maf file, there is also a corresponding vcf file.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class GscExperimentValidator extends AbstractProcessor<Experiment, Boolean> {
    private DirectoryLister directoryLister = new DirectoryListerImpl();
    public static final String VCF_EXTENSION = ".vcf.gz";

    /**
     * Validates the given GSC experiment.  Non-GSC experiments just return true.
     *
     * @param experiment the input to the processor
     * @param context the context for this QC call
     *
     * @return true if not a GSC experiment or if a valid GSC experiment, false otherwise
     *
     * @throws ProcessorException if there is an unrecoverable error
     */
    protected Boolean doWork(final Experiment experiment, final QcContext context) throws ProcessorException {
        if (!Experiment.TYPE_GSC.equals(experiment.getType())) {
            return true;
        }
        boolean isValid = true;
        for (final Archive archive : experiment.getArchivesForStatus(Archive.STATUS_UPLOADED)) {
            context.setArchiveInProgress(archive);
            if (archive.getArchiveType().equals(Archive.TYPE_LEVEL_2) || archive.getArchiveType().equals(Archive.TYPE_LEVEL_3)) {
                boolean archiveIsValid = validateLevel2or3Archive(experiment, archive, context);
                if (! archiveIsValid || (context.getErrorsByArchiveName(archive).size() > 0)) {
                    archive.setDeployStatus(Archive.STATUS_INVALID);
                }
                isValid = archiveIsValid && isValid;
            }
        }
        return isValid;
    }

    private boolean validateLevel2or3Archive(
            final Experiment experiment, final Archive archive, final QcContext context) {
        boolean isValid = true;
        DirectoryLister directoryLister = getDirectoryLister();
        // get list of maf files
        File[] mafFiles = directoryLister.getFilesInDirectoryByExtension(archive.getDeployDirectory(), AbstractMafFileHandler.MAF_EXTENSION);
        // get list of vcf files
        File[] vcfFiles = directoryLister.getFilesInDirectoryByExtension(archive.getDeployDirectory(), VCF_EXTENSION);
        // make sure for each vcf file, there is a maf file
        if (vcfFiles != null) {
            for (final File vcfFile : vcfFiles) {
                boolean foundMaf = false;
                String nameWithoutExtension = vcfFile.getName().substring(0, vcfFile.getName().indexOf(VCF_EXTENSION));
                for (final File mafFile : mafFiles) {
                    if (mafFile.getName().equals(nameWithoutExtension + AbstractMafFileHandler.MAF_EXTENSION)) {
                        foundMaf = true;
                        break;
                    }
                }
                if (!foundMaf) {
                    // if this is not stand-alone, see if this archive is replacing an older one... if so, see if the VCF is in there
                    if (! context.isNoRemote()) {
                        Archive replacedArchive = experiment.getPreviousArchiveFor(archive);
                        if (replacedArchive != null) {
                            File[] existingMafFiles = directoryLister.getFilesInDirectoryByExtension(replacedArchive.getDeployDirectory(), AbstractMafFileHandler.MAF_EXTENSION);
                            for (final File existingMafFile : existingMafFiles) {
                                if (existingMafFile.getName().equals(nameWithoutExtension + AbstractMafFileHandler.MAF_EXTENSION)) {
                                    foundMaf = true;
                                    break;
                                }
                            }
                        }
                    } else {
                        context.addWarning(new StringBuilder().append("VCF file '").append(nameWithoutExtension).
                                append(VCF_EXTENSION).append("' must be present in previous version of the archive.  If it is not, DCC processing will fail.").toString());
                        // set to true, since we don't know if it's there or not (future: use remote service to validate file existence)
                        foundMaf = true;
                    }
                    // might have found it in previous archive, if not that is an error
                    if (!foundMaf) {
                    	context.addError(MessageFormat.format(
                    			MessagePropertyType.EXPERIMENT_PROCESSING_ERROR,
                    			experiment,
                    			new StringBuilder().append("VCF file '").append(vcfFile.getName()).
                                append("' does not have MAF file (expected to find '").
                                append(nameWithoutExtension).append(AbstractMafFileHandler.MAF_EXTENSION).
                                append("' in this archive or previous archive)").toString()));
                        isValid = false;
                    }
                }
            }
        }
        return isValid;
    }

    /**
     * Extracted for testing purposes.
     *
     * @return the directory lister to use
     */
    protected DirectoryLister getDirectoryLister() {
        return directoryLister;
    }

    /**
     * Gets the name of the processor, in descriptive English.  Name should fit into a sentence in the form "Execution of "
     * + getName() + " completed", if possible.
     *
     * @return the descriptive name of this processor
     */
    public String getName() {
        return "validation of GSC archive set";
    }
}
