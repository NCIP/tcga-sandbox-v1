/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Class to test the added Alphanumeric comparator for sorting collections
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@SuppressWarnings("unchecked")
public class AlphanumComparatorFastTest {

    List<String> testList;

    @Before
    public void before() throws Exception {
        testList = new LinkedList<String>();
        testList.add("2 bouh");
        testList.add("A23BOP45");
        testList.add("AB3BOP45");
        testList.add("AB1BOP45");
        testList.add("assertion4");
        testList.add("assertion3");
    }

    @Test
    public void testAlphaNumComparator(){
        java.util.Collections.sort(testList,new AlphanumComparator());
        assertNotNull(testList);
        assertEquals(6,testList.size());
        assertEquals("2 bouh",testList.get(0));
        assertEquals("A23BOP45",testList.get(1));
        assertEquals("AB1BOP45",testList.get(2));
        assertEquals("AB3BOP45",testList.get(3));
        assertEquals("assertion3",testList.get(4));
        assertEquals("assertion4",testList.get(5));
    }


}
