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

package org.broadinstitute.sting.gatk.walkers.annotator;

import org.broadinstitute.sting.utils.variantcontext.VariantContext;
import org.broadinstitute.sting.utils.codecs.vcf.*;
import org.broadinstitute.sting.commandline.Hidden;
import org.broadinstitute.sting.gatk.contexts.AlignmentContext;
import org.broadinstitute.sting.gatk.contexts.AlignmentContextUtils;
import org.broadinstitute.sting.gatk.contexts.ReferenceContext;
import org.broadinstitute.sting.utils.variantcontext.VariantContextUtils;
import org.broadinstitute.sting.gatk.refdata.RefMetaDataTracker;
import org.broadinstitute.sting.gatk.walkers.*;
import org.broadinstitute.sting.gatk.walkers.annotator.interfaces.AnnotationType;
import org.broadinstitute.sting.gatk.walkers.annotator.interfaces.GenotypeAnnotation;
import org.broadinstitute.sting.gatk.walkers.annotator.interfaces.InfoFieldAnnotation;
import org.broadinstitute.sting.utils.BaseUtils;
import org.broadinstitute.sting.utils.classloader.PluginManager;
import org.broadinstitute.sting.utils.SampleUtils;
import org.broadinstitute.sting.commandline.Argument;
import org.broadinstitute.sting.commandline.Output;
import org.broadinstitute.sting.utils.codecs.vcf.VCFUtils;

import java.util.*;


/**
 * Annotates variant calls with context information.  Users can specify which of the available annotations to use.
 */
@Requires(value={},referenceMetaData=@RMD(name="variant",type=VariantContext.class))
@Allows(value={DataSource.READS, DataSource.REFERENCE})
@Reference(window=@Window(start=-50,stop=50))
@By(DataSource.REFERENCE)
public class VariantAnnotator extends RodWalker<Integer, Integer> {

    @Output(doc="File to which variants should be written",required=true)
    protected VCFWriter vcfWriter = null;

    @Argument(fullName="sampleName", shortName="sample", doc="The sample (NA-ID) corresponding to the variant input (for non-VCF input only)", required=false)
    protected String sampleName = null;

    @Argument(fullName="annotation", shortName="A", doc="One or more specific annotations to apply to variant calls", required=false)
    protected List<String> annotationsToUse = new ArrayList<String>();

    @Argument(fullName="group", shortName="G", doc="One or more classes/groups of annotations to apply to variant calls", required=false)
    protected List<String> annotationGroupsToUse = new ArrayList<String>();

    @Argument(fullName="expression", shortName="E", doc="One or more specific expressions to apply to variant calls; see documentation for more details", required=false)
    protected List<String> expressionsToUse = new ArrayList<String>();

    @Argument(fullName="useAllAnnotations", shortName="all", doc="Use all possible annotations (not for the faint of heart)", required=false)
    protected Boolean USE_ALL_ANNOTATIONS = false;

    @Argument(fullName="list", shortName="ls", doc="List the available annotations and exit")
    protected Boolean LIST = false;

    @Hidden
    @Argument(fullName = "assume_single_sample_reads", shortName = "single_sample", doc = "The single sample that we should assume is represented in the input bam (and therefore associate with all reads regardless of whether they have read groups)", required = false)
    protected String ASSUME_SINGLE_SAMPLE = null;

    @Hidden
    @Argument(fullName="vcfContainsOnlyIndels", shortName="dels",doc="Use if you are annotating an indel vcf, currently VERY experimental", required = false)
    protected boolean indelsOnly = false;

    private HashMap<String, String> nonVCFsampleName = new HashMap<String, String>();

    private VariantAnnotatorEngine engine;

    private Collection<VariantContext> indelBufferContext;


    private void listAnnotationsAndExit() {
        List<Class<? extends InfoFieldAnnotation>> infoAnnotationClasses = new PluginManager<InfoFieldAnnotation>(InfoFieldAnnotation.class).getPlugins();
        System.out.println("\nAvailable annotations for the VCF INFO field:");
        for (int i = 0; i < infoAnnotationClasses.size(); i++)
            System.out.println("\t" + infoAnnotationClasses.get(i).getSimpleName());
        System.out.println();
        List<Class<? extends GenotypeAnnotation>> genotypeAnnotationClasses = new PluginManager<GenotypeAnnotation>(GenotypeAnnotation.class).getPlugins();
        System.out.println("\nAvailable annotations for the VCF FORMAT field:");
        for (int i = 0; i < genotypeAnnotationClasses.size(); i++)
            System.out.println("\t" + genotypeAnnotationClasses.get(i).getSimpleName());
        System.out.println();
        System.out.println("\nAvailable classes/groups of annotations:");
        for ( Class c : new PluginManager<AnnotationType>(AnnotationType.class).getInterfaces() )
            System.out.println("\t" + c.getSimpleName());
        System.out.println();
        System.exit(0);
    }

    /**
     * Prepare the output file and the list of available features.
     */
    public void initialize() {

        if ( LIST )
            listAnnotationsAndExit();

        // get the list of all sample names from the variant VCF input rod, if applicable
        Set<String> rodName = new HashSet<String>();
        rodName.add("variant");
        Set<String> samples = SampleUtils.getUniqueSamplesFromRods(getToolkit(), rodName);

        // add the non-VCF sample from the command-line, if applicable
        if ( sampleName != null  ) {
            nonVCFsampleName.put(sampleName.toUpperCase(), "variant");
            samples.add(sampleName.toUpperCase());
        }

        // if there are no valid samples, warn the user
        if ( samples.size() == 0 ) {
            logger.warn("There are no samples input at all; use the --sampleName argument to specify one if desired.");
        }

        if ( USE_ALL_ANNOTATIONS )
            engine = new VariantAnnotatorEngine(getToolkit());
        else
            engine = new VariantAnnotatorEngine(getToolkit(), annotationGroupsToUse, annotationsToUse);
        engine.initializeExpressions(expressionsToUse);

        // setup the header fields
        // note that if any of the definitions conflict with our new ones, then we want to overwrite the old ones
        Set<VCFHeaderLine> hInfo = new HashSet<VCFHeaderLine>();
        hInfo.addAll(engine.getVCFAnnotationDescriptions());
        for ( VCFHeaderLine line : VCFUtils.getHeaderFields(getToolkit(), Arrays.asList("variant")) ) {
            if ( isUniqueHeaderLine(line, hInfo) )
                hInfo.add(line);
        }

        VCFHeader vcfHeader = new VCFHeader(hInfo, samples);
        vcfWriter.writeHeader(vcfHeader);

        if ( indelsOnly ) {
            indelBufferContext = null;
        }
    }

    public static boolean isUniqueHeaderLine(VCFHeaderLine line, Set<VCFHeaderLine> currentSet) {
        if ( !(line instanceof VCFCompoundHeaderLine) )
            return true;

        for ( VCFHeaderLine hLine : currentSet ) {
            if ( hLine instanceof VCFCompoundHeaderLine && ((VCFCompoundHeaderLine)line).sameLineTypeAndName((VCFCompoundHeaderLine)hLine) )
                return false;
        }

        return true;
    }

    /**
     * Initialize the number of loci processed to zero.
     *
     * @return 0
     */
    public Integer reduceInit() { return 0; }


    /**
     * We want reads that span deletions
     *
     * @return true
     */
    public boolean includeReadsWithDeletionAtLoci() { return true; }

    /**
     * We want to see extended events if annotating indels
     *
     * @return true
     */
    public boolean generateExtendedEvents() { return indelsOnly; }

    /**
     * For each site of interest, annotate based on the requested annotation types
     *
     * @param tracker  the meta-data tracker
     * @param ref      the reference base
     * @param context  the context for the given locus
     * @return 1 if the locus was successfully processed, 0 if otherwise
     */
    public Integer map(RefMetaDataTracker tracker, ReferenceContext ref, AlignmentContext context) {
        if ( tracker == null )
            return 0;

        Collection<VariantContext> VCs = tracker.getVariantContexts(ref, "variant", null, context.getLocation(), true, false);
        if ( VCs.size() == 0 )
            return 0;

        Collection<VariantContext> annotatedVCs = VCs;

        // if the reference base is not ambiguous, we can annotate
        Map<String, AlignmentContext> stratifiedContexts;
        if ( BaseUtils.simpleBaseToBaseIndex(ref.getBase()) != -1 ) {
            if ( ! context.hasExtendedEventPileup() ) {
                stratifiedContexts = AlignmentContextUtils.splitContextBySampleName(context.getBasePileup(), ASSUME_SINGLE_SAMPLE);
            } else {
                stratifiedContexts = AlignmentContextUtils.splitContextBySampleName(context.getExtendedEventPileup(), ASSUME_SINGLE_SAMPLE);
            }
            if ( stratifiedContexts != null ) {
                annotatedVCs = new ArrayList<VariantContext>(VCs.size());
                for ( VariantContext vc : VCs )
                    annotatedVCs.addAll(engine.annotateContext(tracker, ref, stratifiedContexts, vc));
            }
        }

        if ( ! indelsOnly ) {
            for ( VariantContext annotatedVC : annotatedVCs )
                vcfWriter.add(annotatedVC, ref.getBase());
        } else {
            // check to see if the buffered context is different (in location) this context
            if ( indelBufferContext != null && ! VariantContextUtils.getLocation(getToolkit().getGenomeLocParser(),indelBufferContext.iterator().next()).equals(VariantContextUtils.getLocation(getToolkit().getGenomeLocParser(),annotatedVCs.iterator().next())) ) {
                for ( VariantContext annotatedVC : indelBufferContext )
                    vcfWriter.add(annotatedVC, ref.getBase());
                indelBufferContext = annotatedVCs;
            } else {
                indelBufferContext = annotatedVCs;
            }
        }

        return 1;
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
        logger.info("Processed " + result + " loci.\n");
    }
}
