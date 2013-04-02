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

package org.broadinstitute.sting.gatk.walkers.beagle;

import org.broadinstitute.sting.utils.variantcontext.Allele;
import org.broadinstitute.sting.utils.variantcontext.Genotype;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;
import org.broadinstitute.sting.commandline.Argument;
import org.broadinstitute.sting.commandline.Output;
import org.broadinstitute.sting.gatk.contexts.AlignmentContext;
import org.broadinstitute.sting.gatk.contexts.ReferenceContext;
import org.broadinstitute.sting.utils.variantcontext.VariantContextUtils;
import org.broadinstitute.sting.gatk.datasources.rmd.ReferenceOrderedDataSource;
import org.broadinstitute.sting.gatk.refdata.features.beagle.BeagleFeature;
import org.broadinstitute.sting.gatk.refdata.RefMetaDataTracker;
import org.broadinstitute.sting.gatk.walkers.RodWalker;
import org.broadinstitute.sting.gatk.walkers.RMD;
import org.broadinstitute.sting.gatk.walkers.Requires;
import org.broadinstitute.sting.utils.GenomeLoc;
import org.broadinstitute.sting.utils.SampleUtils;
import org.broadinstitute.sting.utils.codecs.vcf.VCFUtils;
import org.broadinstitute.sting.utils.codecs.vcf.*;

import java.util.*;
import static java.lang.Math.log10;


/**
 * Takes files produced by Beagle imputation engine and creates a vcf with modified annotations.
 */
@Requires(value={},referenceMetaData=@RMD(name=BeagleOutputToVCFWalker.INPUT_ROD_NAME, type=VariantContext.class))

public class BeagleOutputToVCFWalker  extends RodWalker<Integer, Integer> {

    public static final String INPUT_ROD_NAME = "variant";
    public static final String COMP_ROD_NAME = "comp";
    public static final String R2_ROD_NAME = "beagleR2";
    public static final String PROBS_ROD_NAME = "beagleProbs";
    public static final String PHASED_ROD_NAME = "beaglePhased";

    @Output(doc="File to which variants should be written",required=true)
    protected VCFWriter vcfWriter = null;

    @Argument(fullName="output_file", shortName="output", doc="Please use --out instead" ,required=false)
    @Deprecated
    protected String oldOutputArg;

    @Argument(fullName="dont_mark_monomorphic_sites_as_filtered", shortName="keep_monomorphic", doc="If provided, we won't filter sites that beagle tags as monomorphic.  Useful for imputing a sample's genotypes from a reference panel" ,required=false)
    public boolean DONT_FILTER_MONOMORPHIC_SITES = false;

    @Argument(fullName="no" +
            "call_threshold", shortName="ncthr", doc="Threshold of confidence at which a genotype won't be called", required=false)
    private double noCallThreshold = 0.0;

    protected static String line = null;

    private final double MIN_PROB_ERROR = 0.000001;
    private final double MAX_GENOTYPE_QUALITY = 6.0;

    public void initialize() {

        // setup the header fields

        final Set<VCFHeaderLine> hInfo = new HashSet<VCFHeaderLine>();
        hInfo.addAll(VCFUtils.getHeaderFields(getToolkit()));
        hInfo.add(new VCFFormatHeaderLine("OG",1, VCFHeaderLineType.String, "Original Genotype input to Beagle"));
        hInfo.add(new VCFInfoHeaderLine("R2", 1, VCFHeaderLineType.Float, "r2 Value reported by Beagle on each site"));
        hInfo.add(new VCFInfoHeaderLine("NumGenotypesChanged", 1, VCFHeaderLineType.Integer, "The number of genotypes changed by Beagle"));
        hInfo.add(new VCFFilterHeaderLine("BGL_RM_WAS_A", "This 'A' site was set to monomorphic by Beagle"));
        hInfo.add(new VCFFilterHeaderLine("BGL_RM_WAS_C", "This 'C' site was set to monomorphic by Beagle"));
        hInfo.add(new VCFFilterHeaderLine("BGL_RM_WAS_G", "This 'G' site was set to monomorphic by Beagle"));
        hInfo.add(new VCFFilterHeaderLine("BGL_RM_WAS_T", "This 'T' site was set to monomorphic by Beagle"));

        // Open output file specified by output VCF ROD
        final List<ReferenceOrderedDataSource> dataSources = this.getToolkit().getRodDataSources();

        for( final ReferenceOrderedDataSource source : dataSources ) {
            if (source.getName().equals(COMP_ROD_NAME)) {
                hInfo.add(new VCFInfoHeaderLine("ACH", 1, VCFHeaderLineType.Integer, "Allele Count from Comparison ROD at this site"));
                hInfo.add(new VCFInfoHeaderLine("ANH", 1, VCFHeaderLineType.Integer, "Allele Frequency from Comparison ROD at this site"));
                hInfo.add(new VCFInfoHeaderLine("AFH", 1, VCFHeaderLineType.Float, "Allele Number from Comparison ROD at this site"));
                break;
            }

        }

        Set<String> samples = SampleUtils.getSampleListWithVCFHeader(getToolkit(), Arrays.asList(INPUT_ROD_NAME));

        final VCFHeader vcfHeader = new VCFHeader(hInfo, samples);
        vcfWriter.writeHeader(vcfHeader);
    }

    public Integer map( RefMetaDataTracker tracker, ReferenceContext ref, AlignmentContext context ) {

        if ( tracker == null )
            return 0;

        GenomeLoc loc = context.getLocation();
        VariantContext vc_input = tracker.getVariantContext(ref,INPUT_ROD_NAME, null, loc, true);

        VariantContext vc_comp = tracker.getVariantContext(ref,COMP_ROD_NAME, null, loc, true);

        if ( vc_input == null  )
            return 0;

        if (vc_input.isFiltered()) {
            vcfWriter.add(vc_input, ref.getBase());
            return 1;
        }
        List<Object> r2rods = tracker.getReferenceMetaData(R2_ROD_NAME);

        // ignore places where we don't have a variant
        if ( r2rods.size() == 0 )
            return 0;

        BeagleFeature beagleR2Feature = (BeagleFeature)r2rods.get(0);

        List<Object> gProbsrods = tracker.getReferenceMetaData(PROBS_ROD_NAME);

        // ignore places where we don't have a variant
        if ( gProbsrods.size() == 0 )
            return 0;

        BeagleFeature beagleProbsFeature = (BeagleFeature)gProbsrods.get(0);

        List<Object> gPhasedrods = tracker.getReferenceMetaData(PHASED_ROD_NAME);

        // ignore places where we don't have a variant
        if ( gPhasedrods.size() == 0 )
            return 0;

        BeagleFeature beaglePhasedFeature = (BeagleFeature)gPhasedrods.get(0);

        // get reference base for current position
        byte refByte = ref.getBase();

        // make new Genotypes based on Beagle results
        Map<String, Genotype> genotypes = new HashMap<String, Genotype>(vc_input.getGenotypes().size());


        // for each genotype, create a new object with Beagle information on it

        int numGenotypesChangedByBeagle = 0;
        Integer alleleCountH = 0, chrCountH = 0;
        Double alleleFrequencyH = 0.0;
        int beagleVarCounts = 0;

        Map<String,Genotype> hapmapGenotypes = null;

        if (vc_comp != null) {
            hapmapGenotypes = vc_comp.getGenotypes();
        }

        for ( Map.Entry<String, Genotype> originalGenotypes : vc_input.getGenotypes().entrySet() ) {

            Genotype g = originalGenotypes.getValue();
            Set<String> filters = new LinkedHashSet<String>(g.getFilters());

            boolean genotypeIsPhased = true;
            String sample = g.getSampleName();

            // If we have  a Hapmap (comp) ROD, compute Hapmap AC, AN and AF
            // use sample as key into genotypes structure
            if (vc_comp != null) {

                if (vc_input.getGenotypes().containsKey(sample) && hapmapGenotypes.containsKey(sample))  {

                    Genotype hapmapGenotype = hapmapGenotypes.get(sample);
                    if (hapmapGenotype.isCalled()){
                        chrCountH += 2;
                        if (hapmapGenotype.isHet()) {
                            alleleCountH += 1;
                        }    else if (hapmapGenotype.isHomVar()) {
                            alleleCountH += 2;
                        }
                    }
                }
            }

            ArrayList<String> beagleProbabilities = beagleProbsFeature.getProbLikelihoods().get(sample);
            ArrayList<String> beagleGenotypePairs = beaglePhasedFeature.getGenotypes().get(sample);

            // original alleles at this genotype
            Allele originalAlleleA = g.getAllele(0);

            Allele originalAlleleB = (g.getAlleles().size() == 2) ? g.getAllele(1) : g.getAllele(0); // hack to deal with no-call genotypes


            // We have phased genotype in hp. Need to set the isRef field in the allele.
            List<Allele> alleles = new ArrayList<Allele>();

            String alleleA = beagleGenotypePairs.get(0);
            String alleleB = beagleGenotypePairs.get(1);

            // Beagle always produces genotype strings based on the strings we input in the likelihood file.
            String refString = vc_input.getReference().getDisplayString();
            if (refString.length() == 0) // ref was null
                refString = Allele.NULL_ALLELE_STRING;

            Allele bglAlleleA, bglAlleleB;

            if (alleleA.matches(refString))
               bglAlleleA = Allele.create(alleleA,true);
            else
               bglAlleleA = Allele.create(alleleA,false);

            if (alleleB.matches(refString))
                bglAlleleB = Allele.create(alleleB,true);
            else
                bglAlleleB = Allele.create(alleleB,false);


            alleles.add(bglAlleleA);
            alleles.add(bglAlleleB);

            // Compute new GQ field = -10*log10Pr(Genotype call is wrong)
            // Beagle gives probability that genotype is AA, AB and BB.
            // Which, by definition, are prob of hom ref, het and hom var.
            Double probWrongGenotype, genotypeQuality;
            Double homRefProbability = Double.valueOf(beagleProbabilities.get(0));
            Double hetProbability = Double.valueOf(beagleProbabilities.get(1));
            Double homVarProbability = Double.valueOf(beagleProbabilities.get(2));

            if (bglAlleleA.isReference() && bglAlleleB.isReference()) // HomRef call
                probWrongGenotype = hetProbability + homVarProbability;
            else if ((bglAlleleB.isReference() && bglAlleleA.isNonReference()) || (bglAlleleA.isReference() && bglAlleleB.isNonReference()))
                probWrongGenotype = homRefProbability + homVarProbability;
            else // HomVar call
                probWrongGenotype = hetProbability + homRefProbability;

            // deal with numerical errors coming from limited formatting value on Beagle output files
            if (probWrongGenotype > 1 - MIN_PROB_ERROR)
                probWrongGenotype = 1 - MIN_PROB_ERROR;
            
            if (1-probWrongGenotype < noCallThreshold) {
                // quality is bad: don't call genotype
                alleles.clear();
                alleles.add(originalAlleleA);
                alleles.add(originalAlleleB);
                genotypeIsPhased = false;
            }

            if (probWrongGenotype < MIN_PROB_ERROR)
                genotypeQuality = MAX_GENOTYPE_QUALITY;
            else
                genotypeQuality = -log10(probWrongGenotype);

            HashMap<String,Object> originalAttributes = new HashMap<String,Object>(g.getAttributes());

            // get original encoding and add to keynotype attributes
            String a1, a2, og;
            if (originalAlleleA.isNoCall())
                a1 = ".";
            else if (originalAlleleA.isReference())
                a1 = "0";
            else
                a1 = "1";

            if (originalAlleleB.isNoCall())
                a2 = ".";
            else if (originalAlleleB.isReference())
                a2 = "0";
            else
                a2 = "1";

            og = a1+"/"+a2;

            // See if Beagle switched genotypes
            if (!((bglAlleleA.equals(originalAlleleA) && bglAlleleB.equals(originalAlleleB) ||
                    (bglAlleleA.equals(originalAlleleB) && bglAlleleB.equals(originalAlleleA))))){
                originalAttributes.put("OG",og);
                numGenotypesChangedByBeagle++;
            }
            else {
                originalAttributes.put("OG",".");
            }
            Genotype imputedGenotype = new Genotype(originalGenotypes.getKey(), alleles, genotypeQuality, filters,originalAttributes , genotypeIsPhased);
            if ( imputedGenotype.isHet() || imputedGenotype.isHomVar() ) {
                beagleVarCounts++;
            }

            genotypes.put(originalGenotypes.getKey(), imputedGenotype);

        }

        VariantContext filteredVC;
        if ( beagleVarCounts > 0 || DONT_FILTER_MONOMORPHIC_SITES )
            filteredVC = new VariantContext("outputvcf", vc_input.getChr(), vc_input.getStart(), vc_input.getEnd(), vc_input.getAlleles(), genotypes, vc_input.getNegLog10PError(), vc_input.filtersWereApplied() ? vc_input.getFilters() : null, vc_input.getAttributes());
        else {
            Set<String> removedFilters = vc_input.filtersWereApplied() ? new HashSet<String>(vc_input.getFilters()) : new HashSet<String>(1);
            removedFilters.add(String.format("BGL_RM_WAS_%s",vc_input.getAlternateAllele(0)));
            filteredVC = new VariantContext("outputvcf", vc_input.getChr(), vc_input.getStart(), vc_input.getEnd(), new HashSet<Allele>(Arrays.asList(vc_input.getReference())), genotypes, vc_input.getNegLog10PError(), removedFilters, vc_input.getAttributes());
        }

        HashMap<String, Object> attributes = new HashMap<String, Object>(filteredVC.getAttributes());
        // re-compute chromosome counts
        VariantContextUtils.calculateChromosomeCounts(filteredVC, attributes, false);

        // Get Hapmap AC and AF
        if (vc_comp != null) {
            attributes.put("ACH", alleleCountH.toString() );
            attributes.put("ANH", chrCountH.toString() );
            attributes.put("AFH", String.format("%4.2f", (double)alleleCountH/chrCountH) );

        }

        attributes.put("NumGenotypesChanged", numGenotypesChangedByBeagle );
        if( !beagleR2Feature.getR2value().equals(Double.NaN) ) {
            attributes.put("R2", beagleR2Feature.getR2value().toString() );
        }


        vcfWriter.add(VariantContext.modifyAttributes(filteredVC,attributes), ref.getBase());


        return 1;

    }

    public Integer reduceInit() {
        return 0; // Nothing to do here
    }

    /**
     * Increment the number of loci processed.
     *
     * @param value result of the map.
     * @param sum   accumulator for the reduce.
     * @return the new number of loci processed.
     */
    public Integer reduce(Integer value, Integer sum) {
        return sum + value;
    }

    /**
     * Tell the user the number of loci processed and close out the new variants file.
     *
     * @param result  the number of loci seen.
     */
    public void onTraversalDone(Integer result) {
        System.out.printf("Processed %d loci.\n", result);
    }
}
