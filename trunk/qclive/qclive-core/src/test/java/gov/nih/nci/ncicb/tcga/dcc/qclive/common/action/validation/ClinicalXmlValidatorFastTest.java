/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CodeTableQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.SchemaException;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.util.XPathXmlParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRIDProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRIDProcessorImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRUtils;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BarcodeTumorValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.DateComparator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidatorImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.ShippedPortionIdProcessorImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ClinicalLoaderQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.clinical.ClinicalTable;
import gov.nih.nci.ncicb.tcga.dcc.qclive.util.QCliveXMLSchemaValidator;
import junit.framework.Assert;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test class for ClinicalXmlValidator
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class ClinicalXmlValidatorFastTest {

    private static final String SAMPLES_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;
    private final String testLocation = SAMPLES_DIR
            + "qclive/clinicalXmlValidator";
    private final String schemaLocationTestFile = testLocation + File.separator
            + "schema_location_test.xml";
    private final String badBcrArchiveLocation = testLocation
            + "/bad_GBM.bio.2.13.0" + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;
    private final String barcodeBadArchiveLocation = testLocation
            + "/barcodebad_GBM.bio.2.13.0" + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;
    private final String imageArchiveLocation = testLocation
            + "/intgen.org_GBM.tissue_images.Level_1.1.0.0"
            + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;

    private final String dateValidationLocation = testLocation
            + "/dateValidation";
    private final String dateValidation = dateValidationLocation
            + "/clinicalDateValidation.xml";
    private final String dateValidationDifferentNamespace = dateValidationLocation
            + "/clinicalDateValidationDifferentNamespace.xml";
    private final String dateValidationNoMonth = dateValidationLocation
            + "/clinicalDateValidationNoMonth.xml";
    private final String dateValidationBadDate = dateValidationLocation
            + "/clinicalDateValidationBadDate.xml";
    private final String dateValidationLeapYear = dateValidationLocation
            + "/clinicalDateValidationLeapYear.xml";
    private final String dateValidationMultipleBadDates = dateValidationLocation
            + "/clinicalDateValidationMultipleBadDates.xml";
    private final String dateValidationFutureDate = dateValidationLocation
            + "/clinicalDateValidationFutureDate.xml";
    private final String dateValidationAttributesOnSeveralLines = dateValidationLocation
            + "/clinicalDateValidationAttributesOnSeveralLinesComplete.xml";
    private final String dateValidationAttributesOnSeveralLinesAndMissingRequiredDayElements = dateValidationLocation
            + "/clinicalDateValidationAttributesOnSeveralLinesAndMissingRequiredDayElementsComplete.xml";
    private final String dateValidationAttributesOnSeveralLinesAndMissingRequiredMonthElements = dateValidationLocation
            + "/clinicalDateValidationAttributesOnSeveralLinesAndMissingRequiredMonthElementsComplete.xml";
    private final String dateComparisonLocation = testLocation
            + "/dateComparison";
    private final String dateComparison = dateComparisonLocation
            + File.separator + "clinicalDateComparison.xml";

    private final String schemaValidationXmlLocation = testLocation
            + "/schemaValidation";
    private final String schemaValidationValidXml = schemaValidationXmlLocation
            + "/intgen.org_biospecimen.TCGA-02-0004.xml";
    private final String schemaValidationInvalidXml = schemaValidationXmlLocation
            + "/intgen.org_clinical.TCGA-02-0004.xml";

    private final String schemaValidationValidAuxiliary = schemaValidationXmlLocation + File.separator + "validAuxiliary.xml";
    private final String schemaValidationInvalidAuxiliary = schemaValidationXmlLocation + File.separator + "invalidAuxiliary.xml";

    private final Mockery context = new JUnit4Mockery();
    private ClinicalXmlValidator clinicalXmlValidator;
    private Archive archive;
    private QcContext qcContext;
    private XPathXmlParser xPathXmlParser;
    private UUIDService mockUuidService;
    private ClinicalLoaderQueries mockClinicalLoaderQueries;
    private QcLiveBarcodeAndUUIDValidator mockQcLiveBarcodeAndUUIDValidator;
    private List<ClinicalTable> clinicalTables;
    private Map<String, Date> dateNameToValueMap;
    private Map<String, String> datePrecisionMap;
    private final ShippedPortionIdProcessorImpl shippedPortionIdProcessor = new ShippedPortionIdProcessorImpl();
    private CodeTableQueries mockCodeTableQueries;
    private BCRUtils mockBcrUtils;

    private static String validXsdDomainPattern = "ncisvn\\.nci\\.nih\\.gov";
    private static String validXsdPrefixPattern = "bcr";
    private static String validXsdVersionPattern = "2\\.6(\\.\\d*)?";

    private final String shipmentPortionPath = "//portions/shipment_portion";
    private final String bcrShipmentPortionUuidElementName = "bcr_shipment_portion_uuid";
    private final String centerIdElementName = "center_id";
    private final String plateIdElementName = "plate_id";
    private final String shipmentPortionBcrAliquotBarcodeElementName = "shipment_portion_bcr_aliquot_barcode";

    @Before
    public void setup() {
        archive = new Archive();
        mockQcLiveBarcodeAndUUIDValidator = context
                .mock(QcLiveBarcodeAndUUIDValidator.class);
        mockBcrUtils = context.mock(BCRUtils.class);
        clinicalXmlValidator = new ClinicalXmlValidator(
                mockQcLiveBarcodeAndUUIDValidator);
        clinicalXmlValidator.setBcrUtils(mockBcrUtils);
        archive.setExperimentType(Experiment.TYPE_BCR);
        archive.setTumorType("fakeTumor");
        qcContext = new QcContext();
        qcContext.setArchive(archive);

        clinicalXmlValidator
                .setBarcodeTumorValidator(new BarcodeTumorValidator() {
                    public boolean barcodeIsValidForTumor(final String barcode,
                                                          final String tumorAbbreviation) {
                        return true;
                    }
                });
        xPathXmlParser = new XPathXmlParser();
        mockUuidService = context.mock(UUIDService.class);
        mockClinicalLoaderQueries = context.mock(ClinicalLoaderQueries.class);
        clinicalXmlValidator.setUuidService(mockUuidService);
        clinicalXmlValidator
                .setClinicalLoaderQueries(mockClinicalLoaderQueries);
        clinicalXmlValidator.setAllowLocalSchema(false);

        QCliveXMLSchemaValidator validator = new QCliveXMLSchemaValidator();
        validator.setValidXsdDomainPattern(validXsdDomainPattern);
        validator.setValidXsdPrefixPattern(validXsdPrefixPattern);
        validator.setValidXsdVersionPattern(validXsdVersionPattern);

        clinicalXmlValidator.setqCliveXMLSchemaValidator(validator);
        clinicalXmlValidator.setShipmentPortionPath(shipmentPortionPath);
        clinicalXmlValidator
                .setBcrShipmentPortionUuidElementName(bcrShipmentPortionUuidElementName);
        clinicalXmlValidator.setCenterIdElementName(centerIdElementName);
        clinicalXmlValidator.setPlateIdElementName(plateIdElementName);
        clinicalXmlValidator
                .setShipmentPortionBcrAliquotBarcodeElementName(shipmentPortionBcrAliquotBarcodeElementName);
        clinicalTables = new ArrayList<ClinicalTable>();
        clinicalTables.add(mockClinicalTable("patient", "bcr_patient_barcode",
                "bcr_patient_uuid"));
        clinicalTables.add(mockClinicalTable("sample", "bcr_sample_barcode",
                "bcr_sample_uuid"));
        clinicalTables.add(mockClinicalTable("portion", "bcr_portion_barcode",
                "bcr_portion_uuid"));
        clinicalTables.add(mockClinicalTable("analyte", "bcr_analyte_barcode",
                "bcr_analyte_uuid"));
        clinicalTables.add(mockClinicalTable("aliquot", "bcr_aliquot_barcode",
                "bcr_aliquot_uuid"));
        clinicalTables.add(mockClinicalTable("slide", "bcr_slide_barcode",
                "bcr_slide_uuid"));
        clinicalTables.add(mockClinicalTable("shipment_portion",
                "shipment_portion_bcr_aliquot_barcode",
                "bcr_shipment_portion_uuid"));

        context.checking(new Expectations() {
            {
                allowing(mockClinicalLoaderQueries).getAllClinicalTables();
                will(returnValue(clinicalTables));
            }
        });

        dateNameToValueMap = new HashMap<String, Date>();
        datePrecisionMap = new HashMap<String, String>();
        clinicalXmlValidator
                .setShippedPortionIdProcessor(shippedPortionIdProcessor);
        mockCodeTableQueries = context.mock(CodeTableQueries.class);
        clinicalXmlValidator.setCodeTableQueries(mockCodeTableQueries);
        shippedPortionIdProcessor.setShipmentPortionPath(shipmentPortionPath);
    }

    @After
    public void cleanup() {
        final File fileToDelete = new File(schemaLocationTestFile);
        fileToDelete.delete();
    }

    private ClinicalTable mockClinicalTable(final String nodeName,
                                            final String barcodeColumnName, final String uuidColumnName) {
        final ClinicalTable table = new ClinicalTable();
        table.setElementNodeName(nodeName);
        table.setUuidElementName(uuidColumnName);
        table.setBarcodeElementName(barcodeColumnName);
        return table;
    }

    private void setupForOldXSD() {
        final BCRIDProcessor bcridProcessor = new BCRIDProcessorImpl() {
            @Override
            public boolean slideBarcodeExists(final String barcode) {
                return false;
            }
        };
        bcridProcessor.setAliquotElementXPath("ALIQUOT");
        bcridProcessor.setAliquotBarcodeElement("BCRALIQUOTBARCODE");
        bcridProcessor.setShipDayElement("DAYOFSHIPMENT");
        bcridProcessor.setShipMonthElement("MONTHOFSHIPMENT");
        bcridProcessor.setShipYearElement("YEAROFSHIPMENT");
        clinicalXmlValidator.setBcridProcessor(bcridProcessor);

        clinicalXmlValidator.setDayOfPrefix("DAYOF");
        clinicalXmlValidator.setMonthOfPrefix("MONTHOF");
        clinicalXmlValidator.setYearOfPrefix("YEAROF");
    }

    private void setupForNewXSD() {
        final BCRIDProcessor bcridProcessor = new BCRIDProcessorImpl() {
            @Override
            public boolean slideBarcodeExists(final String barcode) {
                return false;
            }
        };
        bcridProcessor.setAliquotElementXPath("//bios:aliquot");
        bcridProcessor.setAliquotBarcodeElement("bios:bcr_aliquot_barcode");
        bcridProcessor.setShipDayElement("bios:day_of_shipment");
        bcridProcessor.setShipMonthElement("bios:month_of_shipment");
        bcridProcessor.setShipYearElement("bios:year_of_shipment");
        bcridProcessor.setAliquotUuidElement("bios:bcr_aliquot_uuid");
        clinicalXmlValidator.setBcridProcessor(bcridProcessor);

        clinicalXmlValidator.setDayOfPrefix("day_of_");
        clinicalXmlValidator.setMonthOfPrefix("month_of_");
        clinicalXmlValidator.setYearOfPrefix("year_of_");

        clinicalXmlValidator.setDatesToValidateString("birth,last_known_alive,death,last_followup,initial_pathologic_diagnosis,tumor_progression," +
                "tumor_recurrence,new_tumor_event_after_initial_treatment,additional_surgery_locoregional_procedure," +
                "additional_surgery_metastatic_procedure,form_completion,procedure,radiation_treatment_start," +
                "radiation_treatment_end,drug_treatment_start,drug_treatment_end,radiation_therapy_start,radiation_therapy_end," +
                "drug_therapy_start,drug_therapy_end,collection,shipment");

    }

    private void setupForExistingSlideBarcodes() {
        final BCRIDProcessor bcridProcessor = new BCRIDProcessorImpl() {

            public boolean slideBarcodeExists(final String barcode) {
                return true;
            }

        };

        bcridProcessor.setAliquotElementXPath("//bios:aliquot");
        bcridProcessor.setAliquotBarcodeElement("bios:bcr_aliquot_barcode");
        bcridProcessor.setShipDayElement("bios:day_of_shipment");
        bcridProcessor.setShipMonthElement("bios:month_of_shipment");
        bcridProcessor.setShipYearElement("bios:year_of_shipment");
        bcridProcessor.setAliquotUuidElement("bios:bcr_aliquot_uuid");
        clinicalXmlValidator.setBcridProcessor(bcridProcessor);

        clinicalXmlValidator.setDayOfPrefix("day_of_");
        clinicalXmlValidator.setMonthOfPrefix("month_of_");
        clinicalXmlValidator.setYearOfPrefix("year_of_");

        clinicalXmlValidator.setDatesToValidateString(
                "birth,last_known_alive,death,last_followup,initial_pathologic_diagnosis,tumor_progression," +
                        "tumor_recurrence,new_tumor_event_after_initial_treatment,additional_surgery_locoregional_procedure," +
                        "additional_surgery_metastatic_procedure,form_completion,procedure,radiation_treatment_start," +
                        "radiation_treatment_end,drug_treatment_start,drug_treatment_end,radiation_therapy_start,radiation_therapy_end," +
                        "drug_therapy_start,drug_therapy_end,collection,shipment");
    }

    private void setupForNamespaceXSD() {
        final BCRIDProcessor bcridProcessor = new BCRIDProcessorImpl() {
            @Override
            public boolean slideBarcodeExists(final String barcode) {
                return false;
            }
        };
        bcridProcessor.setAliquotElementXPath("//bios:aliquot");
        bcridProcessor.setAliquotBarcodeElement("bios:bcr_aliquot_barcode");
        bcridProcessor.setShipDayElement("bios:day_of_shipment");
        bcridProcessor.setShipMonthElement("bios:month_of_shipment");
        bcridProcessor.setShipYearElement("bios:year_of_shipment");
        clinicalXmlValidator.setBcridProcessor(bcridProcessor);

        clinicalXmlValidator.setDayOfPrefix("day_of_");
        clinicalXmlValidator.setMonthOfPrefix("month_of_");
        clinicalXmlValidator.setYearOfPrefix("year_of_");

        clinicalXmlValidator
                .setDatesToValidateString("birth,last_known_alive,death,last_followup,initial_pathologic_diagnosis,tumor_progression,tumor_recurrence,new_tumor_event_after_initial_treatment,additional_surgery_locoregional_procedure,additional_surgery_metastatic_procedure,form_completion,procedure,radiation_treatment_start,radiation_treatment_end,drug_treatment_start,drug_treatment_end,radiation_therapy_start,radiation_therapy_end,drug_therapy_start,drug_therapy_end,collection,shipment");
    }

    private void setupDatesToValidate() {
        clinicalXmlValidator
                .setDatesToValidateString("birth,death,squirrel,collection,chipmunk,initial_diagnosis");
    }

    /**
     * Sets the dates to be validated to the values that will be set in the bean
     */
    private void setupDatesToValidateComplete() {

        final List<String> datesToValidate = new ArrayList<String>();
        datesToValidate.add("birth");
        datesToValidate.add("death");
        datesToValidate.add("last_followup");
        datesToValidate.add("initial_pathologic_diagnosis");
        datesToValidate.add("tumor_recurrence");
        datesToValidate.add("tumor_progression");
        datesToValidate.add("drug_therapy_start");
        datesToValidate.add("drug_therapy_end");
        datesToValidate.add("radiation_therapy_start");
        datesToValidate.add("radiation_therapy_end");
        datesToValidate.add("procedure");
        datesToValidate.add("collection");
        datesToValidate.add("shipment");

        clinicalXmlValidator.setDatesToValidate(datesToValidate);
    }

    @Test
    public void testGoodXml() throws Processor.ProcessorException {
        // NOTE: this XML has no UUIDs populated in it, but it does have empty
        // UUID elements
        setupForNewXSD();

        final String barcode1 = "TCGA-00-0000";
        final String barcode2 = "TCGA-11-1111";
        final String filename1 = "nationwidechildrens.org_biospecimen." + barcode1 + ".xml";
        final String filename2 = "nationwidechildrens.org_biospecimen." + barcode2 + ".xml";

        context.checking(new Expectations() {
            {
                // there are 21 aliquot barcodes in this file
                exactly(36)
                        .of(mockQcLiveBarcodeAndUUIDValidator)
                        .validateAnyBarcode(
                                with(any(String.class)),
                                with(qcContext),
                                with(filename1),
                                with(false), with(any(String.class)));
                will(returnValue(true));

                // and 20 aliquot barcodes in this file
                exactly(31)
                        .of(mockQcLiveBarcodeAndUUIDValidator)
                        .validateAnyBarcode(
                                with(any(String.class)),
                                with(qcContext),
                                with(filename2),
                                with(false), with(any(String.class)));
                will(returnValue(true));

            }
        });

        addPatientBarcodeExpectation(filename1, barcode1);
        addPatientBarcodeExpectation(filename2, barcode2);
        addMockBcrUtilsExpectation(false);

        archive.setDeployLocation(testLocation
                + "/nationwidechildrens.org_COAD.bio.Level_1.30.2.0"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        archive.setArchiveFile(new File(testLocation
                + "/nationwidechildrens.org_COAD.bio.Level_1.30.2.0"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION));
        final boolean isValid = clinicalXmlValidator
                .execute(archive, qcContext);
        assertTrue("Execution failed: " + qcContext.getErrors(), isValid);
    }

    @Test
    public void testXmlNoUuidValuesButRequired()
            throws Processor.ProcessorException {
        // make UUIDs required, should fail because this XML has no UUID values.
        setupForNewXSD();
        clinicalXmlValidator.setUuidsRequired(true);

        final String barcode1 = "TCGA-00-0000";
        final String barcode2 = "TCGA-11-1111";
        final String filename1 = "nationwidechildrens.org_biospecimen." + barcode1 + ".xml";
        final String filename2 = "nationwidechildrens.org_biospecimen." + barcode2 + ".xml";

        context.checking(new Expectations() {
            {
                // there are 21 aliquot barcodes in this file
                exactly(36)
                        .of(mockQcLiveBarcodeAndUUIDValidator)
                        .validateAnyBarcode(
                                with(any(String.class)),
                                with(qcContext),
                                with(filename1),
                                with(false), with(any(String.class)));
                will(returnValue(true));

                // and 20 aliquot barcodes in this file
                exactly(31)
                        .of(mockQcLiveBarcodeAndUUIDValidator)
                        .validateAnyBarcode(
                                with(any(String.class)),
                                with(qcContext),
                                with(filename2),
                                with(false), with(any(String.class)));
                will(returnValue(true));
            }
        });

        addPatientBarcodeExpectation(filename1, barcode1);
        addPatientBarcodeExpectation(filename2, barcode2);
        addMockBcrUtilsExpectation(false);

        archive.setDeployLocation(testLocation
                + "/nationwidechildrens.org_COAD.bio.Level_1.30.2.0"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        archive.setArchiveFile(new File(testLocation
                + "/nationwidechildrens.org_COAD.bio.Level_1.30.2.0"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION));
        final boolean isValid = clinicalXmlValidator
                .execute(archive, qcContext);
        assertFalse(isValid);
        assertTrue(qcContext.getErrorCount() > 0);
        assertEquals(67, qcContext.getErrorCount()); // there are 67 elements
        // between the two files
        // that can have UUIDs,
        // should be an error
        // for all of them
    }

    @Test
    public void testClinicalFilenameWithPatientBarcode() throws Processor.ProcessorException {

        final String barcode = "TCGA-00-0000";
        final String filename = "nationwidechildrens.org_clinical.TCGA-00-0000.xml";
        final File fileWithPatientBarcode = checkProcessFile(filename, barcode);

        addMockBcrUtilsExpectation(false);
        clinicalXmlValidator.processFile(fileWithPatientBarcode, qcContext);
         assertEquals(qcContext.getErrors().toString(), 0, qcContext.getErrorCount());
    }

    @Test
    public void testClinicalFilenameWithoutPatientBarcode() throws Processor.ProcessorException {

        final String barcode = null;
        final String filename = "nationwidechildrens.org_clinical.TCGA-00.xml";
        final File fileWithoutPatientBarcode = checkProcessFile(filename, barcode);

        addMockBcrUtilsExpectation(false);

        assertFalse(clinicalXmlValidator.processFile(fileWithoutPatientBarcode, qcContext));
        assertEquals(qcContext.getErrors().toString(), 1, qcContext.getErrorCount());
        assertEquals("An error occurred while processing XML file '" + filename + "': " +
                "The filename '" + filename + "' does not contain a patient barcode.", qcContext.getErrors().get(0));
    }

    @Test
    public void testClinicalFileWithPatientBarcodeNotMatching() throws Processor.ProcessorException {

        final String barcode = "TCGA-11-1111";
        final String filename = "nationwidechildrens.org_clinical.TCGA-11-1111.xml";
        final File fileWithPatientBarcode = checkProcessFile(filename, barcode);

        addMockBcrUtilsExpectation(false);

        assertFalse(clinicalXmlValidator.processFile(fileWithPatientBarcode, qcContext));
        assertEquals(qcContext.getErrors().toString(), 1, qcContext.getErrorCount());
        assertEquals("An error occurred while processing XML file '" + filename + "': " +
                "The file '" + filename + "' has a patient barcode [TCGA-00-0000] " +
                "that does not match the patient barcode found in the filename [TCGA-11-1111].", qcContext.getErrors().get(0));
    }

    @Test
    public void testBiospecimenFilenameWithPatientBarcode() throws Processor.ProcessorException {

        final String barcode = "TCGA-00-0000";
        final String filename = "nationwidechildrens.org_biospecimen.TCGA-00-0000.xml";
        final File fileWithPatientBarcode = checkProcessFile(filename, barcode);

        addMockBcrUtilsExpectation(false);
        clinicalXmlValidator.processFile(fileWithPatientBarcode, qcContext);
        assertEquals(qcContext.getErrors().toString(), 0, qcContext.getErrorCount());
    }

    @Test
    public void testBiospecimenFilenameWithoutPatientBarcode() throws Processor.ProcessorException {

        final String barcode = null;
        final String filename = "nationwidechildrens.org_biospecimen.TCGA-00.xml";
        final File fileWithoutPatientBarcode = checkProcessFile(filename, barcode);

        addMockBcrUtilsExpectation(false);

        assertFalse(clinicalXmlValidator.processFile(fileWithoutPatientBarcode, qcContext));
        assertEquals(qcContext.getErrors().toString(), 1, qcContext.getErrorCount());
        assertEquals("An error occurred while processing XML file '" + filename + "': " +
                "The filename '" + filename + "' does not contain a patient barcode.", qcContext.getErrors().get(0));
    }

    @Test
    public void testBiospecimenFileWithPatientBarcodeNotMatching() throws Processor.ProcessorException {

        final String barcode = "TCGA-11-1111";
        final String filename = "nationwidechildrens.org_biospecimen.TCGA-11-1111.xml";
        final File fileWithPatientBarcode = checkProcessFile(filename, barcode);

        addMockBcrUtilsExpectation(false);

        assertFalse(clinicalXmlValidator.processFile(fileWithPatientBarcode, qcContext));
        assertEquals(qcContext.getErrors().toString(), 1, qcContext.getErrorCount());
        assertEquals("An error occurred while processing XML file '" + filename + "': " +
                "The file '" + filename + "' has a patient barcode [TCGA-00-0000] " +
                "that does not match the patient barcode found in the filename [TCGA-11-1111].", qcContext.getErrors().get(0));
    }

    /**
     * Return a <code>File</code> object from the given filename prefixed by the 'invalidPatientBarcodeInFilename' test directory.
     * <p/>
     * Also add expectations for calling mockQcLiveBarcodeAndUUIDValidator).validateAnyBarcode() returning the given barcode
     * when calling mockQcLiveBarcodeAndUUIDValidator).getPatientBarcode().
     *
     * @param filename the filename to use for testing
     * @param barcode  the barcode returned by expectations
     * @return a <code>File</code> object from the given filename prefixed by the 'invalidPatientBarcodeInFilename' test directory
     */
    private File checkProcessFile(final String filename, final String barcode) {

        final String pathToFilename = testLocation + File.separator + "patientBarcodeValidation";
        final File file = new File(pathToFilename + File.separator + filename);

        setupForNewXSD();
        archive.setDeployLocation(pathToFilename + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);

        context.checking(new Expectations() {{
            allowing(mockQcLiveBarcodeAndUUIDValidator).validateAnyBarcode(with(any(String.class)),
                    with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)), with(any(String.class)));
            will(returnValue(true));
        }});

        addPatientBarcodeExpectation(filename, barcode);

        return file;
    }

    @Test
    public void testBadXml() throws Processor.ProcessorException {
        setupForOldXSD();
        // test archive with XML that doesn't conform to XSD
        archive.setDeployLocation(badBcrArchiveLocation);
        archive.setArchiveFile(new File(badBcrArchiveLocation));

        addMockBcrUtilsExpectation(false);

        assertFalse(
                "Execution should have failed because XML does not conform to XSD!",
                clinicalXmlValidator.execute(archive, qcContext));
    }

    @Test
    public void testBadBarcodes() throws Processor.ProcessorException {
        setupForNewXSD();
        clinicalXmlValidator
                .setBarcodeAndUUIDValidator(new QcLiveBarcodeAndUUIDValidatorImpl());
        // test archive with invalid barcodes
        archive.setDeployLocation(barcodeBadArchiveLocation);
        archive.setArchiveFile(new File(barcodeBadArchiveLocation));
        final String filename = "nationwidechildrens.org_biospecimen.TCGA-00-0000.xml";

        addMockBcrUtilsExpectation(false);

        assertFalse(
                "Execution should have failed because XML contains an invalid barcode",
                clinicalXmlValidator.execute(archive, qcContext));
        assertEquals(qcContext.getErrors().toString(), 3,
                qcContext.getErrorCount());
        assertEquals(
                "An error occurred while validating barcode 'WHAT-IS-THIS': The Sample barcode 'WHAT-IS-THIS' in file " + filename + " has an invalid format",
                qcContext.getErrors().get(0).trim());
        assertEquals(
                "An error occurred while validating barcode 'THIS-IS-NOT-AT-ALL-VALID': The Aliquot barcode 'THIS-IS-NOT-AT-ALL-VALID' in file " + filename + " has an invalid format",
                qcContext.getErrors().get(1).trim());
        assertEquals(
                "An error occurred while validating barcode 'NEITHER-IS-THIS': The Aliquot barcode 'NEITHER-IS-THIS' in file " + filename + " has an invalid format",
                qcContext.getErrors().get(2).trim());
    }

    @Test
    public void testImages() throws Processor.ProcessorException {
        setupForOldXSD();
        // test an image archive to make sure it validates
        archive.setDeployLocation(imageArchiveLocation);
        archive.setArchiveFile(new File(imageArchiveLocation));
        assertTrue(clinicalXmlValidator.execute(archive, qcContext));
    }

    @Test
    public void testNamespaceSchemaDeclaration()
            throws ParserConfigurationException, IOException, SAXException,
            Processor.ProcessorException {
        setupForOldXSD();
        archive.setDeployLocation(testLocation + "/intgen.org_TEST.bio.1.1.0"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        archive.setArchiveFile(new File(testLocation
                + "/intgen.org_TEST.bio.1.1.0"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION));

        final boolean isValid = clinicalXmlValidator
                .execute(archive, qcContext);
        assertFalse(qcContext.getErrors().toString(), isValid);
    }

    @Test
    public void testNamespaceSchemaCombined()
            throws ParserConfigurationException, IOException, SAXException,
            Processor.ProcessorException {
        setupForOldXSD();

        final String barcode1 = "TCGA-00-0000";
        final String barcode2 = "TCGA-11-1111";
        final String filename1 = "nationwidechildrens.org_biospecimen." + barcode1 + ".xml";
        final String filename2 = "nationwidechildrens.org_biospecimen." + barcode2 + ".xml";

        context.checking(new Expectations() {
            {
                exactly(36)
                        .of(mockQcLiveBarcodeAndUUIDValidator)
                        .validateAnyBarcode(
                                with(any(String.class)),
                                with(qcContext),
                                with(filename1),
                                with(false), with(any(String.class)));
                will(returnValue(true));
                exactly(31)
                        .of(mockQcLiveBarcodeAndUUIDValidator)
                        .validateAnyBarcode(
                                with(any(String.class)),
                                with(qcContext),
                                with(filename2),
                                with(false), with(any(String.class)));
                will(returnValue(true));
            }
        });

        addPatientBarcodeExpectation(filename1, barcode1);
        addPatientBarcodeExpectation(filename2, barcode2);
        addMockBcrUtilsExpectation(false);

        archive.setDeployLocation(testLocation
                + "/nationwidechildrens.org_COAD.bio.Level_1.30.2.0"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        archive.setArchiveFile(new File(testLocation
                + "/nationwidechildrens.org_COAD.bio.Level_1.30.2.0"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION));
        final boolean isValid = clinicalXmlValidator
                .execute(archive, qcContext);
        assertTrue(qcContext.getErrors().toString(), isValid);
        assertEquals(0, qcContext.getErrorCount());
    }

    /**
     * Check that the dates of the given file are valid
     *
     * @param filepath           the filepath
     * @param dateNameToValueMap the Map to store date values for later comparison
     * @return <code>true</code> if the date validation was succesfull,
     *         <code>false</code> otherwise
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    private boolean runDateValidation(final String filepath,
                                      final Map<String, Date> dateNameToValueMap) throws IOException,
            SAXException, ParserConfigurationException {
        final File archiveFile = new File(filepath);
        qcContext.setFile(archiveFile);
        final Document document = xPathXmlParser.parseXmlFile(archiveFile,
                false);
        return clinicalXmlValidator.checkDateValidation(archiveFile,
                xPathXmlParser.getXPath(), document, qcContext,
                dateNameToValueMap, datePrecisionMap);
    }

    @Test
    public void testValidDateValidation() throws Processor.ProcessorException,
            IOException, SAXException, ParserConfigurationException {
        setupForNewXSD();
        final boolean isValid = runDateValidation(dateValidation,
                dateNameToValueMap);
        assertTrue(qcContext.getErrors().toString(), isValid);
    }

    @Test
    public void testValidDateValidationDifferentNamespace()
            throws Processor.ProcessorException, IOException, SAXException,
            ParserConfigurationException {
        setupForNewXSD();
        final boolean isValid = runDateValidation(
                dateValidationDifferentNamespace, dateNameToValueMap);
        assertTrue(qcContext.getErrors().toString(), isValid);
    }

    @Test
    public void testValidDateComparison() throws Exception {

        final String datesToValidateString = "birth,death,squirrel,last_followup";
        final int expectedDatesToValidateSize = 4;
        final String dateComparatorString = "birth<=squirrel, birth<death, death>=squirrel, death>birth, "
                + "birth==squirrel, death==death, birth<>death,birth<last_followup";
        final int expectedDatesToCompareSize = expectedDatesToValidateSize;
        final int expectedDateComparatorsSize = 8;
        final boolean dateValidationExpectedValid = true;
        final boolean dateComparisonExpectedValid = true;

        checkDateComparison(datesToValidateString, expectedDatesToValidateSize,
                dateComparatorString, expectedDatesToCompareSize,
                expectedDateComparatorsSize, dateValidationExpectedValid, dateComparisonExpectedValid);
    }

    @Test
    public void testInvalidDateComparison() throws Exception {

        final String datesToValidateString = "birth,death";
        final int expectedDatesToValidateSize = 2;
        final String dateComparatorString = "birth==death";
        final int expectedDatesToCompareSize = expectedDatesToValidateSize;
        final int expectedDateComparatorsSize = 1;
        final boolean dateValidationExpectedValid = true;
        final boolean dateComparisonExpectedValid = false;

        checkDateComparison(datesToValidateString, expectedDatesToValidateSize,
                dateComparatorString, expectedDatesToCompareSize,
                expectedDateComparatorsSize, dateValidationExpectedValid, dateComparisonExpectedValid);
    }

    @Test
    public void testDateComparisonWithInvalidDateFormat() throws Exception {

        final String datesToValidateString = "birth,moose";
        final int expectedDatesToValidateSize = 2;
        final String dateComparatorString = "birth==moose";
        final int expectedDatesToCompareSize = expectedDatesToValidateSize;
        final int expectedDateComparatorsSize = 1;
        final boolean dateComparisonExpectedValid = false;

        checkDateComparison(datesToValidateString, expectedDatesToValidateSize,
                dateComparatorString, expectedDatesToCompareSize,
                expectedDateComparatorsSize, false, dateComparisonExpectedValid);
    }

    @Test
    public void testDateComparisonMissingDate() throws Exception {

        final String datesToValidateString = "squirrel,nuts";
        final int expectedDatesToValidateSize = 2;
        final String dateComparatorString = "squirrel<>nuts";
        final int expectedDatesToCompareSize = expectedDatesToValidateSize;
        final int expectedDateComparatorsSize = 1;
        final boolean dateValidationExpectedValid = true;
        final boolean dateComparisonExpectedValid = true;

        checkDateComparison(datesToValidateString, expectedDatesToValidateSize,
                dateComparatorString, expectedDatesToCompareSize,
                expectedDateComparatorsSize, dateValidationExpectedValid, dateComparisonExpectedValid);
    }

    /**
     * This method attempts to validate a few selected dates in a complete
     * clinical XML file with some of the date elements spread on several lines.
     * <p/>
     * It is assumed that the XML file is following the
     * 'intgen.org_TCGA_ver1_17.xsd' schema.
     *
     * @throws Processor.ProcessorException
     * @throws IOException
     */
    @Test
    public void testValidDateValidationAttributesOnSeveralLines()
            throws Processor.ProcessorException, IOException, SAXException,
            ParserConfigurationException {
        setupForNewXSD();
        // First with all the expected date fields that will be set during bean
        // initialization
        setupDatesToValidateComplete();
        final File archiveFile = new File(
                dateValidationAttributesOnSeveralLines);
        final Document document = xPathXmlParser.parseXmlFile(archiveFile,
                false);
        boolean isValid = clinicalXmlValidator.checkDateValidation(archiveFile,
                xPathXmlParser.getXPath(), document, qcContext,
                dateNameToValueMap, datePrecisionMap);
        assertTrue(qcContext.getErrors().toString(), isValid);

        // Then with some unexpected date fields
        setupDatesToValidate();
        isValid = clinicalXmlValidator.checkDateValidation(archiveFile,
                xPathXmlParser.getXPath(), document, qcContext,
                dateNameToValueMap, datePrecisionMap);
        assertTrue(qcContext.getErrors().toString(), isValid);
    }

    /**
     * This method attempts to validate a few selected dates in a complete
     * clinical XML file with some of the date elements spread on several lines
     * and a few required date elements missing
     * <p/>
     * It is assumed that the XML file is following the
     * 'intgen.org_TCGA_ver1_17.xsd' schema, except for the missing DAYOF
     * element
     *
     * @throws Processor.ProcessorException
     * @throws IOException
     */
    @Test
    public void testValidDateValidationAttributesOnSeveralLinesAndMissingRequiredDayElements()
            throws Processor.ProcessorException, IOException, SAXException,
            ParserConfigurationException {
        setupForNewXSD();
        // First with all the expected date fields that will be set during bean
        // initialization
        setupDatesToValidateComplete();
        final File archiveFile = new File(
                dateValidationAttributesOnSeveralLinesAndMissingRequiredDayElements);
        final Document document = xPathXmlParser.parseXmlFile(archiveFile,
                false);
        boolean isValid = clinicalXmlValidator.checkDateValidation(archiveFile,
                xPathXmlParser.getXPath(), document, qcContext,
                dateNameToValueMap, datePrecisionMap);
        assertFalse(qcContext.getErrors().toString(), isValid);

        // Then with some unexpected date fields
        setupDatesToValidate();
        isValid = clinicalXmlValidator.checkDateValidation(archiveFile,
                xPathXmlParser.getXPath(), document, qcContext,
                dateNameToValueMap, datePrecisionMap);
        assertFalse(qcContext.getErrors().toString(), isValid);
    }

    /**
     * This method attempts to validate a few selected dates in a complete
     * clinical XML file with some of the date elements spread on several lines
     * and a few required date elements missing
     * <p/>
     * It is assumed that the XML file is following the
     * 'intgen.org_TCGA_ver1_17.xsd' schema, except for the missing MONTHOF
     * element
     *
     * @throws Processor.ProcessorException
     * @throws IOException
     */
    @Test
    public void testValidDateValidationAttributesOnSeveralLinesAndMissingRequiredMonthElements()
            throws Processor.ProcessorException, IOException, SAXException,
            ParserConfigurationException {
        setupForNewXSD();
        // First with all the expected date fields that will be set during bean
        // initialization
        setupDatesToValidateComplete();
        final File archiveFile = new File(
                dateValidationAttributesOnSeveralLinesAndMissingRequiredMonthElements);
        final Document document = xPathXmlParser.parseXmlFile(archiveFile,
                false);
        boolean isValid = clinicalXmlValidator.checkDateValidation(archiveFile,
                xPathXmlParser.getXPath(), document, qcContext,
                dateNameToValueMap, datePrecisionMap);
        assertFalse(qcContext.getErrors().toString(), isValid);

        // Then with some unexpected date fields
        setupDatesToValidate();
        isValid = clinicalXmlValidator.checkDateValidation(archiveFile,
                xPathXmlParser.getXPath(), document, qcContext,
                dateNameToValueMap, datePrecisionMap);
        assertFalse(qcContext.getErrors().toString(), isValid);
    }

    @Test
    public void testDateValidationNoMonth()
            throws Processor.ProcessorException, IOException, SAXException,
            ParserConfigurationException {
        setupForNewXSD();
        setupDatesToValidate();
        final boolean isValid = runDateValidation(dateValidationNoMonth,
                dateNameToValueMap);
        assertFalse(qcContext.getErrors().toString(), isValid);
        assertTrue(qcContext.getErrors().toString()
                .contains("clinicalDateValidationNoMonth.xml"));
    }

    @Test
    public void testDateValidationBadDate()
            throws Processor.ProcessorException, IOException, SAXException,
            ParserConfigurationException {
        setupForNewXSD();
        setupDatesToValidate();
        final boolean isValid = runDateValidation(dateValidationBadDate,
                dateNameToValueMap);
        assertFalse(qcContext.getErrors().toString(), isValid);
        assertTrue(qcContext.getErrors().toString()
                .contains("clinicalDateValidationBadDate.xml"));
    }

    @Test
    public void testDateValidationLeapYear()
            throws Processor.ProcessorException, IOException, SAXException,
            ParserConfigurationException {
        setupForNewXSD();
        setupDatesToValidate();
        final boolean isValid = runDateValidation(dateValidationLeapYear,
                dateNameToValueMap);
        assertTrue(qcContext.getErrors().toString(), isValid);
    }

    @Test
    public void testDateValidationMultipleBadDates()
            throws Processor.ProcessorException, IOException, SAXException,
            ParserConfigurationException {
        setupForNewXSD();
        setupDatesToValidate();
        final boolean isValid = runDateValidation(
                dateValidationMultipleBadDates, dateNameToValueMap);
        assertFalse(qcContext.getErrors().toString(), isValid);
        assertTrue(qcContext.getErrors().toString()
                .contains("clinicalDateValidationMultipleBadDates.xml"));
    }

    @Test
    public void testDateValidationFutureDate()
            throws Processor.ProcessorException, IOException, SAXException,
            ParserConfigurationException {

        setupForNewXSD();
        setupDatesToValidate();
        final boolean isValid = runDateValidation(dateValidationFutureDate, dateNameToValueMap);

        assertFalse(qcContext.getErrors().toString(), isValid);
        Assert.assertEquals(1, qcContext.getErrorCount());
        Assert.assertEquals("An error occurred while processing XML file 'clinicalDateValidationFutureDate.xml': " +
                "Date '3/30/2199' is in the future, which is not valid.", qcContext.getErrors().get(0));
    }

    @Test
    public void testValidationWithNamespaceXML()
            throws Processor.ProcessorException {
        setupForNamespaceXSD();
        archive.setDeployLocation(testLocation + "/xmlWithNamespaces"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        archive.setArchiveFile(new File(testLocation + "/xmlWithNamespaces"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION));

        addMockBcrUtilsExpectation(false);

        final boolean isValid = clinicalXmlValidator
                .execute(archive, qcContext);
        assertFalse(qcContext.getErrors().toString(), isValid);
        assertEquals(qcContext.getErrors().toString(), 1, qcContext.getErrorCount());
    }

    @Test
    public void testFailedValidationWithNamespace()
            throws Processor.ProcessorException {
        // make sure it is finding and validating the dates
        setupForNamespaceXSD();
        archive.setDeployLocation(testLocation + "/badXmlWithNamespaces"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        archive.setArchiveFile(new File(testLocation + "/badXmlWithNamespaces"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION));

        final String barcode = "TCGA-00-0000";
        final String filename = "nationwidechildrens.org_clinical." + barcode + ".xml";

        addMockBcrUtilsExpectation(false);

        final boolean isValid = clinicalXmlValidator
                .execute(archive, qcContext);
        assertFalse(isValid);
        assertEquals(qcContext.getErrors().toString(), 2, qcContext.getErrorCount());
        assertEquals(
                "An error occurred while processing XML file '" + filename + "': Date '100/1/1950' for birth does not have the expected format (month '100' expected to be 1 or 2 digits).",
                qcContext.getErrors().get(0));
        assertEquals(
                "An error occurred while processing XML file '" + filename + "': Date '2/31/2010' for last_followup is not valid.",
                qcContext.getErrors().get(1));

    }

    /**
     * @param barcode             the barcode from the XML file
     * @param uuidFromXml         the uuid from the XML file -- null to mock no UUID given
     * @param uuidFromDb          the uuid from the db -- null to mock a new UUID
     * @param barcodeMappedToUuid the barcode mapped to the uuid in the database -- null to mock
     *                            no mapping
     * @param barcodeWillValidate whether the barcode should be valid or not
     * @param xmlFile             the xml file where all are found
     */
    private void addBarcodeExpectation(final String barcode,
                                       final String uuidFromXml, final String uuidFromDb,
                                       final String barcodeMappedToUuid,
                                       final boolean barcodeWillValidate, final String xmlFile,
                                       final String barcodeType) {
        context.checking(new Expectations() {
            {
                one(mockQcLiveBarcodeAndUUIDValidator).validateAnyBarcode(
                        barcode, qcContext, xmlFile, false, barcodeType);
                will(returnValue(barcodeWillValidate));
                if (barcodeWillValidate) {
                    if (uuidFromXml != null) {
                        one(mockQcLiveBarcodeAndUUIDValidator)
                                .validateUUIDFormat(uuidFromXml);
                        will(returnValue(true));

                        one(mockUuidService).getLatestBarcodeForUUID(
                                uuidFromXml);
                        will(returnValue(barcodeMappedToUuid));

                        one(mockUuidService).getUUIDForBarcode(barcode);
                        will(returnValue(uuidFromDb));
                    }
                }
            }
        });
    }

    @Test
    public void testWithUuidConflicts() throws Processor.ProcessorException {
        // uuid in XML, uuid associated with different barcode. barcode not
        // associated with a UUID.
        setupForNewXSD();
        archive.setDeployLocation(testLocation + "/uuidIngestion"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);

        final String barcode = "TCGA-00-0000";
        final String filename = "biospecimenFileWithUUIDs." + barcode + ".xml";

        addBarcodeExpectation("TCGA-00-0000-10A",
                "12345678-1111-1111-1111-abcdefabcdef", null,
                "TCGA-not-a-match-1", true, filename,
                "Sample");
        addBarcodeExpectation("TCGA-00-0000-10A-01",
                "12345678-2222-2222-2222-abcdefabcdef", null,
                "TCGA-not-a-match-2", true, filename,
                "Portion");
        addBarcodeExpectation("TCGA-00-0000-10A-01D",
                "12345678-3333-3333-3333-abcdefabcdef", null,
                "TCGA-not-a-match-3", true, filename,
                "Analyte");
        addBarcodeExpectation("TCGA-00-0000-10A-01D-A004-05",
                "12345678-4444-4444-4444-abcdefabcdef", null,
                "TCGA-a-different-barcode", true,
                filename, "Aliquot");
        addBarcodeExpectation("TCGA-00-0000-10A-01D-A078-02",
                "12345678-5555-5555-5555-abcdefabcdef", null,
                "TCGA-another-different-barcode", true,
                filename, "Aliquot");
        addBarcodeExpectation("TCGA-00-0000",
                "32a7b943-c30d-49cd-bb9b-81ad7a1018c9", null,
                "TCGA-unique-barcode-1", true, filename,
                "Patient");

        addPatientBarcodeExpectation(filename, barcode);

        addMockBcrUtilsExpectation(false);

        // with existing UUID in db that matches the XML UUID
        final File fileWIthUUIDs = new File(testLocation
                + "/uuidIngestion/" + filename);
        assertFalse(clinicalXmlValidator.processFile(fileWIthUUIDs, qcContext));
        assertEquals(qcContext.getErrors().toString(), 6,
                qcContext.getErrorCount());
        assertEquals(qcContext.getWarnings().toString(), 0,
                qcContext.getWarningCount());
        assertEquals(
                "patient TCGA-00-0000 is assigned UUID 32a7b943-c30d-49cd-bb9b-81ad7a1018c9 in the XML but the DCC has that UUID assigned to barcode TCGA-unique-barcode-1",
                qcContext.getErrors().get(0));
        assertEquals(
                "analyte TCGA-00-0000-10A-01D is assigned UUID 12345678-3333-3333-3333-abcdefabcdef in the XML but the DCC has that UUID assigned to barcode TCGA-not-a-match-3",
                qcContext.getErrors().get(3));
        assertEquals(
                "aliquot TCGA-00-0000-10A-01D-A004-05 is assigned UUID 12345678-4444-4444-4444-abcdefabcdef in the XML but the DCC has that UUID assigned to barcode TCGA-a-different-barcode",
                qcContext.getErrors().get(4));
    }

    @Test
    public void testWithBarcodeUuidConflict()
            throws Processor.ProcessorException {
        // TEST: uuid from XML not in db, but barcode has UUID (different)
        // assigned -- error
        setupForNewXSD();
        archive.setDeployLocation(testLocation + "/uuidIngestion"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);

        final String barcode = "TCGA-00-0000";
        final String filename = "biospecimenFileWithUUIDs." + barcode + ".xml";

        addBarcodeExpectation("TCGA-00-0000-10A",
                "12345678-1111-1111-1111-abcdefabcdef", "another-uuid-1", null,
                true, filename, "Sample");
        addBarcodeExpectation("TCGA-00-0000-10A-01",
                "12345678-2222-2222-2222-abcdefabcdef", "another-uuid-2", null,
                true, filename, "Portion");
        addBarcodeExpectation("TCGA-00-0000-10A-01D",
                "12345678-3333-3333-3333-abcdefabcdef", "another-uuid-3", null,
                true, filename, "Analyte");
        addBarcodeExpectation("TCGA-00-0000-10A-01D-A004-05",
                "12345678-4444-4444-4444-abcdefabcdef", "some-other-uuid",
                null, true, filename, "Aliquot");
        addBarcodeExpectation("TCGA-00-0000-10A-01D-A078-02",
                "12345678-5555-5555-5555-abcdefabcdef", "a-different-uuid",
                null, true, filename, "Aliquot");
        addBarcodeExpectation("TCGA-00-0000",
                "32a7b943-c30d-49cd-bb9b-81ad7a1018c9", null,
                "TCGA-unique-barcode-1", true, filename,
                "Patient");

        addPatientBarcodeExpectation(filename, barcode);
        addMockBcrUtilsExpectation(false);

        final File fileWIthUUIDs = new File(testLocation
                + "/uuidIngestion/" + filename);
        assertFalse(clinicalXmlValidator.processFile(fileWIthUUIDs, qcContext));
        assertEquals(6, qcContext.getErrorCount());
        assertEquals(
                "sample TCGA-00-0000-10A is assigned UUID 12345678-1111-1111-1111-abcdefabcdef in the XML, but the DCC has that sample barcode associated with UUID another-uuid-1",
                qcContext.getErrors().get(1));
        assertEquals(
                "analyte TCGA-00-0000-10A-01D is assigned UUID 12345678-3333-3333-3333-abcdefabcdef in the XML, but the DCC has that analyte barcode associated with UUID another-uuid-3",
                qcContext.getErrors().get(3));
        assertEquals(
                "aliquot TCGA-00-0000-10A-01D-A004-05 is assigned UUID 12345678-4444-4444-4444-abcdefabcdef in the XML, but the DCC has that aliquot barcode associated with UUID some-other-uuid",
                qcContext.getErrors().get(4));
    }

    @Test
    public void testWithInvalidUuids() throws Processor.ProcessorException {
        setupForNewXSD();
        archive.setDeployLocation(testLocation + "/uuidIngestion"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);

        final String barcode = "TCGA-00-0000";
        final String filename = "biospecimenWithInvalidUuids." + barcode + ".xml";

        addBarcodeExpectation("TCGA-00-0000-10A", "this-is-not-a-valid-uuid",
                "12345678-1111-1111-1111-abcdefabcdef", "TCGA-00-0000-10A",
                true, filename, "Sample");
        addBarcodeExpectation("TCGA-00-0000-10A-01",
                "12345678-2222-2222-2222-abcdefabcdef", null, null, true,
                filename, "Portion");
        addBarcodeExpectation("TCGA-00-0000-10A-01D",
                "12345678-3333-3333-3333-abcdefabcdef", null, null, true,
                filename, "Analyte");
        addBarcodeExpectation("TCGA-00-0000-10A-01D-A004-05",
                "12345678-4444-4444-4444-abcdefabcdef", null, null, true,
                filename, "Aliquot");
        addBarcodeExpectation("TCGA-00-0000-10A-01D-A078-02",
                "12345678-5555-5555-5555-abcdefabcdef", null, null, true,
                filename, "Aliquot");
        addBarcodeExpectation("TCGA-00-0000",
                "ef7b6923-f50b-46cc-b167-c1f4d78b8d91", null, null, true,
                filename, "Patient");

        addPatientBarcodeExpectation(filename, barcode);
        addMockBcrUtilsExpectation(false);

        final File fileWithInvalidUuids = new File(testLocation
                + "/uuidIngestion/" + filename);
        assertFalse(clinicalXmlValidator.processFile(fileWithInvalidUuids,
                qcContext));
        assertEquals(1, qcContext.getErrorCount());
        assertEquals(
                "sample TCGA-00-0000-10A is assigned UUID this-is-not-a-valid-uuid in the XML, but the DCC has that sample barcode associated with UUID 12345678-1111-1111-1111-abcdefabcdef",
                qcContext.getErrors().get(0));
    }

    @Test
    public void testWithUppercaseUuids() throws Processor.ProcessorException {
        setupForNewXSD();
        archive.setDeployLocation(testLocation + "/uuidIngestion"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);

        final String barcode = "TCGA-00-0000";
        final String filename = "biospecimenWithUppercaseUuids." + barcode + ".xml";

        // with existing UUID in db that matches the XML UUID once it is
        // lowercased
        final File fileWIthUUIDs = new File(testLocation
                + "/uuidIngestion/" + filename);

        // the UUIDs and barcodes are already mapped, so they are valid
        addBarcodeExpectation("TCGA-00-0000-10A",
                "12345678-1111-1111-1111-abcdefabcdef",
                "12345678-1111-1111-1111-abcdefabcdef", "TCGA-00-0000-10A",
                true, filename, "Sample");
        addBarcodeExpectation("TCGA-00-0000-10A-01",
                "12345678-2222-2222-2222-abcdefabcdef",
                "12345678-2222-2222-2222-abcdefabcdef", "TCGA-00-0000-10A-01",
                true, filename, "Portion");
        addBarcodeExpectation("TCGA-00-0000-10A-01D",
                "12345678-3333-3333-3333-abcdefabcdef",
                "12345678-3333-3333-3333-abcdefabcdef", "TCGA-00-0000-10A-01D",
                true, filename, "Analyte");
        addBarcodeExpectation("TCGA-00-0000-10A-01D-A004-05",
                "12345678-4444-4444-4444-abcdefabcdef",
                "12345678-4444-4444-4444-abcdefabcdef",
                "TCGA-00-0000-10A-01D-A004-05", true,
                filename, "Aliquot");
        addBarcodeExpectation("TCGA-00-0000-10A-01D-A078-02",
                "12345678-5555-5555-5555-abcdefabcdef",
                "12345678-5555-5555-5555-abcdefabcdef",
                "TCGA-00-0000-10A-01D-A078-02", true,
                filename, "Aliquot");
        addBarcodeExpectation("TCGA-00-0000",
                "0c539d3c-8c17-4f2f-b800-1e9ff3392bb8",
                "0c539d3c-8c17-4f2f-b800-1e9ff3392bb8", "TCGA-00-0000", true,
                filename, "Patient");

        addPatientBarcodeExpectation(filename, barcode);
        addMockBcrUtilsExpectation(false);

        clinicalXmlValidator.processFile(fileWIthUUIDs, qcContext);
        assertEquals(qcContext.getErrors().toString(), 0,
                qcContext.getErrorCount());
        assertEquals(qcContext.getWarnings().toString(), 0,
                qcContext.getWarningCount());
    }

    @Test
    public void testSetDateComparatorsString() throws Exception {

        clinicalXmlValidator.setDateComparatorsString("a>=b");

        assertEquals(2, clinicalXmlValidator.getDatesToCompare().size());
        assertTrue(clinicalXmlValidator.getDatesToCompare().contains("a"));
        assertTrue(clinicalXmlValidator.getDatesToCompare().contains("b"));

        assertEquals(1, clinicalXmlValidator.getDateComparators().size());

        final DateComparator dateComparator = clinicalXmlValidator
                .getDateComparators().get(0);
        assertNotNull(dateComparator);
        assertEquals("a", dateComparator.getLeftOperandName());
        assertEquals("b", dateComparator.getRightOperandName());
        assertEquals(DateComparator.Operator.GE, dateComparator.getOperator());
    }

    @Test
    public void testSetDateComparatorsStringSeveralNonUniqueNames()
            throws Exception {

        clinicalXmlValidator
                .setDateComparatorsString("a==b, c<>d, e<=f, g>=h, i<j, k>l, a<>c");

        assertEquals(12, clinicalXmlValidator.getDatesToCompare().size());
        assertTrue(clinicalXmlValidator.getDatesToCompare().contains("a"));
        assertTrue(clinicalXmlValidator.getDatesToCompare().contains("b"));
        assertTrue(clinicalXmlValidator.getDatesToCompare().contains("c"));
        assertTrue(clinicalXmlValidator.getDatesToCompare().contains("d"));
        assertTrue(clinicalXmlValidator.getDatesToCompare().contains("e"));
        assertTrue(clinicalXmlValidator.getDatesToCompare().contains("f"));
        assertTrue(clinicalXmlValidator.getDatesToCompare().contains("g"));
        assertTrue(clinicalXmlValidator.getDatesToCompare().contains("h"));
        assertTrue(clinicalXmlValidator.getDatesToCompare().contains("i"));
        assertTrue(clinicalXmlValidator.getDatesToCompare().contains("j"));
        assertTrue(clinicalXmlValidator.getDatesToCompare().contains("k"));
        assertTrue(clinicalXmlValidator.getDatesToCompare().contains("l"));

        assertEquals(7, clinicalXmlValidator.getDateComparators().size());
    }

    @Test(expected = SchemaException.class)
    public void testAllowURLSchemaWithLocalSchemaLocation() throws Exception {

        final File localSchemaLocationFile = new File(schemaLocationTestFile);
        FileUtil.writeContentToFile(getLocalSchemaData(),
                localSchemaLocationFile);

        ClinicalXmlValidator clinicalXmlValidator = getClinicalXMLValidator(
                localSchemaLocationFile, false, false);
        QCliveXMLSchemaValidator validator = new QCliveXMLSchemaValidator();
        validator.setValidXsdDomainPattern("tcga-data\\.nci\\.nih\\.gov");
        validator.setValidXsdPrefixPattern("bcr");
        validator.setValidXsdVersionPattern("2\\.4(\\.\\d*)?");

        clinicalXmlValidator.setqCliveXMLSchemaValidator(validator);

        clinicalXmlValidator.validateSchema(localSchemaLocationFile,
                getDocument(localSchemaLocationFile), qcContext);
    }

    @Test
    public void testValidateSchemaWithInvalidXml() throws Exception {
        final File schemaValidationFile = new File(schemaValidationInvalidXml);
        clinicalXmlValidator.validateSchema(schemaValidationFile,
                getDocument(schemaValidationFile), qcContext);
        assertEquals("Unexpected error ", 1, qcContext.getErrorCount());
    }

    @Test
    public void testValidateSchemaWithValidAuxiliary() throws Exception {

        clinicalXmlValidator.getqCliveXMLSchemaValidator().setValidXsdDomainPattern("tcga-data\\.nci\\.nih\\.gov");
 	 	clinicalXmlValidator.getqCliveXMLSchemaValidator().setValidXsdVersionPattern("2\\.6(\\.\\d*)?");

        final File schemaValidationFile = new File(schemaValidationValidAuxiliary);
        clinicalXmlValidator.validateSchema(schemaValidationFile, getDocument(schemaValidationFile), qcContext);

        assertEquals(qcContext.getErrors().toString(), 0, qcContext.getErrorCount());
    }

    @Test
    public void testValidateSchemaWithInvalidAuxiliary() throws Exception {

        clinicalXmlValidator.getqCliveXMLSchemaValidator().setValidXsdDomainPattern("tcga-data\\.nci\\.nih\\.gov");
        clinicalXmlValidator.getqCliveXMLSchemaValidator().setValidXsdVersionPattern("2\\.6(\\.\\d*)?");

        final File schemaValidationFile = new File(schemaValidationInvalidAuxiliary);
        clinicalXmlValidator.validateSchema(schemaValidationFile, getDocument(schemaValidationFile), qcContext);

        assertEquals(qcContext.getErrors().toString(), 1, qcContext.getErrorCount());
        assertTrue(qcContext.getErrors().get(0).contains("One of '{\"http://tcga.nci/bcr/xml/clinical/shared/2.6\":bcr_patient_barcode}' is expected."));
    }

    @Test
    public void testValidateXsdVersionValid() throws Exception {
        final File schemaValidationFile = new File(schemaValidationValidXml);
        clinicalXmlValidator.checkXsdVersion(schemaValidationFile,
                getDocument(schemaValidationFile), qcContext);
    }

    @Test
    public void testValidateXsdVersionInValid() throws Exception {
        final File schemaValidationFile = new File(schemaLocationTestFile);
        FileUtil.writeContentToFile(getURLSchemaDataWrongVersion(),
                schemaValidationFile);

        clinicalXmlValidator.checkXsdVersion(schemaValidationFile,
                getDocument(schemaValidationFile), qcContext);
        assertTrue(qcContext.getErrors().size() > 0);
        assertEquals(
                "An error occurred while processing XSD file 'schema_location_test.xml': Version '2.3' is unsupported.",
                qcContext.getErrors().get(0));
    }

    @Test
    public void testValidateXsdMissingSchemaLocation() throws Exception {
        final File schemaValidationFile = new File(schemaValidationValidXml);
        Document invalidXml = getDocument(schemaValidationFile);
        Node node = invalidXml.getElementsByTagName("bios:tcga_bcr").item(0);
        node.getAttributes().removeNamedItem("schemaVersion");
        clinicalXmlValidator.checkXsdVersion(schemaValidationFile, invalidXml,
                qcContext);
        assertTrue(qcContext.getErrors().size() > 0);
        assertEquals(
                "An error occurred while processing XML file 'intgen.org_biospecimen.TCGA-02-0004.xml': XML did not specify a version using 'schemaVersion'",
                qcContext.getErrors().get(0));
    }


    @Test
    public void testCheckShippedPortion() throws TransformerException, IOException, SAXException, XPathExpressionException, ParserConfigurationException {

        final String barcode = "TCGA-00-0000";
        final String filename = "shippedPortionGood_2.5." + barcode + ".xml";

        context.checking(new Expectations() {
            {
                one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat(
                        "D573317C-0B6E-475D-B950-237809987AA9");
                will(returnValue(true));
                allowing(mockCodeTableQueries).bcrCenterIdExists("20");
                will(returnValue(true));
            }
        });
        boolean portionsValid = clinicalXmlValidator.checkShippedPortion(
                new File(testLocation + "/shippedPortionValidation/" + filename),
                qcContext);
        assertTrue(qcContext.getErrors().toString(), portionsValid);
    }

    @Test
    public void testShippedPortionGood() throws ParserConfigurationException,
            IOException, SAXException, Processor.ProcessorException {
        setupForNewXSD();

        clinicalXmlValidator.getqCliveXMLSchemaValidator().setValidXsdDomainPattern("tcga-data\\.nci\\.nih\\.gov");
        clinicalXmlValidator.getqCliveXMLSchemaValidator().setValidXsdVersionPattern("2\\.6(\\.\\d*)?");

        archive.setDeployLocation(testLocation + "/shippedPortionValidation"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);

        final String barcode = "TCGA-00-0000";
        final String filename = "shippedPortionGood_2.5." + barcode + ".xml";

        final File shippedPortionGood = new File(testLocation
                + "/shippedPortionValidation/" + filename);

        // the UUIDs and barcodes are already mapped, so they are valid
        addBarcodeExpectation("TCGA-00-0000",
                "a6a863d9-b39d-4965-b47f-79a1bcf3c976",
                "a6a863d9-b39d-4965-b47f-79a1bcf3c976", "TCGA-00-0000", true,
                filename, "Patient");
        addBarcodeExpectation("TCGA-A1-A0SH-01A",
                "6f9b8101-1d09-44f5-9dba-aacb69eee129",
                "6f9b8101-1d09-44f5-9dba-aacb69eee129", "TCGA-A1-A0SH-01A",
                true, filename, "Sample");
        addBarcodeExpectation("TCGA-A1-A0SH-01A-11",
                "ac195d12-154b-4f6c-9c36-caa158a9cfa5",
                "ac195d12-154b-4f6c-9c36-caa158a9cfa5", "TCGA-A1-A0SH-01A-11",
                true, filename, "Portion");
        addBarcodeExpectation("TCGA-A1-A0SH-01A-11D",
                "56a1bcfa-7d39-405b-93ed-61dfa8bdb407",
                "56a1bcfa-7d39-405b-93ed-61dfa8bdb407", "TCGA-A1-A0SH-01A-11D",
                true, filename, "Analyte");
        addBarcodeExpectation("TCGA-A1-A0SH-01A-11D-A086-02",
                "fae672ab-375a-4313-9987-3b07460fb4f6",
                "fae672ab-375a-4313-9987-3b07460fb4f6",
                "TCGA-A1-A0SH-01A-11D-A086-02", true,
                filename, "Aliquot");
        addBarcodeExpectation("TCGA-A1-A0SH-01A-21-A10C-20",
                "d573317c-0b6e-475d-b950-237809987aa9",
                "d573317c-0b6e-475d-b950-237809987aa9",
                "TCGA-A1-A0SH-01A-21-A10C-20", true,
                filename, "Shipment Portion");

        context.checking(new Expectations() {
            {
                one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat(
                        "D573317C-0B6E-475D-B950-237809987AA9");
                will(returnValue(true));
                allowing(mockCodeTableQueries).bcrCenterIdExists("20");
                will(returnValue(true));
            }
        });

        addPatientBarcodeExpectation(filename, barcode);
        addMockBcrUtilsExpectation(false);

        boolean isValid = clinicalXmlValidator.processFile(shippedPortionGood, qcContext);
        assertTrue(qcContext.getErrors().toString(), isValid);
        assertEquals(qcContext.getErrors().toString(), 0,
                qcContext.getErrorCount());
        assertEquals(qcContext.getWarnings().toString(), 0,
                qcContext.getWarningCount());
    }

    @Test
    public void testShippedPortionNoShippedPortions()
            throws ParserConfigurationException, IOException, SAXException,
            Processor.ProcessorException {
        setupForNewXSD();
        archive.setDeployLocation(testLocation + "/shippedPortionValidation"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);

        final String barcode = "TCGA-00-0000";
        final String filename = "shippedPortionNoShippedPortions_2.4." + barcode + ".xml";

        final File shippedPortionGood = new File(
                testLocation
                        + "/shippedPortionValidation/" + filename);
        // the UUIDs and barcodes are already mapped, so they are valid
        addBarcodeExpectation("TCGA-00-0000",
                "a6a863d9-b39d-4965-b47f-79a1bcf3c976",
                "a6a863d9-b39d-4965-b47f-79a1bcf3c976", "TCGA-00-0000", true,
                filename, "Patient");
        addBarcodeExpectation("TCGA-A1-A0SH-01A",
                "6f9b8101-1d09-44f5-9dba-aacb69eee129",
                "6f9b8101-1d09-44f5-9dba-aacb69eee129", "TCGA-A1-A0SH-01A",
                true, filename, "Sample");
        addBarcodeExpectation("TCGA-A1-A0SH-01A-11",
                "ac195d12-154b-4f6c-9c36-caa158a9cfa5",
                "ac195d12-154b-4f6c-9c36-caa158a9cfa5", "TCGA-A1-A0SH-01A-11",
                true, filename, "Portion");
        addBarcodeExpectation("TCGA-A1-A0SH-01A-11D",
                "56a1bcfa-7d39-405b-93ed-61dfa8bdb407",
                "56a1bcfa-7d39-405b-93ed-61dfa8bdb407", "TCGA-A1-A0SH-01A-11D",
                true, filename, "Analyte");
        addBarcodeExpectation("TCGA-A1-A0SH-01A-11D-A086-02",
                "fae672ab-375a-4313-9987-3b07460fb4f6",
                "fae672ab-375a-4313-9987-3b07460fb4f6",
                "TCGA-A1-A0SH-01A-11D-A086-02", true,
                filename, "Aliquot");

        addPatientBarcodeExpectation(filename, barcode);
        addMockBcrUtilsExpectation(false);

        assertTrue(clinicalXmlValidator.processFile(shippedPortionGood,
                qcContext));
        assertEquals(qcContext.getErrors().toString(), 0,
                qcContext.getErrorCount());
        assertEquals(qcContext.getWarnings().toString(), 0,
                qcContext.getWarningCount());
    }

    @Test
    public void testShippedPortionBad() throws ParserConfigurationException,
            IOException, SAXException, Processor.ProcessorException {
        setupForNewXSD();
        archive.setDeployLocation(testLocation + "/shippedPortionValidation"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);

        final String barcode = "TCGA-00-0000";
        final String filename = "shippedPortionBad_2.4." + barcode + ".xml";

        final File shippedPortionGood = new File(testLocation
                + "/shippedPortionValidation/" + filename);
        // the UUIDs and barcodes are already mapped, so they are valid
        addBarcodeExpectation("TCGA-00-0000",
                "a6a863d9-b39d-4965-b47f-79a1bcf3c976",
                "a6a863d9-b39d-4965-b47f-79a1bcf3c976", "TCGA-00-0000", true,
                filename, "Patient");
        addBarcodeExpectation("TCGA-A1-A0SH-01A",
                "6f9b8101-1d09-44f5-9dba-aacb69eee129",
                "6f9b8101-1d09-44f5-9dba-aacb69eee129", "TCGA-A1-A0SH-01A",
                true, filename, "Sample");
        addBarcodeExpectation("TCGA-A1-A0SH-01A-11",
                "ac195d12-154b-4f6c-9c36-caa158a9cfa5",
                "ac195d12-154b-4f6c-9c36-caa158a9cfa5", "TCGA-A1-A0SH-01A-11",
                true, filename, "Portion");
        addBarcodeExpectation("TCGA-A1-A0SH-01A-11D",
                "56a1bcfa-7d39-405b-93ed-61dfa8bdb407",
                "56a1bcfa-7d39-405b-93ed-61dfa8bdb407", "TCGA-A1-A0SH-01A-11D",
                true, filename, "Analyte");
        addBarcodeExpectation("TCGA-A1-A0SH-01A-11D-A086-02",
                "fae672ab-375a-4313-9987-3b07460fb4f6",
                "fae672ab-375a-4313-9987-3b07460fb4f6",
                "TCGA-A1-A0SH-01A-11D-A086-02", true,
                filename, "Aliquot");
        addBarcodeExpectation("TCGA-A1-A0SH-01A-21-013A-20",
                "d573317c-0b6e-475d-b950-237809987aa9",
                "d573317c-0b6e-475d-b950-237809987aa9",
                "TCGA-A1-A0SH-01A-21-013A-20", true,
                filename, "Shipment Portion");

        addPatientBarcodeExpectation(filename, barcode);

        context.checking(new Expectations() {
            {
                allowing(mockBcrUtils).isAuxiliaryFile(with(any(File.class)));
                will(returnValue(false));

                one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat(
                        "D573317C-0B6E-475D-B950-237809987AA9");
                will(returnValue(false));
                allowing(mockCodeTableQueries).bcrCenterIdExists("20");
                will(returnValue(false));
            }
        });

        assertFalse(clinicalXmlValidator.processFile(shippedPortionGood,
                qcContext));
        assertEquals(qcContext.getErrors().toString(), 2,
                qcContext.getErrorCount());
        assertEquals(qcContext.getWarnings().toString(), 0,
                qcContext.getWarningCount());
        assertEquals(
                "An error occurred while processing XML file '" + filename + "': Shipped portion uuid : 'D573317C-0B6E-475D-B950-237809987AA9' in '" + filename + "': 'is not a valid shipped portion uuid",
                qcContext.getErrors().get(0));
        assertEquals(
                "An error occurred while processing XML file '" + filename + "': BCR centerid : '20' in '" + filename + "': 'is not a valid BCR center code",
                qcContext.getErrors().get(1));
    }


    @Test
    public void testExistingSlideBarcodes() throws Processor.ProcessorException {
        // NOTE: this XML has no UUIDs populated in it, but it does have empty
        // UUID elements
        setupForExistingSlideBarcodes();

        final String barcode1 = "TCGA-00-0000";
        final String filename1 = "nationwidechildrens.org_biospecimen." + barcode1 + ".xml";

        final String barcode2 = "TCGA-11-1111";
        final String filename2 = "nationwidechildrens.org_biospecimen." + barcode2 + ".xml";

        context.checking(new Expectations() {
            {
                exactly(32)
                        .of(mockQcLiveBarcodeAndUUIDValidator)
                        .validateAnyBarcode(
                                with(any(String.class)),
                                with(qcContext),
                                with(filename1),
                                with(false), with(any(String.class)));
                will(returnValue(true));

                exactly(29)
                        .of(mockQcLiveBarcodeAndUUIDValidator)
                        .validateAnyBarcode(
                                with(any(String.class)),
                                with(qcContext),
                                with(filename2),
                                with(false), with(any(String.class)));
                will(returnValue(true));
            }
        });

        addPatientBarcodeExpectation(filename1, barcode1);
        addPatientBarcodeExpectation(filename2, barcode2);
        addMockBcrUtilsExpectation(false);

        archive.setDeployLocation(testLocation
                + "/nationwidechildrens.org_COAD.bio.Level_1.30.2.0"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        archive.setArchiveFile(new File(testLocation
                + "/nationwidechildrens.org_COAD.bio.Level_1.30.2.0"
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION));
        final boolean isValid = clinicalXmlValidator
                .execute(archive, qcContext);
        assertTrue("Execution failed: " + qcContext.getErrors(), isValid);
    }

    @Test
    public void testCheckForUuidConflictsNoDuplicate() {
        final Map<String, String> barcodeToUuid = new HashMap<String, String>();
        final Map<String, String> uuidToBarcode = new HashMap<String, String>();

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat("uuid");
            will(returnValue(true));
            one(mockUuidService).getLatestBarcodeForUUID("uuid");
            will(returnValue("barcode"));
            one(mockUuidService).getUUIDForBarcode("barcode");
            will(returnValue("uuid"));
        }});

        assertTrue(clinicalXmlValidator.checkForUuidConflicts("barcode", "uuid", "TEST", barcodeToUuid, uuidToBarcode, qcContext));
        // it should have added the mapping in for this pair
        assertEquals("barcode", uuidToBarcode.get("uuid"));
        assertEquals("uuid", barcodeToUuid.get("barcode"));
    }

    @Test
    public void testCheckForUuidConflicts() {
        // APPS-2923 scenario 1: uuid and barcode repeated in file, but same association so okay
        final Map<String, String> barcodeToUuid = new HashMap<String, String>();
        final Map<String, String> uuidToBarcode = new HashMap<String, String>();

        barcodeToUuid.put("barcode", "uuid");
        uuidToBarcode.put("uuid", "barcode");

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat("uuid");
            will(returnValue(true));
            one(mockUuidService).getLatestBarcodeForUUID("uuid");
            will(returnValue("barcode"));
            one(mockUuidService).getUUIDForBarcode("barcode");
            will(returnValue("uuid"));
        }});

        assertTrue(clinicalXmlValidator.checkForUuidConflicts("barcode", "uuid", "TEST", barcodeToUuid, uuidToBarcode, qcContext));
    }

    @Test
    public void testCheckForDuplicateUuids() {
        // APPS-2923 scenario 2: same uuid in file with 2 different barcodes; uuid not in database
        final Map<String, String> barcodeToUuid = new HashMap<String, String>();
        final Map<String, String> uuidToBarcode = new HashMap<String, String>();

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat("someuuid");
            will(returnValue(true));
            one(mockUuidService).getLatestBarcodeForUUID("someuuid");
            will(returnValue(null));
            one(mockUuidService).getUUIDForBarcode("aBarcode");
            will(returnValue(null));
        }});

        barcodeToUuid.put("differentBarcode", "someuuid");
        uuidToBarcode.put("someuuid", "differentBarcode");
        assertFalse(clinicalXmlValidator.checkForUuidConflicts("aBarcode", "someUuid", "Sample", barcodeToUuid, uuidToBarcode, qcContext));
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("Sample aBarcode is assigned UUID someuuid in the XML, but differentBarcode is also assigned that UUID", qcContext.getErrors().get(0));
    }

    @Test
    public void testCheckForDuplicateUuidsInDatabase() {
        // APPS-2923 scenario 3: same uuid in file with 2 different barcodes; uuid linked to differentBarcode in database
        final Map<String, String> barcodeToUuid = new HashMap<String, String>();
        final Map<String, String> uuidToBarcode = new HashMap<String, String>();

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat("someuuid");
            will(returnValue(true));
            one(mockUuidService).getLatestBarcodeForUUID("someuuid");
            will(returnValue("differentBarcode"));
            one(mockUuidService).getUUIDForBarcode("aBarcode");
            will(returnValue(null));
        }});

        barcodeToUuid.put("differentBarcode", "someUuid");
        uuidToBarcode.put("someUuid", "differentBarcode");
        assertFalse(clinicalXmlValidator.checkForUuidConflicts("aBarcode", "someUuid", "Sample", barcodeToUuid, uuidToBarcode, qcContext));
        assertEquals(1, qcContext.getErrorCount());
        // because UUID associated to differentBarcode in database, we know that is the right one
        assertEquals("Sample aBarcode is assigned UUID someuuid in the XML but the DCC has that UUID assigned to barcode differentBarcode", qcContext.getErrors().get(0));
    }

    @Test
    public void testCheckForDuplicateBarcodes() {
        // APPS-2923 scenario 4: different UUIDs linked to same barcode in different places in the file; nothing in the database yet.
        final Map<String, String> barcodeToUuid = new HashMap<String, String>();
        final Map<String, String> uuidToBarcode = new HashMap<String, String>();

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat("uuid1");
            will(returnValue(true));
            one(mockUuidService).getLatestBarcodeForUUID("uuid1");
            will(returnValue(null));
            one(mockUuidService).getUUIDForBarcode("aBarcode");
            will(returnValue(null));
        }});

        barcodeToUuid.put("aBarcode", "uuid2");
        uuidToBarcode.put("uuid2", "aBarcode");
        assertFalse(clinicalXmlValidator.checkForUuidConflicts("aBarcode", "uuid1", "Portion", barcodeToUuid, uuidToBarcode, qcContext));
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("Portion aBarcode is assigned UUID uuid1 in the XML, but earlier in the file it was assigned UUID uuid2", qcContext.getErrors().get(0));
    }

    @Test
    public void testCheckForDuplicateBarcodesInDatabase() {
        // APPS-2923 scenario 5: different UUIDs linked to same barcode in different places in the file; barcode assocated to uuid2 in database
        final Map<String, String> barcodeToUuid = new HashMap<String, String>();
        final Map<String, String> uuidToBarcode = new HashMap<String, String>();

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat("uuid1");
            will(returnValue(true));
            one(mockUuidService).getLatestBarcodeForUUID("uuid1");
            will(returnValue(null));
            one(mockUuidService).getUUIDForBarcode("aBarcode");
            will(returnValue("uuid2"));
        }});

        barcodeToUuid.put("aBarcode", "uuid2");
        uuidToBarcode.put("uuid2", "aBarcode");
        assertFalse(clinicalXmlValidator.checkForUuidConflicts("aBarcode", "uuid1", "Portion", barcodeToUuid, uuidToBarcode, qcContext));
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("Portion aBarcode is assigned UUID uuid1 in the XML, but the DCC has that Portion barcode associated with UUID uuid2", qcContext.getErrors().get(0));
    }

    @Test
    public void testUuidDbConflictDifferentCase() {
        final Map<String, String> barcodeToUuid = new HashMap<String, String>();
        final Map<String, String> uuidToBarcode = new HashMap<String, String>();

        final String uuid = "d573317c-0b6e-475d-b950-237809987aa1";
        final String uuidUppercase = "D573317C-0B6E-475D-B950-237809987AA1";
        final String correctBarcode = "TCGA-GD-A0SH-01A-21";
        final String interloperBarcode = "TCGA-GD-A0SH-01A-22";

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat(uuid);
            will(returnValue(true));

            one(mockUuidService).getLatestBarcodeForUUID(uuid);
            will(returnValue(correctBarcode));

            one(mockUuidService).getUUIDForBarcode(interloperBarcode);
            will(returnValue(null));
        }});

        assertFalse(clinicalXmlValidator.checkForUuidConflicts(interloperBarcode, uuidUppercase, "Portion", barcodeToUuid, uuidToBarcode, qcContext));
    }

    private ClinicalXmlValidator getClinicalXMLValidator(
            final File localSchemaLocationFile, final Boolean isURL,
            final Boolean allowLocalSchema) {
        ClinicalXmlValidator clinicalXmlValidator = new ClinicalXmlValidator(
                mockQcLiveBarcodeAndUUIDValidator) {
            public Boolean getAllowLocalSchema() {
                return allowLocalSchema;
            }

            protected boolean isValidURL(final String value) {
                return isURL;
            }

            protected Source getSource(final Boolean isURL,
                                       final String schema, final File xmlFile) throws IOException {

                return new StreamSource(localSchemaLocationFile);

            }

            protected Boolean validateSchema(
                    final List<Source> schemaSourceList,
                    final Document document, final File xmlFile,
                    final QcContext context) throws SAXException, IOException {
                return true;
            }
        };

        return clinicalXmlValidator;
    }


    @Test
    public void validateSchema() throws Exception{
        final String xmlFile = SAMPLES_DIR+ "qclive/clinicalXmlValidator"+File.separator+"schemaValidation"+File.separator+"nationwidechildrens.org_biospecimen.TCGA-00-A000.xml";
        QCliveXMLSchemaValidator validator = new QCliveXMLSchemaValidator();
        validator.setValidXsdDomainPattern("tcga-data(-stg)?(-prod)?\\.nci\\.nih\\.gov");
        validator.setValidXsdPrefixPattern("bcr");
        validator.setValidXsdVersionPattern("2\\.6(\\.\\d*)?");
        final QcContext qcContext = new QcContext();
        boolean valid = validator.validateSchema(new File(xmlFile), qcContext,false, validator.getXSDURLPattern());
        assertTrue(qcContext.getErrors().toString(),valid);

    }


    private String getLocalSchemaData() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<cesc:tcga_bcr\n"
                + "	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                + "	xmlns:cesc=\"http://tcga.nci/bcr/xml/clinical/cesc/2.4\"\n"
                + "	xmlns:admin=\"http://tcga.nci/bcr/xml/administration/2.4\"\n"
                + "	xmlns:shared=\"http://tcga.nci/bcr/xml/clinical/shared/2.4\"\n"
                + "	xmlns:rad=\"http://tcga.nci/bcr/xml/clinical/radiation/2.4\"\n"
                + "	xmlns:rx=\"http://tcga.nci/bcr/xml/clinical/pharmaceutical/2.4\"\n"
                + "	schemaVersion=\"2.4\"\n"
                + "	xsi:schemaLocation=\"http://tcga.nci/bcr/xml/clinical/cesc/2.4 TCGA_BCR.CESC_Clinical.xsd\">\n"
                + "<admin:admin>\n" + "</admin:admin>\n"
                + "<cesc:patient></cesc:patient>\n"
                + "</cesc:tcga_bcr>";
    }

    private String getURLSchemaDataWrongVersion() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<cesc:tcga_bcr\n"
                + "	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                + "	xmlns:cesc=\"http://tcga.nci/bcr/xml/clinical/cesc/2.3\"\n"
                + "	xmlns:admin=\"http://tcga.nci/bcr/xml/administration/2.3\"\n"
                + "	xmlns:shared=\"http://tcga.nci/bcr/xml/clinical/shared/2.3\"\n"
                + "	xmlns:rad=\"http://tcga.nci/bcr/xml/clinical/radiation/2.3\"\n"
                + "	xmlns:rx=\"http://tcga.nci/bcr/xml/clinical/pharmaceutical/2.3\"\n"
                + "	schemaVersion=\"2.3\"\n"
                + "	xsi:schemaLocation=\"http://tcga.nci/bcr/xml/clinical/cesc http://tcga-data.nci.nih.gov/docs/xsd/BCR/tcga.nci/bcr/xml/clinical/cesc/2.3/TCGA_BCR.CESC_Clinical.xsd\">\n"
                + "<admin:admin>\n" + "</admin:admin>\n" + "</cesc:tcga_bcr>";
    }

    private Document getDocument(final File xmlFile) throws Exception {
        XPathXmlParser xPathXmlParser = new XPathXmlParser();
        return xPathXmlParser.parseXmlFile(xmlFile, false, true);
    }

    /**
     * Run the date comparison (after the date validation) and verify assertions
     *
     * @param datesToValidateString       the dates to validate String
     * @param expectedDatesToValidateSize the expected size of dates to validate
     * @param dateComparatorString        the dates comparator string
     * @param expectedDatesToCompareSize  the expected size of dates to compare
     * @param expectedDateComparatorsSize the expected size of dateComparators
     * @param dateValidationExpectedValid <code>true</code> if the date validation is expected valid, <code>false</code> otherwise
     * @param dateComparisonExpectedValid <code>true</code> if the date comparison is expected valid, <code>false</code> otherwise
     * @throws Exception
     */
    private void checkDateComparison(
            final String datesToValidateString,
            final int expectedDatesToValidateSize,
            final String dateComparatorString,
            final int expectedDatesToCompareSize,
            final int expectedDateComparatorsSize,
            final boolean dateValidationExpectedValid,
            final boolean dateComparisonExpectedValid) throws Exception {

        setupForNewXSD();

        clinicalXmlValidator.setDatesToValidateString(datesToValidateString);
        assertEquals(expectedDatesToValidateSize, clinicalXmlValidator
                .getDatesToValidate().size());

        clinicalXmlValidator.setDateComparatorsString(dateComparatorString);
        assertEquals(expectedDateComparatorsSize, clinicalXmlValidator
                .getDateComparators().size());
        assertEquals(expectedDatesToCompareSize, clinicalXmlValidator
                .getDatesToCompare().size());

        boolean isValid = runDateValidation(dateComparison, dateNameToValueMap);
        Assert.assertEquals(qcContext.getErrors().toString(), dateValidationExpectedValid, isValid);

        isValid &= clinicalXmlValidator.checkDatesComparison(dateNameToValueMap, datePrecisionMap,
                new File(dateComparison), qcContext);
        assertEquals(qcContext.getErrors().toString(), dateComparisonExpectedValid, isValid);

        if (!dateComparisonExpectedValid) {

            assertTrue(qcContext.getErrorCount() > 0);

            if (dateValidationExpectedValid) {
                for (String error : qcContext.getErrors()) {
                    assertTrue(error.contains("Date comparison error"));
                }

            } else {
                for (String error : qcContext.getErrors()) {
                    assertTrue(error.contains("does not have the expected format"));
                }
            }
        }
    }

    /**
     * Add an expectation for mockQcLiveBarcodeAndUUIDValidator.getPatientBarcode(filename)
     * that will return the given barcode
     *
     * @param filename the expected filename
     * @param barcode  the barcode returned by the call to the mock
     */
    private void addPatientBarcodeExpectation(final String filename, final String barcode) {
        context.checking(new Expectations() {{
            exactly(1).of(mockQcLiveBarcodeAndUUIDValidator).getPatientBarcode(filename);
            will(returnValue(barcode));
        }});
    }

    /**
     * Add an expectation for the call of methods on mockBcrUtils
     *
     * @param isAuxiliaryFileExpectedReturnValue
     *         the return value for isAuxiliaryFile() call
     */
    private void addMockBcrUtilsExpectation(final Boolean isAuxiliaryFileExpectedReturnValue) {

        context.checking(new Expectations() {{

            if (isAuxiliaryFileExpectedReturnValue != null) {
                allowing(mockBcrUtils).isAuxiliaryFile(with(any(File.class)));
                will(returnValue(isAuxiliaryFileExpectedReturnValue));
            }
            allowing(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat(with(any(String.class)));
            will(returnValue(true));
            allowing(mockUuidService).getLatestBarcodeForUUID(with(any(String.class)));
            will(returnValue("TCGA-00-0000"));
            allowing(mockUuidService).getUUIDForBarcode(with(any(String.class)));
            will(returnValue("81b70c58-4a12-448c-a594-2ade44f6a0ae"));
        }});
    }
}
