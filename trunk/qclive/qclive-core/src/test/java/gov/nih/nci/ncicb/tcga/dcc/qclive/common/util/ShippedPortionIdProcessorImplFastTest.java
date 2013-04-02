/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Test class for ShippedPortionIdProcessorImpl.
 * 
 * @author Deepak Srinivasan Last updated by: $Author$
 * @version $Rev$
 */
public class ShippedPortionIdProcessorImplFastTest {
	private static final String SAMPLES_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;
	private final String testDir = SAMPLES_DIR
			+ "qclive/shippedPortionIdProcessor";
	private final String shippedPortionFileName = "/shipped_portions_2.4.xml";
	private final String shippedPortionFileNameNoElement = "/no_shipped_portions_2.4.xml";
	final ShippedPortionIdProcessorImpl processor = new ShippedPortionIdProcessorImpl();

	@Test
	public void testGetTextElementUuidGood() throws TransformerException,
			IOException, SAXException, XPathExpressionException,
			ParserConfigurationException {
		Collection<String> uuids = processor.getTextElements(new File(testDir
				+ shippedPortionFileName), "//portions/shipment_portion",
				"bcr_shipment_portion_uuid");
		assertTrue(uuids.contains("b838468b-dfd5-49d4-bd7e-e74266201951"));
		assertTrue(uuids.contains("32f953f4-8d4f-4c18-9b09-c18a511eda6f"));
		assertTrue(uuids.contains("D573317C-0B6E-475D-B950-237809987AA9"));
		assertEquals(3, uuids.size());
	}

	@Test
	public void testGetTextElementCenterIdGood() throws TransformerException,
			IOException, SAXException, XPathExpressionException,
			ParserConfigurationException {
		Collection<String> centerIds = processor.getTextElements(new File(
				testDir + shippedPortionFileName),
				"//portions/shipment_portion", "center_id");
		assertTrue(centerIds.contains("1"));
		assertTrue(centerIds.contains("20"));
		assertEquals(2, centerIds.size());
	}

	@Test
	public void testGetTextElementBcrAliquotBarcodeGood()
			throws TransformerException, IOException, SAXException,
			XPathExpressionException, ParserConfigurationException {
		Collection<String> bcrAliquotBarcodes = processor.getTextElements(
				new File(testDir + shippedPortionFileName),
				"//portions/shipment_portion",
				"shipment_portion_bcr_aliquot_barcode");
		assertTrue(bcrAliquotBarcodes.contains("TCGA-A3-3308-01A-03-1234-20"));
		assertTrue(bcrAliquotBarcodes.contains("TCGA-06-0208-01A-01-0231-02"));
		assertTrue(bcrAliquotBarcodes.contains("TCGA-A1-A0SH-01A-21-A13A-20"));
		assertEquals(3, bcrAliquotBarcodes.size());
	}

	@Test
	public void testShippedPortionExists() throws TransformerException,
			IOException, SAXException, XPathExpressionException,
			ParserConfigurationException {
		processor.setShipmentPortionPath("//portions/shipment_portion");
		assertTrue(processor.shippedPortionExists(new File(testDir
				+ shippedPortionFileName)));
	}

	@Test
	public void testShippedPortionDoesNotExist() throws TransformerException,
			IOException, SAXException, XPathExpressionException,
			ParserConfigurationException {
		processor.setShipmentPortionPath("//portions/shipment_portion");
		assertFalse(processor.shippedPortionExists(new File(testDir
				+ shippedPortionFileNameNoElement)));
	}

	@Test
	public void testGetTextElementParentDoesNotExist()
			throws TransformerException, IOException, SAXException,
			XPathExpressionException, ParserConfigurationException {
		Collection<String> emptyList = processor.getTextElements(new File(
				testDir + shippedPortionFileName), "//foo",
				"shipment_portion_bcr_aliquot_barcode");
		assertTrue(emptyList.isEmpty());
	}

	@Test
	public void testGetElementDoesNotExist() throws TransformerException,
			IOException, SAXException, XPathExpressionException,
			ParserConfigurationException {
		Collection<String> emptyList = processor.getTextElements(new File(
				testDir + shippedPortionFileName),
				"//portions/shipment_portion", "foo");
		assertTrue(emptyList.isEmpty());
	}
}
