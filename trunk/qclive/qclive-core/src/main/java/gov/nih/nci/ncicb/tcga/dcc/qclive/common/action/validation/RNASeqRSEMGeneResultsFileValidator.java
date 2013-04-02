/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;


import java.util.Arrays;
import java.util.List;

/**
 * A validator to check rsem genes results files
 *
 * @author Stan Girshik
 */
public class RNASeqRSEMGeneResultsFileValidator extends RNASeqRSEMValidator {
		    
	private static final List<String> EXPECTED_COLUMNS = Arrays.asList(GENE_ID,RAW_COUNT,SCALED_ESTIMATE,TRANSCRIPT_ID);
	public static final String RSEM_GENES_RESULTS_FILE_EXTENSION = "rsem.genes.results";	
	
	@Override
	public String getName() {
		return "RNASeq RSEM Gene results file validation";
	}	

	@Override
	protected List<String> getExpectedColumns() {
		 return EXPECTED_COLUMNS;
	}	

	@Override
	protected String getFileExtension() {
		 return RSEM_GENES_RESULTS_FILE_EXTENSION;
	}	
}
