/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;

/**
 * @author David Nassau
 */
public class ThreadedBufferedWriterSlowTest extends TestCase {

    static final int BUFFER_SIZE = 16384;
    static final int DATA_COLUMNS = 25;
    static final int DATA_ROWS = 5;
    static final int SLEEP_BETWEEN_COLUMNS = 100;
    private static final String SAMPLE_FOLDER = 
    	Thread.currentThread().getContextClassLoader().getResource("samples/threadedBufferedWriter").getPath() + File.separator;

    private static String testOutputFile = SAMPLE_FOLDER + "threaded_output.txt";
    private static String testCompareFile = SAMPLE_FOLDER + "nonthreaded_output.txt";
//    public void testWriter() throws IOException, InterruptedException {
//        PrintWriter writer = new PrintWriter( new ThreadedBufferedWriter( new FileWriter(testOutputFile), BUFFER_SIZE ));
//        writeOutput( writer );
//    }

    public void testCompareSpeed() throws IOException, InterruptedException {

        PrintWriter writer1 = null;
        PrintWriter writer2 = null;

        try {
            //noinspection IOResourceOpenedButNotSafelyClosed
            writer1 = new PrintWriter( new ThreadedBufferedWriter( new FileWriter( testOutputFile ), BUFFER_SIZE ) );
            long before1 = System.currentTimeMillis();
            writeOutput( writer1 );
            long after1 = System.currentTimeMillis();
            long diff1 = after1 - before1;
            System.out.println( "Threaded: took " + diff1 + " milliseconds" );
            //noinspection IOResourceOpenedButNotSafelyClosed
            writer2 = new PrintWriter( new BufferedWriter( new FileWriter( testCompareFile ), BUFFER_SIZE ) );
            long before2 = System.currentTimeMillis();
            writeOutput( writer2 );
            long after2 = System.currentTimeMillis();
            long diff2 = after2 - before2;
            System.out.println( "Nonthreaded: took " + diff2 + " milliseconds" );
            compareOutput();
            //assertTrue(diff2 >= diff1);
            System.out.println( "Threaded is " + ( diff2 - diff1 ) + " millis faster" );
        } finally {
            IOUtils.closeQuietly(writer1);
            IOUtils.closeQuietly(writer2);
        }
    }

    private void writeOutput( final PrintWriter writer ) throws InterruptedException, IOException {
        //Random rnd = new Random();
        double[][] numbers = new double[DATA_ROWS][DATA_COLUMNS];
        for(int irow = 0; irow < DATA_ROWS; irow++) {
            for(int icol = 0; icol < DATA_COLUMNS; icol++) {
                //double number = rnd.nextDouble();
                double number = irow + ( (double) icol ) / 10. + ( (double) icol ) / 100. + ( (double) icol ) / 1000. + ( (double) icol ) / 10000. + ( (double) icol ) / 100000.;
                Thread.sleep( SLEEP_BETWEEN_COLUMNS );  //simulating a program that's doing some other work like db retrieval
                writer.print( number );
                writer.print( '\t' );
                numbers[irow][icol] = number;
            }
            writer.println();
            System.out.println( "wrote row " + irow );
        }
        writer.flush();
        writer.close();
        checkOutput( numbers );
    }

    private void checkOutput( final double[][] numbers ) throws IOException {

        BufferedReader reader = null;
        try {
            //noinspection IOResourceOpenedButNotSafelyClosed
            reader = new BufferedReader( new FileReader( testOutputFile ) );
            String line = reader.readLine();
            int irow = 0;
            while(line != null) {
                StringTokenizer st = new StringTokenizer( line, "\t" );
                int icol = 0;
                while(st.hasMoreTokens()) {
                    double d = Double.parseDouble( st.nextToken() );
                    assertEquals( d, numbers[irow][icol] );
                    icol++;
                }
                assertEquals( icol, DATA_COLUMNS );
                line = reader.readLine();
                irow++;
            }
            assertEquals(irow, DATA_ROWS);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    private void compareOutput() throws IOException {

        BufferedReader reader1 = null;
        BufferedReader reader2 = null;

        try {
            //noinspection IOResourceOpenedButNotSafelyClosed
            reader1 = new BufferedReader( new FileReader( testOutputFile ) );
            //noinspection IOResourceOpenedButNotSafelyClosed
            reader2 = new BufferedReader( new FileReader( testCompareFile ) );
            String line1 = reader1.readLine();
            String line2 = reader2.readLine();
            while(line1 != null && line2 != null) {
                assertEquals( line1, line2 );
                line1 = reader1.readLine();
                line2 = reader2.readLine();
            }
            assertNull( line1 );
            assertNull( line2 );
        } finally {
            IOUtils.closeQuietly(reader1);
            IOUtils.closeQuietly(reader2);
        }
    }
    //added to try profiler
//    public static void main(String[] args) {
//        try {
//            ThreadedBufferedWriterTest test = new ThreadedBufferedWriterTest();
//            test.testCompareSpeed();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
