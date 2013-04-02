package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.ArchiveListInfo;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.ArchiveListLink;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.ArchiveListerQueries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Fast tests for ArchiveListerServiceImpl.  Uses JMock to mock DAO interface.
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class ArchiveListerServiceImplFastTest {
    private final Mockery context = new JUnit4Mockery();
    private ArchiveListerServiceImpl archiveListerService;
    private ArchiveListerQueries mockArchiveListerQueries;
    private ArchiveListerQueries.LinkMaker mockLinkMaker;
    private List<ArchiveListLink> expectedLinks;
    private ArchiveListLink expectedCurrentPage, expectedParentPage;


    @Before
    public void setUp() throws Exception {
        expectedLinks = new ArrayList<ArchiveListLink>();
        expectedCurrentPage = new ArchiveListLink();
        expectedParentPage = new ArchiveListLink();

        mockArchiveListerQueries = context.mock(ArchiveListerQueries.class);
        archiveListerService = new ArchiveListerServiceImpl();
        archiveListerService.setArchiveListerQueries(mockArchiveListerQueries);
        mockLinkMaker = context.mock(ArchiveListerQueries.LinkMaker.class);
    }


    @Test
    public void testWithAccessLevel() throws ArchiveListerQueries.ArchiveListerException {
        final ArchiveListLink topLevelCollection = new ArchiveListLink();

        context.checking(new Expectations() {{
            one(mockArchiveListerQueries).getDiseaseLinks(mockLinkMaker, "someAccessLevel");
            will(returnValue(expectedLinks));

            one(mockArchiveListerQueries).getCollectionLinks(mockLinkMaker, "someAccessLevel", null, null, null, null);
            will(returnValue(Arrays.asList(topLevelCollection)));

            one(mockLinkMaker).makeCurrentPage("someAccessLevel");
            will(returnValue(expectedCurrentPage));

            one(mockLinkMaker).makeParentPage(null);
            will(returnValue(expectedParentPage));
        }});

        final ArchiveListInfo archiveListInfo = archiveListerService.getArchiveListInfo(mockLinkMaker, "someAccessLevel", null, null, null, null, null, null);
        assertEquals(expectedLinks, archiveListInfo.getPageLinks());
        assertTrue(expectedLinks.contains(topLevelCollection));
        assertEquals(expectedCurrentPage, archiveListInfo.getCurrentPage());
        assertEquals(expectedParentPage, archiveListInfo.getParentPage());
    }


    @Test
    public void testWithAccessAndDisease() throws ArchiveListerQueries.ArchiveListerException {
        final ArchiveListLink diseaseLevelCollection = new ArchiveListLink();
        context.checking(new Expectations() {{
            one(mockArchiveListerQueries).getCenterTypeLinks(mockLinkMaker, "open", "testDisease");
            will(returnValue(expectedLinks));

            one(mockArchiveListerQueries).getCollectionLinks(mockLinkMaker, "open", "testDisease", null, null, null);
            will(returnValue(Arrays.asList(diseaseLevelCollection)));

            one(mockLinkMaker).makeCurrentPage("testDisease");
            will(returnValue(expectedCurrentPage));

            one(mockLinkMaker).makeParentPage("open");
            will(returnValue(expectedParentPage));
        }});
        final ArchiveListInfo archiveListInfo = archiveListerService.getArchiveListInfo(mockLinkMaker, "open", "testDisease", null, null, null, null, null);
        assertEquals(expectedLinks, archiveListInfo.getPageLinks());
        assertTrue(expectedLinks.contains(diseaseLevelCollection));
        assertEquals(expectedCurrentPage, archiveListInfo.getCurrentPage());
        assertEquals(expectedParentPage, archiveListInfo.getParentPage());
    }

    @Test
    public void testWithAccessDiseaseAndCenterType() throws ArchiveListerQueries.ArchiveListerException {
        final ArchiveListLink collection1 = new ArchiveListLink();
        final ArchiveListLink collection2 = new ArchiveListLink();
        context.checking(new Expectations() {{
            one(mockArchiveListerQueries).getCenterLinks(mockLinkMaker, "controlled", "ov", "cgcc");
            will(returnValue(expectedLinks));

            one(mockArchiveListerQueries).getCollectionLinks(mockLinkMaker, "controlled", "ov", "cgcc", null, null);
            will(returnValue(Arrays.asList(collection1, collection2)));

            one(mockLinkMaker).makeCurrentPage("cgcc");
            will(returnValue(expectedCurrentPage));

            one(mockLinkMaker).makeParentPage("ov");
            will(returnValue(expectedParentPage));
        }});
        final ArchiveListInfo archiveListInfo = archiveListerService.getArchiveListInfo(mockLinkMaker, "controlled", "ov", "cgcc", null, null, null, null);
        assertEquals(expectedLinks, archiveListInfo.getPageLinks());
        assertTrue(expectedLinks.contains(collection1));
        assertTrue(expectedLinks.contains(collection2));
        assertEquals(expectedCurrentPage, archiveListInfo.getCurrentPage());
        assertEquals(expectedParentPage, archiveListInfo.getParentPage());
    }

    @Test
    public void testWithAccessDiseaseCenterTypeAndCenter() throws ArchiveListerQueries.ArchiveListerException {
        context.checking(new Expectations() {{
            one(mockArchiveListerQueries).getPlatformLinks(mockLinkMaker, "anAccess", "aDisease", "aCenterType", "aCenter");
            will(returnValue(expectedLinks));

            one(mockArchiveListerQueries).getCollectionLinks(mockLinkMaker, "anAccess", "aDisease", "aCenterType", "aCenter", null);
            will(returnValue(new ArrayList<ArchiveListInfo>()));

            one(mockLinkMaker).makeCurrentPage("aCenter");
            will(returnValue(expectedCurrentPage));

            one(mockLinkMaker).makeParentPage("aCenterType");
            will(returnValue(expectedParentPage));
        }});
        final ArchiveListInfo archiveListInfo = archiveListerService.getArchiveListInfo(mockLinkMaker, "anAccess", "aDisease", "aCenterType", "aCenter", null, null, null);
        assertEquals(expectedLinks, archiveListInfo.getPageLinks());
        assertEquals(expectedCurrentPage, archiveListInfo.getCurrentPage());
        assertEquals(expectedParentPage, archiveListInfo.getParentPage());
    }

    @Test
    public void testWithAllExceptArchive() throws ArchiveListerQueries.ArchiveListerException {
        final ArchiveListLink someCollection = new ArchiveListLink();
        context.checking(new Expectations() {{
            one(mockArchiveListerQueries).getArchiveLinks(mockLinkMaker, "open", "ucec", "gsc", "baylor", "ABI");
            will(returnValue(expectedLinks));

            one(mockArchiveListerQueries).getCollectionLinks(mockLinkMaker, "open", "ucec", "gsc", "baylor", "ABI");
            will(returnValue(Arrays.asList(someCollection)));

            one(mockLinkMaker).makeCurrentPage("ABI");
            will(returnValue(expectedCurrentPage));

            one(mockLinkMaker).makeParentPage("baylor");
            will(returnValue(expectedParentPage));
        }});
        final ArchiveListInfo archiveListInfo = archiveListerService.getArchiveListInfo(mockLinkMaker, "open", "ucec", "gsc", "baylor", "ABI", null, null);
        assertEquals(expectedLinks, archiveListInfo.getPageLinks());
        assertTrue(expectedLinks.contains(someCollection));
        assertEquals(expectedCurrentPage, archiveListInfo.getCurrentPage());
        assertEquals(expectedParentPage, archiveListInfo.getParentPage());
    }

    @Test
    public void testWithArchive() throws ArchiveListerQueries.ArchiveListerException {
        context.checking(new Expectations() {{
          one(mockArchiveListerQueries).getArchiveFileLinks("anArchiveName");
            will(returnValue(expectedLinks));

            one(mockLinkMaker).makeCurrentPage("anArchiveName");
            will(returnValue(expectedCurrentPage));

            one(mockLinkMaker).makeParentPage("platform");
            will(returnValue(expectedParentPage));
        }});
        final ArchiveListInfo archiveListInfo = archiveListerService.getArchiveListInfo(mockLinkMaker, "access", "disease", "centerType", "center", "platform", "anArchiveName", null);
        assertEquals(expectedLinks, archiveListInfo.getPageLinks());
        assertEquals(expectedCurrentPage, archiveListInfo.getCurrentPage());
        assertEquals(expectedParentPage, archiveListInfo.getParentPage());
    }

    @Test
    public void testTopLevelCollectionFiles() throws ArchiveListerQueries.ArchiveListerException {
        context.checking(new Expectations() {{
            one(mockArchiveListerQueries).getCollectionFileLinks("aCollection", "access", null, null, null, null);
            will(returnValue(expectedLinks));

            one(mockLinkMaker).makeCurrentPage("aCollection");
            will(returnValue(expectedCurrentPage));

            one(mockLinkMaker).makeParentPage("access");
            will(returnValue(expectedParentPage));
        }});
        final ArchiveListInfo archiveListInfo = archiveListerService.getArchiveListInfo(mockLinkMaker, "access", null, null, null, null, null, "aCollection");
        assertEquals(expectedLinks, archiveListInfo.getPageLinks());
    }

    @Test
    public void testBcrBiotabCollectionFiles() throws ArchiveListerQueries.ArchiveListerException {
        context.checking(new Expectations() {{
            one(mockArchiveListerQueries).getCollectionFileLinks("biotab", "protected", "laml", "bcr", null, null);
            will(returnValue(expectedLinks));

            one(mockLinkMaker).makeCurrentPage("biotab");
            will(returnValue(expectedCurrentPage));
            one(mockLinkMaker).makeParentPage("bcr");
            will(returnValue(expectedParentPage));
        }});
        final ArchiveListInfo archiveListInfo = archiveListerService.getArchiveListInfo(mockLinkMaker, "protected", "laml", "bcr", null, null, null, "biotab");
        assertEquals(expectedLinks, archiveListInfo.getPageLinks());
    }

}
