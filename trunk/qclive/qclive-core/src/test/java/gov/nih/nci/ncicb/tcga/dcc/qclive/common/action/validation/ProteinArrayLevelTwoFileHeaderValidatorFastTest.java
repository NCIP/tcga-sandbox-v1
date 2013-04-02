package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContent;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContentImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * Test for protein array level two file header validator.
 * 
 * @author chenjw Last updated by: $Author$
 * @version $Rev$
 */
public class ProteinArrayLevelTwoFileHeaderValidatorFastTest {

	private static final String TEST_FILE_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator
			+ "qclive"
			+ File.separator
			+ "proteinArrayValidator" + File.separator + "level2";

	private ProteinArrayLevelTwoFileHeaderValidator validator;
	private QcContext qcContext;
	private Archive level2ProteinArchive;

	@Before
	public void setUp() {
		final TabDelimitedContent sdrf = new TabDelimitedContentImpl();
		String[] headers = new String[] { "Annotations File",
				"Array Data File", "Comment [TCGA Data Level]",
				"Derived Array Data File", "Comment [TCGA Data Level]", "File" };
		String[] row1 = new String[] { "a", "a.level1.txt", "Level 1",
				"a.level2.txt", "Level 2", "1" };
		String[] row2 = new String[] { "b", "b.level1.txt", "Level 1",
				"b.level2.txt", "Level 2", "2" };
		String[] row3 = new String[] { "c", "c.level1.txt", "Level 1",
				"b.level2.txt", "Level 2", "3" };
		Map<Integer, String[]> sdrfContents = new HashMap<Integer, String[]>();
		sdrfContents.put(0, headers);
		sdrfContents.put(1, row1);
		sdrfContents.put(2, row2);
		sdrfContents.put(3, row3);

		sdrf.setTabDelimitedContents(sdrfContents);
		sdrf.setTabDelimitedHeader(headers);

		qcContext = new QcContext();
		qcContext.setSdrf(sdrf);

		validator = new ProteinArrayLevelTwoFileHeaderValidator();

		level2ProteinArchive = new Archive();
		level2ProteinArchive.setArchiveType(Archive.TYPE_LEVEL_2);
		level2ProteinArchive
				.setPlatform(ProteinArrayLevelThreeDataFileValidator.PROTEIN_ARRAY_PLATFORM);

	}

	@Test
	public void testValid() throws Processor.ProcessorException {
		// the .tar.gz doesn't really exist, but the code uses the deploy
		// location to figure out the directory location
		// by remove the .tar.gz, so this lets it find the test files.
		level2ProteinArchive.setDeployLocation(TEST_FILE_DIR + File.separator
				+ "validTest" + ".tar.gz");

		final boolean isValid = validator.execute(level2ProteinArchive,
				qcContext);
		assertTrue(isValid);
		assertEquals(0, qcContext.getErrorCount());
		assertEquals(0, qcContext.getWarningCount());

	}

	@Test
	public void testInvalid() throws Processor.ProcessorException {
		level2ProteinArchive.setDeployLocation(TEST_FILE_DIR + File.separator
				+ "invalidTest" + ".tar.gz");
		final boolean isValid = validator.execute(level2ProteinArchive,
				qcContext);
		assertFalse(isValid);
		assertEquals(1, qcContext.getErrorCount());
		assertEquals(
				"level 2 file b.level2.txt refers to 'wombat' in its header, but that is not a level 1 file listed in the SDRF",
				qcContext.getErrors().get(0));
	}

	@Test(expected = Processor.ProcessorException.class)
	public void testNoSdrf() throws Processor.ProcessorException {
		qcContext.setSdrf(null);
		validator.execute(level2ProteinArchive, qcContext);
	}

	@Test
	public void testBadFile() throws Processor.ProcessorException {
		level2ProteinArchive.setDeployLocation(TEST_FILE_DIR + File.separator
				+ "badTest" + ".tar.gz");
		final boolean isValid = validator.execute(level2ProteinArchive,
				qcContext);
		assertFalse(isValid);
		assertEquals(2, qcContext.getErrorCount());

		assertEquals(
				"level 2 file a.level2.txt is not tab delimited at line number : '1'",
				qcContext.getErrors().get(0));
		assertEquals(
				"level 2 file b.level2.txt must contain at least three columns (identifier columns, then data columns",
				qcContext.getErrors().get(1));
	}

	@Test
	public void testFileNotTabDelimited() throws Processor.ProcessorException {
		level2ProteinArchive.setDeployLocation(TEST_FILE_DIR + File.separator
				+ "badFileTabDTest" + ".tar.gz");
		final boolean isValid = validator.execute(level2ProteinArchive,
				qcContext);
		assertFalse(isValid);
		assertEquals(2, qcContext.getErrorCount());
		assertEquals(
				"level 2 file a.level2.txt is not tab delimited at line number : '2'",
				qcContext.getErrors().get(0));
		assertEquals(
				"level 2 file a.level2.txt is not tab delimited at line number : '3'",
				qcContext.getErrors().get(1));
	}
}