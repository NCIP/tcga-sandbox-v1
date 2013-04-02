package gov.nih.nci.ncicb.tcga.dcc.common.util;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileInfoQueries;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Test for SourceFileTypeFinder
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class SourceFileTypeFinderSdrfParserImplFastTest {
    private final Mockery context = new JUnit4Mockery();
    private FileInfoQueries mockFileInfoQueries;
    private SourceFileTypeFinderSdrfParserImpl sourceFileTypeFinder;

    private FileInfo pretendFileInfo;
    private Archive pretendArchive;

    private static final String SDRF_LOCATION =
            Thread.currentThread().getContextClassLoader().getResource("samples/sdrf/test1.sdrf.txt").getPath();

    @Before
    public void setUp() {
        mockFileInfoQueries = context.mock(FileInfoQueries.class);
        sourceFileTypeFinder = new SourceFileTypeFinderSdrfParserImpl();
        sourceFileTypeFinder.setFileInfoQueries(mockFileInfoQueries);

        pretendFileInfo = new FileInfo();
        pretendArchive = new Archive();
        pretendArchive.setDomainName("centerName");
        pretendArchive.setPlatform("platformName");
        pretendArchive.setTumorType("TSTCA");

        context.checking(new Expectations() {{
            one(mockFileInfoQueries).getFileForFileId(123L);
            will(returnValue(pretendFileInfo));
        }});
    }

    private void hasSdrf() {
        context.checking(new Expectations() {{
            one(mockFileInfoQueries).getSdrfFilePathForExperiment("centerName", "platformName", "TSTCA");
            will(returnValue(SDRF_LOCATION));
        }});
    }

    private void fileIsInLatestArchive() {
        context.checking(new Expectations() {{
            one(mockFileInfoQueries).getLatestArchiveContainingFile(pretendFileInfo);
            will(returnValue(pretendArchive));
        }});
    }

    @Test
    public void testFindSourceFileType() throws IOException {
        hasSdrf();
        fileIsInLatestArchive();
        pretendFileInfo.setFileName("aFile.txt");
        final String sourceFileType = sourceFileTypeFinder.findSourceFileType(123);
        assertEquals("happy", sourceFileType);
    }

    @Test
    public void testFindSourceFileTypeBadProtocol() throws IOException {
        hasSdrf();
        fileIsInLatestArchive();
        pretendFileInfo.setFileName("bFile.txt");
        final String sourceFileType = sourceFileTypeFinder.findSourceFileType(123);
        assertNull(sourceFileType);
    }

    @Test
    public void testFindSourceFileTypeFileNotInSDRF() throws IOException {
        hasSdrf();
        fileIsInLatestArchive();
        pretendFileInfo.setFileName("iDoNotExist.txt");
        final String sourceFileType = sourceFileTypeFinder.findSourceFileType(123);
        assertNull(sourceFileType);
    }

    @Test
    public void testFindSourceFileTypeNoSDRF() throws IOException {
        fileIsInLatestArchive();
        context.checking(new Expectations() {{
            one(mockFileInfoQueries).getSdrfFilePathForExperiment("centerName", "platformName", "TSTCA");
            will(returnValue(null));
        }});

        pretendFileInfo.setFileName("aFile.txt");
        final String sourceFileType = sourceFileTypeFinder.findSourceFileType(123);
        assertNull(sourceFileType);
    }

    @Test
    public void testFileSourceFileTypeNoLatestArchive() throws IOException {
        context.checking(new Expectations() {{
            one(mockFileInfoQueries).getLatestArchiveContainingFile(pretendFileInfo);
            will(returnValue(null));
        }});
        final String sourceFileType = sourceFileTypeFinder.findSourceFileType(123);
        assertNull(sourceFileType);
    }

}
