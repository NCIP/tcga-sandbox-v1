package org.broadinstitute.sting.gatk.walkers.variantrecalibration;

import org.broadinstitute.sting.WalkerTest;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

import java.util.*;

public class VariantRecalibrationWalkersIntegrationTest extends WalkerTest {
    static HashMap<String, String> clusterFiles = new HashMap<String, String>();
    static HashMap<String, String> tranchesFiles = new HashMap<String, String>();
    static HashMap<String, String> inputVCFFiles = new HashMap<String, String>();

    private static class VRTest {
        String inVCF;
        String tranchesMD5;
        String recalMD5;
        String cutVCFMD5;
        public VRTest(String inVCF, String tranchesMD5, String recalMD5, String cutVCFMD5) {
            this.inVCF = validationDataLocation + inVCF;
            this.tranchesMD5 = tranchesMD5;
            this.recalMD5 = recalMD5;
            this.cutVCFMD5 = cutVCFMD5;
        }
    }

    VRTest lowPass = new VRTest("phase1.projectConsensus.chr20.raw.snps.vcf",
            "d33212a84368e821cbedecd4f59756d6",  // tranches
            "a35cd067f378442eee8cd5edeea92be0",  // recal file
            "126d52843f4a57199ee97750ffc16a07"); // cut VCF

    @DataProvider(name = "VRTest")
    public Object[][] createData1() {
        return new Object[][]{ {lowPass} };
        //return new Object[][]{ {yriTrio}, {lowPass} }; // Add hg19 chr20 trio calls here
    }

    @Test(dataProvider = "VRTest")
    public void testVariantRecalibrator(VRTest params) {
        //System.out.printf("PARAMS FOR %s is %s%n", vcf, clusterFile);
        WalkerTest.WalkerTestSpec spec = new WalkerTest.WalkerTestSpec(
                "-R " + b37KGReference +
                        " -B:dbsnp,VCF,known=true,training=false,truth=false,prior=10.0 " + GATKDataLocation + "dbsnp_132_b37.leftAligned.vcf" +
                        " -B:hapmap,VCF,known=false,training=true,truth=true,prior=15.0 " + comparisonDataLocation + "Validated/HapMap/3.3/sites_r27_nr.b37_fwd.vcf" +
                        " -B:omni,VCF,known=false,training=true,truth=true,prior=12.0 " + comparisonDataLocation + "Validated/Omni2.5_chip/Omni25_sites_1525_samples.b37.vcf" +
                        " -T VariantRecalibrator" +
                        " -B:input,VCF " + params.inVCF +
                        " -L 20:1,000,000-40,000,000" +
                        " -an QD -an HaplotypeScore -an HRun" +
                        " -percentBad 0.07" +
                        " --minNumBadVariants 0" +
                        " --trustAllPolymorphic" + // for speed
                        " -recalFile %s" +
                        " -tranchesFile %s",
                Arrays.asList(params.recalMD5, params.tranchesMD5));
        executeTest("testVariantRecalibrator-"+params.inVCF, spec).getFirst();
    }

    @Test(dataProvider = "VRTest",dependsOnMethods="testVariantRecalibrator")
    public void testApplyRecalibration(VRTest params) {
        WalkerTest.WalkerTestSpec spec = new WalkerTest.WalkerTestSpec(
                "-R " + b37KGReference +
                        " -T ApplyRecalibration" +
                        " -L 20:12,000,000-30,000,000" +
                        " -NO_HEADER" +
                        " -B:input,VCF " + params.inVCF +
                        " -o %s" +
                        " -tranchesFile " + getFileForMD5(params.tranchesMD5) +
                        " -recalFile " + getFileForMD5(params.recalMD5),
                Arrays.asList(params.cutVCFMD5));
        executeTest("testApplyRecalibration-"+params.inVCF, spec);
    }
}

