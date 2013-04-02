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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Test class for ExportUtils
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ExportUtilsFastTest {
    @Test
    public void testGetExportStringNull() {
        String str = ExportUtils.getExportString(null, null);
        assertEquals("", str);
    }

    @Test
    public void testGetExportString() {
        String str = ExportUtils.getExportString("Hello", null);
        assertEquals("Hello", str);
    }

    @Test
    public void testGetExportStringNewline() {
        String str = ExportUtils.getExportString("Hello\nWorld", null);
        assertEquals("Hello World", str);
    }

    @Test
    public void testGetExportStringDate() {
        String str = ExportUtils.getExportString(new Date(1234567), new SimpleDateFormat("MM/dd/yyyy"));
        assertEquals("12/31/1969", str);
    }

    @Test
    public void testGetExportStringCollection() {
        String str = ExportUtils.getExportString(Arrays.asList("1", 123, false), null);
        assertEquals("1; 123; false", str);
    }
}
