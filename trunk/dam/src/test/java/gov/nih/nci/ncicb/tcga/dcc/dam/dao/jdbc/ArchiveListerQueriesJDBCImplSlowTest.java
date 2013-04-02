package gov.nih.nci.ncicb.tcga.dcc.dam.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.ArchiveQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.CenterQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.PlatformQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.TumorQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.ArchiveListLink;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.ArchiveListerQueries;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * DBUnit test for ArchiveListerQueries JDBC implementation.
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ArchiveListerQueriesJDBCImplSlowTest extends DBUnitTestCase {

    private static final String PATH_TO_SAMPLES_DIR = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String DB_PROPERTIES_FILE = "dccCommon.unittest.properties";
    private static final String DATA_FILE = "portal" + File.separator + "dao" + File.separator + "ArchiveListerQueries_Data.xml";

    private ArchiveListerQueriesJDBCImpl archiveListerQueries;
    private ArchiveListerQueries.LinkMaker stubLinkMaker;
    private SimpleDateFormat simpleDateFormat;

    public ArchiveListerQueriesJDBCImplSlowTest() {
        super(PATH_TO_SAMPLES_DIR, DATA_FILE, DB_PROPERTIES_FILE);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        archiveListerQueries = new ArchiveListerQueriesJDBCImpl();
        archiveListerQueries.setDataSource(getDataSource());
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        final TumorQueriesJDBCImpl tumorQueries = new TumorQueriesJDBCImpl();
        tumorQueries.setDataSource(getDataSource());
        archiveListerQueries.setTumorQueries(tumorQueries);

        final CenterQueriesJDBCImpl centerQueries = new CenterQueriesJDBCImpl();
        centerQueries.setDataSource(getDataSource());
        archiveListerQueries.setCenterQueries(centerQueries);

        final PlatformQueriesJDBCImpl platformQueries = new PlatformQueriesJDBCImpl();
        platformQueries.setDataSource(getDataSource());
        archiveListerQueries.setPlatformQueries(platformQueries);

        final ArchiveQueriesJDBCImpl archiveQueries = new ArchiveQueriesJDBCImpl();
        archiveQueries.setDataSource(getDataSource());
        archiveListerQueries.setArchiveQueries(archiveQueries);

        stubLinkMaker = new ArchiveListerQueries.LinkMaker() {
            @Override
            public String makeLinkUrl(final String value, final boolean isCollection) {
                return "test?" + value;
            }

            @Override
            public ArchiveListLink makeCurrentPage(final String currentPageValue) {
                return null;
            }

            @Override
            public ArchiveListLink makeParentPage(final String parentPageValue) {
                return null;
            }
        };

    }

    public void testGetDiseaseLinks() throws ArchiveListerQueries.ArchiveListerException {
        final List<ArchiveListLink> diseaseLinks = archiveListerQueries.getDiseaseLinks(stubLinkMaker, "open");

        // public available archives for GBM and LAML
        assertEquals(2, diseaseLinks.size());

        assertEquals("GBM", diseaseLinks.get(0).getDisplayName());
        assertEquals("test?GBM", diseaseLinks.get(0).getUrl());
        assertEquals("2011-01-27 12:00:00.0", diseaseLinks.get(0).getDeployDate().toString());

        assertEquals("LAML", diseaseLinks.get(1).getDisplayName());
        assertEquals("test?LAML", diseaseLinks.get(1).getUrl());
        assertEquals("2011-01-20 12:00:00.0", diseaseLinks.get(1).getDeployDate().toString());
    }

    public void testGetDiseaseLinksInvalidAccessLevel() {
        try {
            archiveListerQueries.getDiseaseLinks(stubLinkMaker, "llama");
            fail("Exception was not thrown");
        } catch (ArchiveListerQueries.ArchiveListerException e) {
            assertTrue(e.getMessage().contains("llama is not a valid access level"));
        }

    }

    public void testGetCenterTypeLinksOpen() throws ArchiveListerQueries.ArchiveListerException {
        final List<ArchiveListLink> centerTypeLinks = archiveListerQueries.getCenterTypeLinks(stubLinkMaker, "open", "GBM");

        // public available GBM archives are CGCC
        assertEquals(1, centerTypeLinks.size());

        assertEquals("CGCC", centerTypeLinks.get(0).getDisplayName());
        assertEquals("test?CGCC", centerTypeLinks.get(0).getUrl());
        assertEquals("2011-01-27 12:00:00.0", centerTypeLinks.get(0).getDeployDate().toString());
    }

    public void testGetCenterTypeLinksControlled() throws ArchiveListerQueries.ArchiveListerException {
        final List<ArchiveListLink> centerTypeLinks = archiveListerQueries.getCenterTypeLinks(stubLinkMaker, "controlled", "GBM");

        // protected GBM archivs are BCR and CGCC
        assertEquals(2, centerTypeLinks.size());

        assertEquals("BCR", centerTypeLinks.get(0).getDisplayName());
        assertEquals("test?BCR", centerTypeLinks.get(0).getUrl());
        assertEquals("2011-01-18 12:00:00.0", centerTypeLinks.get(0).getDeployDate().toString());

        assertEquals("CGCC", centerTypeLinks.get(1).getDisplayName());
        assertEquals("test?CGCC", centerTypeLinks.get(1).getUrl());
        assertEquals("2011-03-24 12:00:00.0", centerTypeLinks.get(1).getDeployDate().toString());
    }


    public void testGetCenterTypeLinksNoResults() throws ArchiveListerQueries.ArchiveListerException {
        // valid disease, but no public archives for it
        final List<ArchiveListLink> links = archiveListerQueries.getCenterTypeLinks(stubLinkMaker, "open", "BRCA");
        assertEquals(0, links.size());
    }

    public void testGetCenterTypeLinksInvalidDisease() {
        try {
            archiveListerQueries.getCenterTypeLinks(stubLinkMaker, "open", "influenza");
            fail("exception should have been thrown");
        } catch (ArchiveListerQueries.ArchiveListerException e) {
            assertEquals("'influenza' is not a valid disease abbreviation", e.getMessage());
        }

    }

    public void testGetCenterLinks() throws Exception {
        List<ArchiveListLink> centerLinks = archiveListerQueries.getCenterLinks(stubLinkMaker, "controlled", "BRCA", "BCR");
        assertEquals(1, centerLinks.size());
        assertEquals("bcr_center.org", centerLinks
                .get(0).getDisplayName());
        assertEquals("test?bcr_center.org", centerLinks.get(0).getUrl());
        assertEquals("2011-01-17 12:00:00.0", centerLinks.get(0).getDeployDate().toString());

        centerLinks = archiveListerQueries.getCenterLinks(stubLinkMaker, "open", "GBM", "CGCC");
        assertEquals(1, centerLinks.size());
        assertEquals("cgcc_center.org", centerLinks.get(0).getDisplayName());
        assertEquals("test?cgcc_center.org", centerLinks.get(0).getUrl());
        assertEquals("2011-01-27 12:00:00.0", centerLinks.get(0).getDeployDate().toString());
    }

    public void testGetCenterLinksBadCenterType() {
        try {
            archiveListerQueries.getCenterLinks(stubLinkMaker, "open", "GBM", "walrus");
            fail("exception should have been thrown");
        } catch (ArchiveListerQueries.ArchiveListerException e) {
            assertEquals("'walrus' is not a valid center type code", e.getMessage());
        }
    }

    public void testGetPlatformLinks() throws Exception {
        List<ArchiveListLink> platformLinks = archiveListerQueries.getPlatformLinks(stubLinkMaker, "controlled", "GBM", "BCR", "bcr_center.org");
        assertEquals(1, platformLinks.size());
        assertEquals("bio", platformLinks.get(0).getDisplayName());
        assertEquals("test?bio", platformLinks.get(0).getUrl());
        assertEquals("2011-01-18 12:00:00.0", platformLinks.get(0).getDeployDate().toString());

        platformLinks = archiveListerQueries.getPlatformLinks(stubLinkMaker, "open", "GBM", "CGCC", "cgcc_center.org");
        assertEquals(1, platformLinks.size());
        assertEquals("Genome_Wide_SNP6", platformLinks.get(0).getDisplayName());
        assertEquals("2011-01-27 12:00:00.0", platformLinks.get(0).getDeployDate().toString());
    }

    public void testGetPlatformLinksBadCenter() {
        try {
            archiveListerQueries.getPlatformLinks(stubLinkMaker, "open", "BRCA", "BCR", "cgcc_center.org");
            fail("exception should have been thrown");
        } catch (ArchiveListerQueries.ArchiveListerException e) {
            assertEquals("'cgcc_center.org' is not a valid BCR center domain name", e.getMessage());
        }
    }

    public void testGetArchiveLinks() throws ArchiveListerQueries.ArchiveListerException {
        final List<ArchiveListLink> archiveLinks = archiveListerQueries.getArchiveLinks(stubLinkMaker, "controlled", "GBM", "CGCC", "cgcc_center.org", "Genome_Wide_SNP6");

        // 2 controlled SNP6 GBM archives -- expect link for browsing files, link for .tar.gz download, and .md5 download
        assertEquals(6, archiveLinks.size());
        assertEquals("cgcc_center.org_GBM.Genome_Wide_SNP6.Level_1.1.0.0", archiveLinks.get(0).getDisplayName());
        assertEquals("test?cgcc_center.org_GBM.Genome_Wide_SNP6.Level_1.1.0.0", archiveLinks.get(0).getUrl());
        assertNull(archiveLinks.get(0).getFileSizeInBytes());
        assertEquals("2011-02-26 12:00:00.0", archiveLinks.get(1).getDeployDate().toString());

        assertEquals("cgcc_center.org_GBM.Genome_Wide_SNP6.Level_1.1.0.0.tar.gz", archiveLinks.get(1).getDisplayName());
        assertEquals("/path/to/level1/archive/cgcc_center.org_GBM.Genome_Wide_SNP6.Level_1.1.0.0.tar.gz", archiveLinks.get(1).getUrl());
        assertEquals(new Long(90000*1024), archiveLinks.get(1).getFileSizeInBytes());
        assertEquals("2011-02-26 12:00:00.0", archiveLinks.get(1).getDeployDate().toString());

        assertEquals("cgcc_center.org_GBM.Genome_Wide_SNP6.Level_1.1.0.0.tar.gz.md5", archiveLinks.get(2).getDisplayName());
        assertEquals("/path/to/level1/archive/cgcc_center.org_GBM.Genome_Wide_SNP6.Level_1.1.0.0.tar.gz.md5", archiveLinks.get(2).getUrl());
        assertEquals("2011-02-26 12:00:00.0", archiveLinks.get(2).getDeployDate().toString());
        assertNull(archiveLinks.get(2).getFileSizeInBytes());

        assertEquals("cgcc_center.org_GBM.Genome_Wide_SNP6.Level_2.1.0.0", archiveLinks.get(3).getDisplayName());
        assertEquals("test?cgcc_center.org_GBM.Genome_Wide_SNP6.Level_2.1.0.0", archiveLinks.get(3).getUrl());
        assertNull(archiveLinks.get(3).getFileSizeInBytes());
        assertEquals("2011-03-24 12:00:00.0", archiveLinks.get(4).getDeployDate().toString());

        assertEquals("cgcc_center.org_GBM.Genome_Wide_SNP6.Level_2.1.0.0.tar.gz", archiveLinks.get(4).getDisplayName());
        assertEquals("/path/to/level2/archive/cgcc_center.org_GBM.Genome_Wide_SNP6.Level_2.1.0.0.tar.gz", archiveLinks.get(4).getUrl());
        assertEquals("2011-03-24 12:00:00.0", archiveLinks.get(4).getDeployDate().toString());
        assertEquals(new Long(50000*1024), archiveLinks.get(4).getFileSizeInBytes());

        assertEquals("cgcc_center.org_GBM.Genome_Wide_SNP6.Level_2.1.0.0.tar.gz.md5", archiveLinks.get(5).getDisplayName());
        assertEquals("/path/to/level2/archive/cgcc_center.org_GBM.Genome_Wide_SNP6.Level_2.1.0.0.tar.gz.md5", archiveLinks.get(5).getUrl());
        assertNull(archiveLinks.get(5).getFileSizeInBytes());
        assertEquals("2011-03-24 12:00:00.0", archiveLinks.get(4).getDeployDate().toString());
    }

    public void testGetArchiveLinksMultiple() throws ArchiveListerQueries.ArchiveListerException {
        final List<ArchiveListLink> archiveLinks = archiveListerQueries.getArchiveLinks(stubLinkMaker, "controlled", "GBM", "CGCC", "cgcc_center.org", "Genome_Wide_SNP6");
        assertEquals(6, archiveLinks.size());
    }

    public void testGetArchiveLinksBadPlatform() {
        try {
            archiveListerQueries.getArchiveLinks(stubLinkMaker, "controlled", "GBM", "CGCC", "cgcc_center.org", "diving");
            fail("exception should have been thrown");
        } catch (ArchiveListerQueries.ArchiveListerException e) {
            assertEquals("'diving' is not a valid platform name", e.getMessage());
        }
    }

    public void testGetArchiveFileLinks() throws ArchiveListerQueries.ArchiveListerException {
        final List<ArchiveListLink> fileLinks = archiveListerQueries.getArchiveFileLinks("cgcc_center.org_GBM.Genome_Wide_SNP6.mage-tab.1.1.0");
        assertEquals(2, fileLinks.size());

        assertEquals("hello.sdrf.txt", fileLinks.get(0).getDisplayName());
        assertEquals("/location/of/file/hello.sdrf.txt", fileLinks.get(0).getUrl());
        assertEquals(new Long(12345), fileLinks.get(0).getFileSizeInBytes());
        assertEquals("2011-01-27 12:00:00.0", fileLinks.get(0).getDeployDate().toString());

        assertEquals("readme.txt", fileLinks.get(1).getDisplayName());
        assertEquals("/location/of/file/readme.txt", fileLinks.get(1).getUrl());
        assertEquals(new Long(111), fileLinks.get(1).getFileSizeInBytes());
        assertEquals("2011-01-27 12:00:00.0", fileLinks.get(1).getDeployDate().toString());
    }

    public void testGetArchiveFileLinksBadArchive() {
        try {
            archiveListerQueries.getArchiveFileLinks("no");
            fail("exception should have been thrown");
        } catch (ArchiveListerQueries.ArchiveListerException e) {
            assertEquals("'no' is not a valid archive name", e.getMessage());
        }
    }

    public void testGetCollectionLinksAccessLevel() throws ArchiveListerQueries.ArchiveListerException {
        final List<ArchiveListLink> collectionLinks = archiveListerQueries.getCollectionLinks(stubLinkMaker, "open", null, null, null, null);
        assertEquals(1, collectionLinks.size());
        assertEquals("DCC Notes", collectionLinks.get(0).getDisplayName());
        assertEquals("test?DCC Notes", collectionLinks.get(0).getUrl());
        assertEquals("2011-08-23 00:00:00.0", collectionLinks.get(0).getDeployDate().toString());
    }

    public void testGetCollectionLinksDiseaseLevel() throws ArchiveListerQueries.ArchiveListerException {
        final List<ArchiveListLink> collectionLinks = archiveListerQueries.getCollectionLinks(stubLinkMaker, "controlled", "BRCA", null, null, null);
        assertEquals(1, collectionLinks.size());
        assertEquals("brca_working_group_data", collectionLinks.get(0).getDisplayName());
        assertEquals("test?brca_working_group_data", collectionLinks.get(0).getUrl());
        assertEquals("2011-07-01 00:00:00.0", collectionLinks.get(0).getDeployDate().toString());
    }

    public void testGetCollectionLinksCenterTypeLevel() throws ArchiveListerQueries.ArchiveListerException {
        final List<ArchiveListLink> collectionLinks = archiveListerQueries.getCollectionLinks(stubLinkMaker, "controlled", "GBM", "BCR", null, null);
        assertEquals(1, collectionLinks.size());
        assertEquals("biotab", collectionLinks.get(0).getDisplayName());
        assertEquals("test?biotab", collectionLinks.get(0).getUrl());
        assertEquals("2011-09-23 00:00:00.0", collectionLinks.get(0).getDeployDate().toString());
    }

    public void testGetCollectionLinksCenterLevel() throws ArchiveListerQueries.ArchiveListerException {
        final List<ArchiveListLink> collectionLinks = archiveListerQueries.getCollectionLinks(stubLinkMaker, "open", "BRCA", "BCR", "bcr_center.org", null);
        assertEquals(1, collectionLinks.size());
        assertEquals("minbiotab", collectionLinks.get(0).getDisplayName());
        assertEquals("test?minbiotab", collectionLinks.get(0).getUrl());
        assertEquals("2010-01-01 00:00:00.0", collectionLinks.get(0).getDeployDate().toString());
    }

    public void testGetCollectionLinksPlatformLevel() throws ArchiveListerQueries.ArchiveListerException {
        final List<ArchiveListLink> collectionLinks = archiveListerQueries.getCollectionLinks(stubLinkMaker, "controlled", "GBM", "BCR", "bcr_center.org", "bio");
        assertEquals(1, collectionLinks.size());
        assertEquals("notes", collectionLinks.get(0).getDisplayName());
        assertEquals("test?notes", collectionLinks.get(0).getUrl());
        assertEquals("2011-08-10 00:00:00.0", collectionLinks.get(0).getDeployDate().toString());
    }

    public void testGetCollectionFileLinks() throws ArchiveListerQueries.ArchiveListerException {
        final List<ArchiveListLink> collectionFileLinks = archiveListerQueries.getCollectionFileLinks("biotab", "controlled", "GBM", "BCR", null, null);
        assertEquals(3, collectionFileLinks.size());
        assertEquals("gbm_patients_biotab.txt", collectionFileLinks.get(0).getDisplayName());
        assertEquals("/path/to/biotab/gbm_patients_biotab.txt", collectionFileLinks.get(0).getUrl());
        assertEquals("2011-09-23 00:00:00.0", collectionFileLinks.get(0).getDeployDate().toString());
        assertEquals(new Long(101), collectionFileLinks.get(0).getFileSizeInBytes());
        assertEquals("gbm_portions_biotab.txt", collectionFileLinks.get(1).getDisplayName());
        assertEquals("/path/to/biotab/gbm_portions_biotab.txt", collectionFileLinks.get(1).getUrl());
        assertEquals("2011-09-21 00:00:00.0", collectionFileLinks.get(1).getDeployDate().toString());
        assertEquals(new Long(306), collectionFileLinks.get(1).getFileSizeInBytes());
        assertEquals("gbm_samples_biotab.txt", collectionFileLinks.get(2).getDisplayName());
        assertEquals("/path/to/biotab/gbm_samples_biotab.txt", collectionFileLinks.get(2).getUrl());
        assertEquals("2011-09-22 00:00:00.0", collectionFileLinks.get(2).getDeployDate().toString());
        assertEquals(new Long(200), collectionFileLinks.get(2).getFileSizeInBytes());
    }

    public void testGetCollectionFileBadCollection() throws ArchiveListerQueries.ArchiveListerException {
        try {
            archiveListerQueries.getCollectionFileLinks("moose", "open", "BRCA", "BCR", "bcr_center.org", null);
            fail("Exception should have been thrown for invalid collection name");
        } catch (ArchiveListerQueries.ArchiveListerException e) {
            assertEquals("'moose' does not exist on the filesystem", e.getMessage());
        }
    }
}
