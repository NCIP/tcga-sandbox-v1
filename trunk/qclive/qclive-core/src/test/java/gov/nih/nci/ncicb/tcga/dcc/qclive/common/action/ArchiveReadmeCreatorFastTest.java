/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for ArchiveReadmeCreator class
 * 
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class ArchiveReadmeCreatorFastTest {

	private final String SAMPLES_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;
	private final Archive archive = new Archive();
	private String expectedReadmeText;

	@Before
	public void setUp() throws Exception {
		archive.setRealName("a_test.archive.1.0.0");
		final Tumor tumor = new Tumor();
		tumor.setTumorDescription("DISEASE NAME");
		tumor.setTumorName("DN");
		archive.setTheTumor(tumor);

		final Center center = new Center();
		center.setCenterDisplayName("SOME CENTER");
		center.setCenterName("fake.center.org");
		archive.setTheCenter(center);

		final Platform platform = new Platform();
		platform.setPlatformDisplayName("THIS IS NOT A REAL PLATFORM");
		archive.setThePlatform(platform);

		archive.setExperimentType(Experiment.TYPE_GSC);
		archive.setArchiveType(Archive.TYPE_LEVEL_1);

		archive.setSerialIndex("1");
		archive.setRevision("0");

		final Calendar now = Calendar.getInstance();
		final SimpleDateFormat dateFormat = new SimpleDateFormat(
				ArchiveReadmeCreator.DATE_FORMAT_FOR_README);
		expectedReadmeText = ArchiveReadmeCreator.formatParagraph(
				ArchiveReadmeCreator.LINE_WIDTH,
				"README for archive a_test.archive.1.0.0")
				+ "\n"
				+ ArchiveReadmeCreator.formatParagraph(
						ArchiveReadmeCreator.LINE_WIDTH,
						"This file was automatically generated on ",
						dateFormat.format(now.getTime()),
						" using the TCGA DCC Archive Processing System.")
				+ "\n"
				+ ArchiveReadmeCreator
						.formatParagraph(
								ArchiveReadmeCreator.LINE_WIDTH,
								"The Cancer Genome Atlas (TCGA), a project of the National Cancer Institute and the National ",
								"Human Genome Research Institute (NHGRI), is the foundation of a large-scale collaborative ",
								"effort to understand the genomic changes that occur in cancer. For more information, see ",
								"http://cancergenome.nih.gov/dataportal/index.asp.")
				+ "\n"
				+ ArchiveReadmeCreator
						.formatParagraph(
								ArchiveReadmeCreator.LINE_WIDTH,
								"The files in this archive contain data derived from tissue samples of patients that were diagnosed ",
								"with DISEASE NAME. The data was produced by the SOME CENTER (fake.center.org) ",
								"Genome Sequencing Center (GSC) using the THIS IS NOT A REAL PLATFORM platform.")
				+ "\n"
				+ ArchiveReadmeCreator
						.formatParagraph(
								ArchiveReadmeCreator.LINE_WIDTH,
								"This archive is the initial version of the 1st archive and contains only Level 1 (raw) data.")
				+ "\n"
				+ ArchiveReadmeCreator
						.formatParagraph(
								ArchiveReadmeCreator.LINE_WIDTH,
								"The MANIFEST.txt lists all the files, and their MD5 signatures, that should be included in this ",
								"complete archive.")
				+ "\n"
				+ ArchiveReadmeCreator
						.formatParagraph(
								ArchiveReadmeCreator.LINE_WIDTH,
								"A DESCRIPTION.txt file may be included by the submitting center that provides details about the ",
								"data files included in this archive.")
				+ "\n"
				+ ArchiveReadmeCreator
						.formatParagraph(
								ArchiveReadmeCreator.LINE_WIDTH,
								"The TCGA Data Primer provides an in depth description of TCGA data enterprise including data ",
								"classification and organization (including data types and data levels), how to access the data, ",
								"and a description of some possible ways to aggregate TCGA data. ",
								"http://tcga-data.nci.nih.gov/docs/TCGA_Data_Primer.pdf")
                + "\n"
                + ArchiveReadmeCreator
                .formatParagraph(
                        ArchiveReadmeCreator.LINE_WIDTH, "If you use TCGA data in your publications, please follow the guidelines described at ",
                        "http://cancergenome.nih.gov/abouttcga/policies/publicationguidelines.")
				+ "\n"
				+ "For even more information, please visit the TCGA Data Portal at http://tcga-data.nci.nih.gov\n"
				+ "\n"
				+ "For help, please contact NCICB Support http://ncicb.nci.nih.gov/NCICB/support\n";
	}

	@Test
	public void testCreateReadme() {
		final String readmeText = ArchiveReadmeCreator
				.createReadmeText(archive);
		assertEquals(expectedReadmeText, readmeText);
	}

	@Test
	public void testExecute() throws Processor.ProcessorException, IOException,
			NoSuchAlgorithmException, ParseException {
		final Mockery context = new JUnit4Mockery();
		final ManifestParser manifestParser = context
				.mock(ManifestParser.class);
		final File manifest = new File(SAMPLES_DIR
				+ "qclive/archiveReadmeCreator/MANIFEST.txt");
		final File readme = new File(SAMPLES_DIR
				+ "qclive/archiveReadmeCreator/README_TEST.txt");

		try {
			final ArchiveReadmeCreator archiveReadmeCreator = new ArchiveReadmeCreator() {

				@Override
				protected File getArchiveManifestFile(final Archive archive) {
					return manifest;
				}

				@Override
				protected File makeReadmeFile(final Archive archive) {
					return readme;
				}
			};
			archiveReadmeCreator.setManifestParser(manifestParser);
			final QcContext qcContext = new QcContext();
			context.checking(new Expectations() {
				{
					one(manifestParser).addFileToManifest(readme, manifest);
				}
			});

			archiveReadmeCreator.execute(archive, qcContext);
			assertTrue(qcContext.getErrorCount() == 0);
			assertTrue(readme.exists());
			context.assertIsSatisfied();

		} finally {
			readme.deleteOnExit();
		}
	}

	@Test
	public void testGetOrdinal() {
		assertEquals("1st", ArchiveReadmeCreator.getOrdinal("1"));
		assertEquals("2nd", ArchiveReadmeCreator.getOrdinal("2"));
		assertEquals("3rd", ArchiveReadmeCreator.getOrdinal("3"));
		assertEquals("4th", ArchiveReadmeCreator.getOrdinal("4"));
		assertEquals("9th", ArchiveReadmeCreator.getOrdinal("9"));
		assertEquals("11th", ArchiveReadmeCreator.getOrdinal("11"));
		assertEquals("12th", ArchiveReadmeCreator.getOrdinal("12"));
		assertEquals("13th", ArchiveReadmeCreator.getOrdinal("13"));
		assertEquals("51st", ArchiveReadmeCreator.getOrdinal("51"));
		assertEquals("42nd", ArchiveReadmeCreator.getOrdinal("42"));
		assertEquals("23rd", ArchiveReadmeCreator.getOrdinal("23"));
		assertEquals("104th", ArchiveReadmeCreator.getOrdinal("104"));
		assertEquals("567th", ArchiveReadmeCreator.getOrdinal("567"));
		assertEquals("NaN", ArchiveReadmeCreator.getOrdinal("hi"));
		assertEquals("NaN", ArchiveReadmeCreator.getOrdinal(null));
	}

	@Test
	public void testFormatParagraph() {
		assertEquals("\n", ArchiveReadmeCreator.formatParagraph(100));
		assertEquals("This is a test\n",
				ArchiveReadmeCreator.formatParagraph(100, "This is a test"));
		assertEquals("This\nis\na\ntest\n",
				ArchiveReadmeCreator.formatParagraph(1, "This is a test"));

		assertEquals(
				"This is a\ntest of\nthe\nemergency\nbroadcast\nsystem.\nThis is\nonly a\ntest.\n",
				ArchiveReadmeCreator
						.formatParagraph(10, "This is a test",
								" of the emergency broadcast system. This is only a test."));

		assertEquals(
				"This is a test of\nthe emergency\nbroadcast system.\nThis is only a test.\n",
				ArchiveReadmeCreator
						.formatParagraph(20, "This is a test",
								" of the emergency broadcast system. This is only a test."));

		assertEquals("ThisWordIsLongerThanTenCharacters\nand these\nare not\n",
				ArchiveReadmeCreator.formatParagraph(10,
						"ThisWordIsLongerThanTenCharacters and these are not"));

	}
}
