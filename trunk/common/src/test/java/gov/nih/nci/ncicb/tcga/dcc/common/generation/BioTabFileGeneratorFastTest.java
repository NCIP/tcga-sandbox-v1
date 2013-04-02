package gov.nih.nci.ncicb.tcga.dcc.common.generation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileCollection;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileCollectionQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class for BioTabFileGenerator
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class BioTabFileGeneratorFastTest {

    private static final String SAMPLE_DIR =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;

    private static final String sampleXMLFilesDir = SAMPLE_DIR
            + "bcrXmlFiles" + File.separator;

    private static final String bioTabFilesDir = SAMPLE_DIR
            + "biotabFiles" + File.separator;
    
    public static final String INTGEN_ORG = "intgen.org";
    
    public static final String NATIONWIDE_ORG = "nationwide.org";

    private final static String bioTabFilesTmpDir = SAMPLE_DIR +
            "tmp" + File.separator;

    private final static String bioTabFilesHomeDir = SAMPLE_DIR +
            "home" + File.separator;

    private static final String bioTabFilesEmptyElementDir = SAMPLE_DIR
            + "biotabFiles" + File.separator + "emptyElement" + File.separator;

    private static final String bioTabFilesEmptyValueDir = SAMPLE_DIR
            + "biotabFiles" + File.separator + "emptyValue" + File.separator;

    private final static List<String> GENERATED_CNTL_FILES_INTGEN = Arrays.asList(
            "intgen.org_control_cntl.txt",
            "intgen.org_biospecimen_aliquot_cntl.txt",
            "intgen.org_biospecimen_analyte_cntl.txt",
            "intgen.org_biospecimen_portion_cntl.txt",
            "intgen.org_biospecimen_protocol_cntl.txt",
            "intgen.org_biospecimen_sample_cntl.txt");

    private final static List<String> GENERATED_CNTL_FILES = Arrays.asList(
            "control_cntl.txt",
            "biospecimen_aliquot_cntl.txt",
            "biospecimen_analyte_cntl.txt",
            "biospecimen_portion_cntl.txt",
            "biospecimen_protocol_cntl.txt",
            "biospecimen_sample_cntl.txt");

    private final static List<String> GENERATED_FILES_INTGEN = Arrays.asList(
            "intgen.org_biospecimen_aliquot_ov.txt",
            "intgen.org_biospecimen_analyte_ov.txt",
            "intgen.org_clinical_patient_ov.txt",
            "intgen.org_biospecimen_portion_ov.txt",
            "intgen.org_biospecimen_protocol_ov.txt",
            "intgen.org_biospecimen_sample_ov.txt",
            "intgen.org_biospecimen_shipment_portion_ov.txt",
            "intgen.org_biospecimen_slide_ov.txt",
            "intgen.org_auxiliary_ov.txt");

    private final static List<String> GENERATED_FILES_NWCH = Arrays.asList(
            "nationwide.org_biospecimen_aliquot_ov.txt",
            "nationwide.org_biospecimen_analyte_ov.txt",
            "nationwide.org_clinical_drug_ov.txt",
            "nationwide.org_clinical_examination_ov.txt",
            "nationwide.org_clinical_patient_ov.txt",
            "nationwide.org_biospecimen_portion_ov.txt",
            "nationwide.org_biospecimen_protocol_ov.txt",
            "nationwide.org_clinical_radiation_ov.txt",
            "nationwide.org_biospecimen_sample_ov.txt",
            "nationwide.org_biospecimen_shipment_portion_ov.txt",
            "nationwide.org_biospecimen_slide_ov.txt",
            "nationwide.org_clinical_surgery_ov.txt",
            "nationwide.org_auxiliary_ov.txt",
            "nationwide.org_clinical_cqcf_ov.txt",
            "nationwide.org_clinical_follow_up_v2.1_ov.txt",
            "nationwide.org_biospecimen_cqcf_ov.txt",
            "nationwide.org_biospecimen_normal_control_ov.txt",
            "nationwide.org_biospecimen_tumor_sample_ov.txt"
            );

    private final static List<String> GENERATED_FILES_ALL = Arrays.asList(
            "biospecimen_aliquot_ov.txt",
            "biospecimen_analyte_ov.txt",
            "clinical_drug_ov.txt",
            "clinical_examination_ov.txt",
            "clinical_patient_ov.txt",
            "biospecimen_portion_ov.txt",
            "biospecimen_protocol_ov.txt",
            "clinical_radiation_ov.txt",
            "biospecimen_sample_ov.txt",
            "biospecimen_shipment_portion_ov.txt",
            "biospecimen_slide_ov.txt",
            "clinical_surgery_ov.txt",
            "auxiliary_ov.txt",
            "biospecimen_normal_control_ov.txt",
            "biospecimen_tumor_sample_ov.txt",
            "clinical_follow_up_v2.1_ov.txt",
            "clinical_cqcf_ov.txt",
            "biospecimen_cqcf_ov.txt"
            );

    private final static List<String> GENERATED_FILES_CLINICAL_EMPTY_ELEMENTS = Arrays.asList(
            "clinical_patient_ov.txt");

    private final static List<String> GENERATED_FILES_CLINICAL_EMPTY_VALUES = Arrays.asList(
            "clinical_patient_ov.txt");

    private final static String INTGEN_CNTL_DIR = bioTabFilesHomeDir +
            "cntl" + File.separator +
            "bcr" + File.separator +
            "intgen.org" + File.separator +
            "biotab" + File.separator +
            "clin" + File.separator;

    private static final String PUBLIC_CNTL_DIR_ALL = bioTabFilesHomeDir +
            "cntl" + File.separator +
            "bcr" + File.separator +
            "biotab" + File.separator +
            "clin" + File.separator;

    private final static String INTGEN_DIR = bioTabFilesHomeDir +
            "ov" + File.separator +
            "bcr" + File.separator +
            "intgen.org" + File.separator +
            "biotab" + File.separator +
            "clin" + File.separator;

    private final static String NWCH_DIR = bioTabFilesHomeDir +
            "ov" + File.separator +
            "bcr" + File.separator +
            "nationwide.org" + File.separator +
            "biotab" + File.separator +
            "clin" + File.separator;

    private static final String BIOTAB_FILES_HOME_DIR = bioTabFilesHomeDir +
            "ov" + File.separator +
            "bcr" + File.separator +
            "biotab" + File.separator +
            "clin" + File.separator;

    private static final String templatesDir =
            Thread.currentThread().getContextClassLoader().getResource("schema").getPath() + File.separator;

    private static final String appContextFile = "samples/applicationContext-fast.xml";
    private static ApplicationContext appContext;
    private Mockery context = new JUnit4Mockery();
    private BioTabFileGenerator bioTabFileGenerator;
    private FileArchiveQueries mockFileArchiveQueries;
    private String diseaseName = "OV";
    private FileCollectionQueries mockFileCollectionQueries;
    private BioTabDataProcessorUtil bioTabDataProcessorUtil;
    
    @Before
    public void setup() throws Exception {
        mockFileArchiveQueries = context.mock(FileArchiveQueries.class);
        mockFileCollectionQueries = context.mock(FileCollectionQueries.class);
        bioTabDataProcessorUtil = new BioTabDataProcessorUtil();

        appContext = new ClassPathXmlApplicationContext(appContextFile);
        bioTabFileGenerator = (BioTabFileGenerator) appContext.getBean("bioTabFileGenerator");

        bioTabFileGenerator.setTemplateFilesDir(templatesDir);
        bioTabFileGenerator.setFileArchiveQueries(mockFileArchiveQueries);
        bioTabFileGenerator.setBioTabFilesHomeDir(bioTabFilesHomeDir);
        bioTabFileGenerator.setBioTabFilesTmpDir(bioTabFilesTmpDir);

        bioTabFileGenerator.setFileCollectionQueries(mockFileCollectionQueries);
        bioTabFileGenerator.setBioTabDataProcessorUtil(bioTabDataProcessorUtil);
    }

    @After
    public void cleanup() {
        FileUtil.deleteDir(new File(bioTabFilesHomeDir));
        FileUtil.deleteDir(new File(bioTabFilesTmpDir));
    }

    @Test
    public void testFollowupXMLFiles() throws Exception{
        final FileCollection collectionBiotab = new FileCollection();
        collectionBiotab.setName("biotab");
        
        final FileCollection collectionBiotabIntgen = new FileCollection();
        collectionBiotabIntgen.setName("biotab.intgen");
        
        final FileCollection collectionBiotabNationwide = new FileCollection();
        collectionBiotabNationwide.setName("biotab.nwch");

        context.checking(new Expectations() {{
            one(mockFileArchiveQueries).getClinicalXMLFileLocations();
            will(returnValue(getFollowupXMLFiles()));

            atLeast(1).of(mockFileCollectionQueries).saveCollection(BioTabFileGenerator.BIOTAB, false, diseaseName, "BCR", null, null);
            will(returnValue(collectionBiotab));

            atLeast(1).of(mockFileCollectionQueries).saveCollection(BioTabFileGenerator.BIOTAB, false, diseaseName, "BCR", NATIONWIDE_ORG, null);
            will(returnValue(collectionBiotabNationwide));
        }});
        
        addExpectations(GENERATED_FILES_ALL, BIOTAB_FILES_HOME_DIR, collectionBiotab);
        addExpectations(GENERATED_FILES_INTGEN, INTGEN_DIR, collectionBiotabIntgen);
        addExpectations(GENERATED_FILES_NWCH, NWCH_DIR, collectionBiotabNationwide);

        bioTabFileGenerator.generate(diseaseName);
        assertTrue(new File(BIOTAB_FILES_HOME_DIR, "clinical_follow_up_v2.1_ov.txt").exists());
        assertTrue(new File(BIOTAB_FILES_HOME_DIR, "clinical_follow_up_v1.1_ov.txt").exists());
        assertTrue(new File(NWCH_DIR, "nationwide.org_clinical_follow_up_v2.1_ov.txt").exists());
        assertTrue(new File(NWCH_DIR ,"nationwide.org_clinical_follow_up_v1.1_ov.txt").exists());
    }


    @Test
    public void testGenerate() throws Exception {

        final FileCollection collectionBiotab = new FileCollection();
        collectionBiotab.setName("biotab");
        
        final FileCollection collectionBiotabIntgen = new FileCollection();
        collectionBiotabIntgen.setName("biotab.intgen");
        
        final FileCollection collectionBiotabNationwide = new FileCollection();
        collectionBiotabNationwide.setName("biotab.nwch");

        context.checking(new Expectations() {{
            one(mockFileArchiveQueries).getClinicalXMLFileLocations();
            will(returnValue(getXMLFiles()));

            atLeast(1).of(mockFileCollectionQueries).saveCollection(BioTabFileGenerator.BIOTAB, false, diseaseName, "BCR", null, null);
            will(returnValue(collectionBiotab));

            atLeast(1).of(mockFileCollectionQueries).saveCollection(BioTabFileGenerator.BIOTAB, false, diseaseName, "BCR", INTGEN_ORG, null);
            will(returnValue(collectionBiotabIntgen));
            
            atLeast(1).of(mockFileCollectionQueries).saveCollection(BioTabFileGenerator.BIOTAB, false, diseaseName, "BCR", NATIONWIDE_ORG, null);
            will(returnValue(collectionBiotabNationwide));
        }});

        addExpectations(GENERATED_FILES_ALL, BIOTAB_FILES_HOME_DIR, collectionBiotab);
        addExpectations(GENERATED_FILES_INTGEN, INTGEN_DIR, collectionBiotabIntgen);
        addExpectations(GENERATED_FILES_NWCH, NWCH_DIR, collectionBiotabNationwide);
        
        bioTabFileGenerator.generate(diseaseName);
        validateIntgenPublicBioTabFiles();
        validateNationwidePublicBioTabFiles();
        validatePublicBioTabFiles();
    }

    @Test
    public void testControlBiotabFiles() throws Exception {

        final FileCollection collectionBiotab = new FileCollection();
        collectionBiotab.setName("biotab");
        
        final FileCollection collectionBiotabIntgen = new FileCollection();
        collectionBiotabIntgen.setName("biotab.intgen");
        
        diseaseName = "CNTL";

        context.checking(new Expectations() {{
            one(mockFileArchiveQueries).getClinicalXMLFileLocations();
            will(returnValue(getCNTLXMLFiles()));

            atLeast(1).of(mockFileCollectionQueries).saveCollection(BioTabFileGenerator.BIOTAB, false, diseaseName, "BCR", null, null);
            will(returnValue(collectionBiotab));

            atLeast(1).of(mockFileCollectionQueries).saveCollection(BioTabFileGenerator.BIOTAB, false, diseaseName, "BCR", INTGEN_ORG, null);
            will(returnValue(collectionBiotabIntgen));
        }});

        addExpectations(GENERATED_FILES_ALL, BIOTAB_FILES_HOME_DIR, collectionBiotab);
        addExpectations(GENERATED_FILES_INTGEN, INTGEN_DIR, collectionBiotabIntgen);
        
        bioTabFileGenerator.generate(diseaseName);
        validateBioTabFiles(INTGEN_CNTL_DIR,GENERATED_CNTL_FILES_INTGEN, bioTabFilesDir, "intgen.org_clinical_cntl.tar.gz");
        validateBioTabFiles(PUBLIC_CNTL_DIR_ALL,GENERATED_CNTL_FILES, bioTabFilesDir, "clinical_cntl.tar.gz");
    }

    @Test
    public void testGenerateBioTabForSpecificXMLFiles() throws Exception {
        bioTabFileGenerator.generate(diseaseName, getBCRXMLFiles());
        validateBioTabFiles(bioTabFilesTmpDir, GENERATED_FILES_ALL, bioTabFilesDir, "");
    }

    @Test
    public void testGenerateBioTabEmptyElements() throws Exception {
        bioTabFileGenerator.generate(diseaseName, getEmptyElementTestXMLFiles());
        validateBioTabFiles(bioTabFilesTmpDir, GENERATED_FILES_CLINICAL_EMPTY_ELEMENTS, bioTabFilesEmptyElementDir, "");
    }

    @Test
    public void testGenerateBioTabEmptyValue() throws Exception {
        bioTabFileGenerator.generate(diseaseName, getEmptyValueTestXMLFiles());
        validateBioTabFiles(bioTabFilesTmpDir, GENERATED_FILES_CLINICAL_EMPTY_VALUES, bioTabFilesEmptyValueDir, "");
    }

    public void addExpectations(final List<String> filenames, final String directory, final FileCollection collection) {
        context.checking(new Expectations() {
            {
                for (final String publicFile : filenames) {
                    allowing(mockFileCollectionQueries).saveFileToCollection(with(collection), with(any(String.class)), with(any(Date.class)));
                }
            }
        });
    }

    public void validateIntgenPublicBioTabFiles() throws IOException {
        validateBioTabFiles(INTGEN_DIR, GENERATED_FILES_INTGEN, bioTabFilesDir, "intgen.org_clinical_ov.tar.gz");
    }

    public void validateNationwidePublicBioTabFiles() throws IOException {
        validateBioTabFiles(NWCH_DIR, GENERATED_FILES_NWCH, bioTabFilesDir, "nationwide.org_clinical_ov.tar.gz");
    }

    public void validatePublicBioTabFiles() throws IOException {
        validateBioTabFiles(BIOTAB_FILES_HOME_DIR, GENERATED_FILES_ALL, bioTabFilesDir, "clinical_ov.tar.gz");
    }

    private void validateBioTabFiles(final String actualDir,
                                     final List<String> fileNames,
                                     final String expectedDir,
                                     final String compressedFileName) throws IOException {
        for (final String fileName : fileNames) {
            final File actualFile = new File(actualDir, fileName);
            assertTrue(actualFile.exists());

            final String expectedData = FileUtil.readFile(new File(expectedDir, fileName), false);
            final String actualData = FileUtil.readFile(actualFile, false);
            System.out.println("Comparing file actual file: '" + actualFile + "' to expected file: '" + new File(expectedDir, fileName) + "'");
            assertEquals(expectedData, actualData);
        }
        if(!compressedFileName.isEmpty()){
            assertTrue(new File(actualDir, compressedFileName).exists());
        }

    }

    private Map<String, List<String>> getFollowupXMLFiles() {
        final Map<String, List<String>> xmlFilesByCenterName = new HashMap<String, List<String>>();
        List<String> xmlFileLocations = new ArrayList<String>();

        xmlFileLocations.add(sampleXMLFilesDir + "nationwide.org_clinical.TCGA-AA-0000.xml");
        xmlFileLocations.add(sampleXMLFilesDir + "nationwide.org_clinical.TCGA-AA-0001.xml");
        xmlFilesByCenterName.put(NATIONWIDE_ORG, xmlFileLocations);

        return xmlFilesByCenterName;
    }
    
    private List<String> getBCRXMLFiles(){
        final List<String> xmlFileLocations = new ArrayList<String>();

        xmlFileLocations.add(sampleXMLFilesDir + "intgen.org_biospecimen.TCGA-00-1111.xml");
        xmlFileLocations.add(sampleXMLFilesDir + "intgen.org_clinical.TCGA-00-1111.xml");
        xmlFileLocations.add(sampleXMLFilesDir + "intgen.org_auxiliary.TCGA-00-1111.xml");
        xmlFileLocations.add(sampleXMLFilesDir + "nationwide.org_biospecimen.TCGA-AA-0000.xml");
        xmlFileLocations.add(sampleXMLFilesDir + "nationwide.org_clinical.TCGA-AA-0000.xml");
        xmlFileLocations.add(sampleXMLFilesDir + "nationwide.org_auxiliary.TCGA-AA-0000.xml");

        return xmlFileLocations;
    }

    private List<String> getEmptyElementTestXMLFiles() {
        final List<String> xmlFileLocations = new ArrayList<String>();

        xmlFileLocations.add(sampleXMLFilesDir + "nationwidechildrens.org_clinical.TCGA-XX-XXXX.xml");
        xmlFileLocations.add(sampleXMLFilesDir + "nationwidechildrens.org_clinical.TCGA-YY-YYYY.xml");
        xmlFileLocations.add(sampleXMLFilesDir + "nationwidechildrens.org_clinical.TCGA-ZZ-ZZZZ.xml");

        return xmlFileLocations;
    }

    private List<String> getEmptyValueTestXMLFiles() {
        final List<String> xmlFileLocations = new ArrayList<String>();

        xmlFileLocations.add(sampleXMLFilesDir + "nationwidechildrens.org_clinical.TCGA-AA-AAAA.xml");
        xmlFileLocations.add(sampleXMLFilesDir + "nationwidechildrens.org_clinical.TCGA-BB-BBBB.xml");
        xmlFileLocations.add(sampleXMLFilesDir + "nationwidechildrens.org_clinical.TCGA-CC-CCCC.xml");

        return xmlFileLocations;
    }

    private Map<String, List<String>> getXMLFiles() {
        final Map<String, List<String>> xmlFilesByCenterName = new HashMap<String, List<String>>();
        List<String> xmlFileLocations = new ArrayList<String>();

        xmlFileLocations.add(sampleXMLFilesDir + "intgen.org_biospecimen.TCGA-00-1111.xml");
        xmlFileLocations.add(sampleXMLFilesDir + "intgen.org_clinical.TCGA-00-1111.xml");
        xmlFileLocations.add(sampleXMLFilesDir + "intgen.org_auxiliary.TCGA-00-1111.xml");
        xmlFilesByCenterName.put(INTGEN_ORG, xmlFileLocations);

        xmlFileLocations = new ArrayList<String>();
        xmlFileLocations.add(sampleXMLFilesDir + "nationwide.org_biospecimen.TCGA-AA-0000.xml");
        xmlFileLocations.add(sampleXMLFilesDir + "nationwide.org_clinical.TCGA-AA-0000.xml");
        xmlFileLocations.add(sampleXMLFilesDir + "nationwide.org_auxiliary.TCGA-AA-0000.xml");
        xmlFilesByCenterName.put(NATIONWIDE_ORG, xmlFileLocations);

        return xmlFilesByCenterName;
    }

    private Map<String, List<String>> getCNTLXMLFiles() {
        final Map<String, List<String>> xmlFilesByCenterName = new HashMap<String, List<String>>();
        List<String> xmlFileLocations = new ArrayList<String>();

        xmlFileLocations.add(sampleXMLFilesDir + "intgen.org_control.TCGA-AV-9999.xml");
        xmlFilesByCenterName.put(INTGEN_ORG, xmlFileLocations);

        return xmlFilesByCenterName;
    }

}
