/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Barcode;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileInfoQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ShippedBiospecimenQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BarcodeUuidResolver;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.BCRDataService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Unit test for VcfProcessor
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class VcfProcessorFastTest {

    private static final String SAMPLE_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator + "qclive" + File.separator + "vcfProcessor";

    private Mockery context = new JUnit4Mockery();
    private VcfProcessor vcfProcessor;
    private BarcodeUuidResolver mockBarcodeUuidResolver;
    private ShippedBiospecimenQueries mockShippedBiospecimenQueries;
    private BCRDataService mockBcrDataService;
    private FileInfoQueries mockFileInfoQueries;
    private CommonBarcodeAndUUIDValidator mockBarcodeAndUUIDValidator;
    private QcContext qcContext;
    private Center center;
    private Tumor disease;

    @Before
    public void setup() {
        vcfProcessor = new VcfProcessor();
        mockBarcodeUuidResolver = context.mock(BarcodeUuidResolver.class);
        vcfProcessor.setBarcodeUuidResolver(mockBarcodeUuidResolver);
        mockShippedBiospecimenQueries = context.mock(ShippedBiospecimenQueries.class);
        vcfProcessor.setShippedBiospecimenQueries(mockShippedBiospecimenQueries);
        mockBcrDataService = context.mock(BCRDataService.class);
        vcfProcessor.setBcrDataService(mockBcrDataService);
        mockFileInfoQueries = context.mock(FileInfoQueries.class);
        vcfProcessor.setFileInfoQueries(mockFileInfoQueries);
        mockBarcodeAndUUIDValidator = context.mock(CommonBarcodeAndUUIDValidator.class);
        vcfProcessor.setBarcodeAndUUIDValidator(mockBarcodeAndUUIDValidator);
        qcContext = new QcContext();
        final Archive archive = new Archive();
        center = new Center();
        disease = new Tumor();
        archive.setTheCenter(center);
        archive.setTheTumor(disease);
        archive.setId(567L);
        qcContext.setArchive(archive);
    }

    @Test
    public void testProcessFileSampleNameBarcode() throws Processor.ProcessorException, UUIDException {
        final Barcode barcode = new Barcode();
        barcode.setUuid("the-uuid");
        final List<Long> biospecimenIds = Arrays.asList(123L);
        context.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidator).validateUUIDFormat("TCGA-AA-BBBB-01A-01B-CCCC-DD");
            will(returnValue(false));

            one(mockBarcodeUuidResolver).resolveBarcodeAndUuid("TCGA-AA-BBBB-01A-01B-CCCC-DD", null,
                    disease, center, false);
            will(returnValue(barcode));

            one(mockShippedBiospecimenQueries).getShippedBiospecimenIds(Arrays.asList("the-uuid"));
            will(returnValue(biospecimenIds));

            one(mockFileInfoQueries).getFileId("testSampleNameBarcode.vcf", 567L);
            will(returnValue(9L));

            one(mockBcrDataService).addShippedBiospecimensFileRelationship(biospecimenIds, 9L);
        }});
        vcfProcessor.processFile(new File(SAMPLE_DIR, "testSampleNameBarcode.vcf"), qcContext);
    }

    @Test
    public void testProcessFileSampleNameUuid() throws Processor.ProcessorException {
        // SAMPLE has SampleName that's a UUID
        final List<Long> biospecimenIds = Arrays.asList(56L);
        context.checking(new Expectations() {{
            one(mockShippedBiospecimenQueries).getShippedBiospecimenIds(Arrays.asList("12345678-1111-2222-3333-4444-abcdefabcdef"));
            will(returnValue(biospecimenIds));

            one(mockBarcodeAndUUIDValidator).validateUUIDFormat("12345678-1111-2222-3333-4444-abcdefabcdef");
            will(returnValue(true));

            one(mockFileInfoQueries).getFileId("testSampleNameUuid.vcf", 567L);
            will(returnValue(159L));
            
            one(mockBcrDataService).addShippedBiospecimensFileRelationship(biospecimenIds, 159L);
        }});
        vcfProcessor.processFile(new File(SAMPLE_DIR, "testSampleNameUuid.vcf"), qcContext);

    }

    @Test
    public void testProcessFileSampleUuid() throws Processor.ProcessorException {
        // sample has SampleUUID field

        final List<Long> biospecimenIds = Arrays.asList(88L);
        context.checking(new Expectations() {{
            one(mockShippedBiospecimenQueries).getShippedBiospecimenIds(Arrays.asList("87654321-4444-3333-2222-1111-fedcbafedcba"));
            will(returnValue(biospecimenIds));

            one(mockFileInfoQueries).getFileId("testSampleUuid.vcf", 567L);
            will(returnValue(53L));

            one(mockBcrDataService).addShippedBiospecimensFileRelationship(biospecimenIds, 53L);
        }});

        vcfProcessor.processFile(new File(SAMPLE_DIR, "testSampleUuid.vcf"), qcContext);
    }

    @Test
    public void testProcessFileNoSample() throws Processor.ProcessorException {
        // no sample header
       
        vcfProcessor.processFile(new File(SAMPLE_DIR, "noSampleHeader.vcf"), qcContext);
        assertEquals("Unable to add relationship between VCF file noSampleHeader.vcf and aliquots", qcContext.getWarnings().get(0));

    }

    @Test (expected = Processor.ProcessorException.class)
    public void testProcessFileSampleNameException() throws UUIDException, Processor.ProcessorException {
        // case where SampleName is barcode and no UUID found so resolver throws exception
        final Barcode barcode = new Barcode();
        barcode.setUuid("the-uuid");
        context.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidator).validateUUIDFormat("TCGA-AA-BBBB-01A-01B-CCCC-DD");
            will(returnValue(false));

            one(mockBarcodeUuidResolver).resolveBarcodeAndUuid("TCGA-AA-BBBB-01A-01B-CCCC-DD", null,
                    disease, center, false);
            will(throwException(new UUIDException("no uuid found for barcode")));

        }});
        vcfProcessor.processFile(new File(SAMPLE_DIR, "testSampleNameBarcode.vcf"), qcContext);
    }

}
