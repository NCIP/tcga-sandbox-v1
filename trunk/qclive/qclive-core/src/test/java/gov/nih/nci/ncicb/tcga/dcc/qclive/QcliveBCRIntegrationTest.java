/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive;

import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * QcLive integration test for BCR archive
 *
 * @author Stanley Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class QcliveBCRIntegrationTest extends QcliveAbstractBCRIntegrationTest{

	/** Logger */
	private static final Logger logger = Logger.getLogger(QcliveBCRIntegrationTest.class);

	@Test
	public void testArchiveInfoRecords() {		
		logger.info(" testArchiveInfoRecords - IN ");	
		List<Map<String,Object>> archiveInfoRecords = retrieveArchiveInfoRecords();
		assertTrue(archiveInfoRecords != null && archiveInfoRecords.size() == 1);				
		assertEquals((String)archiveInfoRecords.get(0).get("ARCHIVE_NAME"), goldArchiveName);
		assertTrue(((java.util.Date)archiveInfoRecords.get(0).get("DATE_ADDED")).after(testStartDate));
		logger.info(" testArchiveInfoRecords - OUT ");	
	}

    /**
     * Checks that the archive + md5 is available in the ftp directory
     * @throws InterruptedException 
     */
    @Test
    public void testFSArchiveInFTP() throws InterruptedException {
    	logger.info(" testFSArchiveInFTP - IN ");	    
        final String tarGzFilepath = offlineBcrDir + File.separator + goldArchiveName + FileUtil.TAR_GZ;
        final String md5Filepath = tarGzFilepath + FileUtil.MD5;

        final long expectedTarGzSize = 80418;
        final long expectedMd5Size = 89;

        final String expectedMD5Sum = "bd821f087412e0bc24b9fd03416bdd52";

        // before checking the directory for files, sleep for a minute to make sure that all files are finished copying
        Thread.currentThread().sleep(60000);
        checkFSArchiveInFTP(tarGzFilepath, md5Filepath, expectedTarGzSize, expectedMd5Size, expectedMD5Sum);
    	logger.info(" testFSArchiveInFTP - OUT ");	
    }
}
