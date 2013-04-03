package gov.nih.nci.ncicb.tcga.dcc.uuid.webservice;

import com.sun.jersey.api.core.InjectParam;
import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean.ValidationErrors;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean.ValidationResult;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean.ValidationResults;
import gov.nih.nci.ncicb.tcga.dcc.uuid.webservice.bean.UUIDBrowserWSQueryParamBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represent a web service to validate the existence of a uuid in the database
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
@Path("/validation")
@Scope("request")
public class ValidationWebService {
    // Logger
    protected final Log logger = LogFactory.getLog(getClass());
    @InjectParam
    private UUIDWebServiceUtil uuidWebServiceUtil;
    @InjectParam
    private UUIDService uuidService;

    @InjectParam
    private CommonBarcodeAndUUIDValidator commonBarcodeAndUUIDValidator;

    /**
     * get Metadata From uuid in JSON format
     *
     * @param UUIDs
     * @return MetadataViewWS
     */
    @GET
    @Path("/json/uuidExists/{UUIDs:.+}")
    @Produces(MediaType.APPLICATION_JSON)
    public ValidationResults validateUUIDsJSON(@PathParam("UUIDs") final String UUIDs) {
        return validateUUIDs(UUIDs, MediaType.APPLICATION_JSON);

    }


    /**
     * get Metadata From uuid in XML format
     *
     * @param UUIDs
     * @return MetadataViewWS
     */
    @GET
    @Path("/xml/uuidExists/{UUIDs:.+}")
    @Produces(MediaType.APPLICATION_XML)
    public ValidationResults validateUUIDsXML(@PathParam("UUIDs") final String UUIDs) {
        return validateUUIDs(UUIDs, MediaType.APPLICATION_XML);
    }

    /**
     * get Metadata From Barcode in JSON format
     *
     * @param barcodes
     * @return MetadataViewWS
     */
    @GET
    @Path("/json/barcodeExists/{barcodes:.+}")
    @Produces(MediaType.APPLICATION_JSON)
    public ValidationResults getMetadataFromBarcodeExistsJSON(@PathParam("barcodes") final String barcodes) {
        return validateBarcodes(barcodes, MediaType.APPLICATION_JSON);
    }


    /**
     * get Metadata From Barcode in XML format
     *
     * @param barcodes
     * @return MetadataViewWS
     */
    @GET
    @Path("/xml/barcodeExists/{barcodes:.+}")
    @Produces(MediaType.APPLICATION_XML)
    public ValidationResults getMetadataFromBarcodeExistsXML(@PathParam("barcodes") final String barcodes) {
        return validateBarcodes(barcodes, MediaType.APPLICATION_XML);
    }

    /**
     * Validate the given UUIDs and related barcodes (for each uuid there needs to be 1 barcode):
     *
     * - UUIDs are in the right format, are Aliquots UUIDs and have been received by DCC
     * - Associated barcode are in the right format (Aliquot) and are the latest barcodes for the UUIDs
     *
     * @param uuids the UUID to validate (comma delimited)
     * @param barcodes the associated barcodes to validate (comma delimited)
     * @return a {@link ValidationResults} object that contains the result of the validation in a JSON format
     */
    @GET
    @Path("/json/validateAliquotUuidsAndBarcodes/")
    @Produces(MediaType.APPLICATION_JSON)
    public ValidationResults validateUuidsAndBarcodesJson(@QueryParam("uuids") final String uuids,
                                                          @QueryParam("barcodes") final String barcodes) {
        return validateUUIDsAndBarcodes(uuids, barcodes);
    }

    /**
     * Validate the given UUIDs and related barcodes (for each uuid there needs to be 1 barcode):
     *
     * - UUIDs are in the right format, are Aliquots UUIDs and have been received by DCC
     * - Associated barcode are in the right format (Aliquot) and are the latest barcodes for the UUIDs
     *
     * @param uuids the UUID to validate (comma delimited)
     * @param barcodes the associated barcodes to validate (comma delimited)
     * @return a {@link ValidationResults} object that contains the result of the validation in an XML format
     */
    @GET
    @Path("/xml/validateAliquotUuidsAndBarcodes/")
    @Produces(MediaType.APPLICATION_XML)
    public ValidationResults validateUuidsAndBarcodesXml(@QueryParam("uuids") final String uuids,
                                                         @QueryParam("barcodes") final String barcodes) {
        return validateUUIDsAndBarcodes(uuids, barcodes);
    }

    /**
     * Validate the given UUIDs and related barcodes (for each uuid there needs to be 1 barcode):
     *
     * - UUIDs are in the right format, are Aliquots UUIDs and have been received by DCC
     * - Associated barcode are in the right format (Aliquot) and are the latest barcodes for the UUIDs
     *
     *
     * @param uuids the UUID to validate (comma delimited)
     * @param barcodes the associated barcodes to validate (comma delimited)
     * @return a {@link ValidationResults} object that contains the result of the validation in a JSON format
     */
    private ValidationResults validateUUIDsAndBarcodes(final String uuids,
                                                       final String barcodes) {

        final ValidationResults result = new ValidationResults();
        final List<ValidationResult> validationResultList = new ArrayList<ValidationResult>();
        result.setValidationResult(validationResultList);

        final List<String> uuidList = Arrays.asList(uuids.split(ConstantValues.WS_BARCODE_DELIMITER, -1));
        final List<String> barcodeList = Arrays.asList(barcodes.split(ConstantValues.WS_BARCODE_DELIMITER, -1));

        if(uuidList.size() != barcodeList.size()) {

            final String invalidValue = "Found " + uuidList.size() + " UUIDs and " + barcodeList.size() + " barcodes";
            final String errorMessage = "There must be as many barcodes as UUIDs provided";

            final ValidationResult validationResult = createValidationResultWithError(invalidValue, errorMessage, false);
            result.getValidationResult().add(validationResult);

        } else {

            for(int i=0; i < uuidList.size(); i++) {

                final String uuid = uuidList.get(i);
                final String barcode = barcodeList.get(i);

                final boolean isValidFormatUuid = commonBarcodeAndUUIDValidator.validateUUIDFormat(uuid);

                if(isValidFormatUuid) {

                    if(uuidService.uuidExists(uuid)) {

                        if(commonBarcodeAndUUIDValidator.isAliquotUUID(uuid)) {

                            if(commonBarcodeAndUUIDValidator.validateUUIDBarcodeMapping(uuid, barcode)) {

                                final ValidationResult validationResult = new ValidationResult();
                                validationResult.setExistsInDB(true);
                                result.getValidationResult().add(validationResult);

                            } else {
                                final String invalidValue = "[uuid:" + uuid + "], [barcode:" + barcode + "]";
                                final String errorMessage = new StringBuffer().append("UUID '")
                                        .append(uuid)
                                        .append("' and barcode '")
                                        .append(barcode)
                                        .append("' do not match.").toString();

                                final ValidationResult validationResult = createValidationResultWithError(invalidValue, errorMessage, true);
                                result.getValidationResult().add(validationResult);
                            }

                        } else {
                            final String invalidValue = uuid;
                            final String errorMessage = "The uuid '" + uuid + "' is not assigned to an aliquot";

                            final ValidationResult validationResult = createValidationResultWithError(invalidValue, errorMessage, true);
                            result.getValidationResult().add(validationResult);
                        }

                    } else {
                        final String invalidValue = uuid;
                        final String errorMessage = "The uuid '" + uuid + "' has not been submitted by the BCR yet, so data for it cannot be accepted";

                        final ValidationResult validationResult = createValidationResultWithError(invalidValue, errorMessage, false);
                        result.getValidationResult().add(validationResult);
                    }

                } else {
                    final String invalidValue = uuid;
                    final String errorMessage = "The uuid '" + uuid + "' has an invalid format";

                    final ValidationResult validationResult = createValidationResultWithError(invalidValue, errorMessage, false);
                    result.getValidationResult().add(validationResult);
                }
            }
        }

        return result;
    }

    /**
     * Return a {@link ValidationResult} with an error.
     *
     * @param invalidValue the invalid value
     * @param errorMessage the error message
     * @param existsInDB whether the value exists in the DB or not
     * @return a {@link ValidationResult} with an error
     */
    private ValidationResult createValidationResultWithError(final String invalidValue,
                                                             final String errorMessage,
                                                             final boolean existsInDB) {

        final ValidationErrors.ValidationError validationError = new ValidationErrors.ValidationError();
        validationError.setInvalidValue(invalidValue);
        validationError.setErrorMessage(errorMessage);

        final List<ValidationErrors.ValidationError> validationErrorList = new ArrayList<ValidationErrors.ValidationError>();
        validationErrorList.add(validationError);

        final ValidationErrors validationErrors = new ValidationErrors();
        validationErrors.setValidationErrors(validationErrorList);

        final ValidationResult validationResult = new ValidationResult();
        validationResult.setExistsInDB(existsInDB);
        validationResult.setValidationError(validationErrors);

        return validationResult;
    }

    private ValidationResults validateBarcodes(final String barcodes, final String mediaTypeString) {
        // get all barcodes
        final List<String> barcodesToValidate = Arrays.asList(barcodes.split(ConstantValues.WS_BARCODE_DELIMITER, -1));
        final List<String> barcodePassedFormatValidation = new ArrayList<String>();
        final List<ValidationResult> validatedBarcodes = new ArrayList<ValidationResult>();
        final UUIDBrowserWSQueryParamBean queryParamBean = new UUIDBrowserWSQueryParamBean();
        // validate barcodes format
        for (final String barcode : barcodesToValidate) {
            queryParamBean.setBarcode(barcode);
            try {
                uuidWebServiceUtil.validate(queryParamBean, barcode, mediaTypeString);
                barcodePassedFormatValidation.add(barcode);
            } catch (WebApplicationException e) {
                ValidationResult validationResult = new ValidationResult();
                validationResult.setValidationObject(barcode);
                validationResult.setValidationError(((ValidationErrors) e.getResponse().getEntity()));
                validatedBarcodes.add(validationResult);
            }
        }
        // get the existing barcodes from DB
        final List<String> existingBarcodes = uuidService.getExistingBarcodes(barcodePassedFormatValidation);

        // update the status of the UUIDs
        for (final String barcode : barcodePassedFormatValidation) {
            ValidationResult validationResult = new ValidationResult();
            validationResult.setValidationObject(barcode);
            validationResult.setExistsInDB((existingBarcodes.contains(barcode)) ? true : false);
            validatedBarcodes.add(validationResult);
        }
        ValidationResults validationResults = new ValidationResults();
        validationResults.setValidationResult(validatedBarcodes);

        return validationResults;
    }

    private ValidationResults validateUUIDs(final String UUIDs, final String mediaTypeString) {
        // get all uuids
        final List<String> UUIDsToValidate = Arrays.asList(UUIDs.split(ConstantValues.WS_BATCH_DELIMITER, -1));
        final List<String> UUIDsPassedFormatValidation = new ArrayList<String>();
        final List<ValidationResult> validatedUUIDs = new ArrayList<ValidationResult>();

        final UUIDBrowserWSQueryParamBean queryParamBean = new UUIDBrowserWSQueryParamBean();
        // validate uuids format
        for (final String UUID : UUIDsToValidate) {
            queryParamBean.setUuid(UUID);
            try {
                uuidWebServiceUtil.validate(queryParamBean, UUID, mediaTypeString);
                UUIDsPassedFormatValidation.add(UUID);
            } catch (WebApplicationException e) {
                ValidationResult validationResult = new ValidationResult();
                validationResult.setValidationObject(UUID);
                validationResult.setValidationError(((ValidationErrors) e.getResponse().getEntity()));
                validatedUUIDs.add(validationResult);
            }
        }
        // get the existing UUIDs from DB
        final List<String> existingUUIDs = uuidService.getUUIDsExistInDB(UUIDsPassedFormatValidation);

        // update the status of the UUIDs
        for (final String UUID : UUIDsPassedFormatValidation) {
            ValidationResult validationResult = new ValidationResult();
            validationResult.setValidationObject(UUID);

            final boolean uuidExistsInDB = StringUtil.containsIgnoreCase(existingUUIDs, UUID);
            validationResult.setExistsInDB(uuidExistsInDB);
            validatedUUIDs.add(validationResult);
        }
        ValidationResults validationResults = new ValidationResults();
        validationResults.setValidationResult(validatedUUIDs);

        return validationResults;
    }

    public UUIDWebServiceUtil getUuidWebServiceUtil() {
        return uuidWebServiceUtil;
    }

    public void setUuidWebServiceUtil(UUIDWebServiceUtil uuidWebServiceUtil) {
        this.uuidWebServiceUtil = uuidWebServiceUtil;
    }

    public UUIDService getUuidService() {
        return uuidService;
    }

    public void setUuidService(UUIDService uuidService) {
        this.uuidService = uuidService;
    }

    public CommonBarcodeAndUUIDValidator getCommonBarcodeAndUUIDValidator() {
        return commonBarcodeAndUUIDValidator;
    }

    public void setCommonBarcodeAndUUIDValidator(final CommonBarcodeAndUUIDValidator commonBarcodeAndUUIDValidator) {
        this.commonBarcodeAndUUIDValidator = commonBarcodeAndUUIDValidator;
    }
}
