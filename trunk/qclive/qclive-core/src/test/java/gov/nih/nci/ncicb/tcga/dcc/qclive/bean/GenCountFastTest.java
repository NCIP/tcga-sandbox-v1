/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG(TM)
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: kaned
 * Date: Jul 30, 2009
 * Time: 2:25:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class GenCountFastTest {

    @Before
    public void setup() {
    }

    @Test
    public void testSetter() throws Exception {
        GenCount summary = new GenCount();
        assertEquals(0L,(long)summary.getCountAsNumber());
        summary.setCountAsNumber("12");
        assertEquals(12L,(long)summary.getCountAsNumber());
        summary.setCountAsNumber((String)null);
        assertEquals(null,summary.getCountAsNumber());
        summary.setCountAsNumber("15");
        assertEquals(15L,(long)summary.getCountAsNumber());
        summary.setCountAsNumber("NaN");
        assertEquals(null,summary.getCountAsNumber());
    }

}

