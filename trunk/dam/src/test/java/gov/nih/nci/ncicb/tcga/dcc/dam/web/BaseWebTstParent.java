/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.web;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: Oct 6, 2008
 * Time: 11:08:07 AM
 * To change this template use File | Settings | File Templates.
 */
//parent class of the two test classes in this package
public class BaseWebTstParent extends TestCase {

    protected static final String TEST_DATAFILE = 
    	Thread.currentThread().getContextClassLoader().getResource("samples/mock_sampleinfo.txt").getPath();
    protected static final String DISEASE_TYPE = "GBM";
    protected static List<DataSet> dpts;

    public void setUp() throws Exception {
        loadDataPointList();
    }

    //TODO Move to utility or to a new parent class
    public void loadDataPointList() throws IOException {
        if(dpts == null) {
            dpts = new ArrayList<DataSet>();
            BufferedReader reader = null;
            try {
                //todo: use File.Separator? But Java knows how to handle forward slash on any platform. Is needed?
                final String fname = TEST_DATAFILE;
                reader = new BufferedReader( new FileReader( fname ) );
                String record = reader.readLine();
                while(record != null) {
                    final StringTokenizer st = new StringTokenizer( record, "\t" );
                    final String platformType = readNext( st );
                    final String center = readNext( st );
                    final String level = readNext( st );
                    final String batch = readNext( st );
                    final String sample = readNext( st );
                    final String availability = readNext( st );
                    final String sProtected = readNext( st );
                    final String barcode = readNext( st );
                    final String platform = readNext( st );
                    final DataSet dataSet = new DataSet();
                    dataSet.setPlatformTypeId( platformType );
                    dataSet.setCenterId( center );
                    dataSet.setLevel( level );
                    dataSet.setBatch( batch );
                    dataSet.setSample( sample );
                    dataSet.setAvailability( availability );
                    dataSet.setProtected( "Y".equals( sProtected ) );
                    final ArrayList<String> barcodes = new ArrayList<String>();
                    barcodes.add( barcode );
                    dataSet.setBarcodes( barcodes );
                    dataSet.setPlatformId( platform );
                    dpts.add( dataSet );
                    record = reader.readLine();
                }
            }
            finally {
                if(reader != null) {
                    reader.close();
                }
            }
        }
    }

    //TODO Move to utility or to a new parent class
    //compensates for a quirk of the string tokenizer - skips null tokens so we have to insert . in the test data
    private String readNext( final StringTokenizer st ) {
        String ret = st.nextToken();
        if(".".equals( ret )) {
            ret = null; //"";
        }
        return ret;
    }
}
