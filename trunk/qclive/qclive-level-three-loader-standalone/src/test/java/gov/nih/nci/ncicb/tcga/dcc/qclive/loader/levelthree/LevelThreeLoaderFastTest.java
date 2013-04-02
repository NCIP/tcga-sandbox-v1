/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.loader.levelthree;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDDAO;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContent;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContentImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedContentNavigator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedFileParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc.LevelThreeQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.LoaderException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;

/**
 * Class used to unit test LevelThreeLoader
 *
 * @author Stanley Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class LevelThreeLoaderFastTest {

    private Mockery context = new JUnit4Mockery();
    private ArchiveQueries mockArchiveQueries = context.mock(ArchiveQueries.class);
    private LevelThreeQueries mockLthreeQueries = context.mock(LevelThreeQueries.class, "disease");
    private LevelThreeQueries mockCommonLevelThreeQueries = context.mock(LevelThreeQueries.class, "common");
    private UUIDDAO mockUuiddao = context.mock(UUIDDAO.class);
    private CommonBarcodeAndUUIDValidator mockCommonBarcodeAndUUIDValidator = context.mock(CommonBarcodeAndUUIDValidator.class);
    private Archive archive;
    private Center archiveCenter;

    private LevelThreeLoader loader;

    private static final String SAMPLE_DIR =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;

    private static final String JHU_MAGE_TAB_LOCATION = SAMPLE_DIR +
            "qclive" + File.separator +
            "LevelThreeLoader" + File.separator +
            "jhu-usc.edu_UCEC.HumanMethylation27.mage-tab.1.1.0";

    private static final String JHU_ARCHIVE_FILE_LOCATION = SAMPLE_DIR +
            "qclive" + File.separator +
            "LevelThreeLoader" + File.separator +
            "jhu-usc.edu_UCEC.HumanMethylation27.Level_3.1.0.0";

    private static final String MSK_ARCHIVE_FILE_LOCATION = SAMPLE_DIR +
            "qclive" + File.separator +
            "LevelThreeLoader" + File.separator +
            "mskcc.org_GBM.HG-CGH-244A.Level_3.1.5.0";

    private static final String MSK_ARCHIVE_MAGETAB_FILE_LOCATION = SAMPLE_DIR +
            "qclive" + File.separator +
            "LevelThreeLoader" + File.separator +
            "mskcc.org_GBM.HG-CGH-244A.mage-tab.1.5.0";

    private static final String HUDSONALPHA_ARCHIVE_FILE_LOCATION = SAMPLE_DIR +
            "qclive" + File.separator +
            "LevelThreeLoader" + File.separator +
            "hudsonalpha.org_OV.Human1MDuo.Level_3.1.2.0";

    private static final String GENOMEWUSTL_ARCHIVE_MAGETAB_FILE_LOCATION = SAMPLE_DIR +
            "qclive" + File.separator +
            "LevelThreeLoader" + File.separator +
            "genome.wustl.edu.Genome_Wide_SNP_6.mage-tab.1.5.0";

    private static final String GENOMEWUSTL_ARCHIVE_FILE_LOCATION = SAMPLE_DIR +
            "qclive" + File.separator +
            "LevelThreeLoader" + File.separator +
            "genome.wustl.edu.Genome_Wide_SNP_6.Level_3.1.2.0";

    private static final String HUDSONALPHA_ARCHIVE_MAGETAB_FILE_LOCATION = SAMPLE_DIR +
            "qclive" + File.separator +
            "LevelThreeLoader" + File.separator +
            "hudsonalpha.org_OV.Human1MDuo.mage-tab.9.3.0";

    private static final String TSV_ARCHIVE_FILE_LOCATION = SAMPLE_DIR +
            "qclive" + File.separator +
            "LevelThreeLoader" + File.separator +
            "hms.harvard.edu_GBM.HG-CGH-244A.Level_3.9.4.0";

    private static final String TSV_ARCHIVE_MAGETAB_FILE_LOCATION = SAMPLE_DIR +
            "qclive" + File.separator +
            "LevelThreeLoader" + File.separator +
            "hms.harvard.edu_GBM.HG-CGH-244A.mage-tab.1.15.0";

    private static final String BASE_ARCHIVE_FILE_LOCATION = SAMPLE_DIR +
            "qclive" + File.separator +
            "LevelThreeLoader" + File.separator;

    private static final String FIRMA_ARCHIVE_MAGETAB_FILE_LOCATION = SAMPLE_DIR +
            "qclive" + File.separator +
            "LevelThreeLoader" + File.separator +
            "lbl.gov_GBM.HuEx-1_0-st-v2.mage-tab.1.0.0";


    private static final String FIRMA_ARCHIVE_FILE_LOCATION = SAMPLE_DIR +
            "qclive" + File.separator +
            "LevelThreeLoader" + File.separator +
            "lbl.gov_GBM.HuEx-1_0-st-v2.Level_3.1.0.0";

    private static final String MDA_MDA_RPPACore_ARCHIVE_FILE_LOCATION = SAMPLE_DIR +
            "qclive" + File.separator +
            "LevelThreeLoader" + File.separator +
            "mdanderson.org_UCEC.MDA_RPPA_Core.Level_3.1.0.0";

    private static final String UNC_AgilentG4502A_07_1_ARCHIVE_FILE_LOCATION = SAMPLE_DIR +
            "qclive" + File.separator +
            "LevelThreeLoader" + File.separator +
            "unc.edu_KIRC.AgilentG4502A_07_1.Level_3.1.0.0";

    private static final String UNC_AgilentG4502A_07_1_ARCHIVE_MAGETAB_FILE_LOCATION = SAMPLE_DIR +
            "qclive" + File.separator +
            "LevelThreeLoader" + File.separator +
            "unc.edu_KIRC.AgilentG4502A_07_1.mage-tab.1.0.0";

    private static final String UNC_AgilentG4502A_07_2_ARCHIVE_FILE_LOCATION = SAMPLE_DIR +
            "qclive" + File.separator +
            "LevelThreeLoader" + File.separator +
            "unc.edu_KIRC.AgilentG4502A_07_2.Level_3.1.0.0";

    private static final String UNC_AgilentG4502A_07_2_ARCHIVE_MAGETAB_FILE_LOCATION = SAMPLE_DIR +
            "qclive" + File.separator +
            "LevelThreeLoader" + File.separator +
            "unc.edu_KIRC.AgilentG4502A_07_2.mage-tab.1.0.0";


    private static final String UNC_AgilentG4502A_07_3_ARCHIVE_FILE_LOCATION = SAMPLE_DIR +
            "qclive" + File.separator +
            "LevelThreeLoader" + File.separator +
            "unc.edu_KIRC.AgilentG4502A_07_3.Level_3.1.0.0";

    private static final String UNC_AgilentG4502A_07_3_ARCHIVE_MAGETAB_FILE_LOCATION = SAMPLE_DIR +
            "qclive" + File.separator +
            "LevelThreeLoader" + File.separator +
            "unc.edu_KIRC.AgilentG4502A_07_3.mage-tab.1.0.0";

    private static final String UNC_HT_HG_U133A_ARCHIVE_FILE_LOCATION = SAMPLE_DIR +
            "qclive" + File.separator +
            "LevelThreeLoader" + File.separator +
            "broad.mit.edu_KIRC.HT_HG_U133A.Level_3.1.0.0";

    private static final String UNC_HT_HG_U133A_ARCHIVE_MAGETAB_FILE_LOCATION = SAMPLE_DIR +
            "qclive" + File.separator +
            "LevelThreeLoader" + File.separator +
            "broad.mit.edu_KIRC.HT_HG_U133A.mage-tab.1.0.0";

    private static final String MIRNASEQ_ARCHIVE_LOCATION = SAMPLE_DIR +
            "qclive" + File.separator +
            "LevelThreeLoader" + File.separator +
            "bcgsc.ca_BRCA.IlluminaGA_miRNASeq.Level_3.1.0.0";

    private static final String RNASEQ_ARCHIVE_LOCATION = SAMPLE_DIR +
            "qclive" + File.separator +
            "LevelThreeLoader" + File.separator +
            "unc.edu_READ.IlluminaGA_RNASeq.Level_3.1.0.0";

    private static final String HISEQ_MIRNASEQ_ARCHIVE_LOCATION = SAMPLE_DIR +
        "qclive" + File.separator +
        "LevelThreeLoader" + File.separator +
        "bcgsc.ca_BRCA.IlluminaHiSeq_miRNASeq.Level_3.1.0.0";

    private static final String HISEQ_RNASEQ_ARCHIVE_LOCATION = SAMPLE_DIR +
            "qclive" + File.separator +
            "LevelThreeLoader" + File.separator +
            "unc.edu_READ.IlluminaHiSeq_RNASeq.Level_3.1.0.0";

    private static final String HISEQ_RNASEQV2_ARCHIVE_LOCATION = SAMPLE_DIR +
            "qclive" + File.separator +
            "LevelThreeLoader" + File.separator +
            "unc.edu_FOO.IlluminaHiSeq_RNASeq2.Level_3.1.0.0";

    private static final String ILLUMINAHISEQ_DNASEQC_ARCHIVE_LOCATION = SAMPLE_DIR +
            "qclive" + File.separator +
            "LevelThreeLoader" + File.separator +
            "hms.harvard.edu_READ.IlluminaHiSeq_DNASeqC.Level_3.1.0.0";


    private static final String ILLUMINAHISEQ_DNASEQC_MAGE_TAB_ARCHIVE_LOCATION = SAMPLE_DIR +
            "qclive" + File.separator +
            "LevelThreeLoader" + File.separator +
            "hms.harvard.edu_READ.IlluminaHiSeq_DNASeqC.mage-tab.1.0.0";

    private static final String MDA_RPPA_CORE_MAGE_TAB_ARCHIVE_LOCATION = SAMPLE_DIR +
                "qclive" + File.separator +
                "LevelThreeLoader" + File.separator +
                "mdanderson.org_UCEC.MDA_RPPA_Core.mage-tab.1.1.0";

    private static final String BROAD_SNP_6_LEVEL_3_LOCATION = SAMPLE_DIR + "qclive" + File.separator +
            "LevelThreeLoader" + File.separator + "broad.mit.edu_BLCA.Genome_Wide_SNP_6.Level_3.1.0.0";

    private static final String BROAD_SNP_6_MAGE_TAB_LOCATION = SAMPLE_DIR + "qclive" + File.separator +
            "LevelThreeLoader" + File.separator + "broad.mit.edu_BLCA.Genome_Wide_SNP_6.mage-tab.1.0.0";

    @Before
    public void setUp() {
        // all centers set up
        loader = new LevelThreeLoader();
        loader.setArchiveQueries(mockArchiveQueries);
        loader.setLevelThreeQueries(mockLthreeQueries);
        loader.setCommonLevelThreeQueries(mockCommonLevelThreeQueries);
        loader.setCommonBarcodeAndUUIDValidator(mockCommonBarcodeAndUUIDValidator);
        loader.setUuiddao(mockUuiddao);
    }

    private void setUpNewSnp6() {
        List<CenterPlatformPattern> patterns = new ArrayList<CenterPlatformPattern>();
        CenterPlatformPattern broadSnp6 = new CenterPlatformPattern("broad.mit.edu", "Genome_Wide_SNP_6",
                Arrays.asList("*.hg18.seg.txt", "*.hg19.seg.txt", "*.nocnv_hg18.seg.txt", "*.nocnv_hg19.seg.txt"));
        patterns.add(broadSnp6);
        loader.setPatterns(patterns);

        archive = new Archive(BROAD_SNP_6_LEVEL_3_LOCATION + ".tar.gz");
        archive.setDeployLocation(BROAD_SNP_6_LEVEL_3_LOCATION + ".tar.gz");
        Platform platform = new Platform();
        platform.setPlatformName("Genome_Wide_SNP_6");
        platform.setPlatformId(1);
        archive.setThePlatform(platform);
        archive.setPlatform("Genome_Wide_SNP_6");

        archive.setDomainName("broad.mit.edu");
        archive.setTumorType("BLCA");
        Tumor tumor = new Tumor();
        tumor.setTumorName("BLCA");
        archive.setTheTumor(tumor);

        archiveCenter = new Center();
        archiveCenter.setCenterId(2);
        archiveCenter.setCenterName("broad.mit.edu");
        archive.setTheCenter(archiveCenter);

        archive.setRealName("broad.mit.edu_BLCA.Genome_Wide_SNP_6.Level_3.1.0.0");
        archive.setId(111L);
        archive.setExperimentType(Experiment.TYPE_CGCC);
        archive.setSerialIndex("1");
        archive.setRevision("0");
        archive.setDeployStatus(Archive.STATUS_AVAILABLE);
    }

    private void setUpIlluminaHiSeq_DNASeqC() {
        List<CenterPlatformPattern> patternsList = new ArrayList<CenterPlatformPattern>();
        CenterPlatformPattern cpp = new CenterPlatformPattern();
        cpp.setCenter("hms.harvard.edu");
        cpp.setPattern(Arrays.asList(new String[]{"*Segment.tsv"}));
        cpp.setPlatform("IlluminaHiSeq_DNASeqC");
        patternsList.add(cpp);
        loader.setPatterns(patternsList);

        archive = new Archive(ILLUMINAHISEQ_DNASEQC_ARCHIVE_LOCATION + ".tar.gz");
        archive.setDeployLocation(ILLUMINAHISEQ_DNASEQC_ARCHIVE_LOCATION + ".tar.gz");
        Platform thePlatform = new Platform();
        thePlatform.setPlatformName("IlluminaHiSeq_DNASeqC");
        thePlatform.setPlatformId(1);
        archive.setThePlatform(thePlatform);
        archive.setDomainName("hms.harvard.edu");
        archive.setTumorType("READ");
        archiveCenter = new Center();
        archiveCenter.setCenterId(2);
        archiveCenter.setCenterName("hms.harvard.edu");
        archive.setTheCenter(archiveCenter);
        archive.setRealName("archiveLongName");
        archive.setId(111L);
        archive.setExperimentType(Experiment.TYPE_CGCC);
        archive.setSerialIndex("1");
        archive.setRevision("2");
        archive.setDeployStatus(Archive.STATUS_AVAILABLE);
        Tumor theTumor = new Tumor();
        theTumor.setTumorName("READ");
        archive.setTheTumor(theTumor);

    }

    private void setUpMDA_RPPA_Core() {
        List<CenterPlatformPattern> patternsList = new ArrayList<CenterPlatformPattern>();
        CenterPlatformPattern cpp = new CenterPlatformPattern();
        cpp.setCenter("mdanderson.org");
        cpp.setPattern(Arrays.asList(new String[]{"*protein_expression*.txt"}));
        cpp.setPlatform("MDA_RPPA_Core");
        patternsList.add(cpp);
        loader.setPatterns(patternsList);

        archive = new Archive(MDA_MDA_RPPACore_ARCHIVE_FILE_LOCATION + ".tar.gz");
        archive.setDeployLocation(MDA_MDA_RPPACore_ARCHIVE_FILE_LOCATION + ".tar.gz");
        Platform thePlatform = new Platform();
        thePlatform.setPlatformName("MDA_RPPA_Core");
        thePlatform.setPlatformId(1);
        archive.setThePlatform(thePlatform);
        archive.setDomainName("mdanderson.org");
        archive.setTumorType("UCEC");
        archiveCenter = new Center();
        archiveCenter.setCenterId(2);
        archiveCenter.setCenterName("mdanderson.org");
        archive.setTheCenter(archiveCenter);
        archive.setRealName("archiveLongName");
        archive.setId(111L);
        archive.setExperimentType(Experiment.TYPE_CGCC);
        archive.setSerialIndex("1");
        archive.setRevision("2");
        archive.setDeployStatus(Archive.STATUS_AVAILABLE);
        Tumor theTumor = new Tumor();
        theTumor.setTumorName("UCEC");
        archive.setTheTumor(theTumor);

    }

    private void setUpforMethylation() {
        //JHU &&  Methylation27 Specific set up
        List<CenterPlatformPattern> patternsList = new ArrayList<CenterPlatformPattern>();
        CenterPlatformPattern cpp = new CenterPlatformPattern();
        cpp.setCenter("jhu-usc.edu");
        cpp.setPattern(Arrays.asList(new String[]{"*.lvl-3.*.txt"}));
        cpp.setPlatform("HumanMethylation27");
        patternsList.add(cpp);
        cpp = new CenterPlatformPattern();
        cpp.setCenter("jhu-usc.edu");
        cpp.setPattern(Arrays.asList(new String[]{"*.lvl-3.*.txt"}));
        cpp.setPlatform("HumanMethylation37");

        patternsList.add(cpp);
        loader.setPatterns(patternsList);

        archive = new Archive(JHU_ARCHIVE_FILE_LOCATION + ".tar.gz");
        archive.setDeployLocation(JHU_ARCHIVE_FILE_LOCATION + ".tar.gz");
        Platform thePlatform = new Platform();
        thePlatform.setPlatformName("HumanMethylation27");
        thePlatform.setPlatformId(1);
        archive.setThePlatform(thePlatform);
        archive.setDomainName("jhu-usc.edu");
        archive.setTumorType("UCEC");
        archiveCenter = new Center();
        archiveCenter.setCenterId(2);
        archiveCenter.setCenterName("jhu-usc.edu");
        archive.setTheCenter(archiveCenter);
        archive.setRealName("archiveLongName");
        archive.setId(111L);
        archive.setExperimentType(Experiment.TYPE_BCR);
        archive.setSerialIndex("1");
        archive.setRevision("2");
        archive.setDeployStatus(Archive.STATUS_AVAILABLE);
        Tumor theTumor = new Tumor();
        theTumor.setTumorName("TEST");
        archive.setTheTumor(theTumor);
    }

    // hudson alpha center specific set up
    private void setUpforHudsonAlpha() {
        List<CenterPlatformPattern> patternsList = new ArrayList<CenterPlatformPattern>();
        CenterPlatformPattern cpp = new CenterPlatformPattern();
        cpp.setCenter("hudsonalpha.org");
        cpp.setPattern(Arrays.asList(new String[]{"*.loh.txt", "*.seg.txt", "*.segnormal.txt"}));
        cpp.setPlatform("Human1MDuo");
        patternsList.add(cpp);
        loader.setPatterns(patternsList);
        archive = new Archive(HUDSONALPHA_ARCHIVE_FILE_LOCATION + ".tar.gz");
        archive.setDeployLocation(HUDSONALPHA_ARCHIVE_FILE_LOCATION + ".tar.gz");
        Platform thePlatform = new Platform();
        thePlatform.setPlatformName("Human1MDuo");
        thePlatform.setPlatformId(1);
        archive.setThePlatform(thePlatform);
        archive.setDomainName("hudsonalpha.org");
        archive.setTumorType("OV");
        archiveCenter = new Center();
        archiveCenter.setCenterId(2);
        archiveCenter.setCenterName("hudsonalpha.org");
        archive.setTheCenter(archiveCenter);
        archive.setRealName("archiveLongName");
        archive.setId(111L);
        archive.setExperimentType(Experiment.TYPE_BCR);
        archive.setSerialIndex("1");
        archive.setSerialIndex("1");
        archive.setDeployStatus(Archive.STATUS_AVAILABLE);
        Tumor theTumor = new Tumor();
        theTumor.setTumorName("TEST");
        archive.setTheTumor(theTumor);
    }

    // genome wustl center specific set up
    private void setUpforGenomeWustl() {
        List<CenterPlatformPattern> patternsList = new ArrayList<CenterPlatformPattern>();
        CenterPlatformPattern cpp = new CenterPlatformPattern();
        cpp.setCenter("genome.wustl.edu");
        cpp.setPattern(Arrays.asList(new String[]{"*.segmented.dat"}));
        cpp.setPlatform("Genome_Wide_SNP_6");
        patternsList.add(cpp);
        loader.setPatterns(patternsList);
        archive = new Archive(GENOMEWUSTL_ARCHIVE_FILE_LOCATION + ".tar.gz");
        archive.setDeployLocation(GENOMEWUSTL_ARCHIVE_FILE_LOCATION + ".tar.gz");
        Platform thePlatform = new Platform();
        thePlatform.setPlatformName("Genome_Wide_SNP_6");
        thePlatform.setPlatformId(1);
        archive.setThePlatform(thePlatform);
        archive.setDomainName("genome.wustl.edu");
        archive.setTumorType("LAML");
        archiveCenter = new Center();
        archiveCenter.setCenterId(2);
        archiveCenter.setCenterName("genome.wustl.edu");
        archive.setTheCenter(archiveCenter);
        archive.setRealName("archiveLongName");
        archive.setId(111L);
        archive.setExperimentType(Experiment.TYPE_CGCC);
        archive.setSerialIndex("1");
        archive.setRevision("1");
        archive.setDeployStatus(Archive.STATUS_AVAILABLE);
        Tumor theTumor = new Tumor();
        theTumor.setTumorName("TEST");
        archive.setTheTumor(theTumor);
    }

    // tsv file center specific set up
    private void setUpforTsvFile() {
        List<CenterPlatformPattern> patternsList = new ArrayList<CenterPlatformPattern>();
        CenterPlatformPattern cpp = new CenterPlatformPattern();
        cpp.setCenter("hms.harvard.edu");
        cpp.setPattern(Arrays.asList(new String[]{"*lowess_normalized.tsv", "*Segment.tsv"}));
        cpp.setPlatform("HG-CGH-244A");
        patternsList.add(cpp);
        loader.setPatterns(patternsList);
        archive = new Archive(TSV_ARCHIVE_FILE_LOCATION + ".tar.gz");
        archive.setDeployLocation(TSV_ARCHIVE_FILE_LOCATION + ".tar.gz");
        Platform thePlatform = new Platform();
        thePlatform.setPlatformName("HG-CGH-244A");
        thePlatform.setPlatformId(1);
        archive.setThePlatform(thePlatform);
        archive.setDomainName("hms.harvard.edu");
        archive.setTumorType("GBM");
        archiveCenter = new Center();
        archiveCenter.setCenterId(2);
        archiveCenter.setCenterName("hms.harvard.edu");
        archive.setTheCenter(archiveCenter);
        archive.setRealName("archiveLongName");
        archive.setId(111L);
        archive.setExperimentType(Experiment.TYPE_BCR);
        archive.setSerialIndex("1");
        archive.setSerialIndex("1");
        archive.setDeployStatus(Archive.STATUS_AVAILABLE);
        Tumor theTumor = new Tumor();
        theTumor.setTumorName("TEST");
        archive.setTheTumor(theTumor);
    }

    // firma file center specific set up
    private void setUpforFirma() {
        List<CenterPlatformPattern> patternsList = new ArrayList<CenterPlatformPattern>();
        CenterPlatformPattern cpp = new CenterPlatformPattern();
        cpp.setCenter("lbl.gov");
        cpp.setPattern(Arrays.asList(new String[]{"*.FIRMA.txt", "*.gene.txt"}));
        cpp.setPlatform("HuEx-1_0-st-v2");
        patternsList.add(cpp);
        loader.setPatterns(patternsList);
        archive = new Archive(FIRMA_ARCHIVE_FILE_LOCATION + ".tar.gz");
        archive.setDeployLocation(FIRMA_ARCHIVE_FILE_LOCATION + ".tar.gz");
        Platform thePlatform = new Platform();
        thePlatform.setPlatformName("HuEx-1_0-st-v2");
        thePlatform.setPlatformId(1);
        archive.setThePlatform(thePlatform);
        archive.setDomainName("lbl.gov");
        archive.setTumorType("GBM");
        archiveCenter = new Center();
        archiveCenter.setCenterId(2);
        archiveCenter.setCenterName("lbl.gov");
        archive.setTheCenter(archiveCenter);
        archive.setRealName("archiveLongName");
        archive.setId(111L);
        archive.setExperimentType(Experiment.TYPE_BCR);
        archive.setSerialIndex("1");
        archive.setSerialIndex("1");
        archive.setDeployStatus(Archive.STATUS_AVAILABLE);
        Tumor theTumor = new Tumor();
        theTumor.setTumorName("TEST");
        archive.setTheTumor(theTumor);
    }

    private void setUpforMSKCC() {
        // MSKCC Specific set up
        List<CenterPlatformPattern> patternsList = new ArrayList<CenterPlatformPattern>();
        CenterPlatformPattern cpp = new CenterPlatformPattern();
        cpp.setCenter("mskcc.org");
        cpp.setPattern(Arrays.asList(new String[]{"*.CBS.txt", "*CBS_out.txt"}));
        cpp.setPlatform("HG-CGH-244A");
        patternsList.add(cpp);
        loader.setPatterns(patternsList);
        archive = new Archive(MSK_ARCHIVE_FILE_LOCATION + ".tar.gz");
        archive.setDeployLocation(MSK_ARCHIVE_FILE_LOCATION + ".tar.gz");
        Platform thePlatform = new Platform();
        thePlatform.setPlatformName("HG-CGH-244A");
        thePlatform.setPlatformId(1);
        archive.setThePlatform(thePlatform);
        archive.setDomainName("mskcc.org");
        archive.setTumorType("GBM");
        archiveCenter = new Center();
        archiveCenter.setCenterId(2);
        archiveCenter.setCenterName("mskcc.org");
        archive.setTheCenter(archiveCenter);
        archive.setRealName("archiveLongName");
        archive.setId(111L);
        archive.setExperimentType(Experiment.TYPE_BCR);
        archive.setSerialIndex("1");
        archive.setSerialIndex("1");
        archive.setDeployStatus(Archive.STATUS_AVAILABLE);
        Tumor theTumor = new Tumor();
        theTumor.setTumorName("TEST");
        archive.setTheTumor(theTumor);
        archive.setRevision("1");
    }

    private void setUpforGeneEpression(final String archiveLocation,
                                       final String platform,
                                       final String center) {
        // MSKCC Specific set up
        List<CenterPlatformPattern> patternsList = new ArrayList<CenterPlatformPattern>();
        CenterPlatformPattern cpp = new CenterPlatformPattern();
        cpp.setCenter(center);
        cpp.setPattern(Arrays.asList(new String[]{"*.tcga_level3.data.txt"}));
        cpp.setPlatform(platform);
        patternsList.add(cpp);
        loader.setPatterns(patternsList);
        archive = new Archive(archiveLocation + ".tar.gz");
        archive.setDeployLocation(archiveLocation + ".tar.gz");
        Platform thePlatform = new Platform();
        thePlatform.setPlatformName(platform);
        thePlatform.setPlatformId(1);
        archive.setThePlatform(thePlatform);
        archive.setDomainName(center);
        archive.setTumorType("KIRC");
        archiveCenter = new Center();
        archiveCenter.setCenterId(2);
        archiveCenter.setCenterName(center);
        archive.setTheCenter(archiveCenter);
        archive.setRealName("archiveLongName");
        archive.setId(111L);
        archive.setExperimentType(Experiment.TYPE_BCR);
        archive.setSerialIndex("1");
        archive.setSerialIndex("1");
        archive.setDeployStatus(Archive.STATUS_AVAILABLE);
        Tumor theTumor = new Tumor();
        theTumor.setTumorName("TEST");
        archive.setTheTumor(theTumor);
    }

    @Test
    public void testLoadMSKCCArchiveByName() throws LoaderException {
        setUpforMSKCC();

        final List<FileInfo> archiveFile = new ArrayList<FileInfo>();
        FileInfo fi = new FileInfo();
        fi.setFileName("MSK_00001_251469322729_S01_CGH-v4_91__GCN_V3_A1__CBS_out.txt");
        fi.setId(1L);
        archiveFile.add(fi);

        final String sdrfLocation = MSK_ARCHIVE_MAGETAB_FILE_LOCATION + ".tar.gz";
        final String base = archive.getTheCenter().getCenterName() + "_" +
                archive.getTheTumor().getTumorName() + "." +
                archive.getThePlatform().getPlatformName();
        context.checking(new Expectations() {{
            allowing(mockArchiveQueries).getArchiveIdByName("mskcc.org_GBM.HG-CGH-244A.Level_3.8.2.0");
            will(returnValue(111L));
            allowing(mockArchiveQueries).getArchive(111);
            will(returnValue(archive));
            allowing(mockArchiveQueries).getSdrfDeployLocation("mskcc.org", "HG-CGH-244A", "GBM");
            will(returnValue(sdrfLocation));
            allowing(mockArchiveQueries).getCenterByDomainNameAndPlatformName("mskcc.org", "HG-CGH-244A");
            will(returnValue(archiveCenter));
            allowing(mockLthreeQueries).getExperimentId(base, new Integer(archive.getSerialIndex()), new Integer(archive.getRevision()));
            will(returnValue(null));
            allowing(mockLthreeQueries).insertExperiment(
                    archiveCenter.getCenterId(),
                    archive.getThePlatform().getPlatformId(),
                    base,
                    new Integer(archive.getSerialIndex()),
                    new Integer(archive.getRevision()));
            will(returnValue(555));
            allowing(mockLthreeQueries).createDataSet(2, 555, 1, "archiveLongName/*CBS_out.txt", "copy_number_analysis", "PUBLIC", 0, 3, 111L);
            will(returnValue(222));
            allowing(mockArchiveQueries).getFilesForArchive(111L);
            will(returnValue(archiveFile));

            allowing(mockLthreeQueries).createDataSetFile(222, "MSK_00001_251469322729_S01_CGH-v4_91__GCN_V3_A1__CBS_out.txt", 1L);
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("TCGA-02-0001-01C-01D-0183-04");
            will(returnValue(false));
            allowing(mockLthreeQueries).updateDataSetFile(222, "MSK_00001_251469322729_S01_CGH-v4_91__GCN_V3_A1__CBS_out.txt", 1L);
            allowing(mockLthreeQueries).updateDataSet(222);

            allowing(mockLthreeQueries).updateArchiveLoadedDate(111L);
            allowing(mockArchiveQueries).updateArchiveInfo(111L);

            allowing(mockLthreeQueries).getHybRefId("TCGA-02-0001-01C-01D-0183-04");
            allowing(mockLthreeQueries).getHybrefDataSetId(0, 222);
            allowing(mockLthreeQueries).addCNAValue(with(any(List.class)));

        }});
        loader.loadArchiveByName("mskcc.org_GBM.HG-CGH-244A.Level_3.8.2.0");
        // check if disease context was set correctly        
        assertTrue(archive.getTheTumor().getTumorName().equals(DiseaseContextHolder.getDisease()));
    }

    @Test(expected = LoaderException.class)
    public void loadArchiveByFailName() throws LoaderException {

        context.checking(new Expectations() {{
            allowing(mockArchiveQueries).getArchiveIdByName("bad_archive_name");
            will(returnValue(-1L));
        }});
        loader.loadArchiveByName("bad_archive_name");
        fail();
    }

    @Test
    public void testLoadJHUArchiveByName() throws LoaderException {
        setUpforMethylation();

        final Center center = archiveCenter;

        final List<FileInfo> archiveFile = new ArrayList<FileInfo>();
        FileInfo fi = new FileInfo();
        fi.setFileName("jhu-usc.edu_UCEC.HumanMethylation27.1.lvl-3.TCGA-AP-A051-01A-21D-A00U-05.txt");
        fi.setId(1L);
        archiveFile.add(fi);

        final String sdrfLocation = JHU_MAGE_TAB_LOCATION+".tar.gz";
        final String base = archive.getTheCenter().getCenterName() + "_" +
                archive.getTheTumor().getTumorName() + "." +
                archive.getThePlatform().getPlatformName();
        context.checking(new Expectations() {{
            allowing(mockArchiveQueries).getArchiveIdByName("jhu-usc.edu_UCEC.HumanMethylation27.Level_3.1.0.0");
            will(returnValue(111L));
            allowing(mockArchiveQueries).getArchive(111L);
            will(returnValue(archive));
            allowing(mockArchiveQueries).getSdrfDeployLocation("jhu-usc.edu", "HumanMethylation27", "UCEC");
            will(returnValue(sdrfLocation));
            allowing(mockArchiveQueries).getCenterByDomainNameAndPlatformName("jhu-usc.edu", "HumanMethylation27");
            will(returnValue(center));
            allowing(mockLthreeQueries).getExperimentId(base, new Integer(archive.getSerialIndex()), new Integer(archive.getRevision()));
            will(returnValue(null));
            allowing(mockLthreeQueries).insertExperiment(
                    center.getCenterId(),
                    archive.getThePlatform().getPlatformId(),
                    base,
                    new Integer(archive.getSerialIndex()),
                    new Integer(archive.getRevision()));
            allowing(mockLthreeQueries).createDataSet(2, 0, 1, "archiveLongName/*.lvl-3.*.txt", "methylation_analysis", "PUBLIC", 0, 3, 111L);
            will(returnValue(222));
            allowing(mockArchiveQueries).getFilesForArchive(111L);
            will(returnValue(archiveFile));

            allowing(mockLthreeQueries).createDataSetFile(222, "jhu-usc.edu_UCEC.HumanMethylation27.1.lvl-3.TCGA-AP-A051-01A-21D-A00U-05.txt", 1L);
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("TCGA-AP-A051-01A-21D-A00U-05");
            will(returnValue(false));
            allowing(mockLthreeQueries).updateDataSet(222);
            allowing(mockLthreeQueries).updateDataSetFile(222, "jhu-usc.edu_UCEC.HumanMethylation27.1.lvl-3.TCGA-AP-A051-01A-21D-A00U-05.txt", 1L);
            allowing(mockLthreeQueries).getHybRefId("TCGA-AP-A051-01A-21D-A00U-05");
            will(returnValue(123));
            allowing(mockLthreeQueries).getHybrefDataSetId(123, 222);
            will(returnValue(333));

            allowing(mockLthreeQueries).addMethylationValue(with(any(List.class)));

            allowing(mockLthreeQueries).updateArchiveLoadedDate(111L);
            allowing(mockArchiveQueries).updateArchiveInfo(111L);

        }});
        loader.loadArchiveByName("jhu-usc.edu_UCEC.HumanMethylation27.Level_3.1.0.0");
        // check if disease context was set correctly
        assertTrue(archive.getTheTumor().getTumorName().equals(DiseaseContextHolder.getDisease()));
    }

    @Test(expected = LoaderException.class)
    public void testLoadInvalidJHUArchiveByName() throws LoaderException {
        setUpforMethylation();

        final Center center = archiveCenter;

        final List<FileInfo> archiveFile = new ArrayList<FileInfo>();
        FileInfo fi = new FileInfo();
        fi.setFileName("jhu-usc.edu_UCEC.HumanMethylation37.1.lvl-3.TCGA-AP-A051-01A-21D-A00U-05.txt");
        fi.setId(1L);
        archiveFile.add(fi);

        final String sdrfLocation = JHU_MAGE_TAB_LOCATION;
        archive.getThePlatform().setPlatformName("HumanMethylation37");
        final String base = archive.getTheCenter().getCenterName() + "_" +
                archive.getTheTumor().getTumorName() + "." +
                archive.getThePlatform().getPlatformName();
        context.checking(new Expectations() {{
            allowing(mockArchiveQueries).getArchiveIdByName("jhu-usc.edu_UCEC.HumanMethylation37.Level_3.1.0.0");
            will(returnValue(111L));
            allowing(mockArchiveQueries).getArchive(111L);
            will(returnValue(archive));
            allowing(mockArchiveQueries).getFilesForArchive(111L);
            will(returnValue(archiveFile));
            allowing(mockArchiveQueries).getSdrfDeployLocation("jhu-usc.edu", "HumanMethylation37", "UCEC");
            will(returnValue(sdrfLocation));
            allowing(mockArchiveQueries).getCenterByDomainNameAndPlatformName("jhu-usc.edu", "HumanMethylation37");
            will(returnValue(center));
            allowing(mockLthreeQueries).getExperimentId(base, new Integer(archive.getSerialIndex()), new Integer(archive.getRevision()));
            will(returnValue(null));
            allowing(mockLthreeQueries).insertExperiment(
                    center.getCenterId(),
                    archive.getThePlatform().getPlatformId(),
                    base,
                    new Integer(archive.getSerialIndex()),
                    new Integer(archive.getRevision()));


        }});
        loader.loadArchiveByName("jhu-usc.edu_UCEC.HumanMethylation37.Level_3.1.0.0");
    }

    @Test
    public void testBetaLoaderGoodCase() throws LoaderException {
        setUpforMethylation();
        loader.setBatchSize(3);
        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("TCGA-AP-A051-01A-21D-A00U-05");
            will(returnValue(false));
            allowing(mockLthreeQueries).getHybRefId("TCGA-AP-A051-01A-21D-A00U-05");
            will(returnValue(123));
            allowing(mockLthreeQueries).getHybrefDataSetId(123, 123);
            will(returnValue(333));
            exactly(1).of(mockLthreeQueries).addMethylationValue(with(checkBetaList(new ArrayList<Object[]>())));
        }});
        final String jhuLocation = JHU_ARCHIVE_FILE_LOCATION + ".tar.gz";
        String fileLocation = jhuLocation.substring(0, jhuLocation.length() - 7);
        loader.loadBetaFile(fileLocation +
                File.separator +
                "jhu-usc.edu_UCEC.HumanMethylation27.1.lvl-3.TCGA-AP-A051-01A-21D-A00U-05.txt", 123);

    }


    @Test(expected = LoaderException.class)
    public void testSegTsvLoaderBadHeaderName() throws LoaderException {
        setUpforTsvFile();
        loader.loadBetaFile(JHU_ARCHIVE_FILE_LOCATION +
                File.separator + ".." +
                File.separator + "unitTestBadCases" +
                File.separator + "SEGTSV_bad_header.txt",
                123);
        fail();

    }

    @Test
    public void testFIRMABadHeaderName() throws LoaderException {

        setUpforFirma();

        String fileLocation = BASE_ARCHIVE_FILE_LOCATION +
                "unitTestBadCases" +
                File.separator + "FIRMA_bad_header.txt";


        final String sdrfLocation = FIRMA_ARCHIVE_MAGETAB_FILE_LOCATION ;

        context.checking(new Expectations() {{
            allowing(mockLthreeQueries).getHybRefId(with(any(String.class)));
            will(returnValue(123));
            allowing(mockLthreeQueries).getHybrefDataSetId(123, 123);
            will(returnValue(333));
        }});
        TabDelimitedContentNavigator sdrfNavigator = loader.loadSDRF(sdrfLocation);
        final int extractIndex = loader.getExtractNamePosition(sdrfNavigator);
        try {
            loader.loadFIRMAFile(fileLocation
                    , sdrfNavigator, extractIndex, 123);
        } catch (LoaderException e) {
            assertTrue(e.getMessage().contains("Invalid number of tokens"));
        }

    }

    @Test(expected = LoaderException.class)
    public void testBetaLoaderExtraTokenInFile() throws LoaderException {
        setUpforMethylation();

        context.checking(new Expectations() {{
            allowing(mockLthreeQueries).getHybRefId("TCGA-AP-A051-01A-21D-A00U-05");
            will(returnValue(123));
            allowing(mockLthreeQueries).getHybrefDataSetId(123, 123);
            will(returnValue(333));
            allowing(mockLthreeQueries).updateArchiveLoadedDate(111L);
            allowing(mockLthreeQueries).updateArchiveLoadedDate(111L);
            allowing(mockArchiveQueries).updateArchiveInfo(111L);
        }});

        loader.loadBetaFile(SAMPLE_DIR + "unitTestBadCases" + File.separator + "HumanMethylation27_TooManyTokens.txt", 123);

        fail();
    }


    @Test(expected = LoaderException.class)
    public void testBetaLoaderMissingBetaValue() throws LoaderException {
        setUpforMethylation();

        context.checking(new Expectations() {{
            allowing(mockLthreeQueries).getHybRefId("TCGA-AP-A051-01A-21D-A00U-05");
            will(returnValue(123));
            allowing(mockLthreeQueries).getHybrefDataSetId(123, 123);
            will(returnValue(333));
            allowing(mockLthreeQueries).addMethylationValue(with(any(List.class)));

        }});

        loader.loadBetaFile(SAMPLE_DIR + "unitTestBadCases" + File.separator + "HumanMethylation27_MissingBetaValue.txt", 123);
        fail();
    }

    @Test(expected = LoaderException.class)
    public void testBetaLoaderMissingProbeName() throws LoaderException {
        setUpforMethylation();

        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("TCGA-AB-2802-03A-01D-0741-05");
            will(returnValue(false));
            allowing(mockLthreeQueries).getHybRefId("TCGA-AB-2802-03A-01D-0741-05");
            will(returnValue(123));
            allowing(mockLthreeQueries).getHybrefDataSetId(123, 123);
            will(returnValue(333));
            allowing(mockLthreeQueries).addMethylationValue(with(any(List.class)));

        }});

        loader.loadBetaFile(SAMPLE_DIR + "qclive" + File.separator +
                "LevelThreeLoader" + File.separator + "unitTestBadCases"
                + File.separator + "HumanMethylation405_badProbeName.txt", 123);
        fail();
    }

    @Test
    public void testLoadMSKCC() throws LoaderException {
        setUpforMSKCC();
        loader.setBatchSize(3);
        String fileLocation = MSK_ARCHIVE_FILE_LOCATION;

        final String sdrfLocation = MSK_ARCHIVE_MAGETAB_FILE_LOCATION ;
        context.checking(new Expectations() {{
            allowing(mockLthreeQueries).getHybRefId("TCGA-02-0001-01C-01D-0183-04");
            will(returnValue(123));
            allowing(mockLthreeQueries).getHybrefDataSetId(123, 123);
            will(returnValue(333));
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("TCGA-02-0001-01C-01D-0183-04");
            will(returnValue(false));
            allowing(mockLthreeQueries).addCNAValue(with(any(List.class)));
        }});
        TabDelimitedContentNavigator sdrfNavigator = loader.loadSDRF(sdrfLocation );
        final int extractNameIndex = loader.getExtractNamePosition(sdrfNavigator);
        loader.loadCnaValues(
        		new File(fileLocation + File.separator + "MSK_00001_251469322729_S01_CGH-v4_91__GCN_V3_A1__CBS_out.txt"),
        		extractNameIndex,
        		sdrfNavigator,
        		123,
        		"",
        		"mskcc.org");
    }

    @Test
    public void testLoadMSKCCMissingSegMean() throws LoaderException {
        setUpforMSKCC();
        loader.setBatchSize(3);
        String fileLocation = BASE_ARCHIVE_FILE_LOCATION +
                File.separator + "unitTestBadCases" +
                File.separator + "MSKCC_missingsegMean.txt";

        final String sdrfLocation = MSK_ARCHIVE_MAGETAB_FILE_LOCATION ;
        context.checking(new Expectations() {{
            allowing(mockLthreeQueries).getHybRefId("TCGA-02-0001-01C-01D-0183-04");
            will(returnValue(123));
            allowing(mockLthreeQueries).getHybrefDataSetId(123, 123);
            will(returnValue(333));
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("TCGA-02-0001-01C-01D-0183-04");
            will(returnValue(false));
            allowing(mockLthreeQueries).addCNAValue(with(any(List.class)));
        }});
        TabDelimitedContentNavigator sdrfNavigator = loader.loadSDRF(sdrfLocation);
        final int extractNameIndex = loader.getExtractNamePosition(sdrfNavigator);
        try {
            loader.loadCnaValues(new File(fileLocation), extractNameIndex, sdrfNavigator, 123, "", "mskcc.org");
        }
        catch (LoaderException e) {
            assertTrue(e.getMessage().contains("Bean property [segMean] for record number [2] may not be empty"));
            return;
        }
        fail();

    }

    @Test
    public void testLoadGenomeWustlSameAsBroad() throws LoaderException {
        setUpforGenomeWustl();
        loader.setBatchSize(3);
        String fileLocation = GENOMEWUSTL_ARCHIVE_FILE_LOCATION;

        final String sdrfLocation = GENOMEWUSTL_ARCHIVE_MAGETAB_FILE_LOCATION ;
        context.checking(new Expectations() {{
            allowing(mockLthreeQueries).getHybRefId(with(any(String.class)));
            will(returnValue(123));
            allowing(mockLthreeQueries).getHybrefDataSetId(123, 123);
            will(returnValue(333));
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("TCGA-AB-2802-11A-01D-0756-21");
            will(returnValue(false));
            allowing(mockLthreeQueries).addCNAValue(with(any(List.class)));
        }});
        TabDelimitedContentNavigator sdrfNavigator = loader.loadSDRF(sdrfLocation);
        final int extractNameIndex = loader.getExtractNamePosition(sdrfNavigator);
        loader.loadCnaValues(
        		new File(fileLocation + File.separator + "TCGA-AB-2802-11A-01D-0756-21.segmented.dat"),
        		extractNameIndex,
        		sdrfNavigator,
        		123,
        		"Genome_Wide_SNP_6",
        		"genome.wustl.edu");
    }

    // missing barcode means that the barcode is not found in SDRF
    // this test checks if only two recrods were written, skipping the third one that has an invalid barcode.
    @Test
    public void testLoadHudsonalphaWithMissingBarcode() throws LoaderException {
        setUpforHudsonAlpha();

        // set batchsize to 1 , to control hoe many records get written
        loader.setBatchSize(1);
        String fileLocation = BASE_ARCHIVE_FILE_LOCATION +
                File.separator + "unitTestBadCases" +
                File.separator + "HudsonAlpha_missingBarcode.txt";

        final String sdrfLocation = HUDSONALPHA_ARCHIVE_MAGETAB_FILE_LOCATION ;
        context.checking(new Expectations() {{
            allowing(mockLthreeQueries).getHybRefId(with(any(String.class)));
            will(returnValue(123));
            allowing(mockLthreeQueries).getHybrefDataSetId(123, 123);
            will(returnValue(333));
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("TCGA-09-0364-01A-02D-0358-06");
            will(returnValue(false));
            exactly(1).of(mockLthreeQueries).addCNAValue(with(any(List.class)));
        }});
        TabDelimitedContentNavigator sdrfNavigator = loader.loadSDRF(sdrfLocation);
        final int extractNameIndex = loader.getExtractNamePosition(sdrfNavigator);
        loader.loadCnaValues(new File(fileLocation), extractNameIndex, sdrfNavigator, 123, "", "hudsonalpha.org");
    }

    @Test
    public void testLoadIlluminaHiSeq_DNASeqCSegTsvFile() throws LoaderException {
        setUpIlluminaHiSeq_DNASeqC();
        final String sdrfLocation = ILLUMINAHISEQ_DNASEQC_MAGE_TAB_ARCHIVE_LOCATION + ".tar.gz";

        final List<FileInfo> archiveFile = new ArrayList<FileInfo>();
        final FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName("TCGA-AG-A032-01A-01D-A077-02_TCGA-AG-A032-10A-01D-A078-02_Segment.tsv");

        fileInfo.setId(100l);
        archiveFile.add(fileInfo);

        final List<String> tumorBarcodes = Arrays.asList(new String[]{"TCGA-AG-A032-01A-01D-A077-02"});

        context.checking(new Expectations() {{
            one(mockArchiveQueries).getArchiveIdByName("hms.harvard.edu_READ.IlluminaHiSeq_DNASeqC.Level_3.1.0.0.tar.gz");
            will(returnValue(123l));
            one(mockArchiveQueries).getArchive(123l);
            will(returnValue(archive));
            one(mockArchiveQueries).getFilesForArchive(123l);
            will(returnValue(archiveFile));
            allowing(mockArchiveQueries).getSdrfDeployLocation("hms.harvard.edu", "IlluminaHiSeq_DNASeqC", "READ");
            will(returnValue(sdrfLocation));
            one(mockArchiveQueries).getCenterByDomainNameAndPlatformName("hms.harvard.edu", "IlluminaHiSeq_DNASeqC");
            will(returnValue(archive.getTheCenter()));
            one(mockLthreeQueries).getExperimentId("hms.harvard.edu_READ.IlluminaHiSeq_DNASeqC", 1, 2);
            will(returnValue(100));
            one(mockLthreeQueries).createDataSet(2, 100, 1, "archiveLongName/*Segment.tsv", "copy_number_analysis", "PUBLIC", 0, 3, 111L);
            will(returnValue(100));
            one(mockLthreeQueries).createDataSetFile(100, "TCGA-AG-A032-01A-01D-A077-02_TCGA-AG-A032-10A-01D-A078-02_Segment.tsv", 100L);
            one(mockCommonLevelThreeQueries).getTumorBarcodesForFile(100l);
            will(returnValue(tumorBarcodes));
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("TCGA-AG-A032-01A-01D-A077-02");
            will(returnValue(false));
            allowing(mockLthreeQueries).getHybrefDataSetId(123, 100);
            will(returnValue(333));

            allowing(mockLthreeQueries).getHybRefId(with(any(String.class)));
            will(returnValue(123));
            allowing(mockLthreeQueries).addCNAValue(with(checkIlluminaHiSeq_DNASeqCTsvList()));
            allowing(mockLthreeQueries).updateDataSetFile(100, "TCGA-AG-A032-01A-01D-A077-02_TCGA-AG-A032-10A-01D-A078-02_Segment.tsv", 100L);
            allowing(mockLthreeQueries).updateDataSet(100);
            allowing(mockLthreeQueries).updateArchiveLoadedDate(123l);
            allowing(mockArchiveQueries).updateArchiveInfo(123l);


        }});
        loader.loadArchiveByName("hms.harvard.edu_READ.IlluminaHiSeq_DNASeqC.Level_3.1.0.0.tar.gz");

    }

    @Test
    public void testLoadFirmaFile() throws LoaderException {
        setUpforFirma();

        String fileLocation = FIRMA_ARCHIVE_FILE_LOCATION;
        final String sdrfLocation = FIRMA_ARCHIVE_MAGETAB_FILE_LOCATION ;
        final List<Object[]> firmaValList = new ArrayList<Object[]>();

        context.checking(new Expectations() {{
            allowing(mockLthreeQueries).getHybRefId(with(any(String.class)));
            will(returnValue(123));
            allowing(mockLthreeQueries).getHybrefDataSetId(123, 123);
            will(returnValue(333));
            allowing(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat(with(any(String.class)));
            will(returnValue(false));
            allowing(mockLthreeQueries).addExpGeneValue(with(checkFirmaList(firmaValList)));

        }});
        TabDelimitedContentNavigator sdrfNavigator = loader.loadSDRF(sdrfLocation);
        final int extractIndex = loader.getExtractNamePosition(sdrfNavigator);
        loader.loadFIRMAFile(fileLocation +
                File.separator +
                "lbl.gov_GBM.HuEx-1_0-st-v2.1.FIRMA.txt", sdrfNavigator, extractIndex, 123);

    }

    @Test(expected = LoaderException.class)
    public void testFirmaWrongNumberOfTokens() throws LoaderException {
        setUpforFirma();
        final String sdrfLocation = FIRMA_ARCHIVE_MAGETAB_FILE_LOCATION ;

        context.checking(new Expectations() {{
            allowing(mockLthreeQueries).getHybRefId(with(any(String.class)));
            will(returnValue(123));
            allowing(mockLthreeQueries).getHybrefDataSetId(123, 123);
            will(returnValue(333));
            allowing(mockLthreeQueries).addExpGeneValue(with(checkFirmaList(new ArrayList<Object[]>())));

        }});
        TabDelimitedContentNavigator sdrfNavigator = loader.loadSDRF(sdrfLocation);
        final int extractIndex = loader.getExtractNamePosition(sdrfNavigator);


        loader.loadFIRMAFile(SAMPLE_DIR + "unitTestBadCases" + File.separator + "FIRMA_bad_tokens_number.txt", sdrfNavigator, extractIndex, 123);
        fail();
    }

    @Test
    public void testLoadProteinExpressionGood() throws LoaderException {
        Map<String,String> antibodyAnnotationData = new HashMap<String,String>();
        antibodyAnnotationData.put("4E-BP1-R-V", "blue");
        context.checking(new Expectations() {{
            exactly(2).of(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("fe469d1c-3900-4649-a36e-562f8047f8e0");
            will(returnValue(true));
            exactly(2).of(mockUuiddao).getLatestBarcodeForUUID("fe469d1c-3900-4649-a36e-562f8047f8e0");
            will(returnValue("TCGA-02-0001-01C-01D-0183-04"));
            allowing(mockLthreeQueries).getHybRefId("TCGA-02-0001-01C-01D-0183-04");
            will(returnValue(123));
            allowing(mockLthreeQueries).getHybrefDataSetId(123, 123);
            will(returnValue(333));
            allowing(mockLthreeQueries).addProteinExpValue(with(any(List.class)));
        }});
        loader.loadProteinFile(MDA_MDA_RPPACore_ARCHIVE_FILE_LOCATION +
                File.separator +
                "mdanderson.org_UCEC.MDA_RPPA_Core.protein_expression.Level_3.uuid.txt", 123, antibodyAnnotationData);
    }

    @Test(expected = LoaderException.class)
    public void testLoadProteinExpressionBadFile() throws LoaderException {
        Map<String,String> antibodyAnnotationData = new HashMap<String,String>();
        loader.loadProteinFile(MDA_MDA_RPPACore_ARCHIVE_FILE_LOCATION +
                File.separator +
                "foo", 123, antibodyAnnotationData);
    }

    @Test
    public void testLoadProteinExpressionBadSampleRef() throws LoaderException {
        Map<String,String> antibodyAnnotationData = new HashMap<String,String>();
        antibodyAnnotationData.put("sky", "blue");
        try {
            loader.loadProteinFile(MDA_MDA_RPPACore_ARCHIVE_FILE_LOCATION +
                    File.separator +
                    "badheaderline.txt", 123, antibodyAnnotationData);
        } catch(LoaderException le) {
            assertEquals("Invalid file format: Expecting a tab delimited header sampleref line : " +
                    "'Sample REF\tfe469d1c-3900-4649-a36e-562f8047f8e0\tihavethreeheaders' " +
                    "with no more than two elements. DatasetId  datasetId = 123", le.getMessage());
        }
    }

    @Test
    public void testLoadProteinExpressionControlUuid() throws LoaderException {
        Map<String,String> antibodyAnnotationData = new HashMap<String,String>();
        antibodyAnnotationData.put("sky", "blue");
        context.checking(new Expectations() {{
            exactly(1).of(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("foouuid");
            will(returnValue(false));
        }});
        loader.loadProteinFile(MDA_MDA_RPPACore_ARCHIVE_FILE_LOCATION +
                File.separator +
                "mdanderson.org_UCEC.MDA_RPPA_Core.protein_expression.Level_3.control.txt", 123, antibodyAnnotationData);
    }

    @Test
    public void testLoadProteinExpressionBadDataLine() throws LoaderException {
        Map<String,String> antibodyAnnotationData = new HashMap<String,String>();
        antibodyAnnotationData.put("sky", "blue");
        context.checking(new Expectations() {{
            exactly(2).of(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("fe469d1c-3900-4649-a36e-562f8047f8e0");
            will(returnValue(true));
            exactly(2).of(mockUuiddao).getLatestBarcodeForUUID("fe469d1c-3900-4649-a36e-562f8047f8e0");
            will(returnValue("TCGA-02-0001-01C-01D-0183-04"));
            allowing(mockLthreeQueries).getHybRefId("TCGA-02-0001-01C-01D-0183-04");
            will(returnValue(123));
            allowing(mockLthreeQueries).getHybrefDataSetId(123, 123);
            will(returnValue(333));
            allowing(mockLthreeQueries).addProteinExpValue(with(any(List.class)));
        }});
        try {
            loader.loadProteinFile(MDA_MDA_RPPACore_ARCHIVE_FILE_LOCATION +
                    File.separator +
                    "baddataline.txt", 123, antibodyAnnotationData);
        } catch(LoaderException le) {
            assertEquals("Invalid file format: Expecting a tab delimited header data line " +
                    ": '-0.6254172315' with no more than two elements. datasetId = 123", le.getMessage());
        }
    }

    @Test
    public void testLoadProteinExpressionBadAntibodyName() throws LoaderException {
        Map<String,String> antibodyAnnotationData = new HashMap<String,String>();
        antibodyAnnotationData.put("sky", "blue");
        context.checking(new Expectations() {{
            exactly(2).of(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("fe469d1c-3900-4649-a36e-562f8047f8e0");
            will(returnValue(true));
            exactly(2).of(mockUuiddao).getLatestBarcodeForUUID("fe469d1c-3900-4649-a36e-562f8047f8e0");
            will(returnValue("TCGA-02-0001-01C-01D-0183-04"));
            allowing(mockLthreeQueries).getHybRefId("TCGA-02-0001-01C-01D-0183-04");
            will(returnValue(123));
            allowing(mockLthreeQueries).getHybrefDataSetId(123, 123);
            will(returnValue(333));
            allowing(mockLthreeQueries).addProteinExpValue(with(any(List.class)));
        }});
        try {
            loader.loadProteinFile(MDA_MDA_RPPACore_ARCHIVE_FILE_LOCATION +
                    File.separator +
                    "badantibodyname.txt", 123, antibodyAnnotationData);
        } catch(LoaderException le) {
            assertEquals("Antibody name cannot be empty : in data line : '\t-0.6254172315'. Failing load.", le.getMessage());
        }
    }

    @Test
    public void testLoadProteinExpressionNoHugoGeneSymbol() throws LoaderException {
        Map<String,String> antibodyAnnotationData = new HashMap<String,String>();
        antibodyAnnotationData.put("foo", "blue");
        context.checking(new Expectations() {{
            exactly(2).of(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("fe469d1c-3900-4649-a36e-562f8047f8e0");
            will(returnValue(true));
            exactly(2).of(mockUuiddao).getLatestBarcodeForUUID("fe469d1c-3900-4649-a36e-562f8047f8e0");
            will(returnValue("TCGA-02-0001-01C-01D-0183-04"));
            allowing(mockLthreeQueries).getHybRefId("TCGA-02-0001-01C-01D-0183-04");
            will(returnValue(123));
            allowing(mockLthreeQueries).getHybrefDataSetId(123, 123);
            will(returnValue(333));
            allowing(mockLthreeQueries).addProteinExpValue(with(any(List.class)));
        }});
        try {
            loader.loadProteinFile(MDA_MDA_RPPACore_ARCHIVE_FILE_LOCATION +
                    File.separator +
                    "mdanderson.org_UCEC.MDA_RPPA_Core.protein_expression.Level_3.uuid.txt", 123, antibodyAnnotationData);
        } catch(LoaderException le) {
            assertEquals("Unable to find hugo gene symbol for antibody name : 4E-BP1-R-Vin data line : '4E-BP1-R-V\t-0.6254172315'. Failing load.", le.getMessage());
        }
    }

    @Test
    public void testLoadProteinExpressionBadProteinValue() throws LoaderException {
        Map<String,String> antibodyAnnotationData = new HashMap<String,String>();
        antibodyAnnotationData.put("sky", "blue");
        context.checking(new Expectations() {{
            exactly(2).of(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("fe469d1c-3900-4649-a36e-562f8047f8e0");
            will(returnValue(true));
            exactly(2).of(mockUuiddao).getLatestBarcodeForUUID("fe469d1c-3900-4649-a36e-562f8047f8e0");
            will(returnValue("TCGA-02-0001-01C-01D-0183-04"));
            allowing(mockLthreeQueries).getHybRefId("TCGA-02-0001-01C-01D-0183-04");
            will(returnValue(123));
            allowing(mockLthreeQueries).getHybrefDataSetId(123, 123);
            will(returnValue(333));
            allowing(mockLthreeQueries).addProteinExpValue(with(any(List.class)));
        }});
        try {
            loader.loadProteinFile(MDA_MDA_RPPACore_ARCHIVE_FILE_LOCATION +
                    File.separator +
                    "badproteinvalue.txt", 123, antibodyAnnotationData);
        } catch(LoaderException le) {
            assertEquals("The protein value : hello! in data line : 'sky\thello!' is not a valid number. Failing load.", le.getMessage());
        }
    }

    @Test
    public void testLoadAgilentG4502A_07_1_DataFile() throws LoaderException {
        setUpforGeneEpression(UNC_AgilentG4502A_07_1_ARCHIVE_FILE_LOCATION, "AgilentG4502A_07_1", "unc.edu");
        testLoadDataFile(UNC_AgilentG4502A_07_1_ARCHIVE_FILE_LOCATION,
                UNC_AgilentG4502A_07_1_ARCHIVE_MAGETAB_FILE_LOCATION ,
                "US82800149_251976011553_S01_GE2_105_Dec08.txt_lmean.out.logratio.gene.tcga_level3.data.txt");
    }


    @Test
    public void testLoadAgilentG4502A_07_2_DataFile() throws LoaderException {
        setUpforGeneEpression(UNC_AgilentG4502A_07_2_ARCHIVE_FILE_LOCATION, "AgilentG4502A_07_2", "unc.edu");
        testLoadDataFile(UNC_AgilentG4502A_07_2_ARCHIVE_FILE_LOCATION,
                UNC_AgilentG4502A_07_2_ARCHIVE_MAGETAB_FILE_LOCATION ,
                "US82800149_251976011553_S01_GE2_105_Dec08.txt_lmean.out.logratio.gene.tcga_level3.data.txt");
    }

    @Test
    public void testLoadAgilentG4502A_07_3_DataFile() throws LoaderException {
        setUpforGeneEpression(UNC_AgilentG4502A_07_3_ARCHIVE_FILE_LOCATION, "AgilentG4502A_07_3", "unc.edu");
        testLoadDataFile(UNC_AgilentG4502A_07_3_ARCHIVE_FILE_LOCATION,
                UNC_AgilentG4502A_07_3_ARCHIVE_MAGETAB_FILE_LOCATION ,
                "US82800149_251976011553_S01_GE2_105_Dec08.txt_lmean.out.logratio.gene.tcga_level3.data.txt");
    }


    @Test
    public void testLoad_HT_HG_U133A_DataFile() throws LoaderException {
        setUpforGeneEpression(UNC_HT_HG_U133A_ARCHIVE_FILE_LOCATION, "HT_HG_U133A", "broad.mit.edu");
        testLoadDataFile(UNC_HT_HG_U133A_ARCHIVE_FILE_LOCATION,
                UNC_HT_HG_U133A_ARCHIVE_MAGETAB_FILE_LOCATION ,
                "US82800149_251976011553_S01_GE2_105_Dec08.txt_lmean.out.logratio.gene.tcga_level3.data.txt");
    }

    private void testLoadDataFile(final String fileLocation,
                                  final String sdrfLocation,
                                  final String dataFile) throws LoaderException {

        final List<Object[]> dataValList = new ArrayList<Object[]>();

        context.checking(new Expectations() {{
            allowing(mockLthreeQueries).getHybRefId(with(any(String.class)));
            will(returnValue(123));
            allowing(mockLthreeQueries).getHybrefDataSetId(123, 123);
            will(returnValue(333));
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("TCGA-A3-3306-01A-01R-0864-07");
            will(returnValue(false));
            allowing(mockLthreeQueries).addExpGeneValue(with(checkGenericList(dataValList)));

        }});
        TabDelimitedContentNavigator sdrfNavigator = loader.loadSDRF(sdrfLocation);
        final int extractIndex = loader.getExtractNamePosition(sdrfNavigator);
        loader.loadDataFile(fileLocation +
                File.separator +
                dataFile, extractIndex, sdrfNavigator, 123);

    }

    @Test
    public void testLoadHybRef() throws LoaderException {
        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("TCGA-AP-A051-01A-21D-A00U-05");
            will(returnValue(false));
            one(mockLthreeQueries).getHybRefId("TCGA-AP-A051-01A-21D-A00U-05");
            will(returnValue(123));
            one(mockLthreeQueries).getHybrefDataSetId(123, 123);
            will(returnValue(333));
        }});
        loader.loadHybRef("TCGA-AP-A051-01A-21D-A00U-05", "TCGA-AP-A051-01A-21D-A00U-05", 123);

    }

    @Test
    public void testLoadHybRefNullBarcode() throws LoaderException {
        context.checking(new Expectations() {{
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("TCGA-AP-A051-01A-21D-A00U-05");
            will(returnValue(false));
            one(mockLthreeQueries).getHybRefId("TCGA-AP-A051-01A-21D-A00U-05");
            will(returnValue(null));
            one(mockArchiveQueries).getUUIDforBarcode("TCGA-AP-A051-01A-21D-A00U-05");
            will(returnValue("uuid"));
            one(mockLthreeQueries).insertHybRef("TCGA-AP-A051-01A-21D-A00U-05", "TCGA-AP-A051-01", "uuid");
            one(mockLthreeQueries).getHybrefDataSetId(0, 123);
            will(returnValue(333));
        }});
        loader.loadHybRef("TCGA-AP-A051-01A-21D-A00U-05", "TCGA-AP-A051-01A-21D-A00U-05", 123);
    }

    @Test
    public void testLoadSDRF() throws LoaderException {
        setUpforMethylation();
        final String sdrfLocation = JHU_MAGE_TAB_LOCATION;

        TabDelimitedContentNavigator sdrfNavigator = loader.loadSDRF(sdrfLocation);
        assertEquals(sdrfNavigator.getNumRows(), 26);
        assertTrue(sdrfNavigator.getHeaderIDByName("Extract Name") != -1);
    }

    @Test
    public void testLoadSDRFBadSDRF() throws LoaderException {
        setUpforMethylation();
        final String sdrfLocation = "bad" + JHU_MAGE_TAB_LOCATION;
        try {
            loader.loadSDRF(sdrfLocation);
        } catch (LoaderException e) {
            assertEquals("Directory does not exist: " + sdrfLocation, e.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void testBadSDRNullSDRF() throws LoaderException {
        setUpforMethylation();
        try {
            loader.loadSDRF("");
        } catch (LoaderException e) {
            assertEquals("Invalid magetabDir ", e.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void testFindRecordInSDRF() throws LoaderException {
        setUpforMethylation();
        final String sdrfLocation = JHU_MAGE_TAB_LOCATION;
        TabDelimitedContentNavigator sdrfNavigator = loader.loadSDRF(sdrfLocation);
        final int extractIndex = loader.getExtractNamePosition(sdrfNavigator);
        String record = loader.findRecordInSDRF(sdrfNavigator, extractIndex, "TCGA-AP-A051-01A-21D-A00U-05");
        assertEquals(record, "TCGA-AP-A051-01A-21D-A00U-05");
    }

    @Test
    public void testFindEmptyRecordInSDRF() throws LoaderException {
        setUpforMethylation();
        final String sdrfLocation = JHU_MAGE_TAB_LOCATION;
        TabDelimitedContentNavigator sdrfNavigator = loader.loadSDRF(sdrfLocation);
        final int extractNameIndex = loader.getExtractNamePosition(sdrfNavigator);
        String record = loader.findRecordInSDRF(sdrfNavigator, extractNameIndex, "TCGA-AP-A051-01A-21D-A00U-05");
        assertEquals(record, "TCGA-AP-A051-01A-21D-A00U-05");
    }

    @Test
    public void testRetrieveExperiment() {
        setUpforMethylation();
        archive.setRevision("5");
        context.checking(new Expectations() {{
            one(mockArchiveQueries).getCenterByDomainNameAndPlatformName("jhu-usc.edu", "HumanMethylation27");
            will(returnValue(archiveCenter));
            one(mockLthreeQueries).getExperimentId("jhu-usc.edu_TEST.HumanMethylation27", 1, 5);
            will(returnValue(null));
            one(mockLthreeQueries).insertExperiment(2, 1, "jhu-usc.edu_TEST.HumanMethylation27", 1, 5);
            will(returnValue(123));
        }});

        ExperimentCenterBean bean = loader.retrieveExperiment(archive);
        assertTrue(bean.getCenterId() == 2);
        assertTrue(bean.getExperimentId() == 123);
    }

    @Test
    public void testRetrieveExistingExperiment() {
        setUpforMethylation();
        archive.setRevision("5");
        context.checking(new Expectations() {{
            one(mockArchiveQueries).getCenterByDomainNameAndPlatformName("jhu-usc.edu", "HumanMethylation27");
            will(returnValue(archiveCenter));
            one(mockLthreeQueries).getExperimentId("jhu-usc.edu_TEST.HumanMethylation27", 1, 5);
            will(returnValue(123));
        }});

        ExperimentCenterBean bean = loader.retrieveExperiment(archive);
        assertTrue(bean.getCenterId() == 2);
        assertTrue(bean.getExperimentId() == 123);
    }

    @Test
    public void testGAMiRNASeqLoading() throws LoaderException {
        loadMiRNASeqData(MIRNASEQ_ARCHIVE_LOCATION);
    }

    @Test
    public void testHiSeqMiRNASeqLoading() throws LoaderException {
        loadMiRNASeqData(HISEQ_MIRNASEQ_ARCHIVE_LOCATION);
    }


    private void loadMiRNASeqData(final String archiveLocation) throws LoaderException {

        final Long archiveId = 1L;
        final String archiveName = "archiveName";
        final String archiveFileName = archiveLocation + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;
        final Integer archiveSeriaIndex = 1;
        final Integer archiveRevision = 0;
        final String miRnaPattern = "*.mirna.quantification.txt";
        final String isoformPattern = "*.isoform.quantification.txt";

        final List<String> archiveFilePatterns = new ArrayList<String>();
        archiveFilePatterns.add(miRnaPattern);
        archiveFilePatterns.add(isoformPattern);

        final Integer centerId = 2;
        final String centerName = "bcgsc.ca";
        final Integer platformId = 1;
        final String platformName = "IlluminaGA_miRNASeq";
        final String tumorAbbreviation = "BRCA";
        final Long miRnaFileId = 1L;
        final Long isoFormFileId = 1L;

        final Integer experimentId = 0;
        final Integer loadComplete = 0;
        final Integer dataLevel = 3;
        final String miRnaSourceFileType = "mirna_quantification";
        final String isoformSourceFileType = "isoform_quantification";
        final String publicAccessLevel = "PUBLIC";
        final Integer dataSetId = 1;
        final String aliquotBarcode = "TCGA-A1-A0SE-01A-11R-A085-13";
        final String uuid = "69de087d-e31d-4ff5-a760-6be8da96b6e2";
        final String miRnaFilename = aliquotBarcode + ".mirna.quantification.txt";
        final String isoformFilename = uuid + ".isoform.quantification.txt";
        final Integer hybRefId = 1;
        final Integer hybRefDataSetId = 1;

        setupForCGCCArchive(archiveId,
                archiveName,
                archiveFileName,
                archiveSeriaIndex,
                archiveRevision,
                archiveFilePatterns,
                centerId,
                centerName,
                platformId,
                platformName,
                tumorAbbreviation);

        context.checking(new Expectations() {{

            one(mockArchiveQueries).getArchiveIdByName(archiveName);
            will(returnValue(archiveId));
            one(mockArchiveQueries).getArchive(archiveId);
            will(returnValue(archive));
            one(mockArchiveQueries).getFilesForArchive(archiveId);
            will(returnValue(getFilesForMiRNASeqArchive(miRnaFileId, miRnaFilename, isoFormFileId, isoformFilename)));
            one(mockArchiveQueries).getSdrfDeployLocation(centerName, platformName, tumorAbbreviation);
            will(returnValue(archiveFileName));
            one(mockArchiveQueries).getCenterByDomainNameAndPlatformName(centerName, platformName);
            will(returnValue(getCenterForCGCCArchive(centerId)));
            one(mockLthreeQueries).getExperimentId(centerName + "_" + tumorAbbreviation + "." + platformName, archiveSeriaIndex, archiveRevision);
            will(returnValue(null));
            one(mockLthreeQueries).insertExperiment(centerId, platformId, centerName + "_" + tumorAbbreviation + "." + platformName, archiveSeriaIndex, archiveRevision);

            // miRNA quantification
            one(mockLthreeQueries).createDataSet(centerId, experimentId, platformId, archiveName + "/" + miRnaPattern, miRnaSourceFileType, publicAccessLevel, loadComplete, dataLevel, archiveId);
            will(returnValue(dataSetId));
            one(mockLthreeQueries).createDataSetFile(dataSetId, miRnaFilename, miRnaFileId);
            one(mockCommonBarcodeAndUUIDValidator).getAliquotBarcode(with(checkInput(miRnaFilename)));
            will(returnValue(aliquotBarcode));
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat(aliquotBarcode);
            will(returnValue(false));
            one(mockLthreeQueries).getHybRefId(aliquotBarcode);
            will(returnValue(hybRefId));
            one(mockLthreeQueries).getHybrefDataSetId(hybRefId, dataSetId);
            will(returnValue(hybRefDataSetId));
            one(mockLthreeQueries).addMirnaSeqValue(with(checkMiRnaSeq(
                    new ArrayList<Object[]>(), "hsa-let-7a-1", "19969", "6050.123099", "N", "", "", "", dataSetId, hybRefId))); // Those values come from the test miRNA file
            one(mockLthreeQueries).updateDataSetFile(dataSetId, miRnaFilename, miRnaFileId);
            one(mockLthreeQueries).updateDataSet(dataSetId);

            // miRNA quantification isoform quantification
            one(mockLthreeQueries).createDataSet(centerId, experimentId, platformId, archiveName + "/" + isoformPattern, isoformSourceFileType, publicAccessLevel, loadComplete, dataLevel, archiveId);
            will(returnValue(dataSetId));
            one(mockLthreeQueries).createDataSetFile(dataSetId, isoformFilename, isoFormFileId);
            one(mockCommonBarcodeAndUUIDValidator).getAliquotBarcode(with(checkInput(isoformFilename)));
            will(returnValue(null));
            one(mockCommonBarcodeAndUUIDValidator).getUUID(with(checkInput(isoformFilename)));
            will(returnValue(uuid));
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat(uuid);
            will(returnValue(true));
            one(mockUuiddao).getLatestBarcodeForUUID(uuid);
            will(returnValue(aliquotBarcode));
            one(mockLthreeQueries).getHybRefId(aliquotBarcode);
            will(returnValue(hybRefId));
            one(mockLthreeQueries).getHybrefDataSetId(hybRefId, dataSetId);
            will(returnValue(hybRefDataSetId));
            one(mockLthreeQueries).addMirnaSeqValue(with(checkMiRnaSeq(
                    new ArrayList<Object[]>(), "hsa-let-7a-1", "4", "1.211903", "N", "hg19:9:96938242-96938263:+", "mature", "MIMAT0000062", dataSetId, hybRefId))); // Those values come from the test isoform file
            one(mockLthreeQueries).updateDataSetFile(dataSetId, isoformFilename, isoFormFileId);
            one(mockLthreeQueries).updateDataSet(dataSetId);

            // Update
            one(mockLthreeQueries).updateArchiveLoadedDate(archiveId);
            one(mockArchiveQueries).updateArchiveInfo(archiveId);
        }});

        loader.loadArchiveByName(archiveName);
    }


    @Test
    public void getAnnotations() throws Exception{
        final String sdrfFile = MDA_RPPA_CORE_MAGE_TAB_ARCHIVE_LOCATION+File.separator+"mdanderson.org_UCEC.MDA_RPPA_Core.sdrf.txt";
        final Map<String,String> annotations = loader.getAnnotations(getSdrfNavigator(sdrfFile),MDA_RPPA_CORE_MAGE_TAB_ARCHIVE_LOCATION);
        assertEquals(5,annotations.size());

        final String[][] expectedValues = new String[][]{
            {"14-3-3_epsilon-M-C","YWHAE"},
            {"4E-BP1-R-V","EIF4EBP1"},
            {"4E-BP1_pS65-R-V","EIF4EBP1"},
            {"4E-BP1_pT37-R-V","EIF4EBP1"},
            {"4E-BP1_pT70-R-C","EIF4EBP1"}
        };

        for(int i=0; i < expectedValues.length;i++){
            assertEquals(expectedValues[i][1],annotations.get(expectedValues[i][0]));
        }

    }

    private TabDelimitedContentNavigator getSdrfNavigator(final String sdrfFile) throws Exception{
        TabDelimitedContent sdrf = new TabDelimitedContentImpl();
        TabDelimitedFileParser sdrfParser = new TabDelimitedFileParser();
        sdrfParser.setTabDelimitedContent(sdrf);
        sdrfParser.loadTabDelimitedContent(new File(sdrfFile),true );
        sdrfParser.loadTabDelimitedContentHeader();
        TabDelimitedContentNavigator sdrfNavigator = new TabDelimitedContentNavigator();
        sdrfNavigator.setTabDelimitedContent( sdrf );
        return sdrfNavigator;
    }

    @Test
    public void testGARNASeqLoading() throws LoaderException {
        loadRNASeqData(RNASEQ_ARCHIVE_LOCATION);
    }

    @Test
    public void testHiSeqRNASeqLoading() throws LoaderException {
        loadRNASeqData(HISEQ_RNASEQ_ARCHIVE_LOCATION);
    }

    @Test
    public void testHiSeqRNASeqV2Loading() throws LoaderException {
        loadRNASeqV2Data(HISEQ_RNASEQV2_ARCHIVE_LOCATION);
    }

    /**
     * Load RNASeqV2 data for the given archive location.
     *
     * @param archiveLocation the archive location
     */
    private void loadRNASeqV2Data(final String archiveLocation) throws LoaderException {

        final Long archiveId = 1L;
        final String archiveName = "archiveName";
        final String archiveFileName = archiveLocation + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;
        final Integer archiveSeriaIndex = 1;
        final Integer archiveRevision = 0;
        final String exonPattern = "*.exon_quantification.txt";
        final String junctionPattern = "*.junction_quantification.txt";
        final String rsemGenesResultsPattern = "*.rsem.genes.results";
        final String rsemGenesNormalizedResultsPattern = "*.rsem.genes.normalized_results";
        final String rsemIsoformsResultsPattern = "*.rsem.isoforms.results";
        final String rsemIsoformsNormalizedResultsPattern = "*.rsem.isoforms.normalized_results";

        final List<String> archiveFilePatterns = new ArrayList<String>();
        archiveFilePatterns.add(exonPattern);
        archiveFilePatterns.add(junctionPattern);
        archiveFilePatterns.add(rsemGenesResultsPattern);
        archiveFilePatterns.add(rsemGenesNormalizedResultsPattern);
        archiveFilePatterns.add(rsemIsoformsResultsPattern);
        archiveFilePatterns.add(rsemIsoformsNormalizedResultsPattern);

        final Integer centerId = 2;
        final String centerName = "unc.edu";
        final Integer platformId = 1;
        final String platformName = "IlluminaHiSeq_RNASeqV2";
        final String tumorAbbreviation = "FOO";

        setupForCGCCArchive(archiveId,
                archiveName,
                archiveFileName,
                archiveSeriaIndex,
                archiveRevision,
                archiveFilePatterns,
                centerId,
                centerName,
                platformId,
                platformName,
                tumorAbbreviation);

        final Long exonFileId = 1L;
        final Long junctionFileId = 1L;
        final Long rsemGenesResultsFileId = 1L;
        final Long rsemGenesNormalizedResultsFileId = 1L;
        final Long rsemIsoformsResultsFileId = 1L;
        final Long rsemIsoformsNormalizedResultsFileId = 1L;

        final Integer experimentId = 0;
        final Integer loadComplete = 0;
        final Integer dataLevel = 3;
        final String exonSourceFileType = "expression_exon";
        final String rsemGeneSourceFileType = "expression_rsem_gene";
        final String rsemGeneNormalizedSourceFileType = "expression_rsem_gene_normalized";
        final String rsemIsoformsSourceFileType = "expression_rsem_isoforms";
        final String rsemIsoformsNormalizedSourceFileType = "expression_rsem_isoforms_normalized";

        final String junctionSourceFileType = "expression_junction";
        final String publicAccessLevel = "PUBLIC";
        final Integer dataSetId = 1;
        final String aliquotBarcode = "TCGA-AZ-AZ09-01A-23B-AZ09-01";
        final String uuid = "69de087d-e31d-4ff5-a760-6be8da96b6e2";
        final String exonFilename = "unc.edu.TCGA-AZ-AZ09-01A-23B-AZ09-01.exon_quantification.txt";
        final String junctionFilename = "unc.edu." + uuid + ".junction_quantification.txt";
        final String rsemGenesResultsFilename = "unc.edu.TCGA-AZ-AZ09-01A-23B-AZ09-01.rsem.genes.results";
        final String rsemGenesNormalizedResultsFilename = "unc.edu.TCGA-AZ-AZ09-01A-23B-AZ09-01.rsem.genes.normalized_results";
        final String rsemIsoformsResultsFilename = "unc.edu.TCGA-AZ-AZ09-01A-23B-AZ09-01.rsem.isoforms.results";
        final String rsemIsoformsNormalizedResultsFilename = "unc.edu.TCGA-AZ-AZ09-01A-23B-AZ09-01.rsem.isoforms.normalized_results";
        final Integer hybRefId = 1;
        final Integer hybRefDataSetId = 1;

        context.checking(new Expectations() {{

            one(mockArchiveQueries).getArchiveIdByName(archiveName);
            will(returnValue(archiveId));
            one(mockArchiveQueries).getArchive(archiveId);
            will(returnValue(archive));
            one(mockArchiveQueries).getFilesForArchive(archiveId);
            will(returnValue(getFilesForRNASeqV2Archive(exonFileId, exonFilename, junctionFileId, junctionFilename,
                    rsemGenesResultsFileId, rsemGenesResultsFilename, rsemGenesNormalizedResultsFileId, rsemGenesNormalizedResultsFilename,
                    rsemIsoformsResultsFileId, rsemIsoformsResultsFilename, rsemIsoformsNormalizedResultsFileId, rsemIsoformsNormalizedResultsFilename)));
            one(mockArchiveQueries).getSdrfDeployLocation(centerName, platformName, tumorAbbreviation);
            will(returnValue(archiveFileName));
            one(mockArchiveQueries).getCenterByDomainNameAndPlatformName(centerName, platformName);
            will(returnValue(getCenterForCGCCArchive(centerId)));
            one(mockLthreeQueries).getExperimentId(centerName + "_" + tumorAbbreviation + "." + platformName, archiveSeriaIndex, archiveRevision);
            will(returnValue(null));
            one(mockLthreeQueries).insertExperiment(centerId, platformId, centerName + "_" + tumorAbbreviation + "." + platformName, archiveSeriaIndex, archiveRevision);

            // exon quantification
            one(mockLthreeQueries).createDataSet(centerId, experimentId, platformId, archiveName + "/" + exonPattern, exonSourceFileType, publicAccessLevel, loadComplete, dataLevel, archiveId);
            will(returnValue(dataSetId));
            one(mockLthreeQueries).createDataSetFile(dataSetId, exonFilename, exonFileId);
            one(mockCommonBarcodeAndUUIDValidator).getAliquotBarcode(with(checkInput(exonFilename)));
            will(returnValue(aliquotBarcode));
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat(aliquotBarcode);
            will(returnValue(false));
            one(mockLthreeQueries).getHybRefId(aliquotBarcode);
            will(returnValue(hybRefId));
            one(mockLthreeQueries).getHybrefDataSetId(hybRefId, dataSetId);
            will(returnValue(hybRefDataSetId));
            one(mockLthreeQueries).addRnaSeqValue(with(checkRnaSeq(
                    new ArrayList<Object[]>(), "chr1:12595-12721:+", "62", "0.488188976377953", "0.0599623694322567", null, null, null, dataSetId, hybRefId))); // Those values come from the test exon file
            one(mockLthreeQueries).updateDataSetFile(dataSetId, exonFilename, exonFileId);
            one(mockLthreeQueries).updateDataSet(dataSetId);

            // junction quantification
            one(mockLthreeQueries).createDataSet(centerId, experimentId, platformId, archiveName + "/" + junctionPattern, junctionSourceFileType, publicAccessLevel, loadComplete, dataLevel, archiveId);
            will(returnValue(dataSetId));
            one(mockLthreeQueries).createDataSetFile(dataSetId, junctionFilename, junctionFileId);
            one(mockCommonBarcodeAndUUIDValidator).getAliquotBarcode(with(checkInput(junctionFilename)));
            will(returnValue(null));
            one(mockCommonBarcodeAndUUIDValidator).getUUID(with(checkInput(junctionFilename)));
            will(returnValue(uuid));
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat(uuid);
            will(returnValue(true));
            one(mockUuiddao).getLatestBarcodeForUUID(uuid);
            will(returnValue(aliquotBarcode));
            one(mockLthreeQueries).getHybRefId(aliquotBarcode);
            will(returnValue(hybRefId));
            one(mockLthreeQueries).getHybrefDataSetId(hybRefId, dataSetId);
            will(returnValue(hybRefDataSetId));
            one(mockLthreeQueries).addRnaSeqValue(with(checkRnaSeq(
                    new ArrayList<Object[]>(), "chr1:12227:+,chr1:12595:+", "0", null, null, null, null, null, dataSetId, hybRefId))); // Those values come from the test junction file
            one(mockLthreeQueries).updateDataSetFile(dataSetId, junctionFilename, junctionFileId);
            one(mockLthreeQueries).updateDataSet(dataSetId);

            // gene quantification - rsem genes results
            one(mockLthreeQueries).createDataSet(centerId, experimentId, platformId, archiveName + "/" + rsemGenesResultsPattern, rsemGeneSourceFileType, publicAccessLevel, loadComplete, dataLevel, archiveId);
            will(returnValue(dataSetId));
            one(mockLthreeQueries).createDataSetFile(dataSetId, rsemGenesResultsFilename, rsemGenesResultsFileId);
            one(mockCommonBarcodeAndUUIDValidator).getAliquotBarcode(with(checkInput(rsemGenesResultsFilename)));
            will(returnValue(aliquotBarcode));
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat(aliquotBarcode);
            will(returnValue(false));
            one(mockLthreeQueries).getHybRefId(aliquotBarcode);
            will(returnValue(hybRefId));
            one(mockLthreeQueries).getHybrefDataSetId(hybRefId, dataSetId);
            will(returnValue(hybRefDataSetId));
            one(mockLthreeQueries).addRnaSeqValue(with(checkRnaSeq(
                    new ArrayList<Object[]>(), "?|100130426", "0.00", null, null, null, "0", "uc011lsn.1", dataSetId, hybRefId))); // Those values come from the test rsem genes results file
            one(mockLthreeQueries).updateDataSetFile(dataSetId, rsemGenesResultsFilename, rsemGenesResultsFileId);
            one(mockLthreeQueries).updateDataSet(dataSetId);

            // gene quantification - rsem genes normalized results
            one(mockLthreeQueries).createDataSet(centerId, experimentId, platformId, archiveName + "/" + rsemGenesNormalizedResultsPattern, rsemGeneNormalizedSourceFileType, publicAccessLevel, loadComplete, dataLevel, archiveId);
            will(returnValue(dataSetId));
            one(mockLthreeQueries).createDataSetFile(dataSetId, rsemGenesNormalizedResultsFilename, rsemGenesNormalizedResultsFileId);
            one(mockCommonBarcodeAndUUIDValidator).getAliquotBarcode(with(checkInput(rsemGenesNormalizedResultsFilename)));
            will(returnValue(aliquotBarcode));
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat(aliquotBarcode);
            will(returnValue(false));
            one(mockLthreeQueries).getHybRefId(aliquotBarcode);
            will(returnValue(hybRefId));
            one(mockLthreeQueries).getHybrefDataSetId(hybRefId, dataSetId);
            will(returnValue(hybRefDataSetId));
            one(mockLthreeQueries).addRnaSeqValue(with(checkRnaSeq(
                    new ArrayList<Object[]>(), "?|100133144", null, null, null, "11.3650", null, null, dataSetId, hybRefId))); // Those values come from the test rsem genes normalized results file
            one(mockLthreeQueries).updateDataSetFile(dataSetId, rsemGenesNormalizedResultsFilename, rsemGenesNormalizedResultsFileId);
            one(mockLthreeQueries).updateDataSet(dataSetId);

            // gene quantification - rsem isoforms results
            one(mockLthreeQueries).createDataSet(centerId, experimentId, platformId, archiveName + "/" + rsemIsoformsResultsPattern, rsemIsoformsSourceFileType, publicAccessLevel, loadComplete, dataLevel, archiveId);
            will(returnValue(dataSetId));
            one(mockLthreeQueries).createDataSetFile(dataSetId, rsemIsoformsResultsFilename, rsemIsoformsResultsFileId);
            one(mockCommonBarcodeAndUUIDValidator).getAliquotBarcode(with(checkInput(rsemIsoformsResultsFilename)));
            will(returnValue(aliquotBarcode));
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat(aliquotBarcode);
            will(returnValue(false));
            one(mockLthreeQueries).getHybRefId(aliquotBarcode);
            will(returnValue(hybRefId));
            one(mockLthreeQueries).getHybrefDataSetId(hybRefId, dataSetId);
            will(returnValue(hybRefDataSetId));
            one(mockLthreeQueries).addRnaSeqValue(with(checkRnaSeq(
                    new ArrayList<Object[]>(), "uc010unu.1", "33.72", null, null, null, "1.10337072831939e-06", null, dataSetId, hybRefId))); // Those values come from the test rsem isoforms results file
            one(mockLthreeQueries).updateDataSetFile(dataSetId, rsemIsoformsResultsFilename, rsemIsoformsResultsFileId);
            one(mockLthreeQueries).updateDataSet(dataSetId);

            // gene quantification - rsem isoforms normalized results
            one(mockLthreeQueries).createDataSet(centerId, experimentId, platformId, archiveName + "/" + rsemIsoformsNormalizedResultsPattern, rsemIsoformsNormalizedSourceFileType, publicAccessLevel, loadComplete, dataLevel, archiveId);
            will(returnValue(dataSetId));
            one(mockLthreeQueries).createDataSetFile(dataSetId, rsemIsoformsNormalizedResultsFilename, rsemIsoformsNormalizedResultsFileId);
            one(mockCommonBarcodeAndUUIDValidator).getAliquotBarcode(with(checkInput(rsemIsoformsNormalizedResultsFilename)));
            will(returnValue(aliquotBarcode));
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat(aliquotBarcode);
            will(returnValue(false));
            one(mockLthreeQueries).getHybRefId(aliquotBarcode);
            will(returnValue(hybRefId));
            one(mockLthreeQueries).getHybrefDataSetId(hybRefId, dataSetId);
            will(returnValue(hybRefDataSetId));
            one(mockLthreeQueries).addRnaSeqValue(with(checkRnaSeq(
                    new ArrayList<Object[]>(), "uc010unu.1", null, null, null, "13.5700", null, null, dataSetId, hybRefId))); // Those values come from the test rsem isoforms normalized  results file
            one(mockLthreeQueries).updateDataSetFile(dataSetId, rsemIsoformsNormalizedResultsFilename, rsemIsoformsNormalizedResultsFileId);
            one(mockLthreeQueries).updateDataSet(dataSetId);

            // Update
            one(mockLthreeQueries).updateArchiveLoadedDate(archiveId);
            one(mockArchiveQueries).updateArchiveInfo(archiveId);
        }});

        loader.loadArchiveByName(archiveName);
    }

    /**
     * Return a {@link List} of {@link FileInfo} for the given parameters.
     *
     * @param exonFileId the exon file Id
     * @param exonFilename the exon filename
     * @param junctionFileId the junction file Id
     * @param junctionFilename the junction filename
     * @param rsemGenesResultsFileId the rsem genes results file Id
     * @param rsemGenesResultsFilename the rsem genes results filename
     * @param rsemGenesNormalizedResultsFileId the rsem genes normalized results file Id
     * @param rsemGenesNormalizedResultsFilename the rsem genes normalized results filename
     * @param rsemIsoformsResultsFileId the rsem isoforms results file Id
     * @param rsemIsoformsResultsFilename the rsem isoforms results filename
     * @param rsemIsoformsNormalizedResultsFileId the rsem isoforms normalized results file Id
     * @param rsemIsoformsNormalizedResultsFilename  the rsem isoforms normalized results filename
     * @return a {@link List} of {@link FileInfo} for the given parameters
     */
    private List<FileInfo> getFilesForRNASeqV2Archive(final Long exonFileId,
                                                      final String exonFilename,
                                                      final Long junctionFileId,
                                                      final String junctionFilename,
                                                      final Long rsemGenesResultsFileId,
                                                      final String rsemGenesResultsFilename,
                                                      final Long rsemGenesNormalizedResultsFileId,
                                                      final String rsemGenesNormalizedResultsFilename,
                                                      final Long rsemIsoformsResultsFileId,
                                                      final String rsemIsoformsResultsFilename,
                                                      final Long rsemIsoformsNormalizedResultsFileId,
                                                      final String rsemIsoformsNormalizedResultsFilename) {

        final List<FileInfo> result = new ArrayList<FileInfo>();

        final FileInfo exonFileInfo = new FileInfo();
        exonFileInfo.setId(exonFileId);
        exonFileInfo.setFileName(exonFilename);
        result.add(exonFileInfo);

        final FileInfo junctionFileInfo = new FileInfo();
        junctionFileInfo.setId(junctionFileId);
        junctionFileInfo.setFileName(junctionFilename);
        result.add(junctionFileInfo);

        final FileInfo rsemGenesResultsFileInfo = new FileInfo();
        rsemGenesResultsFileInfo.setId(rsemGenesResultsFileId);
        rsemGenesResultsFileInfo.setFileName(rsemGenesResultsFilename);
        result.add(rsemGenesResultsFileInfo);

        final FileInfo rsemGenesNormalizedResultsFileInfo = new FileInfo();
        rsemGenesNormalizedResultsFileInfo.setId(rsemGenesNormalizedResultsFileId);
        rsemGenesNormalizedResultsFileInfo.setFileName(rsemGenesNormalizedResultsFilename);
        result.add(rsemGenesNormalizedResultsFileInfo);

        final FileInfo rsemIsoformsResultsFileInfo = new FileInfo();
        rsemIsoformsResultsFileInfo.setId(rsemIsoformsResultsFileId);
        rsemIsoformsResultsFileInfo.setFileName(rsemIsoformsResultsFilename);
        result.add(rsemIsoformsResultsFileInfo);

        final FileInfo rsemIsoformsNormalizedResultsFileInfo = new FileInfo();
        rsemIsoformsNormalizedResultsFileInfo.setId(rsemIsoformsNormalizedResultsFileId);
        rsemIsoformsNormalizedResultsFileInfo.setFileName(rsemIsoformsNormalizedResultsFilename);
        result.add(rsemIsoformsNormalizedResultsFileInfo);

        return result;
    }

    private void loadRNASeqData(final String archiveLocation) throws LoaderException {
        final Long archiveId = 1L;
        final String archiveName = "archiveName";
        final String archiveFileName = archiveLocation + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;
        final Integer archiveSeriaIndex = 1;
        final Integer archiveRevision = 0;
        final String exonPattern = "*.exon.quantification.txt";
        final String genePattern = "*.gene.quantification.txt";
        final String junctionPattern = "*.spljxn.quantification.txt";

        final List<String> archiveFilePatterns = new ArrayList<String>();
        archiveFilePatterns.add(exonPattern);
        archiveFilePatterns.add(genePattern);
        archiveFilePatterns.add(junctionPattern);

        final Integer centerId = 2;
        final String centerName = "unc.edu";
        final Integer platformId = 1;
        final String platformName = "IlluminaGA_RNASeq";
        final String tumorAbbreviation = "READ";
        final Long exonFileId = 1L;
        final Long geneFileId = 1L;
        final Long junctionFileId = 1L;

        final Integer experimentId = 0;
        final Integer loadComplete = 0;
        final Integer dataLevel = 3;
        final String exonSourceFileType = "expression_exon";
        final String geneSourceFileType = "expression_gene";
        final String junctionSourceFileType = "expression_junction";
        final String publicAccessLevel = "PUBLIC";
        final Integer dataSetId = 1;
        final String aliquotBarcode = "TCGA-AG-3587-01A-01R-0821-07";
        final String uuid = "69de087d-e31d-4ff5-a760-6be8da96b6e2";
        final String exonFilename = "UNCID_53197." + aliquotBarcode + ".100914_UNC8-RDR3001640_00029_FC_62HPTAAXX.6.trimmed.annotated.exon.quantification.txt";
        final String geneFilename = "UNCID_52843." + aliquotBarcode + ".100914_UNC8-RDR3001640_00029_FC_62HPTAAXX.6.trimmed.annotated.gene.quantification.txt";
        final String junctionFilename = "UNCID_53190." + uuid + ".100914_UNC8-RDR3001640_00029_FC_62HPTAAXX.6.trimmed.annotated.spljxn.quantification.txt";
        final Integer hybRefId = 1;
        final Integer hybRefDataSetId = 1;

        setupForCGCCArchive(archiveId,
                archiveName,
                archiveFileName,
                archiveSeriaIndex,
                archiveRevision,
                archiveFilePatterns,
                centerId,
                centerName,
                platformId,
                platformName,
                tumorAbbreviation);

        context.checking(new Expectations() {{

            one(mockArchiveQueries).getArchiveIdByName(archiveName);
            will(returnValue(archiveId));
            one(mockArchiveQueries).getArchive(archiveId);
            will(returnValue(archive));
            one(mockArchiveQueries).getFilesForArchive(archiveId);
            will(returnValue(getFilesForRNASeqArchive(exonFileId, exonFilename, geneFileId, geneFilename, junctionFileId, junctionFilename)));
            one(mockArchiveQueries).getSdrfDeployLocation(centerName, platformName, tumorAbbreviation);
            will(returnValue(archiveFileName));
            one(mockArchiveQueries).getCenterByDomainNameAndPlatformName(centerName, platformName);
            will(returnValue(getCenterForCGCCArchive(centerId)));
            one(mockLthreeQueries).getExperimentId(centerName + "_" + tumorAbbreviation + "." + platformName, archiveSeriaIndex, archiveRevision);
            will(returnValue(null));
            one(mockLthreeQueries).insertExperiment(centerId, platformId, centerName + "_" + tumorAbbreviation + "." + platformName, archiveSeriaIndex, archiveRevision);

            // exon quantification
            one(mockLthreeQueries).createDataSet(centerId, experimentId, platformId, archiveName + "/" + exonPattern, exonSourceFileType, publicAccessLevel, loadComplete, dataLevel, archiveId);
            will(returnValue(dataSetId));
            one(mockLthreeQueries).createDataSetFile(dataSetId, exonFilename, exonFileId);
            one(mockCommonBarcodeAndUUIDValidator).getAliquotBarcode(with(checkInput(exonFilename)));
            will(returnValue(aliquotBarcode));
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat(aliquotBarcode);
            will(returnValue(false));
            one(mockLthreeQueries).getHybRefId(aliquotBarcode);
            will(returnValue(hybRefId));
            one(mockLthreeQueries).getHybrefDataSetId(hybRefId, dataSetId);
            will(returnValue(hybRefDataSetId));
            one(mockLthreeQueries).addRnaSeqValue(with(checkRnaSeq(
                    new ArrayList<Object[]>(), "chr1:11874-12227:+", "611", "1.72598870056497", "1.00192496271577", null, null, null, dataSetId, hybRefId))); // Those values come from the test exon file
            one(mockLthreeQueries).updateDataSetFile(dataSetId, exonFilename, exonFileId);
            one(mockLthreeQueries).updateDataSet(dataSetId);

            // gene quantification
            one(mockLthreeQueries).createDataSet(centerId, experimentId, platformId, archiveName + "/" + genePattern, geneSourceFileType, publicAccessLevel, loadComplete, dataLevel, archiveId);
            will(returnValue(dataSetId));
            one(mockLthreeQueries).createDataSetFile(dataSetId, geneFilename, geneFileId);
            one(mockCommonBarcodeAndUUIDValidator).getAliquotBarcode(with(checkInput(geneFilename)));
            will(returnValue(aliquotBarcode));
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat(aliquotBarcode);
            will(returnValue(false));
            one(mockLthreeQueries).getHybRefId(aliquotBarcode);
            will(returnValue(hybRefId));
            one(mockLthreeQueries).getHybrefDataSetId(hybRefId, dataSetId);
            will(returnValue(hybRefDataSetId));
            one(mockLthreeQueries).addRnaSeqValue(with(checkRnaSeq(
                    new ArrayList<Object[]>(), "?|100130426", "0", "0", "0", null, null, null, dataSetId, hybRefId))); // Those values come from the test gene file
            one(mockLthreeQueries).updateDataSetFile(dataSetId, geneFilename, geneFileId);
            one(mockLthreeQueries).updateDataSet(dataSetId);

            // junction quantification
            one(mockLthreeQueries).createDataSet(centerId, experimentId, platformId, archiveName + "/" + junctionPattern, junctionSourceFileType, publicAccessLevel, loadComplete, dataLevel, archiveId);
            will(returnValue(dataSetId));
            one(mockLthreeQueries).createDataSetFile(dataSetId, junctionFilename, junctionFileId);
            one(mockCommonBarcodeAndUUIDValidator).getAliquotBarcode(with(checkInput(junctionFilename)));
            will(returnValue(null));
            one(mockCommonBarcodeAndUUIDValidator).getUUID(with(checkInput(junctionFilename)));
            will(returnValue(uuid));
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat(uuid);
            will(returnValue(true));
            one(mockUuiddao).getLatestBarcodeForUUID(uuid);
            will(returnValue(aliquotBarcode));
            one(mockLthreeQueries).getHybRefId(aliquotBarcode);
            will(returnValue(hybRefId));
            one(mockLthreeQueries).getHybrefDataSetId(hybRefId, dataSetId);
            will(returnValue(hybRefDataSetId));
            one(mockLthreeQueries).addRnaSeqValue(with(checkRnaSeq(
                    new ArrayList<Object[]>(), "chr1:12227:+,chr1:12595:+", "1", null, null, null, null, null, dataSetId, hybRefId))); // Those values come from the test junction file
            one(mockLthreeQueries).updateDataSetFile(dataSetId, junctionFilename, junctionFileId);
            one(mockLthreeQueries).updateDataSet(dataSetId);

            // Update
            one(mockLthreeQueries).updateArchiveLoadedDate(archiveId);
            one(mockArchiveQueries).updateArchiveInfo(archiveId);
        }});

        loader.loadArchiveByName(archiveName);
    }

    @Test
    public void testGetSourceFileTypeValid() throws LoaderException {
        assertEquals("snp_analysis.seg", loader.getSourceFileType("hudsonalpha.org", "Genome_Wide_SNP_6", "snp_analysis.seg.txt"));
        assertEquals("snp_analysis.seg", loader.getSourceFileType("broad.mit.edu", "Genome_Wide_SNP_6", "snp_analysis.seg.txt"));
        assertEquals("snp_analysis.segnormal", loader.getSourceFileType("broad.mit.edu", "Genome_Wide_SNP_6", "snp_analysis.segnormal.txt"));
        assertEquals("loh", loader.getSourceFileType("broad.mit.edu", "Genome_Wide_SNP_6", "snp_analysis.loh.txt"));
        assertEquals("snp_analysis.seg", loader.getSourceFileType("hudsonalpha.org", "ANY", "snp_analysis.seg.txt"));
        assertEquals("snp_analysis.segnormal", loader.getSourceFileType("hudsonalpha.org", "ANY", "snp_analysis.segnormal.txt"));
        assertEquals("loh", loader.getSourceFileType("hudsonalpha.org", "ANY", "snp_analysis.loh.txt"));
        assertEquals("snp_analysis.hg18.seg", loader.getSourceFileType("broad.mit.edu", "Genome_Wide_SNP_6", "*.hg18.seg.txt"));
        assertEquals("snp_analysis.hg19.seg", loader.getSourceFileType("broad.mit.edu", "Genome_Wide_SNP_6", "*.hg19.seg.txt"));
        assertEquals("snp_analysis.nocnv_hg18.seg", loader.getSourceFileType("broad.mit.edu", "Genome_Wide_SNP_6", "*.nocnv_hg18.seg.txt"));
        assertEquals("snp_analysis.nocnv_hg19.seg", loader.getSourceFileType("broad.mit.edu", "Genome_Wide_SNP_6", "*.nocnv_hg19.seg.txt"));
    }

    @Test
    public void testGetSourceFileTypeRNASeqValid() throws LoaderException {

        assertEquals("expression_exon", loader.getSourceFileType("bcgsc.ca", "IlluminaGA_RNASeq", "*.exon.quantification.txt"));
        assertEquals("expression_junction", loader.getSourceFileType("bcgsc.ca", "IlluminaGA_RNASeq", "*.spljxn.quantification.txt"));
        assertEquals("expression_gene", loader.getSourceFileType("bcgsc.ca", "IlluminaGA_RNASeq", "*.gene.quantification.txt"));

        assertEquals("expression_exon", loader.getSourceFileType("bcgsc.ca", "IlluminaHiSeq_RNASeq", "*.exon.quantification.txt"));
        assertEquals("expression_junction", loader.getSourceFileType("bcgsc.ca", "IlluminaHiSeq_RNASeq", "*.spljxn.quantification.txt"));
        assertEquals("expression_gene", loader.getSourceFileType("bcgsc.ca", "IlluminaHiSeq_RNASeq", "*.gene.quantification.txt"));

        assertEquals("expression_exon", loader.getSourceFileType("unc.edu", "IlluminaGA_RNASeq", "*.exon.quantification.txt"));
        assertEquals("expression_junction", loader.getSourceFileType("unc.edu", "IlluminaGA_RNASeq", "*.spljxn.quantification.txt"));
        assertEquals("expression_gene", loader.getSourceFileType("unc.edu", "IlluminaGA_RNASeq", "*.gene.quantification.txt"));

        assertEquals("expression_exon", loader.getSourceFileType("unc.edu", "IlluminaHiSeq_RNASeq", "*.exon.quantification.txt"));
        assertEquals("expression_junction", loader.getSourceFileType("unc.edu", "IlluminaHiSeq_RNASeq", "*.spljxn.quantification.txt"));
        assertEquals("expression_gene", loader.getSourceFileType("unc.edu", "IlluminaHiSeq_RNASeq", "*.gene.quantification.txt"));
    }

    @Test
    public void testGetSourceFileTypeRNASeqInvalid() {

        final String pattern = "*.exon_quantification.txt";//This is a RNASeq V2 pattern
        try {
            loader.getSourceFileType("bcgsc.ca", "IlluminaGA_RNASeq", pattern);
            fail("LoaderException was not thrown.");

        } catch (final LoaderException e) {
            Assert.assertEquals("Unknown source file type for file pattern " + pattern, e.getMessage());
        }
    }

    @Test
    public void testGetSourceFileTypeRNASeqV2Valid() throws LoaderException {

        assertEquals("expression_exon", loader.getSourceFileType("bcgsc.ca", "IlluminaGA_RNASeqV2", "*.exon_quantification.txt"));
        assertEquals("expression_junction", loader.getSourceFileType("bcgsc.ca", "IlluminaGA_RNASeqV2", "*.junction_quantification.txt"));
        assertEquals("expression_rsem_gene", loader.getSourceFileType("bcgsc.ca", "IlluminaGA_RNASeqV2", "*.rsem.genes.results"));
        assertEquals("expression_rsem_gene_normalized", loader.getSourceFileType("bcgsc.ca", "IlluminaGA_RNASeqV2", "*.rsem.genes.normalized_results"));
        assertEquals("expression_rsem_isoforms", loader.getSourceFileType("bcgsc.ca", "IlluminaGA_RNASeqV2", "*.rsem.isoforms.results"));
        assertEquals("expression_rsem_isoforms_normalized", loader.getSourceFileType("bcgsc.ca", "IlluminaGA_RNASeqV2", "*.rsem.isoforms.normalized_results"));

        assertEquals("expression_exon", loader.getSourceFileType("bcgsc.ca", "IlluminaHiSeq_RNASeqV2", "*.exon_quantification.txt"));
        assertEquals("expression_junction", loader.getSourceFileType("bcgsc.ca", "IlluminaHiSeq_RNASeqV2", "*.junction_quantification.txt"));
        assertEquals("expression_rsem_gene", loader.getSourceFileType("bcgsc.ca", "IlluminaHiSeq_RNASeqV2", "*.rsem.genes.results"));
        assertEquals("expression_rsem_gene_normalized", loader.getSourceFileType("bcgsc.ca", "IlluminaHiSeq_RNASeqV2", "*.rsem.genes.normalized_results"));
        assertEquals("expression_rsem_isoforms", loader.getSourceFileType("bcgsc.ca", "IlluminaHiSeq_RNASeqV2", "*.rsem.isoforms.results"));
        assertEquals("expression_rsem_isoforms_normalized", loader.getSourceFileType("bcgsc.ca", "IlluminaHiSeq_RNASeqV2", "*.rsem.isoforms.normalized_results"));

        assertEquals("expression_exon", loader.getSourceFileType("unc.edu", "IlluminaGA_RNASeqV2", "*.exon_quantification.txt"));
        assertEquals("expression_junction", loader.getSourceFileType("unc.edu", "IlluminaGA_RNASeqV2", "*.junction_quantification.txt"));
        assertEquals("expression_rsem_gene", loader.getSourceFileType("unc.edu", "IlluminaGA_RNASeqV2", "*.rsem.genes.results"));
        assertEquals("expression_rsem_gene_normalized", loader.getSourceFileType("unc.edu", "IlluminaGA_RNASeqV2", "*.rsem.genes.normalized_results"));
        assertEquals("expression_rsem_isoforms", loader.getSourceFileType("unc.edu", "IlluminaGA_RNASeqV2", "*.rsem.isoforms.results"));
        assertEquals("expression_rsem_isoforms_normalized", loader.getSourceFileType("unc.edu", "IlluminaGA_RNASeqV2", "*.rsem.isoforms.normalized_results"));

        assertEquals("expression_exon", loader.getSourceFileType("unc.edu", "IlluminaHiSeq_RNASeqV2", "*.exon_quantification.txt"));
        assertEquals("expression_junction", loader.getSourceFileType("unc.edu", "IlluminaHiSeq_RNASeqV2", "*.junction_quantification.txt"));
        assertEquals("expression_rsem_gene", loader.getSourceFileType("unc.edu", "IlluminaHiSeq_RNASeqV2", "*.rsem.genes.results"));
        assertEquals("expression_rsem_gene_normalized", loader.getSourceFileType("unc.edu", "IlluminaHiSeq_RNASeqV2", "*.rsem.genes.normalized_results"));
        assertEquals("expression_rsem_isoforms", loader.getSourceFileType("unc.edu", "IlluminaHiSeq_RNASeqV2", "*.rsem.isoforms.results"));
        assertEquals("expression_rsem_isoforms_normalized", loader.getSourceFileType("unc.edu", "IlluminaHiSeq_RNASeqV2", "*.rsem.isoforms.normalized_results"));
    }

    @Test
    public void testGetSourceFileTypeRNASeqV2Invalid() {

        final String pattern = "*.exon.quantification.txt";//This is a RNASeq pattern (not V2)
        try {
            loader.getSourceFileType("bcgsc.ca", "IlluminaGA_RNASeqV2", pattern);
            fail("LoaderException was not thrown.");

        } catch (final LoaderException e) {
            Assert.assertEquals("Unknown source file type for file pattern " + pattern, e.getMessage());
        }
    }

    @Test
    public void testLoadMDA_RPPA_CoreData() throws LoaderException {
        setUpMDA_RPPA_Core();
        final String sdrfLocation = MDA_RPPA_CORE_MAGE_TAB_ARCHIVE_LOCATION+ ".tar.gz";

        final List<FileInfo> archiveFile = new ArrayList<FileInfo>();
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName("mdanderson.org_UCEC.MDA_RPPA_Core.protein_expression.Level_3.control.txt");
        fileInfo.setId(100l);
        archiveFile.add(fileInfo);

        fileInfo = new FileInfo();
        fileInfo.setFileName("mdanderson.org_UCEC.MDA_RPPA_Core.protein_expression.Level_3.uuid.txt");
        fileInfo.setId(101l);
        archiveFile.add(fileInfo);

        final String latestBarcode = "TCGA-AG-A032-01A-01D-A077-02";

        context.checking(new Expectations() {{
            one(mockArchiveQueries).getArchiveIdByName("mdanderson.org_UCEC.MDA_RPPA_Core.Level_3.1.0.0.tar.gz");
            will(returnValue(123l));
            one(mockArchiveQueries).getArchive(123l);
            will(returnValue(archive));
            one(mockArchiveQueries).getFilesForArchive(123l);
            will(returnValue(archiveFile));
            allowing(mockArchiveQueries).getSdrfDeployLocation("mdanderson.org", "MDA_RPPA_Core", "UCEC");
            will(returnValue(sdrfLocation));
            one(mockArchiveQueries).getCenterByDomainNameAndPlatformName("mdanderson.org", "MDA_RPPA_Core");
            will(returnValue(archive.getTheCenter()));
            one(mockLthreeQueries).getExperimentId("mdanderson.org_UCEC.MDA_RPPA_Core", 1, 2);
            will(returnValue(100));
            one(mockLthreeQueries).createDataSet(2, 100, 1, "archiveLongName/*protein_expression*.txt", "protein_expression", "PUBLIC", 0, 3, 111L);
            will(returnValue(100));
            one(mockLthreeQueries).createDataSetFile(100, "mdanderson.org_UCEC.MDA_RPPA_Core.protein_expression.Level_3.control.txt", 100L);
            one(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("foouuid");
            will(returnValue(false));
            allowing(mockLthreeQueries).updateDataSetFile(100, "mdanderson.org_UCEC.MDA_RPPA_Core.protein_expression.Level_3.control.txt", 100L);
            allowing(mockLthreeQueries).updateDataSet(100);
            allowing(mockLthreeQueries).updateArchiveLoadedDate(123l);
            allowing(mockArchiveQueries).updateArchiveInfo(123l);



            one(mockLthreeQueries).createDataSetFile(100, "mdanderson.org_UCEC.MDA_RPPA_Core.protein_expression.Level_3.uuid.txt", 101L);
            allowing(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("fe469d1c-3900-4649-a36e-562f8047f8e0");
            will(returnValue(true));
            exactly(2).of(mockUuiddao).getLatestBarcodeForUUID("fe469d1c-3900-4649-a36e-562f8047f8e0");
            will(returnValue(latestBarcode));

            allowing(mockLthreeQueries).getHybrefDataSetId(123, 100);
            will(returnValue(333));

            allowing(mockLthreeQueries).getHybRefId(with(any(String.class)));
            will(returnValue(123));
            allowing(mockLthreeQueries).addProteinExpValue(with(checkMDA_RPPA_CoreData()));
            allowing(mockLthreeQueries).updateDataSetFile(100, "mdanderson.org_UCEC.MDA_RPPA_Core.protein_expression.Level_3.uuid.txt", 101L);
            allowing(mockLthreeQueries).updateDataSet(100);
            allowing(mockLthreeQueries).updateArchiveLoadedDate(123l);
            allowing(mockArchiveQueries).updateArchiveInfo(123l);


        }});
        loader.loadArchiveByName("mdanderson.org_UCEC.MDA_RPPA_Core.Level_3.1.0.0.tar.gz");
    }

    @Test
    public void testGetPatternsForCenterPlatform() {
        final CenterPlatformPattern broad1 = new CenterPlatformPattern("broad", "snp6", Arrays.asList("pattern1", "pattern2"));
        final CenterPlatformPattern broad2 = new CenterPlatformPattern("broad", "snp6", Arrays.asList("pattern3", "pattern1"));
        final CenterPlatformPattern other = new CenterPlatformPattern("hi", "snp6", Arrays.asList("apples"));

        loader.setPatterns(Arrays.asList(broad1, other, broad2));

        List<String> patterns = loader.getPatternsForCenterPlatform("broad", "snp6");
        assertEquals(3, patterns.size());
        assertTrue(patterns.contains("pattern1"));
        assertTrue(patterns.contains("pattern2"));
        assertTrue(patterns.contains("pattern3"));
    }

    @Test
    public void testBroadSnp6WithUuidExtractNames() throws LoaderException {
        setUpNewSnp6();

        final List<FileInfo> level3Files = new ArrayList<FileInfo>();
        final FileInfo file1 = new FileInfo();
        file1.setFileName("WHIRR_p_TCGA_168_169_170_redo_N_GenomeWideSNP_6_A07_845096.hg18.seg.txt");
        file1.setId(1L);

        final FileInfo file2 = new FileInfo();
        file2.setFileName("WHIRR_p_TCGA_168_169_170_redo_N_GenomeWideSNP_6_A07_845096.hg19.seg.txt");
        file2.setId(2L);

        final FileInfo file3 = new FileInfo();
        file3.setFileName("WHIRR_p_TCGA_168_169_170_redo_N_GenomeWideSNP_6_A07_845096.nocnv_hg18.seg.txt");
        file3.setId(3L);

        final FileInfo file4 = new FileInfo();
        file4.setFileName("WHIRR_p_TCGA_168_169_170_redo_N_GenomeWideSNP_6_A07_845096.nocnv_hg19.seg.txt");
        file4.setId(4L);

        level3Files.add(file1);
        level3Files.add(file2);
        level3Files.add(file3);
        level3Files.add(file4);

        context.checking(new Expectations() {{
            one(mockArchiveQueries).getArchiveIdByName("broad.mit.edu_BLCA.Genome_Wide_SNP_6.Level_3.1.0.0");
            will(returnValue(111L));

            one(mockArchiveQueries).getArchive(111L);
            will(returnValue(archive));

            one(mockArchiveQueries).getFilesForArchive(111L);
            will(returnValue(level3Files));

            one(mockArchiveQueries).getSdrfDeployLocation(archive.getDomainName(), archive.getPlatform(), archive.getTumorType());
            will(returnValue(BROAD_SNP_6_MAGE_TAB_LOCATION + ".tar.gz"));

            one(mockArchiveQueries).getCenterByDomainNameAndPlatformName(archive.getDomainName(), archive.getPlatform());
            will(returnValue(archive.getTheCenter()));

            one(mockLthreeQueries).getExperimentId("broad.mit.edu_BLCA.Genome_Wide_SNP_6", 1, 0);
            will(returnValue(42));

            one(mockLthreeQueries).createDataSet(archive.getTheCenter().getCenterId(), 42, archive.getThePlatform().getPlatformId(),
                    "broad.mit.edu_BLCA.Genome_Wide_SNP_6.Level_3.1.0.0/*.hg18.seg.txt", "snp_analysis.hg18.seg", "PUBLIC", 0, 3, 111L);
            will(returnValue(18));

            one(mockLthreeQueries).createDataSet(archive.getTheCenter().getCenterId(), 42, archive.getThePlatform().getPlatformId(),
                    "broad.mit.edu_BLCA.Genome_Wide_SNP_6.Level_3.1.0.0/*.hg19.seg.txt", "snp_analysis.hg19.seg", "PUBLIC", 0, 3, 111L);
            will(returnValue(19));

            one(mockLthreeQueries).createDataSet(archive.getTheCenter().getCenterId(), 42, archive.getThePlatform().getPlatformId(),
                    "broad.mit.edu_BLCA.Genome_Wide_SNP_6.Level_3.1.0.0/*.nocnv_hg18.seg.txt", "snp_analysis.nocnv_hg18.seg", "PUBLIC", 0, 3, 111L);
            will(returnValue(20));

            one(mockLthreeQueries).createDataSet(archive.getTheCenter().getCenterId(), 42, archive.getThePlatform().getPlatformId(),
                    "broad.mit.edu_BLCA.Genome_Wide_SNP_6.Level_3.1.0.0/*.nocnv_hg19.seg.txt", "snp_analysis.nocnv_hg19.seg", "PUBLIC", 0, 3, 111L);
            will(returnValue(21));

            one(mockLthreeQueries).createDataSetFile(18, file1.getFileName(), file1.getId());
            one(mockLthreeQueries).createDataSetFile(19, file2.getFileName(), file2.getId());
            one(mockLthreeQueries).createDataSetFile(20, file3.getFileName(), file3.getId());
            one(mockLthreeQueries).createDataSetFile(21, file4.getFileName(), file4.getId());

            exactly(4).of(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("822fdeac-85dd-47dd-a991-49f6b5dbe188");
            will(returnValue(true));

            exactly(4).of(mockUuiddao).getLatestBarcodeForUUID("822fdeac-85dd-47dd-a991-49f6b5dbe188");
            will(returnValue("TCGA-BARCODE-HI-THERE"));

            exactly(4).of(mockCommonBarcodeAndUUIDValidator).validateUUIDFormat("TCGA-BARCODE-HI-THERE");
            will(returnValue(false));

            exactly(4).of(mockLthreeQueries).getHybRefId("TCGA-BARCODE-HI-THERE");
            will(returnValue(null));

            exactly(4).of(mockArchiveQueries).getUUIDforBarcode("TCGA-BARCODE-HI-THERE");
            will(returnValue("822fdeac-85dd-47dd-a991-49f6b5dbe188"));

            exactly(4).of(mockLthreeQueries).insertHybRef("TCGA-BARCODE-HI-THERE", "TCGA-BARCODE-HI-TH", "822fdeac-85dd-47dd-a991-49f6b5dbe188");
            will(returnValue(123));

            one(mockLthreeQueries).getHybrefDataSetId(123, 18);
            will(returnValue(180));

            one(mockLthreeQueries).getHybrefDataSetId(123, 19);
            will(returnValue(190));

            one(mockLthreeQueries).getHybrefDataSetId(123, 20);
            will(returnValue(200));

            one(mockLthreeQueries).getHybrefDataSetId(123, 21);
            will(returnValue(210));

            exactly(4).of(mockLthreeQueries).addCNAValue(with(any(List.class)));

            one(mockLthreeQueries).updateDataSetFile(18, file1.getFileName(), file1.getId());
            one(mockLthreeQueries).updateDataSetFile(19, file2.getFileName(), file2.getId());
            one(mockLthreeQueries).updateDataSetFile(20, file3.getFileName(), file3.getId());
            one(mockLthreeQueries).updateDataSetFile(21, file4.getFileName(), file4.getId());

            one(mockLthreeQueries).updateDataSet(18);
            one(mockLthreeQueries).updateDataSet(19);
            one(mockLthreeQueries).updateDataSet(20);
            one(mockLthreeQueries).updateDataSet(21);

            one(mockLthreeQueries).updateArchiveLoadedDate(111L);
            one(mockArchiveQueries).updateArchiveInfo(111L);
        }});

        loader.loadArchiveByName("broad.mit.edu_BLCA.Genome_Wide_SNP_6.Level_3.1.0.0");
    }


    /**
     * Return a Center for a CGCC archive
     *
     * @param centerId the center Id
     * @return a Center for a CGCC archive
     */
    private Center getCenterForCGCCArchive(final Integer centerId) {

        final Center result = new Center();
        result.setCenterId(centerId);
        result.setCenterDisplayName("Center Display Name");
        result.setCenterName("Center Name");
        result.setCenterType("CGCC");
        result.setShortName("CENTER");

        return result;
    }

    /**
     * Return a list of file for the test miRNASeq archive
     *
     * @param miRnaFileId     miRNA file Id
     * @param miRnaFilename   miRNA filename
     * @param isoFormFileId   isoform file Id
     * @param isoformFilename isoform filename
     * @return a list of file for the test miRNASeq archive
     */
    private List<FileInfo> getFilesForMiRNASeqArchive(final Long miRnaFileId,
                                                      final String miRnaFilename,
                                                      final Long isoFormFileId,
                                                      final String isoformFilename) {

        final List<FileInfo> result = new ArrayList<FileInfo>();

        final FileInfo miRnaFileInfo = new FileInfo();
        miRnaFileInfo.setId(miRnaFileId);
        miRnaFileInfo.setFileName(miRnaFilename);
        result.add(miRnaFileInfo);

        final FileInfo isoformFileInfo = new FileInfo();
        isoformFileInfo.setId(isoFormFileId);
        isoformFileInfo.setFileName(isoformFilename);
        result.add(isoformFileInfo);

        return result;
    }

    /**
     * Return a list of file for the test RNASeq archive
     *
     * @param exonFileId       exon file Id
     * @param exonFilename     exon filename
     * @param geneFileId       gene file Id
     * @param geneFilename     gene filename
     * @param junctionFileId   junction file Id
     * @param junctionFilename junction filename
     * @return
     */
    private List<FileInfo> getFilesForRNASeqArchive(final Long exonFileId,
                                                    final String exonFilename,
                                                    final Long geneFileId,
                                                    final String geneFilename,
                                                    final Long junctionFileId,
                                                    final String junctionFilename) {

        final List<FileInfo> result = new ArrayList<FileInfo>();

        final FileInfo exonFileInfo = new FileInfo();
        exonFileInfo.setId(exonFileId);
        exonFileInfo.setFileName(exonFilename);
        result.add(exonFileInfo);

        final FileInfo geneFileInfo = new FileInfo();
        geneFileInfo.setId(geneFileId);
        geneFileInfo.setFileName(geneFilename);
        result.add(geneFileInfo);

        final FileInfo junctionFileInfo = new FileInfo();
        junctionFileInfo.setId(junctionFileId);
        junctionFileInfo.setFileName(junctionFilename);
        result.add(junctionFileInfo);

        return result;
    }

    /**
     * Setup for CGCC archive
     *
     * @param archiveId           the archive Id
     * @param archiveName         the archive name
     * @param archiveFileName     the archive filename
     * @param archiveSeriaIndex   the archive serial index
     * @param archiveRevision     the archive revision
     * @param archiveFilePatterns the archive file patterns
     * @param centerId            the center Id
     * @param centerName          the center name
     * @param platformId          the platform Id
     * @param platformName        the platform name
     * @param tumorAbbreviation   the tumor abbreviation
     */
    private void setupForCGCCArchive(final Long archiveId,
                                     final String archiveName,
                                     final String archiveFileName,
                                     final Integer archiveSeriaIndex,
                                     final Integer archiveRevision,
                                     final List<String> archiveFilePatterns,
                                     final Integer centerId,
                                     final String centerName,
                                     final Integer platformId,
                                     final String platformName,
                                     final String tumorAbbreviation) {

        final CenterPlatformPattern centerPlatformPattern = new CenterPlatformPattern();
        centerPlatformPattern.setCenter(centerName);
        centerPlatformPattern.setPlatform(platformName);
        centerPlatformPattern.setPattern(archiveFilePatterns);

        final List<CenterPlatformPattern> patternsList = new ArrayList<CenterPlatformPattern>();
        patternsList.add(centerPlatformPattern);

        loader.setPatterns(patternsList);

        final Platform platform = new Platform();
        platform.setPlatformName(platformName);
        platform.setPlatformId(platformId);

        final Tumor tumor = new Tumor();
        tumor.setTumorName(tumorAbbreviation);

        final Center center = new Center();
        center.setCenterId(centerId);
        center.setCenterName(centerName);

        archive = new Archive(archiveFileName);
        archive.setDeployLocation(archiveFileName);
        archive.setThePlatform(platform);
        archive.setDomainName(centerName);
        archive.setTumorType(tumorAbbreviation);
        archive.setTheCenter(center);
        archive.setRealName(archiveName);
        archive.setId(archiveId);
        archive.setExperimentType(Experiment.TYPE_CGCC);
        archive.setSerialIndex(archiveSeriaIndex.toString());
        archive.setRevision(archiveRevision.toString());
        archive.setDeployStatus(Archive.STATUS_AVAILABLE);
        archive.setTheTumor(tumor);
    }

    private Matcher<List<Object[]>> checkFirmaList(final List<Object[]> elementsList) {
        return new TypeSafeMatcher<List<Object[]>>() {

            @Override
            public boolean matchesSafely(List<Object[]> elementsList) {
                boolean returnValue = false;
                Object[] record1 = new Object[]{new Integer("123"), new Integer("123"), "2315165", "-0.308423869794525"};
                Object[] record2 = new Object[]{new Integer("123"), new Integer("123"), "2315165", "0.231165064628314"};
                Object[] record3 = new Object[]{new Integer("123"), new Integer("123"), "2315425", "0.205345373758810"};
                Object[] record4 = new Object[]{new Integer("123"), new Integer("123"), "2315425", "-0.258037891403341"};
                Object[] record5 = new Object[]{new Integer("123"), new Integer("123"), "2645133", "NA"};
                Object[] record6 = new Object[]{new Integer("123"), new Integer("123"), "2645133", "NA"};
                if (
                        (ArrayUtils.isEquals(record1, elementsList.get(0))) &&
                                (ArrayUtils.isEquals(record2, elementsList.get(1))) &&
                                (ArrayUtils.isEquals(record3, elementsList.get(2))) &&
                                (ArrayUtils.isEquals(record4, elementsList.get(3))) &&
                                (ArrayUtils.isEquals(record5, elementsList.get(4))) &&
                                (ArrayUtils.isEquals(record6, elementsList.get(5)))) {
                    returnValue = true;
                }
                return returnValue;

            }

            public void describeTo(final Description description) {
                description.appendText("Valid match");
            }
        };
    }

    private Matcher<List<Object[]>> checkGenericList(final List<Object[]> elementsList) {
        return new TypeSafeMatcher<List<Object[]>>() {

            @Override
            public boolean matchesSafely(List<Object[]> elementsList) {
                boolean returnValue = false;
                Object[] record1 = new Object[]{new Integer("123"), new Integer("123"), "ELMO2", "-0.519583333333333"};
                Object[] record2 = new Object[]{new Integer("123"), new Integer("123"), "CREB3L1", "-1.13275"};
                Object[] record3 = new Object[]{new Integer("123"), new Integer("123"), "RPS11", "0.256625"};
                if (ArrayUtils.isEquals(record1, elementsList.get(0)) &&
                        (ArrayUtils.isEquals(record2, elementsList.get(1))) &&
                        (ArrayUtils.isEquals(record3, elementsList.get(2)))) {
                    returnValue = true;
                }
                return returnValue;

            }

            public void describeTo(final Description description) {
                description.appendText("Valid match");
            }
        };
    }

    private Matcher<List<Object[]>> checkTsvList(final List<Object[]> elementsList) {
        return new TypeSafeMatcher<List<Object[]>>() {

            @Override
            public boolean matchesSafely(List<Object[]> elementsList) {
                boolean returnValue = false;
                Object[] record1 = new Object[]{new Integer("123"), new Integer("123"), "1", "554267", "639580", null, "1.2628"};
                Object[] record2 = new Object[]{new Integer("123"), new Integer("123"), "X", "128793197", "154582413", null, "0.0153"};
                Object[] record3 = new Object[]{new Integer("123"), new Integer("123"), "Y", "2710449", "8591071", null, "-0.2889"};
                if (ArrayUtils.isEquals(record1, elementsList.get(0)) &&
                        (ArrayUtils.isEquals(record2, elementsList.get(1))) &&
                        (ArrayUtils.isEquals(record3, elementsList.get(2)))) {
                    returnValue = true;
                }
                return returnValue;

            }

            public void describeTo(final Description description) {
                description.appendText("Valid match");
            }
        };
    }

    private Matcher<List<Object[]>> checkCnaList(final List<Object[]> elementsList) {
        return new TypeSafeMatcher<List<Object[]>>() {

            @Override
            public boolean matchesSafely(List<Object[]> elementsList) {
                boolean returnValue = false;
                Object[] record1 = new Object[]{new Integer("123"), new Integer("123"), "1", "788822", "12186942", null, "0.4435"};
                Object[] record2 = new Object[]{new Integer("123"), new Integer("123"), "1", "12189852", "12190974", null, "0.1875"};
                Object[] record3 = new Object[]{new Integer("123"), new Integer("123"), "1", "12191224", "16877437", null, "0.4429"};
                if (ArrayUtils.isEquals(record1, elementsList.get(0)) &&
                        (ArrayUtils.isEquals(record2, elementsList.get(1))) &&
                        (ArrayUtils.isEquals(record3, elementsList.get(2)))) {
                    returnValue = true;
                }
                return returnValue;

            }

            public void describeTo(final Description description) {
                description.appendText("Valid match");
            }
        };
    }

    private Matcher<List<Object[]>> checkBetaList(final List<Object[]> elementsList) {
        return new TypeSafeMatcher<List<Object[]>>() {

            @Override
            public boolean matchesSafely(List<Object[]> elementsList) {
                boolean returnValue = false;
                Object[] record1 = new Object[]{"cg00000292", new Integer("123"), new Integer("123"), "0.67340591", "ATP2A1", "16", new Integer(28797601)};
                Object[] record2 = new Object[]{"cg00002426", new Integer("123"), new Integer("123"), "0.224362904", "SLMAP", "3", new Integer(57718583)};
                Object[] record3 = new Object[]{"cg04631202", new Integer("123"), new Integer("123"), "NA", "MAN1C1", "1", new Integer(25815047)};
                if (ArrayUtils.isEquals(record1, elementsList.get(0)) &&
                        (ArrayUtils.isEquals(record2, elementsList.get(1))) &&
                        (ArrayUtils.isEquals(record3, elementsList.get(2)))) {
                    returnValue = true;
                }
                return returnValue;

            }

            public void describeTo(final Description description) {
                description.appendText("Valid match");
            }
        };
    }

    /**
     * Return a Matcher for the given list of Objects from miRNASeq file
     *
     * @param elementsList                  the list containing the Objects to verify
     * @param expectedMiRnaId               the expected miRNA Id
     * @param expectedReadCount             the expected read count
     * @param expectedReadsPerMillionMiRnaMapped
     *                                      the expected reads per million RNA mapped
     * @param expectedCrossMapped           the expected croos mapped
     * @param expectedIsoformCoords         the expected isoform coords
     * @param expectedMiRnaRegionAnnotation the expected miRNA Region Annotation
     * @param expectedMiRnaRegionAccession  the expected miRNA Region Accession
     * @param expectedDataSetId             the expected data set Id
     * @param expectedHybridizationRefId    the expected hybridization ref Id
     * @return the Matcher
     */
    private Matcher<List<Object[]>> checkMiRnaSeq(final List<Object[]> elementsList,
                                                  final String expectedMiRnaId,
                                                  final String expectedReadCount,
                                                  final String expectedReadsPerMillionMiRnaMapped,
                                                  final String expectedCrossMapped,
                                                  final String expectedIsoformCoords,
                                                  final String expectedMiRnaRegionAnnotation,
                                                  final String expectedMiRnaRegionAccession,
                                                  final Integer expectedDataSetId,
                                                  final Integer expectedHybridizationRefId) {

        return new TypeSafeMatcher<List<Object[]>>() {

            @Override
            public boolean matchesSafely(List<Object[]> elementsList) {

                boolean result = false;

                final Object[] firstRecord = elementsList.get(0);

                if (firstRecord != null) {

                    final String miRnaId = (String) firstRecord[0];
                    final String readCount = (String) firstRecord[1];
                    final String readsPerMillionMiRnaMapped = (String) firstRecord[2];
                    final String crossMapped = (String) firstRecord[3];
                    final String isoformCoords = (String) firstRecord[4];
                    final String miRnaRegionAnnotation = (String) firstRecord[5];
                    final String miRnaRegionAccession = (String) firstRecord[6];
                    final Integer dataSetId = (Integer) firstRecord[7];
                    final Integer hybridizationRefId = (Integer) firstRecord[8];

                    if (miRnaId.equals(expectedMiRnaId)
                            && readCount.equals(expectedReadCount)
                            && readsPerMillionMiRnaMapped.equals(expectedReadsPerMillionMiRnaMapped)
                            && crossMapped.equals(expectedCrossMapped)
                            && isoformCoords.equals(expectedIsoformCoords)
                            && miRnaRegionAnnotation.equals(expectedMiRnaRegionAnnotation)
                            && miRnaRegionAccession.equals(expectedMiRnaRegionAccession)
                            && dataSetId.equals(expectedDataSetId)
                            && hybridizationRefId.equals(expectedHybridizationRefId)) {

                        result = true;
                    }
                }

                return result;

            }

            public void describeTo(final Description description) {
                description.appendText("Valid match");
            }
        };
    }

    /**
     * Return a Matcher for the given list of Objects from RNASeq file
     *
     * @param elementsList                   the list containing the Objects to verify
     * @param expectedExon the expected exon value
     * @param expectedRawCounts the expected raw counts value
     * @param expectedMedianLengthNormalized the expected median length normalized value
     * @param expectedRpkm the expected RPKM value
     * @param expectedNormalizedCounts the expected normalized counts value
     * @param expectedScaledEstimate the expected scaled estimate value
     * @param expectedTranscriptId the expected transcript Id value
     * @param expectedDataSetId              the expected data set Id value
     * @param expectedHybridizationRefId     the expected hybridization ref Id value
     * @return a Matcher for the given list of Objects from RNASeq file
     */
    private Matcher<List<Object[]>> checkRnaSeq(final List<Object[]> elementsList,
                                                final String expectedExon,
                                                final String expectedRawCounts,
                                                final String expectedMedianLengthNormalized,
                                                final String expectedRpkm,
                                                final String expectedNormalizedCounts,
                                                final String expectedScaledEstimate,
                                                final String expectedTranscriptId,
                                                final Integer expectedDataSetId,
                                                final Integer expectedHybridizationRefId) {

        return new TypeSafeMatcher<List<Object[]>>() {

            @Override
            public boolean matchesSafely(List<Object[]> elementsList) {

                boolean result = false;

                final Object[] firstRecord = elementsList.get(0);

                if (firstRecord != null) {

                    final String exon = (String) firstRecord[0];
                    final String rawCounts = (String) firstRecord[1];
                    final String medianLengthNormalized = (String) firstRecord[2];
                    final String rpkm = (String) firstRecord[3];
                    final String normalizedCounts = (String) firstRecord[4];;
                    final String scaledEstimate = (String) firstRecord[5];;
                    final String transcriptId = (String) firstRecord[6];;
                    final Integer dataSetId = (Integer) firstRecord[7];
                    final Integer hybridizationRefId = (Integer) firstRecord[8];

                    if (StringUtils.equals(exon, expectedExon)
                            && StringUtils.equals(rawCounts, expectedRawCounts)
                            && StringUtils.equals(medianLengthNormalized, expectedMedianLengthNormalized)
                            && StringUtils.equals(rpkm, expectedRpkm)
                            && StringUtils.equals(normalizedCounts, expectedNormalizedCounts)
                            && StringUtils.equals(scaledEstimate, expectedScaledEstimate)
                            && StringUtils.equals(transcriptId, expectedTranscriptId)
                            && dataSetId.equals(expectedDataSetId)
                            && hybridizationRefId.equals(expectedHybridizationRefId)) {

                        result = true;
                    }
                }

                return result;

            }

            public void describeTo(final Description description) {
                description.appendText("Valid match");
            }
        };
    }

    /**
     * @param inputEnd the string that must match the end of the input to check
     * @return a Matcher for verifying an input String
     */
    private Matcher<String> checkInput(final String inputEnd) {

        return new TypeSafeMatcher<String>() {

            @Override
            public boolean matchesSafely(final String s) {
                return s.endsWith(inputEnd);
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("Valid match");
            }
        };
    }

    private Matcher<List<Object[]>> checkIlluminaHiSeq_DNASeqCTsvList() {
        return new TypeSafeMatcher<List<Object[]>>() {

            @Override
            public boolean matchesSafely(List<Object[]> elementsList) {
                final List<Object[]> cnaValList = Arrays.asList(new Object[]{100, 123, "chr1", "1", "549000", null, "-0.078"},
                        new Object[]{100, 123, "chr1", "549001", "6433000", null, "-0.6173"},
                        new Object[]{100, 123, "chr1", "6433001", "6451000", null, "0.0395"});
                if (elementsList.size() == cnaValList.size()) {
                    for (int i = 0; i < elementsList.size(); i++) {
                        if (!ArrayUtils.isEquals(elementsList.get(i), (cnaValList.get(i)))) {
                            return false;
                        }
                    }
                    return true;
                }
                return false;

            }

            public void describeTo(final Description description) {
                description.appendText("Valid match");
            }
        };
    }

    private Matcher<List<Object[]>> checkMDA_RPPA_CoreData() {
          return new TypeSafeMatcher<List<Object[]>>() {

              @Override
              public boolean matchesSafely(List<Object[]> elementsList) {
                  final Object[] data = new Object[]{100, 123, "4E-BP1-R-V", "EIF4EBP1", new Double(-0.6254172315)};
                  final List<Object[]> proteinExpressionValList = new ArrayList<Object[]>();
                  proteinExpressionValList.add(data);
                  if (elementsList.size() == proteinExpressionValList.size()) {
                      for (int i = 0; i < elementsList.size(); i++) {
                          if (!ArrayUtils.isEquals(elementsList.get(i), (proteinExpressionValList.get(i)))) {
                              return false;
                          }
                      }
                      return true;
                  }
                  return false;

              }

              public void describeTo(final Description description) {
                  description.appendText("Valid match");
              }
          };
      }
}
