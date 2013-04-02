/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Log;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.LogQueries;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;

/**
 * Implementation of ArchiveLogger.  Saves information to the log table in the database and adds a log_to_archive
 * link between the archive and the log entry.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: Stanley Girshik $
 * @version $Rev: 3419 $
 */
public class ArchiveLoggerImpl implements ArchiveLogger {
		
    private LogQueries logQueries;
    private ArchiveQueries archiveQueries;
    private boolean isLocal = false;
    
    // loggging to JBoss log
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ArchiveLoggerImpl.class);      
    public void addArchiveLog( final Archive archive, final String message ) {
        if(isLocal) {
        	logger.log(Level.INFO, " LOG TO ARCHIVE: " + message + " [" + archive.getId() + "]" );            
        } else {
            Log log = makeLog( message );
            Integer logId = logQueries.addLogEntry( log );
            // can only save log_to_archive if the archive's ID is already set i.e. if it is saved already
            if(archive.getId() > 0) {
                archiveQueries.addLogToArchiveEntry( archive.getId(), logId );
            }
        }
    }

    public void setLogQueries( final LogQueries logQueries ) {
        this.logQueries = logQueries;
    }

    public void setArchiveQueries( final ArchiveQueries archiveQueries ) {
        this.archiveQueries = archiveQueries;
    }

    protected Log makeLog( String message ) {
        Log log = new Log();
        log.setDescription( message );
        log.setStartTime( new Date() );
        return log;
    }

    public void setLocal( final boolean local ) {
        isLocal = local;
    }
    
    @Override
    public void addTransactionLog(String step,Long transactionId ){
    	try{
			if (transactionId != null && transactionId > 0 && StringUtils.isNotEmpty(step)){		
				logQueries.addTransactionLogRecord(transactionId, step);
			}else{
				throw new IllegalArgumentException(" Unable to log transaction log for a null transaction or blank step");
			}
    	}catch (Exception e){
    		// catching and logging all exception, I don't want logging errors to be caught by client logic
    		logger.log(Level.ERROR, " Exception while trying to add transction log ",e);    		
    	}
    }
    @Override
    public void addErrorMessage (Long txLogId,String archiveName ,String errorMessage){
    	try{
    		if(StringUtils.isNotEmpty(archiveName) && StringUtils.isNotEmpty(errorMessage) && txLogId != null && txLogId > 0){
    			logQueries.addErrorMessage(txLogId,archiveName,errorMessage);
    		}else{
    			throw new IllegalArgumentException(" Unable add error message for empty archive name or empty error message ");
    		}
    	}catch (Exception e){
    		// catching and logging all exception, I don't want logging errors to be caught by client logic
    		logger.log(Level.ERROR, " Exception while adding error message ",e);    		       	
    	}
    }
    @Override
    public Long startTransaction (String archiveName, String env){
    	Long transactionId = null;
    	try{
    		if (StringUtils.isNotEmpty(archiveName) && StringUtils.isNotEmpty(env)){
    			transactionId = logQueries.addTransactionLog(archiveName,env);	
    		}else{
    			throw new IllegalArgumentException(" Unable to start transaction will null archiveName or empty environment ");
    		}
    		
    	}catch (Exception e){
    		// catching and logging all exception, I don't want logging errors to be caught by client logic
    		logger.log(Level.ERROR, " Exception while starting transaction logging transaction ",e);    		      	
    	}
    	return transactionId;
    }
    @Override
    public void endTransaction (Long transactionLogId , Boolean isSuccessful){
    	try{
    		if (transactionLogId != null && transactionLogId > 0){		
    			logQueries.updateTransactionLogStatus(transactionLogId, isSuccessful);
			}else{
				throw new IllegalArgumentException(" Unable to end a transaction will null Id ");
			}
    		
    	}catch (Exception e){
    		// catching and logging all exception, I don't want logging errors to be caught by client logic
    		logger.log(Level.ERROR, " Exception while ending transaction logging transaction ",e);    		     	
    	}
    }
    
    @Override
    public void endTransaction (String archiveName , Boolean isSuccessful){
    	try{
    		if (StringUtils.isNotEmpty(archiveName)){		
    			logQueries.updateTransactionLogStatus(archiveName, isSuccessful);
			}else{
				throw new IllegalArgumentException(" Unable to end a transaction will null archiveName ");
			}
    		
    	}catch (Exception e){
    		// catching and logging all exception, I don't want logging errors to be caught by client logic
    		logger.log(Level.ERROR, " Exception while ending transaction logging transaction ",e);    		     	
    	}
    }

	@Override
	public void updateTransactionLogRecordResult(Long txLogId,String loggingClass,Boolean isSuccesful) {
		try{
			if (txLogId != null && txLogId > 0 && StringUtils.isNotEmpty(loggingClass)){
				logQueries.updateTransactionLogRecordResult (txLogId,loggingClass,isSuccesful);
			}else{
				throw new IllegalArgumentException(" Unable to update a transaction will null Id or logging class ");
			}
		}catch (Exception e){
    		// catching and logging all exception, I don't want logging errors to be caught by client logic
    		logger.log(Level.ERROR, " Exception while updating transactionLogResults",e);    		   	
    	}
	}  
}
