/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

/**
 * Small class that defines a full verbose print exception
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class FancyExceptionLogger {

    public static String printException(Exception e) {
        StringBuilder exception = new StringBuilder();
        if (e != null) {
            StackTraceElement[] ste = e.getStackTrace();
            exception.append("----------------- A New Exception Occured -----------------\n");
            exception.append("Description:\n");
            exception.append("--> " + e.toString() + "\n");
            exception.append("Trace:\n");
            for (int i = 0; i < ste.length; i++) {
                exception.append("--> " + ste[i] + "\n");
            }
            Throwable cause = e.getCause();
            if (cause != null) {
                ste = cause.getStackTrace();
                exception.append("Cause:\n");
                exception.append("--> " + cause.toString() + "\n");
                for (int i = 0; i < ste.length; i++) {
                    exception.append("--> " + ste[i] + "\n");
                }
            }
        }
        return exception.toString();
    }
    
}//End of Class
