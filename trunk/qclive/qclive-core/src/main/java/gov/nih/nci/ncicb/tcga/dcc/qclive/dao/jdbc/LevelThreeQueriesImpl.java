/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;


import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.ArchiveInfo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * LevelThree Queries implementation.
 *
 * @author Stanley Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class LevelThreeQueriesImpl implements LevelThreeQueries {
    public static final String ADD_DATA_SET_FILE = "insert into data_set_file(data_set_file_id, data_set_id, file_name, load_start_date, file_id) values(data_set_file_seq.nextval, ?, ?, sysdate, ?)";

    public static final String UPDATE_DATA_SET_FILE = "update data_set_file set is_loaded=1, load_end_date=sysdate where data_set_id=? and file_name=? and file_id = ?";
    public static final String GET_DATA_SET = "select data_set_id from data_set where experiment_id = ? and source_file_name = ? and source_file_type = ? and access_level = ?";
    public static final String ADD_DATA_SET = "insert into data_set " +
            "(data_set_id," +
            " experiment_id," +
            " source_file_name," +
            " source_file_type," +
            " access_level," +
            " load_complete," +
            " use_in_dam ," +
            " dam_comments ," +
            " data_level ," +
            " center_id," +
            " platform_id," +
            " archive_id ) values (?, ?, ?, ?, ?, ?, null,null, ?, ?, ?, ?)";

    public static final String RETRIEVE_DEPLOY_LOCATION = "select deploy_location, is_latest,disease_abbreviation from archive_info a, disease d where a.disease_id = d.disease_id and archive_name=? ";


    public static final String SET_LOAD_STATUS_QUERY = "update data_set set load_complete=1, use_in_dam=1 where data_set_id = ?";


    public static final String GET_EXPERIMENT_ID_QUERY = "select experiment_id from experiment where base_name = ? and data_deposit_batch = ? and data_revision = ?";
    public static final String ADD_EXPERIMENT = "insert into experiment (experiment_id, center_id, platform_id, base_name, data_deposit_batch, data_revision) values (experiment_experiment_id_seq.NEXTVAL,?,?,?,?,?)";

    public static final String ADD_EXPGENE = "insert into expgene_value(expgene_value_id, data_set_id, hybridization_ref_id, entrez_gene_symbol, expression_value) values(EXPGENE_VALUE_SEQ.nextval, ?,?,?,?)";
    public static final String ADD_METHYLATION_VALUE = "insert into methylation_value(methylation_value_id,probe_name,data_set_id, hybridization_ref_id, beta_value, entrez_gene_symbol, chromosome, chr_position) values(METHYLATION_VALUE_SEQ.nextval, ?, ?, ?, ?, ?, ?, ?)";

    public static final String UPDATE_ARCHIVE_INFO = "update archive_info set DATA_LOADED_DATE=SYSDATE where archive_id=?";

    public static final String ADD_CNA_VALUE = "insert into cna_value(cna_value_id, data_set_id, hybridization_ref_id, chromosome, chr_start, chr_stop, num_mark, seg_mean) values (CNA_VALUE_SEQ.nextval, ?,?,?,?,?,?,?)";

    public static final String GET_HYBREDIZATION_REF = "select hybridization_ref_id from hybridization_ref where bestBarcode=?";
    public static final String ADD_HYBRIDIZATION_REF = "insert into hybridization_ref (hybridization_ref_id, bestBarcode, sample_name, uuid) values (?,?,?,?)";

    public static final String GET_HYBRIDIZATION_REF_ID = " select hybref_hybridization_seq.nextval from dual";

    public static final String GET_HYBREF_DATASET = "select hybref_dataset_id from hybrid_ref_data_set where hybridization_ref_id=? and data_set_id = ?";
    public static final String ADD_HYBREF_DATASET = "insert into hybrid_ref_data_set (hybref_dataset_id, hybridization_ref_id, data_set_id, hybridization_ref_name) values (hybref_dataset_seq.NEXTVAL,?,?,?)";

    public static final String GET_FILE_FOR_ARCHIVE = "select f.file_name,f.file_id from file_info f, file_to_archive fa where fa.archive_id = ? and fa.file_id=f.file_id";


    private static final String EXPERIMENT_INSERT_QUERY = "INSERT INTO experiment (experiment_id, base_name,data_deposit_batch, data_revision,center_id, platform_id ) values(experiment_experiment_id_seq.NEXTVAL,?,?,?,?,?)";

    private static final String ADD_MIRNASEQ_VALUE = "insert into mirnaseq_value (mirnaseq_id, feature, read_count, reads_per_million, cross_mapped, isoform_coords, mirna_region_annotation, mirna_region_accession, data_set_id, hybridization_ref_id)" +
            " values (MIRNASEQ_VALUE_SEQ.NEXTVAL, ?,?,?,?,?,?,?,?,?)";

    private static final String ADD_RNASEQ_VALUE = "insert into rnaseq_value (rnaseq_id, feature, raw_counts, median_length_normalized, rpkm, normalized_counts, scaled_estimate, transcript_id, data_set_id, hybridization_ref_id) " +
            "values(RNASEQ_VALUE_SEQ.NEXTVAL, ?,?,?,?,?,?,?,?,?)";

    private static final String ADD_PROTEINEXP_VALUE = "insert into proteinexp_value(" +
            "proteinexp_id," +
            "data_Set_id," +
            "hybridization_ref_id," +
            "antibody_name," +
            "hugo_gene_symbol," +
            "protein_expression_value) " +
            "values(PROTEINEXP_VALUE_SEQ.NEXTVAL, ?,?,?,?,?)";

    private static final String GET_NEXT_DATA_SET_ID_QUERY = "select data_set_data_set_id_seq.NEXTVAL from dual";

    public static final String GET_TUMOR_BARCODES_FOR_FILE =
            "select built_barcode from " +
                    " shipped_biospecimen_breakdown sb, " +
                    " sample_type s," +
                    " shipped_biospecimen_file sbf" +
                    " where" +
                    " sbf.FILE_ID = ? and" +
                    " sbf.SHIPPED_BIOSPECIMEN_ID = sb.SHIPPED_BIOSPECIMEN_ID and " +
                    " sb.sample_type_code=s.sample_type_code and" +
                    " s.is_tumor=1";




    SimpleJdbcTemplate simpleTemplate = null;


    public void setDataSource(javax.sql.DataSource ds) {
        simpleTemplate = new SimpleJdbcTemplate(ds);
    }

    private JdbcTemplate getJdbcTemplate() {
        return (JdbcTemplate) simpleTemplate.getJdbcOperations();
    }


    @Override
    public List<ArchiveInfo> getArchiveDeployLocation(final List<String> archive_name) {
        return getJdbcTemplate().query(RETRIEVE_DEPLOY_LOCATION, archive_name.toArray(),
                new ParameterizedRowMapper<ArchiveInfo>() {
                    public ArchiveInfo mapRow(final ResultSet resultSet, final int i) throws SQLException {
                        final ArchiveInfo archiveInfo = new ArchiveInfo();
                        archiveInfo.setDeployLocation(resultSet.getString("deploy_location"));
                        archiveInfo.setLatest(resultSet.getInt("is_latest"));
                        archiveInfo.setDiseaseAbbreviation("disease_abbreviation");
                        return archiveInfo;
                    }
                }
        );
    }


    @Override
    public Integer getDataSetId(final Integer experimentId,
                                final String sourceFileName,
                                final String sourceFileType,
                                final String accessLevel) {

        Object[] getDataSetParams = {experimentId, sourceFileName, sourceFileType, accessLevel};
        List<Integer> dataSetIdList = getJdbcTemplate().query(GET_DATA_SET, getDataSetParams,
                new ParameterizedRowMapper<Integer>() {
                    public Integer mapRow(final ResultSet resultSet, final int i) throws SQLException {
                        return resultSet.getInt("data_set_id");
                    }
                }
        );
        if (dataSetIdList != null && dataSetIdList.size() > 0) {
            return dataSetIdList.get(0);
        } else {
            return null;
        }

    }

    @Override
    public Integer createDataSet(final Integer centerId,
                              final Integer experimentId,
                              final Integer platformId,
                              final String sourceFileName,
                              final String sourceFileType,
                              final String accessLevel,
                              final Integer loadComplete,
                              final Integer dataLevel,
                              final Long archiveId) {

        final Integer dataSetId = getJdbcTemplate().queryForInt(GET_NEXT_DATA_SET_ID_QUERY);

        final Object[] createDataSetParams = {
                dataSetId,
                experimentId,
                sourceFileName,
                sourceFileType,
                accessLevel,
                loadComplete,
                dataLevel,
                centerId,
                platformId,
                archiveId,
        };

        getJdbcTemplate().update(ADD_DATA_SET, createDataSetParams);

        return dataSetId;
    }


    @Override
    public Integer getExperimentId(final String base, final Integer data_deposit_batch, final Integer data_revision) {
        Object[] base_batch_revision = {base, data_deposit_batch, data_revision};
        List<Integer> experimentList = getJdbcTemplate().query(GET_EXPERIMENT_ID_QUERY, base_batch_revision,
                new ParameterizedRowMapper<Integer>() {
                    public Integer mapRow(final ResultSet resultSet, final int i) throws SQLException {
                        return resultSet.getInt("experiment_id");
                    }
                }
        );
        if (experimentList != null && experimentList.size() > 0) {
            return experimentList.get(0);
        } else {
            return null;
        }
    }


    @Override
    public Integer insertExperiment(final Integer centerId,
                                    final Integer platformId,
                                    final String baseName,
                                    final Integer dataDepositBatch,
                                    final Integer dataRevision) {

        Object[] experimentParams = {baseName, dataDepositBatch, dataRevision, centerId, platformId};
        getJdbcTemplate().update(EXPERIMENT_INSERT_QUERY, experimentParams);
        return getExperimentId(baseName, dataDepositBatch, dataRevision);

    }

    @Override
    public List<FileInfo> getFilesForArchive(Long archiveId) {
        Object[] getfilesParams = {archiveId};
        return getJdbcTemplate().query(GET_FILE_FOR_ARCHIVE, getfilesParams,
                new ParameterizedRowMapper<FileInfo>() {
                    public FileInfo mapRow(final ResultSet resultSet, final int i) throws SQLException {
                        FileInfo files = new FileInfo();
                        files.setId(resultSet.getLong("file_id"));
                        files.setFileName(resultSet.getString("file_name"));
                        return files;
                    }
                }
        );
    }


    @Override
    public List<String> getTumorBarcodesForFile(final Long fileId){
        return getJdbcTemplate().query(GET_TUMOR_BARCODES_FOR_FILE, new Object[]{fileId},
                new ParameterizedRowMapper<String>() {
                    public String mapRow(final ResultSet resultSet, final int i) throws SQLException {
                        return resultSet.getString("built_barcode");
                    }
                }
        );
    }

    @Override
    public void createDataSetFile(Integer dataSetId, String fileName, Long fileId) {
        Object[] dataSetFileParams = {dataSetId, fileName, fileId};
        getJdbcTemplate().update(ADD_DATA_SET_FILE, dataSetFileParams);
    }

    @Override
    public Integer getHybRefId(String barcode) {
        String[] hybrefParams = {barcode};
        List<Integer> hybrefList = getJdbcTemplate().query(GET_HYBREDIZATION_REF, hybrefParams,
                new ParameterizedRowMapper<Integer>() {
                    public Integer mapRow(final ResultSet resultSet, final int i) throws SQLException {
                        return resultSet.getInt("hybridization_ref_id");
                    }
                }
        );
        if (hybrefList != null && hybrefList.size() > 0) {
            return hybrefList.get(0);
        } else {
            return null;
        }

    }


    @Override
    public Integer insertHybRef(String barcode, String sample, String uuid) {

        Integer hybRefId = getJdbcTemplate().queryForInt(GET_HYBRIDIZATION_REF_ID);
        Object[] params = {hybRefId, barcode, sample, uuid};
        getJdbcTemplate().update(ADD_HYBRIDIZATION_REF, params);
        return hybRefId;
    }

    @Override
    public Integer getHybrefDataSetId(Integer hybRefId, Integer dataSetId) {
        Object[] hybrefDataSetParams = {hybRefId, dataSetId};
        List<Integer> hybrefList = getJdbcTemplate().query(GET_HYBREF_DATASET, hybrefDataSetParams,
                new ParameterizedRowMapper<Integer>() {
                    public Integer mapRow(final ResultSet resultSet, final int i) throws SQLException {
                        return resultSet.getInt("hybref_dataset_id");
                    }
                }
        );
        if (hybrefList != null && hybrefList.size() > 0) {
            return hybrefList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public void addHybRefDataSet(Integer hybRefId, Integer dataSetId, String hybRefName) {
        Object[] params = {hybRefId, dataSetId, hybRefName};
        getJdbcTemplate().update(ADD_HYBREF_DATASET, params);
    }

    @Override
    public void addCNAValue(List<Object[]> cnaParams) {
        simpleTemplate.batchUpdate(ADD_CNA_VALUE, cnaParams);
    }

    @Override
    public void updateDataSetFile(Integer dataSetId, String fileName, Long fileId) {
        Object[] dataSetFileParams = {dataSetId, fileName, fileId};
        getJdbcTemplate().update(UPDATE_DATA_SET_FILE, dataSetFileParams);
    }

    @Override
    public void updateDataSet(Integer dataSetId) {
        Object[] dataSetParam = {dataSetId};
        getJdbcTemplate().update(SET_LOAD_STATUS_QUERY, dataSetParam);
    }

    @Override
    public void updateArchiveLoadedDate(Long archiveId) {
        Object[] archiveParam = {archiveId};
        getJdbcTemplate().update(UPDATE_ARCHIVE_INFO, archiveParam);
    }

    @Override
    public void addMethylationValue(List<Object[]> methylationParams) {
        simpleTemplate.batchUpdate(ADD_METHYLATION_VALUE, methylationParams);
    }

    @Override
    public void addExpGeneValue(List<Object[]> expGeneParams) {
        simpleTemplate.batchUpdate(ADD_EXPGENE, expGeneParams);
    }

    @Override
    public void addMirnaSeqValue(final List<Object[]> batchArgs) {
        simpleTemplate.batchUpdate(ADD_MIRNASEQ_VALUE, batchArgs);
    }

    @Override
    public void addRnaSeqValue(List<Object[]> batchArgs) {
        simpleTemplate.batchUpdate(ADD_RNASEQ_VALUE, batchArgs);
    }

    @Override
    public void addProteinExpValue(List<Object[]>  dataList){
        simpleTemplate.batchUpdate(ADD_PROTEINEXP_VALUE,dataList);
    }
}
