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
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.TumorQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDDAO;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.util.EmailManager;
import gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants;
import gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * The service layer implementation class for different UUID related operations
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */

@Service
public class UUIDServiceImpl implements UUIDService {

    protected final Log logger = LogFactory.getLog(getClass());

    private UUIDDAO uuidDAO;

    private CenterQueries centerQueries;

    private TumorQueries tumorQueries;

    private EmailManager emailManager;

    public void setUuidDAO(final UUIDDAO uuidDAO) {
        this.uuidDAO = uuidDAO;
    }

    public void setCenterQueries(final CenterQueries centerQueries) {
        this.centerQueries = centerQueries;
    }

    public void setTumorQueries(final TumorQueries tumorQueries) {
        this.tumorQueries = tumorQueries;
    }

    public void setEmailManager(final EmailManager emailManager) {
        this.emailManager = emailManager;
    }

    /**
     * Method used to generate UUIDs for centers
     *
     * @param centerID      the center for which UUID has to be created
     * @param numberOfUUIDs the number of UUIDs to be generated
     */
    public List<UUIDDetail> generateUUID(final int centerID, final int numberOfUUIDs,
                                         final UUIDConstants.GenerationMethod method, final String createdBy) throws UUIDException {

        List<UUIDDetail> uuidList = new ArrayList<UUIDDetail>();
        UUIDDetail detail;

        Center center = centerQueries.getCenterById(centerID);
        for (int uuidCount = 0; uuidCount < numberOfUUIDs; uuidCount++) {
            detail = new UUIDDetail();
            detail.setUuid(UUIDGenerator.getUUID().toString());
            detail.setGenerationMethod(method);
            detail.setCenter(center);
            detail.setCreationDate(new Date());
            detail.setCreatedBy(createdBy);
            uuidList.add(detail);
        }

        uuidDAO.addUUID(uuidList);

        //when the UUIDs are generated an email should be sent to the center users
        // with the list of all generated UUIDs
        String centerEmail = center.getCenterEmail();
        if (emailManager != null && method != UUIDConstants.GenerationMethod.API && centerEmail != null) {
            emailManager.sendNewUUIDListToCenter(centerEmail, uuidList);
        }

        return uuidList;
    }

    /**
     * Returns true if uuid matches the pattern for valid UUIDs
     *
     * @param uuid the String to check.
     * @return valid uuid boolean.
     */
    public boolean isValidUUID(final String uuid) {
        return UUIDConstants.UUID_PATTERN.matcher(uuid).matches();
    }

    /**
     * Saves the list of UUIDs uploaded from a file
     *
     * @param centerID  center id
     * @param uuidList  list of UUIDs
     * @param createdBy user id of the creator
     * @return list of UUID uploaded
     * @throws UUIDException
     */
    public List<UUIDDetail> uploadUUID(final int centerID, final List<String> uuidList, final String createdBy)
            throws UUIDException {

        UUIDDetail detail;
        List<UUIDDetail> uuidDetailList = new ArrayList<UUIDDetail>();
        for (final String uuid : uuidList) {
            detail = new UUIDDetail();
            detail.setUuid(uuid);
            detail.setGenerationMethod(UUIDConstants.GenerationMethod.Upload);
            detail.setCenter(centerQueries.getCenterById(centerID));
            detail.setCreationDate(new Date());
            detail.setCreatedBy(createdBy);
            uuidDetailList.add(detail);
        }
        uuidDAO.addUUID(uuidDetailList);

        //when the UUIDs are generated an email should be sent to the center users
        // with the list of all generated UUIDs

        String centerEmail = centerQueries.getCenterById(centerID).getCenterEmail();
        if (centerEmail != null) {
            emailManager.sendNewUUIDListToCenter(centerEmail, uuidDetailList);
        }

        return uuidDetailList;
    }

    /**
     * Get the Center object for a given center name and center type code
     *
     * @param centerName center name
     * @param centerType center type
     * @return Center
     */
    public Center getCenterByNameAndType(final String centerName, final String centerType) {
        return centerQueries.getCenterByName(centerName, centerType);
    }

    /**
     * Returns the list of centers
     * Note that the centers are retrieved from database only once
     *
     * @return list of centers
     */
    public List<Center> getCenters() {
        return centerQueries.getCenterList();
    }

    /**
     * Get the Tumor object for a given disease name
     *
     * @param tumorName disease name
     * @return Tumor
     */
    public Tumor getTumorForName(final String tumorName) {
        return tumorQueries.getTumorForName(tumorName);
    }

    /**
     * Seach UUIDs for the given search criteria
     *
     * @param searchCriteria search criteria
     * @return List of UUIDs for the given search criteria
     */
    public List<UUIDDetail> searchUUIDs(final SearchCriteria searchCriteria) {
        return uuidDAO.searchUUIDs(searchCriteria);
    }

    /**
     * Returns list of active diseases
     * Note that the diseases are retrieved from database only once
     */
    public List<Tumor> getActiveDiseases() {
        return uuidDAO.getActiveDiseases();
    }

    public UUIDDetail getUUIDDetails(final String uuid) throws UUIDException {
        return uuidDAO.getUUIDDetail(uuid);
    }

    /**
     * Returns the list of UUIDs generated in the specified time period
     *
     * @param duration duration for the report : day/month/week
     * @return list of UUIDs
     */
    public List<UUIDDetail> getNewlyGeneratedUUIDs(final Duration duration) {
        return uuidDAO.getNewlyGeneratedUUIDs(duration);
    }

    /**
     * Returns list of Submitted UUIDs
     *
     * @return list of UUIDs
     */
    public List<UUIDDetail> getSubmittedUUIDs() {
        return uuidDAO.getSubmittedUUIDs();
    }

    /**
     * Returns list of Missing UUIDs. The UUIDs are missing if there is no barcode associated with the UUID
     *
     * @return list of UUIDs
     */
    public List<UUIDDetail> getMissingUUIDs() {
        return uuidDAO.getMissingUUIDs();
    }

    /**
     * Barcode for a given human-readable barcode
     *
     * @param barcode
     * @return UUID
     */
    public String getUUIDForBarcode(final String barcode) {
        return uuidDAO.getUUIDForBarcode(barcode);
    }

    @Override
    public List<Barcode> getBarcodesStartingWith(final String barcodePrefix) {
        return uuidDAO.getBarcodesStartingWith(barcodePrefix);
    }

    @Override
    public String getLatestBarcodeForUUID(final String uuid) {
        return uuidDAO.getLatestBarcodeForUUID(uuid);
    }

    /**
     * Use to add a new barcode_history record in database
     *
     * @param barcode details of the barcode history record to be added in database
     * @throws UUIDException exception thrown while saving data to database
     */
    public void addBarcode(final Barcode barcode) throws UUIDException {
        uuidDAO.addBarcode(barcode);
    }

    /**
     * Register the UUID with the system.
     *
     * @param uuid     the UUID to register
     * @param centerID the centerID of the center that created the UUID
     * @throws UUIDException if the UUID already exists, or uses an invalid format
     */
    public void registerUUID(final String uuid, final int centerID) throws UUIDException {
        if (!uuidDAO.uuidExists(uuid)) {
            final UUIDDetail uuidDetail = new UUIDDetail();
            uuidDetail.setUuid(uuid);
            uuidDetail.setCreatedBy(UUIDConstants.MASTER_USER);
            uuidDetail.setCreationDate(new Date());
            uuidDetail.setGenerationMethod(UUIDConstants.GenerationMethod.Upload);
            final Center center = centerQueries.getCenterById(centerID);
            if (center == null) {
                throw new UUIDException("Center with ID " + centerID + " not found");
            }
            uuidDetail.setCenter(center);
            uuidDAO.addUUID(Arrays.asList(uuidDetail));
        } else {
            throw new UUIDException("UUID " + uuid + " is already registered in the system");
        }
    }

    @Override
    public List<String> getUUIDsExistInDB(final List<String> UUIDs) {
        return uuidDAO.getUUIDsExistInDB(UUIDs);
    }

    @Override
    public List<String> getExistingBarcodes(final List<String> barcodes) {
        return uuidDAO.getExistingBarcodes(barcodes);
    }

    @Override
    public List<UuidBarcodeMapping> getLatestBarcodesForUUIDs(final List<String> uuids) {
        return uuidDAO.getLatestBarcodesForUUIDs(uuids);
    }

    @Override
    public List<UuidBarcodeMapping> getUUIDsForBarcodes(final List<String> barcodes) {
        return uuidDAO.getUUIDsForBarcodes(barcodes);
    }

    @Override
    public boolean uuidExists(final String uuid) {
        return uuidDAO.uuidExists(uuid);
    }
}
