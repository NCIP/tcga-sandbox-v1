/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Barcode;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Duration;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.SearchCriteria;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UuidBarcodeMapping;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;

import java.util.List;

/**
 * DAO Layer interface for UUIDs
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */

public interface UUIDDAO {

    /**
     * Use to add a new UUID in database
     *
     * @param uuidDetail details of the UUID to be added in database
     * @return number of rows created
     * @throws UUIDException excpetion thrown while saving data to database
     */
    public int addUUID(List<UUIDDetail> uuidDetail) throws UUIDException;

    /**
     * Adds UUID which doesn't exist into UUID table.  THis API doesn't throw an
     * exception if UUID already exists in the database, it simple ignores that UUID.
     *
     * @param uuidDetailList
     */

    public void addNewUUIDs(final List<UUIDDetail> uuidDetailList);

    /**
     * Use to add a new barcode_history record in database
     *
     * @param barcode details of the barcode history record to be added in database
     * @throws UUIDException exception thrown while saving data to database
     */
    public void addBarcode(Barcode barcode) throws UUIDException;

    /**
     * Get list of active diseases
     *
     * @return all active diseases
     */
    public List<Tumor> getActiveDiseases();

    /**
     * Retunrs disease Object for a given disease Id
     *
     * @param diseaseId disease id
     * @return Tumor
     */
    public Tumor getDisease(int diseaseId);

    /**
     * Search UUIDs for the given criteria
     *
     * @param criteria : search criteria
     * @return search results
     */
    public List<UUIDDetail> searchUUIDs(SearchCriteria criteria);


    /**
     * Returns UUID Details
     *
     * @param uuid uuid
     * @return uuid detail object
     * @throws UUIDException if UUID is not found
     */
    public UUIDDetail getUUIDDetail(String uuid) throws UUIDException;

    /**
     * Barcode for a given barcode id
     *
     * @param barcodeId barcode id
     * @return Barcode
     */
    public Barcode getBarcodeForId(long barcodeId);

    /**
     * Barcode for a given human-readable barcode
     *
     * @param barcode
     * @return UUID
     */
    public String getUUIDForBarcode(String barcode);

    /**
     * Return a list of all <code>Barcode</code>s starting with the given barcode prefix
     *
     * @param barcodePrefix the barcode prefix
     * @return a list of all <code>Barcode</code>s starting with the given barcode prefix
     */
    public List<Barcode> getBarcodesStartingWith(final String barcodePrefix);

    /**
     * Returns the list of UUIDs generated in the specified time period
     *
     * @param duration duration : day/month/week
     * @return list of UUIDs
     */
    public List<UUIDDetail> getNewlyGeneratedUUIDs(Duration duration);

    /**
     * Returns the list of Submitted UUIDs. The UUIDs are submitted if there is a
     * latest barcode associated with the UUID
     *
     * @return list of submitted UUIDs
     */
    public List<UUIDDetail> getSubmittedUUIDs();

    /**
     * Returns the list of Missing UUIDs. The UUIDs are missing if there is no barcode associated with the UUID
     *
     * @return list of missing UUIDs
     */
    public List<UUIDDetail> getMissingUUIDs();

    /**
     * Gets the latest barcode for a uuid.
     *
     * @param uuid the UUID to look up
     * @return the latest barcode for this UUID, or null if there is no latest barcode
     */
    public String getLatestBarcodeForUUID(String uuid);

    /**
     * Checks if a UUID is in the system or not. Does not care if it is associated with a barcode or not.
     *
     * @param uuid the uuid to check
     * @return true if it exists, false if not.
     */
    public boolean uuidExists(String uuid);

    /**
     * get a list of uuids that exist in the database
     *
     * @param UUIDsToValidate
     * @return list of existing uuids
     */
    public List<String> getUUIDsExistInDB(final List<String> UUIDsToValidate);

    /**
     * get a list of barcodes that exist in the database
     *
     * @param barcodes list of barcodes
     * @return list of existing barcodes
     */
    public List<String> getExistingBarcodes(final List<String> barcodes);

    /**
     * get a list of latest UuidBarcodeMapping from uuids in the database
     *
     * @param uuids list of uuids
     * @return list of UuidBarcodeMapping
     */
    public List<UuidBarcodeMapping> getLatestBarcodesForUUIDs(final List<String> uuids);

    /**
     * get a list of latest UuidBarcodeMapping from barcodes in the database
     *
     * @param barcodes list of barcodes
     * @return list of UuidBarcodeMapping
     */
    public List<UuidBarcodeMapping> getUUIDsForBarcodes(final List<String> barcodes);

    /**
     * Adds a relationship between a file_id and participant UUID
     *
     * @param UUID   participant uuid
     * @param fileId file_id
     */
    public void addParticipantFileUUIDAssociation(final String UUID, final Long fileId);

    /**
     * Adds relationship between patient UUID and file id for the given data
     *
     * @param patientsUUIDAndFileId a list of the relationships to add, each element being an array of 4 items: UUID, file Id, UUID, file Id
     */
    public void addParticipantFileUUIDAssociation(final List<Object[]> patientsUUIDAndFileId);  
}


