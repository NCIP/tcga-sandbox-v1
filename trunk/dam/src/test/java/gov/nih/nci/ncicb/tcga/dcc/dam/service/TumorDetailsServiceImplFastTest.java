/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import static org.junit.Assert.assertArrayEquals;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.stats.DataTypeCount;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataTypeCountQueries;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * TumorDetailsServiceImpl unit tests
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class TumorDetailsServiceImplFastTest {

    private Mockery context = new JUnit4Mockery();
    private DataTypeCountQueries mockDataTypeCountQueries;

    private TumorDetailsServiceImpl tumorDetailsService;

    @Before
    public void setUp() {
        mockDataTypeCountQueries = context.mock(DataTypeCountQueries.class);
        tumorDetailsService = new TumorDetailsServiceImpl();
        tumorDetailsService.setDataTypeCountQueries(mockDataTypeCountQueries);
    }

    @Test
    public void testGetTumorSampleTypeCountArray() {
        final DataTypeCount[] expectedDataTypeCountArray = new DataTypeCount[0];
        context.checking(new Expectations() {{
            one(mockDataTypeCountQueries).getDataTypeCountArray("DIS");
            will(returnValue(expectedDataTypeCountArray));
        }});

        assertArrayEquals(expectedDataTypeCountArray, tumorDetailsService.getTumorDataTypeCountArray("DIS"));

    }

    @Test
    public void testCalculateAndSave() {
        context.checking(new Expectations() {{
            one(mockDataTypeCountQueries).calculateAndSaveCounts();
        }});
        
        tumorDetailsService.calculateAndSaveTumorDataTypeCounts();
    }

}
