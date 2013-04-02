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
import gov.nih.nci.ncicb.tcga.dcc.common.framework.SpringApplicationContext;
import gov.nih.nci.ncicb.tcga.dcc.common.generation.FileGenerator;
import gov.nih.nci.ncicb.tcga.dcc.common.generation.FileGeneratorException;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailErrorHelper;

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
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.context.ApplicationContext;

@RunWith(JMock.class)
public class BiotabGeneratorJobFastTest {
	
	 private JobExecutionContext jobExecutionContext;
	 private BiotabGeneratorJob biotabJob;
	 private final Mockery context = new JUnit4Mockery();
	 private final Scheduler mockScheduler = context.mock(Scheduler.class);	
	 private final Job job = context.mock(Job.class);
	 private final ApplicationContext mockCtx = context.mock(ApplicationContext.class);
	 private final FileGenerator mockFileGenerator = context.mock(FileGenerator.class);
	 private final MailErrorHelper mockMailHelper = context.mock(MailErrorHelper.class);
	
	 
     @Before
     public void setup() throws Exception {
     	biotabJob = new BiotabGeneratorJob();	           	       
        jobExecutionContext = getJobExecutionContext();           
        new SpringApplicationContext().setApplicationContext(mockCtx);        
     }
	
	 @Test
	 public void testExecute() throws Exception {			 
	    context.checking(new Expectations() {{
            one(mockCtx).getBean(ConstantValues.BIOTAB_GENERATOR_SPRING_BEAN_NAME);
            will(returnValue(mockFileGenerator));
            one (mockFileGenerator).generate(null);
            will(returnValue(null));            
        }});		   
		biotabJob.execute(getJobExecutionContext());			
		// nothing to assert , but will get coverage in case it gets expanded in the future		
	 }
	 
	 @Test
	 public void testExecuteWithFailure() throws Exception {	
		 context.checking(new Expectations() {{
			 one(mockCtx).getBean(ConstantValues.BIOTAB_GENERATOR_SPRING_BEAN_NAME);
	         will(returnValue(mockFileGenerator));
	         one (mockFileGenerator).generate(null);	            
	         will(throwException(new FileGeneratorException("something has gone wrong")));
	         one(mockCtx).getBean("mailErrorHelper");
	         will(returnValue(mockMailHelper));
	         one(mockMailHelper).send(with(any(String.class)),with(any(String.class)));
	        }});		   
		 biotabJob.execute(getJobExecutionContext());		 		 
	 }
	 	 
	 private JobExecutionContext getJobExecutionContext() {
        final JobDetail jobDetail = new JobDetail();
        final Trigger trigger = new SimpleTrigger();
        final TriggerFiredBundle triggerFiredBundle = new TriggerFiredBundle(jobDetail, trigger, null, false, null, null, null, null);        
        return new JobExecutionContext(mockScheduler, triggerFiredBundle, job);
	 }
	 
	 
}
