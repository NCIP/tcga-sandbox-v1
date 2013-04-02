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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContent;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContentImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedFileParser;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class SdrfRewriterFastTest {

	private static final String SAMPLES_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;

	private String sdrfFile = SAMPLES_DIR + File.separator + "qclive"
			+ File.separator + "sdrfRewriter" + File.separator
			+ "test.sdrf.txt";
	private String tempFile = SAMPLES_DIR + File.separator + "qclive"
			+ File.separator + "sdrfRewriter" + File.separator + "_tmp.txt";

	Experiment experiment;
	Archive newArchive;
	Archive oldArchive;
	Archive mageTabArchive;
	TabDelimitedContent sdrf;
	TabDelimitedFileParser parser;

	@Before
	public void setup() throws IOException,ParseException {
		// 1. create experiment with archives and stuff, and mage-tab archive
		experiment = new Experiment();
		newArchive = new Archive();
		newArchive.setRealName("new_archive");
		newArchive.setDomainName("domain");
		newArchive.setTumorType("tumor");
		newArchive.setPlatform("platform");
		newArchive.setSerialIndex("1");
		newArchive.setRevision("2");
		newArchive.setArchiveType("type");
		oldArchive = new Archive();
		oldArchive.setRealName("old_archive");
		oldArchive.setDomainName("domain");
		oldArchive.setTumorType("tumor");
		oldArchive.setPlatform("platform");
		oldArchive.setSerialIndex("1");
		oldArchive.setRevision("0");
		oldArchive.setArchiveType("type");
		mageTabArchive = new Archive();
		mageTabArchive.setArchiveType(Archive.TYPE_MAGE_TAB);
		mageTabArchive.setDeployLocation(SAMPLES_DIR
				+ "qclive/sdrfRewriter.tar.gz");
		sdrf = new TabDelimitedContentImpl();
		parser = new TabDelimitedFileParser();
		parser.setTabDelimitedContent(sdrf);
		parser.initialize(sdrfFile);
		mageTabArchive.setSdrf(sdrf);
		// take a backup before making changes
		FileUtil.copy(sdrfFile, tempFile);

	}

	@After
	public void cleanup() throws IOException {
		// restore the original file
		FileUtil.move(tempFile, sdrfFile);
	}

	@Test
	public void test() throws Processor.ProcessorException, IOException,ParseException {
		experiment.addArchive(mageTabArchive);
		experiment.addArchive(newArchive);
		experiment.addPreviousArchive(oldArchive);
		// 2. run test
		SdrfRewriter rewriter = new SdrfRewriter();
		QcContext context = new QcContext();
		context.setExperiment(experiment);
		rewriter.execute(mageTabArchive, context);
		assertTrue(context.getErrorCount() == 0);
		assertTrue(context.getWarningCount() == 0);
		// sdrf object should also be changed
		assertEquals("new_archive", sdrf.getTabDelimitedContents().get(1)[18]);
		// read in new SDRF
		TabDelimitedContent newSdrf = new TabDelimitedContentImpl();
		parser.setTabDelimitedContent(newSdrf);
		parser.initialize(sdrfFile);
		assertEquals("SDRF was not rewritten properly", "new_archive", newSdrf
				.getTabDelimitedContents().get(1)[18]);
		assertTrue(context.getAlteredFiles().containsKey("test.sdrf.txt"));
	}

	@Test
	public void testNoChange() throws Processor.ProcessorException {
		experiment.addArchive(mageTabArchive);
		experiment.addArchive(newArchive);
		experiment.addArchive(oldArchive);
		SdrfRewriter rewriter = new SdrfRewriter();
		QcContext context = new QcContext();
		context.setExperiment(experiment);
		rewriter.execute(mageTabArchive, context);
		// since oldArchive is a current archive and not a previous archive,
		// nothing should change in the sdrf
		assertEquals("old_archive", sdrf.getTabDelimitedContents().get(1)[18]);
		assertFalse(new File(sdrfFile + ".orig").exists());
		assertFalse(context.getAlteredFiles().containsKey("test.sdrf.txt"));
		assertTrue(context.getErrorCount() == 0);
	}

	@Test
	public void testDataMatrixFileUpdate() throws Processor.ProcessorException,
			IOException,ParseException {
		experiment.addArchive(mageTabArchive);
		experiment.addArchive(newArchive);
		experiment.addArchive(oldArchive);
		QcContext context = new QcContext();
		context.setExperiment(experiment);
		sdrf = new TabDelimitedContentImpl();
		parser = new TabDelimitedFileParser();
		parser.setTabDelimitedContent(sdrf);
		parser.initialize(sdrfFile);
		mageTabArchive.setSdrf(sdrf);

		SdrfRewriter rewriter = new SdrfRewriter();

		final Map<String, File> splitDataMatrixFiles = new HashMap<String, File>();
		splitDataMatrixFiles
				.put("TCGA-07-0227-20A-01D-0859-05",
						new File(
								"jhu-usc.edu_KIRP.HumanMethylation27.1.lvl-2.TCGA-07-0227-20A-01D-0859-05.txt"));
		splitDataMatrixFiles
				.put("TCGA-A3-3306-01A-01D-0859-05",
						new File(
								"jhu-usc.edu_KIRP.HumanMethylation27.1.lvl-2.TCGA-A3-3306-01A-01D-0859-05.txt"));
		context.addSplitDataMatrixFiles(splitDataMatrixFiles,
				"jhu-usc.edu_KIRP.HumanMethylation27.1.lvl-2.txt");

		rewriter.execute(mageTabArchive, context);

		// read in new SDRF
		TabDelimitedContent newSdrf = new TabDelimitedContentImpl();
		parser.setTabDelimitedContent(newSdrf);
		parser.initialize(sdrfFile);
		// validate the updated fields
		assertEquals(
				"SDRF was not rewritten properly",
				"jhu-usc.edu_KIRP.HumanMethylation27.1.lvl-2.TCGA-07-0227-20A-01D-0859-05.txt",
				newSdrf.getTabDelimitedContents().get(3)[21]);
		assertEquals(
				"SDRF was not rewritten properly",
				"jhu-usc.edu_KIRP.HumanMethylation27.1.lvl-2.TCGA-A3-3306-01A-01D-0859-05.txt",
				newSdrf.getTabDelimitedContents().get(4)[21]);
		assertTrue(context.getAlteredFiles().containsKey("test.sdrf.txt"));
	}

	@Test
	public void testUpdateSDRFFileForBarcodes()
			throws Processor.ProcessorException, IOException,ParseException {
		QcContext context = new QcContext();
		SdrfRewriter rewriter = new SdrfRewriter();

		final Map<String, File> splitDataMatrixFiles = new HashMap<String, File>();
		splitDataMatrixFiles
				.put("TCGA-07-0227-20A-01D-0859-05",
						new File(
								"jhu-usc.edu_KIRP.HumanMethylation27.1.lvl-2.TCGA-07-0227-20A-01D-0859-05.txt"));
		splitDataMatrixFiles
				.put("TCGA-A3-3306-01A-01D-0859-05",
						new File(
								"jhu-usc.edu_KIRP.HumanMethylation27.1.lvl-2.TCGA-A3-3306-01A-01D-0859-05.txt"));
		context.addSplitDataMatrixFiles(splitDataMatrixFiles,
				"jhu-usc.edu_KIRP.HumanMethylation27.1.lvl-2.txt");

		final Map<String, String> hybRefByBarcode = rewriter.updateSDRFFile(
				new File(sdrfFile), context);

		// read in new SDRF
		TabDelimitedContent newSdrf = new TabDelimitedContentImpl();
		parser.setTabDelimitedContent(newSdrf);
		parser.initialize(sdrfFile);
		// validate the updated fields
		assertEquals(
				"SDRF was not rewritten properly",
				"jhu-usc.edu_KIRP.HumanMethylation27.1.lvl-2.TCGA-07-0227-20A-01D-0859-05.txt",
				newSdrf.getTabDelimitedContents().get(3)[21]);
		assertEquals(
				"SDRF was not rewritten properly",
				"jhu-usc.edu_KIRP.HumanMethylation27.1.lvl-2.TCGA-A3-3306-01A-01D-0859-05.txt",
				newSdrf.getTabDelimitedContents().get(4)[21]);
		assertTrue(hybRefByBarcode.size() == 2);
		assertTrue(hybRefByBarcode.get("TCGA-07-0227-20A-01D-0859-05").equals(
				"TCGA-07-0227-20A-01D-0859-05"));
		assertTrue(hybRefByBarcode.get("TCGA-A3-3306-01A-01D-0859-05").equals(
				"TCGA-A3-3306-01A-01D-0859-05"));

	}

	@Test
	public void testUpdateSDRFFileForHybRefIds()
			throws Processor.ProcessorException, IOException,ParseException {
		QcContext context = new QcContext();
		SdrfRewriter rewriter = new SdrfRewriter();

		final Map<String, File> splitDataMatrixFiles = new HashMap<String, File>();
		splitDataMatrixFiles
				.put("0000001",
						new File(
								"jhu-usc.edu_KIRP.HumanMethylation27.1.lvl-2.TCGA-07-0227-20A-01D-0859-05.txt"));
		splitDataMatrixFiles
				.put("0000002",
						new File(
								"jhu-usc.edu_KIRP.HumanMethylation27.1.lvl-2.TCGA-A3-3306-01A-01D-0859-05.txt"));
		context.addSplitDataMatrixFiles(splitDataMatrixFiles,
				"jhu-usc.edu_KIRP.HumanMethylation27.1.lvl-2.txt");
		final File testSDRFFile = getSDRFFileForHybRefIds();
		final Map<String, String> hybRefByBarcode = rewriter.updateSDRFFile(
				testSDRFFile, context);

		// read in new SDRF
		TabDelimitedContent newSdrf = new TabDelimitedContentImpl();
		parser.setTabDelimitedContent(newSdrf);
		parser.initialize(testSDRFFile.getPath());
		// validate the updated fields
		assertEquals(
				"SDRF was not rewritten properly",
				"jhu-usc.edu_KIRP.HumanMethylation27.1.lvl-2.TCGA-07-0227-20A-01D-0859-05.txt",
				newSdrf.getTabDelimitedContents().get(1)[21]);
		assertEquals(
				"SDRF was not rewritten properly",
				"jhu-usc.edu_KIRP.HumanMethylation27.1.lvl-2.TCGA-A3-3306-01A-01D-0859-05.txt",
				newSdrf.getTabDelimitedContents().get(2)[21]);
		assertTrue(hybRefByBarcode.size() == 2);
		assertTrue(hybRefByBarcode.get("0000001").equals(
				"TCGA-07-0227-20A-01D-0859-05"));
		assertTrue(hybRefByBarcode.get("0000002").equals(
				"TCGA-A3-3306-01A-01D-0859-05"));

	}

	@Test
	public void testUpdateSDRFFileForSameAliquotInMultipleDataMatrixFiles()
			throws Processor.ProcessorException, IOException,ParseException {
		QcContext context = new QcContext();
		SdrfRewriter rewriter = new SdrfRewriter();

		Map<String, File> splitDataMatrixFiles = new HashMap<String, File>();
		splitDataMatrixFiles
				.put("TCGA-07-0227-20A-01D-0859-05",
						new File(
								"jhu-usc.edu_KIRP.HumanMethylation27.1.lvl-2_Geno.TCGA-07-0227-20A-01D-0859-05.txt"));
		context.addSplitDataMatrixFiles(splitDataMatrixFiles,
				"jhu-usc.edu_KIRP.HumanMethylation27.1.lvl-2_Geno.txt");
		splitDataMatrixFiles = new HashMap<String, File>();
		splitDataMatrixFiles
				.put("TCGA-07-0227-20A-01D-0859-05",
						new File(
								"jhu-usc.edu_KIRP.HumanMethylation27.1.lvl-2_Paired.TCGA-07-0227-20A-01D-0859-05.txt"));
		context.addSplitDataMatrixFiles(splitDataMatrixFiles,
				"jhu-usc.edu_KIRP.HumanMethylation27.1.lvl-2_Paired.txt");

		final File testSDRFFile = getSDRFFileForSameAliquotInMultipleDataMatrixFiles();
		final Map<String, String> hybRefByBarcode = rewriter.updateSDRFFile(
				testSDRFFile, context);

		// read in new SDRF
		TabDelimitedContent newSdrf = new TabDelimitedContentImpl();
		parser.setTabDelimitedContent(newSdrf);
		parser.initialize(testSDRFFile.getPath());
		// validate the updated fields
		assertEquals(
				"Error updating SDRF File",
				"jhu-usc.edu_KIRP.HumanMethylation27.1.lvl-2_Geno.TCGA-07-0227-20A-01D-0859-05.txt",
				newSdrf.getTabDelimitedContents().get(1)[21]);
		assertEquals(
				"Error updating SDRF File",
				"jhu-usc.edu_KIRP.HumanMethylation27.1.lvl-2_Paired.TCGA-07-0227-20A-01D-0859-05.txt",
				newSdrf.getTabDelimitedContents().get(2)[21]);
		assertEquals(1, hybRefByBarcode.size());

	}

	private File getSDRFFileForHybRefIds() throws IOException {
		final String sdrfData = "Extract Name\tProtocol REF\tLabeled Extract Name\tLabel\tTerm Source REF\tProtocol REF\tHybridization Name\tArray Design File\tTerm Source REF\tProtocol REF\tScan Name\tProtocol REF\tProtocol REF\tNormalization Name\tDerived Array Data Matrix File\tComment [TCGA Data Level]\tComment [TCGA Data Type]\tComment [TCGA Include for Analysis]\tComment [TCGA Archive Name]\tProtocol REF\tNormalization Name\tDerived Array Data Matrix File\tComment [TCGA Data Level]\tComment [TCGA Data Type]\tComment [TCGA Include for Analysis]\tComment [TCGA Archive Name]\tProtocol REF\tNormalization Name\tDerived Array Data Matrix File\tComment [TCGA Data Level]\tComment [TCGA Data Type]\tComment [TCGA Include for Analysis]\tComment [TCGA Archive Name]\n"
				+ "TCGA-07-0227-20A-01D-0859-05\tjhu-usc.edu:labeling:HumanMethylation27:01\tTCGA-07-0227-20A-01D-0859-05\tbiotin\tMGED Ontology\tjhu-usc.edu:hybridization:HumanMethylation27:01\t0000001\tjhu-usc.edu_KIRP.HumanMethylation27.1.adf.txt\tcaArray\tjhu-usc.edu:image_acquisition:HumanMethylation27:01\tTCGA-07-0227-20A-01D-0859-05\tjhu-usc.edu:feature_extraction:HumanMethylation27:01\tjhu-usc.edu:within_bioassay_data_set_function:HumanMethylation27:01\tTCGA-07-0227-20A-01D-0859-05\tjhu-usc.edu_KIRP.HumanMethylation27.1.lvl-1.TCGA-07-0227-20A-01D-0859-05.txt\tLevel 1\tDNA-Methylation\tyes\tjhu-usc.edu_KIRP.HumanMethylation27.Level_1.1.0.0\tjhu-usc.edu:within_bioassay_data_set_function:HumanMethylation27:01\tTCGA-07-0227-20A-01D-0859-05\tjhu-usc.edu_KIRP.HumanMethylation27.1.lvl-2.txt\tLevel 2\tDNA-Methylation\tyes\tjhu-usc.edu_KIRP.HumanMethylation27.Level_2.1.0.0\tjhu-usc.edu:within_bioassay_data_set_function:HumanMethylation27:01\tTCGA-07-0227-20A-01D-0859-05\tjhu-usc.edu_KIRP.HumanMethylation27.1.lvl-3.TCGA-07-0227-20A-01D-0859-05.txt\tLevel 3\tDNA-Methylation\tyes\tjhu-usc.edu_KIRP.HumanMethylation27.Level_3.1.0.0\n"
				+ "TCGA-A3-3306-01A-01D-0859-05\tjhu-usc.edu:labeling:HumanMethylation27:01\tTCGA-A3-3306-01A-01D-0859-05\tbiotin\tMGED Ontology\tjhu-usc.edu:hybridization:HumanMethylation27:01\t0000002\tjhu-usc.edu_KIRP.HumanMethylation27.1.adf.txt\tcaArray\tjhu-usc.edu:image_acquisition:HumanMethylation27:01\tTCGA-A3-3306-01A-01D-0859-05\tjhu-usc.edu:feature_extraction:HumanMethylation27:01\tjhu-usc.edu:within_bioassay_data_set_function:HumanMethylation27:01\tTCGA-A3-3306-01A-01D-0859-05\tjhu-usc.edu_KIRP.HumanMethylation27.1.lvl-1.TCGA-A3-3306-01A-01D-0859-05.txt\tLevel 1\tDNA-Methylation\tyes\tjhu-usc.edu_KIRP.HumanMethylation27.Level_1.1.0.0\tjhu-usc.edu:within_bioassay_data_set_function:HumanMethylation27:01\tTCGA-A3-3306-01A-01D-0859-05\tjhu-usc.edu_KIRP.HumanMethylation27.1.lvl-2.txt\tLevel 2\tDNA-Methylation\tyes\tjhu-usc.edu_KIRP.HumanMethylation27.Level_2.1.0.0\tjhu-usc.edu:within_bioassay_data_set_function:HumanMethylation27:01\tTCGA-A3-3306-01A-01D-0859-05\tjhu-usc.edu_KIRP.HumanMethylation27.1.lvl-3.TCGA-A3-3306-01A-01D-0859-05.txt\tLevel 3\tDNA-Methylation\tyes\tjhu-usc.edu_KIRP.HumanMethylation27.Level_3.1.0.0";

		final File sdrfFile = new File(SAMPLES_DIR + File.separator + "qclive"
				+ File.separator + "sdrfRewriter" + File.separator
				+ "new_sdrf.txt");
		FileUtil.writeContentToFile(sdrfData, sdrfFile);
		return sdrfFile;
	}

	private File getSDRFFileForSameAliquotInMultipleDataMatrixFiles()
			throws IOException {
		final String sdrfData = "Extract Name\tProtocol REF\tLabeled Extract Name\tLabel\tTerm Source REF\tProtocol REF\tHybridization Name\tArray Design File\tTerm Source REF\tProtocol REF\tScan Name\tProtocol REF\tProtocol REF\tNormalization Name\tDerived Array Data Matrix File\tComment [TCGA Data Level]\tComment [TCGA Data Type]\tComment [TCGA Include for Analysis]\tComment [TCGA Archive Name]\tProtocol REF\tNormalization Name\tDerived Array Data Matrix File\tComment [TCGA Data Level]\tComment [TCGA Data Type]\tComment [TCGA Include for Analysis]\tComment [TCGA Archive Name]\tProtocol REF\tNormalization Name\tDerived Array Data Matrix File\tComment [TCGA Data Level]\tComment [TCGA Data Type]\tComment [TCGA Include for Analysis]\tComment [TCGA Archive Name]\n"
				+ "TCGA-07-0227-20A-01D-0859-05\tjhu-usc.edu:labeling:HumanMethylation27:01\tTCGA-07-0227-20A-01D-0859-05\tbiotin\tMGED Ontology\tjhu-usc.edu:hybridization:HumanMethylation27:01\tTCGA-07-0227-20A-01D-0859-05\tjhu-usc.edu_KIRP.HumanMethylation27.1.adf.txt\tcaArray\tjhu-usc.edu:image_acquisition:HumanMethylation27:01\tTCGA-07-0227-20A-01D-0859-05\tjhu-usc.edu:feature_extraction:HumanMethylation27:01\tjhu-usc.edu:within_bioassay_data_set_function:HumanMethylation27:01\tTCGA-07-0227-20A-01D-0859-05\tjhu-usc.edu_KIRP.HumanMethylation27.1.lvl-1.TCGA-07-0227-20A-01D-0859-05.txt\tLevel 1\tDNA-Methylation\tyes\tjhu-usc.edu_KIRP.HumanMethylation27.Level_1.1.0.0\tjhu-usc.edu:within_bioassay_data_set_function:HumanMethylation27:01\tTCGA-07-0227-20A-01D-0859-05\tjhu-usc.edu_KIRP.HumanMethylation27.1.lvl-2_Geno.txt\tLevel 2\tDNA-Methylation\tyes\tjhu-usc.edu_KIRP.HumanMethylation27.Level_2.1.0.0\tjhu-usc.edu:within_bioassay_data_set_function:HumanMethylation27:01\tTCGA-07-0227-20A-01D-0859-05\tjhu-usc.edu_KIRP.HumanMethylation27.1.lvl-3.TCGA-07-0227-20A-01D-0859-05.txt\tLevel 3\tDNA-Methylation\tyes\tjhu-usc.edu_KIRP.HumanMethylation27.Level_3.1.0.0\n"
				+ "TCGA-07-0227-20A-01D-0859-05\tjhu-usc.edu:labeling:HumanMethylation27:01\tTCGA-07-0227-20A-01D-0859-05\tbiotin\tMGED Ontology\tjhu-usc.edu:hybridization:HumanMethylation27:01\tTCGA-07-0227-20A-01D-0859-05\tjhu-usc.edu_KIRP.HumanMethylation27.1.adf.txt\tcaArray\tjhu-usc.edu:image_acquisition:HumanMethylation27:01\tTCGA-07-0227-20A-01D-0859-05\tjhu-usc.edu:feature_extraction:HumanMethylation27:01\tjhu-usc.edu:within_bioassay_data_set_function:HumanMethylation27:01\tTCGA-07-0227-20A-01D-0859-05\tjhu-usc.edu_KIRP.HumanMethylation27.1.lvl-1.TCGA-07-0227-20A-01D-0859-05.txt\tLevel 1\tDNA-Methylation\tyes\tjhu-usc.edu_KIRP.HumanMethylation27.Level_1.1.0.0\tjhu-usc.edu:within_bioassay_data_set_function:HumanMethylation27:01\tTCGA-07-0227-20A-01D-0859-05\tjhu-usc.edu_KIRP.HumanMethylation27.1.lvl-2_Paired.txt\tLevel 2\tDNA-Methylation\tyes\tjhu-usc.edu_KIRP.HumanMethylation27.Level_2.1.0.0\tjhu-usc.edu:within_bioassay_data_set_function:HumanMethylation27:01\tTCGA-07-0227-20A-01D-0859-05\tjhu-usc.edu_KIRP.HumanMethylation27.1.lvl-3.TCGA-07-0227-20A-01D-0859-05.txt\tLevel 3\tDNA-Methylation\tyes\tjhu-usc.edu_KIRP.HumanMethylation27.Level_3.1.0.0";

		final File sdrfFile = new File(SAMPLES_DIR + File.separator + "qclive"
				+ File.separator + "sdrfRewriter" + File.separator
				+ "new_sdrf.txt");
		FileUtil.writeContentToFile(sdrfData, sdrfFile);
		return sdrfFile;
	}

}
