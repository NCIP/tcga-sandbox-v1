package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Barcode;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDDAO;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants;

import java.util.Arrays;
import java.util.Date;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;

/**
 * Test for BarcodeUuidResolverImpl
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class BarcodeUuidResolverImplFastTest {
    private final Mockery context = new JUnit4Mockery();

    private BarcodeUuidResolverImpl barcodeUuidResolver;
    private UUIDDAO mockUuidDao;
    private UUIDService mockUuidService;
    private CommonBarcodeAndUUIDValidator mockBarcodeAndUUIDValidator;
    private Center center;
    private Tumor disease;

    @Before
    public void setUp() throws Exception {
        mockUuidDao = context.mock(UUIDDAO.class);
        mockUuidService = context.mock(UUIDService.class);
        mockBarcodeAndUUIDValidator = context.mock(CommonBarcodeAndUUIDValidator.class);

        barcodeUuidResolver = new BarcodeUuidResolverImpl();
        barcodeUuidResolver.setUuidDAO(mockUuidDao);
        barcodeUuidResolver.setUuidService(mockUuidService);
        barcodeUuidResolver.setBarcodeAndUUIDValidator(mockBarcodeAndUUIDValidator);

        center = new Center();
        center.setCenterId(123);

        disease = new Tumor();
    }

    private void checkBarcode(final Barcode barcodeDetail, final String barcode, final String uuid) {
        assertEquals(barcodeDetail.getBarcode(), barcode);
        assertEquals(barcodeDetail.getUuid(), uuid);
    }

    @Test
    public void testAlreadyRegistered() throws UUIDException {
        // barcode and uuid already associated with each other, no conflict
        context.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidator).validateUUIDFormat("uuid");
            will(returnValue(true));

            one(mockUuidDao).uuidExists("uuid");
            will(returnValue(true));

            one(mockUuidService).getUUIDForBarcode("barcode");
            will(returnValue("uuid"));

            one(mockUuidService).getLatestBarcodeForUUID("uuid");
            will(returnValue("barcode"));
        }});
        final Barcode barcodeDetail = barcodeUuidResolver.resolveBarcodeAndUuid("barcode", "uuid", disease, center, true);
        checkBarcode(barcodeDetail, "barcode", "uuid");

    }

    @Test
    public void testNew() throws UUIDException {
        // barcode and uuid not associated with anything, uuid not in db
        context.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidator).validateUUIDFormat("uuid");
            will(returnValue(true));

            one(mockUuidDao).uuidExists("uuid");
            will(returnValue(false));

            one(mockUuidService).getUUIDForBarcode("barcode");
            will(returnValue(null));

            one(mockUuidService).getLatestBarcodeForUUID("uuid");
            will(returnValue(null));

            one(mockUuidService).registerUUID("uuid", 123);
            one(mockUuidService).addBarcode(with(expectedBarcode("barcode", "uuid", disease)));
        }});
        final Barcode barcodeDetail = barcodeUuidResolver.resolveBarcodeAndUuid("barcode", "uuid", disease, center, true);
        checkBarcode(barcodeDetail, "barcode", "uuid");
    }

    @Test
    public void testExistingUuidNotAssociated() throws UUIDException {
        // the uuid is in the db but there is no association between the uuid and barcode
        context.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidator).validateUUIDFormat("uuid");
            will(returnValue(true));

            one(mockUuidDao).uuidExists("uuid");
            will(returnValue(true));

            one(mockUuidService).getUUIDForBarcode("barcode");
            will(returnValue(null));

            one(mockUuidService).getLatestBarcodeForUUID("uuid");
            will(returnValue(null));

            one(mockUuidService).addBarcode(with(expectedBarcode("barcode", "uuid", disease)));

        }});
        final Barcode barcodeDetail = barcodeUuidResolver.resolveBarcodeAndUuid("barcode", "uuid", disease, center, true);
        checkBarcode(barcodeDetail, "barcode", "uuid");
    }

    @Test
    public void testUuidNotGivenOkayToGenerate() throws UUIDException {
        // no uuid was passed in, barcode is not associated with a uuid
        context.checking(new Expectations() {{
            one(mockUuidService).getUUIDForBarcode("barcode");
            will(returnValue(null));

            one(mockUuidService).generateUUID(123, 1, UUIDConstants.GenerationMethod.API, "DCC");
            will(returnValue(Arrays.asList(new UUIDDetail("new-uuid", new Date(), UUIDConstants.GenerationMethod.API, center, "test"))));

            one(mockUuidService).addBarcode(with(expectedBarcode("barcode", "new-uuid", disease)));
        }});
        final Barcode barcodeDetail = barcodeUuidResolver.resolveBarcodeAndUuid("barcode", null, disease, center, true);
        checkBarcode(barcodeDetail, "barcode", "new-uuid");
    }

    @Test (expected = UUIDException.class)
    public void testUuidNotGivenDoNotGenerate() throws UUIDException {
        // no uuid was passed in, barcode is not associated with a uuid
        context.checking(new Expectations() {{
            one(mockUuidService).getUUIDForBarcode("barcode");
            will(returnValue(null));
        }});
        barcodeUuidResolver.resolveBarcodeAndUuid("barcode", null, disease, center, false);
    }


    @Test
    public void testUuidNotGivenButInDb() throws UUIDException {
        // no uuid was passed in, but it's in the database, associated with the barcode
        context.checking(new Expectations() {{
            one(mockUuidService).getUUIDForBarcode("barcode");
            will(returnValue("some-uuid"));
        }});
        final Barcode barcodeDetail = barcodeUuidResolver.resolveBarcodeAndUuid("barcode", null, disease, center, true);
        checkBarcode(barcodeDetail, "barcode", "some-uuid");
    }

    @Test (expected = UUIDException.class)
    public void testUuidConflict() throws UUIDException {
        // uuid is already associated with another barcode
        context.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidator).validateUUIDFormat("uuid");
            will(returnValue(true));

            one(mockUuidDao).uuidExists("uuid");
            will(returnValue(true));

            one(mockUuidService).getLatestBarcodeForUUID("uuid");
            will(returnValue("muppets"));

            one(mockUuidService).getUUIDForBarcode("barcode");
            will(returnValue(null));
        }});
        barcodeUuidResolver.resolveBarcodeAndUuid("barcode", "uuid", disease, center, true);
    }

    @Test (expected = UUIDException.class)
    public void testBarcodeConflict() throws UUIDException {
        // barcode is already associated with another uuid
        context.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidator).validateUUIDFormat("uuid");
            will(returnValue(true));

            one(mockUuidDao).uuidExists("uuid");
            will(returnValue(true));

            one(mockUuidService).getLatestBarcodeForUUID("uuid");
            will(returnValue(null));

            one(mockUuidService).getUUIDForBarcode("barcode");
            will(returnValue("different-uuid"));
        }});
        barcodeUuidResolver.resolveBarcodeAndUuid("barcode", "uuid", disease, center, true);
    }

    @Test (expected = UUIDException.class)
    public void testBarcodeNewUuidConflict() throws UUIDException {
        // barcode already associated with another uuid, and given uuid is new
        context.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidator).validateUUIDFormat("uuid");
            will(returnValue(true));

            one(mockUuidDao).uuidExists("uuid");
            will(returnValue(false));

            one(mockUuidService).getLatestBarcodeForUUID("uuid");
            will(returnValue(null));

            one(mockUuidService).getUUIDForBarcode("barcode");
            will(returnValue("different-uuid"));
        }});
        barcodeUuidResolver.resolveBarcodeAndUuid("barcode", "uuid", disease, center, true);
    }

    @Test
    public void testUuidEmptyStringNoUuid() throws UUIDException {
        // when the UUID param is an empty string, should be treated the same as a null
        context.checking(new Expectations() {{
            one(mockUuidService).getUUIDForBarcode("barcode");
            will(returnValue(null));

            one(mockUuidService).generateUUID(123, 1, UUIDConstants.GenerationMethod.API, "DCC");
            will(returnValue(Arrays.asList(new UUIDDetail("a-new-uuid", new Date(), UUIDConstants.GenerationMethod.API, center, "test"))));

            one(mockUuidService).addBarcode(with(expectedBarcode("barcode", "a-new-uuid", disease)));
        }});
        final Barcode barcodeDetail = barcodeUuidResolver.resolveBarcodeAndUuid("barcode", "", disease, center, true);
        checkBarcode(barcodeDetail, "barcode", "a-new-uuid");
    }

    @Test
    public void testUuidEmptyStringDbHasUuid() throws UUIDException {
        // UUID is empty string, barcode is associated with a UUID in the db
        context.checking(new Expectations() {{
            one(mockUuidService).getUUIDForBarcode("barcode");
            will(returnValue("existing-uuid"));
        }});
        final Barcode barcodeDetail = barcodeUuidResolver.resolveBarcodeAndUuid("barcode", "", disease, center, true);
        checkBarcode(barcodeDetail, "barcode", "existing-uuid");
    }

    @Test (expected = UUIDException.class)
    public void testUuidInvalid() throws UUIDException {
        context.checking(new Expectations() {{
            one(mockUuidService).getUUIDForBarcode("barcode");
            will(returnValue(null));

            one(mockBarcodeAndUUIDValidator).validateUUIDFormat("blah");
            will(returnValue(false));
        }});
        barcodeUuidResolver.resolveBarcodeAndUuid("barcode", "blah", disease, center, true);
    }

    @Test
    public void testUppercaseUuid() throws UUIDException {
        context.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidator).validateUUIDFormat("12341234-abcd-1234-abcd-efefefefefef");
            will(returnValue(true));

            one(mockUuidDao).uuidExists("12341234-abcd-1234-abcd-efefefefefef");
            will(returnValue(true));

            one(mockUuidService).getUUIDForBarcode("barcode");
            will(returnValue("12341234-abcd-1234-abcd-efefefefefef"));

            one(mockUuidService).getLatestBarcodeForUUID("12341234-abcd-1234-abcd-efefefefefef");
            will(returnValue("barcode"));

        }});
        // pass in uppercase uuid, code should lowercase it...
        final Barcode barcode = barcodeUuidResolver.resolveBarcodeAndUuid("barcode", "12341234-ABCD-1234-ABCD-EFEFEFEFEFEF", disease, center, true);
        checkBarcode(barcode, "barcode", "12341234-abcd-1234-abcd-efefefefefef");

    }

    private static Matcher<Barcode> expectedBarcode(final String barcode, final String uuid, final Tumor disease) {
        return new TypeSafeMatcher<Barcode>() {

            @Override
            public boolean matchesSafely(final Barcode barcodeDetail) {
                return barcodeDetail.getBarcode().equals(barcode) && barcodeDetail.getUuid().equals(uuid) &&
                        barcodeDetail.getDisease().equals(disease);
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("checks barcode detail object");
            }
        };
    }
}
