package org.broadinstitute.sting.gatk.walkers.varianteval.evaluators;

import org.broadinstitute.sting.utils.variantcontext.Allele;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;
import org.broadinstitute.sting.gatk.contexts.AlignmentContext;
import org.broadinstitute.sting.gatk.contexts.ReferenceContext;
import org.broadinstitute.sting.gatk.refdata.RefMetaDataTracker;
import org.broadinstitute.sting.gatk.walkers.varianteval.tags.Analysis;
import org.broadinstitute.sting.gatk.walkers.varianteval.tags.DataPoint;

/**
 * The Broad Institute
 * SOFTWARE COPYRIGHT NOTICE AGREEMENT
 * This software and its documentation are copyright 2009 by the
 * Broad Institute/Massachusetts Institute of Technology. All rights are reserved.
 * <p/>
 * This software is supplied without any warranty or guaranteed support whatsoever. Neither
 * the Broad Institute nor MIT can be responsible for its use, misuse, or functionality.
 */
@Analysis(description = "The overlap between eval and comp sites")
public class CompOverlap extends VariantEvaluator implements StandardEval {
    @DataPoint(description = "number of eval SNP sites")
    long nEvalVariants = 0;

    @DataPoint(description = "number of comp SNP sites")
    long nCompVariants = 0;

    @DataPoint(description = "number of eval sites outside of comp sites")
    long novelSites = 0;

    @DataPoint(description = "number of eval sites at comp sites")
    long nVariantsAtComp = 0;

    @DataPoint(description = "percentage of eval sites at comp sites")
    double compRate = 0.0;

    @DataPoint(description = "number of concordant sites")
    long nConcordant = 0;

    @DataPoint(description = "the concordance rate")
    double concordantRate = 0.0;

    public int getComparisonOrder() {
        return 2;   // we need to see each eval track and each comp track
    }

    public long nNovelSites() { return nEvalVariants - nVariantsAtComp; }
    public double compRate() { return rate(nVariantsAtComp, nEvalVariants); }
    public double concordanceRate() { return rate(nConcordant, nVariantsAtComp); }

    public void finalizeEvaluation() {
        compRate = 100 * compRate();
        concordantRate = 100 * concordanceRate();
        novelSites = nNovelSites();
    }

    public boolean enabled() {
        return true;
    }

    /**
     * Returns true if every allele in eval is also in comp
     *
     * @param eval  eval context
     * @param comp db context
     * @return true if eval and db are discordant
     */
    public boolean discordantP(VariantContext eval, VariantContext comp) {
        for (Allele a : eval.getAlleles()) {
            if (!comp.hasAllele(a, true))
                return true;
        }

        return false;
    }

    public String update2(VariantContext eval, VariantContext comp, RefMetaDataTracker tracker, ReferenceContext ref, AlignmentContext context) {
        boolean evalIsGood = eval != null && eval.isVariant();
        boolean expectingIndels = eval != null && eval.isIndel();

        boolean compIsGood = expectingIndels ? comp != null && comp.isNotFiltered() && comp.isIndel() : comp != null && comp.isNotFiltered() && comp.isSNP() ;

        if (compIsGood) nCompVariants++;           // count the number of comp events
        if (evalIsGood) nEvalVariants++;           // count the number of eval events

        if (compIsGood && evalIsGood) {
            nVariantsAtComp++;

            if (!discordantP(eval, comp)) {    // count whether we're concordant or not with the comp value
                nConcordant++;
            }
        }

        return null; // we don't capture any interesting sites
    }
}
