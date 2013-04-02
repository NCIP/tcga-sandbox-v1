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
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ManifestValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Creates a README file for the Archive, adds it to the directory and to the MANIFEST.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class ArchiveReadmeCreator extends AbstractProcessor<Archive, Archive> {
    private ManifestParser manifestParser;
    private static final String README_FILENAME = "README_DCC.txt";
    public static final String DATE_FORMAT_FOR_README = "MM/dd/yyyy";
    public static final int LINE_WIDTH = 100;
    private static final String NEWLINE = "\n";
    private static final String ORDINAL_SUFFIX_TH = "th";
    private static final String ORDINAL_SUFFIX_ST = "st";
    private static final String ORDINAL_SUFFIX_ND = "nd";
    private static final String ORDINAL_SUFFIX_RD = "rd";
    private static final String NAN = "NaN";

    @Override
    protected Archive doWork(final Archive archive, final QcContext context) throws ProcessorException {
        final String readmeText = createReadmeText(archive);
        final File readmeFile = makeReadmeFile(archive);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(readmeFile);
            fileWriter.write(readmeText);
            manifestParser.addFileToManifest(readmeFile, getArchiveManifestFile(archive));

        } catch (IOException e) {
            context.addError(MessageFormat.format(MessagePropertyType.ARCHIVE_PROCESSING_ERROR, archive, e.getMessage()));        
        } catch (NoSuchAlgorithmException e) {
            context.addError(MessageFormat.format(MessagePropertyType.ARCHIVE_PROCESSING_ERROR, archive, e.getMessage()));
        } catch (ParseException e) {
            context.addError(MessageFormat.format(MessagePropertyType.ARCHIVE_PROCESSING_ERROR, archive, e.getMessage()));
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return archive;
    }

    protected File getArchiveManifestFile(final Archive archive) {
        return new File( archive.getDeployDirectory(), ManifestValidator.MANIFEST_FILE );
    }

    protected File makeReadmeFile(final Archive archive) {
        return new File(archive.getDeployDirectory(), README_FILENAME);
    }

    public String getName() {
        return "archive README creator";
    }

    /**
     * Generates the text for the DCC README file for the given archive.  Uses information contained in the archive object.
     * @param archive the archive
     * @return readme content
     */
    public static String createReadmeText(final Archive archive) {
        final Calendar now = Calendar.getInstance();
        final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_FOR_README);

        final StringBuilder readmeText = new StringBuilder();
        readmeText.append(formatParagraph(LINE_WIDTH, "README for archive ", archive.getRealName()));
        readmeText.append(NEWLINE);
        readmeText.append(formatParagraph(LINE_WIDTH, "This file was automatically generated on ", dateFormat.format(now.getTime()),
                " using the TCGA DCC Archive Processing System."));
        readmeText.append(NEWLINE);
        readmeText.append(formatParagraph(LINE_WIDTH,
                "The Cancer Genome Atlas (TCGA), a project of the National Cancer Institute and the National Human ",
                "Genome Research Institute (NHGRI), is the foundation of a large-scale collaborative effort to ",
                "understand the genomic changes that occur in cancer. For more information, see ",
                "http://cancergenome.nih.gov/dataportal/index.asp."));
        readmeText.append(NEWLINE);
        readmeText.append(formatParagraph(LINE_WIDTH,
                "The files in this archive contain data derived from tissue samples of patients that were diagnosed ",
                "with ", archive.getTheTumor().getTumorDescription(), ". ",
                "The data was produced by the ", archive.getTheCenter().getCenterDisplayName(),
                " (", archive.getTheCenter().getCenterName(), ") ",
                Experiment.getDescriptionForType(archive.getExperimentType()), " (",
                archive.getExperimentType(), ") using the ", archive.getThePlatform().getPlatformDisplayName(), " platform."));
        readmeText.append(NEWLINE);
        readmeText.append(formatParagraph(LINE_WIDTH, "This archive is the ",
                archive.getRevision().equals("0") ? "initial version " : getOrdinal(archive.getRevision()) + " revision ",
                "of the ", getOrdinal(archive.getSerialIndex()), " archive and contains only ",
                Archive.getDescriptionForType(archive.getArchiveType()), " data."));
        readmeText.append(NEWLINE);
        readmeText.append(formatParagraph(LINE_WIDTH,
                "The MANIFEST.txt lists all the files, and their MD5 signatures, that should be included in this complete archive."));
        readmeText.append(NEWLINE);
        readmeText.append(formatParagraph(LINE_WIDTH,
                "A DESCRIPTION.txt file may be included by the submitting center that provides details about the data ",
                "files included in this archive."));
        readmeText.append(NEWLINE);
        readmeText.append(formatParagraph(LINE_WIDTH,
                "The TCGA Data Primer provides an in depth description of TCGA data enterprise including data ",
                "classification and organization (including data types and data levels), how to access the data, ",
                "and a description of some possible ways to aggregate TCGA data. ",
                "http://tcga-data.nci.nih.gov/docs/TCGA_Data_Primer.pdf"));
        readmeText.append(NEWLINE);
        readmeText.append(formatParagraph(LINE_WIDTH,
                "For even more information, please visit the TCGA Data Portal at http://tcga-data.nci.nih.gov"));
        readmeText.append(NEWLINE);
        readmeText.append(formatParagraph(LINE_WIDTH,
                "For help, please contact NCICB Support http://ncicb.nci.nih.gov/NCICB/support"));

       return readmeText.toString();
    }

    /**
     * Formats a paragraph for fixed line width by inserting newlines at appropriate intervals.  Also adds a newline
     * at the end of the paragraph. Note: newlines are only added between words, so most lines will be shorter than
     * the line width.
     * 
     * @param lineWidth the maximum number of characters per line
     * @param paragraphParts the parts of the paragraph
     * @return the formatted paragraph in string form.
     */
    public static String formatParagraph(int lineWidth, String... paragraphParts) {
        StringBuilder paragraph = new StringBuilder();
        for (String paragraphPart : paragraphParts) {
            paragraph.append(paragraphPart);
        }

        String[] words = paragraph.toString().split("[ ]+"); // this will keep tabs and newlines intact as though they are part of a longer word
        StringBuilder formattedParagraph = new StringBuilder();
        int currentLineLength = 0;
        for (String word : words) {
            if (currentLineLength + word.length() + 1 > lineWidth && currentLineLength > 0) {
                formattedParagraph.append(NEWLINE);
                currentLineLength = 0;
            } else if (currentLineLength > 0) {
                formattedParagraph.append(" ");
                currentLineLength++;
            }
            formattedParagraph.append(word);
            currentLineLength += word.length();
        }
        formattedParagraph.append(NEWLINE);
        return formattedParagraph.toString();
    }

    /**
     * Gets the ordinal number representation of the given number (for example, "1st" if "1" is passed in).
     *  If the string passed in is not a number, returns "NaN".
     *
     * @param number the number to get the ordinal of
     * @return the number in ordinal form
     */
    public static String getOrdinal(final String number) {
        try {
            final int value = Integer.valueOf(number);

            // algorithm taken from from http://www.javalobby.org/forums/thread.jspa?threadID=16906&tstart=0
            final int hundredRemainder = value % 100;
            final int tenRemainder = value % 10;

            // this means the number is a teen, which like teen humans need special handling
            if(hundredRemainder - tenRemainder == 10) {
                return number + ORDINAL_SUFFIX_TH;
            }

            switch (tenRemainder) {
                case 1:
                    return number + ORDINAL_SUFFIX_ST;
                case 2:
                    return number + ORDINAL_SUFFIX_ND;
                case 3:
                    return number + ORDINAL_SUFFIX_RD;
                default:
                    return number + ORDINAL_SUFFIX_TH;
            }
        } catch (NumberFormatException e) {
            return NAN;
        }
    }

    public void setManifestParser(final ManifestParser manifestParser) {
        this.manifestParser = manifestParser;
    }
}
