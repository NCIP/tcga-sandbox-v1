/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Barcode;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ShippedBiospecimen;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BCRID;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BiospecimenToFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.BCRIDQueries;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;

/**
 * Test class for BCRIdProcessor
 * 
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class BCRIDProcessorFastTest {

	private final Mockery context = new JUnit4Mockery();
	private BarcodeUuidResolver mockBarcodeUuidResolver;
	private BCRIDQueries mockBCRIDQueries;
	private CenterQueries mockCenterQueries;
	private BCRIDProcessorImpl bcridProcessor;
	private BCRID bcrId;
	private Tumor disease;
    private Center center;
	private static final String SAMPLES_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;

	@Before
	public void setup() {
		mockBarcodeUuidResolver = context.mock(BarcodeUuidResolver.class);
		mockBCRIDQueries = context.mock(BCRIDQueries.class);
		mockCenterQueries = context.mock(CenterQueries.class);

		bcridProcessor = new BCRIDProcessorImpl();
		bcridProcessor.setAliquotElementXPath("//aliquots/aliquot");
		bcridProcessor.setAliquotBarcodeElement("bcr_aliquot_barcode");
		bcridProcessor.setShipDayElement("day_of_shipment");
		bcridProcessor.setShipMonthElement("month_of_shipment");
		bcridProcessor.setShipYearElement("year_of_shipment");
		bcridProcessor.setAliquotUuidElement("bcr_aliquot_uuid");
		bcridProcessor.setBcrIDQueries(mockBCRIDQueries);
		bcridProcessor.setCenterQueries(mockCenterQueries);
		bcridProcessor.setBarcodeUuidResolver(mockBarcodeUuidResolver);
		bcridProcessor.setPatientElementUUIDXPath("//patient/bcr_patient_uuid");
		center = new Center();
		center.setCenterId(1);

		bcridProcessor
				.setShippedPortionBarcodeElement("shipment_portion_bcr_aliquot_barcode");
		bcridProcessor
				.setShippedPortionElementXPath("//portions/shipment_portion");
		bcridProcessor
				.setShippedPortionUuidElement("bcr_shipment_portion_uuid");
		bcridProcessor
				.setShippedPortionShipDayElement("shipment_portion_day_of_shipment");
		bcridProcessor
				.setShippedPortionShipMonthElement("shipment_portion_month_of_shipment");
		bcridProcessor
				.setShippedPortionShipYearElement("shipment_portion_year_of_shipment");

		bcrId = new BCRID();
		disease = new Tumor();
		disease.setTumorId(1);
	}

	@Test
	public void testFindPatientUUIDclinical() throws ParserConfigurationException,
		IOException,SAXException, TransformerException{								
		String filename = SAMPLES_DIR
				+ "qclive/clinicalXmlValidator/nationwidechildrens.org_BRCA.bio.Level_1.85.20.0/" +
				"nationwidechildrens.org_biospecimen.TCGA-E2-A14Z.xml";		
		String testUUID =  bcridProcessor.getPatientUUIDfromFile(new File (filename));
		assertEquals("d7ce6b79-f7c0-481e-9dbd-6273f01c9786",testUUID);
	}
	
	@Test
	public void testFindPatientUUIDBio() throws ParserConfigurationException,
		IOException,SAXException, TransformerException{
		String filename = SAMPLES_DIR
				+ "qclive/biospecimenXmlValidator/" +
				"nationwidechildrens.org_biospecimen.TCGA-C4-A0F6.xml";		
		String testUUID =  bcridProcessor.getPatientUUIDfromFile(new File (filename));
		assertEquals("0FAC0588-2DA9-4048-9FBC-22668E7CB5A5",testUUID);
	}
	
	@Test
	public void testFindPatientUUIDAux() throws ParserConfigurationException,
		IOException,SAXException, TransformerException{
		String filename = SAMPLES_DIR
				+ "qclive/auxiliaryXmlValidator/" +
				"nationwidechildrens.org_auxiliary.TCGA-A1-MR56.xml";		
		String testUUID =  bcridProcessor.getPatientUUIDfromFile(new File (filename));
		assertEquals("6f73f960-7a32-44fa-8e54-3af426025a61",testUUID);
	}
	
	@Test
	public void testFindPatientUUIDControl() throws ParserConfigurationException,
		IOException,SAXException, TransformerException{
		String filename = SAMPLES_DIR
				+ "qclive/biospecimenXmlValidator/" +
				"good_control.TCGA-ZZ-1234.xml";		
		String testUUID =  bcridProcessor.getPatientUUIDfromFile(new File (filename));
		assertEquals("d6f911b5-e895-43f8-8f86-0ac2f1bc6fae",testUUID);
	}	
	@Test
	public void testFindAllAliquotsInFile() throws TransformerException,
			IOException, XPathExpressionException, SAXException,
			ParserConfigurationException {
		// test file with just 3 aliquot elements in it
		final String filename = SAMPLES_DIR
				+ "qclive/bcrIdProcessor/aliquots_1.15.xml";
		final List<String[]> bcrIds = bcridProcessor
				.findAllAliquotsInFile(new File(filename));
		assertEquals(5, bcrIds.size());
        for (String[] bcrId : bcrIds) {
            assertEquals(4, bcrId.length);
            assertEquals("15", bcrId[3]);
        }

		assertEquals("TCGA-02-0001-10A-01W-0190-09", bcrIds.get(0)[0]);
		assertEquals("2007-04-19", bcrIds.get(0)[1]);
		assertEquals("TCGA-02-0001-10A-01W-0189-08", bcrIds.get(1)[0]);
		assertEquals("2007-04-19", bcrIds.get(1)[1]);
		assertEquals("2007-04-19", bcrIds.get(2)[1]);

		// last one should have null for date, because one part was missing
		assertNull(bcrIds.get(3)[1]);

		// check uuids
		assertEquals("11111111-1111-1111-1111-abcdefabcdef", bcrIds.get(0)[2]);
		assertEquals("22222222-2222-2222-2222-abcdefabcdef", bcrIds.get(1)[2]);
		// 3rd aliquot has empty UUID element
		assertNull(bcrIds.get(2)[2]);
		// 4th aliquot has no UUID element
		assertNull(bcrIds.get(3)[2]);
	}

	@Test
	public void testParseBCRID() throws ParseException {
		final String validBarcode = "TCGA-AB-2802-03A-01T-0734-13";
		final BCRID bcrId = bcridProcessor.parseAliquotBarcode(validBarcode);
		assertNotNull(bcrId);
		assertEquals("2802", bcrId.getPatientID());
		assertEquals("AB", bcrId.getSiteID());
		assertEquals("03A", bcrId.getSampleID());
		assertEquals("03", bcrId.getSampleTypeCode());
		assertEquals("A", bcrId.getSampleNumberCode());
		assertEquals("01T", bcrId.getPortionID());
		assertEquals("01", bcrId.getPortionNumber());
		assertEquals("T", bcrId.getPortionTypeCode());
		assertEquals("0734", bcrId.getPlateId());
		assertEquals("13", bcrId.getBcrCenterId());
	}

	@Test
	public void testGetShippedPortions() throws TransformerException,
			IOException, SAXException, XPathExpressionException,
			ParseException, ParserConfigurationException {

		final String shippedPortionFileLocation = SAMPLES_DIR
				+ "qclive/bcrIdProcessor/shippedPortions.xml";
		final File xmlFileWithShippedPortions = new File(
				shippedPortionFileLocation);
		List<ShippedBiospecimen> shippedPortions = bcridProcessor
				.findAllShippedPortionsInFile(xmlFileWithShippedPortions);

		assertEquals(3, shippedPortions.size());

        assertEquals(new Integer(99), shippedPortions.get(0).getBatchNumber());
        assertEquals(new Integer(99), shippedPortions.get(1).getBatchNumber());
        assertEquals(new Integer(99), shippedPortions.get(2).getBatchNumber());

		assertEquals("TCGA-00-0000-01A-21-1234-20", shippedPortions.get(0)
				.getBarcode());
		assertEquals("TCGA-00-0000-01A-22-1234-20", shippedPortions.get(1)
				.getBarcode());
		assertEquals("TCGA-00-0000-01A-23-1234-20", shippedPortions.get(2)
				.getBarcode());

        // this one has capital letters in the file -- make sure it is lowercase in object
		assertEquals("1111111-1234-1234-1234-abcdef654321", shippedPortions
				.get(0).getUuid());
		assertEquals("2222222-1234-1234-1234-abcdef654321", shippedPortions
				.get(1).getUuid());
		assertEquals("3333333-1234-1234-1234-abcdef654321", shippedPortions
				.get(2).getUuid());

		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd");
		assertEquals("2011-12-01", simpleDateFormat.format(shippedPortions.get(
				0).getShippedDate()));
		assertEquals("2011-01-02", simpleDateFormat.format(shippedPortions.get(
				1).getShippedDate()));
		assertEquals("2011-08-03", simpleDateFormat.format(shippedPortions.get(
				2).getShippedDate()));

		assertEquals("21", shippedPortions.get(0).getPortionSequence());
		assertEquals("22", shippedPortions.get(1).getPortionSequence());
		assertEquals("23", shippedPortions.get(2).getPortionSequence());

		assertEquals("20", shippedPortions.get(0).getBcrCenterId());
		assertEquals("20", shippedPortions.get(1).getBcrCenterId());
		assertEquals("20", shippedPortions.get(2).getBcrCenterId());

		assertEquals("1234", shippedPortions.get(0).getPlateId());
		assertEquals("1234", shippedPortions.get(1).getPlateId());
		assertEquals("1234", shippedPortions.get(2).getPlateId());
	}

	@Test
	public void testGetShippedPortionsEmptyDate() throws TransformerException,
			IOException, SAXException, XPathExpressionException,
			ParseException, ParserConfigurationException {
		final String shippedPortionFileLocation = SAMPLES_DIR
				+ "qclive/bcrIdProcessor/shippedPortionsNoDates.xml";
		final File xmlFileWithShippedPortions = new File(
				shippedPortionFileLocation);
		List<ShippedBiospecimen> shippedPortions = bcridProcessor
				.findAllShippedPortionsInFile(xmlFileWithShippedPortions);
		assertEquals(3, shippedPortions.size());
		assertNull(shippedPortions.get(0).getShippedDate());
		assertNull(shippedPortions.get(1).getShippedDate());
		assertNull(shippedPortions.get(2).getShippedDate());
	}

	@Test(expected = ParseException.class)
	public void testGetShippedPortionsBadDate() throws TransformerException,
			IOException, SAXException, XPathExpressionException,
			ParseException, ParserConfigurationException {
		final String shippedPortionFileLocation = SAMPLES_DIR
				+ "qclive/bcrIdProcessor/shippedPortionsBadDate.xml";
		final File xmlFileWithShippedPortions = new File(
				shippedPortionFileLocation);
		bcridProcessor.findAllShippedPortionsInFile(xmlFileWithShippedPortions);
	}

	/*
	 * Covers the case where the aliquot is brand-new and has never been seen
	 * before. The UUID was not given in the XML.
	 */
	@Test
	public void testStoreBCRIDNewAliquotNoUUID() throws ParseException,
			UUIDException {
		bcrId.setShippingDate("this is a shipping date");
		bcrId.setFullID("hi I am a barcode");
		bcrId.setUUID(null);
		final Center bcrCenter = new Center();
		bcrCenter.setCenterId(5);
        bcrId.setArchiveId(789L);

		context.checking(new Expectations() {
			{
				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						"hi I am a barcode", null, disease, bcrCenter, true);
				will(returnValue(makeBarcode("hi I am a barcode",
						"b645a265-fe5a-4755-b548-2169ad6751f3")));

				one(mockBCRIDQueries).exists(bcrId);
				will(returnValue(-1)); // means aliquot doesn't exist in db yet
				one(mockBCRIDQueries).addBCRID(bcrId, false);
				will(returnValue(10)); // id of new aliquot
				one(mockBCRIDQueries).updateBCRIDStatus(bcrId);

				one(mockBCRIDQueries).addArchiveRelationship(bcrId, false,
						new int[] { -1 });
				one(mockBCRIDQueries).updateShipDate(bcrId);

        }});

		bcridProcessor.storeBcrBarcode(bcrId, false, new int[] { -1 }, disease,
				bcrCenter);
		assertEquals(new Integer(10), bcrId.getId());
		assertEquals("b645a265-fe5a-4755-b548-2169ad6751f3", bcrId.getUUID());
	}

	private static Barcode makeBarcode(final String barcode, final String uuid) {
		final Barcode barcodeDetail = new Barcode();
		barcodeDetail.setBarcode(barcode);
		barcodeDetail.setUuid(uuid);
		return barcodeDetail;
	}

	/*
	 * Covers the case when the method is called for the disease data source, so
	 * must use the ID from dccCommon
	 */
	@Test
	public void testStoreBCIRDNewAliquotUseCommonId() throws UUIDException,
			ParseException {
		bcrId.setShippingDate("this is a shipping date");
		bcrId.setUUID(null); // even if uuid is not set in the object, it should
								// find it in the db
		bcrId.setFullID("hi I am a barcode");
        bcrId.setArchiveId(123L);
		final Center bcrCenter = new Center();
		bcrCenter.setCenterId(2);
		context.checking(new Expectations() {
			{
				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						"hi I am a barcode", null, disease, bcrCenter, true);
				will(returnValue(makeBarcode("hi I am a barcode",
						"the-real-uuid")));

				one(mockBCRIDQueries).exists(bcrId);
				will(returnValue(-1)); // means doesn't exist

				one(mockBCRIDQueries).addBCRID(bcrId, true);
				will(returnValue(1234));

				// uses the id 33 that was passed in as a param to the
				// storeBcrBarcode
				one(mockBCRIDQueries).addArchiveRelationship(bcrId, true,
						new int[] { 33 });

				one(mockBCRIDQueries).updateBCRIDStatus(bcrId);
				one(mockBCRIDQueries).updateShipDate(bcrId);

			}
		});
		bcridProcessor.storeBcrBarcode(bcrId, true, new int[] { 33 }, disease,
				bcrCenter);
		assertEquals("the-real-uuid", bcrId.getUUID());
	}

	/*
	 * Covers case where no ship date is given.
	 */
	@Test
	public void testStoreBCRIDNoShipDate() throws ParseException, UUIDException {
		bcrId.setFullID("hi I am a barcode");
		bcrId.setUUID("b645a265-fe5a-4755-b548-2169ad6751f3");
		final Center bcrCenter = new Center();
		bcrCenter.setCenterId(6);
        bcrId.setArchiveId(456L);

		context.checking(new Expectations() {
			{
                one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
                        "hi I am a barcode",
                        "b645a265-fe5a-4755-b548-2169ad6751f3", disease,
                        bcrCenter, true);
                will(returnValue(makeBarcode("hi I am a barcode",
                        "b645a265-fe5a-4755-b548-2169ad6751f3")));

                one(mockBCRIDQueries).exists(bcrId);
                will(returnValue(-1)); // means doesn't exist
                one(mockBCRIDQueries).addBCRID(bcrId, false);
                will(returnValue(10)); // id
                one(mockBCRIDQueries).updateBCRIDStatus(bcrId);
                one(mockBCRIDQueries).addArchiveRelationship(bcrId, false,
                        new int[]{-1});

            }
        });
		bcridProcessor.storeBcrBarcode(bcrId, false, new int[]{-1}, disease,
                bcrCenter);
		assertEquals(new Integer(10), bcrId.getId());
	}

	/*
	 * Error case: conflict found by uuid resolver
	 */
	@Test(expected = UUIDException.class)
	public void testStoreBCRIDConflict() throws UUIDException, ParseException {
		bcrId.setFullID("tcga-barcode");
		bcrId.setUUID("i-am-a-uuid-i-was-programmed-to-identify");

		context.checking(new Expectations() {
			{
				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						with("tcga-barcode"),
						with("i-am-a-uuid-i-was-programmed-to-identify"),
						with(disease), with(any(Center.class)), with(true));
				will(throwException(new UUIDException("conflict!")));

				one(mockBCRIDQueries).exists(bcrId);
				will(returnValue(-1));

			}
		});

		bcridProcessor.storeBcrBarcode(bcrId, false, new int[]{-1}, disease,
                new Center());
	}

	/*
	 * Existing aliquot. UUID in uuid system. UUID not in XML.
	 */
	@Test
	public void testStoreBCRIDExisting() throws ParseException, UUIDException {
		final Integer biospecimenId = 30;
		bcrId.setFullID("hi I am a barcode");
		bcrId.setUUID(null);
		final Center bcrCenter = new Center();
		bcrCenter.setCenterId(80);
		bcrId.setUUID(null);
        bcrId.setArchiveId(999L);
		context.checking(new Expectations() {
			{
				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						"hi I am a barcode", null, disease, bcrCenter, true);
				will(returnValue(makeBarcode("hi I am a barcode", "hi!!!")));

				one(mockBCRIDQueries).exists(bcrId);
				will(returnValue(biospecimenId)); // means exists w/ID=30
				one(mockBCRIDQueries).updateBCRIDStatus(bcrId);
				one(mockBCRIDQueries).getBiospecimenUUID(bcrId);
				will(returnValue("hi!!!"));

				one(mockBCRIDQueries).addArchiveRelationship(bcrId, false,
						new int[] { -1 });
			}
		});
		bcridProcessor.storeBcrBarcode(bcrId, false, new int[] { -1 }, disease,
				bcrCenter);
		assertEquals(biospecimenId, bcrId.getId());
		assertEquals("hi!!!", bcrId.getUUID());
	}

	/*
	 * Existing aliquot. UUID in uuid system. UUID in XML.
	 */
	@Test
	public void testStoreBCRIDExistingWithUUID() throws ParseException,
			UUIDException {
		final Integer biospecimenId = 30;
		bcrId.setFullID("hi I am a barcode");
		final Center bcrCenter = new Center();
		bcrCenter.setCenterId(80);
		bcrId.setUUID("hi!!!");
        bcrId.setArchiveId(123L);
		context.checking(new Expectations() {
            {
                one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
                        "hi I am a barcode", "hi!!!", disease, bcrCenter, true);
                will(returnValue(makeBarcode("hi I am a barcode", "hi!!!")));

                one(mockBCRIDQueries).exists(bcrId);
                will(returnValue(biospecimenId)); // means exists w/ID=30

                one(mockBCRIDQueries).updateBCRIDStatus(bcrId);
                one(mockBCRIDQueries).getBiospecimenUUID(bcrId);
                will(returnValue("hi!!!"));

                one(mockBCRIDQueries).addArchiveRelationship(bcrId, false,
                        new int[]{-1});
            }
        });
		bcridProcessor.storeBcrBarcode(bcrId, false, new int[]{-1}, disease,
                bcrCenter);
		assertEquals(biospecimenId, bcrId.getId());
		assertEquals("hi!!!", bcrId.getUUID());
	}

	/*
	 * Case where uuid is registered to the barcode correctly, but biospecimen
	 * table has a different uuid Not expected to happen, but we still need to
	 * check for it
	 */
	@Test(expected = UUIDException.class)
	public void testBiospecimenUuidConflict() throws UUIDException,
			ParseException {
		final Integer biospecimenId = 30;
		bcrId.setFullID("hi I am a barcode");
		bcrId.setUUID(null);
		final Center bcrCenter = new Center();
		bcrCenter.setCenterId(80);
		bcrId.setUUID(null);
		context.checking(new Expectations() {
            {
                one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
                        "hi I am a barcode", null, disease, bcrCenter, true);
                will(returnValue(makeBarcode("hi I am a barcode", "hi!!!")));

                one(mockBCRIDQueries).exists(bcrId);
                will(returnValue(biospecimenId)); // means exists w/ID=30

                one(mockBCRIDQueries).getBiospecimenUUID(bcrId);
                will(returnValue("conflicting-uuid"));

            }
        });
		bcridProcessor.storeBcrBarcode(bcrId, false, new int[]{-1}, disease,
                bcrCenter);
	}

	@Test
	public void testAddBioSpecimenToFileAssociations() {

		final List<BiospecimenToFile> biospecimenToFileList = new ArrayList<BiospecimenToFile>();
		final BiospecimenToFile biospecimentToFile = new BiospecimenToFile();
		biospecimentToFile.setBiospecimenId(bcrId.getId());
		biospecimentToFile.setFileId(1L);
		biospecimenToFileList.add(biospecimentToFile);

		context.checking(new Expectations() {
            {
                one(mockBCRIDQueries).addBioSpecimenToFileAssociations(
                        biospecimenToFileList);
            }
        });
		bcridProcessor.addBioSpecimenToFileAssociations(biospecimenToFileList,
                disease);
	}

	@Test
	public void testAddBioSpecimenBarcodes() throws ParseException,
			UUIDException {

		final List<BCRID> bcrIdList = new ArrayList<BCRID>();
		final String validBarcode = "TCGA-AB-2802-03A-01T-0734-13";
		bcrIdList.add(bcridProcessor.parseAliquotBarcode(validBarcode));
		context.checking(new Expectations() {
            {
                one(mockBCRIDQueries)
                        .addBioSpecimenBarcodes(bcrIdList, disease);

            }
        });
		bcridProcessor.addBioSpecimenBarcodes(bcrIdList, disease);
	}

	@Test
	public void getBiospecimenIds() throws ParseException {

		final List<String> validBarcode = new ArrayList<String>();
		validBarcode.add("TCGA-AB-2802-03A-01T-0734-13");
		final List<Integer> bcrIds = new ArrayList<Integer>();
		bcrIds.add(1);

		context.checking(new Expectations() {
            {
                one(mockBCRIDQueries).getBiospecimenIds(validBarcode);
                will(returnValue(bcrIds));
            }
        });
		bcridProcessor.getBiospecimenIds(validBarcode);
	}

	/*
	 * add file association for a barcode that is in the db with a uuid
	 */
	@Test
	public void testAddFileAssociation() throws ParseException, UUIDException,
			Processor.ProcessorException {
		final int[] bcrAndBcrFileId = { 0, 0 };
		final String barcode = "TCGA-AB-2802-03A-01T-0734-13";
		final Long fileId = 1L;
		final Long archiveId = 1L;
		final String colName = "Test";
		bcrId = bcridProcessor.parseAliquotBarcode(barcode);
		bcrId.setUUID("b645a264-fe5a-4755-b548-2169ad6751f3");

		context.checking(new Expectations() {
			{
				one(mockBCRIDQueries).exists(bcrId);
				will(returnValue(-1));
				one(mockBCRIDQueries).addBCRID(bcrId, false);
				will(returnValue(10));
				one(mockBCRIDQueries).updateBCRIDStatus(bcrId);
				one(mockBCRIDQueries).findExistingAssociation(fileId, 10,
						colName);
				will(returnValue(0));
				one(mockBCRIDQueries).addFileAssociation(fileId, 10, colName,
						false, bcrAndBcrFileId[1]);
				will(returnValue(20));
				allowing(mockCenterQueries).getCenterIdForBCRCenter("13");
				will(returnValue(14));
				one(mockCenterQueries).getCenterById(14);
				will(returnCenterWithId());
				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						with(barcode),
						with("b645a264-fe5a-4755-b548-2169ad6751f3"),
						with(disease), with(any(Center.class)), with(false));
				will(returnValue(makeBarcode(barcode,
						"b645a264-fe5a-4755-b548-2169ad6751f3")));
			}
		});

		bcridProcessor.addFileAssociation(fileId, bcrId, colName, archiveId,
				false, bcrAndBcrFileId, disease);

		// Validate biospecimen id
		assertEquals(10, bcrAndBcrFileId[0]);
		// Validate biospecimen file id
		assertEquals(20, bcrAndBcrFileId[1]);
	}

	private static Action returnCenterWithId() {
		return new Action() {

			@Override
			public Object invoke(final Invocation invocation) throws Throwable {
				final Center center = new Center();
				center.setCenterId((Integer) invocation.getParameter(0));
				return center;
			}

			@Override
			public void describeTo(final Description description) {
				description.appendText("returns a Center with center ID set");
			}
		};
	}

	@Test
	public void testAddFileAssociationUseIdFromCommon() throws ParseException,
			Processor.ProcessorException, UUIDException {
		final String barcode = "TCGA-AB-2802-03A-01T-0734-13";
		bcrId = bcridProcessor.parseAliquotBarcode(barcode);
		context.checking(new Expectations() {
			{
				one(mockBCRIDQueries).exists(bcrId);
				will(returnValue(100));
				one(mockBCRIDQueries).getBiospecimenUUID(bcrId);
				will(returnValue(null));
				one(mockBCRIDQueries).findExistingAssociation(1234L, 100,
						"testCol");
				will(returnValue(0));
				one(mockBCRIDQueries).addFileAssociation(1234L, 100, "testCol",
						true, 20);
				will(returnValue(20));
				one(mockBCRIDQueries).updateUUIDForBarcode(bcrId);
				one(mockCenterQueries).getCenterIdForBCRCenter("13");
				will(returnValue(18));
				one(mockCenterQueries).getCenterById(18);
				will(returnCenterWithId());
				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						with(barcode), with(any(String.class)), with(disease),
						with(any(Center.class)), with(false));
				will(returnValue(makeBarcode(barcode,
						"b645a264-fe5a-4755-b548-2169ad6751f3")));
			}
		});
		bcridProcessor.addFileAssociation(1234L, bcrId, "testCol", 5678L, true,
				new int[] { 10, 20 }, disease);
		assertEquals("b645a264-fe5a-4755-b548-2169ad6751f3", bcrId.getUUID());
	}

	@Test(expected = UUIDException.class)
	public void testAddFileAssociationUuidConflict() throws ParseException,
			Processor.ProcessorException, UUIDException {
		final String barcode = "TCGA-AB-2802-03A-01T-0734-13";
		bcrId = bcridProcessor.parseAliquotBarcode(barcode);
		context.checking(new Expectations() {
			{
				one(mockBCRIDQueries).exists(bcrId);
				will(returnValue(100));
				one(mockBCRIDQueries).getBiospecimenUUID(bcrId);
				will(returnValue("conflicting-uuid!"));

				one(mockCenterQueries).getCenterIdForBCRCenter("13");
				will(returnValue(18));
				one(mockCenterQueries).getCenterById(18);
				will(returnCenterWithId());
				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						with(barcode), with(any(String.class)), with(disease),
						with(any(Center.class)), with(false));
				will(returnValue(makeBarcode(barcode,
						"b645a264-fe5a-4755-b548-2169ad6751f3")));
			}
		});
		bcridProcessor.addFileAssociation(1234L, bcrId, "testCol", 5678L, true,
				new int[] { 10, 20 }, disease);
	}

    @Test
    public void storeNewBarcode() throws ParseException,UUIDException{
        String barcode = "TCGA-11-3333-33A-44W-5555-66";
        final BCRID bcrID = bcridProcessor.parseAliquotBarcode(barcode);
        bcrID.setArchiveId(150L);
        bcrID.setBcrCenterId("01");
        final Barcode barodeAndUUID = new Barcode();
        barodeAndUUID.setUuid("uuid");
        context.checking(new Expectations() {
            {
                one(mockBCRIDQueries).exists(bcrID);
                will(returnValue(-1));
                one(mockCenterQueries).getCenterIdForBCRCenter(bcrID.getBcrCenterId());
                will(returnValue(1));
                one(mockCenterQueries).getCenterById(1);
                will(returnValue(center));
                one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(bcrID.getFullID(), bcrID.getUUID(), disease, center, false);
                will(returnValue(barodeAndUUID));
                one(mockBCRIDQueries).addBCRID(bcrID,true);
                one(mockBCRIDQueries).updateBCRIDStatus(bcrID);
            }
		});
        bcridProcessor.storeBarcode(bcrID, true, -1, disease);
    }

    @Test(expected = UUIDException.class)
    public void storeExistingBarcodeWithNewUUID() throws ParseException,UUIDException{
        final String barcode = "TCGA-11-3333-33A-44W-5555-66";
        final String uuid = "newuuid";
        final BCRID bcrID = bcridProcessor.parseAliquotBarcode(barcode);
        bcrID.setArchiveId(150L);
        bcrID.setBcrCenterId("01");
        final Barcode barodeAndUUID = new Barcode();
        barodeAndUUID.setUuid(uuid);
        context.checking(new Expectations() {
            {
                one(mockBCRIDQueries).exists(bcrID);
                will(returnValue(100));
                one(mockCenterQueries).getCenterIdForBCRCenter(bcrID.getBcrCenterId());
                will(returnValue(1));
                one(mockCenterQueries).getCenterById(1);
                will(returnValue(center));
                one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(bcrID.getFullID(), bcrID.getUUID(), disease, center, false);
                will(returnValue(barodeAndUUID));
                one(mockBCRIDQueries).getBiospecimenUUID(bcrID);
                will(returnValue("uuid"));
            }
		});
        bcridProcessor.storeBarcode(bcrID, true, -1, disease);

    }

    @Test(expected = UUIDException.class)
    public void storeBarcodeWithoutUUID() throws ParseException,UUIDException{
        final String barcode = "TCGA-11-3333-33A-44W-5555-66";
        final String uuid = "newuuid";
        final BCRID bcrID = bcridProcessor.parseAliquotBarcode(barcode);
        bcrID.setArchiveId(150L);
        bcrID.setBcrCenterId("01");
        final Barcode barodeAndUUID = new Barcode();
        barodeAndUUID.setUuid(uuid);
        context.checking(new Expectations() {
            {
                one(mockBCRIDQueries).exists(bcrID);
                will(returnValue(100));
                one(mockCenterQueries).getCenterIdForBCRCenter(bcrID.getBcrCenterId());
                will(returnValue(1));
                one(mockCenterQueries).getCenterById(1);
                will(returnValue(center));
                one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(bcrID.getFullID(), bcrID.getUUID(), disease, center, false);
                will(throwException(new UUIDException("UUID not found")));
            }
		});
        bcridProcessor.storeBarcode(bcrID, true, -1, disease);

    }

    @Test(expected = ParseException.class)
	public void invalidBarcode() throws ParseException {

		final String barcode = "TCGA002-0001-01C-01R-0178-04";
        BCRID bcrID = bcridProcessor.parseAliquotBarcode(barcode);
	}

    @Test
    public void getBiospecimenIdForUUID(){
        final String uuid = "f9a723ff-3715-4a3c-972b-b329825f4f5f";
        context.checking(new Expectations() {
            {
                one(mockBCRIDQueries).getBiospecimenIdForUUID(uuid);
                will(returnValue(1000l));
            }
		});
        assertNotNull(bcridProcessor.getBiospecimenIdForUUID(uuid));

    }

    @Test
     public void getBiospecimenIdForInvalidUUID(){
         final String uuid = "f9a723ff-3715-4a3c-972b-b329825f4f5f";
         context.checking(new Expectations() {
             {
                 one(mockBCRIDQueries).getBiospecimenIdForUUID(uuid);
                 will(returnValue(null));
             }
         });
         assertNull(bcridProcessor.getBiospecimenIdForUUID(uuid));

    }

    @Test
    public void existingSlideBarcode(){
        final String barcode = "TCGA-02-0000-01A-01W-1111-66";
        context.checking(new Expectations() {
            {
                one(mockBCRIDQueries).slideBarcodeExists(barcode);
                will(returnValue(true));
            }
		});
        assertTrue(bcridProcessor.slideBarcodeExists(barcode));

    }

    @Test
    public void newSlideBarcode(){
        final String barcode = "TCGA-02-0000-01A-01W-1111-66";
        context.checking(new Expectations() {
            {
                one(mockBCRIDQueries).slideBarcodeExists(barcode);
                will(returnValue(false));
            }
		});
        assertFalse(bcridProcessor.slideBarcodeExists(barcode));

    }

    private static Matcher<List<ShippedBiospecimen>> listOfExpectedShippedBiospecimens(final List<String> barcodes, final List<String> uuids) {
        return new TypeSafeMatcher<List<ShippedBiospecimen>>() {
            @Override
            public boolean matchesSafely(final List<ShippedBiospecimen> shippedBiospecimens) {
                int index = 0;
                for (final ShippedBiospecimen shippedBiospecimen : shippedBiospecimens) {
                    if (! shippedBiospecimen.getUuid().equals(uuids.get(index))) {
                        fail("Expected UUID " + uuids.get(index) + " but found " + shippedBiospecimen.getUuid());
                    }
                    if (! shippedBiospecimen.getBarcode().equals(barcodes.get(index))) {
                        fail("Expected barcode " + barcodes.get(index) + " but found " + shippedBiospecimen.getBarcode());
                    }
                    index++;
                }
                return shippedBiospecimens.size() == barcodes.size();
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("expected biospecimens match");
            }
        };
    }

}
