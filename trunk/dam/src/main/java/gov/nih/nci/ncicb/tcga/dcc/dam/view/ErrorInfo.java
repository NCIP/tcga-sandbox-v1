/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.view;

import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;

/**
 * Author: David Nassau
 * <p/>
 * Used to communicate error information through the JSP
 */
public class ErrorInfo {

    private static final int TRUNCATED_STACK_LENGTH = 10;

    /**
     * Injected with values from Spring xml
     */
    public static class ErrorHandlerParameters {

        private static String emailTo;
        private static final ProcessLogger plog = new ProcessLogger();

        public void setEmailTo( final String value ) {
            emailTo = value;
        }

        static String getEmailTo() {
            return emailTo;
        }

        //todo  move to outer class?
        static void log( final ErrorInfo ei ) {
            plog.logError( ei.getException(), ei.getTime() );
        }
    }

    private final Throwable exception;
    private final long time;

    public ErrorInfo( final Throwable e ) {
        this( e, System.currentTimeMillis() );
    }

    public ErrorInfo( final Throwable e, final long time ) {
        this( e, time, true );
    }

    public ErrorInfo( final Throwable e, final long time, final boolean report ) {
        this.exception = e;
        this.time = time;
        if(report) {
            ErrorHandlerParameters.log( this );
        }
    }

    public String getStackTrace() {
        final StackTraceElement[] stackTraceElems = exception.getStackTrace();
        final StringBuilder ret = new StringBuilder();
        for(int i = 0; i < stackTraceElems.length; i++) {
            boolean red = false;
            if(stackTraceElems[i].getClassName().startsWith( "gov.nih.nci.ncicb.tcga.dcc" )) {
                ret.append( "<font color='red'>" );
                red = true;
            }
            ret.append( stackTraceElems[i].toString() );
            if(red) {
                ret.append( "</font>" );
            }
            ret.append( "<br>\n" );
        }
        return ret.toString();
    }

    public String getTruncatedStackTrace() {
        final StackTraceElement[] stackTraceElems = exception.getStackTrace();
        final StringBuilder ret = new StringBuilder();
        final int max = ( stackTraceElems.length > TRUNCATED_STACK_LENGTH ? TRUNCATED_STACK_LENGTH : stackTraceElems.length );
        for(int i = 0; i < max; i++) {
            ret.append( stackTraceElems[i].toString() );
            ret.append( "\n" );
        }
        return ret.toString();
    }

    public String getMessage() {
        String ret = exception.getMessage();
        if(ret == null) {
            ret = exception.getClass().toString(); //esp. for case of NullPointerException which has no message
        }
        return ret;
    }

    public String getEmailTo() {
        return ErrorHandlerParameters.getEmailTo();
    }

    public long getTime() {
        return time;
    }

    Throwable getException() {
        return exception;
    }
}
