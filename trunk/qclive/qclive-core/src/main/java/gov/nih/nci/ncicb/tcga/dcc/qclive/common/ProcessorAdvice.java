/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailSender;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.ArchiveDeployer;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.TraceFileProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.UploadChecker;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.DataMatrixValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.ArchiveLogger;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.Logger;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.QcStatsLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.dao.DataAccessException;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Monitors QC process via AOP. Implements before, after returning, and after
 * throwing advice. Will log what methods are called and their return values.
 * Special handling for Validator objects -- will log results/errors/warnings of
 * validation calls.
 * <p/>
 * Can be modified to specially handle other public calls made to beans during
 * QC process.
 *
 * @author Jessica Chen Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class ProcessorAdvice implements MethodBeforeAdvice,
        AfterReturningAdvice, ThrowsAdvice {

    private Logger logger;
    private QcStatsLogger statsLogger;
    private ArchiveLogger archiveLogger;
    private List<String> archiveLoggingProcessorNames;
    private MailSender mailSender;
    private String subjectPrefix;
    private String environment;

    /**
     * @param logger the logger to use for this monitor
     */
    public void setLogger(final Logger logger) {
        this.logger = logger;
    }

    /**
     * Advises calls that are about to be made. Logs method name and parameters.
     * If it's a ProcessStep, calls special handler.
     *
     * @param method the method that is about to be called
     * @param args   the arguments that are going to be passed to method
     * @param target the object the method is about to be called upon
     */
    public void before(final Method method, final Object[] args,
                       final Object target) {
        if (target instanceof Processor && method.getName().equals("execute")) {
            beforeExecute(args[0], (QcContext) args[1], (Processor) target);
        } else {
            logger.log(Level.DEBUG, new StringBuilder().append("Start ")
                    .append(stringForLogging(method, target, args)).toString());
        }
    }

    /*
      * Log that a validation call is beginning.
      */
    private void beforeExecute(final Object arg, final QcContext context,
                               final Processor step) {
        context.setCurrentProcessName(step.getName());
        if (step instanceof TraceFileProcessor) {
            if (mailSender != null && arg instanceof File
                    && context.getArchive() != null) {
                mailSender
                        .send(context.getCenterEmail(),
                                null,
                                ((File) arg).getName() + " processing started",
                                new StringBuilder()
                                        .append("Processing of trace-sample relationship file '")
                                        .append(((File) arg).getName())
                                        .append("' in archive '")
                                        .append(context.getArchive()
                                                .getRealName())
                                        .append("' has started.  Depending on the size of the file, this can take up to 24 hours to complete.  ")
                                        .append("You will receive another email once the archive is fully processed.")
                                        .toString(), false);
            }
        }
        logger.log(Level.DEBUG, new StringBuilder().append("Beginning ")
                .append(step.getName()).append(" on ").append(arg).toString());
        // 	log transaction log record
        if (arg instanceof Archive || (arg instanceof File)) {
            logTransactionLogRecord(step, arg, context);
        }
    }

    /**
     * Advises calls that have just returned. Logs method name, parameters, and
     * return value. If target is a Processor, calls special handler.
     *
     * @param returnValue the value returned by the method call
     * @param method      the method that just returned
     * @param args        the arguments that were passed to the method
     * @param target      the object the method was called upon
     */
    public void afterReturning(final Object returnValue, final Method method,
                               final Object[] args, final Object target) {
        if (target instanceof Processor && method.getName().equals("execute")) {
            afterReturningExecute(returnValue, args[0], (QcContext) args[1],
                    (Processor) target);
            logStats(returnValue, target);
        } else {
            logger.log(
                    Level.DEBUG,
                    new StringBuilder().append("End ")
                            .append(stringForLogging(method, target, args))
                            .toString());
        }
        // only operate on Archive types
        if (args[0] instanceof Archive || args[0] instanceof File) {
            // if the return value is is false, don't update.
            if (returnValue != null &&
                    (!(returnValue instanceof Boolean) ||
                            (returnValue instanceof Boolean && ((Boolean) returnValue)))
                    ) {

                // log transaction log record , if we get to this step , and the result is not false,
                // a Processor has successfully ran and we log its success.
                archiveLogger.updateTransactionLogRecordResult(((QcContext) args[1])
                        .getStateContext().getTransactionId(), ((Processor) target)
                        .getClass().getSimpleName(), true);
            }
        }
    }

    private void logStats(final Object returnValue, final Object target) {
        if (target instanceof UploadChecker && returnValue instanceof Archive
                && statsLogger != null) {
            statsLogger.logIncomingArchive((Archive) returnValue);
        } else if (target instanceof ArchiveDeployer
                && returnValue instanceof Archive && statsLogger != null) {
            statsLogger.logDeployedArchive((Archive) returnValue);
        }
    }

    /*
      * Checks the return value and error condition of the execute call. Logs as
      * appropriate.
      */
    private void afterReturningExecute(final Object returnVal,
                                       final Object arg, final QcContext context, final Processor step) {
        // if there are exceptions we noted for logging, log them and clear the
        // list
        // ... this is kind of weird, but the exceptions in this case aren't
        // thrown so we can't use AOP to log them.
        // For an example see the AnnotationQueriesException caught in the
        // SdrfProcessor class.
        if (context.getExceptionsToLog().size() > 0) {
            for (final Exception e : context.getExceptionsToLog()) {
                logger.log(e);
            }
            context.getExceptionsToLog().clear();
        }

        final boolean isValidation = returnVal instanceof Boolean;
        final StringBuilder message = new StringBuilder();
        message.append("Execution of ").append(step.getName());
        if (arg != null) {
            message.append(" on ").append(arg.toString());
        }

        if ((isValidation && !(Boolean) returnVal)) {
            message.append(" failed");
        } else {
            // don't log this validator if it passed -- it talks too much
            if (step instanceof DataMatrixValidator) {
                return;
            }
            message.append(" succeeded");
        }

        final Archive archive = (arg instanceof Archive) ? (Archive) arg :
                (returnVal instanceof Archive) ? (Archive) returnVal : null;

        Level logLevel = Level.INFO;
        if (archive != null) {
            final List<String> errors = context.getErrorsByProcessName(archive, step.getName());
            if (errors.size() > 0) {
                appendErrors(message, errors);
            }

            final List<String> warnings = context.getWarningsByProcessName(archive, step.getName());
            if (warnings.size() > 0) {
                appendWarnings(message, warnings);
            }
            logLevel = (errors.size() > 0) ? Level.ERROR : ((warnings.size() > 0) ? Level.WARN : Level.INFO);
            // now, if the process was called on an archive or returned an archive,
            // call special methods to log that

            logArchiveProcess(step, archive, message.toString(), context);
        }
        // reset the proces name
        context.setCurrentProcessName("");
        logger.log(logLevel, message.toString());

    }

    protected void logTransactionLogRecord(final Processor processor,
                                           final Object arg, QcContext context) {
        // if transaction has already been started, log using the existing txid
        if (context.getStateContext().getTransactionId() != null
                && context.getStateContext().getTransactionId() > 0) {
            archiveLogger.addTransactionLog(processor.getClass()
                    .getSimpleName(), context.getStateContext()
                    .getTransactionId());
        } else {
            // if new transaction
            if (arg instanceof gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive) {
                // if archive record has already been created
                Archive archive = (Archive) arg;
                // starting logging transaction
                Long txId = archiveLogger.startTransaction(
                        archive.getArchiveName(), environment);
                context.getStateContext().setTransactionId(txId);
                archiveLogger.addTransactionLog(processor.getClass()
                        .getSimpleName(), context.getStateContext()
                        .getTransactionId());
            } else if (arg instanceof java.io.File) {
                // if archive has not been created yet, use file information to
                // start a transaction
                final String archive = getArchiveName(((File) arg).getName());
                Long txId = archiveLogger
                        .startTransaction(archive, environment);
                context.getStateContext().setTransactionId(txId);
                archiveLogger.addTransactionLog(processor.getClass()
                        .getSimpleName(), context.getStateContext()
                        .getTransactionId());
            }
        }
    }

    protected String getArchiveName(final String fileName) {
        String res = "";
        String extension = "";
        if (fileName != null) {
            if (fileName.endsWith(ConstantValues.COMPRESSED_ARCHIVE_EXTENSION)) {
                extension = ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;
            } else if (fileName.endsWith(ConstantValues.UNCOMPRESSED_ARCHIVE_EXTENSION)) {
                extension = ConstantValues.UNCOMPRESSED_ARCHIVE_EXTENSION;
            }
            res = fileName.substring(0, fileName.length() - extension.length());
        }
        return res;
    }

    private void logArchiveProcess(final Processor processor,
                                   final Archive archive, final String message, QcContext context) {
        if (archiveLoggingProcessorNames != null
                && archiveLogger != null
                && archiveLoggingProcessorNames.contains(processor.getClass()
                .getName())) {
            // if this processor is on the list of classes we want to log, then
            // call archive logger
            try {
                archiveLogger.addArchiveLog(archive, message);
            } catch (DataAccessException e) {
                logger.log(e);
            }
        }
    }

    /**
     * Advises exceptions that were thrown from advised methods. Will log the
     * error and save it internally.
     *
     * @param method the method from which the exception was thrown
     * @param args   the arguments that were passed to the method
     * @param target the object on which the method was called
     * @param ex     the exception that was thrown
     */
    public void afterThrowing(final Method method, final Object[] args,
                              final Object target, final Exception ex) {
        if (ex instanceof Processor.ProcessorException) {
            // ProcessorExceptions are normal ways of stopping processing, so
            // aren't something to worry about
            logger.log(Level.ERROR,
                    "ProcessorException handled: " + ex.getMessage());
            logger.log(
                    Level.DEBUG,
                    new StringBuilder().append("Process halted at ")
                            .append(stringForLogging(method, target, args))
                            .toString());
            QcContext qcContext = getQcContext(target, method, args);
            if (qcContext != null
                    && archiveLoggingProcessorNames.contains(target.getClass()
                    .getName())) {
                // find the archive -- if not in the qcContext, maybe it was the
                // argument of the method call
                Archive archive = qcContext.getArchive();
                if (archive == null && args.length > 0
                        && args[0] instanceof Archive) {
                    archive = (Archive) args[0];
                }
            }

        } else {
            final StringBuilder message = new StringBuilder(
                    "Exception caught during ").append(stringForLogging(method,
                    target, args));
            // other exceptions aren't expected, so mean something is wrong
            logger.log(Level.FATAL, message.toString());
            logger.log(ex);
        }
    }

    /* Private Helper Methods */
    /*
      * Generates a string of a method call in the form
      * targetClassName.methodName(arg1, arg2, etc.)
      */

    private String stringForLogging(final Method method, final Object target,
                                    final Object[] args) {
        final StringBuilder sb = new StringBuilder();
        sb.append(target.getClass().getName());
        sb.append(".");
        sb.append(method.getName());
        sb.append("(");
        if (args != null) {
            sb.append(Arrays.toString(args));
        }
        sb.append(")");
        return sb.toString();
    }

    /*
      * If the validator has warnings, will append them to the StringBuilder
      */
    private void appendWarnings(final StringBuilder message,
                                final List<String> warnings) {
        if (warnings.size() > 0) {
            message.append(" with ").append(warnings.size())
                    .append(" warnings:");
            for (final String warning : warnings) {
                message.append("\n\t");
                message.append(warning);
            }
        }
    }

    /*
      * If the validator has errors, will append them to the StringBuilder
      */
    private void appendErrors(final StringBuilder message,
                              final List<String> errors) {
        if (errors.size() > 0) {
            message.append(" with ").append(errors.size())
                    .append(" errors:");
            for (final String error : errors) {
                message.append("\n\t");
                message.append(error);
            }
        }
    }

    private QcContext getQcContext(final Object target, final Method method,
                                   final Object[] args) {
        if (target instanceof Processor && method.getName().equals("execute")
                && args.length > 1) {
            return (QcContext) args[1];
        } else {
            return null;
        }
    }

    public void setArchiveLogger(final ArchiveLogger archiveLogger) {
        this.archiveLogger = archiveLogger;
    }

    public void setStatsLogger(final QcStatsLogger statsLogger) {
        this.statsLogger = statsLogger;
    }

    public void setArchiveLoggingProcessorNames(
            final List<String> archiveLoggingProcessorNames) {
        this.archiveLoggingProcessorNames = archiveLoggingProcessorNames;
    }

    public void setMailSender(final MailSender mailSender) {
        this.mailSender = mailSender;
    }

    // used to parse out environment from subject prefix header
    protected String parseEnvironment(String prefix) {
        String returnValue = "";
        // the enviroment is parsed from tcga.dcc.subjectPrefix variable
        // which has enviroment enclosed in [ ]
        if (StringUtils.isNotEmpty(prefix) && prefix.contains("[")
                && prefix.contains("]")) {
            int startIndex = prefix.indexOf("[") + 1;
            int endIndex = prefix.indexOf("]");
            if (StringUtils.isNotEmpty(prefix.substring(startIndex, endIndex))) {
                returnValue = prefix.substring(startIndex, endIndex);
            }
        }
        return returnValue;
    }

    public void setSubjectPrefix(String subjectPrefix) {
        this.subjectPrefix = subjectPrefix;
        this.environment = parseEnvironment(subjectPrefix);
    }
}