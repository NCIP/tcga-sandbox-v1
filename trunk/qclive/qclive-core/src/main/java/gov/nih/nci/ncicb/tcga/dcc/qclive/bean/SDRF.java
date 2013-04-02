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
import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Robert S. Sfeir
 */
public class SDRF {

    private List SDRFList = null;

    public SDRF( String dirLocation, String headerListFileName, final ProcessLogger processLogger ) {

        CSVReader reader = null;
        FileReader fileReader = null;

        try {
            //noinspection IOResourceOpenedButNotSafelyClosed
            fileReader = new FileReader(new File(dirLocation, headerListFileName));
            reader = new CSVReader(fileReader, '\t' );
            SDRFList = reader.readAll();
        }
        catch(IOException e) {
            processLogger.addError();
            processLogger.logToLogger( Level.FATAL, "Could not find the SDRF File.  Aborting Processing." );
            processLogger.getLogBuffer().append( "Could not find the SDRF File.  Aborting Processing." );
        } finally {
            IOUtils.closeQuietly(fileReader);
            IOUtils.closeQuietly(reader);
        }
    }

    public List getSDRFList() {
        return SDRFList;
    }

    public List getSDRFColNames() {
        return Arrays.asList( (String[]) SDRFList.get( 0 ) );
    }

    public List getSDRFRowNumber( final int rowNumber ) {
        return Arrays.asList( (String[]) SDRFList.get( rowNumber ) );
    }

    public String getSDRFColNumberInRow( final int rowNumber, final int colNumber ) {
        List theRowList = Arrays.asList( (String[]) SDRFList.get( rowNumber ) );
        return theRowList.get( colNumber ).toString();
    }
}