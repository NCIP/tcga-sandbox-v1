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
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContent;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContentImpl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * TODO: INFO ABOUT CLASS
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class TabDelimitedContentWriterFastTest {

	private static final String SAMPLE_DIR = 
		Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
	
    @Test
    public void testWriteToFile() throws IOException ,ParseException {
        TabDelimitedContent contentObj = new TabDelimitedContentImpl();
        Map<Integer, String[]> content = new HashMap<Integer, String[]>();
        content.put(0, new String[]{"a", "b", "c", "d"});
        content.put(1, new String[]{"a1", "b1", "c1", "d1"});
        content.put(2, new String[]{"a2", "b2", "c2", "d2"});
        contentObj.setTabDelimitedContents(content);
        TabDelimitedContentWriter writer = new TabDelimitedContentWriter();
        File newFile = new File(SAMPLE_DIR + "qclive/tabDelimitedWriter/new.txt");
        writer.writeToFile(contentObj, newFile);
        assertTrue(newFile.exists());
        TabDelimitedFileParser parser = new TabDelimitedFileParser();
        // load expected data
        TabDelimitedContent expected = new TabDelimitedContentImpl();
        parser.setTabDelimitedContent(expected);
        parser.loadTabDelimitedContent(SAMPLE_DIR + "qclive/tabDelimitedWriter/expected.txt");
        // load just-written data
        TabDelimitedContent written = new TabDelimitedContentImpl();
        parser.setTabDelimitedContent(written);
        parser.loadTabDelimitedContent(newFile);
        // for each row in the written content, make sure each cell matches expected
        for (int row = 0; row < written.getTabDelimitedContents().size(); row++) {
            String[] writtenRow = written.getTabDelimitedContents().get(row);
            String[] expectedRow = expected.getTabDelimitedContents().get(row);
            for (int col = 0; col < writtenRow.length; col++) {
                assertEquals(expectedRow[col], writtenRow[col]);
            }
        }
        newFile.deleteOnExit();
    }
}
