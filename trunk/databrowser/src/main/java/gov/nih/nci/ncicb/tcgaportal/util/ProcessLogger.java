package gov.nih.nci.ncicb.tcgaportal.util;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Description :
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class ProcessLogger {
    private final Logger LOGGER = Logger.getLogger( "TCGALogger" );
    private StringBuffer LOG_BUFFER = new StringBuffer( 2096 );
    private int errors = 0;

    public StringBuffer getLogBuffer() {
        return LOG_BUFFER;
    }

    public void setLoggerLevel( final String level ) {
        LOGGER.setLevel( Level.toLevel( level.toUpperCase() ) );
    }

    public Level getLoggerLevel() {
        return LOGGER.getLevel();
    }

    public void logToLogger( Level loggingLevel, String whatToLog ) {
        LOGGER.log( loggingLevel, whatToLog );
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
        LOG_BUFFER = new StringBuffer( 2096 );
    }

    public static String stackTracePrinter( final Throwable exception ) {
        final StackTraceElement[] stack = exception.getStackTrace();
        final StringBuilder buff = new StringBuilder( 2048 );
        if (exception.getMessage() != null) {
            buff.append( exception.getMessage() );
            buff.append( '\n').append( '\n' );
        }
        buff.append( exception.toString() ).append( '\n' );
        for(final StackTraceElement aStack : stack) {
            buff.append( aStack.toString() ).append( '\n' );
        }
        return buff.toString();
    }

    // Records both the error and the time in which it occurred, so it can be matched up with user reports
    public void logError( final Throwable t ) {
        logError( t, System.currentTimeMillis() );
    }

    public void logError( final Throwable t, final long time ) {
        final StringBuilder s = new StringBuilder();
        s.append( "Throwable reported at time " ).append( Long.toString( time ) );
        s.append( "  Message:" ).append( t.getMessage() );
        for(final StackTraceElement ste : t.getStackTrace()) {
            s.append( "\n at " ).append( ste.toString() );
        }
        logToLogger( Level.ERROR, s.toString() );
    }

    public void logDebug(final String message) {
        logToLogger( Level.DEBUG, message);
    }

}
