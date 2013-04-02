/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.loader;

import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.LoaderQueriesException;
import org.springframework.transaction.support.TransactionOperations;

import java.util.List;
import java.util.Map;

/**
 * @author nassaud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface LoaderQueries {

    /**
     * Add a row to the data_set_file table, indicating that the given file is part of the data set.
     * Will assume file isn't loaded yet, and set the load_start_date to the current date.
     *
     * @param datasetId the id of the data set
     * @param filename the name of the file
     * @param fileInfoId
     * @throws LoaderQueriesException
     */
    void insertDataSetFile(final long datasetId, final String filename, long fileInfoId) throws LoaderQueriesException;

    /**
     * Sets is_loaded to true for the data_set_file entry for the given data set and filename.  Will set the load_end_date
     * to the current date.
     *
     * @param datasetid the id of the data set
     * @param filename the name of the file
     */
    void setDataSetFileLoaded(final long datasetid, final String filename) throws LoaderQueriesException;

    long lookupExperimentId(final String baseName, final int dataDepositBatch, final int dataRevision);

    /**
     * Inserts a row in experiment.  One row per archive.
     */
    long insertExperiment(String baseName, int dataDepositBatch, int dataRevision, long centerId, long platformId)
        throws LoaderQueriesException;

    /**
     * Inserts a row in data_set.  One row per data file.
     */
    long insertDataset(long experimentId, String sourceFileName, String sourceFileType, String accessLevel,
                       int dataLevel, long centerId, long platformId, long archiveId)
        throws LoaderQueriesException;

    /**
     * Inserts a row in hybridization_data_group.  One row per distinct data column name per data file.
     */    //todo refactor, have just the singular insert in the dao
    Map<String,Long> insertHybDataGroups(final long datasetId, final List<String> groupColumnNames)
        throws LoaderQueriesException;

    long lookupHybRefId(final String bestbarcode);

    /**
     * Inserts a row in hybridization_ref. One row per barcode per archive.
     *
     */
    long insertHybRef(String bestBarcode, String sampleName, long aliquotId, String uuid)
        throws LoaderQueriesException;

    long insertHybRefDataset(long hybridizationRefId, long dataSetId, String hybridizationRefName)
        throws LoaderQueriesException;


    /**
     * Inserts a row in hybridization_value. One row per probe (row) and result column per dat file.
     * Result values are inserted as strings.
     *
     */
    void insertHybridizationValue(long platformId, long hybRefId, long hybDataGroupId, long probeId, String value)
        throws LoaderQueriesException;

    /**
     * Looks up the center id from the given center name and platform id
     * @param centerName center name
     * @param platformId platform id
     * @return center id
     * @throws LoaderQueriesException exception if center id cannot be found
     */
    int lookupCenterId(String centerName, int platformId) throws LoaderQueriesException;

    int lookupPlatformId(String platformName) throws LoaderQueriesException;

//    int lookupDataTypeId(long platformId, String filetype) throws LoaderQueriesException;

    Map<String,Integer> downloadProbesForPlatform(int platformId) throws LoaderQueriesException;

    boolean hybRefDatasetExists(long hybrefId, long datasetId);

    TransactionOperations getTransactionOperations();

    void setDataSetLoaded(long datasetId) throws LoaderQueriesException;

    void insertHybridizationValues(List<Object[]> batchedArguments) throws LoaderQueriesException;

    public Map<String, Long> lookupFileInfoData(long archiveId) throws LoaderQueriesException;
}
