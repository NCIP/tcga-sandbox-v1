/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.loader.bamloader;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamXmlResult;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamXmlResultSet;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Test class for the Bam Xml File parser.
 *
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BamParserFastTest {

    private final String SAMPLES_DIR = Thread.currentThread().getContextClassLoader().getResource("samples")
            .getPath() + File.separator;
    ;
    private final String BAM_LOADER_DIR = SAMPLES_DIR + "qclive/bamloader";
    private BAMParser parser;

    @Before
    public void setUp() throws Exception {
        parser = new BAMParser();
    }

    @Test
    public void testParser() throws Exception {
        final BamXmlResultSet bamXmlResultSet = parser.parse(BAM_LOADER_DIR + "/CGHubSample.xml");
        assertNotNull(bamXmlResultSet);
        assertNotNull(bamXmlResultSet.getBamXmlResultList());
        final List<BamXmlResult> bamXmlResults = bamXmlResultSet.getBamXmlResultList();
        assertEquals(5, bamXmlResults.size());
        assertEquals("6e53c5fc-7303-4839-a09d-4c23e517d274", bamXmlResults.get(0).getAnalysisId());
        assertEquals("2182ce2c-5941-4b65-9419-fc7966d5e6d5", bamXmlResults.get(0).getAliquotUUID());
        assertEquals("D", bamXmlResults.get(0).getAnalyteCode());
        assertEquals("live", bamXmlResults.get(0).getState());
        assertEquals("COAD", bamXmlResults.get(0).getDisease());
        assertEquals("WGS", bamXmlResults.get(0).getLibraryStrategy());
        assertEquals("BCM", bamXmlResults.get(0).getCenter());
        assertEquals(2, bamXmlResults.get(0).getBamXmlFileRefList().size());
        assertEquals("TCGA-A6-6781-01A-22D-1924-10_wgs_Illumina.bam",
                bamXmlResults.get(0).getBamXmlFileRefList().get(0).getFileName());
        assertEquals("TCGA-A6-6781-01A-22D-1924-10_wgs_Illumina.bam.bai",
                bamXmlResults.get(0).getBamXmlFileRefList().get(1).getFileName());
        assertEquals(1, bamXmlResults.get(1).getBamXmlFileRefList().size());
        assertEquals("TCGA-AA-3516-01A-02D-1554-10_wgs_Illumina.bam",
                bamXmlResults.get(1).getBamXmlFileRefList().get(0).getFileName());
        assertEquals("123450", bamXmlResults.get(1).getBamXmlFileRefList().get(0).getFileSize());
    }

}//End of Class
