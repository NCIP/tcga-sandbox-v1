package gov.nih.nci.ncicb.tcga.dcc.qclive;


import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class QCLiveAbstractCGCCIntegrationTest extends QcliveAbstractBaseIntegrationTest{
	/** Logger */
	private static final Logger logger = Logger.getLogger(QCLiveAbstractCGCCIntegrationTest.class);
	protected static final String goldArchiveName = "";
	
	private static final String SAMPLES_DIR = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
	private static final String goldArchiveLocation = SAMPLES_DIR + "GoldenArchives/CGCC/";			
	private static final String qcLivePollingLocation ="/tcgafiles/ftp_auth/deposit_ftpusers/tcga/";
	@BeforeClass
	public static void setUp()throws IOException, InterruptedException, ParseException, SQLException{
		// do nothing for now
	}
	
	@AfterClass
	public static void tearDown() throws IOException, SQLException{
		// do nothing for now	
	}
}
