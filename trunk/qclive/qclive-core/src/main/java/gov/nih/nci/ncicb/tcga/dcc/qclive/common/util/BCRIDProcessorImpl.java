/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Barcode;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ShippedBiospecimen;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.util.XPathXmlParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BCRID;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BiospecimenToFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.BCRIDQueries;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class which reads XML documents from BCR archives, extracts a BCR ID and sets it into a BCR object which then is persisted the database
 *
 * @author Robert S. Sfeir
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BCRIDProcessorImpl implements BCRIDProcessor {

    private static final Pattern BATCH_NUMBER_PATTERN = Pattern.compile("[0-9]+");
	
    private BCRIDQueries bcrIDQueries;
    private String aliquotElementXPath;
    private String aliquotBarcodeElement;
    private String aliquotUuidElement;
    private String shipDayElement;
    private String shipMonthElement;
    private String shipYearElement;
    private CenterQueries centerQueries;
    private BarcodeUuidResolver barcodeUuidResolver;
    private String shippedPortionBarcodeElement;
    private String shippedPortionShipDayElement;
    private String shippedPortionShipMonthElement;
    private String shippedPortionShipYearElement;
    private String shippedPortionUuidElement;
    private String shippedPortionElementXPath;
    private String patientElementUUIDXPath;

    public void setPatientElementUUIDXPath(String patientElementUUIDXPath) {
		this.patientElementUUIDXPath = patientElementUUIDXPath;
	}

	public void setCenterQueries(final CenterQueries centerQueries) {
        this.centerQueries = centerQueries;
    }
    
    public BCRIDQueries getBcrIDQueries() {
        return bcrIDQueries;
    }

    public void setBcrIDQueries(final BCRIDQueries bcrIDQueries) {
        this.bcrIDQueries = bcrIDQueries;
    }

    public BCRID parseAliquotBarcode(final String aliquotBarcode) throws ParseException {
        final BCRID bcrId = new BCRID();
        bcrId.setFullID(aliquotBarcode.trim());
        final Matcher m = QcLiveBarcodeAndUUIDValidatorImpl.ALIQUOT_BARCODE_PATTERN.matcher(bcrId.getFullID());
        if (m.find()) {
            bcrId.setProjectName(m.group(QcLiveBarcodeAndUUIDValidatorImpl.PROJECT_GROUP));
            bcrId.setSiteID(m.group(QcLiveBarcodeAndUUIDValidatorImpl.TSS_GROUP));
            bcrId.setPatientID(m.group(QcLiveBarcodeAndUUIDValidatorImpl.PATIENT_GROUP));
            bcrId.setSampleID(m.group(QcLiveBarcodeAndUUIDValidatorImpl.SAMPLE_ID_GROUP));
            bcrId.setSampleTypeCode(m.group(QcLiveBarcodeAndUUIDValidatorImpl.SAMPLE_TYPE_CODE_GROUP));
            bcrId.setSampleNumberCode(m.group(QcLiveBarcodeAndUUIDValidatorImpl.SAMPLE_NUMBER_GROUP));
            bcrId.setPortionID(m.group(QcLiveBarcodeAndUUIDValidatorImpl.PORTION_ID_GROUP));
            bcrId.setPortionNumber(m.group(QcLiveBarcodeAndUUIDValidatorImpl.PORTION_NUMBER_GROUP));
            bcrId.setPortionTypeCode(m.group(QcLiveBarcodeAndUUIDValidatorImpl.PORTION_ANALYTE_GROUP));
            bcrId.setPlateId(m.group(QcLiveBarcodeAndUUIDValidatorImpl.PLATE_ID_GROUP));
            bcrId.setBcrCenterId(m.group(QcLiveBarcodeAndUUIDValidatorImpl.BCR_CENTER_ID_GROUP));
        } else {
            // is this an OK Exception to throw or should a customize one be created?
            throw new ParseException(new StringBuilder().append("BCRID: ").append(bcrId.getFullID()).append(" does not follow normal naming conventions").toString(), 1);
        }
        return bcrId;
    }

    private Integer getBatchNumber(final XPathXmlParser parser, final Document document) throws TransformerException {
        Integer batchNumber = null;
        final NodeList batchNumberNodes = parser.getNodes(document, "//admin/batch_number");
        if (batchNumberNodes != null && batchNumberNodes.getLength() > 0) {
            final String batchValue = batchNumberNodes.item(0).getTextContent().trim();
            final Matcher batchMatcher = BATCH_NUMBER_PATTERN.matcher(batchValue);
            if (batchMatcher.find()) {
                batchNumber = Integer.valueOf(batchMatcher.group(0));
            }
        }

        return batchNumber;
    }

    /**
     * Static convenience method for finding BCR IDs in an XML file.
     *
     * @param xmlFile the XML file to look through
     *
     * @return a List of String arrays, where in each array, the first element is the aliquot barcode, the second is
     *         the ship date, the third is the UUID, and the fourth is the batch number
     *
     * @throws TransformerException if there is an error transforming
     * @throws XPathExpressionException if there is an error in an expression in the file
     * @throws IOException if the file can't be read or something
     * @throws javax.xml.parsers.ParserConfigurationException if the parser is not configured correctly
     * @throws org.xml.sax.SAXException if there is a parse problem
     */
    public List<String[]> findAllAliquotsInFile(final File xmlFile)
            throws TransformerException, XPathExpressionException, IOException, SAXException, ParserConfigurationException {

        List<String[]> bcrList = new ArrayList<String[]>();
        XPathXmlParser parser = new XPathXmlParser();
        Document document = parser.parseXmlFile(xmlFile, false, false);

        Integer batchNumber = getBatchNumber(parser, document);

        //Get the main node list.
        NodeList nodes = parser.getNodes(document, aliquotElementXPath);
        if (nodes != null) {
            for (int i = 0; i < nodes.getLength(); i++) {
                // Get element
                Element elem = (Element) nodes.item(i);
                NodeList childNodes = elem.getChildNodes();
                // barcode, ship date, uuid, batch number
                String[] aliquotData = new String[4];
                aliquotData[3] = batchNumber == null ? null : batchNumber.toString();
                String day = "";
                String month = "";
                String year = "";
                String nodeName = null;
                for(int cNode = 0; cNode < childNodes.getLength(); cNode++) {
                	nodeName = getElementLocalPart(childNodes.item(cNode).getNodeName());
                    if(nodeName.equals(aliquotBarcodeElement)) {
                        aliquotData[0] = childNodes.item(cNode).getTextContent().trim();
                    } 
                    else if(nodeName.equals(shipDayElement)) {
                        day = childNodes.item(cNode).getTextContent().trim();
                        if(day.length() == 1) {
                            day = "0" + day;
                        }
                    } 
                    else if(nodeName.equals(shipMonthElement)) {
                        month = childNodes.item(cNode).getTextContent().trim();
                        if (month.length() == 1) {
                            month = "0" + month;
                        }
                    } 
                    else if(nodeName.equals(shipYearElement)) {
                        year = childNodes.item(cNode).getTextContent().trim();
                    } 
                    else if(nodeName.equals(aliquotUuidElement)) {
                        final String uuid = childNodes.item(cNode).getTextContent().trim();
                        if (uuid.length() > 0) {
                            aliquotData[2] = uuid;
                        }
                    }
                }
                String shipDate = null;
                if (day.length() != 0 && month.length() != 0 && year.length() != 0) {
                    shipDate = new StringBuilder().append(year).append("-").append(month).append("-").append(day).toString();
                }
                aliquotData[1] = shipDate;
                bcrList.add(aliquotData);
            }
        }
        return bcrList;
    }


    @Override
    public List<ShippedBiospecimen> findAllShippedPortionsInFile(final File file)
            throws IOException, SAXException, ParserConfigurationException, TransformerException, XPathExpressionException, ParseException {

        final List<ShippedBiospecimen> shippedPortions = new ArrayList<ShippedBiospecimen>();

        final XPathXmlParser parser = new XPathXmlParser();
        final Document document = parser.parseXmlFile(file, false, false);

        final Integer batchNumber = getBatchNumber(parser, document);

        final NodeList nodes = parser.getNodes(document, shippedPortionElementXPath);

        if (nodes != null) {
            for (int i = 0; i < nodes.getLength(); i++) {

                Element elem = (Element) nodes.item(i);
                NodeList childNodes = elem.getChildNodes();

                String day = "";
                String month = "";
                String year = "";
                String barcode = "";
                String uuid = "";
                String nodeName = null;
                for (int cNode = 0; cNode < childNodes.getLength(); cNode++) {
                    nodeName = getElementLocalPart(childNodes.item(cNode).getNodeName());
                    if (nodeName.equals(shippedPortionBarcodeElement)) {
                        barcode = childNodes.item(cNode).getTextContent().trim();

                    } else if (nodeName.equals(shippedPortionShipDayElement)) {
                        day = childNodes.item(cNode).getTextContent().trim();
                        if (day.length() == 1) {
                            day = "0" + day;
                        }
                    } else if (nodeName.equals(shippedPortionShipMonthElement)) {
                        month = childNodes.item(cNode).getTextContent().trim();
                        if (month.length() == 1) {
                            month = "0" + month;
                        }
                    } else if (nodeName.equals(shippedPortionShipYearElement)) {
                        year = childNodes.item(cNode).getTextContent().trim();

                    } else if (nodeName.equals(shippedPortionUuidElement)) {
                        uuid = childNodes.item(cNode).getTextContent().trim();
                    }
                }

                ShippedBiospecimen shippedPortion;
                if (barcode.length() > 0) {
                    // note: ticket exists for getting the info from the meta-data in this and hierarchy, not barcode
                    shippedPortion = ShippedBiospecimen.parseShippedPortionBarcode(barcode);
                } else {
                    // for now.  note this needs to change.  see ticket XXX (todo)
                    throw new ParseException("Barcode for shipment_portion may not be empty", 1);
                }

                if (uuid.length() > 0) {
                    shippedPortion.setUuid(uuid.toLowerCase());
                }

                Date shipDate = null;
                if(day.length() != 0 && month.length() != 0 && year.length() != 0) {
                    shipDate = DateUtils.makeDate(year, month, day);
                }

                shippedPortion.setShippedDate(shipDate);
                shippedPortion.setBatchNumber(batchNumber);

                shippedPortions.add(shippedPortion);
            }
        }

        return shippedPortions;
    }

    /**
     * Retrieves the local part of an element. 
     * 
     * <p>
     * The value returned by this method is the last substring parsed from the provided element string that is 
     * separated by colons ':'.
     * 
     * <p>
     * The W3C XML specification indicates that colons should only be used to separate a namespace prefix from 
     * element names and NOT be used in the names themselves. So this method does not make any assumptions about colons
     * being part of the local part name.
     * 
     * @param element - colon delimited string representing the namespace prefix and local part name
     * @return a string representing the local part of an element
     */
    private String getElementLocalPart(String element) {
    	if(element != null && element.length() > 0) {
    		String[] elementParts = element.split(":");
    		return elementParts[elementParts.length - 1];
    	}
    	else {
    		return element;
    	}
    }
    
    /**
     * Method which takes care of storing the BCR ID to the database. This method makes decisions on whether to perform
     * an update or an insert of a BCR ID. It also decides if a BCR ID is a duplicate (it has the same ship date and
     * barcode) it stores that data into the orphan table.
     *
     * @param theBCRID the BCRID object you want to persist to the database.
     * @param useIdFromCommon Set to true if the barcode Id  from common database should be used for saving the barcode
     * to database, false otherwise
     * @param biospecimenArchiveId biospecimen to archive association id, the method updates it to the new value if a
     * new row is inserted in database
     * @param disease the disease the barcode belongs to
     * @param center the BCR center submitting the barcode
     * @throws java.text.ParseException if there is an error parsing the ship date
     */
    public void storeBcrBarcode(final BCRID theBCRID, final Boolean useIdFromCommon, final int[] biospecimenArchiveId,
                                final Tumor disease, final Center center)
            throws ParseException, UUIDException {
        final Integer aliquotId = getBcrIDQueries().exists(theBCRID);
        final Barcode barcodeUuidDetails = barcodeUuidResolver.resolveBarcodeAndUuid(theBCRID.getFullID(), theBCRID.getUUID(), disease, center, true);
        theBCRID.setUUID(barcodeUuidDetails.getUuid());

        final boolean existsInBiospecimenBarcodeTable = (aliquotId != -1);

        if (existsInBiospecimenBarcodeTable) {
            // make sure the biospecimen UUID is in sync with the UUID from UUID system -- otherwise throw exception!
            final String biospecimenUUID = bcrIDQueries.getBiospecimenUUID(theBCRID);
            if (biospecimenUUID == null) {
                bcrIDQueries.updateUUIDForBarcode(theBCRID);
            } else if (!theBCRID.getUUID().equals(biospecimenUUID)) {
                throw new UUIDException("Unexpected UUID conflict for " + theBCRID.getFullID());
            }
            theBCRID.setId(aliquotId);
            updateBcrBarcode(theBCRID, useIdFromCommon, biospecimenArchiveId);
        } else {
            addNewBcrBarcode(theBCRID, useIdFromCommon, biospecimenArchiveId);            
        }


    }


    private void addNewBcrBarcode(final BCRID theBCRID, final Boolean useIdFromCommon, final int[] biospecimenArchiveId)
            throws ParseException {
        //Don't forget the ID we get so that we can update the ship date properly.
        theBCRID.setId(getBcrIDQueries().addBCRID(theBCRID, useIdFromCommon));
        //this step is redundant since the default value of is_valid in table is '1'
        getBcrIDQueries().updateBCRIDStatus(theBCRID);
        getBcrIDQueries().addArchiveRelationship(theBCRID, useIdFromCommon, biospecimenArchiveId);
        if (theBCRID.getShippingDate() != null) {
            getBcrIDQueries().updateShipDate(theBCRID);
        }

    }

    private void updateBcrBarcode(final BCRID theBCRID, final Boolean useIdFromCommon, final int[] biospecimenArchiveId)
            throws ParseException {
        getBcrIDQueries().updateBCRIDStatus(theBCRID);
        getBcrIDQueries().addArchiveRelationship(theBCRID, useIdFromCommon, biospecimenArchiveId); // !!! need to check if exists
        if (theBCRID.getShippingDate() != null) {
            getBcrIDQueries().updateShipDate(theBCRID);
        }
    }

    /**
     * Stores a barcode in the database.  Will set is_valid to 0 if the barcode wasn't in DB already. If barcode becomes
     * valid later, use storeBCRID method, which will update is_valid as well as other BCR-archive-related things.
     *
     * @param bcrID the barcode to store
     * @param useIdFromCommon Set to true if the barcode Id  from common database should be used to saving the barcode
     * to database, false otherwise
     * @param idFromCommon bcrId
     *
     * @throws ParseException if the barcode isn't valid
     */
    public void storeBarcode(final BCRID bcrID, final Boolean useIdFromCommon, final Integer idFromCommon, final Tumor disease)
            throws ParseException, UUIDException {
        Integer bcrIdentifier = bcrIDQueries.exists(bcrID);

        final String bcrCenterCode = bcrID.getBcrCenterId();
        final int centerId = centerQueries.getCenterIdForBCRCenter(bcrCenterCode);
        final Center center = centerQueries.getCenterById(centerId);
        // set resolver to "do not generate" so if no uuid found in the db for the barcode, it will throw an exception
        final Barcode barcodeAndUuid = barcodeUuidResolver.resolveBarcodeAndUuid(bcrID.getFullID(), bcrID.getUUID(), disease, center, false);
        bcrID.setUUID(barcodeAndUuid.getUuid());

        final boolean bcrExistsInDB = (bcrIdentifier != -1);
        if (bcrExistsInDB) {
            // make sure the biospecimen UUID is in sync with the UUID from UUID system
            final String biospecimenUUID = bcrIDQueries.getBiospecimenUUID(bcrID);
            if (!bcrID.getUUID().equals(biospecimenUUID)) {
                if (biospecimenUUID == null) {
                    bcrIDQueries.updateUUIDForBarcode(bcrID);
                } else if (!bcrID.getUUID().equals(biospecimenUUID)) {
                    throw new UUIDException("Unexpected UUID conflict for " + bcrID.getFullID());
                }
            }
        } else {
            bcrIdentifier = getBcrIDQueries().addBCRID(bcrID, useIdFromCommon);
            // if wasn't there, set is valid to false (0)
            bcrID.setValid(0);
            getBcrIDQueries().updateBCRIDStatus(bcrID);
        }

        bcrID.setId(bcrIdentifier);        
    }

    /**
     * Adds biospecimen to file association in database, adds barcode if not present already
     *
     * @param fileId file Id
     * @param bcrID barcode Id
     * @param colName column name
     * @param archiveId archive id
     * @param useIdFromCommon Set to true if the barcode Id  from common database should be used for saving the barcode
     * to database, false otherwise
     * @param bcrAndBcrFileId : entries in the array : 0=bcrId, 1=bcrToFileId
     */
    public void addFileAssociation(
            final Long fileId, final BCRID bcrID, final String colName, final Long archiveId,
            final Boolean useIdFromCommon, final int[] bcrAndBcrFileId, final Tumor disease) throws ParseException, Processor.ProcessorException, UUIDException {
        // this will store the barcode
        storeBarcode(bcrID, useIdFromCommon, bcrAndBcrFileId[0], disease);
        bcrAndBcrFileId[0] = bcrID.getId();
        bcrAndBcrFileId[1] = addFileAssociation(fileId, bcrID.getId(), colName, useIdFromCommon, bcrAndBcrFileId[1]);
    }

    public void addBioSpecimenToFileAssociations(final List<BiospecimenToFile> biospecimenToFileList, final Tumor disease) {
        getBcrIDQueries().addBioSpecimenToFileAssociations(biospecimenToFileList);
    }

    /**
     * Adds list of biospecimen barcodes
     *
     * @param bcrIdList the BCRID objects to add
     */
    public void addBioSpecimenBarcodes(final List<BCRID> bcrIdList, final Tumor disease)
    throws UUIDException {
        getBcrIDQueries().addBioSpecimenBarcodes(bcrIdList, disease);
    }

    /**
     * retuns biospecimen ids for the given barcodes.
     *
     * @param barcodes the barcodes to get
     *
     * @return list of biospecimen ids
     */
    public List<Integer> getBiospecimenIds(final List<String> barcodes) {
        return getBcrIDQueries().getBiospecimenIds(barcodes);
    }


    public Long getBiospecimenIdForUUID(String uuid){
        return getBcrIDQueries().getBiospecimenIdForUUID(uuid);
    }

    public int addFileAssociation(final Long fileId, final Integer barcodeId, final String colName,
                                   final Boolean useIdFromCommon, final int bcrFileId) {
        int biospecimenFileId = getBcrIDQueries().findExistingAssociation(fileId, barcodeId, colName);
        if (biospecimenFileId == 0) {
            biospecimenFileId = getBcrIDQueries().addFileAssociation(fileId, barcodeId, colName, useIdFromCommon, bcrFileId);
        }
        return biospecimenFileId;
    }

	public String getAliquotElementXPath() {
		return aliquotElementXPath;
	}

	public void setAliquotElementXPath(String aliquotElementXPath) {
		this.aliquotElementXPath = aliquotElementXPath;
	}

	public String getAliquotBarcodeElement() {
		return aliquotBarcodeElement;
	}

	public void setAliquotBarcodeElement(String aliquotBarcodeElement) {
		this.aliquotBarcodeElement = aliquotBarcodeElement;
	}

	public String getAliquotUuidElement() {
		return aliquotUuidElement;
	}

	public void setAliquotUuidElement(String aliquotUuidElement) {
		this.aliquotUuidElement = aliquotUuidElement;
	}

	public String getShipDayElement() {
		return shipDayElement;
	}

	public void setShipDayElement(String shipDayElement) {
		this.shipDayElement = shipDayElement;
	}

	public String getShipMonthElement() {
		return shipMonthElement;
	}

	public void setShipMonthElement(String shipMonthElement) {
		this.shipMonthElement = shipMonthElement;
	}

	public String getShipYearElement() {
		return shipYearElement;
	}

	public void setShipYearElement(String shipYearElement) {
		this.shipYearElement = shipYearElement;
	}

	public void setBarcodeUuidResolver(final BarcodeUuidResolver barcodeUuidResolver) {
        this.barcodeUuidResolver = barcodeUuidResolver;
    }

    public void setShippedPortionBarcodeElement(final String shippedPortionBarcodeElement) {
        this.shippedPortionBarcodeElement = shippedPortionBarcodeElement;
    }

    public void setShippedPortionShipDayElement(final String shippedPortionShipDayElement) {
        this.shippedPortionShipDayElement = shippedPortionShipDayElement;
    }

    public void setShippedPortionShipMonthElement(final String shippedPortionShipMonthElement) {
        this.shippedPortionShipMonthElement = shippedPortionShipMonthElement;
    }

    public void setShippedPortionShipYearElement(final String shippedPortionShipYearElement) {
        this.shippedPortionShipYearElement = shippedPortionShipYearElement;
    }

    public void setShippedPortionUuidElement(final String shippedPortionUuidElement) {
        this.shippedPortionUuidElement = shippedPortionUuidElement;
    }

    public void setShippedPortionElementXPath(final String shippedPortionElementXPath) {
        this.shippedPortionElementXPath = shippedPortionElementXPath;
    }

    public String getShippedPortionBarcodeElement() {
        return shippedPortionBarcodeElement;
    }

    public String getShippedPortionShipDayElement() {
        return shippedPortionShipDayElement;
    }

    public String getShippedPortionShipMonthElement() {
        return shippedPortionShipMonthElement;
    }

    public String getShippedPortionShipYearElement() {
        return shippedPortionShipYearElement;
    }

    public String getShippedPortionUuidElement() {
        return shippedPortionUuidElement;
    }

    public String getShippedPortionElementXPath() {
        return shippedPortionElementXPath;
    }

    @Override
    public boolean slideBarcodeExists(final String barcode){
        return getBcrIDQueries().slideBarcodeExists(barcode);
    }
    
    @Override
    public String getPatientUUIDfromFile(final File file) throws ParserConfigurationException, IOException, SAXException, TransformerException{    	
    	String patientUUID = "";
    	final XPathXmlParser parser = new XPathXmlParser();
        final Document document = parser.parseXmlFile(file, false, false);         
        final NodeList nodes = parser.getNodes(document, patientElementUUIDXPath);
        
        if (nodes != null && nodes.getLength() == 1) {        	        	
        	patientUUID = ((Node) nodes.item(0)).getTextContent().trim();	            
         }         
        return patientUUID;
    }
}
