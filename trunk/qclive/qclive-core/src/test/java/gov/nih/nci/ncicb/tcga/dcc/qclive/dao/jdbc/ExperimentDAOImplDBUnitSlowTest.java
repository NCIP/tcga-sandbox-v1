package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ExperimentDAO;

import java.io.File;

import org.dbunit.operation.DatabaseOperation;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Test class for ExperimentDAO
 * 
 * @author Rohini Raman Last updated by: $Author$
 * @version $Rev$
 */
public class ExperimentDAOImplDBUnitSlowTest extends DBUnitTestCase {
	private static final String appContextFile = "samples/applicationContext-dbunit.xml";
	private static final String PROPERTIES_FILE = "common.unittest.properties";
	private static final String TEST_DATA_FOLDER = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;
	private static final String TEST_DATA_FILE = "qclive/dao/ExperimentQueries_TestData.xml";
	private final ApplicationContext appContext;
	private ExperimentDAO experimentDAO;
	private static final String ARCHIVE_NAME = "unc.edu_OV.H-miRNA_8x15Kv2.Level_1.1.0.0";

	private final Mockery context = new JUnit4Mockery();
	private ArchiveQueries mockDiseaseArchiveQueries = context
			.mock(ArchiveQueries.class);

	public ExperimentDAOImplDBUnitSlowTest() {
		super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
		appContext = new ClassPathXmlApplicationContext(appContextFile);
		experimentDAO = (ExperimentDAO) appContext.getBean("experimentDAO");
		experimentDAO.setDiseaseArchiveQueries(mockDiseaseArchiveQueries);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected DatabaseOperation getSetUpOperation() throws Exception {
		return DatabaseOperation.CLEAN_INSERT;
	}

	@Override
	protected DatabaseOperation getTearDownOperation() throws Exception {
		return DatabaseOperation.DELETE_ALL;
	}

	public void testGetArchiveByName() throws Exception {
		final Archive archive = experimentDAO.getArchiveByName(ARCHIVE_NAME);
		assertEquals(ARCHIVE_NAME, archive.getArchiveName());
	}

	public void testGetDeployDirectoryPath() throws Exception {
		final String deployDiseasePath = "/tumor/ov/cgcc/unc.edu/h-mirna_8x15kv2/hi/";
		final Archive archive = experimentDAO.getArchiveByName(ARCHIVE_NAME);
		final File testDeployDirectory = experimentDAO
				.getDeployDirectoryPath(archive);
		final StringBuilder sb = new StringBuilder();
		sb.append(experimentDAO.getPrivateDeployRoot())
				.append(deployDiseasePath).append(ARCHIVE_NAME);
		final File expectedDeployDir = new File(sb.toString());
		assertTrue(expectedDeployDir.equals(testDeployDirectory));

	}

	public void testUpdateArchiveStatus() throws Exception {
		final Archive archive = experimentDAO.getArchiveByName(ARCHIVE_NAME);
		archive.setDeployStatus(Archive.STATUS_DEPLOYED);

		context.checking(new Expectations() {
			{
				one(mockDiseaseArchiveQueries).updateArchiveStatus(archive);

			}
		});
		experimentDAO.updateArchiveStatus(archive);

		final Archive testArchive = experimentDAO
				.getArchiveByName(ARCHIVE_NAME);
		assertEquals(Archive.STATUS_DEPLOYED, testArchive.getDeployStatus());
	}
}
