/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.stats.TumorMainCount;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.TumorMainCountQueries;

import java.util.LinkedList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * TumorMainServiceImpl unit tests
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class TumorMainServiceImplFastTest {

    private Mockery context = new JUnit4Mockery();
    private TumorMainCountQueries mockTumorMainCountQueries;
    private TumorMainServiceImpl tumorMainService;

    @Before
    public void setUp() {

        mockTumorMainCountQueries = context.mock(TumorMainCountQueries.class);
        tumorMainService = new TumorMainServiceImpl();
        tumorMainService.setTumorMainCountQueries(mockTumorMainCountQueries);
    }

    @Test
    public void testGetTumorMainCountList() throws TumorMainCountQueries.TumorMainCountQueriesException {

        final TumorMainCount tumorMainCountA = new TumorMainCount();
        tumorMainCountA.setTumorAbbreviation("A");

        final TumorMainCount tumorMainCountB = new TumorMainCount();
        tumorMainCountB.setTumorAbbreviation("B");

        final List<TumorMainCount> expectedTumorMainCountList = new LinkedList<TumorMainCount>();
        expectedTumorMainCountList.add(tumorMainCountA);
        expectedTumorMainCountList.add(tumorMainCountB);

        context.checking(new Expectations() {{
            allowing(mockTumorMainCountQueries).getAllTumorMainCount();
            will(returnValue(expectedTumorMainCountList));
        }});

        try {
            List<TumorMainCount> tumorMainCountList = tumorMainService.getTumorMainCountList();

            assertNotNull(tumorMainCountList);
            assertEquals("Unexpected list size: ", 2, tumorMainCountList.size());
            assertEquals("Unexpected TumorMainCount: ", tumorMainCountA, tumorMainCountList.get(0));
            assertEquals("Unexpected TumorMainCount: ", tumorMainCountB, tumorMainCountList.get(1));

        } catch (TumorMainService.TumorMainServiceException e) {
            fail("Unexpected TumorMainServiceException");
        }
    }
}
