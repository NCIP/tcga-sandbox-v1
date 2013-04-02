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
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseRoutingDataSource;
import gov.nih.nci.ncicb.tcga.dcc.common.framework.SpringApplicationContext;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailErrorHelper;
import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;

import java.util.Calendar;
import java.util.Collection;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;

/**
 * Quartz Job used to kick off biotab generation jobs using cron trigger.
 *
 * @author Stanley Girshik Last updated by: $Author$
 * @version $Rev$
 */
public class BiotabSchedulerJob implements Job{
	private final Log logger = LogFactory.getLog(getClass());
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
						
		// since Quartz and Spring don't share a lifecycle, lookup the beans manually
		DiseaseRoutingDataSource diseaseDs = (DiseaseRoutingDataSource) SpringApplicationContext.getObject(ConstantValues.DISEASE_ROUTING_DS);
		Scheduler biotabScheduler = (Scheduler) SpringApplicationContext.getObject(ConstantValues.BIOTAB_SCHEDULER);				
		
		//1. Instead of a making a trip to the db, getting a list of disease names from disease routing ds
		Collection <Object>diseaseNames = diseaseDs.getDiseaseNames();
 		
		// for every disease schedule a biotab generation
		for (Object tumorName:diseaseNames){		
			 try{	
				JobDetail biotabJobDetail = new JobDetail();
				biotabJobDetail.setDurability(true);
				biotabJobDetail.setVolatility(false);
				String schedulingDelay = context.getScheduler().getContext().getString(ConstantValues.BIOTAB_GENERATOR_DELAY);	        
		        // set job name , should be a different one for each tumor
		        biotabJobDetail.setName(tumorName + "_" + UUID.randomUUID());
		        biotabJobDetail.setGroup(ConstantValues.BIOTAB_GENERATOR_JOB);      
		        biotabJobDetail.setJobClass(BiotabGeneratorJob.class);
		        JobDataMap jdMap = new JobDataMap();
		        jdMap.put("tumorName", tumorName);
		        biotabJobDetail.setJobDataMap(jdMap);
		        
		        SimpleTrigger biotabTrigger = new SimpleTrigger();
		        Calendar whenToRun = Calendar.getInstance();
		        whenToRun.add(Calendar.MINUTE,Integer.parseInt(schedulingDelay));
		        biotabTrigger.setName(tumorName + "_" + ConstantValues.BIOTAB_GENERATOR_TRIGGER);
		        biotabTrigger.setStartTime(whenToRun.getTime());
		        biotabTrigger.setJobName(biotabJobDetail.getName() );
		        biotabTrigger.setJobGroup(biotabJobDetail.getGroup());
		        
		        logger.debug("Scheduling a biotab generator job for " + biotabJobDetail.getName());
	        	biotabScheduler.scheduleJob(biotabJobDetail,biotabTrigger);
	        	
	        }catch (SchedulerException e){
	        	 logger.error ("Unable to schedule clinical Biotab generation job " + e.getMessage());
	             // email full stack trace	             
	             sendErrorEmail(e, "Biotab generation error while scheduling biotab generation job");			             
	        }     
		}			
 		
	}
	protected MailErrorHelper getErrorMailSender() {
        return (MailErrorHelper) SpringApplicationContext.getObject(ConstantValues.MAIL_ERROR_HELPER_SPRING_BEAN_NAME);
    }

    private void sendErrorEmail(final Exception exception,
                                final String message) {
        getErrorMailSender().send(message, StringUtil.stackTraceAsString(exception));
    }

}
