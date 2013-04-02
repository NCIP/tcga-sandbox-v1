/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.bamloader;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.DatareportDBUnitConfig;

import java.util.List;

/**
 * Test class for lookupForBAM
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class LookupForBAMSlowTest extends DatareportDBUnitConfig {

    LookupForBAMImpl impl = new LookupForBAMImpl();

    public void testGetCenters() throws Exception {
        impl.setDataSource(getDataSource());
        impl.setupQueries();
        List<CenterShort> centerList = impl.getCenters();
        assertNotNull(centerList);
        assertEquals(10, centerList.size());
        assertEquals(new Integer(9), centerList.get(0).getCenterId());
        assertEquals("BCR", centerList.get(1).getShortName());
    }

    public void testGetAliquots() throws Exception {
        impl.setDataSource(getDataSource());
        impl.setupQueries();
        List<AliquotShort> aliquotList = impl.getAliquots();
        assertNotNull(aliquotList);
        assertEquals(17, aliquotList.size());
        assertEquals(new Long(70), aliquotList.get(0).getAliquotId());
        assertEquals("TCGA-01-0610-01A-01T-0364-07", aliquotList.get(1).getBarcode());
    }

    public void testGetDatatypeBAMs() throws Exception {
        impl.setDataSource(getDataSource());
        impl.setupQueries();
        List<BAMDatatype> bamDatatypeList = impl.getDatatypeBAMs();
        assertNotNull(bamDatatypeList);
        assertEquals(3, bamDatatypeList.size());
        assertEquals(new Integer(1), bamDatatypeList.get(0).getDatatypeBAMId());
        assertEquals("miRNA", bamDatatypeList.get(1).getGeneralDatatype());
    }

    public void testGetDiseases() throws Exception {
        impl.setDataSource(getDataSource());
        impl.setupQueries();
        List<Tumor> diseaseList = impl.getDiseases();
        assertNotNull(diseaseList);
        assertEquals(8, diseaseList.size());
        assertEquals(new Integer(5), diseaseList.get(0).getTumorId());
        assertEquals("BRLC", diseaseList.get(1).getTumorName());
    }

}//End of Class
