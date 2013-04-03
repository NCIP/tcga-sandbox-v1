/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.jaxb;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class provides utility functions for processing JAXB elements generated
 * from TCGA schema documents.
 *
 * @author Matt Nicholls
 *         Last updated by: nichollsmc
 */
public class JAXBUtil {

    private static final Log logger = LogFactory.getLog(JAXBUtil.class);
    private static final String SCHEMA_URL_PREFIX = "http://tcga-data.nci.nih.gov/docs/xsd/BCR/tcga.nci/bcr/xml/";
    private static final String SCHEMA_VERSION = "2.6";
    private static final String META_DATA_SCHEMA_LOCATION = "schema/TCGA_BCR.Metadata.xsd";
    private static final List<String> schemas = new ArrayList<String>();

    static {
        schemas.add(SCHEMA_URL_PREFIX + "administration/" + SCHEMA_VERSION + "/TCGA_BCR.Administration.xsd");
        schemas.add(SCHEMA_URL_PREFIX + "clinical/pharmaceutical/" + SCHEMA_VERSION + "/TCGA_BCR.Pharmaceutical.xsd");
        schemas.add(SCHEMA_URL_PREFIX + "clinical/radiation/" + SCHEMA_VERSION + "/TCGA_BCR.Radiation.xsd");
        schemas.add(SCHEMA_URL_PREFIX + "clinical/shared/" + SCHEMA_VERSION + "/TCGA_BCR.Shared_Clinical_Elements.xsd");
        schemas.add(SCHEMA_URL_PREFIX + "utility/" + SCHEMA_VERSION + "/TCGA_BCR.Utility.xsd");
        schemas.add(SCHEMA_URL_PREFIX + "controls/" + SCHEMA_VERSION + "/TCGA_BCR.Controls.xsd");
    }

    /**
     * Performs the same function as {@link JAXBUtil#unmarshal(File, Class, boolean, boolean)} method with
     * meta-data namespace URI filtering turned off.
     *
     * @param xmlFile   - a {@link File} object representing the XML file to unmarshalled
     * @param jaxbClass - the {@link Class} object that represents a JAXB generated class type
     * @param validate  - boolean indicating weather or not the XML should be validated against a schema
     * @return - an instance of {@link UnmarshalResult} representing the result of the unmarhsalling
     * @throws UnmarshalException if an error occurs during unmarshalling
     */
    public static UnmarshalResult unmarshal(
            final File xmlFile,
            final Class<?> jaxbClass,
            final boolean validate) throws UnmarshalException {

        return unmarshal(xmlFile, jaxbClass, false, validate);
    }

    /**
     * Performs the same function as {@link JAXBUtil#unmarshal(File, String, boolean, boolean)}, but uses the
     * provided {@link Class} object to retrieve the package namespace of the JAXB classes.
     *
     * @param xmlFile                  - a {@link File} object representing the XML file to unmarshalled
     * @param jaxbClass                - the {@link Class} object that represents a JAXB generated class type
     * @param filterMetaDataNamespaces - boolean that specifies whether or not to filter meta-data
     *                                 namespaces using the {@link MetaDataXMLNamespaceFilter}
     * @param validate                 - boolean indicating weather or not the XML should be validated against a schema
     * @throws UnmarshalException if an error occurs during unmarshalling
     */
    public static UnmarshalResult unmarshal(
            final File xmlFile,
            final Class<?> jaxbClass,
            final boolean filterMetaDataNamespaces,
            final boolean validate) throws UnmarshalException {

        return unmarshal(xmlFile, jaxbClass.getPackage().getName(), filterMetaDataNamespaces, validate);
    }

    /**
     * This method unmarshals an XML file into a JAXB object element.
     * <p/>
     * <p/>
     * The underlying type of the JAXB object element returned by this method will correspond
     * to the JAXB object(s) referenced by the package namespace provided in the parameter list.
     * <p/>
     * <p/>
     * If the <code>filterMetaDataNamespaces</code> parameter is set to true, this method will use
     * the {@link MetaDataXMLNamespaceFilter} to filter the namespace URI of specific meta-data
     * elements during unmarshalling that correspond to the TCGA_BCR.Metadata XSD.
     * <p/>
     * <p/>
     * If the <code>validate</code> parameter is set to true, schema validation will be performed.
     * <p/>
     * <p/>
     * If both <code>filterMetaDataNamespaces</code> and <code>validate</code> are set to true,
     * only the meta-data elements will go through schema validation.
     *
     * @param xmlFile                  - a {@link File} object representing the XML file to unmarshalled
     * @param jaxbPackageName          - a string that represents package namespace of the JAXB context objects
     * @param filterMetaDataNamespaces - boolean that specifies whether or not to filter meta-data
     *                                 namespace URIs using the {@link MetaDataXMLNamespaceFilter}
     * @param validate                 - boolean indicating weather or not the XML should be validated against a schema
     * @return - an instance of {@link UnmarshalResult} representing the result of the unmarhsalling
     * @throws UnmarshalException if an error occurs during unmarshalling
     */
    public static UnmarshalResult unmarshal(
            final File xmlFile,
            final String jaxbPackageName,
            final boolean filterMetaDataNamespaces,
            final boolean validate) throws UnmarshalException {

        Object jaxbObject = null;
        ValidationEventCollector validationEventCollector = (validate ? new ValidationEventCollector() : null);
        JAXBContext jaxbContext;
        Unmarshaller unmarshaller;

        if (xmlFile != null && jaxbPackageName != null) {
            FileReader xmlFileReader = null;
            try {
                // Get the JAXB context using the package name and create an unmarshaller
                jaxbContext = JAXBContext.newInstance(jaxbPackageName);
                unmarshaller = jaxbContext.createUnmarshaller();
                xmlFileReader = new FileReader(xmlFile);

                // Unmarshal the XML file
                if (filterMetaDataNamespaces) {
                    final SAXSource source = applyMetaDataNamespaceFilter(unmarshaller, xmlFileReader);
                    jaxbObject = unmarshaller.unmarshal(source);

                    // Perform schema validation meta-data elements only
                    if (validate) {
                        final String metaDataXML = getMetaDataXMLAsString(jaxbContext, jaxbObject);
                        jaxbObject = validate(unmarshaller, validationEventCollector, new StringReader(metaDataXML), true);
                    }
                } else {

                    // Perform schema validation of all XML elements
                    if (validate) {
                        jaxbObject = validate(unmarshaller, validationEventCollector, xmlFileReader, false);
                    } else {
                        jaxbObject = unmarshaller.unmarshal(xmlFile);
                    }
                }
            } catch (Exception e) {
                throw new UnmarshalException(e);
            } finally {
                IOUtils.closeQuietly(xmlFileReader);
            }
        } else {
            throw new UnmarshalException(
                    new StringBuilder()
                            .append("Unmarshalling failed because either the XML file '")
                            .append(xmlFile)
                            .append("' or package namespace '")
                            .append(jaxbPackageName)
                            .append("' was null").toString());
        }

        // Return the result of the unmarshalling
        if (validationEventCollector != null) {
            return new UnmarshalResult(jaxbObject, Arrays.asList(validationEventCollector.getEvents()));
        } else {
            return new UnmarshalResult(jaxbObject, new ArrayList<ValidationEvent>());
        }
    }

    private static String getMetaDataXMLAsString(final JAXBContext jaxbContext, final Object jaxbObject) throws JAXBException {

        // Create an unmarshaller
        final Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        // Marshal the JAXB object meta-data into XML and return the result
        final StringWriter stringWriter = new StringWriter();
        marshaller.marshal(jaxbObject, stringWriter);

        return stringWriter.toString();
    }

    private static Object validate(
            final Unmarshaller unmarshaller,
            final ValidationEventHandler validationEventHandler,
            final Reader reader,
            final boolean includeMetaDataSchema) throws JAXBException, SAXException, IOException {

        Object jaxbObject = null;
        try {
            // Set the schema for the unmarshaller
            final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            final Schema schema = schemaFactory.newSchema(getSchemaSources(includeMetaDataSchema).toArray(new Source[]{}));
            unmarshaller.setSchema(schema);
            unmarshaller.setEventHandler(validationEventHandler);

            // Unmarshal and validate
            jaxbObject = unmarshaller.unmarshal(reader);
        } catch (UnmarshalException ue) {
            // Swallow the exception. The ValidationEventHandler attached to the unmarshaller will
            // contain the validation events
            logger.info(ue);
        }

        return jaxbObject;
    }

    private static List<Source> getSchemaSources(final boolean includeMetaDataSchema) throws IOException {

        final List<Source> streamSources = new ArrayList<Source>();

        // Retrieve meta-data schema if required
        if (includeMetaDataSchema) {
            streamSources.add(
                    new StreamSource(
                            Thread.currentThread().getContextClassLoader().getResourceAsStream(META_DATA_SCHEMA_LOCATION)
                    )
            );
        }

        // Retrieve all required schemas
        for (final String schema : schemas) {
            streamSources.add(
                    new StreamSource(
                            new URL(schema).openStream()
                    )
            );
        }

        return streamSources;
    }

    private static SAXSource applyMetaDataNamespaceFilter(final Unmarshaller unmarshaller, final Reader xmlFileReader)
            throws SAXException, ParserConfigurationException, FileNotFoundException {

        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true); // this should not be changed!
        final XMLReader reader = factory.newSAXParser().getXMLReader();
        final XMLFilterImpl xmlFilter = new MetaDataXMLNamespaceFilter(reader);
        reader.setContentHandler(unmarshaller.getUnmarshallerHandler());

        return new SAXSource(xmlFilter, new InputSource(xmlFileReader));
    }

    /**
     * Performs the same function as {@link JAXBUtil#getJAXBIntrospector(String)}, but uses a JAXB
     * generated class object to retrieve the JAXB package name.
     *
     * @param jaxbClass - the {@link Class} object that the represents a JAXB generated class
     * @return an instance of {@link JAXBIntrospector} that corresponds to the JAXB package name
     * @throws JAXBException if an error occurs while creating a JAXB context
     */
    public static JAXBIntrospector getJAXBIntrospector(final Class<?> jaxbClass) throws JAXBException {

        if (jaxbClass != null) {
            return getJAXBIntrospector(jaxbClass.getPackage().getName());
        } else {
            throw new JAXBException("Could not instantiate JAXB introspector because the JAXB class object was null");
        }
    }

    /**
     * Convenience method for retrieving and instance of {@link JAXBIntrospector} for JAXB generated
     * objects from a specific JAXB package name.
     *
     * @param jaxbPackageName - a string representing the package name of JAXB generated objects
     * @return an instance of {@link JAXBIntrospector} that corresponds to the JAXB package name
     * @throws JAXBException if an error occurs while creating a JAXB context
     */
    public static JAXBIntrospector getJAXBIntrospector(final String jaxbPackageName) throws JAXBException {

        if (jaxbPackageName != null) {
            final JAXBContext context = JAXBContext.newInstance(jaxbPackageName);
            return context.createJAXBIntrospector();
        } else {
            throw new JAXBException("Could not instantiate JAXB introspector because the JAXB package namespace was null");
        }
    }

    /**
     * Utility method for retrieving a string representation of the value referenced by a JAXB generated object.
     * <p/>
     * <p/>
     * This method assumes that <code>value</code> is a valid declared field of the object being passed to it,
     * and also contains the corresponding read method <code>getValue()</code>.
     * <p/>
     * <p/>
     * <b>This method will work with other non-JAXB generated objects that meet the requirements stated above,
     * but it is not recommended.</b>
     *
     * @param valueObject - an object that represents a JAXB generated value object
     * @return a string representation of <code>Object.getValue()</code>
     */
    public static String getJAXBObjectValue(final Object valueObject) {

        String value = null;
        if (valueObject != null) {
            try {
                final Field valueField = valueObject.getClass().getDeclaredField("value");
                final PropertyDescriptor valueObjectDescriptor =
                        new PropertyDescriptor(valueField.getName(), valueObject.getClass());
                value = valueObjectDescriptor.getReadMethod().invoke(valueObject, null).toString();
            } catch (Exception e) {
                logger.error(e.getMessage());
                return value;
            }
        }

        return value;
    }
}