package getmeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
	/**
	 * GetMetaFromXSD - class to slurp the DCC "metadata elements" (i.e., the biospecimen data elements
	 * associated with the TCGA barcode) from BCR XML Schema documents.
	 * This is based on a metadata tagging system within the XML Schema itself, and does not
	 * require attributes to be applied in the XML instance documents.
	 * @author jensenma
	 *
	 */
public class GetMetaFromXSD {
	private static final String XMLSCHEMA_URI = "http://www.w3.org/2001/XMLSchema";
	private static final String METADATA_TAG_ATTRIBUTE_NAME = "is_uuid_associated_metadata";
	private static String inXsElement = "";
	private String uri = "";
    SAXParser parser;
	XMLReader xmlReader;
	private ArrayList<String> metadataTags = new ArrayList<String>();
	
	public GetMetaFromXSD() {}
	/**
	 * parseFile - parse an XSD file on the local filesystem with the SAX parser to slurp metadata elements
	 * @param filename
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	public final void parseFile(final String filename) throws SAXException, ParserConfigurationException, IOException {
		this.uri = convertToFileURL(filename);
		parseURI();
	}
	
	/**
	 * parseURI - parse an XSD file using a URL with the SAX parser to slurp metadata elements
	 * @param uri
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */

	public final void parseURI(final String uri) throws SAXException, ParserConfigurationException, IOException {
		this.uri = uri;
		parseURI();
	}


	/**
	 * hasMetadata - ask whether a parsed XSD contains metadata elements
	 * @return
	 */
	public final boolean hasMetadata() {
		return metadataTags.size() > 0;
	}
	
	/**
	 * numberOfMetadataElements - get the number of metadata elements in a parsed XSD
	 * @return
	 */
	public final int numberOfMetadataElements() {
		return metadataTags.size();
	}
	
	/**
	 * getMetadataTags - retrieve the ArrayList<String> of metadata element tags in a parsed XSD
	 * @return
	 */
	public final ArrayList<String> getMetadataTags() {
		return metadataTags;
	}

	public final String getFilename() {
		return uri;
	}

	private static String convertToFileURL(final String filename) {
		// swiped from http://download.oracle.com/javase/tutorial/jaxp/sax/parsing.html
	    String path = new File(filename).getAbsolutePath();
	    if (File.separatorChar != '/') {
	        path = path.replace(File.separatorChar, '/');
	    }
	    if (!path.startsWith("/")) {
	        path = "/" + path;
	    }
	    return "file:" + path;
	}

	private void parseURI() throws SAXException, ParserConfigurationException, IOException {
		final SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		this.parser = factory.newSAXParser();
		this.xmlReader = parser.getXMLReader();
		xmlReader.setContentHandler(new handleContent());
		xmlReader.parse(this.uri);
	}

	private class handleContent extends DefaultHandler {
		// the business end of this class -- looks for the 
		// <xs:metadata name="[METADATA_TAG_ATTRIBUTE_NAME]" .../> attribute declarations in the 
		// element definitions in the (Biospecimen) XSDs
		public void startDocument() throws SAXException {
		}
		public void startElement(String namespaceURI, String localName, String qName,
								 Attributes atts) throws SAXException {
			// set inXsElement to the element name, if we are in an element definition (not a reference)
			String nameValue = atts.getValue("name");
			if (nameValue != null) {
				// if we are picking up a new element definition, set the inXsElement flag
				if (localName.equals("element") && nameValue.length()>0) {
					inXsElement = nameValue;
				}
				// if we see an attribute declaration named 'metadata' and its value is expected to be true
				// add the element name to the list of metadata elements
				if (localName.equals("attribute") &&
					inXsElement.length() > 0  && 
					nameValue.equals(METADATA_TAG_ATTRIBUTE_NAME)) {
					if (atts.getValue("fixed").equals("true") ||
						atts.getValue("default").equals("true") ) {
						metadataTags.add(inXsElement);
					}
				}
			}
		}
		public void endElement(String namespaceURI, String localName, String qName) 
			throws SAXException {
			// out of element; clear flag var
			if (localName.equals("element")) {
				inXsElement = "";
			}
		}
		public void endDocument() throws SAXException {
		}
		
	}



}
