/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.IDF;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ProtocolNameValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.DirectoryListerImpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validator for mage-tab archive IDF files.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public abstract class AbstractIdfValidator extends AbstractProcessor<Archive, Boolean> {

    public final static String IDF_EXTENSION = ".idf.txt";
    public final static String TERM_SOURCE_MISMATCH_ERROR = "Headers Term Source Name, Term Source File, and Term Source Version must have same number of values";
    public final static String DUP_HEADER_ERROR = "Duplicate header found";
    private static final String IDF_BLANK = "->";

    private enum TermSourceHeader {
        TERM_SOURCE_NAME("Term Source Name"),
        TERM_SOURCE_FILE("Term Source File"),
        TERM_SOURCE_VERSION("Term Source Version");

        private final String name;
        TermSourceHeader(final String name) {this.name = name;}
        public String getName() {return name;}
        @Override
        public String toString() {return getName();}
    }

    /**
     * Extract the {@link IDF} object from the given IDF archive.
     *
     * @param archive the IDF archive
     * @return IDF the {@link IDF} object
     * @throws IOException if there is an I/O Exception while reading the IDF file
     * @throws ProcessorException if a {@link ProcessorException} is thrown while processing the IDF file
     */
    public static IDF getIdf(final Archive archive) throws IOException, ProcessorException {
        final File[] idfFiles = DirectoryListerImpl.getFilesByExtension(archive.getExplodedArchiveDirectoryLocation(), ".idf.txt");
        if (idfFiles.length < 1) {
            archive.setDeployStatus(Archive.STATUS_INVALID);
            throw new ProcessorException(new StringBuilder().append("Archive is missing its ").append(IDF_EXTENSION).append(" file").toString());
        }
        String idfName = idfFiles[0].getCanonicalPath();
        idfName = idfName.substring(idfName.lastIndexOf(File.separator) + 1, idfName.length());
        IDF idfFile = new IDF();
        idfFile.initIDF(archive.getExplodedArchiveDirectoryLocation(), idfName);
        return idfFile;
    }

    /**
     * Run IDF validation
     * @param archive IDF archive
     * @param context QC context
     *
     * @return <code>true<code> if validation passes, or <code>false<code> if validation fails
     */
    protected Boolean doWork(final Archive archive, final QcContext context) throws ProcessorException {
        context.setArchive(archive);
        // return true if this is not a mage-tab archive
        // because there is no IDFs in other archive types
        if (!archive.getArchiveType().equals(Archive.TYPE_MAGE_TAB)) {
            return true;
        }
        boolean valid = true;
        try {
            final IDF idf = getIdf(archive);
            if (idf.getIDFColHeaders().size() > 0) {
                valid = areHeadersAllowed(idf, context);

                valid = areRequiredHeadersPresent(idf, context) && valid;

                final String protocolName = idf.getIDFColValueByColNumber(1, idf.getColNumberByName(PROTOCOL_NAME));
                if (protocolName != null) {
                    final ProtocolNameValidator pnv = new ProtocolNameValidator(protocolName);
                    if (pnv.getGroupCount() != 4) {
                        context.addError(MessageFormat.format(
                                MessagePropertyType.ARCHIVE_PROCESSING_ERROR,
                                archive,
                                INCORRECT_NUMBER_OF_ELEMENTS_PROTOCL_NAME));
                        valid = false;
                    }
                    if (!archive.getDomainName().equals(pnv.getDomain())) {
                        context.addError(MessageFormat.format(
                                MessagePropertyType.ARCHIVE_PROCESSING_ERROR,
                                archive,
                                DOMAIN_NAME_DOES_NOT_MATCH));
                        valid = false;
                    }
                    if (!archive.getPlatform().equals(pnv.getPlatform())) {
                        context.addError(MessageFormat.format(
                                MessagePropertyType.ARCHIVE_PROCESSING_ERROR,
                                archive,
                                PLATFORM_DOES_NOT_MATCH));
                        valid = false;
                    }
                    if (pnv.getProtocolType() == null) {
                        context.addError(MessageFormat.format(
                                MessagePropertyType.ARCHIVE_PROCESSING_ERROR,
                                archive,
                                PROTOCOL_TYPE_IS_INVALID));
                        valid = false;
                    }
                    if (pnv.getVersion() == null) {
                        context.addError(MessageFormat.format(
                                MessagePropertyType.ARCHIVE_PROCESSING_ERROR,
                                archive,
                                VERSION_NUMBER_IS_INVALID));
                        valid = false;
                    }

                    final int protocolDescriptionColumnNumber = idf.getColNumberByName(PROTOCOL_DESCRIPTION);
                    final String protocolDescriptionValue = idf.getIDFColValueByColNumber(1, protocolDescriptionColumnNumber);

                    if (protocolDescriptionValue == null
                            || IDF_BLANK.equals(protocolDescriptionValue)) {
                        context.addError(MessageFormat.format(
                                MessagePropertyType.ARCHIVE_PROCESSING_ERROR,
                                archive,
                                PROTOCOL_DESCRIPTION_FOR_PROTOCOL_NAME + protocolName + PROTOCOL_NAME_CANNOT_BE_EMPTY));
                        valid = false;
                    }
                }

                final List theColList = idf.getIDFColByNumber(1);
                if (theColList.contains(IDF_BLANK)) {
                    context.addError(MessageFormat.format(
                    		MessagePropertyType.ARCHIVE_PROCESSING_ERROR, 
                    		archive, 
                    		IDF_VALUES_NOT_ALLOWED));
                    valid = false;
                }
                // Validate TERM_SOURCE_NAME, TERM_SOURCE_FILE, and TERM_SOURCE_VERSION headers
                final List<List<String>> termSourceHeaderList = new ArrayList<List<String>>();
                int missingHeaderCount = 0;
                int lastHeaderSize = -1;
                boolean equalHeaderSizes = true;
                for (final TermSourceHeader header : TermSourceHeader.values()) {
                    final List<String> headerValues = idf.getAllIDFColValuesByColName(header.getName());
                    if (headerValues == null) { // if given header is not found
                        ++missingHeaderCount;
                    }
                    else { // if given header is found, then validate it
                        if (lastHeaderSize != -1) {
                            equalHeaderSizes = (headerValues.size() == lastHeaderSize) && equalHeaderSizes;
                        }
                        lastHeaderSize = headerValues.size();
                        termSourceHeaderList.add(headerValues);
                        valid = checkForDuplicateHeaders(archive, context, idf, header.getName()) && valid;
                        checkForDuplicateValues(archive, context, header.getName(), headerValues);
                    }
                }
                // validation fails if headers have different value count OR if some headers are missing
                if ( ! equalHeaderSizes || missingHeaderCount > 0 && missingHeaderCount < TermSourceHeader.values().length) {
                        context.addError(MessageFormat.format(
                                MessagePropertyType.ARCHIVE_PROCESSING_ERROR,
                                archive,
                                TERM_SOURCE_MISMATCH_ERROR));
                        valid = false;
                }
                //Find all columns with Term Source REF in the name
                final List<List<String>> colsToWorkWith = idf.getAllColsContainingString(TERM_SOURCE_REF);
                //Hash Set to hold unique values since sometimes a value may repeat, and we only want to notify once.
                final Set<Object> uniqueMissingValues = new HashSet<Object>();
                for (final Object aColsToWorkWith : colsToWorkWith) {
                    final List values = (List) Arrays.asList(aColsToWorkWith).get(0);
                    for (final Object value : values.subList(1, values.size())) {
                        if (!termSourceHeaderList.get(TermSourceHeader.TERM_SOURCE_NAME.ordinal()).contains(value.toString()) &&
                            value.toString().trim().length() > 0) {
                            uniqueMissingValues.add(value);
                        }
                    }
                }
                for (final Object uniqueMissingValue : uniqueMissingValues) {
                    final String val = uniqueMissingValue.toString();
                    context.addError(MessageFormat.format(
                    		MessagePropertyType.ARCHIVE_PROCESSING_ERROR, 
                    		archive, 
                    		FAILURE_ORPHANED_VALUE + val + IDF_ORPHANED_TERM_SOURCE_NAME));
                    valid = false;
                }
            } else {
            	context.addError(MessageFormat.format(
                		MessagePropertyType.ARCHIVE_PROCESSING_ERROR, 
                		archive, 
                		"IDF cannot be empty"));
                valid = false;
            }
        }
        catch (IOException e) {
            archive.setDeployStatus(Archive.STATUS_INVALID);
            throw new ProcessorException(new StringBuilder().append("There was an I/O exception while reading the IDF file: ").append(e.getMessage()).toString(), e);
        }
        if (!valid) {
            archive.setDeployStatus(Archive.STATUS_INVALID);
        }
        return valid;
    }

    /**
     * Validate the headers, adding errors to the context if it finds required headers that missing.
     *
     * @param idf the {@link IDF}
     * @param context the {@link QcContext}
     * @return {@code true} if valid, {@code false} otherwise
     */
    private boolean areRequiredHeadersPresent(final IDF idf,
                                              final QcContext context) {

        boolean valid = true;

        final List<String> idfActualHeaders = idf.getIDFColHeaders();

        for (final String requiredHeader : getRequiredIdfHeaders()) {

            if (!idfActualHeaders.contains(requiredHeader)) {

                valid = false;
                final String errorMessage = requiredHeader + " header is missing from the IDF";
                context.addError(errorMessage);
            }
        }

        return valid;
    }

    /**
     * Validate the headers, adding errors to the context if it finds headers that are not allowed.
     *
     * @param idf the {@link IDF}
     * @param context the {@link QcContext}
     * @return {@code true} if valid, {@code false} otherwise
     */
    private boolean areHeadersAllowed(final IDF idf,
                                      final QcContext context) {

        boolean valid = true;

        final List<String> idfActualHeaders = idf.getIDFColHeaders();

        for (final String idfActualHeader : idfActualHeaders) {

            if (!getAllowedIdfHeaders().contains(idfActualHeader)) {

                valid = false;
                final String errorMessage = idfActualHeader + " is not a valid row header";
                context.addError(errorMessage);
            }
        }

        return valid;
    }

    /**
     * Return the IDF headers that are allowed.
     *
     * @return the IDF headers that are allowed
     */
    public abstract Collection<String> getAllowedIdfHeaders();

    /**
     * Return the IDF headers that are required.
     *
     * @return the IDF headers that are required
     */
    protected abstract Collection<String> getRequiredIdfHeaders();

    /**
     * Get class description name
     *
     * @return class description name
     */
    public abstract String getName();

    /**
     * Check for duplicate headers
     * @param archive IDF archive
     * @param context QC context
     * @param idf idf obj
     * @param headerName headerName
     *
     * @return <code>true<code> if validation passes (i.e. no dups), or <code>false<code> if validation fails (i.e. dups found)
     */
    protected boolean checkForDuplicateHeaders(final Archive archive, final QcContext context, final IDF idf, final String headerName) {
        final List<List<String>> headerList = idf.getAllColsContainingString(headerName);
        if (headerList != null && headerList.size() > 1) {
            context.addError(MessageFormat.format(
                    MessagePropertyType.ARCHIVE_PROCESSING_ERROR,
                    archive,
                    DUP_HEADER_ERROR + ": " + headerName));
            return false;
        }
        return true;
    }

    /**
     * Check for duplicate values in given header
     * @param archive IDF archive
     * @param context QC context
     * @param headerName headerName
     * @param headerValues list of values in given header
     */
    protected void checkForDuplicateValues(final Archive archive, final QcContext context, final String headerName, final List<String> headerValues) {
        final Set<String> uniqVals = new HashSet<String>();
        final Set<String> dups = new HashSet<String>();
        for (final String value : headerValues) {
            if (value != null && ! uniqVals.add(value)) {
                dups.add(value);
            }
        }
        if (dups.size() > 0) {
            context.addWarning(MessageFormat.format(
                    MessagePropertyType.ARCHIVE_PROCESSING_WARNING,
                    archive,
                    "In header " + "'" + headerName + "'" + " found duplicate values: " + dups.toString()));
        }
    }
}
