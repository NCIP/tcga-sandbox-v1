/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.generation;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class for BioTabFileGenerator
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BioTabDataProcessorUtilFastTest {

    private BioTabDataProcessorUtil bioTabDataProcessorUtil;

    @Before
    public void setup() throws Exception {
        bioTabDataProcessorUtil = new BioTabDataProcessorUtil();
    }

    @Test
    public void transformDayMonthYearToDate(){

        Map<String,String> testData = new HashMap<String,String>();
        testData.put("day_of_test1","01");
        testData.put("month_of_test1","03");
        testData.put("year_of_test1","2001");
        testData.put("day_of_test2","01");
        testData.put("month_of_test2","06");
        testData.put("day_of_test3","null");
        testData.put("month_of_test3","null");
        testData.put("year_of_test3","null");
        testData.put("day_of_test4","[NOT Available]");
        testData.put("month_of_test4","04");
        testData.put("year_of_test4","2010");
        testData.put("year_of_test5","2010");

        testData = bioTabDataProcessorUtil.transformDayMonthYearToDate(testData);

        assertEquals(6,testData.size());
        assertEquals("2001-03-01",testData.get("date_of_test1"));
        assertEquals("01",testData.get("day_of_test2"));
        assertEquals("06",testData.get("month_of_test2"));
        assertEquals("NA",testData.get("date_of_test3"));
        assertEquals("2010-04-00",testData.get("date_of_test4"));
        assertEquals("2010",testData.get("year_of_test5"));

    }

    @Test
    public void transformDayMonthYearToDateV2() {
        Map<String,String> testData = new HashMap<String, String>();
        testData.put("day_of_test1", "01");
        testData.put("month_of_test1", "03");
        testData.put("year_of_test1", "2001");
        testData.put("day_of_test2", "01");
        testData.put("month_of_test2", "03");

        testData = bioTabDataProcessorUtil.transformDayMonthYearToDate(testData);

        assertEquals(3, testData.size());
        assertEquals("2001-03-01", testData.get("date_of_test1"));
        assertEquals("01", testData.get("day_of_test2"));
        assertEquals("03", testData.get("month_of_test2"));
    }

     @Test
    public void transformDayMonthYearToDateColumnHeader(){

        List<String> testData = new ArrayList<String>();
        testData.add("day_of_test1");
        testData.add("month_of_test1");
        testData.add("year_of_test1");
        testData.add("day_of_test2");
        testData.add("month_of_test2");
         testData.add("col3");

        testData = bioTabDataProcessorUtil.transformDayMonthYearToDateColumnHeader(testData);

        assertEquals(4,testData.size());
        assertTrue(testData.containsAll(Arrays.asList("date_of_test1","day_of_test2","month_of_test2","col3")));

    }
}
