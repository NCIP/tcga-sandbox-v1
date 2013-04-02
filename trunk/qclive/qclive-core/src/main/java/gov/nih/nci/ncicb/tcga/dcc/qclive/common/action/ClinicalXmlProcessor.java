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
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ShippedBiospecimen;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDDAO;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BCRID;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ClinicalXmlValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRIDProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BiospecimenHelper;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.BCRDataService;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.xml.sax.SAXException;

/**
 * Processes all BCR XML files by adding / updating barcodes for the archive.  Also updates biospecimen_to_archive.
 * <p/>
 * NOTE: needs to update dccCommon first and then insert into the disease-specific db with the same IDs used.
 *
 * @author Jessica Chen
 *         Last updated by: Author: Jeyanthi Thangiah,Rohini Raman
 * @version $Rev: 3419 $
 */
public class ClinicalXmlProcessor extends AbstractArchiveFileProcessor<Archive> {

    private BCRDataService bcrDataService;
    protected UUIDDAO uuidDAOService;
    protected UUIDDAO diseaseUuidDAOService;           
	protected BCRIDProcessor bcrProcessor;      
	private final Log logger = LogFactory.getLog(ClinicalXmlProcessor.class);

	public void setBcrProcessor(BCRIDProcessor bcrProcessor) {
			this.bcrProcessor = bcrProcessor;
	}
	public void setDiseaseUuidDAOService(UUIDDAO diseaseUuidDAOService) {
		this.diseaseUuidDAOService = diseaseUuidDAOService;
	}
    protected Archive getReturnValue(final Map<File, Archive> results, final QcContext context) {
        return context.getArchive();
    }

    protected Archive processFile(final File xmlFile, final QcContext context) throws ProcessorException {
        try {
            final Archive archive = context.getArchive();

            handleShippedPortions(xmlFile, archive);

            handleAliquots(xmlFile, archive);
                                    
            createPatientUUIDFileAssociation(xmlFile,context.getArchive().getFilenameToIdToMap().get(xmlFile.getName()));

        } catch (Exception e) {
            logger.error(" Error processing " + xmlFile + ". ", e);
            context.getArchive().setDeployStatus(Archive.STATUS_IN_REVIEW);

            // Do not send run time exception message to the centers as they might not be
            // meaningful messages.
            // Except DataAccessException. DataAccessException will have better error messages

            if ((e instanceof DataAccessException) ||
                    !(e instanceof RuntimeException)) {
                final StringBuilder errorMsg = new StringBuilder("Error processing '")
                        .append(xmlFile)
                        .append("'. ");
                if (e instanceof DataAccessException) {
                    errorMsg.append(" Unexpected database error.");
                } else {
                    errorMsg.append(e.getMessage());
                }
                throw new ProcessorException(errorMsg.toString());
            } else {
                throw (RuntimeException) e;
            }
        }
        return context.getArchive();
    }
    
    protected void createPatientUUIDFileAssociation ( final File xmlFile, final Long fileId) throws ParserConfigurationException, IOException, SAXException, TransformerException{
    	String patientUUID = bcrProcessor.getPatientUUIDfromFile(xmlFile);
    	
    	uuidDAOService.addParticipantFileUUIDAssociation
			(patientUUID, fileId);
    	diseaseUuidDAOService.addParticipantFileUUIDAssociation
			(patientUUID, fileId);
    }

    private void handleAliquots(final File xmlFile, final Archive archive) throws TransformerException, XPathExpressionException, IOException, SAXException, ParserConfigurationException, ParseException, UUIDException {
        // now handle aliquots
        final List<String[]> bcrIDs = getBcrIdsFromFile(xmlFile);
        final List<Integer> bcrIdList = new ArrayList<Integer>();
        for (final String[] bcrID : bcrIDs) {
            final String barcode = bcrID[0];
            final String shipDate = bcrID[1];
            final String uuid = bcrID[2];
            final String batchNumberString = bcrID[3];
            Integer batchNumber = null;
            if (batchNumberString != null) {
                batchNumber = Integer.valueOf(batchNumberString);
            }
            bcrIdList.add(handleBarcode(barcode, shipDate, uuid, batchNumber, archive, xmlFile).getId());
        }

        BiospecimenHelper.insertBiospecimenFileRelationship(bcrIdList,
                archive.getFilenameToIdToMap().get(xmlFile.getName()),
                bcrDataService,
                archive.getTheTumor());
    }

    private void handleShippedPortions(final File xmlFile, final Archive archive) throws IOException, SAXException, ParserConfigurationException, TransformerException, XPathExpressionException, ParseException {
        // look for shipped portions
        final List<ShippedBiospecimen> shippedPortionsInFile = bcrDataService.findAllShippedPortionsInFile(xmlFile);

        // it is possible there will not be any...
        if (shippedPortionsInFile.size() > 0) {
            Integer shippedPortionTypeId = bcrDataService.getShippedItemId(ShippedBiospecimen.SHIPPED_ITEM_NAME_PORTION);
            bcrDataService.handleShippedBiospecimens(shippedPortionsInFile, shippedPortionTypeId, archive, xmlFile);
        }
    }

    protected List<String[]> getBcrIdsFromFile(final File xmlFile) throws TransformerException, XPathExpressionException, IOException, SAXException, ParserConfigurationException {
        return bcrDataService.findAllAliquotsInFile(xmlFile);
    }

    protected Archive getDefaultReturnValue(final Archive archive) {
        return archive;
    }

    protected String getFileExtension() {
        return ClinicalXmlValidator.XML_EXTENSION;
    }

    protected boolean isCorrectArchiveType(final Archive archive) {
        return archive.getExperimentType().equals(Experiment.TYPE_BCR);
    }

    private BCRID handleBarcode(final String barcode,
                                final String shipDate,
                                final String uuid,
                                final Integer batchNumber,
                                final Archive archive,
                                final File xmlFile) throws ParseException, UUIDException {
        final BCRID bcrId = makeBCRID(barcode);
        bcrId.setBatchNumber(batchNumber);
        bcrId.setShippingDate(shipDate);
        bcrId.setArchiveId(archive.getId());
        bcrId.setValid(1);
        if (uuid != null) {
            bcrId.setUUID(uuid.toLowerCase());
        }
        bcrDataService.handleAliquotBarcode(bcrId, archive, xmlFile);
        return bcrId;

    }

    public String getName() {
        return "clinical XML file processor";
    }


    protected BCRID makeBCRID(final String barcode) throws ParseException {
        return bcrDataService.parseAliquotBarcode(barcode);
    }

    public BCRDataService getBcrDataService() {
        return bcrDataService;
    }

    public void setBcrDataService(BCRDataService bcrDataService) {
        this.bcrDataService = bcrDataService;
    }

	public void setUuidDAOService(UUIDDAO uuidDAOService) {
		this.uuidDAOService = uuidDAOService;
	}
    
}
