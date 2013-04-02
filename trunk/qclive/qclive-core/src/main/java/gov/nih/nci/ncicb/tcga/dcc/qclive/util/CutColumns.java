/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
/**
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: Apr 29, 2009
 * Time: 5:17:16 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Cuts columns out of a tab-delimited file.
 */
public class CutColumns {

    public static List<String[]> cut( File file, String[] selectedHeaders ) throws IOException {
        if(!file.exists()) {
            throw new IOException( "File does not exist" );
        }
        int[] selectedCols = new int[selectedHeaders.length];
        BufferedReader reader = new BufferedReader( new FileReader( file ) );
        try {
            String firstLine = reader.readLine();
            StringTokenizer st = new StringTokenizer( firstLine, "\t" );
            int colidx1 = 0, colidx2 = 0;
            while(st.hasMoreTokens()) {
                String token = st.nextToken();
                if(isSelectedHeader( token, selectedHeaders )) {
                    selectedCols[colidx2++] = colidx1;
                }
                colidx1++;
            }
            if(colidx2 < selectedHeaders.length) {
                //didn't find all the headers apparently
                throw new IllegalArgumentException( "Not all selected headers found in file" );
            }
        }
        finally {
            reader.close();
        }
        return cut( file, selectedCols );
    }

    public static List<String[]> cut( File file, int[] selectedColumns ) throws IOException {
        if(!file.exists()) {
            throw new IOException( "File does not exist" );
        }
        BufferedReader reader = new BufferedReader( new FileReader( file ) );
        try {
            List<String[]> output = new ArrayList<String[]>();
            String line = reader.readLine();
            while(line != null && line.trim().length() > 0) {
                String[] outputline = new String[selectedColumns.length];
                StringTokenizerThatCanReturnNull st = new StringTokenizerThatCanReturnNull( line, "\t" );
                int outputcolumnIdx = 0;
                int inputcolumnIdx = 0;
                while(st.hasMoreTokens()) {
                    String token = st.nextToken();
                    if(isSelectedColumn( selectedColumns, inputcolumnIdx )) {
                        outputline[outputcolumnIdx++] = token;
                    }
                    inputcolumnIdx++;
                }
                output.add( outputline );
                line = reader.readLine();
            }
            return output;
        }
        finally {
            reader.close();
        }
    }

    private static boolean isSelectedHeader( String header, String[] selectedHeader ) {
        for(int i = 0; i < selectedHeader.length; i++) {
            if(header.equals( selectedHeader[i] )) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSelectedColumn( int[] columns, int inputcolumnIdx ) {
        for(int i = 0; i < columns.length; i++) {
            if(inputcolumnIdx == columns[i]) {
                return true;
            }
        }
        return false;
    }
}
