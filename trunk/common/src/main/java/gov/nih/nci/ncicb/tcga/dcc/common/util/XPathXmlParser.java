/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Robert S. Sfeir
 *         Class to parse XML documents using XPath
 */
public class XPathXmlParser implements XmlParser {

    private XPath xPath;

    public XPathXmlParser() {
        final XPathFactory factory = XPathFactory.newInstance();
        xPath = factory.newXPath();
    }

    public XPath getXPath() {
        return xPath;
    }

    public NodeList getNodes(final Document doc,
                             final String xPathExpression) throws TransformerException {
        try {
            return (NodeList) xPath.evaluate(xPathExpression, doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            // probably means the namespace was not found, so just return null
            return null;
        }
    }

    public XPathExpression compileXpathExpression(final String expression) throws XPathExpressionException {
        return getXPath().compile(expression);
    }

    public Document parseXmlFile(final String filename,
                                 final boolean validating) throws ParserConfigurationException, IOException, SAXException {
        return parseXmlFile(new File(filename), validating);
    }

    public Document parseXmlFile(final File file,
                                 final boolean validating) throws ParserConfigurationException, IOException, SAXException {
        return parseXmlFile(file, validating, true);
    }

    public Document parseXmlFile(final File file,
                                 final boolean validating,
                                 final boolean namespaceAware) throws ParserConfigurationException, IOException, SAXException {
        // Create a builder factory
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(validating);
        factory.setNamespaceAware(namespaceAware);
        // Create the builder and parse the file
        final Document document = factory.newDocumentBuilder().parse(file);
        if (namespaceAware) {
            final NamespaceContext namespaceContext = new UniversalNamespaceCache(document, false);
            xPath.setNamespaceContext(namespaceContext);
        }
        return document;
    }

    /**
     * Dump the content of an W3C XML Document into a file.
     *
     * @param document       the document to dump
     * @param outputFilename the output filename
     * @throws TransformerException
     */
    public void dumpDocument(final Document document, final String outputFilename) throws TransformerException {

        final TransformerFactory tFactory = TransformerFactory.newInstance();
        final Transformer transformer = tFactory.newTransformer();

        final DOMSource source = new DOMSource(document);
        final StreamResult result = new StreamResult(new File(outputFilename));

        //Dumping DOM content
        transformer.transform(source, result);
    }

    /**
     * This methods will look at a given directory for any XML files,
     * will parse them and dump the resulting W3C Documents into a new directory.
     * <p/>
     * This will ensure that we can compare XML documents from archives without having
     * to worry about the order of attributes in each elements since the DOM API
     * automatically sorts them in alphabetical order.
     *
     * @param fromDirectoryPath the directory in which to find the XML files
     * @param toDirectoryPath   the directory in which to write the resulting XML files
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerException
     */
    public void traverseDirectoryToParseAndDumpXMLFiles(final String fromDirectoryPath, final String toDirectoryPath)
            throws IOException, ParserConfigurationException, SAXException, TransformerException {

        //Check args validity
        File fromDirectory = new File(fromDirectoryPath);
        if (!fromDirectory.isDirectory()) {
            throw new FileNotFoundException("Not a valid directory: " + fromDirectoryPath);
        }

        File toDirectory = new File(toDirectoryPath);
        if (!toDirectory.exists()) {

            //Create the toDirectory
            boolean success = toDirectory.mkdir();
            if (!success) {
                throw new IOException("Could not create directory: " + toDirectory);
            }
        }

        //Iterate on each XML files in the fromDirectoryPath directory
        //and dump the resulting W3C DOM Document into the toDirectoryPath directory
        File[] xmlFiles = fromDirectory.listFiles(new XMLFileFilter("xml"));
        final boolean validating = false;
        for (File xmlFile : xmlFiles) {

            //Parse XML
            final Document document = parseXmlFile(xmlFile.getAbsolutePath(), validating);

            //Dump Document
            dumpDocument(document, toDirectoryPath + File.separator + xmlFile.getName());
        }
    }
}
