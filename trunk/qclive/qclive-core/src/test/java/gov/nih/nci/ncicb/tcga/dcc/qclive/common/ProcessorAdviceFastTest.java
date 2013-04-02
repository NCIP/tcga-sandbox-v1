/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailSender;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.ArchiveDeployer;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.TraceFileProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.UploadChecker;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.DataMatrixValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.ArchiveLogger;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.Logger;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.QcStatsLogger;
import org.apache.log4j.Level;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test class for QCLogger
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class ProcessorAdviceFastTest {

    private ProcessorAdvice processorAdvice = new ProcessorAdvice();
    private Mockery context = new JUnit4Mockery();
    private Logger logger;
    private Processor mockStep;
    private Method executeMethod;
    private String processStepClass;
    private String mockStepName = "MOCK";
    private MailSender mockMailSender;
    private QcContext qcContext;
    private QcStatsLogger mockStatsLogger;
    private ArchiveLogger archiveLogger;
    private Archive archive = new Archive();

    @Before
    public void setUp() throws NoSuchMethodException {
        qcContext = new QcContext();
        QcLiveStateBean stateContext = new QcLiveStateBean();
        stateContext.setTransactionId(1l);
        qcContext.setStateContext(stateContext);
        // create mocks
        logger = context.mock(Logger.class);
        mockMailSender = context.mock(MailSender.class);
        mockStep = context.mock(Processor.class);
        mockStatsLogger = context.mock(QcStatsLogger.class);
        executeMethod = mockStep.getClass().getMethod("execute", Object.class, QcContext.class);
        processorAdvice.setLogger(logger);
        processorAdvice.setMailSender(mockMailSender);
        processStepClass = mockStep.getClass().getName();
        context.checking(new Expectations() {{
            allowing(mockStep).getName();
            will(returnValue(mockStepName));
        }});
        processorAdvice.setStatsLogger(mockStatsLogger);
        archiveLogger = context.mock(ArchiveLogger.class);
        processorAdvice.setArchiveLogger(archiveLogger);
    }

    @Test
    public void testGetArchiveNameGood() throws Exception {
        String targz = processorAdvice.getArchiveName("test.tar.gz");
        String tar = processorAdvice.getArchiveName("test.tar");
        assertNotNull(targz);
        assertNotNull(tar);
        assertEquals("test", targz);
        assertEquals("test", tar);
    }

    @Test
    public void testGetArchiveNameBad() throws Exception {
        String empty = processorAdvice.getArchiveName(null);
        String wrong = processorAdvice.getArchiveName("test.zip");
        assertNotNull(empty);
        assertNotNull(wrong);
        assertEquals("", empty);
        assertEquals("test.zip", wrong);
    }

    @Test
    public void testBefore() {
        final String validationArg = "arg1";
        // after before is called, logger should have had log called twice, once with debug and once with info
        context.checking(new Expectations() {{
            one(logger).log(Level.DEBUG, "Beginning " + mockStepName + " on " + validationArg);
            allowing(archiveLogger).addTransactionLog(with(any(String.class)), with(any(Long.class)));
            allowing(archiveLogger).updateTransactionLogRecordResult(with(any(Long.class)), with(any(String.class)), with(any(Boolean.class)));

        }});
        processorAdvice.before(executeMethod, new Object[]{validationArg, qcContext}, mockStep);
    }

    @Test
    public void testAfterPassed() {
        final String validationArg = "something";
        context.checking(new Expectations() {{
            one(logger).log(Level.INFO, "Execution of " + mockStepName + " on " + validationArg + " succeeded");
            allowing(archiveLogger).updateTransactionLogRecordResult(with(any(Long.class)), with(any(String.class)), with(any(Boolean.class)));
        }});
        processorAdvice.afterReturning(true, executeMethod, new Object[]{validationArg, qcContext}, mockStep);
    }

    @Test
    public void testLogStatsCalledAfterUpload() {
        context.checking(new Expectations() {{
            allowing(logger).log(with(any(Level.class)), with(any(String.class)));
            one(mockStatsLogger).logIncomingArchive(archive);
            allowing(archiveLogger).updateTransactionLogRecordResult(with(any(Long.class)), with(any(String.class)), with(any(Boolean.class)));
        }});

        // 1. should be called when UploadChecker is the step
        processorAdvice.afterReturning(archive, executeMethod, new Object[]{new File("test"), qcContext}, new UploadChecker());
    }

    @Test
    public void testLogStatsCalledAfterDeployment() {
        context.checking(new Expectations() {{
            allowing(logger).log(with(any(Level.class)), with(any(String.class)));
            one(mockStatsLogger).logDeployedArchive(archive);
            allowing(archiveLogger).updateTransactionLogRecordResult(with(any(Long.class)), with(any(String.class)), with(any(Boolean.class)));
        }});

        processorAdvice.afterReturning(archive, executeMethod, new Object[]{archive, qcContext}, new ArchiveDeployer());
    }

    @Test
    public void testAfterPassedWithWarnings() {
        final int numWarnings = 3;
        final Archive archive = new Archive("test.tar.gz");
        qcContext.setCurrentProcessName(mockStep.getName());
        qcContext.setItemInProgress(archive);
        qcContext.addWarning("warning1");
        qcContext.addWarning("warning2");
        qcContext.addWarning("warning3");
        // check success with warnings
        context.checking(new Expectations() {{
            one(logger).log(Level.WARN, "Execution of " + mockStepName + " on test succeeded with " +
                    numWarnings + " warnings:\n\twarning1\n\twarning2\n\twarning3");
            allowing(archiveLogger).updateTransactionLogRecordResult(with(any(Long.class)), with(any(String.class)), with(any(Boolean.class)));
        }});

        processorAdvice.afterReturning(true, executeMethod, new Object[]{archive, qcContext}, mockStep);
    }

    @Test
    public void testAfterWithErrors() {
        final int numErrors = 2;
        final Archive archive = new Archive("test.tar.gz");
        qcContext.setCurrentProcessName(mockStep.getName());
        qcContext.setItemInProgress(archive);
        qcContext.addError("error1");
        qcContext.addError("error2");
        context.checking(new Expectations() {{
            one(logger).log(Level.ERROR, "Execution of " + mockStepName + " on test failed with " + numErrors + " errors:\n\terror1\n\terror2");
            allowing(archiveLogger).updateTransactionLogRecordResult(with(any(Long.class)), with(any(String.class)), with(any(Boolean.class)));
        }});
        processorAdvice.afterReturning(false, executeMethod, new Object[]{archive, qcContext}, mockStep);
    }

    @Test
    public void testAfterThrowing() {
        @SuppressWarnings({"ThrowableInstanceNeverThrown"})
        final Exception ex = new Exception("this isn't a real exception");
        context.checking(new Expectations() {{
            one(logger).log(Level.FATAL, "Exception caught during " + processStepClass + ".execute([arg1])");
            one(logger).log(ex);
        }});
        processorAdvice.afterThrowing(executeMethod, new Object[]{"arg1"}, mockStep, ex);
    }

    @Test
    public void testAfterThrowingProcessorException() {
        // make it so the mock step is something we call the QcArchiveLogger for
        processorAdvice.setArchiveLoggingProcessorNames(Arrays.asList(mockStep.getClass().getName()));
        @SuppressWarnings({"ThrowableInstanceNeverThrown"})
        final Processor.ProcessorException processorException = new Processor.ProcessorException("test");
        context.checking(new Expectations() {{
            one(logger).log(Level.ERROR, "ProcessorException handled: test");
            one(logger).log(Level.DEBUG, "Process halted at " + mockStep.getClass().getName() + "." + executeMethod.getName() + "([arg])");

        }});
        processorAdvice.afterThrowing(executeMethod, new Object[]{"arg"}, mockStep, processorException);
    }

    @Test
    public void testArchiveLogger() {
        List<String> classNames = new ArrayList<String>();
        classNames.add(mockStep.getClass().getName());
        processorAdvice.setArchiveLoggingProcessorNames(classNames);
        String validationArg = "hi";
        context.checking(new Expectations() {{
            one(logger).log(Level.INFO, "Execution of MOCK on hi succeeded");
            one(archiveLogger).addArchiveLog(archive, "Execution of MOCK on hi succeeded");
            allowing(archiveLogger).updateTransactionLogRecordResult(with(any(Long.class)), with(any(String.class)), with(any(Boolean.class)));
        }});
        processorAdvice.afterReturning(archive, executeMethod, new Object[]{validationArg, qcContext}, mockStep);
    }

    @Test
    public void testArchiveLoggerNotLoggingStep() {
        // same as above test but don't add the mock processor to the list of classes we want to log for the archive logger
        List<String> classNames = new ArrayList<String>();
        processorAdvice.setArchiveLoggingProcessorNames(classNames);
        String validationArg = "hi";
        context.checking(new Expectations() {{
            one(logger).log(Level.INFO, "Execution of MOCK on hi succeeded");
            allowing(archiveLogger).updateTransactionLogRecordResult(with(any(Long.class)), with(any(String.class)), with(any(Boolean.class)));
        }});
        processorAdvice.afterReturning(archive, executeMethod, new Object[]{validationArg, qcContext}, mockStep);
    }

    @Test
    public void testArchiveLoggerAfterException() {
        @SuppressWarnings({"ThrowableInstanceNeverThrown"})
        Processor.ProcessorException processorException = new Processor.ProcessorException("TEST ERROR MSG");
        processorAdvice.setArchiveLoggingProcessorNames(Arrays.asList(mockStep.getClass().getName()));
        context.checking(new Expectations() {{
            allowing(logger).log(with(any(Level.class)), with(any(String.class)));
            //one( archiveLogger ).addArchiveLog( archive, "TEST ERROR MSG");            
        }});
        processorAdvice.afterThrowing(executeMethod, new Object[]{archive, qcContext}, mockStep, processorException);
    }

    @Test
    public void testBeforeTraceFileProcessor() {
        final TraceFileProcessor traceFileProcessor = new TraceFileProcessor();
        File fakeFile = new File("fakeFile");
        Archive archive = new Archive();
        archive.setRealName("theArchive");
        Center center = new Center();
        List<String> emailList = new ArrayList<String>();
        emailList.add("EMAIL");
        center.setEmailList(emailList);
        archive.setTheCenter(center);
        qcContext.setArchive(archive);

        context.checking(new Expectations() {{
            // processor advice's mail sender should be called
            one(mockMailSender).send("EMAIL", null, "fakeFile processing started",
                    "Processing of trace-sample relationship file 'fakeFile' in archive 'theArchive' has started.  Depending on the size of the file, this can take up to 24 hours to complete.  You will receive another email once the archive is fully processed.", false);
            one(logger).log(Level.DEBUG, "Beginning " + traceFileProcessor.getName() + " on fakeFile");
            allowing(archiveLogger).addTransactionLog("TraceFileProcessor", 1l);
            allowing(archiveLogger).updateTransactionLogRecordResult(1l, "DataMatrixValidator", true);
        }});
        processorAdvice.before(executeMethod, new Object[]{fakeFile, qcContext}, traceFileProcessor);
    }

    @Test
    public void testAfterExecuteDataMatrixValidator() {
        final DataMatrixValidator dataMatrixValidator = new DataMatrixValidator();
        final DataMatrix dataMatrix = new DataMatrix();

        // we expect the logger to NOT be called in this case, because the execute method was called on the DataMatrixValidator, which is too spammy for logging
        context.checking(new Expectations() {{
            never(logger).log(with(any(Level.class)), with(any(String.class)));
            allowing(archiveLogger).updateTransactionLogRecordResult(1l, "DataMatrixValidator", true);
        }});

        // this is what would be called if dataMatrixValidator.execute(dataMatrix, qcContext) was called and returned true
        processorAdvice.afterReturning(true, executeMethod, new Object[]{dataMatrix, qcContext}, dataMatrixValidator);
    }

    @Test
    public void testAfterExecuteDataMatrixValFailure() {
        final DataMatrixValidator dataMatrixValidator = new DataMatrixValidator();
        final DataMatrix dataMatrix = new DataMatrix();

        // we expect the logger to be called in this case, even though it is a DataMatrixValidator call, because it failed
        context.checking(new Expectations() {{
            one(logger).log(with(any(Level.class)), with(any(String.class)));
            allowing(archiveLogger).updateTransactionLogRecordResult(1l, "DataMatrixValidator", true);
        }});

        // this is what would be called if dataMatrixValidator.execute(dataMatrix, qcContext) was called and returned true        
        processorAdvice.afterReturning(false, executeMethod, new Object[]{dataMatrix, qcContext}, dataMatrixValidator);
    }

    @Test
    public void testLogTransactionLogRecord() {
        processorAdvice.setSubjectPrefix("[DEV]");
        Object[] argArray = new Object[]{"arg", qcContext};
        context.checking(new Expectations() {{
            allowing(archiveLogger).addTransactionLog(with(any(String.class)), with(any(Long.class)));
            allowing(archiveLogger).startTransaction("test", "DEV");
        }});
        processorAdvice.logTransactionLogRecord(mockStep, argArray[0], qcContext);
        // test new transaction with an Archive
        archive.setArchiveFile(new File("/fakedir/test.tar.gz"));
        argArray = new Object[]{archive, qcContext};
        qcContext.getStateContext().setTransactionId(0l);
        processorAdvice.logTransactionLogRecord(mockStep, argArray[0], qcContext);
        // test new transaction with a File
        File tstFile = new File("/fakedir/test.tar.gz");
        argArray = new Object[]{tstFile, qcContext};
        qcContext.getStateContext().setTransactionId(0l);
        processorAdvice.logTransactionLogRecord(mockStep, argArray[0], qcContext);
    }

    @Test
    public void testParseEnvironment() {
        assertEquals("", processorAdvice.parseEnvironment(""));
        assertEquals("", processorAdvice.parseEnvironment("abc"));
        assertEquals("", processorAdvice.parseEnvironment("[abc"));
        assertEquals("", processorAdvice.parseEnvironment("abc]"));
        assertEquals("", processorAdvice.parseEnvironment("[]"));
        assertEquals("abc", processorAdvice.parseEnvironment("[abc]"));
        assertEquals("abc", processorAdvice.parseEnvironment("[abc][a][b]"));
        assertEquals("abc", processorAdvice.parseEnvironment("blahblah[abc]b]"));

    }


}
