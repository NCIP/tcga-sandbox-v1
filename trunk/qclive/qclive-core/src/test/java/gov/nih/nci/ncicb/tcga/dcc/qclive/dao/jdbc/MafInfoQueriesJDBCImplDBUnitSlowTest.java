/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.MafInfo;

import java.io.File;
import java.math.BigDecimal;
import java.util.Map;

import org.junit.Test;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * Unit test for MafInfoQueries JDBC impl.
 * 
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class MafInfoQueriesJDBCImplDBUnitSlowTest extends DBUnitTestCase {
	private static final String PROPERTIES_FILE = "oracle.unittest.properties";
	private static final String TEST_DATA_FOLDER = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;
	private static final String TEST_DATA_FILE = "qclive/dao/mafInfo_testData.xml";

	private MafInfoQueriesJDBCImpl mafInfoQueries;
	private MafInfo maf;
	private SimpleJdbcTemplate template;

	public MafInfoQueriesJDBCImplDBUnitSlowTest() {
		super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		mafInfoQueries = new MafInfoQueriesJDBCImpl();
		mafInfoQueries.setDataSource(getDataSource());

		template = new SimpleJdbcTemplate(getDataSource());

		maf = new MafInfo();
		// these fields are common across Maf 1 and 2
		maf.setCenterID(1);
		maf.setCenterName("test.org");
		maf.setHugoSymbol("VICTOR");
		maf.setEntrezGeneID(1234);
		maf.setNcbibuild("36");
		maf.setChromosome("5");
		maf.setStartPosition(1000);
		maf.setEndPosition(1001);
		maf.setStrand("+");
		maf.setVariantClassification("varClass");
		maf.setVariantType("varType");
		maf.setReferenceAllele("refAllele");
		maf.setTumorSeqAllele1("tumorSeqAllele1");
		maf.setTumorSeqAllele2("tumorSeqAllele2");
		maf.setDbsnpRS("dbSnpRs");
		maf.setDbSNPValStatus("dbSnpValStatus");
		maf.setTumorSampleBarcode("tumor_barcode");
		maf.setMatchNormalSampleBarcode("normal_barcode");
		maf.setMatchNormSeqAllele1("matchNormSeqlAllele1");
		maf.setMatchNormSeqAllele2("matchNormSeqlAllele2");
		maf.setVerificationStatus("not ver");
		maf.setValidationStatus("not val");
		maf.setMutationStatus("test");
		maf.setSequencingPhase("one");
		maf.setValidationMethod("method");
		maf.setFileID(200000L);
		maf.setSequenceSource("Capture");
	}

	@Test
	public void testAddMaf1() {
		int mafId = -1;
		mafId = mafInfoQueries.addMaf(maf);
		assertTrue(mafId > 0);

		// do a query and check the values inserted
		Map<String, Object> values = getMafInfoFromDB(mafId);
		checkMafValues(values);
	}

	@Test
	public void testAddMaf2() {
		maf.setScore("score");
		maf.setBamFile("bamFile");
		maf.setSequencer("sequencer");

		int mafId = -1;
		mafId = mafInfoQueries.addMaf(maf);
		assertTrue(mafId > 0);

		Map<String, Object> values = getMafInfoFromDB(mafId);
		checkMafValues(values);
		assertEquals("score", values.get("SCORE"));
		assertEquals("bamFile", values.get("BAM_FILE"));
		assertEquals("sequencer", values.get("SEQUENCER"));
	}

	private void checkMafValues(final Map<String, Object> values) {
		assertEquals(1, getIntValue(values, "CENTER_ID"));
		assertEquals("5", values.get("CHROM").toString().trim());
		assertEquals("dbSnpRs", values.get("DBSNP_RS"));
		assertEquals("dbSnpValStatus", values.get("DBSNP_VAL_STATUS"));
		assertEquals(1001, getIntValue(values, "END_POSITION"));
		assertEquals(1234, getIntValue(values, "ENTREZ_GENE_ID"));
		assertEquals(200000, getIntValue(values, "FILE_ID"));
		assertEquals("VICTOR", values.get("HUGO_SYMBOL"));
		assertEquals("normal_barcode", values.get("MATCH_NORM_SAMPLE_BARCODE"));
		assertEquals("matchNormSeqlAllele1",
				values.get("MATCH_NORM_SEQ_ALLELE1"));
		assertEquals("matchNormSeqlAllele2",
				values.get("MATCH_NORM_SEQ_ALLELE2"));
		assertEquals(null, values.get("MATCH_NORM_VALIDATION_ALLELE1"));
		assertEquals(null, values.get("MATCH_NORM_VALIDATION_ALLELE2"));
		assertEquals("test", values.get("MUTATION_STATUS"));
		assertEquals("36", values.get("NCBI_BUILD"));
		assertEquals("refAllele", values.get("REFERENCE_ALLELE"));
		assertEquals("one", values.get("SEQUENCING_PHASE"));
		assertEquals(1000, getIntValue(values, "START_POSITION"));
		assertEquals("+", values.get("STRAND"));
		assertEquals("tumor_barcode", values.get("TUMOR_SAMPLE_BARCODE"));
		assertEquals("tumorSeqAllele1", values.get("TUMOR_SEQ_ALLELE1"));
		assertEquals("tumorSeqAllele2", values.get("TUMOR_SEQ_ALLELE2"));
		assertEquals("method", values.get("VALIDATION_METHOD"));
		assertEquals("not val", values.get("VALIDATION_STATUS"));
		assertEquals("varClass", values.get("VARIANT_CLASSIFICATION"));
		assertEquals("varType", values.get("VARIANT_TYPE"));
		assertEquals("not ver", values.get("VERIFICATION_STATUS"));
		assertEquals("Capture", values.get("SEQUENCE_SOURCE"));
	}

	private int getIntValue(final Map<String, Object> values, final String key) {
		return ((BigDecimal) values.get(key)).intValue();
	}

	private Map<String, Object> getMafInfoFromDB(final int mafId) {
		return template.queryForMap(
				"select * from maf_info where maf_info_id=?", mafId);
	}

}
