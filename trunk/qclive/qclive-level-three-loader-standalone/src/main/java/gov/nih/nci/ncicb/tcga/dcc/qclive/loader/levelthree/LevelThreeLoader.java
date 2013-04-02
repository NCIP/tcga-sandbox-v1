/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.loader.levelthree;

import static gov.nih.nci.ncicb.tcga.dcc.ConstantValues.EXON_QUANTIFICATION_SOURCE_FILE_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.ConstantValues.GENE_QUANTIFICATION_RSEM_GENE_NORMALIZED_SOURCE_FILE_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.ConstantValues.GENE_QUANTIFICATION_RSEM_GENE_SOURCE_FILE_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.ConstantValues.GENE_QUANTIFICATION_RSEM_ISOFORMS_NORMALIZED_SOURCE_FILE_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.ConstantValues.GENE_QUANTIFICATION_RSEM_ISOFORMS_SOURCE_FILE_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.ConstantValues.GENE_QUANTIFICATION_SOURCE_FILE_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.ConstantValues.ISOFORM_QUANTIFICATION_SOURCE_FILE_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.ConstantValues.JUNCTION_QUANTIFICATION_SOURCE_FILE_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.ConstantValues.MIRNA_QUANTIFICATION_SOURCE_FILE_TYPE;
import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDDAO;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContent;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContentImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.LoaderArchive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcLiveStateBean;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedContentNavigator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedFileParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.ArchiveLoader;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc.LevelThreeQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.DummyFileTypeLookup;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.LoaderException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.clinical.ClinicalLoaderException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.levelthree.record.CnaValue;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.levelthree.record.file.segment.BroadMitEduSegmentRecord;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.levelthree.record.file.segment.GenomeWustlEduSegmentRecord;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.levelthree.record.file.segment.HmsHarvardEduSegmentRecord;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.levelthree.record.file.segment.HudsonAlphaOrgSegmentRecord;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.levelthree.record.file.segment.MskccOrgSegmentRecord;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.levelthree.record.file.segment.SegmentRecord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LevelThree loader is used to load level three archives that were processed by
 * QCLIve.
 * 
 * @author Stanley Girshik
 * 
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class LevelThreeLoader implements ArchiveLoader {

	private static final Logger logger = LoggerFactory.getLogger(LevelThreeLoader.class);
	
	private List<CenterPlatformPattern> patterns = new ArrayList<CenterPlatformPattern>();
	private List<String> excludedFiles = new ArrayList<String>();
	private ArchiveQueries archiveQueries = null;
	private LevelThreeQueries levelThreeQueries = null;
	private LevelThreeQueries commonLevelThreeQueries = null;
	private CommonBarcodeAndUUIDValidator commonBarcodeAndUUIDValidator;
	private UUIDDAO uuiddao;
	private String patternFile;
	private Integer batchSize;
	
	private static final Integer DEFAULT_BATCH_SIZE = 1000;
	private static final String EXTRACT_NAME = "Extract Name";
	
	// centers
	private static final String BCGSC_CA_CENTER = "bcgsc.ca";
	private static final String BROAD_MIT_CENTER = "broad.mit.edu";
	private static final String HARVARD_CENTER = "hms.harvard.edu";
	private static final String HUDSON_ALPHA_CENTER = "hudsonalpha.org";
	private static final String JHU_CENTER = "jhu-usc.edu";
	private static final String LBL_CENTER = "lbl.gov";
	private static final String MSKCC_CENTER = "mskcc.org";
	private static final String UNC_CENTER = "unc.edu";
	private static final String WASHU_CENTER = "genome.wustl.edu";
	
	// platforms
	private static final String SNP = "Genome_Wide_SNP_6";
	private static final String BROAD_HT_HG = "HT_HG-U133A";
	private static final String HUMAN_METHYLATION_27 = "HumanMethylation27";
	private static final String HUMAN_METHYLATION_450 = "HumanMethylation450";
	private static final String UNC_MIRNA_EXP = "H-miRNA_8x15K";
	private static final String UNC_MIRNA_EXP2 = "H-miRNA_8x15Kv2";
	private static final String UNC_AGILENTG450_EXP = "AgilentG4502A_07_1";
	private static final String UNC_AGILENTG450_EXP2 = "AgilentG4502A_07_2";
	private static final String UNC_AGILENTG450_EXP3 = "AgilentG4502A_07_3";
	private static final String ILLUMINA_GA_MI_RNASEQ_PLATFORM = "IlluminaGA_miRNASeq";
	private static final String ILLUMINA_GA_RNASEQ_PLATFORM = "IlluminaGA_RNASeq";
	private static final String ILLUMINA_GA_RNASEQV2_PLATFORM = "IlluminaGA_RNASeqV2";
	private static final String ILLUMINA_HISEQ_RNASEQ_PLATFORM = "IlluminaHiSeq_RNASeq";
	private static final String ILLUMINA_HISEQ_RNASEQV2_PLATFORM = "IlluminaHiSeq_RNASeqV2";
	private static final String ILLUMINA_HISEQ_MI_RNASEQ_PLATFORM = "IlluminaHiSeq_miRNASeq";
	private static final String ILLUMINAHISEQ_DNASEQC_PLATFORM = "IlluminaHiSeq_DNASeqC";
	private static final String HUEX_1_0_STV2 = "HuEx-1_0-st-v2";
	private static final String MDA_RPPA_CORE_PLATFORM = "MDA_RPPA_Core";

	// Patterns
	private final static String BARCODE_PATTERN = "TCGA\\-\\w\\w\\-\\w\\w\\w\\w\\-\\d\\d\\w\\-\\d\\d\\w\\-\\w\\w\\w\\w\\-\\d\\d";
	private static final String MIRNA_QUANTIFICATION_PATTERN = "*.mirna.quantification.txt";
	private static final String ISOFORM_QUANTIFICATION_PATTERN = "*.isoform.quantification.txt";
	private static final String PROTEIN_EXPRESSION_PATTERN = "*protein_expression*.txt";

	// Patterns - RNASeq
	private static final String EXON_QUANTIFICATION_PATTERN = "*.exon.quantification.txt";
	private static final String JUNCTION_QUANTIFICATION_PATTERN = "*.spljxn.quantification.txt";
	private static final String GENE_QUANTIFICATION_PATTERN = "*.gene.quantification.txt";

	// File extensions - RNASeq V2
	private static final String GENE_QUANTIFICATION_RSEM_GENE_FILE_EXTENSION = ".rsem.genes.results";
	private static final String GENE_QUANTIFICATION_RSEM_GENE_NORMALIZED_FILE_EXTENSION = ".rsem.genes.normalized_results";
	private static final String GENE_QUANTIFICATION_RSEM_ISOFORMS_FILE_EXTENSION = ".rsem.isoforms.results";
	private static final String GENE_QUANTIFICATION_RSEM_ISOFORMS_NORMALIZED_FILE_EXTENSION = ".rsem.isoforms.normalized_results";

	// Patterns - RNASeq (V2)
	private static final String JUNCTION_QUANTIFICATION_V2_PATTERN = "*.junction_quantification.txt";
	private static final String EXON_QUANTIFICATION_V2_PATTERN = "*.exon_quantification.txt";
	private static final String GENE_QUANTIFICATION_RSEM_GENE_PATTERN = "*"
	        + GENE_QUANTIFICATION_RSEM_GENE_FILE_EXTENSION;
	private static final String GENE_QUANTIFICATION_RSEM_GENE_NORMALIZED_PATTERN = "*"
	        + GENE_QUANTIFICATION_RSEM_GENE_NORMALIZED_FILE_EXTENSION;
	private static final String GENE_QUANTIFICATION_RSEM_ISOFORMS_PATTERN = "*"
	        + GENE_QUANTIFICATION_RSEM_ISOFORMS_FILE_EXTENSION;
	private static final String GENE_QUANTIFICATION_RSEM_ISOFORMS_NORMALIZED_PATTERN = "*"
	        + GENE_QUANTIFICATION_RSEM_ISOFORMS_NORMALIZED_FILE_EXTENSION;

	// barcode separator
	private static final String BARCODE_SEPARATOR = "-";
	private static final String SEGMENT_RECORD_DELIMITER = "\t"; 
	private static final String ACCESS_LEVEL_PUBLIC = "PUBLIC";
	private static final Integer LOADER_LEVEL_THREE = 3;
	private static final Integer LOAD_INCOMPLETE = 0;

	private static final int BETA_FILE_TOKENS = 5;
	private static final int SEG_TSV_TOKENS = 5;

	private static final String COMPOSITE_ELEMENT_REF = "Composite Element REF";
	private static final String GENE_NAME = "Gene Name";
	
	private static final String fileToDbLoadInfoPlaceholder = 
			"\n\n\tLoading file into database\n\t\tFile: %s\n\t\tDatabase table: %s\n";

	@Override
	public ArchiveLoaderType getLoaderType() {
		return ArchiveLoaderType.LEVEL_THREE_LOADER;
	}

	/**
	 * Loads L3 archives by name. First, will parse out disease name from the
	 * archive name and then look up archive deploy location in archive_info
	 * table for that disease.
	 * 
	 * @param archiveName
	 *            name of archive to load
	 * @throws LoaderException
	 *             if there is an error loading an archive by name
	 */
	public void loadArchiveByName(final String archiveName) throws LoaderException {
		Long archiveId = getArchiveQueries().getArchiveIdByName(archiveName);
		if (archiveId == -1) {
			throw new LoaderException("Archive with name '" + archiveName + "' not found -- skipping");
		}
		else {
			logger.info("\n\n\tLoading Level 3 archive\n\t\tName: " + archiveName + "\n\t\tId: " + archiveId + "\n");
			loadArchiveById(archiveId);
		}
	}

	/**
	 * Loads archives by ID.
	 * 
	 * @param archiveId
	 *            id for an archive to load
	 * @throws LoaderException
	 *             if there is an error loading an archive by id
	 */
	private void loadArchiveById(final Long archiveId) throws LoaderException {
		final Archive archive = getArchiveQueries().getArchive(archiveId.intValue());
		DiseaseContextHolder.setDisease(archive.getTheTumor().getTumorName());

		List<FileInfo> archiveFile = getArchiveQueries().getFilesForArchive(archiveId);
		// create a map for easy lookups
		Map<String, Long> archiveFileMap = new HashMap<String, Long>();
		for (FileInfo file : archiveFile) {
			if (!excludedFiles.contains(file.getFileName())) {
				archiveFileMap.put(file.getFileName(), file.getId());
			}
		}
		
		// get magetab archive dir
		final String magetabArchiveDir = getMagetabDir(archive);
		
		// get all files for the archive
		TabDelimitedContentNavigator sdrfNavigator = loadSDRF(magetabArchiveDir);

		parseDataSet(archive, archiveFileMap, sdrfNavigator, magetabArchiveDir);

		// update archive_info for both disease and common databses with a
		// timestamp when an archive is finished loading
		getLevelThreeQueries().updateArchiveLoadedDate(archiveId);
		
		getArchiveQueries().updateArchiveInfo(archiveId);
	}

	/**
	 * Loads SDRF file for an archive.
	 * 
	 * @param magetabDirectory
	 *            magetab archive dir
	 * @return LoaderSDRF object with SDRF data populated
	 * @throws LoaderException
	 *             if there is an error loading an SRF file
	 */
	public TabDelimitedContentNavigator loadSDRF(final String magetabDirectory) throws LoaderException {
		String loaderErrorMessage = null;
		LoaderArchive magetabArchive = null;

		if (magetabDirectory != null && magetabDirectory.length() > 0) {
			magetabArchive = new LoaderArchive(magetabDirectory, new DummyFileTypeLookup());
		}
		else {
			throw new LoaderException("Invalid magetabDir " + magetabDirectory);
		}

		// parse the SDRF
		TabDelimitedContent sdrf = new TabDelimitedContentImpl();
		TabDelimitedFileParser sdrfParser = new TabDelimitedFileParser();
		sdrfParser.setTabDelimitedContent(sdrf);
		try {
			sdrfParser.loadTabDelimitedContent(magetabArchive.getSDRFFile(), true);
		}
		catch (IOException ioe) {
			loaderErrorMessage = new StringBuilder()
			.append("Failed to parse SDRF '")
			.append(magetabArchive.getSDRFFile())
			.append("' (")
			.append(ioe.getMessage())
			.append(")")
			.toString();

			throw new LoaderException(loaderErrorMessage, ioe);
		}
		catch (ParseException pe) {
			loaderErrorMessage = new StringBuilder()
			.append("Failed to parse SDRF ")
			.append(magetabArchive.getSDRFFile())
			.toString();

			throw new LoaderException(loaderErrorMessage, pe);
		}

		sdrfParser.loadTabDelimitedContentHeader();
		TabDelimitedContentNavigator sdrfNavigator = new TabDelimitedContentNavigator();
		sdrfNavigator.setTabDelimitedContent(sdrf);

		return sdrfNavigator;
	}

	/**
	 * Creates an in instance of {@link ExperimentCenterBean} for the provided
	 * archive.
	 * <p/>
	 * This method will create a new experiment record in the database if no
	 * existing experiment record is found for an archive.
	 * 
	 * @param archive
	 *            - and instance of {@link Archive}
	 * @return a combination of experiment id and center id
	 */
	public ExperimentCenterBean retrieveExperiment(final Archive archive) {
		// Get base name (center + disease + platform)
		final String base = archive.getTheCenter().getCenterName() + "_" + archive.getTheTumor().getTumorName() + "."
		        + archive.getThePlatform().getPlatformName();

		// Get the center
		final Center center = getArchiveQueries().getCenterByDomainNameAndPlatformName(archive.getDomainName(),
		        archive.getThePlatform().getPlatformName());

		// Get the experiment Id, if no record exists, create a new one
		Integer experimentId = getLevelThreeQueries().getExperimentId(base, new Integer(archive.getSerialIndex()),
		        new Integer(archive.getRevision()));
		if (experimentId == null) {
			experimentId = getLevelThreeQueries().insertExperiment(center.getCenterId(),
			        archive.getThePlatform().getPlatformId(), base, new Integer(archive.getSerialIndex()),
			        new Integer(archive.getRevision()));
		}

		ExperimentCenterBean ecBean = new ExperimentCenterBean();
		ecBean.setCenterId(center.getCenterId());
		ecBean.setExperimentId(experimentId);

		return ecBean;
	}

	/**
	 * Creates a data_set record.
	 * 
	 * @param centerId
	 *            the center Id
	 * @param experimentId
	 *            the experiment Id
	 * @param archive
	 *            the archive
	 * @param dataSetFileNames
	 *            an array of the names of the files contained in the archive to
	 *            include in the data set
	 * @param pattern
	 *            the file pattern for that data set
	 * @param archiveFiles
	 *            a map of all the files contained in the archive (filename ->
	 *            file Id)
	 * @param sdrfNavigator
	 *            a list of the archive SDRF's rows
	 * @param magetabArchiveDir
	 *            the exploded directory of the mage-tab archive
	 * @throws LoaderException
	 *             if there is an error during loading
	 */
	public void createDataSet(final Integer centerId,
	                          final Integer experimentId,
	                          final Archive archive,
	                          final String[] dataSetFileNames,
	                          final String pattern,
	                          final Map<String, Long> archiveFiles,
	                          final TabDelimitedContentNavigator sdrfNavigator,
	                          final String magetabArchiveDir) throws LoaderException {

		String dataSetInfoPlaceholder = 
				"\n\n\tCreating data set for archive\n" + 
		        "\t\tArchive name: %s\n" +
				"\t\tCenter Id: %d\n" + 
			    "\t\tExperiment Id: %d\n" + 
		        "\t\tSource file type: %s\n" + 
			    "\t\tData set file count: %d\n";
		
		String dataSetFileInfoPlaceholder = "\n\n\tLoading data set file [%s]\n";
		
		final String dataSetRecord = archive.getRealName() + "/" + pattern;
		final String centerName = archive.getTheCenter().getCenterName();
		final String platformName = archive.getThePlatform().getPlatformName();
		final String sourceFileType = getSourceFileType(centerName, platformName, pattern);
		final List<String> dataFileNameList = Arrays.asList(dataSetFileNames);

		logger.info(String.format(
				dataSetInfoPlaceholder,
				archive.realName(),
				centerId,
				experimentId,
				sourceFileType,
				dataFileNameList.size()));
		
		// Create a new data_set
		final Integer dataSetId = getLevelThreeQueries().createDataSet(
				centerId,
				experimentId,
		        archive.getThePlatform().getPlatformId(),
		        dataSetRecord,
		        sourceFileType,
		        ACCESS_LEVEL_PUBLIC,
		        LOAD_INCOMPLETE,
		        LOADER_LEVEL_THREE,
		        archive.getId());

		// Find out where extract name is located in SDRF
		final int extractNameIndex = getExtractNamePosition(sdrfNavigator);

		for (final String dataSetFileName : dataFileNameList) {
			
			if (logger.isDebugEnabled()) {
				logger.debug(String.format(dataSetFileInfoPlaceholder, dataSetFileName));
			}
			
			// Insert a record in data_set_file
			getLevelThreeQueries().createDataSetFile(dataSetId, dataSetFileName, archiveFiles.get(dataSetFileName));
			final String archiveLocation = archive.getDeployLocation().substring(0,
			        archive.getDeployLocation().length() - archive.getDeployedArchiveExtension().length());
			
			// Load data
			loadData(
					sdrfNavigator,
					centerName,
					platformName,
					sourceFileType,
					dataSetId,
					extractNameIndex,
					dataSetFileName,
			        archiveFiles.get(dataSetFileName),
			        archiveLocation,
			        magetabArchiveDir);

			// Indicate that fileLoading is complete
			getLevelThreeQueries().updateDataSetFile(dataSetId, dataSetFileName, archiveFiles.get(dataSetFileName));
		}

		// Set load_complete and use_in_dam to true so it will appear as
		// 'Available' in DAM
		getLevelThreeQueries().updateDataSet(dataSetId);
		logger.info("\n\n\tLoad Complete and Use In DAM set to 1\n");
	}

	/**
	 * Load data for a given center/platform/source file type
	 * 
	 * @param sdrfNavigator
	 *            a list of the archive SDRF's rows
	 * @param centerName
	 *            the center name
	 * @param platformName
	 *            the platform name
	 * @param sourceFileType
	 *            the source file type
	 * @param dataSetId
	 *            the data set Id
	 * @param extractNameIndex
	 *            the index of the 'Extract Name' column in the SDRF
	 * @param fileName
	 *            the name of the file to load
	 * @param archiveLocation
	 *            the path to the archive
	 * @throws LoaderException
	 */
	private void loadData(final TabDelimitedContentNavigator sdrfNavigator,
	                      final String centerName,
	                      final String platformName,
	                      final String sourceFileType,
	                      final Integer dataSetId,
	                      final int extractNameIndex,
	                      final String fileName,
	                      final Long fileId,
	                      final String archiveLocation,
	                      final String magetabArchiveDir) throws LoaderException {
		
		final String absoluteFileName = archiveLocation + "/" + fileName;

		if ((HUDSON_ALPHA_CENTER.equalsIgnoreCase(centerName)) || 
			(MSKCC_CENTER.equalsIgnoreCase(centerName)) ||
			(SNP.equalsIgnoreCase(platformName))) {
			loadCnaValues(
					new File(absoluteFileName),
					extractNameIndex,
					sdrfNavigator,
					dataSetId,
					platformName,
					centerName);
		}
		else if (HARVARD_CENTER.equalsIgnoreCase(centerName) || ILLUMINAHISEQ_DNASEQC_PLATFORM.equals(platformName)) {
			loadSegTsvFile(
					absoluteFileName,
					fileId,
					extractNameIndex,
					sdrfNavigator,
					dataSetId,
					platformName);
		}
		else if ((LBL_CENTER.equalsIgnoreCase(centerName)) && (HUEX_1_0_STV2.contains(platformName))) {
			loadFIRMAFile(
					absoluteFileName,
					sdrfNavigator,
					extractNameIndex,
					dataSetId);
		}
		else if ((JHU_CENTER.equalsIgnoreCase(centerName))
		        && (Arrays.asList(HUMAN_METHYLATION_27, HUMAN_METHYLATION_450).contains(platformName))) {
			loadBetaFile(
					absoluteFileName,
					dataSetId);
		}
		else if ((Arrays.asList(UNC_AGILENTG450_EXP, UNC_AGILENTG450_EXP2, UNC_AGILENTG450_EXP3, BROAD_HT_HG,
		        HUEX_1_0_STV2, UNC_MIRNA_EXP, UNC_MIRNA_EXP2).contains(platformName))) {
			loadDataFile(
					absoluteFileName,
					extractNameIndex,
					sdrfNavigator,
					dataSetId);
		}
		else if ((BCGSC_CA_CENTER.equals(centerName) || UNC_CENTER.equals(centerName))
		        && (Arrays.asList(ILLUMINA_GA_MI_RNASEQ_PLATFORM, ILLUMINA_HISEQ_MI_RNASEQ_PLATFORM))
		                .contains(platformName)) {
			if (MIRNA_QUANTIFICATION_SOURCE_FILE_TYPE.equals(sourceFileType)) {
				loadMiRnaQuantification(absoluteFileName, dataSetId);
			}
			else if (ISOFORM_QUANTIFICATION_SOURCE_FILE_TYPE.equals(sourceFileType)) {
				loadMiRnaIsoformQuantification(absoluteFileName, dataSetId);
			}
			else {
				throw new LoaderException("Could not load Level3 data. Invalid source file type " + sourceFileType);
			}
		}
		else if ((BCGSC_CA_CENTER.equals(centerName) || UNC_CENTER.equals(centerName))) {
			final boolean isRNASeqPlatform = Arrays.asList(ILLUMINA_GA_RNASEQ_PLATFORM, ILLUMINA_HISEQ_RNASEQ_PLATFORM)
			        .contains(platformName);

			final boolean isRNASeqV2Platform = Arrays.asList(ILLUMINA_GA_RNASEQV2_PLATFORM,
			        ILLUMINA_HISEQ_RNASEQV2_PLATFORM).contains(platformName);

			if (isRNASeqPlatform) {
				if (EXON_QUANTIFICATION_SOURCE_FILE_TYPE.equals(sourceFileType)) {
					loadRnaSeqExonOrGeneQuantification(absoluteFileName, dataSetId, true);
				}
				else if (GENE_QUANTIFICATION_SOURCE_FILE_TYPE.equals(sourceFileType)) {
					loadRnaSeqExonOrGeneQuantification(absoluteFileName, dataSetId, false);
				}
				else if (JUNCTION_QUANTIFICATION_SOURCE_FILE_TYPE.equals(sourceFileType)) {
					loadRnaSeqOrRnaSeqV2JunctionQuantification(absoluteFileName, dataSetId);
				}
				else {
					throw new LoaderException("Could not load Level3 data. Invalid source file type " + sourceFileType);
				}
			}
			else if (isRNASeqV2Platform) {
				if (EXON_QUANTIFICATION_SOURCE_FILE_TYPE.equals(sourceFileType)) {
					loadRnaSeqV2ExonQuantification(absoluteFileName, dataSetId);
				}
				else if (JUNCTION_QUANTIFICATION_SOURCE_FILE_TYPE.equals(sourceFileType)) {
					loadRnaSeqOrRnaSeqV2JunctionQuantification(absoluteFileName, dataSetId);
				}
				else if (GENE_QUANTIFICATION_RSEM_GENE_NORMALIZED_SOURCE_FILE_TYPE.equals(sourceFileType)) {
					loadRnaSeqV2GeneQuantificationRsemGeneOrIsoformsNormalized(absoluteFileName, dataSetId, true);
				}
				else if (GENE_QUANTIFICATION_RSEM_ISOFORMS_NORMALIZED_SOURCE_FILE_TYPE.equals(sourceFileType)) {
					loadRnaSeqV2GeneQuantificationRsemGeneOrIsoformsNormalized(absoluteFileName, dataSetId, false);
				}
				else if (GENE_QUANTIFICATION_RSEM_GENE_SOURCE_FILE_TYPE.equals(sourceFileType)) {
					loadRnaSeqV2GeneQuantificationRsemGene(absoluteFileName, dataSetId);
				}
				else if (GENE_QUANTIFICATION_RSEM_ISOFORMS_SOURCE_FILE_TYPE.equals(sourceFileType)) {
					loadRnaSeqV2GeneQuantificationRsemIsoforms(absoluteFileName, dataSetId);
				}
				else {
					throw new LoaderException("Could not load Level3 data. Invalid source file type " + sourceFileType);
				}
			}
		}
		else if (MDA_RPPA_CORE_PLATFORM.equals(platformName)) {
			// get the annotations
			final Map<String, String> annotations = getAnnotations(sdrfNavigator, magetabArchiveDir);
			loadProteinFile(absoluteFileName, dataSetId, annotations);
		}
		else {
			throw new LoaderException("Unknown Level3 data. [center name: " + centerName + " platform name:"
			        + platformName + " source file type: " + sourceFileType + "]");
		}
	}

	/**
	 * Load miRNASeq quantification data
	 * 
	 * @param absoluteFileName
	 *            the name of the miRNASeq quantification file to load
	 * @param dataSetId
	 *            the Id of the data set the file belongs to
	 * @throws LoaderException
	 */
	private void loadMiRnaQuantification(final String absoluteFileName, final Integer dataSetId) throws LoaderException {
		final int miRnaIdColumnNumber = 0;
		final int readCountColumnNumber = 1;
		final int readsPerMillionMiRnaMappedColumnNumber = 2;
		final int crossMappedColumnNumber = 3;

		final Map<Integer, String[]> contentMap = getContentMapWithoutHeaders(absoluteFileName);
		final List<Integer> keys = sortCollection(contentMap.keySet());

		final List<Object[]> batchArgs = new ArrayList<Object[]>();
		final String aliquotBarcodeOrUUID = getAliquotBarcodeOrUUIDFromFilename(absoluteFileName);
		final Integer hybridizationRefId = loadHybRef(aliquotBarcodeOrUUID, aliquotBarcodeOrUUID, dataSetId);
		
		// Unused DB table fields
		final String isoformCoords = "", miRnaRegionAnnotation = "", miRnaRegionAccession = ""; 

		String[] contents;
		String miRnaId, readCount, readsPerMillionMiRnaMapped, crossMapped;
		
		logger.debug(String.format(fileToDbLoadInfoPlaceholder, absoluteFileName, "mirnaseq_value"));
		
		for (final Integer lineNumber : keys) {
			contents = contentMap.get(lineNumber);
			miRnaId = contents[miRnaIdColumnNumber];
			readCount = contents[readCountColumnNumber];
			readsPerMillionMiRnaMapped = contents[readsPerMillionMiRnaMappedColumnNumber];
			crossMapped = contents[crossMappedColumnNumber];

			logger.debug(new StringBuilder("Line:").append(lineNumber).append("|miRnaId:").append(miRnaId)
			        .append("|readCount:").append(readCount).append("|readsPerMillionMiRnaMapped:")
			        .append(readsPerMillionMiRnaMapped).append("|crossMapped:").append(crossMapped)
			        .append("|aliquotBarcodeOrUUID:").append(aliquotBarcodeOrUUID).append("|hybridizationRefId")
			        .append(hybridizationRefId).toString());

			batchArgs.add(new Object[] { miRnaId, readCount, readsPerMillionMiRnaMapped, crossMapped, isoformCoords,
			        miRnaRegionAnnotation, miRnaRegionAccession, dataSetId, hybridizationRefId });

			if (batchArgs.size() == getBatchSize()) {
				getLevelThreeQueries().addMirnaSeqValue(batchArgs);
				batchArgs.clear();
			}
		}

		// Add the last records if the last batch had fewer records than the max
		// batch size
		if (batchArgs.size() > 0) {
			getLevelThreeQueries().addMirnaSeqValue(batchArgs);
			batchArgs.clear();
		}
	}

	/**
	 * Return the aliquot barcode or UUID contained in the given input to parse
	 * if found, <code>null</code> otherwise.
	 * 
	 * @param inputToParse
	 *            the input to parse
	 * @return the aliquot barcode or UUID contained in the given input to parse
	 *         if found, <code>null</code> otherwise
	 */
	private String getAliquotBarcodeOrUUIDFromFilename(final String inputToParse) {
		String result = getCommonBarcodeAndUUIDValidator().getAliquotBarcode(inputToParse);

		if (result == null) {
			result = getCommonBarcodeAndUUIDValidator().getUUID(inputToParse);
		}

		return result;
	}

	/**
	 * Load miRNASeq isoform quantification data
	 * 
	 * @param absoluteFileName
	 *            the name of the miRNASeq isoform quantification file to load
	 * @param dataSetId
	 *            the Id of the data set the file belongs to
	 * @throws LoaderException
	 */
	private void loadMiRnaIsoformQuantification(final String absoluteFileName, final Integer dataSetId)
	        throws LoaderException {

		final int miRnaIdColumnNumber = 0;
		final int isoformCoordsColumnNumber = 1;
		final int readCountColumnNumber = 2;
		final int readsPerMillionMiRnaMappedColumnNumber = 3;
		final int crossMappedColumnNumber = 4;
		final int miRnaRegionColumnNumber = 5;

		final Map<Integer, String[]> contentMap = getContentMapWithoutHeaders(absoluteFileName);
		final List<Integer> keys = sortCollection(contentMap.keySet());

		final List<Object[]> batchArgs = new ArrayList<Object[]>();
		final String aliquotBarcodeOrUUID = getAliquotBarcodeOrUUIDFromFilename(absoluteFileName);
		final Integer hybridizationRefId = loadHybRef(aliquotBarcodeOrUUID, aliquotBarcodeOrUUID, dataSetId);
		
		logger.debug(String.format(fileToDbLoadInfoPlaceholder, absoluteFileName, "mirnaseq_value"));
		
		for (final Integer lineNumber : keys) {
			String[] contents = contentMap.get(lineNumber);
			String miRnaId = contents[miRnaIdColumnNumber];
			String isoformCoords = contents[isoformCoordsColumnNumber];
			String readCount = contents[readCountColumnNumber];
			String readsPerMillionMiRnaMapped = contents[readsPerMillionMiRnaMappedColumnNumber];
			String crossMapped = contents[crossMappedColumnNumber];
			String miRnaRegion = contents[miRnaRegionColumnNumber];
			String[] miRnaRegionFields = getMirnaRegionFields(miRnaRegion);
			String miRnaRegionAnnotation = miRnaRegionFields[0];
			String miRnaRegionAccession = miRnaRegionFields[1];

			logger.debug(new StringBuilder("Line:")
	        .append(lineNumber).append("|miRnaId:")
	        .append(miRnaId)
	        .append("|isoformCoords:")
	        .append(isoformCoords)
	        .append("|readCount:")
	        .append(readCount)
	        .append("|readsPerMillionMiRnaMapped:")
	        .append(readsPerMillionMiRnaMapped)
	        .append("|crossMapped:")
	        .append(crossMapped)
	        .append("|mirnaRegion:")
	        .append(miRnaRegion)
	        .append("|miRnaRegionAnnotation:")
	        .append(miRnaRegionAnnotation)
	        .append("|miRnaRegionAccession:")
	        .append(miRnaRegionAccession)
	        .append("|aliquotBarcodeOrUUID:")
	        .append(aliquotBarcodeOrUUID)
	        .append("|hybridizationRefId")
	        .append(hybridizationRefId)
	        .toString());

			batchArgs.add(
					new Object[] {
							miRnaId,
							readCount,
							readsPerMillionMiRnaMapped,
							crossMapped,
							isoformCoords,
							miRnaRegionAnnotation,
							miRnaRegionAccession,
							dataSetId,
							hybridizationRefId });

			if (batchArgs.size() == getBatchSize()) {
				getLevelThreeQueries().addMirnaSeqValue(batchArgs);
				batchArgs.clear();
			}
		}

		// Add the last records if the last batch had fewer records than the max
		// batch size
		if (batchArgs.size() > 0) {
			getLevelThreeQueries().addMirnaSeqValue(batchArgs);
			batchArgs.clear();
		}
	}

	/**
	 * Parses a miRNA region field into 2 fields returned in an array:
	 * <p/>
	 * - the first element is the miRNA Region Annotation - the second element
	 * is the miRNA Region Accession
	 * 
	 * @param miRnaRegion
	 *            the miRNA Region field to parse
	 * @return an array containing the miRNA Region Annotation and miRNA Region
	 *         Accession
	 */
	private String[] getMirnaRegionFields(final String miRnaRegion) {
		String[] result = new String[2];

		final String splitRegexp = ",";
		final String[] fields = miRnaRegion.split(splitRegexp, -1);

		if (fields.length == 2) {
			result = fields;
		}
		else { // Only found miRNA Region Annotation
			result[0] = fields[0];
			result[1] = ""; // No miRNA Region Accession
		}

		return result;
	}

	/**
	 * Load RNASeq exon or gene quantification data
	 * 
	 * @param absoluteFileName
	 *            the name of the RNASeq exon OR GENE quantification file to
	 *            load
	 * @param dataSetId
	 *            the Id of the data set the file belongs to
	 * @param isExon
	 *            <code>true</code> if this is to load exon data,
	 *            <code>false</code> if it is to load gene data
	 * @throws LoaderException
	 */
	private void loadRnaSeqExonOrGeneQuantification(final String absoluteFileName,
	                                                final Integer dataSetId,
	                                                final boolean isExon) throws LoaderException {
		final int exonOrGeneColumnNumber = 0;
		final int rawCountsColumnNumber = 1;
		final int medianLengthNormalizedColumnNumber = 2;
		final int rpkmColumnNumber = 3;

		final Map<Integer, String[]> contentMap = getContentMapWithoutHeaders(absoluteFileName);
		final List<Integer> keys = sortCollection(contentMap.keySet());
		final List<Object[]> batchArgs = new ArrayList<Object[]>();
		final String aliquotBarcodeOrUUID = getAliquotBarcodeOrUUIDFromFilename(absoluteFileName);
		final Integer hybridizationRefId = loadHybRef(aliquotBarcodeOrUUID, aliquotBarcodeOrUUID, dataSetId);

		// Unused columns
		final String normalizedCounts = null;
		final String scaledEstimate = null;
		final String transcriptId = null;

		logger.debug(String.format(fileToDbLoadInfoPlaceholder, absoluteFileName, "rnaseq_value"));
		
		for (final Integer lineNumber : keys) {
			String[] contents = contentMap.get(lineNumber);
			String exonOrGene = contents[exonOrGeneColumnNumber];
			String rawCounts = contents[rawCountsColumnNumber];
			String medianLengthNormalized = contents[medianLengthNormalizedColumnNumber];
			String rpkm = contents[rpkmColumnNumber];

			logger.debug(new StringBuilder("Line:").append(lineNumber).append("|").append(isExon ? "exon" : "gene")
			        .append(":").append(exonOrGene).append("|rawCounts:").append(rawCounts)
			        .append("|medianLengthNormalized:").append(medianLengthNormalized).append("|rpkm:").append(rpkm)
			        .append("normalizedCounts|:").append(normalizedCounts).append("|scaledEstimate:")
			        .append(scaledEstimate).append("|transcriptId:").append(transcriptId).append("|dataSetId:")
			        .append(dataSetId).append("|hybridizationRefId").append(hybridizationRefId).toString());

			batchArgs.add(new Object[] { exonOrGene, rawCounts, medianLengthNormalized, rpkm, normalizedCounts,
			        scaledEstimate, transcriptId, dataSetId, hybridizationRefId });

			if (batchArgs.size() == getBatchSize()) {
				getLevelThreeQueries().addRnaSeqValue(batchArgs);
				batchArgs.clear();
			}
		}

		// Add the last records if the last batch had fewer records than the max
		// batch size
		if (batchArgs.size() > 0) {
			getLevelThreeQueries().addRnaSeqValue(batchArgs);
			batchArgs.clear();
		}
	}

	/**
	 * Load RNASeq junction quantification data (RNASeq and RNASeq V2)
	 * 
	 * @param absoluteFileName
	 *            the name of the RNASeq exon quantification file to load
	 * @param dataSetId
	 *            the Id of the data set the file belongs to
	 * @throws LoaderException
	 */
	private void loadRnaSeqOrRnaSeqV2JunctionQuantification(final String absoluteFileName, final Integer dataSetId)
	        throws LoaderException {
		loadFeatureAndRawCountsOrNormalizedCount("junction", absoluteFileName, dataSetId, true);
	}

	/**
	 * Load RNASeq or RNASeq V2 data when there are only 2 columns to load.
	 * 
	 * 
	 * @param featureName
	 *            the header name of the first column to load
	 * @param absoluteFileName
	 *            the name of the file to load
	 * @param dataSetId
	 *            the Id of the data set the file belongs to
	 * @param loadRawCounts
	 *            <code>true</code> if the 2nd column header is 'raw_counts',
	 *            <code>false</code> if it is normalized_count
	 * @throws LoaderException
	 */
	private void loadFeatureAndRawCountsOrNormalizedCount(final String featureName,
	                                                      final String absoluteFileName,
	                                                      final Integer dataSetId,
	                                                      final boolean loadRawCounts) throws LoaderException {
		final int featureColumnNumber = 0;
		final int countsColumnNumber = 1;

		final Map<Integer, String[]> contentMap = getContentMapWithoutHeaders(absoluteFileName);
		final List<Integer> keys = sortCollection(contentMap.keySet());

		final List<Object[]> batchArgs = new ArrayList<Object[]>();
		final String aliquotBarcodeOrUUID = getAliquotBarcodeOrUUIDFromFilename(absoluteFileName);
		final Integer hybridizationRefId = loadHybRef(aliquotBarcodeOrUUID, aliquotBarcodeOrUUID, dataSetId);
		
		// Unused columns
		final String medianLengthNormalized = null;
		final String rpkm = null;
		final String scaledEstimate = null;
		final String transcriptId = null;

		String[] contents;
		String feature = null;
		String rawCounts = null;
		String normalizedCount = null;
		
		logger.debug(String.format(fileToDbLoadInfoPlaceholder, absoluteFileName, "rnaseq_value"));
		
		for (final Integer lineNumber : keys) {
			contents = contentMap.get(lineNumber);
			feature = contents[featureColumnNumber];
			if (loadRawCounts) {
				rawCounts = contents[countsColumnNumber];
			}
			else {
				normalizedCount = contents[countsColumnNumber];
			}

			logger.debug(new StringBuilder("Line:").append(lineNumber).append("|").append(featureName).append(":")
			        .append(feature).append("|rawCounts:").append(rawCounts).append("|medianLengthNormalized:")
			        .append(medianLengthNormalized).append("|rpkm:").append(rpkm).append("|normalizedCount:")
			        .append(normalizedCount).append("|scaledEstimate:").append(scaledEstimate).append("|transcriptId:")
			        .append(transcriptId).append("|dataSetId:").append(dataSetId).append("|hybridizationRefId")
			        .append(hybridizationRefId).toString());

			final Object[] args = { feature, rawCounts, medianLengthNormalized, rpkm, normalizedCount, scaledEstimate,
			        transcriptId, dataSetId, hybridizationRefId };
			batchArgs.add(args);

			if (batchArgs.size() == getBatchSize()) {
				getLevelThreeQueries().addRnaSeqValue(batchArgs);
				batchArgs.clear();
			}
		}

		// Add the last records if the last batch had fewer records than the max
		// batch size
		if (batchArgs.size() > 0) {
			getLevelThreeQueries().addRnaSeqValue(batchArgs);
			batchArgs.clear();
		}
	}

	/**
	 * Load RNASeq V2 exon quantification data
	 * 
	 * @param absoluteFileName
	 *            the name of the RNASeq V2 exon quantification file to load
	 * @param dataSetId
	 *            the Id of the data set the file belongs to
	 * @throws LoaderException
	 */
	private void loadRnaSeqV2ExonQuantification(final String absoluteFileName, final Integer dataSetId)
	        throws LoaderException {
		loadRnaSeqExonOrGeneQuantification(absoluteFileName, dataSetId, true);
	}

	/**
	 * Load RNASeq V2 gene quantification data (Rsem Gene Normalized or Rsem
	 * Isoforms Normalized)
	 * 
	 * @param absoluteFileName
	 *            the name of the RNASeq V2 gene quantification file to load
	 *            (Rsem Gene Normalized or Rsem Isoforms Normalized)
	 * @param dataSetId
	 *            the Id of the data set the file belongs to
	 * @param isRsemGeneNormalized
	 *            <code>true</code> if this is to load Rsem Gene Normalized
	 *            data, <code>false</code> if it is to load Rsem Isoforms
	 *            Normalized data
	 * @throws LoaderException
	 */
	private void loadRnaSeqV2GeneQuantificationRsemGeneOrIsoformsNormalized(final String absoluteFileName,
	                                                                        final Integer dataSetId,
	                                                                        final boolean isRsemGeneNormalized)
	        throws LoaderException {
		loadFeatureAndRawCountsOrNormalizedCount(
		        "Rsem " + (isRsemGeneNormalized ? "Gene" : "Isoforms") + " Normalized", absoluteFileName, dataSetId,
		        false);
	}

	/**
	 * Load RNASeq V2 gene quantification data (Rsem Gene)
	 * 
	 * @param absoluteFileName
	 *            the name of the RNASeq V2 gene quantification file to load
	 *            (Rsem Gene)
	 * @param dataSetId
	 *            the Id of the data set the file belongs to
	 * @throws LoaderException
	 */
	private void loadRnaSeqV2GeneQuantificationRsemGene(final String absoluteFileName, final Integer dataSetId)
	        throws LoaderException {
		final int geneColumnNumber = 0;
		final int rawCountsColumnNumber = 1;
		final int scaledEstimateColumnNumber = 2;
		final int transcriptIdColumnNumber = 3;

		final Map<Integer, String[]> contentMap = getContentMapWithoutHeaders(absoluteFileName);
		final List<Integer> keys = sortCollection(contentMap.keySet());

		final List<Object[]> batchArgs = new ArrayList<Object[]>();
		final String aliquotBarcodeOrUUID = getAliquotBarcodeOrUUIDFromFilename(absoluteFileName);
		final Integer hybridizationRefId = loadHybRef(aliquotBarcodeOrUUID, aliquotBarcodeOrUUID, dataSetId);

		String[] contents;
		String gene, rawCounts, scaledEstimate, transcriptId;
		final String normalizedCounts = null, medianLengthNormalized = null, rpkm = null; // Unused
																						  // columns

		logger.debug(String.format(fileToDbLoadInfoPlaceholder, absoluteFileName, "rnaseq_value"));
		
		for (final Integer lineNumber : keys) {

			contents = contentMap.get(lineNumber);

			gene = contents[geneColumnNumber];
			rawCounts = contents[rawCountsColumnNumber];
			scaledEstimate = contents[scaledEstimateColumnNumber];
			transcriptId = contents[transcriptIdColumnNumber];

			logger.debug(new StringBuilder("Line:").append(lineNumber).append("|Rsem Gene:").append(gene)
			        .append("|rawCounts:").append(rawCounts).append("|medianLengthNormalized:")
			        .append(medianLengthNormalized).append("|rpkm:").append(rpkm).append("normalizedCounts|:")
			        .append(normalizedCounts).append("|scaledEstimate:").append(scaledEstimate)
			        .append("|transcriptId:").append(transcriptId).append("|dataSetId:").append(dataSetId)
			        .append("|hybridizationRefId").append(hybridizationRefId).toString());

			final Object[] args = { gene, rawCounts, medianLengthNormalized, rpkm, normalizedCounts, scaledEstimate,
			        transcriptId, dataSetId, hybridizationRefId };
			batchArgs.add(args);

			if (batchArgs.size() == getBatchSize()) {
				getLevelThreeQueries().addRnaSeqValue(batchArgs);
				batchArgs.clear();
			}
		}

		// Add the last records if the last batch had fewer records than the max
		// batch size
		if (batchArgs.size() > 0) {
			getLevelThreeQueries().addRnaSeqValue(batchArgs);
			batchArgs.clear();
		}
	}

	/**
	 * Load RNASeq V2 gene quantification data (Rsem Isoforms)
	 * 
	 * @param absoluteFileName
	 *            the name of the RNASeq V2 gene quantification file to load
	 *            (Rsem Isoforms)
	 * @param dataSetId
	 *            the Id of the data set the file belongs to
	 * @throws LoaderException
	 */
	private void loadRnaSeqV2GeneQuantificationRsemIsoforms(final String absoluteFileName, final Integer dataSetId)
	        throws LoaderException {
		final int isoformColumnNumber = 0;
		final int rawCountsColumnNumber = 1;
		final int scaledEstimateColumnNumber = 2;

		final Map<Integer, String[]> contentMap = getContentMapWithoutHeaders(absoluteFileName);
		final List<Integer> keys = sortCollection(contentMap.keySet());

		final List<Object[]> batchArgs = new ArrayList<Object[]>();
		final String aliquotBarcodeOrUUID = getAliquotBarcodeOrUUIDFromFilename(absoluteFileName);
		final Integer hybridizationRefId = loadHybRef(aliquotBarcodeOrUUID, aliquotBarcodeOrUUID, dataSetId);

		String[] contents;
		String isoform = null;
		String rawCounts = null;
		String scaledEstimate = null;
		
		// Unused columns
		String normalizedCounts = null;
		String medianLengthNormalized = null;
		String rpkm = null; 
		String transcriptId = null;

		logger.debug(String.format(fileToDbLoadInfoPlaceholder, absoluteFileName, "rnaseq_value"));
		
		for (final Integer lineNumber : keys) {
			contents = contentMap.get(lineNumber);
			isoform = contents[isoformColumnNumber];
			rawCounts = contents[rawCountsColumnNumber];
			scaledEstimate = contents[scaledEstimateColumnNumber];

			logger.debug(new StringBuilder("Line:").append(lineNumber).append("|Rsem Isoform:").append(isoform)
			        .append("|rawCounts:").append(rawCounts).append("|medianLengthNormalized:")
			        .append(medianLengthNormalized).append("|rpkm:").append(rpkm).append("normalizedCounts|:")
			        .append(normalizedCounts).append("|scaledEstimate:").append(scaledEstimate)
			        .append("|transcriptId:").append(transcriptId).append("|dataSetId:").append(dataSetId)
			        .append("|hybridizationRefId").append(hybridizationRefId).toString());

			final Object[] args = { isoform, rawCounts, medianLengthNormalized, rpkm, normalizedCounts, scaledEstimate,
			        transcriptId, dataSetId, hybridizationRefId };
			batchArgs.add(args);

			if (batchArgs.size() == getBatchSize()) {
				getLevelThreeQueries().addRnaSeqValue(batchArgs);
				batchArgs.clear();
			}
		}

		// Add the last records if the last batch had fewer records than the max
		// batch size
		if (batchArgs.size() > 0) {
			getLevelThreeQueries().addRnaSeqValue(batchArgs);
			batchArgs.clear();
		}
	}

	/**
	 * Loads a protein expression file into the proteinexp_value table The
	 * values loaded are antibody name, hugo gene symbol and protein expression
	 * value
	 * 
	 * @param fileName
	 *            The file containing the values to be loaded
	 * @param dataSetId
	 *            id of a dataset to be loaded
	 * @param antibodyAnnotationData
	 *            A {@link Map} of antibody name to hugo gene symbol
	 * @throws LoaderException
	 *             A {@link LoaderException} is thrown if there is an error
	 *             logical or otherwise
	 */
	public void loadProteinFile(final String fileName,
	                            final Integer dataSetId,
	                            final Map<String, String> antibodyAnnotationData) throws LoaderException {
		
		logger.debug(String.format(fileToDbLoadInfoPlaceholder, fileName, "proteinexp_value"));
		
		BufferedReader in = null;
		final List<Object[]> dataList = new ArrayList<Object[]>();
		try {
			in = new BufferedReader(new FileReader(fileName));
			String line = in.readLine();
			
			// The first line is of the form Sample REF
			// bc3f01b7-d52c-4212-850f-20acd1c82985
			// where the value following Sample REF is the uuid for this file
			// Verify that the first line contains two values separated by a tab
			final String[] sampleRef = line.split("\t", -1);
			if (sampleRef == null || sampleRef.length <= 1 || sampleRef.length > 2) {
				throw new LoaderException("Invalid file format: "
				        + "Expecting a tab delimited header sampleref line : '" + line
				        + "' with no more than two elements. DatasetId " + " datasetId = " + dataSetId);
			}
			
			final String uuid = sampleRef[1];
			String barcode = null;
			if (getCommonBarcodeAndUUIDValidator().validateUUIDFormat(uuid)) {
				barcode = getUuiddao().getLatestBarcodeForUUID(uuid);
			}
			
			if (StringUtils.isEmpty(barcode)) {
				logger.info("\n\n\tDid not find a barcode associated with the UUID specified : " + uuid + ". Skipping load of file\n");
				return;
			}
			
			// Get the hybridization ref id. Creates hybrefid if necessary
			final Integer hybrefId = loadHybRef(uuid, uuid, dataSetId);
			
			// second line is a header line of the form Composite Element REF
			// Protein Expression
			// we dont need it. so skip it.
			in.readLine();
			while (StringUtils.isNotEmpty(line = in.readLine())) {
				final String[] data = line.split("\t", -1);
				if (data == null || data.length <= 1 || data.length > 2) {
					throw new LoaderException("Invalid file format: "
					        + "Expecting a tab delimited header data line : '" + line
					        + "' with no more than two elements." + " datasetId = " + dataSetId);
				}
				
				final String antibodyName = data[0];
				if (StringUtils.isEmpty(antibodyName)) {
					throw new LoaderException("Antibody name cannot be empty : " + antibodyName + "in data line : '"
					        + line + "'. Failing load.");
				}
				
				final String hugoGeneSymbol = antibodyAnnotationData.get(antibodyName);
				if (StringUtils.isEmpty(hugoGeneSymbol)) {
					throw new LoaderException("Unable to find hugo gene symbol for antibody name : " + antibodyName
					        + "in data line : '" + line + "'. Failing load.");
				}
				
				Double proteinValue = null;
				try {
					proteinValue = Double.parseDouble(data[1]);
				}
				catch (NumberFormatException nfe) {
					throw new LoaderException("The protein value : " + data[1] + " in data line : '" + line
					        + "' is not a valid number. Failing load.");
				}
				
				// data checked. add to the data list
				final Object[] stmtArgs = { dataSetId, hybrefId, antibodyName, hugoGeneSymbol, proteinValue };
				dataList.add(stmtArgs);
				if (dataList.size() == getBatchSize()) {
					getLevelThreeQueries().addProteinExpValue(dataList);
					dataList.clear();
				}
			}
			if (dataList.size() > 0) {
				getLevelThreeQueries().addProteinExpValue(dataList);
				dataList.clear();
			}
		}
		catch (FileNotFoundException fnfe) {
			throw new LoaderException("Unable to find file [" + fileName + "]", fnfe);
		}
		catch (IOException ioe) {
			throw new LoaderException("Failed while processing data file [" + fileName + "]", ioe);
		}
		finally {
			IOUtils.closeQuietly(in);
		}
	}

	/**
	 * Parses a tab delimited file and return it's content as a map of line
	 * number -> line (without the headers line)
	 * 
	 * @param absoluteFileName
	 *            the name of tab delimited file to parse
	 * @return a map of the tab delimited content, without the headers
	 * @throws LoaderException
	 */
	private Map<Integer, String[]> getContentMapWithoutHeaders(final String absoluteFileName) throws LoaderException {
		final Map<Integer, String[]> result;
		try {
			final TabDelimitedFileParser tabDelimitedFileParser = new TabDelimitedFileParser();
			tabDelimitedFileParser.setTabDelimitedContent(new TabDelimitedContentImpl());
			tabDelimitedFileParser.loadTabDelimitedContent(absoluteFileName);
			tabDelimitedFileParser.loadTabDelimitedContentHeader();

			final TabDelimitedContent tabDelimitedContent = tabDelimitedFileParser.getTabDelimitedContent();

			result = tabDelimitedContent.getTabDelimitedContents();
			result.remove(new Integer(0)); // Remove the headers
		}
		catch (final IOException e) {
			throw new LoaderException(new StringBuilder("Error while parsing file '").append(absoluteFileName)
			        .append("'").toString(), e);
		}
		catch (final ParseException pe) {
			throw new LoaderException(new StringBuilder("Error while parsing file '").append(absoluteFileName)
			        .append("'").toString(), pe);
		}

		return result;
	}

	/**
	 * Takes a collection of Integer and return the sorted collection
	 * 
	 * @param integerCollection
	 *            the collection to sort
	 * @return a sorted collection of Integer
	 */
	private List<Integer> sortCollection(final Collection<Integer> integerCollection) {
		final List<Integer> result = new ArrayList<Integer>(integerCollection);
		Collections.sort(result);

		return result;
	}

	/**
	 * Return the source file type for the given combination of
	 * center/platform/pattern
	 * 
	 * @param centerName
	 *            the center name
	 * @param platformName
	 *            the platform name
	 * @param pattern
	 *            the file pattern
	 * @return the source file type for the given combination of
	 *         center/platform/pattern
	 * @throws gov.nih.nci.ncicb.tcga.dcc.qclive.loader.LoaderException
	 *             if the source file type can't be determined
	 */
	protected String getSourceFileType(final String centerName, final String platformName, final String pattern)
	        throws LoaderException {
		String result = null;

		if (MSKCC_CENTER.equalsIgnoreCase(centerName) || HARVARD_CENTER.equalsIgnoreCase(centerName)
		        || ILLUMINAHISEQ_DNASEQC_PLATFORM.equals(platformName)) {
			result = "copy_number_analysis";
		}
		else if (HUDSON_ALPHA_CENTER.equalsIgnoreCase(centerName) || SNP.equalsIgnoreCase(platformName)) {
			if (pattern.contains("segnormal")) {
				result = "snp_analysis.segnormal";
			}
			else if (pattern.contains("loh")) {
				result = "loh";
			}
			else if (pattern.contains(".nocnv_hg18.seg.txt")) {
				result = "snp_analysis.nocnv_hg18.seg";
			}
			else if (pattern.contains(".nocnv_hg19.seg.txt")) {
				result = "snp_analysis.nocnv_hg19.seg";
			}
			else if (pattern.contains(".hg18.seg.txt")) {
				result = "snp_analysis.hg18.seg";
			}
			else if (pattern.contains(".hg19.seg.txt")) {
				result = "snp_analysis.hg19.seg";
			}
			else if (pattern.contains("seg")) {
				result = "snp_analysis.seg";
			}
		}
		else if (LBL_CENTER.equalsIgnoreCase(centerName)) {
			if (pattern.contains("FIRMA")) {
				result = "exon_expression_analysis.FIRMA";
			}
			else if (pattern.contains("gene")) {
				result = "exon_expression_analysis.gene";
			}

		}
		else if (UNC_MIRNA_EXP.equalsIgnoreCase(platformName) || UNC_MIRNA_EXP2.equalsIgnoreCase(platformName)) {
			result = "mirna_expression_analysis";
		}
		else if (Arrays.asList(HUMAN_METHYLATION_27, HUMAN_METHYLATION_450).contains(platformName)) {
			result = "methylation_analysis";
		}
		else if ((Arrays.asList(UNC_AGILENTG450_EXP, UNC_AGILENTG450_EXP2, UNC_AGILENTG450_EXP3, BROAD_HT_HG)
		        .contains(platformName))) {
			result = "gene_expression_analysis";

		}
		else if ((BCGSC_CA_CENTER.equals(centerName) || UNC_CENTER.equals(centerName))
		        && Arrays.asList(ILLUMINA_GA_MI_RNASEQ_PLATFORM, ILLUMINA_HISEQ_MI_RNASEQ_PLATFORM).contains(
		                platformName)) {
			if (pattern.equals(MIRNA_QUANTIFICATION_PATTERN)) {
				result = MIRNA_QUANTIFICATION_SOURCE_FILE_TYPE;
			}
			else if (pattern.equals(ISOFORM_QUANTIFICATION_PATTERN)) {
				result = ISOFORM_QUANTIFICATION_SOURCE_FILE_TYPE;
			}
			else {
				throw new LoaderException("Unknown source file type for file pattern " + pattern);
			}
		}
		else if (BCGSC_CA_CENTER.equals(centerName) || UNC_CENTER.equals(centerName)) {
			if (Arrays.asList(ILLUMINA_GA_RNASEQ_PLATFORM, ILLUMINA_HISEQ_RNASEQ_PLATFORM).contains(platformName)) {// RNASeq
				if (EXON_QUANTIFICATION_PATTERN.equals(pattern)) {
					result = EXON_QUANTIFICATION_SOURCE_FILE_TYPE;
				}
				else if (GENE_QUANTIFICATION_PATTERN.equals(pattern)) {
					result = GENE_QUANTIFICATION_SOURCE_FILE_TYPE;
				}
				else if (JUNCTION_QUANTIFICATION_PATTERN.equals(pattern)) {
					result = JUNCTION_QUANTIFICATION_SOURCE_FILE_TYPE;
				}
				else {
					throw new LoaderException("Unknown source file type for file pattern " + pattern);
				}
			}
			else if (Arrays.asList(ILLUMINA_GA_RNASEQV2_PLATFORM, ILLUMINA_HISEQ_RNASEQV2_PLATFORM).contains(
			        platformName)) {// RNASeq V2
				if (EXON_QUANTIFICATION_V2_PATTERN.equals(pattern)) {
					result = EXON_QUANTIFICATION_SOURCE_FILE_TYPE;
				}
				else if (GENE_QUANTIFICATION_RSEM_GENE_PATTERN.equals(pattern)) {
					result = GENE_QUANTIFICATION_RSEM_GENE_SOURCE_FILE_TYPE;
				}
				else if (GENE_QUANTIFICATION_RSEM_GENE_NORMALIZED_PATTERN.equals(pattern)) {
					result = GENE_QUANTIFICATION_RSEM_GENE_NORMALIZED_SOURCE_FILE_TYPE;
				}
				else if (GENE_QUANTIFICATION_RSEM_ISOFORMS_PATTERN.equals(pattern)) {
					result = GENE_QUANTIFICATION_RSEM_ISOFORMS_SOURCE_FILE_TYPE;
				}
				else if (GENE_QUANTIFICATION_RSEM_ISOFORMS_NORMALIZED_PATTERN.equals(pattern)) {
					result = GENE_QUANTIFICATION_RSEM_ISOFORMS_NORMALIZED_SOURCE_FILE_TYPE;
				}
				else if (JUNCTION_QUANTIFICATION_V2_PATTERN.equals(pattern)) {
					result = JUNCTION_QUANTIFICATION_SOURCE_FILE_TYPE;
				}
				else {
					throw new LoaderException("Unknown source file type for file pattern " + pattern);
				}
			}
		}
		else if (pattern.equals(PROTEIN_EXPRESSION_PATTERN)) {
			result = ConstantValues.PROTEIN_EXPRESSION_SOURCE_FILE_TYPE;
		}
		else {
			throw new LoaderException("Unknown source file type for file pattern " + pattern);
		}

		return result;
	}

	/**
	 * Parses a directory into data_sets per file pattern
	 * 
	 * @param archive
	 *            the archive to load as part of the level three loading process
	 * @param archiveFiles
	 *            a map of all the files contained in the archive (filename ->
	 *            file Id) Files may be excluded from processing based on the -E
	 *            flag
	 * @param sdrfNavigator
	 *            a list of the archive SDRF's rows
	 * @throws LoaderException
	 *             if parsing data set resulted in an error
	 */
	private void parseDataSet(final Archive archive,
	                          final Map<String, Long> archiveFiles,
	                          final TabDelimitedContentNavigator sdrfNavigator,
	                          final String magetabArchiveDir) throws LoaderException {
		// Select a list of files from a directory per pattern defined in the
		// pattern list,
		// for every pattern , load data in the db
		final List<String> patternList = getPatternsForCenterPlatform(
				archive.getTheCenter().getCenterName(),
				archive.getThePlatform().getPlatformName());

		if (patternList == null) {
			// The combination center / platform is not supported
			throw new LoaderException(
					new StringBuilder("The combination of center '")
			        .append(archive.getTheCenter().getCenterName())
			        .append("' and platform '")
			        .append(archive.getThePlatform().getPlatformName())
			        .append("' is not supported by the Level 3 loader")
			        .toString());
		}

		// create an experiment
		final ExperimentCenterBean experimentCenterBean = retrieveExperiment(archive);
		final Integer centerId = experimentCenterBean.getCenterId();
		final Integer experimentId = experimentCenterBean.getExperimentId();
		logger.info("\n\n\tExperiment ID [" + experimentId + "]\n");

		// select all files for the pattern

		// archive dir = deploy directory - archive extension , .tar.gz or .tar
		String archiveDirectory = archive.getDeployLocation().substring(
				0,
				archive.getDeployLocation().length() - archive.getDeployedArchiveExtension().length());
		
		File archiveDir = new File(archiveDirectory);
		
		logger.info("\n\n\tArchive directory [" + archiveDir + "]\n");
		
		boolean dataSetsPresent = false;
		for (String pattern : patternList) {
			String[] dataSetFiles = archiveDir.list(new WildcardFileFilter(pattern));
			List<String> trimmedDataSetFiles = new ArrayList<String>();
			// remove the exclusion files
			for (String dataSetFile : dataSetFiles) {
				if (!excludedFiles.contains(dataSetFile)) {
					trimmedDataSetFiles.add(dataSetFile);
				}
			}
			
			dataSetFiles = trimmedDataSetFiles.toArray(new String[] {});
			// process files per data set
			if (dataSetFiles != null && dataSetFiles.length > 0) {
				dataSetsPresent = true;
				
				createDataSet(
						centerId,
						experimentId,
						archive,
						dataSetFiles,
						pattern,
						archiveFiles,
						sdrfNavigator,
				        magetabArchiveDir);
			}
		}

		// at least one data set must be present, otherwise error out and
		// rollback
		if (!dataSetsPresent) {
			throw new LoaderException(
			        " Error while parsing a data set. At least one data set must be present. archive = "
			                + archive.getDeployLocation());
		}
	}

	/**
	 * Gets all pattern strings for this center/platform combination. If there
	 * are multiple items in the patterns list for the given center and
	 * platform, it will combine them all into one list. Also if the same
	 * pattern is repeated for the center/platform it will only be included
	 * once, to avoid loading the data set for those files twice.
	 * 
	 * @param center
	 *            the archive's center name
	 * @param platform
	 *            the archive's platform name
	 * @return list of file patterns for this center/platform combination, or
	 *         null if none found
	 */
	protected List<String> getPatternsForCenterPlatform(final String center, final String platform) {
		List<String> patternList = new ArrayList<String>();

		for (Iterator<CenterPlatformPattern> it = getPatterns().iterator(); it.hasNext();) {
			CenterPlatformPattern centerPlaformPatternBean = it.next();
			if (center.equalsIgnoreCase(centerPlaformPatternBean.getCenter())
			        && platform.equalsIgnoreCase(centerPlaformPatternBean.getPlatform())) {

				for (final String pattern : centerPlaformPatternBean.getPattern()) {
					if (!patternList.contains(pattern)) {
						patternList.add(pattern);
					}
				}
			}
		}
		return patternList.size() == 0 ? null : patternList;
	}

	/**
	 * A generic level three loader is used to load data in exp_gene table
	 * 
	 * @param fileName
	 *            file name to be loaded
	 * @param sdrfNavigator
	 *            sdrf records
	 * @param extractNameIndex
	 *            index of EXTRACT_NAME column in sdrf
	 * @param dataSetId
	 *            id of a data set to be loaded
	 * @throws LoaderException
	 *             if loading a data file gone bad
	 */
	public void loadDataFile(final String fileName,
	                         final int extractNameIndex,
	                         final TabDelimitedContentNavigator sdrfNavigator,
	                         final Integer dataSetId) throws LoaderException {
		
		logger.debug(String.format(fileToDbLoadInfoPlaceholder, fileName, "expgene_value"));
		
		List<Object[]> batchArguments = new ArrayList<Object[]>();
		BufferedReader reader = null;
		FileReader fileReader = null;

		int lineCounter = 0;
		String barcode = null;
		try {
			fileReader = new FileReader(fileName);
			reader = new BufferedReader(fileReader);
			// one barcode per file, but is repeated across top
			// get out barcode and save to db
			String line = reader.readLine();
			String[] header = line.split("\t", -1);

			if (header == null || header.length <= 1) {
				throw new LoaderException(" Invalid file format: "
				        + "Expecting a tab delimited header line with at least two elements." + " File in error: "
				        + fileName + " datasetId = " + dataSetId);
			}

			// second element should be the barcode
			String hybrefName = header[1];
			if (hybrefName.matches(BARCODE_PATTERN)) {
				barcode = hybrefName;
			}
			else {
				barcode = findRecordInSDRF(sdrfNavigator, extractNameIndex, hybrefName);
			}
			// empty barcode indicates a control record. Data files that contain
			// control records should be skipped
			if (StringUtils.isNotEmpty(barcode)) {
				Integer hybRefId = loadHybRef(barcode, barcode, dataSetId);
				// eat another header which is not used atm
				reader.readLine();
				// loop through the file loading data
				while (StringUtils.isNotEmpty(line = reader.readLine())) {
					// split the line by /t
					String[] tokens = line.split("\t", -1);

					String gene = tokens[0];
					String value = tokens[1];

					// gene can't be null
					if (StringUtils.isEmpty(gene)) {
						throw new LoaderException(" Gene value can't be null. Error in file " + fileName + " in line ="
						        + lineCounter);
					}
					else {
						// strip quotes if any
						gene = gene.replace("\"", "");
					}

					// value can't be null
					if (StringUtils.isEmpty(value)) {
						throw new LoaderException(" Value for gene can't be null. Error in file " + fileName
						        + " in line= " + lineCounter);
					}
					// insert
					Object[] values = { dataSetId, hybRefId, gene, value };
					batchArguments.add(values);
					// do the batch insert if we have reached the limit
					if (batchArguments.size() == getBatchSize()) {
						getLevelThreeQueries().addExpGeneValue(batchArguments);
						batchArguments.clear();
					}
					lineCounter++;
				}
				// write off the ramaining records in the batch
				if (batchArguments.size() > 0) {
					getLevelThreeQueries().addExpGeneValue(batchArguments);
					batchArguments.clear();
				}
			}
			else {
				logger.info("\n\n\tBarcode associated with the file [" + fileName + "] was not found in SDRF. " +
						"Assuming file represents a control sample.\n");
			}
		}
		catch (IOException e) {
			throw new LoaderException("Failed while processing data file [" + fileName + "]", e);
		}
		finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(fileReader);
		}
	}

	/**
	 * Loads LBL center files
	 * 
	 * @param fileName
	 *            file name to process
	 * @param sdrfNavigator
	 *            a data structure containing sdrf elements
	 * @param extractNameIndex
	 *            SDRF column index that corresponds for extract name
	 * @param dataSetId
	 *            id of a dataset to process
	 * @throws LoaderException
	 *             if LBL loading gone bad
	 */
	public void loadFIRMAFile(final String fileName,
	                          final TabDelimitedContentNavigator sdrfNavigator,
	                          final int extractNameIndex,
	                          final Integer dataSetId) throws LoaderException {

		logger.debug(String.format(fileToDbLoadInfoPlaceholder, fileName, "expgene_value"));
		
		List<Object[]> batchArguments = new ArrayList<Object[]>();
		BufferedReader reader = null;
		FileReader fileReader = null;
		int lineNum = 1;
		String barcode = null;
		List<Integer> hybrefIdList = new ArrayList<Integer>();
		try {
			fileReader = new FileReader(fileName);
			reader = new BufferedReader(fileReader);
			// read the first line containing hybref
			String line = reader.readLine();
			String[] hybrefs = line.split("\t", -1);
			if (hybrefs == null || hybrefs.length <= 0) {
				throw new LoaderException(" First line in FIRMA file must contain hybref Names. Error in file: "
				        + fileName);
			}

			// loop through hybref names starting from position hybrefs[1]
			// since hybrefs[0] should contain something like Hybridization REF
			for (int i = 1; i < hybrefs.length; i++) {
				// find the correct barcode
				if (hybrefs[i].matches(BARCODE_PATTERN)) {
					barcode = hybrefs[i];
				}
				else {
					barcode = findRecordInSDRF(sdrfNavigator, extractNameIndex, hybrefs[i]);
				}

				// An empty barcode signifies a control record, add a null to
				// hybrefIdList for these records
				Integer hybrefId = loadHybRef(barcode, hybrefs[i], dataSetId);
				hybrefIdList.add(hybrefId);
			}

			// eat another header line
			line = reader.readLine();
			// read the file, get gene and values and insert them
			while (StringUtils.isNotEmpty(line = reader.readLine())) {
				// split the line by /t
				String[] tokens = line.split("\t", -1);
				String gene = tokens[0];
				if (hybrefIdList != null && hybrefIdList.size() + 1 == tokens.length) {
					// the number of tokens should be hybrefIdList + 1
					for (int i = 0; i < hybrefIdList.size(); i++) {
						// null hybrefId indicates a control record that should
						// be skipped
						if (hybrefIdList.get(i) != null) {
							Object[] values = { dataSetId, hybrefIdList.get(i), gene, tokens[i + 1] };
							batchArguments.add(values);
							// do the batch insert if we have reached the limit
							if (batchArguments.size() == getBatchSize()) {
								getLevelThreeQueries().addExpGeneValue(batchArguments);
								batchArguments.clear();
							}
						}
						else {
							logger.trace("Barcode associated with the record is not found in SDRF. Skipping this line [" + line + "]");
						}
					}
				}
				else {
					throw new LoaderException(
					        " Invalid number of tokens in a lbl center file data record, unable to load. Error on Line number: "
					                + lineNum);
				}
				lineNum++;
			}

			// write off the ramaining records of the batch
			if (batchArguments.size() > 0) {
				getLevelThreeQueries().addExpGeneValue(batchArguments);
				batchArguments.clear();
			}

		}
		catch (IOException e) {
			throw new LoaderException(" Failed while processing FIRMA file  " + fileName, e);
		}
		finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(fileReader);
		}
	}

	/**
	 * Loads JHU center with HumanMethylation27 or HumanMethylation450 platform
	 * 
	 * @param fileName
	 *            filename to load
	 * @param dataSetId
	 *            to associate with the load
	 * @throws LoaderException
	 *             if betafile loading resulted in an error
	 */
	public void loadBetaFile(final String fileName, final Integer dataSetId) throws LoaderException {
		
		logger.debug(String.format(fileToDbLoadInfoPlaceholder, fileName, "methylation_value"));
		
		String barcode = "";
		int lineCounter = 0;
		FileReader fileReader = null;
		BufferedReader reader = null;
		try {
			fileReader = new FileReader(fileName);
			reader = new BufferedReader(fileReader);
			// one barcode per file, but is repeated across top
			// get out barcode and save to db
			String line = reader.readLine();
			String[] header = line.split("\t", -1);
			if (header == null || header.length <= 1) {
				throw new LoaderException(" Invalid file format: "
				        + "Expecting a tab delimited header line with at least two elements." + " File in error: "
				        + fileName + " datasetId = " + dataSetId);
			}

			// second element should be the barcode
			barcode = header[1];
			// an empty barcode signifies a control record file , control record
			// files should be skipped.
			if (StringUtils.isNotEmpty(barcode)) {

				Integer hybRefId = loadHybRef(barcode, barcode, dataSetId);

				List<Object[]> batchArguments = new ArrayList<Object[]>();
				// skip second header
				reader.readLine();
				while (StringUtils.isNotEmpty(line = reader.readLine())) {

					// split the line by \t
					String[] tokens = line.split("\t", -1);
					if (tokens != null && tokens.length == BETA_FILE_TOKENS) {

						String probeName = tokens[0];
						// validation rule: probe name can't be empty
						if (StringUtils.isEmpty(probeName)) {
							throw new LoaderException(" Probe name field can't be empty " + " File in error: "
							        + fileName + " datasetId = " + dataSetId + "lineNumber = " + lineCounter);
						}

						String betaValue = tokens[1];
						// validation rule: betaValue can't be empty, not null
						// constraint in the db
						if (StringUtils.isEmpty(betaValue)) {
							throw new LoaderException(" BetaValue field can't be empty " + " File in error: "
							        + fileName + " datasetId = " + dataSetId + "lineNumber = " + lineCounter);
						}

						String geneSymbol = null;
						if (StringUtils.isNotEmpty(tokens[2])) {
							geneSymbol = tokens[2];
						}

						String chr = null;
						if (StringUtils.isNotEmpty(tokens[3])) {
							chr = tokens[3];
						}

						Integer chrPos = null;
						if (StringUtils.isNotEmpty(tokens[4])) {
							chrPos = Integer.parseInt(tokens[4]);
						}

						Object[] values = { probeName, dataSetId, hybRefId, betaValue, geneSymbol, chr, chrPos };
						batchArguments.add(values);
						// do the batch insert if we have reached the limit
						if (batchArguments.size() == getBatchSize()) {
							getLevelThreeQueries().addMethylationValue(batchArguments);
							batchArguments.clear();
						}

					}
					else {
						throw new LoaderException(" Unexpected file format <" + line
						        + ">. The file must be tab delimited and contain " + BETA_FILE_TOKENS + " tokens"
						        + " File in error: " + fileName + " datasetId = " + dataSetId);
					}
					lineCounter++;
				}

				// write off the ramaining of the batch
				if (batchArguments.size() > 0) {
					getLevelThreeQueries().addMethylationValue(batchArguments);
					batchArguments.clear();
				}
			}
			else {
				logger.info("\n\n\tBarcode associated with the file [" + fileName + "] was not found in SDRF. " +
						"Assuming file respresents a control sample\n");
			}

		}
		catch (IOException e) {
			throw new LoaderException(" Failed while processing Beta file " + fileName, e);
		}
		finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(fileReader);
		}
	}

	/**
	 * Loads harvard center files
	 * 
	 * @param fileName
	 *            to load
	 * @param sdrfNavigator
	 *            a list containing sdrfs
	 * @param extractNameIndex
	 *            index of EXTRACT_NAME colulmn in sdrf
	 * @param dataSetId
	 *            that is associated with the file name
	 * @throws LoaderException
	 *             if there is an error while loadign a seg tsv file
	 */
	public void loadSegTsvFile(final String fileName,
	                           final Long fileId,
	                           final int extractNameIndex,
	                           final TabDelimitedContentNavigator sdrfNavigator,
	                           final Integer dataSetId,
	                           final String platform) throws LoaderException {

		logger.info("\n\n\tProcessing CNA values for file [" + fileName + "]\n");

		String barcode = null;
		String hybrefName = null;
		int lineCounter = 0;
		BufferedReader reader = null;
		FileReader fileReader = null;
		List<Object[]> batchArguments = new ArrayList<Object[]>();
		File extractFile = new File(fileName);
		String extractFileName = extractFile.getName();
		int maxTokens = SEG_TSV_TOKENS;

		// get the barcode from db, illuminahiseq platform doesn't not have
		// hybrefname so use the barcode as hybrefname
		if (ILLUMINAHISEQ_DNASEQC_PLATFORM.equals(platform)) {
			maxTokens = 6;
			// get barcode from the database

			// Because of the current design restrictions the data is associated
			// only with the first tumor barcode. Once APPS-2279 is addressed this
			// code will be updated to associate this data to all the corresponding
			// barcodes
			final List<String> tumorBarcodes = commonLevelThreeQueries.getTumorBarcodesForFile(fileId);
			if (tumorBarcodes.size() > 0) {
				barcode = tumorBarcodes.get(0);
				hybrefName = barcode;
			}
		}
		else {
			String[] fileParts = extractFileName.split("_");
			if (fileParts != null && fileParts.length > 0) {
				hybrefName = fileParts[0];
				if (hybrefName.matches(BARCODE_PATTERN)) {
					barcode = hybrefName;
				}
				else {
					barcode = findRecordInSDRF(sdrfNavigator, extractNameIndex, hybrefName);
				}
			}
		}

		// Load only if the barcode is not empty. An empty barcode signifies a
		// control record which should be skipped
		if (StringUtils.isNotEmpty(barcode)) {
			Integer hybRefId = loadHybRef(barcode, hybrefName, dataSetId);
			
			try {
				fileReader = new FileReader(fileName);
				reader = new BufferedReader(fileReader);
				String line = reader.readLine();
				int segIndex = 0;
				
				while (StringUtils.isNotEmpty(line = reader.readLine())) {
					String[] tokens = line.split("\t", -1);
					if (tokens != null && tokens.length == maxTokens) {
						String chromosome = null;
						if (StringUtils.isNotEmpty(tokens[segIndex])) {
							chromosome = tokens[segIndex];
							segIndex += 1;
						}
						else {
							throw new LoaderException(
									"Error on line [" + lineCounter + "]: Missing value for 'Chromosome'");
						}

						String chrStart = null;
						if (StringUtils.isNotEmpty(tokens[segIndex])) {
							chrStart = tokens[segIndex];
							segIndex += 1;
						}
						else {
							throw new LoaderException(
									"Error on line [" + lineCounter + "]: Missing value for 'Start'");
						}

						String chrStop = null;
						if (StringUtils.isNotEmpty(tokens[segIndex])) {
							chrStop = tokens[segIndex];
							segIndex += 1;
						}
						else {
							throw new LoaderException(
									"Error on line [" + lineCounter + "]: Missing value for 'End'");
						}
						
						String numMark = null;
						if (!ILLUMINAHISEQ_DNASEQC_PLATFORM.equals(platform)) {
							if (StringUtils.isNotEmpty(tokens[segIndex])) {
								numMark = tokens[segIndex];
								segIndex += 1;
							}
							else {
								throw new LoaderException(
										"Error on line [" + lineCounter + "]: Missing value for 'Probe_Number'");
							}
						}
						else {
							segIndex += 2;
						}
						
						String segmentMean = null;
						if (StringUtils.isNotEmpty(tokens[segIndex])) {
							segmentMean = tokens[segIndex];
						}
						else {
							throw new LoaderException(
									"Error on line [" + lineCounter + "]: Missing value for 'Segment_Mean'");
						}

						batchArguments.add(
								new Object[] {
										dataSetId,
										hybRefId,
										chromosome,
										chrStart,
										chrStop,
										numMark,
										segmentMean});
						
						// do the batch insert if we have reached the limit
						if (batchArguments.size() == getBatchSize()) {
							getLevelThreeQueries().addCNAValue(batchArguments);
							batchArguments.clear();
						}
					}
					else {
						throw new LoaderException("Unexpected File format <" + line
						        + ">. The file must be tab delimited and contain " + SEG_TSV_TOKENS + " tokens"
						        + " File in error: " + fileName + " datasetId = " + dataSetId);
					}
					
					segIndex = 0;
					lineCounter++;
				}
				
				// write off the ramaining records
				if (batchArguments.size() > 0) {
					getLevelThreeQueries().addCNAValue(batchArguments);
					batchArguments.clear();
				}
			}
			catch (IOException ioe) {
				throw new LoaderException("Failed while processing segTsv file [" + fileName + "]", ioe);
			}
			finally {
				IOUtils.closeQuietly(reader);
				IOUtils.closeQuietly(fileReader);
			}
		}
		else {
			logger.info("\n\n\tBarcode associated with the file [" + fileName + "] was not found in database/SDRF. " +
					"Assuming file represents a control sample\n");
		}
	}
	
	/**
	 * Loads BROAD SNP6, MSKCC and HUDSONALPHA center files.
	 * 
	 * @param fileName
	 *            the file to load
	 * @param extractNameIndex
	 *            index of "EXTRACT_NAME" column in SDRF
	 * @param sdrfNavigator
	 *            a data structure containing SDRF
	 * @param dataSetId
	 *            id of the data set associated with the load
	 * @param platform
	 *            platform associated with the load
	 * @param center
	 *            center associated with the load
	 * @throws LoaderException
	 *             if CNA load resulted in an error
	 */
	public void loadCnaValues(File segmentFile,
	                          Integer extractNameIndex,
	                          TabDelimitedContentNavigator sdrfNavigator,
	                          Integer dataSetId,
	                          String platform,
	                          String center) throws LoaderException {

		logger.debug(String.format(fileToDbLoadInfoPlaceholder, segmentFile, "cna_value"));
		
		SegmentRecord segmentRecord = getSegmentRecordForCenter(center);
		String prevHybridizationRefName = "";
		Integer recordNumber = 0;

		List<CnaValue> cnaValues = new ArrayList<CnaValue>();
		Map<Integer, String> controlSampleRecords = new HashMap<Integer, String>();
		LineIterator segmentLineIterator = null;
		try {
			segmentLineIterator = FileUtils.lineIterator(segmentFile, CharEncoding.UTF_8);
			String segmentFileRecord = segmentLineIterator.nextLine();
			String[] recordValues = segmentFileRecord.split(SEGMENT_RECORD_DELIMITER);
			segmentRecord.setRecordValues(Arrays.asList(recordValues));
			segmentRecord.assertRecord(true);
			++recordNumber;

			
			while (segmentLineIterator.hasNext()) {
				segmentFileRecord = segmentLineIterator.nextLine();
				recordValues = segmentFileRecord.split(SEGMENT_RECORD_DELIMITER);
				segmentRecord.setRecordValues(Arrays.asList(recordValues));
				segmentRecord.setRecordNumber(recordNumber);
				segmentRecord.assertRecord(false);

				CnaValue cnaValue = segmentRecord.getCnaValue();
				cnaValue.setDataSetId(dataSetId);

				resolveHybridizationRefId(
						cnaValue,
						prevHybridizationRefName,
						sdrfNavigator,
						extractNameIndex,
				        dataSetId);

				validate(cnaValue, recordNumber);

				if (cnaValue.getHybridizationRefId() != null) {
					cnaValues.add(cnaValue.copy());
				}
				else {
					controlSampleRecords.put(recordNumber, cnaValue.getHybridizationRefName());
				}
				
				if (cnaValues.size() == getBatchSize()) {
					if (logger.isDebugEnabled()) {
						printCnaLoadBatchInfo(controlSampleRecords);
					}
					
					persistCnaValues(cnaValues);
					cnaValues.clear();
				}

				prevHybridizationRefName = cnaValue.getHybridizationRefName();
				++recordNumber;
			}

			if (cnaValues.size() > 0) {
				if (logger.isDebugEnabled()) {
					printCnaLoadBatchInfo(controlSampleRecords);
				}
				
				persistCnaValues(cnaValues);
				cnaValues.clear();
			}
		}
		catch (IOException ioe) {
			throw new LoaderException(ioe.getMessage(), ioe);
		}
		finally {
			LineIterator.closeQuietly(segmentLineIterator);
		}
	}
	
	private SegmentRecord getSegmentRecordForCenter(String center) throws LoaderException {
		if (BROAD_MIT_CENTER.equals(center)) {
			return new BroadMitEduSegmentRecord();
		}
		else if (HARVARD_CENTER.equals(center)) {
			return new HmsHarvardEduSegmentRecord();
		}
		else if (HUDSON_ALPHA_CENTER.equals(center)) {
			return new HudsonAlphaOrgSegmentRecord();
		}
		else if (MSKCC_CENTER.equals(center)) {
			return new MskccOrgSegmentRecord();
		}
		else if (WASHU_CENTER.equals(center)) {
			return new GenomeWustlEduSegmentRecord();
		}
		else {
			throw new LoaderException("No segment record type found for center [" + center + "]");
		}
	}
	
	private void resolveHybridizationRefId(CnaValue cnaValue,
	                                       String prevHybridizationRefName,
	                                       TabDelimitedContentNavigator sdrfNavigator,
	                                       Integer extractNameIndex,
	                                       Integer dataSetId) throws LoaderException {
		String barcode = "";
		String hybridizationRefName = cnaValue.getHybridizationRefName();

		if (!prevHybridizationRefName.equals(hybridizationRefName)) {
			if (hybridizationRefName.matches(BARCODE_PATTERN)) {
				barcode = hybridizationRefName;
			}
			else {
				barcode = findRecordInSDRF(sdrfNavigator, extractNameIndex, hybridizationRefName);
			}

			if (StringUtils.isNotEmpty(barcode)) {
				Integer hybridizationRefId = loadHybRef(barcode, hybridizationRefName, dataSetId);
				cnaValue.setHybridizationRefId(hybridizationRefId);
			}
			else {
				logger.info("\n\n\tBarcode associated with file was not found in SDRF, assuming file represents a control sample\n");
			}
		}
	}
	
    private <T> void validate(T beanObject, Integer recordNumber) throws LoaderException {
        String validationErrorMessagePlaceholder = 
        		"\tValidation failure: Encountered [%d] error(s) while validating record of type [%s]\n";
        String invalidBeanPropertyPlaceholder = "\t    Bean property [%s] for record number [%d] %s\n";

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        Set<ConstraintViolation<T>> constraintViolations = validator.validate(beanObject);

        if (!constraintViolations.isEmpty()) {
        	StringBuilder validationExceptionMessage = new StringBuilder();
        	validationExceptionMessage.append(String.format(
            		validationErrorMessagePlaceholder,
            		constraintViolations.size(),
            		beanObject.getClass().getName()));
        	
        	Iterator<ConstraintViolation<T>> constraintViolationIterator = constraintViolations.iterator();
        	while(constraintViolationIterator.hasNext()) {
        		ConstraintViolation<T> constraintViolation = constraintViolationIterator.next();
        		validationExceptionMessage.append(String.format(
        				invalidBeanPropertyPlaceholder,
        				constraintViolation.getPropertyPath(),
        				recordNumber,
        				constraintViolation.getMessage()));
        	}
        	
            throw new LoaderException(validationExceptionMessage.toString());
        }
    }
    
    private void printCnaLoadBatchInfo(Map<Integer, String> controlSampleRecords) {
    	StringBuilder cnaLoadBatchInfo = new StringBuilder();
    	
    	cnaLoadBatchInfo.append("\n\n\tBatch size limit of [" + getBatchSize() + "] has been reached, persisting CNA values\n");
    	
    	cnaLoadBatchInfo
    	.append("\tRunning total of control sample records encountered [")
    	.append(controlSampleRecords.size())
    	.append("]\n");
    	
    	for (Integer key : controlSampleRecords.keySet()) {
    		cnaLoadBatchInfo
    		.append("\trecordNumber: ")
    		.append(key)
    		.append(", barcode: ")
    		.append(controlSampleRecords.get(key))
    		.append("\n");
    	}
    }
    
    private void persistCnaValues(List<CnaValue> cnaValues) {
    	List<Object[]> batchArguments = new ArrayList<Object[]>();
    	
    	for(CnaValue cnaValue : cnaValues) {
    		Object[] values = { 
    				cnaValue.getDataSetId(),
    				cnaValue.getHybridizationRefId(),
    				cnaValue.getChromosome(),
    				cnaValue.getChrStart(),
    				cnaValue.getChrStop(),
    				cnaValue.getNumMark(),
    				cnaValue.getSegMean() };
    		
    		batchArguments.add(values);
    	}
    	
    	getLevelThreeQueries().addCNAValue(batchArguments);
    }
	
	/**
	 * Load hybridization reference data.
	 * <p/>
	 * 1. Checks if the barcode already exists in hybridization_ref , if not
	 * creates a new one. If a uuid is passed then the barcode is the latest
	 * barcode for that uuid. 2. Inserts a new record into hybrid_ref_data_set
	 * 
	 * @param barcodeOrUuid
	 *            the barcode or uuid to insert
	 * @param hybRefName
	 *            hybredization reference name
	 * @param dataSetId
	 *            data set id
	 * @return hybRefId associated with the loading record
	 * @throws LoaderException
	 *             if loading HybRef is resulted in an error
	 */
	public Integer loadHybRef(final String barcodeOrUuid, final String hybRefName, final Integer dataSetId)
	        throws LoaderException {

		Integer hybRefId = null;

		String barcode = null;
		final boolean isUuid = getCommonBarcodeAndUUIDValidator().validateUUIDFormat(barcodeOrUuid);
		if (isUuid) {
			// This is a UUID. Get the latest associated barcode
			barcode = getUuiddao().getLatestBarcodeForUUID(barcodeOrUuid);
		}
		else {
			barcode = barcodeOrUuid;
		}

		if (StringUtils.isNotEmpty(barcode)) {
			// extract barcode sample
			final String[] barcodePart = barcode.split("-");
			final String sample = barcodePart[0] + BARCODE_SEPARATOR + barcodePart[1] + BARCODE_SEPARATOR
			        + barcodePart[2] + BARCODE_SEPARATOR + barcodePart[3].charAt(0) + barcodePart[3].charAt(1);

			// look in hybridization_ref for barcode match
			hybRefId = getLevelThreeQueries().getHybRefId(barcode);

			// means the barcode has not been loaded previously
			if (hybRefId == null) {

				// get uuid
				String uuid = null;
				if (!isUuid) {
					uuid = getArchiveQueries().getUUIDforBarcode(barcode);
				}
				else {
					uuid = barcodeOrUuid;
				}

				if (StringUtils.isEmpty(uuid)) {
					throw new LoaderException(" unable to find UUID for barcode : " + barcode + " for this dataSet : "
					        + dataSetId);
				}

				hybRefId = getLevelThreeQueries().insertHybRef(barcode, sample, uuid);
			}

			// there already a record with the barcode, link hybrid_ref_data_set
			// to the existing id
			final Integer hybRefDataSetId = getLevelThreeQueries().getHybrefDataSetId(hybRefId, dataSetId);
			if (hybRefDataSetId == null) {
				// (Integer hybRefId,Integer dataSetId,String hybRefName);
				getLevelThreeQueries().addHybRefDataSet(hybRefId, dataSetId, hybRefName);
			}
		}

		return hybRefId;
	}

	/**
	 * finds the position of Extract name column in SDRF file
	 * 
	 * @param sdrfNavigator
	 *            a data structure containing sdrf elements
	 * @return index corresponding to Extract Name header
	 * @throws LoaderException
	 *             if can't find EXTRACT_NAME in SDRF
	 */
	protected int getExtractNamePosition(final TabDelimitedContentNavigator sdrfNavigator) throws LoaderException {

		final int extractNamePosition = sdrfNavigator.getHeaderIDByName(EXTRACT_NAME);
		// in case the column is not found
		if (extractNamePosition == -1) {
			throw new LoaderException(" SDRF file must contain " + EXTRACT_NAME
			        + " column. Check your SDRF file and try again.");
		}
		return extractNamePosition;
	}

	/**
	 * Finds a corresponding barcode in SDRF file. If the Extract Name is a UUID
	 * will look up the barcode for the UUID.
	 * 
	 * @param sdrfNavigator
	 *            where to find record
	 * @param extractNameIndex
	 *            the index of the Extract Name column in the sdrf
	 * @param hybRefName
	 *            hybridization reference name to find in sdrf
	 * @return barcode, null if not found
	 * @throws LoaderException
	 *             if an error resulted during searching for hybridization name
	 *             in sdrf
	 */
	public String findRecordInSDRF(final TabDelimitedContentNavigator sdrfNavigator,
	                               int extractNameIndex,
	                               final String hybRefName) throws LoaderException {
		for (int row = 0; row < sdrfNavigator.getNumRows(); row++) {
			final String[] rowData = sdrfNavigator.getRowByID(row);
			for (int col = 0; col < rowData.length; col++) {
				if (hybRefName.equals(sdrfNavigator.getValueByCoordinates(col, row))) {
					String barcodeCandidate = sdrfNavigator.getValueByCoordinates(extractNameIndex, row);
					// there could be several rows for a particular hybrefid,
					// only one valid barcode
					if (barcodeCandidate.matches(BARCODE_PATTERN)) {
						return barcodeCandidate;
					}
					else if (commonBarcodeAndUUIDValidator.validateUUIDFormat(barcodeCandidate)) {
						return getUuiddao().getLatestBarcodeForUUID(barcodeCandidate);

					}

				}
			}
		}
		return null;
	}

	@Override
	public void load(final List<Archive> archivesToLoad, final QcLiveStateBean stateContext)
	        throws ClinicalLoaderException {
		// not used for standalone loader.
	}

	/**
	 * Read all the annotation file names from the sdrf file, get all the
	 * annotations from the annotation files and store it in the map.
	 * 
	 * @param sdrfNavigator
	 * @param mageTabDir
	 * @return map - gene_name indexed by composite_element_ref
	 * @throws LoaderException
	 */

	protected Map<String, String> getAnnotations(final TabDelimitedContentNavigator sdrfNavigator,
	                                             final String mageTabDir) throws LoaderException {

		// map to store the annotation composite ref element and gene name
		final Map<String, String> annotationsByCompRefElement = new HashMap<String, String>();

		final List<String> annotationFileNames = getAnnotationFilenames(sdrfNavigator);

		if (annotationFileNames.size() == 0) {
			throw new LoaderException("SDRF File does not contain annotation file names.");
		}

		for (final String annotationFile : annotationFileNames) {

			BufferedReader reader = null;
			int rowNo = 0;
			try {
				reader = new BufferedReader(new FileReader(new File(mageTabDir, annotationFile)));
				String rowString;
				String[] rowData;

				// get the header
				rowData = reader.readLine().split("\t", -1);
				if (rowData.length < 2) {
					throw new LoaderException("Error reading annotation file " + annotationFile + ". Found only "
					        + rowData.length + " column(s).  At least 2 columns expected.");
				}
				int headerSize = rowData.length;
				int geneNameIndex = getAnnotationHeaderIndex(GENE_NAME, rowData);
				int compositeRefIndex = getAnnotationHeaderIndex(COMPOSITE_ELEMENT_REF, rowData);

				if (compositeRefIndex == -1) {
					throw new LoaderException("Error reading annotation file " + annotationFile + ". "
					        + COMPOSITE_ELEMENT_REF + " header doesn't exist.");
				}
				if (geneNameIndex == -1) {
					throw new LoaderException("Error reading annotation file " + annotationFile + ". " + GENE_NAME
					        + " header doesn't exist.");
				}

				while ((rowString = reader.readLine()) != null) {
					rowData = rowString.split("\t", -1);
					if (rowData.length != headerSize) {
						throw new LoaderException("Error reading annotation file " + annotationFile
						        + ". Number of columns(" + rowData.length + ") in " + rowNo
						        + " doesn't match the header columns (" + headerSize + ")");
					}
					annotationsByCompRefElement.put(rowData[compositeRefIndex], rowData[geneNameIndex]);

				}
			}
			catch (IOException e) {
				throw new LoaderException(" Error reading annotation file :" + annotationFile + " at row " + rowNo
				        + ".", e);
			}
			finally {
				try {
					if (reader != null) {
						reader.close();
					}
				}
				catch (IOException e) {
					logger.error("Unable to close read buffer");
				}
			}
		}
		return annotationsByCompRefElement;
	}

	/**
	 * Get all the annotation filenames from the sdrf file
	 * 
	 * @param sdrfNavigator
	 * @return list of annotation filenames
	 */
	private List<String> getAnnotationFilenames(final TabDelimitedContentNavigator sdrfNavigator) {
		final Set<String> annotationFileNames = new HashSet<String>();
		final List<Integer> antibodyHeaders = sdrfNavigator.getHeaderIdsForName("Annotations File");
		for (Integer id : antibodyHeaders) {
			annotationFileNames.addAll(sdrfNavigator.getColumnValues(id));
		}

		return new ArrayList<String>(annotationFileNames);
	}

	/**
	 * Get annotation header index
	 * 
	 * @param annotationHeader
	 * @param row
	 * @return Header index
	 */
	private Integer getAnnotationHeaderIndex(final String annotationHeader, final String[] row) {
		int col = -1;
		for (final String headerName : row) {
			col++;
			if (annotationHeader.equals(headerName)) {
				return col;
			}

		}
		return col;

	}

	private String getMagetabDir(final Archive archive) throws LoaderException {

		// find the mage-tab for this archive
		String sdrfLocation = getArchiveQueries().getSdrfDeployLocation(archive.getTheCenter().getCenterName(),
		        archive.getThePlatform().getPlatformName(), archive.getTumorType());
		// there should be exactly one mage file for this archive
		if (StringUtils.isEmpty(sdrfLocation)) {
			throw new LoaderException(" Unable to find SDRF file for this center = "
			        + archive.getTheCenter().getCenterName() + " and  platform = "
			        + archive.getThePlatform().getPlatformName() + " disease = " + archive.getTumorType()
			        + " combination ");
		}
		// take out archive extension .tar.gz or .tar at the end
		String magetabDirectory = sdrfLocation.substring(0, sdrfLocation.length()
		        - archive.getDeployedArchiveExtension().length());
		
		logger.info("\n\n\tSDRF location [" + magetabDirectory + "]\n");
		
		return magetabDirectory;
	}

	//
	// Getter / Setter
	//
	public ArchiveQueries getArchiveQueries() {
		return archiveQueries;
	}

	public void setArchiveQueries(final ArchiveQueries archiveQueries) {
		this.archiveQueries = archiveQueries;
	}

	public LevelThreeQueries getLevelThreeQueries() {
		return levelThreeQueries;
	}

	public void setLevelThreeQueries(final LevelThreeQueries levelThreeQueries) {
		this.levelThreeQueries = levelThreeQueries;
	}

	public CommonBarcodeAndUUIDValidator getCommonBarcodeAndUUIDValidator() {
		return commonBarcodeAndUUIDValidator;
	}

	public void setCommonBarcodeAndUUIDValidator(final CommonBarcodeAndUUIDValidator commonBarcodeAndUUIDValidator) {
		this.commonBarcodeAndUUIDValidator = commonBarcodeAndUUIDValidator;
	}

	public List<CenterPlatformPattern> getPatterns() {
		return patterns;
	}

	public void setPatterns(final List<CenterPlatformPattern> patterns) {
		this.patterns = patterns;
	}

	public String getPatternFile() {
		return patternFile;
	}

	public void setPatternFile(final String patternFile) {
		this.patternFile = patternFile;
	}

	public Integer getBatchSize() {
		if (batchSize == null) {
			batchSize = DEFAULT_BATCH_SIZE;
		}
		
		return batchSize;
	}

	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}

	public UUIDDAO getUuiddao() {
		return uuiddao;
	}

	public void setUuiddao(final UUIDDAO uuiddao) {
		this.uuiddao = uuiddao;
	}

	public LevelThreeQueries getCommonLevelThreeQueries() {
		return commonLevelThreeQueries;
	}

	public void setCommonLevelThreeQueries(LevelThreeQueries commonLevelThreeQueries) {
		this.commonLevelThreeQueries = commonLevelThreeQueries;
	}

    public void setExcludedFiles(List<String> excludedFiles) {
        this.excludedFiles = excludedFiles;
    }
}
