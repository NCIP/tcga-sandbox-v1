/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;

import au.com.bytecode.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Robert S. Sfeir
 */
public class IDF {

    private List<String[]> theIDF;

    public void initIDF( String fileLocation, String fileName ) throws IOException {    	
    	FileReader freader = null;
    	CSVReader reader = null;
    	try{
    		freader =  new FileReader( new File( fileLocation, fileName ) );
    		reader = new CSVReader(freader, '\t' );
	        //Set the ArrayList size to 44 as this is currently the max number of possible IDF line entries.
	        theIDF = new ArrayList<String[]>( 44 );
	        String[] nextLine;
	        while(( nextLine = reader.readNext() ) != null) {
	            // nextLine[] is an array of values from the line
	            if(nextLine[0].trim().length() > 0) {
	                theIDF.add( nextLine );
	            }
	        }
    	}finally{    	
    		if (freader != null){
    			freader.close();
    		}
    		if (reader != null){
    			reader.close();
    		}    		
    	}
    }

    public List<String[]> getIDF() {
        return theIDF;
    }

    public List<String> getIDFColHeaders() {
        List<String> headerList = new ArrayList<String>( 44 );
        loopOverArray( 0, headerList );
        return headerList;
    }

    public List<String> getIDFColByNumber( final int colNumber ) {
        List<String> headerList = new ArrayList<String>( 44 );
        loopOverArray( colNumber, headerList );
        return headerList;
    }

    private List<String> loopOverArray( final int colNumber, final List<String> headerList ) {
        if (colNumber >= 0) {
            Iterator<String[]> it = theIDF.iterator();
            if(it.hasNext()) {
                do {
                    String[] stringArr = it.next();
                    if(stringArr[0].trim().length() > 0) {
                        /*
                         * If the length of this stringArr is 1 this means there is only a row header value in this row
                         * this row has no other values.  To avoid array index out of bounds issues and to avoid the compression
                         * of the array causing array lengths to not match, if the row only contains the row header, then
                         * insert the row header as the value for the particular array entry.
                         * If the row was supposed to have a value, this will still fail since the value expected
                         * will never be the value of the row header.
                         */
                        if(stringArr.length == 1) {
                            headerList.add( stringArr[0] );
                        } else {
                            headerList.add( stringArr[colNumber] );
                        }
                    }
                }
                while(it.hasNext());
            }
        }
        return headerList;
    }

    public String getIDFColValueByColNumber( final int colNumber, final int rowValue ) {
        List<String> headerList = new ArrayList<String>( 44 );
        List<String> colList = loopOverArray( colNumber, headerList );
        return colList.size() > rowValue && rowValue >= 0 ? colList.get( rowValue ) : null;
    }

    public int getColNumberByName( final String colName ) {
        List<String> headerList = new ArrayList<String>( 44 );
        List<String> colList = loopOverArray( 0, headerList );
        return colList.indexOf( colName );
    }

    public List<String> getAllIDFColValuesByColName( String colName ) {
        List<String> colValuesList = new ArrayList<String>( 44 );
        final int columnNumber = getColNumberByName( colName );
        final String[] colValueArray;
        if(columnNumber > -1) {
            colValueArray = theIDF.get( columnNumber );
        } else {
            return colValuesList;
        }
        if(colValueArray != null && colValueArray.length > 0) {
            for(final String aColValueArray : colValueArray) {
                if(aColValueArray.trim().length() > 0 && !aColValueArray.trim().equals( colName )) {
                    colValuesList.add( aColValueArray.trim() );
                }
            }
        } else {
            throw new IllegalArgumentException( "The column " + colName + " was not found!" );
        }
        return colValuesList;
    }

    public List<List<String>> getAllColsContainingString( final String valueToLookForInColName ) {
        List<List<String>> colValuesList = new ArrayList<List<String>>( 44 );
        Iterator<String[]> it = theIDF.iterator();
        if(it.hasNext()) {
            do {
                String[] stringArr = it.next();
                if(stringArr[0].trim().length() > 0 && stringArr[0].trim().indexOf(
                        valueToLookForInColName ) != -1) {
                    colValuesList.add( Arrays.asList( stringArr ) );
                }
            }
            while(it.hasNext());
        }
        return colValuesList;
    }
}
