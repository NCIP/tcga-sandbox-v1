package gov.nih.nci.ncicb.tcga.dcc.common.util;
/**
 * This class represents platform names.
 *
 * @author ramanr
 *         Last updated by: ramanr
 * @version $Rev$
 */
public enum PlatformName {
    /**
     * SNP platform name.
     */
    SNP("Genome_Wide_SNP_6"),
    /**
     * BROAD_HT_HG platform name.
     */
    BROAD_HT_HG("HT_HG-U133A"),
    /**
     * HUMAN_METHYLATION_27platform name.
     */
    HUMAN_METHYLATION_27("HumanMethylation27"),
    /**
     * HUMAN_METHYLATION_450 platform name.
     */
    HUMAN_METHYLATION_450("HumanMethylation450"),
    /**
     * UNC_MIRNA_EXP platform name.
     */
    UNC_MIRNA_EXP("H-miRNA_8x15K"),
    /**
     * UNC_MIRNA_EXP2 platform name.
     */
    UNC_MIRNA_EXP2("H-miRNA_8x15Kv2"),
    /**
     * UNC_AGILENTG450_EXP platform name.
     */
    UNC_AGILENTG450_EXP("AgilentG4502A_07_1"),
    /**
     * UNC_AGILENTG450_EXP2 platform name.
     */
    UNC_AGILENTG450_EXP2("AgilentG4502A_07_2"),
    /**
     *  UNC_AGILENTG450_EXP3 platform name.
     */
    UNC_AGILENTG450_EXP3("AgilentG4502A_07_3"),
    /**
     *  ILLUMINA_GA_MI_RNASEQ_PLATFORM platform name.
     */
    ILLUMINA_GA_MI_RNASEQ_PLATFORM("IlluminaGA_miRNASeq"),
    /**
     * ILLUMINA_GA_RNASEQ_PLATFORM platform name.
     */
    ILLUMINA_GA_RNASEQ_PLATFORM("IlluminaGA_RNASeq"),
    /**
     * ILLUMINA_GA_RNASEQV2_PLATFORM platform name.
     */
    ILLUMINA_GA_RNASEQV2_PLATFORM("IlluminaGA_RNASeqV2"),
    /**
     * ILLUMINA_HISEQ_RNASEQ_PLATFORM platform name.
     */
    ILLUMINA_HISEQ_RNASEQ_PLATFORM("IlluminaHiSeq_RNASeq"),
    /**
     * ILLUMINA_HISEQ_RNASEQV2_PLATFORM platform name.
     */
    ILLUMINA_HISEQ_RNASEQV2_PLATFORM("IlluminaHiSeq_RNASeqV2"),
    /**
     *  ILLUMINA_HISEQ_MI_RNASEQ_PLATFORM platform name.
     */
    ILLUMINA_HISEQ_MI_RNASEQ_PLATFORM("IlluminaHiSeq_miRNASeq"),
    /**
     * ILLUMINAHISEQ_DNASEQC_PLATFORM platform name.
     */
    ILLUMINAHISEQ_DNASEQC_PLATFORM("IlluminaHiSeq_DNASeqC"),


    /**
     * HUEX_1_0_STV2 platform name.
     */
    HUEX_1_0_STV2("HuEx-1_0-st-v2"),
    /**
     * MDA_RPPA_CORE_PLATFORM platform name.
     */
    MDA_RPPA_CORE_PLATFORM("MDA_RPPA_Core"),
    /**
     * bio platform name.
     */
    BIO("bio"),
    /**
     * diagnostic images platform.
     */

    DIAGNOSTIC_IMAGES("diagnostic_images"),
    /**
     * pathology reports platform.
     */
    PATHOLOGY_REPORTS("pathology_reports"),
    /**
     * tissue images platform.
     */
    TISSUE_IMAGES("tissue_images"),

    /**
     * All MIRNASeq platforms.
     */
    MI_RNASEQ("miRNASeq"),

    /**
     * All RNA Seq platforms.
     */
    RNASeq("RNASeq"),

    /**
     * firstGenSequencingPlatform ABI.
     */
    ABI("ABI"),

    ILLUMINAGA_DNASEQ("IlluminaGA_DNASeq"),
    ILLUMINAGA_DNASEQ_CONT("IlluminaGA_DNASeq_Cont"),
    SOLID_DNASEQ("SOLiD_DNASeq"),
    SOLID_DNASEQ_CONT("SOLiD_DNASeq_Cont"),
    ILLUMINAHISEQ_DNASEQ("IlluminaHiSeq_DNASeq"),
    ILLUMINAHISEQ_DNASEQ_CONT("IlluminaHiSeq_DNASeq_Cont");


    private String value;

    private PlatformName(final String platformName) {
        this.value = platformName;
    }

    /**
     * Get platform name.
     * @return  platform name.
     */
    public String getValue() {
        return value;
    }

}
