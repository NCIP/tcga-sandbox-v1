/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.dao;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Aliquot;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.AliquotArchive;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.jdbc.AliquotReportDAOImpl;

import java.util.List;

/**
 * Test the biospecimen dao implementation class
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class AliquotReportDAOImplSlowTest extends DatareportDBUnitConfig {

    public void testGetAliquotRows() {
        AliquotReportDAO impl = new AliquotReportDAOImpl();
        impl.setDataSource(getDataSource());
        List<Aliquot> allRows = impl.getAliquotRows();
        assertNotNull(allRows);
        assertEquals(17, allRows.size());
    }

    public void testGetAliquotArchive() {
        AliquotReportDAO impl = new AliquotReportDAOImpl();
        impl.setDataSource(getDataSource());
        List<AliquotArchive> emptyList = impl.getAliquotArchive("",1);
        assertNotNull(emptyList);
        assertEquals(0,emptyList.size());
        List<AliquotArchive> archiveList = impl.getAliquotArchive("TCGA-01-0610-01A-01T-0364-07",1);
        assertNotNull(archiveList);
        assertEquals("mskcc.org_OV.HG-CGH-244A.9.2.0",archiveList.get(0).getArchiveName());
        assertEquals(10,archiveList.get(0).getFileId());
        assertEquals("hudsonalpha.org_GBM.HumanHap550.9.1.58.domi10.idf.txt",
                archiveList.get(0).getFileName());
        assertEquals(10,archiveList.get(0).getFileId());
    }
}//End of Class
