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

package org.broadinstitute.sting.gatk;

import net.sf.picard.reference.IndexedFastaSequenceFile;
import net.sf.picard.util.Interval;
import net.sf.picard.util.IntervalList;
import net.sf.samtools.SAMFileHeader;
import org.broadinstitute.sting.BaseTest;
import org.broadinstitute.sting.commandline.ArgumentException;
import org.broadinstitute.sting.gatk.arguments.GATKArgumentCollection;
import org.broadinstitute.sting.gatk.datasources.reads.SAMReaderID;
import org.broadinstitute.sting.commandline.Tags;
import org.broadinstitute.sting.gatk.walkers.PrintReadsWalker;
import org.broadinstitute.sting.utils.GenomeLoc;
import org.broadinstitute.sting.utils.GenomeLocParser;
import org.broadinstitute.sting.utils.GenomeLocSortedSet;

import org.broadinstitute.sting.utils.exceptions.UserException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * Tests selected functionality in the GenomeAnalysisEngine class
 */
public class GenomeAnalysisEngineUnitTest extends BaseTest {

    @Test(expectedExceptions=ArgumentException.class)
    public void testDuplicateSamFileHandlingSingleDuplicate() throws Exception {
        GenomeAnalysisEngine testEngine = new GenomeAnalysisEngine();

        Collection<SAMReaderID> samFiles = new ArrayList<SAMReaderID>();
        samFiles.add(new SAMReaderID(new File("testdata/exampleBAM.bam"), new Tags()));
        samFiles.add(new SAMReaderID(new File("testdata/exampleBAM.bam"), new Tags()));

        testEngine.setSAMFileIDs(samFiles);
        testEngine.checkForDuplicateSamFiles();
    }

    @Test(expectedExceptions=ArgumentException.class)
    public void testDuplicateSamFileHandlingMultipleDuplicates() throws Exception {
        GenomeAnalysisEngine testEngine = new GenomeAnalysisEngine();

        Collection<SAMReaderID> samFiles = new ArrayList<SAMReaderID>();
        samFiles.add(new SAMReaderID(new File("testdata/exampleBAM.bam"),  new Tags()));
        samFiles.add(new SAMReaderID(new File("testdata/exampleNORG.bam"), new Tags()));
        samFiles.add(new SAMReaderID(new File("testdata/exampleBAM.bam"),  new Tags()));
        samFiles.add(new SAMReaderID(new File("testdata/exampleNORG.bam"), new Tags()));

        testEngine.setSAMFileIDs(samFiles);
        testEngine.checkForDuplicateSamFiles();
    }

    @Test(expectedExceptions=ArgumentException.class)
    public void testEmptyIntervalSetHandling() throws Exception {
        GenomeAnalysisEngine testEngine = new GenomeAnalysisEngine();

        testEngine.setWalker(new PrintReadsWalker());
        testEngine.setIntervals(new GenomeLocSortedSet(null));

        testEngine.validateSuppliedIntervals();
    }

    @DataProvider(name="invalidIntervalTestData")
    public Object[][] invalidIntervalDataProvider() throws Exception {
        GenomeAnalysisEngine testEngine = new GenomeAnalysisEngine();
        GATKArgumentCollection argCollection = new GATKArgumentCollection();
        testEngine.setArguments(argCollection);

        File fastaFile = new File("testdata/exampleFASTA.fasta");
        GenomeLocParser genomeLocParser = new GenomeLocParser(new IndexedFastaSequenceFile(fastaFile));
        testEngine.setGenomeLocParser(genomeLocParser);

        return new Object[][] {
                new Object[] {testEngine, genomeLocParser, "chr1", 10000000, 20000000},
                new Object[] {testEngine, genomeLocParser, "chr2", 1, 2},
                new Object[] {testEngine, genomeLocParser, "chr1", -1, 50}
        };
    }

    @Test(expectedExceptions=UserException.class, dataProvider="invalidIntervalTestData")
    public void testInvalidRODIntervalHandling(GenomeAnalysisEngine testEngine, GenomeLocParser genomeLocParser,
                                               String contig, int intervalStart, int intervalEnd ) throws Exception {

        List<String> intervalArgs = new ArrayList<String>();
        List<GenomeLoc> rodIntervals = Arrays.asList(genomeLocParser.createGenomeLoc(contig, intervalStart, intervalEnd, true));

        testEngine.loadIntervals(intervalArgs, rodIntervals);
    }

    @Test(expectedExceptions=UserException.class, dataProvider="invalidIntervalTestData")
    public void testInvalidBedIntervalHandling(GenomeAnalysisEngine testEngine, GenomeLocParser genomeLocParser,
                                               String contig, int intervalStart, int intervalEnd ) throws Exception {
        // We need to adjust intervalStart, since BED intervals are 0-based. We don't need to adjust intervalEnd,
        // since the ending point is an open interval.
        File bedFile = createTempFile("testInvalidBedIntervalHandling", ".bed",
                                      String.format("%s %d %d", contig, intervalStart -1, intervalEnd));

        List<String> intervalArgs = Arrays.asList(bedFile.getAbsolutePath());
        List<GenomeLoc> rodIntervals = new ArrayList<GenomeLoc>();

        testEngine.loadIntervals(intervalArgs, rodIntervals);
    }

    @Test(expectedExceptions=UserException.class, dataProvider="invalidIntervalTestData")
    public void testInvalidPicardIntervalHandling(GenomeAnalysisEngine testEngine, GenomeLocParser genomeLocParser,
                                                  String contig, int intervalStart, int intervalEnd ) throws Exception {

        SAMFileHeader picardFileHeader = new SAMFileHeader();
        picardFileHeader.addSequence(genomeLocParser.getContigInfo("chr1"));
        IntervalList picardIntervals = new IntervalList(picardFileHeader);
        picardIntervals.add(new Interval(contig, intervalStart, intervalEnd, true, "dummyname"));

        File picardIntervalFile = createTempFile("testInvalidPicardIntervalHandling", ".intervals");
        picardIntervals.write(picardIntervalFile);

        List<String> intervalArgs = Arrays.asList(picardIntervalFile.getAbsolutePath());
        List<GenomeLoc> rodIntervals = new ArrayList<GenomeLoc>();

        testEngine.loadIntervals(intervalArgs, rodIntervals);
    }

    @Test(expectedExceptions=UserException.class, dataProvider="invalidIntervalTestData")
    public void testInvalidGATKFileIntervalHandling(GenomeAnalysisEngine testEngine, GenomeLocParser genomeLocParser,
                                                    String contig, int intervalStart, int intervalEnd ) throws Exception {

        File gatkIntervalFile = createTempFile("testInvalidGATKFileIntervalHandling", ".intervals",
                                               String.format("%s:%d-%d", contig, intervalStart, intervalEnd));

        List<String> intervalArgs = Arrays.asList(gatkIntervalFile.getAbsolutePath());
        List<GenomeLoc> rodIntervals = new ArrayList<GenomeLoc>();

        testEngine.loadIntervals(intervalArgs, rodIntervals);
    }

    private File createTempFile( String tempFilePrefix, String tempFileExtension, String... lines ) throws Exception {
        File tempFile = File.createTempFile(tempFilePrefix, tempFileExtension);
        tempFile.deleteOnExit();

        PrintWriter out = new PrintWriter(tempFile);
        for ( String line : lines ) {
            out.println(line);
        }
        out.close();

        return tempFile;
    }
}
