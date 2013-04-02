/*
 * Copyright (c) 2010, The Broad Institute
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package org.broadinstitute.sting.gatk.walkers.variantutils;

import org.broadinstitute.sting.WalkerTest;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Arrays;

/**
 * Tests CombineVariants
 */
public class CombineVariantsIntegrationTest extends WalkerTest {
//    public static String baseTestString(String args) {
//        return "-T CombineVariants -NO_HEADER -L 1:1-50,000,000 -o %s -R " + b36KGReference + args;
//    }
//
//    public void test1InOut(String file, String md5, boolean vcf3) {
//        test1InOut(file, md5, "", vcf3);
//    }
//
//    public void test1InOut(String file, String md5, String args, boolean vcf3) {
//         WalkerTestSpec spec = new WalkerTestSpec(
//                 baseTestString(" -priority v1 -B:v1,VCF" + (vcf3 ? "3 " : " ") + validationDataLocation + file + args),
//                 1,
//                 Arrays.asList(md5));
//         executeTest("testInOut1--" + file, spec);
//    }
//
//    public void combine2(String file1, String file2, String args, String md5, boolean vcf3) {
//         WalkerTestSpec spec = new WalkerTestSpec(
//                 baseTestString(" -priority v1,v2 -B:v1,VCF" + (vcf3 ? "3 " : " ") + validationDataLocation + file1 + " -B:v2,VCF" + (vcf3 ? "3 " : " ") + validationDataLocation + file2 + args),
//                 1,
//                 Arrays.asList(md5));
//         executeTest("combine2 1:" + new File(file1).getName() + " 2:" + new File(file2).getName(), spec);
//    }
//
//    public void combineSites(String args, String md5) {
//        String file1 = "1000G_omni2.5.b37.sites.vcf";
//        String file2 = "hapmap_3.3.b37.sites.vcf";
//        WalkerTestSpec spec = new WalkerTestSpec(
//                "-T CombineVariants -NO_HEADER -o %s -R " + b37KGReference
//                        + " -L 1:1-10,000,000 -B:omni,VCF " + validationDataLocation + file1
//                        + " -B:hm3,VCF " + validationDataLocation + file2 + args,
//                1,
//                Arrays.asList(md5));
//        executeTest("combineSites 1:" + new File(file1).getName() + " 2:" + new File(file2).getName() + " args = " + args, spec);
//    }
//
//
//    @Test public void test1SNP() { test1InOut("pilot2.snps.vcf4.genotypes.vcf", "2117fff6e0d182cd20be508e9661829c", true); }
//    @Test public void test2SNP() { test1InOut("pilot2.snps.vcf4.genotypes.vcf", "2cfaf7af3dd119df08b8a9c1f72e2f93", " -setKey foo", true); }
//    @Test public void test3SNP() { test1InOut("pilot2.snps.vcf4.genotypes.vcf", "1474ac0fde2ce42a3c24f1c97eab333e", " -setKey null", true); }
//    @Test public void testOfficialCEUPilotCalls() { test1InOut("CEU.trio.2010_03.genotypes.vcf.gz", "7fc66df048a0ab08cf507906e1d4a308", false); } // official project VCF files in tabix format
//
//    @Test public void test1Indel1() { test1InOut("CEU.dindel.vcf4.trio.2010_06.indel.genotypes.vcf", "ec9715f53dbf4531570557c212822f12", false); }
//    @Test public void test1Indel2() { test1InOut("CEU.dindel.vcf4.low_coverage.2010_06.indel.genotypes.vcf", "f1072be5f5c6ee810276d9ca6537224d", false); }
//
//    @Test public void combineTrioCalls() { combine2("CEU.trio.2010_03.genotypes.vcf.gz", "YRI.trio.2010_03.genotypes.vcf.gz", "", "b77a1eec725201d9d8e74ee0c45638d3", false); } // official project VCF files in tabix format
//    @Test public void combineTrioCallsMin() { combine2("CEU.trio.2010_03.genotypes.vcf.gz", "YRI.trio.2010_03.genotypes.vcf.gz", " -minimalVCF", "802977fdfd2f4905b501bb06800f60af", false); } // official project VCF files in tabix format
//    @Test public void combine2Indels() { combine2("CEU.dindel.vcf4.trio.2010_06.indel.genotypes.vcf", "CEU.dindel.vcf4.low_coverage.2010_06.indel.genotypes.vcf", "", "a67157287dd2b24b5cdf7ebf8fcbbe9a", false); }
//
//    @Test public void combineSNPsAndIndels() { combine2("CEU.trio.2010_03.genotypes.vcf.gz", "CEU.dindel.vcf4.low_coverage.2010_06.indel.genotypes.vcf", "", "e1f4718a179f1196538a33863da04f53", false); }
//
//    @Test public void uniqueSNPs() { combine2("pilot2.snps.vcf4.genotypes.vcf", "yri.trio.gatk_glftrio.intersection.annotated.filtered.chr1.vcf", "", "b3783384b7c8e877b971033e90beba48", true); }
//
//    @Test public void omniHM3Union() { combineSites(" -filteredRecordsMergeType KEEP_IF_ANY_UNFILTERED", "902e541c87caa72134db6293fc46f0ad"); }
//    @Test public void omniHM3Intersect() { combineSites(" -filteredRecordsMergeType KEEP_IF_ALL_UNFILTERED", "f339ad4bb5863b58b9c919ce7d040bb9"); }
//
//    @Test public void threeWayWithRefs() {
//        WalkerTestSpec spec = new WalkerTestSpec(
//                baseTestString(" -B:NA19240_BGI,VCF "+validationDataLocation+"NA19240.BGI.RG.vcf" +
//                        " -B:NA19240_ILLUMINA,VCF "+validationDataLocation+"NA19240.ILLUMINA.RG.vcf" +
//                        " -B:NA19240_WUGSC,VCF "+validationDataLocation+"NA19240.WUGSC.RG.vcf" +
//                        " -B:denovoInfo,VCF "+validationDataLocation+"yri_merged_validation_data_240610.annotated.b36.vcf" +
//                        " -setKey centerSet" +
//                        " -filteredRecordsMergeType KEEP_IF_ANY_UNFILTERED" +
//                        " -priority NA19240_BGI,NA19240_ILLUMINA,NA19240_WUGSC,denovoInfo" +
//                        " -genotypeMergeOptions UNIQUIFY -L 1"),
//                1,
//                Arrays.asList("a07995587b855f3214fb71940bf23c0f"));
//        executeTest("threeWayWithRefs", spec);
//    }


    // complex examples with filtering, indels, and multiple alleles
    public void combineComplexSites(String args, String md5) {
        String file1 = "combine.1.vcf";
        String file2 = "combine.2.vcf";
        WalkerTestSpec spec = new WalkerTestSpec(
                "-T CombineVariants -NO_HEADER -o %s -R " + b37KGReference
                        + " -B:one,VCF " + validationDataLocation + file1
                        + " -B:two,VCF " + validationDataLocation + file2 + args,
                1,
                Arrays.asList(md5));
        executeTest("combineComplexSites 1:" + new File(file1).getName() + " 2:" + new File(file2).getName() + " args = " + args, spec);
    }

    @Test public void complexTestFull() { combineComplexSites("", "64b991fd3850f83614518f7d71f0532f"); }
    @Test public void complexTestMinimal() { combineComplexSites(" -minimalVCF", "0db9ef50fe54b60426474273d7c7fa99"); }
    @Test public void complexTestSitesOnly() { combineComplexSites(" -sites_only", "d20acb3d53ba0a02ce92d540ebeda2a9"); }
    @Test public void complexTestSitesOnlyMinimal() { combineComplexSites(" -sites_only -minimalVCF", "8d1b3d120515f8b56b5a0d10bc5da713"); }
}