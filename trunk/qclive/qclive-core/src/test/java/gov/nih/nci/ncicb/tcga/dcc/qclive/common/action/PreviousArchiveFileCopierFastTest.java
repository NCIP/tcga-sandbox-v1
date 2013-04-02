package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParserImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for PreviousArchiveFileCopier
 * 
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class PreviousArchiveFileCopierFastTest {
	private PreviousArchiveFileCopier previousArchiveFileCopier;
	private Experiment experiment;
	private Archive newArchive, previousArchive, newArchiveBad;
	private QcContext qcContext;

	private final File expectedNewFile = new File(TEST_FILE_LOCATION
			+ "newArchive/hello.txt");

	private static final String SAMPLE_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;

	private static final String TEST_FILE_LOCATION = SAMPLE_DIR
			+ "qclive/previousArchiveFileCopier/";

	@Before
	public void setUp() {
		qcContext = new QcContext();
		experiment = new Experiment();

		newArchive = new Archive();
		newArchive.setDeployLocation(TEST_FILE_LOCATION + "newArchive.tar.gz");
		newArchive.setArchiveType(Archive.TYPE_LEVEL_2);
		newArchive.setSerialIndex("5");
		newArchive.setRevision("1");
		newArchive.setDeployStatus(Archive.STATUS_UPLOADED);

		newArchiveBad = new Archive();
		newArchiveBad.setDeployLocation(TEST_FILE_LOCATION
				+ "newArchiveBad.tar.gz");
		newArchiveBad.setArchiveType(Archive.TYPE_LEVEL_2);
		newArchiveBad.setSerialIndex("5");
		newArchiveBad.setRevision("1");
		newArchiveBad.setDeployStatus(Archive.STATUS_UPLOADED);

		previousArchive = new Archive();
		previousArchive.setDeployLocation(TEST_FILE_LOCATION
				+ "previousArchive.tar.gz");
		previousArchive.setArchiveType(Archive.TYPE_LEVEL_2);
		previousArchive.setSerialIndex("5");
		previousArchive.setRevision("0");
		previousArchive.setDeployStatus(Archive.STATUS_AVAILABLE);

		previousArchiveFileCopier = new PreviousArchiveFileCopier();
		previousArchiveFileCopier.setManifestParser(new ManifestParserImpl());

		qcContext.setExperiment(experiment);
	}

	@After
	public void cleanUp() {
		if (expectedNewFile.exists()) {
			expectedNewFile.deleteOnExit();
		}
	}

	@Test
	public void testNoPreviousArchive() throws Processor.ProcessorException {
		experiment.addArchive(newArchive);
		final boolean success = previousArchiveFileCopier.doWork(newArchive,
				qcContext);

		assertTrue(success);
		assertEquals(0, qcContext.getErrorCount());
		assertEquals(0, qcContext.getFilesCopiedFromPreviousArchive().size());
	}

	@Test
	public void testWithPreviousArchive() throws Processor.ProcessorException {
		assertFalse("clean-up did not complete in previous run",
				expectedNewFile.exists());

		experiment.addPreviousArchive(previousArchive);
		experiment.addArchive(newArchive);

		assertTrue(previousArchiveFileCopier.doWork(newArchive, qcContext));
		assertTrue(expectedNewFile.exists());
		assertEquals(0, qcContext.getErrorCount());
		assertTrue(qcContext.getFilesCopiedFromPreviousArchive().contains(
				"hello.txt"));
	}

	@Test
	public void testWithPreviousArchiveMissingFile()
			throws Processor.ProcessorException {
		// manifest for newArchiveBad includes a file that isn't in the new
		// archive but isn't in the previous one either
		// should ignore that file -- assume another validator will handle it
		experiment.addArchive(newArchiveBad);
		experiment.addPreviousArchive(previousArchive);

		assertTrue(previousArchiveFileCopier.doWork(newArchiveBad, qcContext));
		assertEquals(0, qcContext.getErrorCount());
		assertEquals(0, qcContext.getFilesCopiedFromPreviousArchive().size());
	}
}
