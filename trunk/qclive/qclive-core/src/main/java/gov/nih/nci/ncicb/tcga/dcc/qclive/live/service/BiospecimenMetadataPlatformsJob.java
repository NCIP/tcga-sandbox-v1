/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.live.service;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDHierarchyQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.framework.SpringApplicationContext;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailErrorHelper;
import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * BiospecimenMetadataPlatforms job is used to populate platforms in 
 * uuid_hierarchy table. The job queries the database for platform/uuid associations
 * and updates uuid metadata table with these platforms.
 *
 * @author Stanley Girshik Last updated by: $Author$
 * @version $Rev$
 */
public class BiospecimenMetadataPlatformsJob implements Job {

	private final Log logger = LogFactory.getLog(getClass());
	// since Quartz and Spring don't share a lifecycle, lookup the bean manually
	UUIDHierarchyQueries uuidHierarchyQueries = (UUIDHierarchyQueries) SpringApplicationContext.getObject(ConstantValues.UUID_HIERARCHY_QUERIES);
	PlatformTransactionManager transactionManager = (PlatformTransactionManager) SpringApplicationContext.getObject("transactionManager");	
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug(" Updating platforms in uuid_hierarchy ");
		
		// start TX
    	DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
    	transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    	TransactionStatus status = transactionManager.getTransaction(transactionDefinition);		
		try{
			uuidHierarchyQueries.deletePlatforms();
			uuidHierarchyQueries.updateAllUUIDHierarchyPlatforms();
			uuidHierarchyQueries.deduplicatePlatforms();	
			transactionManager.commit(status);
		}catch (Exception e){
			transactionManager.rollback(status);
			String errorMessage = "Unable to update platforms for uuid_hierarchy table due to an unexpected condition"; 
            // email full stack trace	             
            sendErrorEmail(e, errorMessage);
			throw new JobExecutionException(errorMessage,e);
		}		
	}	
	protected MailErrorHelper getErrorMailSender() {
        return (MailErrorHelper) SpringApplicationContext.getObject(ConstantValues.MAIL_ERROR_HELPER_SPRING_BEAN_NAME);
    }

    private void sendErrorEmail(final Exception exception,
                                final String message) {
        getErrorMailSender().send(message, StringUtil.stackTraceAsString(exception));
    }
	public void setUuidHierarchyQueries(UUIDHierarchyQueries uuidHierarchyQueries) {
		this.uuidHierarchyQueries = uuidHierarchyQueries;
	}	
}
