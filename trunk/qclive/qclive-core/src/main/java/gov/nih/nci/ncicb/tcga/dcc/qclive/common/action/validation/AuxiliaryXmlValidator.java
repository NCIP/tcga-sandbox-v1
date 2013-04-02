/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CodeTableQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.JAXBUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.UnmarshalResult;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.TcgaBcr;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractArchiveFileProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRUtils;

import java.io.File;
import java.util.Map;

import javax.xml.bind.ValidationEvent;

/**
 * The <code>AuxiliaryXmlValidator</code> class validates the auxiliary XML files contained in
 * BCR archives.
 *
 * @author Matt Nicholls
 *         Last updated by: nichollsmc
 * @version 
 */
public class AuxiliaryXmlValidator extends AbstractArchiveFileProcessor<Boolean> {

	private BCRUtils bcrUtils;
	private CodeTableQueries codeTableQueries;
	
	private static final String AUX_META_DATA_ERROR_PREFIX = "Auxiliary XML meta-data validation error - ";
	
	/* (non-Javadoc)
	 * @see gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractArchiveFileProcessor#processFile(java.io.File, gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext)
	 */
	@Override
	protected Boolean processFile(final File xmlFile, final QcContext qcContext) throws ProcessorException {
		
		if(bcrUtils.isAuxiliaryFile(xmlFile)) {
	    	try {
	    		// Unmarshal the XML file into a JAXB generated object with validation turned on
	    		final UnmarshalResult unmarshalResult = JAXBUtil.unmarshal(xmlFile, TcgaBcr.class, true, true);
	    		
	    		// Check for schema (syntax) validation errors
	    		if(!unmarshalResult.isValid()) {
	    			getUnmarshalValidationErrors(xmlFile, qcContext, unmarshalResult);
	    			return false;
	    		}
	    		
	    		// Perform semantic validation and return the result
		    	return validateAuxiliaryXmlMetaData(xmlFile, qcContext, (TcgaBcr) unmarshalResult.getJaxbObject());
	    	}
	    	catch(Exception e) {
	    		addErrorMessage(xmlFile, qcContext, e.getMessage());
	    		return false;
	    	}
		}
		else {
			// Return true if the XML file being processed is not an auxiliary XML file,
			// since validation of other BCR XML files is done in separate validators 
			return true;
		}
	}
	
	/**
	 * Performs semantic validation of meta-data elements within an auxiliary XML file. 
	 * 
	 * @param xmlFile - the auxiliary XML file to validate
	 * @param qcContext - the {@link QcContext} instance attached to this validator
	 * @param tcgaBcr - a JAXB generated object instance of type {@link TcgaBcr}
	 * @return true if all the meta-data elements are valid, false otherwise
	 */
	protected boolean validateAuxiliaryXmlMetaData(final File xmlFile, final QcContext qcContext, final TcgaBcr tcgaBcr) {
		
		boolean isValid = true;
		
		// Validate meta-data as required
		isValid &= validatePatientId(xmlFile, qcContext, tcgaBcr);
		isValid &= validateTissueSourceSite(xmlFile, qcContext, tcgaBcr);
		
		return isValid;
	}
	
	private boolean validatePatientId(final File xmlFile, final QcContext qcContext, final TcgaBcr tcgaBcr) {
		
		final String bcrPatientBarcode = tcgaBcr.getPatient().getBcrPatientBarcode().getValue();
		final String patientIdFromBarcode = bcrPatientBarcode.substring(bcrPatientBarcode.lastIndexOf('-') + 1);
		final String patientId = tcgaBcr.getPatient().getPatientId().getValue();
		
		if(!patientId.equals(patientIdFromBarcode)) {
			final StringBuilder errorMessage = new StringBuilder();
			errorMessage
			.append(AUX_META_DATA_ERROR_PREFIX)
			.append("patient_id element value '" + patientId + "' ")
			.append("does not match the patient Id part of the bcr_patient_barcode element value '" + bcrPatientBarcode + "'");
			
			addErrorMessage(xmlFile, qcContext, errorMessage.toString());
			return false;
		}
		
		return true;
	}
	
	private boolean validateTissueSourceSite(final File xmlFile, final QcContext qcContext, final TcgaBcr tcgaBcr) {
		
		final String bcrPatientBarcode = tcgaBcr.getPatient().getBcrPatientBarcode().getValue();
		final String tissueSourceSiteFromBarcode = 
				bcrPatientBarcode.substring(bcrPatientBarcode.indexOf('-') + 1, bcrPatientBarcode.lastIndexOf('-'));
		final String tissueSourceSite = tcgaBcr.getPatient().getTissueSourceSite().getValue();
		
		if(!tissueSourceSite.equals(tissueSourceSiteFromBarcode)) {
			final StringBuilder errorMessage = new StringBuilder();
			errorMessage
			.append(AUX_META_DATA_ERROR_PREFIX)
			.append("tissue_source_site element value '" + tissueSourceSite + "' ")
			.append("does not match the tissue source site part of the bcr_patient_barcode element value '" + bcrPatientBarcode + "'");
			
			addErrorMessage(xmlFile, qcContext, errorMessage.toString());
			return false;
		}
		
		if(!codeTableQueries.tssCodeExists(tissueSourceSite)) {
			final StringBuilder errorMessage = new StringBuilder();
			errorMessage
			.append(AUX_META_DATA_ERROR_PREFIX)
			.append(" tissue_source_site element value '" + tissueSourceSite + "' ")
			.append("does not have a matching record in the database");
			
			addErrorMessage(xmlFile, qcContext, errorMessage.toString());
			return false;
		}
		
		return true;
	}
	
	private void getUnmarshalValidationErrors(final File xmlFile, final QcContext qcContext, final UnmarshalResult unmarshalResult) {
		
		for(final ValidationEvent validationEvent : unmarshalResult.getValidationEvents()) {
			addErrorMessage(xmlFile, qcContext, AUX_META_DATA_ERROR_PREFIX + validationEvent.getMessage());
		}
	}
	
	private void addErrorMessage(final File xmlFile, final QcContext qcContext, final String errorMessage) {
		
		qcContext.addError(
				MessageFormat.format(
						MessagePropertyType.XML_FILE_PROCESSING_ERROR,
						xmlFile.getName(),
						errorMessage));
	}
	
	/* (non-Javadoc)
	 * @see gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor#getName()
	 */
	@Override
	public String getName() {
		return "auxiliary XML file validation";
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractArchiveFileProcessor#getReturnValue(java.util.Map, gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext)
	 */
	@Override
	protected Boolean getReturnValue(final Map<File, Boolean> results, final QcContext context) {
		return !(results.values().contains(false));
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractArchiveFileProcessor#getDefaultReturnValue(gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive)
	 */
	@Override
	protected Boolean getDefaultReturnValue(final Archive archive) {
		return true;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractArchiveFileProcessor#getFileExtension()
	 */
	@Override
	protected String getFileExtension() {
		return ClinicalXmlValidator.XML_EXTENSION;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractArchiveFileProcessor#isCorrectArchiveType(gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive)
	 */
	@Override
	protected boolean isCorrectArchiveType(final Archive archive) throws ProcessorException {
		return Experiment.TYPE_BCR.equals(archive.getExperimentType());
	}
	
	/**
	 * @return the bcrUtils
	 */
	public BCRUtils getBcrUtils() {
		return bcrUtils;
	}

	/**
	 * @param bcrUtils the bcrUtils to set
	 */
	public void setBcrUtils(final BCRUtils bcrUtils) {
		this.bcrUtils = bcrUtils;
	}

	/**
	 * @return the codeTableQueries
	 */
	public CodeTableQueries getCodeTableQueries() {
		return codeTableQueries;
	}

	/**
	 * @param codeTableQueries the codeTableQueries to set
	 */
	public void setCodeTableQueries(final CodeTableQueries codeTableQueries) {
		this.codeTableQueries = codeTableQueries;
	}
}
