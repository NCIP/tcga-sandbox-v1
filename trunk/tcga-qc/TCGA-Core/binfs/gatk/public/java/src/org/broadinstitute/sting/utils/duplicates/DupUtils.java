/*
 * Copyright (c) 2010 The Broad Institute
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
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.broadinstitute.sting.utils.duplicates;

import net.sf.samtools.SAMRecord;
import org.broadinstitute.sting.utils.*;
import org.broadinstitute.sting.utils.collections.Pair;
import org.broadinstitute.sting.utils.exceptions.ReviewedStingException;
import org.broadinstitute.sting.utils.pileup.ReadBackedPileup;
import org.broadinstitute.sting.utils.pileup.PileupElement;
import org.broadinstitute.sting.utils.pileup.ReadBackedPileupImpl;

import java.util.List;
import java.util.Arrays;

public class DupUtils {
    private static SAMRecord tmpCopyRead(SAMRecord read) {
        try {
            return (SAMRecord)read.clone();
        } catch ( CloneNotSupportedException e ) {
            throw new ReviewedStingException("Unexpected Clone failure!");
        }
    }

    public static SAMRecord combineDuplicates(GenomeLocParser genomeLocParser,List<SAMRecord> duplicates, int maxQScore) {
        if ( duplicates.size() == 0 )
            return null;

        // make the combined read by copying the first read and setting the
        // bases and quals to new arrays
        SAMRecord comb = tmpCopyRead(duplicates.get(0));
        //SAMRecord comb = tmpCopyRead(duplicates.get(0));
        comb.setDuplicateReadFlag(false);
        int readLen = comb.getReadBases().length;
        byte[] bases = new byte[readLen];
        byte[] quals = new byte[readLen];

        for ( int i = 0; i < readLen; i++ ) {
            //System.out.printf("I is %d%n", i);
            //for ( SAMRecord read : duplicates ) {
            //    System.out.printf("dup base %c %d%n", (char)read.getReadBases()[i], read.getBaseQualities()[i]);
            //}
            Pair<Byte, Byte> baseAndQual = combineBaseProbs(genomeLocParser,duplicates, i, maxQScore);
            bases[i] = baseAndQual.getFirst();
            quals[i] = baseAndQual.getSecond();            
        }


        comb.setBaseQualities(quals);
        comb.setReadBases(bases);

        return comb;
    }

    private static Pair<Byte, Byte> baseProbs2BaseAndQual(double[] probs, int maxQScore) {
        byte bestBase = 0;
        double bestProb = Double.NEGATIVE_INFINITY;
        double sumProbs = 0;

        for ( int i = 0; i < 4; i++ ) {
            sumProbs += Math.pow(10, probs[i]);
            //System.out.printf("Bestprob is %f > %f%n", bestProb, probs[i]);
            if ( probs[i] > bestProb ) {
                bestBase = BaseUtils.baseIndexToSimpleBase(i);
                bestProb = probs[i];
            }
        }

        Arrays.sort(probs);
        double normalizedP = Math.pow(10, bestProb) / sumProbs;
        double eps = Math.pow(10, -maxQScore/10.0);
        byte qual = QualityUtils.probToQual(normalizedP, eps);
//        if ( false ) {
//            System.out.printf("Best base is %s %.8f%n", bestBase, bestProb);
//            System.out.printf("2nd  base is %.8f%n", probs[1]);
//            System.out.printf("normalized P %.8f%n", normalizedP);
//            System.out.printf("normalized Q %.8f%n", 1 - normalizedP);
//            System.out.printf("max Q        %2d%n", maxQScore);
//            System.out.printf("eps          %.8f%n", eps);
//            System.out.printf("encoded    Q %2d%n", qual);
//        }

        return new Pair<Byte, Byte>(bestBase, qual);
    }

    private static void print4BaseQuals(String header, double[] probs) {
        System.out.printf("%s log10(P(b)) is ", header);
        for ( int i = 0; i < 4; i++ ) {
            System.out.printf("%c=%+.8f ", (char)BaseUtils.baseIndexToSimpleBase(i), probs[i]);
        }
        System.out.printf("%n");
    }

    private static Pair<Byte, Byte> combineBaseProbs(GenomeLocParser genomeLocParser,List<SAMRecord> duplicates, int readOffset, int maxQScore) {
        GenomeLoc loc = genomeLocParser.createGenomeLoc(duplicates.get(0));
        ReadBackedPileup pileup = new ReadBackedPileupImpl(loc, duplicates, readOffset);

        final boolean debug = false;

        // calculate base probs
        double[] qualSums = {0.0, 0.0, 0.0, 0.0};
        if ( debug ) print4BaseQuals("start", qualSums);

        for (PileupElement e : pileup ) {
            int baseIndex = e.getBaseIndex();
            byte qual = e.getQual();
            double pqual = QualityUtils.qualToProb(qual);
            for ( int j = 0; j < 4; j++) {
                qualSums[j] += Math.log10(j == baseIndex ?  pqual : (1 - pqual)/3);
            }

            if ( debug ) print4BaseQuals(String.format("%c Q%2d", e.getBase(), qual), qualSums);
        }
        if ( debug ) print4BaseQuals("final", qualSums);

        Pair<Byte, Byte> combined = baseProbs2BaseAndQual(qualSums, maxQScore);
        if ( debug ) System.out.printf("%s => %c Q%s%n", pileup.getPileupString('N'), (char)(byte)combined.getFirst(), combined.getSecond());

        return combined;
    }
}