/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.view;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;

import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

/**
 * TODO: INFO ABOUT CLASS
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class CellFastTest {

    private Cell cellA, cellAP, cellPN, cellNNA, cellNA;

    @Before
    public void setup() {
        cellA = new Cell();
        cellAP = new Cell();
        cellPN = new Cell();
        cellNNA = new Cell();
        cellNA = new Cell();
        cellA.addDataset( makeDataSet( DataAccessMatrixQueries.AVAILABILITY_AVAILABLE,
                DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITH_MATCHED_NORMAL, false ) );
        cellAP.addDataset( makeDataSet( DataAccessMatrixQueries.AVAILABILITY_AVAILABLE,
                DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITHOUT_MATCHED_NORMAL, true ) );
        cellAP.addDataset( makeDataSet( DataAccessMatrixQueries.AVAILABILITY_PENDING, null, true ) );
        cellPN.addDataset( makeDataSet( DataAccessMatrixQueries.AVAILABILITY_PENDING, null, false ) );
        cellPN.addDataset( makeDataSet( DataAccessMatrixQueries.AVAILABILITY_NOTAVAILABLE, null, true ) );
        cellNNA.addDataset( makeDataSet( DataAccessMatrixQueries.AVAILABILITY_NOTAVAILABLE, null, false ) );
        cellNNA.addDataset( makeDataSet( DataAccessMatrixQueries.AVAILABILITY_NOTAPPLICABLE, null, false ) );
        cellNA.addDataset( makeDataSet( DataAccessMatrixQueries.AVAILABILITY_NOTAPPLICABLE, null, true ) );
    }

    @Test
    public void testGetAvailability() {
        // just A should be A
        assertEquals( cellA.getAvailability(), DataAccessMatrixQueries.AVAILABILITY_AVAILABLE );
        // A and P should be A
        assertEquals( cellAP.getAvailability(), DataAccessMatrixQueries.AVAILABILITY_AVAILABLE );
        // P and N should be P
        assertEquals( cellPN.getAvailability(), DataAccessMatrixQueries.AVAILABILITY_PENDING );
        // N and N/A should be N
        assertEquals( cellNNA.getAvailability(), DataAccessMatrixQueries.AVAILABILITY_NOTAVAILABLE );
        // just N/A should be N/A
        assertEquals( cellNA.getAvailability(), DataAccessMatrixQueries.AVAILABILITY_NOTAPPLICABLE );
    }

    @Test
    public void testGetTumorNormal() {
        // add an unmatched tumor to cellA
        cellA.addDataset( makeDataSet( DataAccessMatrixQueries.AVAILABILITY_AVAILABLE,
                DataAccessMatrixQueries.TUMORNORMAL_HEALTHY_TISSUE_CONTROL, false ) );
        // make sure it still resolves to tumor with normal, since other data set has normal
        assertEquals( cellA.getTumorNormal(), DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITH_MATCHED_NORMAL );
        assertEquals( cellAP.getTumorNormal(), DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITHOUT_MATCHED_NORMAL );
        assertNull( cellPN.getTumorNormal() );
    }

    @Test
    public void testIsProtected() {
        // cellA has one public data set
        assertFalse( cellA.isProtected() );
        // cellAP has two protected data sets
        assertTrue( cellAP.isProtected() );
        // cellPN has one public and one protected data set
        assertTrue( cellPN.isProtected() );
        // cellNNA has two public data sets
        assertFalse( cellNNA.isProtected() );
        // cellNA has one protected data set
        assertTrue( cellNA.isProtected() );
    }

    @Test
    public void testDates() {
        Date jan1 = new GregorianCalendar( 2008, 1, 1 ).getTime();
        Date jan31 = new GregorianCalendar( 2008, 1, 31 ).getTime();
        DataSet ds1 = new DataSet();
        ds1.setDateAdded( jan1 );
        DataSet ds2 = new DataSet();
        ds2.setDateAdded( jan31 );
        Cell cell = new Cell();
        cell.addDataset( ds1 );
        cell.addDataset( ds2 );
        // cell's latest date added should be jan 31
        assertEquals( jan31, cell.getLatestDateAdded() );
        assertEquals( jan1, cell.getEarliestDateAdded() );
        DataSet ds3 = new DataSet(); // no date set in this one
        Cell cell2 = new Cell();
        cell2.addDataset( ds3 );
        cell2.addDataset( ds1 );
        // make sure the null date does not show up as one of the dates
        assertNotNull( cell.getLatestDateAdded() );
        assertNotNull( cell.getEarliestDateAdded() );
    }

    private DataSet makeDataSet( String availability, String tumorNormal, boolean isProtected ) {
        DataSet ds = new DataSet();
        ds.setAvailability( availability );
        ds.setTumorNormal( tumorNormal );
        ds.setProtected( isProtected );
        return ds;
    }
}
