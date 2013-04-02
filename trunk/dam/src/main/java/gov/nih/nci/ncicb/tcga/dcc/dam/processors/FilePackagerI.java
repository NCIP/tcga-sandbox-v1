/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.processors;

import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.FilePackagerBean;

import java.io.File;

/**
 * TODO: INFO ABOUT CLASS
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public interface FilePackagerI {
    void runJob(FilePackagerBean filePackagerBean) throws Exception;

    void setLogger(ProcessLogger logger);

    FilePackagerBean getFilePackagerBean();

    File getCompressedArchive();

    long getActualUncompressedSize();

    long getFileProcessingTime();

    long getArchiveGenerationTime();

    long getTotalTime();

    long getWaitingInQueueTime();
}
