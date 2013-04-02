/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;

import java.util.List;
import java.util.Map;

/**
 * Query interface for experiment objects.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface ExperimentQueries {

    /**
     * Get the experiment object with the given name, creating all Archive objects that are part of it.
     * Includes all archives for this experiment that are either Available with is_latest=1, or
     * Uploaded or Validated.
     *
     * If the experiment name is an archive name, the experiment will contain only that archive.
     *
     * @param experimentName the name of the experiment to get
     * @return the Experiment object or null if no such experiment
     */
    public Experiment getExperiment(String experimentName);

    /**
     * Get an experiment object for a single archive. Will only include the archive of the given name.
     *
     * @param archiveName the name of the archive
     * @return Experiment object or null if no such archive
     */
    public Experiment getExperimentForSingleArchive(String archiveName);

    /**
     * Get a map of all data files for all latest archives in the given experiment.
     *
     * @param experimentName the experiment name (center_disease.platform)
     * @return map of Archive and FileInfo Lists for the experiment
     */
    public Map<Archive, List<FileInfo>> getExperimentDataFiles(String experimentName);
}
