/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;

import java.io.File;
import java.util.Map;

/**
 * An abstract class used to 
 *     encapsulate common validation logic for RSEM RNASEq files
 *
 * @author Stan Girshik
 */
public abstract class RNASeqRSEMValidator extends AbstractSeqDataFileValidator {
													   
	public static final String ILLUMINAGA_RNASEQ_V2 = "IlluminaGA_RNASeqV2";													  
	public static final String ILLUMINAGA_HiSeq_V2 = "IlluminaHiSeq_RNASeqV2";
	    
	public static final String GENE_ID = "gene_id";
	public static final String NORMALIZED_COUNT = "normalized_count";
	public static final String RAW_COUNT = "raw_count";
	public static final String TRANSCRIPT_ID = "transcript_id";
	public static final String ISOFORM_ID = "isoform_id";
	public static final String SCALED_ESTIMATE = "scaled_estimate";	
	
	@Override
	protected boolean valueIsValid(String value, String headerName,
			QcContext context, int rowNum) {
		// no data level validations , so always true
		return true;
	}	

	@Override
	protected Boolean getReturnValue(Map<File, Boolean> results,
			QcContext context) {
		  return !results.containsValue(false);
	}

	@Override
	protected Boolean getDefaultReturnValue(Archive archive) {
		return true;
	}
	
	 @Override
    protected boolean isCorrectArchiveType(final Archive archive) throws ProcessorException {
        return Experiment.TYPE_CGCC.equals(archive.getExperimentType())
                && Archive.TYPE_LEVEL_3.equals(archive.getArchiveType())
                && ((archive.getPlatform().contains(ILLUMINAGA_RNASEQ_V2)) 
                		|| (archive.getPlatform().contains(ILLUMINAGA_HiSeq_V2)));
    }

}
