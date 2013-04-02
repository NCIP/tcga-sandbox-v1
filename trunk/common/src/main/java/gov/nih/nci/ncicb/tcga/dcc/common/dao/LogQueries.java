/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Log;
import gov.nih.nci.ncicb.tcga.dcc.common.web.LogQueryRequest;

import java.util.Collection;

/**
 * @author Robert S. Sfeir, David Kane
 */
public interface LogQueries {

    public Collection getLogInDateRange( LogQueryRequest queryParams );

    public Integer addLogEntry( Log log );

    public Collection getAllLogEntries();

    public void updateLogEntry( Log log );
    
    /**
     * Adds transaction log record
     * 
     * @param archiveName archive name for the logging record
     * @param env environment where the application is running
     * @return transactionLogId
     */
    public Long addTransactionLog(String archiveName, String env);

    /**
     * Updates transactionLogStatus on tranactionLog table
     * 
     * @param transactionLogId id on transactionLogRecord where the update is taking place
     * @param isSuccessful true for successful status, false otherwise
     */
    public void updateTransactionLogStatus(Long transactionLogId,Boolean isSuccessful);
    
    /**
     * Updates transactionLogStatus on tranactionLog table
     * 
     * @param archiveName archive name of transactionLogRecord where the update is taking place
     * @param isSuccessful true for successful status, false otherwise
     */
    public void updateTransactionLogStatus(String archiveName,Boolean isSuccessful);

    /**
     * Adds a record in TransactionLogRecord table
     * 
     * @param txLogId transaction id for which to add transactionLogRecord
     * @param loggingClass class that is doing the logging
     */
    public void addTransactionLogRecord(Long txLogId,String loggingClass);        
    
    /**
     * Adds a record in ErrorMessage table
     * 
     * @param txLogId transaction id for which to add error message
     * @param archiveName for which the error is recorded
     * @param errorMessage error message to record
     */
    public void addErrorMessage(Long txLogId,String archiveName ,String errorMessage);
    
    /**
     * Updates transactionLogRecord isSuccessful flag
     * @param txLogId transactionLog Id for which to update transactionLogresult status
     * @param loggingClassf or which to update transactionLogresult status
     * @param isSuccessful rue for successful status, false otherwise
     */
    public void updateTransactionLogRecordResult(Long txLogId, String loggingClass,Boolean isSuccessful);
            
        
}
