package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.service.FileTypeLookupFromMap;
import gov.nih.nci.ncicb.tcga.dcc.common.service.StatusCallback;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContent;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContentImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.service.LoaderStarter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;

/**
 * TODO: Class description
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class AutoloaderCallerFastTest {

    private AutoloaderCaller autoloaderCaller = new TestableAutoloaderCaller();
    private Map<Integer, String[]> sdrfValues = new HashMap<Integer, String[]>();
    private String[] sdrfHeaders = new String[6];
    private TabDelimitedContent sdrf = new TabDelimitedContentImpl();
    private Archive mageTabArchive = new Archive();
    private List<Archive> deployedArchives = new ArrayList<Archive>();
    private Archive archive = new Archive();
    private FileTypeLookupFromMap lookup = new FileTypeLookupFromMap("archive", "center", "platform");

    private StatusCallback callback = new StatusCallback() {
        public void sendStatus(final Status status) {

        }
    };


    private Mockery context = new JUnit4Mockery();
    private LoaderStarter loaderStarter = context.mock(LoaderStarter.class);

    @Before
    public void setup() throws SchedulerException {

        setupSdrf(sdrf, sdrfHeaders, sdrfValues);

        mageTabArchive.setArchiveType(Archive.TYPE_MAGE_TAB);
        mageTabArchive.setDeployLocation("/path/to/mage-tab.tar.gz");
        deployedArchives.add(mageTabArchive);

        setupArchive(archive, "archive", lookup);
    }

    private void setupSdrf(TabDelimitedContent sdrf, String[] sdrfHeaders, Map<Integer, String[]> sdrfValues) {
        sdrfHeaders[0] = "Protocol REF";
        sdrfHeaders[1] = "Array Data Matrix File";
        sdrfHeaders[2] = "Comment [TCGA Archive Name]";
        sdrfHeaders[3] = "Comment [TCGA Data Level]";
        sdrfHeaders[4] = "Comment [TCGA Data Type]";
        sdrfHeaders[5] = "Comment [TCGA Include for Analysis]";
        sdrfValues.put(0, sdrfHeaders);
        sdrf.setTabDelimitedContents(sdrfValues);
        sdrf.setTabDelimitedHeader(sdrfHeaders);
    }

    private void setupArchive(final Archive archive, final String archiveName, final FileTypeLookupFromMap lookup) throws SchedulerException {
        archive.setSdrf(sdrf);
        archive.setRealName(archiveName);
        archive.setArchiveType(Archive.TYPE_LEVEL_2);
        archive.setDomainName("center");
        archive.setPlatform("platform");
        archive.setTumorType("GBM");
        archive.setDeployLocation("/path/to/" + archiveName + ".tar.gz");
        deployedArchives.add(archive);

        context.checking(new Expectations() {{
            one(loaderStarter).queueLoaderJob("/path/to/" + archiveName, "/path/to/mage-tab", lookup, callback, archive.getExperimentName());
        }});

        ((TestableAutoloaderCaller) autoloaderCaller).addArchiveLookup(archiveName, lookup);
    }

    @Test
    public void testOneFile() throws Processor.ProcessorException {
        // 1. test single file referenced in SDRF

        sdrfValues.put(1, new String[]{"center:PROTOCOL:platform:01", "file.data.txt", "archive", "Level 2", "type", "yes"});
        sdrfValues.put(2, new String[]{"center:PROTOCOL:platform:01", "file.data.txt", "archive", "Level 2", "type", "yes"});
        sdrfValues.put(3, new String[]{"center:PROTOCOL:platform:01", "file.data.txt", "archive", "Level 2", "type", "yes"});

        autoloaderCaller.execute(deployedArchives, new QcContext());

        assertEquals("PROTOCOL", lookup.lookupFileType("file.data.txt", "center", "platform"));
        assertEquals(1, lookup.getFileToTypeMap().size());
    }

    @Test
    public void testNoSuffix() throws Processor.ProcessorException {
        // 2. multiple file names with no prefix, same protocol

        sdrfValues.put(1, new String[]{"center:PROTOCOL:platform:01", "file1.data.txt", "archive", "Level 2", "type", "yes"});
        sdrfValues.put(2, new String[]{"center:PROTOCOL:platform:01", "file2.data.txt", "archive", "Level 2", "type", "yes"});
        sdrfValues.put(3, new String[]{"center:PROTOCOL:platform:01", "file3.data.txt", "archive", "Level 2", "type", "yes"});

        autoloaderCaller.execute(deployedArchives, new QcContext());
        assertEquals(3, lookup.getFileToTypeMap().size());
        assertEquals("PROTOCOL", lookup.lookupFileType("file1.data.txt", "center", "platform"));
        assertEquals("PROTOCOL", lookup.lookupFileType("file2.data.txt", "center", "platform"));
        assertEquals("PROTOCOL", lookup.lookupFileType("file3.data.txt", "center", "platform"));
    }

    @Test
    public void testIndividualSuffixes() throws Processor.ProcessorException {
        sdrfValues.put(1, new String[]{"center:PROTOCOL1:platform:01", "file1.s1.data.txt", "archive", "Level 2", "type", "yes"});
        sdrfValues.put(2, new String[]{"center:PROTOCOL2:platform:01", "file2.s2.data.txt", "archive", "Level 2", "type", "yes"});
        sdrfValues.put(3, new String[]{"center:PROTOCOL3:platform:01", "file3.s3.data.txt", "archive", "Level 2", "type", "yes"});

        autoloaderCaller.execute(deployedArchives, new QcContext());
        assertEquals(3, lookup.getFileToTypeMap().size());
        assertEquals("PROTOCOL1", lookup.lookupFileType("file1.s1.data.txt", "center", "platform"));
        assertEquals("PROTOCOL2", lookup.lookupFileType("file2.s2.data.txt", "center", "platform"));
        assertEquals("PROTOCOL3", lookup.lookupFileType("file3.s3.data.txt", "center", "platform"));
    }

    @Test
    public void testSingleSuffix() throws Processor.ProcessorException {
        // 3. multiple file names, with one prefix

        sdrfValues.put(1, new String[]{"center:PROTOCOL:platform:01", "file1.level_2.data.txt", "archive", "Level 2", "type", "yes"});
        sdrfValues.put(2, new String[]{"center:PROTOCOL:platform:01", "file2.level_2.data.txt", "archive", "Level 2", "type", "yes"});
        sdrfValues.put(3, new String[]{"center:PROTOCOL:platform:01", "file3.level_2.data.txt", "archive", "Level 2", "type", "yes"});

        autoloaderCaller.execute(deployedArchives, new QcContext());
        assertEquals(3, lookup.getFileToTypeMap().size());
        // the single suffix for all files means that there is only one type of level 2 file, so use protocol
        assertEquals("PROTOCOL", lookup.lookupFileType("file1.level_2.data.txt", "center", "platform"));
        assertEquals("PROTOCOL", lookup.lookupFileType("file2.level_2.data.txt", "center", "platform"));
        assertEquals("PROTOCOL", lookup.lookupFileType("file3.level_2.data.txt", "center", "platform"));
    }

    @Test
    public void testMultipleSuffixes() throws Processor.ProcessorException {
        // 4. multiple file names, multiple suffixes

        sdrfValues.put(1, new String[]{"center:PROTOCOL1:platform:01", "file1.s1.data.txt", "archive", "Level 2", "type", "yes"});
        sdrfValues.put(2, new String[]{"center:PROTOCOL1:platform:01", "file2.s1.data.txt", "archive", "Level 2", "type", "yes"});
        sdrfValues.put(3, new String[]{"center:PROTOCOL2:platform:01", "file3.s2.data.txt", "archive", "Level 2", "type", "yes"});
        sdrfValues.put(4, new String[]{"center:PROTOCOL2:platform:01", "file4.s2.data.txt", "archive", "Level 2", "type", "yes"});

        autoloaderCaller.execute(deployedArchives, new QcContext());
        assertEquals(4, lookup.getFileToTypeMap().size());
        assertEquals("PROTOCOL1", lookup.lookupFileType("file1.s1.data.txt", "center", "platform"));
        assertEquals("PROTOCOL1", lookup.lookupFileType("file2.s1.data.txt", "center", "platform"));
        assertEquals("PROTOCOL2", lookup.lookupFileType("file3.s2.data.txt", "center", "platform"));
        assertEquals("PROTOCOL2", lookup.lookupFileType("file4.s2.data.txt", "center", "platform"));

    }


    @Test
    public void testLevel2And3() throws Processor.ProcessorException {
        // 5. some not level 2

        Archive level3Archive = new Archive();
        level3Archive.setArchiveType(Archive.TYPE_LEVEL_3);
        deployedArchives.add(level3Archive);

        sdrfValues.put(1, new String[]{"center:PROTOCOL:platform:01", "file1.level_2.data.txt", "archive", "Level 2", "type", "yes"});
        sdrfValues.put(2, new String[]{"center:PROTOCOL:platform:01", "file2.level_2.data.txt", "archive", "Level 2", "type", "yes"});
        sdrfValues.put(3, new String[]{"center:PROTOCOL:platform:01", "file3.level_2.data.txt", "archive", "Level 2", "type", "yes"});
        sdrfValues.put(4, new String[]{"center:PROTOCOL3:platform:01", "file1.level_3.data.txt", "archive", "Level 3", "type", "yes"});
        sdrfValues.put(5, new String[]{"center:PROTOCOL3:platform:01", "file2.level_3.data.txt", "archive", "Level 3", "type", "yes"});
        sdrfValues.put(6, new String[]{"center:PROTOCOL3:platform:01", "file3.level_3.data.txt", "archive", "Level 3", "type", "yes"});

        autoloaderCaller.execute(deployedArchives, new QcContext());
        assertEquals(3, lookup.getFileToTypeMap().size());

        // make sure all level 2 files are in lookup
        assertEquals("PROTOCOL", lookup.lookupFileType("file1.level_2.data.txt", "center", "platform"));
        assertEquals("PROTOCOL", lookup.lookupFileType("file2.level_2.data.txt", "center", "platform"));
        assertEquals("PROTOCOL", lookup.lookupFileType("file3.level_2.data.txt", "center", "platform"));

        // make sure level 3 files are NOT in lookup
        assertNull(lookup.lookupFileType("file1.level_3.data.txt", "center", "platform"));
        assertNull(lookup.lookupFileType("file2.level_3.data.txt", "center", "platform"));
        assertNull(lookup.lookupFileType("file3.level_3.data.txt", "center", "platform"));

    }

    @Test
    public void testExcludeForAnalysis() throws Processor.ProcessorException {
        // 6. some use for analysis = no
        sdrfValues.put(1, new String[]{"center:PROTOCOL:platform:01", "file1.level_2.data.txt", "archive", "Level 2", "type", "yes"});
        sdrfValues.put(2, new String[]{"center:PROTOCOL:platform:01", "file2.level_2.data.txt", "archive", "Level 2", "type", "yes"});
        sdrfValues.put(3, new String[]{"center:PROTOCOL:platform:01", "file3.level_2.data.txt", "archive", "Level 2", "type", "yes"});
        sdrfValues.put(4, new String[]{"center:PROTOCOL:platform:01", "file4.level_2.data.txt", "archive", "Level 2", "type", "no"});
        sdrfValues.put(5, new String[]{"center:PROTOCOL:platform:01", "file5.level_2.data.txt", "archive", "Level 2", "type", "no"});
        sdrfValues.put(6, new String[]{"center:PROTOCOL:platform:01", "file6.level_2.data.txt", "archive", "Level 2", "type", "no"});

        autoloaderCaller.execute(deployedArchives, new QcContext());
        // should only be 3 files in lookup
        assertEquals(3, lookup.getFileToTypeMap().size());

        // make sure all "include = yes" files are in lookup
        assertEquals("PROTOCOL", lookup.lookupFileType("file1.level_2.data.txt", "center", "platform"));
        assertEquals("PROTOCOL", lookup.lookupFileType("file2.level_2.data.txt", "center", "platform"));
        assertEquals("PROTOCOL", lookup.lookupFileType("file3.level_2.data.txt", "center", "platform"));

        // make sure all "include = no" files are NOT
        assertNull(lookup.lookupFileType("file4.level_2.data.txt", "center", "platform"));
        assertNull(lookup.lookupFileType("file5.level_2.data.txt", "center", "platform"));
        assertNull(lookup.lookupFileType("file6.level_2.data.txt", "center", "platform"));
    }

    @Test
    public void testMultipleArchives() throws SchedulerException, Processor.ProcessorException {
        // 9. multiple archives, to make sure calls loader starter once per archive
        Archive archive2 = new Archive();
        FileTypeLookupFromMap lookup2 = new FileTypeLookupFromMap("archive2", "center", "platform");
        setupArchive(archive2, "archive2", lookup2); // this puts archive2 into deployedArchives list
        String[] sdrfHeaders2 = new String[6];
        Map<Integer, String[]> sdrfValues2 = new HashMap<Integer, String[]>();
        TabDelimitedContent sdrf2 = new TabDelimitedContentImpl();
        setupSdrf(sdrf2, sdrfHeaders2, sdrfValues2);

        sdrfValues.put(1, new String[]{"center:PROTOCOL1:platform:01", "file1.s1.data.txt", "archive", "Level 2", "type", "yes"});
        sdrfValues.put(2, new String[]{"center:PROTOCOL2:platform:01", "file2.s2.data.txt", "archive", "Level 2", "type", "yes"});
        sdrfValues.put(3, new String[]{"center:PROTOCOL3:platform:01", "file3.s3.data.txt", "archive", "Level 2", "type", "yes"});

        sdrfValues2.put(1, new String[]{"center:PROTOCOL1:platform:01", "file1.s1.data.txt", "archive", "Level 2", "type", "yes"});
        sdrfValues2.put(2, new String[]{"center:PROTOCOL1:platform:01", "file1.s1.data.txt", "archive", "Level 2", "type", "yes"});
        sdrfValues2.put(3, new String[]{"center:PROTOCOL1:platform:01", "file1.s1.data.txt", "archive", "Level 2", "type", "yes"});

        autoloaderCaller.execute(deployedArchives, new QcContext());
        assertEquals(3, lookup.getFileToTypeMap().size());
        assertEquals("PROTOCOL1", lookup.lookupFileType("file1.s1.data.txt", "center", "platform"));
        assertEquals("PROTOCOL2", lookup.lookupFileType("file2.s2.data.txt", "center", "platform"));
        assertEquals("PROTOCOL3", lookup.lookupFileType("file3.s3.data.txt", "center", "platform"));

        assertEquals(3, lookup2.getFileToTypeMap().size());
        assertEquals("PROTOCOL1", lookup2.lookupFileType("file1.s1.data.txt", "center", "platform"));
        assertEquals("PROTOCOL2", lookup2.lookupFileType("file2.s2.data.txt", "center", "platform"));
        assertEquals("PROTOCOL3", lookup2.lookupFileType("file3.s3.data.txt", "center", "platform"));

    }

    // 10. patterns specific from actual archives for all center/platform combinations


    @Test
    public void testBroadHtHg() throws Processor.ProcessorException {
        Map<String, String> broadHtHgTest = new HashMap<String, String>();
        broadHtHgTest.put("5500024056197041909864.H07.level2.data.txt", "broad.mit.edu:probeset_rma:HT_HG-U133A:01");
        broadHtHgTest.put("5500024056197041909864.H06.level2.data.txt", "broad.mit.edu:probeset_rma:HT_HG-U133A:01");
        broadHtHgTest.put("5500024056197041909864.H04.level2.data.txt", "broad.mit.edu:probeset_rma:HT_HG-U133A:01");

        loadSdrf(broadHtHgTest);
        autoloaderCaller.execute(deployedArchives, new QcContext());
        assertEquals(3, lookup.getFileToTypeMap().size());

        assertEquals("probeset_rma", lookup.lookupFileType("5500024056197041909864.H07.level2.data.txt", "center", "platform"));
        assertEquals("probeset_rma", lookup.lookupFileType("5500024056197041909864.H06.level2.data.txt", "center", "platform"));
        assertEquals("probeset_rma", lookup.lookupFileType("5500024056197041909864.H04.level2.data.txt", "center", "platform"));
    }

    @Test
    public void testHmsHgCgh() throws Processor.ProcessorException {
        Map<String, String> hmsHgCghTest = new HashMap<String, String>();
        hmsHgCghTest.put("hms.harvard.edu_GBM.HG-CGH-244A.9.data.txt", "hms.harvard.edu:lowess_global_normalization:HG-CGH-244A:01");
        loadSdrf(hmsHgCghTest);
        autoloaderCaller.execute(deployedArchives, new QcContext());
        assertEquals(1, lookup.getFileToTypeMap().size());
        assertEquals("lowess_global_normalization", lookup.lookupFileType("hms.harvard.edu_GBM.HG-CGH-244A.9.data.txt", "center", "platform"));
    }

    @Test
    public void testJhuHumanMethylation() throws Processor.ProcessorException {
        Map<String, String> files = new HashMap<String, String>();
        files.put("jhu-usc.edu_GBM.HumanMethylation27.1.lvl-2.TCGA-06-0875-01A-01D-0392-05.txt", "jhu-usc.edu:within_bioassay_data_set_function:HumanMethylation27:01");
        files.put("jhu-usc.edu_GBM.HumanMethylation27.1.lvl-2.TCGA-06-0876-01A-01D-0392-05.txt", "jhu-usc.edu:within_bioassay_data_set_function:HumanMethylation27:01");
        files.put("jhu-usc.edu_GBM.HumanMethylation27.1.lvl-2.TCGA-06-0877-01A-01D-0392-05.txt", "jhu-usc.edu:within_bioassay_data_set_function:HumanMethylation27:01");
        loadSdrf(files);
        autoloaderCaller.execute(deployedArchives, new QcContext());
        assertEquals(3, lookup.getFileToTypeMap().size());
        assertEquals("within_bioassay_data_set_function", lookup.lookupFileType("jhu-usc.edu_GBM.HumanMethylation27.1.lvl-2.TCGA-06-0875-01A-01D-0392-05.txt", "center", "platform"));
        assertEquals("within_bioassay_data_set_function", lookup.lookupFileType("jhu-usc.edu_GBM.HumanMethylation27.1.lvl-2.TCGA-06-0876-01A-01D-0392-05.txt", "center", "platform"));
        assertEquals("within_bioassay_data_set_function", lookup.lookupFileType("jhu-usc.edu_GBM.HumanMethylation27.1.lvl-2.TCGA-06-0877-01A-01D-0392-05.txt", "center", "platform"));
    }

    @Test
    public void testMskcc() throws Processor.ProcessorException {
        Map<String, String> files = new HashMap<String, String>();
        files.put("mskcc.org_GBM.HG-CGH-244A.8.data.txt", "mskcc.org:bioassay_data_transformation:HG-CGH-244A:01");
        loadSdrf(files);
        autoloaderCaller.execute(deployedArchives, new QcContext());
        assertEquals(1, lookup.getFileToTypeMap().size());
        assertEquals("bioassay_data_transformation", lookup.lookupFileType("mskcc.org_GBM.HG-CGH-244A.8.data.txt", "center", "platform"));
    }

    @Test
    public void testUncAgilent() throws Processor.ProcessorException {
        Map<String, String> files = new HashMap<String, String>();
        files.put("US45102955_251584710517_S01_GE2-v5_91_0806.txt_lmean.out.logratio.probe.tcga_level2.data.txt", "unc.edu:unc_lowess_normalization_probe_level:AgilentG4502A_07_1:01");
        files.put("US45102955_251584710516_S01_GE2-v5_91_0806.txt_lmean.out.logratio.probe.tcga_level2.data.txt", "unc.edu:unc_lowess_normalization_probe_level:AgilentG4502A_07_1:01");
        files.put("US45102955_251584710510_S01_GE2-v5_91_0806.txt_lmean.out.logratio.probe.tcga_level2.data.txt", "unc.edu:unc_lowess_normalization_probe_level:AgilentG4502A_07_1:01");
        loadSdrf(files);
        autoloaderCaller.execute(deployedArchives, new QcContext());
        assertEquals(3, lookup.getFileToTypeMap().size());
        assertEquals("unc_lowess_normalization_probe_level", lookup.lookupFileType("US45102955_251584710517_S01_GE2-v5_91_0806.txt_lmean.out.logratio.probe.tcga_level2.data.txt", "center", "platform"));
        assertEquals("unc_lowess_normalization_probe_level", lookup.lookupFileType("US45102955_251584710516_S01_GE2-v5_91_0806.txt_lmean.out.logratio.probe.tcga_level2.data.txt", "center", "platform"));
        assertEquals("unc_lowess_normalization_probe_level", lookup.lookupFileType("US45102955_251584710510_S01_GE2-v5_91_0806.txt_lmean.out.logratio.probe.tcga_level2.data.txt", "center", "platform"));
    }

    @Test
    public void testUncHmiRNA() throws Processor.ProcessorException {
        Map<String, String> files = new HashMap<String, String>();
        files.put("TCGA-02-0079-01A-01T-0309-07.probe.tcga_level2.data.txt", "unc.edu:unc_quantile_normalization:H-miRNA_8x15K:01");
        files.put("TCGA-06-0648-01A-01T-0309-07.probe.tcga_level2.data.txt", "unc.edu:unc_quantile_normalization:H-miRNA_8x15K:01");
        files.put("TCGA-12-0620-01A-01T-0309-07.probe.tcga_level2.data.txt", "unc.edu:unc_quantile_normalization:H-miRNA_8x15K:01");
        loadSdrf(files);
        autoloaderCaller.execute(deployedArchives, new QcContext());
        assertEquals(3, lookup.getFileToTypeMap().size());
        assertEquals("unc_quantile_normalization", lookup.lookupFileType("TCGA-02-0079-01A-01T-0309-07.probe.tcga_level2.data.txt", "center", "platform"));
        assertEquals("unc_quantile_normalization", lookup.lookupFileType("TCGA-06-0648-01A-01T-0309-07.probe.tcga_level2.data.txt", "center", "platform"));
        assertEquals("unc_quantile_normalization", lookup.lookupFileType("TCGA-12-0620-01A-01T-0309-07.probe.tcga_level2.data.txt", "center", "platform"));
    }

    @Test
    public void testBroadGenomeWideSNP() throws Processor.ProcessorException {
        Map<String, String> files = new HashMap<String, String>();
        files.put("COTES_p_TCGAaffxB8_9a_S_GenomeWideSNP_6_E10_293010.after_5NN.copynumber.data.txt", "broad.mit.edu:after_5NN_copy_number:Genome_Wide_SNP_6:01");
        files.put("COTES_p_TCGAaffxB8_9a_S_GenomeWideSNP_6_E10_293010.copynumber.byallele.data.txt", "broad.mit.edu:copynumber_byallele:Genome_Wide_SNP_6:01");
        files.put("COTES_p_TCGAaffxB8_9a_S_GenomeWideSNP_6_E10_293010.ismpolish.data.txt", "broad.mit.edu:invariantset_medianpolish:Genome_Wide_SNP_6:01");
        files.put("COTES_p_TCGAaffxB8_9a_S_GenomeWideSNP_6_E10_293010.no_outlier.copynumber.data.txt", "broad.mit.edu:no_outlier_copy_number:Genome_Wide_SNP_6:01");

        files.put("COTES_p_TCGAaffxB8_9a_S_GenomeWideSNP_6_E11_293012.after_5NN.copynumber.data.txt", "broad.mit.edu:after_5NN_copy_number:Genome_Wide_SNP_6:01");
        files.put("COTES_p_TCGAaffxB8_9a_S_GenomeWideSNP_6_E11_293012.copynumber.byallele.data.txt", "broad.mit.edu:copynumber_byallele:Genome_Wide_SNP_6:01");
        files.put("COTES_p_TCGAaffxB8_9a_S_GenomeWideSNP_6_E11_293012.copynumber.data.txt", "broad.mit.edu:copy_number:Genome_Wide_SNP_6:01");
        files.put("COTES_p_TCGAaffxB8_9a_S_GenomeWideSNP_6_E11_293012.ismpolish.data.txt", "broad.mit.edu:invariantset_medianpolish:Genome_Wide_SNP_6:01");
        files.put("COTES_p_TCGAaffxB8_9a_S_GenomeWideSNP_6_E11_293012.no_outlier.copynumber.data.txt", "broad.mit.edu:no_outlier_copy_number:Genome_Wide_SNP_6:01");

        files.put("COTES_p_TCGAaffxB8_9a_S_GenomeWideSNP_6_E12_293014.after_5NN.copynumber.data.txt", "broad.mit.edu:after_5NN_copy_number:Genome_Wide_SNP_6:01");
        files.put("COTES_p_TCGAaffxB8_9a_S_GenomeWideSNP_6_E12_293014.copynumber.byallele.data.txt", "broad.mit.edu:copynumber_byallele:Genome_Wide_SNP_6:01");
        files.put("COTES_p_TCGAaffxB8_9a_S_GenomeWideSNP_6_E12_293014.copynumber.data.txt", "broad.mit.edu:copy_number:Genome_Wide_SNP_6:01");
        files.put("COTES_p_TCGAaffxB8_9a_S_GenomeWideSNP_6_E12_293014.ismpolish.data.txt", "broad.mit.edu:invariantset_medianpolish:Genome_Wide_SNP_6:01");
        files.put("COTES_p_TCGAaffxB8_9a_S_GenomeWideSNP_6_E12_293014.no_outlier.copynumber.data.txt", "broad.mit.edu:no_outlier_copy_number:Genome_Wide_SNP_6:01");

        loadSdrf(files);
        autoloaderCaller.execute(deployedArchives, new QcContext());
        assertEquals(14, lookup.getFileToTypeMap().size());

        assertEquals("after_5NN_copy_number", lookup.lookupFileType("COTES_p_TCGAaffxB8_9a_S_GenomeWideSNP_6_E10_293010.after_5NN.copynumber.data.txt", "center", "platform"));
        assertEquals("copynumber_byallele", lookup.lookupFileType("COTES_p_TCGAaffxB8_9a_S_GenomeWideSNP_6_E10_293010.copynumber.byallele.data.txt", "center", "platform"));
        assertEquals("invariantset_medianpolish", lookup.lookupFileType("COTES_p_TCGAaffxB8_9a_S_GenomeWideSNP_6_E10_293010.ismpolish.data.txt", "center", "platform"));
        assertEquals("no_outlier_copy_number", lookup.lookupFileType("COTES_p_TCGAaffxB8_9a_S_GenomeWideSNP_6_E10_293010.no_outlier.copynumber.data.txt", "center", "platform"));
        assertEquals("after_5NN_copy_number", lookup.lookupFileType("COTES_p_TCGAaffxB8_9a_S_GenomeWideSNP_6_E11_293012.after_5NN.copynumber.data.txt", "center", "platform"));
        assertEquals("after_5NN_copy_number", lookup.lookupFileType("COTES_p_TCGAaffxB8_9a_S_GenomeWideSNP_6_E11_293012.after_5NN.copynumber.data.txt", "center", "platform"));
        assertEquals("copynumber_byallele", lookup.lookupFileType("COTES_p_TCGAaffxB8_9a_S_GenomeWideSNP_6_E11_293012.copynumber.byallele.data.txt", "center", "platform"));
        assertEquals("copy_number", lookup.lookupFileType("COTES_p_TCGAaffxB8_9a_S_GenomeWideSNP_6_E11_293012.copynumber.data.txt", "center", "platform"));
        assertEquals("invariantset_medianpolish", lookup.lookupFileType("COTES_p_TCGAaffxB8_9a_S_GenomeWideSNP_6_E11_293012.ismpolish.data.txt", "center", "platform"));
        assertEquals("no_outlier_copy_number", lookup.lookupFileType("COTES_p_TCGAaffxB8_9a_S_GenomeWideSNP_6_E11_293012.no_outlier.copynumber.data.txt", "center", "platform"));
        assertEquals("after_5NN_copy_number", lookup.lookupFileType("COTES_p_TCGAaffxB8_9a_S_GenomeWideSNP_6_E12_293014.after_5NN.copynumber.data.txt", "center", "platform"));
        assertEquals("copynumber_byallele", lookup.lookupFileType("COTES_p_TCGAaffxB8_9a_S_GenomeWideSNP_6_E12_293014.copynumber.byallele.data.txt", "center", "platform"));
        assertEquals("invariantset_medianpolish", lookup.lookupFileType("COTES_p_TCGAaffxB8_9a_S_GenomeWideSNP_6_E12_293014.ismpolish.data.txt", "center", "platform"));
        assertEquals("no_outlier_copy_number", lookup.lookupFileType("COTES_p_TCGAaffxB8_9a_S_GenomeWideSNP_6_E12_293014.no_outlier.copynumber.data.txt", "center", "platform"));
    }

    @Test
    public void testHAIBHuman1MDuo() throws Processor.ProcessorException {
        Map<String, String> files = new HashMap<String, String>();
        files.put("hudsonalpha.org_OV.Human1MDuo.1.1.0.B_Allele_Freq.txt", "hudsonalpha.org:B_allele_freq:Human1MDuo:02");
        files.put("hudsonalpha.org_OV.Human1MDuo.1.1.0.Delta_B_Allele_Freq.txt", "hudsonalpha.org:Delta_B_allele_freq:Human1MDuo:02");
        files.put("hudsonalpha.org_OV.Human1MDuo.1.1.0.Genotypes.txt", "hudsonalpha.org:genotyping:Human1MDuo:02");
        files.put("hudsonalpha.org_OV.Human1MDuo.1.1.0.Normal_LogR.txt", "hudsonalpha.org:Normal_LogR:Human1MDuo:02");
        files.put("hudsonalpha.org_OV.Human1MDuo.1.1.0.Paired_LogR.txt", "hudsonalpha.org:Paired_LogR:Human1MDuo:02");
        files.put("hudsonalpha.org_OV.Human1MDuo.1.1.0.Unpaired_LogR.txt", "hudsonalpha.org:Unpaired_LogR:Human1MDuo:02");

        loadSdrf(files);
        autoloaderCaller.execute(deployedArchives, new QcContext());
        assertEquals(6, lookup.getFileToTypeMap().size());

        assertEquals("B_allele_freq", lookup.lookupFileType("hudsonalpha.org_OV.Human1MDuo.1.1.0.B_Allele_Freq.txt", "center", "platform"));
        assertEquals("Delta_B_allele_freq", lookup.lookupFileType("hudsonalpha.org_OV.Human1MDuo.1.1.0.Delta_B_Allele_Freq.txt", "center", "platform"));
        assertEquals("genotyping", lookup.lookupFileType("hudsonalpha.org_OV.Human1MDuo.1.1.0.Genotypes.txt", "center", "platform"));
        assertEquals("Normal_LogR", lookup.lookupFileType("hudsonalpha.org_OV.Human1MDuo.1.1.0.Normal_LogR.txt", "center", "platform"));
        assertEquals("Paired_LogR", lookup.lookupFileType("hudsonalpha.org_OV.Human1MDuo.1.1.0.Paired_LogR.txt", "center", "platform"));
        assertEquals("Unpaired_LogR", lookup.lookupFileType("hudsonalpha.org_OV.Human1MDuo.1.1.0.Unpaired_LogR.txt", "center", "platform"));
    }


    private void loadSdrf(final Map<String, String> fileToProtocol) {
        int row = 1;
        for (String filename : fileToProtocol.keySet()) {
            sdrfValues.put(row++, new String[]{fileToProtocol.get(filename), filename, "archive", "Level 2", "type", "yes"});
        }
    }

    public class TestableAutoloaderCaller extends AutoloaderCaller {
        private Map<String, FileTypeLookupFromMap> lookups = new HashMap<String, FileTypeLookupFromMap>();

        public void addArchiveLookup(String archiveName, FileTypeLookupFromMap archiveLookup) {
            lookups.put(archiveName, archiveLookup);
        }

        protected LoaderStarter getLoaderStarter() {
            return loaderStarter;
        }

        protected FileTypeLookupFromMap getLookupObject(Archive a) {
            return lookups.get(a.getRealName());
        }

        protected StatusCallback makeStatusCallback(final Archive archive) {
            return callback;
        }
    }

}
