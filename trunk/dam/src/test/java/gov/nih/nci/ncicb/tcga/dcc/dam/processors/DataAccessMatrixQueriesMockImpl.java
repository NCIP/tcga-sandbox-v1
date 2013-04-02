/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.processors;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileClinical;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelOne;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelTwo;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileMetadata;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DAMUtilsI;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Author: David Nassau
 * <p/>
 * Mock datasource for use during development.
 */
//todo: once we don't need it anymore, move this class into test tree or remove from source
public class DataAccessMatrixQueriesMockImpl implements DataAccessMatrixQueries {

    private String dataFileName, dataFilePath, testDownloadFile;

    private DAMUtilsI damUtils;

    public void setDataFilePath( String value ) {
        dataFilePath = value;
    }

    public void setTestDownloadFile( String value ) {
        testDownloadFile = value;
    }

    public DataAccessMatrixQueriesMockImpl( String dataFileName ) throws IOException {
        if(!( new File( dataFileName ) ).exists()) {
            throw new IOException( "File not found: " + dataFileName );
        }
        this.dataFileName = dataFileName;
    }

    public List<DataSet> getDataSetsForDiseaseType( String diseaseType ) throws DAMQueriesException {
        List<DataSet> ret = new ArrayList<DataSet>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader( new FileReader( dataFileName ) );
            String record = reader.readLine();
            while(record != null) {
                StringTokenizer st = new StringTokenizer( record, "\t" );
                String platformType = readNext( st );
                String center = readNext( st );
                String level = readNext( st );
                String batch = readNext( st );
                String sample = readNext( st );
                String availability = readNext( st );
                String sProtected = readNext( st );
                String barcode = readNext( st );
                String platform = readNext( st );
                //todo: add some error checking for bad file format
                DataSet dataSet = new DataSet();
                dataSet.setPlatformTypeId( platformType );
                dataSet.setCenterId( center );
                dataSet.setLevel( level );
                dataSet.setBatch( batch );
                dataSet.setSample( sample );
                dataSet.setAvailability( availability );
                if("Y".equals( sProtected )) {
                    dataSet.setProtected( true );
                }
                // dataSet.setBarcode(barcode);
                dataSet.setPlatformId( platform );
                ret.add( dataSet );
                record = reader.readLine();
            }
        }
        catch(IOException e) {
            //ret = null;
            throw new DAMQueriesException( e );
        }
        finally {
            try {
                if(reader != null) {
                    reader.close();
                }
            }
            catch(IOException ex) {
            }
        }
        return ret;
    }

    @Override
    public List<DataSet> getDataSetsForControls(final List<String> diseaseTypes) throws DAMQueriesException {
        return null;
    }

    //compensates for a quirk of the string tokenizer - skips null tokens so we have to insert .
    private String readNext( StringTokenizer st ) {
        String ret = st.nextToken();
        if(".".equals( ret )) {
            ret = null; //"";
        }
        return ret;
    }

    /**
     * Create one {@link DataFile} for each given {@link DataSet} with some of its attributes set randomly.
     *
     * Note: <code>consolidateFiles</code> argument is ignored in this implementation.
     *
     * @param selectedDataSets the data sets the user selected
     * @param consolidateFiles ignored in this implementation
     * @return a {@link List} of random {@link DataFile}
     * @throws DAMQueriesException
     */
    public List<DataFile> getFileInfoForSelectedDataSets(final List<DataSet> selectedDataSets,
                                                         final boolean consolidateFiles)
            throws DAMQueriesException {

        final List<DataFile> result = new ArrayList<DataFile>();
        final Map<String, List<DataSet>> diseaseToDataSetMap = getDamUtils().groupDataSetsByDisease(selectedDataSets);

        if(diseaseToDataSetMap != null) {
            int nextAvailableFileId = 1;
            for(final String diseaseType : diseaseToDataSetMap.keySet()) {

                final List<DataSet> singleDiseaseDataSets = diseaseToDataSetMap.get(diseaseType);

                if(singleDiseaseDataSets != null && singleDiseaseDataSets.size() > 0) {

                    setDiseaseInContext(diseaseType);
                    result.addAll(getFileInfoForSelectedDataSetsUniqueDisease(singleDiseaseDataSets, nextAvailableFileId));
                    nextAvailableFileId += singleDiseaseDataSets.size();// There is 1 DataFile per DataSet
                }
            }
        }

        return result;
    }

    /**
     * Set the {@link DiseaseContextHolder} disease type
     *
     * @param diseaseType the disease type
     */
    protected void setDiseaseInContext(final String diseaseType) {
        DiseaseContextHolder.setDisease(diseaseType);
    }

    /**
     * Create one {@link DataFile} for each given {@link DataSet} with some of its attributes set randomly.
     * 
     * @param selectedDataSets the data sets the user selected, for a single disease
     * @param nextAvailableFileId
     * @return a {@link List} of random {@link DataFile}
     */
    private List<DataFile> getFileInfoForSelectedDataSetsUniqueDisease(final List<DataSet> selectedDataSets,
                                                                       final int nextAvailableFileId) {
        final List<DataFile> result = new ArrayList<DataFile>();
        final Random random = new Random();
        final String level1 = "1";
        final String level2 = "2";
        final String level3 = "3";
        final String dataFileExtension = ".idat";

        int fileId = nextAvailableFileId;
        for (final DataSet dataset : selectedDataSets) {

            final StringBuffer randomName = makeRandomName(random);
            randomName.append(dataFileExtension);

            final String level = dataset.getLevel();
            DataFile dataFile = null;

            if (level1.equals(level)) {
                dataFile = new DataFileLevelOne();
            } else if (level2.equals(level)) {
                dataFile = new DataFileLevelTwo();
            } else if (level3.equals(level)) {
                dataFile = new DataFileLevelThree();
            } else if (DataAccessMatrixQueries.LEVEL_CLINICAL.equals(level)) {
                dataFile = new DataFileClinical();
            } else if (DataAccessMatrixQueries.LEVEL_METADATA.equals(level)) {
                dataFile = new DataFileMetadata();
            }

            if(dataFile != null) {

                final int randomSize = makeRandomSize(random);
                final String fileIdAsString = "" + fileId++;

                dataFile.setFileName(randomName.toString());
                dataFile.setSize(randomSize);
                dataFile.setFileId(fileIdAsString);
                dataFile.setPlatformTypeId(dataset.getPlatformTypeId());
                dataFile.setCenterId(dataset.getCenterId());
                dataFile.setProtected(dataset.isProtected());
                dataFile.setDisplaySample(dataset.getSample());
            }

            result.add(dataFile);
        }

        return result;
    }

    /**
     * Return a random size
     *
     * @param random a {@link Random} object used to create the random size
     * @return a random size
     */
    private int makeRandomSize(final Random random) {
        return random.nextInt(900000) + 100000;
    }

    /**
     * Return a random name
     *
     * @param random a {@link Random} object used to create the random name
     * @return a random name
     */
    private StringBuffer makeRandomName(final Random random) {

        final StringBuffer result = new StringBuffer();
        final int length = 8 + random.nextInt(6);

        for (int i=0; i<length; i++) {

            final char nameChar = (char) (97 + random.nextInt(26));
            result.append(nameChar);
        }
        
        return result;
    }

    public void addPathsToSelectedFiles( List<DataFile> selectedFiles ) throws DAMQueriesException {
        //boolean ret = true;
        Reader in = null;
        Writer out = null;
        try {
            for(DataFile fileInfo : selectedFiles) {
                //noinspection IOResourceOpenedButNotSafelyClosed
                in = new BufferedReader( new FileReader( testDownloadFile ) );
                String fname = dataFilePath + fileInfo.getFileId() + ".idat";
                File f = new File( fname );
                if(f.exists()) {
                    f.delete();
                }
                //noinspection IOResourceOpenedButNotSafelyClosed
                out = new BufferedWriter( new FileWriter( fname ) );
                int c;
                while(( c = in.read() ) != -1) {
                    out.write( c );
                }
                out.flush();
                out.close();
                in.close();
                System.out.println( "wrote file " + fname );
                fileInfo.setPath( fname );
                fname = fname.replace( '\\', '/' );
                fname = fname.substring( fname.lastIndexOf( '/' ) + 1 );
                fileInfo.setFileName( fname );
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
            //all or nothing
            for(DataFile fileInfo : selectedFiles) {
                fileInfo.setPath( null );
            }
            //ret = false;
            throw new DAMQueriesException( ex );
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
        //return ret;
    }

    public Map<String, Object> getSubmittedSampleIds( String diseaseType ) throws DAMQueriesException {
        //return empty is OK for mock
        return new HashMap<String, Object>();
    }

    public DAMUtilsI getDamUtils() {
        return damUtils;
    }

    public void setDamUtils(DAMUtilsI damUtils) {
        this.damUtils = damUtils;
    }
}
