package org.broadinstitute.sting.utils;

import net.sf.samtools.SAMUtils;

/**
 * QualityUtils is a static class (no instantiation allowed!) with some utility methods for manipulating
 * quality scores.
 *
 * @author Kiran Garimella
 */
public class QualityUtils {
    public final static byte MAX_QUAL_SCORE = SAMUtils.MAX_PHRED_SCORE;
    public final static double MIN_REASONABLE_ERROR = 0.0001;
    public final static byte MAX_REASONABLE_Q_SCORE = 40;

    /**
     * Private constructor.  No instantiating this class!
     */
    private QualityUtils() {}

    /**
     * Convert a quality score to a probability.  This is the Phred-style
     * conversion, *not* the Illumina-style conversion (though asymptotically, they're the same).
     *
     * @param qual a quality score (0-40)
     * @return a probability (0.0-1.0)
     */
    static public double qualToProb(byte qual) {
        return 1.0 - qualToErrorProb(qual);
    }

    static public double qualToProb(int qual) {
        return qualToProb( (double)qual );
    }

    static public double qualToProb(double qual) {
        return 1.0 - Math.pow(10.0, qual/(-10.0));
    }

    /**
     * Convert a quality score to a probability of error.  This is the Phred-style
     * conversion, *not* the Illumina-style conversion (though asymptotically, they're the same).
     *
     * @param qual a quality score (0-40)
     * @return a probability (0.0-1.0)
     */
    static public double qualToErrorProb(byte qual) {
        return Math.pow(10.0, ((double) qual)/-10.0);
    }

    /**
     * Convert a probability to a quality score.  Note, this is capped at Q40.
     *
     * @param prob a probability (0.0-1.0)
     * @return a quality score (0-40)
     */
    static public byte probToQual(double prob) {
        return probToQual(prob, MIN_REASONABLE_ERROR);
        //return (byte) Math.round(-10.0*Math.log10(1.0 - prob + 0.0001));
    }

    /**
     * Convert a probability to a quality score.  Note, this is capped at a quality score which is determined by _eps_.
     *
     * @param prob a probability (0.0-1.0)
     * @param eps min probabilty allowed (0.0-1.0)
     * @return a quality score (0-255)
     */
    static public byte probToQual(double prob, double eps) {
        double lp = Math.round(-10.0*Math.log10(1.0 - prob + eps));
        byte b = boundQual((int)lp);
        //System.out.printf("LP is %f, byte is %d%n", lp, b);
        return b;
    }

    static public double phredScaleCorrectRate(double trueRate) {
        return phredScaleErrorRate(1-trueRate);
    }

    static public double phredScaleErrorRate(double errorRate) {
        return Math.abs(-10.0*Math.log10(errorRate));
    }

    static public double lodToPhredScaleErrorRate(double lod) {
        return phredScaleErrorRate(1.0 / (Math.pow(10.0, lod) + 1.0));
    }
    
    /**
     * Return a quality score, capped at max qual.
     *
     * @param qual  the uncapped quality score
     * @return the capped quality score
     */
    static public byte boundQual(int qual) {
        return boundQual(qual, MAX_QUAL_SCORE);
    }

    /**
     * Returns an integer quality score bounded by 1 - maxQual.
     *
     * @param qual
     * @param maxQual
     * @return
     */
    static public byte boundQual(int qual, byte maxQual) {
        //return (byte) Math.min(qual, maxQual);
        return (byte) Math.max(Math.min(qual, maxQual), 1);
    }

    /**
     * Compress a base and a probability into a single byte so that it can be output in a SAMRecord's SQ field.
     * Note: the highest probability this function can encode is 64%, so this function should only never be used on the best base hypothesis.
     * Another note: the probability encoded here gets rounded to the nearest 1%.
     *
     * @param baseIndex the base index
     * @param prob      the base probability
     * @return a byte containing the index and the probability
     */
    static public byte baseAndProbToCompressedQuality(int baseIndex, double prob) {
        byte compressedQual = 0;

        compressedQual = (byte) baseIndex;

        byte cprob = (byte) (100.0*prob);
        byte qualmask = (byte) 252;
        compressedQual += ((cprob << 2) & qualmask);
        
        return compressedQual;
    }

    /**
     * From a compressed base, extract the base index (0:A, 1:C, 2:G, 3:T)
     *
     * @param compressedQual the compressed quality score, as returned by baseAndProbToCompressedQuality
     * @return base index
     */
    static public int compressedQualityToBaseIndex(byte compressedQual) {
        return (int) (compressedQual & 0x3);
    }

    /**
     * From a compressed base, extract the base probability
     *
     * @param compressedQual the compressed quality score, as returned by baseAndProbToCompressedQuality
     * @return the probability
     */
    static public double compressedQualityToProb(byte compressedQual) {
        // Because java natives are signed, extra care must be taken to avoid
        // shifting a 1 into the sign bit in the implicit promotion of 2 to an int.
        int x2 = ((int) compressedQual) & 0xff;
        x2 = (x2 >>> 2);

        return ((double) x2)/100.0;
    }

    /**
     * Return the complement of a compressed quality
     *
     * @param compressedQual  the compressed quality score (as returned by baseAndProbToCompressedQuality)
     * @return the complementary compressed quality
     */
    static public byte complementCompressedQuality(byte compressedQual) {
        int baseIndex = compressedQualityToBaseIndex(compressedQual);
        double prob = compressedQualityToProb(compressedQual);

        return baseAndProbToCompressedQuality(BaseUtils.complementIndex(baseIndex), prob);
    }

    /**
     * Return the reverse complement of a byte array of compressed qualities
     *
     * @param compressedQuals  a byte array of compressed quality scores
     * @return the reverse complement of the byte array
     */
    static public byte[] reverseComplementCompressedQualityArray(byte[] compressedQuals) {
        byte[] rcCompressedQuals = new byte[compressedQuals.length];

        for (int pos = 0; pos < compressedQuals.length; pos++) {
            rcCompressedQuals[compressedQuals.length - pos - 1] = complementCompressedQuality(compressedQuals[pos]);
        }

        return rcCompressedQuals;
    }

    /**
     * Return the reverse of a byte array of qualities (compressed or otherwise)
     * @param quals   the array of bytes to be reversed
     * @return the reverse of the quality array
     */
    static public byte[] reverseQualityArray( byte[] quals ) {
        return Utils.reverse(quals); // no sense in duplicating functionality
    }
}
