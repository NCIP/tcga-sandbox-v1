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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class to allow easy access to values in a tab delimited file
 *
 * @author Robert S. Sfeir
 *         Last updated by: $Author$
 * @version $Rev: 3419 $
 */
public class TabDelimitedContentNavigator {

    private TabDelimitedContent tabDelimitedContent;
    private List<String> headers;
    private Map<Integer, Integer> commentAndEmptyLineMap;

    /**
     * Get the TabDelimitedContent object, typically set in Spring Framework via injection
     *
     * @return TabDelimitedContent object
     */
    public TabDelimitedContent getTabDelimitedContent() {
        return tabDelimitedContent;
    }

    /**
     * Set the TabDelimitedContent object, typically set in Spring Framework via injection
     *
     * @param tabDelimitedContent TabDelimitedContent object
     */
    public void setTabDelimitedContent(final TabDelimitedContent tabDelimitedContent) {
        this.tabDelimitedContent = tabDelimitedContent;
    }

    /**
     * Return the ID (column number) of of the header item by its name.
     *
     * @param headerName the name of the column you're looking for
     * @return the id of the column you asked for
     */
    public Integer getHeaderIDByName(final String headerName) {
        if (getHeaders() == null) {
            setHeaders(Arrays.asList(tabDelimitedContent.getTabDelimitedHeaderValues()));
        }
        return headers.indexOf(headerName);
    }

    /**
     * Return the column number of the next header with the given name, starting at the given index.
     *
     * @param headerName the name of the header to find
     * @param startIndex the first index to look in (0-based)
     * @return the index of the header
     */
    public Integer getHeaderIDByName(final String headerName, final int startIndex) {
        if (getHeaders() == null) {
            setHeaders(Arrays.asList(tabDelimitedContent.getTabDelimitedHeaderValues()));
        }
        for (int i = startIndex; i < headers.size(); i++) {
            if (headers.get(i).equals(headerName)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Convenience method to load headers in case they're not already loaded as a List so we can operate on them quickly
     *
     * @param headers the List of headers
     */
    private void setHeaders(final List<String> headers) {
        this.headers = headers;
    }

    /**
     * Return the headers of the content object as a List object.
     *
     * @return the headers of the content as a List
     */
    public List<String> getHeaders() {
        if (headers == null) {
            setHeaders(Arrays.asList(tabDelimitedContent.getTabDelimitedHeaderValues()));
        }
        return headers;
    }

    /**
     * Returns the full row and its values.  This is based on the row count <b>including the header row!</b>
     *
     * @param rowID the row number you want.  This is 0 based, so Row 4 would be ID 5.
     * @return the String[] containing all the values in a row
     */
    public String[] getRowByID(final Integer rowID) {
        return tabDelimitedContent.getTabDelimitedContents().get(rowID);
    }

    /**
     * Returns the value in a specific cell of the Tab Delimited content.  The Column represents the Y axis, the row represents the X
     * For example a column value of 2 and row value of 4 would return the value at that intersection
     *
     * @param columnNumber the column number you want to get
     * @param rowNumber    the row number who's value we're looking for.
     * @return the string at that coordinate.
     */
    public String getValueByCoordinates(final Integer columnNumber, final Integer rowNumber) {
        return tabDelimitedContent.getTabDelimitedContents().get(rowNumber)[columnNumber];
    }

    /**
     * Based on a column value, return the full column of data from each row.
     *
     * @param columnNumber the column number you want to get the data from.
     * @return Map of data values.  The key represents the row number, the value is the value in that column.
     */
    public Map<Integer, String> getFullColumnValues(final Integer columnNumber) {
        final Map<Integer, String> colValues = new HashMap<Integer, String>();
        for (final Object o : tabDelimitedContent.getTabDelimitedContents().entrySet()) {
            final Map.Entry entry = (Map.Entry) o;
            final Integer key = (Integer) entry.getKey();
            final String[] value = (String[]) entry.getValue();
            colValues.put(key, value[columnNumber]);
        }
        return colValues;
    }

    /**
     * Gets a list of column values, in order, excluding the header row.
     *
     * @param columnNum the column number to get
     * @return a List of values for the column, excluding the header row; returns empty list if no such column.
     */
    public List<String> getColumnValues(final int columnNum) {
        final List<String> values = new ArrayList<String>();
        if (columnNum >= 0 && columnNum < tabDelimitedContent.getTabDelimitedHeaderValues().length) {
            for (int i = 1; i < tabDelimitedContent.getTabDelimitedContents().size(); i++) {
                final String value = tabDelimitedContent.getTabDelimitedContents().get(i)[columnNum];
                values.add(value);
            }
        }
        return values;
    }

    /**
     * A method which returns a Map containing String[] of the columns who's values you need to work on.
     * So if you submit the values 3,24,89 then you will get a String[] with 3 slots in it, each representing the
     * column's values.<br>
     * <b>Note that row 0 is the row with the header labels</b>
     *
     * @param columnValues the list of columns you want to fetch to add to your custom String[]
     * @return the String[] containing the column data
     */
    public Map getMultipleColumnValues(final Integer... columnValues) {
        final Map<Integer, String[]> theColumnMap = new HashMap<Integer, String[]>();
        final String[] headerNames = new String[columnValues.length];
        //Setup a column number counter so we know that we're going to add the data into the next slot on each row.
        int columnNumberCounter = 0;
        //Make a call to get all the values in the column
        for (final Integer columnValue : columnValues) {
            final Map<Integer, String> theColValues = getFullColumnValues(columnValue);
            //For each value in the column add it to the Map
            for (final Object o : theColValues.entrySet()) {
                String[] rowData = new String[columnValues.length];
                final Map.Entry entry = (Map.Entry) o;
                final Integer key = (Integer) entry.getKey();
                if (theColumnMap.containsKey(key)) {
                    rowData = theColumnMap.get(key);
                    rowData[columnNumberCounter] = (String) entry.getValue();
                    theColumnMap.put(key, rowData);
                } else {
                    rowData[columnNumberCounter] = (String) entry.getValue();
                    theColumnMap.put(key, rowData);
                }
            }
            headerNames[columnNumberCounter] = getHeaders().get(columnValue);
            columnNumberCounter++;
        }
        theColumnMap.put(0, headerNames);
        return theColumnMap;
    }

    /**
     * Convenience method, same as getMultipleColumnValues with Integers, this one looks up the id of the column by name
     * so you don't have to do that first.
     *
     * @param columnNames the names of the columns you want to check against
     * @return Map with String[] of all column values.
     */
    public Map getMultipleColumnValues(final String... columnNames) {
        final Integer[] columnNumbers = new Integer[columnNames.length];
        Integer colCount = 0;
        for (final String columnName : columnNames) {
            columnNumbers[colCount] = getHeaderIDByName(columnName);
            colCount++;
        }
        return getMultipleColumnValues(columnNumbers);
    }

    /**
     * This looks for all columns with the given name, and returns a list of their locations in the header list.
     *
     * @param columnName the column name to find
     * @return a List of column numbers that have the given header name
     */
    public List<Integer> getHeaderIdsForName(final String columnName) {
        final List<Integer> columnIndices = new ArrayList<Integer>();
        final List<String> headers = getHeaders();
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).equals(columnName)) {
                columnIndices.add(i);
            }
        }
        return columnIndices;
    }

    /**
     * @return the number of rows in the tab delimited content, including the header
     */
    public int getNumRows() {
        return tabDelimitedContent.getTabDelimitedContents().size();
    }

    /**
     * Used by parser to setup comment Line Map
     *
     * @param inCommentLineMap the input commentLineMap
     */
    public void setCommentAndEmptyLineMap(final Map<Integer, Integer> inCommentLineMap) {
        this.commentAndEmptyLineMap = inCommentLineMap;
    }

    /**
     * Returns comment and empty line map to be used in MafFileValidator to get correct line no. for error
     *
     * @return commentAndEmptyLineMap return of commentAndEmptyLineMap
     */
    public Map<Integer, Integer> getCommentAndEmptyLineMap() {
        return this.commentAndEmptyLineMap;
    }

    /**
     * Add header + empty and comment line to the current row id and return
     *
     * @param row_id      id of the row currently in process
     * @param headerLines no. of lines for header
     * @return int
     */
    public int getCurrentRowIncludingEmptyAndCommentLines(final int row_id, final int headerLines) {
        final int totalCommentsOrEmptyLinesBeforeRow = getCommentAndEmptyLineMap().get(row_id);

        return row_id + totalCommentsOrEmptyLinesBeforeRow + headerLines;
    }
}
