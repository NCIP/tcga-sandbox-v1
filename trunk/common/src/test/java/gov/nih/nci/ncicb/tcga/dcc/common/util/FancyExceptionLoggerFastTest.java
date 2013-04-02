/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Test class for the FancyExceptionLogger
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class FancyExceptionLoggerFastTest {

    @Test
    public void printException() throws Exception {
        Exception e = new Exception("I am The Evil Exception");
        e.initCause(new Exception("I am The Evil Exception Cause"));
        String res = FancyExceptionLogger.printException(e);
        assertNotNull(res);
        assertTrue(res.contains("I am The Evil Exception"));
        assertTrue(res.contains("I am The Evil Exception Cause"));
    }

}//End of Class
