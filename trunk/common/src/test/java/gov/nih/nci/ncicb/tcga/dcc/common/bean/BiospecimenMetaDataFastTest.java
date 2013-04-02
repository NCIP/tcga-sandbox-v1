/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.bean;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Test class for {@link BiospecimenMetaData}.
 *
 * @author Matt Nicholls
 *         Last updated by: nichollsmc
 */
public class BiospecimenMetaDataFastTest {

    private BiospecimenMetaData biospecimenMetaData;

    @Before
    public void before() {
        biospecimenMetaData = new BiospecimenMetaData("5eb28756-790a-4c1d-b0cd-56f57abd0dd5");
        biospecimenMetaData.setParentUUID("2464578d-92d8-4812-8d8b-3c27f785d44c");
        biospecimenMetaData.setSampleType("01");
        biospecimenMetaData.setVialId("A");
        biospecimenMetaData.setPortionId("21");
        biospecimenMetaData.setAnalyteType("DNA");
    }

    @Test
    public void testToString() {

        StringBuffer expectedOutput = new StringBuffer();
        expectedOutput.append("<tcgaElement href=\"5eb28756-790a-4c1d-b0cd-56f57abd0dd5\">\n");
        expectedOutput.append("    <parentUUID>2464578d-92d8-4812-8d8b-3c27f785d44c</parentUUID>\n");
        expectedOutput.append("    <sampleType>01</sampleType>\n");
        expectedOutput.append("    <analyteType>DNA</analyteType>\n");
        expectedOutput.append("    <vialId>A</vialId>\n");
        expectedOutput.append("    <portionId>21</portionId>\n");
        expectedOutput.append("</tcgaElement>");

        assertEquals(expectedOutput.toString(), biospecimenMetaData.toString().trim());
    }

    @Test
    public void testObjectCopy() {

        final BiospecimenMetaData copy = BiospecimenMetaData.newInstance(biospecimenMetaData);
        assertEquals(copy.toString(), biospecimenMetaData.toString());

        // Change the copy and compare, source and copy should be different
        copy.setUuid("61f0414d-12e7-456b-ace4-ee63e2638b44");
        assertFalse(biospecimenMetaData.getUuid().equals(copy.getUuid()));
    }
}
