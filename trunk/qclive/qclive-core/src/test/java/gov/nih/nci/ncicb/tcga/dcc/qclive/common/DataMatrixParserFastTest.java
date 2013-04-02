/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

/**
 * Test for DataMatrixParser class.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DataMatrixParserFastTest {
    private DataMatrixParser parser;
    private static final String SAMPLE_DIR = 
		Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private String directory = SAMPLE_DIR + "qclive/dataMatrix";

    @Before
    public void setup() {
        parser = new DataMatrixParser();
    }

    @Test
    public void testParse() throws DataMatrixParser.DataMatrixParseError, IOException {
        String directory = SAMPLE_DIR + "qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.0/";
        String file = "jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.beta-value.txt";

        DataMatrix matrix = parser.parse(file, directory);
        assertEquals("Hybridization REF", matrix.getNameType());
        assertEquals("TCGA-06-0132-01A-02D-0242-05", matrix.getNames()[0]);
        assertEquals(0, matrix.getConstantTypes().length);
        assertEquals("Beta value", matrix.getQuantitationTypes()[0]);
    }

    @Test
    public void testParseHeaders() throws DataMatrixParser.DataMatrixParseError {
        String majorHeader = "Hybridization REF\t\t\tsample1\tsample2\tsample3\n";
        String minorHeader = "Composite Element REF\tChr\tPos\tvalue\tvalue\tvalue\n";
        DataMatrix matrix = new DataMatrix();

        parser.parseHeaders(majorHeader, minorHeader, matrix);
        assertEquals("Hybridization REF", matrix.getNameType());
        assertEquals(3, matrix.getNames().length);
        assertEquals("sample1", matrix.getNames()[0]);
        assertEquals("Composite Element REF", matrix.getReporterType());
        assertEquals(2, matrix.getConstantTypes().length);
        assertEquals("Chr", matrix.getConstantTypes()[0]);
        assertEquals(3, matrix.getQuantitationTypes().length);
        assertEquals("value", matrix.getQuantitationTypes()[0]);
    }

    @Test
    public void testMissingTab() throws DataMatrixParser.DataMatrixParseError, IOException {
        String file = "Bad-Matrix-5.data.txt";
        boolean exceptionThrown = false;
        try {
            parser.parse(file, directory);
        }
        catch (DataMatrixParser.DataMatrixParseError ex) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testEmptyElement() throws DataMatrixParser.DataMatrixParseError, IOException {
        DataMatrix matrix = parser.parse("Blank-Element.data.txt", directory);
        assertEquals(4, matrix.getReporterCount());
        // if an exception is thrown, that is a problem, because blank element is valid for parsing        
    }
}
