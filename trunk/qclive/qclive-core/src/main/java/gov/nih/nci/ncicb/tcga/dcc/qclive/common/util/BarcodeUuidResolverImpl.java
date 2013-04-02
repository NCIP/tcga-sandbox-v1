package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Barcode;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDDAO;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Implementation of barcode/uuid resolver.  Figures out if there is any conflict, looks up or generates UUID for barcode
 * if needed.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BarcodeUuidResolverImpl implements BarcodeUuidResolver {
    private UUIDService uuidService;
    private UUIDDAO uuidDAO;
    private CommonBarcodeAndUUIDValidator barcodeAndUUIDValidator;

    @Override
    public Barcode resolveBarcodeAndUuid(final String barcode, final String uuid, final Tumor disease,
                                         final Center center, final boolean generateUuidIfNeeded) throws UUIDException {
        final String lowercaseUuid = (uuid == null ? null : uuid.toLowerCase());

        final String uuidForBarcode = uuidService.getUUIDForBarcode(barcode);
        final boolean uuidGiven = (lowercaseUuid != null && !StringUtils.isEmpty(lowercaseUuid));

        final Barcode barcodeDetail = new Barcode();
        barcodeDetail.setBarcode(barcode);
        barcodeDetail.setUuid(lowercaseUuid);
        barcodeDetail.setDisease(disease);

        if (uuidGiven) {
            if (!barcodeAndUUIDValidator.validateUUIDFormat(lowercaseUuid)) {
                throw new UUIDException("Error processing barcode " + barcode + ": UUID " + lowercaseUuid + " does not have a valid format");
            }
            final String barcodeForUuid = uuidService.getLatestBarcodeForUUID(lowercaseUuid);
            final boolean uuidExists = uuidDAO.uuidExists(lowercaseUuid);
            if (uuidExists) {  // in db
                if (uuidForBarcode == null && barcodeForUuid == null) {
                    // uuid and barcode are not associated in the database, so associate them
                    associateBarcodeWithUuid(barcodeDetail);
                } else if (uuidForBarcode != null && uuidForBarcode.equals(lowercaseUuid)
                        && barcodeForUuid != null && barcodeForUuid.equals(barcode)) {
                    // uuid in system and already associated with this barcode
                    // nothing to do here, just put in code to show all options
                } else if (uuidForBarcode != null && !uuidForBarcode.equals(lowercaseUuid)) {
                    // CONFLICT -- barcode linked to different uuid in db than given in file
                    throw new UUIDException(new StringBuilder().append("Error processing barcode ").append(barcode).
                            append(": barcode is already associated with UUID ").append(uuidForBarcode).toString());
                } else if (barcodeForUuid != null && !barcodeForUuid.equals(barcode)) {
                    // CONFLICT -- uuid from file linked to a different barcode in db
                    throw new UUIDException(new StringBuilder().append("Error processing barcode ").append(barcode).
                            append(": UUID ").append(lowercaseUuid).append(" is already associated with barcode ").append(barcodeForUuid).toString());
                }
            } else { // uuid was not in the db yet
                if (uuidForBarcode != null && !uuidForBarcode.equals(lowercaseUuid)) {
                    // CONFLICT -- barcode linked to a different uuid in db
                    throw new UUIDException(new StringBuilder().append("Error processing barcode ").append(barcode).
                            append(": barcode is already associated with UUID ").append(uuidForBarcode).toString());
                } else {
                    // register the UUID and associate with the barcode
                    uuidService.registerUUID(lowercaseUuid, center.getCenterId());
                    associateBarcodeWithUuid(barcodeDetail);
                }
            }
        } else { // no UUID given
            if (uuidForBarcode != null) {  // found in the db already
                barcodeDetail.setUuid(uuidForBarcode);
            } else {
                if (generateUuidIfNeeded) {
                    // generate a new UUID
                    final List<UUIDDetail> newUUID = uuidService.generateUUID(center.getCenterId(), 1, UUIDConstants.GenerationMethod.API, UUIDConstants.MASTER_USER);
                    barcodeDetail.setUuid(newUUID.get(0).getUuid());
                    associateBarcodeWithUuid(barcodeDetail);
                } else {
                    throw new UUIDException(new StringBuilder().append("UUID for ").append(barcode).append(" not found").toString());
                }
            }
        }
        return barcodeDetail;
    }

    public void setUuidService(final UUIDService uuidService) {
        this.uuidService = uuidService;
    }

    public void setUuidDAO(final UUIDDAO uuidDAO) {
        this.uuidDAO = uuidDAO;
    }

    private void associateBarcodeWithUuid(final Barcode barcodeDetail) throws UUIDException {
        barcodeDetail.setEffectiveDate(new java.util.Date());
        uuidService.addBarcode(barcodeDetail);
    }

    public void setBarcodeAndUUIDValidator(CommonBarcodeAndUUIDValidator barcodeAndUUIDValidator) {
        this.barcodeAndUUIDValidator = barcodeAndUUIDValidator;
    }
}
