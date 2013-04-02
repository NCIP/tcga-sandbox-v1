/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.cli.ParseException;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * QcLive integration test for GSC archive
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class QcLiveAbstractGSCIntegrationTest extends QcliveAbstractBaseIntegrationTest {

	private static final String SAMPLES_DIR = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    protected static final String rootDir = File.separator + "tcgafiles";
    protected static final String ftpAuthDir = rootDir + File.separator + "ftp_auth";
    protected static final String depositDir = ftpAuthDir + File.separator + "deposit_ftpusers";
    protected static final String tcgaDir = depositDir + File.separator + "tcga";
    protected static final String offlineDir = depositDir + File.separator + "offline";
    protected static final String offlineCenterDir = offlineDir + File.separator + "genome.wustl.edu";
    protected static final String offlineGscDir = offlineCenterDir + File.separator + "GSC";

    private static final String qcLivePollingLocation = tcgaDir;

    protected static final String goldArchiveName = "genome.wustl.edu_OV.SOLiD_DNASeq.Level_2.9.1.0";
    
    private static final String goldArchiveLocation = SAMPLES_DIR + "GoldenArchives" + File.separator + "GSC" + File.separator;

    @BeforeClass
    public static void setUp() throws IOException, InterruptedException, ParseException, SQLException {

        // Allow enough time for the file system to settle in its final state
        final Long qcLiveSleepTime = 60 * 1000L; // 1 min

        init();
        initializeDatabase(goldArchiveName);
        runQCLiveWGoldArchive(goldArchiveLocation, goldArchiveName, qcLivePollingLocation, qcLiveSleepTime);
    }

    @AfterClass
    public static void tearDown() throws IOException, SQLException {
        cleanDb();
    }
}
