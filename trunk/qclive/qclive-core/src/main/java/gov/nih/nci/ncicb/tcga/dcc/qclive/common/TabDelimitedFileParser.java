/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common;

import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContent;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * The class which loads and parses the tab delimited data and stores it in the tab delimited content object.
 *
 * @author Robert S. Sfeir
 *         Last updated by: $Author$
 * @version $Rev: 3419 $
 */
public class TabDelimitedFileParser {

    private TabDelimitedContent tabDelimitedContent = null;


    private Map<Integer, Integer> commentAndEmptyLineMap = null;

    /**
     * gets the TabDelimitedContent object.  You want to load that from Spring Framework typically and pass the object around.
     *
     * @return the TabDelimitedContent object
     */
    public TabDelimitedContent getTabDelimitedContent() {
        return tabDelimitedContent;
    }

    /**
     * sets the TabDelimitedContent object.
     *
     * @param tabDelimitedContent the TabDelimitedContent object
     */
    public void setTabDelimitedContent(final TabDelimitedContent tabDelimitedContent) {
        this.tabDelimitedContent = tabDelimitedContent;
    }

    /**
     * Loads the tab delimited file into a HashMap containing rows of data.  Row 0 represents the header of the tab delimited file.
     * Each ROW loaded contains the row number (0-based) and a String[] of the values for each row)
     *
     * @param fileName the name of the tab delimited file we want to load
     * @throws IOException when the exception can't be found.
     */
    public void loadTabDelimitedContent(final String fileName) throws IOException,ParseException {
        loadTabDelimitedContent(new File(fileName));
    }


    public void loadTabDelimitedContent(final File file) throws IOException,ParseException {
        loadTabDelimitedContent(file, null,false);
    }

    public void loadTabDelimitedContent(final File file, final Boolean validateColumnsSize) throws IOException,ParseException {
        loadTabDelimitedContent(file, null,validateColumnsSize);
    }

/**
     * Loads the tab delimited file into a HashMap containing rows of data.  Row 0 represents the header of the tab delimited file.
     * Each ROW loaded contains the row number (0-based) and a String[] of the values for each row)
     *
     * @param file         the tab delimited file we want to load
     * @param commentToken if the line begins with this, it will be skipped.  may be null for no skipping.
     * @throws IOException when the exception can't be found.
     */
    public void loadTabDelimitedContent(final File file, final String commentToken) throws IOException,ParseException {
        loadTabDelimitedContent(file,commentToken,false);
    }

    /**
     * Loads the tab delimited file into a HashMap containing rows of data.  Row 0 represents the header of the tab delimited file.
     * Each ROW loaded contains the row number (0-based) and a String[] of the values for each row)
     *
     * @param file         the tab delimited file we want to load
     * @param commentToken if the line begins with this, it will be skipped.  may be null for no skipping.
     * @throws IOException when the exception can't be found.
     */
    public void loadTabDelimitedContent(final File file, final String commentToken, final boolean validateColumnsSize) throws IOException,ParseException {

        final Map<Integer, String[]> dataMap = new HashMap<Integer, String[]>();
        final Map<Integer, Integer> allDataLinesMapIncludingComments = new HashMap<Integer, Integer>();
        String dataLine;
        Integer rowCount = 0;
        Integer rowCountCommentEmptyLines = 0;

        FileReader fReader = new FileReader(file);
        @SuppressWarnings({"IOResourceOpenedButNotSafelyClosed"}) BufferedReader in = new BufferedReader(fReader);
        int columnCount = -1;
        try {
            while ((dataLine = in.readLine()) != null) {
                //count comment of empty lines also
                if (StringUtils.isEmpty(dataLine.trim()) || StringUtils.startsWith(dataLine, commentToken)) {
                    rowCountCommentEmptyLines++;
                }
                // ignore blank lines!
                if (dataLine.trim().length() > 0 && (commentToken == null || !dataLine.startsWith(commentToken))) {
                    final String[] rowCols = dataLine.split("\t", -1);
                    //Initialize columnCount
                    columnCount = (columnCount == -1)?rowCols.length:columnCount;

                    if(validateColumnsSize && (columnCount != rowCols.length)){
                        throw new ParseException("Error at line no "+ rowCount+": Expected "+columnCount+ " columns but found "+ rowCols.length+" columns.",rowCols.length);
                    }
                    dataMap.put(rowCount, rowCols);
                    rowCount++;
                }

                //setup the map like this <currentDataLineNum,total no. of commentOrEmptyLines>
                allDataLinesMapIncludingComments.put(rowCount, rowCountCommentEmptyLines);

            }
            getTabDelimitedContent().setTabDelimitedContents(dataMap);
            setCommentAndEmptyLineMap(allDataLinesMapIncludingComments);
        }
        finally {
            IOUtils.closeQuietly(fReader);
            IOUtils.closeQuietly(in);

            fReader = null;
            in = null;
        }
    }

    /**
     * loads the first row of the tab delimited Map into the tab delimited content setter method.
     */
    public void loadTabDelimitedContentHeader() {
        getTabDelimitedContent().setTabDelimitedHeader(getTabDelimitedContent().getTabDelimitedContents().get(0));
    }

    /**
     * convenience method to initialize the parser and populate the tab delimited content object with the correct values.
     *
     * @param tabDelimitedFile the tab delimited file we want to initialize this class with.
     * @throws java.io.IOException when the file can't be found.
     */
    public void initialize(final String tabDelimitedFile) throws IOException,ParseException {
        loadTabDelimitedContent(tabDelimitedFile);
        loadTabDelimitedContentHeader();
    }

    /**
     * This Map keeps track of comment and empty line in input file
     *
     * @return commentAndEmptyLineMap
     */
    public Map<Integer, Integer> getCommentAndEmptyLineMap() {
        return commentAndEmptyLineMap;
    }

    /**
     * This Map keeps track of comment and empty line in input file
     *
     * @param commentAndEmptyLineMap map that keeps track of comment and empty lines
     */
    public void setCommentAndEmptyLineMap(final Map<Integer, Integer> commentAndEmptyLineMap) {
        this.commentAndEmptyLineMap = commentAndEmptyLineMap;
    }
}
