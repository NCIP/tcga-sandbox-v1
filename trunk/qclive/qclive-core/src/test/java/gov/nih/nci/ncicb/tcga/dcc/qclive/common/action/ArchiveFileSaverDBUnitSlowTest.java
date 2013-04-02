package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileInfoQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.FileArchiveQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.FileInfoQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;

import java.io.File;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;

/**
 * DB unit test for ArchiveFileSaver. Uses test database to verify
 * inserting/updating of file_info and file_to_archive records.
 * 
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class ArchiveFileSaverDBUnitSlowTest extends DBUnitTestCase {

	private static final String PROPERTIES_FILE = "common.unittest.properties";
	private static final String TEST_DATA_FOLDER = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;
	private static final String TEST_DATA_FILE = "qclive/dao/archiveFileSaver_testData.xml";
	private static final String ARCHIVE_LOCATION = TEST_DATA_FOLDER
			+ "qclive/archiveFileSaver/test.org_TUM.aPlatform.Level_1.1.0.0.tar.gz";

	private static final long ARCHIVE_ID = 10;

	private ArchiveFileSaver archiveFileSaver;
	private FileInfoQueriesJDBCImpl fileInfoQueries;
	private FileArchiveQueriesJDBCImpl fileArchiveQueries;
	private Mockery jmockContext = new JUnit4Mockery();
	private FileInfoQueries mockDiseaseFileInfoQueries = jmockContext.mock(
			FileInfoQueries.class, "disease_fileinfo");
	private FileArchiveQueries mockDiseaseFileArchiveQueries = jmockContext
			.mock(FileArchiveQueries.class, "disease_archive");

	private Archive archive;

	public ArchiveFileSaverDBUnitSlowTest() {
		super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		fileInfoQueries = new FileInfoQueriesJDBCImpl();
		fileInfoQueries.setDataSource(getDataSource());
		fileArchiveQueries = new FileArchiveQueriesJDBCImpl();
		fileArchiveQueries.setDataSource(getDataSource());

		archiveFileSaver = new ArchiveFileSaver();
		archiveFileSaver.setCommonFileInfoQueries(fileInfoQueries);
		archiveFileSaver.setFileArchiveQueries(fileArchiveQueries);
		archiveFileSaver
				.setDiseaseFileArchiveQueries(mockDiseaseFileArchiveQueries);
		archiveFileSaver.setDiseaseFileInfoQueries(mockDiseaseFileInfoQueries);
		archiveFileSaver
				.setAdditionalFiles("manifest.txt,description.txt,changes_dcc.txt,readme_dcc.txt,dcc_altered_files.txt");

		archive = new Archive();
		archive.setArchiveFile(new File(ARCHIVE_LOCATION));
		archive.setDeployLocation(ARCHIVE_LOCATION);
		archive.setId(ARCHIVE_ID);
		archive.setDataType("Expression-Genes");
		archive.setDataTypeId(1);
		archive.setRevision("1");
		archive.setArchiveType("Level_1");
		archive.setSerialIndex("1");
	}

	public void testExecute() throws Processor.ProcessorException {
		// in the archive directory, there are three files: file1.txt,
		// file2.txt, file3.txt, file4.txt and file5.txt
		// in the test db, file1.txt is there, with no url
		// file2.txt is there, as is its file url
		// file3.txt is in there, and is associated with a previous archive
		// [archive_id = 9], has same md5 as the file in db
		// file4.txt is in there, and is associated with a previous archive
		// [archive_id = 9], has a different md5 as the file in db
		// file5.txt is not in db

		QcContext context = new QcContext();
		Experiment experiment = new Experiment();
		final Archive previousArchive = new Archive();
		previousArchive.setId(9L);
		previousArchive.setRealName("");
		previousArchive.setArchiveTypeId(1);
		previousArchive.setArchiveType("Level_1");
		previousArchive.setSerialIndex("1");
		experiment.addArchive(previousArchive);
		experiment.addArchive(archive);
		experiment.addPreviousArchive(previousArchive);
		context.setExperiment(experiment);

		jmockContext.checking(new Expectations() {
			{
				allowing(mockDiseaseFileInfoQueries).updateFile(
						with(any(FileInfo.class)));
				allowing(mockDiseaseFileArchiveQueries)
						.addFileToArchiveAssociation(with(any(FileInfo.class)),
								with(any(Archive.class)),
								with(any(Boolean.class)), with(any(Long.class)));
				allowing(mockDiseaseFileInfoQueries).addFile(
						with(any(FileInfo.class)), with(any(Boolean.class)));
			}
		});

		archiveFileSaver.execute(archive, context);

		// now all five files should be in the db, and each should have a file
		// url
		assertEquals(new Long(1),
				fileInfoQueries.getFileId("file1.txt", ARCHIVE_ID));
		assertEquals(new Long(2),
				fileInfoQueries.getFileId("file2.txt", ARCHIVE_ID));
		long fileId3 = fileInfoQueries.getFileId("file3.txt", ARCHIVE_ID);
		assertEquals(3L, fileId3); // should not add a new file, just update the
									// existing one since the file with same
									// name and md5 is already in db for a
									// previous archive
		long fileId4 = fileInfoQueries.getFileId("file4.txt", ARCHIVE_ID);
		assertTrue(fileId4 > 0);
		FileInfo fileInfoForNewFile4 = fileInfoQueries
				.getFileForFileId(fileId4);
		assertEquals(4, fileInfoForNewFile4.getRevision().intValue());
		long fileId5 = fileInfoQueries.getFileId("file5.txt", ARCHIVE_ID);
		assertTrue(fileId5 > 0); // will assign ID from sequence so can't check
									// exact value, but >0 means exists
		long fileId = fileInfoQueries.getFileId("readme_dcc.txt", ARCHIVE_ID);
		assertEquals(0, fileInfoQueries.getFileForFileId(fileId).getDataLevel()
				.intValue());
		fileId = fileInfoQueries.getFileId("MANIFEST.txt", ARCHIVE_ID);
		assertEquals(0, fileInfoQueries.getFileForFileId(fileId).getDataLevel()
				.intValue());

	}

}
