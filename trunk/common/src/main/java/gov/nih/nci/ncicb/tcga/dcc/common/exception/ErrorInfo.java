/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.exception;

/**
 * Class that holds Error information, taken from DAM codebase  
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */

/*
This class is used to get the stack trace in the correct format in JSP.
 */
    
public class ErrorInfo {

    private static final int TRUNCATED_STACK_LENGTH = 10;

    private final Throwable exception;
    private final long time;

    public ErrorInfo( final Throwable e ) {
        this( e, System.currentTimeMillis() );
    }

    public ErrorInfo( final Throwable e, final long time ) {
        this.exception = e;
        this.time = time;
    }

    // used in the error view [jsp] to show stack trace
    public String getStackTrace() {
        final StackTraceElement[] stackTraceElems = exception.getStackTrace();
        final StringBuilder ret = new StringBuilder();
        for (final StackTraceElement stackTraceElem : stackTraceElems) {
            boolean red = false;
            if (stackTraceElem.getClassName().startsWith("gov.nih.nci.ncicb.tcga.dcc.uuid")) {
                ret.append("<font color='red'>");
                red = true;
            }
            ret.append(stackTraceElem.toString());
            if (red) {
                ret.append("</font>");
            }
            ret.append("<br>\n");
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
    
}
