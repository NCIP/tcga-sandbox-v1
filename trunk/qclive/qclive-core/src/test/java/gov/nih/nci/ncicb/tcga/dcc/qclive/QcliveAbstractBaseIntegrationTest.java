/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive;

import gov.nih.nci.ncicb.tcga.dcc.QCLiveTestDataGenerator;
import gov.nih.nci.ncicb.tcga.dcc.SchemaType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.MD5Validator;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * Abstract base class that contains a collection of methods to run integration tests.
 *
 * @author Stanley Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 */
public abstract class QcliveAbstractBaseIntegrationTest {
	
	/** Logger */
	private static final Logger logger = Logger.getLogger(QcliveAbstractBaseIntegrationTest.class);
	
	protected static Date testStartDate;
	protected static String auxScriptFile ="auxDiseaseData.sql";
	protected static ApplicationContext ctx = null;
	private static final String defaultApplicationContext = "integration.test.applicationContext.xml";
	private static final String isArchiveFinishedSql = "select deploy_status from archive_info where archive_name = ? ";		
	protected static JdbcTemplate localDiseaseTemplate ;
	protected static JdbcTemplate localCommonTemplate ;		
	private static final long timeOutTime = 60000 *15;
	protected static  QCLiveTestDataGenerator dataGenerator = null;

    /**
     * Default time to wait between each checks to see if QcLive has finished running
     */
    protected static final long defaultQcLiveSleepTime = 10 * 1000; // 10 sec

    /**
     * The directories and files in the directory structure expected by QcLive
     */
    private static final String tcgaFilesRootDirName = "tcgafiles";
    private static final String tcgafilesPath = getTcgaFilesRootDirPath();
    private static final String ftpAuthPath = tcgafilesPath + File.separator + "ftp_auth";
    private static final String depositFtpusersPath = ftpAuthPath + File.separator + "deposit_ftpusers";
    private static final String offlinePath = depositFtpusersPath + File.separator + "offline";
    private static final String tcgaPath = depositFtpusersPath + File.separator + "tcga";
    private static final String receivedPath = tcgaPath + File.separator + "received";
    private static final String distroFtpusersPath = ftpAuthPath + File.separator + "distro_ftpusers";
    private static final String anonymousPath = distroFtpusersPath + File.separator + "anonymous";
    private static final String otherPath = anonymousPath + File.separator + "other";
    private static final String includePath = otherPath + File.separator + "include";
    private static final String disclaimerPath = includePath + File.separator + "DATA_USE_DISCLAIMER.txt";
    private static final String anonymousUserCreatedArchives = anonymousPath + File.separator + "userCreatedArchives";
    private static final String tcga4yeoPath = distroFtpusersPath + File.separator + "tcga4yeo";
    private static final String cachefilesPath = tcga4yeoPath + File.separator + "cachefiles";
    private static final String tumorPath = tcga4yeoPath + File.separator + "tumor";
    private static final String tcga4yeoUserCreatedArchives = tcga4yeoPath + File.separator + "userCreatedArchives";
    private static final String tempSvg = distroFtpusersPath + File.separator + "temp-svg";
    
    private static final List<String> validPaths = new ArrayList<String>();
    static {
        validPaths.add(tcgafilesPath);
        validPaths.add(ftpAuthPath);
        validPaths.add(depositFtpusersPath);
        validPaths.add(offlinePath);
        validPaths.add(tcgaPath);
        validPaths.add(receivedPath);
        validPaths.add(distroFtpusersPath);
        validPaths.add(anonymousPath);
        validPaths.add(otherPath);
        validPaths.add(includePath);
        validPaths.add(disclaimerPath);
        validPaths.add(anonymousUserCreatedArchives);
        validPaths.add(tcga4yeoPath);
        validPaths.add(cachefilesPath);
        validPaths.add(tumorPath);
        validPaths.add(tcga4yeoUserCreatedArchives);
        validPaths.add(tempSvg);
    }

	private final String getArchiveInfoRecords ="select * from archive_info";
	
	// initializes integration test
	protected static void init(){
		testStartDate = new Date();
		ctx = new ClassPathXmlApplicationContext(defaultApplicationContext);		
		DriverManagerDataSource localDiseaseDataSource = (DriverManagerDataSource)ctx.getBean("localDiseaseDataSource");
		localDiseaseTemplate = new JdbcTemplate(localDiseaseDataSource);
		DriverManagerDataSource  localCommonDataSource = (DriverManagerDataSource)ctx.getBean("localCommonDataSource");
		localCommonTemplate = new JdbcTemplate(localCommonDataSource);								
		fileSystemInitialization();		
	}
	/**
	 * initializes static database data based on an archive passed in
	 * @param archiveName
	 * @throws IOException
	 * @throws ParseException
	 * @throws SQLException 
	 */
	protected static void initializeDatabase(String archiveName) throws IOException, ParseException, SQLException{
		
		logger.info(" initializing database - IN");
		DriverManagerDataSource devDiseaseDataSource = (DriverManagerDataSource)ctx.getBean("devDiseaseDataSourceDisease");				
		DriverManagerDataSource devDcommonDataSource = (DriverManagerDataSource)ctx.getBean("devCommonDataSource");					
		dataGenerator = new QCLiveTestDataGenerator();
				
		dataGenerator.setDccCommonLocalJdbcTemplate(localCommonTemplate);
		dataGenerator.setDiseaseLocalJdbcTemplate(localDiseaseTemplate);
		
		dataGenerator.setDccCommonDevJdbcTemplate(new JdbcTemplate(devDcommonDataSource));				
		dataGenerator.setDiseaseDevJdbcTemplate(new JdbcTemplate(devDiseaseDataSource));	
		
		dataGenerator.setDiseaseDevSQLInsertScriptFileName("sql/CreateInsertStatementsForClinicalMetaData.sql");		
		dataGenerator.setDccCommonDevSQLInsertScriptFileName("sql/CreateInsertStatementsForDccCommon.sql");
		dataGenerator.setDiseaseDevRefDataSQLInsertScriptFileName("sql/CreateInsertStatementsForDiseaseRefTables.sql");
		dataGenerator.setBarcodeSQLInsertScriptFileName("sql/GetInsertsToAddBarcodes.sql");
        dataGenerator.setCntlDevRefDataSQLInsertScriptFileName("sql/InsertStatementsForCNTLRefTables.sql");
		
		
		/*
		 * run the init scripts
		 */
		//cleanDb(dataGenerator);
		
		Map<String,String> initScripts = new HashMap<String,String>();
		initScripts.put("sql/DeleteFromDccCommon.sql", SchemaType.LOCAL_COMMON.getSchemaValue());
		initScripts.put("sql/DeleteQcliveTestData.sql", SchemaType.LOCAL_DISEASE.getSchemaValue());
		dataGenerator.setInitSQLScriptClassPathLocations(initScripts);
		
		dataGenerator.generateTestData(archiveName);
		//Resource auxFileResoure = new  ClassPathResource(auxScriptFile);			
		//dataGenerator.executeSQLScriptFile(SchemaType.LOCAL_DISEASE, auxFileResoure);
				
		logger.info(" initializing database - OUT ");	
	}
	
	/**
	 * clears out the databases
	 * @throws SQLException 
	 * @throws IOException 
	 */
	protected static void cleanDb() throws IOException, SQLException{
		logger.info(" cleaning database in - IN");
				
		dataGenerator.executeSQLScriptFile(SchemaType.LOCAL_COMMON, new ClassPathResource("sql/DeleteFromDccCommon.sql"));
		dataGenerator.executeSQLScriptFile(SchemaType.LOCAL_DISEASE, new ClassPathResource("sql/DeleteQcliveTestData.sql"));
		
		/*//clean up disease schema
		SimpleJdbcTestUtils.executeSqlScript(
				new SimpleJdbcTemplate(localDiseaseTemplate.getDataSource()),
				(new EncodedResource(new ClassPathResource("sql/DeleteFromDccCommon.sql"))),
				false);
		// clean up common schema
		SimpleJdbcTestUtils.executeSqlScript(
				new SimpleJdbcTemplate(localCommonTemplate.getDataSource()),
				(new EncodedResource(new ClassPathResource("sql/DeleteQcliveTestData.sql"))),
				false);
				*/
		
		
		
		logger.info(" cleaning database in - OUT");
	}

    /**
     * Drops the given archive in QcLive polling directory and wait for the archive to be finished processing
     * 
     * @param goldArchiveLocation path to the golden archive location
     * @param goldArchiveName name of the golden archive
     * @param qcLivePollingLocation path to the QcLive polling directory
     * @param qcLiveSleepTime time between checks if QcLive is finished processing. Use <code>null</code> to use default value.
     * @throws IOException
     * @throws InterruptedException
     */
	protected static void runQCLiveWGoldArchive(final String goldArchiveLocation,
                                                final String goldArchiveName,
                                                final String qcLivePollingLocation,
                                                final Long qcLiveSleepTime) throws IOException, InterruptedException {
		logger.info(" running qcLive - IN");
		FileUtil.copy(goldArchiveLocation + goldArchiveName + ".tar.gz.md5", qcLivePollingLocation);
		FileUtil.copy(goldArchiveLocation + goldArchiveName + ".tar.gz", qcLivePollingLocation);
		
		String archiveAvailable ;
		
		Boolean isQcliveRunning = true;
		Date startWait = new Date ();
		while(isQcliveRunning){
			try{
				archiveAvailable = (String)localCommonTemplate.queryForObject(isArchiveFinishedSql, new String[]{goldArchiveName},String.class);						
				if (Archive.STATUS_INVALID.equalsIgnoreCase(archiveAvailable)){
					throw new IllegalStateException(" QCLive failed on a gold archive with deploy_status = Invalid. The archive should always be valid. Correct the databse/archive and try again");
				}else if (Archive.STATUS_IN_REVIEW.equalsIgnoreCase(archiveAvailable)){
					throw new IllegalStateException(" QCLive failed on a gold archive with deploy_status = In Review. Correct the databse/archive and try again");
				}else if(Archive.STATUS_AVAILABLE.equalsIgnoreCase(archiveAvailable)){
					isQcliveRunning = false;
				}							
			}catch (EmptyResultDataAccessException e){
				// swallow this exception
				// qclive may take its time processing the archive hence the table is empty for a while
			}finally{				
				// failsafe in case something goes bad with the data , sleep no longer then allowed				
				if (((new Date ()).getTime() - startWait.getTime()) > timeOutTime){					
					throw new IllegalStateException(" QCLive timed out on a gold archive while waiting on the gold archive to process. Time out is currently set to " + timeOutTime  + " milliseconds");					
				}	
			}

            // Sleep AFTER testing that qcLive is running,
            // so that the sleep time can be used to make sure the file system has settled to its final state
            if(qcLiveSleepTime != null) {
                Thread.sleep(qcLiveSleepTime);
            } else { // Use default
                Thread.sleep(defaultQcLiveSleepTime);
            }
		}
		logger.info(" running qcLive - OUT");
	}
	
	/**
	 * Retrieves the absolute root directory path of the tcgafiles directory.
	 * 
	 * @return the absolute root directory path of the tcgafiles directory
	 */
	private static String getTcgaFilesRootDirPath() {
    	File tcgaFilesRoot = null;
    	for(final File root : File.listRoots()) {
    		tcgaFilesRoot = new File(root + tcgaFilesRootDirName);
    		if(tcgaFilesRoot.canRead())
    			return tcgaFilesRoot.getAbsolutePath();
    	}
    	return null;
    }

    /**
     * Reset the FS.
     *
     * Disclaimer: do not run this on any of the tiers (DEV and up)
     */
    protected static void fileSystemInitialization()
    {
        logger.info(" fileSystemInitialization - IN");
        cleanPath(tcgafilesPath, validPaths);
        logger.info(" fileSystemInitialization - OUT");
    }

    /**
     * Check all files under the given path and delete them if they are not in the given list of valid paths
     *
     * @param path the path where to start checking the paths validity (this one included)
     * @param validPaths the list of valid paths
     */
    private static void cleanPath(final String path, final List<String> validPaths) {
    	
        // Delete path if not valid
        final File file = new File(path);

        if(!validPaths.contains(file.getAbsolutePath())) {

            final boolean isDeleted = FileUtil.deleteDir(file);

            if(!isDeleted) {
                fail("Could not delete file '" + file.getAbsolutePath() + "'");
            }
        }

        // Recursively clean children (don't try this at home)
        if(file.isDirectory()) {
            final String[] children = file.list();
            for(final String child : children) {
                cleanPath(path + File.separator + child, validPaths);
            }
        }
    }

    /**
     * Check that the archive and its md5 file are both available in the ftp directory.
     *
     * Also check that:
     * - the .tar.gz MD5 sum is correct
     * - the last modified date is on or after the test start date
     * - the file size are as expected
     *
     * @param tarGzFilepath the .tar.gz filepath
     * @param md5Filepath the .md5 filepath
     * @param expectedTarGzSize the expected .tar.gz size
     * @param expectedMd5Size the expected .md5 size
     * @param expectedMD5Sum the expected .tar.gz MD5 sum
     */
    protected static void checkFSArchiveInFTP(final String tarGzFilepath,
                                     final String md5Filepath,
                                     final long expectedTarGzSize,
                                     final long expectedMd5Size,
                                     final String expectedMD5Sum) {

    	logger.info(" checkFSArchiveInFTP - IN");
        final File tarGzFile = checkExists(tarGzFilepath);
        final File md5File = checkExists(md5Filepath);

        checkSize(tarGzFile, expectedTarGzSize);
        checkSize(md5File, expectedMd5Size);

        checkMd5(tarGzFile, expectedMD5Sum);

        checkLastModified(tarGzFile);
        checkLastModified(md5File);
        logger.info(" checkFSArchiveInFTP - OUT");
    }

    /**
     * Checks that the last modified date of the given file is on or after the test start date
     *
     * @param file the file
     */
    protected static void checkLastModified(final File file) {
    	logger.info(" checkLastModified - IN");
        final Date lastModifiedDate = new Date(file.lastModified());
        assertFalse(lastModifiedDate.before(testStartDate));
        logger.info(" checkLastModified - OUT");
    }
    
    /**
     * Retrieves a map with archive_info table values 
     */
	protected List<Map<String,Object>> retrieveArchiveInfoRecords() {
		logger.info(" retrieveArchiveInfoRecords - IN");
		final List<Map<String,Object>> archiveInfoRecordsList = new ArrayList<Map<String,Object>>();
		localCommonTemplate.query(getArchiveInfoRecords, new RowCallbackHandler() {
		    public void processRow(final ResultSet resultSet) throws SQLException {
			    	final Map<String,Object> elementIdMap = new HashMap<String,Object>();
			        elementIdMap.put("ARCHIVE_ID", resultSet.getLong("ARCHIVE_ID"));
			        elementIdMap.put("ARCHIVE_NAME", resultSet.getString("ARCHIVE_NAME"));
			        elementIdMap.put("ARCHIVE_TYPE_ID", resultSet.getLong("ARCHIVE_TYPE_ID"));
			        elementIdMap.put("CENTER_ID", resultSet.getLong("CENTER_ID"));
			        elementIdMap.put("DISEASE_ID", resultSet.getLong("DISEASE_ID"));
			        elementIdMap.put("PLATFORM_ID", resultSet.getLong("PLATFORM_ID"));
			        elementIdMap.put("SERIAL_INDEX", resultSet.getLong("SERIAL_INDEX"));
			        elementIdMap.put("REVISION", resultSet.getLong("REVISION"));
			        elementIdMap.put("SERIES", resultSet.getLong("SERIES"));
			        elementIdMap.put("DATE_ADDED", resultSet.getTimestamp("DATE_ADDED"));
			        elementIdMap.put("DEPLOY_STATUS", resultSet.getString("DEPLOY_STATUS"));
			        elementIdMap.put("DEPLOY_LOCATION", resultSet.getString("DEPLOY_LOCATION"));
			        elementIdMap.put("IS_LATEST", resultSet.getLong("IS_LATEST"));
			        elementIdMap.put("IS_LATEST", resultSet.getLong("INITIAL_SIZE_KB"));
			        elementIdMap.put("FINAL_SIZE_KB", resultSet.getLong("FINAL_SIZE_KB"));
			        elementIdMap.put("IS_LATEST_LOADED", resultSet.getLong("IS_LATEST_LOADED"));
			        elementIdMap.put("DATA_LOADED_DATE", resultSet.getDate("DATA_LOADED_DATE"));		        
			        archiveInfoRecordsList.add(elementIdMap);
		    	}		    	
		 	});
		logger.info(" retrieveArchiveInfoRecords - OUT");
		return archiveInfoRecordsList;
	}


    /**
     * Checks that the given file has the expected MD5 sum
     *
     * @param file the file
     * @param expectedMD5Sum the expected MD5 sum
     */
    protected static void checkMd5(final File file, final String expectedMD5Sum) {
    	logger.info(" checkMd5 - IN");
        try {
            final String md5Sum = MD5Validator.getFileMD5(file);
            assertEquals(expectedMD5Sum.toLowerCase(), md5Sum.toLowerCase());

        } catch (final IOException e) {
            fail("NoSuchAlgorithmException was raised: " + e.getMessage());
            e.printStackTrace();

        } catch (final NoSuchAlgorithmException e) {
            fail("NoSuchAlgorithmException was raised: " + e.getMessage());
            e.printStackTrace();
        }
        logger.info(" checkMd5 - OUT");
    }

    /**
     * Check that the given file has the expected size
     *
     * @param file the file
     * @param expectedSize the expected size
     */
    protected static void checkSize(final File file, long expectedSize) {
        assertEquals(expectedSize, FileUtils.sizeOf(file));
    }

    /**
     * Checks that the file with the given filepath exists
     *
     * @param filepath the filepath
     */
    protected static File checkExists(final String filepath) {

        final File file = new File(filepath);

        if(!file.exists()) {
            throw new RuntimeException("Expected file '" + filepath + "' does not exist");
        }

        return file;
    }
    
    
}
