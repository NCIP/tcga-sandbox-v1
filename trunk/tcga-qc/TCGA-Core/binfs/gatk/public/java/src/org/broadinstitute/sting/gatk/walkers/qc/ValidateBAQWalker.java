package org.broadinstitute.sting.gatk.walkers.qc;

import net.sf.samtools.SAMRecord;
import net.sf.samtools.CigarElement;
import net.sf.samtools.CigarOperator;
import net.sf.picard.reference.IndexedFastaSequenceFile;
import org.broadinstitute.sting.gatk.refdata.ReadMetaDataTracker;
import org.broadinstitute.sting.gatk.walkers.*;
import org.broadinstitute.sting.gatk.contexts.ReferenceContext;
import org.broadinstitute.sting.utils.exceptions.StingException;
import org.broadinstitute.sting.utils.exceptions.ReviewedStingException;
import org.broadinstitute.sting.utils.baq.BAQ;
import org.broadinstitute.sting.utils.MathUtils;
import org.broadinstitute.sting.utils.Utils;
import org.broadinstitute.sting.utils.SimpleTimer;
import org.broadinstitute.sting.commandline.Output;
import org.broadinstitute.sting.commandline.Argument;

import java.io.PrintStream;

/**
 * Walks over the input data set, calculating the number of reads seen for diagnostic purposes.
 * Can also count the number of reads matching a given criterion using read filters (see the
 * --read-filter command line argument).  Simplest example of a read-backed analysis.
 */
@BAQMode(QualityMode = BAQ.QualityMode.DONT_MODIFY, ApplicationTime = BAQ.ApplicationTime.HANDLED_IN_WALKER)
@Reference(window=@Window(start=-5,stop=5))
@Requires({DataSource.READS, DataSource.REFERENCE, DataSource.REFERENCE_BASES})
public class ValidateBAQWalker extends ReadWalker<Integer, Integer> {
    @Output(doc="File to which results should be written",required=true)
    protected PrintStream out;

    @Argument(doc="maximum read length to apply the BAQ calculation too",required=false)
    protected int maxReadLen = 1000;

    @Argument(doc="",required=false)
    protected int bw = 7;

    @Argument(doc="",required=false)
    protected boolean samtoolsMode = false;

    @Argument(doc="only operates on reads with this name",required=false)
    protected String readName = null;

    @Argument(doc="If true, all differences are errors", required=false)
    protected boolean strict = false;

    @Argument(doc="prints info for each read", required=false)
    protected boolean printEachRead = false;

    @Argument(doc="Also prints out detailed comparison information when for known calculation differences", required=false)
    protected boolean alsoPrintWarnings = false;

    @Argument(doc="Include reads without BAQ tag", required=false)
    protected boolean includeReadsWithoutBAQTag = false;

    @Argument(doc="x each read is processed", required=false)
    protected int magnification = 1;

    @Argument(doc="Profile performance", required=false)
    protected boolean profile = false;

    int counter = 0;

    BAQ baqHMM = null;         // matches current samtools parameters

    public void initialize() {
        if ( samtoolsMode )
            baqHMM = new BAQ(1e-3, 0.1, bw, (byte)0, true);
        else
            baqHMM = new BAQ();
    }

    long goodReads = 0, badReads = 0;

    public Integer map(ReferenceContext ref, SAMRecord read, ReadMetaDataTracker tracker) {

        if ( (readName == null || readName.equals(read.getReadName())) && read.getReadLength() <= maxReadLen && (includeReadsWithoutBAQTag || BAQ.hasBAQTag(read) ) ) {
            if ( baqHMM.excludeReadFromBAQ(read) )
                return 0;

            if ( profile ) {
                profileBAQ(ref, read);
            } else {
                validateBAQ(ref, read);
            }

            return 1;
        }

        return 0;
    }

    SimpleTimer tagTimer = new SimpleTimer("from.tag");
    SimpleTimer baqReadTimer = new SimpleTimer("baq.read");
    SimpleTimer glocalTimer = new SimpleTimer("hmm.glocal");

    private void profileBAQ(ReferenceContext ref, SAMRecord read) {
        IndexedFastaSequenceFile refReader = this.getToolkit().getReferenceDataSource().getReference();
        BAQ.BAQCalculationResult baq = null;

        tagTimer.restart();
        for ( int i = 0; i < magnification; i++ ) { BAQ.calcBAQFromTag(read, false, includeReadsWithoutBAQTag); }
        tagTimer.stop();

        baqReadTimer.restart();
        for ( int i = 0; i < magnification; i++ ) { baqHMM.baqRead(read, refReader, BAQ.CalculationMode.RECALCULATE, BAQ.QualityMode.DONT_MODIFY ); }
        baqReadTimer.stop();

        glocalTimer.restart();
        for ( int i = 0; i < magnification; i++ )
            baqHMM.baqRead(read, refReader, BAQ.CalculationMode.RECALCULATE, BAQ.QualityMode.DONT_MODIFY);
        glocalTimer.stop();
    }


    private void validateBAQ(ReferenceContext ref, SAMRecord read) {
        IndexedFastaSequenceFile refReader = this.getToolkit().getReferenceDataSource().getReference();
        byte[] baqFromTag = BAQ.calcBAQFromTag(read, false, includeReadsWithoutBAQTag);
        if (counter++ % 1000 == 0 || printEachRead) out.printf("Checking read %s (%d)%n", read.getReadName(), counter);

        BAQ.BAQCalculationResult baq = baqHMM.calcBAQFromHMM(read, refReader);

        boolean fail = false;
        boolean print = false;
        int badi = 0;

        if ( BAQ.hasBAQTag(read) ) {
            for ( badi = 0; badi < baqFromTag.length; badi++ ) {
                if ( baqFromTag[badi] != baq.bq[badi] ) {
                    if ( cigarLength(read) != read.getReadLength() ) {
                        print = true;
                        fail = false;
                        out.printf("  different, but cigar length != read length%n");
                        break;
                    }
                    if (MathUtils.arrayMin(read.getBaseQualities()) == 0) {
                        print = true;
                        fail = strict;
                        out.printf("  different, but Q0 base detected%n");
                        break;
                    }
                    else if (readHasSoftClip(read) && ! samtoolsMode) {
                        print = true;
                        fail = strict;
                        out.printf("  different, but soft clip detected%n");
                        break;
                    } else if (readHasDeletion(read) ) { // && ! samtoolsMode) {
                        print = true;
                        fail = strict;
                        out.printf("  different, but deletion detected%n");
                        break;
                    } else if ( baq.bq[badi] < baqHMM.getMinBaseQual() ) {
                        print = fail = true;
                        out.printf("  Base quality %d < min %d", baq.bq[badi], baqHMM.getMinBaseQual());
                        break;
                    } else {
                        print = fail = true;
                        break;
                    }
                }
            }
            if ( fail || print )
                badReads++;
            else
                goodReads++;
        }

        if ( fail || printEachRead || ( print && alsoPrintWarnings ) ) {
            byte[] pos = new byte[baq.bq.length];
            for ( int i = 0; i < pos.length; i++ ) pos[i] = (byte)i;

            out.printf("  read length   : %d%n", read.getReadLength());
            out.printf("  read start    : %d (%d unclipped)%n", read.getAlignmentStart(), read.getUnclippedStart());
            out.printf("  cigar         : %s%n", read.getCigarString());
            out.printf("  ref bases     : %s%n", new String(baq.refBases));
            out.printf("  read bases    : %s%n", new String(read.getReadBases()));
            out.printf("  ref length    : %d%n", baq.refBases.length);
            out.printf("  BQ tag        : %s%n", read.getStringAttribute(BAQ.BAQ_TAG));
            if ( BAQ.hasBAQTag(read) ) printQuals("  BQ deltas     : ", getBAQDeltas(read), true);
            printQuals("  original quals: ", read.getBaseQualities(), true);
            printQuals("  baq      quals: ", baq.bq, true);
            printQuals("  positions     : ", pos, true);
            printQuals("  original quals: ", read.getBaseQualities());
            if ( BAQ.hasBAQTag(read) ) printQuals("  tag      quals: ", baqFromTag);
            printQuals("  hmm      quals: ", baq.bq);
            out.printf("  read bases    : %s%n", new String(read.getReadBases()));
            out.println(Utils.dupString('-', 80));
        }


        if ( fail )
            throw new StingException(String.format("BAQ from read and from HMM differ in read %s at position %d: tag qual = %d, hmm qual = %d",
                    read.getReadName(), badi, baqFromTag[badi], baq.bq[badi]));
    }

    private final static boolean readHasSoftClip(SAMRecord read) {
        for (CigarElement e : read.getCigar().getCigarElements()) {
            if ( e.getOperator() == CigarOperator.SOFT_CLIP )
                return true;
        }

        return false;
    }

    private final static boolean readHasDeletion(SAMRecord read) {
        for (CigarElement e : read.getCigar().getCigarElements()) {
            if ( e.getOperator() == CigarOperator.DELETION )
                return true;
        }

        return false;
    }

    public final void printQuals( String prefix, byte[] quals ) {
        printQuals(prefix, quals, false);
    }

    public final void printQuals( String prefix, byte[] quals, boolean asInt ) {
        printQuals(out, prefix, quals, asInt);
    }

    public final static void printQuals( PrintStream out, String prefix, byte[] quals, boolean asInt ) {
        out.print(prefix);
        for ( int i = 0; i < quals.length; i++) {
            if ( asInt ) {
                out.printf("%2d", (int)quals[i]);
                if ( i+1 != quals.length ) out.print(",");
            } else
                out.print((char)(quals[i]+33));
        }
        out.println();
    }

    /**
     * Get the BAQ delta bytes from the tag in read.  Returns null if no BAQ tag is present.
     * @param read
     * @return
     */
    public static byte[] getBAQDeltas(SAMRecord read) {
        byte[] baq = BAQ.getBAQTag(read);
        if ( baq != null ) {
            byte[] deltas = new byte[baq.length];
            for ( int i = 0; i < deltas.length; i++)
                deltas[i] = (byte)(-1 * (baq[i] - 64));
            return deltas;
        } else
            return null;
    }

    private int cigarLength(SAMRecord read) {
        int readI = 0;
        for ( CigarElement elt : read.getCigar().getCigarElements() ) {
            int l = elt.getLength();
            switch (elt.getOperator()) {
                case N: // cannot handle these
                    return 0;
                case H : case P : // ignore pads and hard clips
                    break;
                case S :
                case I :
                    readI += l;
                    break;
                case D : break;
                case M :
                    readI += l;
                    break;
                default:
                    throw new ReviewedStingException("BUG: Unexpected CIGAR element " + elt + " in read " + read.getReadName());
            }
        }
        return readI;
    }

    public Integer reduceInit() { return 0; }

    public Integer reduce(Integer value, Integer sum) {
        return value + sum;
    }

    public void onTraversalDone(Integer nreads) {
        if ( profile ) {
            out.printf("n.reads baq.per.read calculation time.in.secs%n");
            printTimer(nreads, tagTimer);
            printTimer(nreads, glocalTimer);
            printTimer(nreads, baqReadTimer);
        } else {
            out.printf("total reads BAQ'd %d; concordant BAQ reads %d %.4f; discordant BAQ reads %d %.4f%n", nreads,
                    goodReads, (100.0 * goodReads) / nreads,
                    badReads, (100.0 * badReads) / nreads);
        }
    }

    private final void printTimer(int nreads, SimpleTimer timer) {
        out.printf("%d %d %s %.2f%n", nreads, magnification, timer.getName(), timer.getElapsedTime());
    }
}

