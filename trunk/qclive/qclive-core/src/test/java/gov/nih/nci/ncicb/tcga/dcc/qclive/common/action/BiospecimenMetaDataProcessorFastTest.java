/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Barcode;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.BiospecimenMetaData;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ControlQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDHierarchyQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.JAXBUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.UnmarshalResult;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.Control;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.Controls;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.TcgaBcr;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BarcodeUuidResolver;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Test class for testing the functionality of the {@link BiospecimenMetaDataProcessor}.
 *
 * @author Matt Nicholls
 *         Last updated by: nichollsmc
 */
@RunWith(JMock.class)
public class BiospecimenMetaDataProcessorFastTest {

    private static final String SAMPLES_DIR =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;

    final File bcrBiospecimenXMLFile = new File(SAMPLES_DIR +
            "qclive" + File.separator +
            "biospecimenXmlProcessor" + File.separator +
            "nationwidechildrens.org_biospecimen.TCGA-00-0000.xml");

    final File bcrShipmentPortionXMLFile = new File(SAMPLES_DIR +
            "qclive" + File.separator +
            "clinicalXmlValidator" + File.separator +
            "shippedPortionValidation" + File.separator +
            "shippedPortionGood_2.5.TCGA-00-0000.xml");

    final File bcrDiseaseSpecificXMLFile = new File(SAMPLES_DIR +
            "qclive" + File.separator +
            "biospecimenXmlProcessor" + File.separator +
            "intgen.org_clinical.drugs_radiations.xml");

    final File bcrBiospecimenXMLMissingUUIDs = new File(SAMPLES_DIR +
            "qclive" + File.separator +
            "biospecimenXmlProcessor" + File.separator +
            "nationwidechildrens.org_biospecimen.TCGA-BC-TNGO.xml");

    final File bcrClinicalXMLNoExaminationUUID = new File(SAMPLES_DIR +
            "qclive" + File.separator +
            "biospecimenXmlProcessor" + File.separator +
            "intgen.org_clinical.no_examination_uuid_element.xml");

    final File bcrClinicalXMLMissingUUIDs = new File(SAMPLES_DIR +
            "qclive" + File.separator +
            "biospecimenXmlProcessor" + File.separator +
            "intgen.org_clinical.drugs_radiations_missing_uuids.xml");

    final File bcrControlXMLFile = new File(SAMPLES_DIR +
            "qclive" + File.separator +
            "biospecimenXmlProcessor" + File.separator +
            "intgen.org_control.TCGA-AV-9999.xml");

    final File bcrControlHugeXMLFile = new File(SAMPLES_DIR +
            "qclive" + File.separator +
            "biospecimenXmlProcessor" + File.separator +
            "intgen.org_control.TCGA-07-0227.xml");

    private QcContext qcContext;
    private Tumor tumor;
    private Center center;
    private BiospecimenMetaDataProcessor biospecimenMetaDataProcessor;
    private final Mockery mocker = new JUnit4Mockery();
    private UUIDHierarchyQueries mockUUIDHierarchyQueries;
    private ControlQueries mockControlQueries;

    @SuppressWarnings("serial")
    private final Map<String, String> barcodeUUIDMap = new HashMap<String, String>() {{
        put("TCGA-00-0123-F2345", "042f0677-d967-41cc-90f3-e5bb1ffc8c5d");
        put("TCGA-AE-0123-T0123", "2ab8f28e-92bf-4160-ae90-c162f67d2ba1");
        put("TCGA-BC-TNGO", "cb73c2bc-423c-4a94-995e-666f24e0a13b");
        put("TCGA-BC-TNGO-01A", "49f94cd1-b696-4be3-a691-2b4449137eb6");
        put("TCGA-BC-TNGO-01A-11", "4e009ee4-da7a-4739-afa3-5906c44a22e9");
        put("TCGA-BC-TNGO-01A-11D", "e5235ef2-eb14-4d05-ad39-8e6e0f970faa");
        put("TCGA-BC-TNGO-01A-11D-A13V-01", "d5afd039-78fb-44c6-b4d8-bdf88bc14d0e");
        put("TCGA-BC-TNGO-01A-01-TS1", "9ee20938-03cc-49f6-a329-cf950555b3cd");
        put("TCGA-BC-TNGO-10A", "8bf56f14-7d7a-4bef-95e5-cbe4f04d0d84");
    }};

    @Before
    public void before() throws ParseException, UUIDException {
        qcContext = new QcContext();
        // Create the test archive
        Archive archive = new Archive();
        archive.setExperimentType(Experiment.TYPE_BCR);

        // Add tumor to test archive
        tumor = new Tumor();
        tumor.setTumorName("COAD");
        archive.setTheTumor(tumor);

        // Add center to test archive
        center = new Center();
        center.setCenterId(1);
        archive.setTheCenter(center);
        qcContext.setArchive(archive);

        // Create BiospecimenMetaDataProcessor, and set the necessary fields
        biospecimenMetaDataProcessor = new BiospecimenMetaDataProcessor();
        biospecimenMetaDataProcessor.setBarcodeUuidResolver(new BarcodeUuidResolverTestImpl());
        mockUUIDHierarchyQueries = mocker.mock(UUIDHierarchyQueries.class);
        biospecimenMetaDataProcessor.setUuidHierarchyQueries(mockUUIDHierarchyQueries);
        mockControlQueries = mocker.mock(ControlQueries.class);
        biospecimenMetaDataProcessor.setControlQueries(mockControlQueries);
        biospecimenMetaDataProcessor.setDiseaseControlQueries(mockControlQueries);
    }

    @Test
    public void testGetMetaDataForBiospecimenXML() throws ProcessorException, UUIDException {
        final Map<String, String> existingMetaData = new HashMap<String, String>();
        mocker.checking(new Expectations() {{
            oneOf(mockUUIDHierarchyQueries).getMetaData(with(any(List.class)));
            will(returnValue(existingMetaData));
            oneOf(mockUUIDHierarchyQueries).persistUUIDHierarchy(with(any(List.class)));
        }});
        final List<BiospecimenMetaData> biospecimenMetaData =
                biospecimenMetaDataProcessor.handleBiospecimenMetaData(bcrBiospecimenXMLFile, qcContext);
        assertNotNull(biospecimenMetaData);
        assertFalse(biospecimenMetaData.isEmpty());
        assertEquals(36, biospecimenMetaData.size());

        // Patient meta-data object should be the first element in the list
        assertEquals("TCGA-00-0000", biospecimenMetaData.get(0).getBarcode());

        // Slide meta-data object should be the last element in the list
        assertEquals("TCGA-00-0000-01A-01-TS1", biospecimenMetaData.get(biospecimenMetaData.size() - 1).getBarcode());
    }

    @Test
    public void testGetMetaDataForControlXML() throws ProcessorException, UUIDException {
        final Map<String, String> existingMetaData = new HashMap<String, String>();
        mocker.checking(new Expectations() {{
            oneOf(mockUUIDHierarchyQueries).getMetaData(with(any(List.class)));
            will(returnValue(existingMetaData));
            oneOf(mockUUIDHierarchyQueries).persistUUIDHierarchy(with(any(List.class)));
            exactly(5).of(mockControlQueries).persistControl(with(any(Control.class)));
            allowing(mockControlQueries).updateControlForShippedBiospecimen(with(any(Control.class)));
        }});
        final List<BiospecimenMetaData> biospecimenMetaData =
                biospecimenMetaDataProcessor.handleBiospecimenMetaData(bcrControlXMLFile, qcContext);
        assertNotNull(biospecimenMetaData);
        assertFalse(biospecimenMetaData.isEmpty());
        assertEquals(11, biospecimenMetaData.size());
        assertEquals("TCGA-AV-9999", biospecimenMetaData.get(0).getBarcode().trim());
        assertEquals("TCGA-AV-9999-20A-01D-1697-05", biospecimenMetaData.get(biospecimenMetaData.size() - 1).getBarcode());
        assertEquals("COAD", biospecimenMetaData.get(0).getDisease());

    }

    @Test
    public void testProcessControls() throws Exception {
        TcgaBcr tcgaBcr = null;
        try {
            UnmarshalResult unmarshalResult = JAXBUtil.unmarshal(bcrControlXMLFile, TcgaBcr.class, true, false);
            tcgaBcr = (TcgaBcr) unmarshalResult.getJaxbObject();
        } catch (Exception e) {
            //nothing to do
        }
        assertNotNull(tcgaBcr);
        final Controls controls = tcgaBcr.getControls();
        assertNotNull(controls);
        final List<Control> controlList = controls.getControl();
        assertNotNull(controlList);
        assertEquals(5, controlList.size());
        assertEquals("[BRCA, CESC, KIRP, GBM]", controlList.get(0)
                .getAliquotsToDiseases().getDiseaseCodeList().toString());
        assertEquals("[OV, GBM, BLCA]", controlList.get(1)
                .getAliquotsToDiseases().getDiseaseCodeList().toString());
        assertEquals("[BRCA, CESC, KIRP, GBM]", controlList.get(2)
                .getAliquotsToDiseases().getDiseaseCodeList().toString());
        assertEquals("[OV]", controlList.get(3)
                .getAliquotsToDiseases().getDiseaseCodeList().toString());
        assertEquals("[COAD, HNSC, LUAD]", controlList.get(4)
                .getAliquotsToDiseases().getDiseaseCodeList().toString());
    }

    @Test
    public void testProcessControlsHugeFile() throws Exception {
        TcgaBcr tcgaBcr = null;
        try {
            UnmarshalResult unmarshalResult = JAXBUtil.unmarshal(bcrControlHugeXMLFile, TcgaBcr.class, true, false);
            tcgaBcr = (TcgaBcr) unmarshalResult.getJaxbObject();
        } catch (Exception e) {
            //nothing to do
        }
        assertNotNull(tcgaBcr);
        final Controls controls = tcgaBcr.getControls();
        assertNotNull(controls);
        final List<Control> controlList = controls.getControl();
        assertNotNull(controlList);
        assertEquals(543, controlList.size());
        for (int i = 0; i < controlList.size(); i++) {
            final Control control = controlList.get(i);
            final List<String> diseaseCodeTypeList = control.getAliquotsToDiseases()
                    .getDiseaseCodeList();
            for (final String diseaseCodeType : diseaseCodeTypeList) {
                assertNotNull(diseaseCodeType);
                System.out.println(diseaseCodeType);
                assertEquals("KICH2", controlList.get(0).getAliquotsToDiseases().getDiseaseCodeList().get(0));
            }
        }
    }

    @Test
    public void testMetaDataUpdateForBiospecimenXML() throws ProcessorException, UUIDException {
        final Map<String, String> existingMetaData = new HashMap<String, String>();
        mocker.checking(new Expectations() {{
            oneOf(mockUUIDHierarchyQueries).getMetaData(with(any(List.class)));
            will(returnValue(getExistingMetaData()));
            oneOf(mockUUIDHierarchyQueries).persistUUIDHierarchy(with(any(List.class)));
        }});

        final List<BiospecimenMetaData> biospecimenMetaData =
                biospecimenMetaDataProcessor.handleBiospecimenMetaData(bcrBiospecimenXMLFile, qcContext);
        assertNotNull(biospecimenMetaData);
        assertFalse(biospecimenMetaData.isEmpty());
        assertEquals(36, biospecimenMetaData.size());

        // Patient meta-data object should be the first element in the list
        assertEquals("TCGA-00-0000", biospecimenMetaData.get(0).getBarcode());

        // Slide meta-data object should be the last element in the list
        assertEquals("TCGA-00-0000-01A-01-TS1", biospecimenMetaData.get(biospecimenMetaData.size() - 1).getBarcode());

        assertEquals(2, qcContext.getWarningCount());
        List<String> expectedWarningMsgs = Arrays.asList("Meta data was updated: [Barcode: 'TCGA-00-0000-01A-21', TSS: 'C1', Participant Id: '1234', Sample Type: '9', Vial Number: 'A', Portion Id: '12']-->[Barcode: 'TCGA-00-0000-01A-21', TSS: 'C4', Participant Id: '1234', Sample Type: '10', Vial Number: 'A', Portion Id: '10']",
                "Meta data was updated: [Barcode: 'TCGA-00-0000-10A-01D-A004-05', TSS: 'C1', Participant Id: '1234', Sample Type: '01', Vial Number: 'A', Portion Id: '21', Analyte Type Id: 'D', Plate Id: 'A10R']-->[Barcode: 'TCGA-00-0000-10A-01D-A004-05', TSS: 'C4', Participant Id: '1234', Sample Type: '01', Vial Number: 'A', Portion Id: '21', Analyte Type Id: 'D', Plate Id: 'A10R', Center Code: '02']");
        for (final String warning : expectedWarningMsgs) {
            assertTrue(qcContext.getWarnings().contains(warning));
        }
    }

    @Test
    public void testReceivingCenterIdMetaDataForBiospecimenXML() throws ProcessorException, UUIDException {
        final Map<String, String> existingMetaData = new HashMap<String, String>();
        mocker.checking(new Expectations() {{
            oneOf(mockUUIDHierarchyQueries).getMetaData(with(any(List.class)));
            will(returnValue(existingMetaData));
            oneOf(mockUUIDHierarchyQueries).persistUUIDHierarchy(with(any(List.class)));
        }});

        final List<BiospecimenMetaData> biospecimenMetaData =
                biospecimenMetaDataProcessor.handleBiospecimenMetaData(bcrShipmentPortionXMLFile, qcContext);

        // Test receiving center id for aliquot
        BiospecimenMetaData metaData = biospecimenMetaData.get(4);
        assertEquals("02", metaData.getReceivingCenter());

        // Test receiving center id for shipment_portion
        metaData = biospecimenMetaData.get(5);
        assertEquals("20", metaData.getReceivingCenter());
    }

    @Test
    public void testUniqueBarcodeForMetaData() throws ProcessorException, UUIDException {
        final Map<String, String> existingMetaData = new HashMap<String, String>();
        mocker.checking(new Expectations() {{
            oneOf(mockUUIDHierarchyQueries).getMetaData(with(any(List.class)));
            will(returnValue(existingMetaData));
            oneOf(mockUUIDHierarchyQueries).persistUUIDHierarchy(with(any(List.class)));
        }});

        final List<BiospecimenMetaData> biospecimenMetaData =
                biospecimenMetaDataProcessor.handleBiospecimenMetaData(bcrBiospecimenXMLFile, qcContext);
        for (BiospecimenMetaData metaDataPass1 : biospecimenMetaData) {
            String barcode = metaDataPass1.getBarcode();
            int count = 0;
            for (BiospecimenMetaData metaDataPass2 : biospecimenMetaData) {
                if (barcode.equals(metaDataPass2.getBarcode())) {
                    count += 1;
                }
            }
            assertEquals("Barcode '" + barcode + "' must be unique, but occurs more than once in the meta-data set", 1, count);
        }
    }

    @Test
    public void testUniqueUUIDForMetaData() throws ProcessorException, UUIDException {
        final Map<String, String> existingMetaData = new HashMap<String, String>();
        mocker.checking(new Expectations() {{
            oneOf(mockUUIDHierarchyQueries).getMetaData(with(any(List.class)));
            will(returnValue(existingMetaData));
            oneOf(mockUUIDHierarchyQueries).persistUUIDHierarchy(with(any(List.class)));
        }});

        final List<BiospecimenMetaData> biospecimenMetaData =
                biospecimenMetaDataProcessor.handleBiospecimenMetaData(bcrBiospecimenXMLFile, qcContext);
        for (BiospecimenMetaData metaDataPass1 : biospecimenMetaData) {
            String uuid = metaDataPass1.getUuid();
            int count = 0;
            for (BiospecimenMetaData metaDataPass2 : biospecimenMetaData) {
                if (uuid.equals(metaDataPass2.getUuid())) {
                    count += 1;
                }
            }
            assertEquals("UUID '" + uuid + "' must be unique, but occurs more than once in the meta-data set", 1, count);
        }
    }

    @Test
    public void testBiospecimenXMLUUIDInterpolation() throws ProcessorException, UUIDException {
        final Map<String, String> existingMetaData = new HashMap<String, String>();
        mocker.checking(new Expectations() {{
            oneOf(mockUUIDHierarchyQueries).getMetaData(with(any(List.class)));
            will(returnValue(existingMetaData));
            oneOf(mockUUIDHierarchyQueries).persistUUIDHierarchy(with(any(List.class)));
        }});

        final List<BiospecimenMetaData> biospecimenMetaData =
                biospecimenMetaDataProcessor.handleBiospecimenMetaData(bcrBiospecimenXMLFile, qcContext);
        String parentUUID = biospecimenMetaData.get(0).getUuid();

        // Assert samples have patient UUID as parent UUID
        assertEquals(parentUUID, biospecimenMetaData.get(1).getParentUUID());
        assertEquals(parentUUID, biospecimenMetaData.get(13).getParentUUID());

        // Assert portions have sample UUID as parent UUID
        parentUUID = biospecimenMetaData.get(1).getUuid();
        assertEquals(parentUUID, biospecimenMetaData.get(2).getParentUUID());

        // Assert analytes have portion UUID as parent UUID
        parentUUID = biospecimenMetaData.get(2).getUuid();
        assertEquals(parentUUID, biospecimenMetaData.get(3).getParentUUID());

        // Assert aliquots have analyte UUID as parent UUID
        parentUUID = biospecimenMetaData.get(3).getUuid();
        assertEquals(parentUUID, biospecimenMetaData.get(4).getParentUUID());

        // Assert slides have portion UUID as parent UUID
        parentUUID = biospecimenMetaData.get(19).getUuid();
        assertEquals(parentUUID, biospecimenMetaData.get(34).getParentUUID());
    }

    @Test
    public void testGetSlideValueFromBarcode() throws ProcessorException, UUIDException {
        final Map<String, String> existingMetaData = new HashMap<String, String>();
        mocker.checking(new Expectations() {{
            oneOf(mockUUIDHierarchyQueries).getMetaData(with(any(List.class)));
            will(returnValue(existingMetaData));
            oneOf(mockUUIDHierarchyQueries).persistUUIDHierarchy(with(any(List.class)));
        }});

        final List<BiospecimenMetaData> biospecimenMetaData =
                biospecimenMetaDataProcessor.handleBiospecimenMetaData(bcrBiospecimenXMLFile, qcContext);
        assertEquals("BS2", biospecimenMetaData.get(17).getSlide());
        assertEquals("BS1", biospecimenMetaData.get(18).getSlide());
    }

    @Test
    public void testGetMetaDataForDiseaseSpecificXML() throws ProcessorException, UUIDException {
        final Map<String, String> existingMetaData = new HashMap<String, String>();
        mocker.checking(new Expectations() {{
            oneOf(mockUUIDHierarchyQueries).getMetaData(with(any(List.class)));
            will(returnValue(existingMetaData));
            oneOf(mockUUIDHierarchyQueries).persistUUIDHierarchy(with(any(List.class)));
        }});

        final List<BiospecimenMetaData> diseaseSpecificMetaData =
                biospecimenMetaDataProcessor.handleBiospecimenMetaData(bcrDiseaseSpecificXMLFile, qcContext);
        assertNotNull(diseaseSpecificMetaData);
        assertFalse(diseaseSpecificMetaData.isEmpty());
        assertEquals(3, diseaseSpecificMetaData.size());
    }

    @Test
    public void testDiseaseSpecificXMLUUIDInterpolation() throws ProcessorException, UUIDException {
        final Map<String, String> existingMetaData = new HashMap<String, String>();
        mocker.checking(new Expectations() {{
            oneOf(mockUUIDHierarchyQueries).getMetaData(with(any(List.class)));
            will(returnValue(existingMetaData));
            oneOf(mockUUIDHierarchyQueries).persistUUIDHierarchy(with(any(List.class)));
        }});

        final List<BiospecimenMetaData> diseaseSpecificMetaData =
                biospecimenMetaDataProcessor.handleBiospecimenMetaData(bcrDiseaseSpecificXMLFile, qcContext);
        String parentUUID = diseaseSpecificMetaData.get(0).getUuid();

        // Assert drug and radiation have patient UUID as parent UUID
        assertEquals(parentUUID, diseaseSpecificMetaData.get(1).getParentUUID());
        assertEquals(parentUUID, diseaseSpecificMetaData.get(2).getParentUUID());
    }

    @Test
    public void testGetBioMetaDataWithMissingUUIDS() throws ProcessorException, UUIDException {
        final Map<String, String> existingMetaData = new HashMap<String, String>();
        mocker.checking(new Expectations() {{
            oneOf(mockUUIDHierarchyQueries).getMetaData(with(any(List.class)));
            will(returnValue(existingMetaData));
            oneOf(mockUUIDHierarchyQueries).persistUUIDHierarchy(with(any(List.class)));
        }});

        final List<BiospecimenMetaData> biospecimenMetaData =
                biospecimenMetaDataProcessor.handleBiospecimenMetaData(bcrBiospecimenXMLMissingUUIDs, qcContext);
        assertNotNull(biospecimenMetaData);
        assertFalse(biospecimenMetaData.isEmpty());
        assertEquals(20, biospecimenMetaData.size());

        // Missing patient UUID
        BiospecimenMetaData metaData = biospecimenMetaData.get(0);
        assertEquals(barcodeUUIDMap.get(metaData.getBarcode()), metaData.getUuid());

        // Missing sample UUID
        metaData = biospecimenMetaData.get(1);
        assertEquals(barcodeUUIDMap.get(metaData.getBarcode()), metaData.getUuid());

        // Missing portion UUID
        metaData = biospecimenMetaData.get(2);
        assertEquals(barcodeUUIDMap.get(metaData.getBarcode()), metaData.getUuid());

        // Missing analyte UUID
        metaData = biospecimenMetaData.get(3);
        assertEquals(barcodeUUIDMap.get(metaData.getBarcode()), metaData.getUuid());

        // Missing aliquot UUID
        metaData = biospecimenMetaData.get(5);
        assertEquals(barcodeUUIDMap.get(metaData.getBarcode()), metaData.getUuid());

        // Missing slie UUID
        metaData = biospecimenMetaData.get(12);
        assertEquals(barcodeUUIDMap.get(metaData.getBarcode()), metaData.getUuid());

        // Another missing sample UUID
        metaData = biospecimenMetaData.get(13);
        assertEquals(barcodeUUIDMap.get(metaData.getBarcode()), metaData.getUuid());
    }

    @Test
    public void testGetClinicalMetaDataWithMissingUUIDS() throws ProcessorException, UUIDException {
        final Map<String, String> existingMetaData = new HashMap<String, String>();
        mocker.checking(new Expectations() {{
            oneOf(mockUUIDHierarchyQueries).getMetaData(with(any(List.class)));
            will(returnValue(existingMetaData));
            oneOf(mockUUIDHierarchyQueries).persistUUIDHierarchy(with(any(List.class)));
        }});

        final List<BiospecimenMetaData> diseaseSpecificMetaData =
                biospecimenMetaDataProcessor.handleBiospecimenMetaData(bcrClinicalXMLMissingUUIDs, qcContext);
        assertNotNull(diseaseSpecificMetaData);
        assertFalse(diseaseSpecificMetaData.isEmpty());
        assertEquals(3, diseaseSpecificMetaData.size());

        // Missing drug UUID
        BiospecimenMetaData metaData = diseaseSpecificMetaData.get(1);
        assertEquals(barcodeUUIDMap.get(metaData.getBarcode()), metaData.getUuid());

        // Missing radiation UUID
        metaData = diseaseSpecificMetaData.get(2);
        assertEquals(barcodeUUIDMap.get(metaData.getBarcode()), metaData.getUuid());
    }

    @Test
    public void testGetClinicalMetaNoExaminationUUID() throws ProcessorException, UUIDException {
        final Map<String, String> existingMetaData = new HashMap<String, String>();
        mocker.checking(new Expectations() {{
            oneOf(mockUUIDHierarchyQueries).getMetaData(with(any(List.class)));
            will(returnValue(existingMetaData));
            oneOf(mockUUIDHierarchyQueries).persistUUIDHierarchy(with(any(List.class)));
        }});

        final List<BiospecimenMetaData> diseaseSpecificMetaData =
                biospecimenMetaDataProcessor.handleBiospecimenMetaData(bcrClinicalXMLNoExaminationUUID, qcContext);
        assertNotNull(diseaseSpecificMetaData);
        assertFalse(diseaseSpecificMetaData.isEmpty());
        assertEquals(6, diseaseSpecificMetaData.size());
    }

    @Test
    public void testRemoveAuxiliaryXmlFiles() {
        File[] testArchive = {
                new File("testFile_biospecimen.xml"),
                new File("testFile_clinical.xml"),
                new File("testFile_auxiliary.xml")
        };
        File[] resultingArchive = biospecimenMetaDataProcessor.removeAuxiliaryXmlFiles(testArchive);

        assertEquals("testFile_biospecimen.xml", resultingArchive[0].getName());
        assertEquals("testFile_clinical.xml", resultingArchive[1].getName());
        assertEquals(2, resultingArchive.length);

        testArchive = new File[]{
                new File("testFile_biospecimen.xml"),
                new File("testFile_clinical.xml")
        };
        resultingArchive = biospecimenMetaDataProcessor.removeAuxiliaryXmlFiles(testArchive);
        assertEquals("testFile_biospecimen.xml", resultingArchive[0].getName());
        assertEquals("testFile_clinical.xml", resultingArchive[1].getName());
        assertEquals(2, resultingArchive.length);
    }

    @Test
    public void testRemoveAuxiliaryXmlFilesEmptyParam() {
        File[] testArchive = {};
        File[] resultingArchive = biospecimenMetaDataProcessor.removeAuxiliaryXmlFiles(testArchive);
        assertEquals(0, resultingArchive.length);

        assertNull(biospecimenMetaDataProcessor.removeAuxiliaryXmlFiles(null));
    }

    /**
     * Test implementation of {@link BarcodeUuidResolver}
     */
    private class BarcodeUuidResolverTestImpl implements BarcodeUuidResolver {

        @Override
        public Barcode resolveBarcodeAndUuid(final String barcode, final String uuid,
                                             final Tumor disease, final Center center, boolean generateUuidIfNeeded)
                throws UUIDException {

            final Barcode barcodeDetail = new Barcode();
            if (uuid == null || uuid.isEmpty()) {
                final String existingUUID = barcodeUUIDMap.get(barcode);
                barcodeDetail.setUuid((existingUUID == null ? UUID.randomUUID().toString() : existingUUID));
            } else {
                barcodeDetail.setUuid(uuid);
            }

            return barcodeDetail;
        }
    }


    public Map<String, BiospecimenMetaData> getExistingMetaData() {
        final Map<String, BiospecimenMetaData> existingMetaData = new HashMap<String, BiospecimenMetaData>();

        BiospecimenMetaData biospecimenMetaData = new BiospecimenMetaData();
        biospecimenMetaData.setUuid("7c6e3520-8c1e-47e9-b4a9-88e972c5f99a");
        biospecimenMetaData.setUuidType("patient");
        biospecimenMetaData.setDisease("COAD");
        biospecimenMetaData.setBarcode("TCGA-00-0000");
        biospecimenMetaData.setTissueSourceSite("C4");
        biospecimenMetaData.setParticipantId("1234");
        existingMetaData.put("TCGA-00-0000", biospecimenMetaData);

        biospecimenMetaData = new BiospecimenMetaData();
        biospecimenMetaData.setUuidType("portion");
        biospecimenMetaData.setDisease("COAD");
        biospecimenMetaData.setBarcode("TCGA-00-0000-01A-21");
        biospecimenMetaData.setTissueSourceSite("C1");
        biospecimenMetaData.setParticipantId("1234");
        biospecimenMetaData.setSampleType("9");
        biospecimenMetaData.setVialId("A");
        biospecimenMetaData.setPortionId("12");
        existingMetaData.put("TCGA-00-0000-01A-21", biospecimenMetaData);

        biospecimenMetaData = new BiospecimenMetaData();
        biospecimenMetaData.setUuidType("aliquot");
        biospecimenMetaData.setDisease("COAD");
        biospecimenMetaData.setBarcode("TCGA-00-0000-10A-01D-A004-05");
        biospecimenMetaData.setTissueSourceSite("C1");
        biospecimenMetaData.setParticipantId("1234");
        biospecimenMetaData.setSampleType("01");
        biospecimenMetaData.setVialId("A");
        biospecimenMetaData.setPortionId("21");
        biospecimenMetaData.setAnalyteType("D");
        biospecimenMetaData.setPlateId("A10R");
        biospecimenMetaData.setReceivingCenter("06");

        existingMetaData.put("TCGA-00-0000-10A-01D-A004-05", biospecimenMetaData);

        return existingMetaData;

    }

}
