/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.loader.bamloader;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamContext;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailErrorHelper;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;

import org.junit.runner.RunWith;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.SimpleTrigger;
import org.quartz.Scheduler;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.spi.TriggerFiredBundle;
import java.io.IOException;

/**
 * Test class for BAMLoader Scheduler.
 *
 * @author ramanr
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class BAMLoaderSchedulerJobFastTest {

    private final Mockery context = new JUnit4Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    private final Scheduler mockScheduler = context.mock(Scheduler.class);
    private final Job job = context.mock(Job.class);
    private JobExecutionContext jobExecutionContext;
    private BAMLoaderSchedulerJob bamLoaderSchedulerJob;
    private MailErrorHelper mockMailErrorHelper;
    private BAMLoader mockBamLoader;

    @Before
    public void setup() throws Exception {
        mockMailErrorHelper = context
                .mock(MailErrorHelper.class);
        mockBamLoader = context.mock(BAMLoader.class);
        bamLoaderSchedulerJob = new BAMLoaderSchedulerJob(){
            protected MailErrorHelper getErrorMailSender() {
                return mockMailErrorHelper;
            }

            protected BAMLoader getBAMLoader(){
                return mockBamLoader;
            }
        };
        jobExecutionContext = getJobExecutionContext();
    }

    @Test
    public void runBAMLoader() throws Exception {

        context.checking(new Expectations() {
            {
                one(mockBamLoader).loadBAMData(with(any(BamContext.class)));
            }
        });

        bamLoaderSchedulerJob.execute(jobExecutionContext);
    }

    @Test
    public void runBAMLoaderWithException() throws Exception{

        context.checking(new Expectations() {
            {
                one(mockBamLoader).loadBAMData(with(any(BamContext.class)));
                will(throwException(new IOException()));
                one(mockMailErrorHelper).getTo();
                one(mockMailErrorHelper).getMailSender().send(with(any(String.class)),
                        with(any(String.class)), with(any(String.class)),
                        with(any(String.class)), with(any(Boolean.class)));
            }
        });

        bamLoaderSchedulerJob.execute(jobExecutionContext);
    }

    @Test
    public void runBAMLoaderWithErrors() throws Exception{

        context.checking(new Expectations() {
            {
                one(mockBamLoader).loadBAMData(with(addValidationErrorsAndWarnings(true)));

                one(mockMailErrorHelper).getTo();
                one(mockMailErrorHelper).getMailSender().send(with(any(String.class)),
                        with(any(String.class)), with(any(String.class)),
                        with(any(String.class)), with(any(Boolean.class)));
            }
        });

        bamLoaderSchedulerJob.execute(jobExecutionContext);
    }

    @Test
    public void runBAMLoaderWithWarnings() throws Exception{

        context.checking(new Expectations() {
            {
                one(mockBamLoader).loadBAMData(with(addValidationErrorsAndWarnings(false)));

                one(mockMailErrorHelper).getTo();
                one(mockMailErrorHelper).getMailSender().send(with(any(String.class)),
                        with(any(String.class)), with(any(String.class)),
                        with(any(String.class)), with(any(Boolean.class)));
            }
        });

        bamLoaderSchedulerJob.execute(jobExecutionContext);
    }

    private static TypeSafeMatcher<BamContext> addValidationErrorsAndWarnings(final Boolean isError) {

        return new TypeSafeMatcher<BamContext>() {

            @Override
            public boolean matchesSafely(final BamContext bamContext) {

                assertNotNull(bamContext);
                if(isError) {
                    bamContext.addError("QCLive", "Error");
                }else{
                    bamContext.addWarning("QCLive", "Warning");
                }
                return true;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("Cleanup JobDetail matches expectations");
            }
        };
    }

    private JobExecutionContext getJobExecutionContext() {
        return getJobExecutionContext("QCLive");
    }

    private JobExecutionContext getJobExecutionContext(final String groupName) {
        final JobDetail jobDetail = new JobDetail();
        jobDetail.setGroup(groupName);
        final Trigger trigger = new SimpleTrigger();
        final TriggerFiredBundle triggerFiredBundle = new TriggerFiredBundle(
                jobDetail, trigger, null, false, null, null, null, null);
        return new JobExecutionContext(mockScheduler, triggerFiredBundle, job);
    }

}
