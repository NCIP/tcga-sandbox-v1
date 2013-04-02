/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.loader;

import gov.nih.nci.ncicb.tcga.dcc.common.service.FileTypeLookup;

/**
 * This class is for testing.
 *
 * @author David Nassau Last updated by: $Author$
 * @version $Rev$
 */
public class DummyFileTypeLookup implements FileTypeLookup {

    public DummyFileTypeLookup() {
    }

    public String lookupFileType(final String filename, final String center, final String platform) {
        if (center.equals("broad.mit.edu")) {
            if (platform.equals("HT_HG-U133A")) {
                if (filename.endsWith(".level2.data.txt")) {
                    return "probeset_rma";
                }
            } else if (platform.equals("Genome_Wide_SNP_6")) {
                if (filename.endsWith(".after_5NN.copynumber.data.txt")) {
                    return "after5NN_copyNumber";
                } else if (filename.endsWith(".birdseed.data.txt")) {
                    return "birdseed";
                } else if (filename.endsWith(".no_outlier.copynumber.data.txt")) {
                    return "noOutlier_copyNumber";
                } else if (filename.endsWith(".copynumber.data.txt")) {
                    return "copyNumber";
                } else if (filename.endsWith(".ismpolish.data.txt")) {
                    return "ismpolish";
                }
            }
        } else if (center.equals("lbl.gov")) {
            if (platform.equals("HuEx-1_0-st-v2")) {
                if (filename.endsWith(".gene.txt") || filename.endsWith(".exon.txt")) {
                    return "quantile_normalization_protocol_type";
                } else if (filename.endsWith(".firma.txt")) {
                    return "quantile_normalization_protocol_type";
                }
            }
        } else if (center.equals("hudsonalpha.org")) {
            if (platform.equals("HumanHap550")) {
                if (filename.endsWith("B_Allele_Freq.txt")) {
                    return "B_allele_freq";
                }
            }
        } else if (center.equals("jhu-usc.edu")) {
            if (platform.equals("IlluminaDNAMethylation_OMA002_CPI")) {
                if (filename.endsWith(".beta-value.txt") || filename.endsWith(".detection-p-value.txt")) {
                    return "beta_and_p_value";
                }
            }
        } else if (center.equals("unc.edu")) {
            if (platform.equals("H-miRNA_8x15K")) {
                if (filename.endsWith(".tcga_level2.data.txt")) {
                    return "Quantile_Normalized";
                }
            }
        } else if (center.equals("hms.harvard.edu")) {
            if (filename.endsWith("lowess_normalized.tsv")) {
                return "Lowess Normalized";
            }
        }
        return null;
    }
}