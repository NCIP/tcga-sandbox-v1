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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;

/**
 * @author Robert S. Sfeir
 * @author Julien Baboud
 */
public interface XmlParser {

    NodeList getNodes(Document doc, String xPathExpression) throws XPathExpressionException, TransformerException;

    Document parseXmlFile(String filename,
                          boolean validating) throws ParserConfigurationException, IOException, SAXException;

    public Document parseXmlFile(final File file,
                                 final boolean validating) throws ParserConfigurationException, IOException, SAXException;

    XPathExpression compileXpathExpression(String expression) throws XPathExpressionException;

    /**
     * Dump the content of an W3C XML Document into a file.
     *
     * @param document       the document to dump
     * @param outputFilename the output filename
     * @throws TransformerException
     */
    public void dumpDocument(final Document document, final String outputFilename) throws TransformerException;

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
            throws IOException, ParserConfigurationException, SAXException, TransformerException;
}
