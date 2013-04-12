/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.BaseQueriesProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.MafInfo;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.MafInfoQueries;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

/**
 * JDBC implementation of MafQueries.
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class MafInfoQueriesJDBCImpl extends BaseQueriesProcessor implements MafInfoQueries {

    private static final String MAF_KEY_QUERY = "select maf_key_id from maf_key " +
            "where ncbi_build=? and entrez_gene_id=? and center_id=? and chrom=? and start_position=? " +
            "and end_position=? and strand=? and tumor_sample_uuid=? and " +
            "match_norm_sample_uuid=?";

    private static final String MAF_KEY_INSERT = "insert into " +
            "maf_key(maf_key_id, ncbi_build, entrez_gene_id, center_id, chrom, start_position, end_position, strand, " +
            "tumor_sample_uuid, match_norm_sample_uuid) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String INSERT_MAF_INFO = "insert into maf_info(" +
            "maf_info_id, maf_key_id, file_id, hugo_symbol, variant_classification, variant_type, " +
            "tumor_sample_barcode, match_norm_sample_barcode, " +
            "reference_allele, tumor_seq_allele1, tumor_seq_allele2, dbsnp_rs, dbsnp_val_status, " +
            "match_norm_seq_allele1, match_norm_seq_allele2, tumor_validation_allele1, tumor_validation_allele2, " +
            "match_norm_validation_allele1, match_norm_validation_allele2, verification_status, validation_status, " +
            "mutation_status, validation_method, sequencing_phase, score, bam_file, sequencer, sequence_source) " +
            "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * Adds the given MAF into to the database
     *
     * @param theMaf to add
     * @return the id of the inserted maf row
     */
    public Long addMaf(final MafInfo theMaf) {

        Long mafKeyId = getMafKeyId(theMaf);
        if (mafKeyId == null) {
            mafKeyId = insertMafKey(theMaf);
        }
        theMaf.setMafKeyId(mafKeyId);

        final Long mafId = insertMaf(theMaf);

        theMaf.setId(mafId);



        return mafId;
    }

    private Long insertMaf(final MafInfo mafInfo) {
        final Long mafId = getNextSequenceNumber("maf_info_seq");
        getJdbcTemplate().update(INSERT_MAF_INFO,
                mafId,
                mafInfo.getMafKeyId(),
                mafInfo.getFileID(),
                mafInfo.getHugoSymbol(),
                mafInfo.getVariantClassification(),
                mafInfo.getVariantType(),
                mafInfo.getTumorSampleBarcode(),
                mafInfo.getMatchNormalSampleBarcode(),
                mafInfo.getReferenceAllele(),
                mafInfo.getTumorSeqAllele1(),
                mafInfo.getTumorSeqAllele2(),
                mafInfo.getDbsnpRS(),
                mafInfo.getDbSNPValStatus(),
                mafInfo.getMatchNormSeqAllele1(),
                mafInfo.getMatchNormSeqAllele2(),
                mafInfo.getTumorValidationAllele1(),
                mafInfo.getTumorValidationAllele2(),
                mafInfo.getMatchNormValidationAllele1(),
                mafInfo.getMatchNormValidationAllele2(),
                mafInfo.getVerificationStatus(),
                mafInfo.getValidationStatus(),
                mafInfo.getMutationStatus(),
                mafInfo.getValidationMethod(),
                mafInfo.getSequencingPhase(),
                mafInfo.getScore(),
                mafInfo.getBamFile(),
                mafInfo.getSequencer(),
                mafInfo.getSequenceSource());

        return mafId;
    }

    private Long insertMafKey(final MafInfo mafInfo) {
        final Long mafKeyId = getNextSequenceNumber("maf_key_seq");
        getJdbcTemplate().update(MAF_KEY_INSERT,
                mafKeyId,
                mafInfo.getNcbiBuild(),
                mafInfo.getEntrezGeneID(),
                mafInfo.getCenterID(),
                mafInfo.getChromosome(),
                mafInfo.getStartPosition(),
                mafInfo.getEndPosition(),
                mafInfo.getStrand(),
                mafInfo.getTumorSampleUUID(),
                mafInfo.getMatchNormalSampleUUID());

        return mafKeyId;
    }

    private Long getMafKeyId(final MafInfo mafInfo) {
        Long mafKeyId = null;
        try {

            mafKeyId = getJdbcTemplate().queryForLong(MAF_KEY_QUERY,
                    mafInfo.getNcbiBuild(),
                    mafInfo.getEntrezGeneID(),
                    mafInfo.getCenterID(),
                    mafInfo.getChromosome(),
                    mafInfo.getStartPosition(),
                    mafInfo.getEndPosition(),
                    mafInfo.getStrand(),
                    mafInfo.getTumorSampleUUID(),
                    mafInfo.getMatchNormalSampleUUID());

        } catch (IncorrectResultSizeDataAccessException e) {
            // means not there, ok
        }
        return mafKeyId;
    }

    @Override
    public boolean fileIdExistsInMafInfo(Long mafFileId) {
        boolean bRet = false;
        final String select = "select count(file_id) from maf_info where file_id = ?";
        final int count = getJdbcTemplate().queryForInt(select, new Object[]{mafFileId});
        if(count > 0) {
            bRet = true;
        }
        return bRet;
    }

    @Override
    public void deleteMafInfoForFileId(Long mafFileId) {
        final String delete = "delete from maf_info where file_id = ?";
        getJdbcTemplate().update(delete, new  Object[]{mafFileId});
    }
}
