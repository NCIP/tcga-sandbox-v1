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

import java.util.List;

/**
 * Interface for level three loader queries
 * TODO: Whenever the loader is integrated with QCLIve , combine this file with other query interfaces
 * such as experiment queries.
 *
 * @author Stanley Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface LevelThreeQueries {

    public List<ArchiveInfo> getArchiveDeployLocation(final List<String> archive_name);

    public Integer getDataSetId(final Integer experimentId,
                                final String sourceFileName,
                                final String sourceFileType,
                                final String accessLevel);

    /**
     * Create a data set and return the data set Id
     *
     * @param centerId the center Id
     * @param experimentId the experiment Id
     * @param platformId the platform Id
     * @param sourceFileName the source file name
     * @param sourceFileType the source file type
     * @param accessLevel the access level
     * @param loadComplete <code>1</code> if the load is complete, <code>0</code> otherwise
     * @param dataLevel the data level
     * @param archiveId the archive Id
     * @return the data set Id
     */
    public Integer createDataSet(final Integer centerId,
                              final Integer experimentId,
                              final Integer platformId,
                              final String sourceFileName,
                              final String sourceFileType,
                              final String accessLevel,
                              final Integer loadComplete,
                              final Integer dataLevel,
                              final Long archiveId);


    public Integer getExperimentId(final String base, final Integer data_deposit_batch, final Integer data_revision);


    /**
     * Inserts a new experiment
     *
     * @param centerId         id of the center to insert
     * @param platformId       id of the platform to insert
     * @param baseName         base name of the experiment to inset
     * @param dataDepositBatch id for data deposit batch
     * @param dataRevision     data revision id
     * @return id of the experiment inserted / updated
     */
    public Integer insertExperiment(final Integer centerId,
                                    final Integer platformId,
                                    final String baseName,
                                    final Integer dataDepositBatch,
                                    final Integer dataRevision);


    public List<FileInfo> getFilesForArchive(Long archiveId);
    public List<String> getTumorBarcodesForFile(final Long fileId);

    public void createDataSetFile(Integer dataSetId, String fileName, Long fileId);

    public Integer getHybRefId(String barcode);

    public Integer insertHybRef(String barcode, String sample, String uuid);

    public Integer getHybrefDataSetId(Integer hybRefId, Integer dataSetId);

    public void addHybRefDataSet(Integer hybRefId, Integer dataSetId, String hybRefName);

    public void addCNAValue(List<Object[]> cnaParams);

    public void addMethylationValue(List<Object[]> methylationParams);

    public void addExpGeneValue(List<Object[]> expGeneParams);

    public void updateDataSetFile(Integer dataSetId, String fileName, Long fileId);

    public void updateDataSet(Integer dataSetId);

    public void updateArchiveLoadedDate(Long archiveId);

    /**
     * Add miRNASeq records.
     * For each records, the expected parameters are, in the following order:
     *
     * - feature
     * - read_count
     * - reads_per_million
     * - cross_mapped
     * - isoform_coords
     * - mirna_region_annotation
     * - mirna_region_accession
     * - data_set_id
     * - hybridization_ref_id
     *
     * @param batchArgs a list of batch arguments
     */
    public void addMirnaSeqValue(final List<Object[]> batchArgs);

    /**
     * Add RNASeq records.
     * For each records, the expected parameters are, in the following order:
     *
     * - feature
     * - raw counts
     * - median length normalized
     * - rkpm
     * - data_set_id
     * - hybridization_ref_id
     *
     * @param batchArgs a list of batch arguments
     */
    public void addRnaSeqValue(final List<Object[]> batchArgs);

    /**
     * Add protein expression data
     * @param dataList
     * Object array should contain the following values:
     *  - proteinexp_id (a unique key created with the sequence)
     *  - data_Set_id
     *  - hybrid_ref_id
     *  - antibody_name (composite ref name column in file)
     *  - gene_name (from lookup in the antibody_annotation file that came in the mage-tab archive)
     *  - protein_expression_value
     */
    public void addProteinExpValue(List<Object[]>  dataList);
}
