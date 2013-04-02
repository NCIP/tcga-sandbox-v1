/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.DataSorterAndGapFillerI;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.TumorNormalClassifierI;
import org.apache.log4j.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Author: David Nassau
 * <p/>
 * A "parent" level DAO instance that dispatches method calls to all the "child"
 * DAOs, then combines the resulting information.  This hides the multiplicity of database
 * and data types from the calling class.
 */
public class DAMQueriesFacade implements DataAccessMatrixQueries {
    private ProcessLogger logger = new ProcessLogger();
    private List<DataAccessMatrixQueries> DAOs;
    private DAMSubmittedSampleI submittedSampleGetter;
    private DataSorterAndGapFillerI sorterAndGapFiller;
    private TumorNormalClassifierI tumorNormalClassifier;

    public void setDAOs(final List<DataAccessMatrixQueries> DAOs) {
        this.DAOs = DAOs;
    }

    public void setSubmittedSampleGetter(final DAMSubmittedSampleI submittedSampleGetter) {
        this.submittedSampleGetter = submittedSampleGetter;
    }

    public void setSorterAndGapFiller(final DataSorterAndGapFillerI sagp) {
        sorterAndGapFiller = sagp;
    }

    public List<DataSet> getDataSetsForDiseaseType(final String diseaseType) throws DAMQueriesException {
        List<DataSet> dataSetsForDisease = new ArrayList<DataSet>();
        for (final DataAccessMatrixQueries child : DAOs) {
            logger.logToLogger(Level.INFO, "Getting data sets for " + diseaseType + " " + child.getClass().getSimpleName());
            final List<DataSet> childRet = child.getDataSetsForDiseaseType(diseaseType);
            if (childRet != null) {
                dataSetsForDisease.addAll(childRet);
            }
        }
        Set<String> submittedSamples = getSubmittedSampleIds(diseaseType);
        sorterAndGapFiller.sortAndFillGaps(dataSetsForDisease, submittedSamples);
        tumorNormalClassifier.classifyTumorNormal(dataSetsForDisease);
        return dataSetsForDisease;
    }

    @Override
    public List<DataSet> getDataSetsForControls(final List<String> diseaseTypes) throws DAMQueriesException {
        final List<DataSet> controlDataSets = new ArrayList<DataSet>();
        for (final DataAccessMatrixQueries childDAO : DAOs) {
            logger.logToLogger(Level.INFO, "Getting control data sets for " + childDAO.getClass().getSimpleName());
            final List<DataSet> childControlDataSets = childDAO.getDataSetsForControls(diseaseTypes);
            if (childControlDataSets != null) {
                controlDataSets.addAll(childControlDataSets);
            }
        }
        Set<String> submittedControls = getSubmittedControls(diseaseTypes);
        sorterAndGapFiller.sortAndFillGaps(controlDataSets, submittedControls);
        tumorNormalClassifier.classifyTumorNormal(controlDataSets);
        return controlDataSets;
    }

    private Set<String> getSubmittedControls(final List<String> diseaseTypes) throws DAMQueriesException {
        return submittedSampleGetter.getSubmittedControls(diseaseTypes);
    }

    public Set<String> getSubmittedSampleIds(final String diseaseType) throws DAMQueriesException {
        return submittedSampleGetter.getSubmittedSampleIds(diseaseType);
    }

    public List<DataFile> getFileInfoForSelectedDataSets(
            final List<DataSet> selectedDataSets, final boolean consolidateFiles) throws DAMQueriesException {
        final List<DataFile> ret = new ArrayList<DataFile>();
        for (final DataAccessMatrixQueries child : DAOs) {
            final List<DataFile> childRet = child.getFileInfoForSelectedDataSets(selectedDataSets, consolidateFiles);
            if (childRet != null) {
                Collections.sort(childRet, new Comparator<DataFile>() {
                    public int compare(final DataFile df1, final DataFile df2) {
                        if (DAMQueriesMetadata.SAMPLE_ANNOTATION_FILENAME.equalsIgnoreCase(df1.getFileName())) {
                            return -1;
                        }
                        if (DAMQueriesMetadata.SAMPLE_ANNOTATION_FILENAME.equalsIgnoreCase(df2.getFileName())) {
                            return 1;
                        }
                        return df1.getFileName().toLowerCase().compareTo(df2.getFileName().toLowerCase());
                    }
                });
                ret.addAll(childRet);
            }
        }
        return ret;
    }

    public void addPathsToSelectedFiles(final List<DataFile> selectedFiles) throws DAMQueriesException {
        for (final DataAccessMatrixQueries child : DAOs) {
            child.addPathsToSelectedFiles(selectedFiles);
        }
    }

    public void setTumorNormalClassifier(final TumorNormalClassifierI tumorNormalClassifier) {
        this.tumorNormalClassifier = tumorNormalClassifier;
    }
}
