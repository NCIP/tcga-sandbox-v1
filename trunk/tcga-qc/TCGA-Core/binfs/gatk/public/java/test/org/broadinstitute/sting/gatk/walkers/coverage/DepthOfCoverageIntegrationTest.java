package org.broadinstitute.sting.gatk.walkers.coverage;

import org.broadinstitute.sting.WalkerTest;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * IF THERE IS NO JAVADOC RIGHT HERE, YELL AT chartl
 *
 * @Author chartl
 * @Date Feb 25, 2010
 */
public class DepthOfCoverageIntegrationTest extends WalkerTest {

    private boolean RUN_TESTS = true;
    private String root = "-T DepthOfCoverage ";

    private String buildRootCmd(String ref, List<String> bams, List<String> intervals) {
        StringBuilder bamBuilder = new StringBuilder();
        do {
            bamBuilder.append(" -I ");
            bamBuilder.append(bams.remove(0));
        } while ( bams.size() > 0 );

        StringBuilder intervalBuilder = new StringBuilder();
        do {
            intervalBuilder.append(" -L ");
            intervalBuilder.append(intervals.remove(0));
        } while ( intervals.size() > 0 );


        return root + "-R "+ref+bamBuilder.toString()+intervalBuilder.toString();
    }

    private void execute(String name, WalkerTestSpec spec) {
        if ( RUN_TESTS ) {
            executeTest(name,spec);
        }
    }

    @Test
    public void testBaseOutputNoFiltering() {
        String[] intervals = {"/humgen/gsa-hpprojects/GATK/data/Validation_Data/fhs_jhs_30_targts.interval_list"};
        String[] bams = {"/humgen/gsa-hpprojects/GATK/data/Validation_Data/FHS_indexed_subset.bam"};

        String cmd = buildRootCmd(hg18Reference,new ArrayList<String>(Arrays.asList(bams)),new ArrayList<String>(Arrays.asList(intervals))) + " -mmq 0 -mbq 0 -dels -baseCounts -pt readgroup -pt sample -pt library --outputFormat csv -ct 10 -ct 15 -ct 20 -ct 25";
        WalkerTestSpec spec = new WalkerTestSpec(cmd,0, new ArrayList<String>());

        // our base file
        File baseOutputFile = this.createTempFile("depthofcoveragenofiltering",".tmp");
        spec.setOutputFileLocation(baseOutputFile);

        // now add the expected files that get generated
        spec.addAuxFile("423571e4c05e7934322172654ac6dbb7", baseOutputFile);
        spec.addAuxFile("9df5e7e07efeb34926c94a724714c219", createTempFileFromBase(baseOutputFile.getAbsolutePath()+".library_cumulative_coverage_counts"));
        spec.addAuxFile("b9a7748e5aec4dc06daed893c901c00d", createTempFileFromBase(baseOutputFile.getAbsolutePath()+".library_cumulative_coverage_proportions"));
        spec.addAuxFile("9cd395f47b329b9dd00ad024fcac9929", createTempFileFromBase(baseOutputFile.getAbsolutePath()+".library_interval_statistics"));
        spec.addAuxFile("aec669d64d9dd652dd088a5341835ea5", createTempFileFromBase(baseOutputFile.getAbsolutePath()+".library_interval_summary"));
        spec.addAuxFile("f6dbd74d32a48abe71ce08d300bce983", createTempFileFromBase(baseOutputFile.getAbsolutePath()+".library_statistics"));
        spec.addAuxFile("e3a3467ed259ee3680f8d01980f525b7", createTempFileFromBase(baseOutputFile.getAbsolutePath()+".library_summary"));
        spec.addAuxFile("b82846df660f0aac8429aec57c2a62d6", createTempFileFromBase(baseOutputFile.getAbsolutePath()+".read_group_cumulative_coverage_counts"));
        spec.addAuxFile("d32a8c425fadcc4c048bd8b48d0f61e5", createTempFileFromBase(baseOutputFile.getAbsolutePath()+".read_group_cumulative_coverage_proportions"));
        spec.addAuxFile("7b9d0e93bf5b5313995be7010ef1f528", createTempFileFromBase(baseOutputFile.getAbsolutePath()+".read_group_interval_statistics"));
        spec.addAuxFile("2aae346204c5f15517158da8e61a6c16", createTempFileFromBase(baseOutputFile.getAbsolutePath()+".read_group_interval_summary"));
        spec.addAuxFile("e70952f241eebb9b5448f2e7cb288131", createTempFileFromBase(baseOutputFile.getAbsolutePath()+".read_group_statistics"));
        spec.addAuxFile("054ed1e184f46d6a170dc9bf6524270c", createTempFileFromBase(baseOutputFile.getAbsolutePath()+".read_group_summary"));
        spec.addAuxFile("d53431022f7387fe9ac47814ab1fcd88", createTempFileFromBase(baseOutputFile.getAbsolutePath()+".sample_cumulative_coverage_counts"));
        spec.addAuxFile("650ee3714da7fbad7832c9d4ad49eb51", createTempFileFromBase(baseOutputFile.getAbsolutePath()+".sample_cumulative_coverage_proportions"));
        spec.addAuxFile("df0ba76e0e6082c0d29fcfd68efc6b77", createTempFileFromBase(baseOutputFile.getAbsolutePath()+".sample_interval_statistics"));
        spec.addAuxFile("7dcac2e8962c778081486332a4576dc3", createTempFileFromBase(baseOutputFile.getAbsolutePath()+".sample_interval_summary"));
        spec.addAuxFile("a50011571334f17e950ad3ed1149e350", createTempFileFromBase(baseOutputFile.getAbsolutePath()+".sample_statistics"));
        spec.addAuxFile("6f3260504295695d765af639539585c9", createTempFileFromBase(baseOutputFile.getAbsolutePath()+".sample_summary"));
        
        execute("testBaseOutputNoFiltering",spec);
    }

    @Test
    public void testNoCoverageDueToFiltering() {
        String[] intervals = {"/humgen/gsa-hpprojects/GATK/data/Validation_Data/fhs_jhs_30_targts.interval_list"};
        String[] bams = {"/humgen/gsa-hpprojects/GATK/data/Validation_Data/FHS_indexed_subset.bam"};

        String cmd = buildRootCmd(hg18Reference,new ArrayList<String>(Arrays.asList(bams)),new ArrayList<String>(Arrays.asList(intervals))) + " -mmq 0 -mbq 5 --maxBaseQuality 4 -dels -baseCounts -pt readgroup -pt sample -pt library --outputFormat csv";
        WalkerTestSpec spec = new WalkerTestSpec(cmd,0, new ArrayList<String>());

        File baseOutputFile = this.createTempFile("depthofcoveragenofiltering",".tmp");
        spec.setOutputFileLocation(baseOutputFile);

        spec.addAuxFile("6ccd7d8970ba98cb95fe41636a070c1c",baseOutputFile);
        spec.addAuxFile("0ee40f3e5091536c14e077b77557083a",createTempFileFromBase(baseOutputFile.getAbsolutePath()+".library_interval_summary"));

        execute("testNoCoverageDueToFiltering",spec);
    }
}
