/*
 *
 *  * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 *  * Copyright Notice.  The software subject to this notice and license includes both human
 *  * readable source code form and machine readable, binary, object code form (the "caBIG
 *  * Software").
 *  *
 *  * Please refer to the complete License text for full details at the root of the project.
 *
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.common.util.BigDecimalConversions;
import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.DataSetReducer;
import org.apache.log4j.Level;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract class made from DAMQueriesCGCCLevel1 code and made abstract to accommodate other DAOs that serve
 * files from the filesystem and not from the database.
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
public abstract class DAMQueriesFilesystem extends DAMBaseQueriesProcessor implements DataAccessMatrixQueries {
    private DAMUtilsI damUtils;
    private final ProcessLogger logger = new ProcessLogger();

    /**
     * Gets the SQL for getting file info. Takes 3 parameters: barcode, platform ID, center ID.
     * Should select the following columns: file_id, file_name, file_size
     *
     * @return file info sql
     */
    protected abstract String getFileInfoQuery();

    /**
     * Gets the SQL for getting data sets. Takes 2 parameters: disease abbreviation and is control (0 or 1). Should
     * select the following columns:
     * barcode, sample, archive_id, center_id, platform_type_id, sort_order, platform_id, availability, identifiable,
     * serial_index, revision, date_added, platform_alias, data_level
     *
     * @return dataset sql
     */
    protected abstract String getDatasetSql();


     @Override
    public List<DataSet> getDataSetsForDiseaseType(
            final String diseaseType ) throws DataAccessMatrixQueries.DAMQueriesException {
        return getDataSets(Arrays.asList(diseaseType), DAMBaseQueriesProcessor.Control.FALSE);
    }

    @Override
    public List<DataSet> getDataSetsForControls(final List<String> diseaseTypes) throws DataAccessMatrixQueries.DAMQueriesException {
        return getDataSets(diseaseTypes, DAMBaseQueriesProcessor.Control.TRUE);
    }


    private List<DataSet> getDataSets(final List<String> diseaseTypes, final DAMBaseQueriesProcessor.Control isControl) throws DataAccessMatrixQueries.DAMQueriesException {
        List<DataSet> retDataSets = new ArrayList<DataSet>();
        Map<String, Integer> barcodeToBatch = getBarcodeBatches();
        SimpleJdbcTemplate jdbc = new SimpleJdbcTemplate(getDataSource());
        final Map<String, DataSet> createdDataSets = new HashMap<String, DataSet>();
        for(final String diseaseType : diseaseTypes) {
            DiseaseContextHolder.setDisease(diseaseType);
            List<Map<String, Object>> records = jdbc.queryForList(getDatasetSql(), diseaseType, isControl.value());
            getDataSets(records, createdDataSets, barcodeToBatch, diseaseType);

            retDataSets.addAll(createdDataSets.values());
            createdDataSets.clear();
        }
        retDataSets = new DataSetReducer().reduceLevelOne(retDataSets);
        return retDataSets;
    }

    private void getDataSets( final List<Map<String,Object>> records, final Map<String, DataSet> dataSets,
                              final Map<String, Integer> barcodeToBatch, final String diseaseType ) {
        for(final Map<String, Object> record : records) {
            final String platformType = ( record.get( PLATFORM_TYPE_ID ) ).toString();
            final String center = ( record.get( CENTER_ID ) ).toString();
            final String platform = ( record.get( PLATFORM_ID ) ).toString();
            final int sortOrder = ((Number)record.get( "sort_order" )).intValue();
            final String level = record.get("data_level").toString();
            final String sample = (String) record.get( SAMPLE );
            final String si = ( record.get( SERIAL_INDEX ) ).toString();
            final String rev = ( record.get( REVISION ) ).toString();
            String barcode = (String) record.get( BARCODE );
            final String key = new StringBuilder().append( platformType ).append( PIPE_SYMBOL ).append( center ).
                    append( PIPE_SYMBOL ).append( platform ).append( PIPE_SYMBOL ).append( level ).append( PIPE_SYMBOL ).
                    append( sample ).append( PIPE_SYMBOL ).append( si ).append( PIPE_SYMBOL ).append( rev ).toString();
            DataSet ds = dataSets.get( key );
            if(ds != null) {
                // new barcode for existing dataset
                if(!ds.getBarcodes().contains( barcode )) {
                    ds.getBarcodes().add( barcode );
                }
            } else {
                ds = getNewDataSetObject();
                ds.setDiseaseType( diseaseType );
                ds.setArchiveId(BigDecimalConversions.bigDecimalToInteger((BigDecimal) record.get("archive_id")));
                ds.setPlatformTypeId( platformType );
                ds.setPlatformId( platform );
                Object alias = record.get( "platform_alias" );
                if(alias != null) {
                    ds.setPlatformAlias( alias.toString() );
                }
                ds.setCenterId( center );
                ds.setLevel( level );
                ds.setSample( sample );
                // look up batch in map table
                String bcrBatch = ConstantValues.UNCLASSIFIED_BATCH;
                if(barcodeToBatch.get( sample ) != null) {
                    bcrBatch = "Batch " + String.valueOf( barcodeToBatch.get( sample ) );
                }
                ds.setBatch( bcrBatch );
                // set to protected unless "identifiable" is 0
                ds.setProtected( !record.get( "identifiable" ).toString().equals( "0" ) );
                ds.setBarcodes( new ArrayList<String>() );
                ds.getBarcodes().add( barcode );
                String availability;
                final String rsAvail = (String) record.get( AVAILABILITY );
                if(rsAvail.equals( ConstantValues.ARCHIVE_AVAILABLE )) {
                    availability = DataAccessMatrixQueries.AVAILABILITY_AVAILABLE;
                } else if(rsAvail.equals( ConstantValues.ARCHIVE_IN_REVIEW )) {
                    availability = DataAccessMatrixQueries.AVAILABILITY_PENDING;
                } else {
                    availability = DataAccessMatrixQueries.AVAILABILITY_NOTAVAILABLE;
                }
                ds.setAvailability( availability );
                Timestamp timestamp = (Timestamp) record.get( "date_added" );
                if(timestamp != null) {
                    ds.setDateAdded( new Date( timestamp.getTime() ) );
                }
                ds.setPlatformTypeSortOrder( sortOrder );
                dataSets.put( key, ds );
            }
        }
    }

    /**
     * Gets a new data set object of the appropriate subclass of DataSet for this DAO.
     * @return new DataSet subclass instance
     */
    protected abstract DataSet getNewDataSetObject();

    /**
        * @param selectedDataSets the data sets the user selected
        * @param consolidateFiles whether data for selected data sets should be consolidated into as few files as possible or put in one file per sample -- IGNORED IN THIS IMPLEMENTATION
        * @return data files needed to represent all data in selected data sets
        * @throws DataAccessMatrixQueries.DAMQueriesException
        */
       @Override
       public List<DataFile> getFileInfoForSelectedDataSets(
               final List<DataSet> selectedDataSets, final boolean consolidateFiles) throws DataAccessMatrixQueries.DAMQueriesException {
           final List<DataFile> ret = new ArrayList<DataFile>();
           final Map<String, List<DataSet>> dataSetsGroupedByDisease = damUtils.groupDataSetsByDisease(selectedDataSets);
           for(final String diseaseType : dataSetsGroupedByDisease.keySet()) {
               final List<DataSet> singleDiseaseDataSets = dataSetsGroupedByDisease.get(diseaseType);
               if(singleDiseaseDataSets != null && singleDiseaseDataSets.size() > 0) {
                   DiseaseContextHolder.setDisease(diseaseType);
                   ret.addAll(getFileInfoForSelectedDataSetsSingleDisease(singleDiseaseDataSets));
               }
           }
           return ret;
       }

       /**
        *
        * @param selectedDataSets the data sets the user selected
        * @return data files needed to represent all data in selected data sets
        * @throws DataAccessMatrixQueries.DAMQueriesException if there's an error
        */
       private List<DataFile> getFileInfoForSelectedDataSetsSingleDisease(
               final List<DataSet> selectedDataSets) throws DataAccessMatrixQueries.DAMQueriesException {
           final List<DataFile> ret = new ArrayList<DataFile>();

           final HashMap<Integer, Object> preventDupes = new HashMap<Integer, Object>(); //prevent from listing the same file twice, if it's referenced by more than one barcode
           try {
               final ParameterizedRowMapper<DataFile> mapper = new ParameterizedRowMapper<DataFile>() {
                   public DataFile mapRow( final ResultSet rs, final int rowNum ) throws SQLException {
                       final DataFile dataFile = getNewFileInfoInstance();
                       //This block references FILE_INFO_QUERY
                       //String 1 is file_id
                       dataFile.setFileId( rs.getString( 1 ) );
                       //String 2 is file_name
                       dataFile.setFileName(rs.getString(2));
                       //String 3 is file_size
                       dataFile.setSize(rs.getLong(3));
                       return dataFile;
                   }
               };
               final SimpleJdbcTemplate jdbc = new SimpleJdbcTemplate( getDataSource() );
               //query once for each barcode
               for(final DataSet dataset : selectedDataSets) {
                   if(dataSetShouldBeIncluded(dataset)) {
                       for(final String barcode : dataset.getBarcodes()) {
                           logger.logToLogger( Level.DEBUG, "executing query for getFileInfoForSelectedDataSets(), barcode=" + barcode + ": " + System.currentTimeMillis() );
                           List<DataFile> queryResult = jdbc.query(getFileInfoQuery(), mapper, barcode, dataset.getPlatformId(), dataset.getCenterId() );
                           logger.logToLogger( Level.DEBUG, "finished executing query for getFileInfoForSelectedDataSets(), barcode=" + barcode + ": " + System.currentTimeMillis() );
                           for(final DataFile fileInfo : queryResult) {
                               final int iFile = Integer.parseInt( fileInfo.getFileId() );
                               DataFile dupeDF = (DataFile) preventDupes.get( iFile );
                               if(dupeDF == null) {
                                   //some values we just copy from the dataset object - don't need to get from the query
                                   fileInfo.setPlatformTypeId( dataset.getPlatformTypeId() );
                                   fileInfo.setCenterId( dataset.getCenterId() );
                                   fileInfo.setPlatformId( dataset.getPlatformId() );
                                   fileInfo.setLevel( dataset.getLevel() );
                                   fileInfo.setDisplaySample( dataset.getSample() );
                                   fileInfo.setProtected( dataset.isProtected() );
                                   final List<String> fileBarcodes = new ArrayList<String>();
                                   fileBarcodes.addAll(dataset.getBarcodes());
                                   fileInfo.setBarcodes(fileBarcodes);
                                   fileInfo.setDiseaseType(dataset.getDiseaseType());
                                   ret.add( fileInfo );
                                   preventDupes.put( iFile, fileInfo );
                               } else {
                                   //append the sample id for the existing datafile, add barcode if it's different
                                   if(dupeDF.getPlatformId().equals( dataset.getPlatformId() )
                                           && dupeDF.getCenterId().equals( dataset.getCenterId() )
                                           && dupeDF.getLevel().equals( dataset.getLevel() )) {
                                       dupeDF.setDisplaySample( dupeDF.getDisplaySample() + "/" + dataset.getSample() );
                                       for (final String newBarcode : dataset.getBarcodes()) {
                                           if (!dupeDF.getBarcodes().contains(newBarcode)) {
                                               dupeDF.getBarcodes().add(newBarcode);
                                           }
                                       }
                                   }
                               }
                           }
                       }
                   }
               }
           }
           catch(DataAccessException e) {
               logger.logToLogger( Level.ERROR, ProcessLogger.stackTracePrinter( e ) );
               throw new DataAccessMatrixQueries.DAMQueriesException( e );
           }

           return ret;
       }

    /**
     * Gets a new instance of data file which should be a subclass of DataFile of the correct type for this DAO.
     * @return new DataFile subclass instance
     */
    protected abstract DataFile getNewFileInfoInstance();

    /**
     * Checks if this dataset is the correct type to be included by this DAO.
     *
     * @param dataset the data set in question
     * @return if the dataset should be included by this DAO
     */
    protected abstract boolean dataSetShouldBeIncluded(final DataSet dataset);

    public void addPathsToSelectedFiles(
               final List<DataFile> selectedFiles ) throws DataAccessMatrixQueries.DAMQueriesException {
           try {
               final SimpleJdbcTemplate jdbc = new SimpleJdbcTemplate( getDataSource() );
               final String query = "select file_location_url from File_to_Archive f2a, archive_info ai" +
                       " where f2a.archive_id = ai.archive_id and f2a.file_id = ? and ai.is_latest=1";
               for(final DataFile df : selectedFiles) {
                   if(dataFileShouldBeIncluded(df)) {
                       DiseaseContextHolder.setDisease(df.getDiseaseType());
                       df.setPath( jdbc.queryForObject( query, String.class, Integer.valueOf(df.getFileId()) ) );
                   }
               }
           }
           catch(DataAccessException e) {
               logger.logToLogger( Level.ERROR, ProcessLogger.stackTracePrinter( e ) );
               throw new DataAccessMatrixQueries.DAMQueriesException( e );
           }
       }

    /**
     * Checks if the data file should be included in the results for this DAO.
     *
     * @param dataFile the data file in question
     * @return if the the data file should be included
     */
    protected abstract boolean dataFileShouldBeIncluded(final DataFile dataFile);

    public DAMUtilsI getDamUtils() {
        return damUtils;
    }

    public void setDamUtils(final DAMUtilsI damUtils) {
        this.damUtils = damUtils;
    }

}
