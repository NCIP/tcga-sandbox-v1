/*
 * Copyright (c) 2010, The Broad Institute
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
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package org.broadinstitute.sting.gatk.walkers.variantutils;

import org.broadinstitute.sting.utils.variantcontext.VariantContext;
import org.broadinstitute.sting.utils.codecs.vcf.StandardVCFWriter;
import org.broadinstitute.sting.utils.codecs.vcf.VCFHeader;
import org.broadinstitute.sting.utils.codecs.vcf.VCFHeaderLine;
import org.broadinstitute.sting.utils.codecs.vcf.VCFWriter;
import org.broadinstitute.sting.commandline.Argument;
import org.broadinstitute.sting.commandline.Output;
import org.broadinstitute.sting.gatk.GenomeAnalysisEngine;
import org.broadinstitute.sting.gatk.contexts.AlignmentContext;
import org.broadinstitute.sting.gatk.contexts.ReferenceContext;
import org.broadinstitute.sting.gatk.refdata.RefMetaDataTracker;
import org.broadinstitute.sting.gatk.walkers.RMD;
import org.broadinstitute.sting.gatk.walkers.Requires;
import org.broadinstitute.sting.gatk.walkers.RodWalker;
import org.broadinstitute.sting.utils.SampleUtils;
import org.broadinstitute.sting.utils.exceptions.UserException;
import org.broadinstitute.sting.utils.codecs.vcf.VCFUtils;

import java.io.File;
import java.util.*;

/**
 * Takes a VCF file, randomly splits variants into two different sets, and outputs 2 new VCFs with the results.
 */
@Requires(value={},referenceMetaData=@RMD(name="variant", type=VariantContext.class))
public class RandomlySplitVariants extends RodWalker<Integer, Integer> {

    @Output(fullName="out1", shortName="o1", doc="File #1 to which variants should be written", required=true)
    protected VCFWriter vcfWriter1 = null;

    @Output(fullName="out2", shortName="o2", doc="File #2 to which variants should be written", required=true)
    // there's a reported bug in the GATK where we can't have 2 @Output writers
    protected File file2 = null;
    protected StandardVCFWriter vcfWriter2 = null;

    @Argument(fullName="fractionToOut1", shortName="fraction", doc="Fraction of records to be placed in out1 (must be 0 >= fraction <= 1); all other records are placed in out2", required=false)
    protected double fraction = 0.5;

    protected static final String INPUT_VARIANT_ROD_BINDING_NAME = "variant";

    protected int iFraction;

    /**
     * Set up the VCF writer, the sample expressions and regexs, and the JEXL matcher
     */
    public void initialize() {
        if ( fraction < 0.0 || fraction > 1.0 )
            throw new UserException.BadArgumentValue("fractionToOut1", "this value needs to be a number between 0 and 1");
        iFraction = (int)(fraction * 1000.0);

        // setup the header info
        final ArrayList<String> inputNames = new ArrayList<String>();
        inputNames.add( INPUT_VARIANT_ROD_BINDING_NAME );
        Set<String> samples = SampleUtils.getUniqueSamplesFromRods(getToolkit(), inputNames);
        Set<VCFHeaderLine> hInfo = new HashSet<VCFHeaderLine>();
        hInfo.addAll(VCFUtils.getHeaderFields(getToolkit(), inputNames));

        vcfWriter1.writeHeader(new VCFHeader(hInfo, samples));
        vcfWriter2 = new StandardVCFWriter(file2, true);
        vcfWriter2.writeHeader(new VCFHeader(hInfo, samples));
    }

    /**
     * Subset VC record if necessary and emit the modified record (provided it satisfies criteria for printing)
     *
     * @param  tracker   the ROD tracker
     * @param  ref       reference information
     * @param  context   alignment info
     * @return 1 if the record was printed to the output file, 0 if otherwise
     */
    public Integer map(RefMetaDataTracker tracker, ReferenceContext ref, AlignmentContext context) {
        if ( tracker == null )
            return 0;

        Collection<VariantContext> vcs = tracker.getVariantContexts(ref, INPUT_VARIANT_ROD_BINDING_NAME, null, context.getLocation(), true, false);
        for ( VariantContext vc : vcs ) {
            int random = GenomeAnalysisEngine.getRandomGenerator().nextInt(1000);
            if ( random < iFraction )
                vcfWriter1.add(vc, ref.getBase());
            else
                vcfWriter2.add(vc, ref.getBase());
        }

        return 1;
    }

    public Integer reduceInit() { return 0; }

    public Integer reduce(Integer value, Integer sum) { return value + sum; }

    public void onTraversalDone(Integer result) { logger.info(result + " records processed."); }
}
