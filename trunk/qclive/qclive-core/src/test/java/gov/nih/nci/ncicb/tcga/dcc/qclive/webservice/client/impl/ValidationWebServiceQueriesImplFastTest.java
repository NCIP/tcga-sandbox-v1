package gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.client.impl;

import gov.nih.nci.ncicb.tcga.dcc.common.webservice.HttpStatusCode;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean.ValidationResult;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean.ValidationResults;
import gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.util.RestfulWebserviceClient;
import gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.util.WebserviceInput;
import gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.util.WebserviceOutput;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * ValidationWebServiceQueriesImpl unit tests
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class ValidationWebServiceQueriesImplFastTest {

    private Mockery mockery;
    private ValidationWebServiceQueriesImpl validationWebServiceQueriesImpl;
    private RestfulWebserviceClient mockRestfulWebserviceClient;
    private int webServiceBatchSize = 1;
    private String baseValidationWebServiceURL = "http://test";
    private static final String validateUuidsAndBarcodesJsonURL = "http://validateUuidsAndBarcodesJsonURL";

    @Before
    public void setUp() {

        mockery = new Mockery();
        mockRestfulWebserviceClient = mockery.mock(RestfulWebserviceClient.class);

        validationWebServiceQueriesImpl = new ValidationWebServiceQueriesImpl();
        validationWebServiceQueriesImpl.setRestfulWebserviceClient(mockRestfulWebserviceClient);
        validationWebServiceQueriesImpl.setBaseValidationWebServiceURL(baseValidationWebServiceURL);
        validationWebServiceQueriesImpl.setWebServiceBatchSize(webServiceBatchSize);
        validationWebServiceQueriesImpl.setValidateUuidsAndBarcodesJsonURL(validateUuidsAndBarcodesJsonURL);
    }

    @Test
    public void testvalidateUUIDsNull() throws WebServiceException {

        final ValidationResults result = validationWebServiceQueriesImpl.validateUUIDs(null);
        assertNull(result);
    }

    @Test
    public void testvalidateUUIDsThrowsWebServiceException() {

        final ValidationResults result;
        final List<String> uuids = Arrays.asList(new String[]{"uuid1"});
        final int httpStatusNotOk = HttpStatusCode.INTERNAL_SERVER_ERROR;
        final WebserviceOutput expectedWebserviceOutput = new WebserviceOutput(httpStatusNotOk);

        mockery.checking(new Expectations() {{
            one(mockRestfulWebserviceClient).executeGet(with(any(WebserviceInput.class)));
            will(returnValue(expectedWebserviceOutput));
        }});

        try {
            result = validationWebServiceQueriesImpl.validateUUIDs(uuids);
            fail("WebServiceException was not thrown");
        } catch (final WebServiceException e) {
            assertEquals("Error occurred while validating uuid(s). Status code : " + httpStatusNotOk, e.getMessage());
        }
    }

    @Test
    public void testvalidateUUIDsOneBatch() throws WebServiceException {

        final String uuid1 = "uuid1";
        final boolean uuid1ExistsInDB = true;

        final List<String> uuids = Arrays.asList(uuid1);

        final WebserviceOutput expectedWebserviceOutput = makeValidationResults(uuid1, uuid1ExistsInDB);

        mockery.checking(new Expectations() {{
            one(mockRestfulWebserviceClient).executeGet(with(expectedURI("http://test/uuid1")));
            will(returnValue(expectedWebserviceOutput));
        }});

        final ValidationResults result = validationWebServiceQueriesImpl.validateUUIDs(uuids);

        assertNotNull(result);
        assertNotNull(result.getValidationResult());
        assertEquals(1, result.getValidationResult().size());

        final ValidationResult firstValidationResult = result.getValidationResult().get(0);
        assertNotNull(firstValidationResult);

        assertEquals(firstValidationResult.getValidationObject(), uuid1);
        assertTrue(firstValidationResult.existsInDB());
    }

    @Test
    public void testValidateUUIDBadWithSpace() throws WebServiceException {
        // make sure spaces in "UUID" are encoded to avoid 400 errors from webservice
        mockery.checking(new Expectations() {{
            one(mockRestfulWebserviceClient).executeGet(with(expectedURI("http://test/Promega+Ref+Control+Not+UUID")));
            will(returnValue(makeValidationResults("Promega+Ref+Control+Not+UUID", false)));
        }});
        validationWebServiceQueriesImpl.validateUUIDs(Arrays.asList("Promega Ref Control Not UUID"));
    }

    private static Matcher<WebserviceInput> expectedURI(final String uri) {
        return new TypeSafeMatcher<WebserviceInput>() {

            public boolean matchesSafely(final WebserviceInput input) {
                assertEquals(uri, input.getUri().toString());
                return true;
            }

            public void describeTo(final Description description) {
                description.appendText("asserts input uri is ").appendText(uri);
            }
        };
    }

    @Test
    public void testvalidateUUIDsTwoBatches() throws WebServiceException {

        final String uuid1 = "uuid1";
        final boolean uuid1ExistsInDB = true;

        final String uuid2 = "uuid2";
        final boolean uuid2ExistsInDB = false;

        final List<String> uuids = new LinkedList<String>();
        uuids.add(uuid1);
        uuids.add(uuid2);

        final WebserviceOutput expectedWebserviceOutputForFirstBatch = makeValidationResults(uuid1, uuid1ExistsInDB);
        final WebserviceOutput expectedWebserviceOutputForSecondBatch = makeValidationResults(uuid2, uuid2ExistsInDB);

        mockery.checking(new Expectations() {{
            one(mockRestfulWebserviceClient).executeGet(with(validateWebserviceInput(uuid1)));
            will(returnValue(expectedWebserviceOutputForFirstBatch));

            one(mockRestfulWebserviceClient).executeGet(with(validateWebserviceInput(uuid2)));
            will(returnValue(expectedWebserviceOutputForSecondBatch));
        }});

        final ValidationResults result = validationWebServiceQueriesImpl.validateUUIDs(uuids);

        assertNotNull(result);
        assertNotNull(result.getValidationResult());
        assertEquals(2, result.getValidationResult().size());

        final ValidationResult firstValidationResult = result.getValidationResult().get(0);
        assertNotNull(firstValidationResult);

        final ValidationResult secondValidationResult = result.getValidationResult().get(1);
        assertNotNull(secondValidationResult);

        final String uuidResult1 = firstValidationResult.getValidationObject();
        final String uuidResult2 = secondValidationResult.getValidationObject();

        // Testing values regardless of order in which the result put them
        String firstResultUuid = null;
        if(uuid1.equals(uuidResult1)) {
            assertTrue(firstValidationResult.existsInDB());
            firstResultUuid = uuid1;
        } else if(uuid2.equals(uuidResult1)) {
            assertFalse(firstValidationResult.existsInDB());
            firstResultUuid = uuid2;
        } else {
            fail("Unknown UUID: " + uuidResult1);
        }

        if(firstResultUuid.equals(uuidResult2)) {
            fail("Result 1 and 2 are for the same UUID: " + firstResultUuid);
        } else if(uuid1.equals(uuidResult2)) {
            assertTrue(secondValidationResult.existsInDB());
        } else if(uuid2.equals(uuidResult2)) {
            assertFalse(secondValidationResult.existsInDB());
        } else {
            fail("Unknown UUID: " + uuidResult2);
        }
    }

    @Test
    public void testvalidateUUIDsWithDuplicates() throws WebServiceException {

        final String uuid1 = "uuid1";
        final boolean uuid1ExistsInDB = true;

        final List<String> uuids = Arrays.asList(new String[]{uuid1, uuid1});
        final WebserviceOutput expectedWebserviceOutput = makeValidationResults(uuid1, uuid1ExistsInDB);

        mockery.checking(new Expectations() {{
            one(mockRestfulWebserviceClient).executeGet(with(validateWebserviceInput(uuid1)));
            will(returnValue(expectedWebserviceOutput));
        }});

        final ValidationResults result = validationWebServiceQueriesImpl.validateUUIDs(uuids);

        assertNotNull(result);
        assertNotNull(result.getValidationResult());
        assertEquals(1, result.getValidationResult().size());

        final ValidationResult firstValidationResult = result.getValidationResult().get(0);
        assertNotNull(firstValidationResult);
        assertEquals(firstValidationResult.getValidationObject(), uuid1);
        assertTrue(firstValidationResult.existsInDB());
    }

    @Test
    public void testBatchValidateSampleUuidAndSampleTcgaBarcodeWhenNull() throws WebServiceException {

        final ValidationResults result = validationWebServiceQueriesImpl.batchValidateSampleUuidAndSampleTcgaBarcode(null);
        assertNull(result);
    }

    @Test
    public void testBatchValidateSampleUuidAndSampleTcgaBarcodeWhenEmpty() throws WebServiceException {

        final ValidationResults result = validationWebServiceQueriesImpl.batchValidateSampleUuidAndSampleTcgaBarcode(new ArrayList<String[]>());
        assertNull(result);
    }

    @Test
    public void testBatchValidateSampleUuidAndSampleTcgaBarcodeWhenLargerThanWebServiceBatchSize() throws WebServiceException {

        final String uuid1 = "uuid1";
        final String uuid2 = "uuid2";
        final String barcode1 = "barcode1";
        final String barcode2 = "barcode2";
        final String[] pair1 = {uuid1, barcode1};
        final String[] pair2 = {uuid2, barcode2};

        final List<String[]> sampleUuidAndSampleTcgaBarcodePairs = new ArrayList<String[]>();
        sampleUuidAndSampleTcgaBarcodePairs.add(pair1);
        sampleUuidAndSampleTcgaBarcodePairs.add(pair2);

        final WebserviceOutput expectedWebserviceOutput1 = makeValidationResults(uuid1, true);
        final WebserviceOutput expectedWebserviceOutput2 = makeValidationResults(uuid2, true);

        mockery.checking(new Expectations() {{
            one(mockRestfulWebserviceClient).executeGet(with(validateUuidBarcodeWebserviceInput(uuid1, barcode1)));
            will(returnValue(expectedWebserviceOutput1));

            one(mockRestfulWebserviceClient).executeGet(with(validateUuidBarcodeWebserviceInput(uuid2, barcode2)));
            will(returnValue(expectedWebserviceOutput2));
        }});

        final ValidationResults result = validationWebServiceQueriesImpl.batchValidateSampleUuidAndSampleTcgaBarcode(sampleUuidAndSampleTcgaBarcodePairs);
        assertNotNull(result);
        assertNotNull(result.getValidationResult());
        assertEquals(2, result.getValidationResult().size());
    }

    @Test
    public void testBatchValidateSampleUuidAndSampleTcgaBarcodeWhenWebServiceException() {

        final String uuid1 = "uuid1";
        final String barcode1 = "barcode1";
        final String[] pair1 = {uuid1, barcode1};

        final List<String[]> sampleUuidAndSampleTcgaBarcodePairs = new ArrayList<String[]>();
        sampleUuidAndSampleTcgaBarcodePairs.add(pair1);

        final WebserviceOutput expectedWebserviceOutput1 = makeValidationResults(uuid1, true);
        expectedWebserviceOutput1.setStatus(HttpStatusCode.PRECONDITION_FAILED);

        mockery.checking(new Expectations() {{
            one(mockRestfulWebserviceClient).executeGet(with(validateUuidBarcodeWebserviceInput(uuid1, barcode1)));
            will(returnValue(expectedWebserviceOutput1));
        }});

        try {
            validationWebServiceQueriesImpl.batchValidateSampleUuidAndSampleTcgaBarcode(sampleUuidAndSampleTcgaBarcodePairs);
            fail("WebServiceException was not thrown");

        } catch (final WebServiceException e) {
            assertEquals("Error occurred while validating uuid(s) and barcode(s). Status code : 412", e.getMessage());
        }
    }

    @Test
    public void testBatchValidateNeedsEncoding() throws WebServiceException {
        final String uuid1 = "not a good uuid";
        final String barcode1 = "hi";
        final String[] pair1 = {uuid1, barcode1};

        final List<String[]> sampleUuidAndSampleTcgaBarcodePairs = new ArrayList<String[]>();
        sampleUuidAndSampleTcgaBarcodePairs.add(pair1);

        mockery.checking(new Expectations() {{
            one(mockRestfulWebserviceClient).executeGet(with(validateUuidBarcodeWebserviceInput("not+a+good+uuid", "hi")));
            will(returnValue(makeValidationResults("not+a+good+uuid", false)));
        }});

        validationWebServiceQueriesImpl.batchValidateSampleUuidAndSampleTcgaBarcode(sampleUuidAndSampleTcgaBarcodePairs);
    }

    /**
     * {@link TypeSafeMatcher} for validating {@link WebserviceInput} for batchValidateSampleUuidAndSampleTcgaBarcode()
     *
     * @param uuid the uuid string that must be contained in the {@link WebserviceInput} url
     * @param barcode the barcode string that must be contained in the {@link WebserviceInput} url
     * @return a {@link TypeSafeMatcher} for validating {@link WebserviceInput}
     */
    private static TypeSafeMatcher<WebserviceInput> validateUuidBarcodeWebserviceInput(final String uuid,
                                                                                       final String barcode) {

        return new TypeSafeMatcher<WebserviceInput>() {

            @Override
            public boolean matchesSafely(final WebserviceInput webserviceInput) {
                return webserviceInput.getUri().toString().equals(validateUuidsAndBarcodesJsonURL + "?uuids=" + uuid + "&barcodes=" + barcode);
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText(" matches UUID ").appendText(uuid).appendText(" and barcode ").appendText(barcode);
            }
        };
    }

    /**
     * {@link TypeSafeMatcher} for validating {@link WebserviceInput}
     *
     * @param uuid the uuid string that must be contained in the {@link WebserviceInput} url
     * @return a {@link TypeSafeMatcher} for validating {@link WebserviceInput}
     */
    private static TypeSafeMatcher<WebserviceInput> validateWebserviceInput(final String uuid) {

        return new TypeSafeMatcher<WebserviceInput>() {

            @Override
            public boolean matchesSafely(final WebserviceInput webserviceInput) {
                return webserviceInput.getUri().toString().contains(uuid);
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("matches UUID (").appendText(uuid).appendText(")");
            }
        };
    }

    /**
     * Makes a {@link WebserviceOutput} that has its <code>validationResults</code> set with the given UUID and it's existence in the DB.
     *
     * @param uuid the uuid to add in the <code>validationResults</code>
     * @param uuidExistsInDB to add in the <code>validationResults</code>
     * @return a {@link WebserviceOutput} that has its <code>validationResults</code> set with the given UUID and it's existence in the DB
     */
    private WebserviceOutput makeValidationResults(final String uuid, final Boolean uuidExistsInDB) {

        final ValidationResult validationResult = new ValidationResult();
        validationResult.setValidationObject(uuid);
        validationResult.setExistsInDB(uuidExistsInDB);

        final List<ValidationResult> validationResultList = new ArrayList<ValidationResult>();
        validationResultList.add(validationResult);

        final ValidationResults expectedValidationResults = new ValidationResults();
        expectedValidationResults.setValidationResult(validationResultList);

        final int httpStatusOk = HttpStatusCode.OK;
        final WebserviceOutput expectedWebserviceOutput = new WebserviceOutput(httpStatusOk);
        expectedWebserviceOutput.setValidationResults(expectedValidationResults);

        return expectedWebserviceOutput;
    }
}
