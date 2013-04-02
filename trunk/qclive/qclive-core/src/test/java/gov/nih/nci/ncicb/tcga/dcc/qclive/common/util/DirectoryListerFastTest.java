/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for DirectoryLister class
 *
 * @author Stanley Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DirectoryListerFastTest {
	
	private static final String SAMPLES_DIR = 
	    	Thread.currentThread().getContextClassLoader()
	    		.getResource("samples").getPath() + File.separator;	
	
	private final String theDirWFiles = SAMPLES_DIR + 
			File.separator + "qclive" + File.separator + "directoryLister";


    @Test
    public void getFilesInDirectoryByFilePatternListTest() {

        final List<String> filePatternList = new ArrayList<String>();
        filePatternList.add("*.txt");
        filePatternList.add("*.arch");

        final File[] files = DirectoryListerImpl.getFilesByPattern(SAMPLES_DIR +
                File.separator + "qclive" + File.separator + "directoryLister"
                , filePatternList);

        assertNotNull(files);

        final List<String> fileNames = new ArrayList<String>();
        for(final File file : files) {

            if(file != null) {
                fileNames.add(file.getName());
            }
        }

        assertTrue(fileNames.contains("anotherFile.txt"));
        assertTrue(fileNames.contains("archive.arch"));
        assertTrue(fileNames.contains("someFile.txt"));
    }
	
	@Test
	public void getFilesInDirectoryByFilePatternListEmptyTest (){
		List <String> filePatternList = new ArrayList<String>();	
		
		Assert.assertNull(DirectoryListerImpl.getFilesByPattern(theDirWFiles
				, filePatternList));
		
		Assert.assertNull(DirectoryListerImpl.getFilesByPattern(null,
				filePatternList));
		
		filePatternList.add("*.arch");
		Assert.assertNull(DirectoryListerImpl.getFilesByPattern(null,
				filePatternList));
		
		Assert.assertNull(DirectoryListerImpl.getFilesByPattern(theDirWFiles,
				null));
	}
	
	@Test
	public void getFilesInDirectoryByFilePatternListEmptyPatternTest (){
		List <String> filePatternList = new ArrayList<String>();
		filePatternList.add("*.sra.com");
		
		Assert.assertNull(DirectoryListerImpl.getFilesByPattern(theDirWFiles,
				filePatternList));
	}
}
