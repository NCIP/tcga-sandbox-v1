/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamTelemetry;

import javax.sql.DataSource;
import java.util.List;

/**
 * Bam telemetry report dao layer
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface BamTelemetryReportDAO {

    public void setDataSource(DataSource dataSource);

    /**
     * A query to get all of the rows in the BamTelemetry table
     *
     * @return a series of rows to populate the table
     */
    public List<BamTelemetry> getBamTelemetryRows();

}
