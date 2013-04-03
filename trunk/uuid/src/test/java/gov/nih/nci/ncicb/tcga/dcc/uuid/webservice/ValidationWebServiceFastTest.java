package gov.nih.nci.ncicb.tcga.dcc.uuid.webservice;

import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.HttpStatusCode;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean.ValidationErrors;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean.ValidationResult;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean.ValidationResults;
import gov.nih.nci.ncicb.tcga.dcc.uuid.webservice.bean.UUIDBrowserWSQueryParamBean;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
/**
 * Class to test Validation Web service
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class ValidationWebServiceFastTest {

    private final Mockery context = new JUnit4Mockery();
    private ValidationWebService validationWebService;
    private UUIDWebServiceUtil uuidWebServiceUtil;
    private UUIDService uuidService;
    private CommonBarcodeAndUUIDValidator mockCommonBarcodeAndUUIDValidator;

    private UriInfo uriInfo;
    private UriBuilder mockUri = UriBuilder.fromUri("http://myserverisgreat.com/uuid/uuidws/validation/json/");

    @Before
    public void before() throws Exception {
        validationWebService = new ValidationWebService();
        uuidWebServiceUtil = context.mock( UUIDWebServiceUtil.class);
        uuidService = context.mock(UUIDService.class);
        mockCommonBarcodeAndUUIDValidator = context.mock(CommonBarcodeAndUUIDValidator.class);
        validationWebService.setUuidService(uuidService);
        validationWebService.setUuidWebServiceUtil(uuidWebServiceUtil);
        validationWebService.setCommonBarcodeAndUUIDValidator(mockCommonBarcodeAndUUIDValidator);
    }

    @Test
    public void validateUUIDsInDB(){
        final String UUIDsToValidate = "uuid1,uuid2";
        final List<String> UUIDs = Arrays.asList(new String[]{"uuid1","uuid2"});
        context.checking(new Expectations() {{
            allowing(uuidWebServiceUtil).validate(with(validateQueryBean(UUIDs)),
                    with(validateUUID(UUIDs)),
                    with(MediaType.APPLICATION_JSON));
            allowing(uuidService).getUUIDsExistInDB(with(UUIDs));
            will(returnValue(UUIDs));

        }});

        final ValidationResults validationResults = validationWebService.validateUUIDsJSON(UUIDsToValidate);
        assertEquals(2, validationResults.getValidationResult().size());
        assertTrue(validationResults.getValidationResult().get(0).existsInDB());
        assertTrue(validationResults.getValidationResult().get(1).existsInDB());
    }

    @Test
    public void validateUUIDsNotInDB(){
        final String UUIDsToValidate = "uuid1,uuid2,uuid3";
        final List<String> UUIDs = Arrays.asList(new String[]{"uuid1","uuid2","uuid3"});
        final List<String> UUIDsInDB = Arrays.asList(new String[]{"uuid1","uuid2"});
        context.checking(new Expectations() {{
            allowing(uuidWebServiceUtil).validate(with(validateQueryBean(UUIDs)),
                    with(validateUUID(UUIDs)),
                    with(MediaType.APPLICATION_JSON));
            allowing(uuidService).getUUIDsExistInDB(with(UUIDs));
            will(returnValue(UUIDsInDB));

        }});

        final ValidationResults validationResults = validationWebService.validateUUIDsJSON(UUIDsToValidate);
        assertEquals(3, validationResults.getValidationResult().size());
        assertTrue(validationResults.getValidationResult().get(0).existsInDB());
        assertTrue(validationResults.getValidationResult().get(1).existsInDB());
        assertFalse(validationResults.getValidationResult().get(2).existsInDB());
    }


    @Test
    public void validateInvalidUUIDs(){
        final String UUIDsToValidate = "uuid1,Invalid_uuid";
        final List<String> UUIDs = Arrays.asList(new String[]{"uuid1","Invalid_uuid"});
        final List<String> UUIDsInDB = Arrays.asList(new String[]{"uuid1"});

        context.checking(new Expectations() {{
            allowing(uuidWebServiceUtil).validate(with(validateQueryBean(UUIDs)),
                    with(validateUUID(UUIDs)),
                    with(MediaType.APPLICATION_JSON));
            allowing(uuidService).getUUIDsExistInDB(with(UUIDsInDB));
            will(returnValue(UUIDsInDB));

        }});

        final ValidationResults validationResults = validationWebService.validateUUIDsJSON(UUIDsToValidate);
        assertEquals(2, validationResults.getValidationResult().size());
        assertFalse(validationResults.getValidationResult().get(0).existsInDB());
        assertEquals(1, validationResults.getValidationResult().get(0).getValidationError().getValidationError().size());
        assertTrue(validationResults.getValidationResult().get(1).existsInDB());

    }

    @Test
    public void testValidateUuidsAndBarcodesJsonWhenNotEnoughUuids() {

        final String uuids = "a";
        final String barcodes = "b,c";

        final String expectedErrorValue = "Found 1 UUIDs and 2 barcodes";
        final String expectedErrorMessage = "There must be as many barcodes as UUIDs provided";
        final Boolean expectedExistsInDB = false;

        final ValidationResults validationResults = validationWebService.validateUuidsAndBarcodesJson(uuids,  barcodes);
        checkValidationResults(validationResults, expectedErrorValue, expectedErrorMessage, expectedExistsInDB);
    }

    @Test
    public void testValidateUuidsAndBarcodesJsonWhenNotEnoughBarcodes() {

        final String uuids = "a,b";
        final String barcodes = "c";

        final String expectedErrorValue = "Found 2 UUIDs and 1 barcodes";
        final String expectedErrorMessage = "There must be as many barcodes as UUIDs provided";
        final Boolean expectedExistsInDB = false;

        final ValidationResults validationResults = validationWebService.validateUuidsAndBarcodesJson(uuids,  barcodes);
        checkValidationResults(validationResults, expectedErrorValue, expectedErrorMessage, expectedExistsInDB);
    }

    @Test
    public void testValidateUuidsAndBarcodesJsonWhenUuidWrongFormat() {

        final String uuids = "a";
        final String barcodes = "b";

        final String expectedErrorValue = "a";
        final String expectedErrorMessage = "The uuid 'a' has an invalid format";
        final Boolean expectedExistsInDB = false;

        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("a");
            will(returnValue(false));
        }});

        final ValidationResults validationResults = validationWebService.validateUuidsAndBarcodesJson(uuids,  barcodes);
        checkValidationResults(validationResults, expectedErrorValue, expectedErrorMessage, expectedExistsInDB);
    }

    @Test
    public void testValidateUuidsAndBarcodesJsonWhenUuidNotReceivedByDcc() {

        final String uuids = "a";
        final String barcodes = "b";

        final String expectedErrorValue = "a";
        final String expectedErrorMessage = "The uuid 'a' has not been submitted by the BCR yet, so data for it cannot be accepted";
        final Boolean expectedExistsInDB = false;

        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("a");
            will(returnValue(true));
            one(uuidService).uuidExists("a");
            will(returnValue(false));
        }});

        final ValidationResults validationResults = validationWebService.validateUuidsAndBarcodesJson(uuids,  barcodes);
        checkValidationResults(validationResults, expectedErrorValue, expectedErrorMessage, expectedExistsInDB);
    }

    @Test
    public void testValidateUuidsAndBarcodesJsonWhenUuidNotForAliquot() {

        final String uuids = "a";
        final String barcodes = "b";

        final String expectedErrorValue = "a";
        final String expectedErrorMessage = "The uuid 'a' is not assigned to an aliquot";
        final Boolean expectedExistsInDB = true;

        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("a");
            will(returnValue(true));
            one(uuidService).uuidExists("a");
            will(returnValue(true));
            one(mockCommonBarcodeAndUUIDValidator).isAliquotUUID("a");
            will(returnValue(false));
        }});

        final ValidationResults validationResults = validationWebService.validateUuidsAndBarcodesJson(uuids,  barcodes);
        checkValidationResults(validationResults, expectedErrorValue, expectedErrorMessage, expectedExistsInDB);
    }

    @Test
    public void testValidateUuidsAndBarcodesJsonWhenUuidAndBarcodeDoNotMatch() {

        final String uuids = "a";
        final String barcodes = "b";

        final String expectedErrorValue = "[uuid:a], [barcode:b]";
        final String expectedErrorMessage = "UUID 'a' and barcode 'b' do not match.";
        final Boolean expectedExistsInDB = true;

        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("a");
            will(returnValue(true));
            one(uuidService).uuidExists("a");
            will(returnValue(true));
            one(mockCommonBarcodeAndUUIDValidator).isAliquotUUID("a");
            will(returnValue(true));
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDBarcodeMapping("a", "b");
            will(returnValue(false));
        }});

        final ValidationResults validationResults = validationWebService.validateUuidsAndBarcodesJson(uuids,  barcodes);
        checkValidationResults(validationResults, expectedErrorValue, expectedErrorMessage, expectedExistsInDB);
    }

    @Test
    public void testValidateUuidsAndBarcodesJsonWhenValid() {

        final String uuids = "a";
        final String barcodes = "b";

        final Boolean expectedExistsInDB = true;

        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("a");
            will(returnValue(true));
            one(uuidService).uuidExists("a");
            will(returnValue(true));
            one(mockCommonBarcodeAndUUIDValidator).isAliquotUUID("a");
            will(returnValue(true));
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDBarcodeMapping("a", "b");
            will(returnValue(true));
        }});

        final ValidationResults validationResults = validationWebService.validateUuidsAndBarcodesJson(uuids,  barcodes);
        checkValidationResults(validationResults, null, null, expectedExistsInDB);
    }

    /**
     * Check assertions against the given {@link ValidationResults}.
     *
     * @param validationResults the {@link ValidationResults} to check
     * @param expectedErrorValue the expected error value
     * @param expectedErrorMessage the expected error message
     * @param expectedExistsInDB whether the value is expected in the DB or not
     */
    private void checkValidationResults(final ValidationResults validationResults,
                                        final String expectedErrorValue,
                                        final String expectedErrorMessage,
                                        final Boolean expectedExistsInDB) {
        assertNotNull(validationResults);
        final List<ValidationResult> validationResultList = validationResults.getValidationResult();
        assertNotNull(validationResultList);
        assertEquals(1, validationResultList.size());

        final ValidationResult validationResult = validationResultList.get(0);
        assertNotNull(validationResult);
        assertEquals(expectedExistsInDB, validationResult.getExistsInDB());

        if(expectedErrorValue != null && expectedErrorMessage != null) {

            final ValidationErrors validationErrors = validationResult.getValidationError();
            assertNotNull(validationErrors);
            final List<ValidationErrors.ValidationError> validationErrorList = validationErrors.getValidationError();
            assertNotNull(validationErrorList);
            assertEquals(1, validationErrorList.size());

            final ValidationErrors.ValidationError validationError = validationErrorList.get(0);
            assertNotNull(validationError);

            assertEquals(expectedErrorValue, validationError.getInvalidValue());
            assertEquals(expectedErrorMessage, validationError.getErrorMessage());
        }
    }

    private static TypeSafeMatcher<UUIDBrowserWSQueryParamBean> validateQueryBean(final List<String> UUIDs) {
        return new TypeSafeMatcher<UUIDBrowserWSQueryParamBean>() {

            @Override
            public boolean matchesSafely(final UUIDBrowserWSQueryParamBean queryParamBean) {
                if(queryParamBean.getUuid().equals("Invalid_uuid")){
                    throw getWebApplicationException();

                }
                else{
                    return UUIDs.contains(queryParamBean.getUuid());
                }
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("matches UUID");
            }
        };
    }

    private static TypeSafeMatcher<String> validateUUID(final List<String> UUIDs) {
        return new TypeSafeMatcher<String>() {

            @Override
            public boolean matchesSafely(final String UUID) {
                return UUIDs.contains(UUID);
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("matches UUID");
            }
        };
    }

    private static WebApplicationException getWebApplicationException(){
        final ValidationErrors validationErrors = new ValidationErrors();
        final List<ValidationErrors.ValidationError> validationErrorList = new ArrayList<ValidationErrors.ValidationError>();
        final ValidationErrors.ValidationError validationError = new ValidationErrors.ValidationError();
        validationError.setErrorMessage("Imvalid uuid");
        validationErrorList.add(validationError);

        validationErrors.setValidationErrors(validationErrorList);

        final Response response = Response.status(HttpStatusCode.UNPROCESSABLE_ENTITY)
        .entity(validationErrors)
        .type(MediaType.APPLICATION_JSON)
        .build();
        return new WebApplicationException(response);
    }
}
