package org.broadinstitute.sting.utils.codecs.vcf;

import org.broad.tribble.Tribble;
import org.broad.tribble.index.*;
import org.broad.tribble.iterators.CloseableTribbleIterator;
import org.broad.tribble.source.BasicFeatureSource;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * tests out the various functions in the index factory class
 */
public class IndexFactoryUnitTest {

    File inputFile = new File("testdata/HiSeq.10000.vcf");
    File outputFile = new File("testdata/onTheFlyOutputTest.vcf");
    File outputFileIndex = Tribble.indexFile(outputFile);

    /**
     * test out scoring the indexes
     */
    @Test
    public void testScoreIndexes() {
        /*// make a list of indexes to score
        Map<Class,IndexCreator> creators = new HashMap<Class,IndexCreator>();
        // add a linear index with the default bin size
        LinearIndexCreator linearNormal = new LinearIndexCreator();
        linearNormal.initialize(inputFile, linearNormal.defaultBinSize());
        creators.add(LInearIndexlinearNormal);

        // create a tree index with a small index size
        IntervalIndexCreator treeSmallBin = new IntervalIndexCreator();
        treeSmallBin.initialize(inputFile, Math.max(200,treeSmallBin.defaultBinSize()/10));
        creators.add(treeSmallBin);

        List<Index> indexes = new ArrayList<Index>();
        for (IndexCreator creator : creators)
            indexes.add(creator.finalizeIndex(0));

        ArrayList<Double> scores = IndexFactory.scoreIndexes(0.5,indexes,100, IndexFactory.IndexBalanceApproach.FOR_SEEK_TIME);
        System.err.println("scores are : ");
        for (Double score : scores) {
            System.err.println(score);
*/
    }

    //
    // test out scoring the indexes
    //
    @Test
    public void testOnTheFlyIndexing1() throws IOException {
        Index indexFromInputFile = IndexFactory.createIndex(inputFile, new VCFCodec());
        if ( outputFileIndex.exists() ) {
            System.err.println("Deleting " + outputFileIndex);
            outputFileIndex.delete();
        }

        for ( int maxRecords : Arrays.asList(0, 1, 10, 100, 1000, -1)) {
            BasicFeatureSource<VariantContext> source = new BasicFeatureSource<VariantContext>(inputFile.getAbsolutePath(), indexFromInputFile, new VCFCodec());

            int counter = 0;
            VCFWriter writer = new StandardVCFWriter(outputFile);
            writer.writeHeader((VCFHeader)source.getHeader());
            CloseableTribbleIterator<VariantContext> it = source.iterator();
            while (it.hasNext() && (counter++ < maxRecords || maxRecords == -1) ) {
                VariantContext vc = it.next();
                writer.add(vc, vc.getReferenceBaseForIndel());
            }
            writer.close();

            // test that the input index is the same as the one created from the identical input file
            // test that the dynamic index is the same as the output index, which is equal to the input index
            Assert.assertTrue(IndexFactory.onDiskIndexEqualToNewlyCreatedIndex(outputFile, outputFileIndex, new VCFCodec()));
        }
    }
}
