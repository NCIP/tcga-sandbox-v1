/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.service;


import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.PendingUUID;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.PendingUUIDDAO;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.util.JSONUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Pending UUID service used to parse and validate pending UUID messages.
 *
 * @author Stan Girshik
 *         Last updated by: Dominique Berton
 * @version $Rev$
 */


@Service
public class PendingUUIDProcessor implements PendingUUIDService {

    @Autowired
    PendingUUIDDAO pendingUUIDDao;

    @Autowired
    CommonBarcodeAndUUIDValidator commonBarcodeAndUUIDValidator;

    protected final static String BATCH_NUMBER_KEY = "batch_number";
    protected final static String BCR_KEY = "bcr";
    protected final static String SHIPPED_DATE_KEY = "ship_date";
    protected final static String BCR_VALUE_NCH = "NCH";
    protected final static String BCR_VALUE_IGC = "IGC";
    protected final static String PLATE_ID = "plate_id";
    protected final static String CENTER = "center";
    protected final static String PLATE = "plate";
    protected final static String NULL_VALUE = "null";
    protected final static String ANALYTE_TYPE_KEY = "analyte_type";
    protected final static String PORTION_NUMBER_KEY = "portion_number";
    protected final static String SAMPLE_TYPE_KEY = "sample_type";
    protected final static String VIAL_NUMBER_KEY = "vial_number";
    protected final static String BCR_ALIQUOT_UUID_KEY = "bcr_aliquot_uuid";
    protected final static String BCR_SHIPPED_PORTION_UUID_KEY = "bcr_shipment_portion_uuid";
    protected final static String BCR_ALIQUOT_BARCODE_KEY = "bcr_aliquot_barcode";
    protected final static String BCR_SHIPPED_PORTION_BARCODE_KEY = "bcr_shipment_portion_barcode";
    protected final static String ALIQUOT = "Aliquot";
    protected final static String SHIPPED_PORTION = "Shipped Portion";
    protected static final String DATE_FORMAT_US_DASHES = "MM-dd-yyyy";

    protected final Log logger = LogFactory.getLog(PendingUUIDProcessor.class);

    private List<String> errorMessages;
    private Map<String, String> uuidsPresent;
    private Map<String, String> barcodesPresent;

    @Override
    public List<PendingUUID> getPendingUUIDsFromJson(final String incomingJson) {
        final LinkedList<PendingUUID> resList = new LinkedList<PendingUUID>();
        final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_US_DASHES);
        final JSONObject root = parsePendingUUID(incomingJson);
        final JSONObject plate = root.getJSONObject(PLATE);
        //Plate coordinates are A-H, 1-12
        for (int i = 65; i < 73; i++) {
            for (int j = 1; j <= 12; j++) {
                final PendingUUID pendingUUID = new PendingUUID();
                final String plateCoordinate = "" + (char) i + j;
                final JSONObject coordinate = plate.getJSONObject(plateCoordinate);
                pendingUUID.setBcr(root.getString(BCR_KEY));
                pendingUUID.setCenter(root.getString(CENTER));
                try {
                    pendingUUID.setShippedDate(dateFormat.parse(root.getString(SHIPPED_DATE_KEY)));
                } catch (ParseException e) {
                    //Should never happen as validation occurred.
                    logger.info(e);
                }
                pendingUUID.setPlateId(root.getString(PLATE_ID));
                pendingUUID.setPlateCoordinate(plateCoordinate);
                if (!NULL_VALUE.equals(plate.getString(plateCoordinate))) {
                    pendingUUID.setUuid(getAliquotOrShippedPortion(pendingUUID, coordinate,
                            BCR_ALIQUOT_UUID_KEY, BCR_SHIPPED_PORTION_UUID_KEY));
                    pendingUUID.setBcrAliquotBarcode(getAliquotOrShippedPortion(pendingUUID, coordinate,
                            BCR_ALIQUOT_BARCODE_KEY, BCR_SHIPPED_PORTION_BARCODE_KEY));
                    pendingUUID.setBatchNumber(coordinate.getString(BATCH_NUMBER_KEY));
                    pendingUUID.setSampleType(coordinate.getString(SAMPLE_TYPE_KEY));
                    pendingUUID.setAnalyteType(coordinate.getString(ANALYTE_TYPE_KEY));
                    pendingUUID.setPortionNumber(coordinate.getString(PORTION_NUMBER_KEY));
                    pendingUUID.setVialNumber(coordinate.getString(VIAL_NUMBER_KEY));
                }
                resList.add(pendingUUID);
            }
        }
        return resList;
    }

    protected String getAliquotOrShippedPortion(final PendingUUID pendingUUID, final JSONObject coordinate,
                                                final String aliquotKey, final String shippedPortionKey) {
        if (coordinate.has(aliquotKey)) {
            pendingUUID.setItemType(ALIQUOT);
            return coordinate.getString(aliquotKey);
        } else {
            pendingUUID.setItemType(SHIPPED_PORTION);
            return coordinate.getString(shippedPortionKey);
        }
    }

    @Override
    public void persistPendingUUIDs(List<PendingUUID> pendingUUIDList) {
        pendingUUIDDao.insertPendingUUIDList(pendingUUIDList);
    }

    @Override
    public List<String> getErrors() {
        return errorMessages;
    }

    @Override
    public boolean parseAndValidatePendingUUIDJson(final String incomingPendingUUIDMsg) {
        errorMessages = new ArrayList<String>();
        uuidsPresent = new HashMap<String, String>();
        barcodesPresent = new HashMap<String, String>();
        boolean returnValue = true;
        if (JSONUtils.mayBeJSON(incomingPendingUUIDMsg)) {
            final JSONObject parsedJSON = parsePendingUUID(incomingPendingUUIDMsg);
            returnValue &= validateBcr(parsedJSON);
            returnValue &= validateShippedDate(parsedJSON);
            returnValue &= validatePlateId(parsedJSON);
            returnValue &= validateCenter(parsedJSON);
            returnValue &= validatePlate(parsedJSON);
        } else {
            returnValue = false;
            errorMessages.add("The pending uuid message is not a well-formed JSON message");
        }
        return returnValue;
    }

    protected JSONObject parsePendingUUID(final String incomingPendingUUIDJSONMsg) {
        return (JSONObject) JSONSerializer.toJSON(incomingPendingUUIDJSONMsg);
    }

    /**
     * validate a plate
     *
     * @param plateIdNumber
     * @return true if validation passes
     */
    protected boolean validatePlate(final JSONObject plateIdNumber) {
        boolean retValue = true;
        if (checkElementExistence(PLATE, plateIdNumber, null)) {
            if (checkElementType(PLATE, plateIdNumber, null, JSONObject.class)) {
                retValue &= validateWells(plateIdNumber.getJSONObject(PLATE));
            } else {
                retValue = false;
            }
        } else {
            retValue = false;
        }
        return retValue;
    }

    /**
     * validates wells
     *
     * @param plateObj
     * @return true if validation passes
     */
    protected boolean validateWells(final JSONObject plateObj) {
        boolean retValue = true;
        for (int i = 65; i < 73; i++) {
            for (int j = 1; j <= 12; j++) {
                final String wellName = "" + (char) i + j;
                if (!plateObj.containsKey(wellName)) {
                    retValue = false;
                    errorMessages.add("A well " + wellName + " is missing on the plate. A plate must contain" +
                            " 96 wells with names made up of rows represented by letters A-H," +
                            " and columns represented by the numbers 1-12. ( e.g A1, A2.. H12)");
                } else {
                    final Object wellObj = plateObj.get(wellName);
                    // validate if null
                    if (wellObj instanceof JSONNull) {
                        retValue &= true;
                        // error out if any different data type
                    } else if (wellObj instanceof JSONObject) {
                        retValue &= validateAWell((JSONObject) wellObj, wellName);
                    } else {
                        retValue = false;
                        errorMessages.add("A well '" + wellName + "' value has to either " +
                                "be an object with well elements defined or a null.");
                    }
                }
            }
        }
        retValue &= checkDuplicates(uuidsPresent, true);
        retValue &= checkDuplicates(barcodesPresent, false);
        return retValue;
    }

    /**
     * check for duplicates values in the values of a map
     *
     * @param map
     * @param uuid
     * @return false if duplicates are found
     */
    protected boolean checkDuplicates(final Map<String, String> map, final boolean uuid) {
        boolean valid = true;
        final List<String> dups = getDuplicates(map.values());
        if (dups.size() > 0) {
            valid = false;
            for (final String dup : dups) {
                for (final String key : getKeysByValue(map, dup)) {
                    if (uuid) {
                        errorMessages.add("The uuid '" + map.get(key) + "' in well '" + key + "' is a duplicate.");
                    } else {
                        errorMessages.add("The barcode '" + map.get(key) + "' in well '" + key + "' is a duplicate.");
                    }
                }
            }
        }
        return valid;
    }

    /**
     * validate a well
     *
     * @param well
     * @param wellName
     * @return true if validation passes
     */
    protected boolean validateAWell(final JSONObject well, final String wellName) {
        boolean retValue = true;
        retValue &= checkPortionNumber(well, wellName);
        retValue &= checkVialNumber(well, wellName);
        retValue &= validateBatchNumber(well, wellName);
        retValue &= validateSampleType(well, wellName);
        retValue &= validateAnalyteType(well, wellName);
        retValue &= validateBcrAliquotUUIDAndBarcode(well, wellName);
        retValue &= validateAnalyteShippedPortionRelation(well, wellName);
        return retValue;
    }

    /**
     * validates that analyte type is null if shipped portions are used
     *
     * @param well
     * @param wellName
     * @return true if validation passes
     */
    protected boolean validateAnalyteShippedPortionRelation(final JSONObject well, final String wellName) {
        boolean valid = true;
        if (well.has(BCR_SHIPPED_PORTION_UUID_KEY) || well.has(BCR_SHIPPED_PORTION_BARCODE_KEY)) {
            if (!(well.get(ANALYTE_TYPE_KEY) instanceof JSONNull)) {
                valid = false;
                errorMessages.add("Element '" + ANALYTE_TYPE_KEY + "' in well '" + wellName + "' is not valid. " +
                        "The well contains a shipped portion and should have a null analyte type value.");
            }
        }
        return valid;
    }

    /**
     * validates analyte type on a well
     *
     * @param well
     * @param wellName
     * @return true if validation passes
     */
    protected boolean validateAnalyteType(final JSONObject well, final String wellName) {
        boolean retValue = true;
        if (checkElementExistence(ANALYTE_TYPE_KEY, well, wellName)) {
            if (!(well.get(ANALYTE_TYPE_KEY) instanceof JSONNull)) {
                if (!checkElementType(ANALYTE_TYPE_KEY, well, wellName, String.class)) {
                    retValue = false;
                } else {
                    final String analyteType = well.getString(ANALYTE_TYPE_KEY);
                    if (!pendingUUIDDao.isValidAnalyteType(analyteType)) {
                        retValue = false;
                        errorMessages.add("Element '" + ANALYTE_TYPE_KEY + "' of value '" + analyteType +
                                "' in well '" + wellName + "' did not validate against DCC database.");
                    }
                }
            }
        } else {
            retValue = false;
        }
        return retValue;
    }

    /**
     * validates batch number on a well
     *
     * @param well
     * @param wellName
     * @return true if validation passes
     */
    protected boolean validateBatchNumber(final JSONObject well, final String wellName) {
        boolean retValue = true;
        if (checkElementExistence(BATCH_NUMBER_KEY, well, wellName)) {
            if (!checkElementType(BATCH_NUMBER_KEY, well, wellName, String.class)) {
                retValue = false;
            } else {
                final String batchNumber = well.getString(BATCH_NUMBER_KEY);
                if (!pendingUUIDDao.isValidBatchNumber(batchNumber)) {
                    retValue = false;
                    errorMessages.add("Element '" + BATCH_NUMBER_KEY + "' of value '" + batchNumber +
                            "' in well '" + wellName + "' did not validate against DCC database.");
                }
            }
        } else {
            retValue = false;
        }
        return retValue;
    }

    /**
     * validates sample type on a well
     *
     * @param well
     * @param wellName
     * @return true if validation passes
     */
    protected boolean validateSampleType(final JSONObject well, final String wellName) {
        boolean retValue = true;
        if (checkElementExistence(SAMPLE_TYPE_KEY, well, wellName)) {
            if (!checkElementType(SAMPLE_TYPE_KEY, well, wellName, String.class)) {
                retValue = false;
            } else {
                final String sampleType = well.getString(SAMPLE_TYPE_KEY);
                if (!pendingUUIDDao.isValidSampleType(sampleType)) {
                    retValue = false;
                    errorMessages.add("Element '" + SAMPLE_TYPE_KEY + "' of value '" + sampleType +
                            "' in well '" + wellName + "' did not validate against DCC database.");
                }
            }
        } else {
            retValue = false;
        }
        return retValue;
    }

    /**
     * validate portion number
     *
     * @param well
     * @param wellName
     * @return true if validation passes
     */
    protected boolean checkPortionNumber(final JSONObject well, final String wellName) {
        boolean retValue = true;
        if (checkElementExistence(PORTION_NUMBER_KEY, well, wellName)) {
            if (!checkElementType(PORTION_NUMBER_KEY, well, wellName, String.class)) {
                retValue = false;
            }
        } else {
            retValue = false;
        }
        return retValue;
    }

    /**
     * validate vial number
     *
     * @param well
     * @param wellName
     * @return true if validation passes
     */
    protected boolean checkVialNumber(final JSONObject well, final String wellName) {
        boolean retValue = true;
        if (checkElementExistence(VIAL_NUMBER_KEY, well, wellName)) {
            if (!checkElementType(VIAL_NUMBER_KEY, well, wellName, String.class)) {
                retValue = false;
            }
        } else {
            retValue = false;
        }
        return retValue;
    }

    /**
     * validate a center
     *
     * @param plateIdNumber
     * @return true if validation passes
     */
    protected boolean validateCenter(final JSONObject plateIdNumber) {
        boolean retValue = true;
        if (checkElementExistence(CENTER, plateIdNumber, null)) {
            if (!checkElementType(CENTER, plateIdNumber, null, String.class)) {
                retValue = false;
            } else {
                final String bcrCenter = plateIdNumber.getString(CENTER);
                if (!this.pendingUUIDDao.isValidCenter(bcrCenter)) {
                    retValue = false;
                    errorMessages.add("Element " + CENTER + " is not valid. The value " + bcrCenter +
                            " did not validate against a list of bcr center IDs.");
                }
            }
        } else {
            retValue = false;
        }
        return retValue;
    }

    /**
     * validate plate id
     *
     * @param plateIdNumber
     * @return true if validation passes
     */
    protected boolean validatePlateId(final JSONObject plateIdNumber) {
        boolean retValue = true;
        if (checkElementExistence(PLATE_ID, plateIdNumber, null)) {
            if (!checkElementType(PLATE_ID, plateIdNumber, null, String.class)) {
                retValue = false;
            }
        } else {
            retValue = false;
        }
        return retValue;
    }

    /**
     * validate type of element
     *
     * @param elementName
     * @param parentElement
     * @param parentElementName
     * @param type
     * @return true if validation passes
     */
    protected boolean checkElementType(final String elementName, final JSONObject parentElement,
                                       final String parentElementName, Class<?> type) {
        boolean valid = true;
        final Class<?> clazz = parentElement.get(elementName).getClass();
        if (!(clazz.isAssignableFrom(type))) {
            valid = false;
            if (clazz.isAssignableFrom(net.sf.json.JSONArray.class)) {
                //We have duplicated elements or an array
                if (parentElementName == null) {
                    errorMessages.add("Element '" + elementName + "' in the json message is of type Array, " +
                            "it may be a duplicate.");
                } else {
                    errorMessages.add("Element '" + elementName + "' in '" + parentElementName +
                            "' is of type Array, it may be a duplicate.");
                }
            } else {
                if (parentElementName == null) {
                    errorMessages.add("Element '" + elementName + "' in the json message is not valid. " +
                            "The value should be of type '" + type.getSimpleName() + "'.");
                } else {
                    errorMessages.add("Element '" + elementName + "' in '" + parentElementName + "' is not valid. " +
                            "The value should be of type '" + type.getSimpleName() + "'.");
                }
            }
        }
        return valid;
    }

    /**
     * validate element existence
     *
     * @param elementName
     * @param parentElement
     * @param parentElementName
     * @return true if validation passes
     */
    protected boolean checkElementExistence(final String elementName,
                                            final JSONObject parentElement, final String parentElementName) {
        boolean retValue = true;
        if (!parentElement.containsKey(elementName)) {
            retValue = false;
            if (parentElementName == null) {
                errorMessages.add("Element '" + elementName + "' must be present in the json message.");
            } else {
                errorMessages.add("Element '" + elementName + "' must be present in the '" + parentElementName +
                        "' parent element.");
            }
        }
        return retValue;
    }

    /**
     * validate bcr
     *
     * @param plateId
     * @return true if validation passes
     */
    protected boolean validateBcr(final JSONObject plateId) {
        boolean retValue = true;
        if (checkElementExistence(BCR_KEY, plateId, null)) {
            final String bcrValue = plateId.getString(BCR_KEY);
            if (!(BCR_VALUE_IGC.equals(bcrValue)) && !(BCR_VALUE_NCH.equals(bcrValue))) {
                retValue = false;
                errorMessages.add("Element " + BCR_KEY + " must either be " + BCR_VALUE_IGC + " or " +
                        BCR_VALUE_NCH + " but " + bcrValue + " was provided.");
            }
        } else {
            retValue = false;
        }
        return retValue;
    }

    /**
     * validate shipped Date
     *
     * @param plateId
     * @return true if validation passes
     */
    protected boolean validateShippedDate(final JSONObject plateId) {
        boolean retValue = true;
        if (checkElementExistence(SHIPPED_DATE_KEY, plateId, null)) {
            final String shipDateValue = plateId.getString(SHIPPED_DATE_KEY);
            try {
                final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_US_DASHES);
                sdf.setLenient(false);
                sdf.parse(shipDateValue);
            } catch (ParseException e) {
                retValue = false;
                errorMessages.add("Element " + SHIPPED_DATE_KEY + " must be in " + DATE_FORMAT_US_DASHES.toUpperCase()
                        + " format , instead " + shipDateValue + " found.");
            }
        } else {
            retValue = false;
        }
        return retValue;
    }

    /**
     * validates uuid format
     *
     * @param uuid
     * @param uuidKey
     * @param wellName
     * @return true if validation passes
     */
    protected boolean checkUUIDFormat(final String uuid, final String uuidKey, final String wellName) {
        boolean valid = true;
        if (!commonBarcodeAndUUIDValidator.validateUUIDFormat(uuid)) {
            valid = false;
            errorMessages.add("Element '" + uuidKey + "' in well '" + wellName +
                    "' is not valid. The value '" + uuid + "' has an invalid uuid format.");
        }
        return valid;
    }

    /**
     * validates aliquot format
     *
     * @param barcode
     * @param wellName
     * @return true if validation passes
     */
    protected boolean checkAliquotFormat(final String barcode, final String wellName) {
        boolean valid = true;
        if (!commonBarcodeAndUUIDValidator.validateAliquotBarcodeFormat(barcode)) {
            valid = false;
            errorMessages.add("Element '" + BCR_ALIQUOT_BARCODE_KEY + "' in well '" + wellName +
                    "' is not valid. The value '" + barcode + "' has an invalid aliquot barcode format.");
        }
        return valid;
    }

    /**
     * validates shipped portion format
     *
     * @param barcode
     * @param wellName
     * @return true if validation passes
     */
    protected boolean checkShippedPortionFormat(final String barcode, final String wellName) {
        boolean valid = true;
        if (!commonBarcodeAndUUIDValidator.validateShipmentPortionBarcodeFormat(barcode)) {
            valid = false;
            errorMessages.add("Element '" + BCR_SHIPPED_PORTION_BARCODE_KEY + "' in well '" + wellName +
                    "' is not valid. The value '" + barcode + "' has an invalid shipped portion barcode format.");
        }
        return valid;
    }

    /**
     * validate pre existence of uuid
     *
     * @param uuid
     * @param uuidKey
     * @param wellName
     * @return true if uuid does not already exists
     */
    protected boolean checkUUIDAlreadyPending(final String uuid, final String uuidKey, final String wellName) {
        boolean valid = true;
        if (pendingUUIDDao.alreadyPendingUUID(uuid)) {
            valid = false;
            errorMessages.add("Element '" + uuidKey + "' in well '" + wellName +
                    "' is not valid. The value '" + uuid + "' has already been marked as pending.");
        }
        return valid;
    }

    /**
     * validate pre reception of uuid
     *
     * @param uuid
     * @param uuidKey
     * @param wellName
     * @return true if uuid has not already been received
     */
    protected boolean checkUUIDAlreadyReceived(final String uuid, final String uuidKey, final String wellName) {
        boolean valid = true;
        if (pendingUUIDDao.alreadyReceivedUUID(uuid)) {
            valid = false;
            errorMessages.add("Element '" + uuidKey + "' in well '" + wellName +
                    "' is not valid. The value '" + uuid + "' has already been submitted to DCC.");
        }
        return valid;
    }

    /**
     * validate pre existence of barcode
     *
     * @param barcode
     * @param barcodeKey
     * @param wellName
     * @return true if barcode does not already exists
     */
    protected boolean checkBarcodeAlreadyPending(final String barcode, final String barcodeKey,
                                                 final String wellName) {
        boolean valid = true;
        if (pendingUUIDDao.alreadyPendingBarcode(barcode)) {
            valid = false;
            errorMessages.add("Element '" + barcodeKey + "' in well '" + wellName +
                    "' is not valid. The value '" + barcode + "' has already been marked as pending.");
        }
        return valid;
    }

    /**
     * validate pre reception of barcode
     *
     * @param barcode
     * @param barcodeKey
     * @param wellName
     * @return true if barcode has not already been received
     */
    protected boolean checkBarcodeAlreadyReceived(final String barcode, final String barcodeKey,
                                                  final String wellName) {
        boolean valid = true;
        if (pendingUUIDDao.alreadyReceivedBarcode(barcode)) {
            valid = false;
            errorMessages.add("Element '" + barcodeKey + "' in well '" + wellName +
                    "' is not valid. The value '" + barcode + "' has already been submitted to DCC.");
        }
        return valid;
    }

    /**
     * validate uuid
     *
     * @param uuidKey
     * @param well
     * @param wellName
     * @return true if validation passes
     */
    protected boolean validateUUID(final String uuidKey, final JSONObject well, final String wellName) {
        boolean valid = true;
        if (checkElementType(uuidKey, well, wellName, String.class)) {
            final String uuid = well.getString(uuidKey);
            uuidsPresent.put(wellName, uuid);
            valid &= checkUUIDFormat(uuid, uuidKey, wellName);
            valid &= (valid) ? checkUUIDAlreadyPending(uuid, uuidKey, wellName) : false;
            valid &= (valid) ? checkUUIDAlreadyReceived(uuid, uuidKey, wellName) : false;
        } else {
            valid = false;
        }
        return valid;
    }

    /**
     * validate aliquot barcode
     *
     * @param well
     * @param wellName
     * @return true if validation passes
     */
    protected boolean validateAliquotBarcode(final JSONObject well, final String wellName) {
        boolean valid = true;
        if (checkElementType(BCR_ALIQUOT_BARCODE_KEY, well, wellName, String.class)) {
            final String barcode = well.getString(BCR_ALIQUOT_BARCODE_KEY);
            barcodesPresent.put(wellName, barcode);
            valid &= checkAliquotFormat(barcode, wellName);
            valid &= (valid) ? checkBarcodeAlreadyPending(barcode, BCR_ALIQUOT_BARCODE_KEY, wellName) : false;
            valid &= (valid) ? checkBarcodeAlreadyReceived(barcode, BCR_ALIQUOT_BARCODE_KEY, wellName) : false;
        } else {
            valid = false;
        }
        return valid;
    }

    /**
     * validate shipped portion barcode
     *
     * @param well
     * @param wellName
     * @return true if validation passes
     */
    protected boolean validateShippedPortionBarcode(final JSONObject well, final String wellName) {
        boolean valid = true;
        if (checkElementType(BCR_SHIPPED_PORTION_BARCODE_KEY, well, wellName, String.class)) {
            final String barcode = well.getString(BCR_SHIPPED_PORTION_BARCODE_KEY);
            barcodesPresent.put(wellName, barcode);
            valid &= checkShippedPortionFormat(barcode, wellName);
            valid &= (valid) ? checkBarcodeAlreadyPending(barcode, BCR_SHIPPED_PORTION_BARCODE_KEY, wellName) : false;
            valid &= (valid) ? checkBarcodeAlreadyReceived(barcode, BCR_SHIPPED_PORTION_BARCODE_KEY, wellName) : false;
        } else {
            valid = false;
        }
        return valid;
    }

    /**
     * validate aliquot and uuid
     *
     * @param well
     * @param wellName
     * @return true if validation passes
     */
    protected boolean validateBcrAliquotUUIDAndBarcode(final JSONObject well, final String wellName) {
        boolean valid = true;
        if (well.has(BCR_ALIQUOT_UUID_KEY) && well.has(BCR_ALIQUOT_BARCODE_KEY)) {
            valid &= validateUUID(BCR_ALIQUOT_UUID_KEY, well, wellName);
            valid &= validateAliquotBarcode(well, wellName);
        } else if (well.has(BCR_SHIPPED_PORTION_UUID_KEY) && well.has(BCR_SHIPPED_PORTION_BARCODE_KEY)) {
            valid &= validateUUID(BCR_SHIPPED_PORTION_UUID_KEY, well, wellName);
            valid &= validateShippedPortionBarcode(well, wellName);
        } else if (!well.has(BCR_ALIQUOT_UUID_KEY) && !well.has(BCR_SHIPPED_PORTION_UUID_KEY)) {
            valid = false;
            errorMessages.add("Element '" + BCR_ALIQUOT_UUID_KEY + "' or '" + BCR_SHIPPED_PORTION_UUID_KEY +
                    "' must be present in well '" + wellName + "'.");
        } else if (!well.has(BCR_ALIQUOT_BARCODE_KEY) && !well.has(BCR_SHIPPED_PORTION_BARCODE_KEY)) {
            valid = false;
            errorMessages.add("Element '" + BCR_ALIQUOT_BARCODE_KEY + "' or '" + BCR_SHIPPED_PORTION_BARCODE_KEY +
                    "' must be present in well '" + wellName + "'.");
        } else {
            valid = false;
            errorMessages.add("Error in well '" + wellName + "'. " +
                    "If bcr_shipment_portion_uuid is used instead of bcr_aliquot_uuid, " +
                    "then bcr_shipment_portion_barcode must be used instead of bcr_aliquot_barcode or vice versa.");
        }
        return valid;
    }

    /**
     * get duplicate values of a collection of String
     *
     * @param collection
     * @return list of duplicate values
     */
    public List<String> getDuplicates(Collection<String> collection) {
        final List<String> dups = new ArrayList<String>();
        final Set<String> set = new HashSet<String>() {
            @Override
            public boolean add(String e) {
                if (contains(e)) {
                    dups.add(e);
                }
                return super.add(e);
            }
        };
        for (final String str : collection) {
            set.add(str);
        }
        return dups;
    }

    /**
     * get all keys for a value in a map
     *
     * @param map
     * @param value
     * @return list of keys
     */
    public List<String> getKeysByValue(Map<String, String> map, String value) {
        final List<String> keys = new LinkedList<String>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }

    public void setPendingUUIDDao(PendingUUIDDAO pendingUUIDDao) {
        this.pendingUUIDDao = pendingUUIDDao;
    }

    public void setCommonBarcodeAndUUIDValidator(CommonBarcodeAndUUIDValidator commonBarcodeAndUUIDValidator) {
        this.commonBarcodeAndUUIDValidator = commonBarcodeAndUUIDValidator;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public void setUuidsPresent(Map<String, String> uuidsPresent) {
        this.uuidsPresent = uuidsPresent;
    }

    public void setBarcodesPresent(Map<String, String> barcodesPresent) {
        this.barcodesPresent = barcodesPresent;
    }
}//End of Class
