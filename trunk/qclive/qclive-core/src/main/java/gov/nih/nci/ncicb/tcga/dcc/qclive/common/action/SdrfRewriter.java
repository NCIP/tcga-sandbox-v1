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
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidatorImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContent;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContentImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedContentNavigator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedContentWriter;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedFileParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.DirectoryListerImpl;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This rewrites the SDRF file to not include the names of replaced archives.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class SdrfRewriter extends AbstractSdrfHandler<Archive, Archive> {
	private static final Logger logger = Logger.getLogger(SdrfRewriter.class);

    public String getName() {
        return "SDRF archive name comment updater";
    }

    protected Archive doWork(final Archive archive, final QcContext context) throws ProcessorException {
        // only run this on mage-tab archives
        if (!archive.getArchiveType().equals(Archive.TYPE_MAGE_TAB)) {
            return archive;
        }
        // get archive names s to be replaced
        // key is name of replaced archive, value is what is replacing it
        final Map<String, String> replacedArchives = getReplacedArchives(context);

        final TabDelimitedContentNavigator sdrfNavigator = new TabDelimitedContentNavigator();
        sdrfNavigator.setTabDelimitedContent(archive.getSdrf());
        final StringBuilder changeFileMsg = new StringBuilder();

        final boolean madeChange = updateSDRFFile(sdrfNavigator, context, replacedArchives, changeFileMsg);

        if (madeChange) {
            final File[] sdrfFiles = DirectoryListerImpl.getFilesByExtension(archive.getDeployDirectory(), AbstractSdrfHandler.SDRF_EXTENSION);
            File sdrfFile = sdrfFiles[0];
            rewriteSDRFFile(sdrfNavigator, sdrfFile, context, changeFileMsg.toString());
        }
        return archive;
    }


    private Map<String, String> getReplacedArchives(final QcContext context) {

        final Map<String, String> replacedArchives = new HashMap<String, String>();
        for (Archive prevArchive : context.getExperiment().getPreviousArchives()) {
            for (Archive currentArchive : context.getExperiment().getArchives()) {
                if (prevArchive.getDomainName().equals(currentArchive.getDomainName()) &&
                        prevArchive.getTumorType().equals(currentArchive.getTumorType()) &&
                        prevArchive.getPlatform().equals(currentArchive.getPlatform()) &&
                        prevArchive.getSerialIndex().equals(currentArchive.getSerialIndex()) &&
                        prevArchive.getArchiveType().equals(currentArchive.getArchiveType())) {
                    replacedArchives.put(prevArchive.getRealName(), currentArchive.getRealName());
                }
            }
        }
        return replacedArchives;
    }


    private boolean updateSDRFFile(final TabDelimitedContentNavigator sdrfNavigator,
                                   final QcContext context,
                                   final Map<String, String> replacedArchives,
                                   final StringBuilder changeFileMsg) {

        boolean madeChange = false;
        List<Integer> archiveNameCols = sdrfNavigator.getHeaderIdsForName(COMMENT_ARCHIVE_NAME);

        final Map<String, Map<String,File>> splitDataMatrixFilesByOriginalDataMatrixFileByBarcode = context.getSplitDataMatrixFiles();
        final int aliquotColumnIndex = sdrfNavigator.getHeaders().indexOf(ALIQUOT_HEADER);

        for (int row = 1; row < sdrfNavigator.getNumRows(); row++) {
            for (Integer col : archiveNameCols) {
                String archiveName = sdrfNavigator.getValueByCoordinates(col, row);
                if (replacedArchives.get(archiveName) != null) {
                    // replace the value in this cell of the SDRF
                    sdrfNavigator.getTabDelimitedContent().getTabDelimitedContents().get(row)[col] = replacedArchives.get(archiveName);
                    changeFileMsg.append("Updated references to previous archives in 'Comment [Archive Name]");
                    madeChange = true;
                }
            }
            // if there are split data matrix files, then update the corresponding aliquots data matrix file reference
            // to split data matrix filename
            if(updateSDRFFileWithSplitDataMatrixFileNames(splitDataMatrixFilesByOriginalDataMatrixFileByBarcode,
                                                          sdrfNavigator,
                                                          aliquotColumnIndex,
                                                          row)){
                changeFileMsg.append("\n Updated file references for aliquot ")
                        .append(sdrfNavigator.getValueByCoordinates(aliquotColumnIndex, row));
                madeChange = true;
            }
        }

        return madeChange;
    }

    /**
     * Update SDRF file with new data matrix file references
     * @param sdrfFile
     * @param context
     * @return Map<String,String> barcodesByHybRefId
     * @throws ProcessorException
     */

    public Map<String,String> updateSDRFFile(final File sdrfFile,
                                             final QcContext context) throws  ProcessorException{

        final TabDelimitedContent sdrf = new TabDelimitedContentImpl();
        TabDelimitedFileParser sdrfParser = new TabDelimitedFileParser();
        sdrfParser.setTabDelimitedContent( sdrf );
        try {
            sdrfParser.loadTabDelimitedContent( sdrfFile ,true);
        }
        catch(IOException e) {
            throw new ProcessorException( new StringBuilder().append( "Failed to parse SDRF '" ).
                    append( sdrfFile.getName() ).append( "' (" ).append( e.getMessage() ).append( ")" ).toString(), e );
        }catch(ParseException e){
            throw new ProcessorException(new StringBuilder().append("Failed to parse SDRF ").append(sdrfFile.getName()).toString(),e);
        }
        sdrfParser.loadTabDelimitedContentHeader();

        final TabDelimitedContentNavigator sdrfNavigator = new TabDelimitedContentNavigator();
        sdrfNavigator.setTabDelimitedContent(sdrf);
        final Map<String, Map<String,File>> splitDataMatrixFilesByOriginalDataMatrixFileByBarcode = context.getSplitDataMatrixFiles();
        final int aliquotColumnIndex = sdrfNavigator.getHeaders().indexOf(ALIQUOT_HEADER);
        final int barcodeIndex = sdrfNavigator.getHeaders().indexOf(EXTRACT_NAME);

        final Map<String,String> barcodesByHybRefId = new HashMap<String,String>();

        for (int row = 1; row < sdrfNavigator.getNumRows(); row++) {
            // if there are split data matrix files, then update the corresponding aliquots data matrix file reference
            // to split data matrix filename

            if(updateSDRFFileWithSplitDataMatrixFileNames(splitDataMatrixFilesByOriginalDataMatrixFileByBarcode,
                                                          sdrfNavigator,
                                                          aliquotColumnIndex,
                                                          row)){
                final String barcode = sdrfNavigator.getValueByCoordinates(barcodeIndex, row);
                if(CommonBarcodeAndUUIDValidatorImpl.ALIQUOT_BARCODE_PATTERN.matcher(barcode).matches()){
                    barcodesByHybRefId.put(sdrfNavigator.getValueByCoordinates(aliquotColumnIndex, row),barcode);
                }
            }
        }
        // update SDRF File
        if(barcodesByHybRefId.size() > 0){
            rewriteSDRFFile(sdrfNavigator,sdrfFile,context,"");
        }
        return barcodesByHybRefId;
    }

    private boolean updateSDRFFileWithSplitDataMatrixFileNames(final Map<String, Map<String,File>> splitDataMatrixFilesByOriginalDataMatrixFileByBarcode ,
                                                            final TabDelimitedContentNavigator sdrfNavigator,
                                                            final int aliquotColumnIndex,
                                                            final int row){
        if (splitDataMatrixFilesByOriginalDataMatrixFileByBarcode != null &&
            aliquotColumnIndex != -1 &&
            splitDataMatrixFilesByOriginalDataMatrixFileByBarcode.keySet().contains(sdrfNavigator.getValueByCoordinates(aliquotColumnIndex, row))) {
            final String matchedAliquot = sdrfNavigator.getValueByCoordinates(aliquotColumnIndex, row);

            int fileNameColumnIndex = -1;
            List<String> rowData = Arrays.asList(sdrfNavigator.getRowByID(row));
            String splitDataMatrixFileName = "";
            final StringBuilder originalDataMatrixFilNames = new StringBuilder();
            for(String originalDataMatrixFileName: splitDataMatrixFilesByOriginalDataMatrixFileByBarcode.get(matchedAliquot).keySet()){
                originalDataMatrixFilNames.append(originalDataMatrixFileName)
                .append(",");
                if((fileNameColumnIndex = rowData.indexOf(originalDataMatrixFileName)) != -1){
                    splitDataMatrixFileName = splitDataMatrixFilesByOriginalDataMatrixFileByBarcode.get(matchedAliquot).get(originalDataMatrixFileName).getName();
                    break;
                }
            }
            if(fileNameColumnIndex == -1){
                final StringBuilder errorMsg = (new StringBuilder("Line no: ")
                        .append(row)
                        .append("Could not locate data matrix filename '")
                        .append( originalDataMatrixFilNames.toString())
                        .append("' for aliquot ")
                        .append(sdrfNavigator.getValueByCoordinates(aliquotColumnIndex, row))
                        .append("in the sdrf file"));
                throw new RuntimeException(errorMsg.toString());
            }
            sdrfNavigator.getTabDelimitedContent().getTabDelimitedContents().get(row)[fileNameColumnIndex] = splitDataMatrixFileName;
            return true;
        }
        return false;
    }

    public void rewriteSDRFFile(final TabDelimitedContentNavigator sdrfNavigator,
                                 final File sdrfFile,
                                 final QcContext context,
                                 final String changeFileMsg
    ) throws ProcessorException {
        try {
            context.aboutToChangeFile(sdrfFile, changeFileMsg);
            TabDelimitedContentWriter writer = new TabDelimitedContentWriter();
            writer.writeToFile(sdrfNavigator.getTabDelimitedContent(), sdrfFile);
        } catch (IOException e) {
            throw new ProcessorException(new StringBuilder().append("Failed to write updated SDRF: ").append(e.getMessage()).toString());
        } catch (NoSuchAlgorithmException e) {
            throw new ProcessorException(new StringBuilder().append("Failed to write updated SDRF: ").append(e.getMessage()).toString());
        }
    }


}
