/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.webservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.stats.TumorMainCount;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.TumorMainService;

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
 * TumorMainWSTest unit tests
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class TumorMainWSFastTest {

    private Mockery context = new JUnit4Mockery();
    private TumorMainService mockTumorMainService;
    private TumorMainWS tumorMainWS;

    @Before
    public void setup() {
        mockTumorMainService = context.mock(TumorMainService.class);
        tumorMainWS = new TumorMainWS();
        tumorMainWS.setTumorMainService(mockTumorMainService);
    }

    @Test
    public void testGetTumorMainCountList() throws TumorMainService.TumorMainServiceException {

        final TumorMainCount tumorMainCount1 = new TumorMainCount("AZERTY", "A", 111, 222, "10/21/10");
        final TumorMainCount tumorMainCount2 = new TumorMainCount("Gnu is Not Unix", "GNU", 111, 222, "10/21/10");

        final List<TumorMainCount> expectedTumorMainCountList = new LinkedList<TumorMainCount>();
        expectedTumorMainCountList.add(tumorMainCount1);
        expectedTumorMainCountList.add(tumorMainCount2);

        context.checking(new Expectations() {{
            allowing(mockTumorMainService).getTumorMainCountList();
            will(returnValue(expectedTumorMainCountList));
        }});

        final List<TumorMainCount> actualTumorMainCountList = tumorMainWS.getTumorMainCountList();

        assertNotNull(actualTumorMainCountList);
        assertEquals("Unexpected list size: ", 2, actualTumorMainCountList.size());
        assertEquals("Unexpected Tumor Abbreviation: ", "A", actualTumorMainCountList.get(0).getTumorAbbreviation());
        assertEquals("Unexpected Tumor Abbreviation: ", "GNU", actualTumorMainCountList.get(1).getTumorAbbreviation());
    }
}
