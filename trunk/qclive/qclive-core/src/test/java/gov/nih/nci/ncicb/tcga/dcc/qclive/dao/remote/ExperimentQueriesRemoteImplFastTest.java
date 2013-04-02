package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.remote;

import gov.nih.nci.ncicb.tcga.dcc.qclive.soundcheck.RemoteValidationHelper;
import gov.nih.nci.ncicb.tcga.dccws.Archive;
import gov.nih.nci.ncicb.tcga.dccws.FileInfo;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * Test for Experiment Queries remote impl.
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class ExperimentQueriesRemoteImplFastTest {

    private final Mockery context = new JUnit4Mockery();
    private ExperimentQueriesRemoteImpl experimentQueriesRemote;
    private RemoteValidationHelper mockRemoteValidationHelper;

    @Before
    public void setUp() throws Exception {
        mockRemoteValidationHelper = context.mock(RemoteValidationHelper.class);
        experimentQueriesRemote = new ExperimentQueriesRemoteImpl(mockRemoteValidationHelper);
    }

    @Test
    public void testGetExperimentDataFiles() throws Exception {
        final List<Archive> experimentArchives = new ArrayList<Archive>();
        final Archive archive1 = new Archive();
        archive1.setName("archive1");
        archive1.setId(1);
        final Archive archive2 = new Archive();
        archive2.setName("archive2");
        archive2.setId(2);
        experimentArchives.add(archive1);
        experimentArchives.add(archive2);

        final List<FileInfo> archive1Files = new ArrayList<FileInfo>();
        final FileInfo file1 = new FileInfo();
        file1.setId(1);
        file1.setName("file1");
        final FileInfo file2 = new FileInfo();
        file2.setId(2);
        file2.setName("file2");
        archive1Files.add(file1);
        archive1Files.add(file2);

        final List<FileInfo> archive2Files = new ArrayList<FileInfo>();
        final FileInfo file3 = new FileInfo();
        file3.setId(3);
        file3.setName("file3");
        archive2Files.add(file3);

        context.checking(new Expectations() {{
            one(mockRemoteValidationHelper).getLatestArchives("disease", "center", "platform");
            will(returnValue(experimentArchives));

            one(mockRemoteValidationHelper).getArchiveDataFiles(archive1);
            will(returnValue(archive1Files));

            one(mockRemoteValidationHelper).getArchiveDataFiles(archive2);
            will(returnValue(archive2Files));
        }});

        final Map<gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive,
                List<gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo>> dataFiles =
                experimentQueriesRemote.getExperimentDataFiles("center_disease.platform");

        assertEquals(2, dataFiles.size());
        for (final gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive archive : dataFiles.keySet()) {
            final List<gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo> archiveFiles = dataFiles.get(archive);

            if (archive.getId() == 1) {
                assertEquals(2, archiveFiles.size());
                assertEquals("file1", archiveFiles.get(0).getFileName());
                assertEquals("file2", archiveFiles.get(1).getFileName());
            } else if (archive.getId() == 2) {
                assertEquals(1, archiveFiles.size());
                assertEquals("file3", archiveFiles.get(0).getFileName());
            } else {
                fail("Unexpected archive in results: " + archive.getRealName());
            }
        }
    }
}
