package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Barcode;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;

/**
 * Interface for class to resolve uuid for barcodes
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface BarcodeUuidResolver {
    /**
     * Given a barcode and, optionally, a uuid, will resolve any conflict and assign/lookup the UUID as needed.
     * Will return a Barcode object populated with the barcode and uuid.  Other properties of object not guaranteed
     * to be populated.
     *
     * There are six scenarios for this method:
     *
     * 1) barcode and uuid given, already associated -> returns same barcode and uuid
     * 2) barcode and uuid given, not associated -> associates them, then returns same barcode and uuid
     * 3) barcode and uuid given, uuid associated with different barcode -> throws exception
     * 4) barcode and uuid given, barcode associated with different uuid -> throws exception
     * 5) barcode given, associated with uuid in db -> returns barcode and uuid from db
     * 6) barcode given, no uuid in db -> if generate is true, generates uuid, associates with barcode, returns barcode and new uuid
     *
     * @param barcode the barcode
     * @param uuid the uuid, may be null
     * @param disease the disease the barcode is part of
     * @param center the BCR center that has submitted the barcode/uuid
     * @param generateUuidIfNeeded if true, will generate uuid if none; if false, will throw exception if no uuid
     * @return Barcode object with correct barcode and uuid set
     * @throws UUIDException if there is a conflict between what is passed in and the database
     */
    public Barcode resolveBarcodeAndUuid(String barcode, String uuid, Tumor disease, Center center, boolean generateUuidIfNeeded) throws UUIDException;
}
