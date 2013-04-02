/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class BCRXMLFileReader {

    private File bcrXML;
    private final File bcrXSD;
    private Document document;
    private HashSet<String> bcrIDs = null;
    private boolean validate = true;
    private String bcrStr;

    /**
     * @param bcrXML the XML document containing the BCR Data
     * @param bcrXSD the XSD document which matches the intended structure of the bcrXML parameter
     * @throws IOException                  If the document is not found
     * @throws ParserConfigurationException if there is a problem with the parser
     * @throws org.xml.sax.SAXException     if there is any kind of SAX Exception
     */
    public BCRXMLFileReader( File bcrXML, File bcrXSD ) throws IOException, ParserConfigurationException, SAXException {
        this.bcrXML = bcrXML;
        this.bcrXSD = bcrXSD;
        processXMLFile();
    }

    public BCRXMLFileReader( String bcrStr,
                             File bcrXSD ) throws SAXException, IOException, ParserConfigurationException {
        this.bcrStr = bcrStr;
        this.bcrXSD = bcrXSD;
        processXMLString();
    }

    public BCRXMLFileReader( final File bcrXML, final File bcrXSD,
                             final boolean validate ) throws IOException, ParserConfigurationException, SAXException {
        this.bcrXML = bcrXML;
        this.bcrXSD = bcrXSD;
        this.validate = validate;
        processXMLFile();
    }

    private void processXMLFile() throws SAXException, ParserConfigurationException, IOException {

        FileInputStream fileInputStream = null;
        try {
            //noinspection IOResourceOpenedButNotSafelyClosed
            fileInputStream = new FileInputStream(bcrXML);
            processXML(fileInputStream);
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    private void processXMLString() throws SAXException, ParserConfigurationException, IOException {
        processXML( new ByteArrayInputStream( bcrStr.getBytes() ) );
    }

    private void processXML( final InputStream in ) throws SAXException, ParserConfigurationException, IOException {
// Some of the following code is nabbed from http://www.ibm.com/developerworks/xml/library/x-javaxmlvalidapi.html
        final SchemaFactory factory
                = SchemaFactory.newInstance( "http://www.w3.org/2001/XMLSchema" );
        final File schemaLocation = bcrXSD;
        final Schema schema;
        schema = factory.newSchema( schemaLocation );
        final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware( true ); // never forget this
        final DocumentBuilder builder = domFactory.newDocumentBuilder();
        document = builder.parse( in );
        final DOMSource source = new DOMSource( document );
        final Validator validator = schema.newValidator();
        //Check to see if we actually want to validate docs or just skip.
        if(isValidate()) {
            validator.validate( source );
        }
    }

    public Set<String> getBCRIDs() {
        //BCRSAMPLEBARCODE is BCRID
        if(bcrIDs == null) {
            bcrIDs = new HashSet<String>();
            final NodeList nl = document.getElementsByTagName( "BCRSAMPLEBARCODE" );
            for(int i = 0; i < nl.getLength(); i++) {
                final Node n = nl.item( i );
                bcrIDs.add( n.getTextContent() );
            }
        }
        return bcrIDs;
    }

    public boolean isValidate() {
        return validate;
    }

    public void setValidate( final boolean validate ) {
        this.validate = validate;
    }

    public Document getDocument() {
        return document;
    }
}
