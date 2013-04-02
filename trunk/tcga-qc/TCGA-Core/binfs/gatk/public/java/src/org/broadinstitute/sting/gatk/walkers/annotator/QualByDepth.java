package org.broadinstitute.sting.gatk.walkers.annotator;

import org.broadinstitute.sting.utils.variantcontext.Genotype;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;
import org.broadinstitute.sting.utils.codecs.vcf.VCFHeaderLineType;
import org.broadinstitute.sting.utils.codecs.vcf.VCFInfoHeaderLine;
import org.broadinstitute.sting.gatk.contexts.AlignmentContext;
import org.broadinstitute.sting.gatk.contexts.ReferenceContext;
import org.broadinstitute.sting.gatk.refdata.RefMetaDataTracker;
import org.broadinstitute.sting.gatk.walkers.annotator.interfaces.InfoFieldAnnotation;
import org.broadinstitute.sting.gatk.walkers.annotator.interfaces.StandardAnnotation;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;


public class QualByDepth extends AnnotationByDepth implements InfoFieldAnnotation, StandardAnnotation {

    public Map<String, Object> annotate(RefMetaDataTracker tracker, ReferenceContext ref, Map<String, AlignmentContext> stratifiedContexts, VariantContext vc) {
        if ( stratifiedContexts.size() == 0 )
            return null;

        final Map<String, Genotype> genotypes = vc.getGenotypes();
        if ( genotypes == null || genotypes.size() == 0 )
            return null;

        int depth = 0;

        for ( Map.Entry<String, Genotype> genotype : genotypes.entrySet() ) {

            // we care only about variant calls with likelihoods
            if ( genotype.getValue().isHomRef() )
                continue;

            AlignmentContext context = stratifiedContexts.get(genotype.getKey());
            if ( context == null )
                continue;

            depth += context.size();
        }

        if ( depth == 0 )
            return null;

        int qDepth = annotationByVariantDepth(genotypes, stratifiedContexts);
        double QD = 10.0 * vc.getNegLog10PError() / (double)qDepth;

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(getKeyNames().get(0), String.format("%.2f", QD));
        return map;
    }

    public List<String> getKeyNames() { return Arrays.asList("QD"); }

    public List<VCFInfoHeaderLine> getDescriptions() { return Arrays.asList(new VCFInfoHeaderLine(getKeyNames().get(0), 1, VCFHeaderLineType.Float, "Variant Confidence/Quality by Depth")); }

}