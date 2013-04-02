package org.broadinstitute.sting.gatk.walkers.sequenom;

import org.broadinstitute.sting.utils.variantcontext.VariantContext;
import org.broadinstitute.sting.gatk.contexts.AlignmentContext;
import org.broadinstitute.sting.gatk.contexts.ReferenceContext;
import org.broadinstitute.sting.gatk.refdata.RefMetaDataTracker;
import org.broadinstitute.sting.gatk.walkers.RodWalker;
import org.broadinstitute.sting.commandline.Output;
import org.broadinstitute.sting.utils.GenomeLoc;

import java.io.PrintStream;

/**
 * Create a mask for use with the PickSequenomProbes walker.
 */
public class CreateSequenomMask extends RodWalker<Integer, Integer> {
    @Output
    PrintStream out;

    public void initialize() {}

	public Integer map(RefMetaDataTracker tracker, ReferenceContext ref, AlignmentContext context) {
        if ( tracker == null )
            return 0;

        int result = 0;
        for ( VariantContext vc : tracker.getAllVariantContexts(ref) ) {
            if ( vc.isSNP() ) {
                GenomeLoc loc = context.getLocation();
                out.println(loc.getContig() + "\t" + (loc.getStart()-1) + "\t" + loc.getStop());
                result = 1;
                break;
            }
        }

        return result;
    }

    public Integer reduceInit() {
        return 0;
    }

	public Integer reduce(Integer value, Integer sum) {
		return value + sum;
	}

    public void onTraversalDone(Integer sum) {
        logger.info("Found " + sum + " masking sites.");
    }
}