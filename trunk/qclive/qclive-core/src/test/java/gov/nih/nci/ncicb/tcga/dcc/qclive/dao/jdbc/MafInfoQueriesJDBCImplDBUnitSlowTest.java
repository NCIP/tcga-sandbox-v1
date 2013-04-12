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
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.io.File;
import java.util.Map;

/**
 * Unit test for MafInfoQueries JDBC impl.
 * 
 * @author Jessica Walton Last updated by: $Author$
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

        // the test file has id = 1 for maf_key and maf_info, so make sure we don't collide
        template.queryForLong("select maf_info_seq.nextval from dual");
        template.queryForLong("select maf_key_seq.nextval from dual");

		maf = new MafInfo();
	}

    public void testNewKeyNewInfo() {

        final Long mafId = doTest(123L, 1, 0, "1", 500, 505, "+", "newTumorUuid", "newNormalUuid",
                "HI", "36", "ins", "type", "AAA", "AAAG", "TTTC", "ok", "good",
                "AAA", "TTT", "AAAG", "TTTC", "AAA", "TTT",
                "verified", "valid", "somatic", "science", "12", "99",
                "hi.bam", "SquirrelSeq5000", "trees", "tcga-1", "tcga-2",
                1, 1); // expect 1 new key and maf info

        // verify inserted data is as expected
        final Map<String, Object> values = template.queryForMap("select * from maf_info, maf_key " +
                "where maf_info.maf_key_id=maf_key.maf_key_id and maf_info.maf_info_id=?", mafId);
        assertEquals("0", values.get("ENTREZ_GENE_ID").toString());
        assertEquals("1", values.get("CENTER_ID").toString());
        assertEquals("1", values.get("CHROM").toString());
        assertEquals("500", values.get("START_POSITION").toString());
        assertEquals("505", values.get("END_POSITION").toString());
        assertEquals("+", values.get("STRAND").toString());
        assertEquals("newTumorUuid", values.get("TUMOR_SAMPLE_UUID").toString());
        assertEquals("newNormalUuid", values.get("MATCH_NORM_SAMPLE_UUID").toString());
        assertEquals("HI", values.get("HUGO_SYMBOL").toString());
        assertEquals("36", values.get("NCBI_BUILD").toString());
        assertEquals("ins", values.get("VARIANT_CLASSIFICATION").toString());
        assertEquals("type", values.get("VARIANT_TYPE").toString());
        assertEquals("AAA", values.get("REFERENCE_ALLELE").toString());
        assertEquals("AAAG", values.get("TUMOR_SEQ_ALLELE1").toString());
        assertEquals("TTTC", values.get("TUMOR_SEQ_ALLELE2").toString());
        assertEquals("ok", values.get("DBSNP_RS").toString());
        assertEquals("good", values.get("DBSNP_VAL_STATUS").toString());
        assertEquals("AAA", values.get("MATCH_NORM_SEQ_ALLELE1").toString());
        assertEquals("TTT", values.get("MATCH_NORM_SEQ_ALLELE2").toString());
        assertEquals("AAAG", values.get("TUMOR_VALIDATION_ALLELE1").toString());
        assertEquals("TTTC", values.get("TUMOR_VALIDATION_ALLELE2").toString());
        assertEquals("AAA", values.get("MATCH_NORM_VALIDATION_ALLELE1").toString());
        assertEquals("TTT", values.get("MATCH_NORM_VALIDATION_ALLELE2").toString());
        assertEquals("verified", values.get("VERIFICATION_STATUS").toString());
        assertEquals("valid", values.get("VALIDATION_STATUS").toString());
        assertEquals("somatic", values.get("MUTATION_STATUS").toString());
        assertEquals("science", values.get("VALIDATION_METHOD").toString());
        assertEquals("12", values.get("SEQUENCING_PHASE").toString());
        assertEquals("99", values.get("SCORE").toString());
        assertEquals("hi.bam", values.get("BAM_FILE").toString());
        assertEquals("SquirrelSeq5000", values.get("SEQUENCER").toString());
        assertEquals("trees", values.get("SEQUENCE_SOURCE").toString());
        assertEquals("tcga-1", values.get("TUMOR_SAMPLE_BARCODE").toString());
        assertEquals("tcga-2", values.get("MATCH_NORM_SAMPLE_BARCODE").toString());
    }

    public void testExistingMafFileId() {
        assertEquals(true, mafInfoQueries.fileIdExistsInMafInfo(200L));
    }

    public void testMafFileIdDoesNotExist() {
        assertEquals(false, mafInfoQueries.fileIdExistsInMafInfo(300L));
    }

    public void testMafFileInfoDeleteForExistingMafFileId() {
        final Long mafId = doTest(123L, 1, 0, "1", 500, 505, "+", "newTumorUuid", "newNormalUuid",
                "HI", "36", "ins", "type", "AAA", "AAAG", "TTTC", "ok", "good",
                "AAA", "TTT", "AAAG", "TTTC", "AAA", "TTT",
                "verified", "valid", "somatic", "science", "12", "99",
                "hi.bam", "SquirrelSeq5000", "trees", "tcga-1", "tcga-2",
                1, 1); // expect 1 new key and maf info
        assertEquals(true, mafInfoQueries.fileIdExistsInMafInfo(123L));
        mafInfoQueries.deleteMafInfoForFileId(123L);
        assertEquals(false, mafInfoQueries.fileIdExistsInMafInfo(123L));
    }

    public void testExistingKeyNewInfo() {

        // has same fields as maf_key in test data
        final Long mafId = doTest(123L, 1, 0, "12", 1000, 2000, "+", "12341234-1111-2222-3333-aaaaaaaaaaaa",
                "12341234-1111-2222-3333-bbbbbbbbbbbb", "hello", "36", "blue", "yes", "G", "C", "G",
                "sure", "alligator", "G", "C", "G", "G", "C", "C", "a-okay", "seems legit", "germline", "science!",
                "100", "5", "key.bam", "DNASeq", "a company", "do", "re",
                0, 1); // should insert 0 maf_keys, 1 maf_info

        // verify the new maf info row uses the existing maf key id
        assertEquals(1L, template.queryForLong("select maf_key_id from maf_info where maf_info_id=?", mafId));
        assertEquals(new Long(1), maf.getMafKeyId());

        // should not use the existing maf info id
        assertNotSame(1L, mafId);
    }

    public void testExistingKeyExistingInfoNewFile() {
        /*
          Should match maf_info and maf_key in test data but different file id, so will insert new row in maf_info
         */
        final Long mafId = doTest(123L, 1, 0, "12", 1000, 2000, "+", "12341234-1111-2222-3333-aaaaaaaaaaaa",
                "12341234-1111-2222-3333-bbbbbbbbbbbb", "HUGO", "36", "del", "squirrel", "TCGA", "TCG-", "AGC-",
                "yeah", "orange", "TCGA", "AGCT", "none", "none", "none", "none", "gold", "unknown", "somatic",
                "magic 8 ball", "III", "99", "BAM.bam", "showbot5000", "France", "barcode1", "barcode2",
                0, 1);

        assertEquals(new Long(1), maf.getMafKeyId());
        assertEquals(mafId, maf.getId());
    }




    private Long doTest(final Long fileId,
                        final Integer centerId,
                        final Integer entrezGeneId,
                        final String chromosome,
                        final Integer startPosition,
                        final Integer endPosition,
                        final String strand,
                        final String tumorUuid,
                        final String normalUuid,
                        final String hugoSymbol,
                        final String ncbiBuild,
                        final String variantClassification,
                        final String variantType,
                        final String referenceAllele,
                        final String tumorSeqAllele1,
                        final String tumorSeqAllele2,
                        final String dbSnpRs,
                        final String dbSnpValStatus,
                        final String matchNormSeqAllele1,
                        final String matchNormSeqAllele2,
                        final String tumorValidationAllele1,
                        final String tumorValidationAllele2,
                        final String matchNormValidationAllele1,
                        final String matchNormValidationAllele2,
                        final String verificationStatus,
                        final String validationStatus,
                        final String mutationStatus,
                        final String validationMethod,
                        final String sequencingPhase,
                        final String score,
                        final String bamFile,
                        final String sequencer,
                        final String sequenceSource,
                        final String tumorBarcode,
                        final String normalBarcode,
                        final int expectedNumNewMafKey,
                        final int expectedNumNewMafInfo) {

        // query for number of maf_key and maf_info rows before insert
        final int numMafRows = template.queryForInt("select count(*) from maf_info");
        final int numMafKeyRows = template.queryForInt("select count(*) from maf_key");

        // populate maf with info that doesn't match what's in the DB
        populateMafInfo(centerId, entrezGeneId, chromosome, startPosition, endPosition, strand, tumorUuid, normalUuid,
                hugoSymbol, ncbiBuild, variantClassification, variantType, referenceAllele, tumorSeqAllele1,
                tumorSeqAllele2, dbSnpRs, dbSnpValStatus, matchNormSeqAllele1, matchNormSeqAllele2, tumorValidationAllele1,
                tumorValidationAllele2, matchNormValidationAllele1, matchNormValidationAllele2, verificationStatus,
                validationStatus, mutationStatus, validationMethod, sequencingPhase, score, bamFile, sequencer,
                sequenceSource, tumorBarcode, normalBarcode, fileId);


        final Long mafId = mafInfoQueries.addMaf(maf);
        assertNotNull(mafId);
        assertEquals(numMafRows + expectedNumNewMafInfo, template.queryForInt("select count(*) from maf_info"));
        assertEquals(numMafKeyRows + expectedNumNewMafKey, template.queryForInt("select count(*) from maf_key"));
        return mafId;
    }

    private void populateMafInfo(final Integer centerId,
                                 final Integer entrezGeneId,
                                 final String chromosome,
                                 final Integer startPosition,
                                 final Integer endPosition,
                                 final String strand,
                                 final String tumorUuid,
                                 final String normalUuid,
                                 final String hugoSymbol,
                                 final String ncbiBuild,
                                 final String variantClassification,
                                 final String variantType,
                                 final String referenceAllele,
                                 final String tumorSeqAllele1,
                                 final String tumorSeqAllele2,
                                 final String dbSnpRs,
                                 final String dbSnpValStatus,
                                 final String matchNormSeqAllele1,
                                 final String matchNormSeqAllele2,
                                 final String tumorValidationAllele1,
                                 final String tumorValidationAllele2,
                                 final String matchNormValidationAllele1,
                                 final String matchNormValidationAllele2,
                                 final String verificationStatus,
                                 final String validationStatus,
                                 final String mutationStatus,
                                 final String validationMethod,
                                 final String sequencingPhase,
                                 final String score,
                                 final String bamFile,
                                 final String sequencer,
                                 final String sequenceSource,
                                 final String tumorBarcode,
                                 final String normalBarcode,
                                 final Long fileId
    ) {

        maf.setCenterID(centerId);
        maf.setEntrezGeneID(entrezGeneId);
        maf.setChromosome(chromosome);
        maf.setStartPosition(startPosition);
        maf.setEndPosition(endPosition);
        maf.setStrand(strand);
        maf.setTumorSampleUUID(tumorUuid);
        maf.setMatchNormalSampleUUID(normalUuid);
        maf.setHugoSymbol(hugoSymbol);
        maf.setNcbiBuild(ncbiBuild);
        maf.setVariantClassification(variantClassification);
        maf.setVariantType(variantType);
        maf.setReferenceAllele(referenceAllele);
        maf.setTumorSeqAllele1(tumorSeqAllele1);
        maf.setTumorSeqAllele2(tumorSeqAllele2);
        maf.setDbsnpRS(dbSnpRs);
        maf.setDbSNPValStatus(dbSnpValStatus);
        maf.setMatchNormSeqAllele1(matchNormSeqAllele1);
        maf.setMatchNormSeqAllele2(matchNormSeqAllele2);
        maf.setTumorValidationAllele1(tumorValidationAllele1);
        maf.setTumorValidationAllele2(tumorValidationAllele2);
        maf.setMatchNormValidationAllele1(matchNormValidationAllele1);
        maf.setMatchNormValidationAllele2(matchNormValidationAllele2);
        maf.setVerificationStatus(verificationStatus);
        maf.setValidationStatus(validationStatus);
        maf.setMutationStatus(mutationStatus);
        maf.setValidationMethod(validationMethod);
        maf.setSequencingPhase(sequencingPhase);
        maf.setScore(score);
        maf.setBamFile(bamFile);
        maf.setSequencer(sequencer);
        maf.setSequenceSource(sequenceSource);
        maf.setTumorSampleBarcode(tumorBarcode);
        maf.setMatchNormalSampleBarcode(normalBarcode);
        maf.setFileID(fileId);
    }

}
