/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * Class to facilitate various types of logging.  This will be turned into an interface impl once we get some
 * database information setup so we can log to both a file and a database based on the implementation.
 */
public class ProcessLogger implements ProcessLoggerI, Serializable {
    private static final long serialVersionUID = 3982251588415140156L;
    private final Logger LOGGER = Logger.getLogger("TCGALogger");
    private StringBuffer LOG_BUFFER = new StringBuffer(2096);
    private int errors = 0;


    public ProcessLogger() {
    }

    public StringBuffer getLogBuffer() {
        return LOG_BUFFER;
    }

    public void setLoggerLevel(final String level) {
        LOGGER.setLevel(Level.toLevel(level.toUpperCase()));
    }

    public void setLoggerLevel(final Level level) {
        LOGGER.setLevel(level);
    }

    public Level getLoggerLevel() {
        return LOGGER.getLevel();
    }

    public void logToLogger(Level loggingLevel, String whatToLog) {
        LOGGER.log(loggingLevel, whatToLog);
    }

    public void addError() {
        ++errors;
    }

    public int getErrorCount() {
        return errors;
    }

    public void resetErrorCount() {
        errors = 0;
    }

    public void resetLogBuffer() {
        LOG_BUFFER = new StringBuffer(2096);
    }

    public static String stackTracePrinter(final Throwable exception) {
        final StackTraceElement[] stack = exception.getStackTrace();
        final StringBuilder buff = new StringBuilder(2048);
        buff.append(exception.toString()).append('\n');
        for (final StackTraceElement aStack : stack) {
            buff.append(aStack.toString()).append('\n');
        }
        return buff.toString();
    }

    // Records both the error and the time in which it occurred, so it can be matched up with user reports

    public void logError(final Throwable t) {
        logError(t, System.currentTimeMillis());
    }

    public void logError(final Throwable t, final long time) {
        final StringBuilder s = new StringBuilder();
        s.append("Throwable reported at time ").append(Long.toString(time)).append('\n');
        s.append("  Class: ").append(t.getClass().getName()).append('\n');
        s.append("  Message:").append(t.getMessage());
        for (final StackTraceElement ste : t.getStackTrace()) {
            s.append("\n at ").append(ste.toString());
        }
        logToLogger(Level.ERROR, s.toString());
    }
}
