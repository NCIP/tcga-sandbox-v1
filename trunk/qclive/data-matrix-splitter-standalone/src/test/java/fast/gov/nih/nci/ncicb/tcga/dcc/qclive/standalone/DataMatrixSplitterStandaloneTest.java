package gov.nih.nci.ncicb.tcga.dcc.qclive.standalone;


import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileToArchive;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BiospecimenToFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.DataMatrixFileBean;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ManifestValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.ArchiveCompressor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.ArchiveCompressorTarGzImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.Level2DataService;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertTrue;
/**
 * Test class for DataMatrixSplitterStandalone tool
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class DataMatrixSplitterStandaloneTest {
 private static final String TEST_DATA_FOLDER = System.getProperty("user.dir") +
            File.separator+
            "qclive"+
            File.separator+
            "data-matrix-splitter-standalone"+
            File.separator+
            "src"+
            File.separator+
            "test"+
            File.separator+
            "resources"+
            File.separator;

    private static final String SAMPLE_FOLDER = TEST_DATA_FOLDER+
            "samples"+
            File.separator;

    private static final String ARCHIVE_FOLDER = SAMPLE_FOLDER+
            "broad.cgcc_gbm.abi.1.1.0"+
            File.separator;

    private String manifest = ARCHIVE_FOLDER+
            ManifestValidator.MANIFEST_FILE;

    private String MULTIPLE_ALIQUOT_FILE = ARCHIVE_FOLDER+
            "broadlvl-2.txt";

    private String ARCHIVE_NAME = SAMPLE_FOLDER+"broad.cgcc_gbm.abi.1.1.0.tar.gz";
    private String  sdrf = ARCHIVE_FOLDER+"broad.sdrf.txt";

    private static final String appContextFile = "applicationContext-fast-test.xml";
    private static ApplicationContext appContext;
    private DataMatrixSplitterStandalone dataMatrixSplitterStandalone;
    private Mockery context = new JUnit4Mockery();
    private Level2DataService mockLevel2DataService =  context.mock(Level2DataService.class);
    private PlatformTransactionManager mockCommonTransactionManager = context.mock(PlatformTransactionManager.class, "common");
    private PlatformTransactionManager mockDiseaseTransactionManager = context.mock(PlatformTransactionManager.class, "disease");
    private ArchiveCompressorTarGzImpl archiveCompressorTarGz;

    @BeforeClass
    public static void runOnce() {
        appContext = new FileSystemXmlApplicationContext("file:" + TEST_DATA_FOLDER + appContextFile);
    }

    @Before
    public void setup()throws IOException {
        new File(ARCHIVE_FOLDER).mkdirs();
        createAliquotsDataMatrixFile(MULTIPLE_ALIQUOT_FILE);
        createManifestFile();
        createSDRFFile();
        dataMatrixSplitterStandalone = (DataMatrixSplitterStandalone) appContext.getBean("dataMatrixSplitterTool");
        dataMatrixSplitterStandalone.setLevel2DataService(mockLevel2DataService);
        dataMatrixSplitterStandalone.setCommonTransactionManager(mockCommonTransactionManager);
        dataMatrixSplitterStandalone.setDiseaseTransactionManager(mockDiseaseTransactionManager);
        archiveCompressorTarGz = new ArchiveCompressorTarGzImpl();
        dataMatrixSplitterStandalone.setArchiveCompressor(archiveCompressorTarGz);
    }


    @After
    public void cleanup() {
        FileUtil.deleteDir(new File(ARCHIVE_FOLDER));
        new File(ARCHIVE_NAME).delete();
        new File(ARCHIVE_NAME+".md5").delete();
    }

    @Test
    public void splitDataMatrixFiles() throws IOException{
        final Map<String,List<DataMatrixFileBean>> dataMatrixFilesByArchiveName = new HashMap<String,List<DataMatrixFileBean>>();
        final List<DataMatrixFileBean> dataMatrixFiles = new ArrayList<DataMatrixFileBean>();
        final DataMatrixFileBean dataMatrixFileBean = new DataMatrixFileBean();
        dataMatrixFileBean.setArchiveDeployLocation(ARCHIVE_NAME);
        dataMatrixFileBean.setFileName("broadlvl-2.txt");
        dataMatrixFileBean.setDataTypeId(1l);
        final Long archiveId = null;
        dataMatrixFiles.add(dataMatrixFileBean);
        dataMatrixFilesByArchiveName.put("broadv1-2.tar.gz",dataMatrixFiles);

        final Map<String,Integer> biospecimenIdsByBarcode = new HashMap<String,Integer>();
        biospecimenIdsByBarcode.put("TCGA-07-0227-20A-01D-0859-05", 1);
        biospecimenIdsByBarcode.put("TCGA-A3-3306-01A-01D-0859-05",2);

        context.checking(new Expectations() {{
            one(mockLevel2DataService).getMultipleAliquotDataMatrixFiles();
            will(returnValue(dataMatrixFilesByArchiveName));
            one(mockCommonTransactionManager).getTransaction(with(any(TransactionDefinition.class)));
            one(mockDiseaseTransactionManager).getTransaction(with(any(TransactionDefinition.class)));
            one(mockLevel2DataService).getSdrfFilePathForExperiment("broad.cgcc", "abi", "gbm");
            will(returnValue(sdrf));
            one(mockLevel2DataService).getBiospecimenIdsForBarcodes(with(validateBarcodes()));
            will(returnValue(biospecimenIdsByBarcode));
            one(mockLevel2DataService).addFiles(with(validateFiles()));
            one(mockLevel2DataService).addFileToArchiveAssociations(with(validateFileToArchives()));
            one(mockLevel2DataService).updateBiospecimenToFileAssociations(with(validateBiospecimenToFileAssociations()));
            one(mockLevel2DataService).deleteFiles(with(validateOldFiles()));
            one(mockLevel2DataService).deleteFileToArchiveAssociations(with(validateOldFiles()), with(archiveId));
            one(mockCommonTransactionManager).commit(with(any(TransactionStatus.class)));
            one(mockDiseaseTransactionManager).commit(with(any(TransactionStatus.class)));
        }});

        dataMatrixSplitterStandalone.splitDataMatrixFiles();
        final File compressedFile = new File(ARCHIVE_NAME);
        final File md5File = new File(ARCHIVE_NAME+".md5");
        assertTrue(compressedFile.exists());
        assertTrue(md5File.exists());

    }

    private File createAliquotsDataMatrixFile(final String dataMatrixFileName) throws IOException {
        final File dataMatrixFile = new File(dataMatrixFileName);

       final String data = "Hybridization REF\t\t\tB04_680888\t\t\tB05_680888\n" +
               "Composite Element REF\tChromosome\tPhysicalPosition\tBeta_Value\tBeta_Value\n" +
               "cg00000292\t51598\t4.433\t0.261571344339623\t0.261571344339623\n" +
               "cg00002426\t51597\t4.433\t0.928198433420366\t0.928198433420366\n" +
               "cg00003994\t51596\t4.433\t0.088645299654474\t0.088645299654474";
        FileUtil.writeContentToFile(data, dataMatrixFile);
        return dataMatrixFile;
    }

    private File createManifestFile() throws IOException {
        final File manifestFile = new File(manifest);
        final String data = "bd1c5eb29cb76508c8afddfbc6f8bf04 DESCRIPTION.txt\n" +
                "bd1c5eb29cb76508c8afddfbc6f8bf04 broadlvl-2.txt";
        FileUtil.writeContentToFile(data, manifestFile);
        return manifestFile;

    }

    private File createSDRFFile() throws IOException{
        final String sdrfData = "Extract Name\tProtocol REF\tLabeled Extract Name\tLabel\tTerm Source REF\tProtocol REF\tHybridization Name\tArray Design File\tTerm Source REF\tProtocol REF\tScan Name\tProtocol REF\tProtocol REF\tNormalization Name\tDerived Array Data Matrix File\tComment [TCGA Data Level]\tComment [TCGA Data Type]\tComment [TCGA Include for Analysis]\tComment [TCGA Archive Name]\tProtocol REF\tNormalization Name\tDerived Array Data Matrix File\tComment [TCGA Data Level]\tComment [TCGA Data Type]\tComment [TCGA Include for Analysis]\tComment [TCGA Archive Name]\tProtocol REF\tNormalization Name\tDerived Array Data Matrix File\tComment [TCGA Data Level]\tComment [TCGA Data Type]\tComment [TCGA Include for Analysis]\tComment [TCGA Archive Name]\n" +
                "TCGA-07-0227-20A-01D-0859-05\tjhu-usc.edu:labeling:HumanMethylation27:01\tTCGA-07-0227-20A-01D-0859-05\tbiotin\tMGED Ontology\tjhu-usc.edu:hybridization:HumanMethylation27:01\tB04_680888\tjhu-usc.edu_KIRP.HumanMethylation27.1.adf.txt\tcaArray\tjhu-usc.edu:image_acquisition:HumanMethylation27:01\tTCGA-07-0227-20A-01D-0859-05\tjhu-usc.edu:feature_extraction:HumanMethylation27:01\tjhu-usc.edu:within_bioassay_data_set_function:HumanMethylation27:01\tTCGA-07-0227-20A-01D-0859-05\tjhu-usc.edu_KIRP.HumanMethylation27.1.lvl-1.TCGA-07-0227-20A-01D-0859-05.txt\tLevel 1\tDNA-Methylation\tyes\tjhu-usc.edu_KIRP.HumanMethylation27.Level_1.1.0.0\tjhu-usc.edu:within_bioassay_data_set_function:HumanMethylation27:01\tTCGA-07-0227-20A-01D-0859-05\tbroadlvl-2.txt\tLevel 2\tDNA-Methylation\tyes\tjhu-usc.edu_KIRP.HumanMethylation27.Level_2.1.0.0\tjhu-usc.edu:within_bioassay_data_set_function:HumanMethylation27:01\tTCGA-07-0227-20A-01D-0859-05\tjhu-usc.edu_KIRP.HumanMethylation27.1.lvl-3.TCGA-07-0227-20A-01D-0859-05.txt\tLevel 3\tDNA-Methylation\tyes\tjhu-usc.edu_KIRP.HumanMethylation27.Level_3.1.0.0\n" +
                "TCGA-A3-3306-01A-01D-0859-05\tjhu-usc.edu:labeling:HumanMethylation27:01\tTCGA-A3-3306-01A-01D-0859-05\tbiotin\tMGED Ontology\tjhu-usc.edu:hybridization:HumanMethylation27:01\tB05_680888\tjhu-usc.edu_KIRP.HumanMethylation27.1.adf.txt\tcaArray\tjhu-usc.edu:image_acquisition:HumanMethylation27:01\tTCGA-A3-3306-01A-01D-0859-05\tjhu-usc.edu:feature_extraction:HumanMethylation27:01\tjhu-usc.edu:within_bioassay_data_set_function:HumanMethylation27:01\tTCGA-A3-3306-01A-01D-0859-05\tjhu-usc.edu_KIRP.HumanMethylation27.1.lvl-1.TCGA-A3-3306-01A-01D-0859-05.txt\tLevel 1\tDNA-Methylation\tyes\tjhu-usc.edu_KIRP.HumanMethylation27.Level_1.1.0.0\tjhu-usc.edu:within_bioassay_data_set_function:HumanMethylation27:01\tTCGA-A3-3306-01A-01D-0859-05\tbroadlvl-2.txt\tLevel 2\tDNA-Methylation\tyes\tjhu-usc.edu_KIRP.HumanMethylation27.Level_2.1.0.0\tjhu-usc.edu:within_bioassay_data_set_function:HumanMethylation27:01\tTCGA-A3-3306-01A-01D-0859-05\tjhu-usc.edu_KIRP.HumanMethylation27.1.lvl-3.TCGA-A3-3306-01A-01D-0859-05.txt\tLevel 3\tDNA-Methylation\tyes\tjhu-usc.edu_KIRP.HumanMethylation27.Level_3.1.0.0";

        final File sdrfFile = new File(sdrf);
        FileUtil.writeContentToFile(sdrfData,sdrfFile);
        return sdrfFile;
    }



    private Matcher<List<String>> validateBarcodes() {

        return new TypeSafeMatcher<List<String>>() {

            @Override
            public boolean matchesSafely(final List<String> barcodes) {
                if(barcodes.size() != 2){
                    return false;
                }
                final List<String> expectedBarcodes = Arrays.asList(new String[]{"TCGA-07-0227-20A-01D-0859-05", "TCGA-A3-3306-01A-01D-0859-05"});
                return expectedBarcodes.containsAll(barcodes);
            }

            public void describeTo(final Description description) {
                description.appendText("match file names");
            }
        };
    }
    private Matcher<List<FileInfo>> validateFiles() {

        return new TypeSafeMatcher<List<FileInfo>>() {

            @Override
            public boolean matchesSafely(final List<FileInfo> files) {
                if(files.size() != 2){
                    return false;
                }
                final List<String> expectedFiles = Arrays.asList(new String[]{"broadlvl-2.B04_680888.txt", "broadlvl-2.B05_680888.txt"});
                final List<String> actualFiles = Arrays.asList(new String[]{files.get(0).getFileName(),files.get(1).getFileName()});
                return expectedFiles.containsAll(actualFiles);
            }

            public void describeTo(final Description description) {
                description.appendText("match file names");
            }
        };
    }

    private Matcher<List<FileToArchive>> validateFileToArchives() {

        return new TypeSafeMatcher<List<FileToArchive>>() {

            @Override
            public boolean matchesSafely(final List<FileToArchive> fileToArchives) {
                if(fileToArchives.size() != 2){
                    return false;
                }
                final List<String> expectedFiles = Arrays.asList(new String[]{"broadlvl-2.B04_680888.txt", "broadlvl-2.B05_680888.txt"});
                final List<String> actualFiles = Arrays.asList(new String[]{fileToArchives.get(0).getFileInfo().getFileName(), fileToArchives.get(1).getFileInfo().getFileName()});
                return expectedFiles.containsAll(actualFiles);
            }

            public void describeTo(final Description description) {
                description.appendText("match file names");
            }
        };
    }


    private Matcher<List<BiospecimenToFile>> validateBiospecimenToFileAssociations() {

        return new TypeSafeMatcher<List<BiospecimenToFile>>() {

            @Override
            public boolean matchesSafely(final List<BiospecimenToFile> biospecimenToFiles) {
                if(biospecimenToFiles.size() != 2){
                    return false;
                }
                return true;
            }

            public void describeTo(final Description description) {
                description.appendText("match file names");
            }
        };
    }

   private Matcher<Map<String,FileInfo>> validateBiospecimenToOldFileAssociations() {

        return new TypeSafeMatcher<Map<String,FileInfo>>() {

            @Override
            public boolean matchesSafely(final Map<String,FileInfo> aliquotFileBeansByBarcode) {
                if(aliquotFileBeansByBarcode.size() != 2){
                    return false;
                }
                return true;
            }

            public void describeTo(final Description description) {
                description.appendText("match file names");
            }
        };
    }

    private Matcher<List<Long>> validateOldFiles() {

        return new TypeSafeMatcher<List<Long>>() {

            @Override
            public boolean matchesSafely(final List<Long> fileIds) {
                if(fileIds.size() != 1){
                    return false;
                }
                return true;
            }

            public void describeTo(final Description description) {
                description.appendText("match file names");
            }
        };
    }


}
