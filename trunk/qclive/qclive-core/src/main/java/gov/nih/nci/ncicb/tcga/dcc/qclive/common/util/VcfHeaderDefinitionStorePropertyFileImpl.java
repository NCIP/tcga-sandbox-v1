/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFileHeader;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.Logger;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * VCF header definition store that uses a property file to fetch definitions.
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class VcfHeaderDefinitionStorePropertyFileImpl implements VcfHeaderDefinitionStore {

    /**
     * Path to the properties
     */
    private static final String PATH_TO_PROPERTIES = Thread.currentThread().getContextClassLoader().getResource("properties").getPath() + File.separator;

    /**
     * Name of the properties file
     */
    private static final String PROPERTIES_FILE_NAME = "vcfHeaderDefinitions.properties";

    /**
     * VCF header expected subfields
     */
    private static final String ID = "ID";
    private static final String NUMBER = "Number";
    private static final String TYPE = "Type";
    private static final String DESCRIPTION = "Description";
    private static final String ENCODING_SCHEME = "UTF-8";

    /**
     * VcfFileHeader Map, keys being of the form <code>headerType.headerId</code>
     */
    private final Map<String, VcfFileHeader> vcfFileHeaderMap = new HashMap<String, VcfFileHeader>();

    /**
     * @param logger the logger to use for logging errors
     */
    public VcfHeaderDefinitionStorePropertyFileImpl(final Logger logger) {
        initVcfFileHeaderMap(logger);
    }

    @Override
    public VcfFileHeader getHeaderDefinition(final String headerType, final String headerId) {
        return getVcfFileHeaderMap().get(makeMapKey(headerType, headerId));
    }

    /**
     * Return the VcfFileHeader Map key for the given header type and header Id
     *
     * @param headerType the header type
     * @param headerId the header Id
     * @return the VcfFileHeader Map key for the given header type and header Id
     */
    private String makeMapKey(final String headerType, final String headerId) {
        return headerType + "." + headerId;
    }

    /**
     * Initialize the VcfFileHeader Map
     *
     * @param logger the logger to use for logging errors
     */
    private void initVcfFileHeaderMap(final Logger logger) {

        try {
            final Properties vcfHeaderDefinitionsProperties = getProperties();
            final Set<String> propertyNames = vcfHeaderDefinitionsProperties.stringPropertyNames();

            for(final String propertyName : propertyNames) {

                final String[] nameParts = propertyName.split("\\.", -1);

                if(nameParts.length == 3) {
                    //Property names are expected to be in the form headerType.headerId.headerSubField

                    final String headerType = nameParts[0];
                    final String headerId = nameParts[1];
                    final String headerSubField = nameParts[2];
                    final String mapKey = makeMapKey(headerType, headerId);
                    final String propertyValue = vcfHeaderDefinitionsProperties.getProperty(propertyName);

                    VcfFileHeader vcfFileHeader = getVcfFileHeaderMap().get(mapKey);

                    if(vcfFileHeader == null) {
                        // Create a new VcfFileHeader and add it to the Map
                        final Map<String, String> values = new HashMap<String, String>();
                        values.put(ID, headerId);
                        values.put(headerSubField, propertyValue);

                        vcfFileHeader = new VcfFileHeader(headerType);
                        vcfFileHeader.setValueMap(values);

                        getVcfFileHeaderMap().put(mapKey, vcfFileHeader);
                        
                    } else {
                        //Update the VcfFileHeader valueMap
                        vcfFileHeader.getValueMap().put(headerSubField, propertyValue);
                    }
                }
            }

            cleanupVcfFileHeaderMap();
            
        } catch (final IOException e) {
            logger.log(e);
        }
    }

    /**
     * Remove from the Map all {@link VcfFileHeader}s with a valueMap that does not contain all the expected following values:
     *
     * - ID
     * - Number
     * - Type
     * - Description
     */
    private void cleanupVcfFileHeaderMap() {

        final List<String> mapKeysToRemove = new ArrayList<String>();

        for(final String mapKey : getVcfFileHeaderMap().keySet()) {

            final VcfFileHeader vcfFileHeader = getVcfFileHeaderMap().get(mapKey);
            final Map<String, String> values = vcfFileHeader.getValueMap();

            if(!values.containsKey(ID)
                    || !values.containsKey(NUMBER)
                    || !values.containsKey(TYPE)
                    || !values.containsKey(DESCRIPTION)) {
                mapKeysToRemove.add(mapKey);
            }
        }

        for(final String mapKeyToRemove : mapKeysToRemove) {
            getVcfFileHeaderMap().remove(mapKeyToRemove);
        }
    }

    /**
     * Return the properties from the given properties file
     *
     * @return the properties from the given properties file
     * @throws IOException if the property file can't be read
     */
    private Properties getProperties() throws IOException {

        Properties result = null;
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(URLDecoder.decode(PATH_TO_PROPERTIES + getPropertiesFileName(), ENCODING_SCHEME));
            result = new Properties();
            result.load(inputStream);

        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return result;
    }

    public Map<String, VcfFileHeader> getVcfFileHeaderMap() {
        return vcfFileHeaderMap;
    }

    /**
     * Return the properties file name to use
     *
     * @return the properties file name to use
     */
    protected String getPropertiesFileName() {
        return PROPERTIES_FILE_NAME;
    }
}
