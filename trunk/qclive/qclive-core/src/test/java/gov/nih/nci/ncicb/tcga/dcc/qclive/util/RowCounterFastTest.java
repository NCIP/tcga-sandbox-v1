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
 * Date: May 21, 2009
 * Time: 12:33:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class RowCounterFastTest extends TestCase {

    RowCounter rowCounter;

    public void setUp() {
        rowCounter = new RowCounter();
    }

    public void testNoRows() {
        assertEquals( rowCounter.getRowCount( "xx" ), 0 );
    }

    public void testOneRow() {
        rowCounter.addToRowCounts( "xx" );
        assertEquals( rowCounter.getRowCount( "xx" ), 1 );
    }

    public void testThreeRows() {
        rowCounter.addToRowCounts( "xx", 3 );
        assertEquals( rowCounter.getRowCount( "xx" ), 3 );
    }

    public void testABunchOfRows() {
        rowCounter.addToRowCounts( "xx", 5 );
        rowCounter.addToRowCounts( "xx" );
        rowCounter.addToRowCounts( "yy" );
        rowCounter.addToRowCounts( "yy" );
        rowCounter.addToRowCounts( "yy" );
        assertEquals( rowCounter.getRowCount( "xx" ), 6 );
        assertEquals( rowCounter.getRowCount( "yy" ), 3 );
    }
}
