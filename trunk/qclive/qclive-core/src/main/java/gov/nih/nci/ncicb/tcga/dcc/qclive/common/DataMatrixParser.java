/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Parser for DataMatrix files.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class DataMatrixParser {

    /**
     * Parses a file into a DataMatrix object.  If the file is not properly formatted, an exception will be thrown.
     * See DataMatrix class for details on what information about each file is saved in the object.
     *
     * @param filename  the name of the file containing the data matrix
     * @param directory the directory where the file is contained
     * @return a DataMatrix object parsed from the given file
     * @throws DataMatrixParseError if the data matrix was not formatted properly and could not be parsed
     * @throws IOException          if there was an error opening or reading the file
     */
    public DataMatrix parse(final String filename, final String directory) throws DataMatrixParseError, IOException {
        // set up DataMatrix object
        final DataMatrix matrix = new DataMatrix();
        matrix.setFilename(filename);
        matrix.setFile(new File(directory, filename));
        // open the file
        FileReader fReader = new FileReader(matrix.getFile());
        BufferedReader bufferedReader = new BufferedReader(fReader);
        try {
            // read and parse headers
            final String majorHeaderStr = bufferedReader.readLine();
            final String minorHeaderStr = bufferedReader.readLine();
            parseHeaders(majorHeaderStr, minorHeaderStr, matrix);
            String lineStr;
            String[] line;
            // parse data lines, counting number of reporters
            int reporterCount = 0;
            while ((lineStr = bufferedReader.readLine()) != null) {
                line = lineStr.split("\t", -1);  // set limit to -1 to indicate trailing tabs count as elements
                if (line.length != 1 + matrix.getConstantTypes().length + matrix.getQuantitationTypes().length) {
                    throw new DataMatrixParseError("Line did not contain expected number of elements: " + lineStr);
                }
                reporterCount++;
            }
            matrix.setNumReporters(reporterCount);
        }
        finally {
            bufferedReader.close();
            fReader.close();
            bufferedReader = null;
            fReader = null;
        }
        return matrix;
    }

    void parseHeaders(final String majorHeader, final String minorHeader, final DataMatrix matrix)
            throws DataMatrixParseError {
        final String[] majorHeaderLine = majorHeader.split("\t", -1);
        if (majorHeaderLine.length < 2) {
            throw new DataMatrixParseError("Major header line does not have expected format -- should be tab-delimited");
        }
        matrix.setNameType(majorHeaderLine[0]);
        int numConstants = 0;
        for (int i = 1; i < majorHeaderLine.length; i++) {
            if (majorHeaderLine[i].equals("")) {
                numConstants++;
            } else {
                // if not blank, don't look for any more constants
                i = majorHeaderLine.length;
            }
        }
        // number of names is number of elements minus the number of constants minus 1 for the name type
        final String[] names = new String[majorHeaderLine.length - numConstants - 1];
        System.arraycopy(majorHeaderLine, numConstants + 1, names, 0, names.length);
        matrix.setNames(names);
        // now parse the minor header
        final String[] minorHeaderLine = minorHeader.split("\t", -1);
        if (minorHeaderLine.length != majorHeaderLine.length) {
            throw new DataMatrixParseError(new StringBuilder().append("Major and minor headers (lines 1 and 2) do not have the same number of elements. Major header has ").append(majorHeaderLine.length).append(" but minor header line has ").append(minorHeaderLine.length).toString());
        }
        matrix.setReporterType(minorHeaderLine[0]);
        // constants
        final String[] constantTypes = new String[numConstants];
        System.arraycopy(minorHeaderLine, 1, constantTypes, 0, numConstants);
        matrix.setConstantTypes(constantTypes);
        // the rest are quantitation types
        final String[] qTypes = new String[minorHeaderLine.length - numConstants - 1];
        System.arraycopy(minorHeaderLine, numConstants + 1, qTypes, 0, qTypes.length);
        matrix.setQuantitationTypes(qTypes);
    }

    /**
     * Exception used to indicate error parsing a data matrix file.
     */
    public class DataMatrixParseError extends Exception {
        /**
         * Constructs an exception with the given message.
         *
         * @param msg the error message
         */
        public DataMatrixParseError(final String msg) {
            super(msg);
        }
    }
}
