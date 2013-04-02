/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.SQLProcessingCleaner;
import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileTrace;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetTrace;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.DataSetReducer;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.ThreadedBufferedWriter;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.TumorNormalClassifierI;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.ErrorInfo;
import org.apache.log4j.Level;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class DAMQueriesTrace extends DAMBaseQueriesProcessor implements DataAccessMatrixQueries {
    private static final String EXPECTED_DATA_TYPE_NAME =   "Trace-Sample Relationship";
    private static final String DATA_SET_SQL =
            "select distinct sb.project_code  || '-' || sb.tss_code  || '-' || sb.participant_code || '-' || sbe.element_value as sample, " +
                    "sb.built_barcode, " +
                    "ai.center_id, " +
                    "dt.data_type_id as platform_type_id, " +
                    "dt.sort_order, " +
                    "ai.platform_id, " +
                    "ai.deploy_status as availability, " +
                    "vi.identifiable, " +
                    "ai.serial_index, " +
                    "ai.revision, " +
                    "ai.date_added, " +
                    "ai.archive_id, " +
                    "pl.platform_alias " +
            "from " +
                    "shipped_biospecimen_file sbtf, " +
                    "shipped_biospecimen sb," +
                    "shipped_biospecimen_element sbe," +
                    "shipped_element_type sbet,"+
                    "file_info fi, " +
                    "file_to_archive  fta, " +
                    "archive_info ai, " +
                    "data_type dt, " +
                    "disease  di, " +
                    "data_visibility  dv, " +
                    "visibility  vi, " +
                    "platform pl " +
            "where  sb.shipped_biospecimen_id = sbtf.shipped_biospecimen_id"+
                    " and   sb.shipped_biospecimen_id  = sbe.shipped_biospecimen_id"+
                    " and   sbe.element_type_id = sbet.element_type_id " +
                    " and   sbet.element_type_name = 'sample_type_code'"+
                    " and   sbtf.file_id =  fi.file_id "+
                    " and   fi.level_number = 1"+
                    " and   fi.file_id  = fta.file_id "+
                    " and   fta.archive_id = ai.archive_id "+
                    " and   ai.is_latest = 1 "+
                    " and   ai.disease_id = di.disease_id "+
                    " and   di.disease_abbreviation=? "+
                    " and   fi.data_type_id  =  dt.data_type_id"+
                    " and   dt.name = '"+ EXPECTED_DATA_TYPE_NAME +"'"+
                    " and   dt.data_type_id = dv.data_type_id"+
                    " and   dv.level_number = 1"+
                    " and   dv.visibility_id = vi.visibility_id"+
                    " and   ai.platform_id = pl.platform_id"+
                    " and   sb.is_viewable= 1" +
                    " and sb.is_control=?";
           
    private static final String SQL_TRACE_SQL = "select bnt.ncbi_trace_id, " +
            "ti.gene_name, " +
            "ti.reference_accession, " +
            "ti.reference_acc_min, " +
            "ti.reference_acc_max " +
            "from  " +
            "shipped_biospecimen sb, " +
            "shipped_biospec_ncbi_trace bnt, " +
            "trace_info ti " +
            "where " +
            "bnt.ncbi_trace_id = ti.ncbi_trace_id " +
            "and bnt.shipped_biospecimen_id = sb.shipped_biospecimen_id " +
            "and sb.built_barcode = ? " +
            "order by bnt.ncbi_trace_id";

    private String tempfileDirectory;
    private Map<String, String> chroms;
    private DAMUtilsI damUtils;

    public DAMQueriesTrace() {
        buildChromosomeMap();
    }

    private void buildChromosomeMap() {
        chroms = new HashMap<String, String>();
        chroms.put("NC_000001.9", "1");
        chroms.put("NC_000002.10", "2");
        chroms.put("NC_000003.10", "3");
        chroms.put("NC_000004.10", "4");
        chroms.put("NC_000005.8", "5");
        chroms.put("NC_000006.10", "6");
        chroms.put("NC_000007.12", "7");
        chroms.put("NC_000008.9", "8");
        chroms.put("NC_000009.10", "9");
        chroms.put("NC_000010.9", "10");
        chroms.put("NC_000011.8", "11");
        chroms.put("NC_000012.10", "12");
        chroms.put("NC_000013.9", "13");
        chroms.put("NC_000014.7", "14");
        chroms.put("NC_000015.8", "15");
        chroms.put("NC_000016.8", "16");
        chroms.put("NC_000017.9", "17");
        chroms.put("NC_000018.8", "18");
        chroms.put("NC_000019.8", "19");
        chroms.put("NC_000020.9", "20");
        chroms.put("NC_000021.7", "21");
        chroms.put("NC_000022.9", "22");
        chroms.put("NC_000023.9", "X");
        chroms.put("NC_000024.8", "Y");
        chroms.put("NC_001807.4", "M"); //mitochrondrial DNA
    }

    public void setTempfileDirectory(final String tempfileDirectory) {
        this.tempfileDirectory = tempfileDirectory;
    }

    //todo: duplication with level 1 dao, fix
    public List<DataSet> getDataSetsForDiseaseType(final String diseaseType)
            throws DataAccessMatrixQueries.DAMQueriesException {
        return getDataSets(diseaseType, false);
    }

    @Override
    public List<DataSet> getDataSetsForControls(final List<String> diseaseTypes)
            throws DAMQueriesException {

        final List<DataSet> result = new ArrayList<DataSet>();

        for(final String diseaseType : diseaseTypes) {
            DiseaseContextHolder.setDisease(diseaseType);
            result.addAll(getDataSets(diseaseType, true));
        }

        return result;
    }

    /**
     * Return a {@link List} of {@link DataSet} for the given disease type.
     *
     * @param diseaseType the disease type
     * @param forControl <code>true</code> to only include {@link DataSet} for control samples, <code>false</code> otherwise
     * @return a {@link List} of {@link DataSet} for the given disease type
     * @throws DAMQueriesException
     */
    protected List<DataSet> getDataSets(final String diseaseType,
                                      final boolean forControl) throws DAMQueriesException {

        final Map<String, Integer> barcodeToBatch = getBarcodeBatches();
        final List<DataSet> result = new ArrayList<DataSet>();
        final Map<String, DataSet> createdDataSets = new HashMap<String, DataSet>();
        final SimpleJdbcTemplate jdbc = new SimpleJdbcTemplate(getDataSource());
        final List<Map<String, Object>> records = jdbc.queryForList(DATA_SET_SQL, diseaseType, forControl?1:0);
        getDataSets(records, createdDataSets, barcodeToBatch, diseaseType);

        result.addAll(createdDataSets.values());
        new DataSetReducer().reduceLevelOne(result);
        return result;
    }

    private void getDataSets(final List<Map<String, Object>> records, final Map<String, DataSet> dataSets,
                              final Map<String, Integer> barcodeToBatch, final String diseaseType ) throws DataAccessMatrixQueries.DAMQueriesException {
        
        for (final Map<String, Object> record : records) {
            final String platformType = (record.get(PLATFORM_TYPE_ID)).toString();
            final String center = (record.get(CENTER_ID)).toString();
            final String platform = (record.get(PLATFORM_ID)).toString();
            final int sortOrder = ((Number) record.get("sort_order")).intValue();
            final String level = "1";
            final String sample = (String) record.get(SAMPLE);
            final String si = (record.get(SERIAL_INDEX)).toString();
            final String rev = (record.get(REVISION)).toString();
            final String key = new StringBuilder().append(platformType).append(PIPE_SYMBOL).append(center).
                    append(PIPE_SYMBOL).append(platform).append(PIPE_SYMBOL).append(level).append(PIPE_SYMBOL).
                    append(sample).append(PIPE_SYMBOL).append(si).append(PIPE_SYMBOL).append(rev).toString();
            DataSet ds = dataSets.get(key);
            if (ds != null) {
                //new barcode for existing dataset
                String barcode = (String) record.get(BUILT_BARCODE);
                if (!ds.getBarcodes().contains(barcode)) {
                    ds.getBarcodes().add(barcode);
                }
            } else {
                ds = new DataSetTrace();
                ds.setDiseaseType( diseaseType );
                ds.setArchiveId(((Number) record.get("archive_id")).intValue());
                ds.setPlatformTypeId(platformType);
                ds.setPlatformAlias((String) record.get("platform_alias"));
                ds.setCenterId(center);
                ds.setLevel(level);
                ds.setSample(sample);
                String availability;
                final String rsAvail = (String) record.get(AVAILABILITY);
                if (rsAvail.equals(ConstantValues.ARCHIVE_AVAILABLE)) {
                    availability = DataAccessMatrixQueries.AVAILABILITY_AVAILABLE;
                } else if (rsAvail.equals(ConstantValues.ARCHIVE_IN_REVIEW)) {
                    availability = DataAccessMatrixQueries.AVAILABILITY_PENDING;
                } else {
                    throw new DataAccessMatrixQueries.DAMQueriesException("Availability value " + rsAvail + " not recognized");
                }
                ds.setAvailability(availability);
                String bcrBatch = ConstantValues.UNCLASSIFIED_BATCH;
                if (barcodeToBatch.get(sample) != null) {
                    bcrBatch = "Batch " + String.valueOf(barcodeToBatch.get(sample));
                }
                ds.setBatch(bcrBatch);
                ds.setPlatformId(platform);
                // set to protected unless "identifiable" is 0
                ds.setProtected(!record.get("identifiable").toString().equals("0"));
                ds.setBarcodes(new ArrayList<String>());
                ds.getBarcodes().add((String) record.get(BUILT_BARCODE));
                Timestamp timestamp = (Timestamp) record.get("date_added");
                if (timestamp != null) {
                    ds.setDateAdded(new Date(timestamp.getTime()));
                }
                ds.setPlatformTypeSortOrder(sortOrder);
                dataSets.put(key, ds);
            }
        }
    }

    /**
     * Build the needed data file objects for the selected data sets.
     * This method groups the datasets by disease and then calls
     * getFileInfoForSelectedDataSetsSingleDisease to retrieve the
     * data files associated with a single disease
     *
     * @param selectedDataSets the data sets the user selected
     * @param consolidateFiles whether data for selected data sets should be consolidated -- IGNORED IN THIS IMPLEMENTATION
     * @return data files needed to represent the given data sets
     * @throws DataAccessMatrixQueries.DAMQueriesException
     *
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
                ret.addAll(getFileInfoForSelectedDataSetsSingleDisease(singleDiseaseDataSets, consolidateFiles));
            }
        }
        return ret;
    }

    /**
     * Build the data file objects needed for the selected data sets.
     * 
     * @param selectedDataSets the data sets the user selected
     * @param consolidateFiles whether data for selected data sets should be consolidated -- IGNORED IN THIS IMPLEMENTATION
     * @return data files needed to represent all data in selected data sets
     * @throws DataAccessMatrixQueries.DAMQueriesException
     */
    public List<DataFile> getFileInfoForSelectedDataSetsSingleDisease(
            final List<DataSet> selectedDataSets, final boolean consolidateFiles) throws DataAccessMatrixQueries.DAMQueriesException {
        //we need to produce one DataFile per center the user has selected
        Map<String, DataFile> dfmap = new HashMap<String, DataFile>();
        for (final DataSet ds : selectedDataSets) {
            if (ds instanceof DataSetTrace) {
                DataFile df = dfmap.get(ds.getCenterId());
                if (df == null) {
                    df = createDataFile(ds);
                    dfmap.put(ds.getCenterId(), df);
                }
                df.getBarcodes().addAll(ds.getBarcodes());
            }
        }
        List<DataFile> ret = new ArrayList<DataFile>();
        for (final String key : dfmap.keySet()) {
            ret.add(dfmap.get(key));
        }
        estimateFileSize(ret);
        return ret;
    }
    private static final long BYTES_PER_ROW = 68;
    private static final long ROWS_PER_BARCODE = 12200; //averaged from db

    private void estimateFileSize(final List<DataFile> files) {
        for (final DataFile df : files) {
            long bcCount = (long) df.getBarcodes().size();
            long totBytes = bcCount * ROWS_PER_BARCODE * BYTES_PER_ROW;
            df.setSize(totBytes);
        }
    }

    private DataFile createDataFile(final DataSet ds) {
        DataFile df = new DataFileTrace();
        df.setFileName(calcFileName(ds));
        df.setDiseaseType(ds.getDiseaseType());
        df.setPlatformTypeId(ds.getPlatformTypeId());
        df.setPlatformId(ds.getPlatformId());
        df.setCenterId(ds.getCenterId());
        df.setProtected(ds.isProtected());
        df.setDisplaySample("selected_samples");
        df.setBarcodes(new TreeSet<String>());
        return df;
    }

    private String calcFileName(final DataSet ds) {
        SimpleJdbcTemplate jdbc = new SimpleJdbcTemplate(getDataSource());
        String center = jdbc.queryForObject("select domain_name from center where center_id = ?", String.class, ds.getCenterId());
        return center + "__trace";
    }

    public void addPathsToSelectedFiles(
            final List<DataFile> selectedFiles) throws DataAccessMatrixQueries.DAMQueriesException {
        if (tempfileDirectory == null) {
            //should have been injected
            throw new DataAccessMatrixQueries.DAMQueriesException("No tempfileDirectory specified");
        }
        if (!(new File(tempfileDirectory)).exists()) {
            throw new DataAccessMatrixQueries.DAMQueriesException("Directory does not exist " + tempfileDirectory);
        }
        final ProcessLogger logger = new ProcessLogger();
        final Connection connection = getConnection();
        try {
            for (final DataFile df : selectedFiles) {
                if (df instanceof DataFileTrace) {
                    DiseaseContextHolder.setDisease(df.getDiseaseType());
                    writeDataFile(logger, (DataFileTrace) df, connection);
                }
            }
        }
        finally {
            SQLProcessingCleaner.cleanUpConnection(connection);
        }
    }

    static final ThreadLocal<String> SQL_TRACE = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return SQL_TRACE_SQL;
        }
    };

    private void writeDataFile(final ProcessLogger logger, final DataFileTrace df,
                               final Connection connection) throws DataAccessMatrixQueries.DAMQueriesException {
        PreparedStatement stmt = null;
        final String TAB = "\t";
        Writer writer = null;
        final String uniqueName = java.util.UUID.randomUUID().toString();
        final String path = tempfileDirectory + "/" + uniqueName;
        df.setPath(path);  //so file packager knows where to copy file from
        try {
            //noinspection JDBCResourceOpenedButNotSafelyClosed
            stmt = connection.prepareStatement(SQL_TRACE.get());
            // note: barcodes is now TreeSet so will be in natural order already
            Collection<String> barcodes = df.getBarcodes();
            if (useThreadedBufferedWriter) {
                writer = new ThreadedBufferedWriter(new FileWriter(path), bufferedWriterBufferSize);
            } else {
                writer = new BufferedWriter(new FileWriter(path), bufferedWriterBufferSize);
            }
            //write header
            writer.write("Barcode");
            writer.write(TAB);
            writer.write("Trace_ID");
            writer.write(TAB);
            writer.write("Gene_Name");
            writer.write(TAB);
            writer.write("Chromosome");
            writer.write(TAB);
            writer.write("Start_Pos");
            writer.write(TAB);
            writer.write("End_Pos");
            writer.write("\n");
            for (final String bc : barcodes) {
                stmt.setString(1, bc);
                ResultSet rs = null;
                try {
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        //BLock references the SQL_TRACE query
                        writer.write(bc);
                        writer.write(TAB);
                        //ncbi_trace_id
                        writer.write(rs.getString(1));
                        writer.write(TAB);
                        //gene_name
                        writer.write(rs.getString(2));
                        writer.write(TAB);
                        //reference_accession
                        writer.write(lookupChromosome(rs.getString(3)));
                        writer.write(TAB);
                        //reference_acc_min
                        writer.write(rs.getString(4));
                        writer.write(TAB);
                        //reference_acc_max
                        writer.write(rs.getString(5));
                        writer.write("\n");
                    }
                }
                finally {
                    SQLProcessingCleaner.cleanUpResultSet(rs);
                }
            }
            writer.flush();
            writer.close();
            writer = null;
        }
        catch (IOException e) {
            new ErrorInfo(e); //logs itself
            throw new DataAccessMatrixQueries.DAMQueriesException(e);
        }
        catch (SQLException e) {
            new ErrorInfo(e); //logs itself
            throw new DataAccessMatrixQueries.DAMQueriesException(e);
        }
        finally {
            if (writer != null) {
                //will only happen if some exception occurred. In that case, we don't care about
                //flushing the buffer-  file will not be used anyway. Just make sure it's closed
                try {
                    writer.close();
                }
                catch (IOException e) {
                    logger.logToLogger(Level.WARN, "Could not close writer.");
                }
            }
            SQLProcessingCleaner.cleanUpStatement(stmt);
        }
    }

    private String lookupChromosome(final String accession) {
        String ret = chroms.get(accession);
        return ret == null ? "?" : ret;
    }

    public DAMUtilsI getDamUtils() {
        return damUtils;
    }

    public void setDamUtils(DAMUtilsI damUtils) {
        this.damUtils = damUtils;
    }

}
