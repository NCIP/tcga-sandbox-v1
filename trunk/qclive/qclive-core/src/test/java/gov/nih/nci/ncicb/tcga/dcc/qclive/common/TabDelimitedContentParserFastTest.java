/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContentImpl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.fail;
/**
 * Class to test various Tab Delimited parser loading features
 *
 * @author Robert S. Sfeir
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 2318 $
 */
public class TabDelimitedContentParserFastTest {
	
	private static final String SAMPLE_DIR = 
		Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private String fileName = SAMPLE_DIR + "qclive/sdrf/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA002_CPI.1.sdrf.txt";
    private String missingColumnsSDRF = SAMPLE_DIR+"qclive/sdrf/missing-columns.sdrf.txt";
    final TabDelimitedFileParser tabDelimitedFileParser = new TabDelimitedFileParser();

    @Test
    public void testLoadTabContentFromFile() throws IOException {
        assertTrue(tabDelimitedFileParser.getTabDelimitedContent().getTabDelimitedContents().containsKey(0));
        assertTrue(tabDelimitedFileParser.getTabDelimitedContent().getTabDelimitedContents().containsKey(43));
        assertEquals(tabDelimitedFileParser.getTabDelimitedContent().getTabDelimitedContents().size(), 44);
    }

    @Test
    public void testLoadTabDelimitedContentHeader() throws IOException {
        tabDelimitedFileParser.loadTabDelimitedContentHeader();
        assertEquals(tabDelimitedFileParser.getTabDelimitedContent().getTabDelimitedHeaderValues()[0], "Extract Name");
        assertEquals(tabDelimitedFileParser.getTabDelimitedContent().getTabDelimitedHeaderValues().length, 21);
    }

    @Before
    public void setUp() throws IOException,ParseException {
        tabDelimitedFileParser.setTabDelimitedContent(new TabDelimitedContentImpl());
        tabDelimitedFileParser.loadTabDelimitedContent(fileName);
    }

    @Test
    public void invalidSdrf() throws IOException{
        try{
            tabDelimitedFileParser.loadTabDelimitedContent(new File(missingColumnsSDRF), true);
            fail("Expected to fail with parse exception ");
        }catch(ParseException e){
            assertEquals("Error at line no 2: Expected 25 columns but found 18 columns.",e.getMessage());
        }
    }
}
