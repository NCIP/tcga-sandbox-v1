/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import org.apache.commons.io.IOUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Robert S. Sfeir
 * @version 1.0
 *          <p/>
 *          A class file to scan emails sent from GForge to turn them into a comma delimited file for import into Jira.
 */
public class IssueScanner {
    private Scanner fileScanner;
    private final ArrayList<Issue> issueList = new ArrayList<Issue>();
    private Issue theIssue;
    private static final String FILE_SYSTEM_LOCATION = System.getProperty("user.dir") + File.separator;
    private static final String QUOTE = "\"";
    private static final String COMMA = ",";

    /**
     * Scan a file containing emails from gforge. Simply reads the file line by line and allows us to parse each line
     * for specific values we're interested in importing into Jira.
     *
     * @param theIssueFile the file we want to scan
     * @throws IOException thrown when the file is not found, or we could not write a file out when done.
     */
    public void scanFile(final File theIssueFile) throws IOException {
        try {
            fileScanner = new Scanner(theIssueFile);
            while (fileScanner.hasNextLine()) {
                processTheLine(fileScanner.nextLine());
            }
        } finally {
            if (issueList.size() > 0) {
                writeCommaDelimitedFile();
            }
            fileScanner.close();
        }
    }

    public static void main(final String args[]) throws IOException {
        final IssueScanner is = new IssueScanner();
        is.scanFile(new File(FILE_SYSTEM_LOCATION + args[0]));
    }

    /**
     * Looks at the string on the line and determined certain values from it and deals with them appropriately. There is
     * one special case and that is when we hit the Comment: section.  That section is multiple lines and we need to
     * tell the scanner to keep going.  Because of that we force a scan read forward until the end of the emai. While
     * the scanner is going forward we're adding the comments to a String Builder before adding it to the comments field
     * in the issue bean.
     *
     * @param theLine the line we just read and we need to parse out.
     */
    private void processTheLine(final String theLine) {
        if (theIssue == null) {
            theIssue = new Issue();
        }
        if (isAMatch("Status:", theLine.trim())) {
            //This status will be mapped in Jira to one that you think is appropriate.  Generally, we use Needs Triage
            //as our starting entry point.
            theIssue.setStatus(fieldValue(theLine));
        } else if (isAMatch("Priority:", theLine.trim())) {
            theIssue.setPriority(fieldValue(theLine));
        } else if (isAMatch("Summary:", theLine.trim())) {
            theIssue.setSummary(fieldValue(theLine));
        } else if (isAMatch("Submitted By:", theLine.trim())) {
            theIssue.setSubmittedBy(fieldValue(theLine));
        } else if (isAMatch("Product:", theLine.trim())) {
            theIssue.setProduct(fieldValue(theLine));
        } else if (isAMatch("Component:", theLine.trim())) {
            theIssue.setComponent(fieldValue(theLine));
        } else if (isAMatch("Submitted By:", theLine.trim())) {
            theIssue.setSubmittedBy(fieldValue(theLine));
        } else if (isAMatch("Initial Comment:", theLine.trim())) {
            //This is a special case because after the comment comes a bunch of info which we need to grab into this one field.
            //We're going to try to keep reading the lines until we get to the end of the email and then break.
            final StringBuilder theFullComment = new StringBuilder();
            while (fileScanner.hasNextLine()) {
                final String theLineToAdd = fileScanner.nextLine();
                if (!isAMatch("You can respond by visiting:", theLineToAdd)) {
                    //Since we're reading line by line, be sure to add a \n to the end of it that will give us a clean
                    //format in Jira.
                    theFullComment.append(theLineToAdd).append("\n");
                } else {
                    //We found the end of the email
                    //Add the issue to the list, clear the issue object and break so we go back to reading.
                    theIssue.setComment(theFullComment.toString());
                    issueList.add(theIssue);
                    theIssue = null;
                    break;
                }
            }
        }
    }

    /**
     * Checks to see if a particular string matches a value we're looking for and returns a boolean. We only need to
     * look at the start of a line since all fields generated by gforge in the email are always at the start of a line.
     *
     * @param lookFor             the value we're interested in.
     * @param theStringToLookInto the String that contains the value
     * @return true or false based on a match.
     */
    private static boolean isAMatch(final String lookFor, final String theStringToLookInto) {
        return theStringToLookInto.startsWith(lookFor);
    }

    /**
     * Return the value after the field name we're looking for and trims out any excess starting or trailing spaces.
     *
     * @param theLine theLine we want to get the value for.
     * @return the value of the field that is contained after the : .
     */
    private static String fieldValue(final String theLine) {
        return theLine.substring(theLine.indexOf(":") + 1).trim();
    }

    /**
     * Call this when you're done creating all the Issues.  This creates an output stream and writes out the beans one
     * by one calling each bean's toString() which returns a comma delimited string of the values contained in that
     * bean. This method also prints out the header of the file before looping over the beans.
     *
     * @throws java.io.IOException if we're not able to write the file out.
     */
    private void writeCommaDelimitedFile() throws IOException {

        AtomicReference<BufferedWriter> out = null;
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            // Create file
            //noinspection IOResourceOpenedButNotSafelyClosed
            fileWriter = new FileWriter(FILE_SYSTEM_LOCATION + "gforge-issues" + new Date().toString() + ".txt");
            final AtomicReference<FileWriter> fstream = new AtomicReference<FileWriter>(fileWriter);
            //noinspection IOResourceOpenedButNotSafelyClosed
            bufferedWriter = new BufferedWriter(fstream.get());
            out = new AtomicReference<BufferedWriter>(bufferedWriter);
            out.get().write(getHeader());
            for (final Issue anIssue : issueList) {
                out.get().write(anIssue.toString());
            }
        } finally {
            out.get().close();
            IOUtils.closeQuietly(fileWriter);
            IOUtils.closeQuietly(bufferedWriter);
        }
    }

    /**
     * Creates the header for the issue file with comma delimited values.
     *
     * @return the fields for the header separated by commas encapsulated in quotes.
     */
    public static String getHeader() {
        final StringBuilder builtIssueString = new StringBuilder();
        return builtIssueString
                .append(QUOTE + "Summary" + QUOTE).append(COMMA)
                .append(QUOTE + "Assignee" + QUOTE).append(COMMA)
                .append(QUOTE + "Component" + QUOTE).append(COMMA)
                .append(QUOTE + "Product" + QUOTE).append(COMMA)
                .append(QUOTE + "Priority" + QUOTE).append(COMMA)
                .append(QUOTE + "Status" + QUOTE).append(COMMA)
                .append(QUOTE + "Reporter" + QUOTE).append(COMMA)
                .append(QUOTE + "Description" + QUOTE).append("\n").toString();
    }
}


