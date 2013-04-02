/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.dao;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.AliquotIdBreakdown;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.jdbc.AliquotIdBreakdownReportDAOImpl;

import java.util.List;

/**
 * Test class for the aliquotId breakdown report dao layer
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class AliquotIdBreakdownReportDAOImplSlowTest extends DatareportDBUnitConfig {

    public void testGetAliquotIdBreakdownRows() {
        AliquotIdBreakdownReportDAO impl = new AliquotIdBreakdownReportDAOImpl();
        impl.setDataSource(getDataSource());
        List<AliquotIdBreakdown> allRows = impl.getAliquotIdBreakdown();
        assertNotNull(allRows);
        assertEquals(17, allRows.size());
        assertEquals("TCGA-01-0138-01A-01R-0231-06",allRows.get(0).getAliquotId());
    }

}//End of Class
