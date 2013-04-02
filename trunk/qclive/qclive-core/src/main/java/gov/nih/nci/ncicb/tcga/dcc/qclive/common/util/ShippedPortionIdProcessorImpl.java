/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import gov.nih.nci.ncicb.tcga.dcc.common.util.XPathXmlParser;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Interface for shipped portion id processing
 *
 * @author Deepak Srinivasan
 *         Last updated by: $Author$
 * @version $Rev: 3419 $
 */
public class ShippedPortionIdProcessorImpl implements ShippedPortionIdProcessor {
    private String shipmentPortionPath;

    /**
     * Retrieve the text elements specified by the elementName as it occurs under the parent xpath in the xml file.
     * @param file the xml file to parse
     * @param parentXPath the parent xpath expression to search for elements
     * @param elementName the element to find within the list of nodes returned by the xpath expression
     * @return Collection<String> a collection of distinct element values found within the file
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws TransformerException
     * @throws XPathExpressionException
     */
    @Override
    public Collection<String> getTextElements(final File file, final String parentXPath, final String elementName) throws IOException, SAXException, ParserConfigurationException, TransformerException, XPathExpressionException {
        final Set<String> textElems = new HashSet<String>();
        final XPathXmlParser parser = new XPathXmlParser();

        final NodeList nodes = parser.getNodes(parser.parseXmlFile(file, false, false), parentXPath);
        if (nodes != null) {
            for (int i = 0; i < nodes.getLength(); i++) {
                // Get element
                final Element shipmentPortionElement = (Element)nodes.item(i);
                final NodeList childNodes = shipmentPortionElement.getChildNodes();
                if(childNodes != null) {
                    for(int j = 0; j < childNodes.getLength(); j++) {
                        if(childNodes.item(j).getNodeName().indexOf(elementName) >= 0) {
                            String text = childNodes.item(j).getTextContent().trim();
                            textElems.add(text);
                        }
                    }
                }
            }
        }
        return textElems;
    }

    /**
     * Checks the input XML ile for the existence shipment_portion element
     * @param file The XML file to check for the shipment_portion element
     * @return Boolean true if the shipment_portion element exists in the xml file
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws TransformerException
     * @throws XPathExpressionException
     */
    @Override
    public Boolean shippedPortionExists(final File file) throws IOException, SAXException, ParserConfigurationException, TransformerException, XPathExpressionException {
        final XPathXmlParser parser = new XPathXmlParser();
        final NodeList nodes = parser.getNodes(parser.parseXmlFile(file, false, false), shipmentPortionPath);
        return (nodes != null && nodes.getLength() > 0);
    }

    /**
     * @return String The shipment_portion xpath element
     */
    public String getShipmentPortionPath() {
        return shipmentPortionPath;
    }

    /**
     * @param shipmentPortionPath The shipment_portion xpath element
     */
    public void setShipmentPortionPath(final String shipmentPortionPath) {
        this.shipmentPortionPath = shipmentPortionPath;
    }

}
