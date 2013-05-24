/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.loader.bamloader;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamContext;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamXmlFileRef;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamXmlResult;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamXmlResultSet;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.BAMFileQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CodeTableQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.TumorQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test class for the Bam Xml File validator.
 *
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class BamValidatorFastTest {

    private final Mockery mockery = new JUnit4Mockery();
    private QcLiveBarcodeAndUUIDValidator mockQcLiveBarcodeAndUUIDValidator;
    private CodeTableQueries mockCodeTableQueries;
    private TumorQueries mockTumorQueries;
    private BAMFileQueries mockBamFileQueries;
    private BAMValidator bamValidator;
    private BamContext bamContext;
    private Tumor tumor;

    @Before
    public void setUp() throws Exception {
        mockQcLiveBarcodeAndUUIDValidator = mockery.mock(QcLiveBarcodeAndUUIDValidator.class);
        mockCodeTableQueries = mockery.mock(CodeTableQueries.class);
        mockTumorQueries = mockery.mock(TumorQueries.class);
        mockBamFileQueries = mockery.mock(BAMFileQueries.class);
        bamValidator = new BAMValidator();
        bamContext = new BamContext();
        bamValidator.setCodeTableQueries(mockCodeTableQueries);
        bamValidator.setQcLiveBarcodeAndUUIDValidator(mockQcLiveBarcodeAndUUIDValidator);
        bamValidator.setTumorQueries(mockTumorQueries);
        bamValidator.setBamFileQueries(mockBamFileQueries);
        tumor = new Tumor();
        tumor.setTumorId(1);
    }

    @Test
    public void testValidateValid() throws Exception {
        mockery.checking(new Expectations() {{
            allowing(mockCodeTableQueries).portionAnalyteExists("D");
            will(returnValue(true));
            allowing(mockCodeTableQueries).portionAnalyteExists("Z");
            will(returnValue(true));
            allowing(mockTumorQueries).getTumorForName("GBM");
            will(returnValue(tumor));
            allowing(mockTumorQueries).getTumorForName("Flu");
            will(returnValue(tumor));
            allowing(mockBamFileQueries).getDCCCenterId("BI");
            will(returnValue(1L));
            allowing(mockBamFileQueries).getDCCCenterId("IGC");
            will(returnValue(2L));
            allowing(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat("uuid1");
            will(returnValue(true));
            allowing(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat("uuid2");
            will(returnValue(true));
        }});
        BamXmlResultSet mockBam = makeBamXmlResultSet("IGC");
        assertEquals(2, mockBam.getBamXmlResultList().size());
        bamValidator.validate(mockBam, bamContext);
        assertNotNull(mockBam);
        assertNotNull(mockBam.getBamXmlResultList());
        assertEquals(2, mockBam.getBamXmlResultList().size());
        assertEquals("1", mockBam.getBamXmlResultList().get(0).getAnalysisId());
        assertEquals("2", mockBam.getBamXmlResultList().get(1).getAnalysisId());
        assertEquals(0, bamContext.getErrorList().size());
        assertEquals(0, bamContext.getWarningList().size());
    }

    @Test
    public void testValidateValidWithWarning() throws Exception {
        mockery.checking(new Expectations() {{
            allowing(mockCodeTableQueries).portionAnalyteExists("D");
            will(returnValue(true));
            allowing(mockCodeTableQueries).portionAnalyteExists("Z");
            will(returnValue(true));
            allowing(mockTumorQueries).getTumorForName("GBM");
            will(returnValue(tumor));
            allowing(mockTumorQueries).getTumorForName("Flu");
            will(returnValue(tumor));
            allowing(mockBamFileQueries).getDCCCenterId("BI");
            will(returnValue(1L));
            allowing(mockBamFileQueries).getDCCCenterId("IGC");
            will(returnValue(2L));
            allowing(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat("uuid1");
            will(returnValue(true));
            allowing(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat("uuid2");
            will(returnValue(true));
        }});
        BamXmlResultSet mockBam = makeBamXmlResultSetBlank("IGC");
        assertEquals(2, mockBam.getBamXmlResultList().size());
        bamValidator.validate(mockBam, bamContext);
        assertNotNull(mockBam);
        assertNotNull(mockBam.getBamXmlResultList());
        assertEquals(2, mockBam.getBamXmlResultList().size());
        assertEquals("1", mockBam.getBamXmlResultList().get(0).getAnalysisId());
        assertEquals("2", mockBam.getBamXmlResultList().get(1).getAnalysisId());
        assertEquals(0, bamContext.getErrorList().size());
        assertEquals(1, bamContext.getWarningList().size());
        assertEquals("BAM Validation Warning: Analyte Code is blank for analysis Id: 2",
                bamContext.getWarningList().get(0));
    }

    @Test
    public void testValidateInvalid() throws Exception {
        mockery.checking(new Expectations() {{
            allowing(mockCodeTableQueries).portionAnalyteExists("D");
            will(returnValue(true));
            allowing(mockCodeTableQueries).portionAnalyteExists("Z");
            will(returnValue(false));
            allowing(mockTumorQueries).getTumorForName("GBM");
            will(returnValue(tumor));
            allowing(mockTumorQueries).getTumorForName("Flu");
            will(returnValue(null));
            allowing(mockBamFileQueries).getDCCCenterId("BI");
            will(returnValue(1L));
            allowing(mockBamFileQueries).getDCCCenterId("JHU");
            will(returnValue(0L));
            allowing(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat("uuid1");
            will(returnValue(true));
            allowing(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat("uuid2");
            will(returnValue(false));
        }});
        BamXmlResultSet mockBam = makeBamXmlResultSet("JHU");
        assertEquals(2, mockBam.getBamXmlResultList().size());
        bamValidator.validate(mockBam, bamContext);
        assertNotNull(mockBam);
        assertNotNull(mockBam.getBamXmlResultList());
        assertEquals(1, mockBam.getBamXmlResultList().size());
        assertEquals("1", mockBam.getBamXmlResultList().get(0).getAnalysisId());
        assertEquals("1", mockBam.getBamXmlResultList().get(0).getDisease());
        assertEquals(4, bamContext.getErrorList().size());
        assertEquals("BAM Validation Error: Disease 'Flu' is invalid for analysis Id: 2",
                bamContext.getErrorList().get(0));
        assertEquals("BAM Validation Error: Aliquot UUID 'uuid2' is invalid for analysis Id: 2",
                bamContext.getErrorList().get(1));
        assertEquals("BAM Validation Error: Analyte Code 'Z' is invalid for analysis Id: 2",
                bamContext.getErrorList().get(2));
        assertEquals("BAM Validation Error: Center 'JHU' is invalid for analysis Id: 2",
                bamContext.getErrorList().get(3));
    }

    @Test
    public void testValidateInvalidMultipleFiles() throws Exception {
        mockery.checking(new Expectations() {{
            allowing(mockCodeTableQueries).portionAnalyteExists("D");
            will(returnValue(true));
            allowing(mockCodeTableQueries).portionAnalyteExists("Z");
            will(returnValue(true));
            allowing(mockTumorQueries).getTumorForName("GBM");
            will(returnValue(tumor));
            allowing(mockTumorQueries).getTumorForName("Flu");
            will(returnValue(null));
            allowing(mockBamFileQueries).getDCCCenterId("BI");
            will(returnValue(1L));
            allowing(mockBamFileQueries).getDCCCenterId("IGC");
            will(returnValue(2L));
            allowing(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat("uuid1");
            will(returnValue(true));
            allowing(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat("uuid2");
            will(returnValue(true));
        }});
        BamXmlResultSet mockBam = makeBamXmlResultSetMultipleFiles("IGC");
        assertEquals(2, mockBam.getBamXmlResultList().size());
        bamValidator.validate(mockBam, bamContext);
        assertNotNull(mockBam);
        assertNotNull(mockBam.getBamXmlResultList());
        assertEquals(1, mockBam.getBamXmlResultList().size());
        assertEquals("1", mockBam.getBamXmlResultList().get(0).getAnalysisId());
        assertEquals("1", mockBam.getBamXmlResultList().get(0).getDisease());
        assertEquals(2, bamContext.getErrorList().size());
        assertEquals("BAM Validation Error: Disease 'Flu' is invalid for analysis Id: 2",
                bamContext.getErrorList().get(0));
        assertEquals("BAM Validation Error: More than 1 file element found for analysis Id: 2",
                bamContext.getErrorList().get(1));
    }

    private BamXmlResultSet makeBamXmlResultSet(String center) {
        BamXmlResultSet bamXmlResultSet = new BamXmlResultSet();
        List<BamXmlResult> bamXmlResultList = new LinkedList<BamXmlResult>();
        bamXmlResultList.add(makeBamXmlResult("1", "GBM", "D", "uuid1", "BI"));
        bamXmlResultList.add(makeBamXmlResult("2", "Flu", "Z", "uuid2", center));
        bamXmlResultSet.setBamXmlResultList(bamXmlResultList);
        return bamXmlResultSet;
    }

    private BamXmlResultSet makeBamXmlResultSetBlank(String center) {
        BamXmlResultSet bamXmlResultSet = new BamXmlResultSet();
        List<BamXmlResult> bamXmlResultList = new LinkedList<BamXmlResult>();
        bamXmlResultList.add(makeBamXmlResult("1", "GBM", "D", "uuid1", "BI"));
        bamXmlResultList.add(makeBamXmlResult("2", "GBM", null, "uuid2", center));
        bamXmlResultSet.setBamXmlResultList(bamXmlResultList);
        return bamXmlResultSet;
    }

    private BamXmlResultSet makeBamXmlResultSetMultipleFiles(String center) {
        BamXmlResultSet bamXmlResultSet = new BamXmlResultSet();
        List<BamXmlResult> bamXmlResultList = new LinkedList<BamXmlResult>();
        bamXmlResultList.add(makeBamXmlResult("1", "GBM", "D", "uuid1", "BI"));
        bamXmlResultList.add(makeBamXmlResultMultipleFile("2", "Flu", null, "uuid2", center));
        bamXmlResultSet.setBamXmlResultList(bamXmlResultList);
        return bamXmlResultSet;
    }

    private BamXmlResult makeBamXmlResult(String id, String disease, String analyte, String uuid,
                                          String center) {
        BamXmlResult bam = new BamXmlResult();
        bam.setAnalysisId(id);
        bam.setDisease(disease);
        bam.setAnalyteCode(analyte);
        bam.setAliquotUUID(uuid);
        bam.setCenter(center);
        bam.setBamXmlFileRefList(new LinkedList<BamXmlFileRef>() {{
            add(new BamXmlFileRef());
        }});
        return bam;
    }

    private BamXmlResult makeBamXmlResultMultipleFile(String id, String disease, String analyte,
                                                      String uuid, String center) {
        BamXmlResult bam = new BamXmlResult();
        bam.setAnalysisId(id);
        bam.setDisease(disease);
        bam.setAnalyteCode(analyte);
        bam.setAliquotUUID(uuid);
        bam.setCenter(center);
        bam.setBamXmlFileRefList(new LinkedList<BamXmlFileRef>() {{
            add(new BamXmlFileRef());
            add(new BamXmlFileRef());
        }});
        return bam;
    }

}//End of class
