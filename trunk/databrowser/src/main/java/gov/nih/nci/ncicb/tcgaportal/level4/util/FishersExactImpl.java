/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.util;

/**
 * An implementation of the Fishers's Exact algorithm Adapted from Oyvind Langsrud's Javascript implementation
 * http://www.matforsk.no/ola/calculateFisherTwoTail.htm
 * <p/>
 * Copied from gov.nih.nci.lmp.gominer.datamodel
 *
 * @author Jessica Chen
 *         Last updated by: $Author: nichollsmc $
 * @version $Rev: 9488 $
 */

public class FishersExactImpl implements FishersExact {

    /**
     * The left, single-sided Fisher pValue
     */
    private double left;
    /**
     * The right, single-sided Fisher pValue
     */
    private double right;
    /**
     * The two-sided Fisher pValue
     */
    private double twotail;
    private double sleft, sright, sless, slarg;
    private int sn11, sn1_, sn_1, sn;
    private double sprob;

    /**
     * Calculates the 2-tailed Fisher p value, using the counts that are expected to be readily available in the system
     *
     * @param totalChanged  The number of genes changed in the experiment
     * @param changedInNode The number of genes changed in a particular node
     * @param total         The total number of genes in the experiment (changed and unchanged)
     * @param inNode        The number of genes in the node (changed and unchanged)
     * @return 2-tailed Fisher p value
     */
    public double calculateFisherTwoTail(final int totalChanged, final int changedInNode, final int total,
                                         final int inNode) {
        return calculateFisherFromMatrix(changedInNode,
                totalChanged - changedInNode,
                inNode - changedInNode,
                (total - inNode) - (totalChanged - changedInNode));
    }

    private double calculateFisherFromMatrix(final int n11, final int n12, final int n21, final int n22) {
        int n11_ = n11;
        int n12_ = n12;
        int n21_ = n21;
        int n22_ = n22;
        if (n11_ < 0) {
            n11_ *= -1;
        }
        if (n12_ < 0) {
            n12_ *= -1;
        }
        if (n21_ < 0) {
            n21_ *= -1;
        }
        if (n22_ < 0) {
            n22_ *= -1;
        }
        final int n1_ = n11_ + n12_;
        final int n_1 = n11_ + n21_;
        final int n = n11_ + n12_ + n21_ + n22_;
        exact(n11_, n1_, n_1, n);
        left = sless;
        right = slarg;
        twotail = sleft + sright;
        if (twotail > 1) {
            twotail = 1;
        }
        return twotail;
    }

    /**
     * @return the fisher left tail value from last calculation
     */
    public double getFisherLeftTail() {
        return left;
    }

    public double calculateFisherLeftTail(final int totalChanged, final int changedInNode, final int total,
                                          final int inNode) {
        calculateFisherFromMatrix(changedInNode,
                totalChanged - changedInNode,
                inNode - changedInNode,
                (total - inNode) - (totalChanged - changedInNode));
        return getFisherLeftTail();
    }

    /**
     * Accessor
     *
     * @return the fisher right tail from last calculation
     */
    public double getFisherRightTail() {
        return right;
    }

    public double calculateFisherRightTail(final int totalChanged, final int changedInNode, final int total,
                                           final int inNode) {
        calculateFisherFromMatrix(changedInNode,
                totalChanged - changedInNode,
                inNode - changedInNode,
                (total - inNode) - (totalChanged - changedInNode));
        return getFisherRightTail();
    }

    /**
     * Accessor
     *
     * @return the fisher two-tail from last calculation
     */
    public double calculateFisherTwoTail() {
        return twotail;
    }

    private static double lngamm(final int z) {
        double x = 0;
        x += 0.1659470187408462e-06 / (z + 7);
        x += 0.9934937113930748e-05 / (z + 6);
        x -= 0.1385710331296526 / (z + 5);
        x += 12.50734324009056 / (z + 4);
        x -= 176.6150291498386 / (z + 3);
        x += 771.3234287757674 / (z + 2);
        x -= 1259.139216722289 / (z + 1);
        x += 676.5203681218835 / (z);
        x += 0.9999999999995183;
        return (Math.log(x) - 5.58106146679532777 - z + (z - 0.5) * Math.log(z + 6.5));
    }

    private static double lnfact(final int n) {
        if (n <= 1) {
            return (0);
        }
        return (lngamm(n + 1));
    }

    private static double lnbico(final int n, final int k) {
        return (lnfact(n) - lnfact(k) - lnfact(n - k));
    }

    private static double hyper_323(final int n11, final int n1_, final int n_1, final int n) {
        return (Math.exp(lnbico(n1_, n11) + lnbico(n - n1_, n_1 - n11) - lnbico(n, n_1)));
    }

    private double hyper(final int n11) {
        return (hyper0(n11, 0, 0, 0));
    }

    private double hyper0(final int n11i, final int n1_i, final int n_1i, final int ni) {
        if (((n1_i == 0) & (n_1i == 0) & (ni == 0))) {
            if (!(n11i % 10 == 0)) {
                if (n11i == sn11 + 1) {
                    sprob =
                            sprob * (((double) sn1_ - (double) sn11) / ((double) n11i)) *
                                    (((double) sn_1 - (double) sn11) / ((double) n11i + (double) sn - (double) sn1_ - (double) sn_1));
                    sn11 = n11i;
                    return sprob;
                }
                if (n11i == sn11 - 1) {
                    sprob = sprob * (((double) sn11) / ((double) sn1_ - (double) n11i)) *
                            (((double) sn11 + (double) sn - (double) sn1_ - (double) sn_1) / ((double) sn_1 -
                                    (double) n11i));
                    sn11 = n11i;
                    return sprob;
                }
            }
            sn11 = n11i;
        } else {
            sn11 = n11i;
            sn1_ = n1_i;
            sn_1 = n_1i;
            sn = ni;
        }
        sprob = hyper_323(sn11, sn1_, sn_1, sn);
        return sprob;
    }

    private double exact(final int n11, final int n1_, final int n_1, final int n) {
        int i, j;
        double p;
        final double prob;
        int max = n1_;
        if (n_1 < max) {
            max = n_1;
        }
        int min = n1_ + n_1 - n;
        if (min < 0) {
            min = 0;
        }
        if (min == max) {
            sless = 1;
            sright = 1;
            sleft = 1;
            slarg = 1;
            return 1;
        }
        prob = hyper0(n11, n1_, n_1, n);
        sleft = 0;
        p = hyper(min);
        for (i = min + 1; p < 0.99999999 * prob; i++) {
            sleft += p;
            p = hyper(i);
        }
        i--;
        if (p < 1.00000001 * prob) {
            sleft += p;
        } else {
            i--;
        }
        sright = 0;
        p = hyper(max);
        for (j = max - 1; p < 0.99999999 * prob; j--) {
            sright += p;
            p = hyper(j);
        }
        j++;
        if (p < 1.00000001 * prob) {
            sright += p;
        } else {
            j++;
        }
        if (Math.abs(i - n11) < Math.abs(j - n11)) {
            sless = sleft;
            slarg = 1 - sleft + prob;
        } else {
            sless = 1 - sright + prob;
            slarg = sright;
        }
        return prob;
    }


}
