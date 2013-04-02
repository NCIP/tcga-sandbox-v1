/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.util;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.SchemaException;
import gov.nih.nci.ncicb.tcga.dcc.common.util.XPathXmlParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ClinicalXmlValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Utility class to validate an XML file against its schema.
 * @author girshiks
 */
public class QCliveXMLSchemaValidator {

	public static final String XSD_EXTENSION = ".xsd";
	
	/**
	 * Regular expression pattern strings for validating XSD URLs in BCR XML
	 * files
	 **/
	private final String PROTOCOL_PATTERN = "http[s]?\\:\\/\\/";
	private final String PATH_PATTERN = "(\\/(\\w|\\W)+)*\\/";
	private String validXsdDomainPattern;
	
	private String validXsdPrefixPattern;
	private String validXsdVersionPattern;
	private Boolean allowLocalSchema;	
	
	
	/** E.g. ^(http[s]?\:\/\/)(tcga-data\.nci\.nih\.gov)((\/(\w|\W)+)*\/)(bcr)((\/(\w|\W)+)*\/)(2\.4(\.\d*)?)\/.+\.xsd$ **/
    private String XSD_URL_PATTERN = "^(" + PROTOCOL_PATTERN + ")(" + validXsdDomainPattern + ")(" + PATH_PATTERN + ")(" + validXsdPrefixPattern + ")" 
    	+ "(" + PATH_PATTERN + ")(" + validXsdVersionPattern + ")\\/.+\\" + ClinicalXmlValidator.XSD_EXTENSION + "$";
    	
	/**
	 * 
	 * @param xmlFile file to validate
	 * @param context QcLive context to record errors to
	 * @param allowLocalSchema boolean flag to allow local schema location
	 * @param XSDURLPattern - pattern to validate XSD URL against
	 * @return true if validation successful, False otherwise
	 * @throws IOException
	 * @throws SAXException
	 * @throws SchemaException
	 * @throws ParserConfigurationException
	 */
	public boolean validateSchema(final File xmlFile,final QcContext context,
			final Boolean allowLocalSchema, final Pattern XSDURLPattern)
			throws IOException, SAXException, SchemaException, ParserConfigurationException {
		XPathXmlParser xPathXmlParser = new XPathXmlParser();
		final Document document = xPathXmlParser.parseXmlFile(xmlFile, false, true);
		
		// Look in the document header to find which XSD(s) it is referring to
		final String schemaLocation = document.getDocumentElement()
				.getAttribute("xsi:schemaLocation");

		if (StringUtils.isBlank(schemaLocation)) {
			context.addError(MessageFormat.format(
					MessagePropertyType.XML_FILE_PROCESSING_ERROR,
					xmlFile.getName(),
					"Could not validate XML attribute 'schemaLocation' because it was either null or empty"));
			return false;
		}

		// Retrieve the XSD URLs from the schema location attribute and validate
		// each
		final List<Source> srcList = new ArrayList<Source>();
		final String[] xsdURLs = schemaLocation.split("\\s");
		boolean isURL;
		for (String schemaURL : xsdURLs) {
			isURL = ConstantValues.HTTP_URL_PATTERN.matcher(schemaURL).matches();
			// If the schema location refers to a local URL and local URLs are
			// not allowed, throw an exception.
			if (!allowLocalSchema && !isURL) {
				throw new SchemaException(
						new StringBuilder(
								" Local schema location is not allowed. Schema location is '")
								.append(schemaURL).append("'").toString());
			} else if (isURL && schemaURL.endsWith(XSD_EXTENSION)) {
				// Validate that the schema location URL is properly formatted
				if (!XSDURLPattern.matcher(schemaURL).matches()) {
					context.addError(MessageFormat.format(
							MessagePropertyType.XML_FILE_PROCESSING_ERROR,
							xmlFile.getName(),
							"XML schemaLocation attribute value '" + schemaURL
									+ "' is not in the appropriate format"));
					return false;
				}
				srcList.add(getSource(isURL, schemaURL, xmlFile));
			}

		}

		return validateSchema(srcList, document,
				xmlFile, context);
	}

	protected  Boolean validateSchema(
			final List<Source> schemaSourceList, final Document document,
			final File xmlFile, final QcContext context) throws SAXException,
			IOException {

		final SchemaFactory factory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		// instantiate the schema
		Schema schema = factory.newSchema(schemaSourceList
				.toArray(new Source[] {}));
		// now validate the file against the schema
		// note: supposedly there is a way to just let the XML document figure
		// out its own schema based on what is referred to, but
		// I could not get that to work, which is why I am looking for the
		// schema in the attribute

		final DOMSource source = new DOMSource(document);
		final Validator validator = schema.newValidator();

		// wow this looks dumb, but isValid has to be final to be accessed from
		// within an inner class
		// and this was IDEA's solution to the problem: make it an array and set
		// the first element of the array
		final boolean[] isValid = new boolean[] { true };

		// add error handler that will add validation errors and warnings
		// directly to the QcContext object
		validator.setErrorHandler(new ErrorHandler() {
			public void warning(final SAXParseException exception) {
				context.addWarning(new StringBuilder()
						.append(xmlFile.getName()).append(": ")
						.append(exception.getMessage()).toString());
			}

			public void error(final SAXParseException exception) {
				context.addError(MessageFormat.format(
						MessagePropertyType.XML_FILE_PROCESSING_ERROR,
						xmlFile.getName(),
						new StringBuilder().append(xmlFile.getName())
								.append(": ").append(exception.getMessage())
								.toString()));
				isValid[0] = false;
			}

			public void fatalError(final SAXParseException exception)
					throws SAXException {
				context.getArchive().setDeployStatus(Archive.STATUS_INVALID);
				throw exception;
			}
		});
		validator.validate(source);
		return isValid[0];
	}

	protected  Source getSource(final Boolean isURL, final String schema,
			final File xmlFile) throws IOException {

		if (isURL) {
			final URL schemaURL = new URL(schema);
			return new StreamSource(schemaURL.toExternalForm());
		} else {
			// get xsd file
			File schemaFile = new File(new StringBuilder(xmlFile.getParent())
					.append(File.separator).append(schema).toString());
			if (!schemaFile.exists()) {
				throw new FileNotFoundException(
						new StringBuilder("Schema ")
								.append(schema)
								.append(" was not found in the archive, even though the XML file ")
								.append(xmlFile.getName())
								.append(" refers to it").toString());
			}
			return new StreamSource(schemaFile);

		}
	}
	
	/**
	 * Builds the regular expression pattern for matching valid XSD URLs. The resulting pattern will look like the following:
	 *
	 * <blockquote><pre>
	 * ^(http[s]?\:\/\/)(domainPattern)((\/(\w|\W)+)*\/)(prefixPattern)((\/(\w|\W)+)*\/)(versionPattern)\/.+\.xsd$
	 * </pre></blockquote>
	 * 
	 * where domainPattern, prefixPattern, and versionPattern are replaced with regex patterns set for instances of
	 * {@link ClinicalXmlValidator}.
	 *
	 * @return {@link Pattern} instance compile from the regular expression for matching XSD URLs
	 */
	public Pattern getXSDURLPattern() {
		StringBuilder pattern = new StringBuilder();
		pattern.append("^(");
		pattern.append(PROTOCOL_PATTERN);
		pattern.append(")(");
		pattern.append(validXsdDomainPattern);
		pattern.append(")(");
		pattern.append(PATH_PATTERN);
		pattern.append(")(");
		pattern.append(validXsdPrefixPattern);
		pattern.append(")(");
		pattern.append(PATH_PATTERN);
		pattern.append(")(");
		pattern.append(validXsdVersionPattern);
		pattern.append(")\\/.+\\");
		pattern.append(ClinicalXmlValidator.XSD_EXTENSION);
		pattern.append("$");
		
		return Pattern.compile(pattern.toString());
	}
	
	public String getValidXsdDomainPattern() {
		return validXsdDomainPattern;
	}

	public void setValidXsdDomainPattern(String validXsdDomainPattern) {
		this.validXsdDomainPattern = validXsdDomainPattern;
	}

	public String getValidXsdPrefixPattern() {
		return validXsdPrefixPattern;
	}

	public void setValidXsdPrefixPattern(String validXsdPrefixPattern) {
		this.validXsdPrefixPattern = validXsdPrefixPattern;
	}

	public String getValidXsdVersionPattern() {
		return validXsdVersionPattern;
	}

	public void setValidXsdVersionPattern(String validXsdVersionPattern) {
		this.validXsdVersionPattern = validXsdVersionPattern;
	}
	public Boolean getAllowLocalSchema() {
		return allowLocalSchema;
	}

	public void setAllowLocalSchema(Boolean allowLocalSchema) {
		this.allowLocalSchema = allowLocalSchema;
	}

}
