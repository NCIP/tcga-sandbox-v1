package org.broadinstitute.sting.gatk.walkers.fasta;

import org.broadinstitute.sting.WalkerTest;
import org.testng.annotations.Test;

import java.util.Arrays;

public class FastaAlternateReferenceIntegrationTest extends WalkerTest {
    @Test
    public void testIntervals() {

        String md5_1 = "328d2d52cedfdc52da7d1abff487633d";

        WalkerTestSpec spec1a = new WalkerTestSpec(
                "-T FastaAlternateReferenceMaker -R " + b36KGReference + " -L 1:10,000,100-10,000,500;1:10,100,000-10,101,000;1:10,900,000-10,900,001 -o %s",
                 1,
                 Arrays.asList(md5_1));
        executeTest("testFastaReference", spec1a);

        WalkerTestSpec spec1b = new WalkerTestSpec(
                "-T FastaReferenceMaker -R " + b36KGReference + " -L 1:10,000,100-10,000,500;1:10,100,000-10,101,000;1:10,900,000-10,900,001 -o %s",
                 1,
                 Arrays.asList(md5_1));
        executeTest("testFastaReference", spec1b);

        WalkerTestSpec spec2 = new WalkerTestSpec(
                "-T FastaAlternateReferenceMaker -R " + b36KGReference + " -B:indels,VCF " + validationDataLocation + "NA12878.chr1_10mb_11mb.slx.indels.vcf4 -B:snpmask,dbsnp " + GATKDataLocation + "dbsnp_129_b36.rod -L 1:10,075,000-10,075,380;1:10,093,447-10,093,847;1:10,271,252-10,271,452 -o %s",
                 1,
                 Arrays.asList("3a48986c3832a768b478c3e95f994b0f"));
        executeTest("testFastaAlternateReferenceIndels", spec2);

        WalkerTestSpec spec3 = new WalkerTestSpec(
                "-T FastaAlternateReferenceMaker -R " + b36KGReference + " -B:snps,GeliText " + validationDataLocation + "NA12878.chr1_10mb_11mb.slx.geli.calls -B:snpmask,dbsnp " + GATKDataLocation + "dbsnp_129_b36.rod -L 1:10,023,400-10,023,500;1:10,029,200-10,029,500 -o %s",
                 1,
                 Arrays.asList("82705a88f6fc25880dd2331183531d9a"));
        executeTest("testFastaAlternateReferenceSnps", spec3);
    }
}
