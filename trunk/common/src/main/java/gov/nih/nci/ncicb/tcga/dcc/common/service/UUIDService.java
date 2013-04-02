/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.service;


import gov.nih.nci.ncicb.tcga.dcc.common.bean.Barcode;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Duration;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.SearchCriteria;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UuidBarcodeMapping;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.GenerationMethod;

import java.util.List;

/**
 * Service layer for different UUID related operations
 * This will be invoked by UI/ReST interface etc
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */

public interface UUIDService {

    /**
     * Generate UUIDs
     *
     * @param centerID      the center for which UUID has to be created.
     * @param numberOfUUIDs the number of UUIDs to be generated
     * @param method        of generation : Web, Rest etc
     * @param createdBy     the name of the user generating this UUID
     * @return returns the list of UUIDs generated
     * @throws UUIDException exception while generating the UUID
     */
    public List<UUIDDetail> generateUUID(int centerID, int numberOfUUIDs, GenerationMethod method, String createdBy)
            throws UUIDException;

    /**
     * Upload the list of UUIDs
     *
     * @param centerID  center id
     * @param uuidList  list of UUIDs
     * @param createdBy user id
     * @return list of UUIDs
     * @throws UUIDException exception if upload fails
     */
    public List<UUIDDetail> uploadUUID(int centerID, List<String> uuidList, String createdBy)
            throws UUIDException;


    /**
     * Returns the list of centers
     *
     * @return list of centers
     */
    public List<Center> getCenters();

    /**
     * Get the Center object for a given center name and center type code
     *
     * @param centerName center name
     * @param centerType center type
     * @return Center
     */
    public Center getCenterByNameAndType(String centerName, String centerType);

    /**
     * Get the Tumor object for a given disease name
     *
     * @param tumorName disease name
     * @return Tumor
     */
    public Tumor getTumorForName(String tumorName);

    /**
     * Search UUIDs for a given criteria
     *
     * @param searchCriteria search parameters
     * @return the search results
     */
    public List<UUIDDetail> searchUUIDs(SearchCriteria searchCriteria);

    /**
     * Get the list of active diseases
     *
     * @return all active diseases
     */
    public List<Tumor> getActiveDiseases();

    /**
     * Returns UUID Details
     *
     * @param uuid uuid
     * @return uuid detail
     * @throws UUIDException exception if UUID is not found
     */
    public UUIDDetail getUUIDDetails(String uuid) throws UUIDException;

    /**
     * Retuns the list of UUIDs generated in the specified time period
     *
     * @param duration duration for the report
     * @return list of UUIDs
     */
    public List<UUIDDetail> getNewlyGeneratedUUIDs(Duration duration);

    /**
     * Returns list of Submitted UUIDs
     *
     * @return list of UUIDs
     */
    public List<UUIDDetail> getSubmittedUUIDs();

    /**
     * Returns list of Missing UUIDs. The UUIDs are missing if there is no barcode associated with the UUID
     *
     * @return list of UUIDs
     */
    public List<UUIDDetail> getMissingUUIDs();

    /**
     * Returns true if uuid matches the pattern for valid UUIDs
     *
     * @param uuid the String to check.
     * @return valid uuid boolean.
     */
    public boolean isValidUUID(String uuid);

    /**
     * Gets UUID for a given barcode
     *
     * @param barcode the barcode
     * @return UUID
     */
    public String getUUIDForBarcode(final String barcode);

    /**
     * Return a list of all <code>Barcode</code>s starting with the given barcode prefix
     *
     * @param barcodePrefix the barcode prefix
     * @return a list of all <code>Barcode</code>s starting with the given barcode prefix
     */
    public List<Barcode> getBarcodesStartingWith(final String barcodePrefix);

    /**
     * Gets the latest barcode associated with this uuid, if any
     *
     * @param uuid the uuid to look for
     * @return the associated barcode or null if none
     */
    public String getLatestBarcodeForUUID(String uuid);

    /**
     * Use to add a new barcode_history record in database
     *
     * @param barcode details of the barcode history record to be added in database
     * @throws UUIDException exception thrown while saving data to database
     */
    public void addBarcode(final Barcode barcode) throws UUIDException;

    /**
     * Register a UUID that was uploaded (created elsewhere).  UUID record will be listed as created by the DCC.
     *
     * @param uuid     the UUID to register
     * @param centerID the center ID of the center that created the UUID
     * @throws UUIDException if the UUID could not be registered because it already exists, or for other reasons
     */
    public void registerUUID(String uuid, int centerID) throws UUIDException;

    /**
     * get a list of uuids that exist in the database
     *
     * @param UUIDs list of uuids
     * @return list of existing uuids
     */
    public List<String> getUUIDsExistInDB(final List<String> UUIDs);

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
     * Checks if a UUID is in the system or not. Does not care if it is associated with a barcode or not.
     *
     * @param uuid the uuid to check
     * @return true if it exists, false if not.
     */
    public boolean uuidExists(final String uuid);
}
