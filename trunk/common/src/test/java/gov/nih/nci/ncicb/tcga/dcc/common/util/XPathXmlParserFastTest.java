/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author Julien Baboud
 *
 * This class is used to be able to easely convert a set of XML files so that their content
 * is the same as what we would get by dumping the W3C DOM Document resulting from these files into
 * a new file
 */
public class XPathXmlParserFastTest {
	
    private static final String SAMPLE_FILE_LOCATION = 
    	Thread.currentThread().getContextClassLoader().getResource("samples/xml").getPath() + File.separator;

    private XPathXmlParser xPathXmlParser;

    @Before
    public void setUp() {
        xPathXmlParser = new XPathXmlParser();
    }

    /**
     * This test has been written to simplify the task of "normalizing" XML documents
     * but is not intended to be run each time. It can be safely ignored.
     * 
     * @throws Exception
     */
    @Test
    @Ignore
    public void test() throws Exception {

        final String pathSeparator = File.separator;
        final String fromDir = pathSeparator + "Download" + pathSeparator + "tcgaArchives" + pathSeparator + "prod" + pathSeparator + "intgen.org_OV.bio.Level_1.9.11.0";
        final String toDir = pathSeparator + "Download" + pathSeparator + "tcgaArchives" + pathSeparator + "prod" + pathSeparator + "intgen.org_OV.bio.Level_1.9.11.0" + pathSeparator + "normalized";

        XPathXmlParser xPathXmlParser = new XPathXmlParser();
        xPathXmlParser.traverseDirectoryToParseAndDumpXMLFiles(fromDir, toDir);
    }

    /**
     * This test has been written to simplify the task of comparing XML files in 2 local directories
     * but is not intended to be run each time. It can be safely ignored.
     *
     * @throws IOException
     */
    @Test
    @Ignore
    public void diff() throws IOException {

        final String pathSeparator = File.separator;
        final String prodDirPath = pathSeparator + "Download" + pathSeparator + "tcgaArchives" + pathSeparator + "dev" + pathSeparator + "intgen.org_OV.bio.Level_1.9.11.0" + pathSeparator + "normalized";
        final String devDirPath = pathSeparator + "Download" + pathSeparator + "tcgaArchives" + pathSeparator + "prod" + pathSeparator + "intgen.org_OV.bio.Level_1.9.11.0" + pathSeparator + "normalized";

        //final String fromDirectoryPath, final String toDirectoryPath
        File prodDir = new File(prodDirPath);
        if(!prodDir.isDirectory()) {
            throw new FileNotFoundException("Not a valid directory: " + prodDirPath);
        }

        File devDir = new File(devDirPath);
        if(!devDir.isDirectory()) {
            throw new FileNotFoundException("Not a valid directory: " + devDirPath);
        }

        String[] xmlFiles = prodDir.list(new XMLFileFilter("xml"));
        for(String xmlFile : xmlFiles) {

            final File prodFile = new File(prodDirPath + pathSeparator + xmlFile);
            final File devFile = new File(devDirPath + pathSeparator + xmlFile);

            if(!prodFile.exists()) {
                throw new FileNotFoundException("File does not exist: " + prodFile.getAbsolutePath());
            } else if(!devFile.exists()) {
                throw new FileNotFoundException("File does not exist: " + devFile.getAbsolutePath());
            } else {
                Assert.assertEquals(FileUtil.readFile(devFile, false), FileUtil.readFile(prodFile, false));
            }
        }
    }

    @Test
    public void testPrefixResolver() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        // XML with namespaces defines in document element
        final Document document = xPathXmlParser.parseXmlFile(new File(SAMPLE_FILE_LOCATION + "simple_namespace.xml"), false, true);
        NodeList nodeList = (NodeList) xPathXmlParser.getXPath().evaluate("//bios:aBiosNode", document, XPathConstants.NODESET);
        assertEquals(1, nodeList.getLength());
        assertEquals("bye", nodeList.item(0).getTextContent());

        nodeList = (NodeList) xPathXmlParser.getXPath().evaluate("//admin:anAdminNode", document, XPathConstants.NODESET);
        assertEquals(1, nodeList.getLength());
        assertEquals("hi", nodeList.item(0).getTextContent());
    }

    @Test
    public void testPrefixResolverChild() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        // XML with some namespaces in document element and some in child node
        final Document document = xPathXmlParser.parseXmlFile(new File(SAMPLE_FILE_LOCATION + "complex_namespace.xml"), false, true);
        NodeList nodeList = (NodeList) xPathXmlParser.getXPath().evaluate("//bios:aBiosNode", document, XPathConstants.NODESET);
        assertEquals(1, nodeList.getLength());
        assertEquals("bye", nodeList.item(0).getTextContent());

        nodeList = (NodeList) xPathXmlParser.getXPath().evaluate("//admin:anAdminNode", document, XPathConstants.NODESET);
        assertEquals(1, nodeList.getLength());
        assertEquals("hi", nodeList.item(0).getTextContent());
    }
}
