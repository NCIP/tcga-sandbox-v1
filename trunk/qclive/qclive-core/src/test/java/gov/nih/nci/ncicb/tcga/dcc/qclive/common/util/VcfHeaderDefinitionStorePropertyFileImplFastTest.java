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
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * VcfHeaderDefinitionStorePropertyFileImpl unit test
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class VcfHeaderDefinitionStorePropertyFileImplFastTest {

    /**
     * VCF header expected subfields
     */
    private static final String NUMBER = "Number";
    private static final String TYPE = "Type";
    private static final String DESCRIPTION = "Description";
    private static final String ID = "ID";

    private Mockery mockery;
    private Logger mockLogger;

    private VcfHeaderDefinitionStorePropertyFileImpl store;

    @Before
    public void setUp() {

        mockery = new JUnit4Mockery();
        mockLogger = mockery.mock(Logger.class);
        store = new VcfHeaderDefinitionStorePropertyFileImpl(mockLogger);
    }

    @Test
    public void testVcfFileHeaderMap() {

        assertNotNull(store);
        final Map<String, VcfFileHeader> vcfFileHeaderMap = store.getVcfFileHeaderMap();

        assertNotNull(vcfFileHeaderMap);
        assertEquals(4, vcfFileHeaderMap.size());

        checkVcfFileHeaderInMap(vcfFileHeaderMap, "INFO", "VLS", "1", "Integer", "\"Final validation status\"");
        checkVcfFileHeaderInMap(vcfFileHeaderMap, "INFO", "SID", "2", "String", "\"Unique identifier\"");
        checkVcfFileHeaderInMap(vcfFileHeaderMap, "FORMAT", "SS", "3", "Float", "\"Variant status\"");
        checkVcfFileHeaderInMap(vcfFileHeaderMap, "FORMAT", "TE", ".", "Flag", "\"Translational effect\"");
    }

    @Test
    public void testGetHeaderDefinitionExist() {

        assertNotNull(store);

        final VcfFileHeader vcfFileHeader1 = store.getHeaderDefinition("INFO", "VLS");
        checkVcfFileHeader(vcfFileHeader1, "INFO", "VLS", "1", "Integer", "\"Final validation status\"");

        final VcfFileHeader vcfFileHeader2 = store.getHeaderDefinition("INFO", "SID");
        checkVcfFileHeader(vcfFileHeader2, "INFO", "SID", "2", "String", "\"Unique identifier\"");

        final VcfFileHeader vcfFileHeader3 = store.getHeaderDefinition("FORMAT", "SS");
        checkVcfFileHeader(vcfFileHeader3, "FORMAT", "SS", "3", "Float", "\"Variant status\"");

        final VcfFileHeader vcfFileHeader4 = store.getHeaderDefinition("FORMAT", "TE");
        checkVcfFileHeader(vcfFileHeader4, "FORMAT", "TE", ".", "Flag", "\"Translational effect\"");
    }

    @Test
    public void testGetHeaderDefinitionDoesNotExist() {

        assertNotNull(store);

        final VcfFileHeader vcfFileHeader1 = store.getHeaderDefinition("FORMAT", "GT");
        assertNull(vcfFileHeader1);

        final VcfFileHeader vcfFileHeader2 = store.getHeaderDefinition("SQUIRREL", "NUTS");
        assertNull(vcfFileHeader2);
    }

    @Test
    public void testMissingPropertiesFile() {

        mockery.checking(new Expectations() {{
            one(mockLogger).log(with(any(IOException.class)));
        }});

        final VcfHeaderDefinitionStorePropertyFileImpl fakeStore = new VcfHeaderDefinitionStorePropertyFileImpl(mockLogger) {

            @Override
            protected String getPropertiesFileName() {
                return "thisPropertiesFileDoesNotExist.properties";
            }
        };

        assertNotNull(fakeStore);

        final Map<String, VcfFileHeader> vcfFileHeaderMap = fakeStore.getVcfFileHeaderMap();
        assertNotNull(vcfFileHeaderMap);
        assertEquals(0, vcfFileHeaderMap.size());
    }

    @Test
    public void testURLEncodedSpaceInPropertyFilePath() {

        final VcfHeaderDefinitionStorePropertyFileImpl fakeStore = new VcfHeaderDefinitionStorePropertyFileImpl(mockLogger) {

            @Override
            protected String getPropertiesFileName() {
                return "white%20space" + File.separator + "file.properties";// Path contains URL encoding for space
            }
        };

        assertNotNull(fakeStore);
    }

    /**
     * Check the existence of a VcfFileHeader in the Map and check it's values
     *
     * @param vcfFileHeaderMap the Map in which to lookup the VcfFileHeader
     * @param expectedHeaderType the expected header type
     * @param expectedHeaderId the expected header Id
     * @param expectedNumberValue the expected Number value
     * @param expectedTypeValue the expected Type value
     * @param expectedDescriptionValue the expected Description value
     */
    private void checkVcfFileHeaderInMap(final Map<String, VcfFileHeader> vcfFileHeaderMap,
                                         final String expectedHeaderType,
                                         final String expectedHeaderId,
                                         final String expectedNumberValue,
                                         final String expectedTypeValue,
                                         final String expectedDescriptionValue) {

        final String mapKey = expectedHeaderType + "." + expectedHeaderId;
        assertTrue(vcfFileHeaderMap.containsKey(mapKey));

        final VcfFileHeader vcfFileHeader = vcfFileHeaderMap.get(mapKey);
        checkVcfFileHeader(vcfFileHeader, expectedHeaderType, expectedHeaderId, expectedNumberValue, expectedTypeValue, expectedDescriptionValue);
    }

    /**
     * Check the given VcfFileHeader values
     *
     * @param vcfFileHeader the VcfFileHeader to check
     * @param expectedHeaderType the expected header type
     * @param expectedHeaderId the expected header Id
     * @param expectedNumberValue the expected Number value
     * @param expectedTypeValue the expected Type value
     * @param expectedDescriptionValue the expected Description value
     */
    private void checkVcfFileHeader(final VcfFileHeader vcfFileHeader,
                                    final String expectedHeaderType,
                                    final String expectedHeaderId,
                                    final String expectedNumberValue,
                                    final String expectedTypeValue,
                                    final String expectedDescriptionValue) {

        assertNotNull(vcfFileHeader);
        assertEquals(expectedHeaderType, vcfFileHeader.getName());

        final Map<String, String> vcfFileHeaderValueMap = vcfFileHeader.getValueMap();
        assertNotNull(vcfFileHeaderValueMap);
        assertTrue(vcfFileHeaderValueMap.containsKey(ID));
        assertEquals(expectedHeaderId, vcfFileHeaderValueMap.get(ID));
        assertTrue(vcfFileHeaderValueMap.containsKey(NUMBER));
        assertEquals(expectedNumberValue, vcfFileHeaderValueMap.get(NUMBER));
        assertTrue(vcfFileHeaderValueMap.containsKey(TYPE));
        assertEquals(expectedTypeValue, vcfFileHeaderValueMap.get(TYPE));
        assertTrue(vcfFileHeaderValueMap.containsKey(DESCRIPTION));
        assertEquals(expectedDescriptionValue, vcfFileHeaderValueMap.get(DESCRIPTION));
    }
}
