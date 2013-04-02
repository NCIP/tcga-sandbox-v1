package org.broadinstitute.sting.gatk.walkers.varianteval.stratifications;

import org.broadinstitute.sting.utils.variantcontext.Allele;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;
import org.broadinstitute.sting.gatk.contexts.ReferenceContext;
import org.broadinstitute.sting.gatk.refdata.RefMetaDataTracker;
import org.broadinstitute.sting.gatk.walkers.varianteval.util.SortableJexlVCMatchExp;
import org.broadinstitute.sting.utils.MathUtils;
import org.broadinstitute.sting.utils.exceptions.UserException;

import java.util.ArrayList;
import java.util.Set;

public class AlleleCount extends VariantStratifier {
    // needs to know the variant context
    private ArrayList<String> states = new ArrayList<String>();

    @Override
    public void initialize(Set<SortableJexlVCMatchExp> jexlExpressions, Set<String> compNames, Set<String> knownNames, Set<String> evalNames, Set<String> sampleNames, Set<String> contigNames) {
        // we can only work with a single eval VCF, and it must have genotypes
        if ( evalNames.size() != 1 )
            throw new UserException.BadArgumentValue("AlleleCount", "AlleleCount stratification only works with a single eval vcf");

        // There are 2 x n sample chromosomes for diploids
        int nchrom = getVariantEvalWalker().getSampleNamesForEvaluation().size() * 2;
        if ( nchrom < 2 )
            throw new UserException.BadArgumentValue("AlleleCount", "AlleleCount stratification requires an eval vcf with at least one sample");

        // create an array containing each of the allele counts
        for( int ac = 0; ac <= nchrom; ac++ ) {
            states.add(String.format("%d", ac));
        }

        getVariantEvalWalker().getLogger().info("AlleleCount using " + nchrom + " chromosomes");
    }

    public ArrayList<String> getAllStates() {
        return states;
    }

    public ArrayList<String> getRelevantStates(ReferenceContext ref, RefMetaDataTracker tracker, VariantContext comp, String compName, VariantContext eval, String evalName, String sampleName) {
        ArrayList<String> relevantStates = new ArrayList<String>(1);

        if (eval != null) {
            int AC = -1;
            if ( eval.hasAttribute("AC") )
                AC = eval.getAttributeAsInt("AC");
            else if ( eval.isVariant() ) {
                for (Allele allele : eval.getAlternateAlleles())
                    AC = Math.max(AC, eval.getChromosomeCount(allele));
            } else
                // by default, the site is considered monomorphic
                AC = 0;
            relevantStates.add(String.format("%d", AC));
        }

        return relevantStates;
    }
}
