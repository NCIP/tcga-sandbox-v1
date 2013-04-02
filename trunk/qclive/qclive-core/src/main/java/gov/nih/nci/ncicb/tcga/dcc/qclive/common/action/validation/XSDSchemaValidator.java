/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.SchemaException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractArchiveFileProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.util.QCliveXMLSchemaValidator;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
/**
 * A processor that is used to validate XML. 
 * @author Stanley Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class XSDSchemaValidator extends AbstractArchiveFileProcessor<Boolean> {	
	
	private QCliveXMLSchemaValidator qCliveXMLSchemaValidator;    	

	@Override
    public String getName() {
        return " XML schema validation";
    }

	@Override
	protected Boolean getReturnValue(final Map<File, Boolean> results, final QcContext context) {
		if(results != null){
			return !(results.values().contains(false));
		}else{
			return null;
		}
    }

	@Override
	protected Boolean processFile(File xmlFile, QcContext context)
			throws gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException {
		boolean valid = true;
		try {
			valid = qCliveXMLSchemaValidator.validateSchema(xmlFile, context, qCliveXMLSchemaValidator.getAllowLocalSchema(), qCliveXMLSchemaValidator.getXSDURLPattern());
		} catch (IOException e) {
			valid = false;
	            context.addError(MessageFormat.format(
	            		MessagePropertyType.XML_FILE_PROCESSING_ERROR, 
	            		xmlFile.getName(), 
	            		new StringBuilder().append("I/O error reading '").append(xmlFile.getName()).append("': ").append(e.getMessage()).toString()));	        
		} catch (SAXException e) {
			valid = false;
            context.addError(MessageFormat.format(
            		MessagePropertyType.XML_FILE_PROCESSING_ERROR, 
            		xmlFile.getName(), 
            		new StringBuilder().append("Error in '").append(xmlFile.getName()).append("': ").append(e.getMessage()).toString()));
		} catch (SchemaException e) {
			valid = false;
            context.addError(MessageFormat.format(
            		MessagePropertyType.XML_FILE_PROCESSING_ERROR,
            		xmlFile.getName(),
            		new StringBuilder().append("Error evaluating '").append(xmlFile.getName()).append("': ").append(e.getMessage()).toString()));
		} catch (ParserConfigurationException e) {
			 valid = false;
	            context.addError(MessageFormat.format(
	            		MessagePropertyType.XML_FILE_PROCESSING_ERROR, 
	            		xmlFile.getName(), 
	            		new StringBuilder().append("Parser error reading '").append(xmlFile.getName()).append("': ").append(e.getMessage()).toString()));
		}
		return valid;
	}

	@Override
    protected Boolean getDefaultReturnValue(final Archive archive) {
        return true;
    }

	@Override
	 protected String getFileExtension() {
        return ClinicalXmlValidator.XML_EXTENSION;
    }
			
	@Override
	protected boolean isCorrectArchiveType(final Archive archive) {
	   return Experiment.TYPE_BCR.equals(archive.getExperimentType());
	}
	public QCliveXMLSchemaValidator getqCliveXMLSchemaValidator() {
		return qCliveXMLSchemaValidator;
	}

	public void setqCliveXMLSchemaValidator(
			QCliveXMLSchemaValidator qCliveXMLSchemaValidator) {
		this.qCliveXMLSchemaValidator = qCliveXMLSchemaValidator;
	}
	
}
