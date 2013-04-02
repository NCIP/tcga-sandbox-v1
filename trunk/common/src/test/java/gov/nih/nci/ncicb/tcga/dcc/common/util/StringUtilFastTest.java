/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for StringUtil
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class StringUtilFastTest {

    @Test
    public void testHasLeadingWhitespace() {

        assertTrue(StringUtil.hasLeadingWhitespace(" test")); // 1 space
        assertTrue(StringUtil.hasLeadingWhitespace("  test"));// More than 1 space
        assertTrue(StringUtil.hasLeadingWhitespace("\ttest"));// 1 tab
        assertTrue(StringUtil.hasLeadingWhitespace("\t\ttest"));// More than 1 tab
        assertFalse(StringUtil.hasLeadingWhitespace("test "));// Only trailing whitespace
    }

    @Test
    public void testHasTrailingWhitespace() {

        assertTrue(StringUtil.hasTrailingWhitespace("test ")); // 1 space
        assertTrue(StringUtil.hasTrailingWhitespace("test  "));// More than 1 space
        assertTrue(StringUtil.hasTrailingWhitespace("test\t"));// 1 tab
        assertTrue(StringUtil.hasTrailingWhitespace("test\t\t"));// More than 1 tab
        assertFalse(StringUtil.hasTrailingWhitespace("  test"));// Only leading whitespace
    }

    @Test
    public void convertListToDelimitedString() {
        final List<String> dataList = new ArrayList();
        dataList.add("data1");
        dataList.add("data2");

        assertTrue("Expected data:  data1,data2", "data1,data2".equals(StringUtil.convertListToDelimitedString(dataList, ',')));
    }

    @Test
    public void testCreatePlaceHolderStringForOneParameter() {

        final String placeHolder = StringUtil.createPlaceHolderString(1);
        assertEquals("?", placeHolder);
    }

    @Test
    public void testCreatePlaceHolderStringForTwoParameters() {

        final String placeHolder = StringUtil.createPlaceHolderString(2);
        assertEquals("?,?", placeHolder);
    }

    @Test
    public void testCreatePlaceHolderStringForTwoParametersUpperCase() {

        final String placeHolder = StringUtil.createPlaceHolderString(2, StringUtil.CaseSensitivity.UPPER_CASE);
        assertEquals("upper(?),upper(?)", placeHolder);
    }

    @Test
    public void testCreatePlaceHolderStringForTwoParametersLowerCase() {

        final String placeHolder = StringUtil.createPlaceHolderString(2, StringUtil.CaseSensitivity.LOWER_CASE);
        assertEquals("lower(?),lower(?)", placeHolder);
    }

    @Test
    public void testContainsIgnoreCase() {

        final String item = "item";
        final List<String> list = new ArrayList<String>();
        list.add(item);

        assertTrue(StringUtil.containsIgnoreCase(list, item));
        assertTrue(StringUtil.containsIgnoreCase(list, item.toLowerCase()));
        assertTrue(StringUtil.containsIgnoreCase(list, item.toUpperCase()));
    }

    @Test
    public void testNormalizeListNull() {
        assertNull(StringUtil.normalize(null));
    }

    @Test
    public void testNormalizeListEmpty() {

        final List<String> emptyList = new ArrayList<String>();
        final List<String> result = StringUtil.normalize(emptyList);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testNormalizeListItemNull() {

        final List<String> list = new ArrayList<String>();
        list.add(null);

        final List<String> result = StringUtil.normalize(list);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testNormalizeListItemDuplicates() {

        final String duplicate = "duplicate";

        final List<String> list = new ArrayList<String>();
        list.add(duplicate);
        list.add(duplicate);

        final List<String> result = StringUtil.normalize(list);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(duplicate, result.get(0));
    }

    @Test
    public void testNormalizeListItemWithLeadingOrTrailingWhitespace() {

        final String item = "item";
        final String whitespace = "   ";

        final List<String> list = new ArrayList<String>();
        list.add(whitespace + item);
        list.add(item + whitespace);
        list.add(whitespace + item + whitespace);

        final List<String> result = StringUtil.normalize(list);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item, result.get(0));
    }

    @Test
    public void testCreateInsertStatement() {

        final List<String> columnNames = Arrays.asList("col1", "col2", "col3");
        final String insertStatement = StringUtil.createInsertStatement("table_name", columnNames);

        assertEquals("insert into table_name (col1,col2,col3) values (?,?,?)", insertStatement);
    }

    @Test
    public void testSpaceAllWhitespace() throws Exception {
        assertEquals(null, StringUtil.spaceAllWhitespace(null));
        assertEquals("", StringUtil.spaceAllWhitespace(""));
        assertEquals("winter is coming", StringUtil.spaceAllWhitespace("winter is coming"));
        assertEquals("winter is coming", StringUtil.spaceAllWhitespace("winter\nis\ncoming"));
        assertEquals("winter is coming", StringUtil.spaceAllWhitespace("winter\tis\tcoming"));
        assertEquals("winter is coming", StringUtil.spaceAllWhitespace("winter\nis\tcoming"));
    }

    @Test
    public void testGetPublicDeployLocation() {
        assertEquals("/path/to/anonymous/archive.tar.gz", StringUtil.getPublicDeployLocation("/path/to/tcga4yeo/archive.tar.gz"));
    }

    @Test
    public void testGetPublicDeployLocationWhenNull() {
        assertNull(StringUtil.getPublicDeployLocation(null));
    }

    @Test
    public void testGetPublicDeployLocationWhenNotProtected() {
        assertNull(StringUtil.getPublicDeployLocation("/path/to/public/archive.tar.gz"));
    }

    @Test
    public void testIsValidTarOrTarGzPrivateDeployLocationWhenTar() {
        assertTrue(StringUtil.isValidTarOrTarGzProtectedDeployLocation("/tcga4yeo/archive.tar"));
    }

    @Test
    public void testIsValidTarOrTarGzPrivateDeployLocationWhenTarGz() {
        assertTrue(StringUtil.isValidTarOrTarGzProtectedDeployLocation("/tcga4yeo/archive.tar.gz"));
    }

    @Test
    public void testIsValidTarOrTarGzPrivateDeployLocationWhenInvalidExtension() {
        assertFalse(StringUtil.isValidTarOrTarGzProtectedDeployLocation("/tcga4yeo/archive.zip"));
    }

    @Test
    public void testIsValidTarOrTarGzPrivateDeployLocationWhenNotProtected() {
        assertFalse(StringUtil.isValidTarOrTarGzProtectedDeployLocation("/notProtected/archive.tar"));
    }

    @Test
    public void testIsValidTarOrTarGzPrivateDeployLocationWhenNull() {
        assertFalse(StringUtil.isValidTarOrTarGzProtectedDeployLocation(null));
    }

    @Test
    public void testGetMd5PrivateDeployLocation() {

        final String path = "/path";
        assertEquals(path + ".md5", StringUtil.getMd5ProtectedDeployLocation(path));
    }

    @Test
    public void testGetMd5PrivateDeployLocationWhenNull() {
        assertNull(StringUtil.getMd5ProtectedDeployLocation(null));
    }

    @Test
    public void testGetExplodedPrivateDeployLocationWhenNull() {
        assertNull(StringUtil.getExplodedProtectedDeployLocation(null));
    }

    @Test
    public void testGetExplodedPrivateDeployLocationWhenWrongExtension() {
        assertNull(StringUtil.getExplodedProtectedDeployLocation("/path/archive.zip"));
    }

    @Test
    public void testGetExplodedPrivateDeployLocationWhenTar() {
        assertEquals("/path/archive", StringUtil.getExplodedProtectedDeployLocation("/path/archive.tar"));
    }

    @Test
    public void testGetExplodedPrivateDeployLocationWhenTarGz() {
        assertEquals("/path/archive", StringUtil.getExplodedProtectedDeployLocation("/path/archive.tar.gz"));
    }
}
