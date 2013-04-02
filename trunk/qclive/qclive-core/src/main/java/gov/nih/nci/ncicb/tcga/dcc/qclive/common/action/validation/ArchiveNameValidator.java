/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validates an archive name against a set pattern.
 * Note: if you want to validate the name parts against the database, use these validators:
 * ArchiveTypeValidator, DomainNameValidator, PlatformValidator, TumorTypeValidator
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class ArchiveNameValidator extends AbstractProcessor<Archive, Boolean> {

    // (center name)_(disease).(platform).(archivetype).(batch).(revision).(series)
    private static final String thePattern = "([a-zA-Z0-9\\.\\-]+)_([a-zA-Z0-9]+)\\.([\\w\\-]+)\\.([\\w\\-]+)\\.(\\d+)\\.(\\d+)\\.(\\d+)";
    /**
     * Pattern to use for archive names.  If a string matches, the first group will be the center name, second group
     * will be the disease name, 3rd will be the platform, 4th the archive type, 5th the batch, 6th revision, 7th series.
     */
    public static final Pattern ARCHIVE_NAME_PATTERN = Pattern.compile(thePattern);

    public static final int INDEX_IN_ARCHIVE_NAME_DOMAIN = 1;
    public static final int INDEX_IN_ARCHIVE_NAME_TUMOR_TYPE = 2;
    public static final int INDEX_IN_ARCHIVE_NAME_PLATFORM = 3;
    public static final int INDEX_IN_ARCHIVE_NAME_ARCHIVE_TYPE = 4;
    public static final int INDEX_IN_ARCHIVE_NAME_SERIAL = 5;
    public static final int INDEX_IN_ARCHIVE_NAME_REVISION = 6;
    public static final int INDEX_IN_ARCHIVE_NAME_SERIES = 7;

    /**
     * Pattern to use for experiment names.  If a string matches, the first group will be the center name, second will
     * be the disease name, third will be the platform name.
     */
    public static final Pattern EXPERIMENT_NAME_PATTERN = Pattern.compile("([a-zA-Z0-9\\.\\-]+)_([a-zA-Z0-9]+)\\.([\\w\\-]+)");
    private static final String ARCHIVE_NAME_FORMAT = "[center]_[disease].[platform].[archive_type].[batch].[revision].[series]";

    /**
     * Validates the format of the archive name.  Does not validate the individual parts -- that is,
     * does not check that the tumor type is a tumor type that exists in the database.
     *
     * @param archive the archive to validate
     * @param context the context for this QC call
     * @return true if the archive name is valid, in terms of format
     * @throws ProcessorException
     */
    protected Boolean doWork(final Archive archive, final QcContext context) throws ProcessorException {
        context.setArchive(archive);
        // make sure the archive file is the right type, based on extension
        final String archiveName = archive.getArchiveFile().getName();
        if (!(archiveName.endsWith(ConstantValues.COMPRESSED_ARCHIVE_EXTENSION) ||
                archiveName.endsWith(ConstantValues.UNCOMPRESSED_ARCHIVE_EXTENSION))) {
            archive.setDeployStatus(Archive.STATUS_INVALID);
            final String errorMessage = new StringBuilder().append("Archives must be ")
                    .append(ConstantValues.COMPRESSED_ARCHIVE_EXTENSION).append(" or ")
                    .append(ConstantValues.UNCOMPRESSED_ARCHIVE_EXTENSION)
                    .append(" files, but '").append(archiveName).append("' does not end with '")
                    .append(ConstantValues.COMPRESSED_ARCHIVE_EXTENSION).append(" or ")
                    .append(ConstantValues.UNCOMPRESSED_ARCHIVE_EXTENSION)
                    .append("'.").toString();
            throw new ProcessorException(errorMessage);
        }

        Matcher matcher = ARCHIVE_NAME_PATTERN.matcher(archive.getArchiveName());
        if (matcher.matches()) {
            extractArchiveNameParts(archive, context, matcher);
            if (!"0".equals(matcher.group(INDEX_IN_ARCHIVE_NAME_SERIES))) {
                // don't throw an exception in this case -- can still do other validations on center name, etc
                context.addError(MessageFormat.format(
                        MessagePropertyType.ARCHIVE_PROCESSING_ERROR,
                        archive.getArchiveFile(),
                        "Archive series must be '0'"));
                return false;
            }
            return true;

        } else {
            // see if name is valid except for missing series -- this is a common error
            matcher = ARCHIVE_NAME_PATTERN.matcher(archive.getArchiveName() + ".0");
            if (matcher.matches()) {
                extractArchiveNameParts(archive, context, matcher);
                context.addError(MessageFormat.format(
                        MessagePropertyType.ARCHIVE_PROCESSING_ERROR,
                        archive.getArchiveFile(),
                        "Archive is missing its series.  Name should be: '" + archive.getArchiveName() + ".0" + "'"));
                archive.setDeployStatus(Archive.STATUS_INVALID);
                return false;
            } else {
                // not just a missing series -- just throw an exception
                final String error = new StringBuilder("Archive filename (").
                        append(archive.getArchiveFile().getName()).append(") does not have valid format.  ").
                        append("Should be: '").append(getExpectedArchiveNameFormat()).
                        append(archive.getDepositedArchiveExtension()).append("'.").toString();
                archive.setDeployStatus(Archive.STATUS_INVALID);
                throw new ProcessorException(error);
            }
        }

    }

    /**
     * Return the expected archive name format.
     *
     * @return the expected archive name format
     */
    public static String getExpectedArchiveNameFormat() {
        return ARCHIVE_NAME_FORMAT;
    }

    private void extractArchiveNameParts(final Archive archive, final QcContext context, final Matcher matcher) {
        // get out the parts
        archive.setDomainName(matcher.group(INDEX_IN_ARCHIVE_NAME_DOMAIN));
        // put the center name in the context so we know who to email errors!
        context.setCenterName(archive.getDomainName());
        archive.setTumorType(matcher.group(INDEX_IN_ARCHIVE_NAME_TUMOR_TYPE));
        archive.setPlatform(matcher.group(INDEX_IN_ARCHIVE_NAME_PLATFORM));
        context.setPlatformName(archive.getPlatform());
        archive.setArchiveType(matcher.group(INDEX_IN_ARCHIVE_NAME_ARCHIVE_TYPE));
        archive.setSerialIndex(matcher.group(INDEX_IN_ARCHIVE_NAME_SERIAL));
        archive.setRevision(matcher.group(INDEX_IN_ARCHIVE_NAME_REVISION));
        archive.setSeries(matcher.group(INDEX_IN_ARCHIVE_NAME_SERIES));
    }

    /**
     * @return the descriptive name for what this processor does
     */
    public String getName() {
        return "archive name validation";
    }

    /**
     * @param archive the archive that was passed in
     * @return descriptive phrase describing the action this took on the given archive
     */
    public String getDescription(final Archive archive) {
        return getName() + (archive == null ? "" : " on " + archive.getArchiveFile().getName());
    }


}
