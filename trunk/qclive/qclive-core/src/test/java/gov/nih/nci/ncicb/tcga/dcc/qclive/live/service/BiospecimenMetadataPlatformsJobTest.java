/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.live.service;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDHierarchyQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.framework.SpringApplicationContext;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailErrorHelper;

import java.util.Calendar;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@RunWith(JMock.class)
public class BiospecimenMetadataPlatformsJobTest {
	private BiospecimenMetadataPlatformsJob bioJob;
	private final Mockery context = new JUnit4Mockery();
	private final Scheduler mockScheduler = context.mock(Scheduler.class);
	private final Job job = context.mock(Job.class);
	private final UUIDHierarchyQueries mockQueries = context.mock(UUIDHierarchyQueries.class);
	private final PlatformTransactionManager mockTxManager = context.mock(PlatformTransactionManager.class);
	
	private SimpleTrigger simpleTrigger;
	private final ApplicationContext mockCtx = context.mock(ApplicationContext.class);
	private final TransactionStatus mockTxStatus = context.mock(TransactionStatus.class);
	private final MailErrorHelper mockErrorHelper = context.mock(MailErrorHelper.class);
	DefaultTransactionDefinition transactionDefinition;
	@Before
	public void setUp() throws SchedulerException {
			
		simpleTrigger = new SimpleTrigger();
		new SpringApplicationContext().setApplicationContext(mockCtx);		
		context.checking(new Expectations() {
			{	
				one(mockCtx).getBean("uuidHierarchyQueriesImpl");
				will(returnValue(mockQueries));					
				one(mockCtx).getBean("transactionManager");
				will(returnValue(mockTxManager));				
			}
		});	
		bioJob = new BiospecimenMetadataPlatformsJob();
			
		transactionDefinition = new DefaultTransactionDefinition();		
		transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
	}
	
	@Test
	public void testJobInvocation() throws SchedulerException{
		Calendar whenToRun = Calendar.getInstance();
		whenToRun.add(Calendar.MINUTE, 1);
		
		simpleTrigger.setStartTime(whenToRun.getTime());
		simpleTrigger.setName("UUID_HIERARCHY_JOB");
		context.checking(new Expectations() {
			{					
				one(mockQueries).updateAllUUIDHierarchyPlatforms();
				one(mockQueries).deletePlatforms();
				one(mockQueries).deduplicatePlatforms();
				one(mockTxManager).getTransaction(transactionDefinition);
				will(returnValue(mockTxStatus));
				one(mockTxManager).commit(mockTxStatus);
			}
		});		
		bioJob.execute(getJobExecutionContext());
	}
	
	@Test
	public void testFailedJobInvocation(){
		Calendar whenToRun = Calendar.getInstance();
		whenToRun.add(Calendar.MINUTE, 1);
		
		simpleTrigger.setStartTime(whenToRun.getTime());
		simpleTrigger.setName("UUID_HIERARCHY_JOB");
		context.checking(new Expectations() {
			{	
				one(mockTxManager).getTransaction(transactionDefinition);
				will(returnValue(mockTxStatus));
				one(mockQueries).deletePlatforms();
				one(mockTxManager).rollback(mockTxStatus);
				one(mockQueries).updateAllUUIDHierarchyPlatforms();
				will(throwException(new DataAccessException("bad things happened") {					
					private static final long serialVersionUID = 1L;					
				}));
				one(mockCtx).getBean(ConstantValues.MAIL_ERROR_HELPER_SPRING_BEAN_NAME);
				will(returnValue(mockErrorHelper));
				one(mockErrorHelper).send(with(any(String.class)),with(any(String.class)));
			}
		});		
		try{
			bioJob.execute(getJobExecutionContext());
			fail();
		}catch (JobExecutionException e){
			assertTrue(e.getMessage().contains("Unable to update platforms for uuid_hierarchy"));
		}
	}
	
	private JobExecutionContext getJobExecutionContext() {
		final JobDetail jobDetail = new JobDetail();
		final Trigger trigger = new SimpleTrigger();
		final TriggerFiredBundle triggerFiredBundle = new TriggerFiredBundle(
				jobDetail, trigger, null, false, null, null, null, null);
		return new JobExecutionContext(mockScheduler, triggerFiredBundle, job);
	}

}
