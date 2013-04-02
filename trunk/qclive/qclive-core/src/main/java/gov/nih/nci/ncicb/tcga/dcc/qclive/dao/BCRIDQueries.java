/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BCRID;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BiospecimenToFile;

import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Robert S. Sfeir
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface BCRIDQueries {

    public Collection getAllBCRIDs();

    /**
     * Adds barcode to database
     *
     * @param theBCRId bcr id
     * @param useIdFromCommon Set to true if the bcrId set in the BCRID object should be used while adding the bar code
     * to database, false otherwise
     *
     * @return barcode id
     */
    Integer addBCRID(BCRID theBCRId, Boolean useIdFromCommon);

    Integer updateBCRIDStatus(BCRID theBCRId);

    Integer exists(BCRID theID);

    Integer exists(String theBarcode);

    boolean uuidExists(String uuid);

    void updateShipDate(BCRID theBCRID) throws ParseException;

    /**
     * Adds bcr to archive relationship in bcr_biospecimen_to_archive table
     *
     * @param theBCRID bcr id
     * @param useIdFromCommon Set to true if the bcrArchiveId from common database should be used for saving the
     * association to database, false otherwise
     * @param bcrArchiveId biospecimen to archive association id
     */
    void addArchiveRelationship(BCRID theBCRID, Boolean useIdFromCommon, int[] bcrArchiveId);

    /**
     * Adds a barcode_history record
     * @param theBCRID
     * @param disease
     * @throws UUIDException
     */
    void addBarcodeHistory(final BCRID theBCRID, final Tumor disease)
    throws UUIDException;

    /**
     * Returns true if slide barcode exists otherwise false
     * @param barcode
     * @return true/false
     */
    public boolean slideBarcodeExists(final String barcode);
    /**
     * Adds BCR to file association in database
     *
     * @param fileId file id
     * @param barcodeId barcode id
     * @param colName column name
     * @param useIdFromCommon Set to true if the bcrFileId from common database should be used for saving the
     * association to database, false otherwise
     * @param bcrFileId bcr to file association id
     */
    int addFileAssociation(Long fileId, Integer barcodeId, String colName, Boolean useIdFromCommon, int bcrFileId);

    Integer findExistingAssociation(Long fileId, Integer bcrId, String colName);

    public void addBioSpecimenToFileAssociations(final List<BiospecimenToFile> biospecimenToFileList);

    public void addBioSpecimenBarcodes(final List<BCRID> bcrIdList, final Tumor disease) throws UUIDException ;
    // Returns biospecimen ids for the given barcodes.

    public List<Integer> getBiospecimenIds(final List<String> barcodes);

    public Map<String,Integer> getBiospecimenIdsForBarcodes(final List<String> barcodes);
    /**
     * Get the UUID for the biospecimen -- does not check the UUID tables, just biospecimen_barcode!
     * @param theID the biospecimen to look up
     * @return the UUID or null if there is none set
     */
    public String getBiospecimenUUID(BCRID theID);

    /**
     * returns biospecimenid for the given uuid
     * @param uuid
     * @return
     */
    public Long getBiospecimenIdForUUID(String uuid);

    public void updateUUIDForBarcode(BCRID theBCRId);    

    public String getHistoryUUIDForBarcode(BCRID bcrId);

    public List<BCRID> getArchiveBarcodes(long archiveId);

    public void updateBiospecimenToFileAssociations(List<BiospecimenToFile>  biospecimenToFiles);
    
}
