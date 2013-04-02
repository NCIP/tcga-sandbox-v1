/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test class for AbstractArchiveFileProcessor
 *
 * @author Stan Girshik Last updated by: $Author$
 * @version $Rev$
 */
public class AbstractArchiveFileProcessorFastTest {

	AbstractArchiveFileProcessor processor;
	Archive archive;	
	private final String SAMPLES_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;
    private final String dirLister = SAMPLES_DIR + 
    		"qclive" + File.separator + "directoryLister"; 
	
	@Before
	public void setUp(){		
		archive = new Archive();
		archive.setDeployLocation(dirLister + ".tar.gz");		
	}

    @Test
    public void testGetFilesForExtension() {

        processor = new MockFileProcessor("*.txt,*.arch");
        final File[] files = processor.getFilesForExtension(archive);

        final List<String> fileNames = new ArrayList<String>();
        assertNotNull(files);

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
	public void testGetEmptyFilesForExtension(){
		processor = new MockFileProcessor("*.wrong,*.stuff");		
		File [] files = processor.getFilesForExtension(archive);
		Assert.assertNull(files);	
	}
	
	@Test
	public void testGetNullFilesForExtension(){
		processor = new MockFileProcessor(null);		
		File [] files = processor.getFilesForExtension(archive);
		Assert.assertNull(files);	
	}
	
	@Test
	public void testGetSingleFileForExtension(){
		processor = new MockFileProcessor("arch");		
		File [] files = processor.getFilesForExtension(archive);	
		Assert.assertTrue(files.length == 1);
		Assert.assertEquals("archive.arch",files[0].getName());	
	}
	
	
	private class MockFileProcessor extends AbstractArchiveFileProcessor{
		private String fileExt;		
		public MockFileProcessor(String fileExtension){
			fileExt = fileExtension;
		}
		
		@Override
		public String getName() {			
			return null;
		}

		@Override
		protected Object getReturnValue(Map results, QcContext context) {
			return null;
		}

		@Override
		protected Object processFile(File file, QcContext context)
				throws ProcessorException {
			return null;
		}

		@Override
		protected Object getDefaultReturnValue(Archive archive) {
			return null;
		}

		@Override
		protected String getFileExtension() {			
			return fileExt;
		}

		@Override
		protected boolean isCorrectArchiveType(Archive archive)
				throws ProcessorException {
			return false;
		}

	}
	
}
