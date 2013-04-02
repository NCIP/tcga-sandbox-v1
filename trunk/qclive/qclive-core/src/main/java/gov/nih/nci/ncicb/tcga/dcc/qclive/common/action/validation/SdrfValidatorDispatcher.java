/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import org.apache.commons.lang.StringUtils;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;

/**
 * Validator that passes through to SDRF validator based on some properties of the archive -- for now just the platform name.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class SdrfValidatorDispatcher extends AbstractProcessor<Archive, Boolean> {

    private Processor<Archive, Boolean> rnaSeqSdrfValidator;
    private Processor<Archive, Boolean> miRnaSeqSdrfValidator;
    private Processor<Archive, Boolean> arraySdrfValidator;
    private Processor<Archive, Boolean> proteinArraySdrfValidator;    
   
	@Override
    protected Boolean doWork(final Archive archive, final QcContext context) throws ProcessorException {

        final String platform = archive.getPlatform();

        if (platform != null && platform.contains(MiRNASeqDataFileValidator.MIRNASEQ)) { // Keep this test ahead of the RNASeq test (to avoid double match)
            return getMiRnaSeqSdrfValidator().execute(archive, context);
        } else if (platform != null && platform.contains(RNASeqDataFileValidator.RNASEQ)) {
            return getRnaSeqSdrfValidator().execute(archive, context);
        }else if (StringUtils.isNotEmpty(platform) && platform.contains(ProteinArraySdrfValidator.PROTEIN_ARRAY_PLATFORM)){
        	return getProteinArraySdrfValidator ().execute(archive, context);        	
        } else {
            return getArraySdrfValidator().execute(archive, context);
        }        
    }

    @Override
    public String getName() {
        return "SDRF validator selector";
    }

    //
    // Getter / Setter
    //

    public Processor<Archive, Boolean> getRnaSeqSdrfValidator() {
        return rnaSeqSdrfValidator;
    }

    public void setRnaSeqSdrfValidator(final Processor<Archive, Boolean> rnaSeqSdrfValidator) {
        this.rnaSeqSdrfValidator = rnaSeqSdrfValidator;
    }

    public Processor<Archive, Boolean> getMiRnaSeqSdrfValidator() {
        return miRnaSeqSdrfValidator;
    }

    public void setMiRnaSeqSdrfValidator(final Processor<Archive, Boolean> miRnaSeqSdrfValidator) {
        this.miRnaSeqSdrfValidator = miRnaSeqSdrfValidator;
    }

    public Processor<Archive, Boolean> getArraySdrfValidator() {
        return arraySdrfValidator;
    }

    public void setArraySdrfValidator(final Processor<Archive, Boolean> arraySdrfValidator) {
        this.arraySdrfValidator = arraySdrfValidator;
    }
    
    public Processor<Archive, Boolean> getProteinArraySdrfValidator() {
		return proteinArraySdrfValidator;
	}
	public void setProteinArraySdrfValidator(
			Processor<Archive, Boolean> proteinArraySdrfValidator) {
		this.proteinArraySdrfValidator = proteinArraySdrfValidator;
	}
}
