package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParserImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ManifestValidator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * test file for datamatrixsplitter
 * 
 * @author Rohini Raman Last updated by: $Author$
 * @version $Rev$
 */

public class DataMatrixSplitterFastTest {

	private static final String SAMPLE_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;

	private String manifest = SAMPLE_DIR + ManifestValidator.MANIFEST_FILE;
	// file which contains multiple aliquots data with single constant column
	private static final String MULTIPLE_ALIQUOT = "multiple_aliquot";
	// file which contains multiple aliquots data with multiple constant column
	private static final String MULTIPLE_ALIQUOT_WITH_MULTIPLE_CONSTANT = "multiple_aliquot_with_multiple_constant";
	// file which contains aliquot data with multiple constant column
	private static final String ALIQUOT_WITH_MULTIPLE_CONSTANT = "aliquot_with_multiple_constant";
	// file which contains aliquot data with single constant column
	private static final String ALIQUOT_A6_2674 = "aliquot_a6_2674";
	// file which contains aliquot data with single constant column
	private static final String ALIQUOT_A6_2677 = "aliquot_a6_2677";
	// file which contains single aliquot data after split
	private static final String SPLIT_ALIQUOT_A6_2670 = "aliquot_a6_2670";
	// file which contains single aliquot data after split
	private static final String SPLIT_ALIQUOT_07_0227 = "aliquot_07_0227";
	// file which contains single aliquot data with multiple constant columns
	// after split
	private static final String SPLIT_ALIQUOT_B04_680888 = "aliquot_b04_680888";
	// file which contains single aliquot data with multiple constant columns
	// after split
	private static final String SPLIT_ALIQUOT_B05_680888 = "aliquot_b05_680888";

	private static final String SAME_ALIQUOT_MULTIPLE_FILE_1 = "same_aliquot_multiple_file_1";
	private static final String SAME_ALIQUOT_MULTIPLE_FILE_2 = "same_aliquot_multiple_file_2";
	private static final String SPLIT_ALIQUOT_TCGA_21_1081_GENOTYPES = "split_aliquot_tcga_21_1081_genotypes";
	private static final String SPLIT_ALIQUOT_TCGA_21_1083_GENOTYPES = "split_aliquot_tcga_21_1083_genotypes";
	private static final String SPLIT_ALIQUOT_TCGA_21_1081_ALLELE_FREQ = "split_aliquot_tcga_21_1081_allele_freq";
	private static final String SPLIT_ALIQUOT_TCGA_21_1083_ALLELE_FREQ = "split_aliquot_tcga_21_1083_allele_freq";

	private static Map<String, String> dataMatrixFileNames = new HashMap<String, String>();

	static {
		dataMatrixFileNames.put(MULTIPLE_ALIQUOT, SAMPLE_DIR
				+ "jhu-usc.edu_COAD.HumanMethylation27.4.lvl-2.txt");
		dataMatrixFileNames.put(MULTIPLE_ALIQUOT_WITH_MULTIPLE_CONSTANT,
				SAMPLE_DIR + "broadlvl-2.txt");
		dataMatrixFileNames.put(ALIQUOT_WITH_MULTIPLE_CONSTANT, SAMPLE_DIR
				+ "broad_cgc_lvl-2.txt");
		dataMatrixFileNames
				.put(ALIQUOT_A6_2674,
						SAMPLE_DIR
								+ "jhu-usc.edu_COAD.HumanMethylation27.4.lvl-2.TCGA-A6-2674-01A-02D-0820-05.txt");
		dataMatrixFileNames
				.put(ALIQUOT_A6_2677,
						SAMPLE_DIR
								+ "jhu-usc.edu_COAD.HumanMethylation27.4.lvl-2.TCGA-A6-2677-01A-01D-0820-05.txt");
		dataMatrixFileNames
				.put(SPLIT_ALIQUOT_A6_2670,
						SAMPLE_DIR
								+ "jhu-usc.edu_COAD.HumanMethylation27.4.lvl-2.TCGA-A6-2670-01A-02D-0820-05.txt");
		dataMatrixFileNames
				.put(SPLIT_ALIQUOT_07_0227,
						SAMPLE_DIR
								+ "jhu-usc.edu_COAD.HumanMethylation27.4.lvl-2.TCGA-07-0227-20A-01D-0820-05.txt");
		dataMatrixFileNames.put(SPLIT_ALIQUOT_B04_680888, SAMPLE_DIR
				+ "broadlvl-2.B04_680888.txt");
		dataMatrixFileNames.put(SPLIT_ALIQUOT_B05_680888, SAMPLE_DIR
				+ "broadlvl-2.B05_680888.txt");
		dataMatrixFileNames.put(SAME_ALIQUOT_MULTIPLE_FILE_1, SAMPLE_DIR
				+ "hudsonalpha.org_LUSC.Human1MDuo.1.0.0.Genotypes.txt");
		dataMatrixFileNames.put(SAME_ALIQUOT_MULTIPLE_FILE_2, SAMPLE_DIR
				+ "hudsonalpha.org_LUSC.Human1MDuo.1.0.0.B_Allele_Freq.txt");
		dataMatrixFileNames
				.put(SPLIT_ALIQUOT_TCGA_21_1081_GENOTYPES,
						SAMPLE_DIR
								+ "hudsonalpha.org_LUSC.Human1MDuo.1.0.0.Genotypes.TCGA-21-1081-01A-01D-0687-06.txt");
		dataMatrixFileNames
				.put(SPLIT_ALIQUOT_TCGA_21_1083_GENOTYPES,
						SAMPLE_DIR
								+ "hudsonalpha.org_LUSC.Human1MDuo.1.0.0.Genotypes.TCGA-21-1083-01A-01D-0687-06.txt");
		dataMatrixFileNames
				.put(SPLIT_ALIQUOT_TCGA_21_1081_ALLELE_FREQ,
						SAMPLE_DIR
								+ "hudsonalpha.org_LUSC.Human1MDuo.1.0.0.B_Allele_Freq.TCGA-21-1081-01A-01D-0687-06.txt");
		dataMatrixFileNames
				.put(SPLIT_ALIQUOT_TCGA_21_1083_ALLELE_FREQ,
						SAMPLE_DIR
								+ "hudsonalpha.org_LUSC.Human1MDuo.1.0.0.B_Allele_Freq.TCGA-21-1083-01A-01D-0687-06.txt");

	}

	private File manifestFile;

	private DataMatrixSplitter dataMatrixSplitter;
	private ManifestParser manifestParser;
	private Archive archive;

	@Before
	public void setup() throws IOException {
		archive = new Archive();
		archive.setRealName("testArchive");
		manifestParser = new ManifestParserImpl();
		manifestFile = createManifestFile();

	}

	@After
	public void cleanup() {
		manifestFile.delete();
		// delete aliquots file
		for (String aliquotDataMatrixFileName : dataMatrixFileNames.values()) {
			new File(aliquotDataMatrixFileName).delete();
		}
	}

	/**
	 * Test multiple aliquots file. Each aliquot data should be split into
	 * individual aliquot file
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMultipleAliquotsDataMatrixFile() throws Exception {
		archive.setArchiveType(Archive.TYPE_LEVEL_2);
		QcContext qcContext = new QcContext();
		final List<File> originalFileList = createMultipleAliquotsDataMatrixFiles();
		final DataMatrixSplitter dataMatrixSplitter = getDataMatrixSplitter(originalFileList);
		dataMatrixSplitter.doWork(archive, qcContext);

		// validate the new data matrix files
		if (qcContext.getErrorCount() > 0) {
			fail(StringUtil.convertListToDelimitedString(qcContext.getErrors(),
					'\n'));
		}

		final List<File> splitFileList = Arrays.asList(new File[] {
				new File(dataMatrixFileNames.get(SPLIT_ALIQUOT_07_0227)),
				new File(dataMatrixFileNames.get(SPLIT_ALIQUOT_A6_2670)) });

		// validate split aliquot files
		validateSplitFiles(splitFileList, originalFileList);

		// validate data
		assertTrue(
				"Error splitting data matrix file",
				getSPLIT_ALIQUOT_07_0227_Data().equals(
						FileUtil.readFile(splitFileList.get(0), false)));
		assertTrue(
				"Error splitting data matrix file",
				getSPLIT_ALIQUOT_A6_2670_Data().equals(
						FileUtil.readFile(splitFileList.get(1), false)));
		// validate manifest file
		validateManifest(splitFileList, originalFileList);

	}

	/**
	 * Test multiple aliquots file with multiple constant columns. Each aliquot
	 * data should be split into individual aliquot file
	 * 
	 * @throws Exception
	 */

	@Test
	public void testMultipleAliquotsWithMultipleConstantColumnsDataMatrixFile()
			throws Exception {
		archive.setArchiveType(Archive.TYPE_LEVEL_2);
		QcContext qcContext = new QcContext();
		final List<File> originalFileList = createMultipleAliquotsWithMultipleConstantColumnsDataMatrixFiles();
		final DataMatrixSplitter dataMatrixSplitter = getDataMatrixSplitter(originalFileList);
		dataMatrixSplitter.doWork(archive, qcContext);

		// validate the new data matrix files
		if (qcContext.getErrorCount() > 0) {
			fail(StringUtil.convertListToDelimitedString(qcContext.getErrors(),
					'\n'));
		}
		final List<File> splitFileList = Arrays.asList(new File[] {
				new File(dataMatrixFileNames.get(SPLIT_ALIQUOT_B04_680888)),
				new File(dataMatrixFileNames.get(SPLIT_ALIQUOT_B05_680888)) });
		validateSplitFiles(splitFileList, originalFileList);
		// validate data
		assertTrue(
				"Error splitting data matrix file",
				getSPLIT_ALIQUOT_B04_680888_Data().equals(
						FileUtil.readFile(splitFileList.get(0), false)));
		assertTrue(
				"Error splitting data matrix file",
				getSPLIT_ALIQUOT_B05_680888_Data().equals(
						FileUtil.readFile(splitFileList.get(1), false)));

		// validate manifest file
		validateManifest(splitFileList, originalFileList);
	}

	/**
	 * Test single aliquots file.
	 * 
	 * @throws Exception
	 */

	@Test
	public void testSingleAliquotDataMatrixFile() throws Exception {
		archive.setArchiveType(Archive.TYPE_LEVEL_2);
		QcContext qcContext = new QcContext();

		final DataMatrixSplitter dataMatrixSplitter = getDataMatrixSplitter(createSingleAliquotDataMatrixFiles());
		dataMatrixSplitter.doWork(archive, qcContext);

		if (qcContext.getErrorCount() > 0) {
			fail(StringUtil.convertListToDelimitedString(qcContext.getErrors(),
					'\n'));
		}
		assertTrue("Data Matrix File doesn't exist", new File(
				dataMatrixFileNames.get(ALIQUOT_A6_2674)).exists());
		assertTrue("Data Matrix File doesn't exist", new File(
				dataMatrixFileNames.get(ALIQUOT_A6_2677)).exists());

		// validate manifest file
		final String updatedManifestData = FileUtil
				.readFile(manifestFile, true);

		assertTrue("Error updating Manifest File",
				updatedManifestData.contains(getFileName(dataMatrixFileNames
						.get(ALIQUOT_A6_2674))));
		assertTrue("Error updating Manifest File",
				updatedManifestData.contains(getFileName(dataMatrixFileNames
						.get(ALIQUOT_A6_2677))));

	}

	/**
	 * Test single aliquots file with multiple columns.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSingleAliquotWithMultipleConstantColumnsDataMatrixFile()
			throws Exception {
		archive.setArchiveType(Archive.TYPE_LEVEL_2);
		QcContext qcContext = new QcContext();

		final DataMatrixSplitter dataMatrixSplitter = getDataMatrixSplitter(createSingleALiquotWithMultipleConstantColumnsDataMatrixFiles());
		dataMatrixSplitter.doWork(archive, qcContext);

		if (qcContext.getErrorCount() > 0) {
			fail(StringUtil.convertListToDelimitedString(qcContext.getErrors(),
					'\n'));
		}
		assertTrue(
				"Data Matrix File doesn't exist",
				new File(dataMatrixFileNames
						.get(ALIQUOT_WITH_MULTIPLE_CONSTANT)).exists());

		// validate manifest file
		final String updatedManifestData = FileUtil
				.readFile(manifestFile, true);
		assertTrue("Error updating Manifest File",
				updatedManifestData.contains(getFileName(dataMatrixFileNames
						.get(ALIQUOT_WITH_MULTIPLE_CONSTANT))));
	}

	@Test
	public void testSameAliquotInMultipleFiles() throws Exception {
		archive.setArchiveType(Archive.TYPE_LEVEL_2);
		QcContext qcContext = new QcContext();
		final List<File> originalFileList = createSameAliquotInMultipleDataMatrixFiles();
		final DataMatrixSplitter dataMatrixSplitter = getDataMatrixSplitter(originalFileList);
		dataMatrixSplitter.doWork(archive, qcContext);

		if (qcContext.getErrorCount() > 0) {
			fail(StringUtil.convertListToDelimitedString(qcContext.getErrors(),
					'\n'));
		}

		final List<File> splitFileList = Arrays.asList(new File[] {
				new File(dataMatrixFileNames
						.get(SPLIT_ALIQUOT_TCGA_21_1081_GENOTYPES)),
				new File(dataMatrixFileNames
						.get(SPLIT_ALIQUOT_TCGA_21_1083_GENOTYPES)),
				new File(dataMatrixFileNames
						.get(SPLIT_ALIQUOT_TCGA_21_1081_ALLELE_FREQ)),
				new File(dataMatrixFileNames
						.get(SPLIT_ALIQUOT_TCGA_21_1083_ALLELE_FREQ)) });

		// validate multiple constant aliquot files
		validateSplitFiles(splitFileList, originalFileList);

		// validate data
		final List<String> expectedBarcodes = Arrays
				.asList(new String[] { "TCGA-21-1081-01A-01D-0687-06",
						"TCGA-21-1083-01A-01D-0687-06" });
		final List<String> expectedOriginalFileNames = Arrays
				.asList(new String[] {
						"hudsonalpha.org_LUSC.Human1MDuo.1.0.0.Genotypes.txt",
						"hudsonalpha.org_LUSC.Human1MDuo.1.0.0.B_Allele_Freq.txt" });

		final Map<String, Map<String, File>> splitDataMatrixFilesByOriginalDataMatrixFileByBarcode = qcContext
				.getSplitDataMatrixFiles();
		assertTrue(expectedBarcodes
				.containsAll(splitDataMatrixFilesByOriginalDataMatrixFileByBarcode
						.keySet()));
		assertTrue(expectedOriginalFileNames
				.containsAll(splitDataMatrixFilesByOriginalDataMatrixFileByBarcode
						.get("TCGA-21-1081-01A-01D-0687-06").keySet()));
		assertTrue(expectedOriginalFileNames
				.containsAll(splitDataMatrixFilesByOriginalDataMatrixFileByBarcode
						.get("TCGA-21-1083-01A-01D-0687-06").keySet()));
		assertEquals(
				"hudsonalpha.org_LUSC.Human1MDuo.1.0.0.Genotypes.TCGA-21-1081-01A-01D-0687-06.txt",
				splitDataMatrixFilesByOriginalDataMatrixFileByBarcode
						.get("TCGA-21-1081-01A-01D-0687-06")
						.get("hudsonalpha.org_LUSC.Human1MDuo.1.0.0.Genotypes.txt")
						.getName());
		assertEquals(
				"hudsonalpha.org_LUSC.Human1MDuo.1.0.0.B_Allele_Freq.TCGA-21-1081-01A-01D-0687-06.txt",
				splitDataMatrixFilesByOriginalDataMatrixFileByBarcode
						.get("TCGA-21-1081-01A-01D-0687-06")
						.get("hudsonalpha.org_LUSC.Human1MDuo.1.0.0.B_Allele_Freq.txt")
						.getName());
		assertEquals(
				"hudsonalpha.org_LUSC.Human1MDuo.1.0.0.Genotypes.TCGA-21-1083-01A-01D-0687-06.txt",
				splitDataMatrixFilesByOriginalDataMatrixFileByBarcode
						.get("TCGA-21-1083-01A-01D-0687-06")
						.get("hudsonalpha.org_LUSC.Human1MDuo.1.0.0.Genotypes.txt")
						.getName());
		assertEquals(
				"hudsonalpha.org_LUSC.Human1MDuo.1.0.0.B_Allele_Freq.TCGA-21-1083-01A-01D-0687-06.txt",
				splitDataMatrixFilesByOriginalDataMatrixFileByBarcode
						.get("TCGA-21-1083-01A-01D-0687-06")
						.get("hudsonalpha.org_LUSC.Human1MDuo.1.0.0.B_Allele_Freq.txt")
						.getName());

		assertTrue(
				"Error splitting data matrix file",
				getSPLIT_ALIQUOT_TCGA_21_1081_GENOTYPES_Data().equals(
						FileUtil.readFile(splitFileList.get(0), false)));
		assertTrue(
				"Error splitting data matrix file",
				getSPLIT_ALIQUOT_TCGA_21_1083_GENOTYPES_Data().equals(
						FileUtil.readFile(splitFileList.get(1), false)));
		assertTrue(
				"Error splitting data matrix file",
				getSPLIT_ALIQUOT_TCGA_21_1081_ALLELE_FREQ_Data().equals(
						FileUtil.readFile(splitFileList.get(2), false)));
		assertTrue(
				"Error splitting data matrix file",
				getSPLIT_ALIQUOT_TCGA_21_1083_ALLELE_FREQ_Data().equals(
						FileUtil.readFile(splitFileList.get(3), false)));

		// validate manifest file
		validateManifest(splitFileList, originalFileList);
	}

	private void validateSplitFiles(final List<File> splitFiles,
			final List<File> originalFiles) {

		for (final File splitFile : splitFiles) {
			assertTrue("Error creating Data Matrix File", splitFile.exists());
		}

		for (final File originalFile : originalFiles) {
			assertFalse("Error deleting Data Matrix File",
					originalFile.exists());
		}
	}

	private void validateManifest(final List<File> splitFiles,
			final List<File> originalFiles) throws IOException {
		final String updatedManifestData = FileUtil
				.readFile(manifestFile, true);
		for (final File splitFile : splitFiles) {
			assertTrue("Error updating Manifest File",
					updatedManifestData.contains(splitFile.getName()));
		}

		for (final File originalFile : originalFiles) {
			assertFalse("Error updating Manifest File",
					updatedManifestData.contains(originalFile.getName()));
		}
	}

	private DataMatrixSplitter getDataMatrixSplitter(
			final List<File> dataMatrixFiles) {
		DataMatrixSplitter dataMatrixSplitter = new DataMatrixSplitter() {

			protected List<File> getDataMatrixFiles(final Archive archive) {
				return dataMatrixFiles;
			}

			protected File getArchiveManifestFile(final Archive archive) {
				return manifestFile;
			}

		};
		dataMatrixSplitter.setManifestParser(manifestParser);
		return dataMatrixSplitter;
	}

	private List<File> createMultipleAliquotsDataMatrixFiles()
			throws IOException {
		List<File> dataMatrixFiles = new ArrayList<File>();
		dataMatrixFiles
				.add(createMultipleAliquotDataMatrixFile(dataMatrixFileNames
						.get(MULTIPLE_ALIQUOT)));
		return dataMatrixFiles;
	}

	private List<File> createMultipleAliquotsWithMultipleConstantColumnsDataMatrixFiles()
			throws IOException {
		List<File> dataMatrixFiles = new ArrayList<File>();
		dataMatrixFiles
				.add(createMultipleAliquotWithMultipleConstantColumnsDataMatrixFile(dataMatrixFileNames
						.get(MULTIPLE_ALIQUOT_WITH_MULTIPLE_CONSTANT)));
		return dataMatrixFiles;
	}

	private List<File> createSingleAliquotDataMatrixFiles() throws IOException {
		List<File> dataMatrixFiles = new ArrayList<File>();
		dataMatrixFiles.add(createAliquotDataMatrixFile(
				dataMatrixFileNames.get(ALIQUOT_A6_2674),
				"TCGA-A6-2674-01A-02D-0820-05"));
		dataMatrixFiles.add(createAliquotDataMatrixFile(
				dataMatrixFileNames.get(ALIQUOT_A6_2677),
				"TCGA-A6-2677-01A-01D-0820-05"));
		return dataMatrixFiles;
	}

	private List<File> createSingleALiquotWithMultipleConstantColumnsDataMatrixFiles()
			throws IOException {
		List<File> dataMatrixFiles = new ArrayList<File>();
		dataMatrixFiles
				.add(createAliquotWithMultipleConstantColumnsDataMatrixFile(dataMatrixFileNames
						.get(ALIQUOT_WITH_MULTIPLE_CONSTANT)));
		return dataMatrixFiles;
	}

	private List<File> createSameAliquotInMultipleDataMatrixFiles()
			throws IOException {
		List<File> dataMatrixFiles = new ArrayList<File>();
		dataMatrixFiles.add(createGenotypesFile(dataMatrixFileNames
				.get(SAME_ALIQUOT_MULTIPLE_FILE_1)));
		dataMatrixFiles.add(createAllele_FreqFile(dataMatrixFileNames
				.get(SAME_ALIQUOT_MULTIPLE_FILE_2)));
		return dataMatrixFiles;
	}

	private File createGenotypesFile(final String dataMatrixFileName)
			throws IOException {
		final File dataMatrixFile = new File(dataMatrixFileName);
		final String data = "Hybridization REF\t\t\tTCGA-21-1081-01A-01D-0687-06\tTCGA-21-1083-01A-01D-0687-06\n"
				+ "Composite Element REF\tChr\tPos\tgenotype\tgenotype\n"
				+ "rs12354060\t1\t10004\tBB\tBB\n"
				+ "rs12354061\t1\t10004\tBB\tBB";
		FileUtil.writeContentToFile(data, dataMatrixFile);
		return dataMatrixFile;
	}

	private File createAllele_FreqFile(final String dataMatrixFileName)
			throws IOException {
		final File dataMatrixFile = new File(dataMatrixFileName);
		final String data = "Hybridization REF\t\t\tTCGA-21-1081-01A-01D-0687-06\tTCGA-21-1083-01A-01D-0687-06\n"
				+ "Composite Element REF\tChr\tPos\tB Allele Freq\tB Allele Freq\n"
				+ "rs12354060\t1\t10004\t0.9949013\t0.9949013\n"
				+ "rs12354061\t1\t10004\t0.9949013\t0.9949013";
		FileUtil.writeContentToFile(data, dataMatrixFile);
		return dataMatrixFile;
	}

	private File createMultipleAliquotDataMatrixFile(
			final String dataMatrixFileName) throws IOException {
		final File dataMatrixFile = new File(dataMatrixFileName);
		final String data = "Hybridization REF\tTCGA-07-0227-20A-01D-0820-05\tTCGA-07-0227-20A-01D-0820-05\tTCGA-07-0227-20A-01D-0820-05\tTCGA-A6-2670-01A-02D-0820-05\tTCGA-A6-2670-01A-02D-0820-05\tTCGA-A6-2670-01A-02D-0820-05\n"
				+ "Composite Element REF\tBeta_Value\tMethylated_Signal_Intensity (M)\tUn-Methylated_Signal_Intensity (U)\tBeta_Value\tMethylated_Signal_Intensity (M)\tUn-Methylated_Signal_Intensity (U)\n"
				+ "cg00000292\t0.261571344339623\t3549\t10019\t0.261571344339623222\t3549222\t10019222\n"
				+ "cg00002426\t0.928198433420366\t8532\t660\t0.928198433420366333\t8532333\t660333\n"
				+ "cg00003994\t0.088645299654474\t744\t7649\t0.088645299654474444\t744444\t7649444";
		FileUtil.writeContentToFile(data, dataMatrixFile);
		return dataMatrixFile;
	}

	private File createMultipleAliquotWithMultipleConstantColumnsDataMatrixFile(
			final String dataMatrixFileName) throws IOException {
		final File dataMatrixFile = new File(dataMatrixFileName);

		final String data = "Hybridization REF\t\t\tB04_680888\tB05_680888\n"
				+ "Composite Element REF\tChromosome\tPhysicalPosition\tBeta_Value\tBeta_Value\n"
				+ "cg00000292\t51598\t4.433\t0.261571344339623\t0.261571344339623\n"
				+ "cg00002426\t51597\t4.433\t0.928198433420366\t0.928198433420366\n"
				+ "cg00003994\t51596\t4.433\t0.088645299654474\t0.088645299654474";
		FileUtil.writeContentToFile(data, dataMatrixFile);
		return dataMatrixFile;
	}

	private File createAliquotWithMultipleConstantColumnsDataMatrixFile(
			final String dataMatrixFileName) throws IOException {
		final File dataMatrixFile = new File(dataMatrixFileName);

		final String data = "Hybridization REF\t\t\tB06_680888\n"
				+ "Composite Element REF\tChromosome\tPhysicalPosition\tBeta_Value\n"
				+ "cg00000292\t51598\t4.433\t0.261571344339623\n"
				+ "cg00002426\t51597\t4.433\t0.928198433420366\n"
				+ "cg00003994\t51596\t4.433\t0.088645299654474";
		FileUtil.writeContentToFile(data, dataMatrixFile);
		return dataMatrixFile;
	}

	private File createAliquotDataMatrixFile(final String dataMatrixFileName,
			final String aliquot) throws IOException {
		final File dataMatrixFile = new File(dataMatrixFileName);
		final String data = "Hybridization REF\t"
				+ aliquot
				+ "\t"
				+ aliquot
				+ "\t"
				+ aliquot
				+ "\n"
				+ "Composite Element REF\tBeta_Value\tMethylated_Signal_Intensity (M)\tUn-Methylated_Signal_Intensity (U)\t\n"
				+ "cg00000292\t0.261571344339623\t3549\t10019\n"
				+ "cg00002426\t0.928198433420366\t8532\t660\n"
				+ "cg00003994\t0.088645299654474\t744\t7649";
		FileUtil.writeContentToFile(data, dataMatrixFile);
		return dataMatrixFile;
	}

	private String getSPLIT_ALIQUOT_07_0227_Data() {
		return "Hybridization REF\tTCGA-07-0227-20A-01D-0820-05\tTCGA-07-0227-20A-01D-0820-05\tTCGA-07-0227-20A-01D-0820-05\n"
				+ "Composite Element REF\tBeta_Value\tMethylated_Signal_Intensity (M)\tUn-Methylated_Signal_Intensity (U)\n"
				+ "cg00000292\t0.261571344339623\t3549\t10019\n"
				+ "cg00002426\t0.928198433420366\t8532\t660\n"
				+ "cg00003994\t0.088645299654474\t744\t7649";
	}

	private String getSPLIT_ALIQUOT_A6_2670_Data() {
		return "Hybridization REF\tTCGA-A6-2670-01A-02D-0820-05\tTCGA-A6-2670-01A-02D-0820-05\tTCGA-A6-2670-01A-02D-0820-05\n"
				+ "Composite Element REF\tBeta_Value\tMethylated_Signal_Intensity (M)\tUn-Methylated_Signal_Intensity (U)\n"
				+ "cg00000292\t0.261571344339623222\t3549222\t10019222\n"
				+ "cg00002426\t0.928198433420366333\t8532333\t660333\n"
				+ "cg00003994\t0.088645299654474444\t744444\t7649444";
	}

	private String getSPLIT_ALIQUOT_B04_680888_Data() {
		return "Hybridization REF\t\t\tB04_680888\n"
				+ "Composite Element REF\tChromosome\tPhysicalPosition\tBeta_Value\n"
				+ "cg00000292\t51598\t4.433\t0.261571344339623\n"
				+ "cg00002426\t51597\t4.433\t0.928198433420366\n"
				+ "cg00003994\t51596\t4.433\t0.088645299654474";
	}

	private String getSPLIT_ALIQUOT_B05_680888_Data() {
		return "Hybridization REF\t\t\tB05_680888\n"
				+ "Composite Element REF\tChromosome\tPhysicalPosition\tBeta_Value\n"
				+ "cg00000292\t51598\t4.433\t0.261571344339623\n"
				+ "cg00002426\t51597\t4.433\t0.928198433420366\n"
				+ "cg00003994\t51596\t4.433\t0.088645299654474";
	}

	private String getSPLIT_ALIQUOT_TCGA_21_1081_GENOTYPES_Data() {
		return "Hybridization REF\t\t\tTCGA-21-1081-01A-01D-0687-06\n"
				+ "Composite Element REF\tChr\tPos\tgenotype\n"
				+ "rs12354060\t1\t10004\tBB\n" + "rs12354061\t1\t10004\tBB";
	}

	private String getSPLIT_ALIQUOT_TCGA_21_1083_GENOTYPES_Data() {
		return "Hybridization REF\t\t\tTCGA-21-1083-01A-01D-0687-06\n"
				+ "Composite Element REF\tChr\tPos\tgenotype\n"
				+ "rs12354060\t1\t10004\tBB\n" + "rs12354061\t1\t10004\tBB";
	}

	private String getSPLIT_ALIQUOT_TCGA_21_1081_ALLELE_FREQ_Data() {
		return "Hybridization REF\t\t\tTCGA-21-1081-01A-01D-0687-06\n"
				+ "Composite Element REF\tChr\tPos\tB Allele Freq\n"
				+ "rs12354060\t1\t10004\t0.9949013\n"
				+ "rs12354061\t1\t10004\t0.9949013";
	}

	private String getSPLIT_ALIQUOT_TCGA_21_1083_ALLELE_FREQ_Data() {
		return "Hybridization REF\t\t\tTCGA-21-1083-01A-01D-0687-06\n"
				+ "Composite Element REF\tChr\tPos\tB Allele Freq\n"
				+ "rs12354060\t1\t10004\t0.9949013\n"
				+ "rs12354061\t1\t10004\t0.9949013";
	}

	private File createManifestFile() throws IOException {
		final File manifestFile = new File(manifest);
		final String data = "bd1c5eb29cb76508c8afddfbc6f8bf04 DESCRIPTION.txt\n"
				+ "bd1c5eb29cb76508c8afddfbc6f8bf04 jhu-usc.edu_COAD.HumanMethylation27.4.lvl-2.TCGA-A6-2674-01A-02D-0820-05.txt\n"
				+ "bd1c5eb29cb76508c8afddfbc6f8bf04 jhu-usc.edu_COAD.HumanMethylation27.4.lvl-2.TCGA-A6-2677-01A-01D-0820-05.txt\n"
				+ "bd1c5eb29cb76508c8afddfbc6f8bf04 jhu-usc.edu_COAD.HumanMethylation27.4.lvl-2.txt\n"
				+ "bd1c5eb29cb76508c8afddfbc6f8bf04 broadlvl-2.txt\n"
				+ "bd1c5eb29cb76508c8afddfbc6f8bf04 broad_cgc_lvl-2.txt\n"
				+ "bd1c5eb29cb76508c8afddfbc6f8bf04 hudsonalpha.org_LUSC.Human1MDuo.1.0.0.Genotypes.tx\n"
				+ "bd1c5eb29cb76508c8afddfbc6f8bf04 hudsonalpha.org_LUSC.Human1MDuo.1.0.0.B_Allele_Freq.txt";
		FileUtil.writeContentToFile(data, manifestFile);
		return manifestFile;

	}

	private String getFileName(final String filePath) {
		return new File(filePath).getName();
	}
}
