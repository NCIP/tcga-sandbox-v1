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
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Test Class for pending UUID processor
 *
 * @author Stan Girshik Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class PendingUUIDProcessorFastTest {

    private Mockery context;

    protected static final String JSON_PATH =
            Thread.currentThread().getContextClassLoader().getResource("pendingUUID").getPath() + File.separator;

    private PendingUUIDProcessor processor;
    private static String pendingUUIDMessage = "";
    private static String pendingUUIDMessageWithDups = "";
    PendingUUIDDAO mockPendingUUIDDao;
    CommonBarcodeAndUUIDValidator mockCommonBarcodeAndUUIDValidator;

    @BeforeClass
    public static void initStaticResources() throws IOException {
        InputStream is = new FileInputStream(JSON_PATH + "pending_barcode_sample.json");
        pendingUUIDMessage = IOUtils.toString(is);
        pendingUUIDMessageWithDups = IOUtils.toString(new FileInputStream(JSON_PATH +
                "pending_barcode_sample_dups.json"));
    }

    @Before
    public void setUp() {
        context = new JUnit4Mockery();
        mockPendingUUIDDao = context.mock(PendingUUIDDAO.class);
        mockCommonBarcodeAndUUIDValidator = context.mock(CommonBarcodeAndUUIDValidator.class);
        processor = new PendingUUIDProcessor();
        processor.setPendingUUIDDao(mockPendingUUIDDao);
        processor.setCommonBarcodeAndUUIDValidator(mockCommonBarcodeAndUUIDValidator);
        processor.setErrorMessages(new ArrayList<String>());
        processor.setBarcodesPresent(new HashMap<String, String>());
        processor.setUuidsPresent(new HashMap<String, String>());
    }

    @Test
    public void testGetAliquotOrShippedPortion() throws Exception {
        final String coordinateAliquot = "{\"E6\":{\"bcr_aliquot_uuid\":\"46730-d5f8-4c55-a3a4-be3355122210\"}}";
        final String coordinateShippedPortion = "{\"E8\":{\"bcr_shipment_portion_uuid\":\"57841-d5f8-4c55-a3a4-be3355122210\"}}";
        final JSONObject aliquot = JSONObject.fromObject(coordinateAliquot).getJSONObject("E6");
        final JSONObject shippedPortion = JSONObject.fromObject(coordinateShippedPortion).getJSONObject("E8");
        final PendingUUID pendingUUID = new PendingUUID();
        final String resAliquot = processor.getAliquotOrShippedPortion(pendingUUID, aliquot,
                "bcr_aliquot_uuid", "bcr_shipment_portion_uuid");
        final String resShippedPortion = processor.getAliquotOrShippedPortion(pendingUUID, shippedPortion,
                "bcr_aliquot_uuid", "bcr_shipment_portion_uuid");
        assertNotNull(resAliquot);
        assertEquals("46730-d5f8-4c55-a3a4-be3355122210", resAliquot);
        assertNotNull(resShippedPortion);
        assertEquals("57841-d5f8-4c55-a3a4-be3355122210", resShippedPortion);
    }

    @Test
    public void testGetPendingUUIDsFromJson() throws Exception {
        final List<PendingUUID> resList = processor.getPendingUUIDsFromJson(pendingUUIDMessage);
        assertNotNull(resList);
        assertEquals(96, resList.size());
        assertEquals("0740d340-d5f8-4c55-a3a4-be3355122210", resList.get(0).getUuid());
        assertEquals("A1", resList.get(0).getPlateCoordinate());
        assertEquals("IGC", resList.get(0).getBcr());
        assertEquals("Aliquot", resList.get(0).getItemType());
        assertEquals("2484d340-d5f8-4c55-a3a4-be3355122210", resList.get(1).getUuid());
        assertEquals("A2", resList.get(1).getPlateCoordinate());
        assertEquals("IGC", resList.get(1).getBcr());
        assertEquals("Shipped Portion", resList.get(1).getItemType());
        assertEquals(null, resList.get(43).getUuid());
        assertEquals("D8", resList.get(43).getPlateCoordinate());
        assertEquals("IGC", resList.get(43).getBcr());
        assertEquals(null, resList.get(43).getItemType());
        assertEquals(null, resList.get(95).getUuid());
        assertEquals("H12", resList.get(95).getPlateCoordinate());
        assertEquals("IGC", resList.get(95).getBcr());
        assertEquals(null, resList.get(95).getItemType());
    }

    @Test
    public void testIsReceivedByDccWhenNew() {
        Assert.assertFalse(new PendingUUID().isReceivedByDcc());
    }

    @Test
    public void testIsReceivedByDccWhenIsReceivedByDccIsSet() {
        final PendingUUID pendingUUID = new PendingUUID();
        pendingUUID.setDccReceivedDate(new Date());
        Assert.assertTrue(pendingUUID.isReceivedByDcc());
    }

    @Test
    public void parsePendingUUIDTest() {
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        assertTrue(pendingMessage != null && !pendingMessage.isEmpty());
    }

    @Test(expected = JSONException.class)
    public void parsePendingUUIDInvalidMessageTest() {
        processor.parsePendingUUID(pendingUUIDMessage + " :::");
    }

    @Test
    public void parseAndValidatePendingUUIDJsonTest() {
        context.checking(new Expectations() {{
            one(mockPendingUUIDDao).isValidCenter("03");
            will(returnValue(true));
            allowing(mockPendingUUIDDao).isValidSampleType("01");
            will(returnValue(true));
            allowing(mockPendingUUIDDao).isValidAnalyteType("D");
            will(returnValue(true));
            allowing(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat(with(any(String.class)));
            will(returnValue(true));
            allowing(mockPendingUUIDDao).isValidBatchNumber(with(any(String.class)));
            will(returnValue(true));
            allowing(mockPendingUUIDDao).alreadyPendingUUID(with(any(String.class)));
            will(returnValue(false));
            allowing(mockPendingUUIDDao).alreadyReceivedUUID(with(any(String.class)));
            will(returnValue(false));
            allowing(mockCommonBarcodeAndUUIDValidator).validateAliquotBarcodeFormat(with(any(String.class)));
            will(returnValue(true));
            allowing(mockPendingUUIDDao).alreadyPendingBarcode(with(any(String.class)));
            will(returnValue(false));
            allowing(mockPendingUUIDDao).alreadyReceivedBarcode(with(any(String.class)));
            will(returnValue(false));
            allowing(mockCommonBarcodeAndUUIDValidator).validateShipmentPortionBarcodeFormat(with(any(String.class)));
            will(returnValue(true));
        }});
        assertTrue(processor.parseAndValidatePendingUUIDJson(pendingUUIDMessage));
        assertTrue(processor.getErrors().isEmpty());
    }

    @Test
    public void parseAndValidatePendingUUIDInvalidDupsJsonTest() {
        context.checking(new Expectations() {{
            one(mockPendingUUIDDao).isValidCenter("03");
            will(returnValue(true));
            allowing(mockPendingUUIDDao).isValidSampleType("01");
            will(returnValue(true));
            allowing(mockPendingUUIDDao).isValidAnalyteType("D");
            will(returnValue(true));
            allowing(mockPendingUUIDDao).isValidBatchNumber(with(any(String.class)));
            will(returnValue(true));
            allowing(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat(with(any(String.class)));
            will(returnValue(true));
            allowing(mockPendingUUIDDao).alreadyPendingUUID(with(any(String.class)));
            will(returnValue(false));
            allowing(mockPendingUUIDDao).alreadyReceivedUUID(with(any(String.class)));
            will(returnValue(false));
            allowing(mockCommonBarcodeAndUUIDValidator).validateAliquotBarcodeFormat(with(any(String.class)));
            will(returnValue(true));
            allowing(mockPendingUUIDDao).alreadyPendingBarcode(with(any(String.class)));
            will(returnValue(false));
            allowing(mockPendingUUIDDao).alreadyReceivedBarcode(with(any(String.class)));
            will(returnValue(false));
            allowing(mockCommonBarcodeAndUUIDValidator).validateShipmentPortionBarcodeFormat(with(any(String.class)));
            will(returnValue(true));
        }});
        assertFalse(processor.parseAndValidatePendingUUIDJson(pendingUUIDMessageWithDups));
        assertEquals(3, processor.getErrors().size());
        assertEquals("Element 'plate_id' in the json message is of type Array, it may be a duplicate.",
                processor.getErrors().get(0));
        assertEquals("Element 'portion_number' in 'A3' is of type Array, it may be a duplicate.",
                processor.getErrors().get(1));
        assertEquals("Element 'bcr_shipment_portion_uuid' in 'A4' is of type Array, it may be a duplicate.",
                processor.getErrors().get(2));
    }

    @Test
    public void parseAndValidatePendingUUIDInvalidJsonTest() {
        assertFalse(processor.parseAndValidatePendingUUIDJson(pendingUUIDMessage + "::"));
        List<String> errorList = processor.getErrors();
        assertEquals(errorList.size(), 1);
        assertEquals("The pending uuid message is not a well-formed JSON message", errorList.get(0));
    }

    @Test
    public void testValidateBcrObject() {
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        assertTrue(processor.validateBcr(pendingMessage));
    }

    @Test
    public void testValidateMissingBcrObject() {
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        pendingMessage.remove("bcr");
        assertFalse(processor.validateBcr(pendingMessage));
        assertEquals("Element 'bcr' must be present in the json message.",
                processor.getErrors().get(0));
    }

    @Test
    public void testValidateInvalidBcrObject() {
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        pendingMessage.put("bcr", "bad");
        assertFalse(processor.validateBcr(pendingMessage));
        assertEquals("Element bcr must either be IGC or NCH but bad was provided.", processor.getErrors().get(0));
    }

    @Test
    public void testShipDate() {
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        assertTrue(processor.validateShippedDate(pendingMessage));
    }

    @Test
    public void testShipMissingDate() {
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        pendingMessage.remove("ship_date");
        assertFalse(processor.validateShippedDate(pendingMessage));
        assertEquals("Element 'ship_date' must be present in the json message.",
                processor.getErrors().get(0));
    }

    @Test
    public void testInvalidShipDate() {
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        pendingMessage.put("ship_date", "July 10 1999");
        assertFalse(processor.validateShippedDate(pendingMessage));
        assertEquals("Element ship_date must be in MM-DD-YYYY format , instead July 10 1999 found.", processor.getErrors().get(0));

        pendingMessage.put("ship_date", "02/10/00");
        assertFalse(processor.validateShippedDate(pendingMessage));
        assertEquals("Element ship_date must be in MM-DD-YYYY format , instead 02/10/00 found.", processor.getErrors().get(1));
    }

    @Test
    public void validatePlateId() {
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        assertTrue(processor.validatePlateId(pendingMessage));
    }

    @Test
    public void validateMissingPlateId() {
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        pendingMessage.remove(processor.PLATE_ID);
        assertFalse(processor.validatePlateId(pendingMessage));
        assertEquals("Element 'plate_id' must be present in the json message.",
                processor.getErrors().get(0));
    }

    @Test
    public void testIsCenterValid() {
        context.checking(new Expectations() {{
            one(mockPendingUUIDDao).isValidCenter("03");
            will(returnValue(true));
        }});

        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        assertTrue(processor.validateCenter(pendingMessage));
    }

    @Test
    public void testInvalidCenter() {
        context.checking(new Expectations() {{
            one(mockPendingUUIDDao).isValidCenter("03");
            will(returnValue(false));
        }});

        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        assertFalse(processor.validateCenter(pendingMessage));
        assertEquals("Element center is not valid. The value 03 did not validate against a " +
                "list of bcr center IDs.", processor.getErrors().get(0));
    }

    @Test
    public void testMissingCenterValue() {
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        pendingMessage.remove("center");
        assertFalse(processor.validateCenter(pendingMessage));
        assertEquals("Element 'center' must be present in the json message.",
                processor.getErrors().get(0));
    }

    @Test
    public void testValidatePlate() {
        context.checking(new Expectations() {{
            allowing(mockPendingUUIDDao).isValidSampleType("01");
            will(returnValue(true));
            allowing(mockPendingUUIDDao).isValidAnalyteType("D");
            will(returnValue(true));
            allowing(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat(with(any(String.class)));
            will(returnValue(true));
            allowing(mockPendingUUIDDao).isValidBatchNumber(with(any(String.class)));
            will(returnValue(true));
            allowing(mockPendingUUIDDao).alreadyPendingUUID(with(any(String.class)));
            will(returnValue(false));
            allowing(mockPendingUUIDDao).alreadyReceivedUUID(with(any(String.class)));
            will(returnValue(false));
            allowing(mockCommonBarcodeAndUUIDValidator).validateAliquotBarcodeFormat(with(any(String.class)));
            will(returnValue(true));
            allowing(mockPendingUUIDDao).alreadyPendingBarcode(with(any(String.class)));
            will(returnValue(false));
            allowing(mockPendingUUIDDao).alreadyReceivedBarcode(with(any(String.class)));
            will(returnValue(false));
            allowing(mockCommonBarcodeAndUUIDValidator).validateShipmentPortionBarcodeFormat(with(any(String.class)));
            will(returnValue(true));
        }});
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        assertTrue(processor.validatePlate(pendingMessage));
    }

    @Test
    public void testMissingPlate() {
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        pendingMessage.remove("plate");
        assertFalse(processor.validatePlate(pendingMessage));
        assertEquals("Element 'plate' must be present in the json message.",
                processor.getErrors().get(0));
    }

    @Test
    public void testInvalidPlate() {
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        pendingMessage.put("plate", "fakePlate");
        assertFalse(processor.validatePlate(pendingMessage));
        assertEquals("Element 'plate' in the json message is not valid. " +
                "The value should be of type 'JSONObject'.", processor.getErrors().get(0));
    }

    @Test
    public void testInvalidWellCount() {
        context.checking(new Expectations() {{
            allowing(mockPendingUUIDDao).isValidSampleType("01");
            will(returnValue(true));
            allowing(mockPendingUUIDDao).isValidAnalyteType("D");
            will(returnValue(true));
            allowing(mockPendingUUIDDao).isValidBatchNumber(with(any(String.class)));
            will(returnValue(true));
            allowing(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat(with(any(String.class)));
            will(returnValue(true));
            allowing(mockPendingUUIDDao).alreadyPendingUUID(with(any(String.class)));
            will(returnValue(false));
            allowing(mockPendingUUIDDao).alreadyReceivedUUID(with(any(String.class)));
            will(returnValue(false));
            allowing(mockCommonBarcodeAndUUIDValidator).validateAliquotBarcodeFormat(with(any(String.class)));
            will(returnValue(true));
            allowing(mockPendingUUIDDao).alreadyPendingBarcode(with(any(String.class)));
            will(returnValue(false));
            allowing(mockPendingUUIDDao).alreadyReceivedBarcode(with(any(String.class)));
            will(returnValue(false));
            allowing(mockCommonBarcodeAndUUIDValidator).validateShipmentPortionBarcodeFormat(with(any(String.class)));
            will(returnValue(true));
        }});
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        JSONObject plate = (JSONObject) pendingMessage.get("plate");
        plate.remove("A1");
        plate.remove("H2");
        plate.remove("C3");
        assertFalse(processor.validatePlate(pendingMessage));
        assertEquals("A well A1 is missing on the plate. A plate must contain 96 wells with names made up of rows represented by letters A-H, and columns represented by the numbers 1-12. ( e.g A1, A2.. H12)", processor.getErrors().get(0));
        assertEquals("A well C3 is missing on the plate. A plate must contain 96 wells with names made up of rows represented by letters A-H, and columns represented by the numbers 1-12. ( e.g A1, A2.. H12)", processor.getErrors().get(1));
        assertEquals("A well H2 is missing on the plate. A plate must contain 96 wells with names made up of rows represented by letters A-H, and columns represented by the numbers 1-12. ( e.g A1, A2.. H12)", processor.getErrors().get(2));
    }

    @Test
    public void testValidateNullWells() {
        context.checking(new Expectations() {{
            allowing(mockPendingUUIDDao).isValidSampleType("01");
            will(returnValue(true));
            allowing(mockPendingUUIDDao).isValidAnalyteType("D");
            will(returnValue(true));
            allowing(mockPendingUUIDDao).isValidBatchNumber(with(any(String.class)));
            will(returnValue(true));
            allowing(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat(with(any(String.class)));
            will(returnValue(true));
            allowing(mockPendingUUIDDao).alreadyPendingUUID(with(any(String.class)));
            will(returnValue(false));
            allowing(mockPendingUUIDDao).alreadyReceivedUUID(with(any(String.class)));
            will(returnValue(false));
            allowing(mockCommonBarcodeAndUUIDValidator).validateAliquotBarcodeFormat(with(any(String.class)));
            will(returnValue(true));
            allowing(mockPendingUUIDDao).alreadyPendingBarcode(with(any(String.class)));
            will(returnValue(false));
            allowing(mockPendingUUIDDao).alreadyReceivedBarcode(with(any(String.class)));
            will(returnValue(false));
            allowing(mockCommonBarcodeAndUUIDValidator).validateShipmentPortionBarcodeFormat(with(any(String.class)));
            will(returnValue(true));
        }});
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        JSONObject plate = (JSONObject) pendingMessage.get("plate");
        plate.put("A1", "null");
        plate.put("H12", "null");
        plate.put("H3", "null");
        assertTrue(processor.validatePlate(pendingMessage));
    }

    @Test
    public void testValidateIncorrectWellTypes() {
        context.checking(new Expectations() {{
            allowing(mockPendingUUIDDao).isValidSampleType("01");
            will(returnValue(true));
            allowing(mockPendingUUIDDao).isValidAnalyteType("D");
            will(returnValue(true));
            allowing(mockPendingUUIDDao).isValidBatchNumber(with(any(String.class)));
            will(returnValue(true));
            allowing(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat(with(any(String.class)));
            will(returnValue(true));
            allowing(mockPendingUUIDDao).alreadyPendingUUID(with(any(String.class)));
            will(returnValue(false));
            allowing(mockPendingUUIDDao).alreadyReceivedUUID(with(any(String.class)));
            will(returnValue(false));
            allowing(mockCommonBarcodeAndUUIDValidator).validateAliquotBarcodeFormat(with(any(String.class)));
            will(returnValue(true));
            allowing(mockPendingUUIDDao).alreadyPendingBarcode(with(any(String.class)));
            will(returnValue(false));
            allowing(mockPendingUUIDDao).alreadyReceivedBarcode(with(any(String.class)));
            will(returnValue(false));
            allowing(mockCommonBarcodeAndUUIDValidator).validateShipmentPortionBarcodeFormat(with(any(String.class)));
            will(returnValue(true));
        }});
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        JSONObject plate = (JSONObject) pendingMessage.get("plate");
        plate.put("A1", "some string");
        assertFalse(processor.validatePlate(pendingMessage));
        assertEquals("A well 'A1' value has to either be an object with well elements defined or a null.",
                processor.getErrors().get(0));
    }

    @Test
    public void testValidateWellDups() {
        context.checking(new Expectations() {{
            allowing(mockPendingUUIDDao).isValidSampleType("01");
            will(returnValue(true));
            allowing(mockPendingUUIDDao).isValidAnalyteType("D");
            will(returnValue(true));
            allowing(mockPendingUUIDDao).isValidBatchNumber(with(any(String.class)));
            will(returnValue(true));
            allowing(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat(with(any(String.class)));
            will(returnValue(true));
            allowing(mockPendingUUIDDao).alreadyPendingUUID(with(any(String.class)));
            will(returnValue(false));
            allowing(mockPendingUUIDDao).alreadyReceivedUUID(with(any(String.class)));
            will(returnValue(false));
            allowing(mockCommonBarcodeAndUUIDValidator).validateAliquotBarcodeFormat(with(any(String.class)));
            will(returnValue(true));
            allowing(mockPendingUUIDDao).alreadyPendingBarcode(with(any(String.class)));
            will(returnValue(false));
            allowing(mockPendingUUIDDao).alreadyReceivedBarcode(with(any(String.class)));
            will(returnValue(false));
            allowing(mockCommonBarcodeAndUUIDValidator).validateShipmentPortionBarcodeFormat(with(any(String.class)));
            will(returnValue(true));
        }});
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        JSONObject plate = (JSONObject) pendingMessage.get("plate");
        JSONObject wellA1 = (JSONObject) plate.get("A1");
        JSONObject wellA2 = (JSONObject) plate.get("A2");
        JSONObject wellA3 = (JSONObject) plate.get("A3");
        wellA1.put("bcr_aliquot_uuid", "2312d340-d5f8-4c55-a3a4-be3355122210");
        wellA2.put("bcr_shipment_portion_barcode", "TCGA-BT-A20Q-01A-11D-A14W-01");
        wellA3.put("bcr_aliquot_barcode", "TCGA-CJ-0220d34-01A-13-1742-20");
        assertFalse(processor.validatePlate(pendingMessage));
        assertEquals(6, processor.getErrors().size());
        assertEquals("The uuid '2312d340-d5f8-4c55-a3a4-be3355122210' in well 'A1' is a duplicate.",
                processor.getErrors().get(0));
        assertEquals("The uuid '2312d340-d5f8-4c55-a3a4-be3355122210' in well 'A3' is a duplicate.",
                processor.getErrors().get(1));
        assertEquals("The barcode 'TCGA-CJ-0220d34-01A-13-1742-20' in well 'A4' is a duplicate.",
                processor.getErrors().get(2));
        assertEquals("The barcode 'TCGA-CJ-0220d34-01A-13-1742-20' in well 'A3' is a duplicate.",
                processor.getErrors().get(3));
        assertEquals("The barcode 'TCGA-BT-A20Q-01A-11D-A14W-01' in well 'A2' is a duplicate.",
                processor.getErrors().get(4));
        assertEquals("The barcode 'TCGA-BT-A20Q-01A-11D-A14W-01' in well 'A5' is a duplicate.",
                processor.getErrors().get(5));
    }

    @Test
    public void testGetDuplicates() throws Exception {
        final Collection<String> collection = new ArrayList<String>() {{
            add("un");
            add("deux");
            add("trois");
            add("deux");
        }};
        final List<String> res = processor.getDuplicates(collection);
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals("deux", res.get(0));
    }

    @Test
    public void testGetKeysByValue() throws Exception {
        Map<String, String> map = new HashMap<String, String>() {{
            put("2", "deux");
            put("1", "un");
            put("02", "deux");
        }};
        List<String> res = processor.getKeysByValue(map, "deux");
        assertNotNull(res);
        assertEquals(2, res.size());
        assertEquals("2", res.get(0));
        assertEquals("02", res.get(1));
    }

    @Test
    public void checkVialNumber() {
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        JSONObject plate = (JSONObject) pendingMessage.get("plate");
        assertTrue(processor.checkVialNumber(plate.getJSONObject("A1"), "A1"));
    }

    @Test
    public void checkMissingVialNumber() {
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        JSONObject plate = (JSONObject) pendingMessage.get("plate");
        JSONObject well = plate.getJSONObject("A1");
        well.remove("vial_number");
        assertFalse(processor.checkVialNumber(well, "A1"));
        assertEquals("Element 'vial_number' must be present in the 'A1' parent element.",
                processor.getErrors().get(0));
    }

    @Test
    public void checkWrongTypeVialNumber() {
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        JSONObject plate = (JSONObject) pendingMessage.get("plate");
        JSONObject well = plate.getJSONObject("A1");
        well.put("vial_number", 1);
        assertFalse(processor.checkVialNumber(well, "A1"));
        assertEquals("Element 'vial_number' in 'A1' is not valid. The value should be of type 'String'.", processor.getErrors().get(0));
    }

    @Test
    public void checkPortionNumber() {
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        JSONObject plate = (JSONObject) pendingMessage.get("plate");
        assertTrue(processor.checkPortionNumber(plate.getJSONObject("A1"), "A1"));
    }

    @Test
    public void checkMissingPortionNumber() {
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        JSONObject plate = (JSONObject) pendingMessage.get("plate");
        JSONObject well = plate.getJSONObject("A1");
        well.remove("portion_number");
        assertFalse(processor.checkPortionNumber(well, "A1"));
        assertEquals("Element 'portion_number' must be present in the 'A1' parent element.",
                processor.getErrors().get(0));
    }

    @Test
    public void testInvalidAnalyteShippedPortionRelation() {
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        JSONObject plate = (JSONObject) pendingMessage.get("plate");
        JSONObject well = plate.getJSONObject("A2");
        well.put("analyte_type", "D");
        assertFalse(processor.validateAnalyteShippedPortionRelation(well, "A2"));
        assertEquals("Element 'analyte_type' in well 'A2' is not valid. " +
                "The well contains a shipped portion and should have a null analyte type value.",
                processor.getErrors().get(0));
    }

    @Test
    public void persistPendingUUIDsTest() {
        context.checking(new Expectations() {{
            one(mockPendingUUIDDao).insertPendingUUIDList(null);
        }});
        processor.persistPendingUUIDs(null);
    }

    @Test
    public void validateBatchNumber() {
        context.checking(new Expectations() {{
            one(mockPendingUUIDDao).isValidBatchNumber("2");
            will(returnValue(true));
        }});
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        JSONObject plate = (JSONObject) pendingMessage.get("plate");
        JSONObject well = plate.getJSONObject("A1");
        assertTrue(processor.validateBatchNumber(well, "A1"));
    }

    @Test
    public void validateMissingBatchNumber() {
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        JSONObject plate = (JSONObject) pendingMessage.get("plate");
        JSONObject well = plate.getJSONObject("A1");
        well.remove("batch_number");
        assertFalse(processor.validateBatchNumber(well, "A1"));
        assertEquals("Element 'batch_number' must be present in the 'A1' parent element.",
                processor.getErrors().get(0));
    }

    @Test
    public void validateInvalidBatchNumber() {
        context.checking(new Expectations() {{
            one(mockPendingUUIDDao).isValidBatchNumber("a");
            will(returnValue(false));
        }});
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        JSONObject plate = (JSONObject) pendingMessage.get("plate");
        JSONObject well = plate.getJSONObject("A1");
        well.put("batch_number", "a");
        assertFalse(processor.validateBatchNumber(well, "A1"));
        assertEquals("Element 'batch_number' of value 'a' in well 'A1' did not validate against DCC database.",
                processor.getErrors().get(0));
    }

    @Test
    public void validateSampleTypeTest() {
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        JSONObject plate = (JSONObject) pendingMessage.get("plate");
        JSONObject well = plate.getJSONObject("A1");

        context.checking(new Expectations() {{
            one(mockPendingUUIDDao).isValidSampleType("01");
            will(returnValue(true));

        }});
        assertTrue(processor.validateSampleType(well, "A1"));
    }

    @Test
    public void validateInvalidSampleTypeTest() {
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        JSONObject plate = (JSONObject) pendingMessage.get("plate");
        JSONObject well = plate.getJSONObject("A1");

        context.checking(new Expectations() {{
            one(mockPendingUUIDDao).isValidSampleType("01");
            will(returnValue(false));

        }});
        assertFalse(processor.validateSampleType(well, "A1"));
        assertEquals("Element 'sample_type' of value '01' in well 'A1' did not validate against DCC database.",
                processor.getErrors().get(0));
    }

    @Test
    public void validateAnalyteTypeTest() {
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        JSONObject plate = (JSONObject) pendingMessage.get("plate");
        JSONObject well = plate.getJSONObject("A1");

        context.checking(new Expectations() {{
            one(mockPendingUUIDDao).isValidAnalyteType("D");
            will(returnValue(true));
        }});
        assertTrue(processor.validateAnalyteType(well, "A1"));
    }

    @Test
    public void validateInvalidAnalyteTypeTest() {
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        JSONObject plate = (JSONObject) pendingMessage.get("plate");
        JSONObject well = plate.getJSONObject("A1");

        context.checking(new Expectations() {{
            one(mockPendingUUIDDao).isValidAnalyteType("D");
            will(returnValue(false));
        }});
        assertFalse(processor.validateAnalyteType(well, "A1"));
        assertEquals("Element 'analyte_type' of value 'D' in well 'A1' did not validate against DCC database.",
                processor.getErrors().get(0));
    }

    @Test
    public void validateAnalyteNullTypeTest() {
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        JSONObject plate = (JSONObject) pendingMessage.get("plate");
        JSONObject well = plate.getJSONObject("A1");
        well.put("analyte_type", "null");

        assertTrue(processor.validateAnalyteType(well, "A1"));
    }

    @Test
    public void validateAnalyteNullStringTypeTest() {
        context.checking(new Expectations() {{
            one(mockPendingUUIDDao).isValidAnalyteType("null");
            will(returnValue(false));
        }});
        JSONObject pendingMessage = processor.parsePendingUUID(pendingUUIDMessage);
        JSONObject plate = (JSONObject) pendingMessage.get("plate");
        JSONObject well = plate.getJSONObject("A1");
        well.put("analyte_type", "'null'");

        assertFalse(processor.validateAnalyteType(well, "A1"));
    }


    @Test
    public void testCheckElementTypeValid() throws Exception {
        final String json1 = "{\"E6\":{\"test1\":\"46730-d5f8-4c55-a3a4-be3355122210\"}}";
        final String json2 = "{\"E8\":{\"test2\":1}}";
        final JSONObject jsonRes = JSONObject.fromObject(json1);
        final JSONObject input1 = JSONObject.fromObject(json1).getJSONObject("E6");
        final JSONObject input2 = JSONObject.fromObject(json2).getJSONObject("E8");
        assertTrue(processor.checkElementType("E6", jsonRes, "root", JSONObject.class));
        assertTrue(processor.checkElementType("test1", input1, "E6", String.class));
        assertTrue(processor.checkElementType("test2", input2, "E8", Integer.class));
    }

    @Test
    public void testCheckElementTypeInvalid() throws Exception {
        final String json1 = "{\"E6\":{\"test1\":\"46730-d5f8-4c55-a3a4-be3355122210\"}}";
        final String json2 = "{\"E8\":{\"test2\":1}}";
        final JSONObject jsonRes = JSONObject.fromObject(json1);
        final JSONObject input1 = JSONObject.fromObject(json1).getJSONObject("E6");
        final JSONObject input2 = JSONObject.fromObject(json2).getJSONObject("E8");
        assertFalse(processor.checkElementType("test1", input1, "E6", Integer.class));
        assertFalse(processor.checkElementType("test2", input2, "E8", String.class));
        assertEquals("Element 'test1' in 'E6' is not valid. The value should be of type 'Integer'.",
                processor.getErrors().get(0));
        assertEquals("Element 'test2' in 'E8' is not valid. The value should be of type 'String'.",
                processor.getErrors().get(1));
    }

    @Test
    public void testCheckUUIDFormatValid() throws Exception {
        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("uuid1");
            will(returnValue(true));
        }});
        assertTrue(processor.checkUUIDFormat("uuid1", "key", "A1"));
    }

    @Test
    public void testCheckUUIDFormatInValid() throws Exception {
        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("uuid1");
            will(returnValue(false));
        }});
        assertFalse(processor.checkUUIDFormat("uuid1", "key", "A1"));
        assertEquals("Element 'key' in well 'A1' is not valid. The value 'uuid1' has an invalid uuid format.",
                processor.getErrors().get(0));
    }

    @Test
    public void testCheckAliquotFormatValid() throws Exception {
        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateAliquotBarcodeFormat("barcode1");
            will(returnValue(true));
        }});
        assertTrue(processor.checkAliquotFormat("barcode1", "A1"));
    }

    @Test
    public void testCheckAliquotFormatInValid() throws Exception {
        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateAliquotBarcodeFormat("barcode1");
            will(returnValue(false));
        }});
        assertFalse(processor.checkAliquotFormat("barcode1", "A1"));
        assertEquals("Element 'bcr_aliquot_barcode' in well 'A1' is not valid. " +
                "The value 'barcode1' has an invalid aliquot barcode format.",
                processor.getErrors().get(0));
    }

    @Test
    public void testCheckShippedPortionFormatValid() throws Exception {
        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateShipmentPortionBarcodeFormat("barcode1");
            will(returnValue(true));
        }});
        assertTrue(processor.checkShippedPortionFormat("barcode1", "A1"));
    }

    @Test
    public void testCheckShippedPortionFormatInValid() throws Exception {
        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateShipmentPortionBarcodeFormat("barcode1");
            will(returnValue(false));
        }});
        assertFalse(processor.checkShippedPortionFormat("barcode1", "A1"));
        assertEquals("Element 'bcr_shipment_portion_barcode' in well 'A1' is not valid. " +
                "The value 'barcode1' has an invalid shipped portion barcode format.",
                processor.getErrors().get(0));
    }

    @Test
    public void testCheckUUIDAlreadyPendingNonExist() throws Exception {
        context.checking(new Expectations() {{
            one(mockPendingUUIDDao).alreadyPendingUUID("uuid1");
            will(returnValue(false));
        }});
        assertTrue(processor.checkUUIDAlreadyPending("uuid1", "key", "A1"));
    }

    @Test
    public void testCheckUUIDAlreadyPendingExist() throws Exception {
        context.checking(new Expectations() {{
            one(mockPendingUUIDDao).alreadyPendingUUID("uuid1");
            will(returnValue(true));
        }});
        assertFalse(processor.checkUUIDAlreadyPending("uuid1", "key", "A1"));
        assertEquals("Element 'key' in well 'A1' is not valid. " +
                "The value 'uuid1' has already been marked as pending.",
                processor.getErrors().get(0));
    }

    @Test
    public void testCheckUUIDAlreadyReceivedNonExist() throws Exception {
        context.checking(new Expectations() {{
            one(mockPendingUUIDDao).alreadyReceivedUUID("uuid1");
            will(returnValue(false));
        }});
        assertTrue(processor.checkUUIDAlreadyReceived("uuid1", "key", "A1"));
    }

    @Test
    public void testCheckUUIDAlreadyReceivedExist() throws Exception {
        context.checking(new Expectations() {{
            one(mockPendingUUIDDao).alreadyReceivedUUID("uuid1");
            will(returnValue(true));
        }});
        assertFalse(processor.checkUUIDAlreadyReceived("uuid1", "key", "A1"));
        assertEquals("Element 'key' in well 'A1' is not valid. " +
                "The value 'uuid1' has already been submitted to DCC.",
                processor.getErrors().get(0));
    }

    @Test
    public void testCheckBarcodeAlreadyPendingNonExist() throws Exception {
        context.checking(new Expectations() {{
            one(mockPendingUUIDDao).alreadyPendingBarcode("barcode1");
            will(returnValue(false));
        }});
        assertTrue(processor.checkBarcodeAlreadyPending("barcode1", "key", "A1"));
    }

    @Test
    public void testCheckBarcodeAlreadyPendingExist() throws Exception {
        context.checking(new Expectations() {{
            one(mockPendingUUIDDao).alreadyPendingBarcode("barcode1");
            will(returnValue(true));
        }});
        assertFalse(processor.checkBarcodeAlreadyPending("barcode1", "key", "A1"));
        assertEquals("Element 'key' in well 'A1' is not valid. " +
                "The value 'barcode1' has already been marked as pending.",
                processor.getErrors().get(0));
    }

    @Test
    public void testCheckBarcodeAlreadyReceivedNonExist() throws Exception {
        context.checking(new Expectations() {{
            one(mockPendingUUIDDao).alreadyReceivedBarcode("barcode1");
            will(returnValue(false));
        }});
        assertTrue(processor.checkBarcodeAlreadyReceived("barcode1", "key", "A1"));
    }

    @Test
    public void testCheckBarcodeAlreadyReceivedExist() throws Exception {
        context.checking(new Expectations() {{
            one(mockPendingUUIDDao).alreadyReceivedBarcode("barcode1");
            will(returnValue(true));
        }});
        assertFalse(processor.checkBarcodeAlreadyReceived("barcode1", "key", "A1"));
        assertEquals("Element 'key' in well 'A1' is not valid. " +
                "The value 'barcode1' has already been submitted to DCC.",
                processor.getErrors().get(0));
    }

    @Test
    public void testValidateUUIDValid() throws Exception {
        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("uuid1Value");
            will(returnValue(true));
            one(mockPendingUUIDDao).alreadyPendingUUID("uuid1Value");
            will(returnValue(false));
            one(mockPendingUUIDDao).alreadyReceivedUUID("uuid1Value");
            will(returnValue(false));
        }});
        final String json1 = "{\"E6\":{\"uuid1\":\"uuid1Value\"}}";
        final JSONObject input1 = JSONObject.fromObject(json1).getJSONObject("E6");
        assertTrue(processor.validateUUID("uuid1", input1, "E6"));
    }

    @Test
    public void testValidateUUIDInValidWrongType() throws Exception {
        final String json1 = "{\"E6\":{\"uuid1\":1}}";
        final JSONObject input1 = JSONObject.fromObject(json1).getJSONObject("E6");
        assertFalse(processor.validateUUID("uuid1", input1, "E6"));
        assertEquals(1, processor.getErrors().size());
        assertEquals("Element 'uuid1' in 'E6' is not valid. The value should be of type 'String'.",
                processor.getErrors().get(0));
    }

    @Test
    public void testValidateUUIDInValidWrongFormat() throws Exception {
        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("uuid1Value");
            will(returnValue(false));
        }});
        final String json1 = "{\"E6\":{\"uuid1\":\"uuid1Value\"}}";
        final JSONObject input1 = JSONObject.fromObject(json1).getJSONObject("E6");
        assertFalse(processor.validateUUID("uuid1", input1, "E6"));
        assertEquals(1, processor.getErrors().size());
        assertEquals("Element 'uuid1' in well 'E6' is not valid. The value 'uuid1Value' " +
                "has an invalid uuid format.", processor.getErrors().get(0));
    }

    @Test
    public void testValidateUUIDInValidAlreadyPending() throws Exception {
        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("uuid1Value");
            will(returnValue(true));
            one(mockPendingUUIDDao).alreadyPendingUUID("uuid1Value");
            will(returnValue(true));
        }});
        final String json1 = "{\"E6\":{\"uuid1\":\"uuid1Value\"}}";
        final JSONObject input1 = JSONObject.fromObject(json1).getJSONObject("E6");
        assertFalse(processor.validateUUID("uuid1", input1, "E6"));
        assertEquals(1, processor.getErrors().size());
        assertEquals("Element 'uuid1' in well 'E6' is not valid. The value 'uuid1Value' " +
                "has already been marked as pending.", processor.getErrors().get(0));
    }

    @Test
    public void testValidateUUIDInValidAlreadyReceived() throws Exception {
        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("uuid1Value");
            will(returnValue(true));
            one(mockPendingUUIDDao).alreadyPendingUUID("uuid1Value");
            will(returnValue(false));
            one(mockPendingUUIDDao).alreadyReceivedUUID("uuid1Value");
            will(returnValue(true));
        }});
        final String json1 = "{\"E6\":{\"uuid1\":\"uuid1Value\"}}";
        final JSONObject input1 = JSONObject.fromObject(json1).getJSONObject("E6");
        assertFalse(processor.validateUUID("uuid1", input1, "E6"));
        assertEquals(1, processor.getErrors().size());
        assertEquals("Element 'uuid1' in well 'E6' is not valid. " +
                "The value 'uuid1Value' has already been submitted to DCC.", processor.getErrors().get(0));
    }

    @Test
    public void testValidateAliquotBarcodeValid() throws Exception {
        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateAliquotBarcodeFormat("barcode");
            will(returnValue(true));
            one(mockPendingUUIDDao).alreadyPendingBarcode("barcode");
            will(returnValue(false));
            one(mockPendingUUIDDao).alreadyReceivedBarcode("barcode");
            will(returnValue(false));
        }});
        final String json1 = "{\"E6\":{\"bcr_aliquot_barcode\":\"barcode\"}}";
        final JSONObject input1 = JSONObject.fromObject(json1).getJSONObject("E6");
        assertTrue(processor.validateAliquotBarcode(input1, "E6"));
    }

    @Test
    public void testValidateAliquotBarcodeInValidWrongType() throws Exception {
        final String json1 = "{\"E6\":{\"bcr_aliquot_barcode\":1}}";
        final JSONObject input1 = JSONObject.fromObject(json1).getJSONObject("E6");
        assertFalse(processor.validateAliquotBarcode(input1, "E6"));
        assertEquals(1, processor.getErrors().size());
        assertEquals("Element 'bcr_aliquot_barcode' in 'E6' is not valid. " +
                "The value should be of type 'String'.", processor.getErrors().get(0));
    }

    @Test
    public void testValidateAliquotBarcodeInValidWrongFormat() throws Exception {
        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateAliquotBarcodeFormat("barcode");
            will(returnValue(false));
        }});
        final String json1 = "{\"E6\":{\"bcr_aliquot_barcode\":\"barcode\"}}";
        final JSONObject input1 = JSONObject.fromObject(json1).getJSONObject("E6");
        assertFalse(processor.validateAliquotBarcode(input1, "E6"));
        assertEquals(1, processor.getErrors().size());
        assertEquals("Element 'bcr_aliquot_barcode' in well 'E6' is not valid. " +
                "The value 'barcode' has an invalid aliquot barcode format.", processor.getErrors().get(0));
    }

    @Test
    public void testValidateAliquotBarcodeInValidAlreadyPending() throws Exception {
        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateAliquotBarcodeFormat("barcode");
            will(returnValue(true));
            one(mockPendingUUIDDao).alreadyPendingBarcode("barcode");
            will(returnValue(true));
        }});
        final String json1 = "{\"E6\":{\"bcr_aliquot_barcode\":\"barcode\"}}";
        final JSONObject input1 = JSONObject.fromObject(json1).getJSONObject("E6");
        assertFalse(processor.validateAliquotBarcode(input1, "E6"));
        assertEquals(1, processor.getErrors().size());
        assertEquals("Element 'bcr_aliquot_barcode' in well 'E6' is not valid. " +
                "The value 'barcode' has already been marked as pending.", processor.getErrors().get(0));
    }

    @Test
    public void testValidateAliquotBarcodeInValidAlreadyReceived() throws Exception {
        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateAliquotBarcodeFormat("barcode");
            will(returnValue(true));
            one(mockPendingUUIDDao).alreadyPendingBarcode("barcode");
            will(returnValue(false));
            one(mockPendingUUIDDao).alreadyReceivedBarcode("barcode");
            will(returnValue(true));
        }});
        final String json1 = "{\"E6\":{\"bcr_aliquot_barcode\":\"barcode\"}}";
        final JSONObject input1 = JSONObject.fromObject(json1).getJSONObject("E6");
        assertFalse(processor.validateAliquotBarcode(input1, "E6"));
        assertEquals(1, processor.getErrors().size());
        assertEquals("Element 'bcr_aliquot_barcode' in well 'E6' is not valid. " +
                "The value 'barcode' has already been submitted to DCC.", processor.getErrors().get(0));
    }


    @Test
    public void testValidateShippedPortionBarcodeValid() throws Exception {
        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateShipmentPortionBarcodeFormat("barcode");
            will(returnValue(true));
            one(mockPendingUUIDDao).alreadyPendingBarcode("barcode");
            will(returnValue(false));
            one(mockPendingUUIDDao).alreadyReceivedBarcode("barcode");
            will(returnValue(false));
        }});
        final String json1 = "{\"E6\":{\"bcr_shipment_portion_barcode\":\"barcode\"}}";
        final JSONObject input1 = JSONObject.fromObject(json1).getJSONObject("E6");
        assertTrue(processor.validateShippedPortionBarcode(input1, "E6"));
    }

    @Test
    public void testValidateShippedPortionBarcodeInValidWrongType() throws Exception {
        final String json1 = "{\"E6\":{\"bcr_shipment_portion_barcode\":1}}";
        final JSONObject input1 = JSONObject.fromObject(json1).getJSONObject("E6");
        assertFalse(processor.validateShippedPortionBarcode(input1, "E6"));
        assertEquals(1, processor.getErrors().size());
        assertEquals("Element 'bcr_shipment_portion_barcode' in 'E6' is not valid. " +
                "The value should be of type 'String'.", processor.getErrors().get(0));
    }

    @Test
    public void testValidateShippedPortionBarcodeInValidWrongFormat() throws Exception {
        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateShipmentPortionBarcodeFormat("barcode");
            will(returnValue(false));
        }});
        final String json1 = "{\"E6\":{\"bcr_shipment_portion_barcode\":\"barcode\"}}";
        final JSONObject input1 = JSONObject.fromObject(json1).getJSONObject("E6");
        assertFalse(processor.validateShippedPortionBarcode(input1, "E6"));
        assertEquals(1, processor.getErrors().size());
        assertEquals("Element 'bcr_shipment_portion_barcode' in well 'E6' is not valid. " +
                "The value 'barcode' has an invalid shipped portion barcode format.",
                processor.getErrors().get(0));
    }

    @Test
    public void testValidateShippedPortionBarcodeInValidAlreadyPending() throws Exception {
        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateShipmentPortionBarcodeFormat("barcode");
            will(returnValue(true));
            one(mockPendingUUIDDao).alreadyPendingBarcode("barcode");
            will(returnValue(true));
        }});
        final String json1 = "{\"E6\":{\"bcr_shipment_portion_barcode\":\"barcode\"}}";
        final JSONObject input1 = JSONObject.fromObject(json1).getJSONObject("E6");
        assertFalse(processor.validateShippedPortionBarcode(input1, "E6"));
        assertEquals(1, processor.getErrors().size());
        assertEquals("Element 'bcr_shipment_portion_barcode' in well 'E6' is not valid. " +
                "The value 'barcode' has already been marked as pending.", processor.getErrors().get(0));
    }

    @Test
    public void testValidateShippedPortionBarcodeInValidAlreadyReceived() throws Exception {
        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateShipmentPortionBarcodeFormat("barcode");
            will(returnValue(true));
            one(mockPendingUUIDDao).alreadyPendingBarcode("barcode");
            will(returnValue(false));
            one(mockPendingUUIDDao).alreadyReceivedBarcode("barcode");
            will(returnValue(true));
        }});
        final String json1 = "{\"E6\":{\"bcr_shipment_portion_barcode\":\"barcode\"}}";
        final JSONObject input1 = JSONObject.fromObject(json1).getJSONObject("E6");
        assertFalse(processor.validateShippedPortionBarcode(input1, "E6"));
        assertEquals(1, processor.getErrors().size());
        assertEquals("Element 'bcr_shipment_portion_barcode' in well 'E6' is not valid. " +
                "The value 'barcode' has already been submitted to DCC.", processor.getErrors().get(0));
    }

    @Test
    public void testValidateBcrAliquotUUIDAndBarcodeValidAliquot() throws Exception {
        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("uuid");
            will(returnValue(true));
            one(mockPendingUUIDDao).alreadyPendingUUID("uuid");
            will(returnValue(false));
            one(mockPendingUUIDDao).alreadyReceivedUUID("uuid");
            will(returnValue(false));
            one(mockCommonBarcodeAndUUIDValidator).validateAliquotBarcodeFormat("barcode");
            will(returnValue(true));
            one(mockPendingUUIDDao).alreadyPendingBarcode("barcode");
            will(returnValue(false));
            one(mockPendingUUIDDao).alreadyReceivedBarcode("barcode");
            will(returnValue(false));
        }});
        final String json1 = "{\"E6\":{\"bcr_aliquot_uuid\":\"uuid\"," +
                "\"bcr_aliquot_barcode\":\"barcode\"}}";
        final JSONObject input1 = JSONObject.fromObject(json1).getJSONObject("E6");
        assertTrue(processor.validateBcrAliquotUUIDAndBarcode(input1, "E6"));
    }

    @Test
    public void testValidateBcrAliquotUUIDAndBarcodeValidShippedPortion() throws Exception {
        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("uuid");
            will(returnValue(true));
            one(mockPendingUUIDDao).alreadyPendingUUID("uuid");
            will(returnValue(false));
            one(mockPendingUUIDDao).alreadyReceivedUUID("uuid");
            will(returnValue(false));
            one(mockCommonBarcodeAndUUIDValidator).validateShipmentPortionBarcodeFormat("barcode");
            will(returnValue(true));
            one(mockPendingUUIDDao).alreadyPendingBarcode("barcode");
            will(returnValue(false));
            one(mockPendingUUIDDao).alreadyReceivedBarcode("barcode");
            will(returnValue(false));
        }});
        final String json1 = "{\"E6\":{\"bcr_shipment_portion_uuid\":\"uuid\"," +
                "\"bcr_shipment_portion_barcode\":\"barcode\"}}";
        final JSONObject input1 = JSONObject.fromObject(json1).getJSONObject("E6");
        assertTrue(processor.validateBcrAliquotUUIDAndBarcode(input1, "E6"));
    }

    @Test
    public void testValidateBcrAliquotUUIDAndBarcodeInvalidMissingAliquotBarcode() throws Exception {
        final String json1 = "{\"E6\":{\"bcr_aliquot_uuid\":\"uuid\"}}";
        final JSONObject input1 = JSONObject.fromObject(json1).getJSONObject("E6");
        assertFalse(processor.validateBcrAliquotUUIDAndBarcode(input1, "E6"));
        assertEquals("Element 'bcr_aliquot_barcode' or 'bcr_shipment_portion_barcode' " +
                "must be present in well 'E6'.", processor.getErrors().get(0));
    }

    @Test
    public void testValidateBcrAliquotUUIDAndBarcodeInvalidMissingShippedPortionBarcode() throws Exception {
        final String json1 = "{\"E6\":{\"bcr_shipment_portion_uuid\":\"uuid\"}}";
        final JSONObject input1 = JSONObject.fromObject(json1).getJSONObject("E6");
        assertFalse(processor.validateBcrAliquotUUIDAndBarcode(input1, "E6"));
        assertEquals("Element 'bcr_aliquot_barcode' or 'bcr_shipment_portion_barcode' " +
                "must be present in well 'E6'.", processor.getErrors().get(0));
    }

    @Test
    public void testValidateBcrAliquotUUIDAndBarcodeInvalidMissingAliquotUUID() throws Exception {
        final String json1 = "{\"E6\":{\"bcr_aliquot_barcode\":\"barcode\"}}";
        final JSONObject input1 = JSONObject.fromObject(json1).getJSONObject("E6");
        assertFalse(processor.validateBcrAliquotUUIDAndBarcode(input1, "E6"));
        assertEquals("Element 'bcr_aliquot_uuid' or 'bcr_shipment_portion_uuid' " +
                "must be present in well 'E6'.", processor.getErrors().get(0));
    }

    @Test
    public void testValidateBcrAliquotUUIDAndBarcodeInvalidMissingShippedPortionUUID() throws Exception {
        final String json1 = "{\"E6\":{\"bcr_shipment_portion_barcode\":\"barcode\"}}";
        final JSONObject input1 = JSONObject.fromObject(json1).getJSONObject("E6");
        assertFalse(processor.validateBcrAliquotUUIDAndBarcode(input1, "E6"));
        assertEquals("Element 'bcr_aliquot_uuid' or 'bcr_shipment_portion_uuid' " +
                "must be present in well 'E6'.", processor.getErrors().get(0));
    }

    @Test
    public void testValidateBcrAliquotUUIDAndBarcodeInvalidWrongPairShippedPortion() throws Exception {
        final String json1 = "{\"E6\":{\"bcr_shipment_portion_uuid\":\"uuid\"," +
                "\"bcr_aliquot_barcode\":\"barcode\"}}";
        final JSONObject input1 = JSONObject.fromObject(json1).getJSONObject("E6");
        assertFalse(processor.validateBcrAliquotUUIDAndBarcode(input1, "E6"));
        assertEquals("Error in well 'E6'. If bcr_shipment_portion_uuid is used instead of " +
                "bcr_aliquot_uuid, then bcr_shipment_portion_barcode must be used instead of " +
                "bcr_aliquot_barcode or vice versa.", processor.getErrors().get(0));
    }

    @Test
    public void testValidateBcrAliquotUUIDAndBarcodeInvalidWrongPairAliquot() throws Exception {
        final String json1 = "{\"E6\":{\"bcr_aliquot_uuid\":\"uuid\"," +
                "\"bcr_shipment_portion_barcode\":\"barcode\"}}";
        final JSONObject input1 = JSONObject.fromObject(json1).getJSONObject("E6");
        assertFalse(processor.validateBcrAliquotUUIDAndBarcode(input1, "E6"));
        assertEquals("Error in well 'E6'. If bcr_shipment_portion_uuid is used instead of " +
                "bcr_aliquot_uuid, then bcr_shipment_portion_barcode must be used instead of " +
                "bcr_aliquot_barcode or vice versa.", processor.getErrors().get(0));
    }
}
