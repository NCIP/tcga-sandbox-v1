package gov.nih.nci.ncicb.tcga.dcc.uuid.webservice;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Barcode;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.SearchCriteria;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UuidBarcodeMapping;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.HttpStatusCode;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.WebServiceUtilFastTest;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test class for MappingWebService
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class MappingWebServiceFastTest {

    private final Mockery context = new JUnit4Mockery();
    private MappingWebService mappingWebService;
    private UUIDService mockUuidService;

    /**
     * Wildcard that can be used in barcode mapping web service
     */
    private static final String WILDCARD = "*";

    @Before
    public void setUp() {
        mappingWebService = new MappingWebService();
        mockUuidService = context.mock(UUIDService.class);
        mappingWebService.setUuidService(mockUuidService);
    }

    @Test
    public void testGetBarcodeMappingJSON() {

        final String barcode = "TCGA-02-0028";
        checkGetBarcodeMappingJSON(barcode);
    }

    @Test
    public void testGetBarcodeMappingJSONCaseInsensitive() {

        final String barcode = "tcga-02-0028";
        checkGetBarcodeMappingJSON(barcode);
    }

    @Test
    public void testGetBarcodeMappingJSONWhenBarcodeNotValid() {

        final String barcode = "Squirrel";

        try {
            mappingWebService.getBarcodeMappingJSON(barcode);
            fail("WebApplicationException was not thrown.");

        } catch (final WebApplicationException e) {

            final String expectedErrorMessage = "barcode " + barcode + " is not valid";
            WebServiceUtilFastTest.checkValidationErrorsResponse(e.getResponse(), HttpStatusCode.OK,
                    barcode, expectedErrorMessage, MediaType.APPLICATION_JSON);
        }
    }

    @Test
    public void testGetBarcodeMappingXML() {

        final String barcode = "TCGA-02-0028";
        checkGetBarcodeMappingXML(barcode);
    }

    @Test
    public void testGetBarcodeMappingXMLCaseInsensitive() {

        final String barcode = "tcga-02-0028";
        checkGetBarcodeMappingXML(barcode);
    }

    @Test
    public void testGetBarcodeMappingXMLWhenBarcodeNotValid() {

        final String barcode = "Squirrel";

        try {
            mappingWebService.getBarcodeMappingXML(barcode);
            fail("WebApplicationException was not thrown.");

        } catch (final WebApplicationException e) {

            final String expectedErrorMessage = "barcode " + barcode + " is not valid";
            WebServiceUtilFastTest.checkValidationErrorsResponse(e.getResponse(), HttpStatusCode.OK,
                    barcode, expectedErrorMessage, MediaType.APPLICATION_XML);
        }
    }

    @Test
    public void testGetBarcodeMappingXMLNoMatch() {

        final String barcode = "TCGA-02-0028";

        context.checking(new Expectations() {{
            one(mockUuidService).getUUIDForBarcode(barcode);
            will(returnValue(null));
        }});

        try {
            mappingWebService.getBarcodeMappingXML(barcode);
            fail("WebApplicationException was not thrown.");

        } catch (final WebApplicationException e) {

            final String expectedErrorMessage = "barcode " + barcode + " not found";
            WebServiceUtilFastTest.checkValidationErrorsResponse(e.getResponse(), HttpStatusCode.OK,
                    barcode, expectedErrorMessage, MediaType.APPLICATION_XML);
        }
    }

    @Test
    public void testGetBarcodeMappingJSONNoMatch() {

        final String barcode = "TCGA-02-0028";

        context.checking(new Expectations() {{
            one(mockUuidService).getUUIDForBarcode(barcode);
            will(returnValue(null));
        }});

        try {
            mappingWebService.getBarcodeMappingJSON(barcode);
            fail("WebApplicationException was not thrown.");

        } catch (final WebApplicationException e) {

            final String expectedErrorMessage = "barcode " + barcode + " not found";
            WebServiceUtilFastTest.checkValidationErrorsResponse(e.getResponse(), HttpStatusCode.OK,
                    barcode, expectedErrorMessage, MediaType.APPLICATION_JSON);
        }
    }

    @Test
    public void testGetBarcodeMappingJSONWithWildcard() {

        final String barcodePrefix = "TCGA-02-0028";
        final boolean emptyList = false;
        checkGetBarcodeMappingJSONWithWildcard(barcodePrefix, emptyList);
    }

    @Test
    public void testGetBarcodeMappingJSONWithWildcardNoResult() {

        final String barcodePrefix = "TCGA-02-0028";

        try {
            final boolean emptyList = true;
            checkGetBarcodeMappingJSONWithWildcard(barcodePrefix, emptyList);
            fail("WebApplicationException was not thrown.");

        } catch (final WebApplicationException e) {

            final String expectedErrorMessage = "No barcode found with prefix " + barcodePrefix;
            WebServiceUtilFastTest.checkValidationErrorsResponse(e.getResponse(), HttpStatusCode.OK,
                    barcodePrefix, expectedErrorMessage, MediaType.APPLICATION_JSON);
        }
    }

    @Test
    public void testGetBarcodeMappingJSONWithWildcardCaseInsensitive() {

        final String barcodePrefix = "tcga-02-0028";
        final boolean emptyList = false;
        checkGetBarcodeMappingJSONWithWildcard(barcodePrefix, emptyList);
    }

    @Test
    public void testGetBarcodeMappingJSONWithWildcardIncompleteBarcode() {

        final String barcodePrefix = "TCGA-*";
        final boolean emptyList = false;
        checkGetBarcodeMappingJSONWithWildcard(barcodePrefix, emptyList);
    }

    @Test
    public void testGetBarcodeMappingXMLWithWildcard() {

        final String barcodePrefix = "TCGA-02-0028";
        final boolean emptyList = false;
        checkGetBarcodeMappingXMLWithWildcard(barcodePrefix, emptyList);
    }

    @Test
    public void testGetBarcodeMappingXMLWithWildcardNoResult() {

        final String barcodePrefix = "TCGA-02-0028";
        try {
            final boolean emptyList = true;
            checkGetBarcodeMappingXMLWithWildcard(barcodePrefix, emptyList);
            fail("WebApplicationException was not thrown.");

        } catch (final WebApplicationException e) {

            final String expectedErrorMessage = "No barcode found with prefix " + barcodePrefix;
            WebServiceUtilFastTest.checkValidationErrorsResponse(e.getResponse(), HttpStatusCode.OK,
                    barcodePrefix, expectedErrorMessage, MediaType.APPLICATION_XML);
        }
    }

    @Test
    public void testGetBarcodeMappingXMLWithWildcardCaseInsensitive() {

        final String barcodePrefix = "tcga-02-0028";
        final boolean emptyList = false;
        checkGetBarcodeMappingXMLWithWildcard(barcodePrefix, emptyList);
    }

    @Test
    public void testGetBarcodeMappingXMLWithWildcardIncompleteBarcode() {

        final String barcodePrefix = "TCGA-*";
        final boolean emptyList = false;
        checkGetBarcodeMappingXMLWithWildcard(barcodePrefix, emptyList);
    }

    /**
     * Search for the given barcode prefix with a wildcard and check assertions (JSON search result)
     *
     * @param barcodePrefix the barcode prefix
     * @param emptyList     if <code>true</code> then the barcode list returned will be empty
     */
    private void checkGetBarcodeMappingJSONWithWildcard(final String barcodePrefix, final boolean emptyList) {

        final String uuid = "1-2-3";
        final boolean exactMatch = false;

        setupForBarcodeSearchWithWildcard(barcodePrefix, uuid, exactMatch, emptyList);
        final List<UuidBarcodeMapping> mappings = mappingWebService.getBarcodeMappingJSON(barcodePrefix + WILDCARD);
        checkGetBarcodeMapping(mappings, barcodePrefix, uuid, false);
    }

    /**
     * Search for the given barcode prefix with a wildcard and check assertions (XML search result)
     *
     * @param barcodePrefix the barcode prefix
     * @param emptyList     if <code>true</code> then the barcode list returned will be empty
     */
    private void checkGetBarcodeMappingXMLWithWildcard(final String barcodePrefix, final boolean emptyList) {

        final String uuid = "1-2-3";
        final boolean exactMatch = false;

        setupForBarcodeSearchWithWildcard(barcodePrefix, uuid, exactMatch, emptyList);
        final List<UuidBarcodeMapping> mappings = mappingWebService.getBarcodeMappingXML(barcodePrefix + WILDCARD);
        checkGetBarcodeMapping(mappings, barcodePrefix, uuid, false);
    }

    /**
     * Set up the search with a wildcard
     *
     * @param barcodePrefix the barcode prefix
     * @param uuid          the uuid
     * @param exactMatch    if <code>true</code> then the barcode prefix gets appended with a non blank string
     * @param emptyList     if <code>true</code> then the barcode list returned will be empty
     */
    private void setupForBarcodeSearchWithWildcard(final String barcodePrefix, final String uuid, final boolean exactMatch, final boolean emptyList) {

        context.checking(new Expectations() {{
            one(mockUuidService).getBarcodesStartingWith(barcodePrefix);
            will(returnValue(makeBarcodes(barcodePrefix, uuid, exactMatch, emptyList)));
        }});
    }

    /**
     * Create a list of one <code>Barcode</code> with the given barcode prefix and uuid.
     * If <code>exactMatch</code> if <code>true</code> then the barcode prefix gets appended with a non blank string
     *
     * @param barcodePrefix the barcode prefix
     * @param uuid          the uuid
     * @param exactMatch    if <code>true</code> then the barcode prefix gets appended with a non blank string
     * @param emptyList     if <code>true</code> then the lisst returned will be empty
     * @return a list of one <code>Barcode</code> with the given barcode prefix and uuid
     */
    private Object makeBarcodes(final String barcodePrefix, final String uuid, final boolean exactMatch, final boolean emptyList) {

        final List<Barcode> result = new ArrayList<Barcode>();

        if (!emptyList) {

            final Barcode barcode = new Barcode();
            String barcodeAsString = barcodePrefix;

            if (!exactMatch) {
                barcodeAsString += "-non-blank";
            }

            barcode.setBarcode(barcodeAsString);
            barcode.setUuid(uuid);

            result.add(barcode);
        }

        return result;
    }

    private void setupForBarcodeSearch(final String barcode, final String uuid) {
        context.checking(new Expectations() {{
            one(mockUuidService).getUUIDForBarcode(barcode);
            will(returnValue(uuid));
        }});
    }

    /**
     * Check assertions against the given list of <code>UuidBarcodeMapping</code>
     *
     * @param mappings                the list of <code>UuidBarcodeMapping</code> to check
     * @param expectedBarcodeAsString the expected barcode, as string
     * @param expectedUuidAsString    the expected UUID, as string
     * @param exactMatch              if <code>true</code> the actual barcode is expected to match exactly, otherwise, it must start by the given barcode
     */
    private void checkGetBarcodeMapping(final List<UuidBarcodeMapping> mappings,
                                        final String expectedBarcodeAsString,
                                        final String expectedUuidAsString,
                                        final boolean exactMatch) {
        assertNotNull(mappings);
        assertEquals(1, mappings.size());

        final UuidBarcodeMapping mapping = mappings.get(0);
        assertNotNull(mapping);
        assertEquals(expectedUuidAsString, mapping.getUuid());

        if (exactMatch) {
            assertEquals(expectedBarcodeAsString, mapping.getBarcode());
        } else {
            assertTrue(mapping.getBarcode().startsWith(expectedBarcodeAsString));
        }
    }

    @Test
    public void testGetUuidMappingJSONBad() {

        final String uuid = "uuid-1";

        try {
            mappingWebService.getUuidMappingJSON(uuid);
            fail("WebApplicationException was not thrown.");
        } catch (final WebApplicationException e) {

            final String expectedErrorMessage = "uuid " + uuid + " is not valid";
            WebServiceUtilFastTest.checkValidationErrorsResponse(e.getResponse(), HttpStatusCode.OK,
                    uuid, expectedErrorMessage, MediaType.APPLICATION_JSON);
        }
    }

    @Test(expected = WebApplicationException.class)
    public void testGetUuidMappingXMLBad() {
        final UuidBarcodeMapping mapping = mappingWebService.getUuidMappingXML("uuid-2");
        assertEquals("barcode-2", mapping.getBarcode());
        assertEquals("uuid-2", mapping.getUuid());
    }

    @Test
    public void testGetUuidMappingJSONGood() {
        setupForUuidSearch("barcode-1", "ab260c7a-0eea-49dc-a1b2-12406698c245");
        final UuidBarcodeMapping mapping = mappingWebService.
                getUuidMappingJSON("ab260c7a-0eea-49dc-a1b2-12406698c245");
        assertEquals("barcode-1", mapping.getBarcode());
        assertEquals("ab260c7a-0eea-49dc-a1b2-12406698c245", mapping.getUuid());
    }

    @Test
    public void testGetUuidMappingXMLGood() {
        setupForUuidSearch("barcode-2", "bb260c7a-0eea-49dc-a1b2-12406698c245");
        final UuidBarcodeMapping mapping = mappingWebService.
                getUuidMappingXML("bb260c7a-0eea-49dc-a1b2-12406698c245");
        assertEquals("barcode-2", mapping.getBarcode());
        assertEquals("bb260c7a-0eea-49dc-a1b2-12406698c245", mapping.getUuid());
    }

    @Test
    public void testGetUuidMappingJSONNoMatch() throws UUIDException {

        final String uuid = "ab260c7a-0eea-49dc-a1b2-12406698c245";

        context.checking(new Expectations() {{
            one(mockUuidService).getLatestBarcodeForUUID(uuid);
            will(returnValue(null));
            one(mockUuidService).getUUIDDetails(uuid);
            will(throwException(new UUIDException("no such uuid")));
        }});

        try {
            mappingWebService.getUuidMappingJSON(uuid);
            fail("WebApplicationException was not thrown.");

        } catch (final WebApplicationException e) {

            final String expectedErrorMessage = "no such UUID exists in the system";
            WebServiceUtilFastTest.checkValidationErrorsResponse(e.getResponse(), HttpStatusCode.OK,
                    uuid, expectedErrorMessage, MediaType.APPLICATION_JSON);
        }
    }

    @Test
    public void testGetUuidMappingNoBarcode() throws UUIDException {
        // uuid exists but there is no barcode associated
        context.checking(new Expectations() {{
            one(mockUuidService).getLatestBarcodeForUUID("ab260c7a-0eea-49dc-a1b2-12406698c245");
            will(returnValue(null));
            one(mockUuidService).getUUIDDetails("ab260c7a-0eea-49dc-a1b2-12406698c245");
            will(returnValue(new UUIDDetail()));
        }});
        final UuidBarcodeMapping mapping = mappingWebService.
                getUuidMappingXML("ab260c7a-0eea-49dc-a1b2-12406698c245");
        assertEquals("ab260c7a-0eea-49dc-a1b2-12406698c245", mapping.getUuid());
        assertEquals("", mapping.getBarcode());
    }

    private void setupForUuidSearch(final String barcode, final String uuid) {
        context.checking(new Expectations() {{
            one(mockUuidService).getLatestBarcodeForUUID(uuid);
            will(returnValue(barcode));
        }});
    }

    private void setupForGetCenterUuids() {
        final List<UUIDDetail> searchResults = new ArrayList<UUIDDetail>();
        final UUIDDetail uuidDetail1 = new UUIDDetail();
        uuidDetail1.setUuid("uuid1");
        uuidDetail1.setLatestBarcode("barcode1");
        searchResults.add(uuidDetail1);
        final UUIDDetail uuidDetail2 = new UUIDDetail();
        uuidDetail2.setUuid("uuid2");
        uuidDetail2.setLatestBarcode("barcode2");
        searchResults.add(uuidDetail2);

        final Center testCenter = new Center();
        testCenter.setCenterId(456);
        testCenter.setCenterName("testCenter.org");

        context.checking(new Expectations() {{
            one(mockUuidService).getCenterByNameAndType("testCenter.org", "BCR");
            will(returnValue(testCenter));

            one(mockUuidService).searchUUIDs(with(
                    new TypeSafeMatcher<SearchCriteria>() {
                        public boolean matchesSafely(final SearchCriteria searchCriteria) {
                            return searchCriteria.getCenterId() == 456;
                        }

                        public void describeTo(final Description description) {
                            description.appendText("search criteria with center id = 456");
                        }
                    }
            ));
            will(returnValue(searchResults));
        }});
    }

    @Test
    public void testGetCenterUuidsXml() {
        setupForGetCenterUuids();
        final List<UuidBarcodeMapping> mappings = mappingWebService.getUuidMappingsForCenterXml("testCenter.org");
        testMappingsForCenter(mappings);
    }

    @Test
    public void testGetCenterUuidsJson() {
        setupForGetCenterUuids();
        final List<UuidBarcodeMapping> mappings = mappingWebService.getUuidMappingsForCenterXml("testCenter.org");
        testMappingsForCenter(mappings);
    }

    @Test
    public void testGetCenterUuidsXmlBadCenter() {

        context.checking(new Expectations() {{
            one(mockUuidService).getCenterByNameAndType("oops", "BCR");
            will(returnValue(null));
        }});

        final String centerName = "oops";

        try {
            mappingWebService.getUuidMappingsForCenterXml(centerName);
            fail("WebApplicationException was not thrown.");

        } catch (final WebApplicationException e) {
            final String expectedErrorMessage = centerName + " is not a valid BCR center name.";
            WebServiceUtilFastTest.checkValidationErrorsResponse(e.getResponse(), HttpStatusCode.OK,
                    centerName, expectedErrorMessage, MediaType.APPLICATION_XML);
        }
    }

    private void testMappingsForCenter(final List<UuidBarcodeMapping> mappings) {
        assertEquals(2, mappings.size());
        assertEquals("uuid1", mappings.get(0).getUuid());
        assertEquals("uuid2", mappings.get(1).getUuid());

        assertEquals("barcode1", mappings.get(0).getBarcode());
        assertEquals("barcode2", mappings.get(1).getBarcode());
    }

    /**
     * Search for the given barcode mapping in JSON format and check assertions
     *
     * @param barcode the barcode
     */
    private void checkGetBarcodeMappingJSON(final String barcode) {

        final String uuid = "1-2-3";

        setupForBarcodeSearch(barcode, uuid);
        final List<UuidBarcodeMapping> mappings = mappingWebService.getBarcodeMappingJSON(barcode);

        checkGetBarcodeMapping(mappings, barcode, uuid, true);
    }

    /**
     * Search for the given barcode mapping in XML format and check assertions
     *
     * @param barcode the barcode
     */
    private void checkGetBarcodeMappingXML(final String barcode) {

        final String uuid = "1-2-3";

        setupForBarcodeSearch(barcode, uuid);
        final List<UuidBarcodeMapping> mappings = mappingWebService.getBarcodeMappingXML(barcode);

        checkGetBarcodeMapping(mappings, barcode, uuid, true);
    }
}
