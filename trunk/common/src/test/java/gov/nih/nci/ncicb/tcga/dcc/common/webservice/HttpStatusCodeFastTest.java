/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.webservice;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * HttpStatusCode Unit tests
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class HttpStatusCodeFastTest {

    @Test
    public void testExistingHttpStatusCode() {
        assertEquals("OK", HttpStatusCode.getMessageForHttpStatusCode(HttpStatusCode.OK));
    }

    @Test
    public void testNonExistingHttpStatusCode() {
        assertEquals("Unknown HTTP status code", HttpStatusCode.getMessageForHttpStatusCode(0));
    }
}
