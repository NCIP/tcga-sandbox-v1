
/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.common.bean;

import gov.nih.nci.ncicb.tcga.dcc.common.InvalidMetadataException;
import org.junit.Before;
import org.junit.Test;
import static junit.framework.Assert.assertEquals;
/**
 * Test class for MetaData Bean
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class MetaDataBeanFastTest {

    private MetaDataBean metaDataBean;

    @Before
    public void setup() {
        metaDataBean = new MetaDataBean();
        metaDataBean.setProjectCode("TCGA");
        metaDataBean.setTssCode("02");
        metaDataBean.setParticipantCode("0001");
        metaDataBean.setSampleCode("01");
        metaDataBean.setVial("C");
        metaDataBean.setPortionCode("01");
        metaDataBean.setAnalyteCode("D");
        metaDataBean.setPlateId("0182");
        metaDataBean.setReceivingCenterId("01");

    }


    @Test
    public void getPatientBuiltBarcode() throws Exception{
        assertEquals("TCGA-02-0001",metaDataBean.getPatientBuiltBarcode());
    }

    @Test(expected = InvalidMetadataException.class)
    public void getMissingPatientBuiltBarcode() throws Exception {
        metaDataBean.setParticipantCode(null);
        metaDataBean.getPatientBuiltBarcode();
    }


    @Test
    public void getSampleBuiltBarcode() throws Exception{
        assertEquals("TCGA-02-0001-01",metaDataBean.getSampleBuiltBarcode());
    }

    @Test(expected = InvalidMetadataException.class)
    public void getMissingSampleBuiltBarcode() throws Exception{
        metaDataBean.setSampleCode(null);
        metaDataBean.getSampleBuiltBarcode();
    }

    @Test
    public void getPortionBuiltBarcode() {
        assertEquals("TCGA-02-0001-01C-01",metaDataBean.getPortionBuiltBarcode());
    }

    @Test(expected = InvalidMetadataException.class)
    public void getMissingPortionBuiltBarcode() {
        metaDataBean.setPortionCode("");
        metaDataBean.getPortionBuiltBarcode();
    }

    @Test
    public void getAnalyteBuiltBarcode() {
        assertEquals("TCGA-02-0001-01C-01D",metaDataBean.getAnalyteBuiltBarcode());
    }

    @Test(expected = InvalidMetadataException.class)
    public void getMissingAnalyteBuiltBarcode() {
        metaDataBean.setAnalyteCode("");
        metaDataBean.getAnalyteBuiltBarcode();
    }


    @Test
    public void getAliquotBuiltBarcode() {
        assertEquals("TCGA-02-0001-01C-01D-0182-01",metaDataBean.getAliquotBuiltBarcode());
    }

    @Test(expected = InvalidMetadataException.class)
    public void getMissingAliquotBuiltBarcode() {
        metaDataBean.setParticipantCode(null);
        metaDataBean.getAliquotBuiltBarcode();
    }

    @Test
    public void getShippedPortionBuiltBarcode() {
        assertEquals("TCGA-02-0001-01C-01-0182-01",metaDataBean.getShippedPortionBuiltBarcode());
    }

    @Test(expected = InvalidMetadataException.class)
    public void getMissingShippedPortionBuiltBarcode() {
        metaDataBean.setParticipantCode("");
        metaDataBean.getShippedPortionBuiltBarcode();
    }


}
