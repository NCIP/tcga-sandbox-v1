/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.ArchiveInfo;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.io.File;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LevelThreeQueriesImpl unit test
 * 
 * @author Julien Baboud Last updated by: $Author$
 * @version $Rev$
 */
public class LevelThreeQueriesImplDBUnitSlowTest extends DBUnitTestCase {

	private LevelThreeQueriesImpl levelThreeQueries = null;
	private SimpleJdbcTemplate template = null;

	private static final String PROPERTIES_FILE = "oracle.unittest.properties";
	private static final String TEST_DATA_FOLDER = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;

	private static final String TEST_DATA_FILE = "qclive/dao/levelThreeQueriesImpl_testDb.xml";

	public LevelThreeQueriesImplDBUnitSlowTest() {
		super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
	}

	@Override
	public void setUp() throws Exception {

		super.setUp();
		levelThreeQueries = new LevelThreeQueriesImpl();
		levelThreeQueries.setDataSource(getDataSource());
		template = new SimpleJdbcTemplate(getDataSource());
	}

	/**
	 * Tests getArchiveDeployLocation method s
	 */
	@Test
	public void testGetArchiveDeployLocation() {
		final List<String> archiveNameList = new ArrayList<String>();
		archiveNameList.add("testArchive");
		List<ArchiveInfo> archives = levelThreeQueries
				.getArchiveDeployLocation(archiveNameList);
		assertTrue(archives.size() > 0);
		assertEquals(archives.get(0).getLatest(), new Integer(1));
	}

	/**
	 * Tests bad input for getArchiveDeployLocation method
	 */
	@Test
	public void testFailGetArchiveDeployLocation() {
		final List<String> archiveNameList = new ArrayList<String>();
		archiveNameList.add("badArchive");
		List<ArchiveInfo> archives = levelThreeQueries
				.getArchiveDeployLocation(archiveNameList);
		assertTrue(archives.size() == 0);
	}

	/**
	 * Tests getDataSetId query
	 */
	@Test
	public void testGetDataSet() {
		Integer dataSetId = levelThreeQueries
				.getDataSetId(
						1,
						"jhu-usc.edu_GBM.HumanMethylation27.Level_3.2.1.0/*.lvl-3.*.txt",
						"signal_intensity", "PUBLIC");
		assertNotNull(dataSetId);
		assertEquals(dataSetId.intValue(), 1);
	}

	/**
	 * Tests failed case for getDataSetId query
	 */
	@Test
	public void testFailGetDataSet() {
		Integer dataSetId = levelThreeQueries
				.getDataSetId(
						2,
						"jhu-usc.edu_GBM.HumanMethylation27.Level_3.2.1.0/*.lvl-3.*.txt",
						"signal_intensity", "PUBLIC");
		assertNull(dataSetId);
	}

	/**
	 * Tests for createDataSet
	 */
	@Test
	public void testCreateDataSet() {

		final Integer dataSetId = levelThreeQueries.createDataSet(1, 1, 1,
				"fileName", "Genotypes", "PUBLIC", 1, 3, 1234L);
		// insert into data_set (data_set_id, experiment_id, center_id,
		// platform_id, source_file_name, source_file_type, access_level,
		// load_complete, data_level)
		// values (data_set_data_set_id_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?)";
		final List<Map> resultsList = template.query(
				" select * from data_set where source_file_name = 'fileName'",
				new ParameterizedRowMapper<Map>() {
					public Map<String, String> mapRow(
							final ResultSet resultSet, final int i)
							throws SQLException {
						final Map<String, String> resultMap = new HashMap<String, String>();
						resultMap.put("DATA_SET_ID",
								resultSet.getString("DATA_SET_ID"));
						resultMap.put("EXPERIMENT_ID",
								resultSet.getString("EXPERIMENT_ID"));
						resultMap.put("SOURCE_FILE_NAME",
								resultSet.getString("SOURCE_FILE_NAME"));
						resultMap.put("SOURCE_FILE_TYPE",
								resultSet.getString("SOURCE_FILE_TYPE"));
						resultMap.put("ACCESS_LEVEL",
								resultSet.getString("ACCESS_LEVEL"));
						resultMap.put("LOAD_COMPLETE",
								resultSet.getString("LOAD_COMPLETE"));
						resultMap.put("USE_IN_DAM",
								resultSet.getString("USE_IN_DAM"));
						resultMap.put("DAM_COMMENTS",
								resultSet.getString("DAM_COMMENTS"));
						resultMap.put("DATA_LEVEL",
								resultSet.getString("DATA_LEVEL"));
						resultMap.put("CENTER_ID",
								resultSet.getString("CENTER_ID"));
						resultMap.put("PLATFORM_ID",
								resultSet.getString("PLATFORM_ID"));
						resultMap.put("ARCHIVE_ID",
								resultSet.getString("ARCHIVE_ID"));
						return resultMap;
					}
				});

		assertNotNull(dataSetId);
		assertTrue(dataSetId > 0);

		assertTrue(resultsList.size() > 0);
		final Map resultsMap = resultsList.get(0);
		assertEquals(resultsMap.get("ARCHIVE_ID"), "1234");
		assertEquals(resultsMap.get("PLATFORM_ID"), "1");
		assertEquals(resultsMap.get("CENTER_ID"), "1");
		assertEquals(resultsMap.get("DATA_LEVEL"), "3");
		assertNull(resultsMap.get("DAM_COMMENTS"));
		assertNull(resultsMap.get("USE_IN_DAM"));
		assertEquals(resultsMap.get("LOAD_COMPLETE"), "1");
		assertEquals(resultsMap.get("ACCESS_LEVEL"), "PUBLIC");
		assertEquals(resultsMap.get("SOURCE_FILE_TYPE"), "Genotypes");
		assertEquals(resultsMap.get("SOURCE_FILE_NAME"), "fileName");
		assertEquals(resultsMap.get("EXPERIMENT_ID"), "1");
		assertTrue(new Integer((String) resultsMap.get("DATA_SET_ID")) > 0);
	}

	/**
	 * Tests failed case for getDataSetId query
	 */
	@Test
	public void testFailCreateDataSet() {

		Integer dataSetId = null;

		try {
			dataSetId = levelThreeQueries.createDataSet(3, 1, 1, "fileName",
					"Genotypes", "PUBLIC", 1, 3, 1L);
			final List<Map> resultsList = template
					.query(" select * from data_set where source_file_name = 'fileName'",
							new ParameterizedRowMapper<Map>() {
								public Map<String, String> mapRow(
										final ResultSet resultSet, final int i)
										throws SQLException {
									final Map<String, String> resultMap = new HashMap<String, String>();
									resultMap.put("DATA_SET_ID",
											resultSet.getString("DATA_SET_ID"));
									resultMap.put("EXPERIMENT_ID", resultSet
											.getString("EXPERIMENT_ID"));
									resultMap.put("SOURCE_FILE_NAME", resultSet
											.getString("SOURCE_FILE_NAME"));
									resultMap.put("SOURCE_FILE_TYPE", resultSet
											.getString("SOURCE_FILE_TYPE"));
									resultMap.put("ACCESS_LEVEL",
											resultSet.getString("ACCESS_LEVEL"));
									resultMap.put("LOAD_COMPLETE", resultSet
											.getString("LOAD_COMPLETE"));
									resultMap.put("USE_IN_DAM",
											resultSet.getString("USE_IN_DAM"));
									resultMap.put("DAM_COMMENTS",
											resultSet.getString("DAM_COMMENTS"));
									resultMap.put("DATA_LEVEL",
											resultSet.getString("DATA_LEVEL"));
									resultMap.put("CENTER_ID",
											resultSet.getString("CENTER_ID"));
									resultMap.put("PLATFORM_ID",
											resultSet.getString("PLATFORM_ID"));
									resultMap.put("ARCHIVE_ID",
											resultSet.getString("ARCHIVE_ID"));
									return resultMap;
								}
							});

		} catch (final Exception e) {

			assertNull(dataSetId);
			assertTrue(e instanceof DataIntegrityViolationException);
		}
	}

	@Test
	public void testGetExperiment() {
		Integer experimentId = levelThreeQueries.getExperimentId("base_name",
				1, 1);
		assertEquals(experimentId, new Integer(1));
	}

	@Test
	public void testFailGetExperiment() {
		Integer experimentId = levelThreeQueries.getExperimentId("bad_name", 1,
				1);
		assertNull(experimentId);
	}

	@Test
	public void testInsertExperiment() {
		// test insert
		Integer insertExperimentId = levelThreeQueries.insertExperiment(2, 2,
				"new_base_name", 2, 2);
		Object[] params = new Object[] { "new_base_name", 2, 2 };
		int testValue = template
				.queryForInt(
						" select experiment_id from experiment where base_name = ? and data_deposit_batch = ? and data_revision  = ?",
						params);
		assertTrue(testValue > 1);
	}

	@Test
	public void testGetFilesForArchive() {
		List<FileInfo> fileInfoList = levelThreeQueries
				.getFilesForArchive(1234L);
		assertEquals("test_file", fileInfoList.get(0).getFileName());
	}

	@Test
	public void testFailGetFilesForArchive() {
		List<FileInfo> fileInfoList = levelThreeQueries.getFilesForArchive(5L);
		assertTrue(fileInfoList.size() == 0);
	}

	@Test
	public void testCreateDataSetFile() {
		levelThreeQueries.createDataSetFile(1, "TEST_FILE_NAME_U", 1L);
		final Object[] params = { 1, "TEST_FILE_NAME_U", 1 };
		Integer idValue = template
				.queryForInt(
						" select data_set_file_id from data_set_file where data_set_id =? and file_name = ? and file_id = ?  ",
						params);
		assertTrue(idValue > 0);
	}

	@Test
	public void testGetHybRefId() {
		Integer barcode = levelThreeQueries
				.getHybRefId("TCGA-02-0014-01A-01D-0186-05");
		assertTrue(barcode > 0);
	}

	@Test
	public void testFailGetHybRefId() {
		Integer barcode = levelThreeQueries.getHybRefId("TCGA-BAD-BARCODE");
		assertNull(barcode);
	}

	@Test
	public void testInsertHybRef() {
		Integer hybrefId = levelThreeQueries.insertHybRef(
				"TCGA-02-0014-02A-01D-0186-06", "TCGA-02-0014-02",
				"d7f52f8d-a3fa-4cf8-9dca-859c00b596c7");
		assertTrue(hybrefId > 0);
	}

	@Test
	public void testFailInsertHybRefId() {
		try {
			Integer hybrefId = levelThreeQueries.insertHybRef(
					"TCGA-02-0014-02A-01D-0186-06", null,
					"d7f52f8d-a3fa-4cf8-9dca-859c00b596c7");

		} catch (Exception e) {
			assertTrue(e instanceof DataIntegrityViolationException);
		}

	}

	@Test
	public void testGetHybrefDataSetId() {
		Integer hybrefDataSetId = levelThreeQueries.getHybrefDataSetId(1, 1);
		assertTrue(hybrefDataSetId > 0);
	}

	@Test
	public void testFailGetHybrefDataSetId() {
		Integer hybrefDataSetId = levelThreeQueries.getHybrefDataSetId(3, 1);
		assertNull(hybrefDataSetId);
	}

	@Test
	public void testAddMethylationValue() {
		simpleJdbcTemplate.update("truncate table methylation_value");
		Object[] record1 = new Object[] { "probe1",new Integer("1"), new Integer("1"),
				"0.67340591", "ATP2A1", "16", new Integer(28797601) };
		Object[] record2 = new Object[] { "probe2",new Integer("1"), new Integer("1"),
				"0.224362904", "SLMAP", "3", new Integer(57718583) };
		Object[] record3 = new Object[] { "probe3",new Integer("1"), new Integer("1"),
				"NA", "MAN1C1", "1", new Integer(25815047) };

		List<Object[]> methylationList = new ArrayList<Object[]>();
		methylationList.add(record1);
		methylationList.add(record2);
		methylationList.add(record3);

		levelThreeQueries.addMethylationValue(methylationList);

		final List<Map<String, Object>> results = simpleJdbcTemplate
				.queryForList("select * from methylation_value");
		assertEquals(results.get(0).get("DATA_SET_ID"), new BigDecimal(1));
		assertEquals(results.get(0).get("HYBRIDIZATION_REF_ID"),
				new BigDecimal(1));
		assertEquals(results.get(0).get("BETA_VALUE"), "0.67340591");
		assertEquals(results.get(0).get("ENTREZ_GENE_SYMBOL"), "ATP2A1");
		assertEquals(results.get(0).get("CHROMOSOME"), "16");
		assertEquals(results.get(0).get("CHR_POSITION"), new BigDecimal(
				28797601));
		assertEquals(results.get(0).get("PROBE_NAME"), "probe1");

		assertEquals(results.get(1).get("DATA_SET_ID"), new BigDecimal(1));
		assertEquals(results.get(1).get("HYBRIDIZATION_REF_ID"),
				new BigDecimal(1));
		assertEquals(results.get(1).get("BETA_VALUE"), "0.224362904");
		assertEquals(results.get(1).get("ENTREZ_GENE_SYMBOL"), "SLMAP");
		assertEquals(results.get(1).get("CHROMOSOME"), "3");
		assertEquals(results.get(1).get("CHR_POSITION"), new BigDecimal(
				57718583));
		assertEquals(results.get(1).get("PROBE_NAME"), "probe2");
		
		assertEquals(results.get(2).get("DATA_SET_ID"), new BigDecimal(1));
		assertEquals(results.get(2).get("HYBRIDIZATION_REF_ID"),
				new BigDecimal(1));
		assertEquals(results.get(2).get("BETA_VALUE"), "NA");
		assertEquals(results.get(2).get("ENTREZ_GENE_SYMBOL"), "MAN1C1");
		assertEquals(results.get(2).get("CHROMOSOME"), "1");
		assertEquals(results.get(2).get("CHR_POSITION"), new BigDecimal(
				25815047));
		assertEquals(results.get(2).get("PROBE_NAME"), "probe3");

	}

	@Test
	public void testAddCNAValue() {

		simpleJdbcTemplate.update("truncate table CNA_VALUE");

		Object[] record1 = new Object[] { new Integer("1"), new Integer("1"),
				"1", "788822", "12186942", null, "0.4435" };
		Object[] record2 = new Object[] { new Integer("1"), new Integer("1"),
				"1", "12189852", "12190974", null, "0.1875" };
		Object[] record3 = new Object[] { new Integer("1"), new Integer("1"),
				"1", "12191224", "16877437", null, "0.4429" };
		List<Object[]> cnaList = new ArrayList<Object[]>();
		cnaList.add(record1);
		cnaList.add(record2);
		cnaList.add(record3);

		levelThreeQueries.addCNAValue(cnaList);

		final List<Map<String, Object>> results = simpleJdbcTemplate
				.queryForList("select * from CNA_VALUE");
		assertEquals(results.get(0).get("DATA_SET_ID"), new BigDecimal(1));
		assertEquals(results.get(0).get("HYBRIDIZATION_REF_ID"),
				new BigDecimal(1));
		assertEquals(results.get(0).get("CHROMOSOME"), "1");
		assertEquals(results.get(0).get("CHR_START"), new BigDecimal(788822));
		assertEquals(results.get(0).get("CHR_STOP"), new BigDecimal(12186942));
		assertEquals(results.get(0).get("NUM_MARK"), null);
		assertEquals(results.get(0).get("SEG_MEAN"), "0.4435");

		assertEquals(results.get(1).get("DATA_SET_ID"), new BigDecimal(1));
		assertEquals(results.get(1).get("HYBRIDIZATION_REF_ID"),
				new BigDecimal(1));
		assertEquals(results.get(1).get("CHROMOSOME"), "1");
		assertEquals(results.get(1).get("CHR_START"), new BigDecimal(12189852));
		assertEquals(results.get(1).get("CHR_STOP"), new BigDecimal(12190974));
		assertEquals(results.get(1).get("NUM_MARK"), null);
		assertEquals(results.get(1).get("SEG_MEAN"), "0.1875");

		assertEquals(results.get(2).get("DATA_SET_ID"), new BigDecimal(1));
		assertEquals(results.get(2).get("HYBRIDIZATION_REF_ID"),
				new BigDecimal(1));
		assertEquals(results.get(2).get("CHROMOSOME"), "1");
		assertEquals(results.get(2).get("CHR_START"), new BigDecimal(12191224));
		assertEquals(results.get(2).get("CHR_STOP"), new BigDecimal(16877437));
		assertEquals(results.get(2).get("NUM_MARK"), null);
		assertEquals(results.get(2).get("SEG_MEAN"), "0.4429");
	}

	@Test
	public void testAddExpGenValue() {
		simpleJdbcTemplate.update("truncate table expgene_value");

		Object[] record1 = new Object[] { new Integer("1"), new Integer("1"),
				"ELMO2", "-0.519583333333333" };
		Object[] record2 = new Object[] { new Integer("1"), new Integer("1"),
				"CREB3L1", "-1.13275" };
		Object[] record3 = new Object[] { new Integer("1"), new Integer("1"),
				"RPS11", "0.256625" };
		List<Object[]> expList = new ArrayList<Object[]>();
		expList.add(record1);
		expList.add(record2);
		expList.add(record3);
		levelThreeQueries.addExpGeneValue(expList);

		final List<Map<String, Object>> results = simpleJdbcTemplate
				.queryForList("select * from expgene_value");
		assertEquals(results.get(0).get("DATA_SET_ID"), new BigDecimal(1));
		assertEquals(results.get(0).get("HYBRIDIZATION_REF_ID"),
				new BigDecimal(1));
		assertEquals(results.get(0).get("ENTREZ_GENE_SYMBOL"), "ELMO2");
		assertEquals(results.get(0).get("EXPRESSION_VALUE"),
				"-0.519583333333333");

		assertEquals(results.get(1).get("DATA_SET_ID"), new BigDecimal(1));
		assertEquals(results.get(1).get("HYBRIDIZATION_REF_ID"),
				new BigDecimal(1));
		assertEquals(results.get(1).get("ENTREZ_GENE_SYMBOL"), "CREB3L1");
		assertEquals(results.get(1).get("EXPRESSION_VALUE"), "-1.13275");

		assertEquals(results.get(2).get("DATA_SET_ID"), new BigDecimal(1));
		assertEquals(results.get(2).get("HYBRIDIZATION_REF_ID"),
				new BigDecimal(1));
		assertEquals(results.get(2).get("ENTREZ_GENE_SYMBOL"), "RPS11");
		assertEquals(results.get(2).get("EXPRESSION_VALUE"), "0.256625");
	}

	@Test
	public void testAddMirnaSeqValue() {

		simpleJdbcTemplate.update("truncate table mirnaseq_value");

		final String feature = "test feature";
		final Double readCount = 2.0;
		final Double readsPerMillion = 2.0;
		final String crossMapped = "Y";
		final String isoformCoords = "test isoform coords";
		final String mirnaRegionAnnotation = "test mirna region annotation";
		final String mirnaRegionAccession = "test mirna region accession";
		final Integer dataSetId = 1;
		final Integer hybridizationRefId = 1;

		final Object[] args = { feature, readCount, readsPerMillion,
				crossMapped, isoformCoords, mirnaRegionAnnotation,
				mirnaRegionAccession, dataSetId, hybridizationRefId };

		final List<Object[]> batchArgs = new ArrayList<Object[]>();
		batchArgs.add(args);

		levelThreeQueries.addMirnaSeqValue(batchArgs);

		final List<Map<String, Object>> results = simpleJdbcTemplate
				.queryForList("select * from mirnaseq_value");

		assertNotNull(results);
		assertEquals(1, results.size());

		final Map<String, Object> firstRecord = results.get(0);
		
		assertNotNull(firstRecord);
		assertEquals(feature, firstRecord.get("FEATURE"));
		assertEquals(readCount, firstRecord.get("READ_COUNT"));
		assertEquals(readsPerMillion, firstRecord.get("READS_PER_MILLION"));
		assertEquals(crossMapped, firstRecord.get("CROSS_MAPPED"));
		assertEquals(isoformCoords, firstRecord.get("ISOFORM_COORDS"));
		assertEquals(mirnaRegionAnnotation,
				firstRecord.get("MIRNA_REGION_ANNOTATION"));
		assertEquals(mirnaRegionAccession,
				firstRecord.get("MIRNA_REGION_ACCESSION"));
		assertEquals(new BigDecimal(dataSetId), firstRecord.get("DATA_SET_ID"));
		assertEquals(new BigDecimal(hybridizationRefId),
				firstRecord.get("HYBRIDIZATION_REF_ID"));
	}

	@Test
	public void testAddRnaSeqValue() {

		simpleJdbcTemplate.update("truncate table rnaseq_value");

		final String feature = "test feature";
		final Double rawCounts = 2.0;
		final Double medianLengthNormalized = 3.0;
		final Double rpkm = 4.0;
		final Integer dataSetId = 1;
		final Integer hybridizationRefId = 1;
        final Float normalizedCounts = 1.2F;
        final String scaledEstimate = "Scaled Estimate test";
        final String transcriptId = "Transcript Id test";

		final Object[] args = { feature, rawCounts, medianLengthNormalized,
				rpkm, normalizedCounts, scaledEstimate, transcriptId, dataSetId, hybridizationRefId};

		final List<Object[]> batchArgs = new ArrayList<Object[]>();
		batchArgs.add(args);

		levelThreeQueries.addRnaSeqValue(batchArgs);

		final List<Map<String, Object>> results = simpleJdbcTemplate
				.queryForList("select * from rnaseq_value");

		assertNotNull(results);
		assertEquals(1, results.size());

		final Map<String, Object> firstRecord = results.get(0);
		
		assertNotNull(firstRecord);
		assertEquals(feature, firstRecord.get("FEATURE"));
		assertEquals(rawCounts, firstRecord.get("RAW_COUNTS"));
		assertEquals(medianLengthNormalized,
				firstRecord.get("MEDIAN_LENGTH_NORMALIZED"));
		assertEquals(rpkm, firstRecord.get("RPKM"));
		assertEquals(new BigDecimal(dataSetId), firstRecord.get("DATA_SET_ID"));
		assertEquals(new BigDecimal(hybridizationRefId),
				firstRecord.get("HYBRIDIZATION_REF_ID"));
        assertEquals(normalizedCounts, new Float(firstRecord.get("NORMALIZED_COUNTS").toString()));
        assertEquals(scaledEstimate, firstRecord.get("SCALED_ESTIMATE"));
        assertEquals(transcriptId, firstRecord.get("TRANSCRIPT_ID"));
	}

    @Test
	public void testAddProteinValue() {

		simpleJdbcTemplate.update("truncate table proteinexp_value");

		final List<Object[]> dataList = Arrays.asList(
            new Object[] {1,1,"antibody-name-1", "gene-name-1","0000"},
            new Object[] {1,1,"antibody-name-2", "gene-name-2","1111"}
        );


		levelThreeQueries.addProteinExpValue(dataList);

		final List<Object[]> actualResults = simpleJdbcTemplate
				.query("select proteinexp_id, data_set_id,hybridization_ref_id,antibody_name,hugo_gene_symbol, protein_expression_value from proteinexp_value order by proteinexp_id", new RowMapper<Object[]>() {
                    @Override
                    public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
                        final Object[] row  = new Object[5];
                        int i =0;
                        row[i++] = rs.getInt("data_set_id");
                        row[i++] = rs.getInt("hybridization_ref_id");
                        row[i++] = rs.getString("antibody_name");
                        row[i++] = rs.getString("hugo_gene_symbol");
                        row[i++] = rs.getString("protein_expression_value");

                        return row;
                    }
                });

		assertNotNull(actualResults);
		assertEquals(dataList.size(), actualResults.size());

        for(int i=0 ;i < dataList.size();i++){
            Arrays.equals(dataList.get(i),actualResults.get(i));
        }

	}
}
