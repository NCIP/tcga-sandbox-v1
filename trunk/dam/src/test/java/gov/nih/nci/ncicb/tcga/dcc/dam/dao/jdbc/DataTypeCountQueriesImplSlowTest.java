/*
 *
 *  * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 *  * Copyright Notice.  The software subject to this notice and license includes both human
 *  * readable source code form and machine readable, binary, object code form (the "caBIG
 *  * Software").
 *  *
 *  * Please refer to the complete License text for full details at the root of the project.
 *
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.stats.DataTypeCount;

import java.io.File;

/**
 * DB unit test for DataTypeCountQueries.
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DataTypeCountQueriesImplSlowTest extends DBUnitTestCase {

private static final String PATH_TO_DB_PROPERTIES =
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;

    private static final String DB_PROPERTIES_FILE = "dccCommon.unittest.properties";

    private static final String DATA_FILE = "portal" + File.separator + "dao" + File.separator + "DataTypeCountQueries_Data.xml";


    private DataTypeCountQueriesImpl dataTypeCountQueries;

    public DataTypeCountQueriesImplSlowTest() {
        super(PATH_TO_DB_PROPERTIES, DATA_FILE, DB_PROPERTIES_FILE);
    }

    public void setUp() throws Exception {
        super.setUp();
        dataTypeCountQueries = new DataTypeCountQueriesImpl();
        dataTypeCountQueries.setDataSource(getDataSource());
    }

    public void testGetDataTypeCountArray() {
        DataTypeCount[] dataTypeCounts = dataTypeCountQueries.getDataTypeCountArray("DIS1");

        assertEquals(2, dataTypeCounts.length);

        assertEquals(DataTypeCount.CountType.Case, dataTypeCounts[0].getCountType());
        assertEquals(DataTypeCount.CountType.HealthyControl, dataTypeCounts[1].getCountType());
        
        assertEquals(100, dataTypeCounts[0].getTotal());
        assertEquals(0, dataTypeCounts[1].getTotal());
        assertEquals(50, dataTypeCounts[0].getExome());
        assertEquals(0, dataTypeCounts[1].getExome());
        assertEquals(80, dataTypeCounts[0].getmRna());
        assertEquals(0, dataTypeCounts[1].getmRna());

        dataTypeCounts = dataTypeCountQueries.getDataTypeCountArray("DIS2");
        assertEquals(500, dataTypeCounts[0].getTotal());
        assertEquals(10, dataTypeCounts[1].getTotal());
        assertEquals(400, dataTypeCounts[0].getSnp());
        assertEquals(0, dataTypeCounts[1].getSnp());
        assertEquals(250, dataTypeCounts[0].getExome());
        assertEquals(10, dataTypeCounts[1].getExome());

    }

    public void testCalculateAndSaveCounts() {
        dataTypeCountQueries.calculateAndSaveCounts();

        final DataTypeCount[] gbmCounts = dataTypeCountQueries.getDataTypeCountArray("GBM");
        assertEquals(DataTypeCount.CountType.Case, gbmCounts[0].getCountType());
        assertEquals(8, gbmCounts[0].getTotal());
        assertEquals(1, gbmCounts[0].getSnp());
        assertEquals(2, gbmCounts[0].getMethylation());
        assertEquals(5, gbmCounts[0].getmRna());
        assertEquals(1, gbmCounts[0].getMiRna());
        assertEquals(1, gbmCounts[0].getExome());
        assertEquals(3, gbmCounts[0].getClinical());

        assertEquals(DataTypeCount.CountType.HealthyControl, gbmCounts[1].getCountType());
        assertEquals(5, gbmCounts[1].getTotal());
        assertEquals(1, gbmCounts[1].getSnp());
        assertEquals(0, gbmCounts[1].getMethylation());
        assertEquals(1, gbmCounts[1].getmRna());
        assertEquals(0, gbmCounts[1].getMiRna());
        assertEquals(1, gbmCounts[1].getExome());
        assertEquals(2, gbmCounts[1].getClinical());

        final DataTypeCount[] ovCounts = dataTypeCountQueries.getDataTypeCountArray("OV");
        assertEquals(DataTypeCount.CountType.Case, ovCounts[0].getCountType());
        assertEquals(6, ovCounts[0].getTotal());
        assertEquals(0, ovCounts[0].getSnp());
        assertEquals(1, ovCounts[0].getMethylation());
        assertEquals(4, ovCounts[0].getmRna());
        assertEquals(1, ovCounts[0].getMiRna());
        assertEquals(2, ovCounts[0].getExome());
        assertEquals(0, ovCounts[0].getClinical());

        assertEquals(DataTypeCount.CountType.HealthyControl, ovCounts[1].getCountType());
        assertEquals(0, ovCounts[1].getTotal());
        assertEquals(0, ovCounts[1].getSnp());
        assertEquals(0, ovCounts[1].getMethylation());
        assertEquals(0, ovCounts[1].getmRna());
        assertEquals(0, ovCounts[1].getMiRna());
        assertEquals(0, ovCounts[1].getExome());
        assertEquals(0, ovCounts[1].getClinical());

        final DataTypeCount[] brcaCounts = dataTypeCountQueries.getDataTypeCountArray("BRCA");
        assertEquals(DataTypeCount.CountType.Case, brcaCounts[0].getCountType());
        assertEquals(4, brcaCounts[0].getTotal());
        assertEquals(2, brcaCounts[0].getSnp());
        assertEquals(1, brcaCounts[0].getMethylation());
        assertEquals(1, brcaCounts[0].getmRna());
        assertEquals(0, brcaCounts[0].getMiRna());
        assertEquals(1, brcaCounts[0].getExome());
        assertEquals(0, brcaCounts[0].getClinical());

        assertEquals(DataTypeCount.CountType.HealthyControl, brcaCounts[1].getCountType());
        assertEquals(0, brcaCounts[1].getTotal());
        assertEquals(0, brcaCounts[1].getSnp());
        assertEquals(0, brcaCounts[1].getMethylation());
        assertEquals(0, brcaCounts[1].getmRna());
        assertEquals(0, brcaCounts[1].getMiRna());
        assertEquals(0, brcaCounts[1].getExome());
        assertEquals(0, brcaCounts[1].getClinical());

        final DataTypeCount[] ucecCounts = dataTypeCountQueries.getDataTypeCountArray("UCEC");
        assertEquals(DataTypeCount.CountType.Case, ucecCounts[0].getCountType());
        assertEquals(1, ucecCounts[0].getTotal());
        assertEquals(0, ucecCounts[0].getSnp());
        assertEquals(0, ucecCounts[0].getMethylation());
        assertEquals(1, ucecCounts[0].getmRna());
        assertEquals(1, ucecCounts[0].getMiRna());
        assertEquals(0, ucecCounts[0].getExome());
        assertEquals(0, ucecCounts[0].getClinical());

        assertEquals(DataTypeCount.CountType.HealthyControl, ucecCounts[1].getCountType());
        assertEquals(4, ucecCounts[1].getTotal());
        assertEquals(0, ucecCounts[1].getSnp());
        assertEquals(1, ucecCounts[1].getMethylation());
        assertEquals(1, ucecCounts[1].getmRna());
        assertEquals(1, ucecCounts[1].getMiRna());
        assertEquals(1, ucecCounts[1].getExome());
        assertEquals(0, ucecCounts[1].getClinical());

        final DataTypeCount[] coadCounts = dataTypeCountQueries.getDataTypeCountArray("COAD");
        assertEquals(DataTypeCount.CountType.Case, coadCounts[0].getCountType());
        assertEquals(1, coadCounts[0].getTotal());
        assertEquals(0, coadCounts[0].getSnp());
        assertEquals(0, coadCounts[0].getMethylation());
        assertEquals(0, coadCounts[0].getmRna());
        assertEquals(1, coadCounts[0].getMiRna());
        assertEquals(0, coadCounts[0].getExome());
        assertEquals(0, coadCounts[0].getClinical());

        assertEquals(DataTypeCount.CountType.HealthyControl, coadCounts[1].getCountType());
        assertEquals(0, coadCounts[1].getTotal());
        assertEquals(0, coadCounts[1].getSnp());
        assertEquals(0, coadCounts[1].getMethylation());
        assertEquals(0, coadCounts[1].getmRna());
        assertEquals(0, coadCounts[1].getMiRna());
        assertEquals(0, coadCounts[1].getExome());
        assertEquals(0, coadCounts[1].getClinical());
    }

}
