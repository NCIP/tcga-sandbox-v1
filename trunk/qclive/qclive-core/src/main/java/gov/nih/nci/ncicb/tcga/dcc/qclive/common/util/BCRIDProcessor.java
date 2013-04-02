/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ShippedBiospecimen;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BCRID;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BiospecimenToFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Interface for BCRID loader.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev: 3419 $
 */
public interface BCRIDProcessor {

    BCRID parseAliquotBarcode(String aliquotBarcode) throws ParseException;

    /**
     * Save the barcode to database
     * @param theBCRID BCRID
     * @param useIdFromCommon Set to true if the bcrId set in the BCRID obejct should be used while adding
     * the bar code to database, false otherwise
     * @param biospecimenArchiveId biospecimen to archive association id, the method updates it to the new value if a new is inserted
     * @param disease the disease the barcode belongs to
     * @param center the BCR center submitting the barcode
     * @throws ParseException parsing exception
     * @throws UUIDException uuid exception 
     */
    void storeBcrBarcode(final BCRID theBCRID, final Boolean useIdFromCommon, final int[] biospecimenArchiveId, final Tumor disease, final Center center) throws ParseException, UUIDException;


    void storeBarcode( final BCRID barcode, final Boolean useIdFromCommon, final Integer bcrIdFromCommon, final Tumor disease ) throws ParseException, UUIDException;

    /**
     * Adds biospecimen to file association in database, adds barcode if not present already
     * @param fileId file Id
     * @param barcode barcode Id
     * @param colName column name
     * @param archiveId archive id
     * @param useIdFromCommon Set to true if the barcode Id  from common database should be used for saving the barcode to database, false otherwise
     * @param bcrAndBcrFileId entries in the array : 0=bcrId, 1=bcrToFileId
     * @throws UUIDException uuid exception
     * @throws Processor.ProcessorException processor exception 
     * @throws ParseException parsing exception

     */
    void addFileAssociation( Long fileId, BCRID barcode, String colName, Long archiveId, Boolean useIdFromCommon, int[] bcrAndBcrFileId, Tumor disease) throws ParseException, Processor.ProcessorException, UUIDException;

    void addBioSpecimenToFileAssociations( final List<BiospecimenToFile> biospecimenToFileList, final Tumor disease);

    void addBioSpecimenBarcodes( final List<BCRID> bcrIdList, final Tumor disease) throws UUIDException ;

    // Returns biospecimen ids for the given barcodes.
    List<Integer> getBiospecimenIds(final List<String> barcodes);

    /**
     * Returns biospecimen ids for the given UUID
     * @param uuid
     * @return null if there is no matching UUID
     */
    public Long getBiospecimenIdForUUID(String uuid);

    /**
     * Adds biospecimen id to fileid association in biosspecimen_to_file table
     * @param fileId
     * @param barcodeId
     * @param colName
     * @param useIdFromCommon
     * @param bcrFileId
     * @return biospecimen file id
     */
    public int addFileAssociation(final Long fileId, final Integer barcodeId, final String colName,final Boolean useIdFromCommon, final int bcrFileId);

    /**
     * Method for finding BCR IDs in an XML file.
     *
     * @param file the XML file to look through
     * @return a List of String arrays, where in each array, the first element is the aliquot barcode, and the second is
     *         the ship date, and the third is the UUID found in the file
     * @throws TransformerException if there is an error transforming
     * @throws XPathExpressionException if there is an error in an expression in the file
     * @throws IOException if the file can't be read or something
     * @throws javax.xml.parsers.ParserConfigurationException if the parser is not configured correctly
     * @throws org.xml.sax.SAXException if there is a parse problem
     */
    public List<String[]> findAllAliquotsInFile(File file) throws TransformerException, XPathExpressionException, IOException, SAXException, ParserConfigurationException;

    public List<ShippedBiospecimen> findAllShippedPortionsInFile(File file) throws IOException, SAXException, ParserConfigurationException, TransformerException, XPathExpressionException, ParseException;

    public void setAliquotElementXPath(String aliquotElement);

    public void setAliquotBarcodeElement(String aliquotBarcodeElement);

    public void setShipDayElement(String shipDayElement);

    public void setShipMonthElement(String shipMonthElement);

    public void setShipYearElement(String shipYearElement);

    public void setAliquotUuidElement(String aliquotUuidElement);

	/**
	* Returns true if slide barcode exists otherwise false
	* @param barcode
	* @return true/false
	*/
    public boolean slideBarcodeExists(final String barcode);
    
    /**
     * Extract patient UUID from XML file 
     * @param file where to search for UUID
     * @return UUID
     * @throws ParserConfigurationException, 
     * 		   IOException, 
     * 		   SAXException,
     * 		   TransformerException if parsing of the XML file fails
     */
    public String getPatientUUIDfromFile(final File file) throws ParserConfigurationException, IOException, SAXException,TransformerException;
    
}
