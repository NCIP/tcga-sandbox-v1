/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common;

import junit.framework.TestCase;

/**
 * @author Robert S. Sfeir
 */
public class ProtocolNameValidatorFastTest extends TestCase {

    public void testProtocolNameValidator() {
        final ProtocolNameValidator pnv = new ProtocolNameValidator("mskcc.org:labeling:Agilent-HG-CGH-244K_A:01");
        assertEquals(4, pnv.getGroupCount());
        assertEquals("mskcc.org", pnv.getDomain());
        assertEquals("labeling", pnv.getProtocolType());
        assertEquals("Agilent-HG-CGH-244K_A", pnv.getPlatform());
        assertEquals("01", pnv.getVersion());
        assertTrue(pnv.isValid());
    }

    public void testProtocolInvalid() {

        final ProtocolNameValidator protocolNameValidator = new ProtocolNameValidator("not:valid");
        assertFalse(protocolNameValidator.isValid());
    }
}