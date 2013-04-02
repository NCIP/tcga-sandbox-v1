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
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;

import java.util.List;

public interface DataAccessMatrixQueries extends ConstantValues {

    public static final String DEFAULT_DISEASETYPE = "GBM";

    /**
     * Special exception class that is thrown by methods of this interface.
     * There is a reason for declaring this: because, in writing the interface,
     * we can't anticipate all the exceptions that could be thrown by
     * future implementation methods. So we force the implementation to wrap those exceptions
     * in a DAMQueriesException by calling new DAMQueriesException(String, Throwable).
     * Then the calling method knows what exception to catch, but can still access
     * the underlying exception.
     */
    public static class DAMQueriesException extends Exception {

        public DAMQueriesException(final String message) {
            super(message);
        }

        public DAMQueriesException(final String message, final Throwable cause) {
            super(message, cause);
        }

        public DAMQueriesException(final Throwable cause) {
            super(cause);
        }
    }

    public static final String AVAILABILITY_AVAILABLE = "A";
    public static final String AVAILABILITY_PENDING = "P";
    public static final String AVAILABILITY_NOTAVAILABLE = "N";
    public static final String AVAILABILITY_NOTAPPLICABLE = "NA";
    public static final String TUMORNORMAL_TUMOR_WITH_MATCHED_NORMAL = "TN";
    public static final String TUMORNORMAL_TUMOR_WITHOUT_MATCHED_NORMAL = "T";
    public static final String TUMORNORMAL_NORMAL_WITH_MATCHED_TUMOR = "NT";
    public static final String TUMORNORMAL_HEALTHY_TISSUE_CONTROL = "N";
    public static final String TUMORNORMAL_CELL_LINE_CONTROL = "C";
    /**
     * pseudo platformtype, center Ids used in the UI clinical types
     * The actual values are arbitrary and are not used in db queries
     */
    public static final String CLINICAL_PLATFORMTYPE = "-999";
    public static final String CLINICAL_BIOTAB_CENTER = "-777";
    public static final String CLINICAL_XML_CENTER = "-666";
    public static final String LEVEL_CLINICAL = "C";
    public static final String LEVEL_METADATA = "M";
    public static final String GENERIC_BCR_CENTER = "BCR";
    public static final String SHOW_COMPLETE_ROWS_ONLY = "SCRO";

    /**
     * Returns list of all datasets for a disease type.
     * Each dataset will become a cell in the DAM.
     *
     * @param diseaseType
     * @return
     * @throws DAMQueriesException
     */
    List<DataSet> getDataSetsForDiseaseType(String diseaseType) throws DataAccessMatrixQueries.DAMQueriesException;

    /**
     * Gets a list of all datasets for control samples for the given implementation of this interface.
     *
     * @param diseaseTypes the list of all diseases
     * @return datasets representing control datasets for this DAMQueries implementation
     * @throws DataAccessMatrixQueries.DAMQueriesException
     *
     */
    List<DataSet> getDataSetsForControls(List<String> diseaseTypes) throws DataAccessMatrixQueries.DAMQueriesException;

    /**
     * Given a list of selected datasets (cells), returns additional information
     * pertaining to sample files.
     * The input fields that will be used for querying are: platformId, centerId, sample.
     * <p/>
     * Note: currently, consolidateFiles is only used for level 2 and 3 queries implementations.  In the future, if
     * other types of files want that behavior, they can use that parameter.
     *
     * @param selectedDataSets the data sets the user selected
     * @param consolidateFiles whether data for selected data sets should be consolidated into as few files as possible or put in one file per sample
     * @return data files needed to represent all data in selected data sets
     * @throws DAMQueriesException if there is an error figuring out the file info
     */
    List<DataFile> getFileInfoForSelectedDataSets(
            List<DataSet> selectedDataSets, final boolean consolidateFiles) throws DataAccessMatrixQueries.DAMQueriesException;

    /**
     * Given a list of fileinfo objects, adds path information to those objects.
     * Returns nothing.
     * For level 1, it can be the path to the pre-existing file. For levels 2+,
     * it's a path to a temp file written out from the database
     *
     * @param selectedFiles
     * @return
     * @throws DAMQueriesException
     */
    void addPathsToSelectedFiles(List<DataFile> selectedFiles) throws DataAccessMatrixQueries.DAMQueriesException;
}
