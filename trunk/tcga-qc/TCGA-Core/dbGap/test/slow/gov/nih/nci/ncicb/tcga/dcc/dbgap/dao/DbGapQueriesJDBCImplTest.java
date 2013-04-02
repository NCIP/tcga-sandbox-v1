/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dbgap.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotation;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationCategory;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationClassification;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItem;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItemType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationNote;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.TissueSourceSite;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ClinicalMetaQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.TissueSourceSiteQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationSearchCriteria;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.ClinicalMetaQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.dbgap.DbGapSubmissionGenerator;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test class for DbGapQueriesJDBCImpl
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class DbGapQueriesJDBCImplTest extends DBUnitTestCase {
    private static final String SAMPLES_FOLDER = System.getProperty("user.dir") + "/dbGap/test/samples/";
    private static final String TEST_DATA_FILE = "queriesJdbcImpl/DbGapQueriesJDBCImpl_TestDB.xml";
    private static final String PROPERTIES_FILE = "tcgadata.properties";

    private ClinicalMetaQueries.ClinicalFile testClinicalFileByPatient;
    private ClinicalMetaQueries.ClinicalFile testClinicalFileBySample;
    private ClinicalMetaQueries.ClinicalFile testClinicalFileBySampleExtra;
    private DbGapQueriesJDBCImpl dbGapQueries;
    private List<DccAnnotation> rescindedRedactionList;
    private List<DccAnnotation> redactionList;
    private Boolean annotationQueriesCalled = false;

    public DbGapQueriesJDBCImplTest() throws IOException {
        super(SAMPLES_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);        
    }

    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.CLEAN_INSERT;
    }

    @Override
    protected DatabaseOperation getTearDownOperation() {
        return DatabaseOperation.DELETE_ALL;
    }

    @Override
    protected void setUpDatabaseConfig(final DatabaseConfig config) {
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new OracleDataTypeFactory());
    }
    
    public void setUp() throws Exception {
        super.setUp();

        rescindedRedactionList = new ArrayList<DccAnnotation>();
        redactionList = new ArrayList<DccAnnotation>();

        final ClinicalMetaQueriesJDBCImpl clinicalMetaQueries = new ClinicalMetaQueriesJDBCImpl();
        clinicalMetaQueries.setDataSource(getDataSource());
        dbGapQueries = new DbGapQueriesJDBCImpl(clinicalMetaQueries);
        dbGapQueries.setDataSource(getDataSource());

        final TissueSourceSiteQueries fakeTissueSourceSiteQueries = new TissueSourceSiteQueries() {
            public List<TissueSourceSite> getAllTissueSourceSites() {
                return null;
            }

            public List<TissueSourceSite> getAggregateTissueSourceSites() {
                return null;
            }

            public List<String> getDiseasesForTissueSourceSiteCode(final String tissueSourceSiteCode) {
                return Arrays.asList("GBM");
            }
        };
        dbGapQueries.setTissueSourceSiteQueries(fakeTissueSourceSiteQueries);

        dbGapQueries.setAnnotationQueries(makeFakeAnnotationQueries());

        testClinicalFileByPatient = new ClinicalMetaQueries.ClinicalFile();
        testClinicalFileBySample = new ClinicalMetaQueries.ClinicalFile();
        testClinicalFileBySampleExtra = new ClinicalMetaQueries.ClinicalFile();
        testClinicalFileByPatient.byPatient = true;
        testClinicalFileBySample.byPatient = false;
        testClinicalFileBySampleExtra.byPatient = false;
        testClinicalFileByPatient.id = DbGapSubmissionGenerator.DbGapFile.SubjectsInfo.getFileId();
        testClinicalFileBySample.id = DbGapSubmissionGenerator.DbGapFile.Samples.getFileId();
        testClinicalFileBySampleExtra.id = 100;

        addFileColumn(testClinicalFileByPatient, "PATIENT_BARCODE", "PATIENT", null, 1, "Same as SUBJID", "");
        addFileColumn(testClinicalFileByPatient, "GENDER", "PATIENT", "PATIENT_ELEMENT", 3, "Gender of patient.", "", "MALE", "FEMALE", "");
        addFileColumn(testClinicalFileByPatient, "RADIATIONDOSAGE", "RADIATION", "RADIATION_ELEMENT", 5, "Amount of radiation a patient received.", "RADIATION.PATIENT_ID(+)=PATIENT.PATIENT_ID");

        addFileColumn(testClinicalFileBySample, "ALIQUOT_BARCODE", "ALIQUOT", null, 15, "Same as SAMPID", "ALIQUOT.ANALYTE_ID=ANALYTE.ANALYTE_ID AND ANALYTE.PORTION_ID=PORTION.PORTION_ID AND PORTION.SAMPLE_ID=SAMPLE.SAMPLE_ID");
        addFileColumn(testClinicalFileBySample, "GELIMAGEFILE", "ANALYTE", "ANALYTE_ELEMENT", 13, "Gel image filename", "ANALYTE.PORTION_ID=PORTION.PORTION_ID AND PORTION.SAMPLE_ID=SAMPLE.SAMPLE_ID");
        addFileColumn(testClinicalFileBySample, "SAMPLETYPE", "SAMPLE", "SAMPLE_ELEMENT", 12, "Type of sample", "");

        addFileColumn(testClinicalFileBySampleExtra, "ALIQUOT_BARCODE", "ALIQUOT", null, 15, "descr", "ALIQUOT.ANALYTE_ID=ANALYTE.ANALYTE_ID AND ANALYTE.PORTION_ID=PORTION.PORTION_ID AND PORTION.SAMPLE_ID=SAMPLE.SAMPLE_ID");
        addFileColumn(testClinicalFileBySampleExtra, "HISTOLOGICNUCLEARGRADE", "TUMORPATHOLOGY", "TUMORPATHOLOGY_ELEMENT", 14, "HISTOLOGICNUCLEARGRADE", "TUMORPATHOLOGY.SAMPLE_ID(+)=SAMPLE.SAMPLE_ID");
    }

    public void testGetClinicalDataSampleFile() {
        final List<List<String>> data = dbGapQueries.getClinicalData(testClinicalFileBySample);
        assertNotNull(data);
        assertEquals(5, data.size());
        assertEquals(3, data.get(0).size());
        verifyRow(data.get(0), new String[]{"1111-1111-1111-1111-1111", "image_file1", "Type1"});
        verifyRow(data.get(1), new String[] {"1111-1111-1111-1111-2222", "image_file1", "Type1"});
        verifyRow(data.get(2), new String[]{"1111-1111-2222-1111-1111", "image_file2", "Type1"});
        verifyRow(data.get(3), new String[] {"1111-2222-1111-1111-1111", "image_file3", "Type2"});
        verifyRow(data.get(4), new String[] {"1111-2222-1111-1111-2222", "image_file3", "Type2"});
    }

    public void testGetSubjFile() {
        final ClinicalMetaQueries.ClinicalFile subjFile = new ClinicalMetaQueries.ClinicalFile();
        subjFile.id = DbGapSubmissionGenerator.DbGapFile.Subjects.getFileId();

        final List<List<String>> subjData = dbGapQueries.getClinicalData(subjFile);
        // there are 6 records in biospecimen barcode
        assertEquals(6, subjData.size());
        assertEquals("TCGA-00-0001", subjData.get(0).get(0));
        assertEquals("GBM", subjData.get(0).get(1));
        assertEquals("1", subjData.get(0).get(2));
        assertTrue(annotationQueriesCalled);
    }

    public void testGetSubjFileWithRedaction() {
        DccAnnotation redaction = new DccAnnotation();
        DccAnnotationItem redactedItem = new DccAnnotationItem();
        redactedItem.setItem("TCGA-00-0006");
        redaction.addItem(redactedItem);
        redactionList.add(redaction);

        final ClinicalMetaQueries.ClinicalFile subjFile = new ClinicalMetaQueries.ClinicalFile();
        subjFile.id = DbGapSubmissionGenerator.DbGapFile.Subjects.getFileId();

        final List<List<String>> subjData = dbGapQueries.getClinicalData(subjFile);
        // there are 6, but one is redacted so should be excluded
        assertEquals(5, subjData.size());
        assertTrue(annotationQueriesCalled);
    }

    public void testGetSubjFileWithRescindedRedaction() {
        DccAnnotation redaction = new DccAnnotation();
        DccAnnotationItem redactedItem = new DccAnnotationItem();
        redactedItem.setItem("TCGA-00-0006");
        redaction.addItem(redactedItem);
        redactionList.add(redaction);

        rescindedRedactionList.add(redaction);

        final ClinicalMetaQueries.ClinicalFile subjFile = new ClinicalMetaQueries.ClinicalFile();
        subjFile.id = DbGapSubmissionGenerator.DbGapFile.Subjects.getFileId();

        final List<List<String>> subjData = dbGapQueries.getClinicalData(subjFile);
        assertEquals(6, subjData.size());
        assertTrue(annotationQueriesCalled);
    }

    public void testGetSubjSampleMappingFile() {
        DccAnnotation redaction = new DccAnnotation();
        DccAnnotationItem redactedItem = new DccAnnotationItem();
        redactedItem.setItem("TCGA-00-0006");
        redaction.addItem(redactedItem);
        redactionList.add(redaction);

        final ClinicalMetaQueries.ClinicalFile mappingFile = new ClinicalMetaQueries.ClinicalFile();
        mappingFile.id = DbGapSubmissionGenerator.DbGapFile.SubjectsToSamples.getFileId();

        final List<List<String>> mappingData = dbGapQueries.getClinicalData(mappingFile);
        assertEquals(5, mappingData.size());
        assertEquals("TCGA-00-0001-01C-01R-1234-03", mappingData.get(0).get(0));
        assertEquals("TCGA-00-0001", mappingData.get(0).get(1));
    }

    private void verifyRow(final List<String> actualData, final String[] expectedData) {
        for (int i=0; i<actualData.size(); i++) {
            if (expectedData[i] == null) {
                assertNull(actualData.get(i));
            } else {
                assertEquals(expectedData[i], actualData.get(i));
            }
        }
    }

    public void testGetClinicalDataPatientFile() {
        // call get clinical data on a test file
        final List<List<String>> data = dbGapQueries.getClinicalData(testClinicalFileByPatient);
        assertNotNull(data);
        // should be 4 rows and 3 columns in file
        assertEquals(4, data.size());
        assertEquals(3, data.get(0).size());

        verifyRow(data.get(0), new String[] {"1111", "FEMALE", "11"});
        verifyRow(data.get(1), new String[] {"2222", "MALE", "84"});
        verifyRow(data.get(2), new String[] {"3333", "MALE", null});
        verifyRow(data.get(3), new String[]{ "5555", null, null});
    }

    public void testGetClinicalDataExtraSampleFile() {
        final List<List<String>> data = dbGapQueries.getClinicalData(testClinicalFileBySampleExtra);
        verifyRow(data.get(0), new String[] {"1111-1111-1111-1111-1111", "Grade 1"});
        verifyRow(data.get(1), new String[]{"1111-1111-1111-1111-2222", "Grade 1"});
        verifyRow(data.get(2), new String[] {"1111-1111-2222-1111-1111", "Grade 1"});
        verifyRow(data.get(3), new String[] {"1111-2222-1111-1111-1111", "Grade 2"});
        verifyRow(data.get(4), new String[] {"1111-2222-1111-1111-2222", "Grade 2"});
    }

    protected void addFileColumn(final ClinicalMetaQueries.ClinicalFile clinicalFile, final String name,
                                 final String tableName, final String elementTableName, final Integer clinicalXsdElementId,
                                 final String descr, final String joinClause, final String... values) {
        final ClinicalMetaQueries.ClinicalFileColumn column = new ClinicalMetaQueries.ClinicalFileColumn();
        column.columnName = name;
        column.description = descr;
        column.tableName = tableName;
        column.tableColumnName = name;
        column.joinClause = joinClause;
        column.elementTableName = elementTableName;
        column.xsdElementId = clinicalXsdElementId;
        column.archiveLinkTableName = tableName + "_ARCHIVE";
        column.tableIdColumn = tableName + "_ID";

        if (values.length > 0) {
            column.values = Arrays.asList(values);
        }
        if (clinicalFile.columns == null) {
            clinicalFile.columns = new ArrayList<ClinicalMetaQueries.ClinicalFileColumn>();
        }
        clinicalFile.columns.add(column);
    }


    private AnnotationQueries makeFakeAnnotationQueries() {
        return new AnnotationQueries() {

            public long addNewAnnotation(final DccAnnotation annotation, final boolean useStrictItemValidation) throws AnnotationQueriesException {
                return -1;
            }

            public long addNewAnnotation(final DccAnnotation annotation) throws AnnotationQueriesException {
                return -1;
            }

            public List<DccAnnotationItemType> getItemTypes() {
                return null;
            }

            public List<DccAnnotationCategory> getAnnotationCategories() {
                return null;
            }

            public List<Tumor> getActiveDiseases() {
                return null;
            }

            public DccAnnotation getAnnotationById(final Long annotationId) throws AnnotationQueriesException {
                return null;
            }

            public Long addNewAnnotationNote(final Long annotationId, final DccAnnotationNote note) throws AnnotationQueriesException {
                return null;
            }

            public void editAnnotationNote(final DccAnnotationNote note, final String newText, final String editor) throws AnnotationQueriesException {
                // n/a
            }

            public DccAnnotationNote getAnnotationNoteById(final Long noteId) throws AnnotationQueriesException {
                return null;
            }


            public void updateAnnotation(final long annotationId, final DccAnnotation annotation, final boolean useStrictItemValidation) throws AnnotationQueriesException {
                // n/a
            }

            public void updateAnnotation(final DccAnnotation annotation, final boolean useStrictItemValidation) throws AnnotationQueriesException {
                // n/a
            }

            public void updateAnnotation(final DccAnnotation annotation) throws AnnotationQueriesException {
                // n/a
            }




            public List<DccAnnotation> searchAnnotations(final AnnotationSearchCriteria searchCriteria) {
                annotationQueriesCalled = true;
                if (searchCriteria.getCategoryId() != null && searchCriteria.getCategoryId() == 31L) {
                    // rescissions
                    return rescindedRedactionList;
                } else if (searchCriteria.getClassificationId() != null && searchCriteria.getClassificationId() == 5L) {
                    return redactionList;
                } else {
                    return new ArrayList<DccAnnotation>();
                }
            }

            public List<Long> findMatchingAnnotationIds(final AnnotationSearchCriteria searchCriteria) {
                return null;
            }

            public List<DccAnnotation> searchAllAnnotations() {
                return null;
            }

            public void setCurated(final DccAnnotation annotation, final boolean isCurated) throws AnnotationQueriesException {
                // n/a
            }

            public List<DccAnnotationClassification> getAnnotationClassifications() {
                return null;
            }
        };
    }

}
