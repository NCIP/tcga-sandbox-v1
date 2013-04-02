/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ExperimentQueries;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Test for ExperimentQueries
 *
 * @author ramanr Last updated by: $Author$
 * @version $Rev$
 */
public class ExperimentQueriesJDBCImplDBUnitSlowTest extends DBUnitTestCase {
	private static final String PROPERTIES_FILE = "common.unittest.properties";
	private static final String TEST_DATA_FOLDER = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;
	private static final String TEST_DATA_FILE = "qclive/dao/ExperimentQueries_TestData.xml";

	private static final String appContextFile = "samples/applicationContext-dbunit.xml";

	private ExperimentQueries queries;

	public ExperimentQueriesJDBCImplDBUnitSlowTest() {
		super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		final ApplicationContext appContext = new ClassPathXmlApplicationContext(appContextFile);
		queries = (ExperimentQueries) appContext.getBean("experimentQueries");
	}

	public void testGetExperiment() {

		final Experiment experiment = queries
				.getExperiment("unc.edu_OV.H-miRNA_8x15Kv2");
		assertTrue(experiment.getName().contains("unc.edu_OV.H-miRNA_8x15Kv2"));
		assertEquals(2, experiment.getArchives().size());
		assertNotNull(experiment
				.getArchive("unc.edu_OV.H-miRNA_8x15Kv2.Level_1.1.1.0"));
		assertNotNull(experiment
				.getArchive("unc.edu_OV.H-miRNA_8x15Kv2.Level_1.2.0.0"));
	}

    public void testGetExperimentOverlappingName() {
        final Experiment experiment = queries.getExperiment("unc.edu_OV.H-miRNA_8x15K");
		assertTrue(experiment.getName().equals("unc.edu_OV.H-miRNA_8x15K"));
		assertEquals(1, experiment.getArchives().size());
		assertNotNull(experiment
				.getArchive("unc.edu_OV.H-miRNA_8x15K.Level_1.1.2.0"));
    }

	public void testGetExperimentBadExperiment() {
		try {
			// Invalid scenario - TESTData is not in the dataset
			queries.getExperiment("TESTData");
		} catch (Exception e) {
			assertTrue(e.toString().contains("not a valid Experiment name"));
		}
	}

	public void testGetExperimentWithArchiveName() {
		// pass the archive name as experiment name
		final Experiment experiment = queries
				.getExperimentForSingleArchive("unc.edu_OV.H-miRNA_8x15Kv2.Level_1.1.1.0");
		assertNotNull(experiment);
		// since we passed a specific archive name, only one should be in the
		// experiment
		assertEquals(1, experiment.getArchives().size());
		// but previous archive should still be there too
		assertEquals("unc.edu_OV.H-miRNA_8x15Kv2.Level_1.1.0.0", experiment
				.getPreviousArchiveFor(experiment.getArchives().get(0))
				.getRealName());
	}

    public void testGetExperimentDataFiles() {
        final Map<Archive, List<FileInfo>> experimentDataFiles = queries.getExperimentDataFiles("unc.edu_OV.H-miRNA_8x15Kv2");
        assertEquals(2, experimentDataFiles.size());
        final Archive archive1 = new Archive();
        archive1.setRealName("unc.edu_OV.H-miRNA_8x15Kv2.Level_1.1.0.0");
        archive1.setId(1534L);

        final List<FileInfo> archive1Files = experimentDataFiles.get(archive1);
        assertEquals(2, archive1Files.size());
        assertEquals("level_1_file", archive1Files.get(0).getFileName());
        assertEquals("level_2_Hello", archive1Files.get(1).getFileName());

        final Archive archive2 = new Archive();
        archive2.setRealName("unc.edu_OV.H-miRNA_8x15Kv2.Level_1.2.0.0");
        archive2.setId(1536L);

        final List<FileInfo> archive2Files = experimentDataFiles.get(archive2);
        assertEquals(1, archive2Files.size());
        assertEquals("another_level_1_file", archive2Files.get(0).getFileName());


    }

}
