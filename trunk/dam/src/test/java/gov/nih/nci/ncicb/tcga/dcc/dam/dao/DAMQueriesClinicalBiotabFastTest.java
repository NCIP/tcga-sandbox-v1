package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ClinicalMetaQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileInfoQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.generation.FileGenerator;
import gov.nih.nci.ncicb.tcga.dcc.common.generation.FileGeneratorException;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileClinical;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetClinical;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Fast tests for DAMQueriesClinical
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class DAMQueriesClinicalBiotabFastTest {
    private Mockery context = new JUnit4Mockery();
    private ClinicalMetaQueries mockClinicalMetaDAO = context.mock( ClinicalMetaQueries.class );
    private DAMQueriesClinicalBiotab clinicalDAO = new DAMQueriesClinicalBiotab();
    private FileInfoQueries mockFileInfoQueries = context.mock(FileInfoQueries.class);
    private FileGenerator mockFileGenerator = context.mock(FileGenerator.class);
    private CommonBarcodeAndUUIDValidator mockCommonBarcodeAndUUIDValidator = context.mock(CommonBarcodeAndUUIDValidator.class);

   
    @Before
    public void setup() {
        clinicalDAO.setClinicalMetaQueries( mockClinicalMetaDAO );
        clinicalDAO.setDamUtils(DAMUtils.getInstance());
        clinicalDAO.setFileGenerator(mockFileGenerator);
        clinicalDAO.setCommonBarcodeAndUUIDValidator(mockCommonBarcodeAndUUIDValidator);
        clinicalDAO.setFileInfoQueries(mockFileInfoQueries);
    }

    @Test
    public void testGetFileInfo() throws DataAccessMatrixQueries.DAMQueriesException, FileGeneratorException {

        List<DataSet> selectedDataSets = new ArrayList<DataSet>();
        selectedDataSets.add(makeDataSet(false, "TCGA-1111-11-01", "diseaseType"));
        selectedDataSets.add(makeDataSet( false, "TCGA-3333-33-01", "diseaseType" ));
        final List<String> expectedBarcodes = Arrays.asList("TCGA-1111-11",
                                                            "TCGA-3333-33");
        final List<String> bcrXMLFilesLocation = Arrays.asList("clinical.xml","control.xml","auxiliary.xml","biospecimen.xml");
        final List<String> bioTabFiles = Arrays.asList("patient.txt","aliquot.txt", "control.txt", "auxiliary.txt");

        context.checking( new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).getPatientBarcode("TCGA-1111-11-01");
            will(returnValue("TCGA-1111-11"));
            one(mockCommonBarcodeAndUUIDValidator).getPatientBarcode("TCGA-3333-33-01");
            will(returnValue("TCGA-3333-33"));

            one(mockFileInfoQueries).getBCRXMLFileLocations(with(validateBarcodes(expectedBarcodes)));
            will(returnValue(bcrXMLFilesLocation));
            one(mockFileGenerator).generate("diseaseType",bcrXMLFilesLocation);
            will(returnValue(bioTabFiles));

        }});

        List<DataFile> dataFiles = clinicalDAO.getFileInfoForSelectedDataSets( selectedDataSets, true);
        assertEquals(4, dataFiles.size());

        Map<String, DataFile> dataFilesByName = new HashMap<String, DataFile>();
        for (final DataFile df : dataFiles) {
            dataFilesByName.put( df.getFileName(), df );
            assertFalse(df.mayPossiblyGenerateCacheFile());
            assertNotNull(((DataFileClinical)df).getDateAdded());
        }
        assertTrue(dataFilesByName.keySet().containsAll(bioTabFiles));


        String expectedPatientFile = "patient.txt";
        assertFalse(dataFilesByName.get(expectedPatientFile).isProtected());
        assertEquals( 2, dataFilesByName.get(expectedPatientFile).getSamples().size() );
        assertTrue(dataFilesByName.get(expectedPatientFile).getSamples().containsAll(Arrays.asList("TCGA-1111-11-01","TCGA-3333-33-01")));
        assertEquals(DataAccessMatrixQueries.CLINICAL_BIOTAB_CENTER, dataFilesByName.get(expectedPatientFile).getCenterId());

        assertEquals("diseaseType", dataFilesByName.get(expectedPatientFile).getDiseaseType());

    }

    @Test
    public void testGetFileInfoMultipleDisease() throws Exception {

        List<DataSet> selectedDataSets = new ArrayList<DataSet>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date date1 = sdf.parse("2012-04-08");
        Date date2 = sdf.parse("2012-05-03");
        final DataSet dataSet1 = makeDataSet(false, "TCGA-1111-11-01", "GBM");
        dataSet1.setDateAdded(date1);
        selectedDataSets.add(dataSet1);
        final DataSet dataSet2 = makeDataSet( false, "TCGA-3333-33-01", "BLCA" );
        dataSet2.setDateAdded(date2);
        selectedDataSets.add(dataSet2);

        final List<String> bcrXMLFilesLocation = Arrays.asList("clinical.xml","control.xml","auxiliary.xml","biospecimen.xml");
        final List<String> GBMBioTabFiles = Arrays.asList("patient_GBM.txt","aliquot_GBM.txt", "control_GBM.txt", "auxiliary_GBM.txt");
        final List<String> BLCABioTabFiles = Arrays.asList("patient_BLCA.txt","aliquot_BLCA.txt", "control_BLCA.txt", "auxiliary_BLCA.txt");



        context.checking( new Expectations() {{

            one(mockCommonBarcodeAndUUIDValidator).getPatientBarcode("TCGA-1111-11-01");
            will(returnValue("TCGA-1111-11"));
            one(mockCommonBarcodeAndUUIDValidator).getPatientBarcode("TCGA-3333-33-01");
            will(returnValue("TCGA-3333-33"));

            one(mockFileInfoQueries).getBCRXMLFileLocations(with(validateBarcodes(Arrays.asList("TCGA-1111-11"))));
            will(returnValue(bcrXMLFilesLocation));

            one(mockFileInfoQueries).getBCRXMLFileLocations(with(validateBarcodes(Arrays.asList("TCGA-3333-33"))));
            will(returnValue(bcrXMLFilesLocation));

            one(mockFileGenerator).generate("GBM",bcrXMLFilesLocation);
            will(returnValue(GBMBioTabFiles));

            one(mockFileGenerator).generate("BLCA",bcrXMLFilesLocation);
            will(returnValue(BLCABioTabFiles));

        }});

        List<DataFile> dataFiles = clinicalDAO.getFileInfoForSelectedDataSets( selectedDataSets, true);
        assertEquals(8, dataFiles.size());
        Map<String, DataFile> dataFilesByName = new HashMap<String, DataFile>();
        for (final DataFile df : dataFiles) {
            dataFilesByName.put( df.getFileName(), df );
            assertNotNull(((DataFileClinical)df).getDateAdded());
            if (df.getDiseaseType().equals("GBM")) {
                assertEquals("2012-04-08", sdf.format(((DataFileClinical)df).getDateAdded()));
            } else if (df.getDiseaseType().equals("BLCA")) {
                assertEquals("2012-05-03", sdf.format(((DataFileClinical)df).getDateAdded()));
            }
        }
        List<String> expectedBioTabFiles = new ArrayList<String>(GBMBioTabFiles);
        expectedBioTabFiles.addAll(BLCABioTabFiles);
        assertTrue(expectedBioTabFiles.containsAll(dataFilesByName.keySet()));


        final String expectedFile1 = "patient_GBM.txt";
        final String expectedFile2 = "patient_BLCA.txt";
        assertFalse(dataFilesByName.get(expectedFile1).isProtected());
        assertEquals( 1, dataFilesByName.get(expectedFile1).getSamples().size() );
        assertTrue(dataFilesByName.get(expectedFile1).getSamples().contains("TCGA-1111-11-01"));
        assertTrue(dataFilesByName.get(expectedFile2).getSamples().contains("TCGA-3333-33-01"));

        assertEquals(DataAccessMatrixQueries.CLINICAL_BIOTAB_CENTER, dataFilesByName.get(expectedFile1).getCenterId());

        assertEquals("GBM", dataFilesByName.get(expectedFile1).getDiseaseType());
        assertEquals("BLCA", dataFilesByName.get(expectedFile2).getDiseaseType());

    }

    private DataSet makeDataSet(final boolean isProtected, final String sample, final String diseaseType) {
        DataSet publicDataSet = new DataSetClinical();
        publicDataSet.setProtected( isProtected );
        publicDataSet.setPlatformTypeId(DataAccessMatrixQueries.CLINICAL_PLATFORMTYPE);
        publicDataSet.setCenterId(DataAccessMatrixQueries.CLINICAL_BIOTAB_CENTER);
        publicDataSet.setSample( sample );
        publicDataSet.setDiseaseType( diseaseType );
        publicDataSet.setDateAdded(new Date());
        return publicDataSet;
    }

    private Matcher<List<String>> validateBarcodes(final List<String> expectedBarcodes) {


           return new org.junit.internal.matchers.TypeSafeMatcher<List<String>>() {

               @Override
               public boolean matchesSafely(final List<String> barcodes) {
                   boolean result = (barcodes.size() == expectedBarcodes.size());
                   if(result){
                        result = expectedBarcodes.containsAll(barcodes);
                   }
                   return result;
               }

               public void describeTo(final Description description) {
                   description.appendText("Valid match");
               }
           };
    }


}

