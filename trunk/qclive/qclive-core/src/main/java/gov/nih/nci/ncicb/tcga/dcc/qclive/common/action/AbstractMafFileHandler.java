/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContent;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContentImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedContentNavigator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedFileParser;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract base class for Maf File handlers.
 *
 * @author Jessica Chen
 *         Last updated by:  $Author$
 * @version $Rev: 3419 $
 */
public abstract class AbstractMafFileHandler<O> extends AbstractProcessor<File, O> {

    public static final String COMMENT_LINE_TOKEN = "#";

    public final static String FIELD_HUGO_SYMBOL = "Hugo_Symbol";
    public final static String FIELD_ENTREZ_GENE_ID = "Entrez_Gene_Id";
    public final static String FIELD_CENTER = "GSC_Center";
    public final static String FIELD_CENTER_NAME = "Center";
    public final static String FIELD_NCBI_BUILD = "NCBI_Build";
    public final static String FIELD_CHROMOSOME = "Chromosome";
    public final static String FIELD_START_POSITION = "Start_position";
    public final static String FIELD_END_POSITION = "End_position";
    public final static String FIELD_STRAND = "Strand";
    public final static String FIELD_VARIANT_CLASSIFICATION = "Variant_Classification";
    public final static String FIELD_VARIANT_TYPE = "Variant_Type";
    public final static String FIELD_REFERENCE_ALLELE = "Reference_Allele";
    public final static String FIELD_TUMOR_SEQ_ALLELE1 = "Tumor_Seq_Allele1";
    public final static String FIELD_TUMOR_SEQ_ALLELE2 = "Tumor_Seq_Allele2";
    public final static String FIELD_DBSNP_RS = "dbSNP_RS";
    public final static String FIELD_DBSNP_VAL_STATUS = "dbSNP_Val_Status";
    public final static String FIELD_TUMOR_SAMPLE_UUID = "Tumor_Sample_UUID";
    public final static String FIELD_TUMOR_SAMPLE_BARCODE = "Tumor_Sample_Barcode";
    public final static String FIELD_MATCHED_NORM_SAMPLE_UUID = "Matched_Norm_Sample_UUID";
    public final static String FIELD_MATCHED_NORM_SAMPLE_BARCODE = "Matched_Norm_Sample_Barcode";
    public final static String FIELD_MATCH_NORM_SEQ_ALLELE1 = "Match_Norm_Seq_Allele1";
    public final static String FIELD_MATCH_NORM_SEQ_ALLELE2 = "Match_Norm_Seq_Allele2";
    public final static String FIELD_TUMOR_VALIDATION_ALLELE1 = "Tumor_Validation_Allele1";
    public final static String FIELD_TUMOR_VALIDATION_ALLELE2 = "Tumor_Validation_Allele2";
    public final static String FIELD_MATCH_NORM_VALIDATION_ALLELE1 = "Match_Norm_Validation_Allele1";
    public final static String FIELD_MATCH_NORM_VALIDATION_ALLELE2 = "Match_Norm_Validation_Allele2";
    public final static String FIELD_VERIFICATION_STATUS = "Verification_Status";
    public final static String FIELD_VALIDATION_STATUS = "Validation_Status";
    public final static String FIELD_MUTATION_STATUS = "Mutation_Status";
    public final static String FIELD_VALIDATION_METHOD = "Validation_Method";
    public final static String FIELD_SEQUENCING_PHASE = "Sequencing_Phase";
    public static final String FIELD_MAF2_END_POSITION = "End_Position";
    public static final String FIELD_MAF2_START_POSITION = "Start_Position";
    protected static final String FIELD_SEQUENCE_SOURCE = "Sequence_Source";
    protected static final String FIELD_SEQUENCER = "Sequencer";
    protected static final String FIELD_SCORE = "Score";
    protected static final String FIELD_BAM_FILE = "BAM_File";

    protected final static List<String> MAF_FIELDS = Arrays.asList(
            FIELD_HUGO_SYMBOL,
            FIELD_ENTREZ_GENE_ID,
            FIELD_CENTER,       // Supported only in first gen maf archives
            FIELD_CENTER_NAME,  // Supported only in next gen maf archives
            FIELD_NCBI_BUILD,
            FIELD_CHROMOSOME,
            FIELD_START_POSITION,  // Supported only in first gen maf archives
            FIELD_MAF2_START_POSITION,  // Supported only in next gen maf archives
            FIELD_END_POSITION,     // Supported only in first gen maf archives
            FIELD_MAF2_END_POSITION, // Supported only in next gen maf archives
            FIELD_STRAND,
            FIELD_VARIANT_CLASSIFICATION,
            FIELD_VARIANT_TYPE,
            FIELD_REFERENCE_ALLELE,
            FIELD_TUMOR_SEQ_ALLELE1,
            FIELD_TUMOR_SEQ_ALLELE2,
            FIELD_DBSNP_RS,
            FIELD_DBSNP_VAL_STATUS,
            FIELD_TUMOR_SAMPLE_BARCODE,
            FIELD_MATCHED_NORM_SAMPLE_BARCODE,
            FIELD_MATCH_NORM_SEQ_ALLELE1,
            FIELD_MATCH_NORM_SEQ_ALLELE2,
            FIELD_TUMOR_VALIDATION_ALLELE1,
            FIELD_TUMOR_VALIDATION_ALLELE2,
            FIELD_MATCH_NORM_VALIDATION_ALLELE1,
            FIELD_MATCH_NORM_VALIDATION_ALLELE2,
            FIELD_VERIFICATION_STATUS,
            FIELD_VALIDATION_STATUS,
            FIELD_MUTATION_STATUS,
            FIELD_SEQUENCING_PHASE,
            FIELD_SEQUENCE_SOURCE, // Supported only in next gen maf archives
            FIELD_VALIDATION_METHOD, // for first gen archives, this field should be before FIELD_SEQUENCING_PHASE
            FIELD_SCORE,    // Supported only in next gen maf archives
            FIELD_BAM_FILE, // Supported only in next gen maf archives
            FIELD_SEQUENCER, // Supported only in next gen maf archives
            FIELD_TUMOR_SAMPLE_UUID,   // Supported only in UUID converted archives
            FIELD_MATCHED_NORM_SAMPLE_UUID // Supported only in UUID converted archives

    );

    public static final String MAF_EXTENSION = ".maf";

    private static ThreadLocal<Boolean> isCenterConvertedToUUID = new ThreadLocal<Boolean>();
    // stores maf header list in thread local variable
    private static ThreadLocal<List<String>> mafHeaderList = new ThreadLocal<List<String>>();



    public O execute( final File file, final QcContext context ) throws ProcessorException {
        try{
            setCenterConvertedToUUID(context.isCenterConvertedToUUID());
            return super.execute(file,context);
        }finally{
            cleanup();
        }
    }
    /**
     * Creates a map of the field headers in the maf file, given a list of headers.  Will add an error if there is a
     * missing header.
     *
     * @param headers the list of header names
     * @return a map, where keys are header names and values are the index of the header (column number, 0-based)
     */
    protected Map<String, Integer> mapFieldOrder(final List<String> headers) {
        final Map<String, Integer> fieldOrder = new HashMap<String, Integer>();
        final List<String> headersLowercase = new ArrayList<String>();
        for (final String header : headers) {
            headersLowercase.add(header.toLowerCase());
        }

        for (final String field : getMafFieldList()) {
            final int column = headersLowercase.indexOf(field.toLowerCase());
            if (column != -1) {
                fieldOrder.put(field, column);
            }
        }
        return fieldOrder;
    }

    protected TabDelimitedContentNavigator parseMaf(final File mafFile) throws IOException,ParseException {
        final TabDelimitedContent tabDelimitedContent = new TabDelimitedContentImpl();
        final TabDelimitedFileParser parser = new TabDelimitedFileParser();
        final TabDelimitedContentNavigator nav = new TabDelimitedContentNavigator();
        parser.setTabDelimitedContent(tabDelimitedContent);
        parser.loadTabDelimitedContent(mafFile, "#");
        parser.loadTabDelimitedContentHeader();
        nav.setTabDelimitedContent(parser.getTabDelimitedContent());
        nav.setCommentAndEmptyLineMap(parser.getCommentAndEmptyLineMap());
        return nav;
    }

    /**
     * Returns maf header list
     * @return
     */
    protected List<String> getMafFieldList() {
        return getMafFieldList(true);
    }

    protected List<String> getMafFieldList(final Boolean firstGenMaf) {
        if(getMafHeaderList() == null){
            final List<String>  mafHeaderList = new ArrayList<String>(MAF_FIELDS);
            if(firstGenMaf){
                // remove second gen fields
                mafHeaderList.remove(FIELD_CENTER_NAME);
                mafHeaderList.remove(FIELD_MAF2_START_POSITION);
                mafHeaderList.remove(FIELD_MAF2_END_POSITION);
                mafHeaderList.remove(FIELD_SEQUENCE_SOURCE);
                mafHeaderList.remove(FIELD_SCORE);
                mafHeaderList.remove(FIELD_BAM_FILE);
                mafHeaderList.remove(FIELD_SEQUENCER);
                // switch FIELD_VALIDATION_METHOD position
                mafHeaderList.remove(FIELD_VALIDATION_METHOD);
                mafHeaderList.add(mafHeaderList.indexOf(FIELD_SEQUENCING_PHASE),FIELD_VALIDATION_METHOD);
            }else{
                //remove firstgen specific fields
                mafHeaderList.remove(FIELD_START_POSITION);
                mafHeaderList.remove(FIELD_END_POSITION);
                mafHeaderList.remove(FIELD_CENTER);

            }
            if(!getCenterConvertedToUUID()){
                // remove uuid fields
                mafHeaderList.remove(FIELD_TUMOR_SAMPLE_UUID);
                mafHeaderList.remove(FIELD_MATCHED_NORM_SAMPLE_UUID);
            }
            setMafHeaderList(mafHeaderList);
        }
        return getMafHeaderList();

    }


    private Boolean getCenterConvertedToUUID() {
        return (isCenterConvertedToUUID.get() == null)?false:isCenterConvertedToUUID.get();
    }

    private void setCenterConvertedToUUID(final Boolean centerConvertedToUUID) {
        isCenterConvertedToUUID.set(centerConvertedToUUID);
    }

    protected List<String> getMafHeaderList() {
        return mafHeaderList.get();
    }

    protected  void setMafHeaderList(final List<String> mafHeaders) {
        mafHeaderList.set(mafHeaders);
    }

    public void cleanup(){
        isCenterConvertedToUUID.remove();
        mafHeaderList.remove();
    }
}
