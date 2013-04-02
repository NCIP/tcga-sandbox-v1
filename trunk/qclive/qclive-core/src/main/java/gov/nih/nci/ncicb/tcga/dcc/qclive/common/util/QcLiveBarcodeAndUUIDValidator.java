/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;

import java.util.List;
import java.util.Map;

/**
 * Provide barcode validation
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface QcLiveBarcodeAndUUIDValidator extends CommonBarcodeAndUUIDValidator {

    /**
     * Validate a barcode and put any error message in the context
     *
     * @param input the barcode to validate
     * @param context the <code>QcContext</code>
     * @param fileName the name of the file the barcode is coming from
     * @return <code>true</code> if the barcode is valid, <code>false</code> otherwise
     */
    public Boolean validate(final String input, final QcContext context, final String fileName);

    /**
     * Validates a barcode.
     *
     * @param input the barcode
     * @param context the qc context
     * @param fileName the name of the file containing the barcode
     * @param mustExist if true, will fail if the barcode is not already in the database
     * @return true if the barcode is valid, false otherwise
     */
    public Boolean validate(final String input, final QcContext context, final String fileName, boolean mustExist);

    /**
     * Validates multiple barcodes.
     *
     * @param input list of barcodes or list of uuids
     * @param context the qc context
     * @param fileName the name of the file containing the barcode or uuid
     * @param mustExist if true, will fail if the barcode is not already in the database
     * @return true if the barcode is valid, false otherwise
     */

    public Boolean batchValidate(final List<String> input, final QcContext context, final String fileName, final boolean mustExist);

    /**
     * Validates multiple barcodes and returns the results in a {@link Map} of barcode->validity.
     *
     * @param input list of barcodes
     * @param context the qc context
     * @param fileName the name of the file containing the barcode
     * @param mustExist if true, will fail if the barcode is not already in the database
     * @return the results in a {@link Map} of barcode->validity
     */
    public Map<String, Boolean> batchValidateReportIndividualResults(final List<String> input,
                                                                     final QcContext context,
                                                                     final String fileName,
                                                                     final boolean mustExist);

    /**
     * Validates multiple UUIDs and returns the results in a {@link Map} of barcode->validity.
     *
     * @param uuids the list of UUIDs to validate
     * @param qcContext the qclive context
     * @param fileName the name of the file containing the UUIDs
     * @param mustExist if <code>true</code>, will fail if the UUID is not already in the database
     * @return the results in a {@link Map} of barcode->validity
     */
    public Map<String, Boolean> batchValidateUUIDsReportIndividualResults(final List<String> uuids,
                                                                          final QcContext qcContext,
                                                                          final String fileName,
                                                                          final boolean mustExist);
    /**
     * Validates a barcode of any given type.
     *
     * @param input the barcode
     * @param context the qc context
     * @param fileName the name of the file containing the barcode
     * @param mustExist if true, will fail if the barcode is not already in the database
     * @return true if the barcode is valid, false otherwise
     */
    public Boolean validateAnyBarcode(final String input, final QcContext context, final String fileName, boolean mustExist, final String barcodeType);


    /**
     * Validate a uuid and put any error message in the context
     *
     * @param input    the uuid to validate
     * @param context  the <code>QcContext</code>
     * @param fileName the name of the file the barcode is coming from
     * @return <code>true</code> if the uuid is valid, <code>false</code> otherwise
     */
    public Boolean validateUuid(final String input, final QcContext context, final String fileName);


    /**
     * Validate a uuid and put any error message in the context
     *
     * @param input     the uuid to validate
     * @param context   the <code>QcContext</code>
     * @param fileName  the name of the file the uuid is coming from
     * @param mustExist uuid has to exist in the database
     * @return <code>true</code> if the uuid is valid, <code>false</code> otherwise
     */
    public Boolean validateUuid(final String input, final QcContext context, final String fileName, final boolean mustExist);

    /**
     * Validate a uuid or barcode and put any error message in the context
     *
     * @param input     the uuid or barcode to validate
     * @param context   the <code>QcContext</code>
     * @param fileName  the name of the file the uuid is coming from
     * @param mustExist uuid has to exist in the database
     * @return <code>true</code> if the uuid is valid, <code>false</code> otherwise
     */
    public Boolean validateBarcodeOrUuid(final String input, final QcContext context, final String fileName, final boolean mustExist);

    /**
     * validate the given string is uuid or not
     * @param input
     * @return <code>true</code> if input is uuid, <code>false</code> otherwise
     */
    public Boolean isUUID(final String input);

    /**
     * Batch validation of SampleUUID/SampleTCGABarcode pairs
     *
     * @param sampleUuidAndSampleTcgaBarcodePairs a list of SampleUUID/SampleTCGABarcode pairs
     * @param qcContext the qcLive context in which eventual errors are stored
     * @return <code>true</code> if all given pairs are valid, <code>false</code> otherwise
     */
    public boolean batchValidateSampleUuidAndSampleTcgaBarcode(final List<String[]> sampleUuidAndSampleTcgaBarcodePairs, final QcContext qcContext);
}
