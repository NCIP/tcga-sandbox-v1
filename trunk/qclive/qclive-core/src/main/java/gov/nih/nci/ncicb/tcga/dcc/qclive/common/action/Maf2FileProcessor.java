/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.MafInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Robert S. Sfeir
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class Maf2FileProcessor extends MafFileProcessor {
    private static final String FIELD_MAF2_END_POSITION = "End_Position";
    private static final String FIELD_MAF2_START_POSITION = "Start_Position";
    public final static String FIELD_SEQUENCE_SOURCE = "Sequence_Source";
    public final static String FIELD_SCORE = "Score";
    public final static String FIELD_BAM_FILE = "BAM_File";
    public final static String FIELD_SEQUENCER = "Sequencer";

    @Override
    protected String getEndPositionName() {
        return FIELD_MAF2_END_POSITION;
    }

    @Override
    protected String getStartPositionName() {
        return FIELD_MAF2_START_POSITION;
    }

    @Override
    protected List<String> getMafFieldList() {
        return getMafFieldList(false);
    }

    private String getRowValue(final String[] row, final Map<String, Integer> fieldOrder, final String fieldName) throws ProcessorException {
        if (fieldOrder.get(fieldName) == null) {
            throw new ProcessorException("Required column " + fieldName + " missing");
        }
        return row[fieldOrder.get(fieldName)];
    }

    @Override
    protected void setOtherMafInfoFields(final MafInfo mafInfo, final String[] row, final Map<String, Integer> fieldOrder,
            final long mafFileId) throws ProcessorException {
        mafInfo.setCenterName(getRowValue(row, fieldOrder, FIELD_CENTER_NAME));
        mafInfo.setSequenceSource(getRowValue(row, fieldOrder, FIELD_SEQUENCE_SOURCE));
        mafInfo.setScore(getRowValue(row, fieldOrder, FIELD_SCORE));
        mafInfo.setBamFile(getRowValue(row, fieldOrder, FIELD_BAM_FILE));
        mafInfo.setSequencer(getRowValue(row, fieldOrder, FIELD_SEQUENCER));
        mafInfo.setSequencingPhase(getRowValue(row, fieldOrder, FIELD_SEQUENCING_PHASE));
    }
    
    public String getName() {
        return "maf file processor (version 2)";
    }
}
