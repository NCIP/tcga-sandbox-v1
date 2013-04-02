/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContent;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContentImpl;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Class to test the TabDelimitedContentNavigator
 *
 * @author Robert S. Sfeir
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 2318 $
 */
public class TabDelimitedContentNavigatorFastTest {

    final TabDelimitedContent tabDelimitedContent = new TabDelimitedContentImpl();
    final TabDelimitedContentNavigator nav = new TabDelimitedContentNavigator();

    @Before
    public void setUp() {
        Map<Integer, String[]> hashMap = new HashMap<Integer, String[]>();
        hashMap.put( 5, new String[]{"TCGA-01-06-03-03", "6789.txt", "1244", "1234.txt"} );
        hashMap.put( 1, new String[]{"TCGA-01-02-08-09", "6788.txt", "1245", "1235.txt"} );
        hashMap.put( 2, new String[]{"TCGA-01-02-06-04", "6787.txt", "1246", "1236.txt"} );
        hashMap.put( 3, new String[]{"TCGA-01-02-02-04", "6786.txt", "1247", "1237.txt"} );
        hashMap.put( 4, new String[]{"TCGA-01-02-03-05", "6785.txt", "1248", "1238.txt"} );
        tabDelimitedContent.setTabDelimitedContents( hashMap );
        tabDelimitedContent.setTabDelimitedHeader( new String[]{"Robert", "John", "Albert", "Jack"} );
        nav.setTabDelimitedContent( tabDelimitedContent );
    }

    @After
    public void tearDown() {
        // Add your code here
    }

    @Test
    public void testGetHeaderColumn() {
        Integer headerID = nav.getHeaderIDByName( "John" );
        assertEquals( headerID, new Integer( 1 ) );
    }

    @Test
    public void testGetTabDelimitedContentRow() {
        String[] strings = nav.getRowByID( 2 );
        assertEquals( strings[0], "TCGA-01-02-06-04" );
    }

    @Test
    public void testGetValueByCoordinates() {
        assertEquals( nav.getValueByCoordinates( 2, 3 ), "1247" );
    }

    @Test
    public void testGetFullColumnValues() {
        Map theValues = nav.getFullColumnValues( 3 );
        assertEquals( theValues.get( 5 ), "1234.txt" );
        assertEquals( theValues.get( 1 ), "1235.txt" );
        assertEquals( theValues.get( 2 ), "1236.txt" );
        assertEquals( theValues.get( 3 ), "1237.txt" );
        assertEquals( theValues.get( 4 ), "1238.txt" );
    }

    @Test
    public void testGetMultipleColumnValues() {
        Map theCombinedColumns = nav.getMultipleColumnValues( 0, 2, 3 );
        String[] values = (String[]) theCombinedColumns.get( 5 );
        assertEquals( values[0], "TCGA-01-06-03-03" );
        assertEquals( values[1], "1244" );
        assertEquals( values[2], "1234.txt" );
        values = (String[]) theCombinedColumns.get( 1 );
        assertEquals( values[0], "TCGA-01-02-08-09" );
        assertEquals( values[1], "1245" );
        assertEquals( values[2], "1235.txt" );
        values = (String[]) theCombinedColumns.get( 2 );
        assertEquals( values[0], "TCGA-01-02-06-04" );
        assertEquals( values[1], "1246" );
        assertEquals( values[2], "1236.txt" );
        values = (String[]) theCombinedColumns.get( 3 );
        assertEquals( values[0], "TCGA-01-02-02-04" );
        assertEquals( values[1], "1247" );
        assertEquals( values[2], "1237.txt" );
        values = (String[]) theCombinedColumns.get( 4 );
        assertEquals( values[0], "TCGA-01-02-03-05" );
        assertEquals( values[1], "1248" );
        assertEquals( values[2], "1238.txt" );
        values = (String[]) theCombinedColumns.get( 0 );
        assertEquals( values[0], "Robert" );
        assertEquals( values[1], "Albert" );
        assertEquals( values[2], "Jack" );
    }

    @Test
    public void testGetMultipleColumnValuesByName() {
        Map colVals = nav.getMultipleColumnValues( "Robert", "Albert" );
        String[] values = (String[]) colVals.get( 1 );
        assertEquals( values[0], "TCGA-01-02-08-09" );
        assertEquals( values[1], "1245" );
        values = (String[]) colVals.get( 3 );
        assertEquals( values[0], "TCGA-01-02-02-04" );
        assertEquals( values[1], "1247" );
    }
}
