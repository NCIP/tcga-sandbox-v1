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
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileMutation;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetMutation;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.ThreadedBufferedWriter;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.ErrorInfo;
import org.apache.log4j.Level;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author David Nassau Last updated by: Jeyanthi Thangiah
 * @version $Rev$
 */
public class DAMQueriesMutation extends DAMBaseQueriesProcessor implements DataAccessMatrixQueries {

    private static final String MUTATION_QUERY_SELECT = "select distinct " +
            "c.center_id, c.domain_name, " +
            "mi.tumor_sample_barcode, " +
            "mi.match_norm_sample_barcode, " +
            "ai.date_added, " +
            "ai.archive_id, " +
            "p.platform_id, " +
            "p.platform_alias, " +
            "p.platform_display_name, v.identifiable, at.data_level, dt.data_type_id";

    private static final String MUTATION_QUERY_FROM = " from file_info fi, " +
            "archive_info ai, " +
            "file_to_archive fa, " +
            "maf_info mi, " +
            "maf_key mk, " +
            "shipped_biospecimen bb, " +
            "center_to_bcr_center ctbc, " +
            "platform p, center c, data_type dt," +
            "visibility v, data_visibility dv, archive_type at, " +
            "(select maf_info_id, tumor_sample_barcode||'%' as barcode from maf_info) mv ";

    private static final String MUTATION_QUERY_WHERE = " where " +
            "p.platform_id = ai.platform_id " +
            "and mi.maf_key_id = mk.maf_key_id " +
            "and c.center_id = mk.center_id " +
            "and fa.archive_id = ai.archive_id " +
            "and fa.file_id = fi.file_id " +
            "and mi.file_id = fi.file_id " +
            "and mi.maf_info_id = mv.maf_info_id " +
            "and bb.built_barcode like mv.barcode " +
            "and bb.bcr_center_id = ctbc.bcr_center_id " +
            "and mk.center_id = ctbc.center_id " +
            "and v.visibility_id = dv.visibility_id " +
            "and at.archive_type_id = ai.archive_type_id " +
            "and dt.data_type_id = p.base_data_type_id " +
            "and at.data_level = dv.level_number " +
            "and dv.data_type_id = dt.data_type_id " +
            "and ai.is_latest = 1 " +
            "and bb.is_viewable = 1 " +
            "and bb.is_control = ?";

    static final String MUTATION_QUERY = MUTATION_QUERY_SELECT + MUTATION_QUERY_FROM + MUTATION_QUERY_WHERE;

    static final String[] FIELDS_2_WRITE = {
            "hugo_symbol",
            "entrez_gene_id",
            "domain_name",
            "ncbi_build",
            "chrom",
            "start_position",
            "end_position",
            "strand",
            "variant_classification",
            "variant_type",
            "reference_allele",
            "tumor_seq_allele1",
            "tumor_seq_allele2",
            "dbsnp_rs",
            "dbsnp_val_status",
            "tumor_sample_barcode",
            "match_norm_sample_barcode",
            "match_norm_seq_allele1",
            "match_norm_seq_allele2",
            "tumor_validation_allele1",
            "tumor_validation_allele2",
            "match_norm_validation_allele1",
            "match_norm_validation_allele2",
            "verification_status",
            "validation_status",
            "mutation_status",
            "sequencing_phase",
            "sequence_source",
            "validation_method",
            "score",
            "bam_file",
            "sequencer",
            "tumor_sample_uuid",
            "match_norm_sample_uuid",
            "file_name",
            "archive_name",
            "line_number"
    };

    private String tempfileDirectory;
    private String mutationPlatformTypeId;
    private Integer mutationPlatformTypeSortOrder;
    private String protectedMutationPlatformTypeId;
    private Integer protectedMutationTypeSortOrder;

    public void setTempfileDirectory(final String tempfileDirectory) {
        this.tempfileDirectory = tempfileDirectory;
    }

    @Override
    public List<DataSet> getDataSetsForDiseaseType(
            final String diseaseType) throws DataAccessMatrixQueries.DAMQueriesException {
        return getDataSets(Arrays.asList(diseaseType), Control.FALSE);
    }

    @Override
    public List<DataSet> getDataSetsForControls(final List<String> diseaseTypes) throws DAMQueriesException {
        return getDataSets(diseaseTypes, Control.TRUE);
    }

    private List<DataSet> getDataSets(final List<String> diseaseTypes, final Control isControl) throws DAMQueriesException {
        final List<DataSet> retDataSets = new ArrayList<DataSet>();
        final Map<String, Integer> barcodeToBatch = getBarcodeBatches();
        setInstanceVariables();
        for (final String diseaseType : diseaseTypes) {
            retDataSets.addAll(makeDatasets(diseaseType, barcodeToBatch, isControl.value()));
        }
        return retDataSets;
    }

    private void setInstanceVariables() {
        // this is not a good thing to do -- we shouldn't hard-code the data type.  but there is no way to get the correct
        // data type right now.  ideally the file_info table would have the right data type id.  It doesn't yet.  APPS-1206
        final SimpleJdbcTemplate jdbc = new SimpleJdbcTemplate(getDataSource());
        mutationPlatformTypeId = jdbc.queryForObject("select data_type_id from data_type where ftp_display = 'mutations'", String.class);
        mutationPlatformTypeSortOrder = jdbc.queryForInt("select sort_order from data_type where ftp_display = 'mutations'");

        protectedMutationPlatformTypeId = jdbc.queryForObject("select data_type_id from data_type where ftp_display = 'mutations_protected'", String.class);
        protectedMutationTypeSortOrder = jdbc.queryForInt("select sort_order from data_type where ftp_display = 'mutations_protected'");
    }


    private Collection<DataSet> makeDatasets(final String diseaseType,
                                             final Map<String, Integer> barcodeToBatch, final int isControlValue) {
        final Map<String, DataSet> dataSets = new HashMap<String, DataSet>();
        final SimpleJdbcTemplate jdbc = new SimpleJdbcTemplate(getDataSource());
        //this query will list each barcode just once
        final List<Map<String, Object>> records = jdbc.queryForList(MUTATION_QUERY, isControlValue);
        for (final Map<String, Object> record : records) {
            //mutant cell
            final DataSetMutation dsM = makeDataSet(record);
            dsM.setDiseaseType(diseaseType);

            // is there already a data set for this?
            String key = dsM.getPlatformTypeId() + PIPE_SYMBOL + dsM.getPlatformId() + PIPE_SYMBOL + dsM.getCenterId() +
                    PIPE_SYMBOL + dsM.getMutationBarcode();
            if (dataSets.get(key) == null) {
                // add datasets for this record
                dsM.setTumorNormal(DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITH_MATCHED_NORMAL);
                dsM.setSample(dsM.getMutationBarcode().substring(0, 15));
                String batch = ConstantValues.UNCLASSIFIED_BATCH;
                if (barcodeToBatch.get(dsM.getSample()) != null) {
                    batch = "Batch " + barcodeToBatch.get(dsM.getSample());
                }
                dsM.setBatch(batch);
                dataSets.put(key, dsM);
            }
            //normal cell
            //assumption here is that each normal barcode maps to just one mutant - will always be true?
            final DataSetMutation dsN = makeDataSet(record);
            dsN.setDiseaseType(diseaseType);
            key = dsN.getPlatformTypeId() + PIPE_SYMBOL + dsN.getPlatformId() + PIPE_SYMBOL + dsN.getCenterId() +
                    PIPE_SYMBOL + dsN.getMatchedNormalBarcode();
            if (dataSets.get(key) == null) {
                dsN.setTumorNormal(DataAccessMatrixQueries.TUMORNORMAL_NORMAL_WITH_MATCHED_TUMOR);
                dsN.setSample(dsN.getMatchedNormalBarcode().substring(0, 15));
                String batch = ConstantValues.UNCLASSIFIED_BATCH;
                if (barcodeToBatch.get(dsN.getSample()) != null) {
                    batch = "Batch " + barcodeToBatch.get(dsN.getSample());
                }
                dsN.setBatch(batch);
                dataSets.put(key, dsN);
            }
        }

        return dataSets.values();
    }

    private DataSetMutation makeDataSet(final Map<String, Object> record) {
        final DataSetMutation ds = new DataSetMutation();
        ds.setCenterName(record.get("domain_name").toString());
        ds.setPlatformAlias(record.get("platform_alias").toString());
        ds.setPlatformId(record.get("platform_id").toString());
        ds.setPlatformDisplayName(record.get("platform_display_name").toString());
        ds.setArchiveId(((BigDecimal) record.get("archive_id")).intValue());
        ds.setCenterId((record.get("center_id")).toString());
        ds.setLevel(record.get("data_level").toString());
        // note: don't get batch in this query anymore
        ds.setAvailability(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE);
        ds.setBarcodes(null); //intentionally leave null
        ds.setProtected(record.get("identifiable").toString().equals("1"));

        if (ds.isProtected()) {
            ds.setPlatformTypeId(protectedMutationPlatformTypeId);
            ds.setPlatformTypeSortOrder(protectedMutationTypeSortOrder);
        } else {
            ds.setPlatformTypeId(mutationPlatformTypeId);
            ds.setPlatformTypeSortOrder(mutationPlatformTypeSortOrder);
        }


        ds.setMutationBarcode((String) record.get("tumor_sample_barcode"));
        ds.setMatchedNormalBarcode((String) record.get("match_norm_sample_barcode"));
        //redundant, but add here too
        final List<String> barcodes = new ArrayList<String>();
        barcodes.add(ds.getMutationBarcode());
        barcodes.add(ds.getMatchedNormalBarcode());
        ds.setBarcodes(barcodes);
        final Timestamp timestamp = (Timestamp) record.get("date_added");
        if (timestamp != null) {
            ds.setDateAdded(new Date(timestamp.getTime()));
        }
        return ds;
    }

    /**
     * Build the DataFile objects needed for the selected data sets.
     *
     * @param selectedDataSets the data sets the user selected
     * @param consolidateFiles whether data for selected data sets should be consolidated -- IGNORED IN THIS
     *                         IMPLEMENTATION
     * @return data files representing the given data sets
     * @throws DataAccessMatrixQueries.DAMQueriesException
     *
     */
    public List<DataFile> getFileInfoForSelectedDataSets(
            final List<DataSet> selectedDataSets, final boolean consolidateFiles)
            throws DataAccessMatrixQueries.DAMQueriesException {
        //get a sublist of all the mutation datasets
        final List<DataSetMutation> mutationDatasets = new ArrayList<DataSetMutation>();
        for (final DataSet ds : selectedDataSets) {
            if (ds instanceof DataSetMutation) {
                mutationDatasets.add((DataSetMutation) ds);
            }
        }
        //sort by center, platform, level
        Collections.sort(mutationDatasets, new Comparator<DataSetMutation>() {
            public int compare(final DataSetMutation dsm1, final DataSetMutation dsm2) {
                int compareResult = dsm1.getDiseaseType().compareTo(dsm2.getDiseaseType());
                if (compareResult == 0) {
                    compareResult = dsm1.getCenterId().compareTo(dsm2.getCenterId());
                    if (compareResult == 0) {
                        compareResult = dsm1.getPlatformId().compareTo(dsm2.getPlatformId());
                        if (compareResult == 0) {
                            compareResult = dsm1.getLevel().compareTo(dsm2.getLevel());
                        }
                    }
                }
                return compareResult;
            }
        });
        return createDataFileInstances(mutationDatasets);
    }

    //List has been sorted by center, platform, level.
    // Take the first of each grouping and copy the properties.
    //Then copy in all the barcodes for that grouping

    private List<DataFile> createDataFileInstances(
            final List<DataSetMutation> datasets) throws DataAccessMatrixQueries.DAMQueriesException {
        final List<DataFile> ret = new ArrayList<DataFile>();
        DataFile lastDF = null;
        for (final DataSetMutation ds : datasets) {
            boolean newColumn = false;
            final String center = ds.getCenterId();
            final String level = ds.getLevel();
            final String platform = ds.getPlatformId();
            if (lastDF == null) {
                newColumn = true;
            } else if (!center.equals(lastDF.getCenterId())) {
                newColumn = true;
            } else if (!level.equals(lastDF.getLevel())) {
                newColumn = true;
            } else if (!platform.equals(lastDF.getPlatformId())) {
                newColumn = true;
            } else if (!ds.getDiseaseType().equals(lastDF.getDiseaseType())) {
                newColumn = true;
            }
            if (newColumn) {
                final DataFileMutation df = new DataFileMutation();
                df.setMafFile(true);
                df.setDiseaseType(ds.getDiseaseType());
                df.setPlatformTypeId(ds.getPlatformTypeId());
                df.setCenterId(ds.getCenterId());
                df.setPlatformId(ds.getPlatformId());
                df.setProtected(ds.isProtected());
                df.setLevel(ds.getLevel());
                df.setDisplaySample(LEVEL23_SAMPLE_PHRASE);
                df.setBarcodes(new TreeSet<String>());
                final String filename = ds.getCenterName() + "__" + ds.getPlatformDisplayName() + "_level" + ds.getLevel() + ".maf";
                df.setFileName(filename.replace(' ', '_')); //should do that replace in the set_ method?                
                ret.add(df);
                lastDF = df;
            }
            //add the barcode - will not be duplicates because the barcodes collection is a set
            lastDF.getBarcodes().add(ds.getMutationBarcode());
        }
        estimateSize(ret);
        return ret;
    }

    private static final int BYTES_PER_ROW = 175; //arrived empirically by looking at data

    private void estimateSize(final List<DataFile> dflist) throws DataAccessMatrixQueries.DAMQueriesException {
        final Connection connection = getConnection();
        try {
            for (final DataFile df : dflist) {
                DiseaseContextHolder.setDisease(df.getDiseaseType());
                long totalBytesForFile = 0;
                PreparedStatement stmt = null;
                try {
                    String sql = "select count(maf_info_id) from maf_info mi, maf_key mk, file_to_archive fa, archive_info a " +
                            "where a.archive_id=fa.archive_id and fa.file_id=mi.file_id " +
                            "and tumor_sample_barcode = ? " +
                            "and mi.maf_key_id = mk.maf_key_id and mk.center_id = ? and a.platform_id = ?";

                    //noinspection JDBCResourceOpenedButNotSafelyClosed
                    stmt = connection.prepareStatement(sql);
                    for (final String barcode : df.getBarcodes()) {
                        ResultSet rs = null;
                        try {
                            stmt.setString(1, barcode);
                            stmt.setInt(2, Integer.parseInt(df.getCenterId()));
                            stmt.setInt(3, Integer.parseInt(df.getPlatformId()));
                            rs = stmt.executeQuery();
                            rs.next();
                            final int rowsForBarcode = rs.getInt(1);
                            totalBytesForFile += (rowsForBarcode * BYTES_PER_ROW);
                        } finally {
                            SQLProcessingCleaner.cleanUpResultSet(rs);
                        }
                    }
                    df.setSize(totalBytesForFile);
                } catch (SQLException e) {
                    new ErrorInfo(e); //logs
                    throw new DataAccessMatrixQueries.DAMQueriesException(e);
                } finally {
                    SQLProcessingCleaner.cleanUpStatement(stmt);
                }
            }
        } finally {
            SQLProcessingCleaner.cleanUpConnection(connection);
        }
    }

    //structure we can use for sorting rows in the output

    class MafRowSortInfo {

        int id;
        String hugo;
        int start, end;
        String bc;
    }

    //We use low-level JDBC (as opposed to Spring templates) because it allows us to re-use the
    //prepared statement, gaining in performance.

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
                if (df instanceof DataFileMutation) {
                    DiseaseContextHolder.setDisease(df.getDiseaseType());
                    writeDataFile(logger, (DataFileMutation) df, connection);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessMatrixQueries.DAMQueriesException("SQLException thrown while running prepared statement", e);
        } finally {
            SQLProcessingCleaner.cleanUpConnection(connection);
        }
    }

    private void writeDataFile(
            final ProcessLogger logger, final DataFileMutation dfm,
            final Connection connection) throws SQLException, DataAccessMatrixQueries.DAMQueriesException {
        final StringBuilder sql1 = new StringBuilder("select maf_info_id, hugo_symbol, start_position, end_position");
        sql1.append(" from maf_info mi, maf_key mk, file_to_archive fa, archive_info a ")
                .append(" where mi.tumor_sample_barcode = ? and mk.center_id = ? ")
                .append(" and mi.maf_key_id = mk.maf_key_id ")
                .append(" and mi.file_id =  fa.file_id ")
                .append(" and fa.archive_id = a.archive_id and a.is_latest = 1 and a.platform_id = ?");

        final String sql2 = "select * from maf_info mi, maf_key mk, center, file_info fi, file_to_archive fta, " +
                "archive_info ai " +
                "where mi.maf_key_id = mk.maf_key_id " +
                "and mk.center_id=center.center_id " +
                "and mi.maf_info_id = ? " +
                "and mi.file_id = fi.file_id " +
                "and fi.file_id = fta.file_id " +
                "and fta.archive_id = ai.archive_id " +
                "and ai.is_latest = 1";
        PreparedStatement stmt1 = null, stmt2 = null;
        Writer writer = null;
        final String uniqueName = makeTempFilename(dfm);
        final String path = tempfileDirectory + "/" + uniqueName;
        dfm.setPath(path);  //so file packager knows where to copy file from
        try {
            //noinspection JDBCResourceOpenedButNotSafelyClosed
            stmt1 = connection.prepareStatement(sql1.toString());
            //noinspection JDBCResourceOpenedButNotSafelyClosed
            stmt2 = connection.prepareStatement(sql2);
            //sort the keys so we can retrieve the maf rows in the right order
            final List<MafRowSortInfo> rowkeys = makeSortedRowkeys(stmt1, dfm);
            if (useThreadedBufferedWriter) {
                writer = new ThreadedBufferedWriter(new FileWriter(path), bufferedWriterBufferSize);
            } else {
                writer = new BufferedWriter(new FileWriter(path), bufferedWriterBufferSize);
            }
            writeRows(stmt2, rowkeys, writer);
            writer.flush();
            writer.close();
            writer = null;
        } catch (IOException e) {
            new ErrorInfo(e); //logs itself
            throw new DataAccessMatrixQueries.DAMQueriesException(e);
        } catch (SQLException e) {
            new ErrorInfo(e); //logs itself
            throw new DataAccessMatrixQueries.DAMQueriesException(e);
        } finally {
            if (writer != null) {
                //will only happen if some exception occurred. In that case, we don't care about
                //flushing the buffer-  file will not be used anyway. Just make sure it's closed
                try {
                    writer.close();
                } catch (IOException e) {
                    logger.logToLogger(Level.WARN, "Could not close writer.");
                }
            }
            SQLProcessingCleaner.cleanUpStatement(stmt1);
            SQLProcessingCleaner.cleanUpStatement(stmt2);
        }
    }

    protected String makeTempFilename(final DataFileMutation dfm) {
        return java.util.UUID.randomUUID().toString();
    }

    private void writeRows(
            final PreparedStatement stmt2, final List<MafRowSortInfo> rowkeys,
            final Writer writer) throws SQLException, DataAccessMatrixQueries.DAMQueriesException, IOException {
        writeHeader(writer);
        //since the rowkeys list is already sorted in the order we want, we can just retrieve line by line
        //and build the output
        for (final MafRowSortInfo rowkey : rowkeys) {
            stmt2.setInt(1, rowkey.id);
            ResultSet rs = null;
            try {
                rs = stmt2.executeQuery();
                rs.next();
                for (int i = 0; i < FIELDS_2_WRITE.length; i++) {
                    String writeval = rs.getString(FIELDS_2_WRITE[i]);
                    if (writeval == null) {
                        writeval = "";
                    }
                    writer.write(writeval);
                    if (i == FIELDS_2_WRITE.length - 1) {
                        writer.write('\n');
                    } else {
                        writer.write('\t');
                    }
                }
            } finally {
                SQLProcessingCleaner.cleanUpResultSet(rs); //but not the statement, that's released at a higher level
            }
        }
    }

    //make a sorted list of the key values which we can use to sort all the rows for one maf output file
    //prior to retrieving all the data.  We do this because we can't sort the output rows in a query
    //(can't make on-the-fly temp table of barcodes); and we don't want to retrieve all the values in memory
    //in order to sort them.

    private List<MafRowSortInfo> makeSortedRowkeys(
            final PreparedStatement stmt1,
            final DataFileMutation dfm) throws SQLException {
        final List<MafRowSortInfo> rowkeys = new ArrayList<MafRowSortInfo>();
        for (final String barcode : dfm.getBarcodes()) {  //for each tumor barcode associated with this data file
            stmt1.setString(1, barcode);
            stmt1.setInt(2, Integer.parseInt(dfm.getCenterId()));
            stmt1.setInt(3, Integer.parseInt(dfm.getPlatformId()));
            ResultSet rs = null;
            try {
                rs = stmt1.executeQuery();
                while (rs.next()) {
                    final MafRowSortInfo rowkey = new MafRowSortInfo();
                    rowkey.id = rs.getInt("maf_info_id");
                    rowkey.hugo = rs.getString("hugo_symbol");
                    rowkey.start = rs.getInt("start_position");
                    rowkey.end = rs.getInt("end_position");
                    rowkey.bc = barcode;
                    rowkeys.add(rowkey);
                }
            } finally {
                SQLProcessingCleaner.cleanUpResultSet(rs);  //but leave statement alone - it will be cleaned up in the calling method (faster, since we can reuse the statement)
            }
        }
        //sort the keys in the order we want to retrieve the data
        //sort order is hugo symbol, start pos, end pos, barcode
        Collections.sort(rowkeys, new Comparator<MafRowSortInfo>() {
            public int compare(final MafRowSortInfo row1, final MafRowSortInfo row2) {
                int ret = row1.hugo.compareTo(row2.hugo);
                if (ret == 0) {
                    if (row1.start < row2.start) {
                        ret = -1;
                    } else if (row1.start > row2.start) {
                        ret = 1;
                    } else {
                        if (row1.end < row2.end) {
                            ret = -1;
                        } else if (row2.end > row2.end) {
                            ret = 1;
                        } else {
                            ret = row1.bc.compareTo(row2.bc);
                        }
                    }
                }
                return ret;
            }
        });
        return rowkeys;
    }

    private void writeHeader(final Writer writer) throws IOException {
        for (int i = 0; i < FIELDS_2_WRITE.length; i++) {
            String writeVal = FIELDS_2_WRITE[i];
            if (writeVal.equals("domain_name")) {
                writeVal = "center";
            } else if (writeVal.equals("match_norm_sample_barcode")) {
                writeVal = "matched_norm_sample_barcode";
            } else if (writeVal.equals("match_norm_sample_uuid")) {
                writeVal = "matched_norm_sample_uuid";
            }
            writeVal = titleMafCase(writeVal);
            writer.write(writeVal);
            if (i == FIELDS_2_WRITE.length - 1) {
                writer.write('\n');
            } else {
                writer.write('\t');
            }
        }
    }

    private String titleMafCase(final String s) {
        final char[] cc = s.toCharArray();
        boolean capNext = true;
        for (int i = 0; i < cc.length; i++) {
            if (capNext) {
                cc[i] = Character.toString(cc[i]).toUpperCase().charAt(0); //kludge?
                capNext = false;
            }
            if (cc[i] == '_') {
                capNext = true;
            }
        }
        final String res = new String(cc);
        return res.replace("Uuid", "UUID");
    }

}//End of Class
