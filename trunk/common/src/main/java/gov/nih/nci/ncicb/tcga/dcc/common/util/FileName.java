package gov.nih.nci.ncicb.tcga.dcc.common.util;

/**
 * This class represents file names.
 *
 * @author ramanr
 *         Last updated by:
 * @version $Rev$
 */
public enum FileName {
    /**
     * manifest file name.
     */
    MANIFEST_TXT("MANIFEST.txt"),

    /**
     * README file name.
     */
    README_TXT("README.txt"),

    /**
     * DESCRIPTION file name.
     */
    DESCRIPTION_TXT("DESCRIPTION.txt"),

    /**
     * dcc_altered_files name.
     */

    DCC_ALTERED_FILES_TXT("DCC_ALTERED_FILES.txt"),
    /**
     * maf file extension.
     */
    MAF_EXTENSION(".maf"),
    /**
     * maf file with "germ" text in it.
     */
    GERM_MAF_FILE("germ"),
    /**
     * maf file with "protected" text in it.
     */
    PROTECTED_MAF_FILE("protected"),
    /**
     * maf file with "somatic" text in it.
     */
    SOMATIC_MAF_FILE("somatic"),
    /**
     * BCR clinical files.
     */
    CLINICAL("clinical"),
    /**
     * BCR biospecimen files.
     */
    BIOSPECIMEN("biospecimen"),

    /**
     * BCR Control files.
     */
    CONTROL("control"),

    /**
     * BCR auxilary files.
     */
    AUXILIARY("auxiliary"),

    /**
     * XML file extension.
     */
    XML_EXTENSION(".xml"),

    /**
     * XSD file extension.
     */
    XSD_EXTENSION(".xsd"),

    /**
     * TXT file extension.
     */
    TXT_EXTENSION(".txt"),

    /**
     * IDF file extension.
     */
    IDF_EXTENSION(".idf.txt"),

    /**
     * SDRF file extension.
     */
    SDRF_EXTENSION(".sdrf.txt"),

    /**
     * VCF file extension.
     */
    VCF_EXTENSION(".vcf"),

    /**
     * WIG file extension.
     */
    WIG_EXTENSION(".wig"),

    /**
     * EXON V1 RNASeq file extension.
     */
    EXON_V1_FILE_EXTENSION(".exon.quantification.txt"),

    /**
     * EXON V2 RNASeq file extension.
     */
    EXON_V2_FILE_EXTENSION(".exon_quantification.txt"),

    /**
     * GENE RNASeq file extension.
     */
    GENE_FILE_EXTENSION("gene.quantification.txt"),

    /**
     * JUNCTION V2 RNASeq file extension.
     */
    JUNCTION_V2_FILE_EXTENSION(".junction_quantification.txt"),

    /**
     * JUNCTION V1 RNASeq file extension.
     */
    JUNCTION_V1_FILE_EXTENSION(".spljxn.quantification.txt"),

    /**
     * RSEM GENES NORMALIZED file extension.
     */
    RSEM_GENE_NORMAL_FILE_EXTENSION("rsem.genes.normalized_results"),

    /**
     * RSEM GENES file extension.
     */
    RSEM_GENES_RESULTS_FILE_EXTENSION("rsem.genes.results"),

    /**
     * RSEM ISOFORMS file extension.
     */
    RSEM_ISOFORM_RESULTS_FILE_EXTENSION("rsem.isoforms.results"),

    /**
     * RSEM ISOFORMS NORMALIZED file extension.
     */
    RSEM_ISOFORM_NORMAL_FILE_EXTENSION("rsem.isoforms.normalized_results"),

    /**
     * MIRNA Seq file extension.
     */
    MIRNA_FILE_EXTENSION("mirna.quantification.txt"),

    /**
     * MIRNA Seq Isoform file extension.
     */
    MIRNA_ISOFORM_FILE_EXTENSION("isoform.quantification.txt"),

    /**
     * tar file extension.
     */
    TAR_EXTENSION(".tar"),

    /**
     * tar gz file extension.
     */
    TAR_GZ_EXTENSION(".tar.gz"),
    /**
     * md5 extension
     */
    MD5_EXTENSION(".md5"),

    /**
     * dir unknown.
     */
    DIR_UNKNOWN("unknown"),

    SOMATIC_MAF_EXTENSION(".somatic.maf"),
    PROTECTED_MAF_EXTENSION(".protected.maf");

    private String value;

    private FileName(final String fileName) {
        this.value = fileName;
    }

    /**
     * Get file name.
     *
     * @return file name.
     */
    public String getValue() {
        return value;
    }

}
