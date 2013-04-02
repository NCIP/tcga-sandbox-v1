/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.util;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: May 13, 2009
 * Time: 10:59:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class StringTokenizerThatCanReturnNullFastTest extends TestCase {

    public void testNoNulls() {
        String s = "a\tb\tc";
        StringTokenizerThatCanReturnNull st = new StringTokenizerThatCanReturnNull( s, "\t" );
        assertTrue( st.hasMoreTokens() );
        assertEquals( "a", st.nextToken() );
        assertTrue( st.hasMoreTokens() );
        assertEquals( "b", st.nextToken() );
        assertTrue( st.hasMoreTokens() );
        assertEquals( "c", st.nextToken() );
        assertFalse( st.hasMoreTokens() );
    }

    public void testOneNull() {
        String s = "a\tb\t\tc";
        StringTokenizerThatCanReturnNull st = new StringTokenizerThatCanReturnNull( s, "\t" );
        assertTrue( st.hasMoreTokens() );
        assertEquals( "a", st.nextToken() );
        assertTrue( st.hasMoreTokens() );
        assertEquals( "b", st.nextToken() );
        assertTrue( st.hasMoreTokens() );
        assertNull( st.nextToken() );
        assertTrue( st.hasMoreTokens() );
        assertEquals( "c", st.nextToken() );
        assertFalse( st.hasMoreTokens() );
    }

    public void testConsecutiveNulls() {
        String s = "a\tb\t\t\tc";
        StringTokenizerThatCanReturnNull st = new StringTokenizerThatCanReturnNull( s, "\t" );
        assertTrue( st.hasMoreTokens() );
        assertEquals( "a", st.nextToken() );
        assertTrue( st.hasMoreTokens() );
        assertEquals( "b", st.nextToken() );
        assertTrue( st.hasMoreTokens() );
        assertNull( st.nextToken() );
        assertTrue( st.hasMoreTokens() );
        assertNull( st.nextToken() );
        assertTrue( st.hasMoreTokens() );
        assertEquals( "c", st.nextToken() );
        assertFalse( st.hasMoreTokens() );
    }

    public void testTrailingNull() {
        String s = "a\tb\t";
        StringTokenizerThatCanReturnNull st = new StringTokenizerThatCanReturnNull( s, "\t" );
        assertTrue( st.hasMoreTokens() );
        assertEquals( "a", st.nextToken() );
        assertTrue( st.hasMoreTokens() );
        assertEquals( "b", st.nextToken() );
        assertTrue( st.hasMoreTokens() );
        assertNull( st.nextToken() );
        assertFalse( st.hasMoreTokens() );
    }

    public void testReadPastEnd() {
        String s = "a\tb";
        StringTokenizerThatCanReturnNull st = new StringTokenizerThatCanReturnNull( s, "\t" );
        assertTrue( st.hasMoreTokens() );
        assertEquals( "a", st.nextToken() );
        assertTrue( st.hasMoreTokens() );
        assertEquals( "b", st.nextToken() );
        assertFalse( st.hasMoreTokens() );
        try {
            st.nextToken();
            fail(); //shouldn't get here
        }
        catch(IllegalStateException e) {
        }
    }
}
