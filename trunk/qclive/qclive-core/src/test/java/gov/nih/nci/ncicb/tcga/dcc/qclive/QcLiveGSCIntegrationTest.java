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
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * QcLive integration testing for GSC archives
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class QcLiveGSCIntegrationTest extends QcLiveAbstractGSCIntegrationTest {

    /**
     * Logger
     */
    private static final Logger logger = Logger.getLogger(QcLiveGSCIntegrationTest.class);

    @Test
    public void testArchiveInfoRecords() {

        logger.info("testArchiveInfoRecords - IN");

        final String dateAddedKey = "DATE_ADDED";
        final String archiveNameKey = "ARCHIVE_NAME";

        final List<Map<String, Object>> archiveInfoRecords = retrieveArchiveInfoRecords();
        assertNotNull(archiveInfoRecords);
        assertEquals(1, archiveInfoRecords.size());

        final Map<String, Object> firstArchiveInfoRecord = archiveInfoRecords.get(0);
        assertNotNull(firstArchiveInfoRecord);
        assertTrue(firstArchiveInfoRecord.containsKey(dateAddedKey));
        assertTrue(firstArchiveInfoRecord.containsKey(archiveNameKey));

        final Date dateAdded = (Date) firstArchiveInfoRecord.get(dateAddedKey);
        assertFalse(dateAdded.before(testStartDate));

        final String archiveName = (String) firstArchiveInfoRecord.get(archiveNameKey);
        assertEquals(archiveName, goldArchiveName);

        logger.info("testArchiveInfoRecords - OUT");
    }

    /**
     * Checks that the archive + md5 is available in the ftp directory
     */
    @Test
    public void testFSArchiveInFTP() {

        logger.info("testFSArchiveInFTP - IN");

        final String tarGzFilepath = offlineGscDir + File.separator + goldArchiveName + FileUtil.TAR_GZ;
        final String md5Filepath = tarGzFilepath + FileUtil.MD5;

        final long expectedTarGzSize = 3448;
        final long expectedMd5Size = 87;

        final String expectedMD5Sum = "bd615464af4df7c20eaf8ad98b330e43";

        checkFSArchiveInFTP(tarGzFilepath, md5Filepath, expectedTarGzSize, expectedMd5Size, expectedMD5Sum);

        logger.info("testFSArchiveInFTP - OUT");
    }
}
