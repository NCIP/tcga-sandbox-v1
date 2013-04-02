/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.view;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.Disease;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DAMUtils;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.processors.CachedOutputManager;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.HomePageStatsService;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.HomePageStatsServiceImpl;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.TumorDetailsService;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.DataSorterAndGapFillerI;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.FilterRequestValidatorImpl;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.TumorNormalClassifier;
import org.apache.log4j.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: David Nassau
 * <p/>
 * Called either from DataAccessMatrixController or DataAccessExternalFilterController
 * to get or create the static model. This must be synchronized so only one model
 * is created per disease type per session.
 * <p/>
 * Creates the model from the ctor so it happens immediately upon app-load.
 */
public class StaticMatrixModelFactory implements StaticMatrixModelFactoryI {

    private DataAccessMatrixQueries dataAccessMatrixQueries;
    private DataSorterAndGapFillerI dataSetSorterAndGapFiller;
    private String controlDiseaseAbbreviation;
    private boolean enabled;
    private ProcessLogger logger = new ProcessLogger();

    private TumorDetailsService tumorDetailsService;
    private HomePageStatsService homePageStatsService;

    public StaticMatrixModelFactory(final boolean enabled,
                                    final DataAccessMatrixQueries dataAccessMatrixQueries,
                                    final boolean initNow,
                                    final TumorDetailsService tumorDetailsService,
                                    final HomePageStatsService homePageStatsService,
                                    final DataSorterAndGapFillerI dataSetSorterAndGapFiller,
                                    final String controlDiseaseAbbreviation) {
        this.enabled = enabled;
        this.dataSetSorterAndGapFiller = dataSetSorterAndGapFiller;
        this.dataAccessMatrixQueries = dataAccessMatrixQueries;
        this.controlDiseaseAbbreviation = controlDiseaseAbbreviation;
        this.tumorDetailsService = tumorDetailsService;
        this.homePageStatsService = homePageStatsService;

        if (enabled && initNow) {
            try {
                refreshAll();
            } catch (DataAccessMatrixQueries.DAMQueriesException e) {
                //no matter, it will try again first time a user hits the page
                (new ProcessLogger()).logError(e);
            }
        }
    }

    /**
     * Gets the current model for a given disease type.  If no instance yet exists, creates it.
     * Or, if force==true, will create a new one no matter what (for the "refresh" case).
     * Any existing objects that reference a DAMStaticModel will continue to do so,
     * even after the new one has been created.
     * This way, existing user sessions will not be broken when the DAM is refreshed.
     */
    public DAMModel getOrMakeModel(final String diseaseType,
                                   final boolean force)
            throws DataAccessMatrixQueries.DAMQueriesException {
        if (!enabled) {
            throw new IllegalStateException("StaticMatrixModelFactory is disabled");
        }
        DAMStaticModel model = DAMStaticModel.getInstance(diseaseType);
        if (model == null || force) {
            model = makeModel(diseaseType);
        }
        return model;
    }

    private final Object modelLock = new Object();

    private DAMStaticModel makeModel(final String diseaseType) throws DataAccessMatrixQueries.DAMQueriesException {
        return makeModel(diseaseType, getNonControlActiveDiseaseAbbreviations());
    }

    private DAMStaticModel makeModel(final String diseaseType, final List<String> allDiseaseTypes) throws DataAccessMatrixQueries.DAMQueriesException {
        synchronized (modelLock) {
            DiseaseContextHolder.setDisease(diseaseType);
            DAMStaticModel model = null;
            List<DataSet> dataSets;
            if (diseaseType.equals(controlDiseaseAbbreviation)) {
                dataSets = dataAccessMatrixQueries.getDataSetsForControls(allDiseaseTypes);
            } else {
                dataSets = dataAccessMatrixQueries.getDataSetsForDiseaseType(diseaseType);
            }

            if (dataSets != null) {
                model = DAMStaticModel.createInstance(diseaseType, dataSets, dataSetSorterAndGapFiller);
                CachedOutputManager.registerColumnSizes(model);
            }
            return model;
        }
    }

    /**
     * Updates static models for all
     *
     * @throws DataAccessMatrixQueries.DAMQueriesException
     *
     */
    public synchronized  void refreshAll() throws DataAccessMatrixQueries.DAMQueriesException {
        if (!enabled) {
            throw new IllegalStateException("StaticMatrixModelFactory is disabled");
        }
        TumorNormalClassifier.clearSampleTypeMap();
        FilterRequestValidatorImpl.clearCaches();

        final long startTime = System.currentTimeMillis();

        final List<String> nonControlActiveDiseases = getNonControlActiveDiseaseAbbreviations();

        for (final String diseaseAbbreviation : nonControlActiveDiseases) {
            makeModel(diseaseAbbreviation, nonControlActiveDiseases);
        }

        // add control model
        if (controlDiseaseAbbreviation != null && getActiveDiseaseAbbreviations(true).contains(controlDiseaseAbbreviation)) {
            // make control model using non-control diseases, because control schema does not have complete data tables
            makeModel(controlDiseaseAbbreviation, nonControlActiveDiseases);
        }

        logger.logToLogger(Level.INFO, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        logger.logToLogger(Level.INFO, "Time to create static model " + ((System.currentTimeMillis() - startTime) / 1000) + " secs");
        logger.logToLogger(Level.INFO, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

        tumorDetailsService.calculateAndSaveTumorDataTypeCounts();
        try {
            homePageStatsService.populateTable();
        } catch (HomePageStatsServiceImpl.HomePageStatsServiceException e) {
            throw new DataAccessMatrixQueries.DAMQueriesException(e.getMessage(), e);
        }
    }

    /**
     * Method extracted for ease of testing
     *
     * @return list of active Diseases
     */
    protected List<Disease> getActiveDiseases() {
        return DAMUtils.getInstance().getActiveDiseases();
    }

    private List<String> getActiveDiseaseAbbreviations(boolean includeControlDisease) {
         final List<Disease> activeDiseases = getActiveDiseases();
        final List<String> diseaseAbbreviations = new ArrayList<String>();
        for (final Disease disease : activeDiseases) {
            if (includeControlDisease || !disease.getAbbreviation().equals(controlDiseaseAbbreviation)) {
                diseaseAbbreviations.add(disease.getAbbreviation());
            }
        }
        return diseaseAbbreviations;
    }

    private List<String> getNonControlActiveDiseaseAbbreviations() {
        return getActiveDiseaseAbbreviations(false);
    }

    /**
     * Method extracted for ease of testing
     *
     * @param abbrev the disease abbreviation
     * @return the Disease for the abbreviation
     */
    protected Disease getDisease(String abbrev) {
        return DAMUtils.getInstance().getDisease(abbrev);
    }

    public void setDataSetSorterAndGapFiller(final DataSorterAndGapFillerI dataSetSorterAndGapFiller) {
        this.dataSetSorterAndGapFiller = dataSetSorterAndGapFiller;
    }
}
