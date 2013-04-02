/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.MetaDataBean;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;

import java.util.List;
import java.util.Map;

/**
 * An implementation of QcLiveBarcodeAndUUIDValidator that does not do any validation requiring DB access
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class QcLiveBarcodeAndUUIDValidatorRemoteImpl extends QcLiveBarcodeAndUUIDValidatorImpl {

    @Override
    public String validateBarcodeFormatAndCodes(final String input,
                                                final String fileName,
                                                final String expectedBarcodeType) {

        return validateBarcodeFormat(input, fileName, expectedBarcodeType);
    }

    @Override
    public boolean validateUUIDMetadata(final MetaDataBean metadata) {
        return true;
    }

    @Override
    public boolean isAliquotUUID(final String uuid) {
        return true;
    }

    @Override
    public MetaDataBean getMetadata(final String uuid) throws CommonBarcodeAndUUIDValidatorException {
        return null;
    }

    @Override
    public boolean isMatchingDiseaseForUUID(String uuid, String diseaseAbbreviation) {
        return true;
    }

    @Override
    public Boolean validateUuid(final String input,
                                final QcContext context,
                                final String fileName,
                                final boolean mustExist) {

        return validateUuid(input, context, fileName);
    }

    @Override
    public Boolean validateUuid(final String input,
                                final QcContext context,
                                final String fileName) {

        final Boolean result = validateUUIDFormat(input);

        if(!result) {
            final String errorMessage = new StringBuilder("The uuid '").append(input).append("' in file ").append(fileName).
                append(" has an invalid format").toString();
            context.addError(gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat.format(MessagePropertyType.UUID_VALIDATION_ERROR,
                                            input, errorMessage));
        }

        return result;
    }

    @Override
    public Boolean validateAnyBarcode(final String input,
                                      final QcContext context,
                                      final String fileName,
                                      final boolean mustExist,
                                      final String barcodeType) {
        return super.validateAnyBarcode(input, context, fileName, false, barcodeType);
    }

    @Override
    public Boolean validate(final String input,
                            final QcContext context,
                            final String fileName,
                            final boolean mustExist) {
        return super.validate(input, context, fileName, false);
    }

    @Override
    public Map<String, Boolean> batchValidateReportIndividualResults(final List<String> input,
                                                                     final QcContext context,
                                                                     final String fileName,
                                                                     final boolean mustExist) {

        return super.batchValidateReportIndividualResults(input, context, fileName, false);
    }

    @Override
    public Map<String, Boolean> batchValidateUUIDsReportIndividualResults(final List<String> uuids,
                                                                          final QcContext qcContext,
                                                                          final String fileName,
                                                                          final boolean mustExist) {

        return super.batchValidateUUIDsReportIndividualResults(uuids, qcContext, fileName, false);
    }

    public Boolean validateBarcodeOrUuid(final String input,
                                         final QcContext context,
                                         final String fileName,
                                         final boolean mustExist) {

        return super.validateBarcodeOrUuid(input, context, fileName, false);
    }

    @Override
    public boolean batchValidateSampleUuidAndSampleTcgaBarcode(final List<String[]> sampleUuidAndSampleTcgaBarcodePairs,
                                                               final QcContext qcContext) {
        boolean result = true;

        if(sampleUuidAndSampleTcgaBarcodePairs != null) {

            final String filename = (qcContext.getFile() != null) ? qcContext.getFile().getName() : "";

            for(final String[] sampleUuidAndSampleTcgaBarcodePair : sampleUuidAndSampleTcgaBarcodePairs) {

                if(sampleUuidAndSampleTcgaBarcodePair != null && sampleUuidAndSampleTcgaBarcodePair.length == 2) {

                    final String uuid = sampleUuidAndSampleTcgaBarcodePair[0];
                    final String barcode = sampleUuidAndSampleTcgaBarcodePair[1];

                    result &= validateUuid(uuid, qcContext, filename);
                    result &= validate(barcode, qcContext, filename);
                }
            }
        }

        return  result;
    }

    @Override
    public boolean validateUUIDBarcodeMapping(final String uuid, final String barcode) {

        boolean result = validateUUIDFormat(uuid);
        result &= validateAliquotBarcodeFormat(barcode);

        return result;
    }
}
