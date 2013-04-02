/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.dao;


import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.PendingUUID;

import java.util.List;

/**
 * DAO layer interface for pending UUIDs.
 *
 * @author Stan Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface PendingUUIDDAO {

    /**
     * Inserts a new pending UUID.
     *
     * @param pendingUUID the pending UUID to insert
     * @throws IllegalArgumentException if <code>pendingUUID</code> fails bean validation
     */
    public void insertPendingUUID(final PendingUUID pendingUUID);

    /**
     * Bulk insert of pending UUIDs.
     *
     * @param pendingUUIDList the list of pending UUIDs to insert
     * @throws IllegalArgumentException if a {@link PendingUUID} in the list fails bean validation
     */
    public void insertPendingUUIDList(final List<PendingUUID> pendingUUIDList);

    /**
     * Deletes a pending UUID.
     *
     * @param uuid the UUID to delete
     * @return the number of rows deleted
     * @throws IllegalArgumentException if <code>uuid</code> is empty
     */
    public int deletePendingUUID(final String uuid);


    /**
     * Retrieves all pending UUIDs that have dcc received date to null (meaning pending).
     *
     * @return all pending UUIDs
     */
    public List<PendingUUID> getAllPendingUUIDs();

    /**
     * Checks if the UUID has already been received by DCC.
     *
     * @param uuid the UUID to check
     * @return <code>true</code> if the uuid already exists, <code>false</code> otherwise
     */
    public boolean alreadyReceivedUUID(final String uuid);

    /**
     * Checks if the UUID is already in pending status.
     *
     * @param uuid the UUID to check
     * @return <code>true</code> if the uuid is already pending, <code>false</code> otherwise
     */
    public boolean alreadyPendingUUID(final String uuid);

    /**
     * Checks if the barcode has already been received by DCC.
     *
     * @param barcode the barcode to check
     * @return <code>true</code> if the barcode already exists, <code>false</code> otherwise
     */
    public boolean alreadyReceivedBarcode(final String barcode);

    /**
     * Checks if the barcode is already in pending status.
     *
     * @param barcode the barcode to check
     * @return <code>true</code> if the barcode is already pending, <code>false</code> otherwise
     */
    public boolean alreadyPendingBarcode(final String barcode);

    /**
     * Return the {@link PendingUUID} bean for the given UUID.
     *
     * @param uuid the uuid of the {@link PendingUUID} to retrieve
     * @return the {@link PendingUUID} bean for the given UUID
     */
    public PendingUUID getPendingUuid(final String uuid);

    /**
     * Return a list of {@link PendingUUID} for the given list of UUIDs.
     *
     * @param uuids the list of UUIDs to use for lookup
     * @return a list of {@link PendingUUID} for the given list of UUIDs
     */
    public List<PendingUUID> getPendingUUIDs(final List<String> uuids);

    /**
     * Validates a center against center_to_bcr_center table
     *
     * @param bcrCenterId center Id to validate
     * @return true if valid center, false otherwise
     */
    public boolean isValidCenter(final String bcrCenterId);

    /**
     * Validates a batch number against the database
     *
     * @param batchNumber batchNumber to validate
     * @return true if valid batchNumber, false otherwise
     */
    public boolean isValidBatchNumber(final String batchNumber);

    /**
     * validates if sample type is valid against the database
     *
     * @param sampleType to validate
     * @return True if valid ,false otherwise
     */
    public boolean isValidSampleType(final String sampleType);

    /**
     * validates if analyte type is valid against the database
     *
     * @param analyteType to validate
     * @return True if valid ,false otherwise
     */
    public boolean isValidAnalyteType(final String analyteType);
}
