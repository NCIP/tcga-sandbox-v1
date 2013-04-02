/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive;


import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Abstract class that is used to run an integration test for BCR archives.
 *
 * @author Stanley Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 */
public abstract class QcliveAbstractBCRIntegrationTest extends QcliveAbstractBaseIntegrationTest{
	/** Logger */
	private static final Logger logger = Logger.getLogger(QcliveAbstractBCRIntegrationTest.class);		
	protected static final String goldArchiveName = "nationwidechildrens.org_BLCA.bio.Level_1.86.15.0";

	private static final String SAMPLES_DIR = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
	private static final String goldArchiveLocation = SAMPLES_DIR + "GoldenArchives/BCR/";			
	private static final String qcLivePollingLocation ="/tcgafiles/ftp_auth/deposit_ftpusers/tcga/";
		
    protected final String rootDir = File.separator + "tcgafiles";
    protected final String ftpAuthDir = rootDir + File.separator + "ftp_auth";
    protected final String depositDir = ftpAuthDir + File.separator + "deposit_ftpusers";
    protected final String offlineDir = depositDir + File.separator + "offline";
    protected final String offlineCenterDir = offlineDir + File.separator + "nationwidechildrens.org";
    protected final String offlineBcrDir = offlineCenterDir + File.separator + "BCR";

	@BeforeClass
	public static void setUp()throws IOException, InterruptedException, SQLException, ParseException{
		logger.info(" QcliveAbstractBCRIntegrationTest setUp - IN");		
		init();
		initializeDatabase(goldArchiveName);	
		runQCLiveWGoldArchive(goldArchiveLocation,goldArchiveName,qcLivePollingLocation, null);
		logger.info(" QcliveAbstractBCRIntegrationTest setUp - OUT");
	}
	
	@AfterClass
	public static void tearDown() throws IOException, SQLException{
		logger.info(" running tearDown - IN");		
		cleanDb();
		logger.info(" running tearDown - OUT");		
	}
}
