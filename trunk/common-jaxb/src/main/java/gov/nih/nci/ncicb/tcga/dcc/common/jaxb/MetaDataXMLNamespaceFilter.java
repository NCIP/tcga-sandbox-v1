/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.jaxb;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

import java.util.HashSet;
import java.util.Set;

/**
 * Custom class that can be used by SAX parsers to intercept namespace events within XML
 * instances of the TCGA_BCR.Metadata XSD.
 *
 * @author Matt Nicholls
 *         Last updated by: nichollsmc
 * @version $Rev: $
 */
public class MetaDataXMLNamespaceFilter extends XMLFilterImpl {
	
	private static final String BCR_METADATA_NS_URI = "http://tcga.nci/bcr/xml/metadata";
    private static final String BCR_BIOSPECIMEN_NS_URI = "http://tcga.nci/bcr/xml/biospecimen";
	private static final String ROOT_ELEMENT_NAME = "tcga_bcr";
	private static final Set<String> ELEMENT_FILTER_NAMES = new HashSet<String>();
	static {
		ELEMENT_FILTER_NAMES.add("bcr_patient_barcode");
		ELEMENT_FILTER_NAMES.add("bcr_patient_uuid");
		ELEMENT_FILTER_NAMES.add("drugs");
		ELEMENT_FILTER_NAMES.add("drug");
		ELEMENT_FILTER_NAMES.add("radiations");
		ELEMENT_FILTER_NAMES.add("radiation");
	}
	
	private String xmlInstanceNSURI;
	
	/**
	 * Constructor used to create an instance of {@link MetaDataXMLNamespaceFilter} with a specified
	 * {@link XMLReader} implementation.
	 * 
	 * @param xmlReader - an implementation of the {@link XMLReader} interface
	 */
	public MetaDataXMLNamespaceFilter(final XMLReader xmlReader) {
		super(xmlReader);
	}
	
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) 
    	throws SAXException {
    	
    	if(ROOT_ELEMENT_NAME.equals(localName)) {
    		this.xmlInstanceNSURI = uri;
    		super.startElement(BCR_METADATA_NS_URI, localName, qName, attributes);
    	}
    	else if(xmlInstanceNSURI.equals(uri) || uri.contains(BCR_BIOSPECIMEN_NS_URI) || ELEMENT_FILTER_NAMES.contains(localName)) {
    		super.startElement(BCR_METADATA_NS_URI, localName, qName, attributes);
    	}
    	else {
    		super.startElement(uri, localName, qName, attributes);
    	}
    }
}
