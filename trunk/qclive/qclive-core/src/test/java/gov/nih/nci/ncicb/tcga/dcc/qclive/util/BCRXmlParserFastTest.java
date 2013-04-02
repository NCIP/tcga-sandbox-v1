/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.util;

import gov.nih.nci.ncicb.tcga.dcc.common.util.XPathXmlParser;
import gov.nih.nci.ncicb.tcga.dcc.common.util.XmlParser;

import java.io.File;

import javax.xml.xpath.XPathExpression;

import junit.framework.TestCase;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Robert S. Sfeir Class to test the XML parser which uses xpath to get
 *         at Aliquot information and perhaps other information later
 */
public class BCRXmlParserFastTest extends TestCase {

	private static final String SAMPLE_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;

	public void testReadBCRXmlDoc() throws Exception {
		final XmlParser parser = new XPathXmlParser();
		// Get the main node list.
		final NodeList nodes = parser.getNodes(
				parser.parseXmlFile(SAMPLE_DIR
						+ "qclive/intgen.org_full.TCGA-06-0119.xml", false),
				"//ALIQUOT");
		// Compile other expressions for use:
		final XPathExpression shipDate = parser
				.compileXpathExpression("//SHIPPINGDATE");
		final XPathExpression barCode = parser
				.compileXpathExpression("//BCRALIQUOTBARCODE");
		assertEquals(10, nodes.getLength());
		for (int i = 0; i < nodes.getLength(); i++) {
			// Get element
			final Element elem = (Element) nodes.item(i);
			assertEquals("ALIQUOT", elem.getNodeName());
			assertTrue(elem.hasChildNodes());
			final NodeList childNodes = elem.getChildNodes();
			// We do the inner ifs to get the right tag, we also add another if
			// to check that we are getting the right pairs
			// if the i==0 check fails it means that the test file changed or
			// that we're not getting the right pair of values expected from
			// the first ALIQUIT node in the note list.
			for (int cNode = 0; cNode < childNodes.getLength(); cNode++) {
				if (childNodes.item(cNode).getNodeName().equals("SHIPPINGDATE")) {
					// System.out.println("Shipping is: " +
					// childNodes.item(cNode).getTextContent().trim());
					if (i == 0) {
						assertEquals("2007-06-28 00:00:00.0",
								childNodes.item(cNode).getTextContent().trim());
					}
				}
				if (childNodes.item(cNode).getNodeName()
						.equals("BCRALIQUOTBARCODE")) {
					// System.out.println("Barcode is: " +
					// childNodes.item(cNode).getTextContent().trim());
					if (i == 0) {
						assertEquals("TCGA-06-0119-01A-08D-0214-01", childNodes
								.item(cNode).getTextContent().trim());
					}
				}
			}
		}
	}
}