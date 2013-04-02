/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Test for VcfFileValidatorUtilFastTest
 *
 * @author srinivasand Last updated by: $Author$
 * @version $Rev$
 */
public class VcfFileValidatorUtilFastTest {
    @Test
    public void testIsExistsFormatKeyGood() {
        final String formatString = "ID:BI:GH:TR";
        assertTrue(VcfFileValidatorUtil.isExistsFormatKey(formatString, "BI"));
        assertTrue(VcfFileValidatorUtil.isExistsFormatKey(formatString, "ID"));
        assertTrue(VcfFileValidatorUtil.isExistsFormatKey(formatString, "TR"));
        assertTrue(VcfFileValidatorUtil.isExistsFormatKey(formatString, "T"));
        assertTrue(VcfFileValidatorUtil.isExistsFormatKey(formatString, "D"));
        final String formatStringSingleElement = "BL";
        assertTrue(VcfFileValidatorUtil.isExistsFormatKey(formatStringSingleElement, "BL"));
    }

    @Test
    public void testIsExistsFormatKeyBad() {
        final String formatString = "ID:BI:GH:TR;JK";
        assertFalse(VcfFileValidatorUtil.isExistsFormatKey(formatString, "BIC"));
        assertFalse(VcfFileValidatorUtil.isExistsFormatKey(formatString, "JK"));
        assertFalse(VcfFileValidatorUtil.isExistsFormatKey(formatString, "foo"));
        assertFalse(VcfFileValidatorUtil.isExistsFormatKey(formatString, "I"));
    }

    @Test
    public void testGetSampleValueFound() {
        final String formatKey = "BB";
        final String formatValue = "AA:BB:CC:DD";
        final String sampleValue = "0/0:2:.:foo";
        assertEquals("2", VcfFileValidatorUtil.getSampleValue(formatKey, formatValue, sampleValue));
    }

    @Test
    public void testGetSampleValueNotFound() {
        final String formatKey = "BC";
        final String formatValue = "AA:BB:CC:DD";
        final String sampleValue = "0/0:2:.:foo";
        assertEquals("", VcfFileValidatorUtil.getSampleValue(formatKey, formatValue, sampleValue));
    }

    @Test
    public void testGetSampleValueLessSampleValues() {
        final String formatKey = "DD";
        final String formatValue = "AA:BB:CC:DD";
        final String sampleValue = "0/0:2:.";
        assertEquals("", VcfFileValidatorUtil.getSampleValue(formatKey, formatValue, sampleValue));
    }

    @Test
    public void testGetInfoValueKeyPresent() {
        final String infoKey = "BB";
        final String infoValue = "AA=1;BB=17;DD=4";
        assertEquals("17", VcfFileValidatorUtil.getInfoValue(infoKey, infoValue));
    }

    @Test
    public void testGetInfoValueNoKey() {
        final String infoKey = "BB";
        final String infoValue = "AA=1;DD=4";
        assertEquals("", VcfFileValidatorUtil.getInfoValue(infoKey, infoValue));
    }

    @Test
    public void testGetInfoValuePartialKey() {
        final String infoKey = "BB";
        final String infoValue = "AA=1;DD=4;BB";
        assertEquals("", VcfFileValidatorUtil.getInfoValue(infoKey, infoValue));
    }

    @Test
    public void testContainsInfoKey() {
        assertTrue(VcfFileValidatorUtil.containsInfoKey("HI", "HI=1", true));
        assertFalse(VcfFileValidatorUtil.containsInfoKey("HI", "hi=1", true));
        assertTrue(VcfFileValidatorUtil.containsInfoKey("HI", "hi=1", false));

        assertTrue(VcfFileValidatorUtil.containsInfoKey("HI", "HI=1;AB=0/0;CD=Z", true));
        assertTrue(VcfFileValidatorUtil.containsInfoKey("hi", "HI=1;AB=0/0;CD=Z", false));

        assertTrue(VcfFileValidatorUtil.containsInfoKey("HI", "HI", true));
        assertTrue(VcfFileValidatorUtil.containsInfoKey("HI", "Hi", false));

        assertTrue(VcfFileValidatorUtil.containsInfoKey("HI", "HI;BYE", true));
        assertTrue(VcfFileValidatorUtil.containsInfoKey("Hi", "HI;BYE", false));

        assertFalse(VcfFileValidatorUtil.containsInfoKey("HI", "BYE", true));

        // make sure doesn't match when partial match exists
        assertFalse(VcfFileValidatorUtil.containsInfoKey("HI", "HIX=1;IHI=2", true));
        assertFalse(VcfFileValidatorUtil.containsInfoKey("hi", "HIX=1;IHI=2", false));
        assertFalse(VcfFileValidatorUtil.containsInfoKey("hill", "HI=10;HILLY=2", false));
    }
}
