package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedContentNavigator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcliveCloseableUtil.close;

/**
 * Validates protein array level two data file contents.  For now, this is only checking the header row.
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ProteinArrayLevelTwoFileHeaderValidator extends AbstractProcessor<Archive, Boolean> {

    /**
     * Gets the name of the processor, in descriptive English.
     *
     * @return the descriptive name of this processor
     */
    @Override
    public String getName() {
        return "validation of MDA_RPPA_Core level 2 files";
    }

    /**
     * If this is a protein array level 2 archive, validates that the first line contains names of valid level 1 files
     * referenced in the SDRF.
     *
     * @param archive   the archive to validate
     * @param context the context for this QC call
     * @return true if the archive has valid protein level 2 files, or if this is not a protein level 2 archive
     * @throws gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException if there is an unrecoverable error
     */
    @Override
    protected Boolean doWork(final Archive archive, final QcContext context) throws ProcessorException {
        boolean isValid = true;

        // if this the right kind of archive...
        if (Archive.TYPE_LEVEL_2.equals(archive.getArchiveType()) &&
                ProteinArrayLevelThreeDataFileValidator.PROTEIN_ARRAY_PLATFORM.equals(archive.getPlatform())) {

            if (context.getSdrf() == null) {
                throw new ProcessorException("SDRF not set in context -- can't validate files");
            }

            final TabDelimitedContentNavigator sdrfNavigator = new TabDelimitedContentNavigator();
            sdrfNavigator.setTabDelimitedContent(context.getSdrf());

            // get the lists of level 1 files and level 2 files from the SDRF
            final List<String> level1Files = getFilesForCommentValue(sdrfNavigator, "Comment [TCGA Data Level]", "Level 1");
            final List<String> level2Files = getFilesForCommentValue(sdrfNavigator, "Comment [TCGA Data Level]", "Level 2");

            final File archiveDir = new File(archive.getDeployDirectory());
            for (final String level2Filename : level2Files) {
                final File level2File = new File(archiveDir, level2Filename);
                // not all level 2 files might be in this archive -- they might be in a different archive.  only check
                // if they are part of this archive...
                if (level2File.exists()) {
                    isValid = checkLevel2FileHeader(level2File, level1Files, context) && isValid;
                    isValid = checkLevel2FileTabDelimited(level2File, context) && isValid;
                }
            }
        }
        return isValid;
    }

    private boolean checkLevel2FileTabDelimited(final File level2File, final QcContext context) throws ProcessorException {
        boolean isValid = true;
        BufferedReader reader = null;
        Logger logger = null;
        try {
            logger = context.getLogger();
            //noinspection IOResourceOpenedButNotSafelyClosed
            reader = new BufferedReader(new FileReader(level2File));
            String line = "";
            int lineNum = 1;
            while((line = reader.readLine()) != null) {
                final String[] tokens = line.split("\\t", -1);
                if(tokens == null || tokens.length <= 1) {
                    context.addError("level 2 file " + level2File.getName() + " is not tab delimited at line number : '" + lineNum + "'");
                    isValid = false;
                }
                lineNum++;
            }

        } catch (FileNotFoundException e) {
            throw new ProcessorException(e.getMessage(), e);
        } catch (IOException e) {
            throw new ProcessorException(e.getMessage(), e);
        } finally {
            close(reader, logger);
        }

        return isValid;
    }

    private void close2(BufferedReader reader, Logger logger) {
        try{
            if(reader != null) {
                reader.close();
            }

        } catch (final IOException e) {

            if(logger != null) {
                logger.log(Level.WARN, e.getMessage());
            }
        }
    }

    /*
     * Check the headers to see if all tokens in it after the first two (which are reserved) are valid level 1 filenames.
     */
    private boolean checkLevel2FileHeader(final File level2File, final List<String> level1Files, final QcContext context) throws ProcessorException {

        BufferedReader reader = null;
        try {
            // open the file and parse the first line by tab character
            //noinspection IOResourceOpenedButNotSafelyClosed
            reader = new BufferedReader(new FileReader(level2File));
            final String headerLine = reader.readLine();
            final String[] headers = headerLine.split("\\t", -1);

            // for all headers starting with the 3rd one, check for inclusion in level 1 file list
            boolean headerIsValid = true;
            if (headers.length > 2) {
                for (int i=2; i<headers.length; i++) {
                    final String level1Filename = headers[i];
                    if (!level1Files.contains(level1Filename)) {
                        // error if can't find, but continue to check for additional errors
                        context.addError("level 2 file " + level2File.getName() + " refers to '" + level1Filename + "' in its header, but that is not a level 1 file listed in the SDRF");
                        headerIsValid = false;
                    }
                }
            } else if (headers.length != 1) {
                context.addError("level 2 file " + level2File.getName() + " must contain at least three columns (identifier columns, then data columns");
                headerIsValid = false;
            }
            return headerIsValid;

        } catch (FileNotFoundException e) {
            throw new ProcessorException(e.getMessage(), e);
        } catch (IOException e) {
            throw new ProcessorException(e.getMessage(), e);
        } finally {
            close(reader, context.getLogger());
        }
    }

    /**
     * Gets a list of filenames from the SDRF that have a certain value for a certain Comment column.  If a file column
     * does not have the given comment column it will be ignored.  For example, you can use this to find all values for
     * file columns that have comment columns labeling them as Level 2.
     *
     * @param sdrfNavigator the SDRF
     * @param commentHeader the comment column header to look for
     * @param commentValue the value of the comment column to match
     * @return a list of filenames from file column rows that match the comment value in their comment column
     */
    private List<String> getFilesForCommentValue(final TabDelimitedContentNavigator sdrfNavigator,
                                                 final String commentHeader, final String commentValue) {
        final List<String> files = new ArrayList<String>();

        // key = column position of file column, value = column position of comment column of interest
        Map<Integer, Integer> fileColumnToCommentColumn = new HashMap<Integer, Integer>();
        for (int i=0; i<sdrfNavigator.getHeaders().size(); i++) {
            final String header = sdrfNavigator.getHeaders().get(i);

            // if find a File column, go forward until next File column, looking for the specificed comment column
            if (header.endsWith("File") && i < sdrfNavigator.getHeaders().size()-1) {
                final Integer fileIndex = i;
                String nextHeader;
                while(i < sdrfNavigator.getHeaders().size()-1) {
                    nextHeader = sdrfNavigator.getHeaders().get(i+1);
                    if (nextHeader.endsWith("File")) {
                        // found next File column, continue
                        break;
                    } else if (nextHeader.equals(commentHeader)) {
                        // found desired comment column, get value for this row and save it in list
                        fileColumnToCommentColumn.put(fileIndex, i+1);
                    }
                    i++;
                }
            }
        }

        // go through all data rows of SDRF and, for each File/Comment column pair,
        // check if the comment value is the one we are looking for.  If it is, save the file column value.
        for (int i=1; i<sdrfNavigator.getNumRows(); i++) {
            for (final Integer fileColumnIndex : fileColumnToCommentColumn.keySet()) {
                final Integer commentColumnIndex = fileColumnToCommentColumn.get(fileColumnIndex);
                if (sdrfNavigator.getValueByCoordinates(commentColumnIndex, i).equals(commentValue)) {
                    files.add(sdrfNavigator.getValueByCoordinates(fileColumnIndex, i));
                }
            }
        }

        return files;
    }
}
