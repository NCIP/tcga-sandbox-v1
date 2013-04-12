/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.MafInfo;

/**
 * Interface for MAF queries
 *
 * @author fengla
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface MafInfoQueries {

    public Long addMaf( MafInfo theMaf );

    public boolean fileIdExistsInMafInfo(Long mafFileId);

    public void deleteMafInfoForFileId(Long mafFileId);

}