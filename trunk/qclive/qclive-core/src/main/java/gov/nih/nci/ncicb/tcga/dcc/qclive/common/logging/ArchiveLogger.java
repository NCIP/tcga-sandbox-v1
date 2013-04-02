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
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;

/**
 * Interface for ArchiveLogger.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: Stanley Girshik $
 * @version $Rev: 3419 $
 */
public interface ArchiveLogger {

    public void addArchiveLog( Archive archive, String message );
        
    /**
     * Starts a logging transaction by creating a new record in TransactionLog table
     * 
     * @param archiveName archive name for the logging record
     * @param env environment where the application is running
     * @return transactionLogId
     */
    public Long startTransaction (String archiveName, String env);
    
    /**
     * Ends a logging transaction by updating transactionLogStatus
     * 
     * @param transactionLogId id of a transaction log record to update 
     * @param isSuccesful True if transaction was successful ,false otherwise
     */
    public void endTransaction (Long transactionLogId, Boolean isSuccesful);
    
    /**
     * Ends a logging transaction by updating transactionLogStatus
     * 
     * @param archiveName name of an archive for which to end transaction 
     * @param isSuccesful True if transaction was successful ,false otherwise
     */
    public void endTransaction (String archiveName , Boolean isSuccesful);
    
    /**
     * Ends a logging transaction by updating transactionLogStatus
     * 
     * @param transactionLogId id of a transaction log record to update 
     * @param isSuccesful True if transaction was successful ,false otherwise
     */
    public void addTransactionLog(String loggingClass, Long transactionId );
    
    /**
     * Updates result of a transaction record
     * @param txLogId transactionLog Id for which to update transactionLogresult status
     * @param loggingClassf or which to update transactionLogresult status
     * @param isSuccessful rue for successful status, false otherwise
     */
    public void updateTransactionLogRecordResult(Long txLogId, String loggingClass,Boolean isSuccesful);
    
    /**
     * Adds an error message to logging transaction
     * 
     * @param txLogId transactionLog Id for which to update add error message
     * @param archiveName for which the error is recorded
     * @param errorMessage error message to record
     */
    public void addErrorMessage (Long txLogId, String archiveName,String errorMessage);    
}
