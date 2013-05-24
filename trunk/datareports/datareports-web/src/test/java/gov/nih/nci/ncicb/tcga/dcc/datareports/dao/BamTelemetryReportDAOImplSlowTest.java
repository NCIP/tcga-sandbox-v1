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
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.jdbc.BamTelemetryReportDAOImpl;

import java.util.List;

/**
 * test class for the dao implementation of the bam telemetry report
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class BamTelemetryReportDAOImplSlowTest extends DatareportDBUnitConfig {

    public void testGetBamTelemetryRows() {
        BamTelemetryReportDAO impl = new BamTelemetryReportDAOImpl();
        impl.setDataSource(getDataSource());
        List<BamTelemetry> allRows = impl.getBamTelemetryRows();
        assertNotNull(allRows);
        assertEquals(3, allRows.size());
        assertEquals("OV", allRows.get(0).getDisease());
        assertEquals("hudsonalpha.org (CGCC)", allRows.get(0).getCenter());
        assertEquals("bamfilename1", allRows.get(1).getBamFile());
        assertEquals(new Long(12345), allRows.get(1).getFileSize());
        assertEquals("miRNA", allRows.get(0).getDataType());
        assertEquals("D", allRows.get(1).getAnalyteCode());
        assertEquals("454", allRows.get(1).getLibraryStrategy());
        assertEquals("TCGA-59-2362-10A-01D-0704-01", allRows.get(2).getAliquotId());
        assertEquals("TCGA-59-2362", allRows.get(2).getParticipantId());
        assertEquals("TCGA-59-2362-10A", allRows.get(2).getSampleId());
        assertEquals("d88b35a3-a291-457a-b15b-a314859b25c6", allRows.get(2).getAliquotUUID());
    }

}//End of class
