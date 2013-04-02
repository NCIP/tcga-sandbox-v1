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
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * JDBC implementation of MafQueries.
 *
 * @author fengla
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class MafInfoQueriesJDBCImpl extends BaseQueriesProcessor implements MafInfoQueries {

    /**
     * Adds the given MAF into to the database
     * @param theMaf to add
     * @return the id of the inserted maf row
     */
    public Integer addMaf( final MafInfo theMaf ) {
        final Integer mafId = getNextSequenceNumberAsInteger( "maf_info_seq" );
        final String insert = "insert into maf_info (maf_info_id, " +
                " center_id, " +
                " file_id, " +
                " hugo_symbol, " +
                " entrez_gene_id, " +
                " ncbi_build, " +
                " chrom, " +
                " start_position, " +
                " end_position, " +
                " strand, " +
                " variant_classification, " +
                " variant_type, " +
                " reference_allele, " +
                " tumor_seq_allele1, " +
                " tumor_seq_allele2, " +
                " dbsnp_rs, " +
                " dbsnp_val_status, " +
                " tumor_sample_barcode, " +
                " match_norm_sample_barcode, " +
                " match_norm_seq_allele1, " +
                " match_norm_seq_allele2, " +
                " tumor_validation_allele1, " +
                " tumor_validation_allele2, " +
                " match_norm_validation_allele1, " +
                " match_norm_validation_allele2, " +
                " verification_status, " +
                " validation_status, " +
                " mutation_status, " +
                " sequencing_phase, " +
                " sequence_source, " +
                " validation_method, " +
                " score, bam_file, sequencer ) " +
                " values " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        final SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate( getDataSource() );
        sjdbc.update( insert, mafId,
                theMaf.getCenterID(),
                theMaf.getFileID(),
                theMaf.getHugoSymbol(),
                theMaf.getEntrezGeneID(),
                theMaf.getNcbibuild(),
                theMaf.getChromosome(),
                theMaf.getStartPosition(),
                theMaf.getEndPosition(),
                theMaf.getStrand(),
                theMaf.getVariantClassification(),
                theMaf.getVariantType(),
                theMaf.getReferenceAllele(),
                theMaf.getTumorSeqAllele1(),
                theMaf.getTumorSeqAllele2(),
                theMaf.getDbsnpRS(),
                theMaf.getDbSNPValStatus(),
                theMaf.getTumorSampleBarcode(),
                theMaf.getMatchNormalSampleBarcode(),
                theMaf.getMatchNormSeqAllele1(),
                theMaf.getMatchNormSeqAllele2(),
                theMaf.getTumorValidationAllele1(),
                theMaf.getTumorValidationAllele2(),
                theMaf.getMatchNormValidationAllele1(),
                theMaf.getMatchNormValidationAllele2(),
                theMaf.getVerificationStatus(),
                theMaf.getValidationStatus(),
                theMaf.getMutationStatus(),
                theMaf.getSequencingPhase(),
                theMaf.getSequenceSource(),
                theMaf.getValidationMethod(),
                theMaf.getScore(),
                theMaf.getBamFile(),
                theMaf.getSequencer()
        );
        return mafId;
    }
}
