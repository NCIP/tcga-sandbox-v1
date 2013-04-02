/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.util;

import gov.nih.nci.ncicb.tcga.dcc.dam.view.Header;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Author: David Nassau
 */
public class DataAccessMatrixJSPUtilFastTest extends TestCase {

    private Map<String, List> lookups;

    public void setUp() {
        lookups = new HashMap<String, List>();
        final List<Map> platformTypeRecords = new ArrayList<Map>();
        Map<String, Object> record = new HashMap<String, Object>();
        record.put( DataAccessMatrixJSPUtil.FIELD_DATA_TYPE_ID, 1 );
        record.put( DataAccessMatrixJSPUtil.FIELD_DATA_TYPE_NAME, "Expression-Genes" );
        platformTypeRecords.add( record );
        record = new HashMap<String, Object>();
        record.put( DataAccessMatrixJSPUtil.FIELD_DATA_TYPE_ID, 2 );
        record.put( DataAccessMatrixJSPUtil.FIELD_DATA_TYPE_NAME, "SNP" );
        platformTypeRecords.add( record );
        lookups.put( DataAccessMatrixJSPUtil.ATTRIBUTE_KEY_DATATYPES, platformTypeRecords );
        final List<Map> centerRecords = new ArrayList<Map>();
        record = new HashMap<String, Object>();
        record.put( DataAccessMatrixJSPUtil.FIELD_CENTER_ID, 1 );
        record.put( DataAccessMatrixJSPUtil.FIELD_CENTER_SHORT_NAME, "HMS" );
        centerRecords.add( record );
        record = new HashMap<String, Object>();
        record.put( DataAccessMatrixJSPUtil.FIELD_CENTER_ID, 2 );
        record.put( DataAccessMatrixJSPUtil.FIELD_CENTER_SHORT_NAME, "BI" );
        centerRecords.add( record );
        lookups.put( DataAccessMatrixJSPUtil.ATTRIBUTE_KEY_CENTERS, centerRecords );
        final List<Map> platformRecords = new ArrayList<Map>();
        record = new HashMap<String, Object>();
        record.put( DataAccessMatrixJSPUtil.FIELD_PLATFORM_ID, 1 );
        record.put( DataAccessMatrixJSPUtil.FIELD_PLATFORM_NAME, "IlluminaDNAMethylation_OMA002_CPI" );
        platformRecords.add( record );
        record = new HashMap<String, Object>();
        record.put( DataAccessMatrixJSPUtil.FIELD_PLATFORM_ID, 2 );
        record.put( DataAccessMatrixJSPUtil.FIELD_PLATFORM_NAME, "WHG-4x44K_G4112F" );
        platformRecords.add( record );
        lookups.put( DataAccessMatrixJSPUtil.ATTRIBUTE_KEY_PLATFORMS, platformRecords );
    }

    public void testHeaders() {
        String value, expected;
        for(int i = 0; i < 2; i++) {
            value = DataAccessMatrixJSPUtil.lookupHeaderText( Header.HeaderCategory.PlatformType, Integer.toString( i + 1 ), lookups );
            expected = (String) ( (Map) ( (List) lookups.get( DataAccessMatrixJSPUtil.ATTRIBUTE_KEY_DATATYPES ) ).get( i ) ).get( DataAccessMatrixJSPUtil.FIELD_DATA_TYPE_NAME );
            assertEquals( expected, value );
        }
        //outside the defined values, should be passed back unchanged
        value = DataAccessMatrixJSPUtil.lookupHeaderText( Header.HeaderCategory.PlatformType, "foo" );
        assertEquals( value, "foo" );
        for(int i = 0; i < 2; i++) {
            value = DataAccessMatrixJSPUtil.lookupHeaderText( Header.HeaderCategory.Center, Integer.toString( i + 1 ) ); //don't need to keep passing lookups map in
            expected = (String) ( (Map) ( (List) lookups.get( DataAccessMatrixJSPUtil.ATTRIBUTE_KEY_CENTERS ) ).get( i ) ).get( DataAccessMatrixJSPUtil.FIELD_CENTER_SHORT_NAME );
            assertEquals( expected, value );
        }
        value = DataAccessMatrixJSPUtil.lookupHeaderText( Header.HeaderCategory.Center, "foo" );
        assertEquals( value, "foo" );
        //center plus platform (concatenated index)
        value = DataAccessMatrixJSPUtil.lookupHeaderText( Header.HeaderCategory.Center, "1.1" );
        final String expectedCenter = (String) ( (Map) ( (List) lookups.get( DataAccessMatrixJSPUtil.ATTRIBUTE_KEY_CENTERS ) ).get( 0 ) ).get( DataAccessMatrixJSPUtil.FIELD_CENTER_SHORT_NAME );
        final String expectedPlatform = (String) ( (Map) ( (List) lookups.get( DataAccessMatrixJSPUtil.ATTRIBUTE_KEY_PLATFORMS ) ).get( 0 ) ).get( DataAccessMatrixJSPUtil.FIELD_PLATFORM_NAME );
        expected = expectedCenter + " (" + expectedPlatform + ")";
        assertEquals( expected, value );
    }
}
