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
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedContentNavigator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Base class for SDRF handling steps.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public abstract class AbstractSdrfHandler<I, O> extends AbstractProcessor<I, O> {

    public static final String SDRF_EXTENSION = ".sdrf.txt";
    protected static final List<String> FILE_COLUMN_NAMES = new ArrayList<String>();
    protected static final List<String> ARCHIVE_ADDITIONAL_FILES = new ArrayList<String>();
    protected static final String IMAGE_FILE = "Image File";

    public static final String FILE_COLUMN_SUFFIX = "File";
    static {
        // Array Data File, Array Data Matrix File, Derived Array Data File, Derived Array Data Matrix File
        FILE_COLUMN_NAMES.add("Array Data File");
        FILE_COLUMN_NAMES.add("Array Data Matrix File");
        FILE_COLUMN_NAMES.add("Derived Array Data File");
        FILE_COLUMN_NAMES.add("Derived Array Data Matrix File");
        FILE_COLUMN_NAMES.add("Derived Data File");

        ARCHIVE_ADDITIONAL_FILES.add("MANIFEST.txt");
        ARCHIVE_ADDITIONAL_FILES.add("DESCRIPTION.txt");


    }
    public static final String COMMENT_ARCHIVE_NAME = "Comment [TCGA Archive Name]";
    public static final String COMMENT_DATA_LEVEL = "Comment [TCGA Data Level]";
    public static final String COMMENT_DATA_TYPE = "Comment [TCGA Data Type]";
    public static final String COMMENT_INCLUDE_FOR_ANALYSIS = "Comment [TCGA Include for Analysis]";
    public static final Pattern LEVEL_PATTERN = Pattern.compile("Level (\\d+)");
    public static final String PROTOCOL_REF = "Protocol REF";
    public static final String FILE_COLUMN_END = "File";
    public static final String ALIQUOT_HEADER = "Hybridization Name";
    public static final String EXTRACT_NAME = "Extract Name";

    /**
     * Get a map of comment columns for this file column -- key is comment header name and value is column index
     *
     * @param sdrfNavigator the sdrf navigator
     * @param fileColumn    the index of the file column
     * @return map of locations of comment columns for this file column
     */
    protected Map<String, Integer> getFileCommentColumns(final TabDelimitedContentNavigator sdrfNavigator,
                                                         final int fileColumn) {
        final Map<String, Integer> commentColumnIndex = new HashMap<String, Integer>();
        int columnIndex = fileColumn + 1;
        while (columnIndex < sdrfNavigator.getHeaders().size()) {
            if (sdrfNavigator.getHeaders().get(columnIndex).startsWith("Comment [")) {
                commentColumnIndex.put(sdrfNavigator.getHeaders().get(columnIndex), columnIndex);
            } else {
                break;
            }
            columnIndex++;
        }
        return commentColumnIndex;
    }

    /**
     * Find the index of the protocol reference column that corresponds to this file column.  If there isn't one, returns -1.
     *
     * @param sdrfNav    the sdrf to search
     * @param fileColumn the column index of the file column
     * @return the index of the protocol column or -1 if none found
     */
    protected int findProtocolColumnForFileColumn(final TabDelimitedContentNavigator sdrfNav, final Integer fileColumn) {
        // go backwards from this file column, looking for "Protocol REF".
        for (int columnNumber = fileColumn - 1; columnNumber >= 0; columnNumber--) {
            if (sdrfNav.getHeaders().get(columnNumber).equals(PROTOCOL_REF)) {
                return columnNumber;
            } else if (sdrfNav.getHeaders().get(columnNumber).endsWith(FILE_COLUMN_END)) {
                // If we find another File column before we find the protocol column, that means there is no protocol
                // for this file column (the protocol column sits between file columns)
                return -1;
            }
        }
        return -1;
    }

    /**
     * Get list of all the files for an archive from SDRF file
     *
     * @param archive       the archive under consideration
     * @param sdrfNavigator the sdrf navigator
     * @return set of file names in SDRF
     */
    protected Set<String> getFileNamesInArchiveFromSDRF(final Archive archive,
                                                        final TabDelimitedContentNavigator sdrfNavigator) {

        final Set<String> fileNamesInSDRF = new HashSet<String>();
        String columnName;
        Map<String, Integer> commentColumns;
        String archiveNameFromSDRF;
        for (int columnIndex = 0; columnIndex < sdrfNavigator.getHeaders().size(); columnIndex++) {
            columnName = sdrfNavigator.getHeaders().get(columnIndex);
            if (columnName.endsWith(FILE_COLUMN_SUFFIX)) {
                // get the comment column for archive name
                commentColumns = getFileCommentColumns(sdrfNavigator, columnIndex);
                if (commentColumns.get(COMMENT_ARCHIVE_NAME) != null) {
                    for (int rowIndex = 1; rowIndex < sdrfNavigator.getNumRows(); rowIndex++) {
                        archiveNameFromSDRF = sdrfNavigator.getValueByCoordinates(commentColumns.get(COMMENT_ARCHIVE_NAME), rowIndex);
                        if (archiveNameFromSDRF.equals(archive.getArchiveName())) {
                            fileNamesInSDRF.add(sdrfNavigator.getValueByCoordinates(columnIndex, rowIndex));
                        }
                    }
                }
            }
        }
        return fileNamesInSDRF;
    }

}
