/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcLiveStateBean;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.clinical.ClinicalLoaderException;

import java.util.List;

/**
 * An interface for loading archives to a db
 *
 * @author Stanley Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface ArchiveLoader {

    enum ArchiveLoaderType {
        CLINICAL_LOADER,
        LEVEL_THREE_LOADER
    }

    /**
     * Loads archives to the db
     *
     * @param archivesToLoad a list of archives to load
     */
    public void load(final List<Archive> archivesToLoad, QcLiveStateBean stateContext) throws ClinicalLoaderException;

    /**
     * Returns archive type
     *
     * @return archive type
     */
    public ArchiveLoaderType getLoaderType();
}
