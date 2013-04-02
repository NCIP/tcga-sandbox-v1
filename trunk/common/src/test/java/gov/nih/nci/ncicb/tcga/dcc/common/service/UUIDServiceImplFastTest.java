/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.service;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Barcode;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Duration;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.SearchCriteria;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UuidBarcodeMapping;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDDAO;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailSender;
import gov.nih.nci.ncicb.tcga.dcc.common.util.EmailManager;
import gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants;
import gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.GenerationMethod;
import junit.framework.Assert;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


/**
 * Test class fo the UUID Service layer
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */
@RunWith(JMock.class)
public class UUIDServiceImplFastTest {

    private UUIDServiceImpl uuidService;
    private final Mockery mocker = new JUnit4Mockery();
    private final UUIDDAO uuidDAO = mocker.mock(UUIDDAO.class);
    private final CenterQueries centerQueries = mocker.mock(CenterQueries.class);
    private final MailSender mailSender = mocker.mock(MailSender.class);


    private final static int CENTER_ID = 1;
    private final static int NUMBER_OF_UUID = 5;
    private final static int NUMBER_OF_ROWS_UPDATED = 5;

    @Before
    public void setUp() throws Exception {
        uuidService = new UUIDServiceImpl();

        //use reflection to access the private field
        Field daoServiceField = uuidService.getClass().getDeclaredField("uuidDAO");
        daoServiceField.setAccessible(true);
        daoServiceField.set(uuidService, uuidDAO);
        uuidService.setCenterQueries(centerQueries);
        EmailManager emailManager = new EmailManager(mailSender);

        Field emailManagerField = uuidService.getClass().getDeclaredField("emailManager");
        emailManagerField.setAccessible(true);
        emailManagerField.set(uuidService, emailManager);
    }

    @Test
    public void testGenerateUUID() throws UUIDException {
        mockDAO(NUMBER_OF_UUID);
        mailShouldSend();
        List<UUIDDetail> uuidList = uuidService.generateUUID(CENTER_ID, NUMBER_OF_UUID, GenerationMethod.Web, "master_user");
        assertNotNull(uuidList);
        assertEquals(NUMBER_OF_UUID, uuidList.size());
    }

    @Test
    public void testGenerateUUIDFromAPI() throws UUIDException {
        mockDAO(NUMBER_OF_UUID);
        mailShouldNotSend();
        final boolean[] emailSent = new boolean[1];
        emailSent[0] = false;
        EmailManager emailManagerNotCalled = new EmailManager() {
            @Override
            public void sendNewUUIDListToCenter(final String mailTo, final List<UUIDDetail> details) {
                emailSent[0] = true;
            }
        };
        uuidService.setEmailManager(emailManagerNotCalled);
        uuidService.generateUUID(CENTER_ID, NUMBER_OF_UUID, GenerationMethod.API, "test");
        assertFalse(emailSent[0]);
    }

    @Test
    public void testUploadUUID() throws UUIDException {
        List<String> uuidList = new ArrayList<String>();
        uuidList.add("mammoth");
        uuidList.add("tiger");
        mockDAO(2);
        mailShouldSend();
        List<UUIDDetail> retList = uuidService.uploadUUID(CENTER_ID, uuidList, "");
        assertNotNull(retList);
        assertEquals(2, retList.size());
    }

    @Test
    public void testGetCenters() {

        final List<Center> centersMockList = new ArrayList<Center>();
        centersMockList.add(new Center());
        centersMockList.add(new Center());

        mocker.checking(new Expectations() {{
            oneOf(centerQueries).getCenterList();
            will(returnValue(centersMockList));
        }});
        List<Center> centerList = uuidService.getCenters();
        assertNotNull(centerList);
    }

    @Test
    public void testGetActiveDiseases() {

        final List<Tumor> tumorMockList = new ArrayList<Tumor>();
        tumorMockList.add(new Tumor());
        tumorMockList.add(new Tumor());

        mocker.checking(new Expectations() {{
            oneOf(uuidDAO).getActiveDiseases();
            will(returnValue(tumorMockList));
        }});
        List<Tumor> tumorList = uuidService.getActiveDiseases();
        assertNotNull(tumorList);
    }

    @Test
    public void testSearchUUID() throws UUIDException {

        final List<UUIDDetail> uuidMockList = new ArrayList<UUIDDetail>();
        uuidMockList.add(new UUIDDetail());
        uuidMockList.add(new UUIDDetail());
        final SearchCriteria criteria = new SearchCriteria();

        mocker.checking(new Expectations() {{
            allowing(uuidDAO).searchUUIDs(criteria);
            will(returnValue(uuidMockList));
        }});

        List<UUIDDetail> uuidList = uuidService.searchUUIDs(criteria);
        assertNotNull(uuidList);
        assertEquals(2, uuidList.size());
    }

    @Test
    public void testGetUUIDDetail() throws UUIDException {
        UUIDDetail detail;
        final String uuid = "thisUUID";

        mocker.checking(new Expectations() {{
            oneOf(uuidDAO).getUUIDDetail(uuid);
            will(returnValue(new UUIDDetail()));
        }});

        detail = uuidService.getUUIDDetails(uuid);
        assertNotNull(detail);
    }

    @Test
    public void testNewUUIDreport() throws UUIDException {
        final List<UUIDDetail> uuidMockList = mockUUIDList();
        final Duration duration = Duration.Week;
        mocker.checking(new Expectations() {{
            allowing(uuidDAO).getNewlyGeneratedUUIDs(duration);
            will(returnValue(uuidMockList));
        }});
        List<UUIDDetail> uuidList = uuidService.getNewlyGeneratedUUIDs(duration);
        assertNotNull(uuidList);
        assertEquals(2, uuidList.size());
    }

    @Test
    public void testSubmittedUUIDReport() throws UUIDException {
        final List<UUIDDetail> uuidMockList = mockUUIDList();
        mocker.checking(new Expectations() {{
            allowing(uuidDAO).getSubmittedUUIDs();
            will(returnValue(uuidMockList));
        }});

        List<UUIDDetail> uuidList = uuidService.getSubmittedUUIDs();
        assertNotNull(uuidList);
        assertEquals(2, uuidList.size());
    }

    @Test
    public void testMissingUUIDReport() {
        final List<UUIDDetail> uuidMockList = mockUUIDList();
        mocker.checking(new Expectations() {{
            allowing(uuidDAO).getMissingUUIDs();
            will(returnValue(uuidMockList));
        }});

        List<UUIDDetail> uuidList = uuidService.getMissingUUIDs();
        assertNotNull(uuidList);
        assertEquals(2, uuidList.size());
    }

    @Test
    public void testValidUUID() throws Exception {
        assertTrue(uuidService.isValidUUID("a9baac37-3167-4331-a173-c7bf4d8ae056"));
    }

    @Test
    public void testNonValidUUID() throws Exception {
        assertFalse(uuidService.isValidUUID("blahblahblah"));
    }

    @Test
    public void testGetLatestBarcodeForUUID() {
        mocker.checking(new Expectations() {{
            one(uuidDAO).getLatestBarcodeForUUID("test");
            will(returnValue("barcode!"));
        }});
        final String barcode = uuidService.getLatestBarcodeForUUID("test");
        assertEquals("barcode!", barcode);
    }

    @Test
    public void testGetLatestBarcodeForUUIDNoMatch() {
        mocker.checking(new Expectations() {{
            one(uuidDAO).getLatestBarcodeForUUID("random");
            will(returnValue(null));
        }});
        final String barcode = uuidService.getLatestBarcodeForUUID("random");
        assertNull(barcode);
    }

    @Test
    public void testRegisterUUID() throws UUIDException {
        final Center center = new Center();
        mocker.checking(new Expectations() {{
            one(uuidDAO).uuidExists("1234");
            will(returnValue(false));
            atLeast(1).of(centerQueries).getCenterById(with(CENTER_ID));
            will(returnValue(center));
            one(uuidDAO).addUUID(with(new TypeSafeMatcher<List<UUIDDetail>>() {
                public boolean matchesSafely(final List<UUIDDetail> uuidList) {
                    if (uuidList.size() != 1) {
                        return false;
                    } else {
                        final UUIDDetail uuidDetail = uuidList.get(0);
                        return uuidDetail.getCenter().equals(center) &&
                                uuidDetail.getUuid().equals("1234") &&
                                uuidDetail.getCreatedBy().equals(UUIDConstants.MASTER_USER) &&
                                uuidDetail.getGenerationMethod().equals(UUIDConstants.GenerationMethod.Upload);
                    }
                }

                public void describeTo(final Description description) {
                    description.appendText("checks for expected UUIDDetail");
                }
            }));
            will(returnValue(1));
        }});
        uuidService.registerUUID("1234", CENTER_ID);
    }

    @Test(expected = UUIDException.class)
    public void testRegisterUUIDBadCenter() throws UUIDException {
        mocker.checking(new Expectations() {{
            one(uuidDAO).uuidExists("abc");
            will(returnValue(false));
            one(centerQueries).getCenterById(5);
            will(returnValue(null));
        }});

        uuidService.registerUUID("abc", 5);
    }

    @Test(expected = UUIDException.class)
    public void testRegisterUUIDAlreadyExists() throws UUIDException {
        final UUIDDetail existingDetail = new UUIDDetail();
        // if the UUID exists, throw an exception
        mocker.checking(new Expectations() {{
            one(uuidDAO).uuidExists("uuid-1234");
            will(returnValue(true));
        }});
        uuidService.registerUUID("uuid-1234", CENTER_ID);
    }

    @Test
    public void getUUIDsExistInDB() throws Exception {
        final List<String> UUIDs = Arrays.asList("UUID1", "UUID2");
        mocker.checking(new Expectations() {{
            one(uuidDAO).getUUIDsExistInDB(UUIDs);
            will(returnValue(UUIDs));
        }});
        final List<String> UUIDsExistInDB = uuidService.getUUIDsExistInDB(UUIDs);
        Assert.assertNotNull(UUIDsExistInDB);
        Assert.assertEquals(2, UUIDsExistInDB.size());

    }


    private void mockDAO(final int numberOfUuidsExpected) throws UUIDException {
        final Center center = new Center();
        center.setEmailList(Arrays.asList("test@test"));
        mocker.checking(new Expectations() {{
            atLeast(1).of(centerQueries).getCenterById(with(CENTER_ID));
            will(returnValue(center));
            one(uuidDAO).addUUID(with(expectedSizeUUIDList(numberOfUuidsExpected)));
            will(returnValue(NUMBER_OF_ROWS_UPDATED));
        }});
    }

    private static Matcher<List<UUIDDetail>> expectedSizeUUIDList(final int numberOfUuidsExpected) {
        return new TypeSafeMatcher<List<UUIDDetail>>() {
            @Override
            public boolean matchesSafely(List<UUIDDetail> list) {
                return list.size() == numberOfUuidsExpected;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("expects list of UUIDDetails of size ").appendText(String.valueOf(numberOfUuidsExpected));
            }
        };
    }

    private void mailShouldSend() {
        mocker.checking(new Expectations() {{
            one(mailSender).send(with("test@test"), with((String) null), with("New UUIDs Generated"), with(any(String.class)), with(false));
        }});
    }

    private void mailShouldNotSend() {
        mocker.checking(new Expectations() {{
            never(mailSender).send(with("test@test"), with((String) null), with("New UUIDs Generated"), with(any(String.class)), with(false));
        }});
    }

    private List<UUIDDetail> mockUUIDList() {
        List<UUIDDetail> uuidMockList = new ArrayList<UUIDDetail>();
        uuidMockList.add(new UUIDDetail());
        uuidMockList.add(new UUIDDetail());
        return uuidMockList;
    }

    @Test
    public void testGetUUIDsForBarcodeMultipleMatches() {

        final String barcodePrefix = "squirrel";
        final String[] expectedUUIDs = {"uuid-1", "uuid-2"};

        checkGetBarcodesStartingWith(barcodePrefix, expectedUUIDs, false);
    }

    @Test
    public void testGetUUIDsForBarcodeOneMatch() {

        final String barcodePrefix = "squirrel";
        final String[] expectedUUIDs = {"uuid-1"};

        checkGetBarcodesStartingWith(barcodePrefix, expectedUUIDs, false);
    }

    @Test
    public void testGetUUIDsForBarcodeExactMatch() {

        final String barcodePrefix = "squirrel";
        final String[] expectedUUIDs = {"uuid-1"};

        checkGetBarcodesStartingWith(barcodePrefix, expectedUUIDs, true);
    }

    @Test
    public void testGetUUIDsForBarcodeNoMatch() {

        final String barcodePrefix = "squirrel";
        final String[] expectedUUIDs = {};

        checkGetBarcodesStartingWith(barcodePrefix, expectedUUIDs, false);
    }

    @Test
    public void testGetLatestBarcodesForUUIDs() throws Exception {
        final List<String> uuids = Arrays.asList("UUID1", "UUID2");
        final List<UuidBarcodeMapping> barcodes = Arrays.asList(new UuidBarcodeMapping("barcode1", "UUID1"),
                new UuidBarcodeMapping("barcode2", "UUID2"));
        mocker.checking(new Expectations() {{
            one(uuidDAO).getLatestBarcodesForUUIDs(uuids);
            will(returnValue(barcodes));
        }});
        final List<UuidBarcodeMapping> barcodeList = uuidService.getLatestBarcodesForUUIDs(uuids);
        Assert.assertNotNull(barcodeList);
        Assert.assertEquals(2, barcodeList.size());
        Assert.assertEquals("barcode1", barcodeList.get(0).getBarcode());
        Assert.assertEquals("barcode2", barcodeList.get(1).getBarcode());
    }

    @Test
    public void testGetLatestUUIDsForBarcodes() throws Exception {
        final List<UuidBarcodeMapping> uuids = Arrays.asList(new UuidBarcodeMapping("barcode1", "UUID1"),
                new UuidBarcodeMapping("barcode2", "UUID2"));
        final List<String> barcodes = Arrays.asList("barcode1", "barcode2");
        mocker.checking(new Expectations() {{
            one(uuidDAO).getUUIDsForBarcodes(barcodes);
            will(returnValue(uuids));
        }});
        final List<UuidBarcodeMapping> uuidList = uuidService.getUUIDsForBarcodes(barcodes);
        Assert.assertNotNull(uuidList);
        Assert.assertEquals(2, uuidList.size());
        Assert.assertEquals("UUID1", uuidList.get(0).getUuid());
        Assert.assertEquals("UUID2", uuidList.get(1).getUuid());
    }

    @Test
    public void testUuidExistWhenNotReceivedByDcc() {

        final String uuid= "a";

        mocker.checking(new Expectations() {{
            one(uuidDAO).uuidExists(uuid);
            will(returnValue(true));
        }});

        final boolean uuidExists = uuidService.uuidExists(uuid);
        assertTrue(uuidExists);
    }

    @Test
    public void testUuidExistWhenReceivedByDcc() {

        final String uuid= "a";

        mocker.checking(new Expectations() {{
            one(uuidDAO).uuidExists(uuid);
            will(returnValue(false));
        }});

        final boolean uuidExists = uuidService.uuidExists(uuid);
        assertFalse(uuidExists);
    }

    /**
     * Call getUUIDsForBarcode() service with the given barcode prefix
     * and check expectations against the given uuids
     *
     * @param barcodePrefix the barcode prefix
     * @param expectedUUIDs the expected UUIDs
     * @param exactMatch    if the expected barcode should be an exact match
     */
    private void checkGetBarcodesStartingWith(final String barcodePrefix, final String[] expectedUUIDs, final boolean exactMatch) {

        mocker.checking(new Expectations() {{
            one(uuidDAO).getBarcodesStartingWith(barcodePrefix);
            will(returnValue(makeBarcodesFromUUIDs(barcodePrefix, expectedUUIDs, exactMatch)));
        }});

        final List<Barcode> barcodes = uuidService.getBarcodesStartingWith(barcodePrefix);
        assertNotNull(barcodes);
        assertEquals(expectedUUIDs.length, barcodes.size());

        final String[] actualUUIDs = new String[expectedUUIDs.length];

        for (int i = 0; i < expectedUUIDs.length; i++) {

            final Barcode barcode = barcodes.get(i);
            assertNotNull(barcode);

            final String uuid = barcode.getUuid();
            assertNotNull(uuid);

            // Store uuids for comparison once all uuids are retrieved
            actualUUIDs[i] = uuid;

            final String barcodeAsString = barcode.getBarcode();
            assertNotNull(barcodeAsString);

            if (exactMatch) {
                assertEquals(barcodePrefix, barcodeAsString);
            } else {
                assertTrue(barcodeAsString.startsWith(barcodePrefix));
            }
        }

        assertArrayEquals(expectedUUIDs, actualUUIDs);
    }

    /**
     * Return a list of <code>Barcode</code> with the given barcode prefix, each having one of the expected UUIDs.
     * If <code>exactMatch</code> is <code>true</code> then each barcodePrefix get appended with a non blank string
     *
     * @param barcodePrefix the barcode prefix to use for each <code>Barcode</code>
     * @param expectedUUIDs the list of UUIDs to use for the <code>Barcode</code>s
     * @param exactMatch    if <code>true</code> then each barcodePrefix get appended with a non blank string
     * @return a list of <code>Barcode</code> with the given barcode prefix, each having one of the expected UUIDs
     */
    private List<Barcode> makeBarcodesFromUUIDs(final String barcodePrefix, final String[] expectedUUIDs, boolean exactMatch) {

        final List<Barcode> result = new ArrayList<Barcode>();

        for (int i = 0; i < expectedUUIDs.length; i++) {

            String barcodeAsString = barcodePrefix;

            if (!exactMatch) {
                barcodeAsString += (char) ('A' + i); // Adding non blank string to the barcode prefix
            }

            final Barcode barcode = new Barcode();
            barcode.setBarcode(barcodeAsString);
            barcode.setUuid(expectedUUIDs[i]);

            result.add(barcode);
        }

        return result;
    }
}
