package gov.nih.nci.ncicb.tcga.dcc.dam.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.ArchiveListInfo;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.ArchiveListerQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.ArchiveListerService;

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
import org.springframework.ui.ExtendedModelMap;

/**
 * Test for ArchiveListerController.
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class ArchiveListerControllerFastTest {
    private final Mockery context = new JUnit4Mockery();
    private ArchiveListerController controller;
    private ExtendedModelMap model;
    private ArchiveListerService mockArchiveListerService;
    private ArchiveListInfo archiveListInfo;

    @Before
    public void setup() {
        controller = new ArchiveListerController();
        model = new ExtendedModelMap();
        mockArchiveListerService = context.mock(ArchiveListerService.class);
        controller.setArchiveListerService(mockArchiveListerService);
        archiveListInfo = new ArchiveListInfo();
    }

    private void testHandleRequest(final String accessLevel, final String disease, final String centerType,
                                              final String center, final String platform, final String archiveName, final String collectionName) {
        final String viewName = controller.handleRequest(model, accessLevel, disease, centerType, center, platform, archiveName, collectionName);
        assertEquals("archiveList", viewName);
        final ArchiveListInfo modelArchiveListInfo = (ArchiveListInfo) model.get("archiveListInfo");
        assertNotNull(String.valueOf(model.get("errorMessage")), modelArchiveListInfo);
        assertEquals(archiveListInfo, modelArchiveListInfo);
    }


    public static Matcher<ArchiveListerQueries.LinkMaker> theExpectedLinkMaker(final String testToken,
                                                                               final String expectedUrl,
                                                                               final String expectedCurrentUrl,
                                                                               final String expectedParentUrl,
                                                                               final boolean forCollection) {
        return new TypeSafeMatcher<ArchiveListerQueries.LinkMaker>() {
            @Override
            public boolean matchesSafely(final ArchiveListerQueries.LinkMaker linkMaker) {
                final String url = linkMaker.makeLinkUrl(testToken, forCollection);
                assertEquals(expectedUrl, url);
                assertEquals(expectedCurrentUrl, linkMaker.makeCurrentPage(testToken).getUrl());
                if (expectedParentUrl == null) {
                    assertNull(linkMaker.makeParentPage(testToken));
                } else {
                    assertEquals(expectedParentUrl, linkMaker.makeParentPage(testToken).getUrl());
                }
                return true; // if didn't fail asserts, it matches
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("checks that linkMaker works as expected with the testToken");
            }
        };
    }

    @Test
    public void testWithoutAccessLevel() {
        controller.handleRequest(model, null, null, null, null, null, null, null);
        assertNull(model.get("archiveListInfo"));
        assertEquals("accessLevel (open or controlled) must be given", model.get("errorMessage"));
    }

    @Test
     public void testWithAccessLevel() throws ArchiveListerQueries.ArchiveListerException {
        context.checking(new Expectations() {{
            one(mockArchiveListerService).getArchiveListInfo(
                    with(theExpectedLinkMaker("test",
                            ArchiveListerController.PAGE_NAME + "?accessLevel=controlled&disease=test",
                            ArchiveListerController.PAGE_NAME + "?accessLevel=controlled",
                            null, false)),
                    with("controlled"), with(aNull(String.class)), with(aNull(String.class)),
                    with(aNull(String.class)), with(aNull(String.class)), with(aNull(String.class)), with(aNull(String.class)));
            will(returnValue(archiveListInfo));
        }});

        testHandleRequest("controlled", null, null, null, null, null, null);
     }


    @Test
    public void testWithAccessLevelAndDisease() throws ArchiveListerQueries.ArchiveListerException {
        context.checking(new Expectations() {{
            one(mockArchiveListerService).getArchiveListInfo(
                    with(theExpectedLinkMaker("test",
                            ArchiveListerController.PAGE_NAME + "?accessLevel=al&disease=dis&centerType=test",
                            ArchiveListerController.PAGE_NAME + "?accessLevel=al&disease=dis",
                            ArchiveListerController.PAGE_NAME + "?accessLevel=al", false)),
                    with("al"), with("dis"), with(aNull(String.class)), with(aNull(String.class)),
                    with(aNull(String.class)), with(aNull(String.class)), with(aNull(String.class)));
            will(returnValue(archiveListInfo));
        }});

        testHandleRequest("al", "dis", null, null, null, null, null);
    }

    @Test
    public void testWithAccessLevelDiseaseAndCenterType() throws ArchiveListerQueries.ArchiveListerException {
        context.checking(new Expectations() {{
            one(mockArchiveListerService).getArchiveListInfo(
                    with(theExpectedLinkMaker("test",
                            ArchiveListerController.PAGE_NAME + "?accessLevel=squirrel&disease=racoon&centerType=bacon&center=test",
                            ArchiveListerController.PAGE_NAME + "?accessLevel=squirrel&disease=racoon&centerType=bacon",
                            ArchiveListerController.PAGE_NAME + "?accessLevel=squirrel&disease=racoon", false)),
                    with("squirrel"), with("racoon"), with("bacon"),
                    with(aNull(String.class)), with(aNull(String.class)), with(aNull(String.class)), with(aNull(String.class)));
            will(returnValue(archiveListInfo));
        }});

        testHandleRequest("squirrel", "racoon", "bacon", null, null, null, null);

    }


    @Test
    public void testWithAccessDiseaseCenterTypeAndCenter() throws ArchiveListerQueries.ArchiveListerException {
        context.checking(new Expectations() {{
            one(mockArchiveListerService).getArchiveListInfo(
                    with(theExpectedLinkMaker("test",
                            ArchiveListerController.PAGE_NAME + "?accessLevel=controlled&disease=brca&centerType=bcr&center=intgen.org&platform=test",
                            ArchiveListerController.PAGE_NAME + "?accessLevel=controlled&disease=brca&centerType=bcr&center=intgen.org",
                            ArchiveListerController.PAGE_NAME + "?accessLevel=controlled&disease=brca&centerType=bcr", false)),
                    with("controlled"), with("brca"), with("bcr"), with("intgen.org"),
                    with(aNull(String.class)), with(aNull(String.class)), with(aNull(String.class)));
            will(returnValue(archiveListInfo));
        }});

        testHandleRequest("controlled", "brca", "bcr", "intgen.org", null, null, null);
    }


    @Test
    public void testWithAllParamsUpToPlatform() throws ArchiveListerQueries.ArchiveListerException {
        context.checking(new Expectations() {{
            one(mockArchiveListerService).getArchiveListInfo(
                    with(theExpectedLinkMaker("test",
                            ArchiveListerController.PAGE_NAME + "?accessLevel=controlled&disease=brca&centerType=bcr&center=intgen.org&platform=bio&archive=test",
                            ArchiveListerController.PAGE_NAME + "?accessLevel=controlled&disease=brca&centerType=bcr&center=intgen.org&platform=bio",
                            ArchiveListerController.PAGE_NAME + "?accessLevel=controlled&disease=brca&centerType=bcr&center=intgen.org", false)),
                    with("controlled"), with("brca"), with("bcr"), with("intgen.org"), with("bio"), with(aNull(String.class)), with(aNull(String.class)));
            will(returnValue(archiveListInfo));
        }});

        testHandleRequest("controlled", "brca", "bcr", "intgen.org", "bio", null, null);
    }


    @Test
    public void testWithArchiveName() throws ArchiveListerQueries.ArchiveListerException {
        context.checking(new Expectations() {{
            one(mockArchiveListerService).getArchiveListInfo(with(theExpectedLinkMaker("test",
                    "test",
                    ArchiveListerController.PAGE_NAME + "?accessLevel=open&disease=gbm&centerType=cgcc&center=broad.mit.edu&platform=Genome_Wide_SNP6&archive=broad.mit.edu_GBM.Genome_Wide_SNP6.Level_3.1.0.0",
                    ArchiveListerController.PAGE_NAME + "?accessLevel=open&disease=gbm&centerType=cgcc&center=broad.mit.edu&platform=Genome_Wide_SNP6", false)),
                    with("open"), with("gbm"), with("cgcc"), with("broad.mit.edu"), with("Genome_Wide_SNP6"),
                    with("broad.mit.edu_GBM.Genome_Wide_SNP6.Level_3.1.0.0"), with(aNull(String.class)));
            will(returnValue(archiveListInfo));
        }});
        testHandleRequest("open", "gbm", "cgcc", "broad.mit.edu", "Genome_Wide_SNP6", "broad.mit.edu_GBM.Genome_Wide_SNP6.Level_3.1.0.0", null);
    }

    @Test
    public void testWithCollectionName() throws ArchiveListerQueries.ArchiveListerException {
        context.checking(new Expectations() {{
            one(mockArchiveListerService).getArchiveListInfo(with(theExpectedLinkMaker("test",
                    "test",
                    ArchiveListerController.PAGE_NAME + "?accessLevel=controlled&disease=ucec&centerType=bcr&center=intgen.org&platform=bio&collection=biotab",
                    ArchiveListerController.PAGE_NAME + "?accessLevel=controlled&disease=ucec&centerType=bcr&center=intgen.org&platform=bio", true)),
                    with("controlled"), with("ucec"), with("bcr"), with("intgen.org"), with("bio"), with(aNull(String.class)), with("biotab"));
            will(returnValue(archiveListInfo));
        }});
        testHandleRequest("controlled", "ucec", "bcr", "intgen.org", "bio", null, "biotab");
    }

    @Test
    public void testHandleRequestError() {
        assertEquals("archiveList", controller.handleRequest(model, null, null, null, null, null, null, null));
        assertFalse(model.containsKey("archiveListInfo"));
        assertTrue(model.containsKey("errorMessage"));
    }



}
