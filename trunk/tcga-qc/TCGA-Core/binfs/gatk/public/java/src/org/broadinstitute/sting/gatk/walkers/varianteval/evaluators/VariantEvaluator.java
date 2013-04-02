package org.broadinstitute.sting.gatk.walkers.varianteval.evaluators;

import org.broadinstitute.sting.utils.variantcontext.VariantContext;
import org.broadinstitute.sting.gatk.contexts.AlignmentContext;
import org.broadinstitute.sting.gatk.contexts.ReferenceContext;
import org.broadinstitute.sting.gatk.refdata.RefMetaDataTracker;
import org.broadinstitute.sting.gatk.walkers.varianteval.VariantEvalWalker;
import org.broadinstitute.sting.gatk.walkers.varianteval.util.NewEvaluationContext;
import org.broadinstitute.sting.gatk.walkers.varianteval.util.StateKey;

public abstract class VariantEvaluator {
    public void initialize(VariantEvalWalker walker) {}

    public abstract boolean enabled();

    // Should return the number of VariantContexts expected as inputs to update.  Can be 1 or 2
    public abstract int getComparisonOrder();

    // called at all sites, regardless of eval context itself; useful for counting processed bases
    public void update0(RefMetaDataTracker tracker, ReferenceContext ref, AlignmentContext context) { }

    public String update1(VariantContext vc1, RefMetaDataTracker tracker, ReferenceContext ref, AlignmentContext context) {
        return null;
    }

    public String update1(VariantContext vc1, RefMetaDataTracker tracker, ReferenceContext ref, AlignmentContext context, NewEvaluationContext group) {
        return update1(vc1, tracker, ref, context);
    }


    public String update2(VariantContext vc1, VariantContext vc2, RefMetaDataTracker tracker, ReferenceContext ref, AlignmentContext context) {
        return null;
    }

    public String update2(VariantContext vc1, VariantContext vc2, RefMetaDataTracker tracker, ReferenceContext ref, AlignmentContext context, NewEvaluationContext group) {
        return update2(vc1, vc2, tracker, ref, context);
    }

    public void finalizeEvaluation() {}

    protected double rate(long n, long d) {
        return n / (1.0 * Math.max(d, 1));
    }

    protected long inverseRate(long n, long d) {
        return n == 0 ? 0 : d / Math.max(n, 1);
    }

    protected double ratio(long num, long denom) {
        return ((double)num) / (Math.max(denom, 1));
    }

    public boolean stateIsApplicable(StateKey stateKey) {
        return true;
    }

}
