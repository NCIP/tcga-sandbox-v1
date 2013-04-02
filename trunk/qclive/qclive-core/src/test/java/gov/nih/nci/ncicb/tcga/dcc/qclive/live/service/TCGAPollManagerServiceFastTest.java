package gov.nih.nci.ncicb.tcga.dcc.qclive.live.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailErrorHelper;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcLiveStateBean;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.apache.log4j.Level;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;

/**
 * @author Robert Sfeir
 * @version $Rev$
 * Calss to test various methods in TCGAPollManagerService class
 */
@RunWith (JMock.class)
public class TCGAPollManagerServiceFastTest {
    private final Mockery context = new JUnit4Mockery();
    private final JobScheduler mockJobScheduler = context.mock(JobScheduler.class);
    private final MailErrorHelper mailHelper = context.mock(MailErrorHelper.class);
    
    private final Logger mockLogger = context.mock(Logger.class);

    private static final String DIRS_TO_WATCH = "/tcgafiles/ftp_auth/deposit_ftpusers/baylor,/tcgafiles/ftp_auth/deposit_ftpusers/brawo,/tcgafiles/ftp_auth/deposit_ftpusers/cgcbroad,/tcgafiles/ftp_auth/deposit_ftpusers/cgclbl,/tcgafiles/ftp_auth/deposit_ftpusers/cgcunc,/tcgafiles/ftp_auth/deposit_ftpusers/gscbroad,/tcgafiles/ftp_auth/deposit_ftpusers/jhmi,/tcgafiles/ftp_auth/deposit_ftpusers/mskc,/tcgafiles/ftp_auth/deposit_ftpusers/stanf,/tcgafiles/ftp_auth/deposit_ftpusers/tcgabcr,/tcgafiles/ftp_auth/deposit_ftpusers/washu";

    private static final String LESS_DIRS_TO_WATCH = "/tcgafiles/ftp_auth/deposit_ftpusers/baylor,/tcgafiles/ftp_auth/deposit_ftpusers/brawo,/tcgafiles/ftp_auth/deposit_ftpusers/cgcbroad,/tcgafiles/ftp_auth/deposit_ftpusers/cgclbl,/tcgafiles/ftp_auth/deposit_ftpusers/cgcunc,/tcgafiles/ftp_auth/deposit_ftpusers/gscbroad,/tcgafiles/ftp_auth/deposit_ftpusers/jhmi,/tcgafiles/ftp_auth/deposit_ftpusers/mskc,/tcgafiles/ftp_auth/deposit_ftpusers/stanf";

    private TCGAPollManagerService pollService;
    private final Calendar now = Calendar.getInstance();

    private static final String SAMPLE_DIR = 
    	Thread.currentThread().getContextClassLoader().getResource("samples/qclive").getPath() + File.separator;
    
    @Before
    public void setup() {
        pollService = new TCGAPollManagerService() {
            @Override
            protected Calendar calculateWhenJobShouldRun() {
                return now;
            }
        };
        pollService.setJobScheduler(mockJobScheduler);
        pollService.setExtensionToExclude("md5");
        pollService.setMillisecondsToWaitInMoveLoop(1);
        pollService.setLogger(mockLogger);
        pollService.setMailErrorHelper(mailHelper);
    }

    @Test
    public void testSetDirsToPoll() throws Exception {
        pollService.setDirsToPoll(DIRS_TO_WATCH);
        final File[] theDirectoriesAsFiles = pollService.getTheDirs();
        assertEquals(theDirectoriesAsFiles.length, 11);

        pollService.setDirsToPoll(DIRS_TO_WATCH);
        final File[] theNewDirectoriesAsFiles = pollService.getTheDirs();
        assertSame(theDirectoriesAsFiles, theNewDirectoriesAsFiles);

        pollService.setDirsToPoll(LESS_DIRS_TO_WATCH);
        final File[] theSmallerListOfDirectoriesAsFiles = pollService.getTheDirs();
        assertEquals(theSmallerListOfDirectoriesAsFiles.length, 9);
        assertNotSame(theSmallerListOfDirectoriesAsFiles, theNewDirectoriesAsFiles);

    }

    @Test
    public void testFileFoundWithBulkReceivedDir() throws IOException, SchedulerException {
        pollService.setBulkReceivedWorkingDirectory(SAMPLE_DIR + "/pollManagerService/fakeBulkReceived");
        testFileFound(new File(SAMPLE_DIR + "/pollManagerService/test.txt"),
                new File(SAMPLE_DIR + "/pollManagerService/fakeBulkReceived/test.txt"));
    }

    @Test
    public void testFileFoundWithNoBulkReceivedDir() throws IOException, SchedulerException {
        pollService.setBulkReceivedWorkingDirectory(null);
        testFileFound(new File(SAMPLE_DIR + "/pollManagerService/test.txt"),
                new File(SAMPLE_DIR + "/pollManagerService/received/test.txt"));
    }

    @Test
    public void testFileFoundWithEmptyBulkReceivedDir() throws SchedulerException, IOException {
        pollService.setBulkReceivedWorkingDirectory("");
        testFileFound(new File(SAMPLE_DIR + "/pollManagerService/test.txt"),
                new File(SAMPLE_DIR + "/pollManagerService/received/test.txt"));
    }

    private void testFileFound(final File startLocation, final File expectedEndLocation) throws IOException, SchedulerException {
        try {

            assertTrue(startLocation.createNewFile());
            assertTrue(startLocation.exists());

            context.checking(new Expectations() {{
                allowing(mockLogger).log(with(any(Level.class)), with(any(String.class)));
                one(mockJobScheduler).scheduleUploadCheck(with(expectedEndLocation), with(any(Calendar.class)), with(any(QcLiveStateBean.class)));
            }});

            pollService.handleFileFound(startLocation);

            assertTrue(expectedEndLocation.exists());
            assertFalse(startLocation.exists());
        } finally {
            if (startLocation.exists()) {
                startLocation.deleteOnExit();
            }
            if (expectedEndLocation.exists()) {
                expectedEndLocation.deleteOnExit();
            }
        }

    }

    public void testSetDirsToPollEmpty() {
        pollService.setDirsToPoll("");
        assertEquals(0, pollService.getTheDirs().length);
    }

    public void testSetDirsToPollNull() {
        pollService.setDirsToPoll(null);
        assertEquals(0, pollService.getTheDirs().length);
    }

    @Test
    public void testFileMoved() throws SchedulerException, IOException {
        final File testFile = new File("test.tar.gz");
        final File fileToIgnore = new File("test.tar.gz.md5");
        context.checking( new Expectations() {{
            allowing(mockLogger).log(with(any(Level.class)), with(any(String.class)));
            one(mockJobScheduler).scheduleUploadCheck(testFile, now, null);
        }});

        pollService.fileMoved(testFile);
        pollService.fileMoved(fileToIgnore);
    }
    
    @Test
    public void testCycleStarted() throws IOException, InterruptedException{
    	
    	File tempDir = new File (SAMPLE_DIR+ "tempDir");
    	if (!tempDir.exists()){
    		tempDir.mkdir();
    	}
    	if (tempDir.exists()){
	    	TCGADirectoryPoller poller = new TCGADirectoryPoller();
	    	try{    	
		    	pollService.setDirsToPoll(tempDir.getCanonicalPath());       	                       	        
		        poller.setTpoll(pollService);
		        poller.setMailHelper(mailHelper);
		        context.checking( new Expectations() {{		        	
		            allowing(mailHelper).send("The QCLive application is being restarted or shutdown.", "");
		        }});

		        poller.start();	        		        		       
		        
		        Thread.sleep(1000);
		        			        
		        
	    	}finally{
	    		if (poller != null){
	    			poller.shutdown();
	    		}
	    		if(tempDir != null && tempDir.exists()){
	    			tempDir.delete();
	    		}
	    	}
    	}                      
    }
      
    @Test
    public void testCycleStartedBadDirectory() throws IOException, InterruptedException{
    	
    	File tempDir = new File  (SAMPLE_DIR + "tempDir");
    	File tempDir2 = new File (SAMPLE_DIR + "tempDir2");
    	File tempDir3 = new File (SAMPLE_DIR + "tempDir3");
    	
    	if (!tempDir.exists()){
    		tempDir.mkdir();
    	} 
    	if (!tempDir2.exists()){
    		tempDir2.mkdir();
    	} 
    	if (!tempDir3.exists()){
    		tempDir3.mkdir();
    	} 
    	
    	if (tempDir.exists() && tempDir2.exists() && tempDir3.exists()){
	    	TCGADirectoryPoller poller = new TCGADirectoryPoller();
	    	try{    	
		    	pollService.setDirsToPoll(tempDir.getCanonicalPath()+ "," + tempDir2.getCanonicalPath() + "," +tempDir3.getCanonicalPath()); 		   
		    	
		        poller.setTpoll(pollService);
		        poller.setMailHelper(mailHelper);
		        poller.setPollInterval(100);
		        context.checking( new Expectations() {{		        	
		            allowing(mailHelper).send("The QCLive application is being restarted or shutdown.", "");
		            allowing(mockLogger).log(Level.ERROR, " QClive Directory Poller is unable to poll: " +
		            		(new File(SAMPLE_DIR + "tempDir")).getCanonicalPath() + " " +
		            		(new File(SAMPLE_DIR + "tempDir3")).getCanonicalPath() + " " +
		            		" Make sure the directory exists with correct read/write permissions");
		            allowing(mailHelper).send(" QClive Directory Poller is unable to poll: " +
		            		(new File(SAMPLE_DIR + "tempDir")).getCanonicalPath() + " " +
		            		(new File(SAMPLE_DIR + "tempDir3")).getCanonicalPath() + " " +
		            		" Make sure the directory exists with correct read/write permissions", "");
		        }});

		        poller.start();	        		        		       		        
		        Thread.sleep(1000);
		        // can't set r/w permissions on Windows, 
		        // so deleting the dir to simulate sudden permission change
		        if (tempDir.exists()){
		        	tempDir.delete();
		    	}		        
		        if (tempDir3.exists()){
		        	tempDir3.delete();
		    	}
		        Thread.sleep(1000);		        			        		        
	    	}finally{
	    		if (poller != null){
	    			poller.shutdown();
	    		}
	    		if(tempDir != null && tempDir.exists()){
	    			tempDir.delete();
	    		}
	    		if(tempDir2 != null && tempDir2.exists()){
	    			tempDir2.delete();
	    		}
	    		if(tempDir3 != null && tempDir3.exists()){
	    			tempDir3.delete();
	    		}
	    	}
    	}                      
    }    

}
