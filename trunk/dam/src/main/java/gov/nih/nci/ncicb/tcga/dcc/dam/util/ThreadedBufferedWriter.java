/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.util;

import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import org.apache.log4j.Level;

import java.io.IOException;
import java.io.Writer;

/*
    Author: David Nassau
    Allows the writer to write to file while the calling program is still adding more
    data to the buffer.
    Note: this class is not thread safe (it has its own inner thread but the writer itself can't be used
    by >1 thread)
    Note: usage of this class assumes that the caller calls flush() before calling close(). There is
    no implicit call to flush().
 */
public class ThreadedBufferedWriter extends Writer {

    //inner class containing a thread that writes out the data while the parent is still
    //able to gather new data into the buffer
    private class WriterThread extends Thread {

        static final boolean DEBUG = false;
        String content;
        Writer out;
        boolean isWriting;
        IOException ioException;
        boolean die;

        public WriterThread( Writer out ) {
            if(DEBUG) {
                System.out.println( ">>WriterThread()" );
            }
            this.out = out;
            this.setName( "ThreadedBufferedWriter_WriterThread" );
        }

        //Called by the writer itself to load data to be written. Note - this method lives in the
        //caller's thread so must be synchronized with the WriterThread.
        public void loadBuffer( String s ) throws IOException {
            synchronized(this) {
                if(DEBUG) {
                    logger.logToLogger( Level.DEBUG, ">>WriterThread.loadBuffer()" );
                }
                if(ioException != null) {
                    //something failed while writing, so we want to kill all further processing - this thread is now effectively dead
                    if(DEBUG) {
                        logger.logToLogger( Level.DEBUG, ">>WriterThread.loadBuffer() throwing IOException" );
                    }
                    throw ioException;
                }
                //in case loadBuffer() is called twice rapidly before run() had a chance to pick it up
                //We make the second one wait until run() is done with the first
                while(isWriting) {
                    try {
                        if(DEBUG) {
                            logger.logToLogger( Level.DEBUG, ">>WriterThread.loadBuffer() going into wait" );
                        }
                        this.wait();
                    }
                    catch(InterruptedException e) {
                        logger.logToLogger( Level.INFO, "InterruptedException caught in WriterThread.loadBuffer" );
                    }
                    if(DEBUG) {
                        logger.logToLogger( Level.DEBUG, ">>WriterThread.loadBuffer() woke up from wait" );
                    }
                }
                content = s;
                if(DEBUG) {
                    logger.logToLogger( Level.DEBUG, "loadBuffer: setting isWriting to true" );
                }
                isWriting = true;
                if(DEBUG) {
                    logger.logToLogger( Level.DEBUG, "Calling notify() within WriterThread.loadBuffer()" );
                }
                this.notifyAll();
            }
        }

        //The method that does the actual writing to out stream
        public void run() {
            if(DEBUG) {
                logger.logToLogger( Level.DEBUG, ">>WriterThread.run()" );
            }
            //thread loops eternally as long as parent class is alive
            //we synchronize so that in case the parent class tries to call loadBuffer again
            //while we are still writing, loadBuffer will wait until this loop is finished
            synchronized(this) {
                for(; ;) {
                    if(DEBUG) {
                        logger.logToLogger( Level.DEBUG, "WriterThread.run() woke up from wait; isWriting==" + isWriting );
                    }
                    if(isWriting) {
                        try {
                            if(out == null) {
                                throw new IOException( "Stream closed" );
                            }
                            if(content != null) {
                                if(DEBUG) {
                                    logger.logToLogger( Level.DEBUG, "about to write to stream" );
                                }
                                out.write( content );
                                if(DEBUG) {
                                    logger.logToLogger( Level.DEBUG, "wrote to stream" );
                                }
                            }
                        }
                        catch(IOException e) {
                            if(DEBUG) {
                                logger.logToLogger( Level.DEBUG, "WriterThread.run(): IOException happened" );
                            }
                            logger.logError( e );
                            ioException = e;
                        }
                        finally {
                            if(DEBUG) {
                                logger.logToLogger( Level.DEBUG, "setting isWriting to false" );
                            }
                            isWriting = false;
                            if(DEBUG) {
                                logger.logToLogger( Level.DEBUG, "run() calling notify" );
                            }
                            this.notifyAll();
                        }
                    }
                    try {
                        //waits for parent class to call loadBuffer
                        if(DEBUG) {
                            logger.logToLogger( Level.DEBUG, "WriterThread.run() going into wait" );
                        }
                        this.wait();
                    }
                    catch(InterruptedException e) {
                        logger.logToLogger( Level.INFO, "InterruptedException caught in WriterThread.loadBuffer" );
                    }
                    if(die) {
                        //parent is done writing data, let the thread die
                        if(DEBUG) {
                            logger.logToLogger( Level.DEBUG, "WriterThread.run() has been signalled to die, breaking loop" );
                        }
                        break;
                    }
                }
            }
            if(DEBUG) {
                logger.logToLogger( Level.DEBUG, "WriterThread is dying" );
            }
        }

        //set flag allowing the thread to die - writer calls this from close()
        public void die() {
            if(DEBUG) {
                logger.logToLogger( Level.DEBUG, ">>WriterThread.die()" );
            }
            synchronized(this) {
                while(isWriting) {
                    //let it finish writing the last buffer
                    try {
                        if(DEBUG) {
                            logger.logToLogger( Level.DEBUG, "WriterThread.die going into wait" );
                        }
                        this.wait();
                    }
                    catch(InterruptedException e) {
                        logger.logToLogger( Level.INFO, "InterruptedException caught in WriterThread.loadBuffer" );
                    }
                    if(DEBUG) {
                        logger.logToLogger( Level.DEBUG, "WriterThread.die woke up from wait, isWriting==" + isWriting );
                    }
                }
                if(DEBUG) {
                    logger.logToLogger( Level.DEBUG, "setting die flag to true and calling notify" );
                }
                die = true;
                this.notifyAll();
            }
        }
    }

    private WriterThread writerThread;
    private ProcessLogger logger = new ProcessLogger();
    //e.g. StreamWriter that writes to file
    private Writer out;
    //decided to use a stringbuilder since it watches its memory and avoids unnecessary allocations
    private StringBuilder buf;
    private static int DEFAULT_BUFFER_SIZE = 8192;
    private int bufferSize = DEFAULT_BUFFER_SIZE;
    private int contentLen = 0;
    /**
     * Line separator string. This is the value of the line.separator
     * property at the moment that the stream was created.
     */
    private String lineSeparator;

    public ThreadedBufferedWriter( Writer out ) {
        this( out, DEFAULT_BUFFER_SIZE );
    }

    public ThreadedBufferedWriter( Writer out, int size ) {
        super( new Object() ); //used as "lock" object in a few methods
        if(size <= 0) {
            throw new IllegalArgumentException( "Buffer size <= 0" );
        }
        this.out = out;
        buf = new StringBuilder( size );
        contentLen = 0;
        bufferSize = size;
        lineSeparator = (String) java.security.AccessController.doPrivileged(
                new sun.security.action.GetPropertyAction( "line.separator" ) );
        writerThread = new WriterThread( out );
        writerThread.start();
    }

    //flushes buffer by communicating with the WRiterThread
    void flushBuffer() throws IOException {
        synchronized(lock) {
            String s = buf.toString();
            //clear the buffer so more can be written to it while the WRiterThread is still working
            buf.setLength( 0 );
            contentLen = 0;
            writerThread.loadBuffer( s );
        }
    }

    public void write( int c ) throws IOException {
        if(contentLen + 1 > bufferSize) {
            flushBuffer();
        }
        buf.append( (char) c );
        contentLen++;
    }

    public void write( char cbuf[], int off, int len ) throws IOException {
        if(contentLen + len > bufferSize) {
            flushBuffer();
        }
        buf.append( cbuf, off, len );  //note: this could potentially result in the StringBuilder growing its memory allocation - unlikely and not really a big problem
        contentLen += len;
    }

    public void write( String s, int off, int len ) throws IOException {
        if(contentLen + len > bufferSize) {
            flushBuffer();
        }
        buf.append( s, off, len );
        contentLen += len;
    }

    public void newLine() throws IOException {
        if(contentLen + 1 > bufferSize) {
            flushBuffer();
        }
        buf.append( lineSeparator );
        contentLen++;
    }

    //Note: this writer does not implicit flush upon close() so it's assumed the caller calls flush() then close().
    public void flush() throws IOException {
        synchronized(lock) {
            flushBuffer();
        }
    }

    //assumption here is that the caller will call flush() and then close().
    public void close() throws IOException {
        synchronized(lock) {
            //thread will die but caller already called flush() so there's nothing left to write.
            writerThread.die();
            out.flush();
            out.close();
            out = null;
        }
    }
}