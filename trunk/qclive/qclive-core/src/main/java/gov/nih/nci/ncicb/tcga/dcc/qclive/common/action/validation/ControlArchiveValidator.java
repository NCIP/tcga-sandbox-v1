/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRUtilsImpl;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

/**
 * Validator for Control archive
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ControlArchiveValidator extends AbstractProcessor<Archive, Boolean> {

    private static final String CONTROL_TUMOR_ABBREVIATION = "CNTL";

    /**
     * A filter for XML Control files
     */
    private static final FilenameFilter CONTROL_FILENAME_FILTER = new FilenameFilter() {

        @Override
        public boolean accept(final File dir, final String name) {
            return name != null && name.toLowerCase().endsWith("." + FILE_EXTENSION_XML) && name.contains(BCRUtilsImpl.CONTROL);
        }
    };

    /**
     * A filter for XML Non-Control files
     */
    private static final FilenameFilter NON_CONTROL_FILENAME_FILTER = new FilenameFilter() {

        @Override
        public boolean accept(final File dir, final String name) {
            return name != null && name.toLowerCase().endsWith("." + FILE_EXTENSION_XML) && !name.contains(BCRUtilsImpl.CONTROL);
        }
    };

    /**
     * The expected platform name for Control archives
     */
    private static final String CONTROL_ARCHIVE_PLATFORM_NAME = "bio";

    @Override
    protected Boolean doWork(final Archive archive, final QcContext context) throws ProcessorException {

        boolean result = true;

        if (archive != null && isControlArchive(archive)) {

            result = validatePlatformName(archive, context);
            result &= validateArchiveContentFileNames(archive, context);
        }

        return result;
    }

    /**
     * Validates that the platform name is valid for a Control archive
     *
     * @param archive the archive to validate
     * @param context the qclive context
     * @return <code>true</code> if the platform name is valid, <code>false</code> otherwise
     */
    private boolean validatePlatformName(final Archive archive,
                                         final QcContext context) {

        boolean result = true;

        if(archive != null) {

            final String platformName = archive.getPlatform();
            if(!CONTROL_ARCHIVE_PLATFORM_NAME.equals(platformName)) {

                result = false;

                final String errorMessage = new StringBuilder("Expecting platform name for Control archive to be '")
                        .append(CONTROL_ARCHIVE_PLATFORM_NAME)
                        .append("', but found '")
                        .append(platformName)
                        .append("'")
                        .toString();

                context.addError(MessageFormat.format(
                        MessagePropertyType.ARCHIVE_PROCESSING_ERROR,
                        archive, errorMessage));
            }
        }
        
        return result;
    }

    /**
     * Validates that:
     *  - there is at least 1 control file inside the archive
     *  - there are only control files inside the archive
     *
     * @param archive the archive to validate
     * @param context the qclive context
     * @return <code>true</code> if the archive is valid, <code>false</code> otherwise
     */
    private boolean validateArchiveContentFileNames(final Archive archive,
                                                    final QcContext context) {

        boolean result = true;
        final String deployDirectory = archive.getDeployDirectory();

        if(deployDirectory != null) {

            final String[] controlFiles = new File(deployDirectory).list(CONTROL_FILENAME_FILTER);
            final String[] nonControlFiles = new File(deployDirectory).list(NON_CONTROL_FILENAME_FILTER);

            if(controlFiles == null || controlFiles.length < 1) {
                result = false;
                context.addError(MessageFormat.format(
                        MessagePropertyType.ARCHIVE_PROCESSING_ERROR,
                        archive,
                        "The archive is a control archive and should contain at least 1 control file."));
            }

            if(nonControlFiles != null && nonControlFiles.length > 0) {
                result = false;
                context.addError(MessageFormat.format(
                        MessagePropertyType.ARCHIVE_PROCESSING_ERROR,
                        archive,
                        "The archive is a control archive and should only contain control files. Found: " +
                                StringUtil.convertListToDelimitedString(Arrays.asList(nonControlFiles), ',')));
            }
        }

        return result;
    }

    @Override
    public String getName() {
        return "control archive validation";
    }

    /**
     * Return <code>true</code> if this is a Control archive, <code>false</code> otherwise.
     *
     * @param archive the archive to validate
     * @return <code>true</code> if this is a Control archive, <code>false</code> otherwise
     */
    private boolean isControlArchive(final Archive archive) {
        return CONTROL_TUMOR_ABBREVIATION.equals(archive.getTumorType());
    }
}
