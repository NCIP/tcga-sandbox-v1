/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.live.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

/**
 * Test class for TCGAFileFilter
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class TCGAFileFilterFastTest {

    @Test
    public void testAccept() {
        TCGAFileFilter filter = new TCGAFileFilter();
        filter.setFileExtensions(Arrays.asList(".hi", ".bye"));
        assertTrue(filter.accept(null, "something.hi"));
        assertTrue(filter.accept(null, "anotherThing.bye"));
        assertTrue(filter.accept(null, "CAPSLOCK.HI"));
        assertTrue(filter.accept(null, "CamelCase.Hi"));
        assertTrue(filter.accept(null, "a.b.c.d.bye"));

        assertFalse(filter.accept(null, "blah"));
        assertFalse(filter.accept(null, "hi")); // extension has dot in it
        assertFalse(filter.accept(null, "hello.hi.txt"));
        assertFalse(filter.accept(null, "archive.hi.filepart"));
    }
}
