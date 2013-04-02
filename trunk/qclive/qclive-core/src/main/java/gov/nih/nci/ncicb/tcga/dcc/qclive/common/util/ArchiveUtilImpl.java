/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ManifestValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

/**
 * Provides helper methods for <code>Archive</code> objects
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ArchiveUtilImpl implements ArchiveUtil {

    /**
     * A ManifestParser implementation
     */
    private ManifestParser manifestParser;

    public ManifestParser getManifestParser() {
        return manifestParser;
    }

    public void setManifestParser(final ManifestParser manifestParser) {
        this.manifestParser = manifestParser;
    }

    /**
     * Creates a new file with the given filename and content and add it to the given archive.
     * Modify the archive's manifest accordingly.
     *
     * @param content   the content of the file to create
     * @param filename  the filename of the file to create
     * @param archive   the archive into which to add the file
     * @param qcContext the context in which to log warnings and errors
     */
    @Override
    public void addContentIntoNewFileToArchive(final String content, final String filename, final Archive archive, final QcContext qcContext) {

        //Create the file at the location where the archive was deployed
        final File file = new File(archive.getDeployDirectory(), filename);

        try {
            //Write content to file
            FileUtil.writeContentToFile(content, file);

            //Retrieve manifest from archive
            final File manifestFile = getArchiveManifestFile(archive);

            //Add file to manifest
            getManifestParser().addFileToManifest(file, manifestFile);

        } catch (final IOException e) {
        	qcContext.addError(MessageFormat.format(
        			MessagePropertyType.ARCHIVE_PROCESSING_ERROR,
        			filename,
        			e.getMessage()));
        } catch (final ArchiveUtilException e) {
        	qcContext.addError(MessageFormat.format(
        			MessagePropertyType.ARCHIVE_PROCESSING_ERROR,
        			filename,
        			e.getMessage()));
        } catch (final NoSuchAlgorithmException e) {
        	qcContext.addError(MessageFormat.format(
        			MessagePropertyType.ARCHIVE_PROCESSING_ERROR,
        			filename,
        			e.getMessage()));
        } catch (final ParseException e) {
        	qcContext.addError(MessageFormat.format(
        			MessagePropertyType.ARCHIVE_PROCESSING_ERROR,
        			filename,
        			e.getMessage()));
        }

    }

    /**
     * Return the manifest from the given archive
     *
     * @param archive from which to retrieve the manifest
     * @return the manifest file
     * @throws ArchiveUtilException
     */
    @Override
    public File getArchiveManifestFile(final Archive archive) throws ArchiveUtilException {

        final File result = new File(archive.getDeployDirectory(), ManifestValidator.MANIFEST_FILE);

        if (!result.exists()) {
            throw new ArchiveUtilException("Could not retrieve the manifest from the archive '" + archive.getArchiveName() + "' (Id: " + archive.getId() + ")");
        }

        return result;
    }
}
